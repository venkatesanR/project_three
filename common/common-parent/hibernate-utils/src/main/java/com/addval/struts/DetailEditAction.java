//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\DetailEditAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\DetailEditAction.java

package com.addval.struts;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.environment.Environment;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.ejbutils.dbutils.EJBCriteria;

/**
 * The action for showing the Master/Detail Edit Screen
 * @author AddVal Technology Inc.
 */
public class DetailEditAction extends EditAction
{

	/**
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 * @roseuid 3DDD7DD9015C
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception
	{

            DetailBaseActionMapping baseMapping = (DetailBaseActionMapping) mapping;

			String subsystem = null;

			if (baseMapping.getSubsystem() == null)
			{
				subsystem = getSubsystem(req);
			} else
			{
				subsystem = baseMapping.getSubsystem();
			}

		    Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );

			ActionForward fwd = super.execute(mapping, form, req, res);


			DetailEditForm editForm = (DetailEditForm) form;


			EditorMetaData   editorMetaData = editForm.getDetailMetadata();
			EJBResultSet ejbRS = null;

			// Toggle the mutable flag
			if (editForm.isMutable() || editForm.getResultset() == null) {

				if (editForm.getKindOfAction().equals("insert"))
				{
  					if (editForm.getFormInterceptor() != null)
					{
						editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
					}

					EJBResultSetMetaData 	rsMetaData 	= new EJBResultSetMetaData( editorMetaData );

					ejbRS = new EJBResultSet( rsMetaData );
					editForm.setDetailResultset(ejbRS);

  					if (editForm.getFormInterceptor() != null)
					{
						editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
					}


				} else
				{
					EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(subsystem);
					EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();

					EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria( editorMetaData, req, false );

					if (editForm.getFormInterceptor() != null)
					{
						editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}

					ejbRS = tableManagerRemote.lookup( ejbCriteria );
					EJBStatement stmt = new EJBStatement( ejbRS );

					editForm.setDetailResultset(ejbRS);

					if (editForm.getFormInterceptor() != null)
					{
						editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}
				}
			}
			else {
				// set the mutable flag so that the next time this action is invoked it will re-read the data
				editForm.setMutable( true );
			}

		    Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );

			return mapping.findForward(EditAction._SHOWEDIT_FORWARD);
	}
}
