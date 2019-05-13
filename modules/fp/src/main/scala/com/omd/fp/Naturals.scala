package com.omd.fp

import cats.data.Validated.Valid
import cats.data.ValidatedNec
import cats.~>

private[fp] trait Naturals {
  implicit def validatedToOption[E]: ValidatedNec[E, ?] ~> Option = new (ValidatedNec[E, ?] ~> Option) {
    override def apply[A](fa: ValidatedNec[E, A]): Option[A] = fa match {
      case Valid(a) ⇒ Some(a)
      case _        ⇒ None
    }
  }
}
