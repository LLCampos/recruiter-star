package com.lcampos.model

import cats.syntax.all._
import com.lcampos.duration_per_tech.DurationPerTechGenerator.DurationPerTechPerCategory
import com.lcampos.duration_per_tech.ExperienceItem
import com.lcampos.model.LinkedinProfileManipulator.{TechExperienceSummaryContentId, TechExperienceSummaryId}
import com.lcampos.util.ElementUtil
import com.lcampos.util.time.{InstantRange, toInstantRange}
import org.scalajs.dom.raw.{HTMLElement, HTMLLIElement}
import org.scalajs.dom.{Document, Element, window}

import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt
import scala.scalajs.js.timers.setTimeout

object LinkedinProfileManipulatorBasic extends LinkedinProfileManipulator {

  val ExperienceDescriptionClass = "pv-entity__description"
  val PeopleAlsoViewedTitleClass = "pv-browsemap-section__member-headline"

  val urlSignature: String = "www.linkedin.com/in/"

  protected def getExperienceSection(document: Document): Either[String, Element] =
    ElementUtil.getElementByIdSafeCloned(document, "experience-section")

  protected def addYearsPerTechElem(durationPerTechPerCat: DurationPerTechPerCategory, document: Document): Either[String, Unit] = for {
    profileDetail <- ElementUtil.getFirstElementByClassNameSafe(document.documentElement, "profile-detail")
    durationPerTechSection <- generateYearsPerTechElement(durationPerTechPerCat)
    _ = profileDetail.insertBefore(durationPerTechSection, profileDetail.firstElementChild)
  } yield ()

  private def generateYearsPerTechElement(durationPerTechPerCat: DurationPerTechPerCategory): Either[String, Element] = {
    val durationPerTechElem: Element = durationPerTechElemTemplate

    for {
      durationPerTechElemP <- ElementUtil.querySelectorSafe(durationPerTechElem, "p")
      _ = durationPerTechPerCat.map { case (category, durationPerTech) =>
        val categoryText = s"</br><h5><b>$category:<b></h5></br>"
        addTextToElemAsSpan(categoryText, durationPerTechElemP)
        durationPerTech
          .map { case (tech, years) => s"<b>$tech - </b> $years</br>" }
          .foreach(text => addTextToElemAsSpan(text, durationPerTechElemP))
      }
    } yield durationPerTechElem
  }

  private def addTextToElemAsSpan(text: String, elem: Element) = {
    val span = durationPerTechSpanTemplate
    span.innerHTML = text
    elem.appendChild(span)
  }

  protected def removeTechExperienceSummaryElem(doc: Document): Unit =
    ElementUtil.getElementByIdSafe(doc, TechExperienceSummaryId).map(el => el.parentNode.removeChild(el))

  private def durationPerTechElemTemplate: Element = ElementUtil.elementFromString(
    s"""
      <div class="pv-oc ember-view" id="$TechExperienceSummaryId">
          <section class="pv-profile-section pv-about-section artdeco-card p5 mt4 ember-view"><header class="pv-profile-section__card-header">
              <h2 class="pv-profile-section__card-heading">Tech Experience Summary</h2>
              <!----></header>
              <p class="pv-about__summary-text mt4 t-14 ember-view" id="$TechExperienceSummaryContentId">
              </p>
          </section>
      </div>
      """)

  private def durationPerTechSpanTemplate: Element = ElementUtil.elementFromString("""<span class="lt-line-clamp__line"></span>""")

  protected def showAllExperiences(doc: Document): Unit = {
    ElementUtil.getFirstElementByClassNameSafe(doc.documentElement, "pv-profile-section__see-more-inline").map(elem => {
      elem.asInstanceOf[HTMLElement].click()
      setTimeout(1.milli) {
        window.scroll(0, 0)
      }
    })
  }

  def getExperienceItems(experienceSectionElem: Element): Either[String, List[ExperienceItem]] = {
    val liElems = ElementUtil.getAllLiElements(experienceSectionElem)
    if (liElems.isEmpty) {
      Left("No <li> elements in the experience section")
    } else {
      liElems
        .filterNot(hasSubList)
        .map(fromExperienceListItem)
        .sequence
    }
  }

  private def hasSubList(elem: HTMLLIElement): Boolean =
    ElementUtil.querySelectorSafe(elem, "ul").isRight

  private def fromExperienceListItem(elem: HTMLLIElement): Either[String, ExperienceItem] =
    for {
      summary <- ElementUtil.getFirstElementByClassNameSafe(elem, "pv-entity__summary-info").orElse(
        ElementUtil.getFirstElementByClassNameSafe(elem, "pv-entity__summary-info-v2")
      )
      title <- ElementUtil.querySelectorSafe(summary, "h3").map(_.textContent)
      description = getDescription(elem)
      instantRange <- getEmploymentTsRange(elem)
    } yield ExperienceItem(
      title,
      description,
      instantRange
    )

  private def getEmploymentTsRange(elem: HTMLLIElement): Either[String, InstantRange] = for {
    dateRangeElem <- ElementUtil.getFirstElementByClassNameSafe(elem, "pv-entity__date-range")
    dateRangeStr <- ElementUtil.getNthChildSafe(dateRangeElem, 1).map(_.textContent)
    formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM uuuu")
  } yield toInstantRange(dateRangeStr, " – ", formatter)

  private def getDescription(elem: HTMLLIElement): String =
    ElementUtil
      .getFirstElementByClassNameSafe(elem, ExperienceDescriptionClass)
      .map(ElementUtil.removeBreakTags)
      .map(_.textContent.trim()).getOrElse("")

  def expandEachExperience(doc: Document): Unit =
    ElementUtil.getElementsByClassName[HTMLElement](doc.documentElement, "inline-show-more-text__button").foreach(
      _.click()
    )

  def removeSeeLessFromEachExperienceSection(doc: Document): Unit = {
    // Note: This remove from whole page. If this is problematic, fix it.
    ElementUtil.getElementsByClassName[HTMLElement](doc.documentElement, "inline-show-more-text__link-container-expanded").foreach(elem =>
      elem.parentElement.removeChild(elem)
    )
  }
}
