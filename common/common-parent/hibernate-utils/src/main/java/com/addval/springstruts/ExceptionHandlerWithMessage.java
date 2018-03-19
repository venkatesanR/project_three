//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\ExceptionHandlerWithMessage.java

package com.addval.springstruts;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.config.*;
import org.apache.struts.action.*;
import java.util.*;

/**
 * Global exception handler that can report the exception to the user interface
 * with the actual message text. Currently the whole exception is printed. This
 * exception handler can be attached in the struts-config.xml as shown below
 *
 *       <global-exceptions><exception type="java.lang.Exception"
 * handler="com.addval.struts.ExceptionHandlerWithMessage" key="exception.message"
 * scope="request" path="/notifyException.jsp" /></global-exceptions>
 */
public class ExceptionHandlerWithMessage extends ExceptionHandler {

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(ExceptionHandlerWithMessage.class);

	/**
	 * @return org.apache.struts.action.ActionForward
	 * @roseuid 3E205BA300B5
	 */
	public ActionForward execute() {
		return null;
	}

	/**
	 * @param ex
	 * @param config
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return org.apache.struts.action.ActionForward
	 * @throws javax.servlet.ServletException
	 * @roseuid 3E205C42008B
	 */
	public ActionForward execute(Exception ex, ExceptionConfig config, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException {

            	BaseActionMapping baseMapping = (BaseActionMapping) mapping;

	   	String subsystem = null;

		if (baseMapping.getSubsystem() == null)
		{
		       org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);

			BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();

			subsystem = baseConfig.getSubsystem();
		} else
		{
			subsystem = baseMapping.getSubsystem();
		}

		_logger.trace( "execute.traceEnter" );

		_logger.error( ex );

		String[] args = new String [1];
   		args[0] = ex.getMessage();

   		ex.printStackTrace();

   		// create a new ActionError object
   		ActionError error = new ActionError(config.getKey(), args);

   		// create action forward
   		ActionForward forward = new ActionForward(config.getPath());

   		// Store the ActionError
   		storeException(request, config.getKey(), error, forward, config.getScope());

		_logger.trace( "execute.traceEnter" );

   		return forward;
	}
}
