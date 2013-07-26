package services.login

/**
 * It's the marker interface for subject's credentials.
  */
trait Credentials extends Product {
}

object Credentials {

  def apply[T1](t : T1): Credentials = new Credentials1[T1](t)
  def apply[T1,T2](t : (T1, T2)): Credentials = new Credentials2[T1,T2](t._1, t._2)
  def apply[T1,T2, T3](t : (T1, T2, T3)): Credentials = new Credentials3[T1,T2, T3](t._1, t._2, t._3)
}

class Credentials1[T1](private val v1: T1) extends Tuple1(v1) with Credentials {}
class Credentials2[T1,T2](private val v1: T1, private val v2: T2) extends Tuple2(v1, v2) with Credentials {}
class Credentials3[T1,T2, T3](private val v1: T1, private val v2: T2, private val v3: T3) extends Tuple3(v1, v2, v3) with Credentials {}


