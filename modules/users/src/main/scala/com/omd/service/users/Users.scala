package com.omd.service.users

import cats.Parallel
import cats.data.{Kleisli, ReaderT}
import cats.effect._
import cats.implicits._
import com.omd.fp._
import com.omd.service.domain.{Bindings, Cors, Server}
import com.omd.service.errors._
import com.omd.service.interpreters._
import com.omd.service.users.http._
import com.omd.service.users.interpreters._
import com.olegpy.meow.hierarchy._
import com.typesafe.config.ConfigFactory.parseResources
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, CORSConfig}

object Users extends IOApp {
  implicit private val P: Parallel[IO, IO] = Parallel.identity[IO]

  private def loadConfig: IO[Server] = for {
    config ← IO(parseResources("server.conf"))
    server ← configuration[IO, IO, ConfigError].server(config)
  } yield server

  override def run(args: List[String]): IO[ExitCode] = for {
    server ← loadConfig
    logger ← logger[IO](name = "User-Service")
    _      ← logger.info(s"""Starting server $server""")
    exit   ← start(server)
  } yield exit

  private def start: ReaderT[IO, Server, ExitCode] =
    createService.local[Server](_.cors).tapWithF { case (server, entities) ⇒ startServer(entities)(server) }

  private def createService: ReaderT[IO, Cors, Http[IO, IO]] =
    Kleisli { cors ⇒ userService[IO].map(new Entities(_).routes.orNotFound).map(CORS(_, cors.to[CORSConfig])) }

  private def startServer(entities: Http[IO, IO]): ReaderT[IO, Server, ExitCode] = Kleisli {
    case Server(Bindings(host, port), _) ⇒
      BlazeServerBuilder[IO]
        .bindHttp(port = port, host = host)
        .withHttpApp(entities)
        .withBanner(banner)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
  }
}
