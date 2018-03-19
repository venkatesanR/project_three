//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\sessutils\\EJBUserSessionPK.java

package com.addval.sessutils;

import java.io.Serializable;

public class EJBUserSessionPK implements Serializable 
{
	private String _sessionKey = null;
	
	/**
	 * SESSION_KEY of APP_USER_SESSION table
	 */
	private String _userName = null;
	
	/**
	 * @param sessionKey
	 * @param userName
	 * @roseuid 401FAC47035C
	 */
	public EJBUserSessionPK(String sessionKey, String userName) 
	{
		_sessionKey = sessionKey;
		_userName = userName;		
	}
	
	/**
	 * @return java.lang.String
	 * @roseuid 401FAC47036C
	 */
	public String getSessionKey() 
	{

		return _sessionKey;		
	}
	
	/**
	 * @return java.lang.String
	 * @roseuid 401FAC47036D
	 */
	public String getUserName() 
	{

		return _userName;		
	}
	
	/**
	 * @return int
	 * @roseuid 401FAC47036E
	 */
	public int hashCode() 
	{

		String code = _sessionKey + (_userName == null ? "" : _userName);

		return code.hashCode();		
	}
	
	/**
	 * @param obj
	 * @return boolean
	 * @roseuid 401FAC47037A
	 */
	public boolean equals(Object obj) 
	{

		boolean rv = false;

		if (obj == null || !(obj instanceof EJBUserSessionPK)) {

			rv = false;
		}
		else {

			String sessionKey   = ((EJBUserSessionPK)obj).getSessionKey();
			String userName 	= ((EJBUserSessionPK)obj).getUserName();


			sessionKey 	= sessionKey 	 == null ? "" : sessionKey;
			userName 	= userName 	 == null ? "" : userName;


			if (sessionKey.equals( _sessionKey ) && userName.equals( _userName ) )
				rv = true;
			else
				rv = false;
		}

		return rv;		
	}
	
	/**
	 * @return java.lang.String
	 * @roseuid 401FAC47037C
	 */
	public String toString() 
	{
		return "SessionKey=" + getSessionKey() + " : userName =" + getUserName();		
	}
}
