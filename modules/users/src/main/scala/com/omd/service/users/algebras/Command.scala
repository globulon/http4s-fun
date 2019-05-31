package com.omd.service.users.algebras

import com.omd.service.users.domain.Entity

trait Command[+E <: Entity]
