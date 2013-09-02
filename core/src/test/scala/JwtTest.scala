import com.blitz.idm.app.json.{JNull, Json}
import com.blitz.idm.app.jwt.Algorithm
import org.specs2.mutable.Specification

/**
 *
 */
class JwtTest extends Specification {

  "Checking of JSON serialization" should {

    "checking of existence of HS256 algorithm " in {
      Algorithm.valueOf("HS256") must be equalTo Algorithm.HS256
    }
  }

}
