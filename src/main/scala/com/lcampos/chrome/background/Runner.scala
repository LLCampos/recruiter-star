package com.lcampos.chrome.background

import chrome.tabs.bindings.Tab.Id
import chrome.tabs.bindings.{ChangeInfo, Tab}
import com.lcampos.chrome.Config
import com.lcampos.chrome.background.models.{Command, Event}
import com.lcampos.chrome.background.services.browser.BrowserNotificationService
import com.lcampos.chrome.background.services.storage.StorageService
import com.lcampos.chrome.common.I18NMessages
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class Runner(commandProcessor: CommandProcessor)(implicit ec: ExecutionContext) {

  def run(): Unit = {
    log("This was run by the background script")
    processExternalMessages()
    notifyOfStatusComplete()
  }

  /**
   * Enables the future-based communication between contexts to the background contexts.
   *
   * Internally, this is done by string-based messages, which we encode as JSON.
   */
  private def processExternalMessages(): Unit = {
    chrome.runtime.Runtime.onMessage.listen { message =>
      message.value.foreach { any =>
        val response = Future
          .fromTry { Try(any.asInstanceOf[String]).flatMap(Command.decode) }
          .map { cmd =>
            log(s"Got command = $cmd")
            cmd
          }
          .flatMap(commandProcessor.process)
          .recover { case NonFatal(ex) =>
            log(s"Failed to process command, error = ${ex.getMessage}")
            Event.CommandRejected(ex.getMessage)
          }
          .map(_.asJson.noSpaces)

        /**
         * NOTE: When replying on futures, the method returning an async response is the only reliable one
         * otherwise, the sender is getting no response, a way to use the async method is to pass a response
         * in case of failures even if that case was already handled with the CommandRejected event.
         */
        message.response(response, "Impossible failure")
      }
    }
  }

  private def notifyOfStatusComplete(): Unit = {
    chrome.tabs.Tabs.onUpdated.listen { case (tabId: Id, changeInfo: ChangeInfo, tab: Tab) =>
      changeInfo.status.toOption match {
        case Some(status) if status == "complete" =>
          val url = tab.url.toOption.getOrElse("")
          chrome.tabs.Tabs.sendMessage(tabId, s"$url | page was reloaded")
        case _ => ()
      }
    }
  }

  private def log(msg: String): Unit = {
    println(s"background: $msg")
  }
}

object Runner {

  def apply(config: Config)(implicit ec: ExecutionContext): Runner = {
    val storage = new StorageService
    val messages = new I18NMessages
    val browserNotificationService = new BrowserNotificationService(messages)
    val commandProcessor = new CommandProcessor(storage, browserNotificationService)

    new Runner(commandProcessor)
  }
}
