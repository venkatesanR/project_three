//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\sessutils\\server\\EJBSUserSessionMgrBean.java

package com.addval.sessutils.server;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.NamingException;

import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.sessutils.EJBEUserSessionHome;
import com.addval.sessutils.EJBEUserSessionRemote;
import com.addval.sessutils.EJBUserSessionPK;
import com.addval.sessutils.utils.AVUserSession;

public class EJBSUserSessionMgrBean implements SessionBean
{
	private static final String _module = "com.addval.sessutils.server.EJBSUserSessionBean";
	private SessionContext _context;
	private String _subsystem = null;

	/**
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FADE400CB
	 */
	public void ejbCreate() throws RemoteException
	{

	}

	/**
	 * @param context
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAE4E03C8
	 */
	public void unsetSessionContext(SessionContext context) throws RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAEFD00BB
	 */
	public void ejbRemove() throws EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAF2500AB
	 */
	public void ejbActivate() throws EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAF26005D
	 */
	public void ejbPassivate() throws EJBException, RemoteException
	{

	}

	/**
	 * @param userName
	 * @return boolean
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAF3B01A5
	 */
	public String isLoggedIn(String userName) throws RemoteException
	{
		Connection 	conn 	   = null;
		PreparedStatement stmt = null;
		ResultSet  	rs 		   = null;
		boolean 	isLoggedIn = false;
		String  	cookieInfo = null;
		try
		{
			conn = getConnection();
			String sql = "SELECT " + UserSessionTbl._sessionInfo + " FROM "+ UserSessionTbl._tblName + " WHERE " + UserSessionTbl._userName + "= ? AND " + UserSessionTbl._logoffTime + " IS NULL ORDER BY LOGON_TIME DESC";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, userName);
			rs = stmt.executeQuery();
			
			isLoggedIn = rs.next();

			// Return the cookie information of the remote user
			if(isLoggedIn) {
				cookieInfo = rs.getString( UserSessionTbl._sessionInfo );
				/* This happens on te JSP page now
				 if(cookieInfo != null){

				 	invalidate(cookieInfo);
				 	isLoggedIn = false;

				}
				*/
			}

			return cookieInfo;
		}
		catch (SQLException se) {
			Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( se );
			throw new EJBException( se );
		}
		catch (Exception e) {

			Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( e );
			throw new EJBException( e );
		}
		finally {

			try {
				if (rs   != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) releaseConnection( conn );
			}
			catch (SQLException se) {
				Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( se );
				throw new EJBException( se );
			}
		}
	}

    public boolean isLoggedOut(String userName,String sessionInfo) throws RemoteException
    {
        Connection 	conn 	   = null;
        PreparedStatement stmt = null;
        ResultSet  	rs 		   = null;
        boolean 	isLoggedOut = false;
        String  	cookieInfo = null;
        try
        {
            conn = getConnection();
            String sql = "SELECT 1 FROM "+ UserSessionTbl._tblName + " WHERE " + UserSessionTbl._userName + "= ? AND " + UserSessionTbl._sessionInfo + "= '" + sessionInfo +"' AND " + UserSessionTbl._logoffTime + " IS NOT NULL ORDER BY LOGON_TIME DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            
            rs = stmt.executeQuery();
            isLoggedOut = rs.next();
            return isLoggedOut;

        }
        catch (SQLException se) {
            Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( se );
            throw new EJBException( se );
        }
        catch (Exception e) {

            Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( e );
            throw new EJBException( e );
        }
        finally {

            try {
                if (rs   != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) releaseConnection( conn );
            }
            catch (SQLException se) {
                Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( se );
                throw new EJBException( se );
            }
        }
    }

    /**
	 * @param userName
	 * @return java.lang.String
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAF5B029F
	 */
	public String login(String userName,String sessionInfo) throws RemoteException
	{
	    Connection conn = null;
	    long key = -1;

	    try {
		    conn = getConnection();
		    String sequenceName = Environment.getInstance( _subsystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.UserSessionSequenceName", "APP_USER_SESSION_SEQ" );
		    key = getNextID( sequenceName,conn );
    		String      beanName = Environment.getInstance( _subsystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.UserSessionBeanName", "AVEUserSession" );
			EJBEUserSessionHome home = (EJBEUserSessionHome)EJBEnvironment.lookupEJBInterface(_subsystem, beanName, EJBEUserSessionHome.class);
			EJBEUserSessionRemote remote   = home.create(  String.valueOf(key), userName ,sessionInfo);
		    return String.valueOf(key);
	    }
	    catch(Exception e)
	    {
		    Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( e );
		    throw new EJBException( e );
	    }
		finally {

				if (conn != null) releaseConnection( conn );
		}
	}

	/**
	 * @param sessionKey
	 * @param userName
	 * @return com.addval.sessutils.utils.AVUserSession
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAF7E036B
	 */
	public AVUserSession lookup(String sessionKey, String userName) throws RemoteException
	{
		return null;
	}

	/**
	 * @param sessionKey
	 * @param userName
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FAFCB0148
	 */
	public void logout(String sessionKey, String userName) throws RemoteException
	{
	    try {
			String      beanName = Environment.getInstance( _subsystem ).getCnfgFileMgr().getPropertyValue( "editorMetaData.UserSessionBeanName", "AVEUserSession" );
			EJBEUserSessionHome home = (EJBEUserSessionHome)EJBEnvironment.lookupEJBInterface(_subsystem, beanName, EJBEUserSessionHome.class);
			EJBEUserSessionRemote remote = home.findByPrimaryKey( new EJBUserSessionPK( sessionKey, userName));
			remote.logout( sessionKey, userName);
	    }
	    catch(Exception e)
	    {
		    Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( e );
		    throw new EJBException( e );
	    }
	}

	/**
	 * @return java.sql.Connection
	 * @roseuid 401FAFF70167
	 */
	private Connection getConnection()
	{
		return Environment.getInstance(_subsystem).getDbPoolMgr().getConnection();
	}

	/**
	 * @param conn
	 * @roseuid 401FB009007D
	 */
	private void releaseConnection(Connection conn)
	{
		Environment.getInstance(_subsystem).getDbPoolMgr().releaseConnection(conn);
	}

	/**
	 * @param seqName
	 * @param conn
	 * @return long
	 * @throws java.sql.SQLException
	 * @roseuid 401FB028036B
	 */
	private long getNextID(String seqName, Connection conn) throws SQLException
	{
		String sql  = "select " + seqName + ".nextVal from dual";
		java.sql.Statement stmt = null;
		java.sql.ResultSet rs    = null;
		try {
			stmt = conn.createStatement();
			stmt.executeQuery( sql );
			rs = stmt.getResultSet();
			if (rs.next())
				return rs.getLong( 1 );
		}
		finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}
		return 0;
	}

	/**
	 * @param context
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FBD8E0000
	 */
	public void setSessionContext(SessionContext context) throws EJBException, RemoteException
	{
		_context = context;
		try
		{
			_subsystem = (String) EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );
        }
        catch(NamingException nex){
			nex.printStackTrace();
		}

	}

	private void invalidate(String cookieInfo) {
		try {

			String urlStr = Environment.getInstance( _subsystem ).getCnfgFileMgr().getPropertyValue( "app.logout.url", null );
			if(urlStr == null)
				throw new com.addval.utils.XRuntime( _module, "ngrm.logout is not defined in " + _subsystem + ".properties");

 			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Cookie", cookieInfo);
			InputStream in= conn.getInputStream();
			/*
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String inLine = reader.readLine();
			while (inLine != null) {
				System.out.println(inLine);
				inLine = reader.readLine();
			}
			if(reader != null) reader.close();
			*/
			if(in != null) in.close();
		}
		catch(ConnectException cx){
			System.out.println("***********************************************************************************************");
			System.out.println("ngrm.logout property is not configured properly. Please check " + _subsystem + ".properties");
			System.out.println("***********************************************************************************************");
		}
		catch(Exception ex) {


			Environment.getInstance( _subsystem ).getLogFileMgr( _module ).logError( ex );
			throw new EJBException( ex );
		}
	}

	/**
	 * @author AddVal Technology Inc.
	 */
	public final static class UserSessionTbl
	{
		private static final String _tblName = "APP_USER_SESSION";
		private static final String _sessionKey = "SESSION_KEY";
		private static final String _userName = "USER_NAME";
		private static final String _loginTime = "LOGON_TIME";
		private static final String _logoffTime = "LOGOFF_TIME";
		private static final String _sessionInfo = "SESSION_INFO";

	}
}
