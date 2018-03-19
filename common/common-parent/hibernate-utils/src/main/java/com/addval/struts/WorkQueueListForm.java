
package com.addval.struts;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.LabelValueBean;
import com.addval.environment.Environment;
import com.addval.ejbutils.server.EJBSEditorMetaDataHome;
import com.addval.ejbutils.server.EJBSEditorMetaDataRemote;
import com.addval.environment.EJBEnvironment;
import com.addval.dbutils.RSIterAction;
import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.wqutils.WQSMetaDataHome;
import com.addval.wqutils.WQSMetaDataRemote;
import com.addval.wqutils.metadata.WQMetaData;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;

import com.addval.utils.WorkQueueUtils;

public class WorkQueueListForm extends ListForm
{
	private String queueName = null;
	private WQMetaData wqMetaData = null;
	private ArrayList queueNames = null;

	public ArrayList getQueueNames() {
		return queueNames;
	}

	public void setQueueNames(ArrayList queueNames) {
		this.queueNames = queueNames;
	}

	public WQMetaData getWQMetaData() {
		return wqMetaData;
	}
	public void setWQMetaData(WQMetaData wqMetaData) {
		this.wqMetaData = wqMetaData;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}


	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);
		BaseFormBeanConfig   formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());
		String subsystem = formConfig.getSubsystem();

		if (subsystem == null || subsystem.equals("")) {
			BaseControllerConfig baseConfig = (BaseControllerConfig) appConfig.getControllerConfig();
		 	subsystem = baseConfig.getSubsystem();
	 	}

		try {

			String queueName = getQueueName();
			if(queueName == null) {

				queueName = request.getParameter("queueName");
				setQueueName( queueName );

			}

			queueNames = new ArrayList();

			if(queueName != null && queueName.length() > 0) {

				WQSMetaDataHome   metaDataHome   = ( WQSMetaDataHome ) EJBEnvironment.lookupEJBInterface( subsystem , "AVWQMetaDataServer" , WQSMetaDataHome.class );
				WQSMetaDataRemote metaDataRemote = metaDataHome.create( );
				Vector wqMetaDatas = metaDataRemote.lookup( WorkQueueUtils.getInstance().getQueueNames( request ) );

				WQMetaData wqMetaData = null;
				WQMetaData wqMetaDataTemp = null;

				for(Iterator iterator = wqMetaDatas.iterator(); iterator.hasNext();) {

					wqMetaDataTemp = (WQMetaData) iterator.next();
					if(wqMetaDataTemp.getQueueName().equals(queueName) ) {
						wqMetaData = wqMetaDataTemp;
					}

					queueNames.add( new LabelValueBean( wqMetaDataTemp.getQueueDesc(), wqMetaDataTemp.getQueueName() ) );
				}

				if(wqMetaData!= null && wqMetaData.getEditorMetaData().getName() != null ) {

					formConfig.setEditorName( wqMetaData.getEditorMetaData().getName() );
					String queueCriteria = wqMetaData.getQueueWhereClause(request);
					if(queueCriteria != null && queueCriteria.length() > 0)
						request.setAttribute("QUEUE_CRITERIA",queueCriteria);

				}
				setWQMetaData(wqMetaData);
			}

			//System.out.println( "Calling Super" );

			superReset (mapping,request);

		}
		catch(Exception ex) {
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError("Error looking up data in WorkQueueListForm");
			Environment.getInstance(subsystem).getLogFileMgr(getClass().getName()).logError(ex);
		}
	}

	protected void superReset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping,request);
	}
}
