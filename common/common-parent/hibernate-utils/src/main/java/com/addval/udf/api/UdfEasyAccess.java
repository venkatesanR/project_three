package com.addval.udf.api;

import java.util.Map;
import java.util.Date;

/**
 * This interface is implemented by UdfHolder, to provide convenient methods for accessing UdfField information.
 *
 * Any application class that implements UdfEnabled can also implement this interface,
 * to provide convenient methods for the rest of the application code to use.
 */
public interface UdfEasyAccess
{

	//------------ Single "put" method ----------

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
	public UdfField putField(String fieldName, Object fieldValue);


	//------------ "Test" methods ----------

	/**
	 * Returns true IFF the field exists.
	 */
	public boolean fieldExists(String fieldName);

	/**
	 * Returns true IFF the field exists and has a non-null value.
	 */
	public boolean hasNonNullFieldValue(String fieldName);


	//------------ get Type method ----------

	/**
	 * Returns the specified field's UdfValueType (e.g. UdfValueType.DOUBLE),
	 * or null if the specified fieldName is not found.
	 */
	public UdfValueType getFieldType(String fieldName);


	//------------ get Value methods ----------
	/**
	 * Returns the specified field's value as Object.
	 */
	public Object getFieldValue(String fieldName);

	/**
	 * Returns the specified field's String value.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.STRING.
	 */
	public String getFieldStringValue(String fieldName);

	/**
	 * Returns the specified field's Boolean value.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.BOOLEAN.
	 */
	public Boolean getFieldBooleanValue(String fieldName);

	/**
	 * Returns the specified field's Integer value.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.INTEGER.
	 */
	public Integer getFieldIntegerValue(String fieldName);

	/**
	 * Returns the specified field's Double value.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.DOUBLE.
	 */
	public Double getFieldDoubleValue(String fieldName);


	/**
	 * Returns the specified field's Date value.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.DATE.
	 */
	public Date getFieldDateValue(String fieldName);

	/**
	 * Returns the specified field's Boolean value, as primitive boolean.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.BOOLEAN
	 * or if the value is null.
	 */
	public boolean getFieldbooleanValue(String fieldName);

	/**
	 * Returns the specified field's Integer value, as primitive int.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.INTEGER
	 * or if the value is null.
	 */
	public Integer getFieldintValue(String fieldName);

	/**
	 * Returns the specified field's Double value, as primitive double.
	 * Throws UdfApplicationUsageException is the UdField's type is not UdfValueType.DOUBLE
	 * or if the value is null.
	 */
	public Double getFielddoubleValue(String fieldName);

}
