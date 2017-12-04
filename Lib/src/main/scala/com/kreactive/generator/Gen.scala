package com.kreactive.generator

import java.util.UUID

import scala.util.Random

/**
  * Created by cyrille on 15/12/2016.
  */
trait Gen[T] {
  def apply(): T

  def map[U](f: T => U) = flatMap(t => Gen.from(f(t)))
  def flatMap[U](f: T => Gen[U]): Gen[U] = Gen.from(f(apply()).apply())
  def foreach[U](f: T => U): Unit = f(apply())
  def flatten[U](implicit ev: T => Gen[U]): Gen[U] = flatMap(ev)
  private def filterRec(p: T => Boolean): T = {
    val t = apply()
    if (p(t)) t else filterRec(p)
  }
  def filter(p: T => Boolean): Gen[T] = Gen.from(filterRec(p))
}

object Gen {
  def from[T](t: => T): Gen[T] = new Gen[T] {
    def apply() = t
  }
  def apply[T](implicit T: Gen[T]): Gen[T] = T
  val stringLength = 10
  implicit val stringGen: Gen[String] = from(Random.nextString(stringLength))
  implicit val uuidGen: Gen[UUID] = from(UUID.randomUUID())
}
