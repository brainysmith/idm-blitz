import sbt._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "web-login"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
/*    jdbc,
    anorm*/
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
      {
        requireJsFolder := "js"
        requireJs ++= Seq("main.js", "login.js", "/utils/placeholder.js")
        externalIvySettingsURL(url(System.getProperty("ivy.settings.path")))
      }
  )

}
