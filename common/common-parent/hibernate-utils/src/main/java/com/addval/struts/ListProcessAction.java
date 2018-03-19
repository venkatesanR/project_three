//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\ListProcessAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\ListProcessAction.java

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import com.addval.environment.Environment;

/**
 * Action to process a List Search Screen
 * @author AddVal Technology Inc.
 */
public class ListProcessAction extends BaseAction 
{
	
	/**
	 * @param mapping The ActionMapping used to select this instance
	 * @param form The optional ActionForm bean for this request (if any)
	 * @param req The non-HTTP request we are processing
	 * @param res The non-HTTP response we are creating
	 * @return Return an ActionForward instance describing where and how
	 * control should be forwarded, or null if the response has already
	 * been completed.
	 * @roseuid 3DCC92AF02BC
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) 
	{

            BaseActionMapping baseMapping = (BaseActionMapping) mapping;

			String subsystem = null;

			if (baseMapping.getSubsystem() == null)
			{
				subsystem = getSubsystem(req);
			} else
			{
				subsystem = baseMapping.getSubsystem();
			}

			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );

			String actionType = req.getParameter( "kindOfAction" );

			if (actionType == null) actionType = ListProcessAction._SEARCH_FORWARD;

			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).logInfo( "Start perform(" + actionType + ") . . ."  );

			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceExit" );

			return mapping.findForward(actionType);		
	}
}
