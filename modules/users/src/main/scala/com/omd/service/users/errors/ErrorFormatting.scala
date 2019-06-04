package com.omd.service.users.errors

import cats.Show
import com.omd.service.errors.ErrGenerator

private[errors] trait ErrorFormatting {
  implicit val showUserAlreadyExists: Show[UserAlreadyExists] = Show.show {
    case UserAlreadyExists(name) ⇒ s"""User $name already exists"""
  }

  implicit val showMissingUser: Show[MissingUser] = Show.show {
    case MissingUser(id) ⇒ s"""User $id does not exist"""
  }

  implicit val throwableToAlreadyExist: ErrGenerator[Throwable, UserAlreadyExists] =
    new ErrGenerator[Throwable, UserAlreadyExists] {
      override def apply: Throwable ⇒ UserAlreadyExists = t ⇒ UserAlreadyExists(t.getMessage)
    }
}
