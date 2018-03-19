package com.addval.ejbutils.server;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface BulkUpdateMgrHome extends EJBHome {
	public BulkUpdateMgrRemote create() throws RemoteException, CreateException;
}
