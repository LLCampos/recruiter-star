package com.lcampos.chrome.popup

import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech.Tech
import com.lcampos.model.{UserConfigKeys, UserConfig}
import com.lcampos.util.{ElementUtil, StorageSyncUtil}
import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSConverters.JSRichIterableOnce
import scala.util.{Failure, Success}

class Runner(messages: I18NMessages, backgroundAPI: BackgroundAPI)(implicit ec: ExecutionContext) {

  def run(): Unit = {
    document.onreadystatechange = _ => {
      if (document.readyState == "complete") {
        UserConfig.load.onComplete {
          case Success(userConfig: UserConfig) =>
            extensionActiveHandling(userConfig.isExtensionActive)
            whichTechnologiesToSeeHandling(userConfig.selectedTechnologies)
          case Failure(exception) => println(exception)
        }
      }
    }
//    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the Pop-up")
  }

  private def extensionActiveHandling(isExtensionActive: Boolean): Unit = {
    val isActiveCheckbox = document
      .getElementById("isExtensionActiveCheckbox")
      .asInstanceOf[HTMLInputElement]
    isActiveCheckbox.checked = isExtensionActive
    isActiveCheckbox.onclick = (_: Event) => StorageSyncUtil.set(UserConfigKeys.isExtensionActive, isActiveCheckbox.checked)
  }

  private def whichTechnologiesToSeeHandling(selectedTechnologies: List[String]): Unit =
    ElementUtil.getElementByIdSafeAs[HTMLSelectElement](document, "whichTechnologiesToSee") match {
      case Right(selectElem) =>
        ElementUtil.addOptions(selectElem, Tech.all.map(_.name), selectedTechnologies)
        selectElem.onchange = (_: Event) => StorageSyncUtil.set(
          UserConfigKeys.selectedTechnologies,
          ElementUtil.getAllSelected(selectElem).toJSArray
        )
      case Left(err) => println(err)
    }
}

object Runner {

  def apply()(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    new Runner(messages, backgroundAPI)
  }
}
