//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\DetailEditProcessAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\DetailEditProcessAction.java

package com.addval.struts;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.environment.Environment;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.ejbutils.utils.EJBXRuntime;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;

/**
 * The processing action from a Master/Detail Edit Screen
 * @author AddVal Technology Inc.
 */
public class DetailEditProcessAction extends EditProcessAction
{

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DDD7E130368
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception
	{

		String 			subsystem  = null;
		String 			module	   = getClass().getName();
		DetailEditForm 	editForm   = (DetailEditForm) form;
		EJBResultSet 	masterRS  = null;
		EJBResultSet 	deleteRS 	   = null;
		EJBResultSet 	insertRS 	   = null;
		EditorMetaData editorMetaData = null;
		try {
            DetailBaseActionMapping baseMapping = (DetailBaseActionMapping) mapping;
			String serverType = getServertype(req);
			if (baseMapping.getSubsystem() == null)
				subsystem = getSubsystem(req);
			else
				subsystem = baseMapping.getSubsystem();
		    Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );
//			ActionForward fwd = super.execute(mapping, form, req, res);
			ActionForward fwd = null;
			editorMetaData = editForm.getDetailMetadata();
		    Environment.getInstance( subsystem ).getLogFileMgr( module ).logInfo( "Start perform(" + form + ") Action is . . ." + editForm.getAction()  );
			String action = editForm.getKindOfAction();
			if (editForm.getAction().equalsIgnoreCase("cancel")) {
				fwd = mapping.findForward(EditProcessAction._CANCEL_FORWARD);
			}
			else {
				EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(subsystem);
				EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
				if (action.equals("insert") || action.equals("clone")) {
					masterRS = EjbUtils.getInsertEJBResultSet( editForm.getMetadata(), req, false );
				}
				else if (action.equals( "delete" )) {
					masterRS = EjbUtils.getDeleteEJBResultSet( editForm.getMetadata(), req );
				}
				else if (action.equals( "edit" )) {
					masterRS = EjbUtils.getNewEJBResultSet( editForm.getMetadata(), req, false );
				}




				if (editForm.getFormInterceptor() != null)
				{
					editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, masterRS);

					ActionErrors errors = new ActionErrors();

					editForm.getFormInterceptor().customValidation(mapping,errors,masterRS);

					if(errors.size() > 0) {
						saveErrors( req, errors );
						if (masterRS != null) {
							masterRS.beforeFirst();
							editForm.setResultset( masterRS );
						}
						editForm.setMutable( false );
						return mapping.findForward( EditProcessAction._FAILED_FORWARD );
					}
				}


				deleteRS = EjbUtils.getDetailDeleteEJBResultSet( editorMetaData, req, editForm.getMetadata());
                if (editForm.getFormInterceptor() != null)
                {
                    editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, deleteRS);
                    ActionErrors errors = new ActionErrors();
                    editForm.getFormInterceptor().customValidation(mapping,errors,deleteRS);
                }
				insertRS = EjbUtils.getDetailEJBResultSet( editorMetaData, req );
                if (editForm.getFormInterceptor() != null)
                {
                    editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, insertRS);
                    ActionErrors errors = new ActionErrors();
                    editForm.getFormInterceptor().customValidation(mapping,errors,insertRS);
                }
				boolean updateStatus = tableManagerRemote.updateTransaction( masterRS, deleteRS, insertRS );
/*
				if (!updateStatus) {
					editForm.setErrorMsg( action + " failed. " );
					if (insertRS != null) {
						// Set the form's resultset
						insertRS.beforeFirst();
						editForm.setDetailResultset( insertRS );
					}
					if (masterRS != null) {
						// Set the form's resultset
						masterRS.beforeFirst();
						editForm.setResultset( masterRS );
					}
					fwd = mapping.findForward( EditProcessAction._FAILED_FORWARD );
				}
				else {
					if (serverType.equals("MSAccess"))
						// For MS Access to commit changes to other connections sleep
						java.lang.Thread.currentThread().sleep( 1000 );
					fwd = mapping.findForward(EditProcessAction._DONE_FORWARD);
				}
*/
		   }
	       Environment.getInstance( subsystem ).getLogFileMgr( module ).traceExit( "execute.traceExit" );
		   return mapping.findForward(EditProcessAction._DONE_FORWARD);
		}
		catch (EJBXRuntime xr) {
			Environment.getInstance( subsystem ).getLogFileMgr( module ).logError ( xr );
			ActionErrors errors = new ActionErrors();
			// added changes for processing only a single line of error text
			// useful to filter off any excess Server side error message addition
			// under clustered condition - jeyaraj - 02-Oct-2003
			String errMsg = xr.getMessage();
			int index = errMsg.indexOf( "\n" );
			if (index > 0)
				errMsg = errMsg.substring( 0, index );
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError( "errors.invalid", errMsg ) );
			saveErrors( req, errors );
			editForm.setErrorMsg( errMsg );
			if (insertRS != null) {
				// Set the form's resultset
				insertRS.beforeFirst();
				editForm.setDetailResultset( insertRS );
			}
			if (masterRS != null) {
				// Set the form's resultset
				masterRS.beforeFirst();
				editForm.setResultset( masterRS );
			}
			editForm.setMutable( false );
			return mapping.findForward( EditProcessAction._FAILED_FORWARD );
		}
		catch (Exception e) {
			Environment.getInstance( subsystem ).getLogFileMgr( module ).logError ( e );
			String message = "Critical System Error. Please contact your System Administrator.";
			ActionErrors errors = new ActionErrors();
//			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError( "errors.general", message ) );
			// MAY BE REPLACED LATER WITH MORE MEANINGFUL ERROR MESSAGE - JEYERAJ
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError( "errors.general", e.toString() ) );
			saveErrors( req, errors );
			editForm.setErrorMsg( message );
			if (insertRS != null) {
				// Set the form's resultset
				insertRS.beforeFirst();
				editForm.setDetailResultset( insertRS );
			}
			if (masterRS != null) {
				// Set the form's resultset
				masterRS.beforeFirst();
				editForm.setResultset( masterRS );
			}
			editForm.setMutable( false );
			return mapping.findForward( EditProcessAction._FAILED_FORWARD );
		}
	}
}
