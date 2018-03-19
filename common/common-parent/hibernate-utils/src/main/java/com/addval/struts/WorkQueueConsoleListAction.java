package com.addval.struts;


import com.addval.struts.BaseAction;
import org.apache.struts.action.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.addval.struts.BaseActionMapping;

import java.util.*;

import com.addval.struts.ListAction;
import com.addval.wqutils.WQSMetaDataHome;
import com.addval.wqutils.WQSMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.utils.XRuntime;
import com.addval.utils.WorkQueueUtils;
import com.addval.ejbutils.utils.EJBXRuntime;



public class WorkQueueConsoleListAction extends BaseAction
{
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws java.lang.Exception {

		BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		String subsystem = (baseMapping.getSubsystem() == null)? getSubsystem(req) : baseMapping.getSubsystem();

		try {

			super.execute( mapping, form, req, res );
		   	WorkQueueConsoleListForm listForm = (WorkQueueConsoleListForm) form;

		   	String homeName = Environment.getInstance( subsystem ).getCnfgFileMgr().getPropertyValue("queueMetaData.WQSMetaDataBeanName", "AVWQMetaDataServer" );

		   	WQSMetaDataHome   metaDataHome   = ( WQSMetaDataHome ) EJBEnvironment.lookupEJBInterface( subsystem , homeName , WQSMetaDataHome.class );
		   	WQSMetaDataRemote metaDataRemote = metaDataHome.create( );

            Vector wqMetaDatas = metaDataRemote.lookup(  WorkQueueUtils.getInstance().getQueueNames(req) );
            if(wqMetaDatas != null)
			    wqMetaDatas = metaDataRemote.queueStatus( WorkQueueUtils.getInstance().getWQFilters( req ),wqMetaDatas );
            listForm.setWqMetaDatas(wqMetaDatas) ;
			return mapping.findForward( ListAction._SHOWLIST_FORWARD );

		}
	   catch (EJBXRuntime xr) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error in WorkQueueConsoleListAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

			String errMsg = xr.getMessage();
			int index = errMsg.indexOf( "\n" );
			if (index > 0)
				errMsg = errMsg.substring( 0, index );

			ActionErrors errors = new ActionErrors();
		   	errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general",errMsg ) );
		   	saveErrors( req, errors );
		   	return mapping.findForward( ListAction._SHOWLIST_FORWARD );

	   }
	   catch (Exception ex) {

			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error in WorkQueueConsoleListAction");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

			ActionErrors errors = new ActionErrors();
		   	errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
		   	saveErrors( req, errors );
		   	return mapping.findForward( ListAction._SHOWLIST_FORWARD );

	   }
	}

}
