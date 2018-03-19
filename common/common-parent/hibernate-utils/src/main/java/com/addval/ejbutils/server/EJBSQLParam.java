package com.addval.ejbutils.server;

import java.io.Serializable;

public class EJBSQLParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private int index;
	private int type;
	private Object value;

	public EJBSQLParam(int index, int sqlType, Object value) {
		setIndex(index);
		setType(sqlType);
		setValue(value);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
