//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateTimeConverter.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateTimeConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.utils.EJBXRuntime;
import java.util.Date;
import java.util.Calendar;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import com.addval.utils.AVConstants;
import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnMetaData;
import java.sql.Timestamp;

import com.addval.utils.DateUtl;

/**
 * A converter used by the EJBResultSet for converting formatted Strings to
 * Timestamp data and vice-versa
 */
public class EJBDateTimeConverter extends EJBConverter
{
	private static final String _module = "EJBDateTimeConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF05E9600D4
	 */
	public EJBDateTimeConverter(EJBColumn column, ColumnMetaData metaData, String format)
	{
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF05E9600C0
	 */
	public EJBDateTimeConverter(EJBColumn column, ColumnMetaData metaData)
	{
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3AF05E9600F2
	 */
	public void setValue(String value)
	{
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setDateTimeValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF05E960110
	 */
	public String getString()
	{
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( getDateTime() );
	}

	/**
	 * @param value
	 * @roseuid 3AF05E960124
	 */
	public void setDateTime(Date value)
	{
        validate();

        Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );

        calendar.setTime( value );

        _column.setDateTimeValue( new EJBDateTime( calendar.get( Calendar.MONTH ) + 1,
                                calendar.get( Calendar.DATE ),
                                calendar.get( Calendar.YEAR ),
                                calendar.get( Calendar.HOUR_OF_DAY ),
                                calendar.get( Calendar.MINUTE ),
                                calendar.get( Calendar.SECOND ) ) );
	}

	/**
	 * @return Date
	 * @roseuid 3AF05E96014C
	 */
	public Date getDateTime()
	{
        validate();

        // If the EJBDateTimeValue is not set the return null
        if (_column.getDateTimeValue() == null)
            return null;

		//System.out.println( "Day/Month/Year = " + _column.getDateTimeValue().getDay() + "/" + _column.getDateTimeValue().getMonth() + "/" + _column.getDateTimeValue().getYear() );

        Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );

        calendar.set( _column.getDateTimeValue().getYear(),
                      _column.getDateTimeValue().getMonth() - 1,
                      _column.getDateTimeValue().getDay(),
                      _column.getDateTimeValue().getHour(),
                      _column.getDateTimeValue().getMinute(),
                      _column.getDateTimeValue().getSecond() );

        return calendar.getTime();
	}

	/**
	 * @param cal
	 * @return Date
	 * @roseuid 3F00A859002E
	 */
	protected Date getDateTime(Calendar cal)
	{
        validate();

        // If the EJBDateTimeValue is not set the return null
        if (_column.getDateTimeValue() == null)
            return null;

        Calendar calendar = Calendar.getInstance( cal.getTimeZone() );

        calendar.set( _column.getDateTimeValue().getYear(),
                      _column.getDateTimeValue().getMonth() - 1,
                      _column.getDateTimeValue().getDay(),
                      _column.getDateTimeValue().getHour(),
                      _column.getDateTimeValue().getMinute(),
                      _column.getDateTimeValue().getSecond() );

        return calendar.getTime();
	}

	/**
	 * @param value
	 * @roseuid 3AF749170280
	 */
	public void setTimestamp(Timestamp value)
	{
    	validate();

		if (value == null) {

            _column.setNull();
        }
        else {

        	Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );

        	calendar.setTime( new Date( value.getTime() ) );

	        _column.setDateTimeValue( new EJBDateTime( 	calendar.get( Calendar.MONTH ) + 1,
                                						calendar.get( Calendar.DATE ),
                                						calendar.get( Calendar.YEAR ),
                                						calendar.get( Calendar.HOUR_OF_DAY ),
                                						calendar.get( Calendar.MINUTE ),
                                						calendar.get( Calendar.SECOND )
                                					  )
                                	);
		}
	}

	/**
	 * @return java.sql.Timestamp
	 * @roseuid 3AF737F30294
	 */
	public Timestamp getTimestamp()
	{
        validate();

        // If the EJBDateTimeValue is not set the return null
        if (_column.getDateTimeValue() == null)
            return null;

        Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );

        calendar.set( _column.getDateTimeValue().getYear(),
                      _column.getDateTimeValue().getMonth() - 1,
                      _column.getDateTimeValue().getDay(),
                      _column.getDateTimeValue().getHour(),
                      _column.getDateTimeValue().getMinute(),
                      _column.getDateTimeValue().getSecond() );

        return new Timestamp( calendar.getTime().getTime() );
	}

	/**
	 * @param cal
	 * @return java.sql.Timestamp
	 * @roseuid 3F00A859003E
	 */
	protected Timestamp getTimestamp(Calendar cal)
	{
        validate();

        // If the EJBDateTimeValue is not set the return null
        if (_column.getDateTimeValue() == null)
            return null;

        Calendar calendar = Calendar.getInstance( cal.getTimeZone() );

        calendar.set( _column.getDateTimeValue().getYear(),
                      _column.getDateTimeValue().getMonth() - 1,
                      _column.getDateTimeValue().getDay(),
                      _column.getDateTimeValue().getHour(),
                      _column.getDateTimeValue().getMinute(),
                      _column.getDateTimeValue().getSecond() );

        return new Timestamp( calendar.getTime().getTime() );
	}

	/**
	 * @param value
	 * @param format
	 * @return com.addval.ejbutils.dbutils.EJBDateTime
	 * @roseuid 3AF05E96016B
	 */
	public EJBDateTime formatDBValue(String value, String format)
	{
		//System.out.println( "The Format is = " + format  + " value = " + value );

		SimpleDateFormat formatter = new SimpleDateFormat( format );

		Date twoDigitYearStart = DateUtl.getTwoDigitYearStart();
		if (twoDigitYearStart != null)
			formatter.set2DigitYearStart( twoDigitYearStart );

		formatter.setTimeZone( AVConstants._GMT_TIMEZONE );

        Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );
        calendar.setTime( formatter.parse( value, new ParsePosition(0) ) );

        return new EJBDateTime( calendar.get( Calendar.MONTH ) + 1,
                                calendar.get( Calendar.DATE ),
                                calendar.get( Calendar.YEAR ),
                                calendar.get( Calendar.HOUR_OF_DAY ),
                                calendar.get( Calendar.MINUTE ),
                                calendar.get( Calendar.SECOND ) );
	}

	/**
	 * @return boolean
	 * @roseuid 3AFC82F7019D
	 */
	protected boolean validate()
	{
        boolean valid = _metaData.getType() == ColumnDataType._CDT_DATETIME || _metaData.getType() == ColumnDataType._CDT_TIMESTAMP ? true : false;

        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
