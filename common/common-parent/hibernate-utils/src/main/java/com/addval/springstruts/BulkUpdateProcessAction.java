package com.addval.springstruts;

import java.util.ArrayList;

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

import com.addval.ejbutils.server.BulkUpdateMgr;
import com.addval.esapiutils.validator.HTMLSecurityValidator;
import com.addval.metadata.BulkUpdate;
import com.addval.utils.LogMgr;
import com.addval.utils.StrUtl;

public class BulkUpdateProcessAction extends BaseAction {

	private static transient final Logger _logger = LogMgr.getLogger(BulkUpdateProcessAction.class);
	private HTMLSecurityValidator htmlSecurityValidator;
	private BulkUpdateMgr _bulkUpdateMgr;
	private BulkUpdateUtil _bulkUpdateUtil;
	

	public HTMLSecurityValidator getHtmlSecurityValidator() {
		return htmlSecurityValidator;
	}

	public void setHtmlSecurityValidator(HTMLSecurityValidator htmlSecurityValidator) {
		this.htmlSecurityValidator = htmlSecurityValidator;
	}

	public BulkUpdateMgr getBulkUpdateMgr() {
		return _bulkUpdateMgr;
	}
	
	public void setBulkUpdateMgr(BulkUpdateMgr bulkUpdateMgr) {
		this._bulkUpdateMgr = bulkUpdateMgr;
	}
	
	public BulkUpdateUtil getBulkUpdateUtil() {
		return _bulkUpdateUtil;
	}
	
	public void setBulkUpdateUtil(BulkUpdateUtil bulkUpdateUtil) {
		this._bulkUpdateUtil = bulkUpdateUtil;
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		_logger.trace("execute.traceEnter");

		BulkUpdateForm bulkUpdateForm = (BulkUpdateForm) form;
		String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		String kindOfAction = bulkUpdateForm.getKindOfAction() != null ? bulkUpdateForm.getKindOfAction() : "";
		ActionErrors errors = new ActionErrors();
		ActionMessages messages = new ActionMessages();
		HttpSession currSession = request.getSession(false);
		ActionForward forward = null;
		BulkUpdate bulkUpdate = null;
		try{
			super.execute(mapping, form, request, response);
			try {
				
				errors = bulkUpdateForm.validate(mapping, request);
				if (errors.size() > 0) {
					saveErrors(request, errors);
					return mapping.findForward(BaseAction._ERROR_FORWARD);
				}
				
				bulkUpdate = getBulkUpdateUtil().getBulkUpdate(bulkUpdateForm, request);
				
				if( kindOfAction.equals("insert") || kindOfAction.equals("update") ){
					errors = getBulkUpdateUtil().validateBulkUpdate(getBulkUpdateMgr(),bulkUpdateForm.getMetadata(), bulkUpdate, kindOfAction);

					if (errors.size() > 0) {
						saveErrors(request, errors);
						return mapping.findForward(BaseAction._ERROR_FORWARD);
					}
				}

				if (kindOfAction.equals("insert") || kindOfAction.equals("update") || kindOfAction.equals("delete") ) {

					if (kindOfAction.equals("insert")) {
						try {
							getBulkUpdateMgr().addNewBulkUpdate(bulkUpdate);
							messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", ResourceUtils.message(request, "info.bulkupdate.newsuccess", "New Update added Successfully.")));
						}
						catch (Exception ex) {
							if (ex.getMessage().indexOf("SQLException: ORA-00001:") != -1 && bulkUpdateForm.getConfirmation() != null && bulkUpdateForm.getConfirmation().equalsIgnoreCase("YES")) {
								kindOfAction = "update";
							}
							else {
								throw ex;
							}
						}
					}
					
					if (kindOfAction.equals("update")) {

						getBulkUpdateMgr().updateBulkUpdate(bulkUpdate);
						bulkUpdateForm.setConfirmation(null);
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", ResourceUtils.message(request, "info.bulkupdate.updatesuccess", "Update Name Updated Successfully.")));
					}

					if (kindOfAction.equals("delete")) {
						getBulkUpdateMgr().deleteBulkUpdate(bulkUpdate);
						bulkUpdate.setUpdateName(null);
						bulkUpdate.setUpdateDesc(null);
						messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", ResourceUtils.message(request, "info.bulkupdate.deletesuccess", "Update deleted Successfully.")));
					}

					bulkUpdateForm.setUpdateName(bulkUpdate.getUpdateName() );
					bulkUpdateForm.setUpdateDesc(bulkUpdate.getUpdateDesc());
				}
				else if (kindOfAction.length() == 0) {
					String directoryName=null;
					htmlSecurityValidator=getHtmlSecurityValidator();
					
					if(request.getAttribute( "directoryName" ) != null)
					{
						directoryName=htmlSecurityValidator.getValidInput("BulkUpdateProcessAction", (String) request.getAttribute( "criteriaName" ));
					}
					else{
						directoryName=htmlSecurityValidator.getValidInput("BulkUpdateProcessAction", request.getParameter( "criteriaName" ));
					}
					
					
					//String directoryName = (request.getAttribute( "directoryName" ) != null)? (String) request.getAttribute( "directoryName" ):request.getParameter( "directoryName" );
					//todo We should change criteriaName by updateName
					String updateName = (request.getAttribute( "criteriaName" ) != null)? (String) request.getAttribute( "criteriaName" ):request.getParameter( "criteriaName" );
					
					_logger.debug("BulkUpdateProcessAction:directoryName=" + directoryName);
					_logger.debug("BulkUpdateProcessAction:updateName=" + updateName);
					
					if (!StrUtl.isEmpty( directoryName ) ) {

						bulkUpdateForm.setDirectoryName( directoryName );
						bulkUpdate.setDirectoryName( directoryName );

						if(!StrUtl.isEmpty( updateName )){
							bulkUpdate.setUpdateName( updateName );
							BulkUpdate bulkUpdateDB = getBulkUpdateMgr().lookupBulkUpdate(bulkUpdate);
							bulkUpdateForm.setBulkUpdate(bulkUpdateDB);
						}
						
					}
				}
				else if (kindOfAction.equals("select")) {
					String updateName = bulkUpdateForm.getUpdateName();
					if (!StrUtl.isEmptyTrimmed(updateName)) {
						bulkUpdate.setUpdateName( updateName );
						BulkUpdate bulkUpdateDB = getBulkUpdateMgr().lookupBulkUpdate(bulkUpdate);
						bulkUpdateForm.setBulkUpdate(bulkUpdateDB);
					}
				}				
				else if (kindOfAction.equals("cancel")) {
					
					/*We should encode the url and pass it as CallBackLink.
					 ../editor/QueryList.do?editorName=LIST_USER_CRITERIA&EDITOR_NAME_lookUp=DEMO_WEEKLY_REPORT
					 ..%2Feditor%2FQueryList.do%3FeditorName%3DLIST_USER_CRITERIA%26EDITOR_NAME_lookUp%3DDEMO_WEEKLY_REPORT
					 */

					forward = mapping.findForward(BaseAction._CANCEL_FORWARD);
			        String callBackLink = bulkUpdateForm.getCallBackLink();
			        if(!StrUtl.isEmptyTrimmed( callBackLink )){
			        	forward = new ActionForward(forward.getName(), callBackLink ,true);
			        }
				}

				if (forward != null) {
					return forward;
				}

				saveMessages(request, messages);
			}
			finally {
				if(bulkUpdate == null){
					bulkUpdate = getBulkUpdateUtil().getBulkUpdate(bulkUpdateForm, request);
				}
				ArrayList<BulkUpdate> bulkUpdates = getBulkUpdateMgr().lookupBulkUpdates(bulkUpdate);
				getBulkUpdateUtil().initializeForm(request, bulkUpdateForm, bulkUpdates);
			}
		}
		catch (Exception ex) {
			bulkUpdateForm.setKindOfAction(null);
			_logger.error(ex);

			if (ex != null && ex.getMessage().indexOf("SQLException: ORA-00001:") != -1) {
				bulkUpdateForm.setConfirmation("QUESTION");
				messages.add(Globals.MESSAGE_KEY, new ActionMessage("info.general", ResourceUtils.message(request, "info.bulkupdate.overwritequestion", "Warning! : Update Name already Exists. Please click YES to Overwrite it")));
			}
			else if (ex != null) {
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
}
