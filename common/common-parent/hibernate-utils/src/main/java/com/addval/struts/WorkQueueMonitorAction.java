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
import com.addval.wqutils.metadata.WQMetaData;



public class WorkQueueMonitorAction extends BaseAction
{
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws java.lang.Exception {
        WorkQueueMonitorForm wqForm = (WorkQueueMonitorForm) form;
        BaseActionMapping baseMapping = (BaseActionMapping) mapping;
		String subsystem = (baseMapping.getSubsystem() == null)? getSubsystem(request) : baseMapping.getSubsystem();

        try{

            String homeName = Environment.getInstance( subsystem ).getCnfgFileMgr().getPropertyValue("queueMetaData.WQSMetaDataBeanName", "AVWQMetaDataServer" );
            WQSMetaDataHome   metaDataHome   = ( WQSMetaDataHome ) EJBEnvironment.lookupEJBInterface( subsystem , homeName , WQSMetaDataHome.class );
            WQSMetaDataRemote metaDataRemote = metaDataHome.create( );

            Vector wqMetaDatas = metaDataRemote.lookup(  WorkQueueUtils.getInstance().getQueueNames( request ) );
            if(wqMetaDatas != null) {
                wqMetaDatas = metaDataRemote.queueMonitor( WorkQueueUtils.getInstance().getWQFilters( request ) ,wqMetaDatas );
            }
            wqForm.setWqMetaDatas(wqMetaDatas) ;
            return mapping.findForward( BaseAction._DONE_FORWARD );

        }
        catch (EJBXRuntime xr) {

            Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error in WorkQueueMonitorAction");
            Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(xr);

            String errMsg = xr.getMessage();
            int index = errMsg.indexOf( "\n" );
            if (index > 0)
                errMsg = errMsg.substring( 0, index );

            ActionErrors errors = new ActionErrors();
            errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general",errMsg ) );
            saveErrors( request, errors );
            return mapping.findForward( BaseAction._ERROR_FORWARD  );

       }
       catch (Exception ex) {

            Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error in WorkQueueConsoleListAction");
            Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);

            ActionErrors errors = new ActionErrors();
            errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.general", "Critical System Error - Please Contact System Administrator") );
            saveErrors( request, errors );
            return mapping.findForward( BaseAction._ERROR_FORWARD  );
       }
    }
}
