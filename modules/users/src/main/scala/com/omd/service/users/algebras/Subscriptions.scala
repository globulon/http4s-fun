package com.omd.service.users.algebras

import com.omd.service.users.domain.Subscription

trait Subscriptions[F[_]] extends Repo[F, Subscription]