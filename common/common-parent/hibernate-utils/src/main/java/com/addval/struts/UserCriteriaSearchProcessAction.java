//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\UserCriteriaSearchProcessAction.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.struts;

import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;
import java.util.ArrayList;
import java.util.Iterator;
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

public class UserCriteriaSearchProcessAction extends BaseAction
{

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
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		String subsystem = (baseMapping.getSubsystem() == null) ? getSubsystem( req ) : baseMapping.getSubsystem();
		String userName  = (req.getUserPrincipal() != null) ? req.getUserPrincipal().getName() : "";

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );

 		try {

			super.execute(mapping, form, req, res);

			UserCriteriaSearchForm listForm = (UserCriteriaSearchForm) form;

			String kindOfAction = listForm.getKindOfAction() != null ? listForm.getKindOfAction() : "";

			listForm.setSubSystem(subsystem);
			listForm.setUserId(userName);

			EditorMetaData   editorMetaData = listForm.getMetadata();
            if (editorMetaData == null) {
				listForm.reset(mapping, req);
				editorMetaData = listForm.getMetadata();
			}

            if(listForm.getEditorMetaDataCopy() == null){
                listForm.setEditorMetaDataCopy((EditorMetaData)editorMetaData.clone() );     
            }


            UserCriteriaUtil util = new UserCriteriaUtil(subsystem);
			util.setCriteriaNames(listForm);

			if(kindOfAction.equals( "apply" )) {
				HttpSession currSession = req.getSession( false );
				UserCriteria criteria = (UserCriteria) currSession.getAttribute( userName + "_" + editorMetaData.getName() + "_USER_CRITERIA" );
				listForm.setCriteria(criteria);
                listForm.setCriteriaName( criteria.getCriteriaName() );
                listForm.setCriteriaDesc( criteria.getCriteriaDesc() );
			}

			if (listForm.getFormInterceptor() != null)
			{
				listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, null);
			}

			util.setResultset(listForm,req,userName);

			if (listForm.getFormInterceptor() != null)
			{
				listForm.getFormInterceptor().afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, null);
			}
			util.setCriteriaNames(listForm);
        }
		catch (XRuntime xr) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in UserCriteriaSearchProcessAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage() ) );
			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}
		catch (Exception ex) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in UserCriteriaSearchProcessAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		return mapping.findForward(ListAction._SHOWLIST_FORWARD);
	}
}
