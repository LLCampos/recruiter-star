package com.lcampos.duration_per_tech

import com.lcampos.util.ElementUtil
import org.scalajs.dom.Document
import org.scalajs.dom.raw.{Element, NodeList}

object PageManipulator {

  def addDurationPerTechToPage(document: Document): Either[String, Unit] = for {
    experienceSectionElem <- ElementUtil.getElementByIdSafe(document, "experience-section")
    durationPerTech <- DurationPerTechGenerator.getFromLinkedinExperienceSection(experienceSectionElem)
    _ <- addYearsPerTechElem(durationPerTech, document)
  } yield ()

  private def addYearsPerTechElem(durationPerTech: Map[String, String], document: Document): Either[String, Unit] = for {
    mainColumnDiv <- ElementUtil.getElementByIdSafe(document, "main").map(_.firstElementChild)
    profileDetail <- ElementUtil.getFirstElementByClassNameSafe(document.documentElement, "profile-detail")
    aboutSection <- ElementUtil.getNthChildSafe(profileDetail, 2)
    durationPerTechSection: Element = generateYearsPerTechElement(aboutSection, durationPerTech)
    _ = profileDetail.insertBefore(durationPerTechSection, profileDetail.firstElementChild)
  } yield ()

  private def generateYearsPerTechElement(elementToClone: Element, durationPerTech: Map[String, String]): Element = {
    val durationPerTechElem: Element = elementToClone.cloneNode(true).asInstanceOf[Element]

    durationPerTechElem.querySelector("h2").innerText = "Tech Experience Summary"

    val durationPerTechElemP: Element = durationPerTechElem.querySelector("p")

    val spans: NodeList = durationPerTechElemP.querySelectorAll("span")

    // Each of these has different classes
    val spanNormal = spans.item(0).asInstanceOf[Element]
    val spanLast = spans.item(spans.length - 2).asInstanceOf[Element]
    val spanEllipsis = spans.item(spans.length - 1).asInstanceOf[Element]

    durationPerTechElemP.innerHTML = ""

    val durationPerTechTexts = durationPerTech.map { case (tech, years) => s"<b>$tech - </b> $years</br>" }

    durationPerTechTexts.init.foreach { text =>
      val normalSpanClone = spanNormal.cloneNode(true).asInstanceOf[Element]
      normalSpanClone.innerHTML = text
      durationPerTechElemP.appendChild(normalSpanClone)
    }

    spanLast.innerHTML = durationPerTechTexts.last
    durationPerTechElemP.appendChild(spanLast)

    durationPerTechElemP.appendChild(spanEllipsis)

    durationPerTechElem
  }
}
