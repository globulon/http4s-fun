package com.omd.service.algebras

import com.omd.service.domain.Server

trait Configuration[Config, M[_], N[_]] {
  def server: Config ⇒ M[Server]
}
