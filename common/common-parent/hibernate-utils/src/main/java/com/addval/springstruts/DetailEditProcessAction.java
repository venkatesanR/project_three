/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.springstruts;

import java.util.ArrayList;
import java.util.List;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.utils.EJBXRuntime;

import com.addval.metadata.EditorMetaData;
import com.addval.utils.ListUtl;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The processing action from a Master/Detail Edit Screen
 *
 * @author AddVal Technology Inc.
 */
public class DetailEditProcessAction extends EditProcessAction {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(DetailEditProcessAction.class);

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DDD7E130368
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		DetailEditForm editForm = (DetailEditForm) form;
		EJBResultSet masterRS = null;
		List<EJBResultSet> deleteSet = new ArrayList<EJBResultSet>();
		List<EJBResultSet> insertSet = new ArrayList<EJBResultSet>();
		try {
			_logger.trace("execute.traceEnter");
			_logger.info("Start perform(" + form + ") Action is . . ." + editForm.getAction());
			ActionErrors errors = new ActionErrors();

			String action = editForm.getKindOfAction();
			if (editForm.getAction().equalsIgnoreCase("cancel") || editForm.getKindOfAction().equalsIgnoreCase("cancel")) {
				boolean hasSeachCriteria = EjbUtils.isLookupFilterPresent(req, editForm.getMetadata());
				if(hasSeachCriteria){
					ActionForward forward = mapping.findForward(EditProcessAction._CANCEL_FORWARD);
					String cancelUrl = forward.getPath();
					cancelUrl += ((cancelUrl.indexOf("?") != -1) ? "&" : "?");
					cancelUrl +="initialLookup=true";
					cancelUrl += ((cancelUrl.indexOf("?") != -1) ? "&" : "?");
					boolean includeHeaderFooter = req.getParameter( "IncludeHeaderFooter" ) != null ? (new Boolean( req.getParameter( "IncludeHeaderFooter" ) )).booleanValue() : true;
					cancelUrl += "IncludeHeaderFooter="+includeHeaderFooter;
					boolean previewExpand = req.getParameter( "previewExpand" ) != null ? (new Boolean( req.getParameter( "previewExpand" ) )).booleanValue() : true;
					cancelUrl += "&previewExpand="+previewExpand;
					return new ActionForward(EditProcessAction._CANCEL_FORWARD, cancelUrl, false);
				}
				else {
					return mapping.findForward(EditProcessAction._CANCEL_FORWARD);
				}				
			}
			if (action.equals("insert") || action.equals("clone")) {
				masterRS = EjbUtils.getInsertEJBResultSet(editForm.getMetadata(), req, false);
			}
			else if (action.equals("delete")) {
				masterRS = EjbUtils.getDeleteEJBResultSet(editForm.getMetadata(), req);
			}
			else if (action.equals("edit")) {
				masterRS = EjbUtils.getNewEJBResultSet(editForm.getMetadata(), req, false);
			}
			
			if (editForm.getFormInterceptor() != null) {
				editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, masterRS);
				editForm.getFormInterceptor().customValidation(mapping, errors, masterRS);
			}

			if (editForm.getDetailMetadata() == null) {
				List<EditorMetaData> detailMetadata = new ArrayList<EditorMetaData>();
				String userName = (req.getUserPrincipal() != null) ? req.getUserPrincipal().getName() : "DEFAULT";
				for (String editorName : editForm.getDetailEditorName()) {
					EditorMetaData editorMetaData = lookupMetadata(editorName,  editForm.getDetailEditorType(), userName, req.getSession());
					detailMetadata.add(editorMetaData);
				}
				editForm.setDetailMetadata(detailMetadata);
				editForm.configureMetadata();
			}

			for (EditorMetaData editorMetaData : editForm.getDetailMetadata()) {
				
				EJBResultSet deleteRS = EjbUtils.getDetailDeleteEJBResultSet(editorMetaData, req, editForm.getMetadata());
				if (editForm.getFormInterceptor() != null) {
					editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, deleteRS);
					editForm.getFormInterceptor().customValidation(mapping, errors, deleteRS);
				}
				deleteSet.add(deleteRS);
				
				if (!action.equals("delete")) {
					EJBResultSet insertRS = EjbUtils.getDetailEJBResultSet(editorMetaData, req);
					if (editForm.getFormInterceptor() != null) {
						editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, insertRS);
						editForm.getFormInterceptor().customValidation(mapping, errors, insertRS);
					}
					insertSet.add(insertRS);
				}
			}
			
			if (errors.size() > 0) {
				saveErrors(req, errors);
				if (masterRS != null) {
					masterRS.beforeFirst();
					editForm.setResultset(masterRS);
				}
				if(!ListUtl.isEmpty(insertSet)){
					for (EJBResultSet insertRS : insertSet) {
						insertRS.beforeFirst();
					}
					editForm.setDetailResultset(insertSet);
				}
				editForm.setMutable(false);
				return mapping.findForward(EditProcessAction._FAILED_FORWARD);
			}

			
			getTableManager().updateTransaction(masterRS, deleteSet, insertSet);
			_logger.trace("execute.traceExit");
			
			/* Avoid duplicate submissions F5 to refresh : We need to redirect with parameters */
			ActionForward forward = mapping.findForward(EditProcessAction._DONE_FORWARD);
			String forwardUrl = forward.getPath();
			String whereClause = buildWhereClause(editForm.getMetadata(), req);
			forwardUrl = (forwardUrl.indexOf("?") != -1)? forwardUrl + whereClause : forwardUrl +"?" +  whereClause.substring(1);
			return new ActionForward(EditProcessAction._DONE_FORWARD, forwardUrl, true);
		}
		catch (EJBXRuntime xr) {
			_logger.error(xr);
			ActionErrors errors = new ActionErrors();
			String errMsg = xr.getMessage();

			// translate to an i18n string if possible
			errMsg = translateErrorMessage(req, errMsg);

			int index = errMsg.indexOf("\n");
			if (index > 0){
				errMsg = errMsg.substring(0, index);
			}

			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
			saveErrors(req, errors);
			editForm.setErrorMsg(errMsg);

			if(!ListUtl.isEmpty(insertSet)){
				for (EJBResultSet insertRS : insertSet) {
					insertRS.beforeFirst();
				}
				editForm.setDetailResultset(insertSet);
				if (editForm.getFormInterceptor() != null) {
					editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
				}
			}

			if (masterRS != null) {
				masterRS.beforeFirst();
				editForm.setResultset(masterRS);
			}

			editForm.setMutable(false);
			if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null)){
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD);
			}
			return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}
		catch (Exception e) {
			e.printStackTrace();
			String message = "Critical System Error. Please contact your System Administrator.";
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", e.toString()));
			saveErrors(req, errors);
			editForm.setErrorMsg(message);
			
			if(!ListUtl.isEmpty(insertSet)){
				for (EJBResultSet insertRS : insertSet) {
					insertRS.beforeFirst();
				}
				editForm.setDetailResultset(insertSet);
				if (editForm.getFormInterceptor() != null) {
					editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
				}
			}

			if (masterRS != null) {
				masterRS.beforeFirst();
				editForm.setResultset(masterRS);
			}
			editForm.setMutable(false);
			return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}

	}
}
