package com.lcampos.duration_per_tech

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
      case s"$years yrs $months mos" => (Some(years), Some(months))
      case s"$years yrs" => (Some(years), None)
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

  def technologies: Set[String] = {
    val tokens = s"$title $description"
      .split("(\\s|,|\\.|!|:|/)+")
      .map(_.toLowerCase)
    TechList.all.keys
      .filter(tokens.contains)
      .map(t => TechList.all(t)).toSet
  }
}

object ExperienceItem {
  def fromLinkedinExperienceSectionElem(elem: Element): Seq[ExperienceItem] =
    entries(elem.querySelectorAll("li")).map(tuple => tuple._2 match {
      case el: HTMLLIElement => fromExperienceListItem(el)
    }).toSeq

  private def fromExperienceListItem(elem: HTMLLIElement): ExperienceItem = {
    val summary = elem.getElementsByClassName("pv-entity__summary-info").item(0)
    val title = summary.querySelector("h3").textContent
    val descriptionElem = Option(elem.getElementsByClassName("pv-entity__description").item(0))
    val description = descriptionElem.map(_.textContent.trim()).getOrElse("")
    val employmentDuration = elem.getElementsByClassName("pv-entity__bullet-item-v2").item(0).textContent
    ExperienceItem(
      title,
      description,
      employmentDuration
    )
  }
}
