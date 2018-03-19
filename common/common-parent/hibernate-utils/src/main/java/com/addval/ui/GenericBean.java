package com.addval.ui;

import java.io.Serializable;

public class GenericBean<T> implements Serializable{
	private String id = null;
	
	private String controlName= null;
	private String controlNameOld= null;
	
	private String mappingName = null;
	private boolean udf = false;
	private T value;
	private String dataDelimiter = null;
	private String format = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getControlName() {
		return controlName;
	}

	public void setControlName(String controlName) {
		this.controlName = controlName;
	}

	public String getControlNameOld() {
		return controlNameOld;
	}

	public void setControlNameOld(String controlNameOld) {
		this.controlNameOld = controlNameOld;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getMappingName() {
		return mappingName;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	public boolean isUdf() {
		return udf;
	}

	public void setUdf(boolean udf) {
		this.udf = udf;
	}

	public String getDataDelimiter() {
		return dataDelimiter;
	}

	public void setDataDelimiter(String dataDelimiter) {
		this.dataDelimiter = dataDelimiter;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
