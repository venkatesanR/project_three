//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\ejbutils\\utils\\EJBXRuntime.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\utils\\EJBXRuntime.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\utils\\EJBXRuntime.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.utils;

import java.io.Serializable;

/**
 * A serializable custom Runtime exception class that is used on the client and
 * server components of com.addval.ejbutils
 */
public class EJBXRuntime extends Exception implements Serializable
{
	private static final int _AV_ERROR = -1000;
	private static final String _AV_ERRORDESC = "Critical System Error. Please contact System Administrator";

	/**
	 * @param e
	 * @roseuid 3B6EE10502B9
	 */
	public EJBXRuntime(Exception e)
	{
		super( e.getMessage() );
	}

	/**
	 * Constructor. Calls the super class to set the exception. Sets
	 * the orgin of the exception locally.
	 * @param source String
	 * @param desc String
	 * @param errNumber int
	 * @exception
	 * @roseuid 3AE8CEDA00A9
	 */
	public EJBXRuntime(String source, String desc, int errNumber)
	{
		super( "ERROR :" + String.valueOf( errNumber ) + " : " + source + " : " + desc );
	}

	/**
	 * Constructor. Calls the super class to set the exception. Sets
	 * the orgin of the exception locally.
	 * @param source String
	 * @param desc String
	 * @exception
	 * @roseuid 3AE8CEDA0095
	 */
	public EJBXRuntime(String source, String desc)
	{
		this( source, desc, _AV_ERROR );
	}

	/**
	 * @param message
	 * @roseuid 3B6EE1180111
	 */
	public EJBXRuntime(String message)
	{
		super( message );
	}
}
