package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.duration_per_tech.Tech
import com.lcampos.model.{InternalMessages, LinkedinProfileManipulator, UserConfig}
import com.lcampos.util.{FutureUtil, Repeat}
import odelay.Timer
import org.scalajs.dom

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages)
  (implicit ec: ExecutionContext, timer: Timer) {

  def run(): Unit =
    LinkedinProfileManipulator.apply(dom.window.location.href, dom.document) match {
      case Some(manipulator) => onSupportedPage(manipulator)
      case None => ()
    }

  private def onSupportedPage(manipulator: LinkedinProfileManipulator): Unit =
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v: String) if v.contains("page was reloaded") || v == InternalMessages.RefreshApp =>
          UserConfig.load.flatMap { userConf =>
            if (userConf.isExtensionActive) {
              if (userConf.selectedTechnologies.isEmpty) manipulator.removePreviousHighlights()
              val waitTime = if (v == InternalMessages.RefreshApp) 0.millis else 500.millis
              onExtensionActive(userConf, manipulator, waitTime)
            } else {
              Future(onExtensionInactive(manipulator))
            }
          }
        case _ => ()
      }
    }

  private def onExtensionInactive(manipulator: LinkedinProfileManipulator): Unit = {
    manipulator.removeTechExperienceSummaryElem()
    manipulator.removePreviousHighlights()
  }

  private def onExtensionActive(userConfig: UserConfig, profileManipulator: LinkedinProfileManipulator, waitTime: FiniteDuration): Future[Unit] = {
    val selectedTech = userConfig.selectedTechnologies.flatMap(Tech.fromName)
    val techToShow = if (userConfig.selectedTechnologies.isEmpty) Tech.all else selectedTech
    for {
      _ <- FutureUtil.delay(waitTime)
      _ <- addTechExperienceSummaryBoxWithRetries(profileManipulator, techToShow)
      _ <- highlightSelectedTechnologies(profileManipulator, selectedTech)
    } yield ()
  }

  private def addTechExperienceSummaryBoxWithRetries(linkedinProfileManipulator: LinkedinProfileManipulator, baseTechs: List[Tech]): Future[Unit] =
    retry.Pause(100, 100.milli)(timer) { () =>
      Future {
        linkedinProfileManipulator.addDurationPerTech(baseTechs)
      }
    }.map {
      case Right(_) => ()
      case Left(err) => println(err)
    }

  private def highlightSelectedTechnologies(profileManipulator: LinkedinProfileManipulator, selectedTech: List[Tech]) = Future {
    if (selectedTech.nonEmpty) {
      profileManipulator.expandEachExperience()
      profileManipulator.removeSeeLessFromEachExperienceSection()
      profileManipulator.expandAbout()
      Repeat.repeat(10, 1.second)(profileManipulator.highlight(selectedTech))
    }
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext, t: Timer): Runner = {
    val backgroundAPI = new BackgroundAPI
    val messages = new I18NMessages
    new Runner(config.activeTabConfig, backgroundAPI, messages)
  }
}
