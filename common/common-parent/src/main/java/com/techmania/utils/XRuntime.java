package com.techmania.utils;

public class XRuntime extends RuntimeException {

	private String _source;

	private int _errNumber;

	public XRuntime(String source, String desc, int errNumber) {

		super(desc);
		_source = source;
		_errNumber = errNumber;
	}

	public XRuntime(String source, String desc) {

		super(desc);
		_source = source;
	}

	public XRuntime(String source, Throwable e) {
		super(e.getMessage());
		_source = source;
	}

	public XRuntime(String desc) {
		super(desc);
	}

	public String getSource() {
		return _source;
	}

	public String getMessage() {
		return super.getMessage();
	}

	public int getErrNumber() {

		return _errNumber;
	}

	public String toString() {
		return ("XRuntime: Source : " + getSource() + "; Error: " + getMessage());
	}
}
