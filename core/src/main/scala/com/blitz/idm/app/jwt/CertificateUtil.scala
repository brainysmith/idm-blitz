package com.blitz.idm.app.jwt

import java.security.cert._
import scala.util.{Failure, Success, Try}
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayInputStream
import com.blitz.idm.app.security.{KeyStoreManager, TrustedCertsFactory}

/**
 * The collection of handy util functions to work with X509 certificates.
 */
trait CertificateUtil {
  self: TrustedCertsFactory =>

  /**
   * Verifies the a certificate or certificate chain represented by array containing
   * base64 encoded DER PKIX certificate values. The first is considered as the leaf of chain
   * and other - as intermediate certificates.
   * @param chain - an array of base64 encoded DER certificate values.
   * @return if validation succeeded the method returns Right(leaf certificate as X509Certificate object),
   *         otherwise - Left("error message")
   */
  def verifyCertificateChain(chain: Array[String]): Either[String, X509Certificate] = decodeBase64(chain).right.map(s => KeyStoreManager.verifyCertificate(s(0), s.drop(1).toSet)).joinRight

  private def decodeBase64(arr: Array[String]): Either[String, Seq[X509Certificate]] = Try {
    val factory = CertificateFactory.getInstance("X.509", "BC")

    val decoded = arr.map(s => Try {
      val b = Base64.decodeBase64(s)
      val is = new ByteArrayInputStream(b)
      factory.generateCertificate(is).asInstanceOf[X509Certificate]
    } match {
      case Success(c) => Right(c)
      case Failure(e) => Left(e.getMessage)
    })

    val fails = decoded.filter(_.isLeft)
    fails.isEmpty match {
      case true => Right(decoded.map({case Right(c) => c}).toSeq)
      case false => fails.fold(Left(""))({case (Left(a), Left(b)) => Left(a + "; " + b)}).asInstanceOf[Either[String, Seq[X509Certificate]]]
    }
  } match {
    case Success(v) => v
    case Failure(e) => Left(e.getMessage)
  }

}

