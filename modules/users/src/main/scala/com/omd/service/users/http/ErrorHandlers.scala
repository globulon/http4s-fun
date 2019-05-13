package com.omd.service.users.http

import cats.effect._
import cats.{ApplicativeError, Show}
import com.omd.service.errors._
import com.omd.service.http
import com.omd.service.http.Message
import com.omd.service.users.errors.{UserAlreadyExists, UserError}
import com.omd.service.users.errors.{UserAlreadyExists, UserError}
import com.omd.service.users.errors.{UserAlreadyExists, UserError}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

private[http] trait ErrorHandlers {
  implicit def handleUserErrors[F[_]: Sync](implicit AE: ApplicativeError[F, UserError]): ErrorHandler[F, UserError] =
    new ErrorHandler[F, UserError] with Http4sDsl[F] {

      private val h = makeHandle[F, UserError] {
        case e @ UserAlreadyExists(_) ⇒ Conflict(Message(http.Error, Show[UserAlreadyExists].show(e)))
      }

      override def handle(routes: HttpRoutes[F]): HttpRoutes[F] = h(routes)
    }
}
