package com.kreactive.util

import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}
import play.api.libs.json.{JsString, JsSuccess, Json}

/**
  * Created by cyrille on 22/11/2016.
  */
class NoSlashSpec extends TestSuite with WordSpecLike with MustMatchers {

  implicit class Context(val sc: StringContext) {
    def n(args: Any*) = NoSlash(sc.s(args: _*))
  }

  "a NoSlash" should {
    "have an explicit toString" in {
      val hello = NoSlash("hello")
      hello.toString mustBe """noslash"hello""""
    }

    "be deserialized from a string without slash" in {
      JsString("hello world").validate[NoSlash] mustBe JsSuccess(NoSlash("hello world"))
    }

    "not be deserialized from a string with slash" in {
      val des = JsString("hello/world").validate[NoSlash]
      assert(des.isError)
    }

    "be serialized as a string" in {
      Json.toJson(NoSlash("hello world")) mustBe Json.toJson("hello world")
    }
  }
  import NoSlash._
  "the path interpolator" should {
    "split a string in segments according to slashes" in {
      path"hello/world/what/up?" mustBe List(n"hello", n"world", n"what", n"up?")
    }

    "split correctly if first character is /" in {
      path"/hello/world" mustBe List(n"", n"hello", n"world")
    }

    "split correctly if last character is /" in {
      path"hello/world/" mustBe List(n"hello", n"world", n"")
    }

    "split correctly even if some segments are empty" in {
      path"hello///world" mustBe List(n"hello", n"", n"", n"world")
    }

    "split correctly even if it's only /" in {
      path"/" mustBe List(n"", n"")
    }
  }

  "the noslash interpolator" should {
    "encapsulate a string without slashes" in {
      noslash"hello" mustBe n"hello"
    }

    "take the first part of a string with slashes" in {
      noslash"hello/world" mustBe n"hello"
    }
  }

  "the flatten method" should {
    "reconstruct the original path" in {
      List("/hello/world/what/up?", "hello/wo//rld/what/up?////", "", "  / ", "/", " /", "/ ").foreach {
        path =>
          NoSlash.flatten(path"$path") mustBe path
      }
    }
  }

  "the split method" should {
    "give the same result as the interpolator" in {
      List("hello/world/what/up?", "/hello/world", "hello/world/", "hello///world", "/").foreach {
        path =>
          path"$path" mustBe NoSlash.split(path)
      }
    }
  }

}
