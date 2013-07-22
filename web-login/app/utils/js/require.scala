package utils.js

import play.api.templates.Html
import views.html.helper.requireJs
import controllers.routes

object require {

  def apply(module: String): Html = {
    requireJs(module, routes.Assets.at("js/lib/require.min.js").url)
  }
}
