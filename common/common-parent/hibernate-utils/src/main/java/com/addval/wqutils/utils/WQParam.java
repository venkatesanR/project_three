package com.addval.wqutils.utils;


import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;

public class WQParam
{
	private String _key = null;
	private String _name = null;
	private String _type = null;

	public String getKey(){
		return  _key;
	}

	public void setKey(String key){
		_key = key;
	}

	public String getName(){
		return  _name;
	}
	public void setName(String name){
		_name = name;
	}

	public String getType(){
		return  _type;
	}
	public void setType(String type){
		_type = type;
	}
	public String toString() {
		StringBuffer param = new StringBuffer();
		param.append( "Key  : ").append( getKey() ).append( System.getProperty( "line.separator" ) );
		param.append( "Name : ").append( getName() ).append( System.getProperty( "line.separator" ) );
		param.append( "Type : ").append( getType() ).append( System.getProperty( "line.separator" ) );
		return param.toString();
	}
}
