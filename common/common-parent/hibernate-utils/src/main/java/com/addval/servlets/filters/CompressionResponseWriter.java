//Source file: d:\\projects\\common\\source\\com\\addval\\servlets\\filters\\CompressionResponseWriter.java


package com.addval.servlets.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class CompressionResponseWriter extends PrintWriter {
	private CompressionResponseStream _stream;
	
	/**
	 * @param stream
	 * @throws java.io.IOException
	 * @roseuid 3E94148F0372
	 */
	public CompressionResponseWriter(CompressionResponseStream stream) throws IOException {
        super( (OutputStream)stream );
        _stream = stream;		
	}
	
	/**
	 * @param buf[]
	 * @param off
	 * @param len
	 * @roseuid 3E941490003E
	 */
	public void write(char buf[], int off, int len) {
 		try {
 			_stream.println( new String( buf ) );
 		}
 		catch(IOException ioe) {
 			throw new RuntimeException( ioe.toString() );
 		}		
	}
	
	/**
	 * @param buf[]
	 * @roseuid 3E941490005D
	 */
	public void write(char buf[]) {
		write( buf, 0, buf.length );		
	}
}
