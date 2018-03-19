//Source file: D:\\projects\\COMMON\\src\\com\\addval\\struts\\FormInterceptor.java

//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\FormInterceptor.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\FormSecurityManager.java

package com.addval.springstruts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import java.io.Serializable;
import org.apache.struts.action.ActionErrors;

/**
 * Classes implementing the FormInterceptor Interface can be attached to a
 * ListForm, EditForm  through struts-config.xml.
 * The implementing classes can modify the criteria or results from an action
 * before display
 */
public interface FormInterceptor extends Serializable
{
	public static final String _LIST_ACTION_TYPE = "LIST";
	public static final String _EDIT_ACTION_TYPE = "EDIT";

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
	 * @roseuid 3F07B6340115
	 */
	public void beforeDataProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBResultSet ejbRS);

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
	 * @roseuid 3F07B6340166
	 */
	public void afterDataProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBResultSet ejbRS);

	/**
	 * This interface method is called just before data lookup in list form or edit
	 * form
	 * EditorMetaData is available in the form.
	 * EJBCriteria from the request is passed into the method. This argument may be
	 * null
	 * This interface can be used to modify the lookup criteria
	 *
	 * @param actionType
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param ejbCriteria
	 * @roseuid 3F07B6C903C3
	 */
	public void beforeDataLookup(String actionType, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBCriteria ejbCriteria);

	/**
	 * This interface method is called just after data lookup before display in jsp of
	 * list form or edit form
	 * EditorMetaData is available in the form.
	 * EJBResultSet is available in the form
	 * EJBCriteria from the request is passed into the method. This argument may be
	 * null
	 * This interface can be used to modify the results after the lookup
	 *
	 * @param actiontype
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @param ejbCriteria
	 * @roseuid 3F07B6CA003F
	 */
	public void afterDataLookup(String actiontype, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBCriteria ejbCriteria);

	/**
	 * @param mapping
	 * @param errors
	 * @param ejbRS
	 * @roseuid 3F61F66B0140
	 */
	public void customValidation(ActionMapping mapping, ActionErrors errors, EJBResultSet ejbRS);
}
