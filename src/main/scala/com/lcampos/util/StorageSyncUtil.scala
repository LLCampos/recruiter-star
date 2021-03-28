package com.lcampos.util

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

object StorageSyncUtil {

  def get(implicit ec: ExecutionContext): Future[Map[String, Any]] =
    chrome.storage.Storage.sync.get().map(dicJs =>
      dicJs.values.sliding(2,2).map(v => v.head.asInstanceOf[String] -> v.last).toList.toMap
    )

  def get[A](key: String)(implicit ec: ExecutionContext): Future[Option[A]] =
    get.map(_.get(key).map(_.asInstanceOf[A]))

  def set(key: String, value: Any): Future[Unit] =
    chrome.storage.Storage.sync.set(
      Map(key -> value).asInstanceOf[js.Dictionary[js.Any]]
    )
}
