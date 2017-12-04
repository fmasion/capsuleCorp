package com.kreactive.enum

import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}

/**
  * Created by cyrille on 10/01/2017.
  */
trait EnumCompanionTestKit extends MustMatchers {
  self: TestSuite with WordSpecLike =>

  def testValues[T](o: EnumCompanion[T]) = {
    "an enum" should {
      "extend Product with Serializable" in {
        assertCompiles("""implicitly[T <:< (Product with Serializable)]""".stripMargin)
      }

      "have distinct value names" in {
        o.values.toList.map(_.toString) diff o.values.map(_.toString).toList mustBe Nil
      }

      "not forget any of their values" in {
        pending
      }
    }
  }
}
