//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\InitialListForm.java

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;

/**
 * <b>ActionForm This form is not longer needed. You can now configure a ListForm 
 * and set the InitialList property to be true or false instead </b>
 */
public class InitialListForm extends ListForm {
	
	/**
	 * @param mapping
	 * @param request
	 * @roseuid 3E205C200230
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		super.reset(mapping, request);

		setInitialLookup(true);		
	}
}
