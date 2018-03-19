package com.addval.ejbutils.server;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface UserCriteriaMgrHome extends EJBHome {
	public UserCriteriaMgrRemote create() throws RemoteException, CreateException;
}