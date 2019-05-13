package com.omd.service.algebras

import cats.Show

trait Logging[F[_]] {
  def info[A: Show](a: ⇒ A): F[Unit]

  def warn[A: Show](a: ⇒ A): F[Unit]

  def error[A: Show](a: ⇒ A): F[Unit]
}
