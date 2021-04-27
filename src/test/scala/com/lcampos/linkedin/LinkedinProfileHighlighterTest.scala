package com.lcampos.linkedin

import com.lcampos.duration_per_tech.Tech
import com.lcampos.duration_per_tech.TechCategory.ProgrammingLanguage
import com.lcampos.util.ElementUtil
import org.specs2.mutable.Specification

class LinkedInProfileHighlighterTest extends Specification {

  "LinkedInProfileHighlighterTest" should {
    "highlight one word" in {
      val elem = ElementUtil.elementFromString("<div>Java</div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>"
    }

    "remove highlight from one word" in {
      val elem = ElementUtil.elementFromString("<div>Java</div>")
      val innerHtmlBefore = elem.innerHTML
      val techToHighlight = Tech.fromName("Java").get
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>"
      LinkedInProfileHighlighter.removePreviousHighlights(List(elem))
      elem.innerHTML must be equalTo innerHtmlBefore
    }

    "highlight one word around other words" in {
      val elem = ElementUtil.elementFromString("<div>you know that Java is cool</div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "you know that <span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span> is cool"
    }

    "highlight one word followed by punctuation" in {
      val elem = ElementUtil.elementFromString("<div>Java!</div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>!"
    }

    "highlight more than one word" in {
      val elem = ElementUtil.elementFromString("<div>Java and Python</div>")
      val tech1 = Tech.fromName("Java").get
      val tech2 = Tech.fromName("Python").get
      LinkedInProfileHighlighter.highlight(List(tech1, tech2), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #E77471\">Java</span> and <span class=\"highlighted\" style=\"background-color: #FFF380\">Python</span>"
    }

    "remove highlight from more than one word" in {
      val elem = ElementUtil.elementFromString("<div>Java and Python</div>")
      val innerHtmlBefore = elem.innerHTML
      val tech1 = Tech.fromName("Java").get
      val tech2 = Tech.fromName("Python").get
      LinkedInProfileHighlighter.highlight(List(tech1, tech2), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #E77471\">Java</span> and <span class=\"highlighted\" style=\"background-color: #FFF380\">Python</span>"
      LinkedInProfileHighlighter.removePreviousHighlights(List(elem))
      elem.innerHTML must be equalTo innerHtmlBefore
    }

    "do not highlight word already highlighted" in {
      val elem = ElementUtil.elementFromString("<div><span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span></div>")
      val techToHighlight = Tech.fromName("Java").get
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Java</span>"
    }

    "if element text matches more than one alias, only highlight using the longer alias (shorter alias in the beginning of longer alias)" in {
      val elem = ElementUtil.elementFromString("<div>Play Framework</div>")
      val techToHighlight = Tech("Play", Set("Play", "Play Framework"), ProgrammingLanguage)
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">Play Framework</span>"
    }

    "if element text matches more than one alias, only highlight using the longer alias (shorter alias in the end of longer alias)" in {
      val elem = ElementUtil.elementFromString("<div>TIBCO StreamBase</div>")
      val techToHighlight = Tech("", Set("StreamBase", "TIBCO StreamBase"), ProgrammingLanguage)
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">TIBCO StreamBase</span>"
    }

    "highlights tech names with non-alphanumeric characters" in {
      val elem = ElementUtil.elementFromString("<div>C++</div>")
      val techToHighlight = Tech("", Set("C++"), ProgrammingLanguage)
      LinkedInProfileHighlighter.highlight(List(techToHighlight), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">C++</span>"
    }

    "highlight technologies with longer names first" in {
      val elem = ElementUtil.elementFromString("<div>C++ and C</div>")
      val techToHighlight1 = Tech("", Set("C"), ProgrammingLanguage)
      val techToHighlight2 = Tech("", Set("C++"), ProgrammingLanguage)
      LinkedInProfileHighlighter.highlight(List(techToHighlight1, techToHighlight2), List(elem))
      elem.innerHTML must be equalTo "<span class=\"highlighted\" style=\"background-color: #FFF380\">C++</span> and <span class=\"highlighted\" style=\"background-color: #E77471\">C</span>"
    }
  }
}
