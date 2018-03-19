/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.command;

import java.util.Vector;

public class CommandException extends RuntimeException implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private int _errorCode;
	private Vector _errors;

	public CommandException(int errorCode, String errorMessage){
		super( errorMessage );
		setErrorCode( errorCode );
	}

	public CommandException(int errorCode, String errorMessage, Vector errors){
		this( errorMessage, errors );
		setErrorCode( errorCode );
	}

	public CommandException(Vector errors){
    	_errors = errors;
	}

	public CommandException(String message, Vector errors){
        super(message);
    	_errors = errors;
	}

	public CommandException(String message){
        super(message);
	}

	public int getErrorCode(){
		return _errorCode;
	}

	public void setErrorCode(int aErrorCode){
		_errorCode = aErrorCode;
	}

	public Vector getErrors(){
      return _errors;
	}

	public void setErrors(Vector aErrors){
      _errors = aErrors;
	}

	public String getHtmlErrorMsg()	{
		StringBuffer htmlErrorMsgBuffer = new StringBuffer();
		String  currentMsg = getMessage();
		if ( currentMsg != null ){
			htmlErrorMsgBuffer.append ( currentMsg );
		}
		for ( int i=0;  _errors!=null && i<_errors.size(); i++ ){
			Object errorInfo = _errors.get ( i );

			if ( errorInfo instanceof ErrorInfo )
				currentMsg = ((ErrorInfo)errorInfo).getErrorMsg();
			else if ( errorInfo instanceof String )
				currentMsg = (String)errorInfo;
			else
				currentMsg = null;

			if ( currentMsg!=null && currentMsg.length() > 0 ){
				if ( htmlErrorMsgBuffer.length() > 0 )
					htmlErrorMsgBuffer.append ( "<br>" );
				htmlErrorMsgBuffer.append ( currentMsg );
			}
		}
    	return htmlErrorMsgBuffer.toString();
	}
}