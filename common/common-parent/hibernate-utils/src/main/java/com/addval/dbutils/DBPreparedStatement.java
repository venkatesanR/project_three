//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DBPreparedStatement.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.addval.utils.AVConstants;


/**
 * @author AddVal Technology Inc.
 * This is a wrapper around the PreparedStatement object. It has to derive from
 * the DBStatement object, so that the getResultSet() method call is handled by
 * DBSatement.getResultSet() method. Hence DBConnection always returns
 * DBPreparedStatement and not PreparedStatement
 */
public class DBPreparedStatement extends DBStatement implements PreparedStatement {
	private PreparedStatement _pstmt = null;
	private static final long _MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	private String _preparedSQLTrace = null;
	private StringBuffer _preparedSQLParamsTrace = null;

	/**
	 * @param log
	 * @param errorlog
	 * @param pstmt
	 * @roseuid 3E5FF161032A
	 */
	public DBPreparedStatement(Logger log, Logger errorlog, Logger batcherrorlog, PreparedStatement pstmt) {

		super( log, errorlog, batcherrorlog, pstmt );
		_pstmt = pstmt;
	}


	/**
	 * @param log
	 * @param pstmt
	 * @roseuid 3E5FF161032A
	 */
	public DBPreparedStatement(Logger log, PreparedStatement pstmt) {

		super( log, pstmt );
		_pstmt = pstmt;
	}

	/**
	 * @return java.sql.ResultSet
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8EB0159
	 */
	public ResultSet executeQuery() throws SQLException {

      try {
         logInfoDetails( "executeQuery()" );
         DBResultSet rs  = new DBResultSet( _pstmt.executeQuery() );
		 rs.setStatement( this );
		 clearPreparedSQLParamsForErrorLogging();
		 return rs;
      }
      catch ( SQLException se ) {
         logDebugDetails( se.getMessage() );
		 printErrorDebug("executeQuery()", se.getMessage());
		 clearPreparedSQLParamsForErrorLogging();
         throw se;
      }
	}

	/**
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8EB028F
	 */
	public int executeUpdate() throws SQLException {

		try {
			logInfoDetails( "executeUpdate" );
			int ret = _pstmt.executeUpdate();
			clearPreparedSQLParamsForErrorLogging();
			return ret;
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			printErrorDebug("executeUpdate", se.getMessage());
			clearPreparedSQLParamsForErrorLogging();
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8EB034E
	 */
	public void setNull(int arg0, int arg1) throws SQLException {

		String details = "setNull( " + arg0 + ", " + arg1 + " )" ;
		try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details );
			_pstmt.setNull( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details, se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8EC0291
	 */
	public void setBoolean(int arg0, boolean arg1) throws SQLException {

		String details =  "setBoolean( " + arg0 + ", " + arg1 + " )";

		try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details );
			_pstmt.setBoolean( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8ED01B6
	 */
	public void setByte(int arg0, byte arg1) throws SQLException {

		String details = "setByte( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details );
			_pstmt.setByte( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details  , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8EE0258
	 */
	public void setShort(int arg0, short arg1) throws SQLException {

		String details = "setShort( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details );
			_pstmt.setShort( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8EF0187
	 */
	public void setInt(int arg0, int arg1) throws SQLException {

		String details = "setInt( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details );
			_pstmt.setInt( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F0003E
	 */
	public void setLong(int arg0, long arg1) throws SQLException {

		String details = "setLong( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setLong( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F00323
	 */
	public void setFloat(int arg0, float arg1) throws SQLException {

		String details = "setFloat( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setFloat( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F1022A
	 */
	public void setDouble(int arg0, double arg1) throws SQLException {

		String details = "setDouble( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setDouble( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F20109
	 */
	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {

		String details = "setBigDecimal( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setBigDecimal( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F30038
	 */
	public void setString(int arg0, String arg1) throws SQLException {

		String details = "setString( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setString( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1[]
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F302CD
	 */
	public void setBytes(int arg0, byte arg1[]) throws SQLException {

		String details = "setBytes( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setBytes( arg0, arg1 );
		}
		catch( SQLException se ) {

			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F401C0
	 */
	public void setDate(int arg0, Date arg1) throws SQLException {

		String details = "setDate( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setDate( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F500C7
	 */
	public void setTime(int arg0, Time arg1) throws SQLException {

		String details = "setTime( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setTime( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F502B2
	 */
	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {

		String details = "setTimestamp( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details );
			_pstmt.setTimestamp( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F6015F
	 */
	public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		String details = "setAsciiStream( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setAsciiStream( arg0, arg1, arg2 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F700C0
	 */
	public void setUnicodeStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		String details =  "setUnicodeStream( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setUnicodeStream( arg0, arg1, arg2 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F80071
	 */
	public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		String details = "setBinaryStream( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setBinaryStream( arg0, arg1, arg2 );
		}
		catch( SQLException se ) {

			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F8039C
	 */
	public void clearParameters() throws SQLException {

		try {
			logInfoDetails( "clearParameters()" );
			clearPreparedSQLParamsForErrorLogging();
			_pstmt.clearParameters();
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( "clearParameters()" , se );
		    clearPreparedSQLParamsForErrorLogging();
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F803CF
	 */
	public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {

		String details = "setObject( " + arg0 + ", " + arg1 + ", " + arg2 + ", " + arg3 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setObject( arg0,  arg1, arg2, arg3 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8F90308
	 */
	public void setObject(int arg0, Object arg1, int arg2) throws SQLException {

		String details = "setObject( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setObject( arg0, arg1, arg2 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FA02EB
	 */
	public void setObject(int arg0, Object arg1) throws SQLException {

		String details = "setObject( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setObject( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FB00DA
	 */
	public boolean execute() throws SQLException {

		try {
			logInfoDetails( "execute()" );

			boolean ret = _pstmt.execute();
			clearPreparedSQLParamsForErrorLogging();

			return ret;
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			printErrorDebug("execute", se.getMessage());
		 	clearPreparedSQLParamsForErrorLogging();

			throw se;
		}
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FB01B6
	 */
	public void addBatch() throws SQLException {

		try {
			logInfoDetails( "addBatch()" );

			String batchTrace = getPreparedSQLTrace() + java.lang.System.getProperty("line.separator") + getPreparedSQLParamsForErrorLogging();

			saveBatchSQLForErrorLogging( batchTrace );

		 	clearPreparedSQLParamsForErrorLogging();

			_pstmt.addBatch();

		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );

			printErrorDebug("addBatch", se.getMessage());

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FB027E
	 */
	public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {

		String details = "setCharacterStream( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setCharacterStream( arg0, arg1, arg2 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FC00EF
	 */
	public void setRef(int arg0, Ref arg1) throws SQLException {

		String details = "setRef( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setRef( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FC0320
	 */
	public void setBlob(int arg0, Blob arg1) throws SQLException {

		String details = "setBlob( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setBlob( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
		    logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FD0119
	 */

	public void setClob(int arg0, Clob arg1) throws SQLException {

		String details = "setClob( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setClob( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}


	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FE0020
	 */
	public void setArray(int arg0, Array arg1) throws SQLException {

		String details = "setArray( " + arg0 + ", " + arg1 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setArray( arg0, arg1 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @return java.sql.ResultSetMetaData
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FE01BA
	 */
	public ResultSetMetaData getMetaData() throws SQLException {

		try {
			logInfoDetails( "getMetaData()" );
			return _pstmt.getMetaData();
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( "getMetaData()" , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FE028D
	 */
	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {

		String details = "setDate( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setDate( arg0, arg1 != null ? new Date( getLocalTotalMillis( arg1.getTime() - (long)arg1.getTime()%_MILLIS_PER_DAY, arg2 ) ) : null );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FF00CC
	 */
	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {

		String details = "setTime( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setTime( arg0, arg1 != null ? new Time( getLocalTotalMillis( arg1.getTime()%_MILLIS_PER_DAY, arg2 ) ) : null );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE8FF03E3
	 */
	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {

		String details = "setTimestamp( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );
			//_pstmt.setTimestamp( arg0, arg1, arg2 );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setTimestamp( arg0, arg1 != null ? new Timestamp( getLocalTotalMillis( arg1.getTime(), arg2 ) ) : null );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE9000308
	 */
	public void setNull(int arg0, int arg1, String arg2) throws SQLException {

		String details = "setNull( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );

			savePreparedSQLParamsForErrorLogging( details );

			_pstmt.setNull( arg0, arg1, arg2 );
		}
		catch( SQLException se ) {
			logDebugDetails( se.getMessage() );
			logErrorDetails( details , se );

			throw se;
		}
	}

	/**
	 * @param dateMillis
	 * @param calendar
	 * @return long
	 * @roseuid 3E78CB9E02C6
	 */
	protected static long getLocalTotalMillis(long dateMillis, Calendar calendar) {

		// Create a new Calendar in the timezone that was provided
		Calendar inputCalendar = new GregorianCalendar( calendar.getTimeZone() );
		Calendar localCalendar = new GregorianCalendar( AVConstants._LOCAL_TIMEZONE );

		inputCalendar.setTime( new Date( dateMillis ) );
		//System.out.println( "Input Time = " + dateMillis );
		localCalendar.set( Calendar.DATE		, inputCalendar.get( Calendar.DATE 			) );
		localCalendar.set( Calendar.MONTH		, inputCalendar.get( Calendar.MONTH 		) );
		localCalendar.set( Calendar.YEAR		, inputCalendar.get( Calendar.YEAR 			) );
		localCalendar.set( Calendar.HOUR_OF_DAY	, inputCalendar.get( Calendar.HOUR_OF_DAY 	) );
		localCalendar.set( Calendar.MINUTE		, inputCalendar.get( Calendar.MINUTE 		) );
		localCalendar.set( Calendar.SECOND		, inputCalendar.get( Calendar.SECOND 		) );
		localCalendar.set( Calendar.MILLISECOND		, 0  );
		//System.out.println( localCalendar );
		long millis = localCalendar.getTime().getTime();
		//System.out.println( "Output Time = " + millis );
		return millis;
	}

	public java.sql.ParameterMetaData getParameterMetaData() throws java.sql.SQLException {
		try {
			logInfoDetails( "getParameterMetaData()" );
			return _pstmt.getParameterMetaData();
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
            logErrorDetails( "getParameterMetaData()" , e );
			throw e;
		}
	}

	public void setURL(int parameterIndex, java.net.URL x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setURL(" + parameterIndex + ", " + x + ")" );
			_pstmt.setURL(parameterIndex, x);
		}
		catch ( SQLException e ) {
			logDebugDetails( e.getMessage() );
            logErrorDetails( "setURL(" + parameterIndex + ", " + x + ")" , e );
			throw e;
		}
	}

	public String getPreparedSQLTrace()
	{
		return _preparedSQLTrace;
	}


	public void setPreparedSQLTrace(String preparedSQLTrace)
	{
		_preparedSQLTrace = preparedSQLTrace;
	}




	/**
	** Save the SQL Params in a buffer for error logging in a separate error log in case of an error
	** with the details of the error
	**/
	protected void savePreparedSQLParamsForErrorLogging(String paramsTrace)
	{
		if (_preparedSQLParamsTrace	== null)
		   _preparedSQLParamsTrace = new StringBuffer();

		_preparedSQLParamsTrace.append(paramsTrace ).append( java.lang.System.getProperty("line.separator") );
	}


	/**
	** Clear the batchSQL in a buffer for error logging in preparation for the next batch
	**/
	protected void clearPreparedSQLParamsForErrorLogging()
	{
		 _preparedSQLParamsTrace = null;
	}


	/**
	** get the batchSQL in a buffer for error logging
	**/
	protected String getPreparedSQLParamsForErrorLogging()
	{
		if (_preparedSQLParamsTrace != null)
		   return _preparedSQLParamsTrace.toString();
        return "";
	}

	protected void printErrorDebug(String source, String msg)
	{
        String errString =  source + " " + msg + java.lang.System.getProperty("line.separator") + (_preparedSQLTrace == null ? "" :  _preparedSQLTrace)
                    + java.lang.System.getProperty("line.separator")  + (_preparedSQLParamsTrace == null ? "No Params" : _preparedSQLParamsTrace.toString());

        logErrorDetails( errString , null );
	}


/* Needed for JDK 1.6 change implementation to _pstmt */

	public void setNClob(int arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setNClob(String arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setBlob(int arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setBlob not implemented" , null );
	}

	public void setBlob(String arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setBlob not implemented" , null );
	}

	public void setClob(int arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setClob not implemented" , null );
	}

	public void setClob(String arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setClob not implemented" , null );
	}

	public void setNCharacterStream(int arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setNCharacterStream not implemented" , null );
	}

	public void setNCharacterStream(String arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setNCharacterStream not implemented" , null );
	}

	public void setCharacterStream(int arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setCharacterStream not implemented" , null );
	}

	public void setCharacterStream(String arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setCharacterStream not implemented" , null );
	}

	public void setBinaryStream(int arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setBinaryStream not implemented" , null );
	}

	public void setBinaryStream(String arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setBinaryStream not implemented" , null );
	}

	public void setAsciiStream(int arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setAsciiStream not implemented" , null );
	}

	public void setAsciiStream(String arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setAsciiStream not implemented" , null );
	}



	public void setNCharacterStream(int arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setNCharacterStream not implemented" , null );
	}

	public void setNCharacterStream(String arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setNCharacterStream not implemented" , null );
	}

	public void setCharacterStream(int arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setCharacterStream not implemented" , null );
	}

	public void setCharacterStream(String arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setCharacterStream not implemented" , null );
	}

	public void setBinaryStream(int arg0, java.io.InputStream arg1, long l) throws SQLException {
        logErrorDetails( "setBinaryStream not implemented" , null );
	}

	public void setBinaryStream(String arg0, java.io.InputStream arg1, long l) throws SQLException {
        logErrorDetails( "setBinaryStream not implemented" , null );
	}

	public void setAsciiStream(int arg0, java.io.InputStream arg1, long l) throws SQLException {
        logErrorDetails( "setAsciiStream not implemented" , null );
	}

	public void setAsciiStream(String arg0, java.io.InputStream arg1, long l) throws SQLException {
        logErrorDetails( "setAsciiStream not implemented" , null );
	}


	public void setSQLXML(int arg0, java.sql.SQLXML arg1) throws SQLException {
        logErrorDetails( "setSQLXML not implemented" , null );
	}

	public void setSQLXML(String arg0, java.sql.SQLXML arg1) throws SQLException {
        logErrorDetails( "setSQLXML not implemented" , null );
	}


	public void setNClob(int arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setNClob(String arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}


	public void setBlob(int arg0, java.io.InputStream arg1, long l) throws SQLException {
        logErrorDetails( "setBlob not implemented" , null );
	}

	public void setBlob(String arg0, java.io.InputStream arg1, long l) throws SQLException {
        logErrorDetails( "setBlob not implemented" , null );
	}



	public void setClob(int arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setClob not implemented" , null );
	}

	public void setClob(String arg0, java.io.Reader arg1, long l) throws SQLException {
        logErrorDetails( "setClob not implemented" , null );
	}

	public void setNClob(int arg0, java.sql.NClob arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setNClob(String arg0, java.sql.NClob arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setNString(int arg0, String arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setNString(String arg0, String arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}


	public void setRowId(String columnName, java.sql.RowId id) throws java.sql.SQLException {
        logErrorDetails( "setRowId not implemented" , null );
	}

	public void setRowId(int columnIndex, java.sql.RowId id) throws java.sql.SQLException {
        logErrorDetails( "setRowId not implemented" , null );
	}



}
