package com.omd.service.http

import cats.effect.Sync
import io.circe._
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

private[http] trait JsonCodecs {
  implicit final def encodeJson[F[_]: Sync, A <: Product: Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  implicit final def decodeJson[F[_]: Sync, A <: Product: Decoder]: EntityDecoder[F, A] =
    jsonOf[F, A]
}
