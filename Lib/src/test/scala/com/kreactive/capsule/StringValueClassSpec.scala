package com.kreactive.capsule

import com.kreactive.data.Tree
import com.typesafe.config.ConfigFactory
import configs.syntax._
import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}
import play.api.libs.json.Json

/**
  * Created by cyrille on 13/01/2017.
  */

case class Key(k: String) extends AnyVal

object Key extends StringValueClass[Key] {
  override def construct: (String) => Key = apply
  override def deconstruct: (Key) => String = _.k
}

class StringValueClassSpec extends TestSuite with MustMatchers with WordSpecLike {

  "a string value class" should {
    val map = Map(Key("key") -> "value")
    val tree = Tree("rootData", Map(Key("key") -> Tree[Key, String]("childData", Map())))
    val treeJson = Json.obj("data" -> "rootData", "children" -> Json.obj("key" -> Json.obj("data" -> "childData", "children" -> Json.obj())))
    val treeConfig = ConfigFactory.parseString(
      """
        |data = "rootData"
        |children.key = {
        |  data = "childData"
        |  children = {}
        |}
      """.stripMargin
    )
    val mapJson = Json.obj("key" -> "value")
    val mapConfig = ConfigFactory.parseString("""{key = value}""")
    "expose a json serializer for maps" in {
      import Key.keyFormat //used for desambiguiuty with built-in play serializer
      Json.toJson(map) mustBe mapJson
    }
    "expose a json deserializer for maps" in {
      val des = mapJson.validate[Map[Key, String]]
      des.isSuccess mustBe true
      des.get mustBe map
    }
    "expose a config deserializer for maps" in {
      val des = mapConfig.extract[Map[Key, String]]
      des.isSuccess mustBe true
      des.value mustBe map
    }
    "expose a json serializer for trees" in {
      Json.toJson(tree) mustBe treeJson
    }
    "expose a json deserializer for trees" in {
      val des = treeJson.validate[Tree[Key, String]]
      des.isSuccess mustBe true
      des.get mustBe tree
    }

    "expose a config deserializer for trees" in {
      val des = treeConfig.extract[Tree[Key, String]]
      des.isSuccess mustBe true
      des.value mustBe tree
    }

  }

}
