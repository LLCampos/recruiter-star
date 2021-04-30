package com.lcampos.model

import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import com.lcampos.duration_per_tech.{DurationPerTechGenerator, ExperienceItem, Tech}
import com.lcampos.linkedin.LinkedInProfileHighlighter
import com.lcampos.model.LinkedInProfileManipulator.TechExperienceSummaryId
import com.lcampos.util.ElementUtil
import org.scalajs.dom.{Document, Element}
import scalajs.js.timers.setTimeout

trait LinkedInProfileManipulator {
  val document: Document

  val ExperienceDescriptionClass: String

  def highlight(techToHighlight: List[Tech]): Unit =
    LinkedInProfileHighlighter.highlight(techToHighlight, getElementsToHighlight)

  def removePreviousHighlights(): Unit =
    LinkedInProfileHighlighter.removePreviousHighlights(getElementsToHighlight)

  def addDurationPerTech(baseTechs: List[Tech] = Tech.all): Either[String, Unit] = {
    removeTechExperienceSummaryElem()
    showAllExperiences()
    setTimeout(1000) {
      expandEachExperienceAndCleanUp()
    }
    for {
      experienceSectionElem <- getExperienceSection
      experienceItems <- getExperienceItems(experienceSectionElem)
      durationPerTechPerCat = DurationPerTechGenerator.getFromLinkedInExperienceItems(experienceItems, baseTechs)
      _ <- if (durationPerTechPerCat.nonEmpty) addYearsPerTechElem(durationPerTechPerCat) else Right(())
    } yield ()
  }

  protected def getExperienceItems(experienceSectionElem: Element): Either[String, List[ExperienceItem]]
  protected def getExperienceSection: Either[String, Element]
  protected def addYearsPerTechElem(durationPerTechPerCat: DurationPerTechPerCategory): Either[String, Unit]

  def removeTechExperienceSummaryElem(): Unit =
    ElementUtil.getElementByIdSafe(document, TechExperienceSummaryId).map(el => el.parentNode.removeChild(el))

  // Expand the whole Experience section
  protected def showAllExperiences(): Unit

  protected def expandEachExperience(): Unit
  protected def cleanUpAfterExpandingEachExperience(): Unit

  def expandEachExperienceAndCleanUp(): Unit = {
    expandEachExperience()
    setTimeout(1000) {
      cleanUpAfterExpandingEachExperience()
    }
  }

  def getAllExperienceItemsTitlesSections: List[Element]

  private def getElementsToHighlight: List[Element] =
    ElementUtil.getElementsForClasses(document, List(
      ExperienceDescriptionClass,
    )) ++ ElementUtil.getElementsForIds(document, List(
      LinkedInProfileManipulator.TechExperienceSummaryContentId
    )) ++ getAllExperienceItemsTitlesSections ++ getProfileSpecificElementsToHighlight

  protected def getProfileSpecificElementsToHighlight: List[Element]
}

object LinkedInProfileManipulator {
  val TechExperienceSummaryId = "tech-experience-summary"
  val TechExperienceSummaryContentId = "tech-experience-summary-content"

  def apply(url: String, document: Document): Option[LinkedInProfileManipulator] =
    if (url.contains(LinkedInProfileManipulatorBasic.UrlSignature))
      Some(LinkedInProfileManipulatorBasic(document))
    else if (url.contains(LinkedInProfileManipulatorPremium.UrlSignature))
      Some(LinkedInProfileManipulatorPremium(document))
    else
      None
}
