import com.lcampos.{ExperienceItem, LinkedinYearsPerTech}
import org.scalajs.dom._
import org.specs2.mutable.Specification
import test_data.Example1

import scala.concurrent.duration._

class LinkedinYearsPerTechTest extends Specification {

  val domParser = new DOMParser()

  "LinkedinYearsPerTechTest" should {
    "getFromLinkedinExperienceSection" should {
      "correctly parse base example" in {
        val elem = elementFromString(Example1.example)
        LinkedinYearsPerTech.getFromLinkedinExperienceSection(elem) must be equalTo Map(
          "JavaScript" -> "11 yrs and 4 mos",
          "CSS" -> "11 yrs and 4 mos",
          "HTML5" -> "11 yrs and 4 mos",
          "Azure" -> "7 yrs and 4 mos",
        )
      }
    }
  }

  "ExperienceItem" should {
    "fromLinkedinExperienceSectionElem" should {
      "correctly parse example 1" in {
        val elem = elementFromString(Example1.example)

        val expected = Seq(
          ExperienceItem(
            "Software Developer @ DXS powered by agap2i",
            "Frontend and Backend developer: JavaScript, CSS, HTML5, Azure.",
            "7 yrs 4 mos"
          ),
          ExperienceItem(
            "Analyst/Software Developer",
            "Frontend and Backend developer.Worked with JavaScript, CSS and HTML5.",
            "4 yrs"
          ),
        )

        ExperienceItem.fromLinkedinExperienceSectionElem(elem) must be equalTo expected
      }
    }

    "duration" should {
      "return correct duration when duration string only contains number of years" in {
        ExperienceItem("", "", "4 yrs").duration must be some Duration(4 * 365, DAYS)
      }

      "return correct duration when duration string contains years and months" in {
        ExperienceItem("", "", "7 yrs 4 mos").duration must be some Duration(7 * 365 + 4 * 30, DAYS)
      }

      "return correct duration when duration string contains years and months with more than one digit" in {
        ExperienceItem("", "", "11 yrs 10 mos").duration must be some Duration(11 * 365 + 10 * 30, DAYS)
      }

      "return None if duration string doesn't have the expected format" in {
        ExperienceItem("", "", "11 years 10 mos").duration must beNone
      }
    }

    "technologies" should {
      "return empty if no technology in experience item" in {
        ExperienceItem("Developer", "I did cool stuff!", "").technologies must be equalTo Set()
      }

      "return technologies in title" in {
        ExperienceItem("Java Developer", "I did cool stuff!", "").technologies must be equalTo Set("Java")
      }

      "return technologies in description" in {
        ExperienceItem("Developer", "I did cool stuff using CSS and JavaScript", "").technologies must be equalTo Set("CSS", "JavaScript")
      }

      "return technologies in description and title" in {
        ExperienceItem("Java Developer", "I did cool stuff using CSS and JavaScript", "").technologies must be equalTo Set("Java", "CSS", "JavaScript")
      }

      "return technologies even if followed by a punctuation character" in {
        ExperienceItem("", "I did cool stuff using JavaScript! And CSS, Java.", "").technologies must be equalTo Set("JavaScript", "CSS", "Java")
      }

      "be case insensitive" in {
        ExperienceItem("JaVa Developer", "I did cool stuff using css and Javascript", "").technologies must be equalTo Set("Java", "CSS", "JavaScript")
      }
    }
  }

  def elementFromString(s: String): Element =
    domParser.parseFromString(s, "text/html").documentElement
}
