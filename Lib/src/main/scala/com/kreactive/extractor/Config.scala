package com.kreactive.extractor

import com.typesafe.config.ConfigValue
import configs.Configs

/**
  * Created by cyrille on 11/01/2017.
  */
object Config {
  def apply[T](implicit T: Configs[T]): Extractor[ConfigValue, T] =
    Extractor[ConfigValue](T.extractValue(_).toOption)

  def apply[T](path: String)(implicit T: Configs[T]): Extractor[com.typesafe.config.Config, T] =
    Extractor[com.typesafe.config.Config](T.get(_, path).toOption)
}
