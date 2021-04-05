package com.lcampos.util

import scala.concurrent.duration.FiniteDuration

object Repeat {

  // Repeat action n times, waiting t between actions.
  def repeat(n: Int, t: FiniteDuration)(action: => Unit) : Unit = {
    action
    (1 to n) foreach { _ =>
      scalajs.js.timers.setTimeout(t) {
        action
      }
    }
  }
}
