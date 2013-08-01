package com.blitz.idm.app

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable

/**
 *
 */
object SubstitutionResolver {

  implicit def resolverCanBuildFrom = new ReplacerCanBuildFrom

  def resolve(toResolve: String) = new StringBuilder(toResolve).scanLeft(new Resolver)(_ += _)

}

private[app] class ReplacerCanBuildFrom extends CanBuildFrom[StringBuilder, Resolver, String] {

  def apply(from: StringBuilder): mutable.Builder[Resolver, String] = apply()

  def apply(): mutable.Builder[Resolver, String] = new mutable.Builder[Resolver, String] {

    val repl = new StringBuilder("")

    def +=(elem: Resolver): this.type = elem.map(e => {
      repl.append(e)
      this
    })

    def clear() {repl.clear()}

    def result(): String = repl.mkString

  }

}

private[app] class Resolver {

  val acc = new StringBuilder("")
  var open = true
  var subst = false
  var prevChar: Char = _

  @inline final def map[B](f: String => B): B = {
    if (open) {
      {(str: String) => {
        val res = f(if(subst)appConfiguration.lookupConfig(str).getOrElse("${" + str + "}") else str)
        acc.clear()
        subst = false
        res
      }}.apply(acc.mkString)
    }
    else f("")
  }

  final def += (ch: Char): this.type = {
    if(!open) {
      if(ch == '}') {
        open = true
        subst = true
      }
      else
        acc += ch
    }
    else {
      if(prevChar == '$'){
        if(ch == '{'){
          open = false
        }
        else {
          acc += prevChar
          if(ch != '$')
            acc += ch
        }
      }
      else if(ch != '$')
        acc += ch
    }
    prevChar = ch
    this
  }

}
