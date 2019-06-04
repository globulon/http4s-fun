package com.omd.service

import cats.data._

package object errors extends ErrorConversions with HHandlers {
  def captured[E <: AppErr](e: E, es: E*): Captured[E] = Captured(NonEmptyChain(e, es: _*))
}
