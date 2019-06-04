package com.omd.service.users.domain

final case class UserDefinition(name: String)
final case class User(override val id: Long, name: String) extends Entity
