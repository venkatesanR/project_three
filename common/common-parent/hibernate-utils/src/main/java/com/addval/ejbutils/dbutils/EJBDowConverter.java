//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDowConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;

/**
 * A converter used by the EJBResultSet for converting formatted Strings to
 * Day-of-week data and vice-versa
 */
public class EJBDowConverter extends EJBConverter {
	public static final String _MONDAY = "MONDAY";
	public static final String _TUESDAY = "TUESDAY";
	public static final String _WEDNESDAY = "WEDNESDAY";
	public static final String _THURSDAY = "THURSDAY";
	public static final String _FRIDAY = "FRIDAY";
	public static final String _SATURDAY = "SATURDAY";
	public static final String _SUNDAY = "SUNDAY";
	public static final String _MON_DOW = "1";
	public static final String _TUE_DOW = "2";
	public static final String _WED_DOW = "3";
	public static final String _THU_DOW = "4";
	public static final String _FRI_DOW = "5";
	public static final String _SAT_DOW = "6";
	public static final String _SUN_DOW = "7";
	private static final String _module = "EJBDowConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3B02F42901C2
	 */
	public EJBDowConverter(EJBColumn column, ColumnMetaData metaData, String format) {
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3B02F42901A4
	 */
	public EJBDowConverter(EJBColumn column, ColumnMetaData metaData) {
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3B02F42901D6
	 */
	public void setValue(String value) {
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setDowValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B02F42901EA
	 */
	public String getString() {
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getDowValue() );
	}

	/**
	 * @param value
	 * @roseuid 3B02F42901FE
	 */
	public void setDow(int value) {
        validate();

        _column.setDowValue( value );
	}

	/**
	 * @return int
	 * @roseuid 3B02F429021C
	 */
	public int getDow() {
        validate();

        return _column.getDowValue();
	}

	/**
	 * @param value
	 * @param format
	 * @return int
	 * @roseuid 3B02F4290230
	 */
	public int formatDBValue(String value, String format) {
        String rv = "";

        if (value.indexOf( _MONDAY.substring    ( 0, format.length() ) ) >= 0 || value.equals(_MON_DOW))
            rv += _MON_DOW;
        if (value.indexOf( _TUESDAY.substring   ( 0, format.length() ) ) >= 0 || value.equals(_TUE_DOW))
            rv += _TUE_DOW;
        if (value.indexOf( _WEDNESDAY.substring ( 0, format.length() ) ) >= 0 || value.equals(_WED_DOW))
            rv += _WED_DOW;
        if (value.indexOf( _THURSDAY.substring  ( 0, format.length() ) ) >= 0 || value.equals(_THU_DOW))
            rv += _THU_DOW;
        if (value.indexOf( _FRIDAY.substring    ( 0, format.length() ) ) >= 0 || value.equals(_FRI_DOW))
            rv += _FRI_DOW;
        if (value.indexOf( _SATURDAY.substring  ( 0, format.length() ) ) >= 0 || value.equals(_SAT_DOW))
            rv += _SAT_DOW;
        if (value.indexOf( _SUNDAY.substring    ( 0, format.length() ) ) >= 0 || value.equals(_SUN_DOW))
            rv += _SUN_DOW;
        if (rv.length() == 0)
            throw new XRuntime( _module, "Value could not be parsed : " + value );

        return Integer.parseInt( rv );
	}

	/**
	 * @return boolean
	 * @roseuid 3B02F4290244
	 */
	protected boolean validate() {
        boolean valid = _metaData.getType() == ColumnDataType._CDT_DOW ? true : false;

        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}

	/**
	 * @param value
	 * @return java.lang.String
	 * @roseuid 3B032C09027E
	 */
	protected String formatDisplayValue(int value) {
        String rv   = "";
        String dow  = String.valueOf( value );

        if (dow.indexOf( _MON_DOW ) >= 0 )
            rv += _MONDAY.substring( 0, _format.length() ) + ",";
        if (dow.indexOf( _TUE_DOW ) >= 0)
            rv += _TUESDAY.substring( 0, _format.length() ) + ",";
        if (dow.indexOf( _WED_DOW ) >= 0)
            rv += _WEDNESDAY.substring( 0, _format.length() ) + ",";
        if (dow.indexOf( _THU_DOW ) >= 0)
            rv += _THURSDAY.substring( 0, _format.length() ) + ",";
        if (dow.indexOf( _FRI_DOW ) >= 0)
            rv += _FRIDAY.substring( 0, _format.length() ) + ",";
        if (dow.indexOf( _SAT_DOW ) >= 0)
            rv += _SATURDAY.substring( 0, _format.length() ) + ",";
        if (dow.indexOf( _SUN_DOW ) >= 0)
            rv += _SUNDAY.substring( 0, _format.length() ) + ",";
        if (rv.length() == 0)
            throw new XRuntime( _module, "Value could not be parsed : " + value );

        return rv.substring( 0, rv.length() - 1 );
	}
}
