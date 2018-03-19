/*
 * Copyright 2004
 * AddVal Technology Inc.
 */
package com.addval.utils.date;

import com.addval.utils.date.xbeans.*;

import java.util.Date;

/**
 * This class provides "toDate" methods for XBean-to-Date conversion, and "to<type>String" methods for Date-to-XBean conversion.
 * The "XBeans" that are supported are those in the package com.addval.utils.date.xbeans.
 * Each of these XBeans relies on a internal formatted String representation of a date, time, date/time, etc.;
 * and each supports an optional int attribute for the GMT offset in minutes.
 * <p>
 * This class uses a com.addval.utils.date.DateFormatUtil to handle the Date-to-String and String-to-Date aspects of conversion.
 * The default formats used in DateFormatUtil and the com.addval.utils.date.xbeans.* XBeans have been specified so as to
 * be identical, but this is not required: the "toDate" (XBean-to-Date) methods will work fine for any non-default format
 * attribute that may appear in an XBean.  However, the "to<type>String" (Date-to-XBean) methods will always set the
 * XBean's format attribute to the corresponding DateFormatUtil default format.
 * <p>
 * No GMT/local timezone conversion is ever performed by this class, even if GMT offset information has been specified.
 * GMT offset is simplied "associated" with the specified date/time.  It is up to the calling application to perform
 * any desired conversion between GMT and Local, since only the calling application knows (a) whether the value is
 * currently GMT or local, and (b) whether some other portion of the application will/will not be performing timezone conversion.
 */
public class DateXBeanConverter
{
	private static final int _MILLISECS_IN_MINUTE 	= 60 * 1000;
	private static final int _MILLISECS_IN_HOUR 	= 60 * _MILLISECS_IN_MINUTE;
	private static final int _MILLISECS_IN_DAY 		= 24 * _MILLISECS_IN_HOUR;

	private DateFormatUtil _dateFormatUtil;

	/**
	 * Constructor.  Establishes a DateFormatUtil with default characteristics.
	 */
	public DateXBeanConverter()
	{
		_dateFormatUtil = new DateFormatUtil();
	}

	/**
	 * Provides access to the DateFormatUtil
	 */
	public DateFormatUtil getDateFormatUtil()
	{
		return _dateFormatUtil;
	}

	/**
	 * Allows replacement of the default DateFormatUtil with the specified DateFormatUtil.
	 * <p>
	 * Note that the specified dateFormatUtil's TimeZone is set to "GMT" by this method,
	 * thus (possibly) altering the input object.  This action is necessary to avoid the possibility
	 * of non-GMT-TimeZone induced "conversion" when parsing/formatting.  This class itself performs
	 * all requested conversions, using only explicitly-specified values for gmtOffsetMinutes.
	 */
	public void setDateFormatUtil(DateFormatUtil dateFormatUtil)
	{
		_dateFormatUtil = dateFormatUtil;
	}


	//------------------------------------------- private methods ------------------------------------------

	/**
	 * When numMinutes represents the same-signed minutes for a timezone specification such as "GMT+0800",
	 * then this operation represents a "GMT-to-local" conversion.
	 */
	public Date addMinutes( Date date, long numMinutes )
	{
		Date retDate = null;
		if (date != null)
		{
			retDate = new Date( date.getTime() + (numMinutes*_MILLISECS_IN_MINUTE) );
		}
		return retDate;
	}

	/**
	 * When numMinutes represents the same-signed minutes for a timezone specification such as "GMT+0800",
	 * then this operation represents a "local-to-GMT" conversion.
	 */
	public Date subtractMinutes( Date date, long numMinutes )
	{
		return addMinutes(date, -numMinutes);
	}


	//------------------------------------------- toDate methods ------------------------------------------

	/**
	 * Converts the specified com.addval.utils.date.xbeans.DateString to the equivalent java.util.Date.
	 * <p>
 	 * No GMT/local conversion is ever performed, even if GMTOffsetMinutes is non-nil.
	 */
	public Date toDate(com.addval.utils.date.xbeans.DateString xDateString) throws java.text.ParseException
	{
		Date retDate = null;
		if (xDateString != null && xDateString.getStringValue() != null)
		{
			// If xDateString's format matches the _dateFormatUtil's default DateFormat, use the (faster) parseDate method;
			// otherwise use the (slower) specified-format parse method.

			String xFormat = xDateString.getFormat();
			if (xFormat.equals( _dateFormatUtil.getDateFormat() ))
			{
				retDate = _dateFormatUtil.parseDate( xDateString.getStringValue() );
			}
			else
			{
				retDate = _dateFormatUtil.parse( xDateString.getStringValue(), xFormat );
			}
		}
		return retDate;
	}

	/**
	 * Converts the specified com.addval.utils.date.xbeans.TimeNoSecsString to the equivalent java.util.Date.
	 * <p>
 	 * No GMT/local conversion is ever performed, even if GMTOffsetMinutes is non-nil.
	 */
	public Date toDate(com.addval.utils.date.xbeans.TimeNoSecsString xString) throws java.text.ParseException
	{
		Date retDate = null;
		if (xString != null && xString.getStringValue() != null)
		{
			// If xString's format matches the _dateFormatUtil's default TimeNoSecsFormat, use the (faster) parseTimeNoSecs method;
			// otherwise use the (slower) specified-format parse method.

			String xFormat = xString.getFormat();
			if (xFormat.equals( _dateFormatUtil.getTimeNoSecsFormat() ))
			{
				retDate = _dateFormatUtil.parseTimeNoSecs( xString.getStringValue() );
			}
			else
			{
				retDate = _dateFormatUtil.parse( xString.getStringValue(), xFormat );
			}
		}
		return retDate;
	}

	/**
	 * Converts the specified com.addval.utils.date.xbeans.TimeString to the equivalent java.util.Date.
	 * <p>
 	 * No GMT/local conversion is ever performed, even if GMTOffsetMinutes is non-nil.
	 */
	public Date toDate(com.addval.utils.date.xbeans.TimeString xString) throws java.text.ParseException
	{
		Date retDate = null;
		if (xString != null && xString.getStringValue() != null)
		{
			// If xString's format matches the _dateFormatUtil's default TimeFormat, use the (faster) parseTime method;
			// otherwise use the (slower) specified-format parse method.

			String xFormat = xString.getFormat();
			if (xFormat.equals( _dateFormatUtil.getTimeFormat() ))
			{
				retDate = _dateFormatUtil.parseTime( xString.getStringValue() );
			}
			else
			{
				retDate = _dateFormatUtil.parse( xString.getStringValue(), xFormat );
			}

		}
		return retDate;
	}

	/**
	 * Converts the specified com.addval.utils.date.xbeans.DateTimeNoSecsString to the equivalent java.util.Date.
	 * <p>
 	 * No GMT/local conversion is ever performed, even if GMTOffsetMinutes is non-nil.
	 */
	public Date toDate(com.addval.utils.date.xbeans.DateTimeNoSecsString xString) throws java.text.ParseException
	{
		Date retDate = null;
		if (xString != null && xString.getStringValue() != null)
		{
			// If xString's format matches the _dateFormatUtil's default DateTimeNoSecsFormat, use the (faster) parseDateTimeNoSecs method;
			// otherwise use the (slower) specified-format parse method.

			String xFormat = xString.getFormat();
			if (xFormat.equals( _dateFormatUtil.getDateTimeNoSecsFormat() ))
			{
				retDate = _dateFormatUtil.parseDateTimeNoSecs( xString.getStringValue() );
			}
			else
			{
				retDate = _dateFormatUtil.parse( xString.getStringValue(), xFormat );
			}
		}
		return retDate;
	}

	/**
	 * Converts the specified com.addval.utils.date.xbeans.DateTimeString to the equivalent java.util.Date.
	 * <p>
 	 * No GMT/local conversion is ever performed, even if GMTOffsetMinutes is non-nil.
	 */
	public Date toDate(com.addval.utils.date.xbeans.DateTimeString xString) throws java.text.ParseException
	{
		Date retDate = null;
		if (xString != null && xString.getStringValue() != null)
		{
			// If xString's format matches the _dateFormatUtil's default DateTimeFormat, use the (faster) parseDateTime method;
			// otherwise use the (slower) specified-format parse method.

			String xFormat = xString.getFormat();
			if (xFormat.equals( _dateFormatUtil.getDateTimeFormat() ))
			{
				retDate = _dateFormatUtil.parseDateTime( xString.getStringValue() );
			}
			else
			{
				retDate = _dateFormatUtil.parse( xString.getStringValue(), xFormat );
			}

		}
		return retDate;
	}

	/**
	 * Converts the specified com.addval.utils.date.xbeans.TimestampString to the equivalent java.util.Date.
	 * <p>
 	 * No GMT/local conversion is ever performed, even if GMTOffsetMinutes is non-nil.
	 */
	public Date toDate(com.addval.utils.date.xbeans.TimestampString xString) throws java.text.ParseException
	{
		Date retDate = null;
		if (xString != null && xString.getStringValue() != null)
		{
			// If xString's format matches the _dateFormatUtil's default TimestampFormat, use the (faster) parseTimestamp method;
			// otherwise use the (slower) specified-format parse method.

			String xFormat = xString.getFormat();
			if (xFormat.equals( _dateFormatUtil.getTimestampFormat() ))
			{
				retDate = _dateFormatUtil.parseTimestamp( xString.getStringValue() );
			}
			else
			{
				retDate = _dateFormatUtil.parse( xString.getStringValue(), xFormat );
			}
		}
		return retDate;
	}



	//------------------------------------------- to<type>String methods ------------------------------------------

	/**
	 * Converts the specified java.util.Date to an equivalent com.addval.utils.date.xbeans.DateString, using the
	 * DateFormatUtil's current default DateFormat.
	 * <p>
 	 * If setGMTOffsetMinutes=true, the returned object's GMTOffsetMinutes attribute will be set to the
 	 * value specified by the gmtOffsetMinutes argument.  Otherwise the returned object's GMTOffsetMinutes attribute will
 	 * be nil.
	 */
	public com.addval.utils.date.xbeans.DateString toDateString(Date date, boolean setGMTOffsetMinutes, int gmtOffsetMinutes)
	{
		com.addval.utils.date.xbeans.DateString xString = null;
		if (date != null)
		{
			// Format the date into a string
			String formattedString = _dateFormatUtil.formatDate( date );

			// Construct the XmlObject
			xString = com.addval.utils.date.xbeans.DateString.Factory.newInstance();
			xString.setStringValue( formattedString );
			if (setGMTOffsetMinutes)
			{
				xString.setGMTOffsetMinutes( gmtOffsetMinutes );
			}
		}
		return xString;
	}

	/**
	 * Converts the specified java.util.Date to an equivalent com.addval.utils.date.xbeans.TimeNoSecsString, using the
	 * DateFormatUtil's current default TimeNoSecsFormat.
	 * <p>
 	 * If setGMTOffsetMinutes=true, the returned object's GMTOffsetMinutes attribute will be set to the
 	 * value specified by the gmtOffsetMinutes argument.  Otherwise the returned object's GMTOffsetMinutes attribute will
 	 * be nil.
	 */
	public com.addval.utils.date.xbeans.TimeNoSecsString toTimeNoSecsString(Date date, boolean setGMTOffsetMinutes, int gmtOffsetMinutes)
	{
		com.addval.utils.date.xbeans.TimeNoSecsString xString = null;
		if (date != null)
		{
			// Format the date into a string
			String formattedString = _dateFormatUtil.formatTimeNoSecs( date );

			// Construct the XmlObject
			xString = com.addval.utils.date.xbeans.TimeNoSecsString.Factory.newInstance();
			xString.setStringValue( formattedString );
			if (setGMTOffsetMinutes)
			{
				xString.setGMTOffsetMinutes( gmtOffsetMinutes );
			}
		}
		return xString;
	}

	/**
	 * Converts the specified java.util.Date to an equivalent com.addval.utils.date.xbeans.TimeString, using the
	 * DateFormatUtil's default TimeFormat.
	 * <p>
 	 * If setGMTOffsetMinutes=true, the returned object's GMTOffsetMinutes attribute will be set to the
 	 * value specified by the gmtOffsetMinutes argument.  Otherwise the returned object's GMTOffsetMinutes attribute will
 	 * be nil.
	 */
	public com.addval.utils.date.xbeans.TimeString toTimeString(Date date, boolean setGMTOffsetMinutes, int gmtOffsetMinutes)
	{
		com.addval.utils.date.xbeans.TimeString xString = null;
		if (date != null)
		{
			// Format the date into a string
			String formattedString = _dateFormatUtil.formatTime( date );

			// Construct the XmlObject
			xString = com.addval.utils.date.xbeans.TimeString.Factory.newInstance();
			xString.setStringValue( formattedString );
			if (setGMTOffsetMinutes)
			{
				xString.setGMTOffsetMinutes( gmtOffsetMinutes );
			}
		}
		return xString;
	}

	/**
	 * Converts the specified java.util.Date to an equivalent com.addval.utils.date.xbeans.DateTimeNoSecsString, using the
	 * DateFormatUtil's default DateTimeNoSecsStringFormat.
	 * <p>
 	 * If setGMTOffsetMinutes=true, the returned object's GMTOffsetMinutes attribute will be set to the
 	 * value specified by the gmtOffsetMinutes argument.  Otherwise the returned object's GMTOffsetMinutes attribute will
 	 * be nil.
	 */
	public com.addval.utils.date.xbeans.DateTimeNoSecsString toDateTimeNoSecsString(Date date, boolean setGMTOffsetMinutes, int gmtOffsetMinutes)
	{
		com.addval.utils.date.xbeans.DateTimeNoSecsString xString = null;
		if (date != null)
		{
			// Format the date into a string
			String formattedString = _dateFormatUtil.formatDateTimeNoSecs( date );

			// Construct the XmlObject
			xString = com.addval.utils.date.xbeans.DateTimeNoSecsString.Factory.newInstance();
			xString.setStringValue( formattedString );
			if (setGMTOffsetMinutes)
			{
				xString.setGMTOffsetMinutes( gmtOffsetMinutes );
			}
		}
		return xString;
	}

	/**
	 * Converts the specified java.util.Date to an equivalent com.addval.utils.date.xbeans.DateTimeString, using the
	 * DateFormatUtil's default DateTimeStringFormat.
	 * <p>
 	 * If setGMTOffsetMinutes=true, the returned object's GMTOffsetMinutes attribute will be set to the
 	 * value specified by the gmtOffsetMinutes argument.  Otherwise the returned object's GMTOffsetMinutes attribute will
 	 * be nil.
	 */
	public com.addval.utils.date.xbeans.DateTimeString toDateTimeString(Date date, boolean setGMTOffsetMinutes, int gmtOffsetMinutes)
	{
		com.addval.utils.date.xbeans.DateTimeString xString = null;
		if (date != null)
		{
			// Format the date into a string
			String formattedString = _dateFormatUtil.formatDateTime( date );

			// Construct the XmlObject
			xString = com.addval.utils.date.xbeans.DateTimeString.Factory.newInstance();
			xString.setStringValue( formattedString );
			if (setGMTOffsetMinutes)
			{
				xString.setGMTOffsetMinutes( gmtOffsetMinutes );
			}
		}
		return xString;
	}

	/**
	 * Converts the specified java.util.Date to an equivalent com.addval.utils.date.xbeans.TimestampString, using the
	 * DateFormatUtil's default TimestampStringFormat.
	 * <p>
 	 * If setGMTOffsetMinutes=true, the returned object's GMTOffsetMinutes attribute will be set to the
 	 * value specified by the gmtOffsetMinutes argument.  Otherwise the returned object's GMTOffsetMinutes attribute will
 	 * be nil.
	 */
	public com.addval.utils.date.xbeans.TimestampString toTimestampString(Date date, boolean setGMTOffsetMinutes, int gmtOffsetMinutes)
	{
		com.addval.utils.date.xbeans.TimestampString xString = null;
		if (date != null)
		{
			// Format the date into a string
			String formattedString = _dateFormatUtil.formatTimestamp( date );

			// Construct the XmlObject
			xString = com.addval.utils.date.xbeans.TimestampString.Factory.newInstance();
			xString.setStringValue( formattedString );
			if (setGMTOffsetMinutes)
			{
				xString.setGMTOffsetMinutes( gmtOffsetMinutes );
			}
		}
		return xString;
	}

}