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

  private def onExtensionActive(userConfig: UserConfig, msgValue: String): Future[Unit] = {
    LinkedinProfileManipulator.fromUrl(msgValue) match {
      case Some(manipulator) => onExtensionActive(userConfig, manipulator)
      case None => Future.unit
    }
  }

  private def onExtensionActive(userConfig: UserConfig, profileManipulator: LinkedinProfileManipulator): Future[Unit] = {
    val selectedTech = userConfig.selectedTechnologies.flatMap(Tech.fromName)
    val techToShow = if (userConfig.selectedTechnologies.isEmpty) Tech.all else selectedTech
    for {
      _ <- addTechExperienceSummaryBoxWithRetries(profileManipulator, techToShow)
      _ <- highlightSelectedTechnologies(profileManipulator, selectedTech)
    } yield ()
  }

  private def addTechExperienceSummaryBoxWithRetries(linkedinProfileManipulator: LinkedinProfileManipulator, baseTechs: List[Tech]): Future[Unit] =
    retry.Pause(100, 100.milli)(timer) { () =>
      Future {
        linkedinProfileManipulator.addDurationPerTech(dom.document, baseTechs)
      }
    }.map {
      case Right(_) => ()
      case Left(err) => println(err)
    }

  private def highlightSelectedTechnologies(profileManipulator: LinkedinProfileManipulator, selectedTech: List[Tech]) = Future {
    if (selectedTech.nonEmpty) {
      profileManipulator.expandEachExperience(dom.document)
      profileManipulator.removeSeeLessFromEachExperienceSection(dom.document)
      LinkedinProfileHighlighter.highlight(dom.document, selectedTech)
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
