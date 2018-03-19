package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UIPageSectionConfig implements Serializable{

	private String pageName;
	private String editorType;
	private String componentId;
	private String componentType;

	private List<UIPageSectionProperty> uiPageSectionProperties = null;
	private List<UIPageSectionAttribute> uiPageSectionAttributes = null;

	public UIPageSectionConfig(){

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

	public List<UIPageSectionProperty> getUIPageSectionProperties() {
		if(uiPageSectionProperties == null){
			uiPageSectionProperties = new ArrayList<UIPageSectionProperty>();
		}
		return uiPageSectionProperties;
	}

	public void setUIPageSectionProperties(List<UIPageSectionProperty> uiPageSectionProperties) {
		this.uiPageSectionProperties = uiPageSectionProperties;
	}

	public UIPageSectionProperty getUIPageSectionProperty(String propertyName) {
		if(uiPageSectionProperties != null){
			for(UIPageSectionProperty uiPageSectionProperty:uiPageSectionProperties){
				if(uiPageSectionProperty.getPropertyName().equalsIgnoreCase( propertyName )){
					return uiPageSectionProperty;
				}
			}
		}
		return null;
	}


	public List<UIPageSectionAttribute> getUIPageSectionAttributes() {
		if(uiPageSectionAttributes == null){
			uiPageSectionAttributes =new ArrayList<UIPageSectionAttribute>();
		}
		return uiPageSectionAttributes;
	}

	public void setUIPageSectionAttributes(List<UIPageSectionAttribute> uiPageSectionAttributes) {
		this.uiPageSectionAttributes = uiPageSectionAttributes;
	}
	
	public String toString(){

		StringBuffer buf = new StringBuffer();
		buf.append(" [ ").append("componentId").append("=").append(componentId).append(" ] ");
		buf.append(" [ ").append("componentType").append("=").append(componentType).append(" ] ");
		if(uiPageSectionAttributes != null){
			buf.append(System.getProperty("line.separator")).append("UIPageSectionAttribute").append("{").append(System.getProperty("line.separator"));
			for(UIPageSectionAttribute uiPageSectionAttribute : uiPageSectionAttributes){
				buf.append(uiPageSectionAttribute);
				buf.append(System.getProperty("line.separator"));
			}
			buf.append(System.getProperty("line.separator")).append("}");
		}

		if(uiPageSectionProperties != null){
			buf.append(System.getProperty("line.separator")).append("UIPageSectionProperty").append("{").append(System.getProperty("line.separator"));
			for(UIPageSectionProperty uiPageSectionProperty : uiPageSectionProperties){
				buf.append(uiPageSectionProperty);
				buf.append(System.getProperty("line.separator"));
			}
			buf.append(System.getProperty("line.separator")).append("}");
		}
		return buf.toString();
	}
	


}
