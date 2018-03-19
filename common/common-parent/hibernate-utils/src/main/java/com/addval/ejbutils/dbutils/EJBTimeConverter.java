//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBTimeConverter.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBTimeConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import com.addval.metadata.ColumnDataType;
import com.addval.ejbutils.utils.EJBXRuntime;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import com.addval.utils.AVConstants;
import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnMetaData;
import java.sql.Time;

/**
 * A converter used by the EJBResultSet for converting formatted Strings to Time 
 * data and vice-versa
 */
public class EJBTimeConverter extends EJBConverter 
{
	private static final String _module = "EJBTimeConverter";
	public static final String _HH12 = "12";
	public static final String _HH24 = "24";
	public static final String _AM = "AM";
	public static final String _PM = "PM";
	
	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF05F1E0206
	 */
	public EJBTimeConverter(EJBColumn column, ColumnMetaData metaData, String format) 
	{
        super( column, metaData, format );		
	}
	
	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF05F1E01D4
	 */
	public EJBTimeConverter(EJBColumn column, ColumnMetaData metaData) 
	{
        this( column, metaData, metaData.getFormat() );		
	}
	
	/**
	 * @param value
	 * @roseuid 3AF05F1E0224
	 */
	public void setValue(String value) 
	{
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setIntValue( formatDBValue( value, _format) );		
	}
	
	/**
	 * @return java.lang.String
	 * @roseuid 3AF05F1E0242
	 */
	public String getString() 
	{
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getIntValue() );		
	}
	
	/**
	 * @param value
	 * @roseuid 3AF05F1E0256
	 */
	public void setTime(Time value) 
	{
        validate();

        Calendar calendar=Calendar.getInstance( AVConstants._GMT_TIMEZONE );
        calendar.setTime(value);
        _column.setLongValue( calendar.get( Calendar.HOUR_OF_DAY )*100 + calendar.get( Calendar.MINUTE ) );		
	}
	
	/**
	 * @return java.sql.Time
	 * @roseuid 3AF05F1E0274
	 */
	public Time getTime() 
	{
        validate();

        return new Time( _column.getIntValue()/100, _column.getIntValue()%100, 0 );		
	}
	
	/**
	 * @param value
	 * @return java.lang.String
	 * @roseuid 3B195DCF00CA
	 */
	protected String formatDisplayValue(int value) 
	{
        String rv = null;

        if (_format.equals( _HH24 ))
            rv = String.valueOf( value/100 ) + ":" + String.valueOf( value%100 );
        if (_format.equals( _HH12 ))
            rv = String.valueOf( value < 1200 ? value/100 : value/100 - 12 ) + ":" + String.valueOf( value%100 ) + (value < 1200 ? _AM : _PM ) ;

        return rv;		
	}
	
	/**
	 * @param value
	 * @param format
	 * @return int
	 * @roseuid 3AF05F1E0292
	 */
	public int formatDBValue(String value, String format) 
	{
        int rv = 0;

        if (_format.equals( _HH24 ))
            rv = Integer.parseInt( value.substring( 0, value.indexOf( ":" ) ) + value.substring( value.indexOf( ":" ) + 1 ) );
        if (_format.equals( _HH12 )) {
            // Strip AM/PM
            String  temp    = value.substring( 0, value.length() - 2);
            int     i       = Integer.parseInt( temp.substring( 0, temp.indexOf( ":" ) ) + temp.substring( temp.indexOf( ":" ) + 1 ) );
            rv = value.substring( value.length() - 2 ).equalsIgnoreCase( _AM ) ? i : i + 12;
        }

        return rv;		
	}
	
	/**
	 * @return boolean
	 * @roseuid 3AFC82AA037D
	 */
	protected boolean validate() 
	{
        boolean valid = _metaData.getType() == ColumnDataType._CDT_TIME ? true : false;

        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;		
	}
}
