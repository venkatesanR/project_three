//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\EditAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EditAction.java

package com.addval.springstruts;

import javax.servlet.http.*;
import org.apache.struts.action.*;

import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
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
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(EditAction.class);

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
		EditForm editForm = (EditForm) form;
		
		try{
			BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		
			_logger.trace( "execute.traceEnter" );
		
			super.execute(mapping, form, req, res);
		
			
		
			EditorMetaData   editorMetaData = editForm.getMetadata();
		
			EJBResultSet ejbRS = null;
		
			// Toggle the mutable flag
			if (editForm.isMutable() || editForm.getResultset() == null) {
				if (req.getParameter("kindOfAction") != null && req.getParameter("kindOfAction").equals("insert"))
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
					EJBCriteria ejbCriteria = EjbUtils.getEJBCriteria( editorMetaData, req, false );
					if (editForm.getFormInterceptor() != null)
					{
						editForm.getFormInterceptor().beforeDataLookup(FormInterceptor._EDIT_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
					}
					
					ejbRS = getTableManager().lookup( ejbCriteria);
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
		
			_logger.trace( "execute.traceExit" );
		
			return mapping.findForward(EditAction._SHOWEDIT_FORWARD);
			
		}
		catch( com.addval.utils.XRuntime e){
  			    _logger.error(e);
	            ActionErrors errors = new ActionErrors();
	            String errMsg = e.getMessage();
	            int index = errMsg.indexOf("\n");
	            if(index > 0)
	                errMsg = errMsg.substring(0, index);

	            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
	            saveErrors(req, errors);
	            editForm.setErrorMsg(errMsg);

	            editForm.setMutable(false);
	            if(editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
	                return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

	            return mapping.findForward(EditProcessAction._FAILED_FORWARD);
		}
		
		catch(Exception e){
			    _logger.error(e);
	            ActionErrors errors = new ActionErrors();
	            String errMsg = e.getMessage();
	            int index = errMsg.indexOf("\n");
	            if(index > 0)
	                errMsg = errMsg.substring(0, index);

	            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.invalid", errMsg));
	            saveErrors(req, errors);
	            editForm.setErrorMsg(errMsg);

	            editForm.setMutable(false);
	            if(editForm.getKindOfAction().equalsIgnoreCase("delete") && (mapping.findForwardConfig(EditProcessAction._CANCEL_FORWARD) != null))
	                return mapping.findForward(EditProcessAction._CANCEL_FORWARD);

	            return mapping.findForward(EditProcessAction._FAILED_FORWARD);
			
		}
	}
}
