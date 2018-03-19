//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBCharConverter.java

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
 * A converter used by the EJBResultSet for converting formatted Strings to char
 * data and vice-versa
 */
public class EJBCharConverter extends EJBConverter {
	private static final String _module = "EJBCharConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF03F72004F
	 */
	public EJBCharConverter(EJBColumn column, ColumnMetaData metaData, String format) {
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF03F720031
	 */
	public EJBCharConverter(EJBColumn column, ColumnMetaData metaData) {
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3AF03F720063
	 */
	public void setValue(String value) {
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setStrValue( formatDBValue( value, _format )  );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF03F7200B3
	 */
	public String getString() {
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getCharValue() );
	}

	/**
	 * @param value
	 * @roseuid 3AF03F7200D1
	 */
	public void setChar(char value) {
        validate();

        _column.setCharValue( value );
	}

	/**
	 * @return char
	 * @roseuid 3AF03F7200F9
	 */
	public char getChar() {
        validate();

        return _column.getCharValue();
	}

	/**
	 * @param value
	 * @roseuid 3AF738910088
	 */
	public void setBoolean(boolean value) {
        validate();

        _column.setBoolValue( value );
	}

	/**
	 * @return boolean
	 * @roseuid 3AF0583A010F
	 */
	public boolean getBoolean() {
        validate();

        return _column.getBoolValue();
	}

	/**
	 * @param value
	 * @roseuid 3AF7390C0374
	 */
	public void setByte(byte value) {
        validate();

        _column.setByteValue( value );
	}

	/**
	 * @return byte
	 * @roseuid 3AF6C7D002D5
	 */
	public byte getByte() {
        validate();

        return _column.getByteValue();
	}

	/**
	 * @param value
	 * @param format
	 * @return java.lang.String
	 * @roseuid 3AF03F72010D
	 */
	public String formatDBValue(String value, String format) {
        return value == null ? value : String.valueOf( value.charAt( 0 ) );
	}

	/**
	 * @return boolean
	 * @roseuid 3AFC8324010C
	 */
	protected boolean validate() {
        boolean valid = (_metaData.getType() == ColumnDataType._CDT_CHAR || _metaData.getType() == ColumnDataType._CDT_BOOLEAN) ? true : false;

        if ( !valid )
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
