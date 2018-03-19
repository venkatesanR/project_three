//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\ListAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\ListAction.java

package com.addval.springstruts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

/**
 * Action to create a List Search Screen
 *
 * @author AddVal Technology Inc.
 */

public class ListAction extends BaseAction {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(ListAction.class);

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
	 * @roseuid 3DCC92FC01CD
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		_logger.trace("execute.traceEnter");
		ActionErrors errors = new ActionErrors();

		try {
			super.execute(mapping, form, req, res);
			ListForm listForm = (ListForm) form;
			EditorMetaData editorMetaData = listForm.getDisplayMetadata();
			Vector searchable = editorMetaData.getSearchableColumns();
			Vector aggregatable = editorMetaData.getAggregatableColumns();
			// boolean initialLookup = (searchable == null && aggregatable == null) || (EjbUtils.isLookupFilterPresent(req, editorMetaData));
			boolean initialLookup = (searchable == null && aggregatable == null);
			String kindOfAction = req.getParameter("kindOfAction");
			boolean isApplyFilter = "ApplyFilter".equalsIgnoreCase(kindOfAction);

			EJBResultSet ejbRS = null;
			EJBCriteria ejbCriteria = null;

			if (isApplyFilter && listForm.getTemplateAdvSearchColumns() != null && listForm.getTemplateAdvSearchColumns().size() > 0) {
				listForm.setSearchColumns(listForm.getTemplateAdvSearchColumns());
			}

			if (initialLookup || listForm.getInitialLookup() || editorMetaData.isInitialLookup()) {
				req.setAttribute("isLookupForm", listForm.getForLookup());
				if (listForm.getForLookup() == false) {
					req.setAttribute("exactSearch", new Boolean(listForm.isExactSearch()));
					if (listForm.getSearchColumns() != null) {
						String columnValue = null;
						for (SearchColumn searchColumn : listForm.getSearchColumns()) {
							columnValue = searchColumn.getColumnValue();
							if (StrUtl.isEmptyTrimmed(columnValue)) {
								columnValue = searchColumn.getColumnOption(); // dropdown value.
							}
							if (!StrUtl.isEmptyTrimmed(searchColumn.getColumnName()) && !StrUtl.isEmptyTrimmed(columnValue)) {
								req.setAttribute(searchColumn.getColumnName() + "_lookUp", columnValue);
								req.setAttribute(searchColumn.getColumnName() + "operator_lookUp", searchColumn.getColumnOperator());
							}
						}
					}
					if ("preview".equalsIgnoreCase(kindOfAction)) {
						String previewId = listForm.getPreviewId();
						if (!StrUtl.isEmptyTrimmed(previewId)) {
							String keyvalues[] = StringUtils.split(previewId, "|");
							String keyvalue[] = null;
							String columnName = null;
							for (int i = 0; i < keyvalues.length; i++) {
								keyvalue = StringUtils.split(keyvalues[i], ":");
								if (keyvalue.length == 2) {
									columnName = keyvalue[0];
									if (columnName.endsWith("_KEY")) {
										columnName = columnName.toUpperCase().substring(0, columnName.lastIndexOf("_KEY"));
									}
									req.setAttribute(columnName + "_preview", keyvalue[1]);
								}
							}
						}
					}
					ejbCriteria = EjbUtils.getEJBCriteria(listForm.getMetadata(), listForm.getDisplayMetadata(), req, true);
				}
				else {
					ejbCriteria = EjbUtils.getEJBCriteria(listForm.getMetadata(), listForm.getDisplayMetadata(), req, true);
				}
				if (listForm.getFormInterceptor() != null) {
					listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
				}
				ejbRS = getTableManagerBean(ejbCriteria).lookup(ejbCriteria);
			}
			else {
				if (listForm.getFormInterceptor() != null) {
					listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
				}
			}
			listForm.setResultset(ejbRS);
			if (listForm.getFormInterceptor() != null) {
				listForm.getFormInterceptor().afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
			}
		}
		catch (XRuntime xr) {
			String errMsg = xr.getMessage();
			if (!StrUtl.isEmptyTrimmed(errMsg)) {
				for (String err : errMsg.split("NEWLINE")) {
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", err));
				}
			}
			saveErrors(req, errors);
			return mapping.findForward(ListAction._SHOWLIST_FORWARD);
		}
		catch (java.lang.Exception ex) {
			_logger.error("Error looking up data in ListAction");
			_logger.error(ex);
			throw (ex);

		}
		_logger.trace("execute.traceExit");
		return mapping.findForward(ListAction._SHOWLIST_FORWARD);
	}
}
