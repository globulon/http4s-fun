package com.omd.service.errors

import cats.data.{Kleisli, OptionT}
import cats.implicits._
import cats.{ApplicativeError, Endo, Semigroup, Show}
import org.http4s.{HttpRoutes, Response}

import scala.reflect.ClassTag
import scala.util.Try

private[errors] trait ErrorConversions {
  implicit final def genConfigError: ErrGenerator[Throwable, ConfigError] = new ErrGenerator[Throwable, ConfigError] {
    override def apply: Throwable ⇒ ConfigError = t ⇒ ConfigError(t.getMessage)
  }

  implicit final def genErrs[E <: AppErr](implicit EG: ErrGenerator[Throwable, E]): ErrGenerator[Throwable, Captured[E]] =
    new ErrGenerator[Throwable, Captured[E]] {
      override def apply: Throwable ⇒ Captured[E] = a ⇒ captured(EG.apply(a))
    }

  implicit final def capturedSemigroup[E <: AppErr]: Semigroup[Captured[E]] = (x: Captured[E], y: Captured[E]) ⇒
    Captured[E](x.errs ++ y.errs)

  implicit final def showConfigError: Show[ConfigError] = Show.show {
    case ConfigError(msg) ⇒ s"""[ConfigError] $msg"""
  }

  implicit final def errorChannel[F[_], E <: AppErr: ClassTag](implicit AE: ApplicativeError[F, E]): ErrorChannel[F, E] =
    new ErrorChannel[F, E] {
      override def raise[A](e: E): F[A] = AE.raiseError(e)
      override def fold[A](a: Try[A])(implicit EG: ErrGenerator[Throwable, E]): F[A] = a.fold(
        {
          case e: E ⇒ raise(e)
          case t    ⇒ raise(EG.apply(t))
        },
        AE.point
      )
      override def recover[A](fa: F[A])(f: E ⇒ F[A]): F[A] = AE.handleErrorWith(fa)(f)
    }

  implicit class ErrorChannelOps[F[_], E <: AppErr](e: E)(implicit EC: ErrorChannel[F, E]) {
    def raise[A]: F[A] = ErrorChannel[F, E].raise[A](e)
  }

  final def makeHandle[F[_], E <: Throwable](handler: E ⇒ F[Response[F]])
                                            (implicit EH: ApplicativeError[F, E]): Endo[HttpRoutes[F]] =
    routes ⇒
      Kleisli { req ⇒
        OptionT { routes.run(req).value.handleErrorWith(e ⇒ handler(e).map(Option(_))) }
    }
}
