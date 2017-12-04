package com.kreactive.data

import scala.collection.immutable.Seq
/**
  * Created by cyrille on 02/01/2017.
  */
trait Emptyable[T] {
  def empty: T
}

object Emptyable {
  def apply[T](e: T) = new Emptyable[T] { val empty = e }

  implicit val zero: Emptyable[Int] = Emptyable[Int](0)
  implicit def none[T]: Emptyable[Option[T]] = Emptyable[Option[T]](None)
  implicit def nil[T]: Emptyable[List[T]] = Emptyable[List[T]](Nil)
  implicit def seq[T]: Emptyable[Seq[T]] = Emptyable[Seq[T]](Seq())
  implicit def emptyMap[K, T]: Emptyable[Map[K, T]] = Emptyable[Map[K, T]](Map())
}
