package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;

import org.springframework.ejb.support.AbstractStatelessSessionBean;

import com.addval.metadata.ColumnInfo;
import com.addval.metadata.EditorMetaData;
import com.addval.springutils.ServerRegistry;
import com.addval.utils.Pair;

public class EJBSEditorMetaDataBeanSpringImpl extends AbstractStatelessSessionBean implements EJBSEditorMetaData, java.io.Serializable {
	private EJBSEditorMetaData _serverUtility;
	protected String _PRIMARY_CONTEXT_ID = "com.addval";
	protected String _EJBSEDITORMETADATA_BEAN_ID = "com.addval.ejbutils.server.EJBSEditorMetaData";

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
		return _EJBSEDITORMETADATA_BEAN_ID;
	}

	/**
	 * Obtain our POJO service object from the BeanFactory/ApplicationContext
	 * 
	 * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()
	 */
	protected void onEjbCreate() throws CreateException {
		_serverUtility = (EJBSEditorMetaData) getBeanFactory().getBean(getSpringBeanId());
	}

	public EditorMetaData lookup(String name, String type) throws RemoteException {
		return _serverUtility.lookup(name, type);
	}

	public ColumnInfo lookupColumnInfo(String colName) throws RemoteException {
		return _serverUtility.lookupColumnInfo(colName);
	}

	public java.util.Vector lookupAllColumns(String name, String type) throws RemoteException {
		return _serverUtility.lookupAllColumns(name, type);
	}

	public EditorMetaData update(EditorMetaData editorMetaData) throws RemoteException {
		return _serverUtility.update(editorMetaData);
	}

	public EditorMetaData lookup(String name, String type, String userId) throws RemoteException {
		return _serverUtility.lookup(name, type, userId, getSessionContext().isCallerInRole("config"));
	}

	public EditorMetaData lookup(String name, String type, String userId, boolean configRole) throws RemoteException {
		return _serverUtility.lookup(name, type, userId, configRole);
	}

	public EditorMetaData update(EditorMetaData editorMetaData, String userId) throws RemoteException {
		return _serverUtility.update(editorMetaData, userId);
	}

    public List<Pair> lookupNameLabelPairs(String type) throws RemoteException {
		return _serverUtility.lookupNameLabelPairs(type);
    }
	
	
}