package com.lcampos.linkedin

import com.lcampos.duration_per_tech.Tech
import com.lcampos.util.SearchAndReplace
import org.scalajs.dom.Element

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
      tech.aliases.foreach(alias => highlight(alias, elementsToHighlight, color))
    }
  }

  def removePreviousHighlights(elementsToRemoveHighlightFrom: List[Element]): Unit =
    SearchAndReplace.replace(
      "<span class=\"highlighted\" style=\"background-color: .*?\">(.*?)</span>",
      "$1",
      elementsToRemoveHighlightFrom
    )

  private def highlight(techNameToHighlight: String, elementsToHighlight: List[Element], color: String): Unit =
    SearchAndReplace.replace(
      s"\\b$techNameToHighlight\\b",
      s"<span class='highlighted' style='background-color: $color'>$techNameToHighlight</span>",
      elementsToHighlight
    )
}
