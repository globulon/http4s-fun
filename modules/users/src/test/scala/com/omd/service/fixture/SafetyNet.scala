package com.omd.service.fixture

import cats.Eval
import cats.effect.{IO, LiftIO}

private[fixture] trait SafetyNet {
  implicit final def safeLift: LiftIO[Safe] = new LiftIO[Safe] {
    override def liftIO[A](ioa: IO[A]): Safe[A] = new Safe[A] {
      //attempt warranties you will never have an exception being thrown
      override def effect: Eval[Either[Throwable, A]] = Eval.later(ioa.attempt.unsafeRunSync())
    }
  }

  implicit def ioWrapper[A](ioa: IO[A]): IOWrapper[A] = new IOWrapper[A](ioa)
}

final class IOWrapper[A](val ioa: IO[A]) extends AnyVal {
  def effect[Test](f: A ⇒ Test): Test = (for {
    a ← ioa
    r ← IO(f(a))
  } yield r).unsafeRunSync()
}
