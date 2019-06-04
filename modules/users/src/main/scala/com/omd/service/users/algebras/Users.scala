package com.omd.service.users.algebras

import com.omd.service.users.domain.{User, UserDefinition}

trait Users[F[_]] extends Repo[F, User]{
  def findById: Long ⇒ F[Option[User]]

  def findBy: UserDefinition ⇒ F[Option[User]]

  def findAll: F[List[User]]

  def create: UserDefinition ⇒ F[Unit]
}
