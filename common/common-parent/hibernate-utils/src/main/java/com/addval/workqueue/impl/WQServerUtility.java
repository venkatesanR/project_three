/**
 * Copyright
 * AddVal Technology Inc.
 */
package com.addval.workqueue.impl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.jms.Destination;
import javax.jms.ObjectMessage;

import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import org.apache.log4j.Logger;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.WorkQueueUtils;
import com.addval.workqueue.api.WQSMetaData;
import com.addval.workqueue.api.WQServer;
import com.addval.wqutils.metadata.WQMetaData;
import com.addval.wqutils.utils.WQConstants;

public class WQServerUtility implements WQServer {
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WQServerUtility.class);
	private WQDAO _dao = null;
	private WorkQueueUtils workQueueUtils = null;

	private EJBSTableManager _tableManager = null;
	private WQSMetaData _workQueueMetaData = null;
	private String _notifyTopicName = null;
	private String _notifyMessengerName = null;

	public WorkQueueUtils getWorkQueueUtils() {
		if (this.workQueueUtils == null) {
			this.workQueueUtils = new WorkQueueUtils();
		}
		return this.workQueueUtils;
	}

	public void setWorkQueueUtils(WorkQueueUtils workQueueUtils) {
		this.workQueueUtils = workQueueUtils;
	}

	public void setDao(WQDAO dao) {
		_dao = dao;
	}

	public WQDAO getDao() {
		return _dao;
	}

	public EJBSTableManager getTableManager() {
		return _tableManager;
	}

	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}

	public WQSMetaData getWorkQueueMetaData() {
		return _workQueueMetaData;
	}

	public void setWorkQueueMetaData(WQSMetaData workQueueMetaData) {
		this._workQueueMetaData = workQueueMetaData;
	}

	public String getNotifyTopicName() {
		return _notifyTopicName;
	}

	public void setNotifyTopicName(String notifyTopicName) {
		this._notifyTopicName = notifyTopicName;
	}

	public String getNotifyMessengerName() {
		return _notifyMessengerName;
	}

	public void setNotifyMessengerName(String notifyMessengerName) {
		this._notifyMessengerName = notifyMessengerName;
	}

	public void sendMessage(String queueName, HashMap params) throws RemoteException, EJBXRuntime {
		try {
			WQMetaData wqMetaData = getWorkQueueMetaData().lookup(queueName);
			EditorMetaData metadata = wqMetaData.getEditorMetaData();
			EJBResultSet ejbRS = getWorkQueueUtils().getInsertEJBResultSet(metadata, params);
			sendMessage(ejbRS);
			notifyTopic(wqMetaData, ejbRS);
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

	public void sendMessage(EJBResultSet ejbRS) throws RemoteException, EJBXRuntime {
		try {
			ejbRS = getTableManager().updateTransaction(ejbRS);
			if (ejbRS == null) {
				throw new EJBXRuntime("Insert failed");
			}
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

	public EJBResultSet deleteMessage(String queueName, HashMap params) throws RemoteException, EJBXRuntime {
		try {
			WQMetaData wqMetaData = getWorkQueueMetaData().lookup(queueName);
			EditorMetaData metadata = wqMetaData.getEditorMetaData();
			EJBResultSet ejbRS = getWorkQueueUtils().getDeleteEJBResultSet(metadata, params);
			return deleteMessage(ejbRS);
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

	public EJBResultSet deleteMessage(EJBResultSet ejbRS) throws RemoteException, EJBXRuntime {
		try {
			ejbRS = getTableManager().updateTransaction(ejbRS);
			if (ejbRS == null) {
				throw new EJBXRuntime("Delete failed");
			}
			return ejbRS;
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

	public EJBResultSet processMessage(EJBCriteria wqCriteria) throws RemoteException, EJBXRuntime {
		Hashtable params = new Hashtable();
		params.put(WQConstants._MESSAGE_STATUS_COLUMN, WQConstants._STATUS_INPROCESS);
		return updateMessage(wqCriteria, params);
	}

	public EJBResultSet processNextMessage(EJBCriteria wqCriteria) throws RemoteException, EJBXRuntime {
		EJBResultSet ejbRS = getTableManager().lookup(wqCriteria);
		EJBResultSetMetaData rsMetaData = (EJBResultSetMetaData) ejbRS.getMetaData();
		boolean doContinue = false;
		String columnValue = null;
		while (ejbRS.next()) {
			doContinue = false;
			Vector<ColumnMetaData> keyColumns = rsMetaData.getEditorMetaData().getKeyColumns();
			Hashtable params = new Hashtable();
			if (keyColumns != null) {
				for (ColumnMetaData columnMetaData : keyColumns) {
					columnValue = ejbRS.getString(columnMetaData.getName());
					if (columnValue != null) {
						params.put(columnMetaData.getName(), columnValue);
					} else {
						doContinue = true;
						break;
					}
				}
			}
			if (doContinue) {
				continue;
			}
			wqCriteria.where(params, null);
			return processMessage(wqCriteria);
		}
		throw new EJBXRuntime("[no_msg_to_process]");
	}

	public void unProcessMessage(String userName) throws RemoteException, EJBXRuntime {
		// Update Message Status to UNPROCESSED.
		getDao().updateUnProcessMessage(userName);
	}

	public void unProcessMessage(String userName, Hashtable sqls) throws RemoteException, EJBXRuntime {
		getDao().updateUnProcessMessage(userName, sqls);
	}

	private void notifyTopic(WQMetaData wqMetaData, EJBResultSet ejbRS) {

		if ((getNotifyTopicName() != null) && (getNotifyMessengerName() != null)) {
			try {
				MessengerManager mgr = MessengerManager.getInstance();
				Messenger messenger = null;
				Vector msg = new Vector();
				Destination destination = null;
				ObjectMessage message = null;

				if (mgr != null) {
					messenger = mgr.get(getNotifyMessengerName());

					if (messenger != null) {
						msg.add(wqMetaData);
						msg.add(ejbRS);
						destination = messenger.getDestination(getNotifyTopicName());
						// Create the Object Message
						message = messenger.createObjectMessage(msg);
						messenger.send(destination, message);
					}
				}
			}
			catch (Exception ex) {
				_logger.error(ex);
				throw new EJBException(ex);
			}
		}
	}

	private EJBResultSet updateMessage(EJBCriteria criteria, Hashtable params) throws EJBXRuntime {
		try {
			if (params == null) {
				throw new EJBXRuntime("Update params  should not be null");
			}
			EJBResultSet ejbRS = getTableManager().lookup(criteria);
			EJBResultSetMetaData rsMetaData = (EJBResultSetMetaData) ejbRS.getMetaData();

			String columnName = null;
			String columnValue = null;

			for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();) {
				columnName = (String) iterator.next();
				if (!rsMetaData.getEditorMetaData().isColumnValid(columnName)) {
					throw new EJBXRuntime("The Queue : " + rsMetaData.getEditorMetaData().getName() + " does not have the required " + columnName + " associated with the table : " + rsMetaData.getEditorMetaData().getSource());
				}
			}

			if (ejbRS == null || ejbRS.getRecords().size() == 0) {
				throw new EJBXRuntime("Record is already processed by someone, Please process Next.");
			}
			while (ejbRS.next()) {
				for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();) {
					columnName = (String) iterator.next();
					columnValue = (String) params.get(columnName);
					ejbRS.updateString(columnName, columnValue);

				}
			}
			return getTableManager().updateTransaction(ejbRS);
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

}