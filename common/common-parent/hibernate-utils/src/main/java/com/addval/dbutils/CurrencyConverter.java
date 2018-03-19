//Source file: D:/users/anitha/Projects/Common/src/client/source/com/addval/dbutils/CurrencyConverter.java

/* Copyright AddVal Technology Inc. */

package com.addval.dbutils;


public class CurrencyConverter extends Converter {
	
	/**
	   @roseuid 39C7A7550118
	 */
	public CurrencyConverter(String tableName, String columnName, boolean nullable) {

		super( tableName , columnName , nullable );
	}
	
	/**
	   @roseuid 39C7A7000057
	 */
	public static String formatDisplayValue(double value, String format) {

		java.text.DecimalFormat df = new java.text.DecimalFormat( format );
	    return df.format( value );
	}
	
	/**
	   @roseuid 39C7A71F02B5
	 */
	public static double formatDBValue(String value, String format) {

		return Double.parseDouble( value );
	}
}
