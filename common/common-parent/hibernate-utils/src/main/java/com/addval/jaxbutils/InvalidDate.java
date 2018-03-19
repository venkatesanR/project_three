package com.addval.jaxbutils;

import java.util.Date;

public class InvalidDate extends Date {
	private String value = null;
	private static final long serialVersionUID = 1L;

	public InvalidDate(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
