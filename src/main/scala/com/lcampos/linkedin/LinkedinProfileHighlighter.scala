package com.lcampos.linkedin

import com.lcampos.duration_per_tech.Tech
import com.lcampos.util.SearchAndReplace
import org.scalajs.dom.Element

import scala.util.matching.Regex

object LinkedinProfileHighlighter {

  private val HighlightingColors = List(
    "#FFF380", // Corn Yellow
    "#E77471", // Light Coral (Red)
    "#7FFFD4", // Aquamarine (Blue)
    "#FF8040", // Mango Orange
    "#F778A1", // Carnation Pink
    "#82CAFA", // Light Sky Blue
    "#C38EC7", // Purple Dragon
    "#E3E4FA", // Lavender blue
    "#89C35C", // Green Peas
  )

  def highlight(techToHighlight: List[Tech], elementsToHighlight: List[Element]): Unit = {
    removePreviousHighlights(elementsToHighlight)
    techToHighlight.zip(HighlightingColors).foreach { case (tech, color) =>
      tech.aliases.toList
        .sortBy(_.length).reverse // Highlight the longer aliases first; otherwise, if one of the aliases is a substring of other, longer, alias, the longer alias will not be matched
        .foreach(alias => highlight(alias, elementsToHighlight, color))
    }
  }

  def removePreviousHighlights(elementsToRemoveHighlightFrom: List[Element]): Unit =
    SearchAndReplace.replace(
      s"$OpeningHighlightSpanMatcher(.*?)</span>",
      "$1",
      elementsToRemoveHighlightFrom
    )

  private def highlight(techNameToHighlight: String, elementsToHighlight: List[Element], color: String): Unit = {
    val escapedTechName = Regex.quote(techNameToHighlight)
    SearchAndReplace.replace(
      s"\\b(?<!$OpeningHighlightSpanMatcher)$escapedTechName\\b",
      s"${openingHighlightSpan(color)}$techNameToHighlight</span>",
      elementsToHighlight
    )
  }

  private def openingHighlightSpan(backgroundColor: String) = s"<span class='highlighted' style='background-color: $backgroundColor'>".replace("'", "\"")
  private val OpeningHighlightSpanMatcher = openingHighlightSpan(".{7}")
}
