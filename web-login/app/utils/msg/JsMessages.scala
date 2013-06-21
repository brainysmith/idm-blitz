package utils.msg

import play.api.i18n._
import play.api.Application
import play.api.templates.Html

object JsMessages {

  /**
   * Generates a JavaScript function able to compute localized messages filtered by the a prefix.
   *
   * Example:
   *
   * {{{
   *   JsMessages.subset("pageName")
   * }}}
   */
  def apply(prefixes: String*)(implicit app: Application, lang: Lang): Html = {
    byPrefix(Option("msg"), Option(prefixes))(app, lang)
  }

  def apply(namespace: Option[String])(prefixes: String*)(implicit app: Application, lang: Lang): Html = {
    byPrefix(namespace, Option(prefixes))(app, lang)
  }

  /**
   * Generates a JavaScript function able to compute localized messages for a given keys subset.
   *
   * Example:
   *
   * {{{
   *   JsMessages.subset(
   *     "error.required",
   *     "error.number"
   *   )(Some("window.MyMessages"))
   * }}}
   */
  def byKeys(keys: String*)(namespace: Option[String] = Some("msg"))(implicit app: Application, lang: Lang): Html = {
    val messages = (for {
      key <- keys
      message <- allMessages.get(key)
    } yield (key, message)).toMap
    apply(namespace, messages)
  }

  private def byPrefix(namespace: Option[String], prefixesOpt: Option[Seq[String]])(implicit app: Application, lang: Lang): Html = {
    prefixesOpt match {
      case Some(prefixes) => apply(namespace, allMessages.filter(msg => prefixes.exists(prefix => msg._1.startsWith(prefix))))
      case None => apply(namespace, allMessages)
    }
  }


  private def apply(namespace: Option[String], messages: Map[String, String]): Html = {
    import org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript
    Html("""%s(function(u){var ms={%s};return function(k){if(typeof k == "object"){for(var i=0;i<k.length&&ms[k[i]]!==u;i++);var m=ms[k[i]]||k[0]}else{m=((ms[k]!==u)?ms[k]:k)}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})()""".format(
      namespace.map{_ + "="}.getOrElse(""),
      (for ((key, msg) <- messages) yield {
        "'%s':'%s'".format(escapeEcmaScript(key), escapeEcmaScript(msg.replace("''", "'")))
      }).mkString(",")
    ))
  }

  private def allMessages(implicit app: Application, lang: Lang) =
    Messages.messages.get("default").getOrElse(Map.empty) ++ Messages.messages.get(lang.code).getOrElse(Map.empty)
}
