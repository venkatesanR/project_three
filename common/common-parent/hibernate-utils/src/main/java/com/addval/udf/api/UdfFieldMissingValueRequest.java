package com.addval.udf.api;

import com.addval.utils.StrUtl;

import java.io.Serializable;

/**
 * An instance of this class can be inserted into a UdfField, when the UdfField is missing a value.
 */
public class UdfFieldMissingValueRequest implements Serializable {

	private String _requestOrigin;
	private String _requesterId;
	private String _requestExplanation;


	public String getRequestOrigin() {
		return _requestOrigin;
	}
	public void setRequestOrigin(String requestOrigin) {
		_requestOrigin = requestOrigin;
	}
	public boolean isSetRequestOrigin() {
		return !StrUtl.isEmptyTrimmed(_requestOrigin);
	}


	public String getRequesterId() {
		return _requesterId;
	}
	public void setRequesterId(String requesterId) {
		_requesterId = requesterId;
	}
	public boolean isSetRequesterId() {
		return !StrUtl.isEmptyTrimmed(_requesterId);
	}



	public String getRequestExplanation() {
		return _requestExplanation;
	}
	public void setRequestExplanation(String requestExplanation) {
		_requestExplanation = requestExplanation;
	}
	public boolean isSetRequestExplanation() {
		return !StrUtl.isEmptyTrimmed(_requestExplanation);
	}

}