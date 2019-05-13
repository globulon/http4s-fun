package com.omd.service.interpreters

import cats.Applicative
import cats.implicits._
import com.omd.fp.TerminalK
import com.omd.service.algebras.{ConfigParsing, GetAttributeValue}
import com.omd.service.errors.{AppErr, ErrGenerator, ErrorChannel}
import com.typesafe.config.Config

import scala.collection.JavaConverters._
import scala.util.Try

private[interpreters] trait ConfigParsers {

  implicit final def configParser[M[_]: Applicative, E <: AppErr](implicit GE: ErrGenerator[Throwable, E],
                                                                  EC: ErrorChannel[M, E]): ConfigParsing[Config, M, E] =
    new ConfigParsing[Config, M, E] {
      override def parse[A: GetAttributeValue[Config, ?]](key: String): Config ⇒ M[A] =
        c ⇒ EC.fold[A](Try(GetAttributeValue[Config, A].apply(key)(c)))

      override def parse[A: GetAttributeValue[Config, ?]](key: String, orElse: ⇒ A): Config ⇒ M[A] =
        c ⇒ EC.recover(parse[A](key).apply(c))(_ ⇒ Applicative[M].point(orElse))

      override def parse[N[_]: Applicative: TerminalK, A: GetAttributeValue[Config, ?]](key: String): Config ⇒ M[N[A]] =
        c ⇒ EC.recover(parse[A](key).apply(c).map(Applicative[N].pure[A]))(_ ⇒ Applicative[M].pure(TerminalK[N].one[A]))
    }

  final def getAttribute[Conf, A](f: String ⇒ Conf ⇒ A): GetAttributeValue[Conf, A] = (v1: String) ⇒ f(v1)

  implicit def getInt: GetAttributeValue[Config, Int] = getAttribute { k ⇒
    _.getInt(k)
  }

  implicit def getString: GetAttributeValue[Config, String] = getAttribute { k ⇒
    _.getString(k)
  }

  implicit def getConfigList: GetAttributeValue[Config, List[Config]] = getAttribute { k ⇒
    _.getConfigList(k).asScala.toList
  }

  implicit def getConfig: GetAttributeValue[Config, Config] = getAttribute { k ⇒
    _.getConfig(k)
  }
}
