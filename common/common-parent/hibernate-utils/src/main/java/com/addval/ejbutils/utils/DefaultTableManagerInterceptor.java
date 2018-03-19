package com.addval.ejbutils.utils;

import com.addval.ejbutils.dbutils.EJBResultSet;

/**
 * Classes implementing the TableManagerInterceptor Interface can be attached to a
 * TableManagerBean  through ejb-jar.xml.
 * The implementing classes can perform additional work within transaction
 * along with the actual work performed by the TableManagerBean
 */
/**
 * Convienience class that provides an implementation of all the TableManagerInterceptor
 * methods
 * This class can be extended for custom implementations and only the required
 * hooks be implemented
 */
public class DefaultTableManagerInterceptor implements TableManagerInterceptor
{
	private String _subSystem;
	private String _callerPrincipalName;

	public void beforeUpdate(EJBResultSet ejbRS) throws Exception
	{

	}

	public void afterUpdate(EJBResultSet ejbRS) throws Exception
	{

	}

	public void beforeLookupForUpdate(EJBResultSet ejbRS) throws Exception
	{

	}

	public void afterLookupForUpdate(EJBResultSet ejbRS) throws Exception
	{

	}

	public void setSubSystem( String subString)
	{
		_subSystem = subString;
	}

	public String getSubSystem()
	{
		return _subSystem;
	}
	
	public void setCallerPrincipalName (String callerPrincipalName) {
		_callerPrincipalName = callerPrincipalName;
	}

	public String getCallerPrincipalName() {
		return _callerPrincipalName;
	}

}
