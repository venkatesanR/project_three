/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.hibernateutils;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.addval.command.Command;

public class CRUDCommand extends Command {
	private static final long serialVersionUID = 1L;
	public final static String ACTION_INSERT = "create";
	public final static String ACTION_UPDATE = "update";
	public final static String ACTION_MERGE = "merge";
	public final static String ACTION_DELETE = "delete";
	public final static String ACTION_READ = "read";
	public final static String ACTION_SEARCH = "search";
	public final static String ACTION_SEARCH_BY_CRITERIA = "searchByCriteria";

	private Object _value;
	private String _entityName;
	private java.io.Serializable _key;
	private String _action;
	private DetachedCriteria _criteria;
	private int _firstResult = 0;
	private int _maxResult = -1;
	private boolean _countOnly = false;

	private List _searchOutput;
	private int _searchResultCount;
	private Object _readOutput;

	private Class aliasToBean = null; // ResultTransformer

	public CRUDCommand() {
	}

	// command inputs
	public void setValue(Object aValue) {
		_value = aValue;
	}

	public Object getValue() {
		return _value;
	}

	public void setEntityName(String aEntityName) {
		_entityName = aEntityName;
	}

	public String getEntityName() {
		return _entityName;
	}

	public void setAction(String aAction) {
		_action = aAction;
	}

	public String getAction() {
		return _action;
	}

	public void setKey(java.io.Serializable aKey) {
		_key = aKey;
	}

	public java.io.Serializable getKey() {
		return _key;
	}

	public void setCriteria(DetachedCriteria aCriteria) {
		_criteria = aCriteria;
	}

	public org.hibernate.criterion.DetachedCriteria getCriteria() {
		return _criteria;
	}

	public int getFirstResult() {
		return _firstResult;
	}

	public int getMaxResult() {
		return _maxResult;
	}

	public void setFirstResult(int firstResult) {
		_firstResult = firstResult;
	}

	public void setMaxResult(int maxResult) {
		_maxResult = maxResult;
	}

	public void setCountOnly(boolean countOnly) {
		_countOnly = countOnly;
	}

	public boolean getCountOnly() {
		return _countOnly;
	}

	public Class getAliasToBean() {
		return aliasToBean;
	}

	public void setAliasToBean(Class aliasToBean) {
		this.aliasToBean = aliasToBean;
	}

	// command outputs
	public List getSearchOutput() {
		return _searchOutput;
	}

	public void setSearchOutput(List aOutput) {
		_searchOutput = aOutput;
	}

	public int getSearchResultCount() {
		return _searchResultCount;
	}

	public void setSearchResultCount(int count) {
		_searchResultCount = count;
	}

	public Object getReadOutput() {
		return _readOutput;
	}

	public void setReadOutput(Object aOutput) {
		_readOutput = aOutput;
	}
}