package com.blitz.idm.app.jwt

/**
 *
 */
trait Token {

  def header: Header

  def claimSet: ClaimSet

}

trait Factory {
  self: HeaderFactory =>

  protected def token(header: Header, claimSet: ClaimSet): Token
  
  def apply(serialized: String): Token = {
    serialized.split('.') match {
      case split if split.length == 3 => token(header(split(0)), null)
      case e => throw new IllegalArgumentException("Given token '" + serialized + "' does not consist of three parts.")
    }
    
    
    
  }
  
}
