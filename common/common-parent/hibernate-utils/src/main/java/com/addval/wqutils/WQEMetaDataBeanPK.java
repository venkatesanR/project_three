//Source file: D:\\projects\\COMMON\\src\\com\\addval\\wqutils\\client\\WQEMetaDataBeanPK.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.wqutils;

import java.io.Serializable;

public class WQEMetaDataBeanPK implements Serializable
{
	private String _name = null;

	/**
	 * @param name
	 * @roseuid 3FDE4B720059
	 */
	public WQEMetaDataBeanPK(String name)
	{

		_name = name;
	}

	/**
	 * Access method for the _name property.
	 * @return   the current value of the _name property
	 * @roseuid 3FDE4B7200C7
	 */
	public String getName()
	{

		return _name;
	}

	/**
	 * @return int
	 * @roseuid 3FDE4B720103
	 */
	public int hashCode()
	{

		return _name.hashCode();
	}

	/**
	 * @param obj
	 * @return boolean
	 * @roseuid 3FDE4B720135
	 */
	public boolean equals(Object obj)
	{

		boolean rv = false;

		if (obj == null || !(obj instanceof WQEMetaDataBeanPK)) {

			rv = false;
		}
		else {

			String name = ((WQEMetaDataBeanPK)obj).getName();

			name = name == null ? "" : name;

			if (name.equals( getName() ))
				rv = true;
			else
				rv = false;
		}

		return rv;
	}

	/**
	 * @return String
	 * @roseuid 3FDE4B7201AD
	 */
	public String toString()
	{

		return "Queue Name :" + getName();
	}
}
