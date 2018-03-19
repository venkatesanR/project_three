
package com.addval.struts;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionMessage;
import com.addval.environment.Environment;
import com.addval.utils.XRuntime;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UpdateUserCriteria;
import com.addval.metadata.UpdateUserCriteriaMgr;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.Globals;
import org.apache.regexp.RE;
import com.addval.utils.StrUtl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateUserCriteriaProcessAction extends BaseAction
{
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res)
	{
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		String subsystem = (baseMapping.getSubsystem() == null) ? getSubsystem( req ) : baseMapping.getSubsystem();

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );
		UpdateUserCriteriaForm critForm = null;
		UpdateUserCriteria criteria = null;
		ActionMessages messages = new ActionMessages();
		ActionErrors errors = new ActionErrors();

		try {

			super.execute(mapping, form, req, res);
			critForm = (UpdateUserCriteriaForm) form;
			critForm.setSubSystem(subsystem);
		 	EditorMetaData   editorMetaData = critForm.getMetadata();
			if (editorMetaData == null) {
				critForm.reset(mapping, req);
				editorMetaData = critForm.getMetadata();
			}

			UpdateUserCriteriaUtil	util 	= new UpdateUserCriteriaUtil(subsystem);
			UpdateUserCriteriaMgr 	manager = new UpdateUserCriteriaMgr(subsystem);
			String kindOfAction = critForm.getKindOfAction() != null ? critForm.getKindOfAction() : "";

			try {
				errors = critForm.validate(mapping,req);
				if(errors.size() > 0) {
					saveErrors( req, errors );
					return mapping.findForward( ListAction._ERROR_FORWARD );
				}

				if(kindOfAction.equals("insert")) {
					String critName = critForm.getNewCriteriaName();
					if(critName == null || critName.trim().length() < 1)
						throw new XRuntime(critForm.getSubSystem(),"Please Enter Criteria Name.");

                    RE matcher = new RE( "^\\w([\\-\\s\\w]*)$" );					

					if (!matcher.match( critName ))
						throw new XRuntime(critForm.getSubSystem(),"Please Enter Valid Criteria Name.");

					criteria = util.getUpdateUserCriteria( critForm );

					util.validateUpdateSql(editorMetaData,criteria);

					try{

						manager.insert( criteria );
						critForm.setCriteriaName( criteria.getCriteriaName() );
						critForm.setCriteriaDesc( criteria.getCriteriaDesc() );
						critForm.setNewCriteriaName( null );
						critForm.setNewCriteriaDesc( null );
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "New Update Criteria added Successfully." ));
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

					criteria = util.getUpdateUserCriteria( critForm );

					util.validateUpdateSql(editorMetaData,criteria);

					manager.update( criteria );
					critForm.setCriteriaName(criteria.getCriteriaName());
					critForm.setCriteriaDesc(criteria.getCriteriaDesc());
					critForm.setOverwrite(null);
					critForm.setNewCriteriaName( null );
					critForm.setNewCriteriaDesc( null );
					messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "Update Criteria Updated Successfully." ));
				}
				else if(kindOfAction.equals("delete")) {

					criteria = util.getUpdateUserCriteria( critForm );
					manager.delete( criteria );
					criteria = null;
					critForm.setCriteriaName( null );
					critForm.setCriteriaDesc( null );
					messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", "Update Criteria deleted Successfully." ));
				}
				else if (kindOfAction.equals( "cancel" )) {
					Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
					return mapping.findForward(ListAction._CANCEL_FORWARD);
				}
				else if (kindOfAction.length() == 0) {

					String directoryName = (String)req.getAttribute( "directoryName" );
					String criteriaName  = (String)req.getAttribute( "criteriaName" );

					if (!StrUtl.isEmpty( directoryName )) {

						critForm.setDirectoryName( directoryName );
						if (!StrUtl.isEmpty( criteriaName ))
							critForm.setCriteriaName( criteriaName );
					}
				}

				saveMessages(req, messages);
			}
			finally {
				critForm.setCriteria(criteria);
				util.setDirectoryNames( critForm );

				if(
					(!StrUtl.isEmpty(critForm.getDirectoryName()) && !kindOfAction.equals("")) ||
					(!StrUtl.isEmpty(critForm.getDirectoryName()) && !StrUtl.isEmpty(critForm.getCriteriaName()))
				   )
				{
					util.setCriteriaNames( critForm );
				}

				if(!(kindOfAction.equals("update") || kindOfAction.equals("insert") ))
					util.populateData(critForm);

			}
		}
		catch (XRuntime xr) {

			critForm.setKindOfAction(null);

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in UpdateUserCriteriaProcessAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

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

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in UpdateUserCriteriaProcessAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );

		}
		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		return mapping.findForward(ListAction._DONE_FORWARD);
	}
}
