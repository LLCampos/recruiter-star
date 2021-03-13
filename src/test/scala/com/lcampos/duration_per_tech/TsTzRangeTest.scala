package com.lcampos.duration_per_tech

import org.specs2.mutable.Specification
import java.time.Instant
import scala.concurrent.duration._


class TsTzRangeTest extends Specification {

  "TsTzRangeTest" should {
    "totalDuration" should {
      "calculate total duration for one range" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val range = TsTzRange(now, after5)
        TsTzRange.totalDuration(Seq(range)) must be equalTo 5.seconds
      }

      "calculate total duration for two non overlapping ranges" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val after10 = now.plusSeconds(10)
        val after15 = now.plusSeconds(15)
        val range1 = TsTzRange(now, after5)
        val range2 = TsTzRange(after10, after15)
        TsTzRange.totalDuration(Seq(range1, range2)) must be equalTo 10.seconds
      }

      "calculate total duration for two overlapping ranges" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val after10 = now.plusSeconds(10)
        val after15 = now.plusSeconds(15)
        val range1 = TsTzRange(now, after10)
        val range2 = TsTzRange(after5, after15)
        TsTzRange.totalDuration(Seq(range1, range2)) must be equalTo 15.seconds
      }

      "calculate total duration for two overlapping and one non-overlapping ranges" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val after10 = now.plusSeconds(10)
        val after15 = now.plusSeconds(15)
        val after20 = now.plusSeconds(20)
        val after25 = now.plusSeconds(25)
        val range1 = TsTzRange(now, after10)
        val range2 = TsTzRange(after5, after15)
        val range3 = TsTzRange(after20, after25)
        TsTzRange.totalDuration(Seq(range1, range2, range3)) must be equalTo 20.seconds
      }

      "should be order-insensitive" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val after10 = now.plusSeconds(10)
        val after15 = now.plusSeconds(15)
        val after20 = now.plusSeconds(20)
        val after25 = now.plusSeconds(25)
        val range1 = TsTzRange(now, after10)
        val range2 = TsTzRange(after5, after15)
        val range3 = TsTzRange(after20, after25)
        TsTzRange.totalDuration(Seq(range3, range1, range2)) must be equalTo 20.seconds
      }

      "calculate total duration for three overlapping ranges" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val after10 = now.plusSeconds(10)
        val after15 = now.plusSeconds(15)
        val after25 = now.plusSeconds(25)
        val range1 = TsTzRange(now, after10)
        val range2 = TsTzRange(after5, after15)
        val range3 = TsTzRange(after5, after25)
        TsTzRange.totalDuration(Seq(range1, range2, range3)) must be equalTo 25.seconds
      }
    }

    "duration" should {
      "calculate duration" in {
        val now = Instant.now()
        val after5 = now.plusSeconds(5)
        val range = TsTzRange(now, after5)
        range.duration must be equalTo 5.seconds
      }

      "calculate 0 duration" in {
        val now = Instant.now()
        val range = TsTzRange(now, now)
        range.duration must be equalTo 0.seconds
      }
    }
  }
}
