package com.lcampos.util

import org.scalajs.dom.{DOMParser, Element}

object ElementUtil {

  val domParser = new DOMParser()

  def elementFromString(s: String): Element =
    domParser.parseFromString(s, "text/html").documentElement
}
