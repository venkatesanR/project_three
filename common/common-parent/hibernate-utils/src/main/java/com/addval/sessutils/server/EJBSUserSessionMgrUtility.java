//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\sessutils\\server\\EJBSUserSessionMgrBean.java

package com.addval.sessutils.server;

import com.addval.sessutils.utils.AVUserSession;
import com.addval.sessutils.EJBSUserSessionMgr;

import com.addval.environment.Environment;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.ConnectException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

import org.apache.log4j.Logger;

public class EJBSUserSessionMgrUtility implements EJBSUserSessionMgr
{
	private static final String _module = "com.addval.sessutils.server.EJBSUserSessionMgrUtility";
    private Environment _env = null;
	private String _sessionSeq = null;
	private String _logoutUrl = null;
   	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(EJBSUserSessionMgrUtility.class);


   public Environment getEnvironment() {
		   return _env;
   }

   public void setEnvironment(Environment env) {
	   _env = env;
   }


   public void setSessionSequence(String seq) {
	   _sessionSeq = seq;
   }

   public String getSessionSequence() {
	   return _sessionSeq;
   }

   public void setLogoutUrl(String url) {
	   _logoutUrl = url;
   }

   public String getLogoutUrl() {
	   return _logoutUrl;
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
		PreparedStatement  	stmt 	   = null;
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
			_logger.error(se);
			throw new EJBException( se );
		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException( e );
		}
		finally {

			try {
				if (rs   != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) releaseConnection( conn );
			}
			catch (SQLException se) {
				_logger.error(se);
				throw new EJBException( se );
			}
		}
	}

    public boolean isLoggedOut(String userName,String sessionInfo) throws RemoteException
    {
        Connection 	conn 	   = null;
        PreparedStatement  	stmt 	   = null;
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
			_logger.error(se);
            throw new EJBException( se );
        }
        catch (Exception e) {
			_logger.error(e);
            throw new EJBException( e );
        }
        finally {

            try {
                if (rs   != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) releaseConnection( conn );
            }
            catch (SQLException se) {
				_logger.error(se);
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


		    String sequenceName = getSessionSequence();
		    // getEnvironment().getCnfgFileMgr().getPropertyValue( "editorMetaData.UserSessionSequenceName", "APP_USER_SESSION_SEQ" );
		    key = getNextID( sequenceName,conn );

			insertSession(String.valueOf(key), userName, sessionInfo, conn);

		    return String.valueOf(key);
	    }
	    catch(Exception e)
	    {
		    _logger.error( e );
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
	    Connection conn = null;

	    try {
		    conn = getConnection();

			cancelSession(sessionKey, userName, conn);
	    }
	    catch(Exception e)
	    {
		    _logger.error(e);
		    throw new EJBException( e );
	    }
		finally {
				if (conn != null) releaseConnection( conn );
		}

	}

	/**
	 * @return java.sql.Connection
	 * @roseuid 401FAFF70167
	 */
	private Connection getConnection()
	{
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	/**
	 * @param conn
	 * @roseuid 401FB009007D
	 */
	private void releaseConnection(Connection conn)
	{
		getEnvironment().getDbPoolMgr().releaseConnection(conn);
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


	private void invalidate(String cookieInfo) {
		try {


			//String urlStr = getEnvironment().getCnfgFileMgr().getPropertyValue( "app.logout.url", null );
			String urlStr = getLogoutUrl();
			if(urlStr == null)
				throw new com.addval.utils.XRuntime( _module, "logoutUrl is not defined");

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
			System.out.println("logoutUrl is not configured properly. ");
			System.out.println("***********************************************************************************************");
		}
		catch(Exception ex) {

			_logger.error(ex);
			throw new EJBException( ex );
		}
	}



	private boolean insertSession(String sessionKey, String userName,String sessionInfo,Connection conn)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "INSERT INTO " + UserSessionTbl._tblName + "(" + UserSessionTbl._userName + ","+ UserSessionTbl._sessionKey + "," + UserSessionTbl._loginTime + "," + UserSessionTbl._sessionInfo +") VALUES ( ?, ?, ? ,?)";
			pstmt = conn.prepareStatement( sql) ;
			pstmt.setString(1, userName);
			pstmt.setInt(2, Integer.parseInt( sessionKey ));
			pstmt.setTimestamp(3, new Timestamp( com.addval.utils.DateUtl.getTimestamp().getTime() ), com.addval.utils.AVConstants._GMT_CALENDAR);
			pstmt.setString(4, sessionInfo);
			pstmt.executeUpdate();

			return true;
		}
		catch (SQLException se) {

			_logger.error(se);
			throw new EJBException( se );
		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException( e );
		}
		finally {

			try {
				if (rs   != null) rs.close();
				if (pstmt != null) pstmt.close();
			}
			catch (SQLException se) {
				_logger.error(se);
				throw new EJBException( se );
			}
		}
	}

	/**
	 * @param sessionKey
	 * @param userName
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB6CB02CE
	 */
	private boolean cancelSession(String sessionKey, String userName, Connection conn) throws RemoteException
	{
		PreparedStatement pstmt = null;
		try {
			String sql = "UPDATE " + UserSessionTbl._tblName + " SET " + UserSessionTbl._logoffTime + "= ? " + " WHERE " + UserSessionTbl._sessionKey +" = ? " + " AND " + UserSessionTbl._userName  + " = ?";
			pstmt = conn.prepareStatement( sql) ;
			pstmt.setTimestamp(1, new Timestamp( com.addval.utils.DateUtl.getTimestamp().getTime() ), com.addval.utils.AVConstants._GMT_CALENDAR);
			pstmt.setInt( 2, Integer.parseInt( sessionKey ));
			pstmt.setString(3, userName);
            pstmt.executeUpdate();

            return true;
		}
		catch (SQLException se) {

			_logger.error(se);
		    throw new EJBException( se );
		}
		catch (Exception e) {

			_logger.error(e);
		    throw new EJBException( e );
		}
		finally {

			try {
				if (pstmt != null) pstmt.close();
			}
			catch (SQLException se) {
				_logger.error(se);
				throw new EJBException( se );
			}
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
