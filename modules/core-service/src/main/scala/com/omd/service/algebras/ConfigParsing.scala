package com.omd.service.algebras

import cats._
import com.omd.fp.TerminalK

trait ConfigParsing[Config, M[_], E] {
  def parse[A: GetAttributeValue[Config, ?]](key: String): Config ⇒ M[A]

  def parse[A: GetAttributeValue[Config, ?]](key: String, orElse: ⇒ A): Config ⇒ M[A]

  def parse[N[_]: Applicative: TerminalK, A: GetAttributeValue[Config, ?]](key: String): Config ⇒ M[N[A]]
}

object ConfigParsing {
  def apply[Config, M[_], E](implicit CP: ConfigParsing[Config, M, E]): ConfigParsing[Config, M, E] = CP
}

trait GetAttributeValue[Config, A] extends (String ⇒ Config ⇒ A)

object GetAttributeValue {
  def apply[Config, A](implicit C: GetAttributeValue[Config, A]): GetAttributeValue[Config, A] = C
}
