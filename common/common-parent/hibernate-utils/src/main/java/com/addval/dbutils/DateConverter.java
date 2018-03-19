//Source file: d:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\DateConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date converter. Converts the input date string into a format specified by
 * ORA_FORMAT.
 * @revision $Revision$
 *
 * @author Sankar Dhanushkodi
 */
public class DateConverter extends Converter {

	/**
	 * Date/time represented in Oracle format.
	 */
	private static final String _ORA_FORMAT = "'YYYY/MM/DD HH24:MI:SS'";

	/**
	 * @roseuid 3C632DDA03AE
	 */
	public DateConverter() {

	}

	/**
	 * Constructor. Calls the super constructor (Converter)
	 *
	 * @param tableName table name value
	 * @param columnName column name value
	 * @param nullable true if nullable; otherwise false
	 * @see Converter
	 * @roseuid 37935AF400C0
	 */
	public DateConverter(String tableName, String columnName, boolean nullable) {
      super( tableName, columnName, nullable );
	}

	/**
	 * Converts the content of the input paramter to a formated string. Input format
	 * "yyyy/MM/dd hh:mm:ss" is assumed.
	 *
	 * @param strContent String with a valid date. Input format "yyyy/dd hh:mm:ss" is
	 * assumed
	 * @return A properly formated date string that can be used in Oracle queries
	 * @roseuid 376AAEF5008D
	 */
	public String convert(String strContent) {
      if ( isNull( strContent ) && canContainNull() )
         return "null";
      else
         return "TO_DATE( '" + strContent + "', " + _ORA_FORMAT + ")";    // format "yyyy/MM/dd hh:mm:ss" is assumed
	}

	/**
	 * Formats a date based on the input format specification. Uses
	 * java.text.SimpleDateFormat to format the date.
	 * @deprecated
	 * @param date Date to the formatted.
	 * @param format Format of the date required. (MM/DD/YYYY, etc.,)
	 * @return A string containing the formatted date.
	 * @roseuid 39C7FC3B02BE
	 */
	public static String formatDisplayValue(Date date, String format) {

		//Calendar 			calendar 	= Calendar.getInstance();
		SimpleDateFormat 	formatter 	= new SimpleDateFormat( format );

		//calendar.set( date.year, date.month - 1, date.day );
		return formatter.format( date );
	}
}
