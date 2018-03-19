package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class UIPageConfig implements Serializable {
	private String pageName;
	private String editorType;

	private List<UIPageProperty> uiPageProperties = null; 
	private Hashtable<String, UIPageSectionConfig> uiPageSectionConfig = null;

	public UIPageConfig(){
		
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

	public List<UIPageProperty> getUIPageProperties() {
		if(uiPageProperties == null){
			uiPageProperties = new ArrayList<UIPageProperty>();
		}
		return uiPageProperties;
	}
	
	public void setUIPageProperties(List<UIPageProperty> uiPageProperties) {
		this.uiPageProperties = uiPageProperties;
	}

	public UIPageProperty getUIUIPageProperty(String propertyName) {
		if(uiPageProperties != null){
			for(UIPageProperty uiPageProperty:uiPageProperties){
				if(uiPageProperty.getPropertyName().equalsIgnoreCase( propertyName )){
					return uiPageProperty;
				}
			}
		}
		return null;
	}

	
	public Hashtable<String, UIPageSectionConfig> getUIPageSectionConfig() {
		if(uiPageSectionConfig == null){
			uiPageSectionConfig= new Hashtable<String, UIPageSectionConfig>();
		}
		return uiPageSectionConfig;
	}

	public void setUIPageSectionConfig(Hashtable<String, UIPageSectionConfig> uiPageSectionConfig) {
		this.uiPageSectionConfig = uiPageSectionConfig;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(" [ ").append("pageName").append("=").append(pageName).append(" ] ");
		buf.append(" [ ").append("editorType").append("=").append(editorType).append(" ] ");
		if(uiPageProperties != null){
			buf.append(System.getProperty("line.separator")).append("UIPageProperties").append("{").append(System.getProperty("line.separator"));
			for(UIPageProperty uiPageProperty : uiPageProperties){
				buf.append(uiPageProperty);
				buf.append(System.getProperty("line.separator"));
			}
			buf.append(System.getProperty("line.separator")).append("}");
		}

		if(uiPageSectionConfig != null){
			buf.append(System.getProperty("line.separator")).append("UIPageSectionConfig").append("{").append(System.getProperty("line.separator"));
			for(UIPageSectionConfig uiSectionConfig : uiPageSectionConfig.values()){
				buf.append(uiSectionConfig);
				buf.append(System.getProperty("line.separator"));
			}
			buf.append(System.getProperty("line.separator")).append("}");
		}
		return buf.toString();
	}
}
