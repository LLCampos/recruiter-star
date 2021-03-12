package com.lcampos.duration_per_tech

import scala.concurrent.duration.{DAYS, Duration}

case class ExperienceItem(
  title: String,
  description: String,
  durationDescription: String
) {
  def duration: Option[Duration] = {
    val (yearsOpt, monthsOpt) = durationDescription match {
      // Linkedin Basic
      case s"$years yr $months mos" => (Some(years), Some(months))
      case s"$years yrs $months mos" => (Some(years), Some(months))
      case s"$years yrs $months mo" => (Some(years), Some(months))
      case s"$years yr $months mo" => (Some(years), Some(months))
      case s"$years yrs" => (Some(years), None)
      case s"$years yr" => (Some(years), None)
      case s"$months mos" => (None, Some(months))
      case s"$months mo" => (None, Some(months))
      // Linkedin Premium
      case s"($years year $months months)" => (Some(years), Some(months))
      case s"($years years $months months)" => (Some(years), Some(months))
      case s"($years years $months month)" => (Some(years), Some(months))
      case s"($years year $months month)" => (Some(years), Some(months))
      case s"($years years)" => (Some(years), None)
      case s"($years year)" => (Some(years), None)
      case s"($months months)" => (None, Some(months))
      case s"($months month)" => (None, Some(months))
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
      .filter(tech => tech.multiWordAliases.exists(allText.contains))
      .toSet
  }

  private def getOneWordTechnologies: Set[Tech] = {
    val tokens = allText.split("(\\s|,|\\.|!|:|/|;|\\(|\\)|\\[|])+")
    TechList.all
      .filter(tech => tech.aliases.exists(tokens.contains))
      .toSet
  }

  private def getTechnologiesWithDot: Set[Tech] =
    TechList.all
      .filter(tech => tech.aliasesWithDot.exists(allText.contains))
      .toSet

  private val allText = s"$title $description"
}
