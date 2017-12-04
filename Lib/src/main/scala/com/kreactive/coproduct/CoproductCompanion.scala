package com.kreactive.coproduct

import com.kreactive.util.{ClassTager, PairUtils}
import play.api.libs.json._

import scala.reflect.ClassTag
import scala.util.Try

/**
  * A helper trait for coproduct of (case) types serialization.
  * Usage example :
  *
  * trait MainType
  *
  * case class Foo(t: String) extends MainType
  * case class Bar(t: Int) extends MainType
  * case class EmptyCaseClass() extends MainType
  * case object CaseObject extends MainType
  *
  * object MainType extends CoproductCompanion[MainType] {
  *   implicit val format = makeOFormat(Json.format[Foo], Json.format[Bar], EmptyCaseClass(), CaseObject)
  * }
  *
  * Serialization example
  *
  * <code>
  *   Json.toJson(Foo("foo"): MainType) = {type: "Foo", data: {t: "foo"}}
  * </code>
  */
trait CoproductCompanion[T] extends CoproductLowerPriorityImplicits[T] with PairUtils {
  import scala.language.implicitConversions
  // condition on S =!= T to avoid infinite loop
  implicit def formatSubType[S <: T: ClassTag](S: Format[S])(implicit s: S =!= T): OFormat[T] = OFormat[T](readSubType(S), writeSubType(S))

  implicit def formatEmptySubType[S <: T with Product with Serializable: ClassTag](s: S): OFormat[T] =
    OFormat[T](readEmptySubType(s), writeEmptySubType(s))

  protected final def makeReads(readList: Reads[T]*) = readList.reduce(_ orElse _)
  protected final def makeOWrites(writeList: OWrites[T]*)(implicit T: ClassTag[T]) =
    OWrites[T]{t =>
      val res = writeList.map(_.writes(t)).reduce(_ ++ _)
      if (res == Json.obj())
        throw new NotImplementedError(s"OWrites[${typeFromTag[T]}] is incomplete: cannot serialize $t: ${t.getClass.getSimpleName}")
      else res
    }
  protected final def makeOFormat(formatList: OFormat[T]*)(implicit T: ClassTag[T]) = OFormat(makeReads(formatList: _*), makeOWrites(formatList: _*))
}

trait CoproductLowerPriorityImplicits[T] extends ClassTager {
  import scala.language.implicitConversions
  private val typedFormat = Json.format[TypedData]

  implicit def readEmptySubType[S <: T with Product with Serializable: ClassTag](s: S): Reads[T] =
    readSubType[S](Reads.pure(s))
  implicit def writeEmptySubType[S <: T with Product with Serializable: ClassTag](s: S): OWrites[T] =
    OWrites(t => if (t == s) typedFormat.writes(TypedData(JsNull, typeFromTag[S])) else Json.obj())

  implicit def readSubType[S <: T: ClassTag](S: Reads[S]): Reads[T] = Reads[T](js => for {
    typed <- js.validate(typedFormat)
    if typed.t == typeFromTag[S]
    s <- typed.d.validate(S)
  } yield s: T)
  implicit def writeSubType[S <: T: ClassTag](S: Writes[S]): OWrites[T] = OWrites[T]{ t =>
    Try(t.asInstanceOf[S]).map(s => typedFormat.writes(TypedData(S.writes(s), typeFromTag[S]))).getOrElse(Json.obj())
  }

  implicit def inferFormat(implicit reads: Reads[T], writes: OWrites[T]) = OFormat(reads, writes)
}

private[coproduct] case class TypedData(d: JsValue, t: String)

private[coproduct] object TypedData {
  implicit val typedFormat = Json.format[TypedData]
}


// Imported from shapeless. This ensures that the two types are different.
// If the types happen to be equal, two implicit values will be available and chaos will follow
sealed class =!=[A,B]

trait LowerPriorityImplicits {
  implicit def equal[A]: =!=[A, A] = sys.error("should not be called")
}
object =!= extends LowerPriorityImplicits {
  implicit def nequal[A,B](implicit same: A =:= B = null): =!=[A,B] =
    if (same != null) sys.error("should not be called explicitly with same type")
    else new =!=[A,B]
}
