package com.omd.service.users.errors

import com.omd.service.errors.AppErr

sealed abstract class SubscriptionError() extends AppErr
final case class MissingSubscription(id: Long) extends SubscriptionError