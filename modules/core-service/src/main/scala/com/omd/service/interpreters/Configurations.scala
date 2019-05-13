package com.omd.service.interpreters

import cats.data.Kleisli
import cats.implicits._
import cats.{Monad, Parallel}
import com.omd.service.algebras.Configuration
import com.omd.service.domain.{Bindings, Cors, Server}
import com.omd.service.errors.{AppErr, ErrGenerator, ErrorChannel}
import com.typesafe.config.Config

import scala.language.postfixOps

private[interpreters] trait Configurations {
  final def configuration[M[_]: Monad, F[_], E <: AppErr](implicit ME: ErrorChannel[M, E],
                                                          P: Parallel[M, F],
                                                          GE: ErrGenerator[Throwable, E]): Configuration[Config, M, F] =
    new Configuration[Config, M, F] {
      private val parser = configParser[M, E]

      import parser._

      private def bindings: Kleisli[M, Config, Bindings] =
        (Kleisli(parse[String](key = "bindings.host")), Kleisli(parse[Int](key = "bindings.port"))).parMapN(Bindings)

      private def cors: Kleisli[M, Config, Cors] =
        Kleisli(parse[String](key = "cors.origin")).map(Cors)

      override def server: Config ⇒ M[Server] = (bindings, cors) parMapN { Server } run
    }
}
