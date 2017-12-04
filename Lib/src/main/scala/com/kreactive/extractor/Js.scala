package com.kreactive.extractor

import play.api.libs.json.{JsValue, Json, Reads}

import scala.util.Try

/**
  * Extractor for jsValues :
  *
  * Usage
  *
  *  case class Foo(foo: String)
  *
  *  object Foo {
  *    implicit val reads = Json.reads[Foo]
  *    lazy val js = Js[Foo]
  *  }
  *
  *  Json.obj("foo" -> "bar") match {
  *    case Foo.js(bar) => assert(bar == Foo("bar"))
  *  }
  *
  *  """{"foo": "bar"}""" match {
  *    case Js.parse(Foo.js(bar)) => assert(bar == Foo("bar"))
  *  }
  */
object Js {
  def apply[T: Reads]: Extractor[JsValue, T] = Extractor[JsValue](_.asOpt[T])

  val parse: Extractor[String, JsValue] = Extractor[String](s => Try(Json.parse(s)).toOption)
}

