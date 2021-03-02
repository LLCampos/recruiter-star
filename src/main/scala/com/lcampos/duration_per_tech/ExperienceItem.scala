package com.lcampos.duration_per_tech

import cats.syntax.all._
import com.lcampos.util.ElementUtil
import org.scalajs.dom.raw.{Element, HTMLLIElement}

import scala.concurrent.duration.{DAYS, Duration}

case class ExperienceItem(
  title: String,
  description: String,
  durationDescription: String
) {
  def duration: Option[Duration] = {
    val (yearsOpt, monthsOpt) = durationDescription match {
      case s"$years yr $months mos" => (Some(years), Some(months))
      case s"$years yrs $months mos" => (Some(years), Some(months))
      case s"$years yrs $months mo" => (Some(years), Some(months))
      case s"$years yr $months mo" => (Some(years), Some(months))
      case s"$years yrs" => (Some(years), None)
      case s"$years yr" => (Some(years), None)
      case s"$months mos" => (None, Some(months))
      case s"$months mo" => (None, Some(months))
      case _ => (None, None)
    }

    val yearsStr = yearsOpt.getOrElse("0")
    val monthsStr = monthsOpt.getOrElse("0")

    for {
      years <- yearsStr.toIntOption
      months <- monthsStr.toIntOption
      totalDays = 365 * years.toInt + 30 * months.toInt
    } yield Duration(totalDays, DAYS)
  }

  def technologies: Set[Tech] =
    getOneWordTechnologies ++ getMultiWordTechnologies

  private def getMultiWordTechnologies: Set[Tech] = {
    TechList.all
      .filter(_.name.split(" ").length > 1)
      .filter(tech => allText.contains(tech.name))
      .toSet
  }

  private def getOneWordTechnologies: Set[Tech] = {
    val tokens = allText.split("(\\s|,|\\.|!|:|/|;|\\(|\\))+")
    TechList.all
      .filter(tech => tokens.contains(tech.name))
      .toSet
  }

  private val allText = s"$title $description"
}

object ExperienceItem {
  def fromLinkedinExperienceSectionElem(elem: Element): Either[String, List[ExperienceItem]] = {
    val liElems= ElementUtil.getAllLiElements(elem)
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
    employmentDuration <- ElementUtil.getFirstElementByClassNameSafe(elem, "pv-entity__bullet-item-v2").map(_.textContent)
  } yield ExperienceItem(
      title,
      description,
      employmentDuration
    )

  private def getDescription(elem: HTMLLIElement): String = {
    val descriptionElem = Option(elem.getElementsByClassName("pv-entity__description").item(0))
    descriptionElem.foreach(el =>
      el.innerHTML = el.innerHTML.replace("<br>", " ").replace("</br>", " ")
    )
    descriptionElem.map(_.textContent.trim()).getOrElse("")
  }
}
