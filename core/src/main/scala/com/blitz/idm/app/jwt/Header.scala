package com.blitz.idm.app.jwt

import com.blitz.idm.app.jwt.ReservedHeaderParameters._
import org.apache.commons.codec.binary.Base64

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
    case None => "Found unknown header parameter '" + n + "'."
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

trait SignHeader extends Header with HeaderStore

trait EncryptionHeader extends Header with HeaderStore

trait HeaderStore {

  def getAsString(name: String): Option[String]

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
