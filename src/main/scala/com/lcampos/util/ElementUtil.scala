package com.lcampos.util

import org.scalajs.dom.{DOMParser, Document, Element}

object ElementUtil {

  val domParser = new DOMParser()

  def documentFromString(s: String): Document =
    domParser.parseFromString(s, "text/html")

  def elementFromString(s: String): Element =
    documentFromString(s).documentElement

  def getElementByIdSafe(doc: Document, id: String): Either[String, Element] = {
    doc.getElementById(id) match {
      case null => Left(s"Element with id '$id' not found")
      case elem => Right(elem)
    }
  }

  def getFirstElementByClassNameSafe(elem: Element, className: String): Either[String, Element] = {
    elem.getElementsByClassName(className).item(0) match {
      case null => Left(s"Element with class '$className' not found")
      case elem => Right(elem)
    }
  }

  def querySelectorSafe(elem: Element, selectors: String): Either[String, Element] = {
    elem.querySelector(selectors) match {
      case null => Left(s"Element for selector '$selectors' not found")
      case elem => Right(elem)
    }
  }
}
