//Source file: C:\\users\\prasad\\Projects\\cargores\\source\\com\\addval\\cfc\\InvalidInputException.java

//Source file: D:\\Projects\\CargoRes\\source\\com\\addval\\cfc\\InvalidInputException.java

package com.techmania.common.exceptions;

import java.util.Vector;

/**
 * This is an application exception that may be generated by CargoRes Engines.
 */
public class XInvalidInput extends Exception implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3025437665849259247L;

    /**
     * Indicates the error code for the application exception
     */
    private int _errorCode;

    /**
     * Indicates the Error Info
     */
    private Vector _errors;

    /**
     * @param errorCode
     * @param errorMessage
     * @roseuid 3EC028310148
     */
    public XInvalidInput(int errorCode, String errorMessage) {
        super(errorMessage);

        setErrorCode(errorCode);
    }

    /**
     * @param errorCode
     * @param errorMessage
     * @param errors
     * @roseuid 3EC028190119
     */
    public XInvalidInput(int errorCode, String errorMessage, Vector errors) {
        this(errorMessage, errors);

        setErrorCode(errorCode);
    }

    /**
     * @param errors
     * @roseuid 3D1CF6620376
     */
    public XInvalidInput(Vector errors) {
        _errors = errors;
    }

    /**
     * @param message
     * @param errors
     * @roseuid 3D1CF4DB0388
     */
    public XInvalidInput(String message, Vector errors) {
        super(message);
        _errors = errors;
    }

    /**
     * @param message
     * @roseuid 3C71604D0106
     */
    public XInvalidInput(String message) {
        super(message);
    }

    /**
     * Access method for the _errorCode property.
     *
     * @return the current value of the _errorCode property
     */
    public int getErrorCode() {
        return _errorCode;
    }

    /**
     * Sets the value of the _errorCode property.
     *
     * @param aErrorCode the new value of the _errorCode property
     */
    public void setErrorCode(int aErrorCode) {
        _errorCode = aErrorCode;
    }

    /**
     * Access method for the _errors property.
     *
     * @return the current value of the _errors property
     */
    public Vector getErrors() {
        return _errors;
    }

    /**
     * Sets the value of the _errors property.
     *
     * @param aErrors the new value of the _errors property
     */
    public void setErrors(Vector aErrors) {
        _errors = aErrors;
    }

    /**
     * @return java.lang.String
     * @roseuid 3D1CF6AE01A9
     */
    public String getHtmlErrorMsg() {
        StringBuffer htmlErrorMsgBuffer = new StringBuffer();
        String currentMsg = getMessage();
        if (currentMsg != null) {
            htmlErrorMsgBuffer.append(currentMsg);
        }
        for (int i = 0; _errors != null && i < _errors.size(); i++) {
            Object errorInfo = _errors.get(i);

            if (errorInfo instanceof String) {
                currentMsg = (String) errorInfo;
            } else {
                currentMsg = null;
            }

            if (currentMsg != null && currentMsg.length() > 0) {
                if (htmlErrorMsgBuffer.length() > 0) {
                    htmlErrorMsgBuffer.append("<br>");
                }
                htmlErrorMsgBuffer.append(currentMsg);
            }
        }
        return htmlErrorMsgBuffer.toString();
    }
}
