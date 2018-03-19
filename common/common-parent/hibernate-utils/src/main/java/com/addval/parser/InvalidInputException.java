//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\InvalidInputException.java

package com.addval.parser;

import java.util.Vector;

public class InvalidInputException extends Exception 
{
    private Vector _errors = null;
    
    /**
    @param message
    @roseuid 3D6B0B1C0041
     */
    public InvalidInputException(String message) 
    {
        super(message);     
    }
    
    /**
    @param message
    @param errors
    @roseuid 3D6B0B1B034D
     */
    public InvalidInputException(String message, java.util.Vector errors) 
    {
        super(message);
        _errors = errors;     
    }
    
    /**
    @param errors
    @roseuid 3D6B0B1B027A
     */
    public InvalidInputException(java.util.Vector errors) 
    {
        _errors = errors;     
    }
    
	/**
	 * Added this constructor to preserve stack trace, to fix sonar violation
	 */
	public InvalidInputException(String message, Throwable e) {
		super(message, e);
	}
	
    /**
    Access method for the _errors property.
    @return   the current value of the _errors property
    @roseuid 3D6B0B1C0055
     */
    public Vector getErrors() 
    {
        return _errors;     
    }
    
    /**
    Sets the value of the _errors property.
    @param aErrors the new value of the _errors property
    @roseuid 3D6B0B1C0113
     */
    public void setErrors(java.util.Vector aErrors) 
    {
        _errors = aErrors;     
    }
    
    /**
    @return String
    @roseuid 3D6B0B1C01E6
     */
    public String getHtmlErrorMsg() 
    {
        StringBuffer htmlErrorMsgBuffer = new StringBuffer();
        String  currentMsg = getMessage();
        if ( currentMsg != null )
        {
            htmlErrorMsgBuffer.append ( currentMsg );
        }
        for ( int i=0;  _errors!=null && i<_errors.size(); i++ )
        {
            currentMsg = (String) _errors.get ( i );
            if ( currentMsg!=null && currentMsg.length()>0 )
            {
                if ( htmlErrorMsgBuffer.length() > 0 )
                    htmlErrorMsgBuffer.append ( "<br>" );
                htmlErrorMsgBuffer.append ( currentMsg );
            }
        }
        return htmlErrorMsgBuffer.toString();     
    }
}
