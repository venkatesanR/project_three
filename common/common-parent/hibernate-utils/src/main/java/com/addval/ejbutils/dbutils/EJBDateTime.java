//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateTime.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateTime.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateTime.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import com.addval.utils.AVConstants;

public class EJBDateTime implements Serializable
{
	private int _month = -1;
	private int _year = -1;
	private int _day = -1;
	private int _hour = -1;
	private int _minute = -1;
	private int _second = -1;

	/**
	 * @param month
	 * @param day
	 * @param year
	 * @param hour
	 * @param minute
	 * @param second
	 * @roseuid 3AF02438004D
	 */
	public EJBDateTime(int month, int day, int year, int hour, int minute, int second)
	{
        this( month, day, year );
        _hour   = hour;
        _minute = minute;
        _second = second;
	}

	/**
	 * @param month
	 * @param day
	 * @param year
	 * @roseuid 3AF02413002B
	 */
	public EJBDateTime(int month, int day, int year)
	{
        _month  = month;
        _day    = day;
        _year   = year;
	}

	/**
	 * @param date
	 * @roseuid 3AF9BA0802DE
	 */
	public EJBDateTime(Date date)
	{
		//System.out.println( "Date read from DB is : " + date );

        Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );
        calendar.setTime( date );

        _month  = calendar.get( Calendar.MONTH ) + 1;
        _day    = calendar.get( Calendar.DATE );
        _year   = calendar.get( Calendar.YEAR );
        _hour   = calendar.get( Calendar.HOUR_OF_DAY );
        _minute = calendar.get( Calendar.MINUTE );
        _second = calendar.get( Calendar.SECOND );

        //System.out.println( "EJBDateTime Day/Month/Year = " + _day + "/" + _month + "/" + _year +  ":" + _hour + ":" + _minute + ":" + _second  );
	}

	/**
	 * @return int
	 * @roseuid 3B007557032C
	 */
	public int getYear()
	{
        return _year;
	}

	/**
	 * @param value
	 * @roseuid 3B007558000C
	 */
	public void setYear(int value)
	{
        _year = value;
	}

	/**
	 * @return int
	 * @roseuid 3B007573014B
	 */
	public int getMonth()
	{
        return _month;
	}

	/**
	 * @param value
	 * @roseuid 3B00757301B0
	 */
	public void setMonth(int value)
	{
        _month = value;
	}

	/**
	 * @return int
	 * @roseuid 3B007574032D
	 */
	public int getDay()
	{
        return _day;
	}

	/**
	 * @param value
	 * @roseuid 3B0075740374
	 */
	public void setDay(int value)
	{
        _day = value;
	}

	/**
	 * @return int
	 * @roseuid 3B0075750389
	 */
	public int getHour()
	{
        return _hour;
	}

	/**
	 * @param value
	 * @roseuid 3B00757503D9
	 */
	public void setHour(int value)
	{
        _hour = value;
	}

	/**
	 * @return int
	 * @roseuid 3B007577001B
	 */
	public int getMinute()
	{
        return _minute;
	}

	/**
	 * @param value
	 * @roseuid 3B007577006B
	 */
	public void setMinute(int value)
	{
        _minute = value;
	}

	/**
	 * @return int
	 * @roseuid 3B0075DB0259
	 */
	public int getSecond()
	{
        return _second;
	}

	/**
	 * @param value
	 * @roseuid 3B0075DB028B
	 */
	public void setSecond(int value)
	{
        _second = value;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C8D49E800DC
	 */
	public String toXML()
	{
		return null;
	}

	/**
	 * @param xml
	 * @roseuid 3C8D49E8012C
	 */
	public void fromXML(String xml)
	{

	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C8D4A1A01BA
	 */
	public String toString()
	{

		StringBuffer str 	= new StringBuffer();
		final String SPACE 	= " ";

		str.append( getMonth() + "/" + getDay() + "/" + getYear() );
		if (getHour() != -1 || getMinute() != -1 || getSecond() != -1)
			str.append( SPACE + getHour() + ":" + getMinute() + ":" + getSecond() );

		return str.toString();
	}
}
