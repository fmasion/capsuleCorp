package com.kreactive.configs

import com.typesafe.config.{ConfigException, ConfigFactory}
import configs.{Configs, Result}
import configs.syntax._
import org.scalatest.{Assertion, TestSuite, WordSpecLike}

import scala.reflect.ClassTag

/**
  * Created by cyrille on 13/03/2017.
  */
sealed trait ConfigType extends Product with Serializable

sealed trait ConfigStringType extends ConfigType

object ConfigType {
  case object Number extends ConfigStringType
  case object String extends ConfigStringType
  case object Boolean extends ConfigStringType
  case object Array extends ConfigType
  case object Object extends ConfigType

  val values: Seq[ConfigType] = Seq(Number, String, Boolean, Array, Object)
}

object ConfigStringType {
  val values: Seq[ConfigStringType] = Seq(ConfigType.Number, ConfigType.String, ConfigType.Boolean)
  val nonValues: Set[ConfigType] = ConfigType.values.toSet -- values
}

trait ConfigTestKit { self: TestSuite with WordSpecLike =>

  private val typeExamplesConfig = ConfigFactory.parseString(
    """
      |Number = 10
      |String = hello
      |Boolean = true
      |Array = []
      |Object = {}
    """.stripMargin
  )

  def assertConfigException[T <: ConfigException: ClassTag](res: Result[_]) =
    assertThrows[T](throw res.failed.value.head.throwable)
  private def checkNonType[T: Configs](non_types: Set[ConfigType]): Set[Assertion] =
    non_types.map(t => assertConfigException[ConfigException.WrongType](typeExamplesConfig.get[T](t.toString)))
  def checkType[T: Configs](t: ConfigType): Set[Assertion] = t match {
    case ConfigType.String => checkNonType(ConfigStringType.nonValues)
    case other => checkNonType[T](ConfigType.values.toSet - other)
  }
}
