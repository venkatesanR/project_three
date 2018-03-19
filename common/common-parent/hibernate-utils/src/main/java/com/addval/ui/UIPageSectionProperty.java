package com.addval.ui;

import java.io.Serializable;

public class UIPageSectionProperty implements Serializable {

	private String pageName;
	private String editorType;
	private String componentId;
	private String componentType;
	
	private String propertyName = null;
	private String propertyValue = null;
	
	public UIPageSectionProperty(){
		
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

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
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
	public void copy(UIPageSectionProperty uiPageSectionProperty){
		this.pageName = uiPageSectionProperty.getPageName();
		this.editorType = uiPageSectionProperty.getEditorType();
		this.componentId = uiPageSectionProperty.getComponentId();
		this.componentType = uiPageSectionProperty.getComponentType();
		this.propertyName = uiPageSectionProperty.getPropertyName();
		this.propertyValue = uiPageSectionProperty.getPropertyValue();
	}

	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(" [ ").append("propertyName").append("=").append(propertyName).append(" ] ");
		buf.append(" [ ").append("propertyValue").append("=").append(propertyValue).append(" ] ");
		return buf.toString();
	}
}
