package Utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


object DateUtils {

  def currentTime(format: String): String = {
    val formatter = DateTimeFormat.forPattern(format)
    DateTime.now.toString(formatter)
  }

  def addYear(date: String, year: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusYears(year).toString(OPformat)
  }

  def addMonth(date: String, months: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusMonths(months).toString(OPformat)
  }

  def addWeek(date: String, weeks: Int, IPformat: String, OPformat: String): String ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusWeeks(weeks).toString(OPformat)
  }

  def addDay(date: String, days: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusDays(days).toString(OPformat)
  }

  def addHour(date: String, hour: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusHours(hour).toString(OPformat)
  }

  def addMin(date: String, min: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusMinutes(min).toString(OPformat)
  }

  def addSec(date: String, sec: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusSeconds(sec).toString(OPformat)
  }

  def minusYear(date: String, year: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusYears(year).toString(OPformat)
  }

  def minusMonth(date: String, month: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusMonths(month).toString(OPformat)
  }

  def minusWeek(date: String, week: Int, IPformat: String, OPformat: String): String ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusWeeks(week).toString(OPformat)
  }

  def minusDay(date: String, days: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusDays(days).toString(OPformat)
  }

  def minusHour(date: String, hour: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusHours(hour).toString(OPformat)
  }

  def minusMin(date: String, min: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusMinutes(min).toString(OPformat)
  }

  def minusSec(date: String, sec: Int, IPformat: String, OPformat: String): String = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minusSeconds(sec).toString(OPformat)
  }

  def dayOfMonth(date: String, IPformat: String): Int = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).dayOfMonth().get()
  }

  def dayOfYear(date: String, IPformat: String): Int = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).dayOfYear().get()
  }

  def dayOfWeek(date: String, IPformat: String): Int = {
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).dayOfWeek().get()
  }

  def hourOfDay(date: String, IPformat: String): Int ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).hourOfDay().get()
  }

  def minuteOfDay(date: String, IPformat: String): Int ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minuteOfDay().get()
  }

  def minuteOfHour(date: String, IPformat: String): Int ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).minuteOfHour().get()
  }

  def secondOfDay(date: String, IPformat: String): Int ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).secondOfDay().get()
  }

  def monthOfYear(date: String, IPformat: String): Int ={
    DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).monthOfYear().get()
  }

  def datediff(): Unit ={

  }

  def getMonth(): Unit ={

  }
  //etc
  def stringToDate(): Unit ={

  }

  def runTimeCalculation(): Unit ={

  }

  def dateToUnixTimestamp(): Unit ={

  }

  def unixTimestampToDate(): Unit ={

  }

  def isLeapYear(): Unit ={

  }

  





  def main(args: Array[String]): Unit = {
    dayOfMonth("2017-05-01","yyyy-MM-dd")
  }
}
