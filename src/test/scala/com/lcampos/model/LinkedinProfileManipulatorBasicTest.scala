package com.lcampos.model

import com.lcampos.duration_per_tech.ExperienceItem
import com.lcampos.util.ElementUtil
import com.lcampos.util.time.{InstantRange, currentInstantYearMonth, parseDateStrToInstant}
import org.scalajs.dom.Element
import org.specs2.mutable.Specification
import test_data.linkedin_basic.experience_section.{ExampleBasic, ExampleBreakTag, ExampleExperienceRangeOnlyYears, ExampleMultiSectionExperience}
import test_data.linkedin_basic.full_profile._


class LinkedInProfileManipulatorBasicTest extends Specification {

  "LinkedInProfileManipulatorBasic" should {
    "addDurationPerTech" should {
      "return error if no experience section" in {
        val doc = ElementUtil.documentFromString(Example1_NoExperienceSection.example)
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beLeft("Element with id 'experience-section' not found")
      }

      "return error on empty experience section" in {
        val doc = ElementUtil.documentFromString(Example2_EmptyExperienceSection.example)
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beLeft("No <li> elements in the experience section")
      }

      "return error on empty experience items" in {
        val doc = ElementUtil.documentFromString(Example3_EmptyExperienceItems.example)
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beLeft("Element with class 'pv-entity__summary-info-v2' not found")
      }

      "return error on empty summary" in {
        val doc = ElementUtil.documentFromString(Example4_EmptySummary.example)
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beLeft("Element for selector 'h3' not found")
      }

      "add tech experience section to valid document" in {
        val doc = ElementUtil.documentFromString(Example6_ValidPage.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight((elem: Element) => {
          elem.textContent.contains("Python -  3 years and 3 months") &&
          elem.textContent.contains("Programming Languages")
        })
      }

      "if page already has a tech experience summary section, replace it" in {
        val doc = ElementUtil.documentFromString(Example7_PageAlreadyWithTechExperienceSummary.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight((elem: Element) => {
          elem.textContent.contains("Python -  3 years and 3 months")
        })
        doc.documentElement.textContent.contains("4 years and 4 months") must beFalse

        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight((elem: Element) => {
          elem.textContent.contains("Java -  4 years and 4 months")
        })
        doc.documentElement.textContent.contains("3 years and 3 months") must beFalse
      }

      "if page with no reference to technologies has a tech experience summary section from previous profile, remove it" in {
        val doc = ElementUtil.documentFromString(Example9_PageAlreadyWithTechExperienceSummaryAndNoTech.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
      }

      "if experience section doesn't reference any technologies, don't add tech experience summary" in {
        val doc = ElementUtil.documentFromString(Example8_NoReferenceToTechnologies.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
        LinkedInProfileManipulatorBasic(doc).addDurationPerTech() must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
      }
    }

    "getExperienceItems" should {
      "correctly parse example 1" in {
        val elem = ElementUtil.elementFromString(ExampleBasic.example)

        val expected = Seq(
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

        LinkedInProfileManipulatorBasic(elem.ownerDocument).getExperienceItems(elem) must beRight(expected)
      }

      "deal correctly with break tags" in {
        val elem = ElementUtil.elementFromString(ExampleBreakTag.example)

        val expected = List(
          ExperienceItem(
            "Software Developer @ DXS powered by agap2i",
            "Frontend and Backend developer:  JavaScript Python",
            InstantRange(
              parseDateStrToInstant("2013-11-01"),
              currentInstantYearMonth
            )
          )
        )

        LinkedInProfileManipulatorBasic(elem.ownerDocument).getExperienceItems(elem) must beRight(expected)
      }

      "deal with multi-sections experience items" in {
        val elem = ElementUtil.elementFromString(ExampleMultiSectionExperience.example)
        LinkedInProfileManipulatorBasic(elem.ownerDocument).getExperienceItems(elem) must beRight((experienceItems: List[ExperienceItem]) => {
          experienceItems.size must be equalTo 3
        })
      }

      "deal with experience duration range only having years, not months" in {
        val elem = ElementUtil.elementFromString(ExampleExperienceRangeOnlyYears.example)

        val expected = Seq(
          ExperienceItem(
            "Software Developer @ DXS powered by agap2i",
            "Frontend and Backend developer: JavaScript",
            InstantRange(
              parseDateStrToInstant("2013-01-01"),
              parseDateStrToInstant("2015-01-01"),
            )
          ),
        )

        LinkedInProfileManipulatorBasic(elem.ownerDocument).getExperienceItems(elem) must beRight(expected)
      }
    }
  }
}
