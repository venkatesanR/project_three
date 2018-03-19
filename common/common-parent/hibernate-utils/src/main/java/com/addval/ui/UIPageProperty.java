package com.addval.ui;

import java.io.Serializable;

public class UIPageProperty implements Serializable {

	private String pageName;
	private String editorType;
	
	private String propertyName = null;
	private String propertyValue = null;
	
	public UIPageProperty(){
		
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	public void copy(UIPageProperty uiPageProperty){
		this.pageName = uiPageProperty.getPageName();
		this.editorType = uiPageProperty.getEditorType();
		this.propertyName = uiPageProperty.getPropertyName();
		this.propertyValue = uiPageProperty.getPropertyValue();
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(" [ ").append("propertyName").append("=").append(propertyName).append(" ] ");
		buf.append(" [ ").append("propertyValue").append("=").append(propertyValue).append(" ] ");
		return buf.toString();
	}
	
}
