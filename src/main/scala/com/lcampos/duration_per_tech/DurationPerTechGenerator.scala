package com.lcampos.duration_per_tech

import cats.kernel.Semigroup
import cats.syntax.all._
import org.scalajs.dom.raw._

import scala.concurrent.duration.Duration


object DurationPerTechGenerator {

  private val DaysInYear = 365
  private val DaysInMonth = 30

  def getFromLinkedinExperienceSection(elem: Element): Either[String, Map[String, String]] = {
    ExperienceItem.fromLinkedinExperienceSectionElem(elem).map { experienceItems =>
      experienceItems.map(getDurationPerTech).sequence match {
        case Some(durationPerTechSeq) =>
          durationPerTechSeq
            .reduce(Semigroup[Map[String, Duration]].combine)
            .view.mapValues(formatDuration).toMap
        case None =>
          Map()
      }
    }
  }

  private def formatDuration(duration: Duration): String = {
    val days = duration.toDays
    val years = days / DaysInYear
    val months = (days % DaysInYear) / DaysInMonth

    (years, months) match {
      case (0, months) => s"$months months"
      case (years, 0) => s"$years years"
      case (years, months) => s"$years years and $months months"
    }
  }

  protected def getDurationPerTech(experienceItem: ExperienceItem): Option[Map[String, Duration]] =
    experienceItem.duration match {
      case Some(duration) => Some(experienceItem.technologies.map(_ -> duration).toMap)
      case None =>
        println(s"Couldn't parse duration: ${experienceItem.durationDescription}")
        None
    }
}
