package com.lcampos.model

trait LinkedinProfileManipulator {
  val urlSignature: String
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
}
case object LinkedinProfileManipulatorPremium extends LinkedinProfileManipulator {
  val urlSignature: String = "www.linkedin.com/recruiter/profile/"
}

case object NoOpManipulator extends LinkedinProfileManipulator {
  val urlSignature: String = ""
}
