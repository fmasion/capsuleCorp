package com.kreactive.data

import org.scalatest.{MustMatchers, TestSuite, WordSpecLike}

/**
  * Created by cyrille on 05/01/2017.
  */
class TreeSpec extends TestSuite with WordSpecLike with MustMatchers {

  "a tree of emptyable" should {
    implicit val stringEmpty = Emptyable("")
    "trim its branches when they are empty" in {
      val tree = Tree[String, String]("", Map())
      val addElem = Tree.lens(List("level1", "level2", "level3")).compose(Tree.dataLens).set("hello world")
      val removeElem = Tree.lens(List("level1", "level2", "level3")).compose(Tree.dataLens).set("")
      val newTree = removeElem(addElem(tree))
      newTree mustBe tree
    }

    "not trim its branches when they are not empty, even if changed element is empty" in {
      val tree = Tree[String, String]("", Map())
      val addElem = Tree.lens(List("level1", "level2", "level3")).compose(Tree.dataLens).set("hello world")
      val addOtherElem = Tree.lens(List("level1", "level2")).compose(Tree.dataLens).set("hello world")
      val removeOtherElem = Tree.lens(List("level1", "level2")).compose(Tree.dataLens).set("")
      val newTree = removeOtherElem(addOtherElem(addElem(tree)))
      newTree mustBe addElem(tree)
      newTree.height mustBe 3
      newTree.size mustBe 1
    }
  }
}
