//Source file: d:\\projects\\Common\\src\\client\\source\\com\\addval\\struts\\ListProcessAction.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\ListProcessAction.java

package com.addval.struts;

import javax.servlet.http.*;
import javax.ejb.EJBException;

import org.apache.struts.action.*;
import com.addval.environment.Environment;
import com.addval.environment.EJBEnvironment;
import com.addval.wqutils.WQServerRemote;
import com.addval.wqutils.WQServerHome;
import com.addval.utils.XRuntime;
import com.addval.utils.WorkQueueUtils;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBCriteriaColumn;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManagerHome;
import com.addval.ejbutils.server.EJBSTableManagerRemote;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.metadata.ColumnMetaData;

import java.util.Vector;
import java.util.Iterator;
import java.rmi.RemoteException;

/**
 * Action to process a List Search Screen
 * @author AddVal Technology Inc.
 */
public class WorkQueueListProcessAction extends BaseAction
{

	/**
	 * @param mapping The ActionMapping used to select this instance
	 * @param form The optional ActionForm bean for this request (if any)
	 * @param req The non-HTTP request we are processing
	 * @param res The non-HTTP response we are creating
	 * @return Return an ActionForward instance describing where and how
	 * control should be forwarded, or null if the response has already
	 * been completed.
	 * @roseuid 3DCC92AF02BC
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) {

		BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		String subsystem = (baseMapping.getSubsystem() == null)?getSubsystem(req):baseMapping.getSubsystem();

		try {
			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceEnter( "execute.traceEnter" );

			String actionType = req.getParameter( "kindOfAction" );
			if (actionType == null) actionType = ListProcessAction._SEARCH_FORWARD;

            EJBResultSet ejbRS = null;
            WorkQueueListForm listForm = (WorkQueueListForm) form;

			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).logInfo( "Start perform(" + actionType + ") . . ."  );

            if ( actionType.endsWith( "_process" ) || actionType.endsWith( "_process_next" ) ) {

				String queueName = listForm.getQueueName();
				if(queueName != null && queueName.length() > 0) {

					String homeName = EJBEnvironment.getInstance( subsystem ).getCnfgFileMgr().getPropertyValue("queueMetaData.WQServerBeanName", "AVWQServer" );

					WQServerHome   wqServerHome   = ( WQServerHome ) EJBEnvironment.lookupEJBInterface( subsystem , homeName, WQServerHome.class );
					WQServerRemote wqServerRemote = wqServerHome.create();
					EJBCriteria ejbCriteria = null;

                    String queueCriteria = listForm.getWQMetaData().getQueueWhereClause(req);
                    if(queueCriteria != null && queueCriteria.length() > 0)
                        req.setAttribute("QUEUE_CRITERIA",queueCriteria);
                    else
                        req.setAttribute("QUEUE_CRITERIA",null);

					if ( actionType.endsWith( "_process_next" ) ) {
						ejbCriteria = EjbUtils.getEJBCriteria( listForm.getMetadata(), req, true );
                        if (listForm.getFormInterceptor() != null){
                            listForm.getFormInterceptor().beforeDataLookup(FormInterceptor._LIST_ACTION_TYPE, mapping, form, req, res, ejbCriteria);
                        }
						ejbRS = wqServerRemote.processNextMessage( ejbCriteria );
					}
					else if ( actionType.endsWith( "_process" ) ) {
						ejbCriteria = EjbUtils.getEJBCriteria( listForm.getMetadata(), req, false );
                        ejbRS = wqServerRemote.processMessage( ejbCriteria );
					}

                    if (ejbRS == null)
						throw new EJBXRuntime(getClass().getName() , "Process Message failed.");

                    WorkQueueUtils.getInstance().setAttributes(ejbRS,req);

				}
			}
            else if (actionType.equalsIgnoreCase( "delete" )) {

                String queueGroupName =  listForm.getWQMetaData().getQueueGroupName();

				// If your custom action is going to handle delete then just forward it
				if (mapping.findForwardConfig( queueGroupName + "_process_" + actionType ) == null) {

					EJBSTableManagerHome   tableManagerHome   = getTableManagerHome( subsystem );
					EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
					ejbRS = EjbUtils.getDeleteEJBResultSet( listForm.getMetadata() , req );
					ejbRS = tableManagerRemote.updateTransaction ( ejbRS );

					if (ejbRS == null) {
						throw new EJBXRuntime(getClass().getName() , "Delete Message failed.");
					}
				}
				else {

					actionType = queueGroupName + "_process_" + actionType;
				}
			}

			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).logInfo( "End perform(" + actionType + ") . . ."  );
			Environment.getInstance( subsystem ).getLogFileMgr( getClass().getName() ).traceExit( "execute.traceExit" );

			return mapping.findForward(actionType);
		}
		catch (EJBXRuntime xr) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error in WorkQueueConsoleListAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

			String errMsg = xr.getMessage();
			int index = errMsg.indexOf( "\n" );
			if (index > 0)
				errMsg = errMsg.substring( 0, index );

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", errMsg ) );
			saveErrors( req, errors );

			return mapping.findForward( ListAction._ERROR_FORWARD );
	   	}
	   	catch (Exception ex) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error in WorkQueueConsoleListAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			ActionErrors errors = new ActionErrors();
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
			saveErrors( req, errors );
			return mapping.findForward( ListAction._ERROR_FORWARD );
	  	}
	}
}
