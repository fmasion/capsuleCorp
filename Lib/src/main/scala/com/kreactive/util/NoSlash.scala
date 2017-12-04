package com.kreactive.util

import com.kreactive.capsule.ValueClass

/**
  * Created by cyrille on 22/11/2016.
  */
case class NoSlash(str: String) extends AnyVal {
  override def toString = s"""noslash"$str""""
}

object NoSlash extends ValueClass[String, NoSlash]{

  val SLASH = "/"

  def flatten(l: List[NoSlash]): String = {
    l.map(_.str).mkString(SLASH)
  }

  implicit class NoSlashStringContext(val sc: StringContext) extends AnyVal {
    // the split function used here is from java.lang.String. Its second arguments -1 ensures that all matches split the string
    // (otherwise, empty strings at the end of the array are discarded).
    def path(args: Any*): List[NoSlash] = split(sc.s(args: _*))
    def noslash(args: Any*) = path(args: _*).head
  }

  def split(str: String): List[NoSlash] = str.split(SLASH, -1).map(apply).toList

  override def construct: (String) => NoSlash = apply
  override def deconstruct: (NoSlash) => String = _.str
  override val condition: String => Boolean = !_.contains(SLASH)
}