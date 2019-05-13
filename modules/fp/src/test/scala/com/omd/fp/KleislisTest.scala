package com.omd.fp

import cats._
import cats.data._
import cats.implicits._
import org.scalatest.{MustMatchers, WordSpecLike}

final class KleislisTest extends WordSpecLike with MustMatchers {
  "|||" should {
    "give first result" in {
      (Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) ||| Kleisli.liftF[Option, Int, Int](None)).run(4) must be(Some(8))
    }

    "recover" in {
      (Kleisli.liftF[Option, Int, Int](None) ||| Kleisli[Option, Int, Int](x ⇒ Some(x * 2))).run(4) must be(Some(8))
    }

    "not recover" in {
      (Kleisli.liftF[Option, Int, Int](None) ||| Kleisli.liftF[Option, Int, Int](None)).run(4) must be(None)
    }
  }

  ">=>" should {
    "compose" in {
      (Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) >=> Kleisli.liftF[Option, Int, Int](None)).run(4) must be(None)
      (Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) >=> Kleisli[Option, Int, Int](x ⇒ Some(x * 2))).run(4) must be(Some(16))
    }

    "not compose" in {
      (Kleisli.liftF[Option, Int, Int](None) >=> Kleisli[Option, Int, Int](x ⇒ Some(x * 2))).run(4) must be(None)
    }
  }

  ">==>" should {
    "compose" in {
      (Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) >==> ((_: Int) ⇒ None)).run(4) must be(None)
      (Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) >==> ((x: Int) ⇒ Some(x * 2))).run(4) must be(Some(16))
    }

    "not compose" in {
      (Kleisli.liftF[Option, Int, Int](None) >==> ((x: Int) ⇒ Some(x * 2))).run(4) must be(None)
    }
  }

  "=<<" should {
    "compose" in {
      Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) =<< Some(4) must be(Some(8))
    }

    "not compose" in {
      Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) =<< None must be(None)
    }
  }

  ">>::" should {
    "compose" in {
      Some(4) >>:: Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) must be(Some(8))
    }

    "not compose" in {
      None >>:: Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) must be(None)
    }
  }

  "=<<<" should {
    "compose" in {
      Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) =<<< 4 must be(Some(8))
    }
  }

  ">>>::" should {
    "compose" in {
      4 >>>:: Kleisli[Option, Int, Int](x ⇒ Some(x * 2)) must be(Some(8))
    }
  }

  "pointF" should {
    "compose lift" in {
      (Some(4) >>>:: Kleisli[Id, Int, Int](_ * 2).pointF[Option]) must be(Some(8))
    }
  }
}
