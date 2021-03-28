package com.lcampos.chrome.popup

import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.model.StorageKeys
import com.lcampos.util.StorageSyncUtil
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLInputElement

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Runner(messages: I18NMessages, backgroundAPI: BackgroundAPI)(implicit ec: ExecutionContext) {

  def run(): Unit = {
    log("This was run by the popup script")
    document.onreadystatechange = _ => {
      if (document.readyState == "complete") {
        extensionActiveHandling()
      }
    }
//    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the Pop-up")
  }

  private def log(msg: String): Unit = {
    println(s"popup: $msg")
  }

  private def extensionActiveHandling(): Unit = {
    val isActiveCheckbox = document
      .getElementById("isExtensionActiveCheckbox")
      .asInstanceOf[HTMLInputElement]

    StorageSyncUtil.get[Boolean](StorageKeys.isExtensionActive).onComplete {
      case Success(isActiveOpt) => isActiveOpt match {
        case Some(isActive) => isActiveCheckbox.checked = isActive
        case None => isActiveCheckbox.checked = true
      }
      case Failure(exception) => println(s"failure when getting from storage! $exception")
    }

    isActiveCheckbox.onclick = (_: Event) => StorageSyncUtil.set(StorageKeys.isExtensionActive, isActiveCheckbox.checked)
  }
}

object Runner {

  def apply()(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    new Runner(messages, backgroundAPI)
  }
}
