package com.omd.service.users.http

import com.omd.fp._
import com.omd.service.domain.Cors
import org.http4s.server.middleware.CORSConfig
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.concurrent.duration._

final class TranslationsTest extends WordSpecLike with MustMatchers {
  "tranlate cors" should {
    "translate to http4s cors" in {
      Cors(origin = "http://alpha.com").to[CORSConfig] must be(expectd)
    }
  }

  private def expectd = CORSConfig(
    anyOrigin = false,
    allowedOrigins = Set("http://alpha.com"),
    anyMethod = false,
    allowedMethods = Some(Set("GET", "POST", "PUT", "DELETE", "OPTIONS")),
    allowCredentials = true,
    maxAge = 1.day.toSeconds
  )
}
