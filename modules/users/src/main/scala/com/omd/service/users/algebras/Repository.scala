package com.omd.service.users.algebras

import com.omd.service.users.domain.Entity

trait Repository[F[_], ID, E <: Entity] {
  def findById: ID ⇒ F[Option[E]]
}
