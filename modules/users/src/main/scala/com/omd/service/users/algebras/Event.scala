package com.omd.service.users.algebras

import java.time.ZonedDateTime

import com.omd.service.users.domain.Entity

trait Event[+E <: Entity] {
  def at: ZonedDateTime
}
