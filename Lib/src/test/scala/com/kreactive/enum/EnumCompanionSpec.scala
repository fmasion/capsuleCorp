package com.kreactive.enum

import org.scalatest.{MustMatchers, Suite, WordSpecLike}
import play.api.libs.json.{JsError, JsString, Json, JsonValidationError}

/**
  * Created by cyrille on 10/01/2017.
  */
class EnumCompanionSpec extends Suite with MustMatchers with WordSpecLike {

  sealed trait Sex extends Product with Serializable

  object Sex extends EnumCompanion[Sex] {
    case object Forgotten extends Sex
    case object Male extends Sex
    case object Female extends Sex

    val values = Seq(Male, Female)
  }
  "an enum companion" should {
    "get an object from its name, if it is defined in the enum" in {
      Sex("Male") mustBe Some(Sex.Male)
      Sex("Female") mustBe Some(Sex.Female)
      Sex("Forgotten") mustBe None
    }

    "serialize its elements as JsString" in {
      Json.toJson(Sex.Male) mustBe JsString("Male")
      Json.toJson(Sex.Female) mustBe JsString("Female")
    }

    "deserialize its elements from JsString" in {
      JsString("Male").validate[Sex].get mustBe Sex.Male
      JsString("Female").validate[Sex].get mustBe Sex.Female
      val undefined = JsString("Forgotten").validate[Sex]
      undefined.isError mustBe true
      undefined.asInstanceOf[JsError].errors.flatMap(_._2) must contain (JsonValidationError("Invalid value in enum Sex: Forgotten"))
    }

    "order its values" in {
      import Sex.ordering.mkOrderingOps

      assert(Sex.Male < Sex.Female)
      assert(Sex.Forgotten < Sex.Male)
      assert(Sex.Forgotten < Sex.Female)
    }
  }
}
