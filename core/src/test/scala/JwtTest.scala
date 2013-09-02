import com.blitz.idm.app.json.{JNull, Json}
import com.blitz.idm.app.jwt.{StringOrUri, Algorithm}
import java.net.URISyntaxException
import org.specs2.mutable.Specification

/**
 *
 */
class JwtTest extends Specification {

  "Checking of JSON serialization" should {

    "checking of existence of HS256 algorithm " in {
      Algorithm.valueOf("HS256") must be equalTo Algorithm.HS256
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









  }

}
