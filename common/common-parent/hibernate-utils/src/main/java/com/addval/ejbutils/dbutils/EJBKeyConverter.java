//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBKeyConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\Projects\AVOptimizer\source\com\addval\ejbutils\dbutils\EJBKeyConverter.java

/* AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;

/**
 * A converter used by the EJBResultSet for converting formatted Strings to
 * database key data and vice-versa. The keys that this converter deals with
 * should all be integers.
 */
public class EJBKeyConverter extends EJBConverter {
	private static final String _module = "EJBKeyConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3B39AA530077
	 */
	public EJBKeyConverter(EJBColumn column, ColumnMetaData metaData, String format) {
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3B39AA530063
	 */
	public EJBKeyConverter(EJBColumn column, ColumnMetaData metaData) {
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3B39AA530095
	 */
	public void setValue(String value) {
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setKeyValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B39AA53009F
	 */
	public String getString() {
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getKeyValue() );
	}

	/**
	 * @param value[]
	 * @roseuid 3B39AA5300C7
	 */
	public void setBytes(byte value[]) {
        validate();

        _column.setKeyValue( new String(value) );
	}

	/**
	 * @return byte[]
	 * @roseuid 3B39AA5300DC
	 */
	public byte[] getBytes() {
        validate();

        return _column.getKeyValue().getBytes();
	}

	/**
	 * @param value
	 * @param format
	 * @return java.lang.String
	 * @roseuid 3B39AA5300F0
	 */
	public String formatDBValue(String value, String format) {
        return value;
	}

	/**
	 * @return boolean
	 * @roseuid 3B39AA530104
	 */
	protected boolean validate() {
        boolean valid = _metaData.getType() == ColumnDataType._CDT_KEY ? true : false;
        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
