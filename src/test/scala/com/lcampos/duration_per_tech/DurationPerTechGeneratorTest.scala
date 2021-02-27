package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.specs2.mutable.Specification
import test_data.experience_section.{Example1, Example2, Example3_ItemWithEmptyDescription}

class DurationPerTechGeneratorTest extends Specification {


  "LinkedinYearsPerTechTest" should {
    "getFromLinkedinExperienceSection" should {
      "correctly parse example 1" in {
        val elem = ElementUtil.elementFromString(Example1.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "JavaScript" -> "11 years and 4 months",
          "CSS" -> "11 years and 4 months",
          "HTML" -> "11 years and 4 months",
          "Azure" -> "7 years and 4 months",
        ))
      }

      "correctly parse example 2" in {
        val elem = ElementUtil.elementFromString(Example2.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Java" -> "3 years and 4 months",
          "Spring Framework" -> "3 years and 4 months",
          "MongoDB" -> "3 years and 4 months",
          "Git" -> "3 years and 4 months",
          "Docker" -> "3 years and 4 months",
          "AWS" -> "3 years and 4 months",
        ))
      }

      "correctly parse experience section in which an item description is empty" in {
        val elem = ElementUtil.elementFromString(Example3_ItemWithEmptyDescription.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Python" -> "0 years and 2 months",
        ))
      }
    }
  }
}
