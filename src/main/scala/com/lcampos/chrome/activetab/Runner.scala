package com.lcampos.chrome.activetab

import com.lcampos.DurationPerTechGenerator
import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import org.scalajs.dom
import org.scalajs.dom.raw.{Element, NodeList}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.js.timers._

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages) {

  def run(): Unit = {
    log("This was run by the active tab")
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v: String) if v == "page was reloaded" =>
          setTimeout(3.seconds) {
            addDurationPerTechToPage()
          }
        case _ => ()
      }
    }
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
  }

  private def addDurationPerTechToPage(): Unit = {
    val experienceSectionElem: Element = dom.document.getElementById("experience-section")
    val durationPerTech = DurationPerTechGenerator.getFromLinkedinExperienceSection(experienceSectionElem)
    addYearsPerTechElem(durationPerTech)
  }

  private def addYearsPerTechElem(durationPerTech: Map[String, String]): Unit = {
    val mainColumnDiv: Element = dom.document.getElementById("main").firstElementChild
    val profileDetail: Element = mainColumnDiv.children.item(1)
    val aboutSection: Element = profileDetail.children.item(2)
    val durationPerTechSection: Element = generateYearsPerTechElement(aboutSection, durationPerTech)
    profileDetail.insertBefore(durationPerTechSection, profileDetail.firstElementChild)
  }

  private def generateYearsPerTechElement(elementToClone: Element, durationPerTech: Map[String, String]): Element = {
    val durationPerTechElem: Element = elementToClone.cloneNode(true).asInstanceOf[Element]

    durationPerTechElem.querySelector("h2").innerText = "Tech Experience Summary"

    val durationPerTechElemP: Element = durationPerTechElem.querySelector("p")

    val spans: NodeList = durationPerTechElemP.querySelectorAll("span")

    // Each of these has different classes
    val spanNormal = spans.item(0).asInstanceOf[Element]
    val spanLast = spans.item(spans.length - 2).asInstanceOf[Element]
    val spanEllipsis = spans.item(spans.length - 1).asInstanceOf[Element]

    durationPerTechElemP.innerHTML = ""

    val durationPerTechTexts = durationPerTech.map { case (tech, years) => s"<b>$tech - </b> $years</br>" }

    durationPerTechTexts.init.foreach { text =>
      val normalSpanClone = spanNormal.cloneNode(true).asInstanceOf[Element]
      normalSpanClone.innerHTML = text
      durationPerTechElemP.appendChild(normalSpanClone)
    }

    spanLast.innerHTML = durationPerTechTexts.last
    durationPerTechElemP.appendChild(spanLast)

    durationPerTechElemP.appendChild(spanEllipsis)

    durationPerTechElem
  }

  private def log(msg: String): Unit = {
    println(s"activeTab: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
