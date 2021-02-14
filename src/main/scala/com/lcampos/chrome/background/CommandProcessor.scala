package com.lcampos.chrome.background

import com.lcampos.chrome.background.models.{Command, Event}
import com.lcampos.chrome.background.services.browser.BrowserNotificationService
import com.lcampos.chrome.background.services.storage.StorageService

import scala.concurrent.{ExecutionContext, Future}

/**
 * Any command supported by the BackgroundAPI is handled here.
 */
private[background] class CommandProcessor(
    productStorage: StorageService,
    browserNotificationService: BrowserNotificationService
)(implicit ec: ExecutionContext) {

  def process(command: Command): Future[Event] = command match {
    case Command.SendBrowserNotification(title, message) =>
      browserNotificationService.notify(title, message)
      Future.successful(Event.BrowserNotificationSent())
  }
}
