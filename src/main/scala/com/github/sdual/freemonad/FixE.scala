package com.github.sdual.freemonad

import scalaz.Functor

sealed trait FixE[F[_], E]
object FixE {
  case class Fix[F[_], E](f: F[FixE[F, E]]) extends FixE[F, E]
  case class Throwy[F[_], E](e: E) extends FixE[F, E]

  def fix[E](toy: CharToy[FixE[CharToy, E]]): FixE[CharToy, E] = Fix[CharToy, E](toy)

  def throwy[F[_], E](e: E): FixE[F, E] = Throwy(e)

  def catchy[F[_]: Functor, E1, E2](ex: => FixE[F, E1])(f: E1 => FixE[F, E2]): FixE[F, E2] = ex match {
    case Fix(x) => Fix[F, E2](Functor[F].map(x) {catchy(_)(f)})
    case Throwy(e) => f(e)
  }

}
