import sbt._
import play.Project._
import Keys._

import sbt._
object ApplicationBuild extends Build {

  val appName         = "web-login"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
/*    jdbc,
    anorm*/
  )

  val main = play.Project(appName, appVersion, appDependencies, file("."), settings = Defaults.defaultSettings ++ Seq(
    unmanagedJars in Compile := {
      Option(System.getProperty("dep.unmanaged")).map(list => {
        list.split(';').toSeq.map(new File(_))
      }).getOrElse(Seq.empty).classpath
    }
  )).settings(
      {
        requireJsFolder := "js"
        requireJs ++= Seq("conf.js", "main.js", "login.js", "/utils/placeholder.js")
        externalIvySettingsURL(url(System.getProperty("ivy.settings.path")))
      }
  )

}
