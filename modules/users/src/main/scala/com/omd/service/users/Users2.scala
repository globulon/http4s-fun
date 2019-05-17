package com.omd.service.users

import cats.{Monad, Parallel}
import cats.data.ReaderT
import cats.instances.string.catsStdShowForString
import com.olegpy.meow.hierarchy._
import com.omd.service.domain.{Cors, Server}
import com.omd.service.errors._
import com.omd.service.interpreters._
import com.typesafe.config.ConfigFactory.parseResources
import org.http4s._
import scalaz.zio._
import scalaz.zio.interop.catz.taskConcurrentInstances
//import scalaz.zio.interop._

object Users2 extends App {
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

  private def createService: ReaderT[Task, Cors, Http[Task, Task]] = ???
//    Kleisli { cors ⇒ userService[Task].map(new Entities(_).routes.orNotFound).map(CORS(_, cors.to[CORSConfig])) }

  private def startServer(entities: Http[Task, Task]): ReaderT[Task, Server, Int] = ???

//    Kleisli {
//    case Server(Bindings(host, port), _) ⇒
//      BlazeServerBuilder[Task]
//        .bindHttp(port = port, host = host)
//        .withHttpApp(entities)
//        .withBanner(banner)
//        .serve
//        .compile
//        .drain
//        .as(ExitCode.Success)
//  }

//  def run(args: List[String]): ZIO[Environment, Nothing, Int]
}
