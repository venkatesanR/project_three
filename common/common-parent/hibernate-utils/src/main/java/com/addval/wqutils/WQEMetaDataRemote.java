package com.addval.wqutils;

import com.addval.wqutils.metadata.WQMetaData;
import java.rmi.RemoteException;
import javax.ejb.*;

public interface WQEMetaDataRemote extends EJBObject
{

    /**
     * @roseuid 3FDE4C2D010C
     * @J2EE_METHOD  --  lookup
     */
    public WQMetaData lookup    () throws RemoteException;
}