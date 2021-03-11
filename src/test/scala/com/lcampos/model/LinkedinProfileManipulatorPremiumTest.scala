package com.lcampos.model

import com.lcampos.duration_per_tech.ExperienceItem
import com.lcampos.util.ElementUtil
import org.scalajs.dom.Element
import org.specs2.mutable.Specification
import test_data.linkedin_premium.experience_section
import test_data.linkedin_premium.full_profile

class LinkedinProfileManipulatorPremiumTest extends Specification {

  "LinkedinProfileManipulatorPremium" should {
    "getExperienceItems" should {
      "correctly parse basic example" in {
        val elem = ElementUtil.elementFromString(experience_section.ExampleBasic.example)

        val expected = Seq(
          ExperienceItem(
            "Java Backend Developer",
            "",
            "(1 year 11 months)"
          ),
          ExperienceItem(
            "Consultor de TI",
            "By Bold International",
            "(2 years 1 month)"
          ),
          ExperienceItem(
            "Java Programmer (Outsourcing na Everis Portugal)",
            "-Outsourcing na Everis Portugal, S.A.",
            "(1 year 1 month)"
          ),
        )

        LinkedinProfileManipulatorPremium.getExperienceItems(elem) must beRight(expected)
      }

      "correctly deal with break tags" in {
        val elem = ElementUtil.elementFromString(experience_section.ExampleBreakTags.example)

        val expected = Seq(
          ExperienceItem(
            "Backend Developer",
            "Python Java JavaScript",
            "(2 years 1 month)"
          )
        )

        LinkedinProfileManipulatorPremium.getExperienceItems(elem) must beRight(expected)
      }

      "correctly deal with no duration section" in {
        val elem = ElementUtil.elementFromString(experience_section.ExampleNoDuration.example)

        val expected = Seq(
          ExperienceItem(
            "Consultor de TI",
            "By Bold International",
            ""
          ),
          ExperienceItem(
            "Java Programmer (Outsourcing na Everis Portugal)",
            "-Outsourcing na Everis Portugal, S.A.",
            "(1 year 1 month)"
          ),
        )

        LinkedinProfileManipulatorPremium.getExperienceItems(elem) must beRight(expected)
      }
    }

    "addDurationPerTech" in {
      "add tech experience section to valid document" in {
        val doc = ElementUtil.documentFromString(full_profile.ExampleBasic.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
        LinkedinProfileManipulatorPremium.addDurationPerTech(doc) must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight((elem: Element) => {
          elem.textContent.contains("Java - 3 years and 7 months") &&
            elem.textContent.contains("Programming Languages")
        })
      }
    }
  }
}
