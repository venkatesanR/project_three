/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.command;

import java.io.Serializable;

public class ErrorInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String _errorMsg;
	private Object _data = null;

	public ErrorInfo(String errorMsg, Object data) {
		_errorMsg = errorMsg;
		_data = data;
	}

	public String getErrorMsg() {
		return _errorMsg;
	}

	public void setErrorMsg(String aErrorMsg) {
		_errorMsg = aErrorMsg;
	}

	public Object getData() {
		return _data;
	}

	public void setData(Object aData) {
		_data = aData;
	}

}
