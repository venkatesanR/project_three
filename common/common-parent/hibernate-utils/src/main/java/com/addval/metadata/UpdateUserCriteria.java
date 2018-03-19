
package com.addval.metadata;

import java.io.Serializable;


public class UpdateUserCriteria implements Serializable
{
	private String editorName = null;
	private String directoryName = null;
	private String criteriaName = null;
	private String criteriaDesc = null;
	private String filter = null;
	private String defaultFilter = null;
	private String updateValue = null;

	public UpdateUserCriteria(String editorName,String directoryName,String criteriaName,String criteriaDesc,String filter,String defaultFilter,String updateValue)
	{
		this.editorName = editorName;
		this.directoryName = directoryName;
		this.criteriaName = criteriaName;
		this.criteriaDesc = criteriaDesc;
		this.filter = filter;
		this.defaultFilter = defaultFilter;
		this.updateValue = updateValue;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getCriteriaName() {
		return criteriaName;
	}

	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}

	public String getCriteriaDesc() {
		return criteriaDesc;
	}

	public void setCriteriaDesc(String criteriaDesc) {
		this.criteriaDesc = criteriaDesc;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getDefaultFilter() {
		return defaultFilter;
	}

	public void setDefaultFilter(String defaultFilter) {
		this.defaultFilter = defaultFilter;
	}

	public String getUpdateValue() {
		return updateValue;
	}

	public void setUpdateValue(String updateValue) {
		this.updateValue = updateValue;
	}
}
