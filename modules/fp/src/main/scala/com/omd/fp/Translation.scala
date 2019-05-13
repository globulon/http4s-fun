package com.omd.fp

trait Translation[A, B] {
  def to: A ⇒ B
}

private[fp] trait Translations {
  type |~~>[A, B] = Translation[A, B]

  def translate[A, B](f: A ⇒ B): A |~~> B = new (A |~~> B) {
    override val to: A ⇒ B = f
  }

  implicit def translateOps[A](a: A): TranslateOps[A] = new TranslateOps(a)
}

final class TranslateOps[A](val a: A) extends AnyVal {
  def to[B](implicit t: A |~~> B): B = t.to(a)
}
