package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.model.{LinkedinProfileManipulator, StorageKeys}
import com.lcampos.util.StorageSyncUtil
import odelay.Timer
import org.scalajs.dom

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages)
  (implicit ec: ExecutionContext, timer: Timer) {

  def run(): Unit = {
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v: String) if v.contains("page was reloaded") =>
          StorageSyncUtil.get[Boolean](StorageKeys.isExtensionActive).flatMap {
            case Some(isActive) => if (isActive) addTechExperienceSummaryBoxWithRetries(v) else Future.unit
            case None => addTechExperienceSummaryBoxWithRetries(v)
          }
        case _ => ()
      }
    }
  }

  private def addTechExperienceSummaryBoxWithRetries(msg: String) =
    retry.Pause(50, 100.milli)(timer) { () =>
      Future {
        addTechExperienceSummaryBox(msg)
      }
    }.map {
      case Right(_) => ()
      case Left(err) => println(err)
    }

  private def addTechExperienceSummaryBox(msg: String) =
      LinkedinProfileManipulator.fromUrl(msg) match {
        case Some(manipulator) =>
          manipulator.addDurationPerTech(dom.document)
        case None => Right(())
      }

  private def log(msg: String): Unit = {
    println(s"activeTab: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext, t: Timer): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
