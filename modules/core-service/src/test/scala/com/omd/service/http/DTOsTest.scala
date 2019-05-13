package com.omd.service.http

import cats.scalatest.EitherMatchers
import io.circe.syntax._
import org.scalatest.{MustMatchers, WordSpecLike}

final class DTOsTest extends WordSpecLike with MustMatchers with EitherMatchers {
  "Encoding" should {
    "encode info msg" in {
      Message(Info, "test").asJson.as[Message] must beRight(Message(Info, "test"))
    }
    "encode error msg" in {
      Message(Error, "test").asJson.as[Message] must beRight(Message(Error, "test"))
    }
  }
}
