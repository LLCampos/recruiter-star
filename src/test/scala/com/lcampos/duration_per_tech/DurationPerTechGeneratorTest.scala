package com.lcampos.duration_per_tech

import com.lcampos.util.time.{InstantRange, currentInstantYearMonth, parseDateStrToInstant}
import org.specs2.mutable.Specification

import scala.collection.immutable.ListMap

class DurationPerTechGeneratorTest extends Specification {


  "DurationPerTechGeneratorTest" should {
    "getFromLinkedinExperienceSection" should {
      "correctly parse example 1" in {
        val experienceItems = List(
          ExperienceItem(
            "Software Developer @ DXS powered by agap2i",
            "Frontend and Backend developer:  JavaScript, CSS, HTML5, Azure.",
            InstantRange(
              parseDateStrToInstant("2013-11-01"),
              currentInstantYearMonth
            )
          ),
          ExperienceItem(
            "Analyst/Software Developer",
            "Frontend and Backend developer. Worked with JavaScript, CSS and HTML5.",
            InstantRange(
              parseDateStrToInstant("2009-12-01"),
              parseDateStrToInstant("2013-11-01"),
            )
          ),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "JavaScript" -> "11 years and 3 months",
            "CSS" -> "11 years and 3 months",
            "HTML" -> "11 years and 3 months",
          ),
          "Cloud" -> ListMap(
            "Azure" -> "7 years and 4 months",
          )
        )
      }

      "correctly parse experience section in which an item description is empty" in {
        val experienceItems = List(
          ExperienceItem(
            "Python Developer",
            "",
            InstantRange(
              parseDateStrToInstant("2009-03-01"),
              parseDateStrToInstant("2009-05-01"),
            )
          ),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "Python" -> "2 months",
          )
        )
      }

      "correctly sum technology experience even when they are represented with different aliases" in {
        val experienceItems = List(
          ExperienceItem(
            "Java",
            "",
            InstantRange(
              parseDateStrToInstant("2009-03-01"),
              parseDateStrToInstant("2011-03-01"),
            )
          ),
          ExperienceItem(
            "JavaEE",
            "",
            InstantRange(
              parseDateStrToInstant("2012-03-01"),
              parseDateStrToInstant("2014-03-01"),
            )),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "Java" -> "4 years",
          )
        )
      }

      "correctly sum overlapping experiences" in {
        val experienceItems = List(
          ExperienceItem(
            "Java",
            "",
            InstantRange(
              parseDateStrToInstant("2009-03-01"),
              parseDateStrToInstant("2011-03-01"),
            )
          ),
          ExperienceItem(
            "Java",
            "",
            InstantRange(
              parseDateStrToInstant("2010-03-01"),
              parseDateStrToInstant("2014-03-01"),
            )),
        )

        DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems) must be equalTo ListMap(
          "Programming Languages" -> ListMap(
            "Java" -> "5 years",
          )
        )
      }
    }
  }
}
