//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBDoubleConverter.java

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
 * A converter used by the EJBResultSet for converting formatted Strings to double
 * data and vice-versa
 */
public class EJBDoubleConverter extends EJBConverter {
	private static final String _module = "EJBDoubleConverter";

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF062760008
	 */
	public EJBDoubleConverter(EJBColumn column, ColumnMetaData metaData, String format) {
        super( column, metaData, format );
	}

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF0627503C8
	 */
	public EJBDoubleConverter(EJBColumn column, ColumnMetaData metaData) {
        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param value
	 * @roseuid 3AF062760030
	 */
	public void setValue(String value) {
        validate( value );

        if (value == null || value.length() == 0)
            _column.setNull();
        else
            _column.setDblValue( formatDBValue( value, _format) );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF062760045
	 */
	public String getString() {
        validate();

        return _column.isNull() || !_column.isAvailable() ? null : formatDisplayValue( _column.getDblValue() );
	}

	/**
	 * @param value
	 * @roseuid 3AF062760063
	 */
	public void setDouble(double value) {
        validate();

        _column.setDblValue( value );
	}

	/**
	 * @return double
	 * @roseuid 3AF062760081
	 */
	public double getDouble() {
        validate();

        return _column.getDblValue();
	}

	/**
	 * @param value
	 * @param format
	 * @return double
	 * @roseuid 3AF06276009F
	 */
	public double formatDBValue(String value, String format) {
        return Double.parseDouble( value );
	}

	/**
	 * @return boolean
	 * @roseuid 3AFC815A0095
	 */
	protected boolean validate() {
        boolean valid = _metaData.getType() == ColumnDataType._CDT_DOUBLE ? true : false;

        if (!valid)
            throw new XRuntime( _module, "Invalid Type : " + _metaData.getType() );

        return valid;
	}
}
