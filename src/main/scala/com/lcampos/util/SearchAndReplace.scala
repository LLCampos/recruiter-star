package com.lcampos.util

import org.scalajs.dom.{Element, document}

object SearchAndReplace {

  def replace(pattern: String, replacement: String, target: Element): Unit =
    target.innerHTML = target.innerHTML.replaceAll(pattern, replacement)

  def replace(pattern: String, replacement: String, targets: List[Element]): Unit =
    targets.foreach(t => replace(pattern, replacement, t))
}
