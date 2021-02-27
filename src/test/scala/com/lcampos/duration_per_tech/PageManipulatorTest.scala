package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.specs2.mutable.Specification
import test_data.full_profile.{Example1_NoExperienceSection, Example2_EmptyExperienceSection, Example3_EmptyExperienceItems}

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
        PageManipulator.addDurationPerTechToPage(doc) must beLeft("Element with class 'pv-entity__summary-info' not found")
      }
    }
  }
}
