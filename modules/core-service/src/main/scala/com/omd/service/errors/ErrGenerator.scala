package com.omd.service.errors

trait ErrGenerator[T, E] {
  def apply: T ⇒ E
}

object ErrGenerator {
  def apply[T, E](implicit G: ErrGenerator[T, E]): ErrGenerator[T, E] = G
}
