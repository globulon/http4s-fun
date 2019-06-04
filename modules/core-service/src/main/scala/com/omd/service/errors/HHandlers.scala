package com.omd.service.errors

import org.http4s.HttpRoutes
import shapeless.{::, <:!<, HList, HNil}

protected[errors] trait HHandlers {
  implicit final def hErrs[F[_], Err, Errs <: HList](implicit notHL: Err <:!< HList,
                                                     handler: ErrorHandler[F, Err], handlers: ErrorHandler[F, Errs]): ErrorHandler[F, Err :: Errs] =
    new ErrorHandler[F, Err :: Errs] {
      override def handle: HttpRoutes[F] ⇒ HttpRoutes[F] = handler.handle compose handlers.handle
    }

  implicit final def nilErr[F[_]]: ErrorHandler[F, HNil] = new ErrorHandler[F, HNil] {
    override def handle: HttpRoutes[F] ⇒ HttpRoutes[F] = identity
  }
}
