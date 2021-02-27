package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.scalajs.dom.Document
import org.scalajs.dom.raw.Element

object PageManipulator {

  def addDurationPerTechToPage(document: Document): Either[String, Unit] = for {
    experienceSectionElem <- ElementUtil.getElementByIdSafe(document, "experience-section")
    durationPerTech <- DurationPerTechGenerator.getFromLinkedinExperienceSection(experienceSectionElem)
    _ <- addYearsPerTechElem(durationPerTech, document)
  } yield ()

  private def addYearsPerTechElem(durationPerTech: Map[String, String], document: Document): Either[String, Unit] = for {
    profileDetail <- ElementUtil.getFirstElementByClassNameSafe(document.documentElement, "profile-detail")
    aboutSection <- ElementUtil.getFirstElementByClassNameSafe(document.documentElement, "pv-about-section")
    durationPerTechSection <- generateYearsPerTechElement(aboutSection, durationPerTech)
    _ = profileDetail.insertBefore(durationPerTechSection, profileDetail.firstElementChild)
  } yield ()

  private def generateYearsPerTechElement(elementToClone: Element, durationPerTech: Map[String, String]): Either[String, Element] = {
    val durationPerTechElem: Element = elementToClone.cloneNode(true).asInstanceOf[Element]

    for {
      h2 <- ElementUtil.querySelectorSafe(durationPerTechElem, "h2")
      _ = h2.innerText = "Tech Experience Summary"

      durationPerTechElemP <- ElementUtil.querySelectorSafe(durationPerTechElem, "p")
      spans = durationPerTechElemP.querySelectorAll("span")

      // Each of these has different classes
      spanNormal = spans.item(0).asInstanceOf[Element]
      spanLast = spans.item(spans.length - 2).asInstanceOf[Element]
      spanEllipsis = spans.item(spans.length - 1).asInstanceOf[Element]

      _ = durationPerTechElemP.innerHTML = ""

      durationPerTechTexts = durationPerTech.map { case (tech, years) => s"<b>$tech - </b> $years</br>" }

      _ = durationPerTechTexts.init.foreach { text =>
        val normalSpanClone = spanNormal.cloneNode(true).asInstanceOf[Element]
        normalSpanClone.innerHTML = text
        durationPerTechElemP.appendChild(normalSpanClone)
      }

      _ = spanLast.innerHTML = durationPerTechTexts.last
      _ = durationPerTechElemP.appendChild(spanLast)
      _= durationPerTechElemP.appendChild(spanEllipsis)
    } yield durationPerTechElem
  }
}
