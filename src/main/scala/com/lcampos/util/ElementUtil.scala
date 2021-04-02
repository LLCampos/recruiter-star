package com.lcampos.util

import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLLIElement, HTMLOptionElement, HTMLSelectElement}

import scala.scalajs.js.Object.entries

object ElementUtil {

  private val domParser = new DOMParser()

  def documentFromString(s: String): Document =
    domParser.parseFromString(s, "text/html")

  def elementFromString(s: String): Element = {
    val div = document.createElement("div")
    div.innerHTML = s
    div.firstElementChild
  }

  def getElementByIdSafe(doc: Document, id: String): Either[String, Element] =
    doc.getElementById(id) match {
      case null => Left(s"Element with id '$id' not found")
      case elem => Right(elem)
    }

  def getElementByIdSafeAs[A](doc: Document, id: String): Either[String, A] =
    getElementByIdSafe(doc, id).flatMap { elem =>
      try {
        Right(elem.asInstanceOf[A])
      } catch {
        case err: Throwable => Left(err.toString)
      }
    }

  def getElementByIdSafeCloned(doc: Document, id: String): Either[String, Element] =
    getElementByIdSafe(doc, id).map(_.cloneNode(true).asInstanceOf[Element])

  def getFirstElementByClassNameSafe(elem: Element, className: String): Either[String, Element] =
    elem.getElementsByClassName(className).item(0) match {
      case null => Left(s"Element with class '$className' not found")
      case elem => Right(elem)
    }

  def getFirstElementByTagNameSafe(elem: Element, tagName: String): Either[String, Element] =
    elem.getElementsByTagName(tagName).item(0) match {
      case null => Left(s"Element with tag '$tagName' not found")
      case elem => Right(elem)
    }

  def querySelectorSafe(elem: Element, selectors: String): Either[String, Element] =
    elem.querySelector(selectors) match {
      case null => Left(s"Element for selector '$selectors' not found")
      case elem => Right(elem)
    }

  def getNthChildSafe(elem: Element, index: Int): Either[String, Element] = {
    elem.children.item(index) match {
      case null => Left(s"Element $elem doesn't have a child in position $index")
      case elem => Right(elem)
    }
  }

  def getAllLiElements(elem: Element): List[HTMLLIElement] =
    toListOf(elem.querySelectorAll("li"))

  def appendNewLine(elem: Element): Unit =
    elem.innerHTML = elem.innerHTML + "</br>"

  def removeBreakTags(elem: Element): Element = {
    val elemCopy = elem.cloneNode(true).asInstanceOf[Element]
    elemCopy.innerHTML = elemCopy.innerHTML.replace("<br>", " ").replace("</br>", " ")
    elemCopy
  }

  def addOption(selectElem: HTMLSelectElement, optionValue: String, selected: Boolean = false): Unit = {
    val option = document.createElement("option").asInstanceOf[HTMLOptionElement]
    option.value = optionValue
    option.text = optionValue
    option.selected = selected
    selectElem.add(option)
  }

  def addOptions(selectElem: HTMLSelectElement, optionsValues: List[String], selectedOptions: List[String] = List.empty): Unit =
    optionsValues.foreach { optionValue =>
      val selected = if (selectedOptions.contains(optionValue)) true else false
      ElementUtil.addOption(selectElem, optionValue, selected)
    }

  def getAllSelected(selectElem: HTMLSelectElement): List[String] =
    toListOf[HTMLOptionElement](selectElem.children)
      .filter(_.selected)
      .map(_.value)

  private def toListOf[A](domList: DOMList[_]): List[A] =
    entries(domList)
      .map(_._2)
      .collect { case a: A => a }
      .toList
    
}
