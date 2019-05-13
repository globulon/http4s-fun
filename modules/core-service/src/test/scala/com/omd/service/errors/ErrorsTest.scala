package com.omd.service.errors

import cats.Show
import org.scalatest.{MustMatchers, WordSpecLike}

final class ErrorsTest extends WordSpecLike with MustMatchers {
  "show config error" should {
    "show info" in {
      Show[ConfigError].show(ConfigError("boom")) must be("[ConfigError] boom")
    }
  }
}
