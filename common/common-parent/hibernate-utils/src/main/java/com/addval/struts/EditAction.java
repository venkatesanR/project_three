//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\EditAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EditAction.java

package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.dbutils.EJBStatement;
import java.util.Vector;
import com.addval.environment.Environment;
import com.addval.ejbutils.dbutils.EJBCriteria;

/**
 * The action for showing the Edit Screen
 * @author AddVal Technology Inc.
 */
public class EditAction extends BaseAction
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
	 * @roseuid 3DCC93450073
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

		super.execute(mapping, form, req, res);

		EditForm editForm = (EditForm) form;

		EditorMetaData   editorMetaData = editForm.getMetadata();
		EJBResultSet ejbRS = null;

		// Toggle the mutable flag
		if (editForm.isMutable() || editForm.getResultset() == null) {

			if (editForm.getKindOfAction().equals("insert"))
			{
				EJBResultSetMetaData 	rsMetaData 	= new EJBResultSetMetaData( editorMetaData );

				if (editForm.getFormInterceptor() != null)
				{
					editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
				}

				ejbRS = new EJBResultSet( rsMetaData );
				ejbRS.insertRow();
				ejbRS.beforeFirst();

				editForm.setResultset(ejbRS);

				if (editForm.getFormInterceptor() != null)
				{
					editForm.getFormInterceptor().afterDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, null);
				}
			}
			else
			{
				EJBSTableManagerHome   tableManagerHome   = getTableManagerHome(subsystem);
				EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();

				EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria( editorMetaData, req, false );

				if (editForm.getFormInterceptor() != null)
				{
					editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
				}

				ejbRS = tableManagerRemote.lookup( ejbCriteria);
				EJBStatement stmt = new EJBStatement( ejbRS );

				editForm.setResultset(ejbRS);

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
