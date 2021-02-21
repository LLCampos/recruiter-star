import org.scalajs.dom._
import org.specs2.mutable.Specification
import test_data.Example1

class LinkedinYearsPerTechTest extends Specification {

  val domParser = new DOMParser()

  "LinkedinYearsPerTechTest" should {
    "get" should {
      "correctly parse base example" in {
        val elem = elementFromString(Example1.example)
        LinkedinYearsPerTech.get(elem) must be equalTo Map(
          "Javascript" -> 11,
          "CSS" -> 11,
          "HTML5" -> 11,
          "Azure" -> 7,
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
            "Frontend and Backend developer: Javascript, CSS, HTML, Azure.",
            "7 yrs 4 mos"
          ),
          ExperienceItem(
            "Analyst/Software Developer",
            "Frontend and Backend developer.Worked with Javascript, CSS and HTML.",
            "4 yrs"
          ),
        )

        ExperienceItem.fromLinkedinExperienceSectionElem(elem) must be equalTo expected
      }
    }
  }

  def elementFromString(s: String): Element =
    domParser.parseFromString(s, "text/html").documentElement
}
