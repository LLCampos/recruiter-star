package com.lcampos.chrome.background.services.storage

import io.circe.Json
import io.circe.parser.parse

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js

/**
 * Internal service available to the background context, which allows dealing with the storage local.
 */
private[background] class StorageService(implicit ec: ExecutionContext) {
  import StorageService._
  //import js.JSConverters._

  def save(installedOn: Long): Future[Unit] = {
    val json = s"""{"lastUsedOn": $installedOn}"""
    val dict = js.Dictionary(StorageKey -> js.Any.fromString(json))

    chrome.storage.Storage.local.set(dict)
  }

  def load(): Future[Option[Long]] = {
    chrome.storage.Storage.local
      //.get(key)
      .get(???) // TODO: Fix
      .map(_.asInstanceOf[js.Dictionary[String]])
      .map { dict =>
        val json = dict.getOrElse(StorageKey, "{}")
        parse(json).toOption
          .flatMap(_.as[Json].toOption)
          .flatMap(_.hcursor.downField("lastUsedOn").as[Long].toOption)
      }
  }
}

private[background] object StorageService {

  private val StorageKey = "data"
}
