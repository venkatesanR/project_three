/*
 * Pair.java
 *
 * Created on July 17, 2003, 10:53 AM
 */

package com.addval.utils;

/**
 *
 * @author  ravi
 */
public class Pair implements java.io.Serializable
{
	private Object _first;
	private Object _second;
	private int _hashCode;
	
	/** Creates a new instance of Pair */
	public Pair(Object first, Object second) 
	{
		if(first == null || second == null)
			throw new IllegalArgumentException("Pair cannot be constructed with null objects.");
		_first = first;
		_second = second;
		_hashCode = _first.hashCode() * 31 + _second.hashCode();
	}
	
	/** Getter for property first.
	 * @return Value of property first.
	 *
	 */
	public java.lang.Object getFirst() 
	{
		return _first;
	}
		
	/** Getter for property second.
	 * @return Value of property second.
	 *
	 */
	public java.lang.Object getSecond() 
	{
		return _second;
	}
	
	public int hashCode()
	{
		return _hashCode;
	}
	
	public boolean equals(Object obj)
	{
		if(obj == this)return true;
		if(obj == null || !(obj instanceof Pair) )return false;
				
		Pair other = (Pair)obj;
		if(this.getFirst().equals(other.getFirst()) &&
			this.getSecond().equals(other.getSecond()))return true;
		
		return false;				
	}
	
	public String toString()
	{
		return "Pair[" + _first + ", " + _second + "]";
	}
}
