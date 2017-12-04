package com.kreactive.coproduct

import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}
import play.api.libs.json.{JsNull, JsResultException, Json}

class CoproductCompanionSpec extends TestSuite with WordSpecLike with MustMatchers {

  trait MainType

  case class Foo(str: String) extends MainType
  case class Bar(num: Int) extends MainType
  case class Empty() extends MainType
  case object EmptyObject extends MainType
  case object Forgotten extends MainType

  object MainType extends CoproductCompanion[MainType] {
    //TODO Implicit resolution inconsistencies with those two lines.
//         implicit val reads = makeReads(Json.reads[Foo], Json.reads[Bar], Empty(), EmptyObject)
//         implicit val writes = makeOWrites(Json.writes[Foo], Json.writes[Bar], Empty(), EmptyObject)
    implicit val format = makeOFormat(Json.format[Foo], Json.format[Bar], Empty(), EmptyObject)
  }

  val foo = Foo("foo")
  val bar = Bar(0)
  val fooJson = Json.toJson(TypedData(Json.obj("str" -> "foo"), "Foo"))
  val barJson = Json.toJson(TypedData(Json.obj("num" -> 0), "Bar"))
  val emptyJson = Json.toJson(TypedData(JsNull, "Empty"))
  val emptyObjectJson = Json.toJson(TypedData(JsNull, "EmptyObject$"))
  val forgottenJson = Json.toJson(TypedData(JsNull, "Forgotten$"))

  "a coproduct companion" should {
    "serialize its elements coherently" in {
      Json.toJson(foo) mustBe fooJson
      Json.toJson(bar) mustBe barJson
      Json.toJson(Empty()) mustBe emptyJson
      Json.toJson(EmptyObject) mustBe emptyObjectJson
      assertThrows[NotImplementedError](Json.toJson(Forgotten))
    }

    "deserialize its elements coherently" in {
      fooJson.as[MainType] mustBe foo
      barJson.as[MainType] mustBe bar
      emptyJson.as[MainType] mustBe Empty()
      emptyObjectJson.as[MainType] mustBe EmptyObject
      assertThrows[JsResultException](forgottenJson.as[MainType])
    }
  }
}
