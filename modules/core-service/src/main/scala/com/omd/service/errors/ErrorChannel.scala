package com.omd.service.errors

import scala.util.Try

trait ErrorChannel[F[_], E <: Throwable] {
  def raise[A](e: E): F[A]

  def fold[A](a: Try[A])(implicit EG: ErrGenerator[Throwable, E]): F[A]

  def recover[A](fa: F[A])(f: E ⇒ F[A]): F[A]
}

object ErrorChannel {
  def apply[F[_], E <: AppErr](implicit EC: ErrorChannel[F, E]): ErrorChannel[F, E] = EC
}
