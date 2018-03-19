package com.addval.udfutils;

import java.io.Serializable;
import java.util.HashMap;


public class EntityDefn implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8968301640941529592L;

	/**
	 * Indicates the name of the entity definition
	 */
	protected String _name;

	protected String _className;

	protected HashMap<String, AttributeDefn> _attributeDefns;

	public EntityDefn() {

	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityDefn other = (EntityDefn) obj;
		if (_name == null) {
			if (other._name != null)
				return false;
		} else if (!_name.equals(other._name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EntityDefn [_name=" + _name + "]";
	}

	public String getClassName() {
		return _className;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public HashMap<String, AttributeDefn> getAttributeDefns() {
		return _attributeDefns;
	}

	public void setAttributeDefns(HashMap<String, AttributeDefn> attributeDefns) {
		_attributeDefns = attributeDefns;
	}


}
