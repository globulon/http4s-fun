package com.omd.service.http

import io.circe._
import io.circe.literal._
import io.circe.syntax._

private[http] trait DTOs {
  implicit val messageTypeEncoder: Encoder[MessageType] = Encoder.instance {
    case Info  ⇒ "info".asJson
    case Error ⇒ "error".asJson
  }

  implicit val messageTypeDecoder: Decoder[MessageType] = (c: HCursor) ⇒
    c.value.as[String].flatMap {
      case "info"  ⇒ Right(Info)
      case "error" ⇒ Right(Error)
      case other   ⇒ Left(DecodingFailure(s"Invalid message type $other", List.empty))
    }

  implicit val messageEncoder: Encoder[Message] =
    Encoder.instance { case Message(typ, body) ⇒ json"""{"type": $typ, "name": $body}""" }

  implicit val messageDecoder: Decoder[Message] = (c: HCursor) ⇒
    for {
      typ  ← c.downField("type").as[MessageType]
      name ← c.downField("name").as[String]
    } yield Message(typ, name)
}
