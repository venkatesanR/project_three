package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import javax.ejb.*;

/**
 * The home interface for the entity bean EJBEEditorMetaDataBean
 */
public interface EJBEEditorMetaDataHome extends EJBHome
{
    
    /**
     * @roseuid 3B1A8066027C
     * @J2EE_METHOD  --  findByPrimaryKey
     * Called by the client to find an EJB bean instance, usually find by primary key.
     */
    public EJBEEditorMetaDataRemote findByPrimaryKey    (EJBEEditorMetaDataBeanPK pk) 
                throws RemoteException, FinderException;
}