package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.chrome.facades.SweetAlert

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSConverters._

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages)(implicit
    ec: ExecutionContext
) {

  def run(): Unit = {
    log("This was run by the active tab")
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v : String) if v == "page was reloaded" => openAlert()
        case _ => ()
      }
    }
    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the tab!!")
  }

  private def openAlert(): Unit = {
    SweetAlert(new SweetAlert.Options {
      title = messages.appName
      text = "Do you like this template?"
      icon = chrome.runtime.Runtime.getURL("icons/96/app.png")
      buttons = Option(List("No", "Yes").toJSArray).orUndefined
    }).toFuture.onComplete { t =>
      log(s"SweetAlert result: $t")
    }
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
