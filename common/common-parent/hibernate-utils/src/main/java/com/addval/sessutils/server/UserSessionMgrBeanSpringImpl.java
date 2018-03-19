package com.addval.sessutils.server;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;

import org.springframework.ejb.support.AbstractStatelessSessionBean;

import com.addval.sessutils.EJBSUserSessionMgr;
import com.addval.sessutils.utils.AVUserSession;
import com.addval.springutils.ServerRegistry;

public class UserSessionMgrBeanSpringImpl extends AbstractStatelessSessionBean implements EJBSUserSessionMgr, java.io.Serializable {
	private static final long serialVersionUID = 5202446251110547978L;
	private EJBSUserSessionMgr userSessionMgr;
	protected String _PRIMARY_CONTEXT_ID = "com.addval";
	protected String _EJBSUSERSESSIONMGR_BEAN_ID = "com.addval.sessutils.server.EJBSUserSessionMgr";

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
		return _EJBSUSERSESSIONMGR_BEAN_ID;
	}

	protected void onEjbCreate() throws CreateException {
		userSessionMgr = (EJBSUserSessionMgr) getBeanFactory().getBean(getSpringBeanId());
	}

	public String isLoggedIn(String userName) throws RemoteException {
		return userSessionMgr.isLoggedIn(userName);
	}

	public java.lang.String login(String userName, String sessionInfo) throws RemoteException {
		return userSessionMgr.login(userName, sessionInfo);
	}

	public AVUserSession lookup(String sessionKey, String userName) throws RemoteException {
		return userSessionMgr.lookup(sessionKey, userName);
	}

	public void logout(String sessionKey, String userName) throws RemoteException {
		userSessionMgr.logout(sessionKey, userName);
	}

	public boolean isLoggedOut(String userName, String sessionInfo) throws RemoteException {
		return userSessionMgr.isLoggedOut(userName, sessionInfo);
	}

}