package com.addval.udf.api;

import java.util.Map;

/**
 * This is the interface that must be implemented by UdfEnabled application classes
 * in order to enable persistence.
 *
 * PersistenceContext must match one of the configured values (in the UdfDomainDefinition).
 * It is recommended that the name of the appropriate db table be used as the persistenceContext.
 *
 * Example persistenceContext values for a UdfPersistable "Booking" object:
 *		"BOOKING", "BOOKING_HIST", "EV_BOOKING", "EV_BOOKING_HIST".
 */
public interface UdfEnabledPersistable extends UdfEnabled
{
	/**
	 * The UdfEnabledPersistable application class needs to implement this method,
	 * which provides the values for its PRIMARY KEY columns.
	 *
	 * The Map returned is:  Map<String PKColumnName, Object PKValue).
	 * PKValue can be String, Integer, or Long.
	 */
	public Map<String,Object> getPersistenceKeys(String persistenceContext);
}
