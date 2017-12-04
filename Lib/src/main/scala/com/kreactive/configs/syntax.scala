package com.kreactive.configs

import com.typesafe.config.ConfigValue
import configs.Configs
import configs.syntax._

/**
  * Created by cyrille on 08/03/2017.
  */
object syntax {

  implicit class ConfigsOps[T](val configs: Configs[T]) extends AnyVal {
    def offset(suffix: String): Configs[T] = Configs.from((c, p) => c.get(s"$p.$suffix")(configs))
    def atIndex(n: Int): Configs[T] = Configs.fromTry((c, p) => configs.extractValue(c.get[Array[ConfigValue]](p).value.apply(n)).value)
  }

  implicit class ConfigsMapOps[K, T](val map: Map[K, Configs[T]]) extends AnyVal {
    def sequence: Configs[Map[K, T]] = map.foldLeft(Configs.successful(Map[K, T]())) {
      case (building, (k, conf)) => for {
        b <- building
        c <- conf
      } yield b + (k -> c)
    }
  }
}