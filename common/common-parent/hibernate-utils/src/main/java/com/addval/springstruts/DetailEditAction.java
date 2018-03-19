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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.EditorMetaData;

/**
 * The action for showing the Master/Detail Edit Screen
 * 
 * @author AddVal Technology Inc.
 */
public class DetailEditAction extends EditAction {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(DetailEditAction.class);

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DDD7DD9015C
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		_logger.trace("execute.traceEnter");
		DetailEditForm editForm = (DetailEditForm) form;
		boolean isMutable = new Boolean(editForm.isMutable());
		ActionForward fwd = super.execute(mapping, form, req, res);
		List<EditorMetaData> detailMetadata = editForm.getDetailMetadata();
		EJBResultSet ejbRS = null;
		// Toggle the mutable flag
		if (isMutable || editForm.getResultset() == null) {
			if (editForm.getKindOfAction().equals("insert")) {
				if (editForm.getFormInterceptor() != null) {
					editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
				}
				editForm.setDetailResultset(new ArrayList<EJBResultSet>());
				for (EditorMetaData editorMetaData : editForm.getDetailMetadata()) {
					ejbRS = EjbUtils.getDetailEJBResultSet(editorMetaData, req);
					editForm.getDetailResultset().add(ejbRS);
				}
				if (editForm.getFormInterceptor() != null) {
					editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
				}
			}
			else {
				editForm.setDetailResultset(new ArrayList<EJBResultSet>());
				for (EditorMetaData editorMetaData : detailMetadata) {
					EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria(editorMetaData, req, false);
					if (editForm.getFormInterceptor() != null) {
						editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}
					ejbRS = getTableManager().lookup(ejbCriteria);
					editForm.getDetailResultset().add(ejbRS);
					if (editForm.getFormInterceptor() != null) {
						editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}
				}
			}
		}
		else {
			// set the mutable flag so that the next time this action is invoked it will re-read the data
			editForm.setMutable(true);
		}
		_logger.trace("execute.traceExit");
		return mapping.findForward(EditAction._SHOWEDIT_FORWARD);
	}
}
