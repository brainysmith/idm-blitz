package utils

/**
  */

import conf.WlApp._

trait IntegrationTestUtil {

  val dirOfScreenShots = conf.main.tests.dirOfScreenShots

  def makeScreenShotPath(baseName: String): String = {
    val time = System.currentTimeMillis()
    val fileName = baseName + "." + time + ".png"
    dirOfScreenShots.fold(fileName)(_ + "/" + fileName)
  }

}
