package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech
import com.lcampos.model.{LinkedinProfileManipulator, LinkedinProfileManipulatorBasic, LinkedinProfileManipulatorPremium, NoOpManipulator}
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.js.timers._

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages) {

  def run(): Unit = {
    log("This was run by the active tab")
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v: String) if v.contains("page was reloaded") =>
          setTimeout(100.milli) {
            LinkedinProfileManipulator.fromUrl(v) match {
              case LinkedinProfileManipulatorBasic => duration_per_tech.PageManipulator.addDurationPerTechToPage(dom.document)
              case LinkedinProfileManipulatorPremium => log("Premium page!")
              case NoOpManipulator => ()
            }
          }
        case _ => ()
      }
    }
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
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
