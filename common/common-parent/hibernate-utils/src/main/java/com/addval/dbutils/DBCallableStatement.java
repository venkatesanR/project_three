//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DBCallableStatement.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;

import com.addval.utils.GenUtl;

/**
 * @author AddVal Technology Inc.
 */
public class DBCallableStatement extends DBPreparedStatement implements CallableStatement {
	private CallableStatement _cstmt = null;

    public DBCallableStatement(Logger log, Logger errorlog, Logger batcherrorlog, CallableStatement cstmt) {

        super( log, errorlog, batcherrorlog, cstmt );
		_cstmt = cstmt;
    }


	/**
	 * @param log
	 * @param cstmt
	 * @roseuid 3E601B7E0124
	 */
	public DBCallableStatement(Logger log, CallableStatement cstmt) {

		super( log, cstmt );
		_cstmt = cstmt;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE9040047
	 */
	public void registerOutParameter(int arg0, int arg1) throws SQLException {
        String details = "registerOutParameter( " + arg0 + ", " + arg1 + " )";
		try {
            savePreparedSQLParamsForErrorLogging( details  );
			logInfoDetails( details );
			_cstmt.registerOutParameter( arg0, arg1 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details, se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90402E5
	 */
	public void registerOutParameter(int arg0, int arg1, int arg2) throws SQLException {

        String details = "registerOutParameter( " + arg0 + ", " + arg1 + ", " + arg2 + " )";
		try {
			logInfoDetails( details );
            savePreparedSQLParamsForErrorLogging( details  );
			_cstmt.registerOutParameter( arg0, arg1, arg2 );
		}
		catch (SQLException se) {

			logDebugDetails( se.getMessage() );
            logErrorDetails( details, se );
			throw se;
		}
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE905012E
	 */
	public boolean wasNull() throws SQLException {

		try {
			 logInfoDetails( "wasNull()" );
			 return _cstmt.wasNull();
		}
		catch (SQLException se)  {;

			logDebugDetails( se.getMessage() );
            logErrorDetails( "wasNull()" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.lang.String
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90501B0
	 */
	public String getString(int arg0) throws SQLException {

		String details = "getString( " + arg0 + ")";
		try {
			logInfoDetails( details );
			return _cstmt.getString( arg0 );
		}
		catch (SQLException se) {

			logDebugDetails( se.getMessage() );
            logErrorDetails(  details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90502DD
	 */
	public boolean getBoolean(int arg0) throws SQLException {

		String details =  "getBoolean( " + arg0 + ")";
		try {
			logInfoDetails( details );
			return _cstmt.getBoolean( arg0 );
		}
		catch (SQLException se) {

			logDebugDetails( se.getMessage() );
            logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return byte
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE9060017
	 */
	public byte getByte(int arg0) throws SQLException {

		String details = "getByte( " + arg0 + ")";
		try {
			logInfoDetails( details );
			return _cstmt.getByte( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return short
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90600F4
	 */
	public short getShort(int arg0) throws SQLException {

		String details = "getShort( " + arg0 + ")";
		try {
			logInfoDetails( details );
			return _cstmt.getShort( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90601B2
	 */
	public int getInt(int arg0) throws SQLException {

		String details = "getInt( " + arg0 + " )" ;
		try {
			logInfoDetails( details );
			return _cstmt.getInt( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails(  details , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return long
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90602A2
	 */
	public long getLong(int arg0) throws SQLException {

		try {
			logInfoDetails( "getLong( " + arg0 + " )" );
			return _cstmt.getLong( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails(  "getLong( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return float
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE9060389
	 */
	public float getFloat(int arg0) throws SQLException {

		try {
			logInfoDetails( "getFloat( " + arg0 + " )" );
			return _cstmt.getFloat( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getFloat( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return double
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90700D7
	 */
	public double getDouble(int arg0) throws SQLException {

		try {
			logInfoDetails( "getDouble( " + arg0 + " )" );
			return _cstmt.getDouble( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getDouble( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90701D1
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {

		try {
			logInfoDetails( "getBigDecimal( " + arg0 + ", " + arg1 + " )" );
			return _cstmt.getBigDecimal( arg0, arg1 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBigDecimal( " + arg0 + ", " + arg1 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return byte[]
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90703A8
	 */
	public byte[] getBytes(int arg0) throws SQLException {

		try {
			logInfoDetails( "getBytes( " + arg0 + " )" );
			return _cstmt.getBytes( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBytes( " + arg0 + " )" , se);
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.sql.Date
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90800A6
	 */
	public Date getDate(int arg0) throws SQLException {

		try {
			logInfoDetails( "getDate( " + arg0 + " )" );
			return _cstmt.getDate( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getDate( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.sql.Time
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90801E7
	 */
	public Time getTime(int arg0) throws SQLException {

		try {
			logInfoDetails( "getTime( " + arg0 + " )" );
			return _cstmt.getTime( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTime( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.sql.Timestamp
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE9080381
	 */
	public Timestamp getTimestamp(int arg0) throws SQLException {

		try {
			logInfoDetails( "getTimestamp( " + arg0 + " )" );
			return _cstmt.getTimestamp( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTimestamp( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90900BC
	 */
	public Object getObject(int arg0) throws SQLException {

		try {
			logInfoDetails( "getObject( " + arg0 + " )" );
			return _cstmt.getObject( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getObject( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90901D4
	 */
	public BigDecimal getBigDecimal(int arg0) throws SQLException {

		try {
			logInfoDetails( "getBigDecimal( " + arg0 + " )" );
			return _cstmt.getBigDecimal( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBigDecimal( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE9090256
	 */
	public Object getObject(int arg0, Map arg1) throws SQLException {

		try {
			logInfoDetails( "getObject( " + arg0 + ", " + arg1 + " )" );
			return _cstmt.getObject( arg0, arg1 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getObject( " + arg0 + ", " + arg1 + " )"  , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.sql.Ref
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90A00C7
	 */
	public Ref getRef(int arg0) throws SQLException {

		try {
			logInfoDetails( "getRef( " + arg0 + " )" );
			return _cstmt.getRef( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getRef( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.sql.Blob
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90A0212
	 */
	public Blob getBlob(int arg0) throws SQLException {

		try {

			logInfoDetails( "getBlob( " + arg0 + " )" );
			return _cstmt.getBlob( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBlob( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return java.sql.Clob
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90A035C
	 */
	public Clob getClob(int arg0) throws SQLException {

		try {
			logInfoDetails( "getClob( " + arg0 + " )" );
			return _cstmt.getClob( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getClob( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @return Array
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90B00AB
	 */
	public Array getArray(int arg0) throws SQLException {

		try {
			logInfoDetails( "getArray( " + arg0 + " )" );
			return _cstmt.getArray( arg0 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getArray( " + arg0 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return Date
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90B0191
	 */
	public Date getDate(int arg0, Calendar arg1) throws SQLException {

		try {
			logInfoDetails( "getDate( " + arg0 + ", " + arg1 + " )" );
			return _cstmt.getDate( arg0 ) != null ? new Date( DBResultSet.getTotalMillis( _cstmt.getDate( arg0 ), null, arg1 ) ) : null;
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getDate( " + arg0 + ", " + arg1 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Time
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90B02D2
	 */
	public Time getTime(int arg0, Calendar arg1) throws SQLException {

		try {
			logInfoDetails( "getTime( " + arg0 + ", " + arg1 + " )" );
			return _cstmt.getTime( arg0, arg1 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTime( " + arg0 + ", " + arg1 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Timestamp
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90C0048
	 */
	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {

		try {
			logInfoDetails( "getTimestamp( " + arg0 + ", " + arg1 + " )" );
			return _cstmt.getTimestamp( arg0 ) != null ? new Timestamp( DBResultSet.getTotalMillis( new Date( _cstmt.getTimestamp( arg0 ).getTime() ), null, arg1 ) ) : null;
			//return _cstmt.getDate( arg0 ) != null ? new Timestamp( DBResultSet.getTotalMillis( _cstmt.getDate( arg0 ), _cstmt.getTime( arg0 ), arg1 ) ) : null;
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTimestamp( " + arg0 + ", " + arg1 + " )" , se );
			throw se;
		}
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5FE90C020B
	 */
	public void registerOutParameter(int arg0, int arg1, String arg2) throws SQLException {

        String details = "registerOutParameter( " + arg0 + ", " + arg1 + "," + arg2 + " )";
		try {
			logInfoDetails( details );
            savePreparedSQLParamsForErrorLogging( details  );
			_cstmt.registerOutParameter( arg0, arg1, arg2 );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details, se );
			throw se;
		}
	}

	public java.sql.Array getArray(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getArray( " + parameterName + " )" );
			return _cstmt.getArray( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getArray( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.math.BigDecimal getBigDecimal(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getBigDecimal( " + parameterName + " )" );
			return _cstmt.getBigDecimal( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBigDecimal( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Blob getBlob(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getBlob( " + parameterName + " )" );
			return _cstmt.getBlob( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBlob( " + parameterName + " )" , se );
			throw se;
		}
	}

	public boolean getBoolean(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getBoolean( " + parameterName + " )" );
			return _cstmt.getBoolean( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBoolean( " + parameterName + " )" , se );
			throw se;
		}
	}

	public byte getByte(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getByte( " + parameterName + " )" );
			return _cstmt.getByte( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getByte( " + parameterName + " )" , se );
			throw se;
		}
	}

	public byte[] getBytes(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getBytes( " + parameterName + " )" );
			return _cstmt.getBytes( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getBytes( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Clob getClob(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getClob( " + parameterName + " )" );
			return _cstmt.getClob( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getClob( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Date getDate(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getDate( " + parameterName + " )" );
			return _cstmt.getDate( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getDate( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Date getDate(String parameterName, java.util.Calendar cal) throws java.sql.SQLException {
		try {
			logInfoDetails( "getDate( " + parameterName + ", " + cal + " )" );
			return _cstmt.getDate( parameterName, cal );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getDate( " + parameterName + ", " + cal + " )" , se );
			throw se;
		}
	}

	public double getDouble(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getDouble( " + parameterName + " )" );
			return _cstmt.getDouble( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
			throw se;
		}
	}

	public float getFloat(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getFloat( " + parameterName + " )" );
			return _cstmt.getFloat( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getFloat( " + parameterName + " )" , se );
			throw se;
		}
	}

	public int getInt(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getInt( " + parameterName + " )" );
			return _cstmt.getInt( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getInt( " + parameterName + " )" , se );
			throw se;
		}
	}

	public long getLong(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getLong( " + parameterName + " )" );
			return _cstmt.getLong( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getLong( " + parameterName + " )" , se );
			throw se;
		}
	}

	public Object getObject(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getObject( " + parameterName + " )" );
			return _cstmt.getObject( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getObject( " + parameterName + " )" , se );
			throw se;
		}
	}

	public Object getObject(String parameterName, java.util.Map map) throws java.sql.SQLException {
		try {
			logInfoDetails( "getObject( " + parameterName + ", " + map + " )" );
			return _cstmt.getObject( parameterName, map );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getObject( " + parameterName + ", " + map + " )" , se );
			throw se;
		}
	}

	public java.sql.Ref getRef(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getRef( " + parameterName + " )" );
			return _cstmt.getRef( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getRef( " + parameterName + " )" , se );
			throw se;
		}
	}

	public short getShort(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getShort( " + parameterName + " )" );
			return _cstmt.getShort( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getShort( " + parameterName + " )" , se );
			throw se;
		}
	}

	public String getString(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getString( " + parameterName + " )" );
			return _cstmt.getString( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getString( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Time getTime(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getTime( " + parameterName + " )" );
			return _cstmt.getTime( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTime( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Time getTime(String parameterName, java.util.Calendar cal) throws java.sql.SQLException {
		try {
			logInfoDetails( "getTime( " + parameterName + ", " + cal + " )" );
			return _cstmt.getTime( parameterName, cal );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTime( " + parameterName + ", " + cal + " )" , se );
			throw se;
		}
	}

	public java.sql.Timestamp getTimestamp(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getTimestamp( " + parameterName + " )" );
			return _cstmt.getTimestamp( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTimestamp( " + parameterName + " )" , se );
			throw se;
		}
	}

	public java.sql.Timestamp getTimestamp(String parameterName, java.util.Calendar cal) throws java.sql.SQLException {
		try {
			logInfoDetails( "getTimestamp( " + parameterName + ", " + cal + " )" );
			return _cstmt.getTimestamp( parameterName, cal );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getTimestamp( " + parameterName + ", " + cal + " )" , se  );
			throw se;
		}
	}

	public java.net.URL getURL(int parameterIndex) throws java.sql.SQLException {
		try {
			logInfoDetails( "getURL( " + parameterIndex + " )" );
			return _cstmt.getURL( parameterIndex );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getURL( " + parameterIndex + " )" , se );
			throw se;
		}
	}

	public java.net.URL getURL(String parameterName) throws java.sql.SQLException {
		try {
			logInfoDetails( "getURL( " + parameterName + " )" );
			return _cstmt.getURL( parameterName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "getURL( " + parameterName + " )" , se );
			throw se;
		}
	}

	public void registerOutParameter(String parameterName, int sqlType) throws java.sql.SQLException {
        String details = "registerOutParameter( " + parameterName + ", " + sqlType + " )";
		try {
			logInfoDetails( details );
            savePreparedSQLParamsForErrorLogging( details  );
			_cstmt.registerOutParameter( parameterName, sqlType );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details, se );
			throw se;
		}
	}

	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws java.sql.SQLException {
		String details = "registerOutParameter( " + parameterName + ", " + sqlType + ", " + typeName + " )";
        try {
			logInfoDetails( details );
            savePreparedSQLParamsForErrorLogging( details  );
			_cstmt.registerOutParameter( parameterName, sqlType, typeName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details, se );
			throw se;
		}
	}

	public void registerOutParameter(String parameterName, int sqlType, int scale) throws java.sql.SQLException {
		String details = "registerOutParameter( " + parameterName + ", " + sqlType + ", " + scale + " )";
        try {
			logInfoDetails( details );
			savePreparedSQLParamsForErrorLogging( details  );
            _cstmt.registerOutParameter( parameterName, sqlType, scale );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( details, se );
			throw se;
		}
	}

	public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws java.sql.SQLException {
		try {
			logInfoDetails( "setAsciiStream( " + parameterName + ", " + x + ", " + length + " )" );
			_cstmt.setAsciiStream( parameterName, x, length );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setAsciiStream( " + parameterName + ", " + x + ", " + length + " )" , se );
			throw se;
		}
	}

	public void setBigDecimal(String parameterName, java.math.BigDecimal x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setBigDecimal( " + parameterName + ", " + x + " )" );
			_cstmt.setBigDecimal( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setBigDecimal( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws java.sql.SQLException {
		try {
			logInfoDetails( "setBinaryStream( " + parameterName + ", " + x + ", " + length + " )" );
			_cstmt.setBinaryStream( parameterName, x, length );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setBinaryStream( " + parameterName + ", " + x + ", " + length + " )" , se );
			throw se;
		}
	}

	public void setBoolean(String parameterName, boolean x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setBoolean( " + parameterName + ", " + x + " )" );
			_cstmt.setBoolean( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setBoolean( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setByte(String parameterName, byte x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setByte( " + parameterName + ", " + x + " )" );
			_cstmt.setByte( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setByte( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setBytes(String parameterName, byte[] x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setBytes( " + parameterName + ", " + GenUtl.arrayToString(x) + " )" );
			_cstmt.setBytes( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setBytes( " + parameterName + ", " + GenUtl.arrayToString(x) + " )" , se );
			throw se;
		}
	}

	public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws java.sql.SQLException {
		try {
			logInfoDetails( "setCharacterStream( " + parameterName + ", " + reader + ", " + length + " )" );
			_cstmt.setCharacterStream( parameterName, reader, length );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setCharacterStream( " + parameterName + ", " + reader + ", " + length + " )" , se );
			throw se;
		}
	}

	public void setDate(String parameterName, java.sql.Date x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setDate( " + parameterName + ", " + x + " )" );
			_cstmt.setDate( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setDate( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setDate(String parameterName, java.sql.Date x, java.util.Calendar cal) throws java.sql.SQLException {
		try {
			logInfoDetails( "setDate( " + parameterName + ", " + x + ", " + cal + " )" );
			_cstmt.setDate( parameterName, x, cal );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setDate( " + parameterName + ", " + x + ", " + cal + " )" , se );
			throw se;
		}
	}

	public void setDouble(String parameterName, double x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setDouble( " + parameterName + ", " + x + " )" );
			_cstmt.setDouble( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setDouble( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setFloat(String parameterName, float x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setFloat( " + parameterName + ", " + x + " )" );
			_cstmt.setFloat( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setFloat( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setInt(String parameterName, int x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setInt( " + parameterName + ", " + x + " )" );
			_cstmt.setInt( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setInt( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setLong(String parameterName, long x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setLong( " + parameterName + ", " + x + " )" );
			_cstmt.setLong( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setLong( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setNull(String parameterName, int sqlType) throws java.sql.SQLException {
		try {
			logInfoDetails( "setNull( " + parameterName + ", " + sqlType + " )" );
			_cstmt.setNull( parameterName, sqlType );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setNull( " + parameterName + ", " + sqlType + " )" , se );
			throw se;
		}
	}

	public void setNull(String parameterName, int sqlType, String typeName) throws java.sql.SQLException {
		try {
			logInfoDetails( "setNull( " + parameterName + ", " + sqlType + ", " + typeName + " )" );
			_cstmt.setNull( parameterName, sqlType, typeName );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setNull( " + parameterName + ", " + sqlType + ", " + typeName + " )" , se );
			throw se;
		}
	}

	public void setObject(String parameterName, Object x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setObject( " + parameterName + ", " + x + " )" );
			_cstmt.setObject( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setObject( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setObject(String parameterName, Object x, int targetSqlType) throws java.sql.SQLException {
		try {
			logInfoDetails( "setObject( " + parameterName + ", " + x + ", " + targetSqlType + " )" );
			_cstmt.setObject( parameterName, x, targetSqlType );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setObject( " + parameterName + ", " + x + ", " + targetSqlType + " )" , se );
			throw se;
		}
	}

	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws java.sql.SQLException {
		try {
			logInfoDetails( "setObject( " + parameterName + ", " + x + ", " + targetSqlType + ", " + scale + " )" );
			_cstmt.setObject( parameterName, x, targetSqlType, scale );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setObject( " + parameterName + ", " + x + ", " + targetSqlType + ", " + scale + " )" , se );
			throw se;
		}
	}

	public void setShort(String parameterName, short x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setShort( " + parameterName + ", " + x + " )" );
			_cstmt.setShort( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setShort( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setString(String parameterName, String x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setString( " + parameterName + ", " + x + " )" );
			_cstmt.setString( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setString( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setTime(String parameterName, java.sql.Time x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setTime( " + parameterName + ", " + x + " )" );
			_cstmt.setTime( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setTime( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setTime(String parameterName, java.sql.Time x, java.util.Calendar cal) throws java.sql.SQLException {
		try {
			logInfoDetails( "setTime( " + parameterName + ", " + x + ", " + cal + " )" );
			_cstmt.setTime( parameterName, x, cal );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setTime( " + parameterName + ", " + x + ", " + cal + " )" , se );
			throw se;
		}
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x) throws java.sql.SQLException {
		try {
			logInfoDetails( "setTimestamp( " + parameterName + ", " + x + " )" );
			_cstmt.setTimestamp( parameterName, x );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setTimestamp( " + parameterName + ", " + x + " )" , se );
			throw se;
		}
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x, java.util.Calendar cal) throws java.sql.SQLException {
		try {
			logInfoDetails( "setTimestamp( " + parameterName + ", " + x + ", " + cal + " )" );
			_cstmt.setTimestamp( parameterName, x, cal );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setTimestamp( " + parameterName + ", " + x + ", " + cal + " )" , se );
			throw se;
		}
	}

	public void setURL(String parameterName, java.net.URL val) throws java.sql.SQLException {
		try {
			logInfoDetails( "setURL( " + parameterName + ", " + val + " )" );
			_cstmt.setURL( parameterName, val );
		}
		catch (SQLException se) {
			logDebugDetails( se.getMessage() );
            logErrorDetails( "setURL( " + parameterName + ", " + val + " )" , se );
			throw se;
		}
	}

/* Needed for JDK 1.6. Implementation should be linked to _cstmt. */

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
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setNCharacterStream(String arg0, java.io.Reader arg1) throws SQLException {
        logErrorDetails( "setNClob not implemented" , null );
	}

	public void setAsciiStream(int arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setAsciiStream not implemented" , null );
	}

	public void setAsciiStream(String arg0, java.io.InputStream arg1) throws SQLException {
        logErrorDetails( "setAsciiStream not implemented" , null );
	}

	public void setClob(int arg0, java.sql.Clob arg1) throws SQLException {
        logErrorDetails( "setClob not implemented" , null );
	}

	public void setClob(String arg0, java.sql.Clob arg1) throws SQLException {
        logErrorDetails( "setClob not implemented" , null );
	}

	public void setBlob(int arg0, java.sql.Blob arg1) throws SQLException {
        logErrorDetails( "setBlob not implemented" , null );
	}

	public void setBlob(String arg0, java.sql.Blob arg1) throws SQLException {
        logErrorDetails( "setBlob not implemented" , null );
	}


	public java.io.Reader getCharacterStream(int i) throws java.sql.SQLException {
        logErrorDetails( "getCharacterStream not implemented" , null );
        return null;
	}

	public java.io.Reader getCharacterStream(String s) throws java.sql.SQLException {
        logErrorDetails( "getCharacterStream not implemented" , null );
        return null;
	}


	public java.io.Reader getNCharacterStream(int i) throws java.sql.SQLException {
        logErrorDetails( "getNCharacterStream not implemented" , null );
        return null;
	}

	public java.io.Reader getNCharacterStream(String s) throws java.sql.SQLException {
        logErrorDetails( "getNCharacterStream not implemented" , null );
        return null;
	}

	public String getNString(int i) throws java.sql.SQLException {
        logErrorDetails( "getNString not implemented" , null );
        return null;
	}

	public String getNString(String s) throws java.sql.SQLException {
        logErrorDetails( "getNString not implemented" , null );
        return null;
	}

	public java.sql.SQLXML getSQLXML(int i) throws java.sql.SQLException {
        logErrorDetails( "getSQLXML not implemented" , null );
        return null;
	}

	public java.sql.SQLXML getSQLXML(String s) throws java.sql.SQLException {
        logErrorDetails( "getSQLXML not implemented" , null );
        return null;
	}

	public java.sql.NClob getNClob(int i) throws java.sql.SQLException {
        logErrorDetails( "getNClob not implemented" , null );
        return null;
	}

	public java.sql.NClob getNClob(String s) throws java.sql.SQLException {
        logErrorDetails( "getNClob not implemented" , null );
        return null;
	}

	public java.sql.RowId getRowId(int i) throws java.sql.SQLException {
        logErrorDetails( "getRowId not implemented" , null );
        return null;
	}

	public java.sql.RowId getRowId(String s) throws java.sql.SQLException {
        logErrorDetails( "getRowId not implemented" , null );
        return null;
	}

}
