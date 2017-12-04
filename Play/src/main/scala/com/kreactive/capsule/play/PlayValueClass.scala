package com.kreactive.capsule.play

import com.kreactive.capsule.ValueClass
import play.api.mvc.{PathBindable, QueryStringBindable}

/**
  * Created by cyrille on 27/01/2017.
  */
trait PlayValueClass[Value, Capsule] extends IsoPathBindable with IsoQueryStringBindable { self: ValueClass[Value, Capsule] =>
  implicit def valueClassPathBindable(implicit V: PathBindable[Value]): PathBindable[Capsule] =
    isoPathBindable(deconstruct, construct)
  implicit def valueClassQueryStringBindable(implicit V: QueryStringBindable[Value]): QueryStringBindable[Capsule] =
    isoQueryStringBindable(deconstruct, construct)

}

trait IsoPathBindable {
  def isoPathBindable[I, O](apply: I => O, unapply: O => I)(implicit OutPathBindable: PathBindable[O]): PathBindable[I] =
    new PathBindable[I] {
      override def bind(key: String, value: String): Either[String, I] = OutPathBindable.bind(key, value).right.map(unapply)

      override def unbind(key: String, value: I): String = OutPathBindable.unbind(key, apply(value))
    }
}

trait IsoQueryStringBindable {
  def isoQueryStringBindable[I, O](apply: I => O, unapply: O => I)(implicit O: QueryStringBindable[O]): QueryStringBindable[I] =
    new QueryStringBindable[I] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, I]] = O.bind(key, params).map(_.right.map(unapply))

      override def unbind(key: String, value: I): String = O.unbind(key, apply(value))
    }
}
