/*
 * AggregatableListAction.java
 *
 * Created on July 31, 2003, 3:39 PM
 */

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.ejbutils.dbutils.EJBResultSet;
import java.util.Vector;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.environment.Environment;

/**
 *
 * @author  ravi
 */
public class AggregatableListAction extends BaseAction 
{
	
	/** Creates a new instance of AggregatableListAction */
	public AggregatableListAction() 
	{
	}
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception 
	{		
		BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		String subsystem = null;

		if (baseMapping.getSubsystem() == null){
			subsystem = getSubsystem(req);
		} 
		else{
			subsystem = baseMapping.getSubsystem();
		}

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() )
				.traceEnter( "execute.traceEnter" );

		try{
			super.execute(mapping, form, req, res);
			/*
			AggregatableListForm listForm = (AggregatableListForm) form;
			EditorMetaData   editorMetaData = listForm.getMetadata();

			if (editorMetaData != null){
				Environment.getInstance(subsystem).getLogFileMgr(getClass().getName())
						.logInfo("Editormetadata is not null");
			} 
			else{
				Environment.getInstance(subsystem).getLogFileMgr(getClass().getName())
						.logInfo("Editormetadata is null");
				listForm.reset(mapping, req);
				editorMetaData = listForm.getMetadata();
			}

			Vector searchable   = editorMetaData.getSearchableColumns();
			Vector aggregatable = editorMetaData.getAggregatableColumns();
			boolean lookup = (searchable == null && aggregatable == null) || 
								(EjbUtils.isLookupFilterPresent(req, editorMetaData));

			EJBResultSet ejbRS = null;
			EJBCriteria ejbCriteria = null;

			if (lookup || (listForm.getInitialLookup() == true)) {
				EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(subsystem);
				EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();

				if (listForm.getForLookup() == false){
					ejbCriteria = EjbUtils.getEJBCriteria( editorMetaData, req, true );
				} 
				else{
					ejbCriteria = EjbUtils.getEJBCriteriaForLookup( editorMetaData, req);
				}

				if (listForm.getFormInterceptor() != null){
					listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, 
							mapping, form, req, res, ejbCriteria);
				}

				//execute sql and get results
				ejbRS = tableManagerRemote.lookup( ejbCriteria);

				//EJBStatement stmt = new EJBStatement( ejbRS );
			} 
			else{
				if (listForm.getFormInterceptor() != null){
					listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, 
							mapping, form, req, res, ejbCriteria);
				}
			}


			listForm.setResultset(ejbRS);
			if (listForm.getFormInterceptor() != null){
				listForm.getFormInterceptor().afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
			}
			 */

		}
		catch (java.lang.Exception ex){
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName())
							.logError("Error looking up data in ListAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
			throw(ex);
		}

		Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() )
				.traceExit( "execute.traceExit" );
		return mapping.findForward(ListAction._SHOWLIST_FORWARD);		
	}		
}
