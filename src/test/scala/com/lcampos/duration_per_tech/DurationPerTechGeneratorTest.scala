package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.specs2.mutable.Specification
import test_data.experience_section._

import scala.collection.immutable.ListMap

class DurationPerTechGeneratorTest extends Specification {


  "LinkedinYearsPerTechTest" should {
    "getFromLinkedinExperienceSection" should {
      "correctly parse example 1" in {
        val elem = ElementUtil.elementFromString(Example1.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Programming Languages" -> Map(
            "JavaScript" -> "11 years and 4 months",
            "CSS" -> "11 years and 4 months",
            "HTML" -> "11 years and 4 months",
          ),
          "Cloud" -> Map(
            "Azure" -> "7 years and 4 months",
          )
        ))
      }

      "correctly parse example 2" in {
        val elem = ElementUtil.elementFromString(Example2.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Programming Languages" -> Map(
            "Java" -> "3 years and 4 months"
          ),
          "Cloud" -> Map(
            "AWS" -> "3 years and 4 months",
          ),
          "Frameworks" -> Map(
            "Spring Framework" -> "3 years and 4 months",
          ),
          "Databases" -> Map(
            "MongoDB" -> "3 years and 4 months",
          ),
          "Other" -> Map(
            "Git" -> "3 years and 4 months",
            "Docker" -> "3 years and 4 months",
          )
        ))
      }

      "correctly parse experience section in which an item description is empty" in {
        val elem = ElementUtil.elementFromString(Example3_ItemWithEmptyDescription.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Programming Languages" -> Map(
            "Python" -> "2 months",
          )
        ))
      }

      "correctly format duration when only years and only months" in {
        val elem = ElementUtil.elementFromString(Example4_OnlyMonthsAndOnlyYears.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Programming Languages" -> Map(
            "JavaScript" -> "4 years",
          ),
          "Cloud" -> Map(
            "Azure" -> "4 months",
          )
        ))
      }

      "existence of break tags shouldn't affect extraction of technologies" in {
        val elem = ElementUtil.elementFromString(Example5_BreakTag.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Programming Languages" -> Map(
            "JavaScript" -> "7 years and 4 months",
            "Python" -> "7 years and 4 months",
          ),
        ))
      }

      "deal with multi-sections experience items" in {
        val elem = ElementUtil.elementFromString(Example6_MultiSectionExperience.example)
        DurationPerTechGenerator.getFromLinkedinExperienceSection(elem) must beRight(Map(
          "Programming Languages" -> ListMap(
            "Java" -> "2 years and 1 months",
            "Scala" -> "2 years and 1 months",
            "Python" -> "7 months",
          ),
        ))
      }
    }
  }
}
