package com.addval.udf.api;

import java.io.Serializable;

/**
 * This class provides access to the integer values associated with each UdfValueType.
 *
 * This is primarily to enable persistence of a UdfValueType in a database column.
 *
 * IT IS DISCOURAGED FOR APPLICATION CODE TO CREATE ITS OWN INTEGER "CONSTANTS" THAT CORRESPOND
 * TO THE INTEGER VALUES ENCODED HERE.  APPLICATION LOGIC SHOULD BE CODED TO THE ENUMERATED
 * VALUES OF UdfValueType.
 */
public class UdfIntValueType implements Serializable {

	private int _intValueType;

	public UdfIntValueType(int intValueType) {
		_intValueType = intValueType;
	}

	public UdfIntValueType(UdfValueType udfValueType) {
		_intValueType = UdfIntValueType.toInt(udfValueType);
	}

	public int getType() {
		return _intValueType;
	}

	public int toInt() {
		return _intValueType;
	}

	public String toString() {
		return Integer.toString(_intValueType);
	}

	public UdfValueType toUdfValueType() {
		return UdfIntValueType.fromInt(_intValueType);
	}

	/**
	 * This static method returns the int value associated with the specified UdfValueType.
	 **/
	public static int toInt(UdfValueType udfValueType) {

		switch(udfValueType) {
			case UNDEFINED:  return 0;
			case STRING:     return 1;
			case BOOLEAN:    return 2;
			case INTEGER:    return 3;
			case DOUBLE:     return 4;
			case DATE:       return 5;
			default:         return 0;
		}
	}

	/**
	 * This static method returns a UdfValueType for the specified int value.
	 **/
	public static UdfValueType fromInt(int intValueType) {

		switch(intValueType) {
			case 0: return UdfValueType.UNDEFINED;
			case 1: return UdfValueType.STRING;
			case 2: return UdfValueType.BOOLEAN;
			case 3: return UdfValueType.INTEGER;
			case 4: return UdfValueType.DOUBLE;
			case 5: return UdfValueType.DATE;
			default:return UdfValueType.UNDEFINED;
		}
	}

}
