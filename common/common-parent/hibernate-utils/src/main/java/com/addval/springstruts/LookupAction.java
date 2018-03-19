//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\LookupAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\LookupAction.java

package com.addval.springstruts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;

/**
 * Action to create a Lookup Screen
 * 
 * @author AddVal Technology Inc.
 */
public class LookupAction extends ListAction {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(LookupAction.class);

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param resp
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DD2EDE903C2
	 */
	public org.apache.struts.action.ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		_logger.trace("execute.traceEnter");
		LookupForm lkpForm = (LookupForm) form;

		EditorMetaData editorMetaData = lkpForm.getMetadata();
		if (editorMetaData == null) {
			editorMetaData = getEditorMetadataServer().lookup(lkpForm.getEditorName(), lkpForm.getEditorType());
			lkpForm.setMetadata(editorMetaData);
		}
		req.setAttribute("exactSearch", new Boolean(editorMetaData.isExactMatchLookup()));

		ActionForward fwd = super.execute(mapping, form, req, resp);

		Vector displayableColumns = editorMetaData.getDisplayableColumns();
		String fieldName = lkpForm.getDisplayFieldName();
		String originalFieldName = fieldName;
		if (fieldName.indexOf("_look") > 0 || fieldName.indexOf("_edit") > 0 || fieldName.indexOf("_search") > 0)
			originalFieldName = fieldName.substring(0, fieldName.lastIndexOf("_"));

		ColumnMetaData columnMetaData = null;
		int cellNo = 0;
		for (int index = 0; index < displayableColumns.size(); index++) {
			columnMetaData = (ColumnMetaData) displayableColumns.get(index);
			if (columnMetaData.getName().equalsIgnoreCase(originalFieldName)) {
				cellNo = index;
				break;
			}
		}
		lkpForm.setSelectCellNo(cellNo);
		_logger.trace("execute.traceExit");
		return fwd;
	}
}
