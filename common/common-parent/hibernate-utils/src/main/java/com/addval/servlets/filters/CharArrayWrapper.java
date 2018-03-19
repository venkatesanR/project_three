//Source file: d:\\projects\\common\\source\\com\\addval\\servlets\\filters\\CharArrayWrapper.java

package com.addval.servlets.filters;

import java.io.IOException;
import java.io.CharArrayWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * A response wrapper that takes everything the client
 * would normally output and saves it in one big
 * character array.
 */
public class CharArrayWrapper extends HttpServletResponseWrapper {
	private CharArrayWriter _charWriter;
	
	/**
	 * Initializes wrapper.
	 * <P>
	 * First, this constructor calls the parent
	 * constructor. That call is crucial so that the response
	 * is stored and thus setHeader, setStatus, addCookie,
	 * and so forth work normally.
	 * <P>
	 * Second, this constructor creates a CharArrayWriter
	 * that will be used to accumulate the response.
	 * 
	 * @param response
	 * @roseuid 3E9414820002
	 */
	public CharArrayWrapper(HttpServletResponse response) {
		super( response );
		_charWriter = new CharArrayWriter();		
	}
	
	/**
	 * When servlets or JSP pages ask for the Writer,
	 * don't give them the real one. Instead, give them
	 * a version that writes into the character array.
	 * The filter needs to send the contents of the
	 * array to the client (perhaps after modifying it).
	 * 
	 * @return java.io.PrintWriter
	 * @roseuid 3E941482002A
	 */
	public PrintWriter getWriter() {
		return new PrintWriter( _charWriter );		
	}
	
	/**
	 * Get the underlying character array.
	 * 
	 * @return char[]
	 * @roseuid 3E9414820066
	 */
	public char[] toCharArray() {
		return _charWriter.toCharArray();		
	}
	
	/**
	 * Get a String representation of the entire buffer.
	 * <P>
	 * Be sure <B>not</B> to call this method multiple times
	 * on the same wrapper. The API for CharArrayWriter
	 * does not guarantee that it "remembers" the previous
	 * value, so the call is likely to make a new String
	 * every time.
	 * 
	 * @return java.lang.String
	 * @roseuid 3E94153D0390
	 */
	public String toString() {
		return _charWriter.toString();		
	}
}
