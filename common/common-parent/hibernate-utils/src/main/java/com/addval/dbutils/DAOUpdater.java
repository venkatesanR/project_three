//Source file: C:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DAOUpdater.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import org.apache.commons.beanutils.WrapDynaBean;
import java.util.Hashtable;
import com.addval.utils.XRuntime;
import java.util.Vector;
import com.addval.metadata.ColumnDataType;
import com.addval.utils.AVConstants;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;
import com.addval.utils.CacheMgr;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author AddVal Technology Inc.
 */
public class DAOUpdater {
	private static final String _module = "com.addval.dbutils.DAOUpdater";

	/**
	 * @param cacheMgr
	 * @param dbPoolMgr
	 * @param module
	 * @param sqlName
	 * @param instance
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3EA9D8C602AF
	 */
	public static int update(CacheMgr cacheMgr, DBPoolMgr dbPoolMgr, String module, String sqlName, Object instance) throws SQLException {

		int 		rv 			= 0;
		String 		serverType 	= dbPoolMgr.getServerType();
		Hashtable 	sqls 		= (Hashtable)cacheMgr.get( module );

		if (sqls == null)
			throw new XRuntime( _module, "The Environment's Cache is not loaded with the SQLs for : " + module );

		DAOSQLStatement statement = (DAOSQLStatement)sqls.get( sqlName );
		String 			sql 	  = statement != null ? statement.getSQL( serverType ) : null;

		if (statement == null || sql == null)
			throw new XRuntime( _module, "The SQL statement for : " + sqlName + " for the server : " + serverType + " has not been defined" );

		// Now execute the SQL
		Connection conn = dbPoolMgr.getConnection();
		try {

			rv = update( conn, sql, statement, instance );
		}
		finally {

			dbPoolMgr.releaseConnection( conn );
		}

		return rv;
	}

	/**
	 * @param conn
	 * @param sql
	 * @param statement
	 * @param instance
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3EA9E113033C
	 */
	private static int update(Connection conn, String sql, DAOSQLStatement statement, Object instance) throws SQLException {

		int 			  rv 	= 0;
		PreparedStatement pstmt = null;

		try {

			pstmt = conn.prepareStatement( sql );

			//

			rv = pstmt.executeUpdate();

			return rv;
		}
		finally {

			pstmt.close();
		}
	}

	/**
	 * @param pstmt
	 * @param statement
	 * @param instance
	 * @throws java.sql.SQLException
	 * @roseuid 3EA9E23B00AB
	 */
	private static void setProperties(PreparedStatement pstmt, DAOSQLStatement statement, Object instance) throws SQLException {

		WrapDynaBean bean   = new WrapDynaBean( instance );
		DAOParam 	 param  = null;
		Object 		 value  = null;
		Vector 		 params = statement.getCriteriaParams();
		int 		 size   = params == null ? 0 : params.size();

		for (int index = 0; index < size; index++) {

			param = (DAOParam)params.elementAt( index );
			value = bean.get( param.getName() );

			// Handle Nulls
			if (value == null) {

				pstmt.setNull( index + 1, param.getColumnType() );
			}
			else {

				switch (param.getColumnType()) {

					case ColumnDataType._CDT_STRING:
					case ColumnDataType._CDT_CHAR:
					case ColumnDataType._CDT_USER:
						pstmt.setString( index + 1, (String)value );
						break;

					case ColumnDataType._CDT_INT:
						pstmt.setInt( index + 1, ((Integer)value).intValue() );
						break;

					case ColumnDataType._CDT_LONG:
					case ColumnDataType._CDT_VERSION:
						pstmt.setLong( index + 1, ((Long)value).longValue() );
						break;

					case ColumnDataType._CDT_DOUBLE:
						pstmt.setDouble( index + 1, ((Double)value).doubleValue() );
						break;

					case ColumnDataType._CDT_FLOAT :
						pstmt.setFloat( index + 1, ((Float)value).floatValue() );
						break;

					case ColumnDataType._CDT_DATE :
						pstmt.setDate( index + 1, new Date( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
						break;

					case ColumnDataType._CDT_DATETIME :
					case ColumnDataType._CDT_TIMESTAMP :
						pstmt.setTimestamp( index + 1, new Timestamp( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
						break;

					case ColumnDataType._CDT_TIME:
						pstmt.setTime( index + 1, new Time( ((java.util.Date)value).getTime() ), AVConstants._GMT_CALENDAR );
						break;

					case ColumnDataType._CDT_BOOLEAN :
						pstmt.setBoolean( index + 1, ((Boolean)value).booleanValue() );
						break;

					default:
						throw new XRuntime(  _module, "Column Type " + param.getColumnType() + " is not recognized for :" + param.getName() );

				}
			}
		}
	}
}
