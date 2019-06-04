package com.omd.service.errors

//import shapeless.{:+:, <:!<, Coproduct, Poly1}

private[errors] trait CoHandlers {


//  implicit def coHandler[F[_], Err, Errs <: Coproduct](implicit notCo: Err <:!< Coproduct,
//                                                       handler: ErrorHandler[F, Err],
//                                                       handlers: ErrorHandler[F, Errs]): ErrorHandler[F, Err :+: Errs] =
//    new ErrorHandler[F, Err :+: Errs] {
//      private object h extends Poly1 {
//        implicit val handlerErr  = at[Err] { _ ⇒  handler.handle }
//        implicit val handlerErrs = at[Errs] { _ ⇒ handlers.handle }
//      }
//
//      override def handle: HttpRoutes[F] ⇒ HttpRoutes[F] =
//    }
}
