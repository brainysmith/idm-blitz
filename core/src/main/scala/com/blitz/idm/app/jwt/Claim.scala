package com.blitz.idm.app.jwt

import com.blitz.idm.app.CustomEnumeration
import org.apache.commons.codec.binary.Base64

/**
 * This class represents any claims in JWT. These can be header claims or body claims.
 */

sealed trait Claim {

  def name: String

  def mandatory: Boolean

}

sealed trait ClaimWithFunctions[V, T] extends Claim {

  def validate(v: V)

  def create(v: V): T

}

object HeaderType extends Enumeration {
  type HeaderType = Value
  val PLAIN, SIGNED, ENCRYPTION = Value
}

import HeaderType._

sealed abstract class ReservedHeaderParameter[V, T](private val _name: String,
                                                    private val _mandatory: Boolean,
                                                    val headerType: HeaderType,
                                                    private val vf: V => Unit,
                                                    private val cf: V => T) extends ClaimWithFunctions[V, T] with ReservedClaims.Val {
  def name: String = _name

  def mandatory: Boolean = _mandatory

  def validate(v: V) {vf(v)}

  def create(v: V): T = cf(v)

}

object ReservedHeaderParameters extends CustomEnumeration[Claim] {

  case object ALG extends ReservedHeaderParameter("alg", true, PLAIN,
    (s: String) => Algorithm.optValueOf(s).fold[Unit](throw new IllegalStateException("Header parameter 'alg' has a wrong value '" + s + "'."))(a => {
      if (!a.supported) throw new IllegalStateException("Algorithm '" + a.name + "' is not supported.")}),
    (s: String) => Algorithm.valueOf(s))
  case object TYP extends ReservedHeaderParameter("typ", false, PLAIN, (s: String) => {}, (s: String) => s)
  case object CTY extends ReservedHeaderParameter("cty", false, PLAIN, (s: String) => {}, (s: String) => s)

  case object JKU extends ReservedHeaderParameter("jku", false, SIGNED,
    (s: String) => if (!StringOrUri(s).isUri) throw new IllegalStateException("Header parameter 'jku' must be an URI."),
    (s: String) => StringOrUri(s).uri)
  case object JWK extends ReservedHeaderParameter("jwk", false, SIGNED,
    (s: String) => throw new UnsupportedOperationException,
    (s: String) => throw new UnsupportedOperationException)
  case object X5U extends ReservedHeaderParameter("x5u", false, SIGNED,
    (s: String) => if (!StringOrUri(s).isUri) throw new IllegalStateException("Header parameter 'x5u' must be an URI."),
    (s: String) => StringOrUri(s))
  case object X5T extends ReservedHeaderParameter("x5t", false, SIGNED,
    (s: String) => if (Base64.decodeBase64(s).length != 20) throw new IllegalStateException("Header parameter 'x5t' has a wrong length."),
    (s: String) => Array(Base64.decodeBase64(s)))
  case object X5C extends ReservedHeaderParameter("x5c", false, SIGNED,
    (a: Array[String]) => "",
    (a: Array[String]) => "")

}

sealed abstract class ReservedClaim[V, T](private val _name: String,
                                          private val _mandatory: Boolean,
                                          private val vf: V => Unit,
                                          private val cf: V => T) extends ClaimWithFunctions[V, T] with ReservedClaims.Val {
  def name: String = _name

  def mandatory: Boolean = _mandatory

  def validate(v: V) {vf(v)}

  def create(v: V): T = cf(v)

}

object ReservedClaims extends CustomEnumeration[Claim] {

  case object ISS extends ReservedClaim("iss", false, (s: String) => StringOrUri(s), (s: String) => StringOrUri(s))
  case object SUB extends ReservedClaim("sub", false, (s: String) => StringOrUri(s), (s: String) => StringOrUri(s))
  case object AUD extends ReservedClaim("aud", false, (s: Any) => {
    s match {
      case s: String => StringOrUri(s)
      case a: Array[String] => a.foreach(e => StringOrUri(e))
      case _ => throw new IllegalArgumentException("Wrong claim 'aud'")
    }
  }, (s: Any) => {
    s match {
      case s: String => Array(StringOrUri(s))
      case a: Array[String] => a.map(e => StringOrUri(e))
      case _ => throw new IllegalArgumentException("Wrong claim 'aud'")
    }
  })
  case object EXP extends ReservedClaim("exp",false,
    (i: Int) => {if (!IntDate.now.before(IntDate(i))) throw new IllegalStateException("The JWT token is expired.")},
    (i: Int) => IntDate(i))
  case object NBF extends ReservedClaim("nbf",false,
    (i: Int) => {if (!IntDate.now.after(IntDate(i))) throw new IllegalStateException("The time to process the JWT has not come yet.")},
    (i: Int) => IntDate(i))
  case object IAT extends ReservedClaim("iat", false, (i: Int) => IntDate(i), (i: Int) => IntDate(i))
  case object JTI extends ReservedClaim("jti", false, (i: String) => {}, (i: String) => i)
  case object TYP extends ReservedClaim("typ", false, (i: String) => {}, (i: String) => i)

}

