//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DBStatement.java

/**
 * Copyright
 * AddVal Technology Inc.
 */


package com.addval.dbutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.addval.utils.GenUtl;

/**
 * Encapsulates the Database Statement Object.
 * All database operations are performed through this.
 * Follows JDBC 2.0 specification.  You can look at
 * java.sql docs for more information
 * @author Sankar Dhanushkodi
 * @version $Revision$@author AddVal Technology Inc.
 */
public class DBStatement implements Statement {

	private Logger _logger = com.addval.utils.LogMgr.getLogger("SQL");
	private Logger _errorlog = com.addval.utils.LogMgr.getLogger( "ERROR_SQL" );;
	private Logger _batcherrorlog = com.addval.utils.LogMgr.getLogger( "BATCH_ERROR_SQL" );


	private Statement _stmt = null;
	private StringBuffer _batchSQL = null;


	/**
	 * Constructor. Initializes the internal log file and the Statement
	 * variable
	 * @param log LogFileMgr
	 * @param errorlog LogFileMgr
	 * @param stmt Statement
	 * @exception:
	 * @roseuid 37EBBCD3007A
	 */
	public DBStatement(Logger log,Logger errorlog, Logger batcherrorlog, Statement stmt) {
		_logger  			= log;
    	_errorlog 		= errorlog;
    	_batcherrorlog 	= batcherrorlog;
        _stmt 			= stmt;
	}


	/**
	 * Constructor. Initializes the internal log file and the Statement
	 * variable
	 * @param log LogFileMgr
	 * @param stmt Statement
	 * @exception:
	 * @roseuid 37EBBCD3007A
	 */
	public DBStatement(Logger log, Statement stmt) {
        _logger  = log;
        _stmt = stmt;
	}

	/**
	 * Executes the given SQL string on the database that returns
	 * a single result set.
	 * @param sql String. A valid SQL Statement
	 * @return  ResultSet
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB8A8036F
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
      try {
         logSql( "executeQuery( " + sql +" )" );
         DBResultSet rs = new DBResultSet( _stmt.executeQuery( sql ) );
		 rs.setStatement( this );
		 return rs;
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "executeQuery( " + sql +" )" , e );
         throw e;
      }
	}



	/**
	 * Executes an SQL INSERT, UPDATE, or DELETE
	 * statement that does not have parameter placeholders. This
	 * may also be used to execute SQL statements that return
	 * nothing such as SQL DDL statements (CREATE TABLE,
	 * DROP TABLE, etc.,)
	 * @param sql String. SQL INSERT, UPDATE or DELETE or
	 * a DDL statement.
	 * @return  int - indicating the number of rows affected.
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB8D600AE
	 */
	public int executeUpdate(String sql) throws SQLException {
      try {
         logSql( "executeUpdate( " + sql +" )" );
         return _stmt.executeUpdate( sql );
      }
      catch ( SQLException e ) {
      	// Its the responsibility of the caller to log the error
         logDebugDetails( e.getMessage() );
         logErrorDetails("executeUpdate( " + sql + ")" , e);
         throw e;
      }
	}

	/**
	 * Closes the statement.  Although JDBC documentation
	 * states that the Statement objects will be closed
	 * automatically by the Java garbage collector -- it does not
	 * seem to happen.  It is recommended as a good
	 * programming practice to close the Statement explicitly when
	 * they are no longer needed.
	 * Errors such as "Out of Database Connections" normaly
	 * indicate that there are too many Statement objects open.
	 * @param:
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB8EF038F
	 */
	public void close() throws SQLException {
      try {
         logInfoDetails( "close()" );
         _stmt.close();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "close()",e);
         throw e;
      }
	}
	
	public void close(int connId) throws SQLException {
	      try {
	         logInfoDetails( "close()[" + connId + "]"  );
	         _stmt.close();
	      }
	      catch ( SQLException e ) {
	         logDebugDetails( e.getMessage() );
	         logErrorDetails( "close()",e);
	         throw e;
	      }
		}

	/**
	 * The maximum field size (in bytes) is set to limit the size of
	 * data that can be returned. Applies only to fields of type
	 * BINARY, VARBINARY, LONGVARBINARY, CHAR,
	 * VARCHAR and LONGVARCHAR.
	 * Maximum field size should be greater than 256 bytes.
	 * @param:
	 * @return: Number of bytes that a ResultSet column may
	 * contain; 0 indicates that there is no limit.
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB90D0017
	 */
	public int getMaxFieldSize() throws SQLException {
      try {
         logInfoDetails( "getMaxFieldSize()" );
         return _stmt.getMaxFieldSize();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getMaxFieldSize()",e);
         throw e;
      }
	}

	/**
	 * Sets the maximum size for a column in a result set.
	 * Ideally this should be set to a value greater than 256 bytes.
	 * @param max int
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @see DBStatement#getMaxFieldSize
	 * @roseuid 37EBB92F00E8
	 */
	public void setMaxFieldSize(int max) throws SQLException {
      try {
         logInfoDetails( "setMaxFieldSize( " + max + " )" );
         _stmt.setMaxFieldSize( max );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setMaxFieldSize( " + max + " ) " , e );
         throw e;
      }
	}

	/**
	 * Returns the maximum number of rows that a ResultSet
	 * object may contain.
	 * @param:
	 * @return Maximum number of rows that a ResultSet can
	 * contain - int
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB94B0373
	 */
	public int getMaxRows() throws SQLException {
      try {
         logInfoDetails( "getMaxRows()" );
         return _stmt.getMaxRows();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getMaxRows()" , e  );
         throw e;
      }
	}

	/**
	 * Sets the limit for the maximum number of rows in a
	 * ResultSet object to max.
	 * @param max int
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB976036B
	 */
	public void setMaxRows(int max) throws SQLException {
      try {
         logInfoDetails( "setMaxRows( " + max + " )" );
         _stmt.setMaxRows( max );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setMaxRows( " + max + ")" , e );
         throw e;
      }
	}

	/**
	 * Sets the Statement object's escape scanning mode to
	 * "enable". When enable is ture (the default), the driver
	 * will scan for any escape syntax and do escape
	 * substitution before sending the escaped SQL statement
	 * to the database. Note: This will not work for
	 * PreparedStatements.
	 * @param enable boolean. true to enable escape scanning.
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB98E026B
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
      try {
         logInfoDetails( "setEscapeProcessing( " + enable + " )" );
         _stmt.setEscapeProcessing( enable );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setEscapeProcessing( " + enable + " )" , e );
         throw e;
      }
	}

	/**
	 * The query time out limit is the number of seconds the
	 * driver will wait for a Statement object to execute. If the
	 * limit is exceeded a SQLException is thrown.
	 * @param:
	 * @return Query timeout limit in seconds - int; 0 means no
	 * timeout limit.
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB9CE0303
	 */
	public int getQueryTimeout() throws SQLException {
      try {
         logInfoDetails( "getQueryTimeout()" );
         return _stmt.getQueryTimeout();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getQueryTimeout()" , e );
         throw e;
      }
	}

	/**
	 * Sets to "seconds" the time limit for the number or seconds
	 * a driver will wait for a Statement object to be executed.
	 * @param seconds - int
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBB9F000C7
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
      try {
         logInfoDetails( "setQueryTimeout( " + seconds + " )" );
         _stmt.setQueryTimeout( seconds );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setQueryTimeout( " + seconds + " )" , e );
         throw e;
      }
	}

	/**
	 * This method an be used by one thread to cancel a
	 * statement that is being executed by another thread
	 * if the driver and DBMS both support aborting a SQL
	 * statement
	 * @param:
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBA1D036B
	 */
	public void cancel() throws SQLException {
      try {
         logInfoDetails( "cancel()" );
         _stmt.cancel();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "cancel()" , e );
         throw e;
      }
	}

	/**
	 * Returns the first warning reported by calls on this
	 * Statement object. Subsequent warnings of there are any
	 * are chained to this first warning.  A Statement objects
	 * warning chain is automatically cleared each time it is
	 * executed.
	 * @param:
	 * @return First SQLWarning; NULL if there are no warnings.
	 * @throws java.sql.SQLException
	 * @return SQLException
	 * @roseuid 37EBBA3902CB
	 */
	public SQLWarning getWarnings() throws SQLException {
      try {
         logInfoDetails( "getWarnings()" );
         return _stmt.getWarnings();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getWarnings()" , e );
         throw e;
      }
	}

	/**
	 * Clears the warnings reported for this Statement object.
	 * @param:
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBA670295
	 */
	public void clearWarnings() throws SQLException {
      try {
         logInfoDetails( "clearWarnings()" );
         _stmt.clearWarnings();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "clearWarnings()" , e );
         throw e;
      }
	}

	/**
	 * Sets to "name" the SQL cursor name that will be used by
	 * subsequent Statement excute methods.  This name can
	 * then be used in SQL positioned update and positioned
	 * delete statemets.
	 * @param name String. The new cursor name which must
	 * be unique within a connection
	 * @throws java.sql.SQLException
	 * @roseuid 37EBBA7D017E
	 */
	public void setCursorName(String name) throws SQLException {
      try {
         logSql( "setCursorName( " + name + " )" );
         _stmt.setCursorName( name );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setCursorName( " + name + " )" , e );
         throw e;
      }
	}

	/**
	 * Executes a SQL statement that may return one or more
	 * result sets, one or more update counts, or any
	 * combination of result sets and update counts.
	 * A call to this method executes a SQL statement and returns
	 * true if the first result is a result set; it returns false if the first
	 * result set is an update count. One needs to call either the
	 * method getResultSet or getUpdateCount to actually
	 * retrieve the result and the method getMoreResults to move
	 * to any subsequent results.
	 * @param sql  String. Any SQL Statement
	 * @return true if the first result is a ResultSet or false if it is an
	 * integer
	 * @throws java.sql.SQLException
	 * @return SQLException
	 * @roseuid 37EBBACE0121
	 */
	public boolean execute(String sql) throws SQLException {
      try {
         logSql( "execute( " + sql + " )" );

         return _stmt.execute( sql );

      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "execute( " + sql +" )" , e );
         throw e;
      }
	}

	/**
	 * Accessor to the underlying ResultSet in the class.
	 * @param:
	 * @return ResultSet
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBAF8009F
	 */
	public ResultSet getResultSet() throws SQLException {
      try {
         logInfoDetails( "getResultSet()" );

         DBResultSet rs = new DBResultSet( _stmt.getResultSet() );
         rs.setStatement( this );

         return rs;
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getResultSet()" , e );
         throw e;
      }
	}

	/**
	 * This method should be called only after a call to execute
	 * or getMoreResults, and it should be called only once
	 * per result.
	 * If this method returns an integer greater than 0, the integer
	 * represents the number of rows affected by a statement
	 * modifying a table. 0 indicates either that no rows were
	 * affected or that the SQL statement was a DDL. A return
	 * value of -1 means that the result is a ResultSet object or that
	 * there are no more results.
	 * @param:
	 * @return > 0 - Number of rows affected by an update
	 * operation
	 * @throws java.sql.SQLException
	 * @return 0 - No rows were affected or that the operation was
	 * a DDL command
	 * @return -1 - The result is a ResultSet object or that there are
	 * no more results
	 * @exception  SQLException
	 * @roseuid 37EBBB1A02C4
	 */
	public int getUpdateCount() throws SQLException {
      try {
         logInfoDetails( "getUpdateCount()" );
         return _stmt.getUpdateCount();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getUpdateCount()" , e );
         throw e;
      }
	}

	/**
	 * Moves to Statement object's next result and implicitly closes
	 * any current ResultSet object. The method getMoreResults
	 * is used after a Statement has been executed with a call
	 * to the method "execute" and the method "getResultSet" or
	 * "getUpdateCount" has been called
	 * @param:
	 * @return true if the next result is a ResultSet. false if it is an
	 * integer.
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBB4201B4
	 */
	public boolean getMoreResults() throws SQLException {
      try {
         logInfoDetails( "getMoreResults()" );
         return _stmt.getMoreResults();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getMoreResults()" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Gives the driver a hint as to the direction in which
	 * the rows in a result set will be processed. The hint applies
	 * only to result sets created using this Statement object. The
	 * default value is ResultSet.FETCH_FORWARD.
	 * Note that this method sets the default fetch direction for
	 * result sets generated by this Statement object. Each result
	 * set has its own methods for getting and setting its own fetch
	 * direction.
	 * @param  direction - the initial direction for processing rows
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error occurs or the given
	 * direction is not one of ResultSet.FETCH_FORWARD, ResultSet.FETCH_REVERSE, or
	 * ResultSet.FETCH_UNKNOWN
	 * @roseuid 37EBBB66010B
	 */
	public void setFetchDirection(int direction) throws SQLException {
      try {
         logInfoDetails( "setFetchDirection( " + direction + " )" );
         _stmt.setFetchDirection( direction );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setFetchDirection( " + direction + " )" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Retrieves the direction for fetching rows from
	 * database tables that is the default for result sets generated
	 * from this Statement object. If this Statement object has not
	 * set a fetch direction by calling the method
	 * setFetchDirection, the return value is
	 * implementation-specific.
	 * @param:
	 * @return the default fetch direction for result sets generated
	 * from this Statement object
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBB870253
	 */
	public int getFetchDirection() throws SQLException {
      try {
         logInfoDetails( "getFetchDirection()" );
         return _stmt.getFetchDirection();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getFetchDirection()" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Gives the JDBC driver a hint as to the number of
	 * rows that should be fetched from the database when more
	 * rows are needed. The number of rows specified affects only
	 * result sets created using this statement. If the value
	 * specified is zero, then the hint is ignored. The default value
	 * is zero.
	 * @param rows - the number of rows to fetch
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error occurs, or the condition 0
	 * <= rows <= this.getMaxRows() is not satisfied.
	 * @roseuid 37EBBBA10052
	 */
	public void setFetchSize(int rows) throws SQLException {
      try {
         logInfoDetails( "setFetchSize( " + rows + " )" );
         _stmt.setFetchSize( rows );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "setFetchSize( " + rows + " )" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Retrieves the number of result set rows that is the
	 * default fetch size for result sets generated from this
	 * Statement object. If this Statement object has not set a
	 * fetch size by calling the method setFetchSize, the return
	 * value is implementation-specific.
	 * @param:
	 * @return the default fetch size for result sets generated from this Statement
	 * object
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error occurs
	 * @roseuid 37EBBBCA0029
	 */
	public int getFetchSize() throws SQLException {
      try {
         logInfoDetails( "getFetchSize()" );
         return _stmt.getFetchSize();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getFetchSize()" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Retrieves the result set concurrency.
	 * @param:
	 * @return int
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBBE50168
	 */
	public int getResultSetConcurrency() throws SQLException {
      try {
         logInfoDetails( "getResultSetConcurrency()" );
         return _stmt.getResultSetConcurrency();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getResultSetConcurrency()" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Determine the result set type.
	 * @param:
	 * @return int
	 * @throws java.sql.SQLException
	 * @exception SQLException
	 * @roseuid 37EBBC00018F
	 */
	public int getResultSetType() throws SQLException {
      try {
         logInfoDetails( "getResultSetType()" );
         return _stmt.getResultSetType();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getResultSetType()" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Adds a SQL command to the current batch of
	 * commmands for the statement. This method is optional.
	 * @param sql - typically this is a static SQL INSERT or
	 * UPDATE statement
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error
	 * occurs, or the driver does not support batch statements
	 * @roseuid 37EBBC180270
	 */
	public void addBatch(String sql) throws SQLException {
      try {
         logSql( "addBatch( " + sql + " )" );

		 saveBatchSQLForErrorLogging("addBatch( " + sql + " )");

         _stmt.addBatch( sql );
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "addBatch( " + sql +" )" , e );
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Makes the set of commands in the current batch empty. This method is
	 * optional.
	 * @param:
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error
	 * occurs or the driver does not support batch statements
	 * @roseuid 37EBBC360042
	 */
	public void clearBatch() throws SQLException {
      try {
         logInfoDetails( "clearBatch()" );

		 clearBatchSQLForErrorLogging();

         _stmt.clearBatch();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "clearBatch()" , e );
		 clearBatchSQLForErrorLogging();
         throw e;
      }
	}

	/**
	 * JDBC 2.0 Submits a batch of commands to the database for
	 * execution. This method is optional.
	 * @param:
	 * @return: an array of update counts containing one element
	 * for each command in the batch. The array is ordered
	 * according to the order in which commands were inserted
	 * into the batch.
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error
	 * occurs or the driver does not support batch statements
	 * @roseuid 37EBBC52033B
	 */
	public int[] executeBatch() throws SQLException {

      try {
         logInfoDetails( "executeBatch()" );
         int [] ret 		 =  _stmt.executeBatch();
		 clearBatchSQLForErrorLogging();

         return ret;
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "executeBatch( " + getBatchSQLForErrorLogging() +" )" , e  );
         if(_errorlog != null) {
//	         if(_errorlog.debugOn() && (ret != null)) _errorlog.logSql("executeBatch( " + GenUtl.arrayToString(ret) + " )" );
		 }

         logBatchErrorDetails( "executeBatch( " + getBatchSQLForErrorLogging() +" )" , e );
         if ((_batcherrorlog != null) && (_batcherrorlog.isDebugEnabled()))
         {
//	         if(ret != null) _batcherrorlog.logSql("executeBatch( " + GenUtl.arrayToString(ret) + " )" );
		 }

		 clearBatchSQLForErrorLogging();

         throw e;
      }
	}

	/**
	 * JDBC 2.0 Returns the Connection object that produced this
	 * Statement object.
	 * @param:
	 * @return the connection that produced this statement
	 * @throws java.sql.SQLException
	 * @exception SQLException - if a database access error occurs
	 * @roseuid 37EBBC7B01C8
	 */
	public Connection getConnection() throws SQLException {
      try {
         logInfoDetails( "getConnection()" );
         return _stmt.getConnection();
      }
      catch ( SQLException e ) {
         logDebugDetails( e.getMessage() );
         logErrorDetails( "getConnection" , e );
         throw e;
      }
	}

	public boolean execute(String sql, String[] columnNames) throws java.sql.SQLException
	{
		try {
			logSql( "execute( " + sql + ", " + GenUtl.arrayToString(columnNames) + " )" );
			return _stmt.execute(sql, columnNames);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "execute( " + sql + ", " + GenUtl.arrayToString(columnNames) + " )" , e );
			throw e;
		}
	}

	public boolean execute(String sql, int[] columnIndexes) throws java.sql.SQLException
	{
		try {
			logSql( "execute( " + sql + ", " + GenUtl.arrayToString(columnIndexes) + " )" );
			return _stmt.execute(sql, columnIndexes);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "execute( " + sql + ", " + GenUtl.arrayToString(columnIndexes) + " )" , e );
			throw e;
		}
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws java.sql.SQLException
	{
		try {
			logSql( "execute( " + sql + ", " + autoGeneratedKeys + " )" );
			return _stmt.execute(sql, autoGeneratedKeys);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "execute( " + sql + ", " + autoGeneratedKeys + " )" , e );

			throw e;
		}
	}

	public int executeUpdate(String sql, String[] columnNames) throws java.sql.SQLException
	{
		try {
			logSql( "executeUpdate( " + sql + ", " + GenUtl.arrayToString(columnNames) + " )" );
			return _stmt.executeUpdate(sql, columnNames);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "executeUpdate( " + sql + ", " + GenUtl.arrayToString(columnNames) + " )" , e );

			throw e;
		}
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws java.sql.SQLException
	{
		try {
			logSql( "executeUpdate( " + sql + ", " + autoGeneratedKeys + " )" );
			return _stmt.executeUpdate(sql, autoGeneratedKeys);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "executeUpdate( " + sql + ", " + autoGeneratedKeys + " )" , e );
			throw e;
		}
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws java.sql.SQLException
	{
		try {
			logSql( "executeUpdate( " + sql + ", " + GenUtl.arrayToString(columnIndexes) + " )" );
			return _stmt.executeUpdate(sql, columnIndexes);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "executeUpdate( " + sql + ", " + GenUtl.arrayToString(columnIndexes) + " )" ,  e );
			throw e;
		}
	}

	public java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException
	{
		try {
			logSql( "getGeneratedKeys()" );
			return _stmt.getGeneratedKeys();
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "getGeneratedKeys()" , e );

			throw e;
		}
	}

	public boolean getMoreResults(int current) throws java.sql.SQLException
	{
		try {
			logSql( "getMoreResults(" + current + ")" );
			return _stmt.getMoreResults(current);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "getGeneratedKeys()" , e );
			throw e;
		}
	}

	public int getResultSetHoldability() throws java.sql.SQLException
	{
		try {
			logSql( "getResultSetHoldability()" );
			return _stmt.getResultSetHoldability();
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
			logErrorDetails( "getResultSetHoldability()" , e );
			throw e;
		}
	}


	/**
	** Save the batchSQL in a buffer for error logging in a separate error log in case of an error
	** with the details of the error
	**
	** Batch error logging is turned on only if the BATCH_ERR_SQL_LOG is defined in the log file and SQL Trace is
	** turned on. This is to prevent memory issues due to saving too many batch SQLs
	**/
	public void saveBatchSQLForErrorLogging(String batchSQL)
	{
         if ((_batcherrorlog != null) && (_batcherrorlog.isDebugEnabled()))
         {
			if (_batchSQL == null)
			{
				 _batchSQL = new StringBuffer();
			}

         	_batchSQL = _batchSQL.append("addBatch( " ).append( batchSQL ).append( " )" ).append( java.lang.System.getProperty("line.separator") );
		 }
	}


	/**
	** Clear the batchSQL in a buffer for error logging in preparation for the next batch
	**/
	public void clearBatchSQLForErrorLogging()
	{
		 _batchSQL = null;
	}


	/**
	** get the batchSQL in a buffer for error logging
	**/
	public String getBatchSQLForErrorLogging()
	{
		if (_batchSQL != null)
		   return _batchSQL.toString();
		return "";
	}

    protected void logErrorDetails(String message, Exception exception)
    {
		_logger.error(message, exception);

        if (_errorlog == null)
            return;
        _errorlog.info( message );
        if (exception != null)
            _errorlog.error( exception.getMessage() );
    }

    protected void logBatchErrorDetails(String message, Exception exception)
    {
		_logger.error(message, exception);

        if (_batcherrorlog == null)
            return;
        _batcherrorlog.info( message );
        _batcherrorlog.error( exception.getMessage() );
    }

    public void logInfoDetails( String message )
    {
		_logger.info(message);

       /* if (_log != null)
            _log.logInfo( message );*/
    }

	public void logDebugDetails(String message)
	{
		_logger.debug(message);

		/*if (_log != null)
			_log.logDebug( message );*/
	}

    public void logSql(String message)
    {
		//_logger.info(message);
        logInfoDetails( message );
    }

/* Needed for JDK 1.6 migration */
	public boolean isPoolable() throws java.sql.SQLException
	{
		return _stmt.isPoolable();
	}

	public void setPoolable(boolean b) throws java.sql.SQLException
	{
		_stmt.setPoolable(b);
	}

	public boolean isClosed() throws java.sql.SQLException
	{
		return _stmt.isClosed();
	}

	public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
		return _stmt.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
		return _stmt.unwrap(iface);
	}

}
