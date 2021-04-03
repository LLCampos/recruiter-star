package com.lcampos.duration_per_tech

import com.lcampos.util.time.InstantRange

case class ExperienceItem(
  title: String,
  description: String,
  instantRange: InstantRange
) {
  def technologies(baseTechs: List[Tech]): Set[Tech] =
    getOneWordTechnologies(baseTechs) ++ getMultiWordTechnologies(baseTechs) ++ getTechnologiesWithDot(baseTechs)

  private def getMultiWordTechnologies(baseTechs: List[Tech]): Set[Tech] =
    baseTechs
      .filter(tech => tech.multiWordAliases.exists(allText.contains))
      .toSet

  private def getOneWordTechnologies(baseTechs: List[Tech]): Set[Tech] = {
    val tokens = allText.split("(\\s|,|\\.|!|:|/|;|\\(|\\)|\\[|])+")
    baseTechs
      .filter(tech => tech.aliases.exists(tokens.contains))
      .toSet
  }

  private def getTechnologiesWithDot(baseTechs: List[Tech]): Set[Tech] =
    baseTechs
      .filter(tech => tech.aliasesWithDot.exists(allText.contains))
      .toSet

  private val allText = s"$title $description"
}
