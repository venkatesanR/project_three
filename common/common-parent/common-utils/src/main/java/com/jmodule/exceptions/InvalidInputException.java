package com.jmodule.exceptions;

public class InvalidInputException extends RuntimeException {
	private static final long serialVersionUID = -5585470120154696481L;
	public InvalidInputException(String message) {
		super(message);
	}
}
