package com.addval.action;

import java.io.Serializable;

public class ActionMessage implements Serializable {
	private static final long serialVersionUID = -4066489089758792144L;
	protected String key = null;
	protected Object values[] = null;
	protected boolean resource;

	public ActionMessage(String key) {
		this(key, ((Object[]) (null)));
	}

	public ActionMessage(String key, Object value0) {
		this(key, new Object[] { value0 });
	}

	public ActionMessage(String key, Object value0, Object value1) {
		this(key, new Object[] { value0, value1 });
	}

	public ActionMessage(String key, Object value0, Object value1, Object value2) {
		this(key, new Object[] { value0, value1, value2 });
	}

	public ActionMessage(String key, Object value0, Object value1, Object value2, Object value3) {
		this(key, new Object[] { value0, value1, value2, value3 });
	}

	public ActionMessage(String key, Object values[]) {
		this.key = key;
		this.values = values;
		resource = true;
	}

	public ActionMessage(String key, boolean resource) {
		this.key = key;
		this.resource = resource;
	}

	public String getKey() {
		return key;
	}

	public Object[] getValues() {
		return values;
	}

	public boolean isResource() {
		return resource;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(key);
		buff.append("[");
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				buff.append(values[i]);
				if (i < values.length - 1) {
					buff.append(", ");
				}
			}

		}
		buff.append("]");
		return buff.toString();
	}
}
