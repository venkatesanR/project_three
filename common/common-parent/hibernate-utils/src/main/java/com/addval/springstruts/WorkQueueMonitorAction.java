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

public class WorkQueueMonitorAction extends WorkQueueBaseAction {
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WorkQueueMonitorAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws java.lang.Exception {
		WorkQueueMonitorForm wqForm = (WorkQueueMonitorForm) form;
		try {
			Vector<WQMetaData> wqMetaDatas = lookupWqSMetaData(getWqSMetaData(), request.getSession(), getWorkQueueUtils().getQueueNames(request));
			if (wqMetaDatas != null) {
				wqMetaDatas = getWqSMetaData().queueMonitor(getWorkQueueUtils().getWQFilters(request), wqMetaDatas);
			}
			wqForm.setWqMetaDatas(wqMetaDatas);
			return mapping.findForward(BaseAction._DONE_FORWARD);
		}
		catch (EJBXRuntime ex) {
			_logger.error("Error in WorkQueueMonitorAction");
			_logger.error(ex);
			String errMsg = ex.getMessage();

			// translate to an i118n string
            errMsg = translateErrorMessage(request,errMsg);

			int index = errMsg.indexOf("\n");
			if (index > 0)
				errMsg = errMsg.substring(0, index);

			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", errMsg));
			saveErrors(request, errors);
			return mapping.findForward(BaseAction._ERROR_FORWARD);

		}
		catch (Exception ex) {
			_logger.error("Error in WorkQueueMonitorAction");
			_logger.error(ex);

			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", ResourceUtils.message(request, "error.criticalerror", "Critical System Error - Please Contact System Administrator")));
			saveErrors(request, errors);
			return mapping.findForward(BaseAction._ERROR_FORWARD);
		}
	}
}
