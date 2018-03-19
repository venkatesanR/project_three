package com.addval.ui;

import java.io.Serializable;

public class EditorField implements Serializable, Comparable<EditorField> {
	private static final long serialVersionUID = 8068299563860003948L;
	private String value = null;

	public EditorField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public int compareTo(EditorField that) {
		if (this.getValue() == that.getValue()) {
			return 0;
		}

		if (that.getValue() == null) {
			return 1;
		}

		if (this.getValue() == null) {
			return -1;
		}
		return this.getValue().compareTo(that.getValue());
	}
	
	public String toString(){
		return getValue();
	}
}
