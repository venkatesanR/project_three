//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DBConnection.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.Connection;
import com.addval.utils.LogFileMgr;
import java.util.Date;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLWarning;
import java.util.Map;

import org.apache.log4j.Logger;

import com.addval.utils.GenUtl;

public class DBConnection implements Connection {
	private Logger _log = null;
	private Logger _errorlog = null;
	private Logger _batcherrorlog = null;
	private Connection _conn = null;
	private Date _timestamp = null;
	private int _connId = 0;
	private static int _connCounter = 0;


	/**
	 * @param log
	 * @param errorlog
	 * @param conn
	 * @roseuid 37EA62390131
	 */
	public DBConnection(Logger log, Logger errorlog, Logger batcherrorlog, Connection conn) {
        _log  				= log;
        _errorlog   		= errorlog;
        _batcherrorlog   	= batcherrorlog;
        _conn 				= conn;
        _timestamp 			= new Date();
        _connCounter++;
        _connId     		= _connCounter;
	}


	/**
	 * @param log
	 * @param conn
	 * @roseuid 37EA62390131
	 */
	public DBConnection(Logger log, Connection conn) {
        _log  		= log;
        _conn 		= conn;
        _timestamp 	= new Date();
        _connCounter++;
        _connId     = _connCounter;
	}

	/**
	 * Package-level access method for the _conn property.
	 * This is intended solely for use by DBPoolMgr, so it can report instance ID info for the underlying Connection in logged messages.
	 *
	 * @return   the current value of the _conn property
	 */
	public Connection getUnderlyingConnection() {

   	   return _conn;
	}

	/**
	 * Package-level access method for the _conn property.
	 * This is intended solely for use by DBPoolMgr, so it can report instance ID info for the underlying Connection in logged messages.
	 *
	 * @return   the current value of the _conn property
	 */
	public void clearUnderlyingConnection() {
       logSql( "logConnectionAction - clearUnderlyingConnection()[" + _connId + "]" );
   	   _conn = null;
	}

	/**
	 * Access method for the _connId property.
	 *
	 * @return   the current value of the _connId property
	 */
	public int getConnId() {

   	   return _connId;
	}

	/**
	 * Access method for the _connCounter property.
	 *
	 * @return   the current value of the _connCounter property
	 */
	public static int getConnCounter() {

		return _connCounter;
	}

	/**
	 * public Statement createStatement()
	 *                           throws SQLException
	 * @return java.sql.Statement
	 * @throws SQLException
	 * @roseuid 37EA651D0379
	 */
	public Statement createStatement() throws SQLException {
        try {
            logInfo( "createStatement()[" + _connId + "]" );
            return new DBStatement( _log, _errorlog, _batcherrorlog, _conn.createStatement() );
        }
        catch ( SQLException e ) {
            logError(  e );
            throw e;
        }
	}

	/**
	 * public PreparedStatement prepareStatement(String sql,
	 *                                           int resultSetType,
	 *                                           int resultSetConcurrency)
	 *                                    throws SQLException
	 *
	 * @param sql
	 * @return java.sql.PreparedStatement
	 * @throws SQLException
	 * @roseuid 37EA65480046
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException {
        try {
            logSql( "prepareStatement()[" + _connId + "] ( " + sql + " )" );

            DBPreparedStatement stmt = new DBPreparedStatement( _log, _errorlog, _batcherrorlog, _conn.prepareStatement( sql ) );
            stmt.setPreparedSQLTrace("prepareStatement()[" + _connId + "] ( " + sql + " )");
            return stmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public CallableStatement prepareCall(String sql,
	 *                                      int resultSetType,
	 *                                      int resultSetConcurrency)
	 *                               throws SQLException
	 * @param sql
	 * @return java.sql.CallableStatement
	 * @throws SQLException
	 * @roseuid 37EA655E02AB
	 */
	public CallableStatement prepareCall(String sql) throws SQLException {
        try {
            logSql( "prepareCall()[" + _connId + "] ( " + sql + " )" );
            DBCallableStatement cstmt = new DBCallableStatement( _log, _errorlog, _batcherrorlog, _conn.prepareCall( sql ) );
            cstmt.setPreparedSQLTrace( "prepareCall()[" + _connId + "] ( " + sql + " )");
            return cstmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public String nativeSQL(String sql)
	 *                           throws SQLException
	 * @param sql
	 * @return java.lang.String
	 * @throws SQLException
	 * @roseuid 37EA657301E3
	 */
	public String nativeSQL(String sql) throws SQLException {
        try {
            logSql( "nativeSQL()[" + _connId + "] ( " + sql + " )" );
            return _conn.nativeSQL( sql );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void setAutoCommit(boolean autoCommit)
	 *                    throws SQLException
	 * @param autoCommit
	 * @throws SQLException
	 * @roseuid 37EA65B20388
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException {
        try {
            logInfo( "setAutoCommit()[" + _connId + "] ( " + autoCommit + " )" );
            _conn.setAutoCommit( autoCommit );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void setAutoCommit(boolean autoCommit)
	 *                    throws SQLException
	 * @return boolean
	 * @throws SQLException
	 * @roseuid 37EA65E700DB
	 */
	public boolean getAutoCommit() throws SQLException {
        try {
            return _conn.getAutoCommit();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void commit()
	 *             throws SQLException
	 *
	 * @throws SQLException
	 * @roseuid 37EA660202ED
	 */
	public void commit() throws SQLException {
        try {
            logInfo( "commit()[" + _connId + "]" );
            _conn.commit();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * @throws SQLException
	 * @roseuid 37EA662800B6
	 */
	public void rollback() throws SQLException {
        try {
            logInfo( "rollback()["  + _connId + "]" );
            _conn.rollback();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * @throws SQLException
	 * @roseuid 37EA66430191
	 */
	public void close() throws SQLException {

		// @@@@@@@@@@ TODO: add check here to make sure the only caller is DBPoolMgr;  OR change to package-protected access? (NO, violates Connection)
		// OR... retain ref to DBPoolMgr, convert this call to releaseConnection?


        try {
			logInfo( "close()[" + _connId + ", instance="+ this + ", wrappedInstance=" + _conn + "]" );

/*
			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEMPORARY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
			// This is added TEMPORARILY to help track down possible causes of connection pool problems.
			com.addval.utils.StackTraceUtl stackTraceUtl = new com.addval.utils.StackTraceUtl();
			String fullstack = stackTraceUtl.filter().toString();
			org.apache.log4j.Logger localLogger = com.addval.utils.LogMgr.getLogger("SQL");
			localLogger.info("@@@@@ TEMPORARY LOG OUTPUT: DBConnection.close() has been called by someone other than DBPoolMgr; \nStack Trace:\n"+fullstack);
			//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ TEMPORARY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
            _conn.close();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * @return boolean
	 * @throws SQLException
	 * @roseuid 37EA665C036E
	 */
	public boolean isClosed() throws SQLException {
        try {
            return _conn.isClosed();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public DatabaseMetaData getMetaData()
	 *                              throws SQLException
	 * @return java.sql.DatabaseMetaData
	 * @throws SQLException
	 * @roseuid 37EA667502B6
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
        try {
            logInfo( "getMetaData()[" + _connId + "]" );
            return _conn.getMetaData();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * @param readOnly
	 * @throws SQLException
	 * @roseuid 37EA66A702B8
	 */
	public void setReadOnly(boolean readOnly) throws SQLException {
        try {
            logInfo( "setReadOnly()[" + _connId + "] (" + readOnly + ")" );
            _conn.setReadOnly( readOnly );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * @return boolean
	 * @throws SQLException
	 * @roseuid 37EA66BF0190
	 */
	public boolean isReadOnly() throws SQLException {
        try {
            return _conn.isReadOnly();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void setCatalog(String catalog)
	 *                 throws SQLException
	 *
	 * @param catalog
	 * @throws SQLException
	 * @roseuid 37EA66DB0385
	 */
	public void setCatalog(String catalog) throws SQLException {
        try {
            logInfo( "setCatalog()[" + _connId + "] ( " + catalog + " )" );
            _conn.setCatalog( catalog );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void setCatalog(String catalog)
	 *                 throws SQLException
	 *
	 * @return java.lang.String
	 * @throws SQLException
	 * @roseuid 37EA67080299
	 */
	public String getCatalog() throws SQLException {
        try {
            return _conn.getCatalog();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void setTransactionIsolation (int level)
	 *                              throws SQLException
	 * @param level
	 * @throws SQLException
	 * @roseuid 37EA672E01B7
	 */
	public void setTransactionIsolation(int level) throws SQLException {
        try {
            logInfo( "setTransactionIsolation()[" + _connId + "] ( " + level + " )" );
            _conn.setTransactionIsolation( level );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public int getTransactionIsolation()
	 *                             throws SQLException
	 * @return int
	 * @throws SQLException
	 * @roseuid 37EA676303E4
	 */
	public int getTransactionIsolation() throws SQLException {
        try {
            return _conn.getTransactionIsolation();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public SQLWarning getWarnings()                       throws SQLException
	 * @return java.sql.SQLWarning
	 * @throws SQLException
	 * @roseuid 37EA678B0265
	 */
	public SQLWarning getWarnings() throws SQLException {
        try {
            return _conn.getWarnings();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void clearWarnings()                   throws SQLException
	 * @throws SQLException
	 * @roseuid 37EA639600E2
	 */
	public void clearWarnings() throws SQLException {
        try {
            logInfo( "clearWarnings()[" + _connId + "]" );
            _conn.clearWarnings();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public Statement createStatement(int resultSetType,
	 *                                  int resultSetConcurrency)
	 *                           throws SQLException
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @return java.sql.Statement
	 * @throws SQLException
	 * @roseuid 37EA63C1030B
	 */
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            logInfo( "createStatement()[" + _connId + "] ( " + resultSetType + ", " + resultSetConcurrency + ") " );
            return new DBStatement( _log, _errorlog, _batcherrorlog, _conn.createStatement( resultSetType, resultSetConcurrency ) );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public PreparedStatement prepareStatement(String sql,
	 *                                           int resultSetType,
	 *                                           int resultSetConcurrency)
	 *                                    throws SQLException
	 *
	 * @param sql
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @return java.sql.PreparedStatement
	 * @throws SQLException
	 * @roseuid 37EA640A01ED
	 */
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            logSql( "prepareStatement()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " + resultSetConcurrency + " )" );

            DBPreparedStatement stmt = new DBPreparedStatement( _log, _errorlog, _batcherrorlog, _conn.prepareStatement( sql, resultSetType, resultSetConcurrency ) );
            stmt.setPreparedSQLTrace("prepareStatement()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " + resultSetConcurrency + " )");
            return stmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public CallableStatement prepareCall(String sql,
	 *                                      int resultSetType,
	 *                                      int resultSetConcurrency)
	 *                               throws SQLException
	 * @param sql
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @return java.sql.CallableStatement
	 * @throws SQLException
	 * @roseuid 37EA6475002F
	 */
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        try {
            logSql( "prepareCall()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " +  resultSetConcurrency + " )" );
            DBCallableStatement cstmt = new DBCallableStatement( _log, _errorlog, _batcherrorlog, _conn.prepareCall( sql, resultSetType, resultSetConcurrency ) );
            cstmt.setPreparedSQLTrace("prepareCall()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " + resultSetConcurrency + " )");
            return cstmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public Map getTypeMap()               throws SQLException
	 * @return java.util.Map
	 * @throws SQLException
	 * @roseuid 37EA64AB03C6
	 */
	public Map getTypeMap() throws SQLException {
        try {
            return _conn.getTypeMap();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public void setTypeMap(Map map)
	 *                 throws SQLException
	 *
	 * @param map
	 * @throws SQLException
	 * @roseuid 37EA64CA0149
	 */
	public void setTypeMap(Map map) throws SQLException {
        try {
            logInfo( "setTypeMap()[" + _connId + "] ( " + map + ") " );
            _conn.setTypeMap( map );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	/**
	 * public Date getTimestamp()
	 *
	 * @return java.util.Date
	 * @roseuid 3A4BE686004B
	 */
	public Date getTimestamp() {
	   return _timestamp;
	}

	public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws java.sql.SQLException {
		try {
            logSql( "createStatement()[" + _connId + "] ( " + resultSetType + ", " + resultSetConcurrency + ", " + resultSetHoldability + " )" );
            return new DBStatement( _log, _errorlog, _batcherrorlog, _conn.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability ) );
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public int getHoldability() throws java.sql.SQLException {
		try {
            return _conn.getHoldability();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws java.sql.SQLException {
		try {
            logSql( "prepareCall()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " +  resultSetConcurrency + ", " + resultSetHoldability + " )" );
            DBCallableStatement cstmt = new DBCallableStatement( _log, _errorlog, _batcherrorlog, _conn.prepareCall( sql, resultSetType, resultSetConcurrency, resultSetHoldability ) );
            cstmt.setPreparedSQLTrace("prepareCall()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " +  resultSetConcurrency + ", " + resultSetHoldability + " )");
            return cstmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws java.sql.SQLException {
        try {
            logSql( "prepareStatement()[" + _connId + "] ( " + sql + ", " + GenUtl.arrayToString(columnNames) + " )" );

            DBPreparedStatement stmt = new DBPreparedStatement( _log, _errorlog, _batcherrorlog, _conn.prepareStatement( sql, columnNames ) );
            stmt.setPreparedSQLTrace("prepareStatement()[" + _connId + "] ( " + sql + ", " + GenUtl.arrayToString(columnNames) + " )" );
            return stmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }

	}

	public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws java.sql.SQLException {
        try {
            logSql( "prepareStatement()[" + _connId + "] ( " + sql + ", " + autoGeneratedKeys + " )" );

            DBPreparedStatement stmt = new DBPreparedStatement( _log, _errorlog, _batcherrorlog, _conn.prepareStatement( sql, autoGeneratedKeys ) );
            stmt.setPreparedSQLTrace("prepareStatement()[" + _connId + "] ( " + sql + ", " + autoGeneratedKeys + " )" );
            return stmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }

	}

	public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws java.sql.SQLException {
        try {
            logSql( "prepareStatement()[" + _connId + "] ( " + sql + ", " + GenUtl.arrayToString(columnIndexes) + " )" );
            DBPreparedStatement stmt = new DBPreparedStatement( _log, _errorlog, _batcherrorlog, _conn.prepareStatement( sql, columnIndexes ) );
            stmt.setPreparedSQLTrace("prepareStatement()[" + _connId + "] ( " + sql + ", " + GenUtl.arrayToString(columnIndexes) + " )");
            return stmt;
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }

	}

	public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws java.sql.SQLException {
		try {
            logSql( "prepareStatement()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " +  resultSetConcurrency + ", " + resultSetHoldability + " )" );

            DBPreparedStatement stmt = new DBPreparedStatement( _log, _errorlog, _batcherrorlog, _conn.prepareStatement( sql, resultSetType, resultSetConcurrency, resultSetHoldability ) );
            stmt.setPreparedSQLTrace("prepareStatement()[" + _connId + "] ( " + sql + ", " + resultSetType + ", " +  resultSetConcurrency + ", " + resultSetHoldability + " )" );
            return stmt;

        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public void releaseSavepoint(java.sql.Savepoint savepoint) throws java.sql.SQLException {
		try {
            logSql( "releaseSavepoint()[" + _connId + "] ( " + savepoint + " )" );
            _conn.releaseSavepoint(savepoint);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public void rollback(java.sql.Savepoint savepoint) throws java.sql.SQLException {
		try {
            logSql( "rollback()[" + _connId + "] ( " + savepoint + " )" );
            _conn.rollback(savepoint);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public void setHoldability(int holdability) throws java.sql.SQLException {
		try {
            logSql( "setHoldability()[" + _connId + "] ( " + holdability + " )" );
            _conn.setHoldability(holdability);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.sql.Savepoint setSavepoint() throws java.sql.SQLException {
		try {
            logSql( "setSavepoint()[" + _connId + "] " );
            return _conn.setSavepoint();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.sql.Savepoint setSavepoint(String name) throws java.sql.SQLException {
		try {
            logSql( "setSavepoint()[" + _connId + "] ( " + name + " )"  );
            return _conn.setSavepoint(name);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

    protected void logInfo( String message )
    {
        if (_log != null)
            _log.info( message );
    }

    protected void logSql( String message )
    {
        logInfo( message );
    }


    protected void logError(Exception exception )
    {
        if (_log != null)
            _log.error( exception );
    }


/* Needed for JDK 1.6 migration */

	public java.sql.Struct createStruct(String name, java.lang.Object[] obj) throws java.sql.SQLException {
		try {
            logSql( "createStruct()[" + _connId + "] ( " + name + " )"  );
            return _conn.createStruct(name, obj);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}


	public java.sql.Array createArrayOf(String name, java.lang.Object[] obj) throws java.sql.SQLException {
		try {
            logSql( "createArrayOf()[" + _connId + "] ( " + name + " )"  );
            return _conn.createArrayOf(name, obj);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.util.Properties getClientInfo() throws java.sql.SQLException {
		try {
            logSql( "getClientInfo()[" + _connId + "]" );
            return _conn.getClientInfo();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}


	public void setClientInfo(java.util.Properties prop)  {
		try {
            logSql( "setClientInfo()[" + _connId + "]" );
            _conn.setClientInfo(prop);
        }
        catch ( SQLException e ) {
            logError( e );
        }
	}


	public String getClientInfo(String name) throws java.sql.SQLException {
		try {
            logSql( "getClientInfo()[" + _connId + "]" );
            return _conn.getClientInfo(name);
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}


	public void setClientInfo(String arg0, String arg1)  {
		try {
            logSql( "setClientInfo()[" + _connId + "]" );
            _conn.setClientInfo(arg0, arg1);
        }
        catch ( SQLException e ) {
            logError( e );
        }
	}

	public boolean isValid(int i) throws java.sql.SQLException {

		return _conn.isValid(i);

	}


	public java.sql.SQLXML createSQLXML() throws java.sql.SQLException {
		try {
            logSql( "createSQLXML()[" + _connId + "] "   );
            return _conn.createSQLXML();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.sql.NClob createNClob() throws java.sql.SQLException {
		try {
            logSql( "createNClob()[" + _connId + "] "   );
            return _conn.createNClob();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}


	public java.sql.Blob createBlob() throws java.sql.SQLException {
		try {
            logSql( "createBlob()[" + _connId + "] "   );
            return _conn.createBlob();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public java.sql.Clob createClob() throws java.sql.SQLException {
		try {
            logSql( "createClob()[" + _connId + "] "   );
            return _conn.createClob();
        }
        catch ( SQLException e ) {
            logError( e );
            throw e;
        }
	}

	public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
		return _conn.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
		return _conn.unwrap(iface);
	}


}
