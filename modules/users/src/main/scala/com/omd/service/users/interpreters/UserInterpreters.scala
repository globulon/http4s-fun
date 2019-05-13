package com.omd.service.users.interpreters

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import com.omd.service.errors.ErrorChannel
import com.omd.service.users.algebras.Users
import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.errors.{UserAlreadyExists, UserError}
import com.omd.service.users.algebras.Users
import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.errors.{UserAlreadyExists, UserError}
import com.omd.service.users.algebras.Users
import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.errors.{UserAlreadyExists, UserError}

private[interpreters] trait UserInterpreters {
  final def userService[M[_]: Sync](implicit EC: ErrorChannel[M, UserError]): M[Users[M]] =
    Ref.of[M, Map[Long, User]](Map.empty).map { users ⇒
      new Users[M] {
        override def findById: Long ⇒ M[Option[User]] = byId ⇒ users.get.map(_.get(byId))

        override def findBy: UserDefinition ⇒ M[Option[User]] = {
          case UserDefinition(name) ⇒ users.get.map(_.values.find(_.name === name))
        }

        override def findAll: M[List[User]] = users.get.map(_.values.toList)

        override def create: UserDefinition ⇒ M[Unit] = { case d @ UserDefinition(_) ⇒
          findBy(d).flatMap {
            case Some(User(_, name)) ⇒ EC.raise(UserAlreadyExists(name))
            case None                ⇒ users.update(makeUser(d))
          }
        }

        private def makeUser: UserDefinition ⇒ Map[Long, User] ⇒ Map[Long, User] = {
          case UserDefinition(name) ⇒ users ⇒ users + ((users.size + 1L) → User(users.size + 1L, name))
        }
      }
    }
}
