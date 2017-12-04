package com.kreactive.extractor

import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}
import play.api.libs.json.{JsString, Json}

/**
  * Created by cyrille on 11/01/2017.
  */
class JsSpec extends TestSuite with MustMatchers with WordSpecLike{

  case class Foo(foo: String)

  object Foo {
    implicit val reads = Json.reads[Foo]
    lazy val js = Js[Foo]
  }

  val fooJson = Json.obj("foo" -> "bar")
  val foo = Foo("bar")

  "a Js extractor" should {
    "match on a valid jsValue" in {

      fooJson match {
        case Foo.js(bar) => assert(bar == foo)
      }
    }
    "not match on invalid jsValue" in {
      JsString("bar") match {
        case Foo.js(_) => fail("a Foo is not serialized as a JsString")
        case _ => succeed
      }
    }
  }

  "the Js.parse extractor" should {
    "match on a valid JSON string" in {
      """{"foo": "bar"}""" match {
        case Js.parse(js) => assert(js == fooJson)
      }
    }

    "not match on an invalid JSON string" in {
      """{foo: "bar"}""" match {
        case Js.parse(_) => fail("invalid json was parsed as valid")
        case _ => succeed
      }
    }

    "compose nicely with a Js extractor" in {
      """{"foo": "bar"}""" match {
        case Js.parse(Foo.js(bar)) => assert(bar == foo)
      }
    }
  }

}
