package com.omd.service.errors

trait ErrGenerator[T, E] {
  def apply: T ⇒ E
}
