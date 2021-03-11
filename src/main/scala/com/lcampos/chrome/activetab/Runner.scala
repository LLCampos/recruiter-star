package com.lcampos.chrome.activetab

import com.lcampos.chrome.Config
import com.lcampos.chrome.background.BackgroundAPI
import com.lcampos.chrome.common.I18NMessages
import com.lcampos.model.LinkedinProfileManipulator
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.js.timers._

class Runner(config: ActiveTabConfig, backgroundAPI: BackgroundAPI, messages: I18NMessages) {

  def run(): Unit = {
    chrome.runtime.Runtime.onMessage.listen { msg =>
      msg.value match {
        case Some(v: String) if v.contains("page was reloaded") =>
          setTimeout(100.milli) {
            LinkedinProfileManipulator.fromUrl(v) match {
              case Some(manipulator) => manipulator.addDurationPerTech(dom.document) match {
                case Left(err) => println(err)
                case Right(_) => ()
              }
              case None => ()
            }
          }
        case _ => ()
      }
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
