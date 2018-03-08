package Utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


object DateUtils {

  def currentTime(format: String): String = {
    /*
      Arguments : ipFormat
      Return : current date and time
      Functionality : get the current date and time in required format
     */
    val formatter = DateTimeFormat.forPattern(format)
    DateTime.now.toString(formatter)
  }

  def dateAdd(): Unit ={
    /*
      Arguments : date, ipFormat, days to add(both positive and negative)
      Return : new date
      Functionality : both date addition and date subtraction operation needs to be done based on the daya to add argument
     */
  }

  def dateExtract(): Unit ={
    /*
      Arguments : date, ipFormat, field to be extracted
      Retrun : value of the expected field
      Functionality : If field to be extracted is given as min, extract only the minutes for the date and return
        eg : year, month, date, hour, min, sec, dayOfMonth, dayOfYear, dayOfWeek, hourOfDay, minuteOfDay, minuteOfHour, secondOfDay, monthOfYear, getMonth
        import org.joda.time.DateTime
        import org.joda.time.format.DateTimeFormat
        DateTime.parse(date, DateTimeFormat.forPattern(IPformat)).plusDays(days).toString(OPformat)
     */
  }

  def dateFormatter(): Unit ={
    /*
      Arguments : date, ipFormat, opFormat
      Return : date in new format
      Functionality : convert the date from one format to other format
     */
  }


  def datediff(): Unit ={
    /*
      Arguments : date1, date2, date1 ipformat, date2 ipformat, difference in sec/min/hour/days/weeks/months/year
      Return : difference value in required format
      Functionality : find the difference between two dates and returns the value in required format
     */
  }

  def stringToDate(): Unit ={
    /*
      Arguments : date, ipFormat
      Return : date
      Functionality : converts the string to date format
     */

  }

  def runTimeCalculation(): Unit ={
    /*
      Arguments : startdate, enddate, required format
      Retrun : difference in required format
      Functionality : difference between two dates in required format
     */
  }



  def dateToUnixTimestamp(): Unit ={

  }

  def unixTimestampToDate(): Unit ={

  }

  def isLeapYear(): Unit ={

  }

}
