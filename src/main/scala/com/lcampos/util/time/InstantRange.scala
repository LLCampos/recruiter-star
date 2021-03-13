package com.lcampos.util.time

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.duration.Duration
import scala.math.Ordering.Implicits._

// Adapted from https://siawyoung.com/tstzrange
// Got authorization from the author that I could use it

case class InstantRange(
  start: Instant,
  end: Instant,
  startInclusive: Boolean = true,
  endInclusive: Boolean = true) {

  require(start <= end)

  def zero: Boolean = this.start == this.end && !this.startInclusive || !this.endInclusive

  private def touchingEdges(left: InstantRange, right: InstantRange): Boolean =
    left.end == right.start && left.endInclusive && right.startInclusive

  // Checks if the end of bounded2 lies anywhere within bounded1
  private def boundedCheckLeftOverlap(bounded1: InstantRange, bounded2: InstantRange): Boolean =
    bounded2.end > bounded1.start && bounded2.end <= bounded1.end

  // Checks if the start of bounded2 lies anywhere within bounded1
  private def boundedCheckRightOverlap(bounded1: InstantRange, bounded2: InstantRange): Boolean =
    bounded2.start >= bounded1.start && bounded2.start < bounded1.end

  // boundedCheck assumes both intervals are bounded
  private def boundedCheck(bounded1: InstantRange, bounded2: InstantRange): Boolean =
    boundedCheckLeftOverlap(bounded1, bounded2) ||
      boundedCheckRightOverlap(bounded1, bounded2) ||
      touchingEdges(bounded2, bounded1) ||
      touchingEdges(bounded1, bounded2)

  def overlaps(other: InstantRange): Boolean = {
    !(this.zero || other.zero) && boundedCheck(this, other)
  }

  def duration: Duration =
    Duration.fromNanos(start.until(end, ChronoUnit.NANOS))

  def plus(other: InstantRange): InstantRange =
    InstantRange(this.start, other.end, this.startInclusive, other.endInclusive)
}

object InstantRange {
  private def joinOverlappingRanges(ranges: Seq[InstantRange]): Seq[InstantRange] =
    ranges.sortBy(_.start).foldLeft(Seq.empty[InstantRange])((ranges, nextRange) =>
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

  def totalDuration(ranges: Seq[InstantRange]): Duration = {
    joinOverlappingRanges(ranges).map(_.duration).reduce((d1, d2) => d1.plus(d2))
  }
}
