package com.addval.metadata;

import java.io.Serializable;

public class EditorFilter implements Serializable, Cloneable {
	private static final long serialVersionUID = -8739065675801124657L;
	
	private String _filterName = null;
	private String _filterDesc = null;
	private String _filterSql = null;
	
	public EditorFilter(){
		
	}
	
	public EditorFilter(String filterName,String filterDesc,String filterSql){
		setFilterName(filterName);
		setFilterDesc(filterDesc);
		setFilterSql(filterSql);
	}

	public String getFilterName() {
		return _filterName;
	}

	public void setFilterName(String filterName) {
		this._filterName = filterName;
	}

	public String getFilterDesc() {
		return _filterDesc;
	}

	public void setFilterDesc(String filterDesc) {
		this._filterDesc = filterDesc;
	}

	public String getFilterSql() {
		return _filterSql;
	}

	public void setFilterSql(String filterSql) {
		this._filterSql = filterSql;
	}

	public Object clone() {
		EditorFilter newEditorFilter= new EditorFilter(getFilterName(),getFilterDesc(),getFilterSql());
		return newEditorFilter;
	}
}
