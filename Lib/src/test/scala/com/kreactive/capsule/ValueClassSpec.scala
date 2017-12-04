package com.kreactive.capsule

import com.kreactive.data.Tree
import com.kreactive.generator.Gen
import com.typesafe.config.ConfigFactory
import play.api.libs.json._
import configs.syntax._
import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}

import scala.util.Random

/**
  * Created by cyrille on 10/03/2017.
  */
case class Caps(value: Int) extends AnyVal

object Caps extends ValueClass[Int, Caps] {
  override def construct: (Int) => Caps = apply
  override def deconstruct: (Caps) => Int = _.value
}

class ValueClassSpec extends TestSuite with MustMatchers with WordSpecLike {
  val caps = Caps(5)
  val int = 5
  val json = JsNumber(5)
  val config = ConfigFactory.parseString("value = 5")

  "a value class" should {

    "expose a json serializer" in {
      Json.toJson(caps) mustBe json
    }
    "expose a json deserializer" in {
      val des = json.validate[Caps]
      des.isSuccess mustBe true
      des.get mustBe caps
    }
    "expose a config deserializer" in {
      val des = config.get[Caps]("value")
      des.isSuccess mustBe true
      des.value mustBe caps
    }

    "expose an order on capsules if one exists on values" in {
      Caps(5) must be < Caps(10)
    }

    "expose a generator for capsule if one exists for values" in {
      implicit val intGen: Gen[Int] = Gen.from(Random.nextInt())
      Caps.generate() mustBe a[Caps]
    }
  }

}
