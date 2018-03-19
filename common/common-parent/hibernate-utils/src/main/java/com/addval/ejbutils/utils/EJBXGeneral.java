//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\utils\\EJBXGeneral.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.utils;

import com.addval.utils.XGeneral;
import java.io.Serializable;

/**
 * A serializable custom generalization of the Exception class
 */
public class EJBXGeneral extends XGeneral implements Serializable {
	
	/**
	 * Constructor. Call the super class and set the source.
	 * @param source String
	 * @param desc String
	 * @exception
	 * @roseuid 3AE8D27E01FE
	 */
	public EJBXGeneral(String source, String desc) {
		super( source, desc );		
	}
}
