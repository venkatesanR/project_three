/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.utils.EJBXRuntime;

public interface WQServer {
	
	/**
	 * This EJB Interface provides interfaces to :
	 * 1. Send a Message
	 * 2. Process a Message
	 * 3. Mark the completion of processing
	 */
    public void sendMessage(String queueName,HashMap params) throws RemoteException,EJBXRuntime;
    public void sendMessage(EJBResultSet ejbRS) throws RemoteException,EJBXRuntime;


	public EJBResultSet deleteMessage(String queueName,HashMap params) throws RemoteException,EJBXRuntime;
	public EJBResultSet deleteMessage(EJBResultSet ejbRS) throws RemoteException,EJBXRuntime;
	
	public EJBResultSet processMessage(EJBCriteria criteria)  throws RemoteException, EJBXRuntime;
    public EJBResultSet processNextMessage(EJBCriteria criteria)  throws RemoteException, EJBXRuntime;

    public void unProcessMessage(String userName) throws RemoteException, EJBXRuntime;
    public void unProcessMessage(String userName,Hashtable sqls) throws RemoteException, EJBXRuntime;
	

}