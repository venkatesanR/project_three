//Source file: d:\\projects\\common\\source\\com\\addval\\servlets\\filters\\CompressionFilter.java

package com.addval.servlets.filters;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.utils.LogFileMgr;
import com.addval.environment.Environment;

public class CompressionFilter implements Filter {
	private int _compressionThreshold = 0;
	private static final String _module = "CompressionFilter";
	private boolean _enabled = true;
	private FilterConfig _filterConfig = null;
	private LogFileMgr _log = null;
	
	/**
	 * @param filterConfig
	 * @roseuid 3E8AD4050268
	 */
	public void init(FilterConfig filterConfig) {
        if (filterConfig == null)
            return;
        setFilterConfig( filterConfig );
        String value = filterConfig.getInitParameter( "enabled" );
        _enabled = value != null && (value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "yes" ));
        if (!_enabled)
            return;
        value = filterConfig.getInitParameter( "debug" );
        if (value != null && (value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "yes" )))
            _log = Environment.getInstance( "filters" ).getLogFileMgr( _module );
        value = filterConfig.getInitParameter( "compressionThreshold" );
        if (value != null)
            _compressionThreshold = Integer.parseInt( value );
        logInfo( "Compression Filter init()" );		
	}
	
	/**
	 * @roseuid 3E8AD40502AF
	 */
	public void destroy() {
        logInfo( "Compression Filter destroy()" );
        _filterConfig = null;		
	}
	
	/**
	 * @param request
	 * @param response
	 * @param chain
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 * @roseuid 3E8AD40502C2
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logInfo( "Compression Filter doFilter()" );
        if (!_enabled) {
            chain.doFilter(request, response);
            return;
        }
        if (!supportsCompression( request )) {
			logInfo( "Browser Supports Compression" );
            chain.doFilter(request, response);
            return;
        }
        if (!(response instanceof HttpServletResponse)) {
            logInfo( "Unknown Response type " + response );
            return;
        }

		// this block of code does not work even for servlet
/*
		HttpServletResponse hResponse = (HttpServletResponse)response;
		HttpServletRequest hRequest = (HttpServletRequest)request;
		// Tell browser we are sending it gzipped data.
		hResponse.setHeader("Content-Encoding", "gzip");
		// Invoke resource, accumulating output in the wrapper.
		CharArrayWrapper responseWrapper = new CharArrayWrapper( hResponse );
		chain.doFilter( request, responseWrapper );
		// Get character array representing output.
		char[] responseChars = responseWrapper.toCharArray();
		// Make a writer that compresses data and puts
		// it into a byte array.
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		GZIPOutputStream zipOut = new GZIPOutputStream( byteStream );
		OutputStreamWriter tempOut = new OutputStreamWriter( zipOut );
		// Compress original output and put it into byte array.
		tempOut.write( responseChars );
		// Gzip streams must be explicitly closed.
		tempOut.close();
		// Update the Content-Length header.
		hResponse.setContentLength( byteStream.size() );
		// Send compressed result to client.
		OutputStream realOut = hResponse.getOutputStream();
		byteStream.writeTo( realOut );
*/

		// below lines work okey for servlets, but fails with jsp
        CompressionResponseWrapper wrappedResponse = new CompressionResponseWrapper((HttpServletResponse)response);
        wrappedResponse.setCompressionThreshold( _compressionThreshold );
        chain.doFilter( request, wrappedResponse );

        logInfo( "Sent Compressed Output" );		
	}
	
	/**
	 * @param filterConfig
	 * @roseuid 3E8AD4060039
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
        _filterConfig = filterConfig;		
	}
	
	/**
	 * @return javax.servlet.FilterConfig
	 * @roseuid 3E8AD406006B
	 */
	public FilterConfig getFilterConfig() {
        return _filterConfig;		
	}
	
	/**
	 * @param info[]
	 * @roseuid 3E8AD40600B1
	 */
	private void logInfo(byte info[]) {
        logInfo( new String( info ) );		
	}
	
	/**
	 * @param info
	 * @roseuid 3E917ED40001
	 */
	private void logInfo(String info) {
        if (_log == null)
            return;
        _log.logInfo( info );
        System.out.println( info );		
	}
	
	/**
	 * @param request
	 * @return boolean
	 * @roseuid 3E94148601DE
	 */
	private boolean supportsCompression(ServletRequest request) {
        if (!(request instanceof HttpServletRequest))
        	return false;
		Enumeration e = ((HttpServletRequest)request).getHeaders( "Accept-Encoding" );
		while (e.hasMoreElements())
			if (((String)e.nextElement()).indexOf( "gzip" ) != -1)
				return true;
		return false;		
	}
}
