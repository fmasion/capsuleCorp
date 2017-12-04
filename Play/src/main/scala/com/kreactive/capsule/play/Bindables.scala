package com.kreactive.capsule.play

import play.api.mvc.PathBindable

/**
  * Created by cyrille on 02/02/2017.
  */
object Bindables {
  implicit def listPathBindable[T](implicit T: PathBindable[T]): PathBindable[List[T]] = new PathBindable[List[T]] {
    override def unbind(key: String, value: List[T]): String = value.map(T.unbind(key, _)).mkString(",")

    override def bind(key: String, value: String): Either[String, List[T]] = {
      val bindEach = value.split(',').toList.map(T.bind(key, _))
      if (bindEach.forall(_.isRight))
        Right(bindEach.flatMap(_.right.toOption))
      else Left(bindEach.flatMap(_.left.toOption).mkString("Error while binding list:\n", "\n", ""))
    }
  }
}
