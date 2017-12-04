package com.kreactive.capsule

import java.util.UUID

import com.typesafe.config.ConfigValue
import configs.Configs
import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}
import play.api.libs.json.{Format, JsSuccess, Json}

import scala.reflect.ClassTag

/**
  * Created by cyrille on 24/11/2016.
  */
trait ValueClassTestKit extends MustMatchers {
  self: TestSuite with WordSpecLike =>

  val uuidValue = UUID.fromString("12345678-1234-1234-1234-123456789abc")

  def testValueClass[V: ClassTag, C](name: String, companion: ValueClass[V, C], testValue: V, testCapsule: C, sameToString: Boolean = true, wrongValue: Option[V] = None)(implicit V: ClassTag[V]) =
    s"a $name value class" should {
      import companion._

      s"construct then deconstruct to the initial value" in {
        deconstruct(construct(testValue)) mustBe testValue
      }

      s"deconstruct then construct to the initial capsule" in {
        construct(deconstruct(testCapsule)) mustBe testCapsule
      }

      if (sameToString) {
        s"have the same string representation as its value" in {
          construct(testValue).toString mustBe testValue.toString
        }
      }

      wrongValue.foreach{wv =>
        s"throw an error on wrong values" in {
          if (condition(wv))
            fail(s"The given wrongValue satisfy the condition: $wv")
          else
            assertThrows(construct(wv))
        }
      }
    }

  def testValueClassConfigs[V: ClassTag: Configs, C](name: String, companion: ValueClass[V, C], testValue: V, configValue: ConfigValue, wrongConfig: Option[ConfigValue] = None)(implicit V: ClassTag[V]) =
    s"a $name value class Configs" should {
      import companion._
      val vName = V.runtimeClass.getSimpleName

      s"transparently be fetched from config as a $vName" in {
        Configs[V].extractValue(configValue).map(construct) mustBe Configs[C].extractValue(configValue)
      }

      wrongConfig.foreach{ wc =>
        s"give a config error on wrong value" in {
          Configs[V].extractValue(wc).map(condition) mustBe Configs.successful(false)
          if (Configs[C].extractValue(wc).isSuccess)
            fail(s"The given wrongValue should not be parseable from config: $wc")
        }
      }
    }

  def testValueClassFormat[V: ClassTag : Format, C](name: String, companion: ValueClass[V, C], testValue: V, wrongValue: Option[V] = None)(implicit V: ClassTag[V]) =
    s"a $name value class Format" should {
      import companion._
      val vName = V.runtimeClass.getSimpleName
      s"transparently serialize as a $vName" in {
        Json.toJson(construct(testValue)) mustBe Json.toJson(testValue)
      }
      s"transparently deserialize as a $vName" in {
        Json.toJson(testValue).validate[C] mustBe JsSuccess(construct(testValue))
      }
      wrongValue.foreach{ wv =>
        s"give a validation error on wrong value" in {
          if (condition(wv))
            fail(s"The given wrongValue satisfy the condition: $wv")
          else if (Json.toJson(wv).validate[C].isSuccess)
            fail(s"The given wrongValue should not be parseable from json: $wv")
        }
      }
    }
}
