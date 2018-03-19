/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.api;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.wqutils.metadata.WQMetaData;

public interface WQSMetaData {

	public WQMetaData lookup(String queue) throws RemoteException, EJBXRuntime;

	public Vector<WQMetaData> lookup(Vector<String> queueCollection) throws RemoteException, EJBXRuntime;

	public Vector<WQMetaData> queueStatus(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection) throws RemoteException, EJBXRuntime;

	public String getVerifiedQueueName(String queue) throws RemoteException, EJBXRuntime;

	/**
	 * Monitor the queue for any new messages. Look for any unprocessed messages
	 * with dwell time <= <dwell time threshold>
	 */

	public Vector<WQMetaData> queueMonitor(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection) throws RemoteException, EJBXRuntime;

	public List<WQMetaData> getQueueSummary() throws RemoteException, EJBXRuntime;   
	
	public List<WQMetaData> getQueueSummary(String queueName) throws RemoteException, EJBXRuntime;
	
	public WQMetaData lookup(String queue, String userName) throws RemoteException, EJBXRuntime;
	
	public Vector<WQMetaData> lookup(Vector<String> queueCollection, String userName) throws RemoteException, EJBXRuntime;
	
	public Vector<WQMetaData> queueStatus(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection, String userName) throws RemoteException, EJBXRuntime;
	
	public List<WQMetaData> getAllQueueSummary(String userName) throws RemoteException, EJBXRuntime;   
		
	public List<WQMetaData> getQueueSummary(String queueName,String userName) throws RemoteException, EJBXRuntime;
	
	public void setEnvironmentInstances(Map<String,Environment> envInstances) throws RemoteException;
}