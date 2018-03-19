//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBShortConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import com.addval.metadata.ColumnDataType;
import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnMetaData;

/**
 * A converter used by the EJBResultSet for converting formatted Strings to short
 * data and vice-versa
 */
public class EJBShortConverter extends EJBConverter {
	private static final String _module = "EJBShortConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3B0331DA0167
	 */
	public EJBShortConverter(EJBColumn column, ColumnMetaData metaData, String format) {
		super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3B0331DA013F
	 */
	public EJBShortConverter(EJBColumn column, ColumnMetaData metaData) {
		this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3B0331DA018F
	 */
	public void setValue(String value) {
		validate( value );

		if (value == null || value.length() == 0)
			_column.setNull();
		else
			_column.setShortValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B0331DA01AD
	 */
	public String getString() {
		validate();

		return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getShortValue() );
	}

	/**
	 * @param value
	 * @roseuid 3B0331DA01CB
	 */
	public void setShort(short value) {
		validate();

		_column.setShortValue( value );
	}

	/**
	 * @return short
	 * @roseuid 3B0331DA01DF
	 */
	public short getShort() {
		validate();

		return _column.getShortValue();
	}

	/**
	 * @param value
	 * @param format
	 * @return short
	 * @roseuid 3B0331DA01FD
	 */
	public short formatDBValue(String value, String format) {
		return Short.parseShort( value );
	}

	/**
	 * @return boolean
	 * @roseuid 3B0331DA021B
	 */
	protected boolean validate() {
		boolean valid = _metaData.getType() == ColumnDataType._CDT_SHORT ? true : false;

		if (!valid)
			throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

		return valid;
	}
}
