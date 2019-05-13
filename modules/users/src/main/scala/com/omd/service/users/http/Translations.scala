package com.omd.service.users.http

import com.omd.fp._
import com.omd.service.domain.Cors
import org.http4s.server.middleware.CORSConfig

import scala.concurrent.duration._

private[http] trait Translations {
  implicit final def translateCors: Cors |~~> CORSConfig = translate[Cors, CORSConfig] {
    case Cors(origin) ⇒
      CORSConfig(
        anyOrigin = false,
        allowedOrigins = Set(origin),
        anyMethod = false,
        allowedMethods = Some(Set("GET", "POST", "PUT", "DELETE", "OPTIONS")),
        allowCredentials = true,
        maxAge = 1.day.toSeconds
      )
  }
}
