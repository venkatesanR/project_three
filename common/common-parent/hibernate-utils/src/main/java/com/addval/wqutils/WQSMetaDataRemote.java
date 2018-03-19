package com.addval.wqutils;

import com.addval.wqutils.metadata.WQMetaData;
import com.addval.ejbutils.utils.EJBXRuntime;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Hashtable;

public interface WQSMetaDataRemote extends EJBObject
{

    /**
     * @roseuid 3FDE43B703D8
     * @J2EE_METHOD  --  lookup
     */
    public WQMetaData lookup(String queueName) throws RemoteException,EJBXRuntime;

    /**
     * @roseuid 3FDE43B800D7
     * @J2EE_METHOD  --  lookup
     */
    public Vector lookup(Vector queueNames) throws RemoteException,EJBXRuntime;

    /**
     * @roseuid 3FDE43B801DB
     * @J2EE_METHOD  --  queueStatus
     */
    public Vector queueStatus(Hashtable filter, Vector qwMetaData) throws RemoteException,EJBXRuntime;


    public String getVerifiedQueueName(String queue) throws RemoteException,EJBXRuntime;


	/**
	 * Monitor the queue for any new messages. Look for any unprocessed messages with dwell time <= <dwell time threshold>
	 */
    public Vector queueMonitor(Hashtable filter, Vector qwMetaData) throws RemoteException,EJBXRuntime;
}