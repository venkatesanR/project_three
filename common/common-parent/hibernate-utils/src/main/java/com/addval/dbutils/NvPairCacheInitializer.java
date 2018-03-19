//Source file: C:\\users\\prasad\\Projects\\cargores\\source\\com\\addval\\cargores\\NvPairCacheInitializer.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.addval.utils.CacheException;
import com.addval.utils.CacheMgr;
import com.addval.utils.DefaultNamedCacheInitializer;

/**
 * Generic cache initializer to load a name-value pair hashtable based on the
 * object & DAO. If only a Hashtable of name-value pairs is required, use this
 * cache initializer and inject the daosql cache and the sql name
 * 
 */
public class NvPairCacheInitializer extends DefaultNamedCacheInitializer {
	private static final String _module = "com.addval.dbutils.NvPairCacheInitializer";
	private String _daoCacheName = null;
	private String _sqlName = null;
	private static final int _MAX_FETCH_ROWS = 1000;
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(NvPairCacheInitializer.class);

	public void setDaoCacheName(String daoCacheName) {
		_daoCacheName = daoCacheName;
	}

	public String getDaoCacheName() {
		return _daoCacheName;
	}

	public void setSqlName(String sqlName) {
		_sqlName = sqlName;
	}

	public String getSqlName() {
		return _sqlName;
	}

	/**
	 * @param cache
	 * @param objectName
	 * @param refreshFlag
	 * @return java.lang.Object
	 * @throws com.addval.utils.CacheException
	 * @roseuid 40631DD600CB
	 */
	public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Hashtable nvPairs = new Hashtable();

		try {
			_logger.debug("Populating NVPair cache for : " + objectName);

			final String NAME = "name";
			final String VALUE = "value";
			DAOSQLStatement statement = getDAOSQLStatement(objectName);

			if (statement == null) {
				_logger.debug("Statement not found for " + objectName);
			}

			DAOUtils utils = new DAOUtils(statement, getServerType());
			Object name = null;
			Object value = null;

			conn = getConnection();
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			pstmt.setFetchSize(_MAX_FETCH_ROWS);
			rs = pstmt.executeQuery();			

			while (rs.next()) {
				name = utils.getProperty(rs, NAME);
				value = utils.getProperty(rs, VALUE);

				if (value == null) {
					value = name;
				}
				if(name != null){
					nvPairs.put(name, value);
				}
			}
		}
		catch (SQLException se) {

			_logger.error(se);
			throw new CacheException(_module, se);
		}
		finally {

			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					releaseConnection(conn);
			}
			catch (SQLException se) {
				_logger.error(se);
				throw new CacheException(_module, se);
			}
		}

		return nvPairs;
	}

	/**
	 * @return java.sql.Connection
	 * @roseuid 406322B400EA
	 */
	protected Connection getConnection() {
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	/**
	 * @param conn
	 * @roseuid 406322B400FA
	 */
	protected void releaseConnection(Connection conn) {
		getEnvironment().getDbPoolMgr().releaseConnection(conn);
	}

	/**
	 * @param objectName
	 * @return com.addval.dbutils.DAOSQLStatement
	 * @roseuid 406322B40109
	 */
	protected DAOSQLStatement getDAOSQLStatement(String objectName) {
		Hashtable sqls = (Hashtable) getEnvironment().getCacheMgr().get(getDaoCacheName());

		return (DAOSQLStatement) sqls.get(getSqlName());
	}

	/**
	 * @return java.lang.String
	 * @roseuid 406322B40128
	 */
	protected String getServerType() {
		return getEnvironment().getDbPoolMgr().getServerType();
	}

}
