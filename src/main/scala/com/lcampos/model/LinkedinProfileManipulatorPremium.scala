package com.lcampos.model

import cats.syntax.all._
import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import com.lcampos.duration_per_tech.ExperienceItem
import com.lcampos.util.ElementUtil
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.{Document, Element}

import scala.scalajs.js.Object.entries

object LinkedinProfileManipulatorPremium extends LinkedinProfileManipulator {
  val urlSignature: String = "www.linkedin.com/recruiter/profile/"
  protected def getExperienceSection(document: Document): Either[String, Element] =
    ElementUtil.getElementByIdSafe(document, "profile-experience")

  def getExperienceItems(experienceSectionElem: Element): Either[String, List[ExperienceItem]] =
    getAllPositionElements(experienceSectionElem)
      .map(getExperienceItem)
      .sequence

  private def getExperienceItem(positionElem: Element): Either[String, ExperienceItem] = for {
    title <- ElementUtil.getFirstElementByTagNameSafe(positionElem, "h4").map(_.textContent.trim)
    duration <- ElementUtil.getFirstElementByClassNameSafe(positionElem, "duration").map(_.textContent)
    description = getDescription(positionElem)
  } yield ExperienceItem(title, description, duration)

  private def getAllPositionElements(elem: Element): List[HTMLElement] =
    entries(elem.getElementsByClassName("position"))
      .map(_._2)
      .collect { case li: HTMLElement => li }
      .toList

  private def getDescription(positionElem: Element): String =
    ElementUtil
      .getFirstElementByClassNameSafe(positionElem, "description")
      .map(_.textContent)
      .getOrElse("")
      .trim

  protected def addYearsPerTechElem(durationPerTechPerCat: DurationPerTechPerCategory, document: Document): Either[String, Unit] = ???

  protected def removeTechExperienceSummaryElem(doc: Document): Unit = ()
  protected def showAllExperiences(doc: Document): Unit = ()
}
