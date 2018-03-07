package com.jmodule.threads;

public class ProcessException extends RuntimeException {
	private static final long serialVersionUID = 1206072002515954721L;

	public ProcessException(String message, Throwable t) {
		super(message, t);
	}
}
