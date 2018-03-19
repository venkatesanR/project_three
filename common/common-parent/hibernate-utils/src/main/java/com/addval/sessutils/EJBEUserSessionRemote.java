package com.addval.sessutils;

import com.addval.sessutils.utils.AVUserSession;
import java.rmi.RemoteException;
import javax.ejb.*;

public interface EJBEUserSessionRemote extends EJBObject
{
    
    /**
     * @roseuid 401FAC460177
     * @J2EE_METHOD  --  lookup
     */
    public AVUserSession lookup    () throws RemoteException;
    
    /**
     * @roseuid 401FAC4601C5
     * @J2EE_METHOD  --  logout
     * public String login(String userName) throws RemoteException;
     */
    public void logout    (String sessionKey, String userName) throws RemoteException;
}