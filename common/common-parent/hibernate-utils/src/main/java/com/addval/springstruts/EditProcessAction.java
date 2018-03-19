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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.addval.dbutils.RSIterAction;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;
import com.addval.springstruts.ResourceUtils;

/**
 * The processing Action from Edit Screen
 *
 * @author AddVal Technology Inc.
 */
public class EditProcessAction extends BaseAction {

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(EditProcessAction.class);

	/**
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @param form
	 *            The optional ActionForm bean for this request (if any)
	 * @param req
	 *            The non-HTTP request we are processing
	 * @param res
	 *            The non-HTTP response we are creating
	 * @return Return an ActionForward instance describing where and how control should be forwarded, or null if the response has already been completed.
	 * @throws java.lang.Exception
	 * @roseuid 3DCC93830376
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		EditForm editForm = (EditForm) form;
		EJBResultSet ejbRS = null;
		String dbError=ResourceUtils.message(req, "error.dbMessage", "Database update failed due to concurrency issues. Please try the transaction again.");
		try {
			EditorMetaData editorMetaData = editForm.getMetadata();
			if (editForm.getAction().equalsIgnoreCase("cancel") || (req.getParameter("action") != null && req.getParameter("action").equals("cancel"))) {
				if (editForm.isListEdit())
					return getParentRefreshAction(mapping, editorMetaData, req, EditProcessAction._CANCEL_FORWARD, editForm.getKindOfAction());

				boolean hasSeachCriteria = EjbUtils.isLookupFilterPresent(req, editorMetaData);
				if(hasSeachCriteria){
					ActionForward forward = mapping.findForward(EditProcessAction._CANCEL_FORWARD);
					String cancelUrl = forward.getPath();
					cancelUrl += ((cancelUrl.indexOf("?") != -1) ? "&" : "?");
					cancelUrl +="initialLookup=true";
					cancelUrl += ((cancelUrl.indexOf("?") != -1) ? "&" : "?");
					boolean includeHeaderFooter = req.getParameter( "IncludeHeaderFooter" ) != null ? (new Boolean( req.getParameter( "IncludeHeaderFooter" ) )).booleanValue() : true;
					cancelUrl += "IncludeHeaderFooter="+includeHeaderFooter;
					return new ActionForward(EditProcessAction._CANCEL_FORWARD, cancelUrl, false);
				}
				else {
					return mapping.findForward(EditProcessAction._CANCEL_FORWARD);
				}
			}

			if (editForm.getKindOfAction().equals("edit"))
				ejbRS = EjbUtils.getNewEJBResultSet(editorMetaData, req, false);
			else if (editForm.getKindOfAction().equals("insert") || editForm.getKindOfAction().equals("clone"))
				ejbRS = EjbUtils.getInsertEJBResultSet(editorMetaData, req, false);
			else if (editForm.getKindOfAction().equalsIgnoreCase("delete"))
				ejbRS = EjbUtils.getDeleteEJBResultSet(editorMetaData, req);

			boolean updateStatus = true;
			if (editForm.getFormInterceptor() != null) {
				editForm.getFormInterceptor().beforeDataProcess(mapping, form, req, res, ejbRS);
				ActionErrors errors = new ActionErrors();
				editForm.getFormInterceptor().customValidation(mapping, errors, ejbRS);
				if (errors.size() > 0) {
					saveErrors(req, errors);
					if (ejbRS != null) {
						ejbRS.beforeFirst();
						editForm.setResultset(ejbRS);
					}

					editForm.setMutable(false);
					if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
						return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

					return mapping.findForward(EditProcessAction._FAILED_FORWARD);
				}
			}

			// Reset the errors
			editForm.setErrorMsg("");
			if (editForm.getKindOfAction().equals("insert") || editForm.getKindOfAction().equals("clone") || editForm.getKindOfAction().equals("edit")) {
				setFileByteArray2EjbResultSet(editForm, editorMetaData, req, ejbRS);
			}

			ejbRS = getTableManager().updateTransaction(ejbRS);
			if (ejbRS == null) {
				updateStatus = false;
				editForm.setErrorMsg(editForm.getKindOfAction() + " failed. ");
			}
			else {
				ejbRS.beforeFirst();
				while (updateStatus && ejbRS.next()) {
					if ((editForm.getKindOfAction().equals("delete") && !ejbRS.rowDeleted()) || (editForm.getKindOfAction().equals("clone") && !ejbRS.rowInserted()) || (editForm.getKindOfAction().equals("insert") && !ejbRS.rowInserted())
							|| (editForm.getKindOfAction().equals("edit") && !ejbRS.rowUpdated())) {
						updateStatus = false;
						editForm.setErrorMsg(editForm.getKindOfAction() + " failed. ");
					}
					else if (ejbRS.getRecord().getSyncStatus() != com.addval.metadata.RecordStatus._RSS_SYNC) {
						updateStatus = false;
						editForm.setErrorMsg(dbError);
					}
				}
			}

			if (editForm.getFormInterceptor() != null)
				editForm.getFormInterceptor().afterDataProcess(mapping, form, req, res, ejbRS);

			if (updateStatus) {
				// Added so that the edit form has the resultset. If anyone overrides this
				// action class and calls super.execute(), the resultset will be available for them
				editForm.setResultset(ejbRS);
				if ((ejbRS != null) && editForm.hasChild() && (editForm.getKindOfAction().equals("insert") || editForm.getKindOfAction().equals("clone")))
					return getEditAction(mapping, editorMetaData, ejbRS, req);
				else if (editForm.isListEdit())
					return getParentRefreshAction(mapping, editorMetaData, req, EditProcessAction._DONE_FORWARD, editForm.getKindOfAction());
				else{
					//return mapping.findForward(EditProcessAction._DONE_FORWARD);

					/* Avoid duplicate submissions F5 to refresh : We need to redirect with parameters */
					ActionForward forward = mapping.findForward(EditProcessAction._DONE_FORWARD);
					String forwardUrl = forward.getPath();
					String whereClause = buildWhereClause(editorMetaData, req);
					forwardUrl = (forwardUrl.indexOf("?") != -1)? forwardUrl + whereClause : forwardUrl +"?" +  whereClause.substring(1);
					return new ActionForward(EditProcessAction._DONE_FORWARD, forwardUrl, true);
				}
			}
			else {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", editForm.getErrorMsg()));
				saveErrors(req, errors);
				editForm.setResultset(null);
				editForm.setMutable(false);
				if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
					return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

				return mapping.findForward(EditProcessAction._FAILED_FORWARD);
			}

			// Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
		}
		catch (XRuntime e) {
			// used to catch the error message specifically thrown from the screen
			_logger.error(e);
			ActionErrors errors = new ActionErrors();
			String errMsg = e.getMessage();

			// translate to an internationalized string
			errMsg = translateErrorMessage(req, errMsg);

			int index = errMsg.indexOf("\n");
			if (index > 0)
				errMsg = errMsg.substring(0, index);

			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
			saveErrors(req, errors);
			editForm.setErrorMsg(errMsg);
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset(ejbRS);
			}

			editForm.setMutable(false);
			if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

			return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}
		catch (EJBXRuntime xr) {
			_logger.error(xr);
			ActionErrors errors = new ActionErrors();

			// added changes for processing only a single line of error text
			// useful to filter off any excess Server side error message addition
			// under clustered condition - jeyaraj - 02-Oct-2003
			String errMsg = xr.getMessage();

			// translate to an internationalized string
			errMsg = translateErrorMessage(req, errMsg);

			int index = errMsg.indexOf("\n");
			if (index > 0)
				errMsg = errMsg.substring(0, index);

			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
			saveErrors(req, errors);
			editForm.setErrorMsg(errMsg);
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset(ejbRS);
			}

			editForm.setMutable(false);
			if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

			return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}
		catch (IOException ioe) {
			_logger.error(ioe);
			ActionErrors errors = new ActionErrors();
			String errMsg = ioe.getMessage();
			int index = errMsg.indexOf("\n");
			if (index > 0)
				errMsg = errMsg.substring(0, index);

			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
			saveErrors(req, errors);
			editForm.setErrorMsg(errMsg);
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset(ejbRS);
			}

			editForm.setMutable(false);
			if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

			return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}
		catch (Exception e) {
			_logger.error(e);
			String message = "Critical System Error. Please contact your System Administrator.";
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(req, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			saveErrors(req, errors);
			editForm.setErrorMsg(message);
			if (ejbRS != null) {
				// Set the form's resultset
				ejbRS.beforeFirst();
				editForm.setResultset(ejbRS);
			}

			editForm.setMutable(false);
			if (editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
				return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

			return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}
	}

	private ActionForward getParentRefreshAction(ActionMapping mapping, EditorMetaData editorMetaData, HttpServletRequest request, String action, String kindOfAction) throws java.io.UnsupportedEncodingException {
		String queryString = getQueryString(editorMetaData, request);
		ActionForward forward = mapping.findForward(action);
		String refreshparent = forward.getPath().substring(1);
		if (refreshparent.indexOf("?") != -1)
			refreshparent += queryString;
		else if (queryString.length() > 0)
			refreshparent += ("?" + queryString.substring(1));

		refreshparent = "/refreshparent.do?LOAD_URL=" + java.net.URLEncoder.encode(refreshparent, "UTF-8") + "&ActionType=" + kindOfAction;

		return new ActionForward(EditProcessAction._DONE_FORWARD, refreshparent, true);
	}

	protected String getLinkJsp(EditorMetaData editorMetaData) {
		if (editorMetaData.getDisplayableColumns() == null)
			return null;
		for (Iterator iter = editorMetaData.getDisplayableColumns().iterator(); iter.hasNext();) {
			ColumnMetaData columnMetaData = (ColumnMetaData) iter.next();
			if (columnMetaData.getType() == ColumnDataType._CDT_LINK && columnMetaData.getName().toUpperCase().endsWith("_EDIT"))
				return columnMetaData.getRegexp();
		}
		return null;
	}

	private boolean isNewUI(ActionMapping mapping) {
		ActionForward aFwd = mapping.findForward(EditProcessAction._DONE_FORWARD);
		if (aFwd.getPath().startsWith("/New")) {
			return true;
		}
		return false;
	}

	private ActionForward getEditAction(ActionMapping mapping, EditorMetaData editorMetaData, EJBResultSet ejbRS, HttpServletRequest request) throws EJBXRuntime {
		boolean isNewUI = isNewUI(mapping);

		String buildQuery = getQueryString(editorMetaData, request);
		/****************************************************************/
		String columnName = null;
		StringBuffer keys = new StringBuffer();
		if (editorMetaData.getKeyColumns() != null) {
			ejbRS.beforeFirst();
			if (ejbRS.next()) {
				for (Iterator iter = editorMetaData.getKeyColumns().iterator(); iter.hasNext();) {
					columnName = ((ColumnMetaData) iter.next()).getName();
					keys.append("&");
					keys.append(columnName + "_KEY");
					keys.append("=");
					keys.append(ejbRS.getString(columnName));
				}
			}
		}

		String linkJsp = getLinkJsp(editorMetaData);
		if ((linkJsp == null) || linkJsp.equals(""))
			throw new EJBXRuntime("Target Link file for columnName is not specified in RegExp column of Table!");

		linkJsp += ((linkJsp.indexOf("?") != -1) ? "&" : "?");
		if (buildQuery.length() > 0)
			linkJsp += (buildQuery.substring(1) + keys.toString());
		else
			linkJsp += ((keys.toString().length() > 0) ? keys.substring(1) : "");

		if (!linkJsp.startsWith("/")) {
			linkJsp = "/" + linkJsp;
		}
		if (isNewUI && linkJsp.startsWith("/ListProcess.do")) {
			linkJsp = "/New" + linkJsp.substring(1);
		}
		return new ActionForward(EditProcessAction._DONE_FORWARD, linkJsp, true);
	}

	protected String getQueryString(EditorMetaData editorMetaData, HttpServletRequest request) {
		StringBuffer buildQuery = new StringBuffer();
		String columnName = null;
		String columnValue = null;
		/****************************************************************/
		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.endsWith("childIndex") || columnName.endsWith("_lookUp") || columnName.endsWith("_PARENT_KEY") || (columnName.endsWith("operator_lookUp") && !columnName.equals("EDITOR_NAME") && !columnName.equals("EDITOR_TYPE")) || columnName.equals("POSITION")
					|| columnName.equals("DETAILS") || columnName.equals("IncludeHeaderFooter") || columnName.equals("minimizeFilter")  || columnName.equals("previewExpand")) {
				columnValue = request.getParameter(columnName);
				if ((columnValue != null) && (columnValue.length() != 0))
					buildQuery.append("&" + columnName + "=" + request.getParameter(columnName).trim());
			}
			else if (columnName.equals("PAGING_ACTION"))
				buildQuery.append("&" + columnName + "=" + RSIterAction._CURR_STR);
		}

		buildQuery.append("&EDITOR_NAME=" + editorMetaData.getName());
		buildQuery.append("&EDITOR_TYPE=" + editorMetaData.getType());
		return buildQuery.toString();
	}

	/**
	 * This method convert file upload in the serveltInputStream to byte Array
	 */
	public void setFileByteArray2EjbResultSet(EditForm form, EditorMetaData editorMetaData, HttpServletRequest request, EJBResultSet ejbRS) throws Exception {
		Vector columns = editorMetaData.getColumnsMetaData();
		ColumnMetaData columnMetaData = null;
		int size = (columns == null) ? 0 : columns.size();
		for (int index = 0; index < size; index++) {
			columnMetaData = (ColumnMetaData) columns.elementAt(index);
			if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {
				try {
					Hashtable files = form.getMultipartRequestHandler().getFileElements();
					if (files == null) {
						return;
					}
					Iterator iter = files.keySet().iterator();
					while (iter.hasNext()) {
						String fileFieldName = (String) iter.next();
						FormFile inputFile = (FormFile) files.get(fileFieldName);
						if (inputFile != null) {
							if (inputFile.getFileSize() > 0) {
								byte[] fileBytes = inputFile.getFileData();
								inputFile.destroy();
								String colName = fileFieldName.substring(0, fileFieldName.lastIndexOf("_edit"));
								ejbRS.updateObject(colName, fileBytes);
								String fileColumnName = colName + "NAME";
								ColumnMetaData fileColumnMetaData = editorMetaData.getColumnMetaData(fileColumnName);
								if (fileColumnMetaData != null) {
									fileColumnMetaData.setUpdatable(true);
									ejbRS.updateString(fileColumnName, inputFile.getFileName());
								}
							}
							else {
								if (form.getKindOfAction().equals("insert") || form.getKindOfAction().equals("clone")) {
									throw new IOException("Please check the given file path is correct");
								}
								inputFile.destroy();
							}
						}
					}
				}
				catch (Exception e) {
					throw new IOException("Please check the given file path is correct");
				}
			}
		}
	}

	private void displayRequestParamter(HttpServletRequest request) {
		System.out.println("displayRequestParamter ");
		java.util.Enumeration enumeration = request.getParameterNames();
		for (; enumeration.hasMoreElements();) {
			String key = (String) enumeration.nextElement();
			String value = request.getParameter(key);
			System.out.println(" key :" + key + "\t value :" + value);
		}
	}

	protected String buildWhereClause(EditorMetaData editorMetaData,HttpServletRequest request) {
		StringBuffer buildQuery = new StringBuffer();
		String columnName = null;
		String columnValue = null;
		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.equals("PAGING_ACTION")) {
				buildQuery.append("&").append(columnName).append("=").append(RSIterAction._CURR_STR);
			}
			else if (columnName.startsWith("searchColumns") || columnName.endsWith("_lookUp") || columnName.endsWith("_PARENT_KEY") || columnName.endsWith("operator_lookUp") && !columnName.equals("EDITOR_NAME") && !columnName.equals("EDITOR_TYPE") || columnName.equals("POSITION") || columnName.equals("DETAILS") || columnName.equals("PAGESIZE") || columnName.equals("IncludeHeaderFooter") || columnName.equals("rowSelector")) {
				columnValue = request.getParameter(columnName);
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = request.getParameter(columnName).trim();
					buildQuery.append("&").append(columnName).append("=").append(columnValue);
				}
			}
		}

		buildQuery.append("&EDITOR_NAME=").append(editorMetaData.getName());
		buildQuery.append("&EDITOR_TYPE=").append(editorMetaData.getType());

		String childIndex = request.getParameter("childIndex") != null ? request.getParameter("childIndex") : "";
		buildQuery.append("&childIndex=").append(childIndex);

		String searchFilterName = request.getParameter("searchFilterName") != null ? request.getParameter("searchFilterName") : "";
		buildQuery.append("&searchFilterName=").append(searchFilterName);

		String templateName =request.getParameter("templateName") != null ? request.getParameter("templateName") : "";
		buildQuery.append("&templateName=").append(templateName);

		boolean minimizeFilter = request.getParameter( "minimizeFilter" ) != null ? (new Boolean( request.getParameter( "minimizeFilter" ) )).booleanValue() : true;
		buildQuery.append("&minimizeFilter=").append(minimizeFilter);

		boolean previewExpand = request.getParameter( "previewExpand" ) != null ? (new Boolean( request.getParameter( "previewExpand" ) )).booleanValue() : true;
		buildQuery.append("&previewExpand=").append(previewExpand);

		boolean advancedSearchExpandFlag = request.getParameter( "advancedSearchExpandFlag" ) != null ? (new Boolean( request.getParameter( "advancedSearchExpandFlag" ) )).booleanValue() : false;
		buildQuery.append("&advancedSearchExpandFlag=").append(advancedSearchExpandFlag);

		String sortName =request.getParameter("sort_Name") != null ? request.getParameter("sort_Name") : "";
		buildQuery.append("&sort_Name=").append(sortName);

		String sortOrder =request.getParameter("sort_Order") != null ? request.getParameter("sort_Order") : "";
		buildQuery.append("&sort_Order=").append(sortOrder);


		return buildQuery.toString();
	}
}
