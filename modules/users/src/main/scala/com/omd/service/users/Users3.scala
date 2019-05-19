package com.omd.service.users

import cats.Functor.ops._
import cats.instances.string.catsStdShowForString
import com.olegpy.meow.hierarchy._
import com.omd.fp._
import com.omd.service.algebras.Logging
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


object Users3 extends CatsApp {
  override def run(args: List[String]): UIO[Int] =  loadConfig >>> boot catchAll(_ ⇒ UIO.succeed(7))

  private def loadConfig: Task[Server] = for {
    config ← Task(parseResources("server.conf"))
    server ← configuration[Task, ParIO[Any, Throwable, ?], ConfigError].server(config)
  } yield server

  private def boot: TaskR[Server, Int] = for {
    s   ← TaskR.environment[Server]
    log ← startLogger
    _   ← log.info(s"""Starting server $s""")
    srv ← createService
    r   ← start(srv)
  }  yield r

  private def startLogger: TaskR[Server, Logging[Task]] =
    TaskR.accessM[Server] { _ ⇒ logger[Task](name = "User-Service") }

  private def createService: TaskR[Server, Http[Task, Task]] = TaskR.accessM[Server] {
    case Server(_, cors) ⇒ userService[Task].map(new Entities(_).routes.orNotFound).map(CORS(_, cors.to[CORSConfig]))
  }

  private def start(entities: Http[Task, Task]): TaskR[Server, Int]  = TaskR.accessM[Server]  {
    case Server(Bindings(h, p), _) ⇒
      BlazeServerBuilder[Task].bindHttp(port = p, host = h)
        .withHttpApp(entities)
        .withBanner(banner)
        .serve
        .compile
        .drain
        .as(0)
  }
}
