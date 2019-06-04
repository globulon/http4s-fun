package com.omd.service.users.errors

import com.omd.service.errors.AppErr

sealed abstract class UserError()               extends AppErr
final case class UserAlreadyExists(msg: String) extends UserError()
final case class MissingUser(id: Long) extends UserError()
