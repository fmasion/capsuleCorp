package com.kreactive.capsule

import com.kreactive.generator.Gen
import com.kreactive.util.CleanUtils
import configs.Configs
import play.api.libs.json.Format

/**
  * Created by cyrille on 07/11/2016.
  */
trait ValueClass[Value, Capsule] extends CleanUtils {
  def construct: Value => Capsule
  def deconstruct: Capsule => Value
  def condition: Value => Boolean = _ => true

  implicit def ordering(implicit Value: Ordering[Value]): Ordering[Capsule] = new Ordering[Capsule] {
    override def compare(x: Capsule, y: Capsule): Int = Value.compare(deconstruct(x), deconstruct(y))
  }

  implicit def format(implicit Value: Format[Value]) =
    cleanFormat(construct, deconstruct, condition)

  implicit def configs(implicit Value: Configs[Value]): Configs[Capsule] =
    cleanConfigs(construct, condition)

  implicit def gen(implicit vGen: Gen[Value]): Gen[Capsule] = vGen.map(construct)
  def generate()(implicit vGen: Gen[Value]) = gen(vGen)()
}
