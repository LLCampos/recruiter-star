package com.lcampos.duration_per_tech

import cats.syntax.all._
import com.lcampos.util.ElementUtil
import org.scalajs.dom.raw.{Element, HTMLLIElement}

import scala.concurrent.duration.{DAYS, Duration}
import scala.scalajs.js.Object.entries

case class ExperienceItem(
  title: String,
  description: String,
  durationDescription: String
) {
  def duration: Option[Duration] = {
    val (yearsOpt, monthsOpt) = durationDescription match {
      case s"$years yr $months mos" => (Some(years), Some(months))
      case s"$years yrs $months mos" => (Some(years), Some(months))
      case s"$years yrs" => (Some(years), None)
      case s"$years yr" => (Some(years), None)
      case s"$months mos" => (None, Some(months))
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

  def technologies: Set[Tech] = {
    val tokens = s"$title $description"
      .split("(\\s|,|\\.|!|:|/|;)+")
      .map(_.toLowerCase)
    TechList.all
      .filter(tech => tokens.contains(tech.name))
      .toSet
  }
}

object ExperienceItem {
  def fromLinkedinExperienceSectionElem(elem: Element): Either[String, List[ExperienceItem]] = {
    val liElems = entries(elem.querySelectorAll("li"))
    if (liElems.isEmpty) {
      Left("No <li> elements in the experience section")
    } else {
      liElems.map(tuple => tuple._2 match {
        case el: HTMLLIElement => fromExperienceListItem(el)
      }).toList.sequence
    }
  }

  private def fromExperienceListItem(elem: HTMLLIElement): Either[String, ExperienceItem] = for {
    summary <- ElementUtil.getFirstElementByClassNameSafe(elem, "pv-entity__summary-info")
    title <- ElementUtil.querySelectorSafe(summary, "h3").map(_.textContent)
    descriptionElem = Option(elem.getElementsByClassName("pv-entity__description").item(0))
    description = descriptionElem.map(_.textContent.trim()).getOrElse("")
    employmentDuration <- ElementUtil.getFirstElementByClassNameSafe(elem, "pv-entity__bullet-item-v2").map(_.textContent)
  } yield ExperienceItem(
      title,
      description,
      employmentDuration
    )
}
