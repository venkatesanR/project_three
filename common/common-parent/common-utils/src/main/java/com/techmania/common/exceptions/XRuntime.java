//Source file: D:\\Projects\\Common\\source\\com\\addval\\utils\\XRuntime.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.techmania.common.exceptions;


/**
 * Extends the RuntimeException class. Stores information about the origin of the
 * Runtime Exception.
 * @author AddVal Technology Inc.
 */
public class XRuntime extends RuntimeException {

	/**
	 * The source from where the exception originated.
	 */
	private String _source;

	/**
	 * The error number of the exception.
	 */
	private int _errNumber;

	/**
	 * Constructor. Calls the super class to set the exception. Sets
	 * the orgin of the exception locally.
	 *
	 * @param source Exception source.
	 * @param desc Exception description.
	 * @param errNumber Exception error number.
	 * @roseuid 39D3C7E20057
	 */
	public XRuntime(String source, String desc, int errNumber) {

		super(desc);
        _source = source;
        _errNumber = errNumber;
	}

	/**
	 * Constructor. Calls the super class to set the exception. Sets
	 * the orgin of the exception locally.
	 *
	 * @param source Exception source.
	 * @param desc Exception description.
	 * @roseuid 376FB13F01B1
	 */
	public XRuntime(String source, String desc) {

		super(desc);
        _source = source;
	}

	public XRuntime(String source, Throwable e) {
		super(e.getMessage());
		_source = source;
	}

	public XRuntime(String desc)
		{
	        super(desc);
	}

	/**
	 * Returns the source attached to the exception.
	 *
	 * @return source String
	 * @roseuid 376FAA87028A
	 */
	public String getSource() {
      return _source;
	}

	/**
	 * Returns the message attached to the exception
	 *
	 * @return message String
	 * @roseuid 376FAB1F0148
	 */
	public String getMessage() {
      return super.getMessage();
	}

	/**
	 * Returns the message attached to the exception
	 *
	 * @return error number int
	 * @roseuid 39D3C79901D8
	 */
	public int getErrNumber() {

		return _errNumber;
	}

	/**
	 * Returns the class as a string. Used for printing/debugging.
	 *
	 * @return Class as a string - String
	 * @roseuid 376FAB290070
	 */
	public String toString() {
      return ("XRuntime: Source : " + getSource() + "; Error: " + getMessage());
	}
}
