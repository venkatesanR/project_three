package com.addval.dbutils;

import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import com.addval.utils.XRuntime;
import org.apache.log4j.Logger;


public class DAOSequenceGenerator {

	/**
	 * ORACLE SPECIFIC. Gets the next sequence number for a sequence.
	 * @param seqName String
	 * @param conn Connection
	 * @return nextID - int
	 * @roseuid 378E2CC501F5
	 */
	public static int getNextID(String seqName, Connection conn) {

		int 		id	 = 0;
		String 		sql	 = "select " + seqName + ".nextVal from dual";
		Statement 	stmt = null;
		ResultSet 	rs   = null;

		try {
			stmt = execQuery( sql, conn );
			rs   = stmt.getResultSet();

			if (rs.next())
				id = rs.getInt( 1 );
		}
		catch ( SQLException e ) {

			throw new XRuntime( "com.addval.dbutils.DBSequenceGenerator.getNextID", e.getMessage(), e.getErrorCode() );
		}
		finally {

			DBUtl.closeFinally( rs, stmt, (org.apache.log4j.Logger)null );
		}

		return id;
	}



	/**
	 * DB Independent Gets the next sequence number for a sequence.
	 * Requires a table called SEQUENCE_GENERATOR(SEQUENCE_NAME VARCHAR(30,
	 * MIN_VALUE NUMERIC(9), MAX_VALUE NUMERIC(9), CURRENT_VALUE NUMBERIC(9),
	 * CYCLE_FLAG NUERIC(1))
	 * @param seqName String
	 * @param conn Connection
	 * @return nextID - int
	 *
	 *
	 */
	public static int getNextSequence(String seqName, Connection conn) {

		int id      	= 0;
		int min_id  	= 0;
		int max_id  	= 0;
		int increment 	= 0;
		int cycle_flag 	= 0;
		int found   	= 0;

		// Get a lock
		String locksql = "UPDATE SEQUENCE_GENERATOR SET CURRENT_VALUE = CURRENT_VALUE WHERE SEQUENCE_NAME = '" + seqName + "' ";

		execUpdate(locksql, conn);

		String sql  = "select MIN_VALUE, MAX_VALUE, INCREMENT, CURRENT_VALUE, CYCLE_FLAG FROM SEQUENCE_GENERATOR WHERE SEQUENCE_NAME = '" + seqName + "'";
		Statement stmt  = execQuery( sql, conn );

		try {
			ResultSet rs    = stmt.getResultSet();

			if ( rs.next() ) {

				min_id = rs.getInt( 1 );
				max_id = rs.getInt( 2 );
				increment = rs.getInt( 3 );
				id = rs.getInt( 4 );
				cycle_flag = rs.getInt(5);
				found = 1;
			}

			stmt.close();
		}
		catch ( SQLException e ) {

			throw new XRuntime( "com.addval.dbutils.DBSequenceGenerator.getNextID", e.getMessage(), e.getErrorCode() );
		}

		if (id < min_id) {

			id = min_id;
		}
		else {

			id = id + increment;
			if (id > max_id)
				id = min_id;
		}

		String updsql  = "UPDATE SEQUENCE_GENERATOR SET CURRENT_VALUE = " + id + " WHERE SEQUENCE_NAME = '" + seqName + "' ";

		execUpdate(updsql, conn);

		return id;
	}

	/**
	 * Performs an "UPDATE" operation on the database.
	 * @param sql  String. A valid SQL string
	 * @param conn Connection
	 * @return Results of the database UPDATE - int
	 * @roseuid 378E27020122
	 */
	public static int execUpdate(String sql, Connection conn) {

		try {

			Statement   stmt    = conn.createStatement();
			int         rv      = stmt.executeUpdate( sql );

			stmt.close();

			return rv;
		}
		catch (SQLException e) {

			throw new XRuntime( "com.addval.dbutils.DBSequenceGenerator.execUpdate", e.getMessage(), e.getErrorCode() );
		}
	}


	/**
	 * Runs a SQL on the database based on the input SQL string.
	 * @param sql  String (A valid SQL string)
	 * @param conn Connection
	 * @return Statement
	 * @roseuid 378E26F803E4
	 */
	public static Statement execQuery(String sql, Connection conn) {

		try {

			Statement stmt = conn.createStatement();
			stmt.executeQuery( sql );

			return stmt;
		}
		catch ( SQLException e ) {

			throw new XRuntime( "com.addval.dbutils.DBSequenceGenerator.execQuery", e.getMessage(), e.getErrorCode() );
        }
	}
}
