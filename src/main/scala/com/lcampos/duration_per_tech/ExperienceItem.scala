package com.lcampos.duration_per_tech

import com.lcampos.util.time.InstantRange

case class ExperienceItem(
  title: String,
  description: String,
  instantRange: InstantRange
) {
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
