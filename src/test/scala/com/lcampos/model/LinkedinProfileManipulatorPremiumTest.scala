package com.lcampos.model

import com.lcampos.duration_per_tech.ExperienceItem
import com.lcampos.util.ElementUtil
import com.lcampos.util.time.{InstantRange, currentInstantYearMonth, parseDateStrToInstant}
import org.scalajs.dom.Element
import org.specs2.mutable.Specification
import test_data.linkedin_premium.experience_section.ExampleExperienceRangeOnlyYears
import test_data.linkedin_premium.{experience_section, full_profile}

class LinkedInProfileManipulatorPremiumTest extends Specification {

  "LinkedInProfileManipulatorPremium" should {
    "getExperienceItems" should {
      "correctly parse basic example" in {
        val elem = ElementUtil.elementFromString(experience_section.ExampleBasic.example)

        val expected = Seq(
          ExperienceItem(
            "Java Backend Developer",
            "",
            InstantRange(
              parseDateStrToInstant("2019-04-01"),
              currentInstantYearMonth
            )
          ),
          ExperienceItem(
            "Consultor de TI",
            "By Bold International",
            InstantRange(
              parseDateStrToInstant("2017-02-01"),
              parseDateStrToInstant("2019-03-01"),
            )
          ),
          ExperienceItem(
            "Java Programmer (Outsourcing na Everis Portugal)",
            "-Outsourcing na Everis Portugal, S.A.",
            InstantRange(
              parseDateStrToInstant("2015-03-01"),
              parseDateStrToInstant("2016-04-01"),
            )
          ),
        )

        LinkedInProfileManipulatorPremium(elem.ownerDocument).getExperienceItems(elem) must beRight(expected)
      }

      "correctly deal with break tags" in {
        val elem = ElementUtil.elementFromString(experience_section.ExampleBreakTags.example)

        val expected = Seq(
          ExperienceItem(
            "Backend Developer",
            "Python Java JavaScript",
            InstantRange(
              parseDateStrToInstant("2017-02-01"),
              parseDateStrToInstant("2019-03-01"),
            )
          )
        )

        LinkedInProfileManipulatorPremium(elem.ownerDocument).getExperienceItems(elem) must beRight(expected)
      }

      "deal with experience duration range only having years, not months" in {
        val elem = ElementUtil.elementFromString(ExampleExperienceRangeOnlyYears.example)

        val expected = Seq(
          ExperienceItem(
            "Java Backend Developer",
            "",
            InstantRange(
              parseDateStrToInstant("2018-01-01"),
              parseDateStrToInstant("2020-01-01"),
            )
          )
        )

        LinkedInProfileManipulatorPremium(elem.ownerDocument).getExperienceItems(elem) must beRight(expected)
      }
    }

    "addDurationPerTech" in {
      "add tech experience section to valid document" in {
        val doc = ElementUtil.documentFromString(full_profile.ExampleBasic.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
        LinkedInProfileManipulatorPremium(doc).addDurationPerTech() must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight((elem: Element) => {
          elem.textContent.contains("Java - 3 years and 7 months") &&
            elem.textContent.contains("Programming Languages")
        })
      }
    }
  }
}
