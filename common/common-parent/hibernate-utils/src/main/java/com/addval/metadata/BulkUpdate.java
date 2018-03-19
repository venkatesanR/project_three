package com.addval.metadata;

import java.io.Serializable;
import java.sql.Timestamp;

public class BulkUpdate implements Serializable {
	private static final long serialVersionUID = -5794010261123025973L;
	private String editorName = null;
	private String directoryName = null;
	private String updateName = null;
	private String updateDesc = null;
	private String updateValue = null;
	private String filter = null;
	
	private String updateStatusCode = null;
	private String updateStatusDesc = null;
	
	private String 		lastUpdatedBy	= null;
	private Timestamp	lastUpdatedDate = null;

	public BulkUpdate(){
		
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
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	public String getUpdateDesc() {
		return updateDesc;
	}
	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}
	public String getUpdateValue() {
		return updateValue;
	}
	public void setUpdateValue(String updateValue) {
		this.updateValue = updateValue;
	}
	public String getFilter() {
		if(filter != null){
			return filter.trim();
		}
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getUpdateStatusCode() {
		return updateStatusCode;
	}

	public void setUpdateStatusCode(String updateStatusCode) {
		this.updateStatusCode = updateStatusCode;
	}

	public String getUpdateStatusDesc() {
		return updateStatusDesc;
	}

	public void setUpdateStatusDesc(String updateStatusDesc) {
		this.updateStatusDesc = updateStatusDesc;
	}
	
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Timestamp getLastUpdatedDate() {
		return lastUpdatedDate;
	}
	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
	
}
