package com.addval.udf.api;

import com.addval.utils.StrUtl;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class implements a "user-defined field". It must always have
 *	(a) Name
 *	(b) Type
 * and optionally can also have
 *	(c) Value
 *
 * Each of the three attrributes (Name, Type, Value) are immutable in nature.
 * Once any of these attributes has been set to a non-null value, it cannot be changed.
 *
 * When constructing a new instance and setting the Type and Value:
 * 	(a)	If Type is set first, when Value is set an exception will be thrown if it does not match the Type.
 * 	(b)	If Value is set first, that will cause Type to be set automatically, based on the type of Value.
 *		You can still do setType(xx) after having set the Value, but when you do, the Type you
 *		specify must match the type of the Value.
 *
 * This class enforces strict consistency between Type and value Object at all times.
 * No type conversions are supported (except for get<primitive>Value methods for int, boolean, and double).
 *
 * Any code that calls any of the "typed" get*Value methods (i.e. other than public Object getValue())
 * is responsible for knowing the Type and calling only the compatible get*Value methods
 *
 * If the wrong get<Typed>Value method is called, a UdfApplicationUsageException will be thrown.
 *
 * NOTE: it is not legal to set a String value for any Type other than STRING.
 */
public class UdfField implements Serializable
{
	private String			_fieldName;
	private UdfValueType	_fieldValueType;
	private Object       	_fieldValueObject;
	private UdfFieldMissingValueRequest	_missingValueRequest;


	// -------- toString ---------

	public String toString() {
		StringBuffer sb = new StringBuffer("UdfField[");
		sb.append("name=").append(_fieldName);
		if (_fieldValueType != null) sb.append(",type=").append(_fieldValueType);
		if (hasNonNullValue())
			sb.append("(").append(_fieldValueObject).append(")");
		else
			sb.append("(null)");
		if (isSetMissingValueRequest())
			sb.append(", missingValueRequest=").append(_missingValueRequest);

		sb.append("]");
		return sb.toString();
	}

	// -------- Constructors ---------

	public UdfField() {
	}

	public UdfField(String fieldName) {
		setName(fieldName);
	}

	public UdfField(String fieldName, UdfValueType valueType) {
		setName(fieldName);
		setType(valueType);
		// Value can be set later, but it must have the specified valueType.
	}

	public UdfField(String fieldName, Object fieldValue) {
		this(fieldName);
		setValue(fieldValue);
		// Type will be determined based on the fieldValue, if it is non-null.
	}

	public UdfField(String fieldName, UdfValueType valueType, Object fieldValue) {
		this(fieldName,valueType);
		setValue(fieldValue);
		// Exception will be thrown of the fieldValue does not match the specified valueType.
	}

	/**
	 * This constructor is designed for use when reading a persisted UdfField from a database that stores its name, type, and value
	 * where the value is always stored as a String.
	 */
	public UdfField(String fieldName, int intValueType, String persistenceStringValue) {
		setName(fieldName);
		setType(intValueType);
		setValueFromPersistenceString(persistenceStringValue);
		// Exception will be thrown of the persistenceStringValue cannot be parsed for the specified value type.
	}


	// -------- "persistenceStringValue" set/get methods, for use by DAO layer ---------

	public void setValueFromPersistenceString(String persistenceStringValue) {
		Object valueObject = null;
		if (this.getType() == null) {
			throw new UdfApplicationUsageException("UdfField, setValueFromPersistenceString cannot be called before setType been called");
		}
		if (StrUtl.isEmptyTrimmed(persistenceStringValue)) {
			throw new UdfApplicationUsageException("UdfField, setValueFromPersistenceString: persistenceStringValue cannot be empty or null String");
		}

		switch(this.getType()) {
			case STRING:     valueObject = persistenceStringValue;
                             break;
			case BOOLEAN:    valueObject = Boolean.valueOf(persistenceStringValue);
                             break;
			case INTEGER:    valueObject = Integer.valueOf(persistenceStringValue);
                             break;
			case DOUBLE:     valueObject = Double.valueOf(persistenceStringValue);
                             break;
			case DATE:       valueObject = new Date( Long.valueOf(persistenceStringValue).longValue() );	// NOTE: Dates are persisted as String representation of milisecs since 1/1/1970, i.e. date.getTime()
                             break;
		}
		setValue(valueObject);
		// Exception will be thrown of the valueObject does not match the specified intValueType.
	}

	public String getPersistenceStringValue() {
		String persistenceStringValue = null;
		if (this.getValue() == null) {
			throw new UdfApplicationUsageException("UdfField, setValueFromPersistenceString cannot be called before setType been called");
		}

		switch(this.getType()) {
			case STRING:     persistenceStringValue = this.getStringValue();
                             break;
			case BOOLEAN:    persistenceStringValue = this.getBooleanValue().toString();
                             break;
			case INTEGER:    persistenceStringValue = this.getIntegerValue().toString();
                             break;
			case DOUBLE:     persistenceStringValue = this.getDoubleValue().toString();
                             break;
			case DATE:       persistenceStringValue = new Long( this.getDateValue().getTime() ).toString();	// NOTE: Dates are persisted as String representation of milisecs since 1/1/1970, i.e. date.getTime()
                             break;
		}
		return persistenceStringValue;
	}


	// -------- "Test value" methods ---------

	/**
	 * Returns true IFF the field exists and has a non-null value.
	 */
	public boolean hasNonNullValue() {
		return getValue() != null;
	}

	public boolean isSetStringValue() {
		return hasNonNullValue() && getType().equals(UdfValueType.STRING);
	}

	public boolean isSetBooleanValue() {
		return hasNonNullValue() && getType().equals(UdfValueType.BOOLEAN);
	}

	public boolean isSetIntegerValue() {
		return hasNonNullValue() && getType().equals(UdfValueType.INTEGER);
	}

	public boolean isSetDoubleValue() {
		return hasNonNullValue() && getType().equals(UdfValueType.DOUBLE);
	}

	public boolean isSetDateValue() {
		return hasNonNullValue() && getType().equals(UdfValueType.DATE);
	}

	// -------- get/set Name methods ---------

	public String getName() {
		return _fieldName;
	}

	public void setName(String fieldName) {
		if (fieldName == null) {
			throw new UdfApplicationUsageException("UdfField, name cannot be set to null");
		}
		if (_fieldValueType != null && !_fieldValueType.equals(fieldName)) {
			throw new UdfApplicationUsageException("UdfField, name cannot be changed after it has been set");
		}
		_fieldName = fieldName;
	}


	// -------- get/set Type methods ---------

	public UdfValueType getType() {
		return _fieldValueType;
	}

	public void setType(UdfValueType valueType) {
		validateNewType(valueType);
		_fieldValueType = valueType;
		enforceTypeValueConsistency();
	}

	/**
	 * This method is intended for use when creating UdfField instance from persisted data.
	 */
	public void setType(int intType) {
		UdfValueType valueType = UdfIntValueType.fromInt(intType);
		validateNewType(valueType);
		_fieldValueType = valueType;
		enforceTypeValueConsistency();
	}

	/**
	 * @@@ IS THIS METHOD NEEDED ANYWHERE?
	 */
	public void setType(String stringType) {
		UdfValueType valueType = UdfValueType.fromString(stringType);
		validateNewType(valueType);
		_fieldValueType = valueType;
		enforceTypeValueConsistency();
	}

	// -------- setValue methods ---------

	/**
	 * Null value is allowed. Argument can be String, Boolean, Integer, Double, or Date.
	 * If type has not already been set, type will be set to match the type of the value argument.
	 */
	public void setValue(Object valueObject) {
		validateValue(valueObject);
		_fieldValueObject = valueObject;
		enforceTypeValueConsistency();
		clearMissingValueRequest();
	}

	public void setValue(boolean booleanValue) {
		_fieldValueObject = new Boolean(booleanValue);
		enforceTypeValueConsistency();
		clearMissingValueRequest();
	}

	public void setValue(int intValue) {
		_fieldValueObject = new Integer(intValue);
		enforceTypeValueConsistency();
		clearMissingValueRequest();
	}

	public void setValue(double doubleValue) {
		_fieldValueObject = new Double(doubleValue);
		enforceTypeValueConsistency();
		clearMissingValueRequest();
	}

	// -------- get*Value methods, returns Object of specified type ---------

	public Object getValue() {
		return _fieldValueObject;
	}

	public String getStringValue() {
		if (!UdfValueType.STRING.equals(_fieldValueType)) {
			throw new UdfApplicationUsageException("UdfEasyAccess.getStringValue() not supported when type is " + _fieldValueType);
		}
		return (String) _fieldValueObject;
	}

	public Boolean getBooleanValue() {
		if (!UdfValueType.BOOLEAN.equals(_fieldValueType)) {
			throw new UdfApplicationUsageException("UdfEasyAccess.getBooleanValue() not supported when type is " + _fieldValueType);
		}
		return (Boolean) _fieldValueObject;
	}

	public Integer getIntegerValue() {
		if (!UdfValueType.INTEGER.equals(_fieldValueType)) {
			throw new UdfApplicationUsageException("UdfEasyAccess.getIntegerValue() not supported when type is " + _fieldValueType);
		}
		return (Integer) _fieldValueObject;
	}

	public Double getDoubleValue() {
		if (!UdfValueType.DOUBLE.equals(_fieldValueType)) {
			throw new UdfApplicationUsageException("UdfEasyAccess.getDoubleValue() not supported when type is " + _fieldValueType);
		}
		return (Double) _fieldValueObject;
	}

	public Date getDateValue() {
		if (!UdfValueType.DATE.equals(_fieldValueType)) {
			throw new UdfApplicationUsageException("UdfEasyAccess.getDateValue() not supported when type is " + _fieldValueType);
		}
		return (Date) _fieldValueObject;
	}



	// -------- *Value methods, returns primitive value of specified type ---------

	public boolean booleanValue() {
		if (_fieldValueObject == null) {
			throw new UdfApplicationUsageException("UdfEasyAccess.booleanValue() not supported when value is null");
		}
		return getBooleanValue().booleanValue();
	}

	public int intValue() {
		if (_fieldValueObject == null) {
			throw new UdfApplicationUsageException("UdfEasyAccess.intValue() not supported when value is null");
		}
		return getIntegerValue().intValue();
	}

	public double doubleValue() {
		if (_fieldValueObject == null) {
			throw new UdfApplicationUsageException("UdfEasyAccess.doubleValue() not supported when value is null");
		}
		return getDoubleValue().doubleValue();
	}


	// -------- private methods used to enforce immutability and type/value consistency ---------

	private void validateValue(Object valueObject) {
		if (_fieldValueObject != null && !_fieldValueObject.equals(valueObject)) {
			throw new UdfApplicationUsageException("UdfField, value cannot be changed after it has been set to a non-null value");
		}
	}

	private void validateNewType(UdfValueType newValueType) {
		if (newValueType == null) {
			throw new UdfApplicationUsageException("UdfField, type cannot be set to null");
		}
		if (_fieldValueType != null && !_fieldValueType.equals(newValueType)) {
			throw new UdfApplicationUsageException("UdfField, type cannot be changed after it has been set");
		}
	}

	private void enforceTypeValueConsistency() {
		if (_fieldValueType == null && _fieldValueObject == null) {
			throw new UdfApplicationUsageException("UdfField, type/value inconsistency, both are null");
		}
		else if (_fieldValueType == null && _fieldValueObject != null) {
			// deduce the Type from the Value, and set the Type
			_fieldValueType = determineTypeFromValue( _fieldValueObject );
		}
		else if (_fieldValueType != null && _fieldValueObject != null) {
			// check that Type and Value are consistent
			UdfValueType typeOfValue = determineTypeFromValue( _fieldValueObject );
			if (!typeOfValue.equals(_fieldValueType)) {
				throw new UdfApplicationUsageException("UdfField, type/value inconsistency, specified type " + _fieldValueType + " does not match value's type " + typeOfValue);
			}
		}
	}

	private UdfValueType determineTypeFromValue(Object valueObject) {
		if (valueObject == null)
			throw new UdfApplicationUsageException("UdfField, BUG: deduceTypeFromValue should not be called with a null Value argument");
		else if (valueObject instanceof String)
			return UdfValueType.STRING;
		else if (valueObject instanceof Boolean)
			return UdfValueType.BOOLEAN;
		else if (valueObject instanceof Integer)
			return UdfValueType.INTEGER;
		else if (valueObject instanceof Double)
			return UdfValueType.DOUBLE;
		else if (valueObject instanceof Date)
			return UdfValueType.DATE;
		else
			throw new UdfApplicationUsageException("UdfField.determineTypeFromValue, unknown UdfValueType for valueObject class " + valueObject.getClass().getName());
	}


	// -------- get/set and other methods for UdfFieldMissingValueRequest ---------

	public boolean isSetMissingValueRequest() {
		return _missingValueRequest != null;
	}

	public UdfFieldMissingValueRequest getMissingValueRequest() {
		return _missingValueRequest;
	}

	public void setMissingValueRequest(UdfFieldMissingValueRequest missingValueRequest) {
		if (hasNonNullValue()) {
			throw new UdfApplicationUsageException("UdfField, cannot setMissingValueRequest, non-null value exists");
		}
		_missingValueRequest = missingValueRequest;
	}

	private void clearMissingValueRequest() {
		if (hasNonNullValue() && isSetMissingValueRequest() ) {
			setMissingValueRequest(null);
		}
	}

}
