/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.api;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface WQServerHome extends EJBHome{
    public WQServerRemote create() throws RemoteException, CreateException;
}