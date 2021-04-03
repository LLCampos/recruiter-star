package com.lcampos.linkedin

import com.lcampos.util.{ElementUtil, SearchAndReplace}
import org.scalajs.dom.{Document, Element}

object LinkedinProfileHighlighter {

  def highlight(doc: Document, techNamesToHighlight: List[String]): Unit = {
    val elementsToHighlight = getElementsToHighlight(doc)
    techNamesToHighlight.foreach(t => highlight(t, elementsToHighlight))
  }

  private def highlight(techNameToHighlight: String, elementsToHighlight: List[Element]): Unit =
    SearchAndReplace.replace(
      techNameToHighlight,
      s"<mark>$techNameToHighlight</mark>",
      elementsToHighlight
    )

  private def getElementsToHighlight(doc: Document): List[Element] =
    getElementsToHighlightByClass(doc, List(
      "pv-entity__description"
    )) ++ getElementsToHighlightByIds(doc, List(
      "tech-experience-summary-content"
    ))


  private def getElementsToHighlightByClass(doc: Document, classes: List[String]): List[Element] =
    classes.flatMap(className => ElementUtil.getElementsByClassName(doc.documentElement, className))

  private def getElementsToHighlightByIds(doc: Document, ids: List[String]): List[Element] =
    ids.flatMap(id => ElementUtil.getElementByIdSafe(doc, id).toOption)
}
