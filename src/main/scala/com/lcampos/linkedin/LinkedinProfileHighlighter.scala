package com.lcampos.linkedin

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

  def highlight(doc: Document, techNamesToHighlight: List[String]): Unit = {
    val elementsToHighlight = getElementsToHighlight(doc)
    techNamesToHighlight.zip(HighlightingColors).foreach { case (name, color) =>
      highlight(name, elementsToHighlight, color)
    }
  }

  private def highlight(techNameToHighlight: String, elementsToHighlight: List[Element], color: String): Unit =
    SearchAndReplace.replace(
      techNameToHighlight,
      s"<span class='highlighted' style='background-color: $color'>$techNameToHighlight</span>",
      elementsToHighlight
    )

  private def getElementsToHighlight(doc: Document): List[Element] = {
    getElementsToHighlightByClass(doc, List(
      LinkedinProfileManipulatorBasic.ExperienceDescriptionClass,
      LinkedinProfileManipulatorPremium.ExperienceDescriptionClass,
      LinkedinProfileManipulatorBasic.PeopleAlsoViewedTitleClass
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
