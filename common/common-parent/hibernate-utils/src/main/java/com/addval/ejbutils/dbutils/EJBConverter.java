//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBConverter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import com.addval.dbutils.DataConverter;
import com.addval.utils.XRuntime;
import com.addval.metadata.ColumnMetaData;

/**
 * An abstract class for all converters
 */
public class EJBConverter extends DataConverter {
	private static final String _module = "EJBConverter";
	protected EJBColumn _column = null;

	/**
	 * @param column
	 * @param metaData
	 * @roseuid 3AF0789D007B
	 */
	public EJBConverter(EJBColumn column, ColumnMetaData metaData) {

        this( column, metaData, metaData.getFormat() );
	}

	/**
	 * @param column
	 * @param metaData
	 * @param format
	 * @roseuid 3AF0784A0361
	 */
	public EJBConverter(EJBColumn column, ColumnMetaData metaData, String format) {

        super( metaData, format );

        _column = column;
	}

	/**
	 * @param value
	 * @roseuid 3AF026C1010E
	 */
	public void setValue(String value) {

        throw new XRuntime( _module, "Cannot Call the Base Class" );
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF026C40307
	 */
	public String getString() {

        throw new XRuntime( _module, "Cannot Call the Base Class" );
	}

	/**
	 * @return boolean
	 * @roseuid 3AF0850A030A
	 */
	protected boolean validate() {

        throw new XRuntime( _module, "Cannot Call the Base Class" );
	}

	/**
	 * @param value
	 * @return boolean
	 * @roseuid 3AF0851F03AB
	 */
	protected boolean validate(String value) {

        boolean valid = validate();

        valid = (value == null || value.length() == 0) ? (_metaData.isNullable() ? true : false ) : true;

        if (!valid)
            throw new XRuntime( _module, "Null not allowed for column : " + _metaData.getName() );

        return valid;
	}
}
