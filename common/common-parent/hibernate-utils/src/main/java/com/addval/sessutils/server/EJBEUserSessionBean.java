//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\sessutils\\server\\EJBEUserSessionBean.java

package com.addval.sessutils.server;

import javax.ejb.*;
import java.sql.*;
import com.addval.sessutils.utils.AVUserSession;
import com.addval.sessutils.EJBUserSessionPK;
import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import javax.naming.NamingException;
import java.rmi.RemoteException;

public class EJBEUserSessionBean implements EntityBean
{
	private static final String _module = "com.addval.sessutils.server.EJBEUserSessionBean";
	private AVUserSession _avUserSession = null;
	private String _subSystem = null;
	private EntityContext _context = null;

	/**
	 * @param context
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB6170271
	 */
	public void setEntityContext(EntityContext context) throws EJBException, RemoteException
	{
		_context = context;
		try
		{
			_subSystem = (String) EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );
		}
		catch(NamingException nex)
		{
			nex.printStackTrace();
		}
	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB61702AF
	 */
	public void unsetEntityContext() throws EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.RemoveException
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB61702BF
	 */
	public void ejbRemove() throws RemoveException, EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB61702CE
	 */
	public void ejbActivate() throws EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB61702CF
	 */
	public void ejbPassivate() throws EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB61702DE
	 */
	public void ejbLoad() throws EJBException, RemoteException
	{

	}

	/**
	 * @throws javax.ejb.EJBException
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FB61702EE
	 */
	public void ejbStore() throws EJBException, RemoteException
	{

	}

	/**
	 * @param pk
	 * @return com.addval.sessutils.EJBUserSessionPK
	 * @roseuid 401FB63E02AF
	 */
	public EJBUserSessionPK ejbFindByPrimaryKey(EJBUserSessionPK pk) throws ObjectNotFoundException
	{
		Connection conn = null;
		PreparedStatement ptsmt = null;
		ResultSet rs = null;

		try {
			if (pk != null && pk.getSessionKey() != null && pk.getUserName() != null) {
				conn = getConnection();
				String sql = "SELECT 1 FROM "+ UserSessionTbl._tblName + " WHERE " + UserSessionTbl._sessionKey + "= ? " + " AND " + UserSessionTbl._userName + "=?";
				ptsmt = conn.prepareStatement( sql );
				ptsmt.setString( 1, pk.getSessionKey() );
				ptsmt.setString( 2, pk.getUserName() );
				rs = ptsmt.executeQuery();

				if (!rs.next())
					throw new ObjectNotFoundException( " User with name " +pk.getUserName() + "not found");
			}
		}
		catch (SQLException se) {

			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
			throw new EJBException( se );
		}
		finally {

			try {
				if (rs   != null) rs.close();
				if (ptsmt != null) ptsmt.close();
				if (conn != null) releaseConnection( conn );
			}
			catch (SQLException se) {
				Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
				throw new EJBException( se );
			}
		}
		return pk;
	}

	/**
	 * @param sessionKey
	 * @param userName
	 * @return com.addval.sessutils.EJBUserSessionPK
	 * @roseuid 401FB69D00BB
	 */
	public EJBUserSessionPK ejbCreate(String sessionKey, String userName,String sessionInfo)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			String sql = "INSERT INTO " + UserSessionTbl._tblName + "(" + UserSessionTbl._userName + ","+ UserSessionTbl._sessionKey + "," + UserSessionTbl._loginTime + "," + UserSessionTbl._sessionInfo +") VALUES ( ?, ?, ? ,?)";
			pstmt = conn.prepareStatement( sql) ;
			pstmt.setString(1, userName);
			pstmt.setInt(2, Integer.parseInt( sessionKey ));
			pstmt.setTimestamp(3, new Timestamp( com.addval.utils.DateUtl.getTimestamp().getTime() ), com.addval.utils.AVConstants._GMT_CALENDAR);
			pstmt.setString(4, sessionInfo);
			pstmt.executeUpdate();

			return new EJBUserSessionPK( String.valueOf( sessionKey ), userName  ) ;
		}
		catch (SQLException se) {

			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
			throw new EJBException( se );
		}
		catch (Exception e) {

			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
			throw new EJBException( e );
		}
		finally {

			try {
				if (rs   != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) releaseConnection( conn );
			}
			catch (SQLException se) {
				Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
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
	public void logout(String sessionKey, String userName) throws RemoteException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			String sql = "UPDATE " + UserSessionTbl._tblName + " SET " + UserSessionTbl._logoffTime + "= ? " + " WHERE " + UserSessionTbl._sessionKey +" = ? " + " AND " + UserSessionTbl._userName  + " = ?";
			pstmt = conn.prepareStatement( sql) ;
			pstmt.setTimestamp(1, new Timestamp( com.addval.utils.DateUtl.getTimestamp().getTime() ), com.addval.utils.AVConstants._GMT_CALENDAR);
			pstmt.setInt( 2, Integer.parseInt( sessionKey ));
			pstmt.setString(3, userName);
            pstmt.executeUpdate();
		}
		catch (SQLException se) {

		    Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
		    throw new EJBException( se );
		}
		catch (Exception e) {

		    Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
		    throw new EJBException( e );
		}
		finally {

			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) releaseConnection( conn );
			}
			catch (SQLException se) {
				Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( se );
				throw new EJBException( se );
			}
		}
	}

	/**
	 * @return java.sql.Connection
	 * @roseuid 401FB7ED0000
	 */
	public Connection getConnection()
	{
		return Environment.getInstance(_subSystem).getDbPoolMgr().getConnection();
	}

	/**
	 * @param conn
	 * @roseuid 401FB7F60138
	 */
	public void releaseConnection(Connection conn)
	{
		Environment.getInstance(_subSystem).getDbPoolMgr().releaseConnection(conn);
	}

	/**
	 * @param sessionKey
	 * @param userName
	 * @roseuid 401FBBDD038A
	 */
	public void ejbPostCreate(String sessionKey, String userName,String cookieInfo)
	{

	}

	/**
	 * @return com.addval.sessutils.utils.AVUserSession
	 * @throws java.rmi.RemoteException
	 * @roseuid 401FBEE903A9
	 */
	public AVUserSession lookup() throws RemoteException
	{
		return null;
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
