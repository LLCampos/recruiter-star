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
  val SkillEndorsementTitleClass: String
  val AboutClass: String

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

  def expandAbout(): Unit

  def getAllExperienceItemsTitlesSections: List[Element]

  private def getElementsToHighlight: List[Element] = {
    getElementsToHighlightByClass(document, List(
      ExperienceDescriptionClass,
      PeopleAlsoViewedTitleClass,
      SkillEndorsementTitleClass,
    )) ++ getElementsToHighlightByIds(document, List(
      LinkedinProfileManipulator.TechExperienceSummaryContentId
    )) ++ getAllExperienceItemsTitlesSections
  }

  private def getElementsToHighlightByClass(doc: Document, classes: List[String]): List[Element] =
    classes.flatMap(className => ElementUtil.getElementsByClassName(doc.documentElement, className))

  private def getElementsToHighlightByIds(doc: Document, ids: List[String]): List[Element] =
    ids.flatMap(id => ElementUtil.getElementByIdSafe(doc, id).toOption)
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
