/*
 * Copyright 2004
 * AddVal Technology Inc.
 */
package com.addval.utils.date;

import java.util. Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

/**
 * This class provides "parse" methods for String-to-Date conversion, and "format" methods for Date-to-String conversion.
 * The class supports six distinct formats that cover the most commonly-encountered needs:
 * <table>
 * 		<tr>	<td><em>Format</em></td>		<td><em>Default</em></td>				</tr>
 * 		<tr>	<td>DateFormat</td>				<td>"yyyy-MM-dd"</td>					</tr>
 * 		<tr>	<td>TimeFormatNoSecs</td>		<td>"HH:mm"</td>						</tr>
 * 		<tr>	<td>TimeFormat</td>				<td>"HH:mm:ss"</td>						</tr>
 * 		<tr>	<td>DateTimeFormatNoSecs</td>	<td>"yyyy-MM-dd HH:mm"</td>				</tr>
 * 		<tr>	<td>DateTimeFormat</td>			<td>"yyyy-MM-dd HH:mm:ss"</td>			</tr>
 * 		<tr>	<td>TimestampFormat</td>		<td>"yyyy-MM-dd HH:mm:ss.SSS"</td>		</tr>
 * </table>
 * <p>
 * This class is aimed at making a server-side developer's life easier in the following ways:
 * <ul>
 * 		<li> Simplicity: single-line method calls support the most common Date/String conversion needs.
 * 		<li> Guarantees reliable/consistent conversion behavior: Date<->String conversion results are NOT dependent upon the JVM's Locale or TimeZone.
 * 		<li> Provides a pretty good set of default formats, thus helping to reduce "unintentional variety" of formats used by an application.
 *		<li> Offers pretty good/robust performance: the internal implementation is optimized for multiple method calls with the default formats,
 *			 but the instantiation overhead is low enough that it is reasonable to use this utility even for doing just a single conversion.
 * </ul>
 * This class can be useful in standardizing date/time formats in areas that are considered "internal" to the application,
 * and are not subject to Localization/Internationalization requirements. Good candidate areas for usage include:
 * <ul>
 * 		<li> Within toString() methods, where the output string is to include a Date.
 * 		<li> In constructing Logger messages that are to include an application Date value.
 * 		<li> Application configuration input files, such as Properties or XML files.
 * </ul>
 * The default formats have been selected for the following advantages:
 * <ul>
 * 		<li> "All numbers, no text" ==> no Locale-specific aspects
 * 		<li> Left-padding with zero(s) where needed ==> string is always same length, with elements at same position within string
 * 		<li> Widespread recognition and relative lack of ambiguity.
 * 		<li> Strings have meaningful comparability and sortability
 * </ul>
 * The default formats (and also the TimeZone and ParseLeniency attributes) have "set" methods, so they can be changed.
 * If an application needs to use a different set of formats, or has requirement that the formats be configurable,
 * the application can provide its own Factory for handing out appropriately-configured instances of this utility.
 * <p>
 * One static "format" method is provided for convenience in situations where performance is not a concern.
 * It supports a "useBestFormat" option.
 * <p>
 * Note to developers: all public methods that access any of the internal SimpleDateFormat members and performs operations
 * that can change that SimpleDateFormat member's state (e.g., sets its attributes, or calls its parse or format methods)
 * must be marked "synchronized" in otder to guaranteee multithread-safety of this class.
 */
public class DateFormatUtil
{
	private static final TimeZone 	_DEFAULT_TIMEZONE = TimeZone.getTimeZone( "GMT" );
	private static final boolean	_DEFAULT_PARSE_LENIENCY = false;
	private static final String		_DEFAULT_DATE_FORMAT 			= "yyyy-MM-dd";
	private static final String		_DEFAULT_TIME_NOSECS_FORMAT 	= "HH:mm";
	private static final String		_DEFAULT_TIME_FORMAT 			= _DEFAULT_TIME_NOSECS_FORMAT + ":ss";
	private static final String		_DEFAULT_DATETIME_NOSECS_FORMAT = _DEFAULT_DATE_FORMAT + " " + _DEFAULT_TIME_NOSECS_FORMAT;
	private static final String		_DEFAULT_DATETIME_FORMAT 		= _DEFAULT_DATE_FORMAT + " " + _DEFAULT_TIME_FORMAT;
	private static final String		_DEFAULT_TIMESTAMP_FORMAT 		= _DEFAULT_DATETIME_FORMAT + ".SSS";

	// Set-able instance attributes
	private TimeZone _timeZone;
	private boolean  _parseLeniency;
	private String 	 _dateFormat;
	private String 	 _timeNoSecsFormat;
	private String 	 _timeFormat;
	private String 	 _dateTimeNoSecsFormat;
	private String 	 _dateTimeFormat;
	private String 	 _timestampFormat;

	// Internal instance attributes
	private Calendar 		 _calendar;		// is always a GregorianCalendar; the only thing that can change is the TimeZone that is used by the Calendar
	private SimpleDateFormat _dateSDF;
	private SimpleDateFormat _timeNoSecsSDF;
	private SimpleDateFormat _timeSDF;
	private SimpleDateFormat _dateTimeNoSecsSDF;
	private SimpleDateFormat _dateTimeSDF;
	private SimpleDateFormat _timestampSDF;


	//----------------- static instance and associated static "format" convenience method -----------------

	private static DateFormatUtil _theStaticDefaultInstance = null;

	/**
	 * This static method provides convenient formatting in situations where neither performance
	 * nor the exact format used is critical, such as in toString() methods and/or in generating
	 * debug log messages.
	 * <p>
	 * If useBestFormat=false, TimestampFormat will be used.
	 * <p>
	 * If useBestFormat=true, the format is based upon which fields have non-zero values.
	 */
	public static synchronized String format(Date date, boolean useBestFormat)
	{
		if (_theStaticDefaultInstance == null)
		{
			_theStaticDefaultInstance = new DateFormatUtil();
		}

		if (!useBestFormat)
		{
			return _theStaticDefaultInstance.formatTimestamp(date);						// yyyy-MM-dd HH:mm:ss.SSS
		}
		else if (GMTDateUtil.hasDate(date))
		{
			if (GMTDateUtil.hasSeconds(date))
			{
				return _theStaticDefaultInstance.formatDateTime(date);					// yyyy-MM-dd HH:mm:ss
			}
			else if (GMTDateUtil.hasTime(date))
			{
				return _theStaticDefaultInstance.formatDateTimeNoSecs(date);			// yyyy-MM-dd HH:mm
			}
			else // this is a "date-only" value, omit the "time" portion
			{
				return _theStaticDefaultInstance.formatDate(date);						// yyyy-MM-dd
			}
		}
		else // this is a "time-only" value, omit the "date" portion
		{
			if (GMTDateUtil.hasSeconds(date))
			{
				return _theStaticDefaultInstance.formatTime(date);						// HH:mm:ss
			}
			else
			{
				return _theStaticDefaultInstance.formatTimeNoSecs(date);				// HH:mm
			}
		}
	}


	//----------------- instance construction methods -----------------

	/**
	 * Constructs a new instance with the default characteristics.
	 */
	public DateFormatUtil()
	{
		_timeZone 				= _DEFAULT_TIMEZONE;
		_parseLeniency 			= _DEFAULT_PARSE_LENIENCY;
		_dateFormat 			= _DEFAULT_DATE_FORMAT;
		_timeNoSecsFormat 		= _DEFAULT_TIME_NOSECS_FORMAT;
		_timeFormat 			= _DEFAULT_TIME_FORMAT;
		_dateTimeNoSecsFormat 	= _DEFAULT_DATETIME_NOSECS_FORMAT;
		_dateTimeFormat 		= _DEFAULT_DATETIME_FORMAT;
		_timestampFormat		= _DEFAULT_TIMESTAMP_FORMAT;

		resetInternals();
	}

	// Private method to reset all internal attributes that can be affected by a "set" method call.
	// This "do-all" approach results in a little more work than is strictly necessary, but it simplies the coding for maintaining instance state.
	private void resetInternals()
	{
		_calendar = new GregorianCalendar( _timeZone );
		// Set the SDFs to null; they will lazy-initialize when their "get" is called.
		_dateSDF			= null;
		_timeNoSecsSDF 		= null;
		_timeSDF 			= null;
		_dateTimeNoSecsSDF 	= null;
		_dateTimeSDF 		= null;
		_timestampSDF 		= null;
	}


	// Private method to construct a new SimpleDateFormat, using current _parseLeniency and current _calendar (which itself uses the current _timeZone).
	private SimpleDateFormat newSimpleDateFormat(String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat( format );
		sdf.setCalendar( _calendar );
		sdf.setLenient( _parseLeniency );
		return sdf;
	}


	// Private lazy-initialization methods for getting the default-format SimpleDateFormat objects.

	private SimpleDateFormat getDateSDF()
	{
		if (_dateSDF == null)
		{
			_dateSDF = newSimpleDateFormat(_dateFormat);
		}
		return _dateSDF;
	}

	private SimpleDateFormat getTimeNoSecsSDF()
	{
		if (_timeNoSecsSDF == null)
		{
			_timeNoSecsSDF = newSimpleDateFormat(_timeNoSecsFormat);
		}
		return _timeNoSecsSDF;
	}

	private SimpleDateFormat getTimeSDF()
	{
		if (_timeSDF == null)
		{
			_timeSDF = newSimpleDateFormat(_timeFormat);
		}
		return _timeSDF;
	}

	private SimpleDateFormat getDateTimeNoSecsSDF()
	{
		if (_dateTimeNoSecsSDF == null)
		{
			_dateTimeNoSecsSDF = newSimpleDateFormat(_dateTimeNoSecsFormat);
		}
		return _dateTimeNoSecsSDF;
	}

	private SimpleDateFormat getDateTimeSDF()
	{
		if (_dateTimeSDF == null)
		{
			_dateTimeSDF = newSimpleDateFormat(_dateTimeFormat);
		}
		return _dateTimeSDF;
	}

	private SimpleDateFormat getTimestampSDF()
	{
		if (_timestampSDF == null)
		{
			_timestampSDF = newSimpleDateFormat(_timestampFormat);
		}
		return _timestampSDF;
	}


	//----------------- instance get/set methods -----------------

	/**
	 * Returns this instance's current default TimeZone used in parse/format methods.  The default TimeZone is GMT.
	 */
	public TimeZone getTimeZone ()
	{
		return _timeZone;
	}

	/**
	 * Sets this instance's default TimeZone.  The default TimeZone is GMT.
	 * The input timeZoneID string must be of the form used by java.util.TimeZone.getTimeZone();
	 * e.g. "GMT", "PST", "America/Los_Angeles", or "GMT-8:00".
	 * If the specified string is null or empty after trimming, the default is restored.
	 * If TimeZone.getTimeZone() does not recognize/support the timeZoneID, GMT will be returned.
	 */
	public synchronized void setTimeZone (String timeZoneID)
	{
		if (timeZoneID == null || timeZoneID.trim().equals(""))
		{
			_timeZone = _DEFAULT_TIMEZONE;
		}
		else
		{
			_timeZone = TimeZone.getTimeZone(timeZoneID);
		}
		resetInternals();
	}

	/**
	 * Sets this instance's default TimeZone.  The default TimeZone is GMT.
	 * If the specified TimeZone is null, the default is restored.
	 */
	public synchronized void setTimeZone (TimeZone timeZone)
	{
		if (timeZone == null)
		{
			_timeZone = _DEFAULT_TIMEZONE;
		}
		else
		{
			_timeZone = timeZone;
		}
		resetInternals();
	}

	/**
	 * Returns whether "parse" methods are allowed to be "lenient"; i.e., will attempt to process value strings that
	 * do not exactly match the format specification.  The default is false.
	 */
	public boolean getLenientParsing ()
	{
		return _parseLeniency;
	}

	/**
	 * Sets whether "parse" methods are allowed to be "lenient"; i.e., will attempt to process value strings that
	 * do not exactly match the format specification.  The default is false.
	 */
	public synchronized void setLenientParsing (boolean parseLeniency)
	{
		_parseLeniency = parseLeniency;
		resetInternals();
	}

	/**
	 * Returns this instance's current default DateFormat.  The initial default is "yyyy-MM-dd".
	 */
	public String getDateFormat ()
	{
		return _dateFormat;
	}

	/**
	 * Sets this instance's default DateFormat to the specified value.  The initial default is "yyyy-MM-dd".
	 * If the specified string is null or empty after trimming, the default is restored.
	 */
	public synchronized void setDateFormat (String dateFormat)
	{
		if (dateFormat == null || dateFormat.trim().equals(""))
		{
			_dateFormat = _DEFAULT_DATE_FORMAT;
		}
		else
		{
			_dateFormat = dateFormat;
		}
		resetInternals();
	}

	/**
	 * Returns this instance's current default TimeNoSecsFormat.  The initial default is "HH:mm".
	 */
	public String getTimeNoSecsFormat ()
	{
		return _timeNoSecsFormat;
	}

	/**
	 * Sets this instance's default TimeNoSecsFormat to the specified value.  The initial default is "HH:mm".
	 * If the specified string is null or empty after trimming, the default is restored.
	 */
	public synchronized void setTimeNoSecsFormat (String timeNoSecsFormat)
	{
		if (timeNoSecsFormat == null || timeNoSecsFormat.trim().equals(""))
		{
			_timeNoSecsFormat = _DEFAULT_TIME_NOSECS_FORMAT;
		}
		else
		{
			_timeNoSecsFormat = timeNoSecsFormat;
		}
		resetInternals();
	}

	/**
	 * Returns this instance's current default TimeFormat.  The initial default is "HH:mm:ss".
	 */
	public String getTimeFormat ()
	{
		return _timeFormat;
	}

	/**
	 * Sets this instance's default TimeFormat to the specified value.  The initial default is "HH:mm:ss".
	 * If the specified string is null or empty after trimming, the default is restored.
	 */
	public synchronized void setTimeFormat (String timeFormat)
	{
		if (timeFormat == null || timeFormat.trim().equals(""))
		{
			_timeFormat = _DEFAULT_TIME_FORMAT;
		}
		else
		{
			_timeFormat = timeFormat;
		}
		resetInternals();
	}

	/**
	 * Returns this instance's current default DateTimeNoSecsFormat.  The initial default is "yyyy-MM-dd HH:mm".
	 */
	public String getDateTimeNoSecsFormat ()
	{
		return _dateTimeNoSecsFormat;
	}

	/**
	 * Sets this instance's default DateTimeNoSecsFormat to the specified value.  The initial default is "yyyy-MM-dd HH:mm".
	 * If the specified string is null or empty after trimming, the default is restored.
	 */
	public synchronized void setDateTimeNoSecsFormat (String dateTimeNoSecsFormat)
	{
		if (dateTimeNoSecsFormat == null || dateTimeNoSecsFormat.trim().equals(""))
		{
			_dateTimeNoSecsFormat = _DEFAULT_DATETIME_NOSECS_FORMAT;
		}
		else
		{
			_dateTimeNoSecsFormat = dateTimeNoSecsFormat;
		}
		resetInternals();
	}

	/**
	 * Returns this instance's current default DateTimeFormat.  The initial default is "yyyy-MM-dd HH:mm:ss".
	 */
	public String getDateTimeFormat ()
	{
		return _dateTimeFormat;
	}

	/**
	 * Sets this instance's default DateTimeFormat to the specified value.  The initial default is "yyyy-MM-dd HH:mm:ss".
	 * If the specified string is null or empty after trimming, the default is restored.
	 */
	public synchronized void setDateTimeFormat (String dateTimeFormat)
	{
		if (dateTimeFormat == null || dateTimeFormat.trim().equals(""))
		{
			_dateTimeFormat = _DEFAULT_DATETIME_FORMAT;
		}
		else
		{
			_dateTimeFormat = dateTimeFormat;
		}
		resetInternals();
	}

	/**
	 * Returns this instance's current default TimestampFormat.  The initial default is "yyyy-MM-dd HH:mm:ss.SSS".
	 */
	public String getTimestampFormat ()
	{
		return _timestampFormat;
	}

	/**
	 * Sets this instance's default TimestampFormat to the specified value.  The initial default is "yyyy-MM-dd HH:mm:ss.SSS".
	 * If the specified string is null or empty after trimming, the default is restored.
	 */
	public synchronized void setTimestampFormat (String timestampFormat)
	{
		if (timestampFormat == null || timestampFormat.trim().equals(""))
		{
			_timestampFormat = _DEFAULT_TIMESTAMP_FORMAT;
		}
		else
		{
			_timestampFormat = timestampFormat;
		}
		resetInternals();
	}


	//----------------- instance "format" methods -----------------

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using the specified format.
	 * <p>
	 * This method constructs a new SimpleDateFormat each time, so if the caller needs to
	 * perform many conversions using the same format, it is more efficient to use one of the "set<type>Format" methods
	 * to set the desired format, and then use the corresponding "format<type>" method.

	 */
	public synchronized String format(Date date, String specifiedFormat)
	{
		return newSimpleDateFormat(specifiedFormat).format(date);
	}

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using this instance's current default DateFormat.
	 */
	public synchronized String formatDate(Date date)
	{
		return getDateSDF().format(date);
	}

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using this instance's current default TimeNoSecsFormat.
	 */
	public synchronized String formatTimeNoSecs(Date date)
	{
		return getTimeNoSecsSDF().format(date);
	}

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using this instance's current default TimeFormat.
	 */
	public synchronized String formatTime(Date date)
	{
		return getTimeSDF().format(date);
	}

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using this instance's current default DateTimeNoSecs format.
	 */
	public synchronized String formatDateTimeNoSecs(Date date)
	{
		return getDateTimeNoSecsSDF().format(date);
	}

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using this instance's current default DateTime format.
	 */
	public synchronized String formatDateTime(Date date)
	{
		return getDateTimeSDF().format(date);
	}

	/**
	 * Format the specified java.util.Date (or any subclass, such as java.sql.Date, java.sql.Time, java.sql.Timestamp),
	 * using this instance's current default Timestamp format.
	 */
	public synchronized String formatTimestamp(Date date)
	{
		return getTimestampSDF().format(date);
	}


	//----------------- instance "parse" methods -----------------

	/**
	 * Parse the specified string using the specified format, returns a java.util.Date.
	 * <p>
	 * This method constructs a new SimpleDateFormat each time, so if the caller needs to
	 * perform many conversions using the same format, it is more efficient to use one of the "set<type>Format" methods
	 * to set the desired format, and then use the corresponding "parse<type>" method.
	 */
	public synchronized java.util.Date parse(String aString, String specifiedFormat) throws java.text.ParseException
	{
		return newSimpleDateFormat(specifiedFormat).parse(aString);
	}

	/**
	 * Parse the specified date string using this instance's current default DateFormat, returns a java.util.Date.
	 */
	public synchronized java.util.Date parseDate(String dateString) throws java.text.ParseException
	{
		return getDateSDF().parse(dateString);
	}

	/**
	 * Parse the specified time string using this instance's current default TimeFormatNoSecs, returns a java.util.Date.
	 */
	public synchronized java.util.Date parseTimeNoSecs(String timeString) throws java.text.ParseException
	{
		return getTimeNoSecsSDF().parse(timeString);
	}

	/**
	 * Parse the specified time string using this instance's current default TimeFormat, returns a java.util.Date.
	 */
	public synchronized java.util.Date parseTime(String timeString) throws java.text.ParseException
	{
		return getTimeSDF().parse(timeString);
	}

	/**
	 * Parse the specified date/time string using this instance's current default DateTimeNoSecsFormat, returns a java.util.Date.
	 */
	public synchronized java.util.Date parseDateTimeNoSecs(String dateTimeString) throws java.text.ParseException
	{
		return getDateTimeNoSecsSDF().parse(dateTimeString);
	}

	/**
	 * Parse the specified date/time string using this instance's current default DateTimeFormat, returns a java.util.Date.
	 */
	public synchronized java.util.Date parseDateTime(String dateTimeString) throws java.text.ParseException
	{
		return getDateTimeSDF().parse(dateTimeString);
	}

	/**
	 * Parse the specified timestamp string using this instance's current default TimestampFormat, returns a java.util.Date.
	 */
	public synchronized java.util.Date parseTimestamp(String timestampString) throws java.text.ParseException
	{
		return getTimestampSDF().parse(timestampString);
	}

}