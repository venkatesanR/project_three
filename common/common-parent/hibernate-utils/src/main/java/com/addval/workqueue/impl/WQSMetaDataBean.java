/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.impl;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ejb.support.AbstractStatelessSessionBean;

import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.springutils.ServerRegistry;
import com.addval.workqueue.api.WQSMetaData;
import com.addval.wqutils.metadata.WQMetaData;

public class WQSMetaDataBean extends AbstractStatelessSessionBean implements WQSMetaData, java.io.Serializable 
{
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WQSMetaDataBean.class);
	private static final long serialVersionUID = 9091042016973148513L;
	private WQSMetaData _wqSMetaData;

	/**
	 * Override default BeanFactoryLocator implementation
	 * 
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext sessionContext) {
		super.setSessionContext(sessionContext);
		ServerRegistry registry = new ServerRegistry();
		setBeanFactoryLocator(registry.getBeanFactoryLocator());
		setBeanFactoryLocatorKey(WQServerConstants.PRIMARY_CONTEXT_ID);
	}

	/**
	 * Obtain our POJO service object from the BeanFactory/ApplicationContext
	 * 
	 * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()
	 */
	
	protected void onEjbCreate() throws CreateException {
		_wqSMetaData = (WQSMetaData) getBeanFactory().getBean(WQServerConstants.WQSMETADATA_BEAN_ID);
		try {
			setEnvironmentInstances(BeanFactoryUtils.beansOfTypeIncludingAncestors((ClassPathXmlApplicationContext) getBeanFactory(), Environment.class));
		}
		catch (RemoteException ex) {
			_logger.error("Error while lookup Environment spring bean instances..");
			_logger.error(ex);
		}
	}

	public WQMetaData lookup(String queue) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.lookup(queue);
	}

	public Vector<WQMetaData> lookup(Vector<String> queueCollection) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.lookup(queueCollection);
	}

	public Vector<WQMetaData> queueStatus(Hashtable filter, Vector<WQMetaData> wqMetaDatas) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.queueStatus(filter, wqMetaDatas);
	}

	public String getVerifiedQueueName(String queue) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.getVerifiedQueueName(queue);
	}

	/**
	 * Monitor the queue for any new messages. Look for any unprocessed messages
	 * with dwell time <= <dwell time threshold>
	 */

	public Vector<WQMetaData> queueMonitor(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.queueMonitor(filter,wqMetaDataCollection);
	}

	public List<WQMetaData> getQueueSummary() throws RemoteException, EJBXRuntime 
	{
		return _wqSMetaData.getQueueSummary();
	}
	
	public List<WQMetaData> getQueueSummary(String queueName) throws RemoteException, EJBXRuntime 
	{
		return _wqSMetaData.getQueueSummary( queueName );
	}
	
	public WQMetaData lookup(String queue, String username) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.lookup(queue, username);
	}

	public Vector<WQMetaData> lookup(Vector<String> queueCollection, String username) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.lookup(queueCollection, username);
	}

	public Vector<WQMetaData> queueStatus(Hashtable filter, Vector<WQMetaData> wqMetaDatas, String username) throws RemoteException, EJBXRuntime {
		return _wqSMetaData.queueStatus(filter, wqMetaDatas, username);
	}
	
	public List<WQMetaData> getAllQueueSummary(String username) throws RemoteException, EJBXRuntime 
	{
		return _wqSMetaData.getAllQueueSummary(username);
	}

	public List<WQMetaData> getQueueSummary(String queueName, String username) throws RemoteException, EJBXRuntime 
	{
		return _wqSMetaData.getQueueSummary( queueName, username );
	}
	
	public void setEnvironmentInstances(Map<String, Environment> envInstances) throws RemoteException {
		_wqSMetaData.setEnvironmentInstances(envInstances);
	}
}