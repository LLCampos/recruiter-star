package com.lcampos.duration_per_tech

import cats.kernel.Semigroup
import cats.syntax.all._
import org.scalajs.dom.raw._

import scala.collection.immutable.ListMap
import scala.concurrent.duration.Duration


object DurationPerTechGenerator {

  private val DaysInYear = 365
  private val DaysInMonth = 30

  type DurationPerTechPerCategory = Map[String, ListMap[String, String]]

  def getFromLinkedinExperienceSection(elem: Element): Either[String, DurationPerTechPerCategory] = {
    ExperienceItem.fromLinkedinExperienceSectionElem(elem).map { experienceItems =>
      experienceItems.map(getDurationPerTech).sequence match {
        case Some(durationPerTechSeq) =>
          convertToStringPrettyPresentation(
            durationPerTechSeq
              .reduce(Semigroup[Map[Tech, Duration]].combine)
              .groupBy(_._1.category)
          )
        case None =>
          Map()
      }
    }
  }

  def convertToStringPrettyPresentation(durationPerTechPerCategory: Map[TechCategory, Map[Tech, Duration]]): DurationPerTechPerCategory =
    durationPerTechPerCategory.map { case (category, durationPerTech) =>
      category.uiRepresentation -> orderDurationPerTech(durationPerTech).map { case (tech, duration) =>
        tech.canonName -> formatDuration(duration)
      }
    }

  private def orderDurationPerTech(durationPerTech: Map[Tech, Duration]): ListMap[Tech, Duration] =
    ListMap.from(durationPerTech.toSeq.sortBy(_._2).reverse)

  private def formatDuration(duration: Duration): String = {
    val days = duration.toDays
    val years = days / DaysInYear
    val months = (days % DaysInYear) / DaysInMonth

    val yearsString = years match {
      case 0 => ""
      case 1 => "1 year"
      case x => s"$x years"
    }

    val monthString = months match {
      case 0 => ""
      case 1 => "1 month"
      case x => s"$x months"
    }

    (yearsString, monthString) match {
      case ("", m) => m
      case (y, "") => y
      case (y, m) => s"$y and $m"
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
