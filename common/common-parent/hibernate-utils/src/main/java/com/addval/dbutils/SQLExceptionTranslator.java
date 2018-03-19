//Source file: c:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\SQLExceptionTranslator.java

//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\SQLExceptionTranslator.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.SQLException;
import org.apache.regexp.RESyntaxException;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;
import java.util.Hashtable;
import org.apache.regexp.RE;

/**
 * @author AddVal Technology Inc.
 */
public abstract class SQLExceptionTranslator
{

	protected Hashtable _errorMessages = null;
	protected RE _regExp = null;
	protected int _keyParenNumber = 1;

	public Hashtable getErrorMessages() {
		return _errorMessages;
	}

	public void setErrorMessages(Hashtable errorMessages) {
		_errorMessages = errorMessages;
	}

	public static SQLExceptionTranslator getInstance(String sqlExceptionTranslatorClass, String serverType, Hashtable errorMessages) {

		try {
			Class translatorClass = Class.forName(sqlExceptionTranslatorClass);
			SQLExceptionTranslator sqlExTranslator = (SQLExceptionTranslator)translatorClass.newInstance();
			sqlExTranslator.setErrorMessages( errorMessages );
			return sqlExTranslator;
		}
		catch(Exception e) {
			return null;
		}
	}

	public void setErrorRegularExpression( String pattern ) {
		try {
			_regExp 		= new RE( pattern );
		}
		catch (RESyntaxException rse) {

			throw new XRuntime( "SQLExceptionTranslator", "Regular Expression Syntax for parsing the error message is invalid : " + pattern );
		}
	}

	/**
	 * @param exception
	 * @return java.lang.String
	 * @roseuid 3EBC456501F4
	 */
	public String translate(SQLException exception) {
		
		if (_errorMessages != null) {
			int errorCode = exception.getErrorCode();
			String errorMessage = exception.getMessage();
			return translate(errorCode,errorMessage);
		}
		return null;
	}

	public String translate(int errorCode, String errorMessage){
		String message = null;
		String keyIndex = "KEY_VALUE[";
		String keyMsg = "";
		if ( errorMessage.indexOf(keyIndex) > -1 ) {
			keyMsg = errorMessage.substring( errorMessage.indexOf(keyIndex) + 10  , errorMessage.length() );
			keyMsg = keyMsg.substring( 0  , keyMsg.indexOf("]") );
		}
		boolean matched = _regExp.match( errorMessage );
		if (matched) {
			String key = String.valueOf( errorCode ) + _regExp.getParen( _keyParenNumber );
			message = (String)_errorMessages.get( key );
			if ( !StrUtl.isEmptyTrimmed(keyMsg) ) {
				message = keyMsg + " " +  message;
				return message;
			}
			if (message != null) {
				message = message + "[" + key + "]";
			}
		}
		return message;
	}
	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F1309800255
	 */
	public abstract boolean isUniqueKeyViolation(SQLException exception);

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A6401D1
	 */
	public abstract boolean isForeignKeyViolation(SQLException exception);

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A65029B
	 */
	public abstract boolean isFatalViolation(SQLException exception);
}
