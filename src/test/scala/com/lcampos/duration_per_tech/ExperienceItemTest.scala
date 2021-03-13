package com.lcampos.duration_per_tech

import com.lcampos.util.time.InstantRange
import org.specs2.mutable.Specification

import java.time.Instant
import scala.concurrent.duration.{DAYS, Duration}

class ExperienceItemTest extends Specification {

  private val defaultInstantRange = InstantRange(Instant.now, Instant.now)

  "ExperienceItem" should {
    "duration" should {
      "return correct duration when duration string only contains number of years" in {
        ExperienceItem("", "", "4 yrs", defaultInstantRange).duration must be some Duration(4 * 365, DAYS)
      }

      "return correct duration when duration string only contains number of months" in {
        ExperienceItem("", "", "3 mos", defaultInstantRange).duration must be some Duration(3 * 30, DAYS)
      }

      "return correct duration when duration string contains years and months" in {
        ExperienceItem("", "", "7 yrs 4 mos", defaultInstantRange).duration must be some Duration(7 * 365 + 4 * 30, DAYS)
      }

      "return correct duration when duration string contains years and months with more than one digit" in {
        ExperienceItem("", "", "11 yrs 10 mos", defaultInstantRange).duration must be some Duration(11 * 365 + 10 * 30, DAYS)
      }

      "return None if duration string doesn't have the expected format" in {
        ExperienceItem("", "", "11 years 10 mos", defaultInstantRange).duration must beNone
      }

      "return correct duration when single year and x months included in duration string" in {
        ExperienceItem("", "", "1 yr 10 mos", defaultInstantRange).duration must be some Duration(1 * 365 + 10 * 30, DAYS)
      }

      "return correct duration when only a single year included in duration string" in {
        ExperienceItem("", "", "1 yr", defaultInstantRange).duration must be some Duration(1 * 365, DAYS)
      }

      "return correct duration when only a single month included in duration string" in {
        ExperienceItem("", "", "1 mo", defaultInstantRange).duration must be some Duration(1 * 30, DAYS)
      }

      "return correct duration when x years and a single month included in duration string" in {
        ExperienceItem("", "", "2 yrs 1 mo", defaultInstantRange).duration must be some Duration(2 * 365 + 1 * 30, DAYS)
      }

      "return correct duration on single year and single month in duration string" in {
        ExperienceItem("", "", "1 yr 1 mo", defaultInstantRange).duration must be some Duration(1 * 365 + 1 * 30, DAYS)
      }
    }

    "technologies" should {
      "return empty if no technology in experience item" in {
        ExperienceItem("Developer", "I did cool stuff!", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set()
      }

      "return technologies in title" in {
        ExperienceItem("Java Developer", "I did cool stuff!", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("Java")
      }

      "return technologies in description" in {
        ExperienceItem("Developer", "I did cool stuff using CSS and JavaScript", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("CSS", "JavaScript")
      }

      "return technologies in description and title" in {
        ExperienceItem("Java Developer", "I did cool stuff using CSS and JavaScript", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("Java", "CSS", "JavaScript")
      }

      "return technologies even if followed by a punctuation character" in {
        ExperienceItem("", "I did cool stuff using JavaScript! And CSS, Java.", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("JavaScript", "CSS", "Java")
      }

      "extract canon tech name" in {
        ExperienceItem("Developer", "I did cool stuff using HTML5", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("HTML")
      }

      "deal with tech names separated by punctuation" in {
        ExperienceItem("Java/Scala Developer", "JavaScript,CSS", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("Java", "Scala", "JavaScript", "CSS")
      }

      "deal with tech name between square brackets" in {
        ExperienceItem("Developer", "I used a nice language [Groovy]", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("Groovy")
      }

      "deal with tech name between parenthesis" in {
        ExperienceItem("Developer", "I used a nice language (Groovy)", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("Groovy")
      }

      "find technologies even if their name or composed by more than one word" in {
        ExperienceItem("Developer", "I used to work on SQL Server", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set("SQL Server")
      }

      "find technologies with a dot in their name" in {
        ExperienceItem(".NET Developer", "Also worked in Node.js", "", defaultInstantRange).technologies.map(_.name) must be equalTo Set(".NET", "Node.js")
      }
    }
  }
}
