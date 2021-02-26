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


object DurationPerTechGenerator {

  private val DaysInYear = 365
  private val DaysInMonth = 30

  def getFromLinkedinExperienceSection(elem: Element): Map[String, String] = {
    val experienceItems = ExperienceItem.fromLinkedinExperienceSectionElem(elem)
    experienceItems.map(getDurationPerTech).sequence match {
      case Some(durationPerTechSeq) =>
        durationPerTechSeq
          .reduce(Semigroup[Map[String, Duration]].combine)
          .view.mapValues(formatDuration).toMap
      case None =>
        println("Couldn't get duration per tech for some of the experience items")
        Map()
    }
  }

  private def formatDuration(duration: Duration): String = {
    val days = duration.toDays
    val years = days / DaysInYear
    val months = (days % DaysInYear) / DaysInMonth
    s"$years years and $months months"
  }

  protected def getDurationPerTech(experienceItem: ExperienceItem): Option[Map[String, Duration]] =
    experienceItem.duration.map(d => experienceItem.technologies.map(_ -> d).toMap)
}
