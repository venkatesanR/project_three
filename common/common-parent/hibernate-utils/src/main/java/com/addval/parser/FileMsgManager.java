//Source file: D:\\PROJECTS\\COMMON\\SOURCE\\com\\addval\\parser\\FileMsgManager.java

package com.addval.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.regexp.RE;

import com.addval.esapiutils.validator.AppSecurityValidatorException;
import com.addval.esapiutils.validator.FileSecurityValidator;
import com.addval.esapiutils.validator.FileSecurityValidatorESAPIImpl;
/**
 * @author AddVal Technology Inc.
 */
public class FileMsgManager
{
	private String _outputFileName;
	private String _outputMsgLineSeparator;
	private String _msgId;
	private StringBuffer _msgBuffer = null;
	private RE _matcher = null;
	private BufferedReader _input = null;
	private BufferedWriter _output = null;
	private InputStream _inputStream = null;
	private InputStreamReader _reader = null;
	private FileSecurityValidator fileSecurityValidator;

	public void setFileSecurityValidator(FileSecurityValidator fileSecurityValidator)
	{
		this.fileSecurityValidator=fileSecurityValidator;
	}

	public FileSecurityValidator getFileSecurityValidator()
	{
		if(fileSecurityValidator==null){
			fileSecurityValidator=new FileSecurityValidatorESAPIImpl();
		}

		return fileSecurityValidator;

	}

	/**
	 * @param inputFileName
	 * @param matcher
	 * @throws java.io.FileNotFoundException
	 * @roseuid 3E15293003D6
	 */
	public FileMsgManager(String inputFileName, RE matcher) throws FileNotFoundException,UnsupportedEncodingException,AppSecurityValidatorException
	{
        this( inputFileName );
        _matcher = matcher;
	}

	/**
	 * @param inputFileName
	 * @param msgId
	 * @throws FileNotFoundException
	 * @throws java.io.FileNotFoundException
	 * @roseuid 3D6B0B180370
	 */
	public FileMsgManager(String inputFileName, String msgId) throws FileNotFoundException,UnsupportedEncodingException,AppSecurityValidatorException
	{
        this( inputFileName );
        _msgId = msgId;
	}

	/**
	 * @param inputFileName
	 * @throws java.io.FileNotFoundException
	 * @throws AppSecurityValidatorException
	 * @roseuid 3D6B0B180366
	 */
	public FileMsgManager(String inputFileName) throws FileNotFoundException,UnsupportedEncodingException, AppSecurityValidatorException
	{
		fileSecurityValidator=getFileSecurityValidator();
		File file= new File(inputFileName);
		String fileName=file.getName();
		fileName=fileSecurityValidator.getValidFileName("FileMsgManager", fileName);
		_inputStream = new FileInputStream(inputFileName);
		_reader = new InputStreamReader(_inputStream,"UTF-8");
		_input = new BufferedReader( _reader );
        _outputMsgLineSeparator = System.getProperty( "line.separator", "\n" );
        _msgBuffer = null;
        _outputFileName = "fileMsgManager.out";
	}

	/**
	 * Access method for the _outputFileName property.
	 * @return   the current value of the _outputFileName property
	 * @roseuid 3D6B0B1900AB
	 */
	public String getOutputFileName()
	{
        return _outputFileName;
	}

	/**
	 * Sets the value of the _outputFileName property.
	 * @param aOutputFileName the new value of the _outputFileName property
	 * @roseuid 3D6B0B1900BF
	 */
	public void setOutputFileName(String aOutputFileName)
	{
        _outputFileName = aOutputFileName;
	}

	/**
	 * Access method for the _outputMsgLineSeparator property.
	 * @return   the current value of the _outputMsgLineSeparator property
	 * @roseuid 3D6B0B1900D3
	 */
	public String getOutputMsgLineSeparator()
	{
      return _outputMsgLineSeparator;
	}

	/**
	 * Sets the value of the _outputMsgLineSeparator property.
	 * @param aOutputMsgLineSeparator the new value of the _outputMsgLineSeparator
	 * property
	 * @roseuid 3D6B0B1900DD
	 */
	public void setOutputMsgLineSeparator(String aOutputMsgLineSeparator)
	{
        _outputMsgLineSeparator = aOutputMsgLineSeparator;
	}

	/**
	 * Access method for the _msgId property.
	 * @return   the current value of the _msgId property
	 * @roseuid 3D6B0B1900F1
	 */
	public String getMsgId()
	{
      return _msgId;
	}

	/**
	 * @return String
	 * @throws IOException
	 * @throws java.io.IOException
	 * @throws AppSecurityValidatorException
	 * @roseuid 3D6B0B190231
	 */
	public String readNextMessage() throws IOException, AppSecurityValidatorException
	{
		fileSecurityValidator=getFileSecurityValidator();
        String line = null;
        if (_msgBuffer == null) {
            //very first call
            line = fileSecurityValidator.safeReadLine(_inputStream);
        }
        else {
            //next call, restore line
            line = _msgBuffer.toString();
            _msgBuffer = null;
        }
        while (line != null) {
            // the read line is checked for the presence of the matching pattern
            if (checkLine( line ) && _msgBuffer != null) {
                //message is ready
                String message = _msgBuffer.toString();
                //save the line
                _msgBuffer = new StringBuffer( line );
                return message;
            }
            //accumulate lines
            if (_msgBuffer == null)
                _msgBuffer = new StringBuffer();
            _msgBuffer.append( line ).append( _outputMsgLineSeparator );
            //read the next line
            line = _input.readLine();

        }//while

        if ( _msgBuffer == null )
            return null;
        //very last message
        String msg = _msgBuffer.toString();
        _msgBuffer = null;
        return msg;
	}

	/**
	 * @param msg
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws java.io.IOException
	 * @throws java.io.FileNotFoundException
	 * @roseuid 3D6B0B190340
	 */
	public void writeMessage(String msg) throws IOException, FileNotFoundException
	{
        if (_output == null)
            _output = new BufferedWriter( new FileWriter( _outputFileName ) );
        _output.write( msg );
        _output.newLine();
        _output.flush();
	}

	/**
	 * @param line
	 * @return boolean
	 * @roseuid 3E152932027B
	 */
	private boolean checkLine(String line)
	{
        // check the line with the matcher regexpression object, if available
        if (_matcher != null)
            return _matcher.match( line );
        // else if prefix alone is specified, check whether the line starts with the given prefix
        if (_msgId != null)
            return line.startsWith( _msgId );
        // if there is no matcher or if there is no specific starting String pattern
        // return true
        return true;
	}

	/**
	 * @throws java.lang.Throwable
	 * @roseuid 3F30F3C4002E
	 */
	public void close() throws IOException
	{
		if (_input !=null) { _input.close();  }
		if (_output!=null) { _output.close(); }
		if (_inputStream!=null) {_inputStream.close(); }
		if (_reader!=null) {_reader.close(); }
	}


// had some difficulty in adding this method to rose model
// peculiarly this method alone is not getting added - to be solved - jeyaraj
	/**
	 * @throws java.lang.Throwable
	 * @roseuid 3F30F3C4002E
	 */
	protected void finalize() throws Throwable
	{
		super.finalize();
		close();
	}
}
