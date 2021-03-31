package com.lcampos.chrome.popup

import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech.TechList
import com.lcampos.model.StorageKeys
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
        extensionActiveHandling()
        whichTechnologiesToSeeHandling()
      }
    }
//    backgroundAPI.sendBrowserNotification(messages.appName, "I'm on the Pop-up")
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
      case Failure(exception) => println(s"failure when getting '${StorageKeys.isExtensionActive}' from storage! $exception")
    }

    isActiveCheckbox.onclick = (_: Event) => StorageSyncUtil.set(StorageKeys.isExtensionActive, isActiveCheckbox.checked)
  }

  private def whichTechnologiesToSeeHandling(): Unit =
    ElementUtil.getElementByIdSafeAs[HTMLSelectElement](document, "whichTechnologiesToSee") match {
      case Right(selectElem) =>
        addTechOptions(selectElem)

        StorageSyncUtil.get[scalajs.js.Array[String]](StorageKeys.selectedTechnologies).onComplete {
          case Success(technologiesOpt) => technologiesOpt match {
            case Some(technologies) => println(technologies)
            case None => ()
          }
          case Failure(exception) => println(s"failure when getting '${StorageKeys.selectedTechnologies}' from storage! $exception")
        }

        selectElem.onchange = (_: Event) => StorageSyncUtil.set(
          StorageKeys.selectedTechnologies,
          ElementUtil.getAllSelected(selectElem).toJSArray
        )
      case Left(err) => println(err)
    }

  private def addTechOptions(whichTechnologiesToSeeElem: HTMLSelectElement): Unit =
      TechList.all.foreach(tech => ElementUtil.addOption(whichTechnologiesToSeeElem, tech.name))
}

object Runner {

  def apply()(implicit ec: ExecutionContext): Runner = {
    val messages = new I18NMessages
    val backgroundAPI = new BackgroundAPI()
    new Runner(messages, backgroundAPI)
  }
}
