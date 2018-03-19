//Source file: C:\\projects\\common\\src\\client\\source\\com\\addval\\utils\\GenUtl.java

/*
 * GenUtl.java
 *
 * Created on June 20, 2003, 12:19 PM
 */

package com.addval.utils;

import java.io.*;
import java.util.*;
import java.lang.reflect.Method;

/**
 * @author  ravi
 */
public class GenUtl
{

	/**
	 * Creates a new instance of GenUtl
	 * @roseuid 3EF36CDB005F
	 */
	public GenUtl()
	{

	}

	/**
	 * compares two objects including nulls and returns true if
	 * they are the same by invoking equals on obj1
	 *
	 * @param obj1
	 * @param obj2
	 * @return boolean
	 * @roseuid 3EF36CDB007D
	 */
	public static boolean equals(Object obj1, Object obj2)
	{
		return (obj1 == null) ? (obj2 == null) : obj1.equals(obj2);
	}

	public static Serializable cloneObject(Serializable aObj) throws IOException, ClassNotFoundException
	{
		//serialize original object to byte stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream(100);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(aObj);
		byte buf[] = baos.toByteArray();
		oos.close();

		//deserialize new route from byte stream

		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Serializable newObj = (Serializable)ois.readObject();
		ois.close();

		return newObj;
	}

	public static boolean listIsEmpty(List list)
	{
		return (list == null || list.isEmpty());
	}

	public static String arrayToString(Object[] arr)
	{
		if(arr == null)return "[]";

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i = 0; i < arr.length; i++){
			sb.append(arr[i]);
			if(i < arr.length-1)sb.append(", ");
		}
		sb.append("]");

		return sb.toString();
	}

	public static String arrayToString(int[] arr)
	{
		if(arr == null)return "[]";

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i = 0; i < arr.length; i++){
			sb.append(arr[i]);
			if(i < arr.length-1)sb.append(", ");
		}
		sb.append("]");

		return sb.toString();
	}

	public static String arrayToString(byte[] arr)
	{
		if(arr == null)return "[]";

		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i = 0; i < arr.length; i++){
			sb.append(arr[i]);
			if(i < arr.length-1)sb.append(", ");
		}
		sb.append("]");

		return sb.toString();
	}


	public static int compare(Object object1, Object object2, String methodName) throws Exception
	{
		try {
			Method method = object1.getClass().getDeclaredMethod( methodName, null );
			Object result1 = method.invoke( object1, null );
			Object result2 = method.invoke( object2, null );
			if (result1 instanceof Boolean)
				return (result1.toString()).compareTo( result2.toString() );

			Comparable value1 = (Comparable) result1;
			return value1.compareTo( result2 );
		}

		catch(Exception e) {
			throw e;
		}
	}

	public static void performSort( String methodName, String sortOrder, List vectorTobeSorted ) throws Exception
	{
		if (methodName != null && methodName.trim().length() != 0 && vectorTobeSorted != null && ! vectorTobeSorted.isEmpty())
		{
			if (sortOrder == null || sortOrder.trim().length() == 0)
				sortOrder = "ASC";

			ObjectComparator comparator = new ObjectComparator( methodName , sortOrder );
			Collections.sort( vectorTobeSorted , comparator );
		}
	}
}
