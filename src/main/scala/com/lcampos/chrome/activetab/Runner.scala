package com.lcampos.chrome.activetab

import com.lcampos.LinkedinYearsPerTech
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
            addYearsPerTechToPage()
          }
        case _ => ()
      }
    }
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
  }

  private def addYearsPerTechToPage(): Unit = {
    val experienceSectionElem: Element = dom.document.getElementById("experience-section")
    val yearsPerTech = LinkedinYearsPerTech.getFromLinkedinExperienceSection(experienceSectionElem)
    addYearsPerTechElem(yearsPerTech)
  }

  private def addYearsPerTechElem(yearsPerTech: Map[String, String]): Unit = {
    val mainColumnDiv: Element = dom.document.getElementById("main").firstElementChild
    val profileDetail: Element = mainColumnDiv.children.item(1)
    val aboutSection: Element = profileDetail.children.item(2)
    val yearsPerTechSection: Element = generateYearsPerTechElement(aboutSection, yearsPerTech)
    profileDetail.insertBefore(yearsPerTechSection, profileDetail.firstElementChild)
  }

  private def generateYearsPerTechElement(elementToClone: Element, yearsPerTech: Map[String, String]): Element = {
    val yearsPerTechElem: Element = elementToClone.cloneNode(true).asInstanceOf[Element]

    yearsPerTechElem.querySelector("h2").innerText = "Years Per Tech"

    val yearsPerTechElemP: Element = yearsPerTechElem.querySelector("p")

    val spans: NodeList = yearsPerTechElemP.querySelectorAll("span")

    // Each of these has different classes
    val spanNormal = spans.item(0).asInstanceOf[Element]
    val spanLast = spans.item(spans.length - 2).asInstanceOf[Element]
    val spanEllipsis = spans.item(spans.length - 1).asInstanceOf[Element]

    yearsPerTechElemP.innerHTML = ""

    val yearsPerTechTexts = yearsPerTech.map { case (tech, years) => s"<b>$tech - </b> $years years</br>" }

    yearsPerTechTexts.init.foreach { text =>
      val normalSpanClone = spanNormal.cloneNode(true).asInstanceOf[Element]
      normalSpanClone.innerHTML = text
      yearsPerTechElemP.appendChild(normalSpanClone)
    }

    spanLast.innerHTML = yearsPerTechTexts.last
    yearsPerTechElemP.appendChild(spanLast)

    yearsPerTechElemP.appendChild(spanEllipsis)

    yearsPerTechElem
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
