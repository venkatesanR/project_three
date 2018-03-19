//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\DefaultFormInterceptor.java

//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\DefaultFormInterceptor.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\FormSecurityManager.java

package com.addval.struts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import org.apache.struts.action.ActionErrors;

/**
 * Conveinience class that provides an implementation of all the FormInterceptor
 * methods
 * This class can be extended for custom implementations and only the required
 * hooks be implemented
 */
public class DefaultFormInterceptor implements FormInterceptor
{

	/**
	 * This interface method is called just before data processing an Edit request
	 * from client
	 * EditorMetaData is available in the form.
	 * EJBResultSet populated from the request is passed into the interface method
	 * This interface can be used to modify the data before it is saved
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param ejbRS
	 * @roseuid 3F07B624014E
	 */
	public void beforeDataProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBResultSet ejbRS)
	{

	}

	/**
	 * This interface method is called just after data processing an Edit request from
	 * client
	 * EditorMetaData is available in the form.
	 * EJBResultSet populated from the request and saved is passed into the interface
	 * method
	 * This interface can be used to perform some action as a result of the data
	 * modification
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param ejbRS
	 * @roseuid 3F07B6240195
	 */
	public void afterDataProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBResultSet ejbRS)
	{

	}

	/**
	 * This interface method is called just before data lookup in list form or edit
	 * form
	 * EditorMetaData is available in the form.
	 * EJBCriteria from the request is passed into the method.
	 * This interface can be used to modify the lookup criteria
	 *
	 * @param actionType
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param ejbCriteria
	 * @roseuid 3F07B6BC00FD
	 */
	public void beforeDataLookup(String actionType, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBCriteria ejbCriteria)
	{

	}

	/**
	 * This interface method is called just after data lookup before display in jsp of
	 * list form or edit form
	 * EditorMetaData is available in the form.
	 * EJBResultSet is available in the form
	 * EJBCriteria from the request is passed into the method.
	 * This interface can be used to modify the results after the lookup
	 *
	 * @param actionType
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param ejbCriteria
	 * @roseuid 3F07B6BC014D
	 */
	public void afterDataLookup(String actionType, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBCriteria ejbCriteria)
	{

	}

	/**
	 * @param mapping
	 * @param errors
	 * @param ejbRS
	 * @roseuid 3F6214120117
	 */
	public void customValidation(ActionMapping mapping, ActionErrors errors, EJBResultSet ejbRS)
	{

	}
}
