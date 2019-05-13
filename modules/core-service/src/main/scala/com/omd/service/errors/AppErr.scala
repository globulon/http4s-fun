package com.omd.service.errors

import cats.data.NonEmptyChain

trait AppErr                                         extends Throwable
final case class Captured[E](errs: NonEmptyChain[E]) extends Throwable with AppErr
