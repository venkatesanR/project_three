package com.addval.udf.api;

/**
 * This establishes enumerated constants and Strings for all UDF value types:
 * UdfValueType.UNDEFINED  	"UNDEFINED"
 * UdfValueType.STRING  	"STRING"
 * UdfValueType.BOOLEAN		"BOOLEAN"
 * UdfValueType.INTEGER		"INTEGER"
 * UdfValueType.DOUBLE		"DOUBLE"
 * UdfValueType.DATE		"DATE"
 *
 * Application logic should not be coded to have knowledge of the String values.
 * Application logic should be coded to the "enum constants", e.g. UdfValueType.BOOLEAN.
 *
 * The associated String values are intended mainly for use in XML interface elements
 * and debug output.
 *
 * Example:
 *		TO BE ADDED
 *
 * NOTE: com.addval.udf.impl.UdfIntValueType provides access to the associated integer values.
 * Access to the integer values should normally be restricted to application (or common/utility)
 * code that is responsible for persisting a UdfValueType, e.g. to a database NUMBER column.
 *
 * @@@ TODO:  is it a good idea to have UNDEFINED?  Why not just use null instead?
 */
public enum UdfValueType {

		UNDEFINED("UNDEFINED"),
		STRING("STRING"),
		BOOLEAN("BOOLEAN"),
		INTEGER("INTEGER"),
		DOUBLE("DOUBLE"),
		DATE("DATE");

	private String _valueType;

	private UdfValueType(String valueType) {
		_valueType = valueType;
	}

	public String getType() {
		return _valueType;
	}

	public String toString() {
		return _valueType;
	}

	public static UdfValueType fromString(String typeString) {

		if (UdfValueType.STRING.toString().equals(typeString))
			return UdfValueType.STRING;
		else if (UdfValueType.BOOLEAN.toString().equals(typeString))
			return UdfValueType.BOOLEAN;
		else if (UdfValueType.INTEGER.toString().equals(typeString))
			return UdfValueType.INTEGER;
		else if (UdfValueType.DOUBLE.toString().equals(typeString))
			return UdfValueType.DOUBLE;
		else if (UdfValueType.DATE.toString().equals(typeString))
			return UdfValueType.DATE;
		else
			return UdfValueType.UNDEFINED;
	}

}
