### HTTP4S Fun

Playing around with Http4s and try to attach an Error handling mechanism
base on the introduction a type error channel

```scala
trait ErrorChannel[F[_], E <: Throwable] {
  def raise[A](e: E): F[A]

  def fold[A](a: Try[A])(implicit EG: ErrGenerator[Throwable, E]): F[A]

  def recover[A](fa: F[A])(f: E ⇒ F[A]): F[A]
}
``` 

Paired with an handling mechanism

```scala
trait ErrorHandler[F[_], E <: Throwable] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}
```

The usage of this handling mechanism forces the application of strongly typed error raise and 
not of a degnerated `throwable` instance losing track of the real domain error

```scala
override def create: UserDefinition ⇒ M[Unit] = { case d @ UserDefinition(_) ⇒
  findBy(d).flatMap {
    case Some(User(_, name)) ⇒ EC.raise(UserAlreadyExists(name))
    case None                ⇒ users.update(makeUser(d))
  }
}
```

The implementation of the error handler :

```scala
private[http] trait ErrorHandlers {
  implicit def handleUserErrors[F[_]: Sync](implicit AE: ApplicativeError[F, UserError]): ErrorHandler[F, UserError] =
    new ErrorHandler[F, UserError] with Http4sDsl[F] {

      private val h = makeHandle[F, UserError] {
        case e @ UserAlreadyExists(_) ⇒ Conflict(Message(http.Error, Show[UserAlreadyExists].show(e)))
      }

      override def handle(routes: HttpRoutes[F]): HttpRoutes[F] = h(routes)
    }
}
```

makes it easy to catch and handle any domain error in the REST service

```scala
final private[users] class Entities[F[_]](users: Users[F])(implicit S: Effect[F], EH: ErrorHandler[F, UserError])
  extends Http4sDsl[F] {
  private def service: HttpRoutes[F] = HttpRoutes.of[F] {
//...
  }

  def routes: HttpRoutes[F] = EH.handle(service)
}
```

#### Modules

The project is multi module project following the guidance of S.O.C principles.
The project includes the following modules

* Core services
* Users entity


#### Running the project


##### Run code

run the sbt console :

```bash
$ sbt -mem 2048
```

select the users project 

```sbtshell
sbt:services> project users
```

run 

```sbtshell
sbt:users> run
```

### Route to service 
```bash
curl -verbose  "http://localhost:8080/index" -H "Accept:application/json"
```

```bash
curl -verbose "http://localhost:8080/users" -H "Accept:application/json" 
```

```bash
curl -verbose -X POST "http://localhost:8080/user" -H "Accept:application/json" -H "Content-Type:application/json" -d '{ "name" : "Andrea" }' 
```

```bash
curl -verbose "http://localhost:8080/user/1" -H "Accept:application/json"
```

#### Code coverage

```bash
$ sbt clean coverage test coverageReport coverageAggregate
```
