package com.omd.service.users.algebras

import com.omd.service.users.domain.Entity

abstract class Aggregate[F[_], E <: Entity, Evt <: Event[E], C <: Command[E]] {
  def apply[EE <: E]: EE ⇒ F[Unit]

  def handle[CC <: C]: CC ⇒ F[Unit]
}
