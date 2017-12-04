package com.kreactive.lens

import play.api.libs.functional.Functor

import scala.language.higherKinds
/**
  * Created by cyrille on 02/01/2017.
  */
trait Lens[Obj, Field] { self =>

  def modifyF[F[_]](f: Field => F[Field])(implicit F: Functor[F]): Obj => F[Obj]

  private val func = Constant.functor[Field]

  def get(o: Obj): Field =
    modifyF[func.Func](field => Constant[Field, Field](field))(func.functor)(o).value

  def set(f: Field): Obj => Obj =
    o => modifyF[Id](_ => Id(f))(Id.functor)(o).value

  def modify(f: Field => Field): Obj => Obj =
    o => modifyF[Id](t => Id(f(t)))(Id.functor)(o).value

  def compose[SubField](that: Lens[Field, SubField]): Lens[Obj, SubField] = new Lens[Obj, SubField] {
    override def modifyF[F[_]](f: (SubField) => F[SubField])(implicit F: Functor[F]): (Obj) => F[Obj] =
      self.modifyF(that.modifyF(f))
  }

  def andThen[OtherField](that: Lens[Obj, OtherField]): Lens[Obj, (Field, OtherField)] =
    Lens[Obj](o => (self.get(o), that.get(o))){
    case (field, otherField) => self.set(field).andThen(that.set(otherField))
  }

}

object Lens {

  def apply[O] = new LensWithObject[O] {}

  def identity[O]: Lens[O, O] = Lens[O](o => o)(o => _ => o)

  def atKey[K, V](key: K, default: => V): Lens[Map[K, V], V] =
    Lens[Map[K, V]](_.getOrElse(key, default))(f => m => if (f == default) m - key else m + (key → f))

  trait LensWithObject[O] {
    def apply[F](g: O => F)(s: F => O => O) =  new Lens[O, F] {
      override def modifyF[Func[_]](f: (F) => Func[F])(implicit F: Functor[Func]): (O) => Func[O] =
        o => F.fmap[F, O](f(g(o)), field => s(field)(o))
      override def get(o: O): F = g(o)
      override def set(f: F): (O) => O = s(f)
    }
  }
}

case class Constant[A, B](value: A)

class ConstantFunctor[A] {
  type Func[B] = Constant[A, B]
  val functor = {
    new Functor[Func] {
      override def fmap[AA, B](m: Func[AA], f: (AA) => B) = Constant(m.value)
    }
  }
}

object Constant {
  implicit def functor[A] = new ConstantFunctor[A]
}
case class Id[A](value: A)

object Id {
  implicit val functor = new Functor[Id] {
    override def fmap[A, B](m: Id[A], f: (A) => B) = Id(f(m.value))
  }
}

