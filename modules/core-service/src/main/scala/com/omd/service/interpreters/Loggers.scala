package com.omd.service.interpreters

import cats.Show
import cats.effect.Sync
import cats.implicits._
import com.omd.service.algebras.Logging
import org.slf4j.{Logger, LoggerFactory}

private[interpreters] trait Loggers {
  final def logger[M[_]: Sync](name: String): M[Logging[M]] =
    Sync[M].delay(LoggerFactory.getLogger(name)) map logging[M]

  private def logging[M[_]: Sync]: Logger ⇒ Logging[M] = logger ⇒
    new Logging[M] {
      override def info[A: Show](a: ⇒ A): M[Unit] = Sync[M].delay(logger.info(Show[A].show(a)))

      override def warn[A: Show](a: ⇒ A): M[Unit] = Sync[M].delay(logger.warn(Show[A].show(a)))

      override def error[A: Show](a: ⇒ A): M[Unit] = Sync[M].delay(logger.error(Show[A].show(a)))
  }
}
