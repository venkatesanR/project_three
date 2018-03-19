//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBConverterFactory.java

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
 * A factory object that returns the appropriate EJBConverter based on the column
 * type defined in the ColumnMetaData
 */
public class EJBConverterFactory {

	/**
	 * @param column
	 * @param metaData
	 * @return com.addval.ejbutils.dbutils.EJBConverter
	 * @roseuid 3AFB0C6F00FD
	 */
	public static EJBConverter getConverter(EJBColumn column, ColumnMetaData metaData) {

        switch ( metaData.getType() ) {

            case ColumnDataType._CDT_CHAR :
            case ColumnDataType._CDT_BOOLEAN :
                return new EJBCharConverter( column, metaData );

            case ColumnDataType._CDT_STRING :
            case ColumnDataType._CDT_USER:
            case ColumnDataType._CDT_FILE:
            case ColumnDataType._CDT_CLOB :
            	return new EJBStringConverter( column, metaData );
            case ColumnDataType._CDT_BLOB :
            	return new EJBStringConverter( column, metaData );
            case ColumnDataType._CDT_SHORT :
                return new EJBShortConverter( column, metaData );

            case ColumnDataType._CDT_INT :
                return new EJBIntConverter( column, metaData );

            case ColumnDataType._CDT_LONG :
            case ColumnDataType._CDT_VERSION :
                return new EJBLongConverter( column, metaData );

            case ColumnDataType._CDT_FLOAT :
                return new EJBFloatConverter( column, metaData );

            case ColumnDataType._CDT_DOUBLE :
                return new EJBDoubleConverter( column, metaData );

            case ColumnDataType._CDT_DATE :
                return new EJBDateConverter( column, metaData );

            case ColumnDataType._CDT_TIME :
                return new EJBTimeConverter( column, metaData );

            case ColumnDataType._CDT_DATETIME :
            case ColumnDataType._CDT_TIMESTAMP :
                return new EJBDateTimeConverter( column, metaData );

            case ColumnDataType._CDT_DOW :
                return new EJBDowConverter( column, metaData );

            case ColumnDataType._CDT_NOTYPE :
                return null;

            case ColumnDataType._CDT_LINK :
                return null;

            case ColumnDataType._CDT_KEY :
                return new EJBKeyConverter( column, metaData );

            default:
                throw new XRuntime( "EJBConverterFactory.getConverter()", "No Such Field Converter Defined : " + column.getName() + " for : " + metaData.getType() );
        }
	}
}
