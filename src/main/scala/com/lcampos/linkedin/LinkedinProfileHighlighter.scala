package com.lcampos.linkedin

import com.lcampos.duration_per_tech.Tech
import com.lcampos.model.{LinkedinProfileManipulator, LinkedinProfileManipulatorBasic, LinkedinProfileManipulatorPremium}
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

  def highlight(doc: Document, techToHighlight: List[Tech]): Unit = {
    val elementsToHighlight = getElementsToHighlight(doc)
    removePreviousHighlights(elementsToHighlight)
    techToHighlight.zip(HighlightingColors).foreach { case (tech, color) =>
      tech.aliases.foreach(alias => highlight(alias, elementsToHighlight, color))
    }
  }

  private def highlight(techNameToHighlight: String, elementsToHighlight: List[Element], color: String): Unit =
    SearchAndReplace.replace(
      s"\\b$techNameToHighlight\\b",
      s"<span class='highlighted' style='background-color: $color'>$techNameToHighlight</span>",
      elementsToHighlight
    )

  private def removePreviousHighlights(elems: List[Element]): Unit =
    SearchAndReplace.replace(
      "<span class=\"highlighted\" style=\"background-color: .*?\">(.*?)</span>",
      "$1",
      elems
    )

  private def getElementsToHighlight(doc: Document): List[Element] = {
    getElementsToHighlightByClass(doc, List(
      LinkedinProfileManipulatorBasic.ExperienceDescriptionClass,
      LinkedinProfileManipulatorBasic.PeopleAlsoViewedTitleClass,
      LinkedinProfileManipulatorBasic.ProfileInfoBelowPicClass,
      LinkedinProfileManipulatorPremium.ExperienceDescriptionClass,
    )) ++ getElementsToHighlightByIds(doc, List(
      LinkedinProfileManipulator.TechExperienceSummaryContentId
    )) ++ LinkedinProfileManipulatorBasic.getAllExperienceItemsTitlesSections(doc) ++
      LinkedinProfileManipulatorPremium.getAllExperienceItemsTitlesSections(doc)
  }


  private def getElementsToHighlightByClass(doc: Document, classes: List[String]): List[Element] =
    classes.flatMap(className => ElementUtil.getElementsByClassName(doc.documentElement, className))

  private def getElementsToHighlightByIds(doc: Document, ids: List[String]): List[Element] =
    ids.flatMap(id => ElementUtil.getElementByIdSafe(doc, id).toOption)
}
