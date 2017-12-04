package com.kreactive.capsule.play

import com.kreactive.capsule.StringValueClass
import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}
import play.api.mvc.{PathBindable, QueryStringBindable}

/**
  * Created by cyrille on 27/01/2017.
  */
case class Key(k: String) extends AnyVal

object Key extends StringValueClass[Key] with PlayValueClass[String, Key] {
  override def construct: (String) => Key = apply
  override def deconstruct: (Key) => String = _.k
}

class PlayValueClassSpec extends TestSuite with WordSpecLike with MustMatchers {

  "a play value class" should {
    "bind transparently from a path" in {
      implicitly[PathBindable[Key]].bind("key", "value") mustBe Right(Key("value"))
    }
    "unbind transparently to a path segment" in {
      implicitly[PathBindable[Key]].unbind("key", Key("value")) mustBe "value"
    }
    "bind transparently from a queryString" in {
      implicitly[QueryStringBindable[Key]].bind("key", Map("key" -> Seq("value"))) mustBe Some(Right(Key("value")))
    }
    "unbind transparently to a queryString" in {
      implicitly[QueryStringBindable[Key]].unbind("key", Key("value")) mustBe "key=value"
    }
  }
}
