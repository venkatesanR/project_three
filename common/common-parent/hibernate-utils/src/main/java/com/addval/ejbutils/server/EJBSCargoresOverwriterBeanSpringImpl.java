package com.addval.ejbutils.server;

import javax.ejb.*;
import java.rmi.RemoteException;

import com.addval.ejbutils.server.EJBSEditorMetaData;
import java.rmi.RemoteException;
import java.util.ArrayList;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnInfo;
import java.util.Vector;
import com.addval.springutils.ServerRegistry;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.ejb.support.AbstractStatelessSessionBean;
import com.addval.metadata.UpdateUserCriteria;


public class EJBSCargoresOverwriterBeanSpringImpl extends AbstractStatelessSessionBean implements EJBSCargoresOverwriter, java.io.Serializable {
	private EJBSCargoresOverwriter _serverUtility;
	protected String _PRIMARY_CONTEXT_ID = "com.addval";
	protected String _EJBSOVERWRITER_BEAN_ID = "com.addval.ejbutils.server.EJBSCargoresOverwriter";

	/**
	  * Override default BeanFactoryLocator implementation
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
		return _EJBSOVERWRITER_BEAN_ID;
	}

	/**
	  * Obtain our POJO service object from the BeanFactory/ApplicationContext
	  * @see org.springframework.ejb.support.AbstractStatelessSessionBean#onEjbCreate()
	  */
	protected void onEjbCreate() throws CreateException {
		_serverUtility = (EJBSCargoresOverwriter) getBeanFactory().getBean(getSpringBeanId());
	}


	public void validate(EditorMetaData metadata,UpdateUserCriteria criteria) throws RemoteException {
		_serverUtility.validate(metadata, criteria);
	}

	public void runOverwriteBatch(String directoryName,String editorName,String criteriaName) throws RemoteException {
		_serverUtility.runOverwriteBatch(directoryName, editorName, criteriaName);
	}

	public void runOverwriteBatch(String scenarioKey,String editorName) throws RemoteException {
		_serverUtility.runOverwriteBatch(scenarioKey, editorName);
	}


}