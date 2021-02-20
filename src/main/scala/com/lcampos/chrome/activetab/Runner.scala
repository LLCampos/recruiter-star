package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scala.concurrent.ExecutionContext

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages) {

  def run(): Unit = {
    log("This was run by the active tab")
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v: String) if v == "page was reloaded" => addYearsPerTechToPage()
        case _ => ()
      }
    }
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
  }

  private def addYearsPerTechToPage(): Unit = {
    val experienceSectionElem: Element = dom.document.getElementById("experience-section")
    val yearsPerTech = getYearsPerTech(experienceSectionElem)
    addBox()
    log(s"addYearsPerTechToPage was called")
  }

  private def addBox(): Unit = {
    val mainColumnDiv: Element = dom.document.getElementById("main").children.item(0)
    val profileDetail: Element = mainColumnDiv.children.item(1)
    val aboutSection: Element = profileDetail.children.item(2)
    val aboutSectionCopy: Element = aboutSection.cloneNode(true).asInstanceOf[Element]
    profileDetail.insertBefore(aboutSectionCopy, profileDetail.firstElementChild)
    log(s"addBox was called")
  }

  private def getYearsPerTech(experienceSectionElem: Element): Map[String, Int] =
    // TODO
    Map("Python" -> 1, "Java" -> 2)

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
