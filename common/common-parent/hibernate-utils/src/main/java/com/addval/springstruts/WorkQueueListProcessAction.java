package com.addval.springstruts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.ui.UIComponentMetadata;
import com.addval.utils.StrUtl;
import com.addval.wqutils.metadata.WQMetaData;

public class WorkQueueListProcessAction extends WorkQueueBaseAction {

	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WorkQueueListProcessAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		try {
			_logger.trace("execute.traceEnter");

			EJBResultSet ejbRS = null;
			WorkQueueListForm listForm = (WorkQueueListForm) form;
			String kindOfAction = request.getParameter("kindOfAction");

			if (!StrUtl.isEmptyTrimmed(kindOfAction)) {
				 boolean isApplyTemplate = "ApplyTemplate".equalsIgnoreCase(kindOfAction);
				String queueName = listForm.getQueueName();
				if (!StrUtl.isEmptyTrimmed(queueName)) {
					WQMetaData wqMetaData = lookupWqSMetaData(getWqSMetaData(), request.getSession(), queueName);
					listForm.setEditorName(wqMetaData.getEditorMetaData().getName());
					listForm.setMetadata(wqMetaData.getEditorMetaData());
					listForm.setWQMetaData(wqMetaData);
				}
				if(isApplyTemplate) {
		            String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "";
		            String editorName = getEditorName(request);
		            String editorType = getEditorType(request);
		            String templateName = (request.getParameter("templateName") != null) ? request.getParameter("templateName") : "";
					String componentPrefix = request.getParameter("componentPrefix") != null ? request.getParameter("componentPrefix") : "";

		            if(!StrUtl.isEmptyTrimmed( templateName )) {
		                removeMetadata(editorName, editorType, userName,templateName, request.getSession());
		                EditorMetaData editorMetaData = lookupMetadata(editorName, editorType, userName, templateName,componentPrefix, request.getSession(), true);
		                listForm.setDisplayMetadata(editorMetaData);
		            }
		            else {
		            	 listForm.setDisplayMetadata(null);
		            }
		        }
			  	else if (kindOfAction.endsWith("_process") || kindOfAction.endsWith("_process_next")) {
					listForm.initializeFromMetadata(request, mapping, listForm.getMetadata());
					String queueCriteria = listForm.getWQMetaData().getQueueWhereClause(request);
					request.setAttribute("QUEUE_CRITERIA", (!StrUtl.isEmptyTrimmed(queueCriteria)) ? queueCriteria : null);

					EJBCriteria ejbCriteria = null;

					if (kindOfAction.endsWith("_process_next")) {
						ejbCriteria = EjbUtils.getEJBCriteria(listForm.getMetadata(),listForm.getDisplayMetadata(), request, true);
						if (listForm.getFormInterceptor() != null) {
							listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, ejbCriteria);
						}
						ejbRS = getWqServer().processNextMessage(ejbCriteria);
					}
					else if (kindOfAction.endsWith("_process")) {
						ejbCriteria = EjbUtils.getEJBCriteria(listForm.getMetadata(), request, false);
						if (listForm.getFormInterceptor() != null) {
							listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, request, response, ejbCriteria);
						}
						ejbRS = getWqServer().processMessage(ejbCriteria);
					}

					if (ejbRS == null) {
						throw new EJBXRuntime(getClass().getName(), "Process Message failed.");
					}
					String processAction = listForm.getWQMetaData().getProcessAction();
					if (!StrUtl.isEmptyTrimmed(processAction)) {
						String queryString = "queueName=" + queueName;
						if (ejbRS != null) {
							ejbRS.beforeFirst();
							if (ejbRS.next()) {
								Vector<ColumnMetaData> keyColumns = ((EJBResultSetMetaData) ejbRS.getMetaData()).getEditorMetaData().getKeyColumns();
								if (keyColumns != null) {
									for (ColumnMetaData columnMetaData : keyColumns) {
										queryString += "&" + columnMetaData.getName() + "_KEY=" + ejbRS.getString(columnMetaData.getName());
									}
								}
							}
						}
						boolean includeHeaderFooter = request.getParameter( "IncludeHeaderFooter" ) != null ? (new Boolean( request.getParameter( "IncludeHeaderFooter" ) )).booleanValue() : true;
						queryString += "&IncludeHeaderFooter="+includeHeaderFooter;
						processAction += (processAction.indexOf("?") != -1) ? "&" : "?";
						processAction += queryString;
						return new ActionForward(ListProcessAction._SEARCH_FORWARD, processAction, true, false);
					}
				}
				else if (kindOfAction.equalsIgnoreCase("delete")) {
					String deleteAction = listForm.getWQMetaData().getDeleteAction();
					if (!StrUtl.isEmptyTrimmed(deleteAction)) {
						String queryString = "queueName=" + queueName;
						if (ejbRS != null) {
							ejbRS.beforeFirst();
							if (ejbRS.next()) {
								Vector<ColumnMetaData> keyColumns = ((EJBResultSetMetaData) ejbRS.getMetaData()).getEditorMetaData().getKeyColumns();
								if (keyColumns != null) {
									for (ColumnMetaData columnMetaData : keyColumns) {
										queryString += "&" + columnMetaData.getName() + "_KEY=" + ejbRS.getString(columnMetaData.getName());
									}
								}
							}
						}
						deleteAction += (deleteAction.indexOf("?") != -1) ? "&" : "?";
						deleteAction += queryString;
						return new ActionForward(ListProcessAction._SEARCH_FORWARD, deleteAction, true, false);
					}
					else {
						ejbRS = EjbUtils.getDeleteEJBResultSet(listForm.getMetadata(), request);
						ejbRS = getTableManager().updateTransaction(ejbRS);
						if (ejbRS == null) {
							throw new EJBXRuntime(getClass().getName(), "Delete Message failed.");
						}
					}
				}
			}
			_logger.trace("execute.traceExit");
			return mapping.findForward(ListProcessAction._SEARCH_FORWARD);

		}
		catch (EJBXRuntime ex) {
			_logger.error("Error looking up data in WorkQueueListProcessAction");
			_logger.error(ex);
			String errMsg = ex.getMessage();

			// translate to an i118n string
            errMsg = translateErrorMessage(request,errMsg);

			int index = errMsg.indexOf("\n");
			if (index > 0) {
				errMsg = errMsg.substring(0, index);
			}
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", errMsg));
			saveErrors(request, errors);
			return mapping.findForward(ListAction._ERROR_FORWARD);
		}
		catch (Exception ex) {
			_logger.error("Error looking up data in WorkQueueListProcessAction");
			_logger.error(ex);
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			saveErrors(request, errors);
			return mapping.findForward(ListAction._ERROR_FORWARD);
		}
	}
}
