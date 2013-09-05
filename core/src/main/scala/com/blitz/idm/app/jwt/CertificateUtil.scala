package com.blitz.idm.app.jwt

import java.security.cert.X509Certificate
import scala.util.{Failure, Success, Try}
import java.security.{SignatureException, InvalidKeyException}

/**
 * The collection of handy util functions to work with X509 certificates.
 */
trait CertificateUtil {
  self: TrustedCertsFactory =>

  def verifyCertificate(cert: X509Certificate, chainCerts: Set[X509Certificate]) {
  }

  def selfSigned(cert: X509Certificate): Boolean = Try{
    cert.verify(cert.getPublicKey)
    true
  } match {
    case Success(v) => v
    case Failure(e: InvalidKeyException) => false
    case Failure(e: SignatureException) => false
    case Failure(e) => throw e
  }

}

trait TrustedCertsFactory {

  def trustedCerts: Set[X509Certificate]

}
