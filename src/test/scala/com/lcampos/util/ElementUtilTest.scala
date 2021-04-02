package com.lcampos.util

import org.specs2.mutable.Specification
import com.lcampos.util.ElementUtil._
import org.scalajs.dom.Element
import org.scalajs.dom.raw.HTMLSelectElement

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

      // TODO: Why is this returning Right??
//      "return left if element can't be converted" in {
//        val doc = documentFromString("<main><select id=\"elem1\"></select></main>")
//        getElementByIdSafeAs[Int](doc, "elem1") must beLeft
//      }
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
  }
}
