package com.blitz.idm.app.jwt

import com.blitz.idm.app.jwt.ReservedClaims._

/**
 * This trait represents a claim set of JWT.
 */
trait ClaimSet {
  self: ClaimSetStore =>

  def iss: Option[StringOrUri] = getAsString(ISS.name).map(s => ISS.create(s))

  /**
   * Returns the identifier of the principal that is the subject of the JWT.
   * @return - if exists - Option of identifier otherwise None
   */
  def sub: Option[StringOrUri] = getAsString(SUB.name).map(s => SUB.create(s))

  /**
   * Returns array of audience identifiers the JWT token is intended for.
   * @return - if exists - Option of identifiers array otherwise None
   */
  def aud: Option[Array[StringOrUri]] = getAsAnyRef(AUD.name).map(s => AUD.create(s))

  /**
   * Returns the expiration time of the JWT.
   * @return - if exists - Option of expiration time otherwise None
   */
  def exp: Option[IntDate] = getAsInt(EXP.name).map(s => EXP.create(s))

  /**
   * Returns the time before which the JWT must not be accepted for processing
   * @return - if exists - Option of not before time otherwise None
   */
  def nbf: Option[IntDate] = getAsInt(NBF.name).map(s => NBF.create(s))

  /**
   * Returns the time at which the JWT was issued
   * @return - if exists - Option of issuing otherwise None
   */
  def iat: Option[IntDate] = getAsInt(IAT.name).map(s => IAT.create(s))

  /**
   * Returns an unique identifier of the JWT.
   * @return - if exists - Option of identifier otherwise None
   */
  def jti: Option[String] = getAsString(JTI.name).map(s => JTI.create(s))

  /**
   * Returns a type of the content of the JWT Claim Set.
   * @return - if exists - Option of type otherwise None
   */
  def typ: Option[String] = getAsString(TYP.name).map(s => TYP.create(s))

}

trait ClaimSetStore {

  def getAsString(name: String): Option[String]

  def getAsInt(name: String): Option[Int]

  def getAsAnyRef(name: String): Option[AnyRef]

}
