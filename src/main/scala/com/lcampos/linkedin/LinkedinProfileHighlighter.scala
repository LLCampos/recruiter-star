package com.lcampos.linkedin

import com.lcampos.util.{ElementUtil, SearchAndReplace}
import org.scalajs.dom.{Document, Element}

object LinkedinProfileHighlighter {

  def highlight(doc: Document): Unit =
    SearchAndReplace.replace(
      "Java",
      "<mark>Java</mark>",
      getElementsToHighlight(doc)
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
