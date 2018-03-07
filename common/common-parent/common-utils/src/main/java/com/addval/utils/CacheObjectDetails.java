//Source file: D:\\projects\\COMMON\\src\\com\\addval\\utils\\CacheDetails.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.utils;

import java.io.Serializable;

public class CacheObjectDetails implements Serializable
{
	private String _cacheObjectName = null;
	private String _className = null;
	private boolean _initilized = false;
	private int _size= 0;

	/**
	 * @param cacheName
	 * @param initilized
	 * @roseuid 3FB507AB0286
	 */
	public CacheObjectDetails(String cacheObjectName,String className)
	{
		_cacheObjectName = cacheObjectName;
		_className = className;
	}

	public String getCacheObjectName()
	{
		return _cacheObjectName;
	}

	public String getClassName()
	{
		return _className;
	}


	public boolean isInitilized()
	{
		return _size > 0;
	}

	public void setSize(int size)
	{
		_size = size;
	}

	public int getSize()
	{
		return _size;
	}

}
