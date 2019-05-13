package com.omd.fp

import cats.data.Kleisli
import cats.{Applicative, ApplicativeError, Functor, Monad, Traverse}

private[fp] trait Kleislis {
  implicit def kleisliOps[M[_], A, B](k: Kleisli[M, A, B]): KleisliOps[M, A, B] = new KleisliOps(k)
}

final class KleisliOps[M[_], A, B](val k: Kleisli[M, A, B]) extends AnyVal {
  def |||[E](other: ⇒ Kleisli[M, A, B])(implicit ME: ApplicativeError[M, E]): Kleisli[M, A, B] = Kleisli { a ⇒
    ME.handleErrorWith(k.run(a)) { _ ⇒
      other.run(a)
    }
  }

  def >=>[C](next: Kleisli[M, B, C])(implicit M: Monad[M]): Kleisli[M, A, C] = Kleisli { a ⇒
    M.flatMap(k(a))(next.run)
  }

  //Allows to compose a Kleisli with an A => M[B] function only
  def >==>[C](next: B ⇒ M[C])(implicit M: Monad[M]): Kleisli[M, A, C] = Kleisli { a ⇒
    M.flatMap(k(a))(next)
  }

  def =<<(input: M[A])(implicit M: Monad[M]): M[B] = M.flatMap(input)(k.run)

  def =<<<(input: A)(implicit M: Monad[M]): M[B] = M.flatMap(M.point(input))(k.run)

  //Because reading from left to right is more intuitive
  def >>::(input: M[A])(implicit M: Monad[M]): M[B] = =<<(input)

  //Because reading from left to right is more intuitive
  def >>>::(input: A)(implicit M: Monad[M]): M[B] = =<<<(input)

  /**
    * Helps keeping operations definition on type level by pointing the input/output args of a 'Kleisli[M, A, B]'
    * to 'Kleisli[M, N[A], N[B] ]' where N is referenced by Functor+Traverse typeclasses
    */
  def pointF[N[_]: Functor: Traverse](implicit A: Applicative[M]): Kleisli[M, N[A], N[B]] =
    Kleisli[M, N[A], N[B]] { na ⇒
      Traverse[N].sequence[M, B](Functor[N].map(na)(k.run))
    }
}
