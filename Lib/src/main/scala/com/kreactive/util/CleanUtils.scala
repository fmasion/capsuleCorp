package com.kreactive.util

import com.typesafe.config.{Config, ConfigValue}
import configs.{Configs, Result}
import play.api.libs.json._

/**
  * Created by cyrille on 24/02/2017.
  */
trait CleanUtils {
  def cleanFormat[Value, Capsule](construct: Value => Capsule, deconstruct: Capsule => Value, condition: Value => Boolean = (_: Value) => true)(implicit Value: Format[Value]): Format[Capsule] =
    Format[Capsule](
      Value.filter(JsonValidationError("did not pass value class condition"))(condition).map(construct),
      Writes(c => Value.writes(deconstruct(c)))
    )

  def cleanConfigs[Value, Capsule](construct: Value => Capsule, condition: Value => Boolean = (_: Value) => true)(implicit Value: Configs[Value]): Configs[Capsule] =
    Configs[Value].flatMap{
      case v if condition(v) => Configs.successful(construct(v))
      case _ => Configs.failure("did not pass value class condition")
    }
}

trait MapUtils {
  implicit def mapFormat[K: Stringable, V: Format]: OFormat[Map[K, V]] = new OFormat[Map[K, V]] {
    override def reads(json: JsValue): JsResult[Map[K, V]] =
      json.validate(Reads.mapReads[V]).map(Stringable.toStringableMap(_))

    override def writes(o: Map[K, V]): JsObject =
      OWrites.map[V].writes(Stringable.toStringMap(o))
  }

  implicit def mapConfig[K: Stringable, V: Configs]: Configs[Map[K, V]] = new Configs[Map[K, V]] {
    override def get(config: Config, path: String): Result[Map[K, V]] =
      Configs.cbfJMapConfigs[Map, String, V].get(config, path).map(Stringable.toStringableMap(_))
  }
}


trait PairUtils {

  import com.kreactive.configs.syntax._

  implicit def pairFormat[A: Format, B: Format] = new Format[(A, B)] {
    override def reads(json: JsValue): JsResult[(A, B)] = for {
      arr <- json.validate[Array[JsValue]].filter(_.length == 2)
      Array(aJs, bJs) = arr
      a <- aJs.validate[A]
      b <- bJs.validate[B]
    } yield (a, b)

    override def writes(o: (A, B)): JsValue = Json.arr(o._1, o._2)
  }

  implicit def pairConfigs[A: Configs, B: Configs]: Configs[(A, B)] = {
    Configs[Array[ConfigValue]].flatMap {
      case l if l.length == 2 => for {
        a <- Configs[A].atIndex(0)
        b <- Configs[B].atIndex(1)
      } yield (a, b)
      case _ => Configs.failure("array length should be 2")
    }
  }
}