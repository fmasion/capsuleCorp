package com.kreactive.data

import com.kreactive.lens.Lens
import com.kreactive.util.{MapUtils, Stringable}
import configs.Configs

/**
  * Created by cyrille on 02/01/2017.
  */
case class Tree[Key, Data](data: Data, children: Map[Key, Tree[Key, Data]]) {
  def map[D: Emptyable](f: Data => D): Tree[Key, D] = collect(PartialFunction(f))
  def foreach[D](f: Data => D): Unit = map(d => Option(f(d)))
  def fold[D >: Data](f: (D, D) => D): D = children.map(_._2.fold(f)).fold(data)(f)
  def findAll(p: Data => Boolean): List[Data] = collect { case d if p(d) => List(d) }.fold(_ ++ _)
  def collect[D](f: PartialFunction[Data, D])(implicit D: Emptyable[D]): Tree[Key, D] =
    Tree(f.applyOrElse[Data, D](data, _ => D.empty), children.map { case (k, v) => k -> v.collect(f) }.filterNot(_._2 == Tree.emptyable[Key, D].empty))
  def filter(p: Data => Boolean)(implicit D: Emptyable[Data]) = collect { case d if p(d) => d }
  def size(implicit Data: Emptyable[Data]) = map(d => if (d == Data.empty) 0 else 1).fold(_ + _)
  def height(implicit Data: Emptyable[Data]): Int = if (children.isEmpty) 0 else {
    val childrenHeight = children.values.map(_.height).max
    childrenHeight + 1
  }
  def toList(implicit D: Emptyable[Data]): List[Data] = map(List(_).filterNot(_ == D.empty)).fold(_ ++ _)

  def toStrings: List[String] = {
    data.toString :: children.toList.flatMap {
      case (k, v) => k.toString :: v.toStrings.map("  " + _)
    }.map("|" + _)
  }

  override def toString: String = toStrings.mkString("\n")
}

trait TreeUtils extends MapUtils {
  import play.api.libs.functional.syntax._
  import play.api.libs.json._
  import com.kreactive.configs.syntax._
  implicit def treeFormat[K, D: Format](implicit K: Stringable[K]): Format[Tree[K, D]] = {
    (
      (__ \ "data").format[D] ~
        (__ \ "children").lazyFormat(mapFormat(K, treeFormat[K, D]))
      ) (Tree.apply, Function.unlift(Tree.unapply))
  }

  implicit def treeConfigs[K, D: Configs](implicit K: Stringable[K]): Configs[Tree[K, D]] = for {
    data <- Configs[D].offset("data")
    children <- Configs(mapConfig[K, Tree[K, D]](K, treeConfigs[K, D])).offset("children")
  } yield Tree(data, children)
}

object Tree extends TreeUtils {
  import scala.language.higherKinds

  implicit def emptyable[K, D: Emptyable]: Emptyable[Tree[K, D]] =
    Emptyable(Tree[K, D](implicitly[Emptyable[D]].empty, Map()))

  def childrenLens[K, D] = Lens[Tree[K, D]](_.children)(ch => _.copy(children = ch))
  def dataLens[K, D] = Lens[Tree[K, D]](_.data)(d => _.copy(data = d))

  def lens[K, D: Emptyable](path: List[K]): Lens[Tree[K, D], Tree[K, D]] = path match {
    case Nil => Lens.identity
    case h :: t => childrenLens[K, D].compose(Lens.atKey(h, emptyable[K, D].empty)).compose(lens(t))
  }
}