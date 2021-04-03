package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech.Tech
import com.lcampos.model.{LinkedinProfileManipulator, UserConfig}
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
          UserConfig.load.flatMap { userConf =>
            if (!userConf.isExtensionActive) {
              Future.unit
            } else {
              val techList = if (userConf.selectedTechnologies.isEmpty) Tech.all else userConf.selectedTechnologies.flatMap(Tech.fromName)
              addTechExperienceSummaryBoxWithRetries(v, techList)
            }
          }
        case _ => ()
      }
    }
  }

  private def addTechExperienceSummaryBoxWithRetries(msg: String, baseTechs: List[Tech]) =
    retry.Pause(50, 100.milli)(timer) { () =>
      Future {
        addTechExperienceSummaryBox(msg, baseTechs)
      }
    }.map {
      case Right(_) => ()
      case Left(err) => println(err)
    }

  private def addTechExperienceSummaryBox(msg: String, baseTechs: List[Tech]) =
    LinkedinProfileManipulator.fromUrl(msg) match {
      case Some(manipulator) =>
        manipulator.addDurationPerTech(dom.document, baseTechs)
      case None => Right(())
    }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext, t: Timer): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
