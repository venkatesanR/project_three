/**
 * Copyright
 * Accenture
 */
package com.addval.workqueue.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJBException;

import org.apache.log4j.Logger;

import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.utils.LogMgr;
import com.addval.workqueue.api.WQSMetaData;
import com.addval.wqutils.metadata.WQMetaData;

public class WQSMetaDataUtility implements WQSMetaData {
	private static transient final Logger _logger = LogMgr.getLogger(WQSMetaDataUtility.class);

	private WQDAO _dao = null;

	public void setDao(WQDAO dao) {
		_dao = dao;
	}

	public WQDAO getDao() {
		return _dao;
	}

	public WQMetaData lookup(String queue) throws RemoteException, EJBXRuntime {
		try {
			if (queue == null || queue.trim().length() < 1) {
				throw new EJBXRuntime("Queue Name / No should not be empty.");
			}
			getDao().validateQueue(queue);
			return getDao().getWQMetaData(queue);
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	public Vector<WQMetaData> lookup(Vector<String> queueCollection) throws RemoteException, EJBXRuntime {
		try {
			if (queueCollection == null || queueCollection.size() == 0) {
				return null;
			}
			Vector<WQMetaData> wqMetaDatas = new Vector<WQMetaData>();
			WQMetaData wqMetaData = null;
			String queue = null;
			for (int i = 0; i < queueCollection.size(); i++) {
				queue = queueCollection.get(i);
				wqMetaData = lookup(queue);
				wqMetaDatas.add(wqMetaData);
			}
			return wqMetaDatas;
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	public Vector<WQMetaData> queueStatus(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection) throws RemoteException, EJBXRuntime {
		try {
			if (wqMetaDataCollection == null || wqMetaDataCollection.size() == 0) {
				throw new EJBXRuntime("WQMetaData Collection should not be empty.");
			}
			WQMetaData wqMetaData = null;
			for (int i = 0; i < wqMetaDataCollection.size(); i++) {
				wqMetaData = wqMetaDataCollection.get(i);
				getDao().populateQueueStatus(filter, wqMetaData);
			}
			return wqMetaDataCollection;
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	public String getVerifiedQueueName(String queue) throws RemoteException, EJBXRuntime {
		try {
			if (queue == null || queue.trim().length() < 1) {
				throw new EJBXRuntime("Queue Name / No should not be empty.");
			}
			return getDao().validateQueue(queue);
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	/**
	 * Monitor the queue for any new messages. Look for any unprocessed messages with dwell time <= <dwell time threshold>
	 */

	public Vector<WQMetaData> queueMonitor(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection) throws RemoteException, EJBXRuntime {
		try {
			if (wqMetaDataCollection == null || wqMetaDataCollection.isEmpty())
				throw new EJBXRuntime("WQMetaData Collection should not be empty.");
			WQMetaData wqMetaData = null;
			for (int i = 0; i < wqMetaDataCollection.size(); i++) {
				wqMetaData = wqMetaDataCollection.get(i);
				if (wqMetaData.getQueueNewMessageTime() >= 0) {
					getDao().populateNewMessageCount(filter, wqMetaData);
				}
			}
			return wqMetaDataCollection;
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	protected List<WQMetaData> getQueueSummary(List<String> queueNames) throws RemoteException, EJBXRuntime {
		if (queueNames == null || queueNames.isEmpty())
			return null;
		Vector<WQMetaData> metaData = lookup(new Vector<String>(queueNames));
		return queueStatus(null, metaData);
	}

	public List<WQMetaData> getQueueSummary(String queueName) throws RemoteException, EJBXRuntime {
		if (queueName == null || queueName.trim().equalsIgnoreCase(""))
			return getQueueSummary();
		List<String> names = new ArrayList<String>();
		names.add(queueName);
		return getQueueSummary(names);
	}

	public List<WQMetaData> getQueueSummary() throws RemoteException, EJBXRuntime {
		List<String> queueNames = getDao().getQueueNames();
		return getQueueSummary(queueNames);
	}

	public WQMetaData lookup(String queue, String username) throws RemoteException, EJBXRuntime {
		try {
			if (queue == null || queue.trim().length() < 1) {
				throw new EJBXRuntime("Queue Name / No should not be empty.");
			}
			getDao().validateQueue(queue);
			getDao().validateQueueForUser(queue, username);
			return getDao().getWQMetaData(queue);
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	public Vector<WQMetaData> lookup(Vector<String> queueCollection, String username) throws RemoteException, EJBXRuntime {
		try {
			if (queueCollection == null || queueCollection.size() == 0) {
				return null;
			}
			Vector<WQMetaData> wqMetaDatas = new Vector<WQMetaData>();
			WQMetaData wqMetaData = null;
			String queue = null;
			for (int i = 0; i < queueCollection.size(); i++) {
				queue = queueCollection.get(i);
				wqMetaData = lookup(queue, username);
				wqMetaDatas.add(wqMetaData);
			}
			return wqMetaDatas;
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	public Vector<WQMetaData> queueStatus(Hashtable filter, Vector<WQMetaData> wqMetaDataCollection, String username) throws RemoteException, EJBXRuntime {
		try {
			if (wqMetaDataCollection == null || wqMetaDataCollection.size() == 0) {
				throw new EJBXRuntime("WQMetaData Collection should not be empty.");
			}
			WQMetaData wqMetaData = null;
			for (int i = 0; i < wqMetaDataCollection.size(); i++) {
				wqMetaData = wqMetaDataCollection.get(i);
				// getDao().populateQueueStatus(filter,wqMetaData);
				getDao().populateQueueStatusForUser(filter, wqMetaData, username);
			}
			return wqMetaDataCollection;
		}
		catch (EJBXRuntime ex) {
			_logger.error(ex);
			throw ex;
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
	}

	protected List<WQMetaData> getQueueSummary(List<String> queueNames, String username) throws RemoteException, EJBXRuntime {
		if (queueNames == null || queueNames.isEmpty())
			return null;
		Vector<WQMetaData> metaData = lookup(new Vector<String>(queueNames), username);
		return queueStatus(null, metaData, username);
	}

	public List<WQMetaData> getQueueSummary(String queueName, String username) throws RemoteException, EJBXRuntime {
		if (queueName == null || queueName.trim().equalsIgnoreCase(""))
			return getQueueSummary(username);
		List<String> names = new ArrayList<String>();
		names.add(queueName);
		return getQueueSummary(names, username);
	}

	public List<WQMetaData> getAllQueueSummary(String username) throws RemoteException, EJBXRuntime {
		// List<String> queueNames = getDao().getQueueNames();
		List<String> queueNames = getDao().getQueueNamesForUser(username);
		return getQueueSummary(queueNames, username);
	}

	public void setEnvironmentInstances(Map<String, Environment> envInstances) {
		getDao().setEnvironmentInstances(envInstances);
	}
}