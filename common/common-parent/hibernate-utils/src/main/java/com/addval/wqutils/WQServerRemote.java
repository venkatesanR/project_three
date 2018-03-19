package com.addval.wqutils;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.HashMap;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.utils.EJBXRuntime;

/**
 * This EJB Interface provides interfaces to :
 * 1. Send a Message
 * 2. Process a Message
 * 3. Mark the completion of processing
 * 4. Query a queue given a WQCriteria
 */
public interface WQServerRemote extends EJBObject {

    public void sendMessage(String queueName,HashMap params) throws RemoteException,EJBXRuntime;
    public void sendMessage(EJBResultSet ejbRS) throws RemoteException,EJBXRuntime;

	public EJBResultSet deleteMessage(EJBResultSet ejbRS) throws RemoteException,EJBXRuntime;
	public EJBResultSet deleteMessage(String queueName,HashMap params) throws RemoteException,EJBXRuntime;

	public EJBResultSet processMessage(EJBCriteria wqCriteria)  throws RemoteException, EJBXRuntime;
    public EJBResultSet processNextMessage(EJBCriteria wqCriteria)  throws RemoteException, EJBXRuntime;

    public void unProcessMessage(String userName) throws RemoteException, EJBXRuntime;
    public void unProcessMessage(String userName,Hashtable sqls) throws RemoteException, EJBXRuntime;
}