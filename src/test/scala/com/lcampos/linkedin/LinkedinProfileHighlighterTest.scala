package com.lcampos.linkedin

import com.lcampos.duration_per_tech.Tech
import com.lcampos.duration_per_tech.TechCategory.ProgrammingLanguage
import com.lcampos.util.ElementUtil
import org.specs2.mutable.Specification

class LinkedinProfileHighlighterTest extends Specification {

  "LinkedinProfileHighlighterTest" should {
    "highlight one word" in {
      val elem = ElementUtil.elementFromString("<div>Java</div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedinProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>"
    }

    "remove highlight from one word" in {
      val elem = ElementUtil.elementFromString("<div>Java</div>")
      val innerHtmlBefore = elem.innerHTML
      val techToHighlight = Tech.fromName("Java").get
      LinkedinProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>"
      LinkedinProfileHighlighter.removePreviousHighlights(List(elem))
      elem.innerHTML must be equalTo innerHtmlBefore
    }

    "highlight one word around other words" in {
      val elem = ElementUtil.elementFromString("<div>you know that Java is cool</div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedinProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "you know that <span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span> is cool"
    }

    "highlight one word followed by punctuation" in {
      val elem = ElementUtil.elementFromString("<div>Java!</div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedinProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>!"
    }

    "highlight more than one word" in {
      val elem = ElementUtil.elementFromString("<div>Java and Python</div>")
      val tech1 = Tech.fromName("Java").get
      val tech2 = Tech.fromName("Python").get
      LinkedinProfileHighlighter.highlight(List(tech1, tech2), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span> and <span class=\"highlighted\" style=\"background-color: #E77471\">Python</span>"
    }

    "remove highlight from more than one word" in {
      val elem = ElementUtil.elementFromString("<div>Java and Python</div>")
      val innerHtmlBefore = elem.innerHTML
      val tech1 = Tech.fromName("Java").get
      val tech2 = Tech.fromName("Python").get
      LinkedinProfileHighlighter.highlight(List(tech1, tech2), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span> and <span class=\"highlighted\" style=\"background-color: #E77471\">Python</span>"
      LinkedinProfileHighlighter.removePreviousHighlights(List(elem))
      elem.innerHTML must be equalTo innerHtmlBefore
    }

    "do not highlight word already highlighted" in {
      val elem = ElementUtil.elementFromString("<div><span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span></div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedinProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>"
    }

    "if element text matches more than one alias, only highlight using the longer alias" in {
      val elem = ElementUtil.elementFromString("<div>Play Framework</div>")
      val techToHighlight = Tech("Play", Set("Play", "Play Framework"), ProgrammingLanguage)
      LinkedinProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Play Framework</span>"
    }
  }
}
