package com.addval.udfutils;

import java.io.Serializable;


public class AttributeDefn implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8968301640941529592L;

	/**
	 * Indicates the name of the entity definition
	 */
	protected String _name;

    protected AttributeType _type;

    protected Boolean _required = false;

    protected Boolean _uppercase = false;

    protected Integer _maxLength = 0;

    protected String _defaultValue;

    protected Boolean _activeStatus = false;

	public AttributeDefn() {

	}


	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public AttributeType getType() {
		return _type;
	}

	public void setType(AttributeType type) {
		_type = type;
	}

	public Boolean getRequired() {
		return _required;
	}

	public void setRequired(Boolean required) {
		_required = required;
	}

	public Boolean getUppercase() {
		return _uppercase;
	}

	public void setUppercase(Boolean uppercase) {
		_uppercase = uppercase;
	}

	public Integer getMaxLength() {
		return _maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		_maxLength = maxLength;
	}

	public String getDefaultValue() {
		return _defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		_defaultValue = defaultValue;
	}

	public Boolean isActiveStatus() {
		return _activeStatus;
	}

	public void setActiveStatus(Boolean activeStatus) {
		_activeStatus = activeStatus;
	}

}
