package com.lcampos.util

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.FiniteDuration

object FutureUtil {
  def delay(duration: FiniteDuration): Future[Unit] = {
    val p = Promise[Unit]()
    scalajs.js.timers.setTimeout(duration) {
      p.success(())
    }
    p.future
  }
}
