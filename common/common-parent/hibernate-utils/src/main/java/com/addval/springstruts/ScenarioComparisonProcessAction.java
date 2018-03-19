
package com.addval.springstruts;

import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;
import com.addval.metadata.UserCriteria;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import com.addval.utils.XRuntime;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.addval.utils.StrUtl;

public class ScenarioComparisonProcessAction extends BaseAction
{

	private static transient final Logger _logger = Logger.getLogger(ScenarioComparisonProcessAction.class);

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


	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return org.apache.struts.action.ActionForward
	 * @roseuid 3F9D566F0292
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res)
	{
		String userName  = (req.getUserPrincipal() != null) ? req.getUserPrincipal().getName() : "";
		HttpSession currSession = req.getSession(false);

		_logger.trace("execute.traceEnter");

 		try {

			super.execute(mapping, form, req, res);

			UserCriteriaSearchForm listForm = (UserCriteriaSearchForm) form;
			String userCriteriaChartSessionKey = userName + "_" + listForm.getEditorName() + "_USER_CRITERIA_CHART";
			String kindOfAction = listForm.getKindOfAction() != null ? listForm.getKindOfAction() : "";
			UserCriteria userCriteria = null;

			listForm.setUserId(userName);

			EditorMetaData   editorMetaData = 	lookupMetadata(listForm.getEditorName(), listForm.getEditorType(), userName, req.getSession());
			listForm.setMetadata(editorMetaData);

            if (editorMetaData == null) {
				listForm.reset(mapping, req);
				editorMetaData = listForm.getMetadata();
			}

            if(listForm.getEditorMetaDataCopy() == null){
                listForm.setEditorMetaDataCopy((EditorMetaData)editorMetaData.clone() );
            }
            userCriteria = getUserCriteriaUtil().getUserCriteria( listForm, req );

			userCriteria.setUserName(userName);
			userCriteria.setEditorName(listForm.getMetadata().getName());
			userCriteria.setEditorType(listForm.getMetadata().getType());
			userCriteria.setCriteriaName(listForm.getCriteriaName());
			userCriteria.setCriteriaDesc(listForm.getCriteriaDesc());

			Hashtable userProfile = (Hashtable) currSession.getAttribute("USER_PROFILE");
			if (userProfile != null) {
				String userGroupStr = (String) userProfile.get("UserGroups");
				if (userGroupStr != null)
					userCriteria.setUserGroups( convertToList( userGroupStr ) );
			}
			listForm.setCriteria( userCriteria );
			if (kindOfAction != null && kindOfAction.equals("search") ) {
				UserCriteria userCriteriaDB = getUserCriteriaMgr().lookupUserCriteria( userCriteria );
				if (userCriteriaDB != null){
					listForm.setCriteria( userCriteriaDB );
					currSession.setAttribute( userCriteriaChartSessionKey, userCriteriaDB.getUserCriteriaCharts() );
				}
			}

			if (listForm.getFormInterceptor() != null)
				listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, listForm, req, res, null);

			getUserCriteriaUtil().setResultset( listForm, req );

			if (listForm.getFormInterceptor() != null)
				listForm.getFormInterceptor().afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, listForm, req, res, null);
			ArrayList userCriteriaNames = getUserCriteriaMgr().getUserCriteriaNames(userCriteria);
			getUserCriteriaUtil().initializeForm(req, listForm, userCriteriaNames);
        }
		catch (XRuntime xr) {

			_logger.error("Error looking up data in ScenarioComparisonProcessAction");
			_logger.error(xr);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage() ) );
			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}
		catch (Exception ex) {

			_logger.error("Error looking up data in ScenarioComparisonProcessAction");
			_logger.error(ex);
			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( req, errors );
			return mapping.findForward( ListAction._ERROR_FORWARD );
		}

		_logger.trace( "execute.traceExit" );
		return mapping.findForward(ListAction._SHOWLIST_FORWARD);
	}

	private List convertToList(String valueStr) {
		List newList = new ArrayList();
		if (!StrUtl.isEmptyTrimmed(valueStr)) {
			String values[] = valueStr.split(",");
			List list = Arrays.asList(values);
			newList.addAll(list);
		}
		return newList;
	}

}
