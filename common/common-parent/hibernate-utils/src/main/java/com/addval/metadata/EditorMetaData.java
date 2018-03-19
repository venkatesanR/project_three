/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.addval.ui.UIComponentMetadata;
import com.addval.utils.Pair;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

public class EditorMetaData implements Serializable, Cloneable {
	private static final long serialVersionUID = -2730480754728094295L;
	private static final transient String _module = "EditorMetaData";
	private String _name = null;
	private String _desc = null;
	private String _type = null;
	private String _source = null;
	private String _sourceSql = null;
	private String _sourceName = null;
	private String _procedure = null;
	private boolean _initialLookup = false;
	private String _interceptor = null;
	private String _securityManager = null;
	private String _securityManagerForEdit = null;
	private String _formType = null;
	private boolean _listEdit = false;
	private boolean _addNewPriv = true;
	private boolean _bulkUploadPriv = false;
	private String _bulkUploadImage = null;
	private boolean _multiDeletePriv = false;
	private boolean _customDisplayPriv = true;
	private boolean _scheduledExportPriv = false;
	private boolean _cancelPriv = false;
	private boolean _footerPriv = true;
	private boolean _showHeader = true;
	private boolean _showPositions = true;

	private boolean _exportPriv = false;
	private boolean _exactMatchLkp = false;
	private boolean _hasHelp = false;
	private boolean _hasChild = false;
	private int _pageSize = 30;
	private int _searchColsPerRow = 0;
	private String _viewRole = "";
	private String _editRole = "";
	private String _childActions = "";
	private String _detailEditorName = "";
	private String _cacheToRefresh = "";

	private String _addlink = null;
	private String _addlinkImage = null;
	private String _deletelinkImage = null;
	private String _cancellinkImage = null;

	private String _clientInterceptorForEdit = null;
	private String _clientInterceptorForList = null;

	private String _updateSectionTitles = null;
	private String _updateColsPerRow = null;
	private String _envSpringBeanId = null;
	private boolean _queryPriv = false;
	private boolean _bulkUpdatePriv = false;
	private boolean _queryToolAdditionalFilter = false;

	/**
	 * Stores the index from 1 to columnCount
	 */
	private Hashtable _indexes = null;
	private boolean _editable = false;
	private Vector _columnsMetaData = null;

	private Vector _editorFilters = null;
	private List<UIComponentMetadata> _userSearchFilters = null;
	private List<UIComponentMetadata> _userTemplates = null;

	private List<String> _sharedSearchFilterNames = null;
	private List<String> _sharedUserTemplateNames = null;

	private boolean isSqlCached = true;

	private HashMap<String,String> columnAlias = null;
	
	private boolean _multiSorting = true;
	
	
	/**
	 * @param name
	 * @param desc
	 * @param type
	 * @param source
	 * @param procedure
	 * @param editable
	 * @roseuid 3AE62AA8032E
	 */
	public EditorMetaData(String name, String desc, String type, String source, String procedure, boolean editable) {

		_name = name;
		_desc = desc;
		_type = type;
		_source = source;
		_procedure = procedure;
		_editable = editable;
		_indexes = new Hashtable();
		_columnsMetaData = new Vector();
	}

	/**
	 * Returns name of the editor
	 *
	 * @return java.lang.String
	 * @roseuid 3AEDBC510197
	 */
	public String getName() {

		return _name;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C8D0F2F027A
	 */
	public String getDesc() {

		return _desc;
	}

	/**
	 * @param desc
	 * @roseuid 3D50785E033E
	 */
	public void setDesc(String desc) {

		_desc = desc;
	}

	/**
	 * Returns type of the editor
	 *
	 * @return java.lang.String
	 * @roseuid 3AEDBC520008
	 */
	public String getType() {

		return _type;
	}

	/**
	 * Returns the table or view name from where data for the editor is to be selected
	 *
	 * @return java.lang.String
	 * @roseuid 3AEDBC520239
	 */
	public String getSource() {

		return _source;
	}

	/**
	 * @param source
	 * @roseuid 3D5078790206
	 */
	public void setSource(String source) {

		_source = source;
	}

	public String getSourceSql() {
		return _sourceSql;
	}

	public void setSourceSql(String sourceSql) {
		this._sourceSql = sourceSql;
	}
	


	public String getSourceName() {
		return _sourceName;
	}

	public void setSourceName(String sourceName) {
		this._sourceName = sourceName;
	}

	/**
	 * Returns the procedure if any to be used for updates to data
	 *
	 * @return java.lang.String
	 * @roseuid 3AEDBC53008C
	 */
	public String getProcedure() {

		return _procedure;
	}

	/**
	 * @param procedure
	 * @roseuid 3D507879036F
	 */
	public void setProcedure(String procedure) {

		_procedure = procedure;
	}

	public boolean isInitialLookup() {
		return _initialLookup;
	}

	public void setInitialLookup(boolean initialLookup) {
		this._initialLookup = initialLookup;
	}

	public String getInterceptor() {
		return _interceptor;
	}

	public void setInterceptor(String interceptor) {
		this._interceptor = interceptor;
	}

	/**
	 * Returns a boolean indicating if the editor for the specific type is editable
	 *
	 * @return boolean
	 * @roseuid 3AEDBF4901E0
	 */
	public boolean isEditable() {

		return _editable;
	}

	/**
	 * @param columnName
	 * @return com.addval.metadata.ColumnMetaData
	 * @roseuid 3CFFEFFC018C
	 */
	public ColumnMetaData getColumnMetaData(String columnName) {

		Integer index = (Integer) _indexes.get(columnName);

		return index == null ? null : getColumnMetaData(index.intValue());
	}

	/**
	 * Returns the ColumnMetaData for the specified index. Index starts from 1 and goes to getColumnCount()
	 *
	 * @param index
	 * @return com.addval.metadata.ColumnMetaData
	 * @roseuid 3AEDBC6E015D
	 */
	public ColumnMetaData getColumnMetaData(int index) {

		if (!isColumnValid(index))
			throw new XRuntime(_module, "Column Index is Invalid : " + String.valueOf(index));

		return (ColumnMetaData) _columnsMetaData.elementAt(index - 1);
	}

	/**
	 * Sets the vector of ColumnMetaData
	 *
	 * @param columnsMetaData
	 * @roseuid 3B0F5F340315
	 */
	public void setColumnsMetaData(Vector columnsMetaData) {

		_columnsMetaData = columnsMetaData;
		_indexes = new Hashtable();

		int size = getColumnCount();

		// Store the index that the user sees ie. 1 to ColumnCount
		for (int index = 0; index < size; index++)
			_indexes.put(((ColumnMetaData) columnsMetaData.elementAt(index)).getName(), new Integer(index + 1));
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3AF841960111
	 */
	public Vector getColumnsMetaData() {

		return _columnsMetaData;
	}

	/**
	 * @param name
	 * @return int
	 * @roseuid 3AF84219030E
	 */
	public int getColumnIndex(String name) {


		Integer index = (Integer) _indexes.get(name.toUpperCase());

		if (index == null)
			throw new XRuntime(_module, "Column Not Found :" + name);

		return index.intValue();
	}

	/**
	 * @return int
	 * @roseuid 3AF8406202C7
	 */
	public int getColumnCount() {

		return _columnsMetaData == null ? 0 : _columnsMetaData.size();
	}

	/**
	 * Returns if the columnIndex is valid. Index starts from 1 and goes to getColumnCount()
	 *
	 * @param index
	 * @return boolean
	 * @roseuid 3AF997D70133
	 */
	public boolean isColumnValid(int index) {

		return index >= 1 && index <= getColumnCount();
	}

	/**
	 * @param name
	 * @return boolean
	 * @roseuid 3B659D4600B4
	 */
	public boolean isColumnValid(String name) {

		return (Integer) _indexes.get(name) != null;
	}

	/**
	 * @param columnMetaData
	 * @roseuid 3AF99A270383
	 */
	public void addColumnMetaData(ColumnMetaData columnMetaData) {

		_columnsMetaData.add(columnMetaData);
		_indexes.put(columnMetaData.getName(), new Integer(getColumnCount()));
	}

	/**
	 * Returns the columns that are searchable for the specific editor
	 *
	 * @return java.util.Vector
	 * @roseuid 3AF9D59B005E
	 */
	public Vector getSearchableColumns() {
		Vector<ColumnMetaData> columnsMetaData = new Vector<ColumnMetaData>();
		for(ColumnMetaData columnMetaData : (Vector<ColumnMetaData>) getColumnsMetaData()){
			if (columnMetaData.isSearchable()){
				columnsMetaData.add(columnMetaData);
			}
		}
		Collections.sort(columnsMetaData, new Comparator<ColumnMetaData>() {
			public int compare(ColumnMetaData arg1, ColumnMetaData arg2) {
				 return (arg2.getSearchOrderSeq() > arg1.getSearchOrderSeq() ? -1 : (arg1.getSearchOrderSeq() == arg2.getSearchOrderSeq() ? 0 : 1)); 
			}
		});
		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * Returns the columns that are aggregatable (group by) for the specific editor
	 *
	 * @return java.util.Vector
	 * @roseuid 3AF9D5AB01A1
	 */
	public Vector getAggregatableColumns() {

		Vector columnsMetaData = new Vector();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;

		while (iterator.hasNext()) {

			columnMetaData = (ColumnMetaData) iterator.next();

			if (columnMetaData.isAggregatable())
				columnsMetaData.add(columnMetaData);
		}

		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * Returns the columns that can be displayed for this editor
	 *
	 * @return java.util.Vector
	 * @roseuid 3B273D38020E
	 */
	public Vector getDisplayableColumns() {

		Vector columnsMetaData = new Vector();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;

		while (iterator.hasNext()) {

			columnMetaData = (ColumnMetaData) iterator.next();

			if (columnMetaData.isDisplayable())
				columnsMetaData.add(columnMetaData);
		}

		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	public Vector getUpdatableColumns() {
		Vector columnsMetaData = new Vector();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;
		while (iterator.hasNext()) {
			columnMetaData = (ColumnMetaData) iterator.next();
			if (columnMetaData.isUpdatable()) {
				columnsMetaData.add(columnMetaData);
			}
		}
		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3B2B278201B7
	 */
	public Vector getKeyColumns() {

		Vector columnsMetaData = new Vector();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;

		while (iterator.hasNext()) {

			columnMetaData = (ColumnMetaData) iterator.next();

			if (columnMetaData.isKey())
				columnsMetaData.add(columnMetaData);
		}

		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3CAB4AEB0317
	 */
	public Vector getEditKeyColumns() {

		Vector columnsMetaData = new Vector();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;

		while (iterator.hasNext()) {

			columnMetaData = (ColumnMetaData) iterator.next();

			if (columnMetaData.isEditKey())
				columnsMetaData.add(columnMetaData);
		}

		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3B8546530320
	 */
	public Vector getCustomColumns() {

		Vector<ColumnMetaData> columnsMetaData = new Vector<ColumnMetaData>();
		Iterator<ColumnMetaData> iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;

		while (iterator.hasNext()) {

			columnMetaData = iterator.next();

			if (columnMetaData.getType() == ColumnDataType._CDT_LINK || columnMetaData.getType() == ColumnDataType._CDT_FILE)
				columnsMetaData.add(columnMetaData);
		}

		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3C898E9F03C1
	 */
	public Vector getComboColumns() {
		Vector columnsMetaData = new Vector();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;

		while (iterator.hasNext()) {

			columnMetaData = (ColumnMetaData) iterator.next();

			if (columnMetaData.isCombo())
				columnsMetaData.add(columnMetaData);
		}

		return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	public boolean hasFileColumn() {
		Iterator<ColumnMetaData> iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;
		while (iterator.hasNext()) {
			columnMetaData = iterator.next();
			if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C89117E0258
	 */
	public String toXML() {

		final String SPACE = " ";
		StringBuffer xml = new StringBuffer();
		int size = getColumnsMetaData() == null ? 0 : getColumnsMetaData().size();

		xml.append("<");
		xml.append("editor ");
		xml.append("name=" + "\"" + getName() + "\"" + SPACE);
		xml.append("desc=" + "\"" + getDesc() + "\"" + SPACE);
		xml.append("type=" + "\"" + getType() + "\"" + SPACE);
		xml.append("source=" + "\"" + getSource() + "\"" + SPACE);
		xml.append("procedure=" + "\"" + getProcedure() + "\"" + SPACE);
		xml.append("editable=" + "\"" + isEditable() + "\"" + SPACE);
		xml.append(">");
		xml.append(System.getProperty("line.separator"));

		for (int index = 1; index <= size; index++) {

			xml.append(getColumnMetaData(index).toXML());
			xml.append(System.getProperty("line.separator"));
		}

		xml.append("</editor>");
		xml.append(System.getProperty("line.separator"));

		return xml.toString();
	}

	/**
	 * @return Object
	 * @roseuid 3E52966B01EE
	 */
	public Object clone() {

		EditorMetaData newMetaData = new EditorMetaData(getName(), getDesc(), getType(), getSource(), getProcedure(), getType() != null);
		newMetaData.setSourceName(getSourceName());
		newMetaData.setSourceSql(getSourceSql());
		newMetaData.setColumnAlias(getColumnAlias());
		newMetaData.setInitialLookup(isInitialLookup());
		newMetaData.setInterceptor(getInterceptor());
		newMetaData.setClientInterceptorForEdit(getClientInterceptorForEdit());
		newMetaData.setClientInterceptorForList(getClientInterceptorForList());
		newMetaData.setSecurityManager(getSecurityManager());
		newMetaData.setSecurityManagerForEdit(getSecurityManagerForEdit());
		newMetaData.setFormType(getFormType());
		newMetaData.setListEdit(isListEdit());
		newMetaData.setPageSize(getPageSize());
		newMetaData.setViewRole(getViewRole());
		newMetaData.setEditRole(getEditRole());
		newMetaData.setAddNewPriv(isAddNewPriv());
		newMetaData.setBulkUploadPriv(isBulkUploadPriv());
		newMetaData.setBulkUploadImage(getBulkUploadImage());
		newMetaData.setMultiDeletePriv(isMultiDeletePriv());
		newMetaData.setCustomDisplayPriv(isCustomDisplayPriv());
		newMetaData.setExportPriv(isExportPriv());
		newMetaData.setExactMatchLookup(isExactMatchLookup());
		newMetaData.setHasHelp(hasHelp());
		newMetaData.setHasChild(hasChild());
		newMetaData.setChildActions(getChildActions());
		newMetaData.setDetailEditorName(getDetailEditorName());
		newMetaData.setCacheToRefresh(getCacheToRefresh());
		newMetaData.setSearchColsPerRow(getSearchColsPerRow());
		newMetaData.setUpdateColsPerRow(getUpdateColsPerRow());
		newMetaData.setEnvSpringBeanId(getEnvSpringBeanId());
		newMetaData.setScheduledExportPriv(isScheduledExportPriv());
		newMetaData.setFooterPriv(isFooterPriv());
		newMetaData.setCancelPriv(isCancelPriv());
		newMetaData.setShowHeader(isShowHeader());
		newMetaData.setShowPositions(isShowPositions());
		newMetaData.setAddlink(getAddlink());
		newMetaData.setAddlinkImage(getAddlinkImage());
		newMetaData.setDeletelinkImage(getDeletelinkImage());
		newMetaData.setCancellinkImage(getCancellinkImage());
		newMetaData.setQueryPriv(hasQueryPriv());
		newMetaData.setBulkUpdatePriv(hasBulkUpdatePriv());
		newMetaData.setQueryToolAdditionalFilter(isQueryToolAdditionalFilter());
		newMetaData.setUpdateSectionTitles(getUpdateSectionTitles());
		newMetaData.setMultiSorting(isMultiSorting());

		int size = _columnsMetaData != null ? _columnsMetaData.size() : 0;
		for (int index = 0; index < size; index++) {
			newMetaData.addColumnMetaData((ColumnMetaData) ((ColumnMetaData) _columnsMetaData.elementAt(index)).clone());
		}

		size = _editorFilters != null ? _editorFilters.size() : 0;
		for (int index = 0; index < size; index++) {
			newMetaData.addEditorFilter((EditorFilter) ((EditorFilter) _editorFilters.elementAt(index)).clone());
		}

		if (_userSearchFilters != null && _userSearchFilters.size() > 0) {
			List<UIComponentMetadata> newUserSearchFilters = new ArrayList<UIComponentMetadata>();
			UIComponentMetadata newComponentMetadata = null;
			for (UIComponentMetadata componentMetadata : _userSearchFilters) {
				newComponentMetadata = new UIComponentMetadata();
				newComponentMetadata.setKey(componentMetadata.getKey());
				newComponentMetadata.setPageName(componentMetadata.getPageName());
				newComponentMetadata.setUserName(componentMetadata.getUserName());
				newComponentMetadata.setComponentType(componentMetadata.getComponentType());
				newComponentMetadata.setComponentId(componentMetadata.getComponentId());
				newComponentMetadata.setJsonString(componentMetadata.getJsonString());
				newUserSearchFilters.add(newComponentMetadata);
			}
			newMetaData.setUserSearchFilters(newUserSearchFilters);
			if (_sharedSearchFilterNames != null) {
				List<String> sharedSearchFilterNames = new ArrayList<String>();
				sharedSearchFilterNames.addAll(_sharedSearchFilterNames);
				newMetaData.setSharedSearchFilterNames(sharedSearchFilterNames);
			}
		}

		if (_userTemplates != null && _userTemplates.size() > 0) {
			List<UIComponentMetadata> newUserTemplates = new ArrayList<UIComponentMetadata>();
			UIComponentMetadata newComponentMetadata = null;
			for (UIComponentMetadata componentMetadata : _userTemplates) {
				newComponentMetadata = new UIComponentMetadata();
				newComponentMetadata.setKey(componentMetadata.getKey());
				newComponentMetadata.setPageName(componentMetadata.getPageName());
				newComponentMetadata.setUserName(componentMetadata.getUserName());
				newComponentMetadata.setComponentType(componentMetadata.getComponentType());
				newComponentMetadata.setComponentId(componentMetadata.getComponentId());
				newComponentMetadata.setJsonString(componentMetadata.getJsonString());
				newUserTemplates.add(newComponentMetadata);
			}
			newMetaData.setUserTemplates(newUserTemplates);
			if (_sharedUserTemplateNames != null) {
				List<String> sharedUserTemplateNames = new ArrayList<String>();
				sharedUserTemplateNames.addAll(_sharedUserTemplateNames);
				newMetaData.setSharedUserTemplateNames(sharedUserTemplateNames);
			}
		}
		return newMetaData;
	}

	public String getDisplayables() {
		ArrayList columnNames = new ArrayList();
		String displayables = "";
		ColumnMetaData columnMetaData = null;
		if (getAggregatableColumns() != null) {
			for (Iterator iterator = getAggregatableColumns().iterator(); iterator.hasNext();) {
				columnMetaData = (ColumnMetaData) iterator.next();
				displayables += "," + columnMetaData.getName();
				columnNames.add(columnMetaData.getName());
			}
		}
		if (getDisplayableColumns() != null) {
			for (Iterator iterator = getDisplayableColumns().iterator(); iterator.hasNext();) {
				columnMetaData = (ColumnMetaData) iterator.next();
				if (!columnNames.contains(columnMetaData.getName())) {
					displayables += "," + columnMetaData.getName();
				}
			}
		}
		displayables = (displayables.length() > 0) ? displayables.substring(1) : null;
		return displayables;
	}

	public String getAggregatables() {
		Vector aggregatableColumns = getAggregatableColumns();
		ColumnMetaData columnMetaData = null;
		String aggregatables = "";
		if (aggregatableColumns != null) {
			for (Iterator iterator = aggregatableColumns.iterator(); iterator.hasNext();) {
				columnMetaData = (ColumnMetaData) iterator.next();
				aggregatables += "," + columnMetaData.getName();
			}
		}
		aggregatables = (aggregatables.length() > 0) ? aggregatables.substring(1) : null;
		return aggregatables;
	}

	public List getSearchableMandatoryColumns() {

		List searchableMandatoryColumns = new ArrayList();
		Iterator iterator = getColumnsMetaData().iterator();
		ColumnMetaData columnMetaData = null;
		int i = 0;
		while (iterator.hasNext()) {

			columnMetaData = (ColumnMetaData) iterator.next();

			if (columnMetaData.isSearchable() && columnMetaData.isSearchableMandatory()) {
				Pair colPair = new Pair(columnMetaData.getName(), columnMetaData.getCaption());
				searchableMandatoryColumns.add(i, colPair);
				i++;
			}

		}

		return searchableMandatoryColumns.size() > 0 ? searchableMandatoryColumns : null;
	}

	/**
	 * List of Conditional Mandatory Columns Groups
	 *
	 */
	public List<List<ColumnMetaData>> getConditionalMandatoryColumns() {
		List<List<ColumnMetaData>> condMandatoryColumns = new ArrayList<List<ColumnMetaData>>();
		Map<String, List<ColumnMetaData>> condMandatoryColumnsMap = new HashMap<String, List<ColumnMetaData>>();
		Vector<ColumnMetaData> columns = getColumnsMetaData();
		if(columns != null){
			String conditionGroupId = null;
			for (ColumnMetaData columnMetaData : columns) {
				conditionGroupId = columnMetaData.getSearchMandatoryGroup();
				if (StrUtl.isEmptyTrimmed(conditionGroupId) || "0".equals(conditionGroupId) || "1".equals(conditionGroupId)) {
					continue;
				}
				if(!condMandatoryColumnsMap.containsKey(conditionGroupId)){
					condMandatoryColumnsMap.put(conditionGroupId,new ArrayList<ColumnMetaData>() );
				}
				condMandatoryColumnsMap.get(conditionGroupId).add(columnMetaData);
			}

			for(String groupId : condMandatoryColumnsMap.keySet()){
				if( condMandatoryColumnsMap.get( groupId ).size() >= 2 ){
					condMandatoryColumns.add(condMandatoryColumnsMap.get( groupId ));
				}
			}
		}
		return condMandatoryColumns.size() > 0 ? condMandatoryColumns : null;
	}

	public String getClientInterceptorForList() {
		return _clientInterceptorForList;
	}

	public void setClientInterceptorForList(String interceptor) {
		this._clientInterceptorForList = interceptor;
	}

	public String getClientInterceptorForEdit() {
		return _clientInterceptorForEdit;
	}

	public void setClientInterceptorForEdit(String interceptor) {
		this._clientInterceptorForEdit = interceptor;
	}

	public String getUpdateSectionTitles() {
		return _updateSectionTitles;
	}

	public void setUpdateSectionTitles(String updateSectionTitles) {
		this._updateSectionTitles = updateSectionTitles;
	}

	public String getSecurityManager() {
		return _securityManager;
	}

	public void setSecurityManager(String manager) {
		this._securityManager = manager;
	}

	public String getSecurityManagerForEdit() {
		return _securityManagerForEdit;
	}

	public void setSecurityManagerForEdit(String manager) {
		this._securityManagerForEdit = manager;
	}

	public String getFormType() {
		return _formType;
	}

	public void setFormType(String formType) {
		this._formType = formType;
	}

	public boolean isListEdit() {
		return _listEdit;
	}

	public void setListEdit(boolean listEdit) {
		this._listEdit = listEdit;
	}

	public boolean isAddNewPriv() {
		return _addNewPriv;
	}

	public void setAddNewPriv(boolean addNewPriv) {
		this._addNewPriv = addNewPriv;
	}
	
	public boolean isBulkUploadPriv() {
		return _bulkUploadPriv;
	}

	public void setBulkUploadPriv(boolean bulkUploadPriv) {
		this._bulkUploadPriv = bulkUploadPriv;
	}

	public String getBulkUploadImage() {
		return _bulkUploadImage;
	}

	public void setBulkUploadImage(String bulkUploadImage) {
		this._bulkUploadImage = bulkUploadImage;
	}
	
	public boolean isMultiDeletePriv() {
		return _multiDeletePriv;
	}

	public void setMultiDeletePriv(boolean multiDeletePriv) {
		this._multiDeletePriv = multiDeletePriv;
	}

	public boolean isCustomDisplayPriv() {
		return _customDisplayPriv;
	}

	public void setCustomDisplayPriv(boolean customDisplayPriv) {
		this._customDisplayPriv = customDisplayPriv;
	}

	public boolean isExportPriv() {
		return _exportPriv;
	}

	public void setExportPriv(boolean exportPriv) {
		this._exportPriv = exportPriv;
	}

	public boolean isScheduledExportPriv() {
		return _scheduledExportPriv;
	}

	public void setScheduledExportPriv(boolean exportPriv) {
		this._scheduledExportPriv = exportPriv;
	}

	public boolean isCancelPriv() {
		return _cancelPriv;
	}

	public void setCancelPriv(boolean cancelPriv) {
		this._cancelPriv = cancelPriv;
	}

	public boolean isFooterPriv() {
		return _footerPriv;
	}

	public void setFooterPriv(boolean footerPriv) {
		this._footerPriv = footerPriv;
	}

	public boolean isShowHeader() {
		return _showHeader;
	}

	public void setShowHeader(boolean showHeader) {
		this._showHeader = showHeader;
	}

	public boolean isShowPositions() {
		return _showPositions;
	}

	public void setShowPositions(boolean showPositions) {
		this._showPositions = showPositions;
	}

	public boolean isExactMatchLookup() {
		return _exactMatchLkp;
	}

	public void setExactMatchLookup(boolean exactMatchLkp) {
		this._exactMatchLkp = exactMatchLkp;
	}

	public boolean hasHelp() {
		return _hasHelp;
	}

	public void setHasHelp(boolean hasHelp) {
		this._hasHelp = hasHelp;
	}

	public boolean hasChild() {
		return _hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this._hasChild = hasChild;
	}

	public int getPageSize() {
		return _pageSize;
	}

	public void setPageSize(int pageSize) {
		this._pageSize = pageSize;
	}

	public int getSearchColsPerRow() {
		return _searchColsPerRow;
	}

	public void setSearchColsPerRow(int colsPerRow) {
		this._searchColsPerRow = colsPerRow;
	}

	public String getUpdateColsPerRow() {
		return _updateColsPerRow;
	}

	public void setUpdateColsPerRow(String colsPerRow) {
		this._updateColsPerRow = colsPerRow;
	}

	public String getEnvSpringBeanId() {
		return _envSpringBeanId;
	}

	public void setEnvSpringBeanId(String envSpringBeanId) {
		this._envSpringBeanId = envSpringBeanId;
	}

	public String getViewRole() {
		return _viewRole;
	}

	public void setViewRole(String viewRole) {
		this._viewRole = viewRole;
	}

	public String getEditRole() {
		return _editRole;
	}

	public void setEditRole(String editRole) {
		this._editRole = editRole;
	}

	public String getChildActions() {
		return _childActions;
	}

	public void setChildActions(String childActions) {
		this._childActions = childActions;
	}

	public String getDetailEditorName() {
		return _detailEditorName;
	}

	public void setDetailEditorName(String detailEditorName) {
		this._detailEditorName = detailEditorName;
	}

	public String getCacheToRefresh() {
		return _cacheToRefresh;
	}

	public void setCacheToRefresh(String cacheToRefresh) {
		this._cacheToRefresh = cacheToRefresh;
	}

	public String getAddlinkImage() {
		return _addlinkImage;
	}

	public void setAddlinkImage(String anImage) {
		this._addlinkImage = anImage;
	}

	public String getAddlink() {
		return _addlink;
	}

	public void setAddlink(String addlink) {
		this._addlink = addlink;
	}

	public String getDeletelinkImage() {
		return _deletelinkImage;
	}

	public void setDeletelinkImage(String anImage) {
		this._deletelinkImage = anImage;
	}

	public String getCancellinkImage() {
		return _cancellinkImage;
	}

	public void setCancellinkImage(String anImage) {
		this._cancellinkImage = anImage;
	}

	public boolean hasQueryPriv() {
		return _queryPriv;
	}

	public void setQueryPriv(boolean queryPriv) {
		this._queryPriv = queryPriv;
	}

	public boolean hasBulkUpdatePriv() {
		return _bulkUpdatePriv;
	}

	public void setBulkUpdatePriv(boolean bulkUpdatePriv) {
		this._bulkUpdatePriv = bulkUpdatePriv;
	}

	public boolean isQueryToolAdditionalFilter() {
		return _queryToolAdditionalFilter;
	}

	public void setQueryToolAdditionalFilter(boolean queryToolAdditionalFilter) {
		this._queryToolAdditionalFilter = queryToolAdditionalFilter;
	}

	public void addEditorFilter(EditorFilter editorFilter) {
		getEditorFilters().add(editorFilter);
	}

	public Vector getEditorFilters() {
		if (_editorFilters == null) {
			_editorFilters = new Vector();
		}
		return _editorFilters;
	}

	public void setEditorFilters(Vector editorFilters) {
		this._editorFilters = editorFilters;
	}

	public List<UIComponentMetadata> getUserSearchFilters() {
		return _userSearchFilters;
	}

	public void setUserSearchFilters(List<UIComponentMetadata> userSearchFilters) {
		this._userSearchFilters = userSearchFilters;
	}

	public List<String> getUserSearchFilterNames() {
		List<String> filterNames = new ArrayList<String>();
		if (_userSearchFilters != null) {
			for (UIComponentMetadata uiCmd : _userSearchFilters) {
				filterNames.add(uiCmd.getComponentId());
			}
		}
		return filterNames.size() > 0 ? filterNames : null;
	}

	public List<String> getSharedSearchFilterNames() {
		return _sharedSearchFilterNames;
	}

	public void setSharedSearchFilterNames(List<String> sharedSearchFilterNames) {
		this._sharedSearchFilterNames = sharedSearchFilterNames;
	}

	public List<UIComponentMetadata> getUserTemplates() {
		return _userTemplates;
	}

	public void setUserTemplates(List<UIComponentMetadata> userTemplates) {
		this._userTemplates = userTemplates;
	}

	public List<String> getSharedUserTemplateNames() {
		return _sharedUserTemplateNames;
	}

	public void setSharedUserTemplateNames(List<String> sharedUserTemplateNames) {
		this._sharedUserTemplateNames = sharedUserTemplateNames;
	}

	public List<String> getUserTemplateNames() {
		List<String> templateNames = new ArrayList<String>();
		if (_userTemplates != null) {
			for (UIComponentMetadata uiCmd : _userTemplates) {
				templateNames.add(uiCmd.getComponentId());
			}
		}
		return templateNames.size() > 0 ? templateNames : null;
	}

	public boolean isSqlCached() {
		return isSqlCached;
	}

	public void setSqlCached(boolean isSqlCached) {
		this.isSqlCached = isSqlCached;
	}

	public HashMap<String, String> getColumnAlias() {
		return columnAlias;
	}

	public void setColumnAlias(HashMap<String, String> columnAlias) {
		this.columnAlias = columnAlias;
	}

	public boolean isMultiSorting() {
		return _multiSorting;
	}

	public void setMultiSorting(boolean multiSorting) {
		this._multiSorting = multiSorting;
	}

	public boolean hasBaseSearch() {
		Vector<ColumnMetaData> columns = getSearchableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isBaseSearch()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasAdvancedSearch() {
		Vector<ColumnMetaData> columns = getSearchableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isAdvancedSearch()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasAdvancedMultiRowSearch() {
		Vector<ColumnMetaData> columns = getSearchableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isAdvancedMultiRowSearch()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasAdvancedDisplay() {
		Vector<ColumnMetaData> columns = getDisplayableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isAdvancedDisplay()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasBaseDisplayLink() {
		Vector<ColumnMetaData> columns = getDisplayableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isBaseDisplayLink()) {
					return true;
				}
			}
		}
		return false;
	}

	public void sortString(List<String> values) {
		Collections.sort(values, new Comparator<String>() {
			public int compare(String arg1, String arg2) {
				if (arg1 == arg2) {
					return 0;
				}
				if (arg2 == null) {
					return 1;
				}
				if (arg1 == null) {
					return -1;
				}
				return arg1.toUpperCase().compareTo(arg2.toUpperCase());
			}
		});
	}
}
