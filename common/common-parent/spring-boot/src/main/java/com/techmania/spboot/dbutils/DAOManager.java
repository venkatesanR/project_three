//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\DAOManager.java

//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOManager.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.techmania.spboot.dbutils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import java.sql.SQLException;
import java.util.Hashtable;
import java.sql.Connection;

/**
 * @author AddVal Technology Inc.
 * Generic Abstract Manager class that server side components can derive to do
 * simple CURD operations given a data object and associate DAO sql objects
 */
public abstract class DAOManager
{
	public static final int _LOOKUP_SQL = 0;
	public static final int _INSERT_SQL = 1;
	public static final int _UPDATE_SQL = 2;
	public static final int _DELETE_SQL = 3;

	/**
	 * Method looks up the database based on DAO sql for lookup and returns a Vector
	 * of objects of the specified type
	 * @param criteria
	 * @param clazzName
	 * @return Vector
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @roseuid 3EAECD9D02AF
	 */
	public Vector lookup(Object criteria, String clazzName) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Vector	    	  instances = new Vector();
		Object	    	  instance	= null;
		Connection		  conn		= null;
		PreparedStatement pstmt 	= null;
		ResultSet   	  rs   		= null;

		// Try to load the requested class
		ClassLoader 	  loader	= Thread.currentThread().getContextClassLoader();
		Class			  clazz		= loader.loadClass( clazzName );

		// Create DAO utilities
		String  		  serverType = getServerType();
		DAOSQLStatement   statement  = getDAOSQLStatement( _LOOKUP_SQL );
		DAOUtils		  utils		 = new DAOUtils( statement, serverType );
		String 			  sql 		 = statement.getSQL( serverType );

		try {

			conn  = getConnection();
			pstmt = conn.prepareStatement( sql );
			utils.setProperties( pstmt, criteria );
			rs    = pstmt.executeQuery();

			while( rs.next() ) {

				instance = clazz.newInstance();
				utils.getProperties( rs, instance );
				instances.add( instance );
			}
		}
		finally {

			if (rs    != null) rs.close()   ;
			if (pstmt != null) pstmt.close();
			if (conn  != null) releaseConnection( conn );
		}

		return instances;
	}

	/**
	 * This method looks up the database based on DAO sql for lookup and returns a
	 * Vector of objects of the specified type. It takes in a criteria which is a
	 * Hashtable
	 * @param criteria
	 * @param clazzName
	 * @return Vector
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @roseuid 3EAF22A000CB
	 */
	public Vector lookup(Hashtable criteria, String clazzName) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Vector	    	  instances = new Vector();
		Object	    	  instance	= null;
		Connection		  conn		= null;
		PreparedStatement pstmt 	= null;
		ResultSet   	  rs   		= null;

		// Try to load the requested class
		ClassLoader 	  loader	= Thread.currentThread().getContextClassLoader();
		Class			  clazz		= loader.loadClass( clazzName );
		DAOUtils		  utils		= new DAOUtils( getDAOSQLStatement( _LOOKUP_SQL ), getServerType() );

		try {

			conn  = getConnection();
			pstmt = lookup( conn, criteria );
			rs    = pstmt.getResultSet();

			while( rs.next() ) {

				instance = clazz.newInstance();
				utils.getProperties( rs, instance );
				instances.add( instance );
			}
		}
		finally {

			if (rs    != null) rs.close()   ;
			if (pstmt != null) pstmt.close();
			if (conn  != null) releaseConnection( conn );
		}

		return instances;
	}

	/**
	 * @param conn
	 * @param statement
	 * @param instance
	 * @return java.sql.PreparedStatement
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @roseuid 3ED2DDE000BB
	 */
	public PreparedStatement lookup(Connection conn, DAOSQLStatement statement, Object instance) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		// Create DAO utilities
		String  		  serverType = getServerType();
		DAOUtils		  utils		 = new DAOUtils		 ( statement, serverType );
		String 			  sql 		 = statement.getSQL	 ( serverType );
		PreparedStatement pstmt 	 = conn.prepareStatement( sql );

		utils.setProperties( pstmt, instance );

		// Execute the PreparedStatement
		pstmt.executeQuery();

		return pstmt;
	}

	/**
	 * @param conn
	 * @param criteria
	 * @return java.sql.PreparedStatement
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @roseuid 3ED2DEC800DA
	 */
	public PreparedStatement lookup(Connection conn, Hashtable criteria) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		// Create DAO utilities
		String  		  serverType = getServerType();
		DAOSQLStatement   statement  = getDAOSQLStatement( _LOOKUP_SQL );
		DAOUtils		  utils		 = new DAOUtils		 ( statement, serverType );
		String 			  sql 		 = statement.getSQL	 ( serverType );
		PreparedStatement pstmt 	 = conn.prepareStatement( sql );

		utils.setProperties( pstmt, criteria );

		// Execute the PreparedStatement
		pstmt.executeQuery();

		return pstmt;
	}

	/**
	 * @param conn
	 * @param statement
	 * @param criteria
	 * @return java.sql.PreparedStatement
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @roseuid 3ED2E03D003E
	 */
	public PreparedStatement lookup(Connection conn, DAOSQLStatement statement, Hashtable criteria) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		// Create DAO utilities
		String  		  serverType = getServerType();
		DAOUtils		  utils		 = new DAOUtils		 ( statement, serverType );
		String 			  sql 		 = statement.getSQL	 ( serverType );
		PreparedStatement pstmt 	 = conn.prepareStatement( sql );

		utils.setProperties( pstmt, criteria );

		// Execute the PreparedStatement
		pstmt.executeQuery();

		return pstmt;
	}

	/**
	 * Inserts the specified object into the database based on DAO sql statements
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EAECE50008C
	 */
	public int insert(Object instance) throws SQLException
	{
		return insert( getDAOSQLStatement( _INSERT_SQL ), instance );
	}

	/**
	 * Updates the specified object into the database based on DAO sql statements
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EAECE70005D
	 */
	public int update(Object instance) throws SQLException
	{
		return update( getDAOSQLStatement( _UPDATE_SQL ), instance );
	}

	/**
	 * Deletes the specified object from the database based on DAO sql statements
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EAECE7001A5
	 */
	public int delete(Object instance) throws SQLException
	{
		return delete( getDAOSQLStatement( _DELETE_SQL ), instance );
	}

	/**
	 * @param statement
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EB158C602FD
	 */
	public int insert(DAOSQLStatement statement, Object instance) throws SQLException
	{
		return executeUpdate( statement, instance );
	}

	/**
	 * @param statement
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EB158C6030D
	 */
	public int update(DAOSQLStatement statement, Object instance) throws SQLException
	{
		return executeUpdate( statement, instance );
	}

	/**
	 * @param statement
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EB158C6030F
	 */
	public int delete(DAOSQLStatement statement, Object instance) throws SQLException
	{
		return executeUpdate( statement, instance );
	}

	/**
	 * @param statement
	 * @param instance
	 * @return int
	 * @throws SQLException
	 * @roseuid 3EAED41402DE
	 */
	private int executeUpdate(DAOSQLStatement statement, Object instance) throws SQLException
	{
		int 			  rv 		 = 0;
		String  		  serverType = getServerType();
		String 			  sql 		 = statement.getSQL( serverType );
		Connection		  conn		 = null;
		PreparedStatement pstmt 	 = null;

		try {

			conn  = getConnection();
			pstmt = conn.prepareStatement( sql );
			DAOUtils utils = new DAOUtils( statement, serverType );

			utils.setProperties( pstmt, instance );

			rv = pstmt.executeUpdate();
		}
		finally {

			if (pstmt != null) pstmt.close();
			if (conn  != null) releaseConnection( conn );
		}

		return rv;
	}

	/**
	 * This method should be overriden by the derived classes
	 * @return String
	 * @roseuid 3EAECF33004E
	 */
	protected abstract String getSubsystem();

	/**
	 * This method should be overriden by the derived classes
	 * @return String
	 * @roseuid 3EAECF4602AF
	 */
	protected abstract String getServerType();

	/**
	 * This method should be overriden by the derived classes to return the
	 * appropriate DAOSQLStatement object based on the sql type that is passed in.
	 * @param type
	 * @return com.addval.dbutils.DAOSQLStatement
	 * @roseuid 3EAECF71000F
	 */
	protected abstract DAOSQLStatement getDAOSQLStatement(int type);

	/**
	 * @return java.sql.Connection
	 * @roseuid 3EB151B5035B
	 */
	protected abstract Connection getConnection();

	/**
	 * @param conn
	 * @roseuid 3EB151D7006D
	 */
	protected abstract void releaseConnection(Connection conn);
}
/**
 *
 * DAOManager.lookup(java.sql.Connection,java.util.Hashtable){
 * // Create DAO utilities
 * String  		  serverType = getServerType();
 * DAOSQLStatement   statement  = getDAOSQLStatement( _LOOKUP_SQL );
 * DAOUtils		  utils		 = new DAOUtils( statement, serverType );
 * String 			  sql 		 = statement.getSQL( serverType );
 * PreparedStatement pstmt 	 = conn.prepareStatement( sql );
 * utils.setProperties( pstmt, criteria );
 * // Execute the PreparedStatement
 * pstmt.executeQuery();
 * return pstmt;
 * }
 *
 */
