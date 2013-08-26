package utils

/**
  */

import conf.WlApp._

trait IntegrationTestUtil {

  val dirOfScreenShots = conf.main.tests.dirOfTests  + "/screenShots/"

  def makeScreenShotPath(baseName: String): String = {
    val time = System.currentTimeMillis()
    val fileName = baseName + "." + time + ".png"
    dirOfScreenShots + fileName
  }

}
