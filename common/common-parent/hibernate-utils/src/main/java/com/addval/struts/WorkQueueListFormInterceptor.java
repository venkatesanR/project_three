package com.addval.struts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBCriteriaColumn;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Conveinience class that provides an implementation of all the FormInterceptor
 * methods
 * This class can be extended for custom implementations and only the required
 * hooks be implemented
 */
public class WorkQueueListFormInterceptor extends DefaultFormInterceptor
{
	// This method is added so that LAST_UPDATED_TIMESTAMP is always added to any work queue list screen

	public void beforeDataLookup(String actionType, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBCriteria ejbCriteria)
	{
		if (ejbCriteria != null) {

			ArrayList orderBy = new ArrayList();
			Vector indexes = (Vector)ejbCriteria.getColumnIndexes().get( "LAST_UPDATED_TIMESTAMP" );

			if (indexes != null && !indexes.isEmpty()) {

				EJBCriteriaColumn[] columns = ejbCriteria.getOrderBy();
				String  sortName = request.getParameter( "SORT_NAME" ) != null ? request.getParameter( "SORT_NAME" ) : request.getParameter( "sort_Name" ) != null ? request.getParameter( "sort_Name" ) : "";

				boolean addTimestampToBegin = sortName.trim().length() == 0;

				/* Removed adding timestamp to the begining and instead basing
				/* on the sortName in the request


				for (int i = 0; i < size; i++) {
					if (columns[i].getOrdering() == com.addval.utils.AVConstants._DESC)
					{
						addTimestampToBegin = false;
						break;
					}
				}
				*/

				Integer index = (Integer)indexes.firstElement();

				// Add only if none of the order by columns are desc
				if (addTimestampToBegin)
            		orderBy.add( (EJBCriteriaColumn)(ejbCriteria.getColumns().elementAt( index.intValue() )) );

				int size = columns != null ? columns.length : 0;
				for (int i = 0; i < size; i++)
					orderBy.add( columns[i] );

				// Always add last_updated_timestamp to the end
				orderBy.add( (EJBCriteriaColumn)(ejbCriteria.getColumns().elementAt( index.intValue() )) );

				ejbCriteria.setOrderBy( (EJBCriteriaColumn[])orderBy.toArray( new EJBCriteriaColumn[size + 1] ) );
			}
		}
	}

}