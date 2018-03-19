package com.addval.springstruts;

import javax.servlet.http.*;
import org.apache.struts.action.*;

/**
 * Action to process a List Search Screen
 * 
 * @author AddVal Technology Inc.
 */
public class ListProcessAction extends BaseAction {
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(ListProcessAction.class);

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
	 * @roseuid 3DCC92AF02BC
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) {
		_logger.trace("execute.traceEnter");
		String kindOfAction = req.getParameter("kindOfAction");
		if (kindOfAction == null || 
				"paging".equalsIgnoreCase(kindOfAction) || 
				"preview".equalsIgnoreCase(kindOfAction)||
				"ApplyTemplate".equalsIgnoreCase(kindOfAction)||
				"SaveFilter".equalsIgnoreCase(kindOfAction)||
				"ApplyFilter".equalsIgnoreCase(kindOfAction)||
				"DeleteFilter".equalsIgnoreCase(kindOfAction)||
				"update".equalsIgnoreCase(kindOfAction)				
				) {
			kindOfAction = ListProcessAction._SEARCH_FORWARD;
		}
		_logger.info("Start perform(" + kindOfAction + ") . . .");
		_logger.trace("execute.traceExit");
		return mapping.findForward(kindOfAction);
	}
}
