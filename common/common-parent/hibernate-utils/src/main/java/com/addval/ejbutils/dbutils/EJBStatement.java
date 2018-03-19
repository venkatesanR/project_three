//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBStatement.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

/* Copyright AddVal Technology Inc. */

package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import java.sql.Statement;
import com.addval.ejbutils.utils.EJBXFeatureNotImplemented;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLWarning;

/**
 * This is an serializable object that implements the java.sql.Statement
 * interface.
 */
public class EJBStatement implements Serializable, Statement {
	private static final transient String _module = "EJBStatement";
	private EJBResultSet _rs = null;

	/**
	 * @param rs
	 * @roseuid 3AF83BC2022E
	 */
	public EJBStatement(EJBResultSet rs) {
		_rs = rs;
		//_rs.setStatement( this );
	}

	/**
	 * @param sql
	 * @roseuid 3AE9FE9700DA
	 */
	public void addBatch(String sql) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @roseuid 3AE9FE970102
	 */
	public void cancel() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @roseuid 3AE9FE970116
	 */
	public void clearBatch() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @roseuid 3AE9FE970134
	 */
	public void clearWarnings() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @roseuid 3AE9FE970153
	 */
	public void close() {
		this._rs = null;
		//throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param sql
	 * @return boolean
	 * @roseuid 3AE9FE970167
	 */
	public boolean execute(String sql) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int[]
	 * @roseuid 3AE9FE970185
	 */
	public int[] executeBatch() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param sql
	 * @return java.sql.ResultSet
	 * @roseuid 3AE9FE970199
	 */
	public ResultSet executeQuery(String sql) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param sql
	 * @return int
	 * @roseuid 3AE9FE9701B7
	 */
	public int executeUpdate(String sql) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return java.sql.Connection
	 * @roseuid 3AE9FE9701D5
	 */
	public Connection getConnection() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE9701F3
	 */
	public int getFetchDirection() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE970211
	 */
	public int getFetchSize() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE970225
	 */
	public int getMaxFieldSize() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE970243
	 */
	public int getMaxRows() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return boolean
	 * @roseuid 3AE9FE970261
	 */
	public boolean getMoreResults() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE97027F
	 */
	public int getQueryTimeout() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return java.sql.ResultSet
	 * @roseuid 3AE9FE9702CF
	 */
	public ResultSet getResultSet() {
		return _rs;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3AF83C5700CA
	 */
	public EJBResultSet getEJBResultSet() {
		return _rs;
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE9702ED
	 */
	public int getResultSetConcurrency() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE97030B
	 */
	public int getResultSetType() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AE9FE970329
	 */
	public int getUpdateCount() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return java.sql.SQLWarning
	 * @roseuid 3AE9FE970347
	 */
	public SQLWarning getWarnings() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param name
	 * @roseuid 3AE9FE970365
	 */
	public void setCursorName(String name) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param enable
	 * @roseuid 3AE9FE97038D
	 */
	public void setEscapeProcessing(boolean enable) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param direction
	 * @roseuid 3AE9FE9703AB
	 */
	public void setFetchDirection(int direction) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param rows
	 * @roseuid 3AE9FE9703C9
	 */
	public void setFetchSize(int rows) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param max
	 * @roseuid 3AE9FE9703E7
	 */
	public void setMaxFieldSize(int max) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param max
	 * @roseuid 3AE9FE98001E
	 */
	public void setMaxRows(int max) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param seconds
	 * @roseuid 3AE9FE980046
	 */
	public void setQueryTimeout(int seconds) {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean execute(String sql, String[] columnNames) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean execute(String sql, int[] columnIndexes) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public int executeUpdate(String sql, String[] columnNames) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean getMoreResults(int current) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public int getResultSetHoldability() throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

/* Needed for Java 1.6 migration */
	public boolean isPoolable() throws java.sql.SQLException {
			throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void setPoolable(boolean b) throws java.sql.SQLException {
			throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean isClosed() throws java.sql.SQLException
	{
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

}
