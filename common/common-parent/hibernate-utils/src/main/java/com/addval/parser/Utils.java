//Source file: D:\\PROJECTS\\COMMON\\SOURCE\\com\\addval\\parser\\Utils.java
package com.addval.parser;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;

public class Utils {

	/**
	 * friendly method to prepare an array from a comma delimited property entry.
	 * @param key
	 * @param prop
	 * @return String[]
	 * @roseuid 3E1555120159
	 */
	public static String[] getPropertyValues(String key, java.util.Properties prop) {
        return splitLine( prop.getProperty( key ) );
	}

	/**
	 * splitting delimiter defaults to comma
	 * @param line
	 * @return String[]
	 * @roseuid 3E155512016E
	 */
	public static String[] splitLine(String line) {
        return splitLine( line, "," );
	}

	/**
	 * @param line
	 * @param delimiter
	 * @return String[]
	 * @roseuid 3E1555120181
	 */
	public static String[] splitLine(String line, String delimiter) {
        return splitLine( line, delimiter, true);
	}

	/**
	 * provision is there to specify whether to trim the splitter entries
	 * null fileds also are returned unlike StringTokenizer
	 * @param line
	 * @param delimiter
	 * @param isTrim
	 * @return String[]
	 * @roseuid 3E15551201BD
	 */
	public static String[] splitLine(String line, String delimiter, boolean isTrim) {
        if (line == null)
            return new String[0];
        if (isNullOrEmpty( delimiter )) {
        	//System.out.println("Found delimiter to be nullOrEmpty");
            if (isTrim)
                return new String[]{ line.trim() };
            return new String[]{ line };
        }
        int delimiterLength = delimiter.length();
        int endIndex;
        int startIndex = 0;
        java.util.Vector result = new java.util.Vector();
        for (int count = 0; (endIndex = line.indexOf( delimiter, startIndex )) != -1; count++) {
            if (isTrim)
                result.add( line.substring(startIndex, endIndex).trim() );
            else
                result.add( line.substring(startIndex, endIndex) );
            startIndex = endIndex + delimiterLength;
        }
        //System.out.println("Found " + result.size() + " entries after split using delimiter:" + delimiter + ":");
        //System.out.println("Delimiter exists check:" + line.indexOf(delimiter,0) );
        if (isTrim)
            result.add( line.substring( startIndex, line.length() ).trim() );
        else
            result.add( line.substring( startIndex, line.length() ) );
        return (String[])result.toArray( new String[result.size()] );
	}

	/**
	 * friendly method to create a new object, when provided with the fully qualified
	 * name of a class
	 * @param className
	 * @return Object
	 * @roseuid 3E15551201E5
	 */
	public static Object getNewObject(String className) {
        try {
            return Class.forName( className ).newInstance();
        }
        catch(ClassNotFoundException cnfe) {
            throw new RuntimeException( "Error While creating object " + className + "\n" + cnfe.toString() );
        }
        catch(InstantiationException ie) {
            throw new RuntimeException( "Error While creating object " + className + "\n" + ie.toString() );
        }
        catch(IllegalAccessException iae) {
            throw new RuntimeException( "Error While creating object " + className + "\n" + iae.toString() );
        }
	}

	/**
	 * @param object
	 * @return java.lang.String
	 * @roseuid 3E15551201F9
	 */
	public static String toString(Object object) {
		if (object == null)
			return "";
		StringBuffer buffer = new StringBuffer( object.getClass().getName() + "\n" );
		try {
			Field[] fields = object.getClass().getDeclaredFields();
			for (int i=0; i<fields.length; i++) {
				Field field = fields[i];
				field.setAccessible( true );
				Object fieldObjects = fields[i].get( object );
				buffer.append( "\t\t" ).append( field.getName() ).append( "\t:\t" ).append( fieldObjects ).append( "\n" );
				if (!(fieldObjects instanceof Object[]))
					continue;
				// handle the Objects present as array here
				Object[] objects = (Object[])fieldObjects;
				for (int j=0; j<objects.length; j++)
					buffer.append( "\t\t\t\t" ).append( objects[j] ).append( "\n" );
			}
			return buffer.toString();
		}
		catch (IllegalAccessException iae) {
			buffer.append( "\n" ).append( "Error Accessing variable " + iae );
			return buffer.toString();
		}
	}

	/**
	 * @param object
	 * @return boolean
	 * @roseuid 3E155512020D
	 */
	public static boolean isNullOrEmpty(Object object) {
        return object == null || object.equals( "" );
	}

	/**
	 * @param object
	 * @return java.lang.Object
	 * @roseuid 3ED4D3AE0185
	 */
	public static Object clone(Object object) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( bout );
            out.writeObject( object );
            out.close();
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( bout.toByteArray() ) );
            Object retObject = in.readObject();
            in.close();
            return retObject;
        }
        catch( ClassNotFoundException cnfe ) {
            throw new RuntimeException( "Error while Cloning! " + cnfe );
        }
        catch( NotSerializableException nse ) {
            throw new RuntimeException( "Object to be cloned must be Serializable fully!" );
        }
        catch( IOException ioe ) {
            throw new RuntimeException( "Error while Cloning! " + ioe );
        }
	}

	/**
	 * @param object[]
	 * @return boolean
	 * @roseuid 3ED4D3AE01A3
	 */
	public static boolean isNullOrEmpty(Object object[]) {
        return object == null || object.length == 0;
	}

	/**
	 * @param object
	 * @return boolean
	 * @roseuid 3ED4D3AE01C1
	 */
	public static boolean isNullOrEmpty(List object) {
        return object == null || object.isEmpty();
	}

	/**
	 * @param object
	 * @return boolean
	 * @roseuid 3ED4D3AE01E9
	 */
	public static boolean isNullOrEmpty(Map object) {
        return object == null || object.isEmpty();
	}

    public static boolean isNullOrEmpty(String object) {
        return object == null || object.length() == 0 ;
    }
}
