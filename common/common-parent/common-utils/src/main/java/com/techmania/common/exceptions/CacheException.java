/*
 * CacheException.java
 *
 * Created on November 21, 2003, 10:13 AM
 */

package com.techmania.common.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 *
 * @author  ravi
 */
public class CacheException extends Exception
{
	protected Throwable _throwable = null;
	protected String _info = null;
	protected static final String _MSG_SEPARATOR = "; ";	
	
	/** Creates a new instance of CacheException */
	public CacheException() 
	{
		super();
	}
		
	public CacheException(String msg) 
	{
		super(msg);
	}
	
	public CacheException(Throwable e) 
	{
		super(e.getMessage());
		_throwable = e;
	}
	
	public CacheException(String info, Throwable e) 
	{
		super(e.getMessage());
		_info = info;
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
			return (_info != null ? (_info + " - ") : "" ) + super.getMessage() + _MSG_SEPARATOR + _throwable.getMessage();
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
		if(_info != null)s.println(_info);
		super.printStackTrace(s);
		if(_throwable != null)_throwable.printStackTrace(s);
	}

	public void printStackTrace(PrintWriter s)
	{
		if(_info != null)s.println(_info);
		super.printStackTrace(s);
		if(_throwable != null)_throwable.printStackTrace(s);
	}

	public String toString()
	{
		return (_info != null ? (_info + " - ") : "" ) + super.toString();
	}	
	
}
