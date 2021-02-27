package com.lcampos.duration_per_tech

import cats.kernel.Semigroup
import cats.syntax.all._
import org.scalajs.dom.raw._

import scala.concurrent.duration.Duration


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
