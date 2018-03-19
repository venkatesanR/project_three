package com.addval.springstruts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.wqutils.metadata.WQMetaData;

public class WorkQueueConsoleAction extends WorkQueueBaseAction {

	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WorkQueueConsoleAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			super.execute(mapping, form, request, response);
			WorkQueueConsoleForm listForm = (WorkQueueConsoleForm) form;
			Vector<WQMetaData> wqMetaDatas = lookupWqSMetaData(getWqSMetaData(), request.getSession(), getWorkQueueUtils().getQueueNames(request));
			if (wqMetaDatas != null) {
				wqMetaDatas = getWqSMetaData().queueStatus(getWorkQueueUtils().getWQFilters(request), wqMetaDatas);
			}
			listForm.setWqMetaDatas(wqMetaDatas);
			return mapping.findForward(ListAction._SHOWLIST_FORWARD);
		}
		catch (EJBXRuntime xr) {
			_logger.error("Error in WorkQueueConsoleAction");
			_logger.error(xr);
			String errMsg = xr.getMessage();
			
			// translate to an i118n string
            errMsg = translateErrorMessage(request,errMsg);

			int index = errMsg.indexOf("\n");
			if (index > 0) {
				errMsg = errMsg.substring(0, index);
			}
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", errMsg));
			saveErrors(request, errors);
			return mapping.findForward(ListAction._SHOWLIST_FORWARD);
		}
		catch (Exception ex) {
			_logger.error("Error in WorkQueueConsoleAction");
			_logger.error(ex);
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			saveErrors(request, errors);
			return mapping.findForward(ListAction._SHOWLIST_FORWARD);
		}
	}
}
