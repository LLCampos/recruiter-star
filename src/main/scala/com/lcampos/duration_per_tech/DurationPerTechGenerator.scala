package com.lcampos.duration_per_tech

import cats.kernel.Semigroup
import cats.syntax.all._
import org.scalajs.dom.raw._

import scala.concurrent.duration.Duration


object DurationPerTechGenerator {

  private val DaysInYear = 365
  private val DaysInMonth = 30

  type DurationPerTechPerCategory = Map[String, Map[String, String]]

  def getFromLinkedinExperienceSection(elem: Element): Either[String, DurationPerTechPerCategory] = {
    ExperienceItem.fromLinkedinExperienceSectionElem(elem).map { experienceItems =>
      experienceItems.map(getDurationPerTech).sequence match {
        case Some(durationPerTechSeq) =>
          durationPerTechSeq
            .reduce(Semigroup[Map[Tech, Duration]].combine)
            .groupBy(_._1.category)
            .map { case (category, durationPerTech) =>
              category.uiRepresentation -> durationPerTech.map { case (tech, duration) => tech.canonName -> formatDuration(duration) }
            }
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

  protected def getDurationPerTech(experienceItem: ExperienceItem): Option[Map[Tech, Duration]] =
    experienceItem.duration match {
      case Some(duration) => Some(experienceItem.technologies.map(_ -> duration).toMap)
      case None =>
        println(s"Couldn't parse duration: ${experienceItem.durationDescription}")
        None
    }
}
