package com.lcampos.duration_per_tech

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.duration.Duration
import scala.math.Ordering.Implicits._

// Adapted from https://siawyoung.com/tstzrange
// Got authorization from the author that I could use it

case class TsTzRange(
  start: Instant,
  end: Instant,
  startInclusive: Boolean = true, 
  endInclusive: Boolean = true) {

  require(start <= end)

  def zero: Boolean = this.start == this.end && !this.startInclusive || !this.endInclusive

  private def touchingEdges(left: TsTzRange, right: TsTzRange): Boolean =
    left.end == right.start && left.endInclusive && right.startInclusive

  // Checks if the end of bounded2 lies anywhere within bounded1
  private def boundedCheckLeftOverlap(bounded1: TsTzRange, bounded2: TsTzRange): Boolean =
    bounded2.end > bounded1.start && bounded2.end <= bounded1.end

  // Checks if the start of bounded2 lies anywhere within bounded1
  private def boundedCheckRightOverlap(bounded1: TsTzRange, bounded2: TsTzRange): Boolean =
    bounded2.start >= bounded1.start && bounded2.start < bounded1.end

  // boundedCheck assumes both intervals are bounded
  private def boundedCheck(bounded1: TsTzRange, bounded2: TsTzRange): Boolean =
    boundedCheckLeftOverlap(bounded1, bounded2) ||
      boundedCheckRightOverlap(bounded1, bounded2) ||
      touchingEdges(bounded2, bounded1) ||
      touchingEdges(bounded1, bounded2)

  def overlaps(other: TsTzRange): Boolean = {
    !(this.zero || other.zero) && boundedCheck(this, other)
  }

  def duration: Duration =
    Duration.fromNanos(start.until(end, ChronoUnit.NANOS))

  def plus(other: TsTzRange): TsTzRange =
    TsTzRange(this.start, other.end, this.startInclusive, other.endInclusive)
}

object TsTzRange {
  private def joinOverlappingRanges(ranges: Seq[TsTzRange]): Seq[TsTzRange] =
    ranges.sortBy(_.start).foldLeft(Seq.empty[TsTzRange])((ranges, nextRange) =>
      ranges.lastOption match {
        case Some(previousRange) =>
          if (previousRange.overlaps(nextRange))
            ranges.init.appended(previousRange.plus(nextRange))
          else
            ranges.appended(nextRange)
        case None =>
          ranges.appended(nextRange)
      }
    )

  def totalDuration(ranges: Seq[TsTzRange]): Duration = {
    joinOverlappingRanges(ranges).map(_.duration).reduce((d1, d2) => d1.plus(d2))
  }
}
