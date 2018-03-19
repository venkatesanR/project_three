//Source file: D:\\projects\\COMMON\\src\\com\\addval\\metadata\\UserCriteria.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.metadata;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class UserCriteria implements Serializable {

	private int userCriteriaKey = 0;
	private String _editorName = null;
	private String _editorType = null;
	private String _userName = null;
	private String _criteriaName = null;
	private String _criteriaDesc = null;
	private String _chartType = null;
	private String _chartJs = null;
	private String _dimension = null;
	private String _measure = null;
	private String _filter = null;
	private String _sortName = null;
	private String _sortOrder = null;
	private boolean _sharedAll;
	private boolean _sharedGroup;

	private String _owner = null;
	private String _customDisplayable = null;
	private String _columnSortOrderSeq = null;
	private String _columnSortOrder = null;
	private String _subTotalColumns = null;
	private boolean _showSubtotalDetail = true;
	private List _userGroups = null;
	private ArrayList _userCriteriaCharts = null;

	private String reportType = null;
	//additionalFilter is used while schedule export 
	private String additionalFilter = null;


	/**
	 * @param editorName
	 * @param editorType
	 * @param userName
	 * @param criteriaName
	 * @param dimension
	 * @param measure
	 * @param filter
	 * @roseuid 3FC8F81F001B
	 */
	public UserCriteria() {

	}

	public UserCriteria(String editorName, String editorType, String userName, String criteriaName, String criteriaDesc, String dimension, String measure, String filter, boolean sharedAll, boolean sharedGroup) {
		this(editorName, editorType, userName, criteriaName, criteriaDesc, dimension, measure, filter, sharedAll, sharedGroup, null, null, null, null, null, true);
	}

	public UserCriteria(String editorName, String editorType, String userName, String criteriaName, String criteriaDesc, String dimension, String measure, String filter, boolean sharedAll, boolean sharedGroup, String owner, String customDisplayable, String columnSortOrderSeq,
			String columnSortOrder, String subTotalColumns, boolean showSubtotalDetail) {
		_editorName = editorName;
		_editorType = editorType;
		_userName = userName;
		_criteriaName = criteriaName;
		_criteriaDesc = criteriaDesc;
		_dimension = dimension;
		_measure = measure;
		_filter = filter;
		_sharedAll = sharedAll;
		_sharedGroup = sharedGroup;
		_owner = owner;
		_customDisplayable = customDisplayable;
		_columnSortOrderSeq = columnSortOrderSeq;
		_columnSortOrder = columnSortOrder;
		_subTotalColumns = subTotalColumns;
		_showSubtotalDetail = showSubtotalDetail;
	}

	/**
	 * Access method for the _editorName property.
	 *
	 * @return the current value of the _editorName property
	 * @roseuid 3FC8F81F0075
	 */
	public String getEditorName() {
		return _editorName;
	}

	/**
	 * Sets the value of the _editorName property.
	 *
	 * @param aEditorName
	 *            the new value of the _editorName property
	 * @roseuid 3FC8F81F008A
	 */
	public void setEditorName(String aEditorName) {
		_editorName = aEditorName;
	}

	/**
	 * Access method for the _editorType property.
	 *
	 * @return the current value of the _editorType property
	 * @roseuid 3FC8F81F00A8
	 */
	public String getEditorType() {
		return _editorType;
	}

	/**
	 * Sets the value of the _editorType property.
	 *
	 * @param aEditorType
	 *            the new value of the _editorType property
	 * @roseuid 3FC8F81F00BC
	 */
	public void setEditorType(String aEditorType) {
		_editorType = aEditorType;
	}

	/**
	 * Access method for the _userName property.
	 *
	 * @return the current value of the _userName property
	 * @roseuid 3FC8F81F00DA
	 */
	public String getUserName() {
		return _userName;
	}

	/**
	 * Sets the value of the _userName property.
	 *
	 * @param aUserName
	 *            the new value of the _userName property
	 * @roseuid 3FC8F81F00F8
	 */
	public void setUserName(String aUserName) {
		_userName = aUserName;
	}

	/**
	 * Access method for the _criteriaName property.
	 *
	 * @return the current value of the _criteriaName property
	 * @roseuid 3FC8F81F0116
	 */
	public String getCriteriaName() {
		return _criteriaName;
	}

	/**
	 * Sets the value of the _criteriaName property.
	 *
	 * @param aCriteriaName
	 *            the new value of the _criteriaName property
	 * @roseuid 3FC8F81F012A
	 */
	public void setCriteriaName(String aCriteriaName) {
		_criteriaName = aCriteriaName;
	}

	/**
	 * Access method for the _dimension property.
	 *
	 * @return the current value of the _dimension property
	 * @roseuid 3FC8F81F0148
	 */
	public String getDimension() {
		return _dimension;
	}

	/**
	 * Sets the value of the _dimension property.
	 *
	 * @param aDimension
	 *            the new value of the _dimension property
	 * @roseuid 3FC8F81F015C
	 */
	public void setDimension(String aDimension) {
		_dimension = aDimension;
	}

	/**
	 * Access method for the _measure property.
	 *
	 * @return the current value of the _measure property
	 * @roseuid 3FC8F81F0184
	 */
	public String getMeasure() {
		return _measure;
	}

	/**
	 * Sets the value of the _measure property.
	 *
	 * @param aMeasure
	 *            the new value of the _measure property
	 * @roseuid 3FC8F81F0198
	 */
	public void setMeasure(String aMeasure) {
		_measure = aMeasure;
	}

	/**
	 * Access method for the _filter property.
	 *
	 * @return the current value of the _filter property
	 * @roseuid 3FC8F81F01B6
	 */
	public String getFilter() {
		return _filter;
	}

	/**
	 * Sets the value of the _filter property.
	 *
	 * @param aFilter
	 *            the new value of the _filter property
	 * @roseuid 3FC8F81F01D4
	 */
	public void setFilter(String aFilter) {
		_filter = aFilter;
	}

	public String getCriteriaDesc() {
		return _criteriaDesc;
	}

	public void setCriteriaDesc(String aCriteriaDesc) {
		_criteriaDesc = aCriteriaDesc;
	}

	public String getSortName() {
		return _sortName;
	}

	public void setSortName(String sortName) {
		this._sortName = sortName;
	}

	public String getSortOrder() {
		return _sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this._sortOrder = sortOrder;
	}

	public boolean getSharedAll() {
		return _sharedAll;
	}

	public void setSharedAll(boolean aSharedAll) {
		this._sharedAll = aSharedAll;
	}

	public boolean getSharedGroup() {
		return _sharedGroup;
	}

	public void setSharedGroup(boolean aSharedGroup) {
		this._sharedGroup = aSharedGroup;
	}

	public List getUserGroups() {
		if(_userGroups == null){
			_userGroups = new ArrayList();
		}
		return _userGroups;
	}

    public String getFirstUserGroup() {
        if (_userGroups != null && _userGroups.size() > 0) {
            return (String) _userGroups.get(0);
        }
        return null;
    }


	public void setUserGroups(List aUserGroups) {
		_userGroups = aUserGroups;
	}

	public String getOwner() {
		return _owner;
	}

	public void setOwner(String owner) {
		this._owner = owner;
	}

	public String getCustomDisplayable() {
		return _customDisplayable;
	}

	public void setCustomDisplayable(String customDisplayable) {
		this._customDisplayable = customDisplayable;
	}

	public String getColumnSortOrder() {
		return _columnSortOrder;
	}

	public void setColumnSortOrder(String columnSortOrder) {
		this._columnSortOrder = columnSortOrder;
	}

	public String getColumnSortOrderSeq() {
		return _columnSortOrderSeq;
	}

	public void setColumnSortOrderSeq(String columnSortOrderSeq) {
		this._columnSortOrderSeq = columnSortOrderSeq;
	}

	public String getSubTotalColumns() {
		return _subTotalColumns;
	}

	public void setSubTotalColumns(String subTotalColumns) {
		this._subTotalColumns = subTotalColumns;
	}

	public boolean isShowSubtotalDetail() {
		return _showSubtotalDetail;
	}

	public void setShowSubtotalDetail(boolean showSubtotalDetail) {
		this._showSubtotalDetail = showSubtotalDetail;
	}

	public String getChartType() {
		return _chartType;
	}

	public void setChartType(String type) {
		_chartType = type;
	}

	public String getChartJs() {
		return _chartJs;
	}

	public void setChartJs(String js) {
		_chartJs = js;
	}

	public int getUserCriteriaKey() {
		return userCriteriaKey;
	}

	public void setUserCriteriaKey(int userCriteriaKey) {
		this.userCriteriaKey = userCriteriaKey;
	}

	public ArrayList getUserCriteriaCharts() {
		if (_userCriteriaCharts == null) {
			_userCriteriaCharts = new ArrayList();
		}
		return _userCriteriaCharts;
	}

	public void setUserCriteriaCharts(ArrayList userCriteriaCharts) {
		this._userCriteriaCharts = userCriteriaCharts;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getAdditionalFilter() {
		return additionalFilter;
	}

	public void setAdditionalFilter(String additionalFilter) {
		System.out.println("additionalFilter" + additionalFilter);
		this.additionalFilter = additionalFilter;
	}

}
