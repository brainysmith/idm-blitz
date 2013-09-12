package com.blitz.idm.app.jwt

import com.blitz.idm.app.jwt.ReservedHeaderParameter._
import org.apache.commons.codec.binary.Base64
import java.net.URI
import java.security.cert.X509Certificate

/**
 * This trait represents the Json Web Token Header.
 * The implementation is based on Internet-Draft draft-ietf-oauth-json-web-token-11 from July 29, 2013.
 */
trait Header {
  self: HeaderStore =>

  private val mandatoryParams = values.filter(_.asInstanceOf[ReservedHeaderParameter[Nothing, Nothing]].headerType == HeaderType.PLAIN).filter(_.mandatory)

  /**
   * validating of mandatory params
   */
  values.filter(_.asInstanceOf[ReservedHeaderParameter[Nothing, Nothing]].headerType == HeaderType.PLAIN).foreach(p => getAsAny(p.name) match {
    case None => throw new IllegalStateException("Header parameter '" + p.name + "' must be defined.")
    case _ =>
  })

  /**
   * doing validation
   */
  names.foreach(n => (optValueOf(n): @unchecked) match {
    case Some(ALG) => getAsString(ALG.name).map(v => ALG.validate(v))
    case Some(TYP) => getAsString(TYP.name).map(v => TYP.validate(v))
    case Some(CTY) => getAsString(CTY.name).map(v => CTY.validate(v))
  })

  /**
   * Returns algorithm of signing or encryption of the token.
   * @return - an algorithm of signing or encryption of the token.
   */
  def alg: Algorithm = getAsString(ALG.name).fold[Algorithm](throw new NoSuchElementException("Header parameter 'alg' must be defined."))(a => ALG.create(a))

  /**
   * Return a type of the JWT.
   * @return if exists - Option of the type otherwise None
   */
  def typ: Option[String] = getAsString(TYP.name).map(s => TYP.create(s))

  /**
   * Return a content type of the JWT.
   * @return if exists - Option of the content type otherwise None
   */
  def cty: Option[String] = getAsString(CTY.name).map(s => CTY.create(s))

}

trait SignHeader extends Header with HeaderStore {

  private val mandatoryParams = values.filter(_.asInstanceOf[ReservedHeaderParameter[Nothing, Nothing]].headerType == HeaderType.SIGNED).filter(_.mandatory)

  /**
   * validating of mandatory params
   */
  values.filter(_.asInstanceOf[ReservedHeaderParameter[Nothing, Nothing]].headerType == HeaderType.PLAIN).foreach(p => getAsAny(p.name) match {
    case None => throw new IllegalStateException("Header parameter '" + p.name + "' must be defined.")
    case _ =>
  })

  /**
   * doing validation
   */
  names.foreach(n => (optValueOf(n): @unchecked) match {
    case Some(JKU) => getAsString(JKU.name).map(v => JKU.validate(v))
    case Some(JWK) => getAsString(JWK.name).map(v => JWK.validate(v))
    case Some(X5U) => getAsString(X5U.name).map(v => X5U.validate(v))
    case Some(X5T) => getAsString(X5T.name).map(v => X5T.validate(v))
    case Some(X5C) => getAsStringArray(X5C.name).map(v => X5C.validate(v))
    case Some(KID) => getAsString(KID.name).map(v => KID.validate(v))
    case Some(CRIT) => getAsStringArray(CRIT.name).map(v => CRIT.validate(v))
  })

  /**
   * Returns a URI that refers to a resource for a set of JSON-encoded public keys, one of which
   * corresponds to the key used to digitally sign the JWS.
   * @return - a URI
   */
  def jku: Option[URI] = getAsString(JKU.name).map(s => JKU.create(s))

  /**
   * Returns the public key corresponding to the key used to sign the JWS.
   * The key is represented as a JSON Web Key.
   * @return - the key represented as JSON Web Key.
   */
  def jwk: Option[WebKey] = getAsString(JWK.name).map(s => JWK.create(s))

  /**
   * Returns a URI that refers to a resource for the X.509 public key certificate or certificate
   * chain corresponding to the key used to sign the JWS.
   * @return - a URI
   */
  def x5u: Option[URI] = getAsString(X5U.name).map(s => X5U.create(s))

  /**
   * Returns SHA-1 thumbprint of the DER encoding of the X.509 certificate corresponding to the key
   * used to sign the JWS.
   * @return - a X.509 certificate thumbprint
   */
  def x5t: Option[Array[Byte]] = getAsString(X5T.name).map(s => X5T.create(s))

  /**
   * Returns a X.509 certificate corresponding to the key used to sign the JWS.
   * @return - a X.509 certificate
   */
  def x5c: Option[X509Certificate] = getAsStringArray(X5C.name).map(s => X5C.create(s))

  /**
   * Returns a key ID indicating which key was used to sign the JWS.
   * @return - a key ID
   */
  def kid: Option[String] = getAsString(KID.name).map(s => KID.create(s))

  /**
   * Returns a list of names of header parameters being extensions to the JWT and JWS specifications.
   * These extensions must be understood and processed.
   * @return - a list of names of header parameters
   */
  def crit: Option[Array[String]] = getAsStringArray(CRIT.name).map(s => CRIT.create(s))

}

trait EncryptionHeader extends Header with HeaderStore

trait HeaderStore {

  def getAsString(name: String): Option[String]

  def getAsStringArray(name: String): Option[Array[String]]

  def getAsInt(name: String): Option[Int]

  def getAsAny(name: String): Option[Any]

  def names: Set[String]

}

trait HeaderFactory {

  def header(str: String): Header =  ((j: String) => createPlain(j).alg.algType match {
    case AlgorithmType.SIGNING => createSign(j)
    case AlgorithmType.ENCRYPTION => createEncryption(j)
  })(new String(Base64.decodeBase64(str), "UTF-8"))

  protected def createPlain(json: String): Header

  protected def createSign(json: String): SignHeader

  protected def createEncryption(json: String): EncryptionHeader

}
