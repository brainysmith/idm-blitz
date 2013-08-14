package services.login

/**
 * It's the marker interface for subject's credentials.
  */
trait Credentials extends Product {
}

object Credentials {

  def apply(t : (String, String)): Credentials = new BasicCredentials(t._1, t._2)
}

abstract class Credentials1[T1](private val v1: T1) extends Tuple1(v1) with Credentials {}
abstract class Credentials2[T1,T2](private val v1: T1, private val v2: T2) extends Tuple2(v1, v2) with Credentials {}
abstract class Credentials3[T1,T2, T3](private val v1: T1, private val v2: T2, private val v3: T3) extends Tuple3(v1, v2, v3) with Credentials {}

class BasicCredentials(val lgn: String, val pswd: String) extends Credentials2[String, String](lgn, pswd) {


  override def toString(): String = {
    val sb =new StringBuilder("BasicCredentials(")
    sb.append("login -> ").append(lgn)
    sb.append(")").toString()
  }
}


