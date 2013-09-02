package com.blitz.idm.app.jwt

/**
 * This trait represents the Json Web Token Header.
 * The implementation is based on Internet-Draft draft-ietf-oauth-json-web-token-11 from July 29, 2013.
 */
trait Header {

  /**
   * Returns algorithm of signing or encryption of the token.
   * @return - an algorithm of signing or encryption of the token.
   */
  def alg: Algorithm

  def iss: StringOrUri

  /**
   * Returns store of headers fields.
   * @return - store of header fields.
   */
  protected def store: Map[String, AnyRef]

}
