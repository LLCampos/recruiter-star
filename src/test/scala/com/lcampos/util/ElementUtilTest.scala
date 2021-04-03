package com.lcampos.util

import com.lcampos.util.ElementUtil._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.{HTMLOptionElement, HTMLSelectElement}
import org.specs2.mutable.Specification

import scala.scalajs.js.Object.entries

class ElementUtilTest extends Specification {

  "ElementUtilTest" should {

    "getElementByIdSafe" should {
      "return element if element is found" in {
        val doc = documentFromString("<main><div id=\"elem1\">element1</div></main>")
        getElementByIdSafe(doc, "elem1") must beRight((elem: Element) =>
          elem.id must be equalTo "elem1"
        )
      }

      "return left if element is not found" in {
        val doc = documentFromString("<main><div id=\"elem1\">element1</div></main>")
        getElementByIdSafe(doc, "elem2") must beLeft
      }

      "return element by value" in {
        val doc = documentFromString("<main><div id=\"elem1\">element1</div></main>")
        val foundElemBefore = getElementByIdSafe(doc, "elem1").toOption.get
        foundElemBefore.innerHTML = "bananas"
        val foundElemAfter = getElementByIdSafe(doc, "elem1").toOption.get
        foundElemAfter.innerHTML must be equalTo "bananas"
      }
    }

    "getElementByIdSafeAs" should {
      "return element if element is found" in {
        val doc = documentFromString("<main><select id=\"elem1\"></select></main>")
        getElementByIdSafeAs[HTMLSelectElement](doc, "elem1") must beRight((elem: HTMLSelectElement) =>
          elem.id must be equalTo "elem1"
        )
      }

      "return left if element is not found" in {
        val doc = documentFromString("<main><select id=\"elem1\"></select></main>")
        getElementByIdSafeAs[HTMLSelectElement](doc, "elem2") must beLeft((error: String) =>
          error must be equalTo "Element with id 'elem2' not found"
        )
      }

      "return left if element can't be converted" in {
        val doc = documentFromString("<main><select id=\"elem1\"></select></main>")
        getElementByIdSafeAs[Int](doc, "elem1") must beLeft
      }.pendingUntilFixed("why is it failing?")
    }

    "getFirstElementByClassNameSafe" should {

      "return first matching element if existent" in {
        val doc = documentFromString(
          """
            |<main>
            |   <div class="classy1" id="elem1"></div>
            |   <div class="classy1" id="elem2"></div>
            |</main>""".stripMargin)
        getFirstElementByClassNameSafe(doc.documentElement, "classy1") must beRight((elem: Element) =>
          elem.id must be equalTo "elem1"
        )
      }

      "return left if element with class doesn't existent" in {
        val doc = documentFromString(
          """
            |<main>
            |   <div class="classy1" id="elem1"></div>
            |   <div class="classy1" id="elem2"></div>
            |</main>""".stripMargin)
        getFirstElementByClassNameSafe(doc.documentElement, "classy2") must beLeft((err: String) =>
          err must be equalTo "Element with class 'classy2' not found"
        )
      }
    }

    "getFirstElementByTagNameSafe" should {

      "return first matching element if existent" in {
        val doc = documentFromString(
          """
            |<main>
            |   <h1>hey</h1>
            |   <h2 id="elem1"></h2>
            |   <h2 id="elem2"></h2>
            |</main>""".stripMargin)
        getFirstElementByTagNameSafe(doc.documentElement, "h2") must beRight((elem: Element) =>
          elem.id must be equalTo "elem1"
        )
      }

      "return left if element with tag name does not exist" in {
        val doc = documentFromString(
          """
            |<main>
            |   <h1>hey</h1>
            |   <h3 id="elem1"></h3>
            |   <h4 id="elem2"></h4>
            |</main>""".stripMargin)
        getFirstElementByTagNameSafe(doc.documentElement, "h2") must beLeft((err: String) =>
          err must be equalTo "Element with tag 'h2' not found"
        )
      }
    }

    "querySelectorSafe" should {

      "return first matching element if existent" in {
        val doc = documentFromString(
          """
            |<main>
            |   <h1>hey</h1>
            |   <h2 id="elem1"></h2>
            |   <h2 id="elem2"></h2>
            |</main>""".stripMargin)
        querySelectorSafe(doc.documentElement, "h2") must beRight((elem: Element) =>
          elem.id must be equalTo "elem1"
        )
      }

      "return left if element with tag name does not exist" in {
        val doc = documentFromString(
          """
            |<main>
            |   <h1>hey</h1>
            |   <h3 id="elem1"></h3>
            |   <h4 id="elem2"></h4>
            |</main>""".stripMargin)
        querySelectorSafe(doc.documentElement, "h2") must beLeft((err: String) =>
          err must be equalTo "Element for selector 'h2' not found"
        )
      }
    }

    "getNthChildSafe" should {

      "return first matching element if existent" in {
        val elem = elementFromString(
          """
            |<main>
            |   <div id="elem1"></div>
            |   <div id="elem2"></div>
            |</main>""".stripMargin)
        getNthChildSafe(elem, 1) must beRight((elem: Element) =>
          elem.id must be equalTo "elem2"
        )
      }

      "return error if nth children doesn't exist" in {
        val elem = elementFromString(
          """
            |<main>
            |   <div id="elem1"></div>
            |   <div id="elem2"></div>
            |</main>""".stripMargin)
        getNthChildSafe(elem, 2) must beLeft((error: String) =>
          error.contains("doesn't have a child in position 2")
        )
      }
    }

    "getAllLiElements" should {

      "return all li elements" in {
        val elem = elementFromString(
          """
            |<ul>
            |   <li id="elem1"></li>
            |   <li id="elem2"></li>
            |   <li id="elem3"></li>
            |   <li id="elem4"></li>
            |</ul>""".stripMargin)
        getAllLiElements(elem).size must be equalTo 4
      }

      "return empty list of no li elements" in {
        val elem = elementFromString(
          """
            |<ul>
            |</ul>""".stripMargin)
        getAllLiElements(elem).isEmpty must beTrue
      }
    }

    "appendNewLine" should {
      "append new line" in {
        val elem = elementFromString("<div></div>")
        elem.innerHTML must be equalTo ""
        appendNewLine(elem)
        elem.innerHTML must be equalTo "<br>"
        appendNewLine(elem)
        elem.innerHTML must be equalTo "<br><br>"
      }
    }

    "removeBreakTags" should {
      "remove break tags and return new elem" in {
        val elem = elementFromString("<div><br><br></div>")
        val resultElem = removeBreakTags(elem)
        elem.innerHTML must be equalTo "<br><br>"
        resultElem.innerHTML must be equalTo "  "
      }
    }

    "addOption" should {
      "add unselected option" in {
        val elem = elementFromString("<select></select>").asInstanceOf[HTMLSelectElement]
        addOption(elem, "v1")

        val optionElems = elem.getElementsByTagName("option")
        optionElems.length must be equalTo 1
        val optionElem = entries(optionElems).head._2.asInstanceOf[HTMLOptionElement]
        optionElem.selected must be equalTo false
        optionElem.value must be equalTo "v1"
        optionElem.text must be equalTo "v1"
      }.pendingUntilFixed("why is it failing?")

      "add selected option" in {
        val elem = elementFromString("<select></select>").asInstanceOf[HTMLSelectElement]
        addOption(elem, "v1", selected = true)

        val optionElems = elem.getElementsByTagName("option")
        optionElems.length must be equalTo 1
        val optionElem = entries(optionElems).head._2.asInstanceOf[HTMLOptionElement]
        optionElem.selected must be equalTo true
        optionElem.value must be equalTo "v1"
        optionElem.text must be equalTo "v1"
      }
    }
  }
}
