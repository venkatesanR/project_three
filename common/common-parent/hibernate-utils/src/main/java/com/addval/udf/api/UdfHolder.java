package com.addval.udf.api;

import com.addval.utils.StrUtl;

import java.io.Serializable;
import java.util.*;


/**
 * An instance of this class holds UdfField objects for a UdfEnabled application object.
 *
 * This class is basically a Map<String,UdfField>, with UdfEasyAccess convenience methods
 * defined so that application code can perform basic operations on the UdfHolder itself,
 * and not have to deal with UdfField objects.
 *
 * In the Map<String,UdfField>, the String key is the "name" of the UdfField.
 * (Note: UdfField names must be unique within a given UdfDomain.)
 *
 * An application's UdfEnabled classes can also implement UdfEasyAccess themselves, if desired,
 * to make the same convenience methods available to the rest of the application.
 *
 * Example 1: An application class that implements UdfEnabled ONLY
 * 		public class Booking implements UdfEnabled { ... }
 *
 *		<somewhere in application that is using a Booking instance>
 *		if ((aBooking.getUdfHolder().get("PET_NAME") != null)
 *				&& (aBooking.getUdfHolder().get("PET_NAME").get() != null {
 *				&& (aBooking.getUdfHolder().get("PET_NAME").get().getValue() != null {
 *			String petName = (String) (aBooking.getUdfHolder().get("PET_NAME").get().getValue());
 *			....
 *
 * Example 2: An application class implements BOTH UdfEnabled AND UdfEasyAccess
 * 		public class Booking implements UdfEnabled, UdfEasyAccess { ... }
 *
 *		<somewhere in application that is using a Booking instance>
 *		String petName = aBooking.getUdfStringValue("PET_NAME");
 *			....
 */
public class UdfHolder implements java.util.Map<String, UdfField>, UdfEasyAccess, Serializable
{
	private Map<String, UdfField> _udfMap;				// the primary attribute.  Must be created and "held" by its UdfEnabledObject.

	private UdfEnabled _theUdfEnabledAppObject;			// is never null

	private UdfEnabled _theParentUdfEnabledAppObject;	// can be null
	private List<UdfHolder> _childUdfHolderList;

	//--------------------------------------  Static factory methods for use by application --------------------------------------

	/**
	 * Static factory method to be used by application code, to enable a "top-level" app object.
	 *
	 * Example:
	 *		App object "Booking" has children "BookingLine" app objects, and both are UdfEnabled.
	 *
	 *		After constructing a new Booking instance, the code should use:
	 *			UdfHolder.enable(booking);
	 *
	 *		After constructing a new BookingLine instance for the Booking, the code should use:
	 *			UdfHolder.enable(bookingLine, booking);
	 */
	public static void enable(UdfEnabled appObject) {
		if (appObject == null) {
			throw new UdfApplicationUsageException("UdfHolder.enable(appObject): appObject cannot be null");
		}
		appObject.setUdfHolder( new UdfHolder(appObject) );
	}

	/**
	 * Static factory method to be used by application code, to enable a "child" app object.
	 *
	 * Example:
	 *		App object "Booking" has children "BookingLine" app objects, and both are UdfEnabled.
	 *
	 *		After constructing a new Booking instance, the code should use:
	 *			UdfHolder.enable(booking);
	 *
	 *		After constructing a new BookingLine instance for the Booking, the code should use:
	 *			UdfHolder.enable(bookingLine, booking);
	 */
	public static void enable(UdfEnabled childAppObject, UdfEnabled parentAppObject) {
		if (childAppObject == null) {
			throw new UdfApplicationUsageException("UdfHolder.enable(childAppObject,parentAppObject): childAppObject cannot be null");
		}
		if (parentAppObject == null) {
			throw new UdfApplicationUsageException("UdfHolder.enable(childAppObject,parentAppObject): parentAppObject cannot be null");
		}
		if (parentAppObject.getUdfHolder() == null) {
			throw new UdfApplicationUsageException("UdfHolder.enable(childAppObject,parentAppObject): parentAppObject has not yet been enabled");
		}
		UdfHolder childUdfHolder = new UdfHolder(childAppObject);
		childAppObject.setUdfHolder( new UdfHolder(childAppObject) );
		childUdfHolder.setParentAppObject( parentAppObject );
	}


	//--------------------------------------  Constructors --------------------------------------

	/**
	 * Note: the constructor is PROTECTED.  Application code should instead use the static
	 * factory methods:
	 *		enable(UdfEnabled)			for the "top-level" enabled app object
	 * or
	 *		enable(UdfEnabled,UdfEnabled)		for a "child" enabled app object
	 *
	 * There is always a 1-to-1 relationship between a UdfEnabled object and its UdfHolder.
	 */
	protected UdfHolder(UdfEnabled theUdfEnabledAppObject) {
		_udfMap = new HashMap<String, UdfField>();
		_theUdfEnabledAppObject = theUdfEnabledAppObject;
	}

	/**
	 * Returns the UdfEnabled application object; i.e. the app object that "has" the UdfHolder.
	 */
	public UdfEnabled getEnabledAppObject() {
		return _theUdfEnabledAppObject;
	}

	//--------------------------------------  Parent App Object get/set --------------------------------------

	public void setParentAppObject(UdfEnabled parentObject) {
		_theParentUdfEnabledAppObject = parentObject;
	}

	public UdfEnabled getParentAppObject() {
		return _theParentUdfEnabledAppObject;
	}



	//--------------------------------------  UdfEasyAccess methods --------------------------------------

	//----- UdfEasyAccess "put" method

	/**
	 * Creates a UdfField instance having the specified name and value,and adds it to the map.
	 * The new UdfField's UdfValueType will be determined based on the specified fieldValue's object type.
	 *
	 * For example, if putField("CURRENT_DATE", new Date()) is called, the new UdfField will have:
	 *	Name = "CURRENT_DATE"
	 *	Type = UdfValueType.DATE
	 *	Value = Date with current system date/time.
	 *
	 * If the map already contains a UdfField with the specified fieldName, the existing
	 * UdfField will be replaced by the new instance, and the old instance will be
	 * returned.
	 */
	public UdfField putField(String fieldName, Object fieldValue) {
		if (StrUtl.isEmptyTrimmed(fieldName)) {
			throw new UdfApplicationUsageException("UdfEasyAccess.putField() fieldName cannot be null/empty");
		}
		UdfField newUdfField = new UdfField(fieldName,fieldValue);
		return _udfMap.put(fieldName, newUdfField);
	}

	//----- UdfEasyAccess "test" methods

	/**
	 * Returns true IFF the field exists.
	 */
	public boolean fieldExists(String fieldName) {
		return get(fieldName) != null;
	}

	/**
	 * Returns true IFF the field exists and has a non-null value.
	 */
	public boolean hasNonNullFieldValue(String fieldName) {
		return fieldExists(fieldName) && get(fieldName).hasNonNullValue();
	}

	//----- UdfEasyAccess "get" methods

	/**
	 * Returns the specified field's UdfValueType (e.g. UdfValueType.DOUBLE),
	 * or null if the specified fieldName is not found.
	 */
	public UdfValueType getFieldType(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getType();
		else
			return null;
	}

	public Object getFieldValue(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getValue();
		else
			return null;
	}

	public String getFieldStringValue(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getStringValue();
		else
			return null;
	}

	public Boolean getFieldBooleanValue(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getBooleanValue();
		else
			return null;
	}

	public Integer getFieldIntegerValue(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getIntegerValue();
		else
			return null;
	}

	public Double getFieldDoubleValue(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getDoubleValue();
		else
			return null;
	}

	public Date getFieldDateValue(String fieldName) {
		if (fieldExists(fieldName))
			return get(fieldName).getDateValue();
		else
			return null;
	}


	//----- UdfEasyAccess "get" methods, return primitive; throw exception if null value

	public boolean getFieldbooleanValue(String fieldName) {
		Boolean value = getFieldBooleanValue(fieldName);
		if (value == null)
			throw new UdfApplicationUsageException("UdfEasyAccess.getFieldbooleanValue() not supported when value is null");
		else
			return new Boolean(value);
	}

	public Integer getFieldintValue(String fieldName) {
		Integer value = getFieldIntegerValue(fieldName);
		if (value == null)
			throw new UdfApplicationUsageException("UdfEasyAccess.getFieldintValue() not supported when value is null");
		else
			return new Integer(value);
	}

	public Double getFielddoubleValue(String fieldName) {
		Double value = getFieldDoubleValue(fieldName);
		if (value == null)
			throw new UdfApplicationUsageException("UdfEasyAccess.getFielddoubleValue() not supported when value is null");
		else
			return new Double(value);
	}

	//--------------------------------------  java.util.Map methods --------------------------------------
	// Recall:  this class implements Map, for Map<String,UdfField>

	public void clear() {
		_udfMap.clear();
	}

	public boolean containsKey(Object key) {
		return _udfMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return _udfMap.containsValue(value);
	}

	public Set<Map.Entry<String,UdfField>> entrySet() {
		return _udfMap.entrySet();
	}

	public boolean equals(Object o) {
		return _udfMap.equals(o);
	}

	public UdfField get(Object key) {
		return _udfMap.get(key);
	}

	public int hashCode() {
		return _udfMap.hashCode();
	}

	public boolean isEmpty() {
		return _udfMap.isEmpty();
	}

	public Set<String> keySet() {
		return _udfMap.keySet();
	}

	public UdfField put(String key, UdfField value) {
		return _udfMap.put(key,value);
	}

	public void putAll(Map<? extends String,? extends UdfField> t) {
		throw new UdfApplicationUsageException("UdfHolder.putAll(...) method is not supported");
	}

	public UdfField remove(Object key) {
		return _udfMap.remove(key);
	}

	public int size() {
		return _udfMap.size();
	}

	public Collection<UdfField> values() {
		return _udfMap.values();
	}


	//--------------------------------------  Other UDF methods, requiring UdfDomainManager ????   --------------------------------------

	//@@@ TODO @@@@@@@@@@@@@@@ add "Validate*" = interface to UdfDomainManager.validate*

	//@@@ TODO @@@@@@@@@@@@@@@ add "Load**" = interface to UdfDomainManager.load*

}
