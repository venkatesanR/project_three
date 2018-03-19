//Source file: D:\\projects\\COMMON\\src\\com\\addval\\utils\\SystemDetails.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.utils;

import java.io.Serializable;
import java.util.Vector;

public class CacheDetails implements Serializable
{
	private String _cacheName = null;
	private Vector _cacheObjects = null;

	/**
	 * @param systemName
	 * @param caches
	 * @roseuid 3FB5069D021A
	 */
	public CacheDetails(String cacheName, Vector cacheObjects)
	{
		_cacheName = cacheName;
		_cacheObjects = cacheObjects;
	}

	/**
	 * Access method for the _systemName property.
	 *
	 * @return   the current value of the _systemName property
	 */
	public String getCacheName()
	{
		return _cacheName;
	}

	/**
	 * Sets the value of the _systemName property.
	 *
	 * @param aSystemName the new value of the _systemName property
	 */
	public void setCacheName(String aCacheName)
	{
		_cacheName = aCacheName;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3FB508C802D6
	 */
	public Vector getCacheObjects()
	{
		return _cacheObjects;
	}
}
