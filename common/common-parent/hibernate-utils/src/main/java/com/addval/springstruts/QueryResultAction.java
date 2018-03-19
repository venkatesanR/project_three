
/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.springstruts;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.addval.metadata.UserCriteria;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

public class QueryResultAction extends BaseAction {
	private static transient final Logger _logger = Logger.getLogger(QueryResultAction.class);
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
		UserCriteriaSearchForm ucRForm = (UserCriteriaSearchForm) form;
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		
		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		HttpSession currSession = request.getSession(false);
		UserCriteria userCriteria = null;
		
		try {
			super.execute(mapping, form, request, response);
			reset(mapping, ucRForm, request);
			
			String kindOfAction = ucRForm.getKindOfAction() != null ? ucRForm.getKindOfAction() : "";
			
			try {
				if (kindOfAction == null || 
						"paging".equalsIgnoreCase(kindOfAction) || 
						"preview".equalsIgnoreCase(kindOfAction)||
						"ApplyTemplate".equalsIgnoreCase(kindOfAction)||
						"SaveFilter".equalsIgnoreCase(kindOfAction)||
						"ApplyFilter".equalsIgnoreCase(kindOfAction)||
						"DeleteFilter".equalsIgnoreCase(kindOfAction)||
						"update".equalsIgnoreCase(kindOfAction)				
						) {
					kindOfAction = ListProcessAction._SEARCH_FORWARD;
				}
				
				if (kindOfAction.equals("search") || kindOfAction.equals("report") || kindOfAction.equals("paging")) {

					userCriteria = new UserCriteria();
					userCriteria.setUserName(userName);
					userCriteria.setEditorName(ucRForm.getMetadata().getName());
					userCriteria.setEditorType(ucRForm.getMetadata().getType());
					
					getUserCriteriaUtil().setUserGroups(userCriteria,request);

					if(!StrUtl.isEmptyTrimmed( ucRForm.getCriteriaName() )){
						userCriteria.setCriteriaName( ucRForm.getCriteriaName() );
						userCriteria = getUserCriteriaMgr().lookupUserCriteria(userCriteria);
						ucRForm.setCriteria(userCriteria);
					}
					ucRForm.setKindOfAction("search");
					
					if( kindOfAction.equals("report") && ucRForm.getCriteria() != null && ucRForm.getCriteria().getUserCriteriaCharts().size() > 0){
						return mapping.findForward("chart");
					}
				}

				if (ucRForm.getFormInterceptor() != null) {
					ucRForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, null);
				}
				getUserCriteriaUtil().setResultset(ucRForm, request);

				if (ucRForm.getFormInterceptor() != null) {
					ucRForm.getFormInterceptor().afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, null);
				}
				
				if( ucRForm.isLoadChartXml() ){
					getUserCriteriaUtil().setChartXML(ucRForm);
				}
			}
			finally {
				ArrayList userCriteriaNames = getUserCriteriaMgr().getUserCriteriaNames(userCriteria);
				getUserCriteriaUtil().initializeForm(request, ucRForm, userCriteriaNames);
			}
		}
		catch (XRuntime xr) {
			_logger.error(xr);
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage()));
			saveErrors(request, errors);
			return mapping.findForward(BaseAction._ERROR_FORWARD);
		}
		catch (Exception ex) {
			_logger.error(ex);			
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));			
			saveErrors(request, errors);
			return mapping.findForward(BaseAction._ERROR_FORWARD);
		}
		_logger.trace("execute.traceExit");
		return mapping.findForward(BaseAction._DONE_FORWARD);
	}

	private void reset(ActionMapping mapping, UserCriteriaSearchForm ucRForm, HttpServletRequest request) throws Exception {
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		ucRForm.setUserId(userName);
		
		String kindOfAction = ucRForm.getKindOfAction() != null ? ucRForm.getKindOfAction() : "";
		String criteriaName = "";
		if( kindOfAction.equals("report") ) {
			criteriaName= request.getParameter( "CRITERIA_NAME_KEY" ) 	!= null ? request.getParameter("CRITERIA_NAME_KEY") :ucRForm.getCriteriaName();
			ucRForm.setCriteriaName(criteriaName);
		}
	}
}
