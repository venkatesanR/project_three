package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import javax.ejb.*;

/**
 * The home interface for the EJBSTableManager session bean
 */
public interface EJBSTableManagerHome extends EJBHome {

    /**
     * Called by the client to create an EJB bean instance. It requires a matching pair in
     * the bean class, i.e. ejbCreate().
     * @roseuid 3B66EC6F0265
     * @J2EE_METHOD  --  create
     */
    public EJBSTableManagerRemote create    ()
                throws RemoteException, CreateException;
}
