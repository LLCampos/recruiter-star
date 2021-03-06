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

  protected def addYearsPerTechElem(durationPerTechPerCat: DurationPerTechPerCategory, document: Document): Either[String, Unit] = for {
    durationPerTechSection <- generateYearsPerTechElement(durationPerTechPerCat)
    primaryContent <- ElementUtil.getElementByIdSafe(document, "primary-content")
    _ = primaryContent.insertBefore(durationPerTechSection, primaryContent.children.item(2))
  } yield ()

  private def generateYearsPerTechElement(durationPerTechPerCat: DurationPerTechPerCategory): Either[String, Element] = {
    val durationPerTechElem: Element = durationPerTechElemTemplate

    for {
      durationPerTechElemModuleBody <- ElementUtil.getFirstElementByClassNameSafe(durationPerTechElem, "module-body")
      _ = durationPerTechPerCat.map { case (category, durationPerTech) =>
        val categoryText = s"</br><p style='font-size: large; font-weight: bold'>$category:</p></br>"
        addTextToElem(categoryText, durationPerTechElemModuleBody)
        durationPerTech
          .map { case (tech, years) => s"<span style='font-weight: bold'>$tech</span> - $years</br>" }
          .foreach(text => addTextToElem(text, durationPerTechElemModuleBody))
      }
      _ = ElementUtil.appendNewLine(durationPerTechElemModuleBody)
    } yield durationPerTechElem
  }

  private def addTextToElem(text: String, elem: Element) = {
    val p = ElementUtil.elementFromString("<p></p>")
    p.innerHTML = text
    elem.appendChild(p)
  }

  private def durationPerTechElemTemplate: Element = ElementUtil.elementFromString(
    s"""
      <div class="module primary-module module-container" id="$TechExperienceSummaryId">
        <div class="module-header">
          <h2 class="title">Tech Experience Summary</h2>
        </div>
        <div class="module primary-module">
          <div class="module-header"/>
          <div class="module-body searchable"></div>
        </div>
      </div>
      """)

  protected def removeTechExperienceSummaryElem(doc: Document): Unit = ()
  protected def showAllExperiences(doc: Document): Unit = ()
}
