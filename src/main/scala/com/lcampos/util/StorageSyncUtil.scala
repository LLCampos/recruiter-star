package com.lcampos.util

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

object StorageSyncUtil {

  def get: Future[js.Dictionary[js.Any]] =
    chrome.storage.Storage.sync.get()

  def get[A](key: String)(implicit ec: ExecutionContext): Future[Option[A]] =
    get.map(_.get(key).map(_.asInstanceOf[A]))

  def add(key: String, value: js.Any)(implicit ec: ExecutionContext): Future[Unit] =
    get.flatMap { currentMap =>
      chrome.storage.Storage.sync.set(
        currentMap.addOne((key, value))
      )
    }
}
