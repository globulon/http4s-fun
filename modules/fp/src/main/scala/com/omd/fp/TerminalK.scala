package com.omd.fp

trait TerminalK[F[_]] {
  def one[A]: F[A]
}

object TerminalK {
  def apply[F[_]](implicit T: TerminalK[F]): TerminalK[F] = T
}

private[fp] trait Terminals {
  implicit val terminalOption: TerminalK[Option] = new TerminalK[Option] {
    override def one[A]: Option[A] = None
  }
}
