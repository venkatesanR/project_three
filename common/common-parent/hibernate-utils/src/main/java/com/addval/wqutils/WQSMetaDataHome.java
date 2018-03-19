package com.addval.wqutils;

import javax.ejb.*;
import java.rmi.RemoteException;

public interface WQSMetaDataHome extends EJBHome
{

    public WQSMetaDataRemote create    () throws RemoteException, CreateException;

}