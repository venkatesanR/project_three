//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBFloatConverter.java

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
 * A converter used by the EJBResultSet for converting formatted Strings to float
 * data and vice-versa
 */
public class EJBFloatConverter extends EJBConverter {
	private static final String _module = "EJBFloatConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3B02F4C40034
	 */
	public EJBFloatConverter(EJBColumn column, ColumnMetaData metaData, String format) {
		super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3B02F4C40020
	 */
	public EJBFloatConverter(EJBColumn column, ColumnMetaData metaData) {
		this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3B02F4C40048
	 */
	public void setValue(String value) {
		validate( value );

		if (value == null || value.length() == 0)
			_column.setNull();
		else
			_column.setFloatValue( formatDBValue( value ) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B02F4C40066
	 */
	public String getString() {
		validate();

		return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getFloatValue() );
	}

	/**
	 * @param value
	 * @roseuid 3B02F4C4007A
	 */
	public void setFloat(float value) {
		validate();

		_column.setFloatValue( value );
	}

	/**
	 * @return float
	 * @roseuid 3B02F4C4008E
	 */
	public float getFloat() {
		validate();

		return _column.getFloatValue();
	}

	/**
	 * @param value
	 * @return float
	 * @roseuid 3B02F4C400A2
	 */
	public float formatDBValue(String value) {
		return Float.parseFloat( value );
	}

	/**
	 * @return boolean
	 * @roseuid 3B02F4C400B6
	 */
	protected boolean validate() {
		boolean valid = _metaData.getType() == ColumnDataType._CDT_FLOAT ? true : false;

		if (!valid)
			throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

		return valid;
	}
}
