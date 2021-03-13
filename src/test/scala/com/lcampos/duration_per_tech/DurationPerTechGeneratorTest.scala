package com.lcampos.duration_per_tech

import com.lcampos.util.time.{TsTzRange, currentInstantYearMonth, parseDateStrToInstant}
import org.specs2.mutable.Specification

import java.time.Instant
import scala.collection.immutable.ListMap

class DurationPerTechGeneratorTest extends Specification {


  "DurationPerTechGeneratorTest" should {
    "getFromLinkedinExperienceSection" should {
      "correctly parse example 1" in {
        val experienceItems = List(
          ExperienceItem(
            "Software Developer @ DXS powered by agap2i",
            "Frontend and Backend developer:  JavaScript, CSS, HTML5, Azure.",
            "7 yrs 4 mos",
            TsTzRange(
              parseDateStrToInstant("2013-11-01"),
              currentInstantYearMonth
            )
          ),
          ExperienceItem(
            "Analyst/Software Developer",
            "Frontend and Backend developer. Worked with JavaScript, CSS and HTML5.",
            "4 yrs",
            TsTzRange(
              parseDateStrToInstant("2009-12-01"),
              parseDateStrToInstant("2013-11-01"),
            )
          ),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "JavaScript" -> "11 years and 4 months",
            "CSS" -> "11 years and 4 months",
            "HTML" -> "11 years and 4 months",
          ),
          "Cloud" -> ListMap(
            "Azure" -> "7 years and 4 months",
          )
        )
      }

      "correctly parse experience section in which an item description is empty" in {
        val experienceItems = List(
          ExperienceItem("Python Developer", "", "2 mos", TsTzRange(Instant.now(), Instant.now())),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "Python" -> "2 months",
          )
        )
      }

      "correctly format duration when only years and only months" in {
        val experienceItems = List(
          ExperienceItem("JavaScript", "", "4 yrs", TsTzRange(Instant.now(), Instant.now())),
          ExperienceItem("Azure", "", "4 mos", TsTzRange(Instant.now(), Instant.now())),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "JavaScript" -> "4 years",
          ),
          "Cloud" -> ListMap(
            "Azure" -> "4 months",
          )
        )
      }

      "correctly sum technology experience even when they are represented with different aliases" in {
        val experienceItems = List(
          ExperienceItem("Java", "", "2 yrs", TsTzRange(Instant.now(), Instant.now())),
          ExperienceItem("JavaEE", "", "2 yrs", TsTzRange(Instant.now(), Instant.now())),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "Java" -> "4 years",
          )
        )
      }
    }
  }
}
