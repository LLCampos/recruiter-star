package com.lcampos.model

import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import com.lcampos.duration_per_tech.{DurationPerTechGenerator, ExperienceItem, Tech, TechList}
import org.scalajs.dom.{Document, Element}

trait LinkedinProfileManipulator {
  val urlSignature: String
  val TechExperienceSummaryId = "tech-experience-summary"

  def addDurationPerTech(document: Document, baseTechs: List[Tech] = TechList.all): Either[String, Unit] = {
    removeTechExperienceSummaryElem(document)
    showAllExperiences(document)
    for {
      experienceSectionElem <- getExperienceSection(document)
      experienceItems <- getExperienceItems(experienceSectionElem)
      durationPerTechPerCat = DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems, baseTechs)
      _ <- if (durationPerTechPerCat.nonEmpty) addYearsPerTechElem(durationPerTechPerCat, document) else Right(())
    } yield ()
  }

  protected def getExperienceItems(experienceSectionElem: Element): Either[String, List[ExperienceItem]]
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
