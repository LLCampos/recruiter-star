package com.lcampos.model

import com.lcampos.duration_per_tech.DurationPerTechGenerator
import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import org.scalajs.dom.{Document, Element}

trait LinkedinProfileManipulator {
  val urlSignature: String
  def addDurationPerTech(document: Document): Either[String, Unit] = {
    removeTechExperienceSummaryElem(document)
    showAllExperiences(document)
    for {
      experienceSectionElem <- getExperienceSection(document)
      durationPerTechPerCat <- DurationPerTechGenerator.getFromLinkedinExperienceSection(experienceSectionElem)
      _ <- if (durationPerTechPerCat.nonEmpty) addYearsPerTechElem(durationPerTechPerCat, document) else Right(())
    } yield ()
  }

  protected def getExperienceSection(document: Document): Either[String, Element]
  protected def addYearsPerTechElem(durationPerTechPerCat: DurationPerTechPerCategory, document: Document): Either[String, Unit]
  protected def removeTechExperienceSummaryElem(doc: Document): Unit
  protected def showAllExperiences(doc: Document): Unit
}

object LinkedinProfileManipulator {
  def fromUrl(url: String): Option[LinkedinProfileManipulator] =
    if (url.contains(LinkedinProfileManipulatorBasic.urlSignature))
      Some(LinkedinProfileManipulatorBasic)
    else if (url.contains(LinkedinProfileManipulatorPremium.urlSignature))
      Some(LinkedinProfileManipulatorPremium)
    else
      None
}
