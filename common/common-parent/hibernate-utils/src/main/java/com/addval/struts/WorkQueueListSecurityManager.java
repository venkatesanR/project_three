package com.addval.struts;

import com.addval.struts.FormSecurityManager;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import com.addval.struts.WorkQueueListForm;

/**
 * see based on the profile
 * FormSecurityManager Interface Implementation -  Restricts the data the user can
 */
public class WorkQueueListSecurityManager implements com.addval.struts.FormSecurityManager
{

   /**
    * @param mapping
    * @param request
    * @param form
    * @roseuid 3DE26F150131
    */
   public void enforceSecurity(ActionMapping mapping, HttpServletRequest request, ActionForm form)
   {

	   WorkQueueListForm listForm = (WorkQueueListForm) form;

 	   EditorMetaData metadata = listForm.getMetadata();

 	   if (metadata != null)
 	   {
 		   for (int idx=1;idx<=metadata.getColumnCount();++idx)
 		   {
 			   ColumnMetaData colMetadata = metadata.getColumnMetaData(idx);

 			   if (colMetadata.isSearchable() && colMetadata.isSecured())
 			   {

 				   String columnValue = null;

				   //Lookfor the value in session
				   columnValue = (String) request.getAttribute(colMetadata.getName() + "_lookUp");

				   if (columnValue == null)
				   {
					   columnValue = (String) request.getAttribute(colMetadata.getName());
				   } else
				   {
					   System.out.println("Found in request = " + columnValue);
				   }


				   if (columnValue == null)
				   {
					   columnValue = (String) request.getSession().getAttribute(colMetadata.getName());
				   } else
				   {
					   System.out.println("Found in session = " + columnValue);
				   }

				   if (columnValue == null)
				   {

					   try
					   {
								javax.servlet.http.HttpSession session = request.getSession();
								java.util.Hashtable userProfile = (java.util.Hashtable)session.getAttribute("USER_PROFILE");
								if (userProfile != null) {
							   //CustomPrincipal principal = (CustomPrincipal) request.getUserPrincipal();
							   //if (principal != null)
							   //{
									columnValue = (String) userProfile.get(colMetadata.getName());
								    //columnValue = (String) principal.getProperty(colMetadata.getName());

								    java.util.Hashtable props = userProfile;
								    //java.util.Hashtable props = principal.getProperties();

								    if (props != null)
								    {
									System.out.println("Properties are: " + props.toString());
								    } else
								    {
									System.out.println("Properties is null");
								    }


 							             if (columnValue == null)
 							             {

								          System.out.println("Column value is still null" + colMetadata.getName());
								     } else
							             {

								          System.out.println("Column value is not null" + columnValue);

								     }


 							     }
 						} catch (Exception ex)
 						{

 						}

 						if (columnValue != null)
 						{
 							   request.setAttribute(colMetadata.getName() + "_lookUp", columnValue);

						       System.out.println("Column value is not null" + columnValue);

 						}

 					}
 				   }

 		   }

 	   }
   }
}
