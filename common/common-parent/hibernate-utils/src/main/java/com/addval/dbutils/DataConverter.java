//Source file: d:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\DataConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.util.Calendar;
import java.text.DecimalFormat;
import java.util.Date;
import com.addval.metadata.ColumnMetaData;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * Generic class used for formatting data (Dates, Numbers, Strings, etc.,). This
 * class can be used for formatting (i.e., date -> text), parsing (text -> date),
 * and normalization.
 *
 * @author AddVal Technology Inc.
 * @see java.text.SimpleDateFormat
 * @see java.text.DecimalFormat
 */
public class DataConverter {
	private static final String _module = "DateConverter";

	/**
	 * Format to convert data into.
	 * Example for numbers: "#,##0.00;(#,##0.00)".
	 * Example for date: "DD/MM/YYY"
	 *
	 * @see java.text.DecimalFormat
	 * @see java.text.SimpleDateFormat
	 */
	protected String _format = null;
	protected ColumnMetaData _metaData = null;

	/**
	 * Three letter abbreviation of the calendar months.
	 */
	protected static final String[] _MMMFormat = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

	/**
	 * Complete names of the calendar months.
	 */
	protected static final String[] _MMMMMFormat = { "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER" };

	/**
	 * Constructor.
	 *
	 * @param metaData
	 * @param format Final format required.
	 * @roseuid 3AF065FA003F
	 */
	public DataConverter(ColumnMetaData metaData, String format) {

      _format   = format;
      _metaData = metaData;
	}

	/**
	 * Returns a string with the specified format.
	 *
	 * @param value String to be formatted.
	 * @return Formatted string.
	 * @roseuid 3AF0690100CF
	 */
	protected String formatDisplayValue(String value) {

		return value;
	}

	/**
	 * Formatted int value.
	 *
	 * @param value Int value to be formatted.
	 * @return String containing the formatted int value.
	 * @roseuid 3AF069220004
	 */
	protected String formatDisplayValue(int value) {

		DecimalFormat df = new DecimalFormat( _format );

		return df.format( value );
	}

	/**
	 * String containing the formatted long value.
	 *
	 * @param value
	 * @return String containing the formatted long value.
	 * @roseuid 3AF0692200EB
	 */
	protected String formatDisplayValue(long value) {

		DecimalFormat df = new DecimalFormat( _format );

		return df.format( value );
	}

	/**
	 * String containing the formatted float value.
	 *
	 * @param value float value to be formatted.
	 * @return String containing the formatted float value.
	 * @roseuid 3AF0692201A9
	 */
	protected String formatDisplayValue(float value) {

		DecimalFormat df = new DecimalFormat( _format );

		return df.format( value );
	}

	/**
	 * String containing the formatted double value.
	 *
	 * @param value double to be formatted.
	 * @return String containing the formatted double value.
	 * @roseuid 3AF0693D023E
	 */
	protected String formatDisplayValue(double value) {

		DecimalFormat df = new DecimalFormat( _format );

		return df.format( value );
	}

	/**
	 * String containing the formatted short value.
	 *
	 * @param value short value to be formatted.
	 * @return String containing the formatted short value.
	 * @roseuid 3AF0693D0266
	 */
	protected String formatDisplayValue(short value) {

		DecimalFormat df = new DecimalFormat( _format );

		return df.format( value );
	}

	/**
	 * String containing the formatted char value.
	 *
	 * @param value char value to be formatted.
	 * @return String containing the formatted char value.
	 * @roseuid 3AF0693F0381
	 */
	protected String formatDisplayValue(char value) {

		return String.valueOf( value );
	}

	/**
	 * String containing the formatted date value.
	 *
	 * @param value date value to be formatted.
	 * @return String containing the formatted date value.
	 * @roseuid 3AF0698D01D5
	 */
	protected String formatDisplayValue(Date value) {

      SimpleDateFormat  formatter   = new SimpleDateFormat( _format );
	  formatter.setTimeZone( com.addval.utils.AVConstants._GMT_TIMEZONE );

      return formatter.format( value );
	}


	public static String formatDisplayValue(Date date, String format) {

		SimpleDateFormat formatter = new SimpleDateFormat( format );
		formatter.setTimeZone( com.addval.utils.AVConstants._GMT_TIMEZONE );

		return formatter.format( date );
	}

	/**
	 * String containing the formatted calendar value.
	 *
	 * @param value Calendar value to be formatted.
	 * @return String containing the formatted calendar value.
	 * @roseuid 3AF070A802AD
	 */
	protected String formatDisplayValue(Calendar value) {

		SimpleDateFormat formatter = new SimpleDateFormat( _format );
		formatter.setTimeZone( value.getTimeZone() );

		return formatter.format( value.getTime() );
	}

	/**
	 * Replaces DOS newline chars in given string with UNIX newline char.
	 *
	 * @param dosStr String with DOS newline characters
	 * @return String with UNIX newline characters
	 * @roseuid 3B0DA84D02E3
	 */
	public static final String dosToUnix(String dosStr) {

		if (dosStr == null)
		 return dosStr;

		final String   	dosNewLine   = "\015\012";
		final String   	unixNewLine  = "\012";
		StringBuffer   	unixStr   	 = new StringBuffer(dosStr.length());
		boolean      	 	first        = true;
		StringTokenizer	st			 = new StringTokenizer(dosStr, dosNewLine);

		while (st.hasMoreTokens()) {

		 if (first)
			first = false;
		 else
			unixStr.append( unixNewLine );

		 unixStr.append( st.nextToken() );
		}

		return unixStr.toString();
	}

	/**
	 * Converts the month to the Calendar objects integer representation of month
	 *
	 * @param month Name of the month
	 * @param format Format of the month (i.e "JAN" or "JANUARY". ). To specify "JAN"
	 * use "MMM" and to specify "JANUARY" use "MMMMM".
	 * @return Returns the number corresponding to the month. Starting with 0 for JAN
	 * @roseuid 3B6F547F0064
	 */
	public static final int convertMonth(String month, String format) {

		int rv = -1;

		if (month != null && format != null && month.length() != 0 && format.length() != 0) {

			month = month.toUpperCase();
			String[] months = null;

			switch (format.length()) {

				case 3 :
				months = _MMMFormat;
				break;

				default :
				months = _MMMMMFormat;
			}

			for (int index = 0; index < months.length; index++) {

				if (month.equals( months[index] )) {
					rv = index;
					break;
				}
				// If none of the months match returns -1
				rv = -1;
			}
		}

		return rv;
	}

	/**
	 * Given calendar's integer representation of month returns month's name (MMM or
	 * MMMMM)
	 *
	 * @param index Month's index. Starting with 0 for "JAN"
	 * @param format Required format of the return string.  "MMM" format will return
	 * "JAN" and "MMMMM" format will return "JANUARY"
	 * @return String corresponding to the month's index.
	 * @roseuid 3B8D3E200201
	 */
	public static final String getMonthName(int index, String format) {

		if (index >=0 && index < 12 && format != null && format.length() != 0) {

			String[] months = null;

			switch (format.length()) {

			case 3 :
				months = _MMMFormat;
				break;

			default :
				months = _MMMMMFormat;
			}

			return months[index];
		}

		return "";
	}
}
