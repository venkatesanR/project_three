package com.addval.springstruts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UserCriteriaChart;
import com.addval.utils.StrUtl;

public class ChartMetadataAction extends BaseAction {
	private static transient final Logger _logger = Logger.getLogger(ChartMetadataAction.class);
	private UserCriteriaUtil userCriteriaUtil;

	public UserCriteriaUtil getUserCriteriaUtil() {
		return userCriteriaUtil;
	}

	public void setUserCriteriaUtil(UserCriteriaUtil userCriteriaUtil) {
		this.userCriteriaUtil = userCriteriaUtil;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		_logger.trace("execute.traceEnter");
		ChartMetadataForm cmdForm = (ChartMetadataForm) form;
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		String kindOfAction = cmdForm.getKindOfAction() != null ? cmdForm.getKindOfAction() : "";
		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		HttpSession currSession = request.getSession(false);
		UserCriteriaChart chart = null;

		try {
			super.execute(mapping, form, request, response);
			reset(mapping, cmdForm, request);
			try {
				errors = cmdForm.validate(mapping, request);
				if (errors.size() > 0) {
					saveErrors(request, errors);
					return mapping.findForward(BaseAction._ERROR_FORWARD);
				}

				String userCriteriaChartSessionKey = userName + "_" + cmdForm.getEditorName() + "_USER_CRITERIA_CHART";

				ArrayList ucCharts = (ArrayList) currSession.getAttribute(userCriteriaChartSessionKey);
				if (ucCharts == null) {
					ucCharts = new ArrayList();
					currSession.setAttribute(userCriteriaChartSessionKey, ucCharts);
				}
				
				cmdForm.setUserCriteriaCharts(ucCharts);

				if (kindOfAction.equals("addNewChart")) {
					cmdForm.setChartIndex(ucCharts.size());
				}
				else if (kindOfAction.equals("editChart")) {
					if (ucCharts.size() > 0) {
						chart = (UserCriteriaChart) ucCharts.get(cmdForm.getChartIndex());
						getUserCriteriaUtil().fromUserCriteriaChart(cmdForm, chart);
					}
				}
				else if (kindOfAction.equals("deleteChart")) {
					if (ucCharts.size() > 0) {
						ucCharts.remove(cmdForm.getChartIndex());
						String message = ResourceUtils.message(request, "info.chartmetadata.tempdeleted", "Configure "+ cmdForm.getChartType() + " is temporarily deleted. Please save the report to delete permanently", cmdForm.getChartType());						
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
						cmdForm.setChartIndex(-1);
					}
				}
				else if (kindOfAction.equals("saveChart")) {
					chart = getUserCriteriaUtil().toUserCriteriaChart(cmdForm, request);
					errors = getUserCriteriaUtil().validateUserCriteriaChart(cmdForm.getMetadata(), chart);
					if (errors.size() > 0) {
						saveErrors(request, errors);
						return mapping.findForward(BaseAction._ERROR_FORWARD);
					}

					if (cmdForm.getChartIndex() == ucCharts.size()) {
						ucCharts.add(chart);
						String message = ResourceUtils.message(request, "info.chartmetadata.tempadded", "Configure "+ cmdForm.getChartType() + " is temporarily added. Please save the report to store permanently", cmdForm.getChartType());												
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
					}
					else {
						ucCharts.set(cmdForm.getChartIndex(), chart);
						String message = ResourceUtils.message(request, "info.chartmetadata.tempupdated", "Configure "+ cmdForm.getChartType() + " is temporarily updated. Please save the report to store permanently", cmdForm.getChartType());												
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
					}
					cmdForm.setChartIndex(-1);
					cmdForm.setChartType(null);
				}
				else if (kindOfAction.equals("cancelChart")) {
					cmdForm.setChartType(null);
					cmdForm.setChartIndex(-1);
				}
				saveMessages(request, messages);
			}
			finally {
				getUserCriteriaUtil().initializeForm(request, cmdForm);
			}
		}
		catch (Exception ex) {
			cmdForm.setKindOfAction(null);
			_logger.error(ex);

			if (ex != null) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ex.getMessage()));
			}
			else {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			}
			saveErrors(request, errors);
			saveMessages(request, messages);
			return mapping.findForward(BaseAction._ERROR_FORWARD);
		}
		_logger.trace("execute.traceExit");
		return mapping.findForward(BaseAction._DONE_FORWARD);
	}

	private void reset(ActionMapping mapping, ChartMetadataForm cmdForm, HttpServletRequest request) throws Exception {
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		EditorMetaData editorMetaData = cmdForm.getMetadata();

		if (editorMetaData == null) {
			String editorName = cmdForm.getEditorName();
			editorMetaData = lookupMetadata(editorName, "DEFAULT", userName, request.getSession());
			cmdForm.initializeFromMetadata(request, mapping, editorMetaData);
		}
		String columnName = null;
		String columnValue = null;

		Map<String,String> selectedAggregators = new HashMap<String,String>();
		Map<String,String> selectedSeriesTypes = new HashMap<String,String>();
		Map<String,String> selectedSeriesDetail = new HashMap<String,String>();
		Map<String,String> selectedLinearAxis = new HashMap<String,String>();

		Map<String,String> selectedThresholds= new HashMap<String,String>();
		Map<String,String> selectedColors= new HashMap<String,String>();

		String[] measures = cmdForm.getMeasures();
		if (measures != null) {
			for (int i = 0; i < measures.length; i++) {
				columnName = measures[i];
				columnValue = request.getParameter(columnName + "_AGG");
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					selectedAggregators.put(columnName, columnValue);
				}
				columnValue = request.getParameter(columnName + "_Series");
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					selectedSeriesTypes.put(columnName, columnValue);
				}

				columnValue = request.getParameter(columnName + "_Detail");
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					selectedSeriesDetail.put(columnName, columnValue);
				}

				columnValue = request.getParameter(columnName + "_LinearAxis");
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					selectedLinearAxis.put(columnName, columnValue);
				}
				
				columnValue = request.getParameter(columnName + "_Thresholds");
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					selectedThresholds.put(columnName, columnValue);
				}

				columnValue = request.getParameter(columnName + "_Colors");
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					selectedColors.put(columnName, columnValue);
				}

			}
		}
		cmdForm.setSelectedAggregators(selectedAggregators);
		cmdForm.setSelectedSeriesTypes(selectedSeriesTypes);
		cmdForm.setSelectedSeriesDetail(selectedSeriesDetail);
		cmdForm.setSelectedLinearAxis(selectedLinearAxis);
		cmdForm.setSelectedThresholds(selectedThresholds);
		cmdForm.setSelectedColors(selectedColors);
		
	}
}
