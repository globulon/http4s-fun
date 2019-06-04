package com.omd.service.users

import cats.effect._
import cats.implicits._
import com.omd.service.errors.ErrorHandler
import com.omd.service.http._
import com.omd.service.users.algebras.Users
import com.omd.service.users.domain.UserDefinition
import com.omd.service.users.errors.UserError
import com.omd.service.users.http._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.dsl._

final private[users] class Entities[F[_]](users: Users[F],
                                         )(implicit S: Effect[F], EH: ErrorHandler[F, UserError])
  extends Http4sDsl[F] {
  private def service: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET → (Root / "user" / LongVar(byId)) ⇒
      S.flatMap(users.findById(byId)) { user ⇒ Ok(user.asJson) }

    case GET → (Root / "users") ⇒
      S.flatMap(users.findAll) { users ⇒ Ok(users.asJson) }

    case req @ POST → (Root / "user") ⇒
      req.as[UserDefinition].flatMap { user ⇒ users.create(user) *> Created(user.asJson) }

    case GET → (Root / "index") ⇒
      Ok(Message(Info, "Welcome to user service").asJson)
  }

  def routes: HttpRoutes[F] = EH.handle(service)
}
