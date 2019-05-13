package com.omd.service.interpreters

import cats.data._
import cats.implicits._
import cats.scalatest.EitherMatchers
import com.omd.service.domain.{Bindings, Cors, Server}
import com.omd.service.errors._
import com.omd.service.errors.{Captured, ConfigError}
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseResources
import org.scalatest.{MustMatchers, WordSpecLike}

final class ConfigurationsTest extends WordSpecLike with MustMatchers with EitherMatchers {
  type Errors      = Captured[ConfigError]
  type ErrorOr[A]  = Validated[Errors, A]
  type ErrorOrE[A] = Either[Errors, A]

  "complete configuration" should {
    "be loaded" in {
      configuration[ErrorOrE, ErrorOr, Errors].server(parseResources("server.conf")) must beRight(
        Server(Bindings("0.0.0.0", 8888), Cors("http://granted.com"))
      )
    }

    "fail invalid parameters" in {
      configuration[ErrorOrE, ErrorOr, Errors].server(ConfigFactory.parseString(invalidParams)) must beLeft[Throwable](
        captured(ConfigError("No configuration setting found for key 'bindings.host'"),
                      ConfigError("No configuration setting found for key 'cors.origin'")))
    }
  }

  private def invalidParams: String =
    """
      |{
      |  cors {
      |    orgin: "http://granted.com"
      |  }
      |
      |  bindings {
      |    hst: "0.0.0.0"
      |    port: 8888
      |  }
      |}
    """.stripMargin
}
