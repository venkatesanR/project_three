package com.addval.sessutils;

import java.rmi.RemoteException;
import javax.ejb.*;

public interface EJBEUserSessionHome extends EJBHome
{

    /**
     * @roseuid 401FAC450214
     * @J2EE_METHOD  --  create
     * Called by the client to create an EJB bean instance. It requires a matching pair in
     * the bean class, i.e. ejbCreate().
     */
    public EJBEUserSessionRemote create(String sessionKey, String username,String cookieInfo)
                throws CreateException, RemoteException;

    /**
     * @roseuid 401FAC4502A1
     * @J2EE_METHOD  --  findByPrimaryKey
     * Called by the client to find an EJB bean instance, usually find by primary key.
     */
    public EJBEUserSessionRemote findByPrimaryKey(EJBUserSessionPK pk)
                throws RemoteException, FinderException;
}