package com.addval.wqutils;

import javax.ejb.*;
import java.rmi.RemoteException;

/**
 * @author AddVal Technology Inc.
 */
public interface WQServerHome extends EJBHome
{

    /**
     * @roseuid 3FDE63FC012E
     * @J2EE_METHOD  --  create
     * Called by the client to create an EJB bean instance. It requires a matching pair in
     * the bean class, i.e. ejbCreate().
     */
    public WQServerRemote create    ()
                throws RemoteException, CreateException;
}