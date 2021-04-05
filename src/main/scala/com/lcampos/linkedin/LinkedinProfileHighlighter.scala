package com.lcampos.linkedin

import com.lcampos.duration_per_tech.Tech
import com.lcampos.model.LinkedinProfileManipulator
import com.lcampos.util.{ElementUtil, SearchAndReplace}
import org.scalajs.dom.{Document, Element}

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

  def highlight(doc: Document, manipulator: LinkedinProfileManipulator, techToHighlight: List[Tech]): Unit = {
    val elementsToHighlight = getElementsToHighlight(doc, manipulator)
    removePreviousHighlights(doc, manipulator)
    techToHighlight.zip(HighlightingColors).foreach { case (tech, color) =>
      tech.aliases.foreach(alias => highlight(alias, elementsToHighlight, color))
    }
  }

  def removePreviousHighlights(doc: Document, manipulator: LinkedinProfileManipulator): Unit = {
    val elementsToRemoveHighlight = getElementsToHighlight(doc, manipulator)
    SearchAndReplace.replace(
      "<span class=\"highlighted\" style=\"background-color: .*?\">(.*?)</span>",
      "$1",
      elementsToRemoveHighlight
    )
  }

  private def highlight(techNameToHighlight: String, elementsToHighlight: List[Element], color: String): Unit =
    SearchAndReplace.replace(
      s"\\b$techNameToHighlight\\b",
      s"<span class='highlighted' style='background-color: $color'>$techNameToHighlight</span>",
      elementsToHighlight
    )

  private def getElementsToHighlight(doc: Document, manipulator: LinkedinProfileManipulator): List[Element] = {
    getElementsToHighlightByClass(doc, List(
      manipulator.ExperienceDescriptionClass,
      manipulator.PeopleAlsoViewedTitleClass,
      manipulator.ProfileInfoBelowPicClass,
      manipulator.SkillEndorsementTitleClass,
      manipulator.AboutClass,
    )) ++ getElementsToHighlightByIds(doc, List(
      LinkedinProfileManipulator.TechExperienceSummaryContentId
    )) ++ manipulator.getAllExperienceItemsTitlesSections
  }


  private def getElementsToHighlightByClass(doc: Document, classes: List[String]): List[Element] =
    classes.flatMap(className => ElementUtil.getElementsByClassName(doc.documentElement, className))

  private def getElementsToHighlightByIds(doc: Document, ids: List[String]): List[Element] =
    ids.flatMap(id => ElementUtil.getElementByIdSafe(doc, id).toOption)
}
