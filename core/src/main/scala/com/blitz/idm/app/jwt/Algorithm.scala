package com.blitz.idm.app.jwt
import com.blitz.idm.app.CustomEnumeration

/**
 * The enum to represent signing and encrypting algorithms to work with JSON.
 */

object AlgorithmType extends Enumeration {
  type AlgorithmType = Value
  val SIGNING, ENCRYPTION = Value
}
import AlgorithmType._

sealed abstract class Algorithm(private val _name: String,
                                val algType: AlgorithmType,
                                val required: Boolean,
                                val description: String,
                                val supported: Boolean = false) extends Algorithm.Val {
  def name = _name
}

object Algorithm extends CustomEnumeration[Algorithm] {

  case object HS256 extends Algorithm("HS256", SIGNING, true, "HMAC using SHA-256 hash algorithm", true)
  case object NONE  extends Algorithm("none", SIGNING, true, "No digital signature or MAC value included", true)
  case object HS384 extends Algorithm("HS384", SIGNING, false, "HMAC using SHA-384 hash algorithm")
  case object HS512 extends Algorithm("HS512", SIGNING, false, "HMAC using SHA-512 hash algorithm")
  case object RS256 extends Algorithm("RS256", SIGNING, false, "RSASSA-PKCS-v1_5 using SHA-256 hash algorithm")
  case object RS384 extends Algorithm("RS384", SIGNING, false, "RSASSA-PKCS-v1_5 using SHA-384 hash algorithm")
  case object RS512 extends Algorithm("RS512", SIGNING, false, "RSASSA-PKCS-v1_5 using SHA-512 hash algorithm")
  case object ES256 extends Algorithm("ES256", SIGNING, false, "ECDSA using P-256 curve and SHA-256 hash algorithm")
  case object ES384 extends Algorithm("ES384", SIGNING, false, "ECDSA using P-384 curve and SHA-384 hash algorithm")
  case object ES512 extends Algorithm("ES512", SIGNING, false, "ECDSA using P-521 curve and SHA-512 hash algorithm")
  case object PS256 extends Algorithm("PS256", SIGNING, false, "RSASSA-PSS using SHA-256 hash algorithm and MGF1 mask generation function with SHA-256")
  case object PS384 extends Algorithm("PS384", SIGNING, false, "RSASSA-PSS using SHA-384 hash algorithm and MGF1 mask generation function with SHA-384")
  case object PS512 extends Algorithm("PS512", SIGNING, false, "RSASSA-PSS using SHA-512 hash algorithm and MGF1 mask generation function with SHA-512")

  case object RSA1_5   extends Algorithm("RSA1_5", ENCRYPTION, true, "RSAES-PKCS1-V1_5 [RFC3447]", true)
  case object RSA_OAEP extends Algorithm("RSA-OAEP", ENCRYPTION, false, "RSAES using Optimal Asymmetric Encryption Padding (OAEP) [RFC3447], with the default parameters specified by RFC 3447 in Section A.2.1")
  case object A128KW extends Algorithm("A128KW", ENCRYPTION, false, "Advanced Encryption Standard (AES) Key Wrap Algorithm [RFC3394] using the default initial value specified in Section 2.2.3.1 and using 128 bit keys")
  case object A192KW extends Algorithm("A192KW", ENCRYPTION, false, "AES Key Wrap Algorithm using the default initial value specified in Section 2.2.3.1 and using 192 bit keys")
  case object A256KW extends Algorithm("A256KW", ENCRYPTION, false, "AES Key Wrap Algorithm using the default initial value specified in Section 2.2.3.1 and using 256 bit keys")
  case object DIR extends Algorithm("dir", ENCRYPTION, false, "Direct use of a shared symmetric key as the Content Encryption Key (CEK) for the content encryption step (rather than using the symmetric key to wrap the CEK)")
  case object ECDH_ES extends Algorithm("ECDH-ES", ENCRYPTION, false, "Elliptic Curve Diffie-Hellman Ephemeral Static [RFC6090] key agreement using the Concat KDF, as defined in Section 5.8.1 of [NIST.800-56A], with the agreed-upon key being used directly as the Content Encryption Key (CEK) (rather than being used to wrap the CEK), as specified in Section 4.7")
  case object ECDH_ES_A128KW extends Algorithm("ECDH-ES+A128KW", ENCRYPTION, false, "Elliptic Curve Diffie-Hellman Ephemeral Static key agreement per ECDH-ES and Section 4.7, where the agreed-upon key is used to wrap the Content Encryption Key (CEK) with the A128KW function (rather than being used directly as the CEK)")
  case object ECDH_ES_A192KW extends Algorithm("ECDH-ES+A192KW", ENCRYPTION, false, "Elliptic Curve Diffie-Hellman Ephemeral Static key agreement, where the agreed-upon key is used to wrap the Content Encryption Key (CEK) with the A192KW function (rather than being used directly as the CEK)")
  case object ECDH_ES_A256KW extends Algorithm("ECDH-ES+A256KW", ENCRYPTION, false, "Elliptic Curve Diffie-Hellman Ephemeral Static key agreement, where the agreed-upon key is used to wrap the Content Encryption Key (CEK) with the A256KW function (rather than being used directly as the CEK)")
  case object A128GCMKW extends Algorithm("A128GCMKW", ENCRYPTION, false, "AES in Galois/Counter Mode (GCM) [AES] [NIST.800-38D] using 128 bit keys")
  case object A192GCMKW extends Algorithm("A192GCMKW", ENCRYPTION, false, "AES GCM using 192 bit keys")
  case object A256GCMKW extends Algorithm("A256GCMKW", ENCRYPTION, false, "AES GCM using 256 bit keys")
  case object PBES2_HS256_A128KW extends Algorithm("PBES2-HS256+A128KW", ENCRYPTION, false, "PBES2 [RFC2898] with HMAC SHA-256 as the PRF and AES Key Wrap [RFC3394] using 128 bit keys for the encryption scheme")
  case object PBES2_HS256_A192KW extends Algorithm("PBES2-HS256+A192KW", ENCRYPTION, false, "PBES2 with HMAC SHA-256 as the PRF and AES Key Wrap using 192 bit keys for the encryption scheme")
  case object PBES2_HS256_A256KW extends Algorithm("PBES2-HS256+A256KW", ENCRYPTION, false, "PBES2 with HMAC SHA-256 as the PRF and AES Key Wrap using 256 bit keys for the encryption scheme")

}
