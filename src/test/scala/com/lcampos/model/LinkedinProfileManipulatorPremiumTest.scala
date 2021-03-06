package com.lcampos.model

import com.lcampos.duration_per_tech.ExperienceItem
import com.lcampos.util.ElementUtil
import org.specs2.mutable.Specification
import test_data.linkedin_premium.experience_section.ExampleBasic

class LinkedinProfileManipulatorPremiumTest extends Specification {

  "LinkedinProfileManipulatorPremium" should {
    "getExperienceItems" in {
      "correctly parse basic example" in {
        val elem = ElementUtil.elementFromString(ExampleBasic.example)

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
    }
  }
}
