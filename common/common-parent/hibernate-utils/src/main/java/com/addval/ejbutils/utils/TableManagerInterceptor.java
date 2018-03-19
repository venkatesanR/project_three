package com.addval.ejbutils.utils;

import java.io.Serializable;

import com.addval.ejbutils.dbutils.EJBResultSet;

/**
 * Classes implementing the TableManagerInterceptor Interface can be attached to a
 * TableManagerBean  through ejb-jar.xml.
 * The implementing classes can perform additional work within transaction
 * along with the actual work performed by the TableManagerBean
 */
public interface TableManagerInterceptor extends Serializable
{
	public void beforeUpdate(EJBResultSet ejbRS) throws Exception;

	public void afterUpdate(EJBResultSet ejbRS) throws Exception;

	public void beforeLookupForUpdate(EJBResultSet ejbRS) throws Exception;

	public void afterLookupForUpdate(EJBResultSet ejbRS) throws Exception;

	public void setSubSystem(String subSystem);

	public String getSubSystem();
	
	public void setCallerPrincipalName(String callerPrincipalName);

	public String getCallerPrincipalName();

}
