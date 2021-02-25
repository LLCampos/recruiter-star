package com.lcampos

import cats.kernel.Semigroup
import cats.syntax.all._
import org.scalajs.dom.raw._

import scala.concurrent.duration.{DAYS, Duration}
import scala.scalajs.js.Object.entries

case class ExperienceItem(
  title: String,
  description: String,
  durationDescription: String
) {
  def duration: Option[Duration] = {
    val durationDescriptionRegex = raw"(\d+) yrs( (\d+) mos)?".r

    val (yearsOpt, monthsOpt) = durationDescription match {
      case durationDescriptionRegex(years, _, null) => (Some(years), None)
      case durationDescriptionRegex(years, _, months) => (Some(years), Some(months))
      case _ => (None, None)
    }

    for {
      yearsStr <- yearsOpt
      monthsStr = monthsOpt.getOrElse("0")
      years <- yearsStr.toIntOption
      months <- monthsStr.toIntOption
      totalDays = 365 * years.toInt + 30 * months.toInt
    } yield Duration(totalDays, DAYS)
  }

  def technologies: Set[String] = {
    val tokens = s"$title $description"
      .split("(\\s|,|\\.|!)+")
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
    val description = elem.getElementsByClassName("pv-entity__description").item(0).textContent.trim()
    val employmentDuration = elem.getElementsByClassName("pv-entity__bullet-item-v2").item(0).textContent
    ExperienceItem(
      title,
      description,
      employmentDuration
    )
  }
}


object LinkedinYearsPerTech {

  private val DaysInYear = 365
  private val DaysInMonth = 30

  def getFromLinkedinExperienceSection(elem: Element): Map[String, String] = {
    val experienceItems = ExperienceItem.fromLinkedinExperienceSectionElem(elem)
    experienceItems.map(getYearsPerTech).sequence match {
      case Some(yearsPerTechSeq) =>
        yearsPerTechSeq
          .reduce(Semigroup[Map[String, Duration]].combine)
          .view.mapValues(formatDuration).toMap
      case None => Map()
    }
  }

  private def formatDuration(duration: Duration): String = {
    val days = duration.toDays
    val years = days / DaysInYear
    val months = (days % DaysInYear) / DaysInMonth
    s"$years yrs and $months mos"
  }

  protected def getYearsPerTech(experienceItem: ExperienceItem): Option[Map[String, Duration]] =
    experienceItem.duration.map(d => experienceItem.technologies.map(_ -> d).toMap)
}
