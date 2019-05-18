package com.omd.service.users

import cats.Functor.ops._
import cats.Parallel
import cats.data.ReaderT
import cats.instances.string.catsStdShowForString
import com.olegpy.meow.hierarchy._
import com.omd.fp._
import com.omd.service.domain.{Bindings, Cors, Server}
import com.omd.service.errors._
import com.omd.service.interpreters._
import com.omd.service.users.http._
import com.omd.service.users.interpreters._
import com.typesafe.config.ConfigFactory.parseResources
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, CORSConfig}
import scalaz.zio._
import scalaz.zio.interop.catz.implicits.ioTimer
import scalaz.zio.interop.catz.{CatsApp, taskEffectInstances}

object Users2 extends CatsApp {
  implicit private val P: Parallel[Task, Task] = Parallel.identity[Task]

  private def loadConfig: Task[Server] = for {
    config ← Task(parseResources("server.conf"))
    server ← configuration[Task, Task, ConfigError].server(config)
  } yield server

  override def run(args: List[String]): UIO[Int] = boot.catchAll(_ ⇒ UIO.succeed(7))

    private def boot: Task[Int] = for {
    server ← loadConfig
    logger ← logger[Task](name = "User-Service")
    _      ← logger.info(s"""Starting server $server""")
    exit   ← start(server)
  } yield exit

  private def start: ReaderT[Task, Server, Int] = ???
//    createService.local[Server](_.cors).tapWithF { case (server, entities) ⇒ startServer(entities)(server) }

  private def createService: Cors ⇒ Task[Http[Task, Task]] =
    cors ⇒ userService[Task].map(new Entities(_).routes.orNotFound).map(CORS(_, cors.to[CORSConfig]))

  private def startServer: Http[Task, Task] ⇒ Server ⇒ Task[Int] = entities ⇒ {
    case Server(Bindings(h, p), _) ⇒
      BlazeServerBuilder[Task]
        .bindHttp(port = p, host = h)
        .withHttpApp(entities)
        .withBanner(banner)
        .serve
        .compile
        .drain
        .as(0)
  }
}
