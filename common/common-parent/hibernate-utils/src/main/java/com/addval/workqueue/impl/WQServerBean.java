/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.impl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;

import org.springframework.ejb.support.AbstractStatelessSessionBean;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.springutils.ServerRegistry;
import com.addval.workqueue.api.WQServer;

public class WQServerBean extends AbstractStatelessSessionBean implements WQServer, java.io.Serializable {
	private static final long serialVersionUID = 2316693884105761589L;
	private WQServer _wqServer;

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
		_wqServer = (WQServer) getBeanFactory().getBean(WQServerConstants.WQSERVER_BEAN_ID);
	}

	public void sendMessage(String queueName, HashMap params) throws RemoteException, EJBXRuntime {
		_wqServer.sendMessage(queueName, params);
	}

	public void sendMessage(EJBResultSet ejbRS) throws RemoteException, EJBXRuntime {
		_wqServer.sendMessage(ejbRS);
	}

	public EJBResultSet deleteMessage(String queueName, HashMap params) throws RemoteException, EJBXRuntime {
		return _wqServer.deleteMessage(queueName, params);
	}

	public EJBResultSet deleteMessage(EJBResultSet ejbRS) throws RemoteException, EJBXRuntime {
		return _wqServer.deleteMessage(ejbRS);
	}

	public EJBResultSet processMessage(EJBCriteria criteria) throws RemoteException, EJBXRuntime {
		return _wqServer.processMessage(criteria);
	}

	public EJBResultSet processNextMessage(EJBCriteria criteria) throws RemoteException, EJBXRuntime {
		return _wqServer.processNextMessage(criteria);
	}

	public void unProcessMessage(String userName) throws RemoteException, EJBXRuntime {
		_wqServer.unProcessMessage(userName);
	}

	public void unProcessMessage(String userName, Hashtable sqls) throws RemoteException, EJBXRuntime {
		_wqServer.unProcessMessage(userName, sqls);
	}

}