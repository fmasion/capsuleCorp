package com.kreactive.util

import scala.reflect.ClassTag

trait ClassTager {
  protected def typeFromTag[S](implicit S: ClassTag[S]) = S.runtimeClass.getSimpleName
}
