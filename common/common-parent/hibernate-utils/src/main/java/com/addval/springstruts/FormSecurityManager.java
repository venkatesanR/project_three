//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\FormSecurityManager.java

package com.addval.springstruts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;

/**
 * Classes implementing the FormSecurityManager Interface can be attached to a
 * ListForm, EditForm  through struts-config.xml.
 *
 * The implementing classes can modify the meta-data to implement data-based
 * security requirements
 */
public interface FormSecurityManager extends java.io.Serializable

{

	/**
	 * The enforce security method will be called from ListAction and EditAction. This
	 * method can then modify the metadata specifically the secured attribute of the
	 * Column Metadata. When the secured attribute of the column metadata is set then
	 * the tag libraries make that field a readonly field.
	 *
	 * Implementers of this interface can use the com.addval.security.CustomPrincipal
	 * to retrieve relevant properties associate with the principal. These properties
	 * can then be used to restrict the data that the user can see/edit using the
	 * ListForm and EditForm
	 * @param mapping
	 * @param request
	 * @param metadata
	 * @roseuid 3DE265F700A8
	 */
	public void enforceSecurity(ActionMapping mapping, HttpServletRequest request, ActionForm metadata);
}
