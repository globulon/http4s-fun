package com.omd.service.users.algebras

import com.omd.service.users.domain.Entity

trait Repo[F[_], E <: Entity] {
  def findById: Long ⇒ F[E]
}
