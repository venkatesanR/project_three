package com.jmodules.threads;

public class SchedulerResponse {
	private String errorMessge;
	private String message;

	public SchedulerResponse(String message) {
		this.message = message;
	}

	public SchedulerResponse(String message, String errorMessge) {
		this(message);
		this.errorMessge = errorMessge;
	}

	public String getErrorMessge() {
		return errorMessge;
	}

	public void setErrorMessge(String errorMessge) {
		this.errorMessge = errorMessge;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
