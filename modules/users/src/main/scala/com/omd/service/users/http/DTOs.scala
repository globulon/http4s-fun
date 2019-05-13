package com.omd.service.users.http

import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.domain.{User, UserDefinition}
import io.circe._
import io.circe.literal._

private[http] trait DTOs {
  implicit val UserEncoder: Encoder[User] =
    Encoder.instance { case User(id, name) ⇒ json"""{"id": $id, "name": $name}""" }

  implicit val UserDecoder: Decoder[User] = (c: HCursor) ⇒
    for {
      id ← c.downField("id").as[Long]
      nm ← c.downField("name").as[String]
    } yield User(id, nm)

  implicit val UserDefEncoder: Encoder[UserDefinition] =
    Encoder.instance { case UserDefinition(name) ⇒ json"""{ "name": $name}""" }

  implicit val UserDefDecoder: Decoder[UserDefinition] = (c: HCursor) ⇒ c.downField("name").as[String] map UserDefinition
}
