/* AddVal Technology Inc. */

package com.addval.utils;


/**
 * The static methods of this class convert the exception messages into the user-friendly diagnostics.
 */
public class ErrorMessageTranslator
{

    /**
     * @roseuid 3C1A8B4B02DB
     */
    public ErrorMessageTranslator()
    {
    }

    /**
     * @param exception
     * @return String
     * @roseuid 3C1A8B6A000E
     */
    public static String translate(Exception exception)
    {
		String exceptionMessage   = exception.getMessage();
		String exceptionClassName = exception.getClass().getName();

		String rv = translate ( exceptionMessage, exceptionClassName );

		return rv;
    }

    /**
     * @param message
     * @param exceptionClassName
     * @return String
     * @roseuid 3C1A8BA3033C
     */
    public static String translate(String message, String exceptionClassName)
    {
		String rv = "Error has been detected.  Please contact your system administrator.";
		return rv;
    }
}
