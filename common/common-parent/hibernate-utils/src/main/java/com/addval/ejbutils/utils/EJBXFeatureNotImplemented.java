//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\utils\\EJBXFeatureNotImplemented.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.utils;

import com.addval.utils.XFeatureNotImplemented;
import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * A serializable custom Runtime exception class that is thrown when 
 * non-implemented methods in com.addval.ejbutils are called
 */
public class EJBXFeatureNotImplemented extends XFeatureNotImplemented implements Serializable {
	
	/**
	 * Constructor. Calls the super class to set the exception. Sets
	 * the orgin of the exception locally.
	 * @param source String
	 * @param desc String
	 * @return
	 * @exception
	 * @roseuid 3AE9A7EE03B5
	 */
	public EJBXFeatureNotImplemented(String source, String desc) {
		super( source, desc );		
	}
}
