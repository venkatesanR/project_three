//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBIntConverter.java

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
 * A converter used by the EJBResultSet for converting formatted Strings to int
 * data and vice-versa
 */
public class EJBIntConverter extends EJBConverter {
	private static final String _module = "EJBIntConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AEF63B701BC
	 */
	public EJBIntConverter(EJBColumn column, ColumnMetaData metaData, String format) {
		super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AEF630F0157
	 */
	public EJBIntConverter(EJBColumn column, ColumnMetaData metaData) {
		super( column, metaData );
	}

	/**
	 * @param value
	 * @roseuid 3AEF5B9501CD
	 */
	public void setValue(String value) {
		validate( value );

		if (value == null || value.length() == 0)
			_column.setNull();
		else
			_column.setIntValue( formatDBValue( value, _format ) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B9501EB
	 */
	public String getString() {
		validate();

		return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getIntValue() );
	}

	/**
	 * @param value
	 * @roseuid 3AF03D3C00EB
	 */
	public void setInt(int value) {
		validate();

		_column.setIntValue( value );
	}

	/**
	 * @return int
	 * @roseuid 3AF03D3C0109
	 */
	public int getInt() {
		validate();

		return _column.getIntValue();
	}

	/**
	 * @param value
	 * @param format
	 * @return int
	 * @roseuid 3AEF647D030C
	 */
	public int formatDBValue(String value, String format) {
		return Integer.parseInt( value );
	}

	/**
	 * @return boolean
	 * @roseuid 3AF08AF20232
	 */
	protected boolean validate() {
		boolean valid = (_metaData.getType() == ColumnDataType._CDT_INT )? true : false;

		if (!valid)
			throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

		return valid;
	}
}
