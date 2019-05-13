package com.omd.service.http

sealed trait MessageType

case object Error extends MessageType
case object Info  extends MessageType

final case class Message(`type`: MessageType, description: String) extends DTO
