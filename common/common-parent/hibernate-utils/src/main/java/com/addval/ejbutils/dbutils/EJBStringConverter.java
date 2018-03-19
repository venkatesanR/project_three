//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBStringConverter.java

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
 * A converter used by the EJBResultSet for retrieving and storing formatted
 * Strings
 */
public class EJBStringConverter extends EJBConverter {
	private static final String _module = "EJBStringConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF058A10168
	 */
	public EJBStringConverter(EJBColumn column, ColumnMetaData metaData, String format) {
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF058A10140
	 */
	public EJBStringConverter(EJBColumn column, ColumnMetaData metaData) {
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3AF058A10190
	 */
	public void setValue(String value) {
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setStrValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF058A101A4
	 */
	public String getString() {
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getStrValue() );
	}

	/**
	 * @param value[]
	 * @roseuid 3AF7489A00EF
	 */
	public void setBytes(byte value[]) {
        validate();

        _column.setStrValue( new String(value) );
	}

	/**
	 * @return byte[]
	 * @roseuid 3AF6DFB1031B
	 */
	public byte[] getBytes() {
        validate();

        return _column.getStrValue().getBytes();
	}

	/**
	 * @param value
	 * @param format
	 * @return java.lang.String
	 * @roseuid 3AF058A10226
	 */
	public String formatDBValue(String value, String format) {
        return value;
	}

	/**
	 * @return boolean
	 * @roseuid 3AFC3F39020F
	 */
	protected boolean validate() {
        boolean valid = (_metaData.getType() == ColumnDataType._CDT_STRING || _metaData.getType() == ColumnDataType._CDT_USER || _metaData.getType() == ColumnDataType._CDT_FILE) ? true : false;
        valid = valid || _metaData.getType() == ColumnDataType._CDT_CLOB || _metaData.getType() == ColumnDataType._CDT_BLOB;
        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
