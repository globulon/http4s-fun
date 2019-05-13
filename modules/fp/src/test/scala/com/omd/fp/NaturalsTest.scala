package com.omd.fp

import cats.data._
import cats.data.Validated.{Invalid, Valid}
import org.scalatest.{MustMatchers, WordSpecLike}

final class NaturalsTest extends WordSpecLike with MustMatchers {
  "Valid" should {
    "convert to some value" in {
      validatedToOption[String].apply(Valid(7)) must be(Some(7))
    }
  }

  "Valid" should {
    "convert to no value" in {
      validatedToOption[String].apply(Invalid(NonEmptyChain("one", "two"))) must be(None)
    }
  }

}
