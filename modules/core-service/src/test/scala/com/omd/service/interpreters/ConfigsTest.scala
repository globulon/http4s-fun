package com.omd.service.interpreters

import cats.data._
import cats.implicits._
import cats.scalatest.ValidatedMatchers
import com.omd.service.algebras.ConfigParsing
import com.omd.service.errors._
import com.omd.service.algebras.ConfigParsing
import com.omd.service.errors.{Captured, ConfigError}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{MustMatchers, WordSpecLike}

final class ConfigsTest extends WordSpecLike with MustMatchers with ValidatedMatchers {

  type Errors     = Captured[ConfigError]
  type ErrorOr[A] = Validated[Errors, A]

  protected lazy val parsing: ConfigParsing[Config, ErrorOr, Errors] = configParser[ErrorOr, Errors]
  import parsing._

  "getValue" should {
    "read string" in {
      parse[String]("foo.bar").apply(config) must beValid("42")
    }

    "read int" in {
      parse[Int]("foo.baz").apply(config) must beValid(43)
    }

    "read config" in {
      parse[Config]("foo.config").apply(config) must be('Valid)
      parse[Config]("foo.config").apply(config) map (_.getString("bass")) must beValid("found")
    }

    "read list of configs" in {
      parse[List[Config]]("foo.configs").apply(config) must be('Valid)
      parse[List[Config]]("foo.configs").apply(config) map (_.size) must beValid(2)
    }

    "fail missing value" in {
      parse[Int]("foo.missing").apply(config) must beInvalid[Throwable](
        captured(ConfigError("No configuration setting found for key 'foo.missing'")))
    }
  }

  "getValue Maybe" should {
    "read string" in {
      parse[Option, String]("foo.bar").apply(config) must beValid[Option[String]](Some("42"))
    }

    "read int" in {
      parse[Option, Int]("foo.baz").apply(config) must beValid[Option[Int]](Some(43))
    }

    "catch missing value" in {
      parse[Option, Int]("foo.missing").apply(config) must beValid[Option[Int]](None)
    }

  }

  "getValue with default" should {
    "getValue existing value" in {
      parse[Int]("foo.baz", orElse = 17).apply(config) must beValid(43)
    }

    "getValue missing value" in {
      parse[Int]("foo.bazz", orElse = 17).apply(config) must beValid(17)
    }
  }

  "errors" should {
    "cumulate" in {
      (parse[Int]("foo.missing").apply(config), parse[Int]("foo.missing").apply(config)).mapN {
        _ + _
      } must beInvalid[Throwable](
        captured(ConfigError("No configuration setting found for key 'foo.missing'"),
                 ConfigError("No configuration setting found for key 'foo.missing'")))
    }
  }

  private def config: Config = ConfigFactory.load("config.conf")

}
