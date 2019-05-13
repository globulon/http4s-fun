package com.omd.service.users.http

import cats.scalatest.EitherMatchers
import com.omd.service.users.domain.User
import io.circe.syntax._
import org.scalatest.{MustMatchers, WordSpecLike}

final class DTOsTest extends WordSpecLike with MustMatchers with EitherMatchers {
  "Encoding" should {
    "encode json" in {
      User(17L, "test").asJson.as[User] must beRight(User(17L, "test"))
    }
  }
}
