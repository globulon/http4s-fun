package com.omd.service.users.interpreters

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import com.omd.service.errors.ErrorChannel
import com.omd.service.users.algebras.Subscriptions
import com.omd.service.users.domain.Subscription
import com.omd.service.users.errors.{MissingSubscription, SubscriptionError}

private[interpreters] trait SubscriptionRepos {
  final def subs[F[_]: Sync](implicit EC: ErrorChannel[F, SubscriptionError]): F[Subscriptions[F]] =
    Ref.of[F, Map[Long, Subscription]](Map.empty).map { subs ⇒
      new Subscriptions[F] {
        override def findById: Long ⇒ F[Subscription] = id ⇒ subs.get.map(_.get(id)) flatMap {
          case Some(s) ⇒ Sync[F].pure(s)
          case _       ⇒ EC.raise(MissingSubscription(id))
        }
      }
    }
}
