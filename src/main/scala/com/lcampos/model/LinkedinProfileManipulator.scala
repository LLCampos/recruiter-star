package com.lcampos.model

import com.lcampos.duration_per_tech
import org.scalajs.dom.Document

trait LinkedinProfileManipulator {
  val urlSignature: String
  def addDurationPerTech(profile: Document): Either[String, Unit]
}

object LinkedinProfileManipulator {
  def fromUrl(url: String): LinkedinProfileManipulator =
    if (url.contains(LinkedinProfileManipulatorBasic.urlSignature))
      LinkedinProfileManipulatorBasic
    else if (url.contains(LinkedinProfileManipulatorPremium.urlSignature))
      LinkedinProfileManipulatorPremium
    else
      NoOpManipulator
}

case object LinkedinProfileManipulatorBasic extends LinkedinProfileManipulator {
  val urlSignature: String = "www.linkedin.com/in/"
  def addDurationPerTech(profile: Document): Either[String, Unit] =
    duration_per_tech.PageManipulator.addDurationPerTechToPage(profile)
}
case object LinkedinProfileManipulatorPremium extends LinkedinProfileManipulator {
  val urlSignature: String = "www.linkedin.com/recruiter/profile/"
  def addDurationPerTech(profile: Document): Either[String, Unit] = ???
}

case object NoOpManipulator extends LinkedinProfileManipulator {
  val urlSignature: String = ""
  def addDurationPerTech(profile: Document): Either[String, Unit] = Right(())
}
