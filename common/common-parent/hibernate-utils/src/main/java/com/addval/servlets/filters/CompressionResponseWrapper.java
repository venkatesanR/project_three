//Source file: d:\\projects\\common\\source\\com\\addval\\servlets\\filters\\CompressionResponseWrapper.java

package com.addval.servlets.filters;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;

public class CompressionResponseWrapper extends HttpServletResponseWrapper {
	
	/**
	 * The threshold number to compress
	 */
	private int _threshold = 0;
	
	/**
	 * Original response
	 */
	private HttpServletResponse _origResponse = null;
	
	/**
	 * The ServletOutputStream that has been returned by getOutputStream(),
	 * if any
	 */
	private CompressionResponseStream _stream = null;
	
	/**
	 * The PrintWriter that has been returned by getWriter(), if any
	 */
	private PrintWriter _writer = null;
	
	/**
	 * @param response
	 * @roseuid 3E917ED80197
	 */
	public CompressionResponseWrapper(HttpServletResponse response) {
        super(response);
        _origResponse = response;		
	}
	
	/**
	 * @param threshold
	 * @roseuid 3E917ED801B5
	 */
	public void setCompressionThreshold(int threshold) {
        _threshold = threshold;		
	}
	
	/**
	 * @throws java.io.IOException
	 * @roseuid 3E917ED801C9
	 */
	private void setOutputStream() throws IOException {
		_stream = new CompressionResponseStream( _origResponse );
		_stream.setCommit( true );
		_stream.setBuffer( _threshold );		
	}
	
	/**
	 * @throws java.io.IOException
	 * @roseuid 3E917ED80260
	 */
	public void flushBuffer() throws IOException {
        _stream.flush();		
	}
	
	/**
	 * @return javax.servlet.ServletOutputStream
	 * @throws java.io.IOException
	 * @roseuid 3E917ED802E2
	 */
	public ServletOutputStream getOutputStream() throws IOException {
        if (_writer != null)
            throw new IllegalStateException( "getWriter() has already been called for this response" );
        if (_stream == null)
            setOutputStream();
        return (ServletOutputStream)_stream;		
	}
	
	/**
	 * @return java.io.PrintWriter
	 * @throws java.io.IOException
	 * @roseuid 3E917ED80378
	 */
	public PrintWriter getWriter() throws IOException {
        if (_writer != null)
            return _writer;
        if (_stream != null)
            throw new IllegalStateException( "getOutputStream() has already been called for this response" );

        setOutputStream();
        _writer = new PrintWriter( _stream );
//        _writer = (PrintWriter)new CompressionResponseWriter( _stream );
        return _writer;		
	}
}
