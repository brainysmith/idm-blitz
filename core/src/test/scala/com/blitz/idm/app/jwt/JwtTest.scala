package com.blitz.idm.app.jwt

import com.blitz.idm.app.json.{JNull, Json}
import java.net.URISyntaxException
import org.specs2.mutable.Specification

/**
 *
 */
class JwtTest extends Specification {

  "Checking of JWT implementation " should {

    "checking of existence of HS256 algorithm " in {
      Algorithm.valueOf("HS256") must be equalTo Algorithm.HS256
    }

    "checking of existence of none algorithm " in {
      Algorithm.valueOf("none") must be equalTo Algorithm.NONE
    }

    "checking of existence of HS384 algorithm " in {
      Algorithm.valueOf("HS384") must be equalTo Algorithm.HS384
    }

    "creating StringOrUri with plain string 'test string' " in {
      StringOrUri("test string").isUri must be equalTo false
    }

    "creating StringOrUri with plain string 'test string' " in {
      StringOrUri("test string").toString must be equalTo "test string"
    }

    "creating StringOrUri with URI 'ftp://ftp.is.co.za/rfc/rfc1808.txt' " in {
      StringOrUri("ftp://ftp.is.co.za/rfc/rfc1808.txt").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'ftp://ftp.is.co.za/rfc/rfc1808.txt' " in {
      StringOrUri("ftp://ftp.is.co.za/rfc/rfc1808.txt").toString must be equalTo "ftp://ftp.is.co.za/rfc/rfc1808.txt"
    }

    "creating StringOrUri with URI 'http://www.ietf.org/rfc/rfc2396.txt' " in {
      StringOrUri("http://www.ietf.org/rfc/rfc2396.txt").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'ldap://[2001:db8::7]/c=GB?objectClass?one' " in {
      StringOrUri("ldap://[2001:db8::7]/c=GB?objectClass?one").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'mailto:John.Doe@example.com' " in {
      StringOrUri("mailto:John.Doe@example.com").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'news:comp.infosystems.www.servers.unix' " in {
      StringOrUri("news:comp.infosystems.www.servers.unix").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'tel:+1-816-555-1212' " in {
      StringOrUri("tel:+1-816-555-1212").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'telnet://192.0.2.16:80/' " in {
      StringOrUri("telnet://192.0.2.16:80/").isUri must be equalTo true
    }

    "creating StringOrUri with URI 'urn:oasis:names:specification:docbook:dtd:xml:4.1.2' " in {
      StringOrUri("urn:oasis:names:specification:docbook:dtd:xml:4.1.2").isUri
    }

    "creating StringOrUri with URI 'ftp://ftp.is co.za/rfc/rfc1808.txt' " in {
      StringOrUri("ftp://ftp.is co.za/rfc/rfc1808.txt") must throwA[URISyntaxException]
    }

    "creating IntDate by passing the number of seconds " in {
      IntDate(7).value must be equalTo 7
    }

    "creating IntDate by passing the negative value as the number of seconds " in {
      IntDate(-7).value must throwA[IllegalArgumentException]
    }

    "creating IntDate by passing string representation of date/time " in {
      IntDate("2000-01-01T0:0:0Z").value must be equalTo 1791994880
    }

    "creating IntDate by passing wrong string representation of date/time " in {
      IntDate("2000_01-01T0:0:0Z").value must throwA[IllegalArgumentException]
    }

    //for later use
    val jsonCertChain ="[\"MIIE3jCCA8agAwIBAgICAwEwDQYJKoZIhvcNAQEFBQAwYzELMAkGA1UEBhMCVVM\n" +
                          "xITAfBgNVBAoTGFRoZSBHbyBEYWRkeSBHcm91cCwgSW5jLjExMC8GA1UECxMoR2\n" +
                          "8gRGFkZHkgQ2xhc3MgMiBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTAeFw0wNjExM\n" +
                          "TYwMTU0MzdaFw0yNjExMTYwMTU0MzdaMIHKMQswCQYDVQQGEwJVUzEQMA4GA1UE\n" +
                          "CBMHQXJpem9uYTETMBEGA1UEBxMKU2NvdHRzZGFsZTEaMBgGA1UEChMRR29EYWR\n" +
                          "keS5jb20sIEluYy4xMzAxBgNVBAsTKmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuZ29kYW\n" +
                          "RkeS5jb20vcmVwb3NpdG9yeTEwMC4GA1UEAxMnR28gRGFkZHkgU2VjdXJlIENlc\n" +
                          "nRpZmljYXRpb24gQXV0aG9yaXR5MREwDwYDVQQFEwgwNzk2OTI4NzCCASIwDQYJ\n" +
                          "KoZIhvcNAQEBBQADggEPADCCAQoCggEBAMQt1RWMnCZM7DI161+4WQFapmGBWTt\n" +
                          "wY6vj3D3HKrjJM9N55DrtPDAjhI6zMBS2sofDPZVUBJ7fmd0LJR4h3mUpfjWoqV\n" +
                          "Tr9vcyOdQmVZWt7/v+WIbXnvQAjYwqDL1CBM6nPwT27oDyqu9SoWlm2r4arV3aL\n" +
                          "GbqGmu75RpRSgAvSMeYddi5Kcju+GZtCpyz8/x4fKL4o/K1w/O5epHBp+YlLpyo\n" +
                          "7RJlbmr2EkRTcDCVw5wrWCs9CHRK8r5RsL+H0EwnWGu1NcWdrxcx+AuP7q2BNgW\n" +
                          "JCJjPOq8lh8BJ6qf9Z/dFjpfMFDniNoW1fho3/Rb2cRGadDAW/hOUoz+EDU8CAw\n" +
                          "EAAaOCATIwggEuMB0GA1UdDgQWBBT9rGEyk2xF1uLuhV+auud2mWjM5zAfBgNVH\n" +
                          "SMEGDAWgBTSxLDSkdRMEXGzYcs9of7dqGrU4zASBgNVHRMBAf8ECDAGAQH/AgEA\n" +
                          "MDMGCCsGAQUFBwEBBCcwJTAjBggrBgEFBQcwAYYXaHR0cDovL29jc3AuZ29kYWR\n" +
                          "keS5jb20wRgYDVR0fBD8wPTA7oDmgN4Y1aHR0cDovL2NlcnRpZmljYXRlcy5nb2\n" +
                          "RhZGR5LmNvbS9yZXBvc2l0b3J5L2dkcm9vdC5jcmwwSwYDVR0gBEQwQjBABgRVH\n" +
                          "SAAMDgwNgYIKwYBBQUHAgEWKmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuZ29kYWRkeS5j\n" +
                          "b20vcmVwb3NpdG9yeTAOBgNVHQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQEFBQADggE\n" +
                          "BANKGwOy9+aG2Z+5mC6IGOgRQjhVyrEp0lVPLN8tESe8HkGsz2ZbwlFalEzAFPI\n" +
                          "UyIXvJxwqoJKSQ3kbTJSMUA2fCENZvD117esyfxVgqwcSeIaha86ykRvOe5GPLL\n" +
                          "5CkKSkB2XIsKd83ASe8T+5o0yGPwLPk9Qnt0hCqU7S+8MxZC9Y7lhyVJEnfzuz9\n" +
                          "p0iRFEUOOjZv2kWzRaJBydTXRE4+uXR21aITVSzGh6O1mawGhId/dQb8vxRMDsx\n" +
                          "uxN89txJx9OjxUUAiKEngHUuHqDTMBqLdElrRhjZkAzVvb3du6/KFUJheqwNTrZ\n" +
                          "EjYx8WnM25sgVjOuH0aBsXBTWVU+4=\",\n" +
                        "\"MIIE+zCCBGSgAwIBAgICAQ0wDQYJKoZIhvcNAQEFBQAwgbsxJDAiBgNVBAcTG1Z\n" +
                          "hbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIE\n" +
                          "luYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb\n" +
                          "24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8x\n" +
                          "IDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMB4XDTA0MDYyOTE3MDY\n" +
                          "yMFoXDTI0MDYyOTE3MDYyMFowYzELMAkGA1UEBhMCVVMxITAfBgNVBAoTGFRoZS\n" +
                          "BHbyBEYWRkeSBHcm91cCwgSW5jLjExMC8GA1UECxMoR28gRGFkZHkgQ2xhc3MgM\n" +
                          "iBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTCCASAwDQYJKoZIhvcNAQEBBQADggEN\n" +
                          "ADCCAQgCggEBAN6d1+pXGEmhW+vXX0iG6r7d/+TvZxz0ZWizV3GgXne77ZtJ6XC\n" +
                          "APVYYYwhv2vLM0D9/AlQiVBDYsoHUwHU9S3/Hd8M+eKsaA7Ugay9qK7HFiH7Eux\n" +
                          "6wwdhFJ2+qN1j3hybX2C32qRe3H3I2TqYXP2WYktsqbl2i/ojgC95/5Y0V4evLO\n" +
                          "tXiEqITLdiOr18SPaAIBQi2XKVlOARFmR6jYGB0xUGlcmIbYsUfb18aQr4CUWWo\n" +
                          "riMYavx4A6lNf4DD+qta/KFApMoZFv6yyO9ecw3ud72a9nmYvLEHZ6IVDd2gWMZ\n" +
                          "Eewo+YihfukEHU1jPEX44dMX4/7VpkI+EdOqXG68CAQOjggHhMIIB3TAdBgNVHQ\n" +
                          "4EFgQU0sSw0pHUTBFxs2HLPaH+3ahq1OMwgdIGA1UdIwSByjCBx6GBwaSBvjCBu\n" +
                          "zEkMCIGA1UEBxMbVmFsaUNlcnQgVmFsaWRhdGlvbiBOZXR3b3JrMRcwFQYDVQQK\n" +
                          "Ew5WYWxpQ2VydCwgSW5jLjE1MDMGA1UECxMsVmFsaUNlcnQgQ2xhc3MgMiBQb2x\n" +
                          "pY3kgVmFsaWRhdGlvbiBBdXRob3JpdHkxITAfBgNVBAMTGGh0dHA6Ly93d3cudm\n" +
                          "FsaWNlcnQuY29tLzEgMB4GCSqGSIb3DQEJARYRaW5mb0B2YWxpY2VydC5jb22CA\n" +
                          "QEwDwYDVR0TAQH/BAUwAwEB/zAzBggrBgEFBQcBAQQnMCUwIwYIKwYBBQUHMAGG\n" +
                          "F2h0dHA6Ly9vY3NwLmdvZGFkZHkuY29tMEQGA1UdHwQ9MDswOaA3oDWGM2h0dHA\n" +
                          "6Ly9jZXJ0aWZpY2F0ZXMuZ29kYWRkeS5jb20vcmVwb3NpdG9yeS9yb290LmNybD\n" +
                          "BLBgNVHSAERDBCMEAGBFUdIAAwODA2BggrBgEFBQcCARYqaHR0cDovL2NlcnRpZ\n" +
                          "mljYXRlcy5nb2RhZGR5LmNvbS9yZXBvc2l0b3J5MA4GA1UdDwEB/wQEAwIBBjAN\n" +
                          "BgkqhkiG9w0BAQUFAAOBgQC1QPmnHfbq/qQaQlpE9xXUhUaJwL6e4+PrxeNYiY+\n" +
                          "Sn1eocSxI0YGyeR+sBjUZsE4OWBsUs5iB0QQeyAfJg594RAoYC5jcdnplDQ1tgM\n" +
                          "QLARzLrUc+cb53S8wGd9D0VmsfSxOaFIqII6hR8INMqzW/Rn453HWkrugp++85j\n" +
                          "09VZw==\",\n" +
                        "\"MIIC5zCCAlACAQEwDQYJKoZIhvcNAQEFBQAwgbsxJDAiBgNVBAcTG1ZhbGlDZXJ\n" +
                          "0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNT\n" +
                          "AzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0a\n" +
                          "G9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkq\n" +
                          "hkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMB4XDTk5MDYyNjAwMTk1NFoXDTE\n" +
                          "5MDYyNjAwMTk1NFowgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTm\n" +
                          "V0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZ\n" +
                          "XJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQD\n" +
                          "ExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9\n" +
                          "AdmFsaWNlcnQuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOOnHK5a\n" +
                          "vIWZJV16vYdA757tn2VUdZZUcOBVXc65g2PFxTXdMwzzjsvUGJ7SVCCSRrCl6zf\n" +
                          "N1SLUzm1NZ9WlmpZdRJEy0kTRxQb7XBhVQ7/nHk01xC+YDgkRoKWzk2Z/M/VXwb\n" +
                          "P7RfZHM047QSv4dk+NoS/zcnwbNDu+97bi5p9wIDAQABMA0GCSqGSIb3DQEBBQU\n" +
                          "AA4GBADt/UG9vUJSZSWI4OB9L+KXIPqeCgfYrx+jFzug6EILLGACOTb2oWH+heQ\n" +
                          "C1u+mNr0HZDzTuIYEZoDJJKPTEjlbVUjP9UNV+mWwD5MlM/Mtsq2azSiGM5bUMM\n" +
                          "j4QssxsodyamEwCW/POuZ6lcg5Ktz885hZo+L7tdEy8W9ViH0Pd\"]"

    "" in {
      "" must be equalTo ""
    }

    "creating a Plaintext JWT header from a string " in {
      SimpleHeaderFactory.header("eyJhbGciOiJub25lIn0").toString must be equalTo "{\"alg\":\"none\"}"
    }

  }

}
