package com.lcampos.model

trait Linkedin {
  val urlSignature: String
}

case object LinkedinBasic extends Linkedin {
  val urlSignature: String = "www.linkedin.com/in/"
}
case object LinkedinPremium extends Linkedin {
  val urlSignature: String = ???
}
