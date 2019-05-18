package com.omd.service.users

import cats.Functor.ops._
import cats.instances.string.catsStdShowForString
import com.olegpy.meow.hierarchy._
import com.omd.fp._
import com.omd.service.domain.{Bindings, Server}
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
import scalaz.zio.interop.ParIO
import scalaz.zio.interop.catz.implicits.ioTimer
import scalaz.zio.interop.catz.{CatsApp, taskEffectInstances, parallelInstance}

object Users2 extends CatsApp {
  override def run(args: List[String]): UIO[Int] = boot.catchAll(_ ⇒ UIO.succeed(7))

  private def loadConfig: Task[Server] = for {
    config ← Task(parseResources("server.conf"))
    server ← configuration[Task, ParIO[Any, Throwable, ?], ConfigError].server(config)
  } yield server

    private def boot: Task[Int] = for {
    server    ← loadConfig
    logger    ← logger[Task](name = "User-Service")
    _         ← logger.info(s"""Starting server $server""")
    entities  ← createService(server)
    exit      ← start(entities)(server)
  } yield exit

 private def createService: Server ⇒ Task[Http[Task, Task]] =
    srv ⇒ userService[Task].map(new Entities(_).routes.orNotFound).map(CORS(_, srv.cors.to[CORSConfig]))

  private def start: Http[Task, Task] ⇒ Server ⇒ Task[Int] = entities ⇒ {
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
