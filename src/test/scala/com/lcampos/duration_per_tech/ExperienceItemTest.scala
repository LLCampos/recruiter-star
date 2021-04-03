package com.lcampos.duration_per_tech

import com.lcampos.util.time.InstantRange
import org.specs2.mutable.Specification

import java.time.Instant

class ExperienceItemTest extends Specification {

  private val defaultInstantRange = InstantRange(Instant.now, Instant.now)

  "ExperienceItem" should {
    "technologies" should {
      "return empty if no technology in experience item" in {
        ExperienceItem("Developer", "I did cool stuff!", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set()
      }

      "return technologies in title" in {
        ExperienceItem("Java Developer", "I did cool stuff!", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("Java")
      }

      "return technologies in description" in {
        ExperienceItem("Developer", "I did cool stuff using CSS and JavaScript", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("CSS", "JavaScript")
      }

      "only return technologies sent in the base list" in {
        val baseTechs = Tech.all.filter(_.name == "CSS")
        ExperienceItem("Developer", "I did cool stuff using CSS and JavaScript", defaultInstantRange).technologies(baseTechs).map(_.name) must be equalTo Set("CSS")
      }

      "return technologies in description and title" in {
        ExperienceItem("Java Developer", "I did cool stuff using CSS and JavaScript", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("Java", "CSS", "JavaScript")
      }

      "return technologies even if followed by a punctuation character" in {
        ExperienceItem("", "I did cool stuff using JavaScript! And CSS, Java.", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("JavaScript", "CSS", "Java")
      }

      "extract canon tech name" in {
        ExperienceItem("Developer", "I did cool stuff using HTML5", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("HTML")
      }

      "deal with tech names separated by punctuation" in {
        ExperienceItem("Java/Scala Developer", "JavaScript,CSS", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("Java", "Scala", "JavaScript", "CSS")
      }

      "deal with tech name between square brackets" in {
        ExperienceItem("Developer", "I used a nice language [Groovy]", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("Groovy")
      }

      "deal with tech name between parenthesis" in {
        ExperienceItem("Developer", "I used a nice language (Groovy)", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("Groovy")
      }

      "find technologies even if their name or composed by more than one word" in {
        ExperienceItem("Developer", "I used to work on SQL Server", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set("SQL Server")
      }

      "find technologies with a dot in their name" in {
        ExperienceItem(".NET Developer", "Also worked in Node.js", defaultInstantRange).technologies(Tech.all).map(_.name) must be equalTo Set(".NET", "Node.js")
      }
    }
  }
}
