package com.addval.wqutils;

import javax.ejb.*;
import java.rmi.RemoteException;

public interface WQEMetaDataHome extends EJBHome
{

    /**
     * @roseuid 3FDE4B7E0132
     * @J2EE_METHOD  --  findByPrimaryKey
     * Called by the client to find an EJB bean instance, usually find by primary key.
     */
    public WQEMetaDataRemote findByPrimaryKey    (WQEMetaDataBeanPK pk)
                throws RemoteException, FinderException;
}