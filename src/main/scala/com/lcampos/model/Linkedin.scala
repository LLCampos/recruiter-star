package com.lcampos.model

trait Linkedin {
  val urlSignature: String
}

object Linkedin {
  def fromUrl(url: String): Linkedin =
    if (url.contains(LinkedinBasic.urlSignature))
      LinkedinBasic
    else if (url.contains(LinkedinPremium.urlSignature))
      LinkedinPremium
    else
      Unknown
}

case object LinkedinBasic extends Linkedin {
  val urlSignature: String = "www.linkedin.com/in/"
}
case object LinkedinPremium extends Linkedin {
  val urlSignature: String = "www.linkedin.com/recruiter/profile/"
}

case object Unknown extends Linkedin {
  val urlSignature: String = ""
}
