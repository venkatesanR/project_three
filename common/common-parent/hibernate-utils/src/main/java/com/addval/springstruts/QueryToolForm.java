package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.regexp.RE;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UserCriteria;
import com.addval.utils.StrUtl;

public class QueryToolForm extends ActionForm {

	private static final long serialVersionUID = 676963331788348924L;

	public static final String SIMPLE_REPORT_TABNAME = "SIMPLE_REPORT";
	public static final String CUSTOMIZATION_TABNAME = "CUSTOMIZATION";

	public static final String ADVANCED_REPORT_TABNAME = "ADVANCED_REPORT";

	private EditorMetaData metadata = null;

	private String kindOfAction = null;
	private String editorName = null;
	private String editorType = "DEFAULT";
	private String reportType = "Simple";
	private String userId = null;
	private String owner = null;
	private boolean lookup = false;

	private String newReportName = null;
	private String reportName = null;
	private String reportDesc = null;
	private ArrayList reportNames = null;
	private ArrayList reportTypes = null;
	private boolean sharedAll;
	private boolean sharedGroup;
	private UserCriteria criteria = null;

	private String confirmation = null;
	public String currentTabName = null;

	private String[] customDisplayableColumns = null;
	private String[] sortOrderColumnsSeq = null;
	private String[] sortOrderColumns = null;

	private Vector<ColumnMetaData> displayableColumns = null;
	private Vector<ColumnMetaData> aggregatableColumns = null;

	private String selectedDisplayables[] = null;
	private String selectedSubTotalColumns[] = null;
	private String selectedAggregatables[] = null;
	private boolean showSubtotalDetail;

	private String filters = null;
	private boolean saveAndRun = false;

	public QueryToolForm() {

	}

	public void initializeFromMetadata(HttpServletRequest request, ActionMapping mapping, EditorMetaData metadata) {
		setMetadata(metadata);
		setEditorName(metadata.getName());
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String kindOfAction = getKindOfAction() != null ? getKindOfAction() : "";
		String value = null;
		RE matcher = null;
		if (kindOfAction.equals("insert")) {
			value = getNewReportName();
			matcher = new RE("^\\w([\\-\\s\\w]*)$");
			if (!matcher.match(value)) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Please enter valid Report Name"));
			}
			value = getReportDesc();
			if( !StrUtl.isEmptyTrimmed( value ) && value.length() > 200 ){
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Report Desc cannot exceeds the maximum length 200. Actual is " + value.length() ));
			}
		}
		return errors;
	}

	public String getDateFormat() {
		String dateFormat = "dd/MM/yy";
		// Date Format read from the EditorMetadata Columns which has CDT_DATE
		// type.
		Vector<ColumnMetaData> searchableColumns = getMetadata().getSearchableColumns();
		if (searchableColumns != null) {
			for (ColumnMetaData columnMetaData : searchableColumns) {
				if (columnMetaData.getType() == ColumnDataType._CDT_DATE) {
					if (!StrUtl.isEmptyTrimmed(columnMetaData.getFormat())) {
						dateFormat = columnMetaData.getFormat();
						break;
					}
				}
			}
		}
		return dateFormat;
	}

	public Vector getDisplayablesColumnNames() {
		Vector displayablesCols = new Vector();
		if (displayableColumns != null) {
			for (ColumnMetaData columnMetaData : displayableColumns) {
				displayablesCols.add(columnMetaData.getName());
			}
		}
		return displayablesCols;
	}

	public Vector getAggregatablesColumnNames() {
		Vector aggregatablesCols = new Vector();
		if (aggregatableColumns != null) {
			for (ColumnMetaData columnMetaData : aggregatableColumns) {
				aggregatablesCols.add(columnMetaData.getName());
			}
		}
		return aggregatablesCols;
	}

	public EditorMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(EditorMetaData metadata) {
		this.metadata = metadata;
	}

	public String getKindOfAction() {
		return kindOfAction;
	}

	public void setKindOfAction(String kindOfAction) {
		this.kindOfAction = kindOfAction;
	}

	public ArrayList getReportNames() {
		return reportNames;
	}

	public void setReportNames(ArrayList reportNames) {
		this.reportNames = reportNames;
	}
	

	public ArrayList getReportTypes() {
		return reportTypes;
	}

	public void setReportTypes(ArrayList reportTypes) {
		this.reportTypes = reportTypes;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isLookup() {
		return lookup;
	}

	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}

	public String getNewReportName() {
		return newReportName;
	}

	public void setNewReportName(String newReportName) {
		this.newReportName = newReportName;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportDesc() {
		return (reportDesc != null)? reportDesc.trim() : null;
	}

	public void setReportDesc(String reportDesc) {
		this.reportDesc = reportDesc;
	}

	public boolean isSharedAll() {
		return sharedAll;
	}

	public void setSharedAll(boolean sharedAll) {
		this.sharedAll = sharedAll;
	}

	public boolean isSharedGroup() {
		return sharedGroup;
	}

	public void setSharedGroup(boolean sharedGroup) {
		this.sharedGroup = sharedGroup;
	}

	public UserCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(UserCriteria criteria) {
		this.criteria = criteria;
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	public String getCurrentTabName() {
		return currentTabName;
	}

	public void setCurrentTabName(String currentTabName) {
		this.currentTabName = currentTabName;
	}

	public String[] getCustomDisplayableColumns() {
		return customDisplayableColumns;
	}

	public void setCustomDisplayableColumns(String[] customDisplayableColumns) {
		this.customDisplayableColumns = customDisplayableColumns;
	}

	public String[] getSortOrderColumnsSeq() {
		return sortOrderColumnsSeq;
	}

	public void setSortOrderColumnsSeq(String[] sortOrderColumnsSeq) {
		this.sortOrderColumnsSeq = sortOrderColumnsSeq;
	}

	public String[] getSortOrderColumns() {
		return sortOrderColumns;
	}

	public void setSortOrderColumns(String[] sortOrderColumns) {
		this.sortOrderColumns = sortOrderColumns;
	}

	public Vector<ColumnMetaData> getDisplayableColumns() {
		return displayableColumns;
	}

	public void setDisplayableColumns(Vector<ColumnMetaData> displayableColumns) {
		this.displayableColumns = displayableColumns;
	}

	public Vector<ColumnMetaData> getAggregatableColumns() {
		return aggregatableColumns;
	}

	public void setAggregatableColumns(Vector<ColumnMetaData> aggregatableColumns) {
		this.aggregatableColumns = aggregatableColumns;
	}

	public String[] getSelectedDisplayables() {
		return selectedDisplayables;
	}

	public void setSelectedDisplayables(String[] selectedDisplayables) {
		this.selectedDisplayables = selectedDisplayables;
	}

	public String[] getSelectedSubTotalColumns() {
		return selectedSubTotalColumns;
	}

	public void setSelectedSubTotalColumns(String[] selectedSubTotalColumns) {
		this.selectedSubTotalColumns = selectedSubTotalColumns;
	}

	public boolean isShowSubtotalDetail() {
		return showSubtotalDetail;
	}

	public void setShowSubtotalDetail(boolean showSubtotalDetail) {
		this.showSubtotalDetail = showSubtotalDetail;
	}

	public String[] getSelectedAggregatables() {
		return selectedAggregatables;
	}

	public void setSelectedAggregatables(String[] selectedAggregatables) {
		this.selectedAggregatables = selectedAggregatables;
	}

	public String getFilters() {
		return (filters != null) ? filters.trim() : filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}
	
	public boolean isSaveAndRun() {
		return saveAndRun;
	}

	public void setSaveAndRun(boolean saveAndRun) {
		this.saveAndRun = saveAndRun;
	}
	
	
	
}
