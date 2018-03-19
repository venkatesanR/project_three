
/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.SQLException;

/**
 * @author AddVal Technology Inc.
 * Sybase specific translator
 */
public class SybaseSQLExceptionTranslator extends SQLExceptionTranslator
{
	private static final String _ERROR_PATTERN = "(with index|constraint name =|with unique index| for object| into column) '([A-Za-z0-9_]*)'";

	/**
	 * @param errorMessages
	 * @roseuid 3EBC542300CB
	 */
	public SybaseSQLExceptionTranslator()
	{
		setErrorRegularExpression( _ERROR_PATTERN );
		_keyParenNumber = 2;
	}

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A9202BD
	 */
	public boolean isUniqueKeyViolation(SQLException exception)
	{
		return false;
	}

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A9202D1
	 */
	public boolean isForeignKeyViolation(SQLException exception)
	{
		return false;
	}

	/**
	 * @param exception
	 * @return boolean
	 * @roseuid 3F130A9202DB
	 */
	public boolean isFatalViolation(SQLException exception)
	{
		return true;
	}
}
