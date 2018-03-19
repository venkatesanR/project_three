/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.api;

import java.rmi.RemoteException;
import javax.ejb.*;

public interface WQSMetaDataHome extends EJBHome{
    public WQSMetaDataRemote create() throws RemoteException, CreateException;
}