//Source file: c:\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\OracleSQLExceptionTranslator.java

//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\OracleSQLExceptionTranslator.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import org.apache.regexp.RESyntaxException;
import com.addval.utils.XRuntime;
import java.util.Hashtable;
import org.apache.regexp.RE;
import java.sql.SQLException;

/**
 * @author AddVal Technology Inc.
 * Oracle specific translator
 */
public class OracleSQLExceptionTranslator extends SQLExceptionTranslator
{
	private Hashtable _errorMessages = null;
	private RE _regExp;
	private static final String _ERROR_PATTERN = "^[0-9A-Za-z\\-]*:[^.]*.([A-Za-z0-9_]*)";

	/**
	 * @param errorMessages
	 * @roseuid 3EBC53F70399
	 */
	public OracleSQLExceptionTranslator()
	{
		setErrorRegularExpression( _ERROR_PATTERN );
	}

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A8B0141
	 */
	public boolean isUniqueKeyViolation(SQLException exception)
	{
		while (exception != null) {
			if ( exception.getErrorCode() == 1 )
				return true;
			exception = exception.getNextException();
		}
		return false;
	}

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A8B014B
	 */
	public boolean isForeignKeyViolation(SQLException exception)
	{
		while (exception != null) {
			if ( exception.getErrorCode() == 2291 || exception.getErrorCode() == 2292 )
				return true;
			exception = exception.getNextException();
		}
		return false;
	}

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A8B015F
	 */
	public boolean isFatalViolation(SQLException exception)
	{
		while (exception != null) {
			if (exception.getErrorCode() == 1 ||
				(exception.getErrorCode() >= 2290 && exception.getErrorCode() <= 2292))
				return false;
			exception = exception.getNextException();
		}
		return true;
	}
}
