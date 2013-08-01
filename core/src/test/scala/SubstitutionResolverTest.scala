import com.blitz.idm.app.SubstitutionResolver
import org.specs2.mutable.Specification

/**
 *
 */
class SubstitutionResolverTest extends Specification {

  System.setProperty("blitzConfUrl", classOf[MainConfigurationTest].getResource("idm_blitz.conf").toURI.toString)

  val orig1 = "Resolve: ${main-conf.data-dir}"
  val resolved1 = "Resolve: ./core/target/test-classes"
  val orig2 = "Resolve: ${main-conf.data-directory}"
  val resolved2 = "Resolve: ${main-conf.data-directory}"
  val orig3 = "Resolve: {main-conf.data-directory}"
  val resolved3 = "Resolve: {main-conf.data-directory}"
  val orig4 = "Resolve: $main-conf.data-directory}"
  val resolved4 = "Resolve: $main-conf.data-directory}"
  val orig5 = "Resolve: $${main-conf.data-dir}"
  val resolved5 = "Resolve: $./core/target/test-classes"
  val orig6 = "Resolve: ${main-conf.data-dir}}"
  val resolved6 = "Resolve: ./core/target/test-classes}"
  val orig7 = "Resolve: ${main-conf.data-dir}, ${idm-conf.idm-home}, ${main-conf.logger.levels.core}"
  val resolved7 = "Resolve: ./core/target/test-classes, ./core/target/test-classes/idm, DEBUG"

  "\nChecking of a substitution resolver " should {

    orig1 + " => " + resolved1 in {
      SubstitutionResolver.resolve(orig1) must be equalTo(resolved1)
    }

    orig2 + " => " + resolved2 in {
      SubstitutionResolver.resolve(orig2) must be equalTo(resolved2)
    }

    orig3 + " => " + resolved3 in {
      SubstitutionResolver.resolve(orig3) must be equalTo(resolved3)
    }

    orig4 + " => " + resolved4 in {
      SubstitutionResolver.resolve(orig4) must be equalTo(resolved4)
    }

    orig5 + " => " + resolved5 in {
      SubstitutionResolver.resolve(orig5) must be equalTo(resolved5)
    }

    orig6 + " => " + resolved6 in {
      SubstitutionResolver.resolve(orig6) must be equalTo(resolved6)
    }

    orig7 + " => " + resolved7 in {
      SubstitutionResolver.resolve(orig7) must be equalTo(resolved7)
    }

  }

}
