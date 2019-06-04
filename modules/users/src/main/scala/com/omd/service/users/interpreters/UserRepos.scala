package com.omd.service.users.interpreters

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import com.omd.service.errors.ErrorChannel
import com.omd.service.users.algebras.Users
import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.errors.{MissingUser, UserAlreadyExists, UserError}

private[interpreters] trait UserRepos {
  final def users[F[_]: Sync](implicit EC: ErrorChannel[F, UserError]): F[Users[F]] =
    Ref.of[F, Map[Long, User]](Map.empty).map { users ⇒
      new Users[F] {
        override def findById: Long ⇒ F[User] = id ⇒ users.get.map(_.get(id)) flatMap {
          case Some(u) ⇒ Sync[F].pure(u)
          case None    ⇒ EC.raise(MissingUser(id))
        }

        private def findBy: UserDefinition ⇒ F[Option[User]] = {
          case UserDefinition(name) ⇒ users.get.map(_.values.find(_.name === name))
        }

        override def findAll: F[List[User]] = users.get.map(_.values.toList)

        override def create: UserDefinition ⇒ F[Unit] = { case d @ UserDefinition(_) ⇒
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
