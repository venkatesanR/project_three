package com.addval.udfutils;

import java.io.Serializable;
import java.util.Date;

public class AttributeValue implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8968701640941529592L;

	private AttributeDefn _attributeDefn = null;

	private String _stringValue = null;
	private Date _dateValue = null;
	private Double _numericValue = null;
	private Boolean _booleanValue = null;

	public AttributeDefn getAttributeDefn() {
		return _attributeDefn;
	}
	public void setAttributeDefn(AttributeDefn attributeDefn) {
		_attributeDefn = attributeDefn;
	}
	public String getStringValue() {
		return _stringValue;
	}
	public void setStringValue(String stringValue) {
		_stringValue = stringValue;
	}
	public Date getDateValue() {
		return _dateValue;
	}
	public void setDateValue(Date dateValue) {
		_dateValue = dateValue;
	}
	public Double getNumericValue() {
		return _numericValue;
	}
	public void setNumericValue(Double numericValue) {
		_numericValue = numericValue;
	}
	public Boolean getBooleanValue() {
		return _booleanValue;
	}
	public void setBooleanValue(Boolean booleanValue) {
		_booleanValue = booleanValue;
	}



}
