package com.addval.udfutils;

public enum AttributeType {
	UNDEFINED(0), INTEGER(1), DOUBLE(2), DATE(3), BOOLEAN(4), STRING(5);

	private int _type;

	private AttributeType(int type) {
		_type = type;
	}

	 public int toInt() { return _type; }

	 public static AttributeType fromInt(int value) {

		 switch(value) {
		 	case 0: return UNDEFINED;

		 	case 1: return INTEGER;

		 	case 2: return DOUBLE;

		 	case 3: return DATE;

		 	case 4: return BOOLEAN;

		 	case 5: return STRING;

		 	default: return UNDEFINED;
		 }

	 }

	public int getType() {
		return _type;
	}

}
