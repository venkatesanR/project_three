//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\LookupXmlAction.java

package com.addval.struts;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.dbutils.RSIterator;
import com.addval.metadata.ColumnMetaData;
import com.addval.dbutils.RSIterAction;
import com.addval.metadata.EditorMetaData;
import java.util.Vector;
import com.addval.environment.Environment;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action to create lookup results in XML form for a lookup query
 * @author AddVal Technology Inc.
 */
public class LookupXmlAction extends LookupAction {

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param resp
	 * @return org.apache.struts.action.ActionForward
	 * @roseuid 3DD3DD2C03D1
	 */
	public org.apache.struts.action.ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse resp) {

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


	    try {



				ActionForward fwd = super.execute(mapping, form, req, resp);

				LookupForm lookupForm 					= (LookupForm) form;
				EJBResultSet    ejbRS 					= lookupForm.getResultset();
				EditorMetaData editorMetaData			= lookupForm.getMetadata();
				Vector displayable						= editorMetaData.getDisplayableColumns();
				EJBStatement    stmt    				= new EJBStatement ( ejbRS );
				com.addval.dbutils.RSIterator   rsItem  = new com.addval.dbutils.RSIterator(stmt, lookupForm.getPosition(),  new RSIterAction(lookupForm.getPagingAction()), ejbRS.getRecords().size(),  10);

				resp.setContentType("text/xml");

				StringBuffer xmlString = new StringBuffer();

				xmlString.append ( "<doc rowcount=\""+ ejbRS.getRecords().size() + "\">" );

				ColumnMetaData columnMetaData 	= null;
				String columnValue 				= null;
				String columnName 				= null;
				String attribute 				= null;

				String   selectFieldName  = lookupForm.getSelectFieldName();
				String dbselectFieldName  = selectFieldName;

				if ( selectFieldName.indexOf("_look")>0 || selectFieldName.indexOf("_edit")>0 ||
					 selectFieldName.indexOf("_search")>0 )
					dbselectFieldName = selectFieldName.substring(0,selectFieldName.lastIndexOf("_"));


				String displayFieldName    = lookupForm.getDisplayFieldName();
				String dbdisplayFieldName  = displayFieldName;

				if ( displayFieldName.indexOf("_look")>0 || displayFieldName.indexOf("_edit")>0 ||
					 displayFieldName.indexOf("_search")>0 )
					dbdisplayFieldName = displayFieldName.substring(0,displayFieldName.lastIndexOf("_"));


				while (rsItem.next())
				{
					attribute = "";
					for (int j=0; j<displayable.size(); j++)
					{
						if ((j == lookupForm.getSelectCellNo()) || (j == lookupForm.getDisplayCellNo()) )
						{
							columnMetaData 	= ( ColumnMetaData ) displayable.elementAt(j);
							columnName 		=  columnMetaData.getName();
							columnValue 	= rsItem.getString(columnName);

							if (columnValue == null)
								columnValue = "";



							if (columnName.equalsIgnoreCase(dbdisplayFieldName))
								columnName = displayFieldName;

							if (columnName.equalsIgnoreCase(dbselectFieldName))
								columnName = selectFieldName;

							attribute = attribute + columnName + "=" + "\"" + columnValue + "\" ";
						}
					}

					xmlString.append ("<row " + attribute + "/>");

			   }

			   xmlString.append("</doc>");



			   resp.getOutputStream().println(xmlString.toString());
			   resp.getOutputStream().flush();

			   Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );

   	   } catch (Exception ex)
   	   {
	      Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in LookupXmlAction");
		  Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
	   } finally
	   {
		   return null;
	   }
	}
}
