//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBLongConverter.java

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
 * A converter used by the EJBResultSet for converting formatted Strings to long
 * data and vice-versa
 */
public class EJBLongConverter extends EJBConverter {
	private static final String _module = "EJBLongConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF05918006E
	 */
	public EJBLongConverter(EJBColumn column, ColumnMetaData metaData, String format) {
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF05918005A
	 */
	public EJBLongConverter(EJBColumn column, ColumnMetaData metaData) {
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3AF05918008C
	 */
	public void setValue(String value) {
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setLongValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF0591800AB
	 */
	public String getString() {
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getLongValue() );
	}

	/**
	 * @param value
	 * @roseuid 3AF0591800D3
	 */
	public void setLong(long value) {
        validate();

        _column.setLongValue( value );
	}

	/**
	 * @return long
	 * @roseuid 3AF0591800E7
	 */
	public long getLong() {
        validate();

        return _column.getLongValue();
	}

	/**
	 * @param value
	 * @param format
	 * @return long
	 * @roseuid 3AF059180119
	 */
	public long formatDBValue(String value, String format) {
        return Long.parseLong( value );
	}

	/**
	 * @return boolean
	 * @roseuid 3AFC45CB02F4
	 */
	protected boolean validate() {
        boolean valid = _metaData.getType() == ColumnDataType._CDT_LONG || _metaData.getType() == ColumnDataType._CDT_VERSION ? true : false;

        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
