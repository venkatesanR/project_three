package com.addval.ejbutils.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;

import org.springframework.ejb.support.AbstractStatelessSessionBean;

import com.addval.metadata.UserCriteria;
import com.addval.springutils.ServerRegistry;

public class UserCriteriaMgrBeanSpringImpl  extends AbstractStatelessSessionBean implements UserCriteriaMgr, Serializable {

	private UserCriteriaMgr _utility;
	protected String _PRIMARY_CONTEXT_ID = "com.addval";
	protected String _USERCRITERIA_MGR_BEAN_ID = "com.addval.ejbutils.server.UserCriteriaMgr";

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
		return _USERCRITERIA_MGR_BEAN_ID;
	}
	

	protected void onEjbCreate() throws CreateException {
		_utility = (UserCriteriaMgr) getBeanFactory().getBean(getSpringBeanId());
	}
	
	public UserCriteria lookupUserCriteria(UserCriteria filter) throws RemoteException{
		return _utility.lookupUserCriteria(filter);
	}
	public void addNewUserCriteria(UserCriteria criteria) throws RemoteException{
		_utility.addNewUserCriteria(criteria);
	}
	public void updateUserCriteria(UserCriteria criteria) throws RemoteException{
		_utility.updateUserCriteria(criteria);
	}
	public void deleteUserCriteria(UserCriteria criteria) throws RemoteException{
		_utility.deleteUserCriteria(criteria);	
	}
	public ArrayList getUserCriteriaNames(UserCriteria filter) throws RemoteException{
		return _utility.getUserCriteriaNames(filter);
	}
	
}
