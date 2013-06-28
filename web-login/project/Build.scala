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
        /*requireJs += "main.js"*/
        requireJsFolder := "js"
        requireJs ++= Seq("app.js", "login.js", "/utils/placeholder.js")
        externalIvySettingsURL(url(System.getProperty("ivy.settings.path")))
        /*requireJs ++= Seq("main.js","/utils/placeholder.js")*/
//        externalIvyFile(baseDirectory(_ / "ivy.xml"))
      }
  )

}
