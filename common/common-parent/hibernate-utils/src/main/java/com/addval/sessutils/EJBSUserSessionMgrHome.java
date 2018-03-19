package com.addval.sessutils;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface EJBSUserSessionMgrHome extends EJBHome {
	public EJBSUserSessionMgrRemote create() throws RemoteException, CreateException;
}