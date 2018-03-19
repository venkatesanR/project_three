package com.addval.springstruts;

import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ModuleConfig;
import org.springframework.beans.factory.annotation.Configurable;

import com.addval.jasperutils.EJBResultSetJasperDataSource;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;

@Configurable("exportForm")
public class ExportForm extends ActionForm {
	private String subSystem = null;
	private String editorName = null;
	private String editorType = null;
	private String kindOfAction = null;

	private String reportTitle = null;
	private String exportType = null;
	private String pageOrientation = null;
	private Map parameters = null;

	private boolean exactSearch = true;
	private String formInterceptorType = null;
	private EditorMetaData metadata = null;
	private JasperReport jasperReport = null;
	private EJBResultSetJasperDataSource jasperDataSource = null;

	private boolean userCriteria = false;
	private boolean additionalFilter = false;
	private Vector<SearchColumn> _advancedSearchColumns = null;

	public ExportForm() {
		// Input
		this.setSubSystem(null);
		this.setEditorName(null);
		this.setEditorType(null);
		this.setKindOfAction(null);
		this.setFormInterceptorType(null);
		this.setMetadata(null);

		// Request
		this.setReportTitle(null);
		this.setExportType(null);
		this.setPageOrientation(null);

		// Response
		this.setJasperReport(null);
		this.setJasperDataSource(null);
		this.setParameters(null);

		this.setUserCriteria(false);
		_advancedSearchColumns = new Vector<SearchColumn>();
	}

	public String getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(String subSystem) {
		this.subSystem = subSystem;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public String getEditorType() {
		if (editorType == null || editorType.trim().length() == 0)
			editorType = "DEFAULT";
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public String getKindOfAction() {
		return kindOfAction;
	}

	public void setKindOfAction(String kindOfAction) {
		this.kindOfAction = kindOfAction;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getPageOrientation() {
		return pageOrientation;
	}

	public void setPageOrientation(String pageOrientation) {
		this.pageOrientation = pageOrientation;
	}

	public boolean isExactSearch() {
		return exactSearch;
	}

	public void setExactSearch(boolean exactSearch) {
		this.exactSearch = exactSearch;
	}

	public String getFormInterceptorType() {
		return formInterceptorType;
	}

	public void setFormInterceptorType(String formInterceptorType) {
		this.formInterceptorType = formInterceptorType;
	}

	public JasperReport getJasperReport() {
		return jasperReport;
	}

	public void setJasperReport(JasperReport jasperReport) {
		this.jasperReport = jasperReport;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public EJBResultSetJasperDataSource getJasperDataSource() {
		return jasperDataSource;
	}

	public void setJasperDataSource(EJBResultSetJasperDataSource jasperDataSource) {
		this.jasperDataSource = jasperDataSource;
	}

	public EditorMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(EditorMetaData metadata) {
		this.metadata = metadata;
	}

	public boolean isUserCriteria() {
		return userCriteria;
	}

	public void setUserCriteria(boolean userCriteria) {
		this.userCriteria = userCriteria;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		ModuleConfig appConfig = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
		BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());
		if (!StrUtl.isEmptyTrimmed(formConfig.getFormInterceptorType())) {
			setFormInterceptorType(formConfig.getFormInterceptorType());
		}
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if (StrUtl.isEmptyTrimmed(this.getEditorName())) {
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", "Editor Name"));
		}

		if (this.getKindOfAction().equalsIgnoreCase("export")) {
			if (StrUtl.isEmptyTrimmed(this.getReportTitle())) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", "Report Title"));
			}
		}

		return errors;
	}

	public boolean isAdditionalFilter() {
		return additionalFilter;
	}

	public void setAdditionalFilter(boolean additionalFilter) {
		this.additionalFilter = additionalFilter;
	}

	public Vector<SearchColumn> getSearchColumnList() {
		return _advancedSearchColumns;
	}

	public Vector<SearchColumn> getSearchColumns() {
		return _advancedSearchColumns;
	}

	public void setSearchColumns(Vector<SearchColumn> advancedSearchColumns) {
		this._advancedSearchColumns = advancedSearchColumns;
	}

	public void setSearchColumns(int i, SearchColumn searchColumn) {
		if (i >= _advancedSearchColumns.size()) {
			int oldsize = _advancedSearchColumns.size();
			for (int idx = oldsize; idx <= i; ++idx) {
				_advancedSearchColumns.add(new SearchColumn());
			}
			_advancedSearchColumns.setElementAt(searchColumn, i);
		}
		else {
			_advancedSearchColumns.setElementAt(searchColumn, i);
		}
	}

	public SearchColumn getSearchColumns(int i) {
		if (i < _advancedSearchColumns.size()) {
			if (_advancedSearchColumns.elementAt(i) == null) {
				setSearchColumns(i, new SearchColumn());
			}
			return (SearchColumn) _advancedSearchColumns.elementAt(i);
		}
		else {
			setSearchColumns(i, new SearchColumn());
			return (SearchColumn) _advancedSearchColumns.elementAt(i);
		}
	}

}
