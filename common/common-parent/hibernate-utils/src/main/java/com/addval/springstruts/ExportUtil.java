package com.addval.springstruts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.jasperutils.EJBResultSetJasperDataSource;
import com.addval.jasperutils.EditorMetaDataReportDesigner;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UserCriteria;
import com.addval.utils.AVConstants;
import com.addval.utils.Pair;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

public class ExportUtil {

	// The export feature is restricted to 50,000 rows. If more rows are needed
	// to be exported use the schedule export feature
	private int _EXPORT_MAX_RECORDS = 50000;
	private EJBSTableManager _tableManager = null;
	private EJBSEditorMetaData _editorMetadataServer = null;

	private String _designFileName = null;

	private com.addval.ejbutils.server.UserCriteriaMgr userCriteriaMgr;
	private UserCriteriaUtil userCriteriaUtil;

	public UserCriteriaUtil getUserCriteriaUtil() {
		return userCriteriaUtil;
	}

	public void setMaxRecords(int maxRecords) {
		_EXPORT_MAX_RECORDS = maxRecords;
	}

	public int getMaxRecords() {
		return _EXPORT_MAX_RECORDS;
	}

	public void setUserCriteriaUtil(UserCriteriaUtil userCriteriaUtil) {
		this.userCriteriaUtil = userCriteriaUtil;
	}

	public com.addval.ejbutils.server.UserCriteriaMgr getUserCriteriaMgr() {
		return userCriteriaMgr;
	}

	public void setUserCriteriaMgr(com.addval.ejbutils.server.UserCriteriaMgr userCriteriaMgr) {
		this.userCriteriaMgr = userCriteriaMgr;
	}

	public String getDesignFileName() {
		return _designFileName;
	}

	public void setDesignFileName(String designFileName) {
		_designFileName = designFileName;
	}

	public ExportUtil() {
	}

	/*
	 * get method for TableManager
	 */
	public EJBSTableManager getTableManager() {
		return _tableManager;
	}

	/*
	 * set method for TableManager
	 */
	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}

	public void setEditorMetadataServer(EJBSEditorMetaData editorMetadataServer) {
		_editorMetadataServer = editorMetadataServer;
	}

	public EJBSEditorMetaData getEditorMetadataServer() {
		return _editorMetadataServer;
	}

	public void setDefaultValues(ExportForm form, HttpServletRequest request) {
		form.setReportTitle(form.getMetadata().getDesc());
		form.setExportType(String.valueOf(EditorMetaDataReportDesigner.CSV_FORMAT));
		form.setPageOrientation("PORTRAIT");
	}

	public ActionErrors export(ActionMapping mapping, ExportForm form, HttpServletRequest request, HttpServletResponse response) throws XRuntime, Exception {
		HttpSession currSession = request.getSession(false);
		ActionErrors errors = new ActionErrors();
		EJBCriteria ejbCriteria = null;
		FormInterceptor formInterceptor = null;
		EJBResultSet ejbResultSet = null;
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";

		request.setAttribute("exactSearch", new Boolean(form.isExactSearch()));
		if (form.getSearchColumns() != null) {
			String columnValue = null;
			for (SearchColumn searchColumn : form.getSearchColumns()) {
				columnValue = searchColumn.getColumnValue();
				if (StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = searchColumn.getColumnOption(); // dropdown value.
				}
				if (!StrUtl.isEmptyTrimmed(searchColumn.getColumnName()) && !StrUtl.isEmptyTrimmed(columnValue)) {
					request.setAttribute(searchColumn.getColumnName() + "_lookUp", columnValue);
					request.setAttribute(searchColumn.getColumnName() + "operator_lookUp", searchColumn.getColumnOperator());
				}
			}
		}
		ejbCriteria = EjbUtils.getEJBCriteria(form.getMetadata(), request, true);
		ejbCriteria.setPageSize(getMaxRecords());
		ejbCriteria.setPageAction(AVConstants._FETCH_FIRST);
		if (!StrUtl.isEmptyTrimmed(form.getFormInterceptorType())) {
			formInterceptor = FormInterceptorFactory.getInstance(form.getFormInterceptorType());
		}

		if (formInterceptor != null) {
			formInterceptor.beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, ejbCriteria);
		}
		if (form.isUserCriteria()) {
			UserCriteria userCriteria = (UserCriteria) currSession.getAttribute(userName + "_" + form.getMetadata().getName() + "_USER_CRITERIA");
			String criteriaName = request.getParameter("criteriaName") != null ? request.getParameter("criteriaName") : "";
			if (userCriteria == null && !StrUtl.isEmptyTrimmed(criteriaName)) {
				UserCriteria criteria = new UserCriteria();
				criteria.setEditorName(form.getEditorName());
				criteria.setEditorType(form.getEditorType());
				criteria.setUserName(userName);
				criteria.setCriteriaName(criteriaName);
				userCriteria = getUserCriteriaMgr().lookupUserCriteria(criteria);
			}

			if (userCriteria != null) {
				Pair pair = getUserCriteriaUtil().getEditorMetaDataSQLPair(form.getMetadata(), userCriteria, request, form.isAdditionalFilter());
				form.setMetadata((EditorMetaData) pair.getFirst());
				EJBCriteria ejbCrit = (new EJBResultSetMetaData(form.getMetadata())).getNewCriteria();
				ejbCrit.setPageSize(getMaxRecords());
				ejbCrit.setPageAction(AVConstants._FETCH_FIRST);
				ejbResultSet = getTableManager().lookup((String) pair.getSecond(), form.getMetadata(), ejbCrit);
			}
			else {
				ejbResultSet = getTableManager().lookup(ejbCriteria);
			}
		}
		else {
			ejbResultSet = getTableManager().lookup(ejbCriteria);
		}

		if (ejbResultSet == null || ejbResultSet.getRecords().size() == 0) {
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "No Records found!"));
			return errors;
		}
		boolean forLandscape = form.getPageOrientation() != null ? form.getPageOrientation().equalsIgnoreCase("LANDSCAPE") : true;
		EditorMetaDataReportDesigner designer = null;

		if (StrUtl.isEmpty(getDesignFileName())) {
			designer = new EditorMetaDataReportDesigner(form.getMetadata(), forLandscape, Integer.parseInt(form.getExportType()));
		}
		else {
			designer = new EditorMetaDataReportDesigner(form.getMetadata(), forLandscape, Integer.parseInt(form.getExportType()), getDesignFileName());
		}

		form.setJasperReport(designer.getJasperReport());
		form.setJasperDataSource(new EJBResultSetJasperDataSource(ejbResultSet));

		if (formInterceptor != null) {
			formInterceptor.afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, ejbCriteria);
		}

		// URL imageUrl = new URL("http://cuckoo:9001/cargores/images/customer_logo.gif");
		// DateTime as Server time instead of GMT time.
		Calendar calendar = Calendar.getInstance();
		// calendar.setTimeZone(AVConstants._GMT_TIMEZONE);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy | HH:mm");
		// sdf.setTimeZone(AVConstants._GMT_TIMEZONE);
		String dateTime = sdf.format(calendar.getTime());

		Map parameters = new HashMap();
		parameters.put("ReportTitle", form.getReportTitle());
		parameters.put("User", userName);
		parameters.put("DateTime", dateTime);
		

		form.setParameters(parameters);

		return errors;
	}

}
