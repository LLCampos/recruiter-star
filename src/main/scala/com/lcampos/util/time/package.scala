package com.lcampos.util

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, Year, YearMonth, ZoneId, ZoneOffset}
import scala.util.{Failure, Success, Try}

package object time {
  def parseDateStrToInstant(dateString: String): Instant =
    LocalDate.parse(dateString).atStartOfDay().toInstant(ZoneOffset.UTC)

  def currentInstantYearMonth: Instant =
    YearMonth.now(ZoneId.of("UTC")).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)

  def getInstantYearMonth(yearMonthStr: String, formatter: DateTimeFormatter): Instant =
    YearMonth.parse(yearMonthStr, formatter).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)

  def toInstantRange(rangeStr: String, separator: String, dateMonthFormatter: DateTimeFormatter): InstantRange = {
    val instants = rangeStr
      .split(separator)
      .map(dateString => {
        if (dateString == "Present") {
          currentInstantYearMonth
        } else {
          Try(getInstantYearMonth(dateString, dateMonthFormatter)) match {
            case Success(instant) => instant
            case Failure(_) => Year.parse(dateString).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)
          }
        }
      })
    InstantRange(instants.head, instants.last)
  }
}
