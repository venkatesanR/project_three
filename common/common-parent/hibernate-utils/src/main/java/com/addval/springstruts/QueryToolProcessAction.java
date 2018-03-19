package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Enumeration;

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
import com.addval.metadata.UserCriteria;
import com.addval.utils.StrUtl;

public class QueryToolProcessAction extends BaseAction {

	private static transient final Logger _logger = Logger.getLogger(QueryToolProcessAction.class);

	private com.addval.ejbutils.server.UserCriteriaMgr userCriteriaMgr;
	private UserCriteriaUtil userCriteriaUtil;

	public com.addval.ejbutils.server.UserCriteriaMgr getUserCriteriaMgr() {
		return userCriteriaMgr;
	}

	public void setUserCriteriaMgr(com.addval.ejbutils.server.UserCriteriaMgr userCriteriaMgr) {
		this.userCriteriaMgr = userCriteriaMgr;
	}

	public UserCriteriaUtil getUserCriteriaUtil() {
		return userCriteriaUtil;
	}

	public void setUserCriteriaUtil(UserCriteriaUtil userCriteriaUtil) {
		this.userCriteriaUtil = userCriteriaUtil;
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		_logger.trace("execute.traceEnter");

		QueryToolForm queryToolForm = (QueryToolForm) form;
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		String kindOfAction = queryToolForm.getKindOfAction() != null ? queryToolForm.getKindOfAction() : "";
		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		HttpSession currSession = request.getSession(false);
		ActionForward forward = null;
		UserCriteria userCriteria = null;

		try {
			super.execute(mapping, form, request, response);
			reset(mapping, queryToolForm, request);

			String userCriteriaChartSessionKey = userName + "_" + queryToolForm.getEditorName() + "_USER_CRITERIA_CHART";

			try {
				errors = queryToolForm.validate(mapping, request);
				if (errors.size() > 0) {
					saveErrors(request, errors);
					return mapping.findForward(BaseAction._ERROR_FORWARD);
				}

				userCriteria = getUserCriteriaUtil().getUserCriteria(queryToolForm, request);
				if (userCriteria.getFilter() != null) {
					userCriteria.setFilter(userCriteria.getFilter().toUpperCase());
				}

				if(kindOfAction.equals("delete")){
					String owner = userCriteria.getOwner();
					if(StrUtl.isEmptyTrimmed( owner ) ){
						owner = request.getParameter( "USER_NAME_KEY" );
						if (!StrUtl.isEmptyTrimmed( owner ) && !userCriteria.getUserName().equalsIgnoreCase( owner )) {
							String criteriaName = request.getParameter("CRITERIA_NAME_KEY") != null ? request.getParameter("CRITERIA_NAME_KEY") : null;

							String message = ResourceUtils.message(request, "error.querytool.ownership", "Edit/delete action can only be performed by criteria owner. " + criteriaName + " criteria is owned by " + owner, criteriaName, owner);
							errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", message));

							kindOfAction = "edit";
							queryToolForm.setKindOfAction( kindOfAction );
						}
					}
				}


				if (kindOfAction.equals("insert") || kindOfAction.equals("update") || kindOfAction.equals("delete") ) {


					errors = getUserCriteriaUtil().validateUserCriteria(queryToolForm.getMetadata(), userCriteria, kindOfAction);
					if (errors.size() > 0) {
						saveErrors(request, errors);
						return mapping.findForward(BaseAction._ERROR_FORWARD);
					}


					if (kindOfAction.equals("insert")) {
						try {
							getUserCriteriaMgr().addNewUserCriteria(userCriteria);
							queryToolForm.setOwner(userCriteria.getUserName());

							String message = ResourceUtils.message(request, "info.querytool.newsuccess", "New User Criteria added Successfully.");
							messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
						}
						catch (Exception ex) {
							if (ex.getMessage().indexOf("ORA-00001:") != -1 && queryToolForm.getConfirmation() != null && queryToolForm.getConfirmation().equalsIgnoreCase("YES")) {
								kindOfAction = "update";
								UserCriteria userCriteriaDB = getUserCriteriaMgr().lookupUserCriteria(userCriteria);
								if(userCriteriaDB != null){
									userCriteria.setOwner(userCriteriaDB.getOwner());
									if (!userCriteria.getUserName().equalsIgnoreCase(userCriteria.getOwner())) {
										String message = ResourceUtils.message(request, "error.querytool.ownership", "Edit/delete action can only be performed by criteria owner. " + userCriteria.getCriteriaName() + " criteria is owned by " + userCriteria.getOwner(), userCriteria.getCriteriaName(), userCriteria.getOwner());
										errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", message));
										saveErrors(request, errors);
										return mapping.findForward(BaseAction._ERROR_FORWARD);
									}
								}
							}
							else {
								throw ex;
							}
						}
					}


					if (kindOfAction.equals("update")) {

						getUserCriteriaMgr().updateUserCriteria(userCriteria);
						queryToolForm.setConfirmation(null);
						String message = ResourceUtils.message(request, "info.querytool.updatesuccess", "User Criteria updated Successfully.");
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
					}

					if (kindOfAction.equals("delete")) {

						if(StrUtl.isEmptyTrimmed( userCriteria.getCriteriaName() )){
							userCriteria.setCriteriaName( request.getParameter("CRITERIA_NAME_KEY") );
						}

						getUserCriteriaMgr().deleteUserCriteria(userCriteria);


						userCriteria.setCriteriaName(null);
						userCriteria.setCriteriaDesc(null);
						currSession.setAttribute(userCriteriaChartSessionKey,new ArrayList());
						String message = ResourceUtils.message(request, "info.querytool.deletesuccess", "User Criteria deleted Successfully.");
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
					}

					if (queryToolForm.isLookup()) {
						queryToolForm.setKindOfAction("close");
					}
					else {
						if( (kindOfAction.equals("insert") || kindOfAction.equals("update")) && queryToolForm.isSaveAndRun() ){
							forward = mapping.findForward("report");
							String forwardUrl = getQueryString(forward.getPath().substring(1),request);
							forwardUrl +="&editorName=" + userCriteria.getEditorName();
							forwardUrl +="&EDITOR_NAME_KEY=" + userCriteria.getEditorName();
							forwardUrl +="&CRITERIA_NAME_KEY=" + userCriteria.getCriteriaName();
							System.out.println("forwardUrl="+forwardUrl);
							forward = new ActionForward(forward.getName(), forwardUrl ,true);
						}
						else {
							forward = mapping.findForward(BaseAction._CANCEL_FORWARD);
						}
					}

					queryToolForm.setReportName(userCriteria.getCriteriaName());
					queryToolForm.setReportDesc(userCriteria.getCriteriaDesc());

					currSession.setAttribute(userCriteriaChartSessionKey,new ArrayList());

				}
				else if (kindOfAction.equals("edit")) {
					String criteriaName = request.getParameter("CRITERIA_NAME_KEY") != null ? request.getParameter("CRITERIA_NAME_KEY") : queryToolForm.getReportName();
					if (!StrUtl.isEmptyTrimmed(criteriaName)) {
						userCriteria.setCriteriaName(criteriaName);
						UserCriteria userCriteriaDB = getUserCriteriaMgr().lookupUserCriteria(userCriteria);
						queryToolForm.setCriteria(userCriteriaDB);

						currSession.setAttribute(userCriteriaChartSessionKey, getUserCriteriaUtil().getUserCriteriaCharts(queryToolForm, userCriteriaDB.getUserCriteriaCharts() ));
					}
				}
				else if (kindOfAction.equals("select")) {
					String criteriaName = queryToolForm.getReportName();
					if (!StrUtl.isEmptyTrimmed(criteriaName)) {
						userCriteria.setCriteriaName(criteriaName);
						UserCriteria userCriteriaDB = getUserCriteriaMgr().lookupUserCriteria(userCriteria);
						queryToolForm.setCriteria(userCriteriaDB);
						currSession.setAttribute(userCriteriaChartSessionKey, getUserCriteriaUtil().getUserCriteriaCharts(queryToolForm, userCriteriaDB.getUserCriteriaCharts() ) );
					}
				}
				else if (kindOfAction.equals("cancel")) {
					forward = mapping.findForward(BaseAction._CANCEL_FORWARD);
				}

				if (forward != null) {
					return forward;
				}

				saveMessages(request, messages);
				saveErrors(request, errors);
			}
			finally {
				if(userCriteria == null){
					userCriteria = getUserCriteriaUtil().getUserCriteria(queryToolForm, request);
				}

				ArrayList userCriteriaNames = getUserCriteriaMgr().getUserCriteriaNames(userCriteria);
				getUserCriteriaUtil().initializeForm(request, queryToolForm, userCriteriaNames);

				if (kindOfAction.equals("addnew")) {
					currSession.setAttribute(userCriteriaChartSessionKey, new ArrayList());
				}
			}
		}
		catch (Exception ex) {
			queryToolForm.setKindOfAction(null);
			_logger.error(ex);
			if (ex == null) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			}
			else {
				String errMsg = ex.getMessage();
				if (errMsg.indexOf("SQLException: ORA-00001:") != -1) {
					queryToolForm.setConfirmation("QUESTION");
					String message = ResourceUtils.message(request, "info.querytool.overwritequestion", "Warning!  : Report Name already Exists. Please click YES to Overwrite it");
					messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", message));
				}
				else {
					if (errMsg.indexOf("Error:") != -1) {
						errMsg =errMsg.split("Error:")[1];
					}
					// translate to an internationalized string
					errMsg = translateErrorMessage(request, errMsg);
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", errMsg));
				}
			}
			saveErrors(request, errors);
			saveMessages(request, messages);
			return mapping.findForward(BaseAction._ERROR_FORWARD);
		}
		_logger.trace("execute.traceExit");
		return mapping.findForward(BaseAction._DONE_FORWARD);
	}

	private void reset(ActionMapping mapping, QueryToolForm queryToolForm, HttpServletRequest request) throws Exception {
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		queryToolForm.setUserId(userName);
		EditorMetaData editorMetaData = queryToolForm.getMetadata();

		if (editorMetaData == null) {
			String editorName = request.getParameter("EDITOR_NAME_lookUp") != null ? request.getParameter("EDITOR_NAME_lookUp") : queryToolForm.getEditorName();
			if (editorName == null) {
				editorName = request.getParameter("EDITOR_NAME_KEY") != null ? request.getParameter("EDITOR_NAME_KEY") : queryToolForm.getEditorName();
			}
			editorMetaData = lookupMetadata(editorName, queryToolForm.getEditorType(), userName, request.getSession());
			queryToolForm.initializeFromMetadata(request, mapping, editorMetaData);
		}
	}

	private String getQueryString(String url,HttpServletRequest request) {
		StringBuffer urlBuf = new StringBuffer();
		for ( Enumeration enum1 = request.getParameterNames( ); enum1.hasMoreElements( ); ) {
			String columnName = ( String ) enum1.nextElement( );

			if("EDITOR_NAME_KEY".equalsIgnoreCase( columnName ) || "CRITERIA_NAME_KEY".equalsIgnoreCase( columnName )){
				continue;
			}

			if (columnName.endsWith( "_PARENT_KEY" ) || columnName.endsWith( "_lookUp" ) || columnName.endsWith( "_KEY" ) || columnName.equals( "EDITOR_NAME" ) || columnName.equals( "EDITOR_TYPE" ) || columnName.equals( "POSITION" ) || columnName.equals("PAGING_ACTION") || columnName.equals("DETAILS") ) {
				String columnValue = request.getParameter( columnName );
				urlBuf.append("&").append(columnName).append("=").append( columnValue);
			}
		}

		return  urlBuf.length() > 0? (url.indexOf("?") != -1) ? (url + urlBuf.toString()): (url +"?" + urlBuf.toString().substring(1) ) : url;
	}


}
