package com.lcampos.model

import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import com.lcampos.duration_per_tech.{DurationPerTechGenerator, ExperienceItem, Tech}
import com.lcampos.linkedin.LinkedinProfileHighlighter
import com.lcampos.model.LinkedinProfileManipulator.TechExperienceSummaryId
import com.lcampos.util.ElementUtil
import org.scalajs.dom.{Document, Element}

trait LinkedinProfileManipulator {
  val document: Document

  val ExperienceDescriptionClass: String
  val PeopleAlsoViewedTitleClass: String

  def highlight(techToHighlight: List[Tech]): Unit =
    LinkedinProfileHighlighter.highlight(techToHighlight, getElementsToHighlight)

  def removePreviousHighlights(): Unit =
    LinkedinProfileHighlighter.removePreviousHighlights(getElementsToHighlight)

  def addDurationPerTech(baseTechs: List[Tech] = Tech.all): Either[String, Unit] = {
    removeTechExperienceSummaryElem()
    showAllExperiences()
    for {
      experienceSectionElem <- getExperienceSection
      experienceItems <- getExperienceItems(experienceSectionElem)
      durationPerTechPerCat = DurationPerTechGenerator.getFromLinkedinExperienceItems(experienceItems, baseTechs)
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
  protected def removeSeeLessFromEachExperienceSection(): Unit

  def expandEachExperienceAndRemoveSeeLessButton(): Unit = {
    expandEachExperience()
    removeSeeLessFromEachExperienceSection()
  }

  def getAllExperienceItemsTitlesSections: List[Element]

  private def getElementsToHighlight: List[Element] =
    ElementUtil.getElementsForClasses(document, List(
      ExperienceDescriptionClass,
      PeopleAlsoViewedTitleClass,
    )) ++ ElementUtil.getElementsForIds(document, List(
      LinkedinProfileManipulator.TechExperienceSummaryContentId
    )) ++ getAllExperienceItemsTitlesSections ::: getProfileSpecificElementsToHighlight

  protected def getProfileSpecificElementsToHighlight: List[Element]
}

object LinkedinProfileManipulator {
  val TechExperienceSummaryId = "tech-experience-summary"
  val TechExperienceSummaryContentId = "tech-experience-summary-content"

  def apply(url: String, document: Document): Option[LinkedinProfileManipulator] =
    if (url.contains(LinkedinProfileManipulatorBasic.UrlSignature))
      Some(LinkedinProfileManipulatorBasic(document))
    else if (url.contains(LinkedinProfileManipulatorPremium.UrlSignature))
      Some(LinkedinProfileManipulatorPremium(document))
    else
      None
}
