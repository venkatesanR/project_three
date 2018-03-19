//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\UserCriteriaProcessAction.java

package com.addval.struts;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import com.addval.metadata.EditorMetaData;
import com.addval.environment.Environment;
import java.util.ArrayList;
import java.util.Iterator;
import com.addval.metadata.UserCriteria;
import com.addval.metadata.UserCriteriaMgr;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.Globals;
import com.addval.utils.XRuntime;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.regexp.RE;

public class UserCriteriaProcessAction extends BaseAction
{

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return org.apache.struts.action.ActionForward
	 * @roseuid 3F9D5675004C
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res)
	{

		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		String subsystem = (baseMapping.getSubsystem() == null) ? getSubsystem( req ) : baseMapping.getSubsystem();
		String userName  = (req.getUserPrincipal() != null) ? req.getUserPrincipal().getName() : "";

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );

		HttpSession currSession = req.getSession( false );
		UserCriteriaForm critForm = null;
		UserCriteria userCriteria = null;
		ActionMessages messages = new ActionMessages();
 		try {

			super.execute(mapping, form, req, res);
			critForm = (UserCriteriaForm) form;

			critForm.setSubSystem(subsystem);
			critForm.setUserId(userName);

			EditorMetaData   editorMetaData = critForm.getMetadata();

			if (editorMetaData == null) {
				critForm.reset(mapping, req);
				editorMetaData = critForm.getMetadata();
			}

			UserCriteriaUtil	util 	= new UserCriteriaUtil(subsystem);
			UserCriteriaMgr 	manager = new UserCriteriaMgr(subsystem);

			String kindOfAction = critForm.getKindOfAction() != null ? critForm.getKindOfAction() : "";

			try {

				if(kindOfAction.equals("insert")) {
					String critName = critForm.getNewCriteriaName();

					if(critName == null || critName.trim().length() < 1)
						throw new XRuntime(critForm.getSubSystem(),"Please Enter Criteria Name.");

                    RE matcher = new RE( "^\\w([\\-\\s\\w]*)$" );

					if (!matcher.match( critName ))
						throw new XRuntime(critForm.getSubSystem(),"Please Enter Valid Criteria Name.");

					userCriteria = util.getUserCriteria( critForm ,req);

 					if( (critForm.getSelectedAggregatables() == null || critForm.getSelectedAggregatables().length == 0 ) && (critForm.getSelectedDisplayables() == null || critForm.getSelectedDisplayables().length == 0))
						throw new XRuntime(critForm.getSubSystem(),"Please Select any Displayable [ OR ] Aggregatable Column(s).");

					util.validateWhereClause(editorMetaData,userCriteria);

					try{
						manager.insert( userCriteria );
						critForm.setCriteriaName( userCriteria.getCriteriaName() );
						critForm.setCriteriaDesc(userCriteria.getCriteriaDesc());
						critForm.setNewCriteriaName( null );
						critForm.setNewCriteriaDesc( null );
						critForm.setOwner( userCriteria.getUserName() );
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "New User Criteria added Successfully." ));
						
	                    ActionForward forward = mapping.findForward( "search" );
	                    if( forward != null ) {
	                    	return forward;
	                    }
						
					}
					catch (XRuntime xr) {
						if (xr.getMessage().indexOf("SQLException: ORA-00001:") != -1 && critForm.getOverwrite() != null && critForm.getOverwrite().equalsIgnoreCase("YES") ){
							kindOfAction = "update";
						}
						else {
							throw xr;
						}

					}
				}

				if(kindOfAction.equals("update")) {

					userCriteria = util.getUserCriteria( critForm ,req);

					if( ( critForm.getSelectedAggregatables() == null || critForm.getSelectedAggregatables().length == 0 ) && (critForm.getSelectedDisplayables() == null || critForm.getSelectedDisplayables().length == 0 ) )
						throw new XRuntime(critForm.getSubSystem(),"Please Select any Displayable [ OR ] Aggregatable Column(s).");

					util.validateWhereClause(editorMetaData,userCriteria);

                    util.validateUser(userCriteria);

                    manager.update( userCriteria );
					critForm.setCriteriaName(userCriteria.getCriteriaName());
					critForm.setCriteriaDesc(userCriteria.getCriteriaDesc());
					critForm.setOverwrite(null);
					critForm.setNewCriteriaName( null );
					critForm.setNewCriteriaDesc( null );
					messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "User Criteria Updated Successfully." ));
					
                    ActionForward forward = mapping.findForward( "search" );
                    if( forward != null ) {
                    	return forward;
                    }

				}
				else if(kindOfAction.equals("delete")) {

					userCriteria = util.getUserCriteria( critForm ,req);
                    util.validateUser(userCriteria);
                    manager.delete( userCriteria );
					critForm.setCriteriaName( null );
					critForm.setCriteriaDesc( null );
					messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "User Criteria deleted Successfully." ));
					
                    ActionForward forward = mapping.findForward( "search" );
                    if( forward != null ) {
                    	return forward;
                    }
				}
				else if(kindOfAction.equals("session")) {

					userCriteria = util.getUserCriteria( critForm ,req);

					if( ( critForm.getSelectedAggregatables() == null || critForm.getSelectedAggregatables().length == 0 ) && (critForm.getSelectedDisplayables() == null || critForm.getSelectedDisplayables().length == 0 ) )
						throw new XRuntime(critForm.getSubSystem(),"Please Select any Displayable [ OR ] Aggregatable Column(s).");

					util.validateWhereClause(editorMetaData,userCriteria);

                    //Based on the criteria values changes we need to set the Criteria name as blank or value.
                    if( util.isCriteriaChanged( userCriteria ) ) {
                        userCriteria.setCriteriaName( "" );
                        userCriteria.setCriteriaDesc( "" );
                    }
                    currSession.setAttribute(userName + "_" + editorMetaData.getName() + "_USER_CRITERIA", userCriteria );
                    messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "User Criteria updated in Session Successfully." ));
                    
                    ActionForward forward = mapping.findForward( "apply" );
                    if( forward != null ) {
                    	return forward;
                    }
                    
				}
				else if(kindOfAction.equals("cancel")) {
                    ActionForward forward = mapping.findForward( "cancel" );
                    if( forward != null ) {
                    	return forward;
                    }
				}
				saveMessages(req, messages);
			}
			finally {
                if( !kindOfAction.equalsIgnoreCase( "select" ) ) {
					if( kindOfAction.equalsIgnoreCase( "edit" ) ) {
						String criteriaName = req.getParameter( "CRITERIA_NAME_KEY" ) != null ? req.getParameter("CRITERIA_NAME_KEY") :"";
						critForm.setCriteriaName( criteriaName );
					}
					else if( kindOfAction.equalsIgnoreCase( "addnew" ) ) {
						critForm.setCriteriaName( null );
					}
					else {
						UserCriteria criteria = (UserCriteria) currSession.getAttribute(userName + "_" + editorMetaData.getName() + "_USER_CRITERIA" );
						if(criteria != null && kindOfAction.equals("delete") ){
							criteria.setCriteriaDesc( null );
						}
						critForm.setCriteria(criteria);
					}
                }
				util.setCriteriaNames(critForm);
				critForm.setAggregatables(editorMetaData.getAggregatableColumns());
				critForm.setDisplayables(util.getDisplayableColumns( editorMetaData ));

				//critForm.setDisplayables(editorMetaData.getDisplayableColumns());

 				if(!(kindOfAction.equals("session") || kindOfAction.equals("update") || kindOfAction.equals("insert") )) {
					util.populateData(critForm);
				}
			}

		}
		catch (XRuntime xr) {

			critForm.setKindOfAction(null);

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in UserCriteriaProcessAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

			ActionErrors errors = new ActionErrors();

			if (xr.getMessage().indexOf("SQLException: ORA-00001:") != -1) {
				critForm.setOverwrite("QUESTION");
				messages.add( Globals.MESSAGE_KEY, new ActionMessage("info.general", "Warning!  : Criteria Name already Exists. Please click YES to Overwrite it" ) );
				saveMessages(req, messages);
			}
			else{
				errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", xr.getMessage() ) );
			}

			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}
		catch (Exception ex) {

			critForm.setKindOfAction(null);

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in UserCriteriaProcessAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		return mapping.findForward(ListAction._DONE_FORWARD);
	}
}
