package com.omd.service.errors

import org.http4s.HttpRoutes

trait ErrorHandler[F[_], E] {
  def handle: HttpRoutes[F] ⇒ HttpRoutes[F]
}

object ErrorHandler {
  def apply[F[_], E <: Throwable](implicit EH: ErrorHandler[F, E]): ErrorHandler[F, E] = EH
}
