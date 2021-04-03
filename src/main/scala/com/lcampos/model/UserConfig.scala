package com.lcampos.model

import com.lcampos.util.StorageSyncUtil

import scala.concurrent.{ExecutionContext, Future}

object UserConfigKeys {
  val isExtensionActive = "recruiter-star-is-active"
  val selectedTechnologies = "recruiter-star-selected-technologies"
}

case class UserConfig(
  isExtensionActive: Boolean,
  selectedTechnologies: List[String]
)

object UserConfig {

  def load(implicit ec: ExecutionContext): Future[UserConfig] = for {
    isExtensionActive <- StorageSyncUtil.get[Boolean](UserConfigKeys.isExtensionActive).map {
      case Some(v) => v
      case None => true
    }
    selectedTechnologies <- StorageSyncUtil.get[scalajs.js.Array[String]](UserConfigKeys.selectedTechnologies).map {
      case Some(v) => v.toList
      case None => List.empty
    }
  } yield UserConfig(isExtensionActive, selectedTechnologies)


}
