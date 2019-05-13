package com.omd.service.fixture

import cats.Eval

trait Safe[A] {
  def effect: Eval[Either[Throwable, A]]
}
