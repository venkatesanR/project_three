package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import javax.ejb.*;

public interface EJBSCargoresOverwriterHome extends EJBHome {

    public EJBSCargoresOverwriterRemote create() throws RemoteException, CreateException;
}
