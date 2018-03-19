/*
 * ServiceLocatorException.java
 *
 * Created on August 29, 2003, 3:06 PM
 */

package com.addval.servicelocator;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author  ravi
 */
public class ServiceLocatorException extends RuntimeException 
{
	Throwable _throwable = null;
	private static final String _MSG_SEPARATOR = "; ";
	
	/** Creates a new instance of ServiceLocatorException */
	public ServiceLocatorException() 
	{
		super();
	}
	
	public ServiceLocatorException(String msg) 
	{
		super(msg);
	}
	
	public ServiceLocatorException(Exception e) 
	{
		super(e.getMessage());
		_throwable = e;
	}
	
	public String getLocalizedMessage() 
	{
		if(_throwable != null){
			return super.getLocalizedMessage() + _MSG_SEPARATOR + _throwable.getLocalizedMessage();
		}
		
		return super.getLocalizedMessage();
	}

	public String getMessage() 
	{
		if(_throwable != null){
			return super.getMessage() + _MSG_SEPARATOR + _throwable.getMessage();
		}
		
		return super.getMessage();		
	}

	public void printStackTrace()
	{
		super.printStackTrace();
		if(_throwable != null)_throwable.printStackTrace();
	}

	public void printStackTrace(PrintStream s) 
	{
		super.printStackTrace(s);
		if(_throwable != null)_throwable.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter s)
	{
		super.printStackTrace(s);
		if(_throwable != null)_throwable.printStackTrace(s);
	}

	public String toString()
	{
		return super.toString();
	}
	
}
