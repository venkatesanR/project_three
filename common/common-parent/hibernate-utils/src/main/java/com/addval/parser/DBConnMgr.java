//Source file: d:\\projects\\aerlingus\\source\\com\\addval\\parser\\DBConnMgr.java

package com.addval.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.addval.utils.XRuntime;
import com.addval.environment.Environment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.util.Hashtable;
import java.sql.Connection;

public class DBConnMgr implements Constants
{
    private static final String _module = "DBConnMgr";
	private static Environment _environment = null;
    /**
     * Access method for the _module property.
     *
     * @return   the current value of the _module property
     */
    public static String getModule()
    {
        return _module;
    }

	public static void setEnvironment(Environment environment) {
        _environment = environment;
    }

    public static Environment getEnvironment() {
        return _environment;
    }

    /**
     * @param SQL
     * @param projectName
     * @param conn
     * @return java.sql.PreparedStatement
     * @roseuid 3D6B0B0D0310
     */
    public static PreparedStatement getPreparedStatement(String SQL, Connection conn)
    {
        try {
            return conn.prepareStatement( SQL );
        }
        catch(SQLException sqle) {
            throw new XRuntime( _module, "Error while Preparing Statement! " + sqle );
        }
    }

    /**
     * @param SQL
     * @param projectName
     * @return java.sql.PreparedStatement
     * @roseuid 3E1528F602E2
     */
    public static PreparedStatement getPreparedStatement(String SQL, String projectName)
    {
        return getPreparedStatement( SQL, getConnection( projectName ) );
    }

    /**
     * @param preparedStatement
     * @param dbValues
     * @param lineMetaData
     * @param dbColumns
     * @param dbColumnTypes
     *
     * @param dbColumns[]
     * @param dbColumnTypes[]
     * @roseuid 3D6F4A03026E
     */
    public static void prepareStatement(PreparedStatement preparedStatement, String dbColumns[], String dbColumnTypes[], java.util.Hashtable dbValues)
    {
        if (preparedStatement == null)
            return;
        int count = dbColumns.length;
        try {
            for (int i=0; i<count; i++) {
                String value = (String)dbValues.get( dbColumns[i] );
                if (dbColumnTypes[i].equals( _CHAR )) {
                    preparedStatement.setString( i+1, value );
                }
                else if (dbColumnTypes[i].equals( _DATE )) {
                    preparedStatement.setDate( i+1, value == null ? null : java.sql.Date.valueOf( value ) );
                }
                else if (dbColumnTypes[i].equals( "DATETIME" )) {
                    preparedStatement.setTime( i+1, value == null ? null : java.sql.Time.valueOf( value ) );
                }
                else if (dbColumnTypes[i].equals( "TIMESTAMP" )) {
                    preparedStatement.setTimestamp( i+1, value == null ? null : java.sql.Timestamp.valueOf( value ) );
                }
                else if (dbColumnTypes[i].equals( _NUMBER )) {
                    if (Utils.isNullOrEmpty( value ))
                        value = "0";
                    preparedStatement.setInt( i+1, Integer.parseInt( value ) );
                }
                else if (dbColumnTypes[i].equals( _DECIMAL )) {
                    if (Utils.isNullOrEmpty( value ))
                        value = "0";
                    preparedStatement.setDouble( i+1, Double.parseDouble( value ) );
                }
            }
            preparedStatement.addBatch();
        }
        catch( SQLException sqle ) {
            throw new XRuntime( _module, "Error while setting data to PreparedStatement! " + sqle );
        }
    }

    /**
     * @param SQL
     * @param projectName
     * @return String
     * @roseuid 3D6B0B0D03DA
     */
    public static String getKey(String SQL, String projectName)
    {
        if (Utils.isNullOrEmpty( SQL ))
            return null;
        Connection conn = null;
        Statement stmt  = null;
        ResultSet rs    = null;
        try {
            conn = getConnection( projectName );
            stmt = conn.createStatement();
            rs = stmt.executeQuery( SQL );
            if (!rs.next())
                return null;
            return rs.getString( 1 );
        }
        catch(SQLException sqle) {
            throw new XRuntime( _module, "Error while getting resultset. " + sqle );
        }
        finally {
            try {
                if (rs != null)
                    rs.getStatement().close();
                releaseConnection( conn, projectName );
            }
            catch(SQLException sqle) {
                // do nothing
            }
        }
    }

    /**
     * @param seqName
     * @param projectName
     * @return String
     * @roseuid 3D6DB4E3033C
     */
    public static String getSeqKey(String seqName, String projectName)
    {
        if (Utils.isNullOrEmpty( seqName ))
            return null;
        return getKey( _SELECT + _SPACE + seqName + _NEXT_VAL, projectName );
    }

    /**
     * @param pStatements
     * @param pStatements[]
     * @roseuid 3D6B0B0E0006
     */
    public static synchronized void executeBatch(PreparedStatement pStatements[])
    {
        try {
            for (int i=0; i<pStatements.length; i++) {
                if (pStatements[i] == null)
                    continue;
//                System.out.println("Executing prepared Statement - " + i);
                pStatements[i].executeBatch();
                pStatements[i].clearBatch();
            }
        }
        catch(SQLException sqle) {
            throw new XRuntime( _module, "Error while Executing Statement! " + sqle );
        }
    }

    /**
     * @param projectName
     * @return java.sql.Connection
     * @roseuid 3D6B0B0E00D8
     */
    public static Connection getConnection(String projectName)
    {
		if(getEnvironment() != null)
        	return getEnvironment().getDbPoolMgr().getConnection();
		return Environment.getInstance( projectName ).getDbPoolMgr().getConnection();
    }

    /**
     * @param conn
     * @param projectName
     * @roseuid 3D6B0B0E019F
     */
    public static void releaseConnection(Connection conn, String projectName)
    {
        if (conn == null)
            return;

		if(getEnvironment() != null)
        	getEnvironment().getDbPoolMgr().releaseConnection( conn );
        else
            Environment.getInstance( projectName ).getDbPoolMgr().releaseConnection( conn );
    }

    /**
     * @param pStmt
     * @return java.sql.ResultSet
     * @roseuid 3E1528F70167
     */
    public static ResultSet getResultSet(PreparedStatement pStmt)
    {
        try {
            return pStmt.executeQuery();
        }
        catch(SQLException sqle) {
            throw new XRuntime( _module, "Error while executing statement! " + sqle );
        }
    }
}
