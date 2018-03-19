//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\ListAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\ListAction.java

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
 * Action to create a List Search Screen
 * @author AddVal Technology Inc.
 */
public class ListAction extends BaseAction
{

	/**
	 * @param mapping The ActionMapping used to select this instance
	 * @param form The optional ActionForm bean for this request (if any)
	 * @param req The non-HTTP request we are processing
	 * @param res The non-HTTP response we are creating
	 * @return Return an ActionForward instance describing where and how
	 * control should be forwarded, or null if the response has already
	 * been completed.
	 * @throws java.lang.Exception
	 * @roseuid 3DCC92FC01CD
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception
	{

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

	            super.execute(mapping, form, req, res);

				ListForm listForm = (ListForm) form;

				EditorMetaData   editorMetaData = listForm.getMetadata();

				if (editorMetaData != null)
				{
			        Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("Editormetadata is not null");
		        } else
		        {
			        Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logInfo("Editormetadata is null");

		    		listForm.reset(mapping, req);

		    		editorMetaData = listForm.getMetadata();
				}

				Vector searchable   = editorMetaData.getSearchableColumns();
				Vector aggregatable = editorMetaData.getAggregatableColumns();
				boolean initialLookup = (searchable == null && aggregatable == null) || (EjbUtils.isLookupFilterPresent(req, editorMetaData));

				EJBResultSet ejbRS = null;
				EJBCriteria ejbCriteria = null;

				if (initialLookup || listForm.getInitialLookup() || editorMetaData.isInitialLookup()) {
					EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(subsystem);

					EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();

					if (listForm.getForLookup() == false) {
						req.setAttribute("exactSearch",new Boolean(listForm.isExactSearch()));
						ejbCriteria = EjbUtils.getEJBCriteria( editorMetaData, req, true );

					}
                    else {
						ejbCriteria = EjbUtils.getEJBCriteriaForLookup( editorMetaData, req);
					}

					if (listForm.getFormInterceptor() != null)
					{
						listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}

					ejbRS = tableManagerRemote.lookup( ejbCriteria);

					EJBStatement stmt = new EJBStatement( ejbRS );

				} else
				{
					if (listForm.getFormInterceptor() != null)
					{
						listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}
				}


				listForm.setResultset(ejbRS);

				if (listForm.getFormInterceptor() != null)
				{
					listForm.getFormInterceptor().afterDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
				}

			}
			catch (java.lang.Exception ex)
			{
				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in ListAction");
				     Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
					 throw(ex);
        	}

			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );
			return mapping.findForward(ListAction._SHOWLIST_FORWARD);
	}
}
