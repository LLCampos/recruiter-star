package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.scalajs.dom.Element
import org.specs2.mutable.Specification
import test_data.full_profile._

class PageManipulatorTest extends Specification {

  "PageManipulatorTest" should {
    "addDurationPerTechToPage" should {
      "return error if no experience section" in {
        val doc = ElementUtil.documentFromString(Example1_NoExperienceSection.example)
        PageManipulator.addDurationPerTechToPage(doc) must beLeft("Element with id 'experience-section' not found")
      }

      "return error on empty experience section" in {
        val doc = ElementUtil.documentFromString(Example2_EmptyExperienceSection.example)
        PageManipulator.addDurationPerTechToPage(doc) must beLeft("No <li> elements in the experience section")
      }

      "return error on empty experience items" in {
        val doc = ElementUtil.documentFromString(Example3_EmptyExperienceItems.example)
        PageManipulator.addDurationPerTechToPage(doc) must beLeft("Element with class 'pv-entity__summary-info-v2' not found")
      }

      "return error on empty summary" in {
        val doc = ElementUtil.documentFromString(Example4_EmptySummary.example)
        PageManipulator.addDurationPerTechToPage(doc) must beLeft("Element for selector 'h3' not found")
      }

      "return error on no employment duration" in {
        val doc = ElementUtil.documentFromString(Example5_NoEmploymentDuration.example)
        PageManipulator.addDurationPerTechToPage(doc) must beLeft("Element with class 'pv-entity__bullet-item-v2' not found")
      }

      "add tech experience section to valid document" in {
        val doc = ElementUtil.documentFromString(Example6_ValidPage.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
        PageManipulator.addDurationPerTechToPage(doc) must beRight
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

        PageManipulator.addDurationPerTechToPage(doc) must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight((elem: Element) => {
          elem.textContent.contains("Java -  4 years and 4 months")
        })
        doc.documentElement.textContent.contains("3 years and 3 months") must beFalse
      }

      "if page with no reference to technologies has a tech experience summary section from previous profile, remove it" in {
        val doc = ElementUtil.documentFromString(Example9_PageAlreadyWithTechExperienceSummaryAndNoTech.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beRight
        PageManipulator.addDurationPerTechToPage(doc) must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
      }

      "if experience section doesn't reference any technologies, don't add tech experience summary" in {
        val doc = ElementUtil.documentFromString(Example8_NoReferenceToTechnologies.example)
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
        PageManipulator.addDurationPerTechToPage(doc) must beRight
        ElementUtil.getElementByIdSafe(doc, "tech-experience-summary") must beLeft
      }
    }
  }
}
