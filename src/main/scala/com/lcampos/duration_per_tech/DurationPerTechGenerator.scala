package com.lcampos.duration_per_tech

import com.lcampos.util.time.InstantRange

import scala.collection.immutable.ListMap
import scala.concurrent.duration.Duration


object DurationPerTechGenerator {

  private val DaysInYear = 365
  private val DaysInMonth = 30

  type DurationPerTechPerCategory = ListMap[String, ListMap[String, String]]

  def getFromLinkedinExperienceItems(experienceItems: List[ExperienceItem]): DurationPerTechPerCategory = {
    val tsRangesPerTech = experienceItems.map(getTsRangesPerTech)
    val durationPerTech = toDurationPerTech(tsRangesPerTech.flatMap(_.toList).groupMap(_._1)(_._2))
    convertToStringPrettyPresentation(durationPerTech.groupBy(_._1.category))
  }

  private def toDurationPerTech(rangesPerTech: Map[Tech, List[InstantRange]]): Map[Tech, Duration] =
    rangesPerTech.view.mapValues(InstantRange.totalDuration).toMap

  private def convertToStringPrettyPresentation(durationPerTechPerCategory: Map[TechCategory, Map[Tech, Duration]]): DurationPerTechPerCategory =
    orderDurationPerTechPerCategory(durationPerTechPerCategory).map { case (category, durationPerTech) =>
      category.uiRepresentation -> orderDurationPerTech(durationPerTech).map { case (tech, duration) =>
        tech.name -> formatDuration(duration)
      }
    }

  private def orderDurationPerTechPerCategory(m: Map[TechCategory, Map[Tech, Duration]]): ListMap[TechCategory, Map[Tech, Duration]] =
    ListMap.from(m.toSeq.sortBy(_._1))

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

  protected def getTsRangesPerTech(experienceItem: ExperienceItem): Map[Tech, InstantRange] =
    experienceItem.technologies.map(_ -> experienceItem.instantRange).toMap
}
