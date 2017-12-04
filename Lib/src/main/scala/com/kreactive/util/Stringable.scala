package com.kreactive.util

/**
  * Created by cyrille on 08/03/2017.
  */
trait Stringable[K] {
  def parse: PartialFunction[String, K]
  def asString: K => String

}

object Stringable {
  def apply[K](ts: K => String)(p: PartialFunction[String, K]): Stringable[K] = new Stringable[K] {
    override val asString: (K) => String = ts
    override val parse: PartialFunction[String, K] = p
  }

  implicit def toStringMap[K, V](m: Map[K, V])(implicit K: Stringable[K]): Map[String, V] = m.map{
    case (k, v) => K.asString(k) -> v
  }

  implicit def toStringableMap[K, V](m: Map[String, V])(implicit K: Stringable[K]): Map[K, V] = m.collect{
    case (k, v) if K.parse isDefinedAt k => K.parse(k) -> v
  }
}
