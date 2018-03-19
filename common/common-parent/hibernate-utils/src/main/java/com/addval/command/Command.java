/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.command;

public class Command implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private transient Service _service;

	private String _serviceName;
	private boolean _successFlag;
	private String _failureReason;

	public void setService(Service svc) {
		_service = svc;
	}

	public Service getService() {
		return _service;
	}

	public boolean getSuccessFlag() {
		return _successFlag;
	}

	public void setSuccessFlag(boolean flag) {
		_successFlag = flag;
	}

	public String getFailureReason() {
		return _failureReason;
	}

	public void setFailureReason(String aReason) {
		_failureReason = aReason;
	}

	public String getServiceName() {
		return _serviceName;
	}

	public void setServiceName(String name) {
		_serviceName = name;
	}

	public void execute() throws CommandException {
		getService().execute(this);
	}
}