package com.omd.service.users

import cats.effect.IO
import cats.scalatest.EitherMatchers
import com.olegpy.meow.hierarchy._
import com.omd.service.errors._
import com.omd.service.fixture.Safe
import com.omd.service.http.{Message ⇒ Msg, _}
import com.omd.service.users.domain.{User, UserDefinition}
import com.omd.service.users.errors.{UserAlreadyExists, UserError}
import com.omd.service.users.http._
import com.omd.service.users.interpreters._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.scalatest.{MustMatchers, WordSpecLike}


final class EntitiesTest extends WordSpecLike with MustMatchers with EitherMatchers with Http4sDsl[IO] {
  "index route" should {
    "deliver welcome message" in {
      routes(emptyUser).run(getIndex).to[Safe].effect.value.map(_.status) must beRight(Status.Ok)
      routes(emptyUser).run(getIndex).flatMap(_.as[Msg]).to[Safe].effect.value must beRight(
        Msg(Info, "Welcome to user service"))
    }
  }

  "user route" should {
    "return no not found" in {
      routes(emptyUser).run(getUser(7L)).to[Safe].effect.value.map(_.status) must beRight(Status.NotFound)
      routes(emptyUser).run(getUser(7L)).flatMap(_.as[Msg]).to[Safe].effect.value must beRight(
        Msg(Error, s"User [7] not found"))
    }

    "return a user" in {
      routes(predictableUser).run(getUser(11L)).to[Safe].effect.value.map(_.status) must beRight(Status.Ok)
      routes(predictableUser).run(getUser(11L)).flatMap(_.as[User]).to[Safe].effect.value must beRight(User(11, "name-11"))
    }
  }

  "Post user" should {
    "return created" in {
      (for {
        srv     ← users[IO]
        _       ← routes(srv).run(postUser)
        found   ← routes(srv).run(getUser(1L))
        user    ← found.as[User]
      } yield user).to[Safe].effect.value must beRight(User(1L, s"exists"))
    }

    "return conflict" in {
      (for {
        srv     ← users[IO]
        _       ← routes(srv).run(postUser)
        status  ← routes(srv).run(postUser).map(_.status)
      } yield status).to[Safe].effect.value must beRight(Status.Conflict)
    }

    "return conflict message" in {
      (for {
        srv  ← users[IO]
        _    ← routes(srv).run(postUser)
        conf ← routes(srv).run(postUser)
        msg  ← conf.as[Msg]
      } yield msg).to[Safe].effect.value must beRight(Msg(Error, "User exists already exists"))
    }

    "return all messages" in {
      (for {
        srv   ← users[IO]
        _     ← routes(srv).run(postUser(name = "a"))
        _     ← routes(srv).run(postUser(name = "b"))
        _     ← routes(srv).run(postUser(name = "c"))
        all   ← routes(srv).run(getUsers)
        users ← all.as[List[User]]
      } yield users).to[Safe].effect.value must beRight(List(User(1L, "a"), User(2L, "b"), User(3L, "c")))
    }
  }

  "users route" should {
    "return empty list" in {
      routes(emptyUser).run(getUsers).to[Safe].effect.value.map(_.status) must beRight(Status.Ok)
      routes(emptyUser).run(getUsers).flatMap(_.as[List[User]]).to[Safe].effect.value must beRight(List.empty[User])
    }

    "return non empty list" in {
      routes(predictableUser).run(getUsers).to[Safe].effect.value.map(_.status) must beRight(Status.Ok)
      routes(predictableUser).run(getUsers).flatMap(_.as[List[User]]).to[Safe].effect.value must beRight(users)
    }
  }

  private def users = List(User(1, "name-1"), User(2, "name-2"), User(3, "name-3"))

  private def getUsers: Request[IO] = Request(GET, uri = Uri(path = s"/users"))

  private def postUser: Request[IO] = Request(POST, uri = Uri(path = "user")).withEntity(UserDefinition("exists"))

  private def postUser(name: String): Request[IO] = Request(POST, uri = Uri(path = "user")).withEntity(UserDefinition(name))

  private def getUser(l: Long): Request[IO] = Request(GET, uri = Uri(path = s"/user/$l"))

  private def getIndex: Request[IO] = Request(GET, uri = Uri.uri("/index"))

  private def routes(users: algebras.Users[IO]): Http[IO, IO] = new Entities[IO](users).routes.orNotFound

  private def emptyUser: algebras.Users[IO] = new algebras.Users[IO] {
    override def findById: Long ⇒ IO[Option[User]]         = _ ⇒ IO(None)
    override def findAll: IO[List[User]]                   = IO(List.empty)
    override def findBy: UserDefinition ⇒ IO[Option[User]] = _ ⇒ IO(None)
    override def create: UserDefinition ⇒ IO[Unit]         = _ ⇒ ErrorChannel[IO, UserError].raise(UserAlreadyExists("unexpected"))
  }

  private def predictableUser: algebras.Users[IO] = new algebras.Users[IO] {
    override def findById: Long ⇒ IO[Option[User]]         = id ⇒ IO(Some(User(id, s"name-$id")))
    override def findAll: IO[List[User]]                   = IO(users)
    override def findBy: UserDefinition ⇒ IO[Option[User]] = _ ⇒ IO(None)
    override def create: UserDefinition ⇒ IO[Unit]         = _ ⇒ ErrorChannel[IO, UserError].raise(UserAlreadyExists("unexpected"))
  }
}
