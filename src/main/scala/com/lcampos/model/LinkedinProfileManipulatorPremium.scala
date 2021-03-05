package com.lcampos.model

import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import org.scalajs.dom.{Document, Element}

object LinkedinProfileManipulatorPremium extends LinkedinProfileManipulator {
  val urlSignature: String = "www.linkedin.com/recruiter/profile/"
  protected def getExperienceSection(document: Document): Either[String, Element] = ???
  protected def addYearsPerTechElem(durationPerTechPerCat: DurationPerTechPerCategory, document: Document): Either[String, Unit] = ???
  protected def removeTechExperienceSummaryElem(doc: Document): Unit = ???
  protected def showAllExperiences(doc: Document): Unit = ???
}
