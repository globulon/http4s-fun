package com.omd.fp

import org.scalatest.{MustMatchers, WordSpecLike}

final class TranslationsTest extends WordSpecLike with MustMatchers {
  "Translation" should {
    "implicitly convert value" in {
      Wrapper(2).to[String] must be("2")
    }
  }

  implicit private def intToString: Wrapper |~~> String = translate[Wrapper, String] { case Wrapper(i) ⇒ i.toString }
}

final case class Wrapper(i: Int)
