package com.addval.esapiutils.encoder;

public class AppSecurityEncoderException extends Exception{
	private static final long serialVersionUID = 1L;

	private Throwable exceptionCause;
	private String message;

	public AppSecurityEncoderException() {
		super();
	}

	public AppSecurityEncoderException(String message) {
		this.message = message;
	}

	public AppSecurityEncoderException(Throwable exceptionCause) {
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
