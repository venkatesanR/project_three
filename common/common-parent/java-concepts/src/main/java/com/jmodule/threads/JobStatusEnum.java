package com.jmodule.threads;

public enum JobStatusEnum {
	SCHEDULED(1, "Scheduled"), RUNNING(2, "Running"), STOPPED(2, "Stopped"), ERROR(4, "Error");
	private Integer id;
	private String message;

	JobStatusEnum(Integer id, String message) {
		this.id = id;
		this.message = message;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
