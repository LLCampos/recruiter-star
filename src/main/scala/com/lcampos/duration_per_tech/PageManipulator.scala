package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.scalajs.dom.Document
import org.scalajs.dom.raw.Element

object PageManipulator {

  private val TechExperienceSummaryId = "tech-experience-summary"

  def addDurationPerTechToPage(document: Document): Either[String, Unit] = for {
    experienceSectionElem <- ElementUtil.getElementByIdSafe(document, "experience-section")
    durationPerTech <- DurationPerTechGenerator.getFromLinkedinExperienceSection(experienceSectionElem)
    _ <- if (durationPerTech.nonEmpty) addYearsPerTechElem(durationPerTech, document) else Right(())
  } yield ()

  private def addYearsPerTechElem(durationPerTech: Map[String, String], document: Document): Either[String, Unit] = for {
    profileDetail <- ElementUtil.getFirstElementByClassNameSafe(document.documentElement, "profile-detail")
    durationPerTechSection <- generateYearsPerTechElement(durationPerTech)
    _ = removeTechExperienceSummaryElem(document)
    _ = profileDetail.insertBefore(durationPerTechSection, profileDetail.firstElementChild)
  } yield ()

  private def generateYearsPerTechElement(durationPerTech: Map[String, String]): Either[String, Element] = {
    val durationPerTechElem: Element = durationPerTechElemTemplate

    for {
      durationPerTechElemP <- ElementUtil.querySelectorSafe(durationPerTechElem, "p")
      durationPerTechTexts = durationPerTech.map { case (tech, years) => s"<b>$tech - </b> $years</br>" }
      _ = durationPerTechTexts.foreach { text =>
        val span = durationPerTechSpanTemplate
        span.innerHTML = text
        durationPerTechElemP.appendChild(span)
      }
    } yield durationPerTechElem
  }

  private def removeTechExperienceSummaryElem(doc: Document): Unit =
    ElementUtil.getElementByIdSafe(doc, TechExperienceSummaryId).map(el => el.parentNode.removeChild(el))

  private def durationPerTechElemTemplate: Element = ElementUtil.elementFromString(
    s"""
      <div class="pv-oc ember-view" id="$TechExperienceSummaryId">
          <section class="pv-profile-section pv-about-section artdeco-card p5 mt4 ember-view"><header class="pv-profile-section__card-header">
              <h2 class="pv-profile-section__card-heading">Tech Experience Summary</h2>
              <!----></header>
              <p class="pv-about__summary-text mt4 t-14 ember-view">
              </p>
          </section>
      </div>
      """)

  private def durationPerTechSpanTemplate: Element = ElementUtil.elementFromString("""<span class="lt-line-clamp__line"></span>""")
}
