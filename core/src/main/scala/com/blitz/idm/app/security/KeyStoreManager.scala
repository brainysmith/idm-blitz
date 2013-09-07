package com.blitz.idm.app.security

import java.security.cert._
import collection.JavaConversions._
import com.blitz.idm.app._
import java.security.{Security, SignatureException, InvalidKeyException, KeyStore}
import java.io.FileInputStream
import scala.util.{Failure, Success, Try}
import org.bouncycastle.jce.provider.BouncyCastleProvider

/**
 * The factory of trusted certificates and anchors.
 */
trait TrustedCertsFactory {

  /**
   * Returns a set of trusted certificates obtained from trusted store.
   * @return a set of trusted certificates
   */
  def trustCerts: Set[X509Certificate]

  /**
   * Contains a set of trusted anchors build on the trusted certificates from trusted store.
   */
  def trustAnchors: java.util.Set[TrustAnchor] = trustCerts.map(c => new TrustAnchor(c, null))

}

/**
 * This object represents the key store manager. It enables working with private keys, certificates and trusted certificates.
 */
object KeyStoreManager extends TrustedCertsFactory {

  Security.addProvider(new BouncyCastleProvider())

  private val _trustedCerts = {
    val keystore = KeyStore.getInstance("JKS")
    var fis: FileInputStream = null
    try {
      fis = new FileInputStream(appConfiguration.main.truststore.path)
      keystore.load(fis, appConfiguration.main.truststore.password.toCharArray)
    }
    finally {
      if(fis != null)
        fis.close()
    }
    keystore.aliases.map(a => keystore.getEntry(a, null)).collect({case t: KeyStore.TrustedCertificateEntry => t.getTrustedCertificate.asInstanceOf[X509Certificate]}).toSet
  }

  /**
   * Returns a set of trusted certificates obtained from trusted store.
   * @return a set of trusted certificates
   */
  def trustCerts: Set[X509Certificate] = _trustedCerts

  /**
   * Verifies a given certificate or a chain of certificates
   * @param cert - a certificate to verify;
   * @param chainCerts - a set of intermediate certificate if using a chain.
   * @return - Right(cert) if valid;
   *           Left("error message") if not valid or any error occurred.
   */
  def verifyCertificate(cert: X509Certificate, chainCerts: Set[X509Certificate]): Either[String, X509Certificate] = Try {
    val selector = new X509CertSelector
    selector.setCertificate(cert)

    val pkixParams = new PKIXBuilderParameters(trustAnchors, selector)
    //TODO CRL checking is disabled
    pkixParams.setRevocationEnabled(false)
    val chainStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(chainCerts + cert), "BC")
    pkixParams.addCertStore(chainStore)

    val builder = CertPathBuilder.getInstance("PKIX", "BC")
    builder.build(pkixParams)
    cert
  } match {
    case Success(v) => Right(v)
    case Failure(e) => Left(e.getMessage)
  }

  def selfSigned(cert: X509Certificate): Either[String, X509Certificate] = Try {
    cert.verify(cert.getPublicKey)
  } match {
    case Success(_) => Right(cert)
    case Failure(e: InvalidKeyException) => Left("Certificate S/N " + cert.getSerialNumber + " is self-signed.")
    case Failure(e: SignatureException) => Left("Certificate S/N " + cert.getSerialNumber + " is self-signed.")
    case Failure(e) => Left("Error occurred while processing certificate S/N " + cert.getSerialNumber + ": " + e.getMessage)
  }

}
