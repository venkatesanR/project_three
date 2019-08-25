/*
 * Copyright 2004
 * AddVal Technology Inc.
 */
package com.techmania.common.util.date;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * This class provides methods for obtaining information about a given java.util.Date, and
 * for doing frequently-needed manipulations involving dates.  All methods are static.
 * <p>
 * An explicit goal for this class is to provide methods whosee behaviour is independent of the
 * JVM's default TimeZone/Locale settings.  To achieve this, this class treats all input Dates
 * as if they were GMT date/times, under a GregorianCalendar.
 * <p>
 * This class is designed more for convenience than high performance.  Most of the methods
 * instantiate a new java.util.Calendar to support the operation.  In performance-critical
 * situtations where you need to perform multiple operations on a single Date, it would be more
 * efficent to make direct use of GregorianCalendar.
 */
public class GMTDateUtil
{
	private static final TimeZone _GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

	public static final long MILLISECS_PER_SEC 		= 1000;
	public static final long MILLISECS_PER_MINUTE 	= 60 * MILLISECS_PER_SEC;
	public static final long MILLISECS_PER_HOUR 	= 60 * MILLISECS_PER_MINUTE;
	public static final long MILLISECS_PER_DAY 		= 24 * MILLISECS_PER_HOUR;

	/**
	 * Returns a new instance of GregorianCalendar, with TimeZone set to GMT, and to the specified date.
	 * <p>
	 * The date is set via calendar.setTimeInMillis(date.getTime()) -- NOT via calendar.setTime(date) --
	 * to ensure that the GMT calendar does interpret the Date using the JVM's default TimeZone.
	 * (Note: am not sure whether different behaviour could indeed result; just trying to make sure.)
	 */
	public static Calendar newGMTCalendarForDate( Date date )
	{
		Calendar calendar = new GregorianCalendar( _GMT_TIMEZONE );
		calendar.setTimeInMillis( date.getTime() );
		return calendar;
	}

	/**
	 * Returns the year, as an int.
	 */
	public static int year(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.YEAR);
	}

	/**
	 * Returns the month, as an int. 1=Jan, 2=Feb, ... 12=Dec.
	 */
	public static int month(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.MONTH) + 1;  	// Note: java.util.Calendar uses the convention 0=Jan, 1=Feb, ..., 11=Dec
	}

	/**
	 * Returns the day (i.e., the day of month), as an int.
	 */
	public static int day(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Returns the hour (0 to 23), as an int.
	 */
	public static int hour(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Returns the minute (0 to 59), as an int.
	 */
	public static int minute(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.MINUTE);
	}

	/**
	 * Returns the second (0 to 59), as an int.
	 */
	public static int second(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.SECOND);
	}

	/**
	 * Returns the millisecond (0 to 999), as an int.
	 */
	public static int millisecond(Date date)
	{
		return newGMTCalendarForDate(date).get(Calendar.MILLISECOND);
	}

	/**
	 * Returns true if the date is anything other than the "epoch" date (January 1, 1970 GMT).
	 * This reflects the convention in java.sql.Time that "time-only" values must always have the epoch date as the date portion.
	 */
	public static boolean hasDate(Date date)
	{
		long originalMillisecs = date.getTime();
		long timePortionOnlyMillisecs = originalMillisecs % MILLISECS_PER_DAY;
		return originalMillisecs != timePortionOnlyMillisecs;
	}

	/**
	 * Returns true if the hour, minute, or second is non-zero.
	 * <p>
	 * The millisecond portion is ignored, since java.sql.Timestamp will typically have a non-zero millisecond value when returning
	 * a DATE/DATETIME from the database, even if that DATE/DATETIME has zero for hour, minute, and second.
	 */
	public static boolean hasTime(Date date)
	{
		Calendar calendar = newGMTCalendarForDate(date);
		return calendar.get(Calendar.HOUR_OF_DAY)!=0 || calendar.get(Calendar.MINUTE)!=0 || calendar.get(Calendar.SECOND)!=0;
	}

	/**
	 * Returns true if the second is non-zero.
	 */
	public static boolean hasSeconds(Date date)
	{
		return ( GMTDateUtil.second(date) != 0 );
	}

	/**
	 * Returns the specified Date, with the "date" portions "zeroed-out" (by setting to the epoch date, January 1, 1970).
	 */
	public static Date clearDatePortion( Date date )
	{
		Calendar calendar = newGMTCalendarForDate(date);
		calendar.set( Calendar.YEAR, 1970 );
		calendar.set( Calendar.MONTH, 0 );
		calendar.set( Calendar.DATE, 1 );
		return new Date( calendar.getTimeInMillis() );
	}

	/**
	 * Returns the specified Date, with the "time" portions zeroed-out.
	 */
	public static Date clearTimePortion( Date date )
	{
		Calendar calendar = newGMTCalendarForDate(date);
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set( Calendar.MINUTE, 0 );
		calendar.set( Calendar.SECOND, 0 );
		calendar.set( Calendar.MILLISECOND, 0 );
		return new Date( calendar.getTimeInMillis() );
	}

	/**
	 * Returns the Date that results from adding numberOfDays to the specified date.
	 * GregorianCalendar's .add(Calendar.DATE,numberOfDays) is used; numberOfDays can be negative.
	 */
	public static Date addDays( Date date, int numberOfDays )
	{
		Calendar calendar = newGMTCalendarForDate(date);
		calendar.add( Calendar.DATE, numberOfDays );
		return new Date( calendar.getTimeInMillis() );
	}

	/**
	 * Returns the Date that results from adding numberOfMinutes to the specified date.
	 * GregorianCalendar's .add(Calendar.MINUTE,numberOfMinutes) is used; numberOfMinutes can be negative.
	 */
	public static Date addMinutes( Date date, int numberOfMinutes )
	{
		Calendar calendar = newGMTCalendarForDate(date);
		calendar.add( Calendar.MINUTE, numberOfMinutes );
		return new Date( calendar.getTimeInMillis() );
	}

	/**
	 * Returns the GMT Date that results from subtracting numberOfMinutes from the specified localDate.
	 */
	public static Date convertLocalToGMT( Date localDate, int gmtOffsetMinutes )
	{
		return addMinutes( localDate, -gmtOffsetMinutes);
	}

	/**
	 * Returns the Local Date that results from adding numberOfMinutes from the specified gmtDate.
	 */
	public static Date convertGMTToLocal( Date gmtDate, int gmtOffsetMinutes )
	{
		return addMinutes( gmtDate, +gmtOffsetMinutes);
	}

	/**
	 * Returns the difference between the two dates, rounded to the whole number of days.
	 * This is intended for use with true GMT dates, in which case the answer is always correct.
	 * If both dates are "local" (to the same unspecified location), the result could be off
	 * by one, since this method does not take possible DST change event into account.
	 * (Don't even THINK about using this method if the two dates are local to two different locations!)
	 */
	public static int daysBetween( Date date1, Date date2 )
	{
		long millisecsApart = date2.getTime() - date1.getTime();
		return (int) Math.round( (double)(millisecsApart) / (double)(MILLISECS_PER_DAY) );
	}

	/**
	 * Returns true if testDate is in the range [fromDate, toDate] (inclusive).
	 * <p>
	 * Behavior corresponds to SQL "testDate BETWEEN fromDate AND toDate".
	 */
	public static boolean isBetween( Date testDate, Date fromDate, Date toDate )
	{
		return testDate.compareTo(fromDate)>=0 && testDate.compareTo(toDate)<=0;
	}

}
