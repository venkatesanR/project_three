//Source file: d:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\Converter.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;


/**
 * This calss is the base class for all the converter classes
 * like IntConverter,StringConveter,DateConverter
 * 
 * @author AddVal Technology Inc.
 */
public class Converter {
	protected String _tableName;
	protected String _columnName;
	
	/**
	 * Can this column contain NULL values?
	 */
	protected boolean _nullable;
	
	/**
	 * @roseuid 3C6876F401C6
	 */
	public Converter() {
		
	}
	
	/**
	 * Constructor. 
	 * 
	 * @param tableName String
	 * @param colName  String
	 * @param nullable boolean(Indicate if the column have NULL values.)
	 * @param columnName
	 * @roseuid 37935D8002D0
	 */
	public Converter(String tableName, String columnName, boolean nullable) {
      _tableName  = tableName;
      _columnName = columnName;
      _nullable   = nullable;		
	}
	
	/**
	 * Converts the input string into a valid output string (checking to see if the 
	 * value is NULL.)
	 * 
	 * @param strContent String
	 * @return Valid output string - String
	 * @roseuid 376AB3B60322
	 */
	public String convert(String strContent) {
      if ( isNull( strContent ) && canContainNull() )
         return "null";
      else
         return strContent;
         //throw new XRuntime( getClass().getName(), "Error: trying to set required value to null for column " + m_tableName + "." + m_columnName );		
	}
	
	/**
	 * Returns true if argument can be interpreted as SQL NULL.
	 * 
	 * @param val - String
	 * @return true if the input string is either null or of 0 length. False otherwise.
	 * @roseuid 37935DFF0160
	 */
	protected boolean isNull(String val) {
      return ( val == null || val.length() == 0 );		
	}
	
	/**
	 * Returns true if the column represented by this converter can contain SQL NULL.
	 * 
	 * @return Returns true if the column represented by this converter can contain 
	 * SQL NULL
	 * @roseuid 37C1AA3603BA
	 */
	protected boolean canContainNull() {
      return _nullable == true;		
	}
}
