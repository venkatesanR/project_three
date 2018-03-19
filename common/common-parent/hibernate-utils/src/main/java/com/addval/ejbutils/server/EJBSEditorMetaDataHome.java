package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import javax.ejb.*;

/**
 * Home Interface for EJBSEditorMetaDataBean
 * @author AddVal Technology Inc.
 */
public interface EJBSEditorMetaDataHome extends EJBHome {
    
    /**
     * @roseuid 3AF926CA0206
     * @J2EE_METHOD  --  create
     */
    public EJBSEditorMetaDataRemote create    () throws RemoteException, CreateException;
}