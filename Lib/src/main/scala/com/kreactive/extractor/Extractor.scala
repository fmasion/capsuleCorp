package com.kreactive.extractor

/**
  * Created by cyrille on 25/01/2017.
  */
trait Extractor[Extractee, Extracted] {
  def unapply(e: Extractee): Option[Extracted]
}

object Extractor {
  def apply[Extractee] = new AppliedExtractor[Extractee]{}

  trait AppliedExtractor[Extractee] {
    def apply[Extracted](u: Extractee => Option[Extracted]) = new Extractor[Extractee, Extracted] {
      override def unapply(e: Extractee): Option[Extracted] = u(e)
    }
  }
}
