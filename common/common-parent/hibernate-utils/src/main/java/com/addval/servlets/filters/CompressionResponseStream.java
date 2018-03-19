//Source file: d:\\projects\\common\\source\\com\\addval\\servlets\\filters\\CompressionResponseStream.java

package com.addval.servlets.filters;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class CompressionResponseStream extends ServletOutputStream {
	
	/**
	 * The threshold number which decides to compress or not.
	 * Users can configure in web.xml to set it to fit their needs.
	 */
	private int _compressionThreshold = 0;
	
	/**
	 * The buffer through which all of our output bytes are passed.
	 */
	private byte _buffer[] = null;
	
	/**
	 * Is it big enough to compress?
	 */
	private boolean _compressionThresholdReached = false;
	
	/**
	 * The number of data bytes currently in the buffer.
	 */
	private int _bufferCount = 0;
	
	/**
	 * Has this stream been closed?
	 */
	private boolean _closed = false;
	
	/**
	 * Should we commit the response when we are flushed?
	 */
	private boolean _commit = true;
	
	/**
	 * The number of bytes which have already been written to this stream.
	 */
	private int _count = 0;
	
	/**
	 * The content length past which we will not write, or -1 if there is
	 * no defined content length.
	 */
	private int _length = - 1;
	
	/**
	 * The underlying gzip output stream to which we should write data.
	 */
	private GZIPOutputStream _gzipstream = null;
	
	/**
	 * The response with which this servlet output stream is associated.
	 */
	private HttpServletResponse _response = null;
	
	/**
	 * The underlying servlet output stream to which we should write data.
	 */
	protected ServletOutputStream _output = null;
	
	/**
	 * @param response
	 * @throws java.io.IOException
	 * @roseuid 3E917ED60004
	 */
	public CompressionResponseStream(HttpServletResponse response) throws IOException {
        super();
        _closed = false;
        _commit = false;
        _count = 0;
        _response = response;
        _output = response.getOutputStream();		
	}
	
	/**
	 * @return boolean
	 * @roseuid 3E917ED6009A
	 */
	public boolean getCommit() {
        return _commit;		
	}
	
	/**
	 * @param commit
	 * @roseuid 3E917ED600A4
	 */
	public void setCommit(boolean commit) {
        _commit = commit;		
	}
	
	/**
	 * @param threshold
	 * @roseuid 3E917ED600B8
	 */
	protected void setBuffer(int threshold) {
        _compressionThreshold = threshold;
        _buffer = new byte[_compressionThreshold];		
	}
	
	/**
	 * Close this output stream, causing any buffered data to be flushed and
	 * any further output data to throw an IOException.
	 * @throws java.io.IOException
	 * @roseuid 3E917ED600CC
	 */
	public void close() throws IOException {
        if (_closed)
            throw new IOException("This output stream has already been closed");
        if (_gzipstream != null)
            _gzipstream.close();
        flush();
        _closed = true;		
	}
	
	/**
	 * @throws java.io.IOException
	 * @roseuid 3E917ED60144
	 */
	public void flush() throws IOException {
        if (_closed)
            throw new IOException("Cannot flush a closed output stream");
        if (!_commit)
            return;
        if (_bufferCount == 0)
            return;
        _output.write( _buffer, 0, _bufferCount );
        _bufferCount = 0;		
	}
	
	/**
	 * @throws java.io.IOException
	 * @roseuid 3E917ED601C7
	 */
	public void flushToGZip() throws IOException {
        if (_bufferCount == 0)
            return;
        _gzipstream.write( _buffer, 0, _bufferCount );
        _bufferCount = 0;		
	}
	
	/**
	 * @param b
	 * @throws java.io.IOException
	 * @roseuid 3E917ED60249
	 */
	public void write(int b) throws IOException {
        if (_closed)
            throw new IOException("Cannot write to a closed output stream");
        if ((_bufferCount >= _buffer.length) || (_count>=_compressionThreshold))
            _compressionThresholdReached = true;

        if (_compressionThresholdReached) {
            writeToGZip(b);
        }
        else {
            _buffer[_bufferCount++] = (byte) b;
            _count++;
        }		
	}
	
	/**
	 * @param b
	 * @throws java.io.IOException
	 * @roseuid 3E917ED602CB
	 */
	public void writeToGZip(int b) throws IOException {
        if (_gzipstream == null) {
            _gzipstream = new GZIPOutputStream( _output );
            flushToGZip();
            _response.addHeader( "Content-Encoding", "gzip" );
        }
        _gzipstream.write( b );		
	}
	
	/**
	 * @param b[]
	 * @throws java.io.IOException
	 * @roseuid 3E917ED60357
	 */
	public void write(byte b[]) throws IOException {
        write(b, 0, b.length);		
	}
	
	/**
	 * @param b[]
	 * @param off
	 * @param len
	 * @throws java.io.IOException
	 * @roseuid 3E917ED603D9
	 */
	public void write(byte b[], int off, int len) throws IOException {
        if (_closed)
            throw new IOException("Cannot write to a closed output stream");
        if (len == 0)
            return;
        if (len <= (_buffer.length - _bufferCount)) {
            System.arraycopy(b, off, _buffer, _bufferCount, len);
            _bufferCount += len;
            _count += len;
            return;
        }
        // buffer full, start writing to gzipstream
        writeToGZip(b, off, len);
        _count += len;		
	}
	
	/**
	 * @param b[]
	 * @param off
	 * @param len
	 * @throws java.io.IOException
	 * @roseuid 3E917ED70088
	 */
	public void writeToGZip(byte b[], int off, int len) throws IOException {
        if (_gzipstream == null) {
            _gzipstream = new GZIPOutputStream( _output );
            flushToGZip();
            _response.addHeader("Content-Encoding", "gzip");
        }
        _gzipstream.write(b, off, len);		
	}
	
	/**
	 * @return boolean
	 * @roseuid 3E917ED70114
	 */
	public boolean closed() {
        return _closed;		
	}
	
	/**
	 * @roseuid 3E917ED70128
	 */
	public void reset() {
        _count = 0;		
	}
}
