package com.addval.ejbutils.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.ejb.support.AbstractStatelessSessionBean;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;
import com.addval.springutils.ServerRegistry;

public class EJBSTableManagerBeanSpringImpl extends AbstractStatelessSessionBean implements EJBSTableManager, Serializable {
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(EJBSTableManagerBeanSpringImpl.class);
	private static final long serialVersionUID = 1L;
	private EJBSTableManager _serverUtility;
	protected String _PRIMARY_CONTEXT_ID = "com.addval";
	protected String _EJBSTABLEMANAGER_BEAN_ID = "com.addval.ejbutils.server.EJBSTableManager";

	/**
	 * Override default BeanFactoryLocator implementation
	 * 
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext sessionContext) {
		super.setSessionContext(sessionContext);
		ServerRegistry registry = new ServerRegistry();
		setBeanFactoryLocator(registry.getBeanFactoryLocator());
		setBeanFactoryLocatorKey(getSpringContextId());

	}

	protected String getSpringContextId() {
		return _PRIMARY_CONTEXT_ID;
	}

	protected String getSpringBeanId() {
		return _EJBSTABLEMANAGER_BEAN_ID;
	}

	/**
	 * Obtain our POJO service object from the BeanFactory/ApplicationContext
	 * 
	 * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()
	 */
	protected void onEjbCreate() throws CreateException {
		_serverUtility = (EJBSTableManager) getBeanFactory().getBean(getSpringBeanId());
		try {
			setEnvironmentInstances(BeanFactoryUtils.beansOfTypeIncludingAncestors((ClassPathXmlApplicationContext) getBeanFactory(), Environment.class));
		}
		catch (RemoteException ex) {
			_logger.error("Error while lookup Environment spring bean instances..");
			_logger.error(ex);
		}
	}

	public EJBResultSet lookupForUpdate(EJBCriteria criteria) throws RemoteException {
		setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
		return _serverUtility.lookupForUpdate(criteria);
	}

	public EJBResultSet lookup(EJBCriteria criteria) throws RemoteException {
		setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
		return _serverUtility.lookup(criteria);
	}

	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData, EJBCriteria criteria) throws RemoteException {
		setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
		return _serverUtility.lookup(sql, editorMetaData, criteria);
	}

	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData) throws RemoteException {
		setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
		return _serverUtility.lookup(sql, editorMetaData);
	}

	public EJBResultSet update(EJBResultSet rs) throws RemoteException, EJBXRuntime {
		try {
			setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
			return _serverUtility.update(rs);
		}
		catch (EJBXRuntime se) {
			getSessionContext().setRollbackOnly();
			throw se;
		}
	}

	public EJBResultSet updateTransaction(EJBResultSet rs) throws RemoteException, EJBXRuntime {
		try {
			setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
			return _serverUtility.updateTransaction(rs);
		}
		catch (EJBXRuntime se) {
			getSessionContext().setRollbackOnly();
			throw se;
		}
	}

	public boolean updateTransaction(EJBResultSet masterSet, EJBResultSet deleteSet, EJBResultSet insertSet) throws EJBXRuntime, RemoteException {
		try {
			setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
			return _serverUtility.updateTransaction(masterSet, deleteSet, insertSet);
		}
		catch (EJBXRuntime se) {
			getSessionContext().setRollbackOnly();
			throw se;
		}
	}
	
	public boolean updateTransaction(EJBResultSet masterSet, List<EJBResultSet> deleteSet, List<EJBResultSet> insertSet) throws EJBXRuntime, RemoteException {
		try {
			setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
			return _serverUtility.updateTransaction(masterSet, deleteSet, insertSet);
		}
		catch (EJBXRuntime se) {
			getSessionContext().setRollbackOnly();
			throw se;
		}
	}	

	/*
	* Mass Update
	*/
	public int updateTransaction(EJBCriteria criteria) throws RemoteException, EJBXRuntime {
		try {
			setCallerPrincipalName(getSessionContext().getCallerPrincipal().getName());
			return _serverUtility.updateTransaction(criteria);
		}
		catch (EJBXRuntime se) {
			getSessionContext().setRollbackOnly();
			throw se;
		}
	}

	public void setCallerPrincipalName(String name) throws RemoteException {
		_serverUtility.setCallerPrincipalName(name);
	}

	public void setEnvironmentInstances(Map<String, Environment> envInstances) throws RemoteException {
		_serverUtility.setEnvironmentInstances(envInstances);
	}

}