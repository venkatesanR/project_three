package com.addval.sessutils;

import com.addval.sessutils.utils.AVUserSession;
import java.rmi.RemoteException;

public interface EJBSUserSessionMgr
{
    /**
     * @roseuid 401FAC470109
     * @J2EE_METHOD  --  checkUser
     */
    public String isLoggedIn(String userName) throws RemoteException;

    /**
     * @roseuid 401FAC470139
     * @J2EE_METHOD  --  login
     */
    public java.lang.String login(String userName,String sessionInfo) throws RemoteException;

    /**
     * @roseuid 401FAC470178
     * @J2EE_METHOD  --  lookup
     */
    public AVUserSession lookup(String sessionKey, String userName) throws RemoteException;

    /**
     * @roseuid 401FAC4701B7
     * @J2EE_METHOD  --  logout
     */
    public void logout(String sessionKey, String userName) throws RemoteException;

    public boolean isLoggedOut(String userName,String sessionInfo) throws RemoteException;

}