package com.addval.esapiutils.validator;

public class AppSecurityValidatorException extends Exception {
	private static final long serialVersionUID = 1L;

	private Throwable exceptionCause;
	private String message;

	public AppSecurityValidatorException() {
		super();
	}

	public AppSecurityValidatorException(String message) {
		this.message = message;
	}

	public AppSecurityValidatorException(Throwable exceptionCause) {
		super();
		this.exceptionCause = exceptionCause;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getCause() {
		return exceptionCause;
	}

}
