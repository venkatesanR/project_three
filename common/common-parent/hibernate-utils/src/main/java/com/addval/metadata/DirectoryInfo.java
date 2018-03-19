package com.addval.metadata;

import java.io.Serializable;

public class DirectoryInfo implements Serializable{
	private static final long serialVersionUID = 4079972331166117642L;

	private String editorName = null;
	private String directoryName = null;
	private String directoryDesc = null;
	private String directoryCondition = null;
	
	public DirectoryInfo(){
		
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

	public String getDirectoryDesc() {
		return directoryDesc;
	}

	public void setDirectoryDesc(String directoryDesc) {
		this.directoryDesc = directoryDesc;
	}

	public String getDirectoryCondition() {
		return directoryCondition;
	}

	public void setDirectoryCondition(String directoryCondition) {
		this.directoryCondition = directoryCondition;
	}

}
