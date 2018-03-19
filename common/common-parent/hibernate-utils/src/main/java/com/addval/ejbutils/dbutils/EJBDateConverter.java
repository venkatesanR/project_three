//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateConverter.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDateConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.utils.EJBXRuntime;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.addval.utils.AVConstants;
import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnMetaData;

import com.addval.utils.DateUtl;

/**
 * A converter used by the EJBResultSet for converting formatted Strings to Date
 * data and vice-versa
 */
public class EJBDateConverter extends EJBConverter
{
	private static final String _module = "EJBDateConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF03E6200EE
	 */
	public EJBDateConverter(EJBColumn column, ColumnMetaData metaData, String format)
	{
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF03E6200BC
	 */
	public EJBDateConverter(EJBColumn column, ColumnMetaData metaData)
	{
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3AF03E5301BF
	 */
	public void setValue(String value)
	{
        validate( value );
        if (value == null || value.length() == 0)
            _column.setNull();
        else
        {
            EJBDateTime ejbDateTime = formatDBValue( value, _format);
            if(ejbDateTime == null) {
                _column.setNull();
            }
            else {
                _column.setDateTimeValue( ejbDateTime );
            }
        }
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF03E5301E7
	 */
	public String getString()
	{
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( getDate() );
	}

	/**
	 * @param value
	 * @roseuid 3AF03E5301FB
	 */
	public void setDate(Date value)
	{
        validate();

        if (value == null) {

            _column.setNull();
        }
        else {

			Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );

			calendar.setTime( value );

			_column.setDateTimeValue( new EJBDateTime( calendar.get( Calendar.MONTH ) + 1,
									calendar.get( Calendar.DATE ),
									calendar.get( Calendar.YEAR ) ) );
		}
	}

	/**
	 * @return Date
	 * @roseuid 3AF03E530219
	 */
	public Date getDate()
	{
        validate();

        // If the EJBDateTimeValue is not set the return null
        if (_column.getDateTimeValue() == null)
            return null;

        Calendar calendar = Calendar.getInstance( AVConstants._GMT_TIMEZONE );

        calendar.set( _column.getDateTimeValue().getYear(),
                      _column.getDateTimeValue().getMonth() - 1,
                      _column.getDateTimeValue().getDay() );

        return calendar.getTime();
	}

	/**
	 * @param cal
	 * @return Date
	 * @roseuid 3F00AC6E0109
	 */
	protected Date getDate(Calendar cal)
	{
        validate();

        // If the EJBDateTimeValue is not set the return null
        if (_column.getDateTimeValue() == null)
            return null;

        Calendar calendar = Calendar.getInstance( cal.getTimeZone() );

        calendar.set( _column.getDateTimeValue().getYear(),
                      _column.getDateTimeValue().getMonth() - 1,
                      _column.getDateTimeValue().getDay() );

        return calendar.getTime();
	}

	/**
	 * @param value
	 * @param format
	 * @return com.addval.ejbutils.dbutils.EJBDateTime
	 * @roseuid 3AF03E53022D
	 */
	public EJBDateTime formatDBValue(String value, String format)
	{

        Calendar            calendar  = Calendar.getInstance( AVConstants._GMT_TIMEZONE );
        SimpleDateFormat    formatter = new SimpleDateFormat( format );
        EJBDateTime ejbDateTime = null;
        /*Search gets initaited when enter key is pressed in list screen.
        When invalid date is entered, Nullpointer exception is thrown. To overcome this issue,
        formatDBValue will return null if invalid date id provided. -BAPRC-799
        */
		formatter.setTimeZone( AVConstants._GMT_TIMEZONE );
		Date twoDigitYearStart = DateUtl.getTwoDigitYearStart();
		if (twoDigitYearStart != null)
			formatter.set2DigitYearStart( twoDigitYearStart );

        formatter.setTimeZone( calendar.getTimeZone() );
        try {
            calendar.setTime( formatter.parse( value, new ParsePosition(0) ) );
            ejbDateTime = new EJBDateTime( calendar.get( Calendar.MONTH ) + 1,
                                calendar.get( Calendar.DATE ),
                                calendar.get( Calendar.YEAR ) );
        }
        catch(NullPointerException e) {
            ejbDateTime = null;
        }
        finally {
            return  ejbDateTime;
        }

	}

	/**
	 * @return boolean
	 * @roseuid 3AFB3454014A
	 */
	protected boolean validate()
	{
        boolean valid = _metaData.getType() == ColumnDataType._CDT_DATE ? true : false;

        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
