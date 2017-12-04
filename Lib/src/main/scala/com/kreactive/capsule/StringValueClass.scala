package com.kreactive.capsule

import com.kreactive.util.{MapUtils, Stringable}
import play.api.libs.json.Format

/**
  * Created by cyrille on 13/01/2017.
  */
trait StringValueClass[K] extends ValueClass[String, K] with MapUtils {
  implicit val stringable: Stringable[K] = Stringable[K](deconstruct)(PartialFunction(construct))
  implicit def keyFormat[V: Format] = mapFormat[K, V]
}