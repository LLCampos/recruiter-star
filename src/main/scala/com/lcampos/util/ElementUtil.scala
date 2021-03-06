package com.lcampos.util

import org.scalajs.dom.raw.HTMLLIElement
import org.scalajs.dom.{DOMParser, Document, Element, document}

import scala.scalajs.js.Object.entries

object ElementUtil {

  val domParser = new DOMParser()

  def documentFromString(s: String): Document =
    domParser.parseFromString(s, "text/html")

  def elementFromString(s: String): Element = {
    val div = document.createElement("div")
    div.innerHTML = s
    div
  }

  def getElementByIdSafe(doc: Document, id: String): Either[String, Element] =
    doc.getElementById(id) match {
      case null => Left(s"Element with id '$id' not found")
      case elem => Right(elem)
    }

  def getElementByIdSafeCloned(doc: Document, id: String): Either[String, Element] =
    getElementByIdSafe(doc, id).map(_.cloneNode(true).asInstanceOf[Element])

  def getFirstElementByClassNameSafe(elem: Element, className: String): Either[String, Element] = {
    elem.getElementsByClassName(className).item(0) match {
      case null => Left(s"Element with class '$className' not found")
      case elem => Right(elem)
    }
  }

  def getFirstElementByTagNameSafe(elem: Element, tagName: String): Either[String, Element] = {
    elem.getElementsByTagName(tagName).item(0) match {
      case null => Left(s"Element with tag '$tagName' not found")
      case elem => Right(elem)
    }
  }

  def querySelectorSafe(elem: Element, selectors: String): Either[String, Element] = {
    elem.querySelector(selectors) match {
      case null => Left(s"Element for selector '$selectors' not found")
      case elem => Right(elem)
    }
  }

  def getNthChildSafe(elem: Element, index: Int): Either[String, Element] = {
    elem.children.item(index) match {
      case null => Left(s"Element $elem doesn't have a child in position $index")
      case elem => Right(elem)
    }
  }

  def getAllLiElements(elem: Element): List[HTMLLIElement] =
    entries(elem.querySelectorAll("li"))
      .map(_._2)
      .collect { case li: HTMLLIElement => li }
      .toList

  def appendNewLine(elem: Element): Unit =
    elem.innerHTML = elem.innerHTML + "</br>"
}
