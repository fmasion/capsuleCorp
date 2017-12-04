package com.kreactive.enum

import com.kreactive.util.ClassTager
import play.api.libs.json._

import scala.reflect.ClassTag

/**
  * A helper trait for coproduct of case objects serialization.
  * Usage example :
  * <code>
  *   // this `extends` avoids some type errors on `values`
  * trait Sex extends Product with Serializable
  *
  * case object Male extends Sex
  * case object Female extends Sex
  *
  * object Sez extends EnumCompanion[Sex] {
  *   val values = Set(Male, Female)
  * }
  * </code>
  *
  * Serialization example
  *
  * <code>
  *   Json.toJson(Male) = JsString("Male")
  * </code>
  */
trait EnumCompanion[T] extends ClassTager {

  def values: Seq[T]

  def apply(name: String): Option[T] = values.find(_.toString == name)

  implicit def format(implicit T: ClassTag[T]) = new Format[T] {
    override def writes(o: T): JsValue = JsString(o.toString)
    override def reads(json: JsValue): JsResult[T] =
      json.validate[String].map(apply).collect(JsonValidationError(s"Invalid value in enum ${typeFromTag[T]}: ${json.as[String]}")){
        case Some(t) => t
      }
  }

  implicit val ordering = new Ordering[T] {
    override def compare(x: T, y: T): Int = values.indexOf(x) - values.indexOf(y)
  }
}
