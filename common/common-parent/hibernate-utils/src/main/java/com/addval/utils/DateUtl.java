/*
 * DateUtl.java
 *
 * Created on June 18, 2003, 11:02 AM
 */

package com.addval.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.regexp.RE;

public class DateUtl {

	private static final String _COMMON_CONFIG = "avcommon";
	private static final String _TWO_DIGIT_CUTOFF_MIN_YEAR = "two.digit.cutoff.min.year";
	private static final String _TWO_DIGIT_DEFAULT_START_YEAR = "two.digit.default.start.year";
	private static final String _TWO_DIGIT_DEFAULT_NBR_OF_YEAR = "two.digit.default.nbr.of.year";
	protected static Date _twoDigitYearStart = null;
	protected static long cutoffYear =2000;
	protected static boolean _twoDigitYearInitialized = false;

	public static Date getTimestamp() {
		Date date = new Date();
		long time = date.getTime() + AVConstants._LOCAL_TIMEZONE_OFFSET + (AVConstants._LOCAL_TIMEZONE.inDaylightTime(date) ? AVConstants._LOCAL_DSTSAVINGS : 0);

		return new Date(time);
	}

	public static Timestamp getGmtTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	public static Timestamp getCurrentTimestamp() {
		Date date = new Date();
		long time = date.getTime() + AVConstants._LOCAL_TIMEZONE_OFFSET + (AVConstants._LOCAL_TIMEZONE.inDaylightTime(date) ? AVConstants._LOCAL_DSTSAVINGS : 0);
		return new Timestamp(time);
	}

	public static long getCutoffYear() {
		if (!_twoDigitYearInitialized) {
			read2DigitYearStart();
		}
		return cutoffYear;
	}

	protected static void read2DigitYearStart() {
		try {
			CnfgFileMgr paramsFile = new com.addval.utils.CnfgFileMgr(_COMMON_CONFIG);
			cutoffYear = paramsFile.getLongValue(_TWO_DIGIT_CUTOFF_MIN_YEAR, 0);
			getInstance(String.valueOf(cutoffYear));
		}
		catch (Exception e) {
		}
		_twoDigitYearInitialized = true;
	}

	public static Date getTwoDigitYearStart() {
		if (!_twoDigitYearInitialized) {
			read2DigitYearStart();
		}
		return _twoDigitYearStart;
	}

	public static void applyTwoDigitYearStart(SimpleDateFormat formatter) {
		Date startYear = getTwoDigitYearStart();
		if (startYear != null) {
			formatter.set2DigitYearStart(startYear);
		}
	}

	public static DateUtl getInstance(String cutoffYearValue) {
		if (!StrUtl.isEmptyTrimmed(cutoffYearValue) ) {
			cutoffYear = Integer.parseInt(cutoffYearValue);
			if ( cutoffYear == 0 ) {
				Date dt = new Date();
				CnfgFileMgr paramsFile = new com.addval.utils.CnfgFileMgr(_COMMON_CONFIG);
				int defaultStartYear = Integer.parseInt(paramsFile.getPropertyValue(_TWO_DIGIT_DEFAULT_START_YEAR, "1900"));
				int defaultNbrOfYear = Integer.parseInt(paramsFile.getPropertyValue(_TWO_DIGIT_DEFAULT_NBR_OF_YEAR, "79"));
				cutoffYear = (defaultStartYear + dt.getYear() ) - defaultNbrOfYear;
			}
			Calendar calendar = Calendar.getInstance(AVConstants._GMT_TIMEZONE);
			// calendar is set for 0 for all except year to make sure that the setting
			// take effect for all days from start of cutoffYear.
			// for eg. if the year is 2000 and If the day has 1 in the below statement, then
			// 1-1-00 would be interpreted by java as 1-1-2100. - jeyaraj - 06-Feb-2005
			calendar.set((int) cutoffYear, 0, 1, 0, 0, 0);
			_twoDigitYearStart = calendar.getTime();
			_twoDigitYearInitialized = true;
		}
		return new DateUtl();

	}

	public static boolean isDateInFormat(String datePattern, String value) {
		String dateRegExp = null;
		if (datePattern.equalsIgnoreCase("HH:MM")) {
			dateRegExp = "^([0-9]|[0-1][0-9]|[2][0-3]):([0-5][0-9])$";
		}
		else if (datePattern.equalsIgnoreCase("HHMM")) {
			//dateRegExp = "^([0-9]|[0-1][0-9]|[2][0-3])([0-5][0-9])$";
			dateRegExp ="^([0-1][0-9]{1}|[2][0-3]{1})([0-5][0-9]){1}$"; //0000 - 2359
		}
		else if (datePattern.equalsIgnoreCase("MM/DD/YYYY")) {
			dateRegExp = "^((([0]\\d)|([1][012])|[1-9])|(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec))(\\-|\\/|\\s)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\6(\\d{2}|([1][9]\\d{2})|([2]\\d{3})))$";
		}
		else if (datePattern.equalsIgnoreCase("DDMMYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01]))(([0]\\d)|([1][012]))(\\d{2})$";
		}
		else if (datePattern.equalsIgnoreCase("DD/MM/YY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-|\\/|\\s)(([0]\\d)|([1][0-2])|([1-9]))(\\-|\\/|\\s)(\\d{2})$";
		}
		else if (datePattern.equalsIgnoreCase("MM/DD/YY")) {
			dateRegExp = "^(([0]\\d)|([1][0-2])|([1-9]))(\\-|\\/|\\s)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-|\\/|\\s)(\\d{2})$";
		}
		else if (datePattern.equalsIgnoreCase("DDMMMYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\d{2})?$";
		}
		else if (datePattern.equalsIgnoreCase("DDMMYYYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01]))(([0]\\d)|([1][012]))(\\d{4})$";
		}
		else if (datePattern.equalsIgnoreCase("MMDDYYYY")) {
			dateRegExp = "^(([0]\\d)|([1][012]))(([0]\\d)|([1]\\d)|([2]\\d)|([3][01]))(\\d{4})$";
		}
		else if (datePattern.equalsIgnoreCase("DD-MMM-YY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\-)((\\d{2}|([1][9]\\d{2})|([2]\\d{3})))?$";
		}
		else if (datePattern.equalsIgnoreCase("YYYY/MM/DD")) {
			dateRegExp = "^(\\d{4})(\\/)(([0]\\d)|([1][012]))(\\/)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))$";
		}
		else if (datePattern.equalsIgnoreCase("YYYY-MM-DD")) {
			dateRegExp = "^(\\d{4})(-)(([0]\\d)|([1][012]))(-)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))$";
		}
		else if (datePattern.equalsIgnoreCase("DD/MMM/YYYY")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\/|\\/)?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\/|\\/)?((([1][9]\\d{2})|([2]\\d{3})))?$";
		}
		else if (datePattern.equalsIgnoreCase("DD/MMM/YYYY HH:MM")) {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\/|\\/)?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\/|\\/)?((([1][9]\\d{2})|([2]\\d{3}))( ([0-9]|[0-1][0-9]|[2][0-3]):([0-5][0-9])))?$";
		}
		else if (datePattern.equalsIgnoreCase("MM/DD/YY HH:MM")) {
			dateRegExp = "^(([0]\\d)|([1][0-2])|([1-9]))(\\-|\\/|\\s)(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-|\\/|\\s)((\\d{2})( ([0-9]|[0-1][0-9]|[2][0-3]):([0-5][0-9])))?$";
		}
		else {
			dateRegExp = "^(([0]\\d)|([1]\\d)|([2]\\d)|([3][01])|([1-9]))(\\-|\\/)?(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(\\-|\\/)?((\\d{2}|([1][9]\\d{2})|([2]\\d{3})))?$";
		}

		if (!StrUtl.isEmptyTrimmed(dateRegExp)) {
			RE matcher = new RE(dateRegExp, RE.MATCH_CASEINDEPENDENT);
			if (!matcher.match(value)) {
				return false;
			}
		}
		return true;
	}
}
