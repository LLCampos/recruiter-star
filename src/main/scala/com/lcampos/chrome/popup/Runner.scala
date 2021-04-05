package com.lcampos.chrome.popup

import chrome.tabs.bindings.TabQuery
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech.Tech
import com.lcampos.model.{InternalMessages, UserConfig, UserConfigKeys}
import com.lcampos.util.{ElementUtil, StorageSyncUtil}
import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLElement, HTMLInputElement, HTMLSelectElement}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSConverters.JSRichIterableOnce
import scala.util.{Failure, Success}

class Runner(messages: I18NMessages, backgroundAPI: BackgroundAPI)(implicit ec: ExecutionContext) {

  def run(): Unit = {
    document.onreadystatechange = _ => {
      if (document.readyState == "complete") {
        doneButtonHandling()
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
    isActiveCheckbox.onclick = (_: Event) => {
      StorageSyncUtil.add(UserConfigKeys.isExtensionActive, isActiveCheckbox.checked)
      sendRefreshExtensionMsgToCurrentTab()
    }
  }

  private def whichTechnologiesToSeeHandling(selectedTechnologies: List[String]): Unit =
    ElementUtil.getElementByIdSafeAs[HTMLSelectElement](document, "whichTechnologiesToSee") match {
      case Right(selectElem) =>
        ElementUtil.addOptions(selectElem, Tech.all.map(_.name), selectedTechnologies)
        selectElem.onchange = (_: Event) => onTechSelection(selectElem)
      case Left(err) => println(err)
    }

  private def onTechSelection(selectElem: HTMLSelectElement): Unit = {
    StorageSyncUtil.add(
      UserConfigKeys.selectedTechnologies,
      ElementUtil.getAllSelected(selectElem).toJSArray
    )
    sendRefreshExtensionMsgToCurrentTab()
  }

  private def doneButtonHandling(): Unit =
    ElementUtil.getElementByIdSafeAs[HTMLElement](document, "doneButton") match {
      case Right(button) => button.onclick = (_: Event) => window.close()
      case Left(err) => println(err)
    }

  private def sendRefreshExtensionMsgToCurrentTab(): Unit =
    sendMsgToCurrentTab(InternalMessages.RefreshApp)

  private def sendMsgToCurrentTab(msg: String): Unit =
    chrome.tabs.Tabs.query(TabQuery(active = true, currentWindow = true)).onComplete {
      case Success(tabs) => tabs.head.id.toOption match {
        case Some(tabId) => chrome.tabs.Tabs.sendMessage(tabId, msg)
        case None => println("Issue getting current tab id")
      }
      case Failure(err) => print(err)
    }
}

object Runner {

  def apply()(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    new Runner(messages, backgroundAPI)
  }
}
