//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\WorkflowInterceptor.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.struts;

import java.io.Serializable;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WorkflowInterceptor extends Serializable
{

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @roseuid 3FCD15F103DE
	 */
	public void preFunction(ActionMapping mapping, ActionForm form, HttpServletRequest request);

}
