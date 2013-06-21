import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "web-login"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
      {       
        externalIvySettingsURL(url(System.getProperty("ivy.settings.path")))
//        externalIvyFile(baseDirectory(_ / "ivy.xml"))
      }
  )

}
