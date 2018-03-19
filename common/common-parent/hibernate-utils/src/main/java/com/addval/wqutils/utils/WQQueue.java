package com.addval.wqutils.utils;


import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author AddVal Technology Inc.
 */
public class WQQueue
{
	private String _name = null;
	private int _noOfParameters = 0;
	private Hashtable _params = null;

	public String getName()
	{
		return _name;
	}
	public void setName(String aName)
	{
		_name = aName;
	}
	public int getNoOfParameters()
	{
		return _noOfParameters;
	}

	public void setNoOfParameters(int aNoOfParameters)
	{
		_noOfParameters = aNoOfParameters;
	}

	public void addWQParam(WQParam param)
	{
		if (_params == null)
			_params = new Hashtable();
		_params.put(param.getKey(), param );
	}

	public Hashtable getWQParams()
	{
		if (_params == null)
			_params = new Hashtable();
		return _params;
	}
	public String toString() {
		StringBuffer queue = new StringBuffer();
		queue.append( "Name  			: ").append( getName() ).append( System.getProperty( "line.separator" ) );
		queue.append( "WQ Parameters Count : ").append( getNoOfParameters() ).append( System.getProperty( "line.separator" ) );
		queue.append( "WQ Params ").append( System.getProperty( "line.separator" ) );

		String paramKey = null;
		WQParam wqParam= null;

		for(Iterator iterator = getWQParams().keySet().iterator();iterator.hasNext(); ) {

			paramKey = (String)iterator.next();
			queue.append( getWQParams().get( paramKey ).toString() ).append( System.getProperty( "line.separator" ) );

		}
		return queue.toString();
	}
}
