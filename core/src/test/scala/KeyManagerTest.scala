import java.io.ByteArrayInputStream
import java.security.cert.{X509Certificate, CertificateFactory}
import org.specs2.mutable.Specification
import com.blitz.idm.app._

/**
 *
 */
class KeyManagerTest extends Specification {

  "Checking of Key manager" should {

    val pemCAcert = "-----BEGIN CERTIFICATE-----\r\n" +
      "MIICQTCCAaqgAwIBAgIEUimOHjANBgkqhkiG9w0BAQUFADBlMQswCQYDVQQGEwJSVTEPMA0GA1UE\r\n" +
      "CBMGTW9zY293MQ8wDQYDVQQHEwZNb3Njb3cxDjAMBgNVBAoTBWJsaXR6MRQwEgYDVQQLEwtkZXZl\r\n" +
      "bG9wbWVudDEOMAwGA1UEAxMFYmxpdHowHhcNMTMwOTA2MDgxMTEwWhcNMjAwOTA0MDgxMTEwWjBl\r\n" +
      "MQswCQYDVQQGEwJSVTEPMA0GA1UECBMGTW9zY293MQ8wDQYDVQQHEwZNb3Njb3cxDjAMBgNVBAoT\r\n" +
      "BWJsaXR6MRQwEgYDVQQLEwtkZXZlbG9wbWVudDEOMAwGA1UEAxMFYmxpdHowgZ8wDQYJKoZIhvcN\r\n" +
      "AQEBBQADgY0AMIGJAoGBANvLaX2n1/+9EU0vLcapLyUj2rlHvJc5+dw4nob+diz3ZC8P9X16vu4f\r\n" +
      "A/qTE7t/4HHsEyiXOzlMDzZYyqtmh/XAzHZ1TYB5WZKzBsqlxLq2/vlqTtLzZDM/mz1cXHq2QES9\r\n" +
      "V51dbTFsiFqwddtqjXkuZuzClAJ5htzLhwoN2MKNAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEAdHfb\r\n" +
      "nQSGicYNoMWysXqLfb8g29h1MocYXcLN2MUmMfVC7uHuWMJ0vWap8/bGzZKluV9rZb67I/PGJH0T\r\n" +
      "bp5gYoLlv0VFyE++iEtQNmW0CW/KdisqHyD9hjou4B/CicpfFhjCxUvs1fTxE+/vnKnWoshk5KSp\r\n" +
      "rvRJNEvzT/+soQw=\r\n" +
      "-----END CERTIFICATE-----"

    val pemAdditionalCert = "-----BEGIN CERTIFICATE-----\r\n" +
      "MIICQTCCAaqgAwIBAgIEUinNyjANBgkqhkiG9w0BAQUFADBlMQswCQYDVQQGEwJSVTEPMA0GA1UE\r\n" +
      "CBMGTW9zY293MQ8wDQYDVQQHEwZNb3Njb3cxDjAMBgNVBAoTBWJsaXR6MRQwEgYDVQQLEwtEZXZl\r\n" +
      "bG9wbWVudDEOMAwGA1UEAxMFYmxpdHowHhcNMTMwOTA2MTI0MjUwWhcNMjAwOTA0MTI0MjUwWjBl\r\n" +
      "MQswCQYDVQQGEwJSVTEPMA0GA1UECBMGTW9zY293MQ8wDQYDVQQHEwZNb3Njb3cxDjAMBgNVBAoT\r\n" +
      "BWJsaXR6MRQwEgYDVQQLEwtEZXZlbG9wbWVudDEOMAwGA1UEAxMFYmxpdHowgZ8wDQYJKoZIhvcN\r\n" +
      "AQEBBQADgY0AMIGJAoGBAIGK1RyfTLf1P3KdfMqZAnoDv0mfffaamBQ1CsAaIfncE3o48ehTLjBb\r\n" +
      "F3+EE+nal24sVolWKeJDoJ6DRHBEUEfJuArbfuke5MUWCJu0i2gid+Sor1AvIUCpXmCcsvU07ZxX\r\n" +
      "Gi62wei3+XvOhVEYA7dGBy90dc385kFOVoWYr3+/AgMBAAEwDQYJKoZIhvcNAQEFBQADgYEADQGB\r\n" +
      "RMd4VbVhCm1ndV637fNL/iDa/63uVKMhBDFItkjFW6Kg+4EF4LURA3ZdQYp6jkU6XKB97z/7cKVI\r\n" +
      "9a6+p3ok325p/qh80D7NWZsvtCD3Z1W38dFzAX3xvykcPYfUfhyMAW+42afkrqWMM3W0PfVnWNBQ\r\n" +
      "9FpzAD7HAh7LZag=\r\n" +
      "-----END CERTIFICATE-----"

    val pemCertChain = "-----BEGIN CERTIFICATE-----\n" +
      "MIIDSjCCAwigAwIBAgIEIZYScDALBgcqhkjOOAQDBQAwZjELMAkGA1UEBhMCUlUxDzANBgNVBAgT\n" +
      "Bk1vc2NvdzEPMA0GA1UEBxMGTW9zY293MQ4wDAYDVQQKEwVibGl0ejEUMBIGA1UECxMLRGV2ZWxv\n" +
      "cG1lbnQxDzANBgNVBAMTBnNibGl0ejAeFw0xMzA5MDYxNTU0MzJaFw0xMzEyMDUxNTU0MzJaMGYx\n" +
      "CzAJBgNVBAYTAlJVMQ8wDQYDVQQIEwZNb3Njb3cxDzANBgNVBAcTBk1vc2NvdzEOMAwGA1UEChMF\n" +
      "YmxpdHoxFDASBgNVBAsTC0RldmVsb3BtZW50MQ8wDQYDVQQDEwZsYmxpdHowggG4MIIBLAYHKoZI\n" +
      "zjgEATCCAR8CgYEA/X9TgR11EilS30qcLuzk5/YRt1I870QAwx4/gLZRJmlFXUAiUftZPY1Y+r/F\n" +
      "9bow9subVWzXgTuAHTRv8mZgt2uZUKWkn5/oBHsQIsJPu6nX/rfGG/g7V+fGqKYVDwT7g/bTxR7D\n" +
      "AjVUE1oWkTL2dfOuK2HXKu/yIgMZndFIAccCFQCXYFCPFSMLzLKSuYKi64QL8Fgc9QKBgQD34aCF\n" +
      "1ps93su8q1w2uFe5eZSvu/o66oL5V0wLPQeCZ1FZV4661FlP5nEHEIGAtEkWcSPoTCgWE7fPCTKM\n" +
      "yKbhPBZ6i1R8jSjgo64eK7OmdZFuo38L+iE1YvH7YnoBJDvMpPG+qFGQiaiD3+Fa5Z8GkotmXoB7\n" +
      "VSVkAUw7/s9JKgOBhQACgYEAnX5q0Ya/IOoN/M5cv2XwmPRWoSrsoUllPWwMNhTKPDtuIm/vZU4W\n" +
      "9Yj2DQw0xqV0YHnK+90qU8XFzwcbb68d8UNUlh9O8sO0EYhi5GarnnqL7vdu1o5UkU76Ews5qo2k\n" +
      "Eo5X0tnfshRXaKn9jDX0zEsgcewKVfHoOdHDuqGePnKjQjBAMB8GA1UdIwQYMBaAFF6B/tq8T6H2\n" +
      "vyI2BumDUfjkg9UoMB0GA1UdDgQWBBTiM3EkQhmfR32A5EYUNnWT1nLEXDALBgcqhkjOOAQDBQAD\n" +
      "LwAwLAIUPRMz4/784I5wzwNHYAmHOyoHMa8CFHugc8ZjVPp76PbYAgHQWAz5I2JL\n" +
      "-----END CERTIFICATE-----"

    val pemSecondCert = "-----BEGIN CERTIFICATE-----\n" +
      "MIIDWzCCAxmgAwIBAgIEONgaQDALBgcqhkjOOAQDBQAwZjELMAkGA1UEBhMCUlUxDzANBgNVBAgT\n" +
      "Bk1vc2NvdzEPMA0GA1UEBxMGTW9zY293MQ4wDAYDVQQKEwVibGl0ejEUMBIGA1UECxMLRGV2ZWxv\n" +
      "cG1lbnQxDzANBgNVBAMTBmZibGl0ejAeFw0xMzA5MDYxNTQ5MTlaFw0xMzEyMDUxNTQ5MTlaMGYx\n" +
      "CzAJBgNVBAYTAlJVMQ8wDQYDVQQIEwZNb3Njb3cxDzANBgNVBAcTBk1vc2NvdzEOMAwGA1UEChMF\n" +
      "YmxpdHoxFDASBgNVBAsTC0RldmVsb3BtZW50MQ8wDQYDVQQDEwZzYmxpdHowggG4MIIBLAYHKoZI\n" +
      "zjgEATCCAR8CgYEA/X9TgR11EilS30qcLuzk5/YRt1I870QAwx4/gLZRJmlFXUAiUftZPY1Y+r/F\n" +
      "9bow9subVWzXgTuAHTRv8mZgt2uZUKWkn5/oBHsQIsJPu6nX/rfGG/g7V+fGqKYVDwT7g/bTxR7D\n" +
      "AjVUE1oWkTL2dfOuK2HXKu/yIgMZndFIAccCFQCXYFCPFSMLzLKSuYKi64QL8Fgc9QKBgQD34aCF\n" +
      "1ps93su8q1w2uFe5eZSvu/o66oL5V0wLPQeCZ1FZV4661FlP5nEHEIGAtEkWcSPoTCgWE7fPCTKM\n" +
      "yKbhPBZ6i1R8jSjgo64eK7OmdZFuo38L+iE1YvH7YnoBJDvMpPG+qFGQiaiD3+Fa5Z8GkotmXoB7\n" +
      "VSVkAUw7/s9JKgOBhQACgYEA4EbCm3xQ8EoZu0y5CQhNmhyioJ+N2lnNxdWsGPivUXun7hyNVkF0\n" +
      "kpICpcSPmXQSsBGXt5AXoWKiCU+xZSuWF4dBuiHz/B9G7k1KPAbmkJzAOWO0mFMNNxot/eyqdQkR\n" +
      "orT8F9dG5rmOKT9VPy2gOiE0e2jfHPXE+elXhyZFSBujUzBRMB8GA1UdIwQYMBaAFMqK+8DYyFiq\n" +
      "02pvyt5cZBJmMKwUMA8GA1UdEwQIMAYBAf8CAQcwHQYDVR0OBBYEFF6B/tq8T6H2vyI2BumDUfjk\n" +
      "g9UoMAsGByqGSM44BAMFAAMvADAsAhQgzrxARBuxfh346zGuvRJWccC8MwIUARvr8GQfskzbrbc3\n" +
      "azKu/QWLIhA=\n" +
      "-----END CERTIFICATE-----"

    val pemFirstCert = "-----BEGIN CERTIFICATE-----\n" +
      "MIIDsDCCAxmgAwIBAgIEKSqj4jANBgkqhkiG9w0BAQsFADBlMQswCQYDVQQGEwJSVTEPMA0GA1UE\n" +
      "CBMGTW9zY293MQ8wDQYDVQQHEwZNb3Njb3cxDjAMBgNVBAoTBWJsaXR6MRQwEgYDVQQLEwtkZXZl\n" +
      "bG9wbWVudDEOMAwGA1UEAxMFYmxpdHowHhcNMTMwOTA2MTU0ODEwWhcNMTMxMjA1MTU0ODEwWjBm\n" +
      "MQswCQYDVQQGEwJSVTEPMA0GA1UECBMGTW9zY293MQ8wDQYDVQQHEwZNb3Njb3cxDjAMBgNVBAoT\n" +
      "BWJsaXR6MRQwEgYDVQQLEwtEZXZlbG9wbWVudDEPMA0GA1UEAxMGZmJsaXR6MIIBtzCCASwGByqG\n" +
      "SM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/\n" +
      "xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208Ue\n" +
      "wwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+Gg\n" +
      "hdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwky\n" +
      "jMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6A\n" +
      "e1UlZAFMO/7PSSoDgYQAAoGAXSwijUAbtpIKL4S12LtPCRoeeV+8pRExYd77gG7p5GLv4snmBqjf\n" +
      "M6PqeKevR09cwLKBA2kvn2nPrQJSxM1XwNYn+xrWYDKHx+EvMRfP2yvsr5zmig06aFnxFnm9FSnN\n" +
      "u4z15aRzYdGxSCjJf0ROhaN3Hij72NQWncLTnxw46/+jUzBRMB8GA1UdIwQYMBaAFP+nwZFc/Qrr\n" +
      "2Z4lHIBfELE9hIBJMA8GA1UdEwQIMAYBAf8CAQcwHQYDVR0OBBYEFMqK+8DYyFiq02pvyt5cZBJm\n" +
      "MKwUMA0GCSqGSIb3DQEBCwUAA4GBAFvDcxIXC2zJvxIg/P9u8OzofxC+tDf6hQxlTRYqKLsQyudM\n" +
      "hkuSACO7oYT/LIpe66t7WD969xyPKmERifj0Ga8garnzNR6Bt83gPZJM1Qd7JwCl+HzTzqHkvD5D\n" +
      "W8WDsCL+s0gteag9U24k+MrQGGxR6z3kHRTbxuoLPQ9BkMoS\n" +
      "-----END CERTIFICATE-----"

    val factory = CertificateFactory.getInstance("X.509")
    val caCert = factory.generateCertificate(new ByteArrayInputStream(pemCAcert.getBytes("UTF-8"))).asInstanceOf[X509Certificate]
    val additionalCert = factory.generateCertificate(new ByteArrayInputStream(pemAdditionalCert.getBytes("UTF-8"))).asInstanceOf[X509Certificate]

    val certChain = factory.generateCertificate(new ByteArrayInputStream(pemCertChain.getBytes("UTF-8"))).asInstanceOf[X509Certificate]
    val secondCert = factory.generateCertificate(new ByteArrayInputStream(pemSecondCert.getBytes("UTF-8"))).asInstanceOf[X509Certificate]
    val firstCert = factory.generateCertificate(new ByteArrayInputStream(pemFirstCert.getBytes("UTF-8"))).asInstanceOf[X509Certificate]

    "checking of loading of trusted certificates " in {
      keystoreManager.trustCerts.head.getSubjectDN.getName must be equalTo "CN=blitz, OU=development, O=blitz, L=Moscow, ST=Moscow, C=RU"
    }

    "validating of a valid certificate " in {
      keystoreManager.verifyCertificate(caCert, Set.empty) must be equalTo Right(caCert)
    }

    "validating of a wrong certificate " in {
      keystoreManager.verifyCertificate(additionalCert, Set.empty) must be equalTo Left("TrustAnchor found but certificate validation failed.")
    }

    "validating of a wrong certificate chain" in {
      keystoreManager.verifyCertificate(certChain, Set(secondCert)) must be equalTo Left("No issuer certificate for certificate in certification path found.")
    }

    "validating of a valid certificate chain" in {
      keystoreManager.verifyCertificate(certChain, Set(firstCert, secondCert)) must be equalTo Right(certChain)
    }



  }
}
