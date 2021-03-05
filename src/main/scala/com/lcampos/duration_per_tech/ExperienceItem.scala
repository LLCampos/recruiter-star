package com.lcampos.duration_per_tech

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
    getOneWordTechnologies ++ getMultiWordTechnologies ++ getTechnologiesWithDot

  private def getMultiWordTechnologies: Set[Tech] = {
    TechList.all
      .filter(_.name.split(" ").length > 1)
      .filter(tech => allText.contains(tech.name))
      .toSet
  }

  private def getOneWordTechnologies: Set[Tech] = {
    val tokens = allText.split("(\\s|,|\\.|!|:|/|;|\\(|\\)|\\[|])+")
    TechList.all
      .filter(tech => tokens.contains(tech.name))
      .toSet
  }

  private def getTechnologiesWithDot: Set[Tech] =
    TechList.all
      .filter(_.name.contains("."))
      .filter(tech => allText.contains(tech.name))
      .toSet

  private val allText = s"$title $description"
}
