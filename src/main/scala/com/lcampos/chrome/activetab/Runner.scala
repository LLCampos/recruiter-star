package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech.Tech
import com.lcampos.linkedin.LinkedinProfileHighlighter
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
            if (userConf.isExtensionActive) {
              onExtensionActive(userConf, v)
            } else {
              Future.unit
            }
          }
        case _ => ()
      }
    }
  }

  private def onExtensionActive(userConfig: UserConfig, msgValue: String) = {
    LinkedinProfileManipulator.fromUrl(msgValue) match {
      case Some(manipulator) =>
        val techList = if (userConfig.selectedTechnologies.isEmpty) Tech.all else userConfig.selectedTechnologies.flatMap(Tech.fromName)
        for {
          _ <- addTechExperienceSummaryBoxWithRetries(manipulator, techList)
          _ <- Future(LinkedinProfileHighlighter.highlight(dom.document, userConfig.selectedTechnologies))
        } yield ()
      case None => Future.unit
    }
  }

  private def addTechExperienceSummaryBoxWithRetries(linkedinProfileManipulator: LinkedinProfileManipulator, baseTechs: List[Tech]) =
    retry.Pause(100, 100.milli)(timer) { () =>
      Future {
        linkedinProfileManipulator.addDurationPerTech(dom.document, baseTechs)
      }
    }.map {
      case Right(_) => ()
      case Left(err) => println(err)
    }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext, t: Timer): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
