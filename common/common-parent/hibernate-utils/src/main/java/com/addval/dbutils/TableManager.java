/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.dbutils;

import com.addval.utils.XRuntime;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * A wrapper class  for common database operations on single tables such as
 * inserts,
 * updates and deletes.  It also has utility functions to execute generic SQL
 * statements.
 * All database access should be processed through this class.  Most operations
 * are preformed
 * by passing Hashtables to the methods. The Hashtables contain information to
 * create valid
 * SQL statements.  The Hashtables typically contain key value pair required to
 * create WHERE
 * clauses or INSERT clauses.
 * @revision $Revision$
 * @author Sanka Dhanushkodi
 */
public class TableManager
{
    private static final String _and = " AND ";
    private static final String _comm = ", ";
    private static final String _eq = " = ";
    public static final long _MILLIS_IN_DAY = 86400000L;
    /**
     * Formatting for displaying date with times.
     */
    public static final String _DATETIME_FORMAT = "yyyy/MM/dd H:mm:ss";
    /**
     * Formatting for displaying dates
     */
    public static final String _DATE_FORMAT = "yyyy/MM/dd";
    /**
     * Formatting for displaying date in the standard US format.
     */
    public static final String _US_DATE_FORMAT = "MM/dd/yyyy";
    private DBTableInfo _tblInfo = null;

    /**
     * Constructor sets the name of the table to be used
     *
     * @param tblInfo  Name of the table to be set.
     * @roseuid 377A7D8B034D
     */
    public TableManager(DBTableInfo tblInfo)
    {
        if(tblInfo == null)
            throw new XRuntime(getClass().getName() + ".TableManager(DBTableInfo)", "arg is null");

        _tblInfo = tblInfo;
    }

    /**
     * Returns the name of the database table that this class was initialized with.
     *
     * @return table name that this class was initialized with
     * @roseuid 3774096A00A7
     */
    public String getTableName()
    {
        return _tblInfo.getTableName();
    }

    /**
     * Uses the parameters in a hashtable as the "where" clause in a  SQL string. For
     * example if the Hashtable has
     * <pre>
     * Key    -> "Name"
     * Value ->  "Jack".
     * This would be converted into
     * "WHERE  Name = 'James'"
     * </pre>
     *
     * @param where Hashtable
     * @return  A SQL Statement - String
     * @throws XRuntime
     * @roseuid 3774080D02B8
     */
    public String select(Hashtable where) throws XRuntime
    {
        String selectClause = buildSelect();
        String whereClause = "";
        if((where != null) && !where.isEmpty())
            whereClause = buildWhere(where);

        String sql = selectClause + " FROM " + this.getTableName() + whereClause;
        return sql;
    }

    /**
     * Performs a database "INSERT" Operation. The values
     * to be inserted are provided in the "values" hashtable.
     *
     * @param values  Hashtable
     *  - This hashtable has the column values that needs to be inserted.
     * @param conn    Connection
     * @return  Return value of the INSERT - int
     * @throws XRuntime If the database insert fails.
     * @roseuid 377407E9000D
     */
    public int insert(Hashtable values, Connection conn) throws XRuntime
    {
        RecID id = new RecID();
        return insert(values, conn, id);
    }

    /**
     * Performs a database "INSERT".   The values
     * to be inserted are provided in the "values" hashtable.
     *
     * @param values Hashtable
     *  - This hashtable has the column values that needs to be inserted.
     * @param conn Connection
     * @param idOut RecID. This is set to the value of the row id that was inserted.
     * @return  Result of the INSERT operation - int
     * @throws XRuntime  If no column value is given.
     * @roseuid 378E269C00F3
     */
    public int insert(Hashtable values, Connection conn, RecID idOut) throws XRuntime
    {
        if(values.isEmpty())
            throw new XRuntime(getClass().getName() + ".insert", "no values to insert");

        String insertClause = buildInsert(values, conn, idOut);
        return execUpdate(insertClause, conn);
    }

    /**
     * Performs a database "UPDATE" based on the "values" and the "where" Hashtables.
     *
     * @param values  Hashtable
     *  - This  has the column values that needs to be inserted.
     * @param where  Hashtable
     *  - This has where condition column values
     * @param conn Connection
     * @return Return value of the database UPDATE operation -
     * @throws XRuntime If the values or the  where Hashtable is empty.
     * @roseuid 377407FC0141
     */
    public int update(Hashtable values, Hashtable where, Connection conn) throws XRuntime
    {
        if(values.isEmpty())
            throw new XRuntime(getClass().getName() + ".update", "no values to update");

        if(where.isEmpty())
            throw new XRuntime(getClass().getName() + ".update", "empty where clause");

        String updateClause = buildUpdate(values);
        String whereClause = buildWhere(where);
        String sql = updateClause + whereClause;
        return execUpdate(sql, conn);
    }

    /**
     * Performs a database "DELETE"  based on the information in the "where"
     * Hashtable. <p>
     * Example:<p>
     * Key: "Name"  Value: "James". This would result in the SQL
     * DELETE FROM ... WHERE Name = 'James'
     *
     * @param where  Hashtable with the where clause.
     * @param conn Database
     * @return Result of the DELETE operation
     * @throws XRuntime If the "where" argument is empty.
     * @roseuid 377408080329
     */
    public int delete(Hashtable where, Connection conn) throws XRuntime
    {
        if(where.isEmpty())
            throw new XRuntime(getClass().getName() + ".delete", "empty where clause");

        String deleteClause = buildDelete();
        String whereClause = buildWhere(where);
        String sql = deleteClause + whereClause;
        return execUpdate(sql, conn);
    }

    /**
     * Runs a SQL on the database based on the input SQL string.
     * @param sql  String (A valid SQL string)
     * @param conn Connection
     * @return Statement
     * @roseuid 378E26F803E4
     */
    public static Statement execQuery(String sql, Connection conn)
    {
        // Check if the query if for running a stored procedure.
        // i.e contains 'exec'
        String sqlUpper = sql.toUpperCase();
        if(sqlUpper.indexOf("EXEC") == -1)
        {
            if(sqlUpper.indexOf("EXECUTE") == -1)
            {
                if(sqlUpper.indexOf("SELECT") == -1)
                    sql = "select * from " + sql;
            }
        } // if EXEC

        try
        {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
            return stmt;
        }
        catch(SQLException e)
        {
            throw new XRuntime("com.addval.dbutils.TableManager.execQuery", e.getMessage(), e.getErrorCode());
        }
    }

    /**
     * Runs a SQL on the database based on the input SQL string.
     *
     * @param sql  String (A valid SQL string)
     * @param rsType  int (The type of the resultset required)
     * @param rsConcurrency  int (The concurrency level of the resultset required)
     * @param conn Connection
     * @return Statement
     * @roseuid 39D13E4F007B
     */
    public static Statement execQuery(String sql, int rsType, int rsConcurrency, Connection conn)
    {
        // Check if the query if for running a stored procedure.
        // i.e contains 'exec'
        String sqlUpper = sql.toUpperCase();
        if(sqlUpper.indexOf("EXEC") == -1)
        {
            if(sqlUpper.indexOf("EXECUTE") == -1)
            {
                if(sqlUpper.indexOf("SELECT") == -1)
                    sql = "select * from " + sql;
            }
        } // if EXEC

        try
        {
            Statement stmt = conn.createStatement(rsType, rsConcurrency);
            stmt.executeQuery(sql);
            return stmt;
        }
        catch(SQLException e)
        {
            throw new XRuntime("com.addval.dbutils.TableManager.execQuery", e.getMessage(), e.getErrorCode());
        }
    }

    /**
     * Returns a Result Set Iterator based on the input SQL string. Useful to get
     * results in some preset batches. It a result set contains 100 records we could
     * use this method to get 10 records at a time.
     * @param sql String (A valid SQL string)
     * @param conn Connection
     * @param currPos String. Current position of the record set
     * @param action RSIterAction
     * @param pageSize int. Page size to return.
     * @return RSIterator
     * @roseuid 378FBCB0013A
     */
    public static RSIterator getRSIterator(String sql, Connection conn, String currPos, RSIterAction action, int pageSize)
    {
        int rowCount = getRowCount(sql, conn);
        Statement stmt = execQuery(sql, conn);
        return new RSIterator(stmt, currPos, action, rowCount, pageSize);
    }

    /**
     * Performs an "UPDATE" operation on the database.
     * @param sql  String. A valid SQL string
     * @param conn Connection
     * @return Results of the database UPDATE - int
     * @roseuid 378E27020122
     */
    public static int execUpdate(String sql, Connection conn)
    {
        Statement stmt = null;
        try
        {
            stmt = conn.createStatement();
            int rv = stmt.executeUpdate(sql);
            return rv;
        }
        catch(SQLException e)
        {
            throw new XRuntime("com.addval.dbutils.TableManager.execUpdate", e.getMessage(), e.getErrorCode());
        }
        finally
        {
            DBUtl.closeFinally(stmt, (Logger) null);
        }
    }

    /**
     * ORACLE SPECIFIC. Gets the next sequence number for a sequence.
     * @param seqName String
     * @param conn Connection
     * @return nextID - int
     * @roseuid 378E2CC501F5
     */
    public static int getNextID(String seqName, Connection conn)
    {
        int id = 0;
        String sql = "select " + seqName + ".nextVal from dual";
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = execQuery(sql, conn);
            rs = stmt.getResultSet();
            if(rs.next())
                id = rs.getInt(1);
        }
        catch(SQLException e)
        {
            throw new XRuntime("com.addval.utils.TableManager.getNextID", e.getMessage(), e.getErrorCode());
        }
        finally
        {
            DBUtl.closeFinally(rs, stmt, (Logger) null);
        }

        return id;
    }

    /**
     * Converts a java.util.Date to a String format. The format is specified by the
     * static variable _DATETIME_FORMAT
     * @param v java.util.Date
     * @return Date in string format
     * @roseuid 378E7D8102A8
     */
    public static String convertDateTime(java.util.Date v)
    {
        String rv = "";
        if(v != null)
        {
            SimpleDateFormat formatter = new SimpleDateFormat(_DATETIME_FORMAT);
            rv = formatter.format(v);
        }

        return rv;
    }

    /**
     * Converts a java.util.Date to a String format. The format is specified by the
     * static variable _DATE_FORMAT
     * @param v java.util.Date
     * @return Date in string format
     * @roseuid 3795029F02B3
     */
    public static String convertDate(java.util.Date v)
    {
        String rv = "";
        if(v != null)
        {
            SimpleDateFormat f = new SimpleDateFormat(_DATE_FORMAT);
            rv = f.format(v);
        }

        return rv;
    }

    /**
     * Creates the first portion of a simple SELECT statement
     * @param
     * @return String
     * @roseuid 378B8F020209
     */
    private String buildSelect()
    {
        String sql = " SELECT * ";
        return sql;
    }

    /**
     * Builds a SQL WHERE clause based on the key/values in the "where" Hashtable.
     *
     * @param where Hashtable
     * @return  A string with a valid WHERE clause
     * @roseuid 378B8F54010C
     */
    public String buildWhere(Hashtable where)
    {
        boolean empty = true;
        DBColumnInfo col = null;
        String val = null;
        String sql = "";
        Enumeration cols = _tblInfo.getColumns();
        while(cols.hasMoreElements())
        {
            col = (DBColumnInfo) cols.nextElement();
            switch(col.getDataType())
            {
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                if(where.get(col.getColumnName()) == null)
                    val = null;
                else
                    val = (where.get(col.getColumnName()).toString().length() == 0) ? "" : convertDateTime((java.util.Date) where.get(col.getColumnName()));

                break;
            default:
                val = (String) where.get(col.getColumnName());
                break;
            }

            if(val != null)
            {
                if(!empty)
                    sql += _and;

                if(col.getConverter().convert(val).equals("null"))
                    sql += (col.getColumnName() + " is " + col.getConverter().convert(val));
                else
                    sql += (col.getColumnName() + _eq + col.getConverter().convert(val));

                empty = false;
            }
        }

        if(!empty)
            sql = " WHERE " + sql;

        return sql;
    }

    /**
     * Builds a "INSERT INTO" statement.  Returns the id value of the new row inserted.
     * @parma conn Connection
     *
     * @param values  Hashtable
     * @param conn Connection
     * @param idOut RecID. This is the id of the row that was inserted.
     * @return String
     * @roseuid 378CCC280092
     */
    public String buildInsert(Hashtable values, Connection conn, RecID idOut)
    {
        boolean colEmpty = true;
        boolean valEmpty = true;
        DBColumnInfo col = null;
        StringBuffer valList = new StringBuffer();
        valList.append("(");
        StringBuffer colList = new StringBuffer();
        colList.append(" (");
        String val = null;
        String v = null;
        String sql = null;
        Enumeration cols = _tblInfo.getColumns();
        while(cols.hasMoreElements())
        {
            col = (DBColumnInfo) cols.nextElement();
            switch(col.getDataType())
            {
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                if(values.get(col.getColumnName()) == null)
                    val = null;
                else
                    val = (values.get(col.getColumnName()).toString().length() == 0) ? "" : convertDateTime((java.util.Date) values.get(col.getColumnName()));

                break;
            default:
                val = (String) values.get(col.getColumnName());
                break;
            }

            if(val == null)
            {
                if(col.isIDColumn())
                {
                    idOut.setId(getNextID(col.getSeqName(), conn));
                    v = idOut.toString();
                    if(!valEmpty)
                        valList.append(_comm);

                    valList.append(v);
                    valEmpty = false;
                    if(!colEmpty)
                        colList.append(_comm);

                    colList.append(col.getColumnName());
                    colEmpty = false;
                }
            }
            else
            {
                v = col.getConverter().convert(val);
                if(!valEmpty)
                    valList.append(_comm);

                valList.append(v);
                valEmpty = false;
                if(!colEmpty)
                    colList.append(_comm);

                colList.append(col.getColumnName());
                colEmpty = false;
            }
        }

        if(!colEmpty && !valEmpty)
            sql = "INSERT INTO " + getTableName() + colList + ") VALUES " + valList + ")";

        return sql;
    }

    /**
     * Builds an UPDATE SQL string based on the values in the update Hashtable
     *
     * @param values Hashtable
     * @return A string with a valide UPDATE SQL statement
     * @roseuid 378CCC2E01D1
     */
    public String buildUpdate(Hashtable values)
    {
        boolean empty = true;
        DBColumnInfo col = null;
        String val = null;
        String sql = " SET ";
        Enumeration cols = _tblInfo.getColumns();
        while(cols.hasMoreElements())
        {
            col = (DBColumnInfo) cols.nextElement();
            switch(col.getDataType())
            {
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
                if(values.get(col.getColumnName()) == null)
                    val = null;
                else
                    val = ((values.get(col.getColumnName())).toString().length() == 0) ? "" : convertDateTime((java.util.Date) values.get(col.getColumnName()));

                break;
            default:
                val = (String) values.get(col.getColumnName());
                break;
            }

            if(val != null)
            {
                if(!empty)
                    sql += _comm;

                sql += (col.getColumnName() + _eq + col.getConverter().convert(val));
                empty = false;
            }
        }

        if(!empty)
            sql = " UPDATE " + getTableName() + sql;

        return sql;
    }

    /**
     * Builds the first portion of a DELETE statement
     *
     * @return Partial SQL Statement - String
     * @roseuid 378CCC390169
     */
    public String buildDelete()
    {
        return " DELETE FROM " + getTableName();
    }

    /**
     * Returns the number of records in a table.
     * @param sql String
     * @param conn Connection
     * @return Number of rows - int
     * @exception
     * @roseuid 378FBEEA0361
     */
    private static int getRowCount(String sql, Connection conn)
    {
        int count = 0;
        String s = sql;
        int i = s.lastIndexOf(" FROM ");
        if(i < 0)
            i = s.lastIndexOf(" from ");

        if(i < 0)
            i = s.lastIndexOf(" From ");

        if(i < 0)
            throw new XRuntime("com.addval.dbutils.TableManager.getRowCount", "SQL Query must contain FROM-Clause");

        String c;
        int endIndex = s.indexOf("ORDER BY");
        if(endIndex < i)
            endIndex = s.indexOf("order by");

        if(endIndex > i)
            c = "select count(*) " + s.substring(i, endIndex);
        else
            c = "select count(*) " + s.substring(i);

        Statement stmt = execQuery(c, conn);
        try
        {
            ResultSet rs = stmt.getResultSet();
            if(rs.next())
                count = rs.getInt(1);

            stmt.close();
        }
        catch(SQLException e)
        {
            throw new XRuntime("com.addval.dbutils.TableManager.getRowCount()", e.getMessage(), e.getErrorCode());
        }

        return count;
    }

    /**
     * Performs a SELECT operation on the database based on the input "where" Hashtable
     *
     * @param where Hashtable
     * @param conn Connection
     * @return Statement
     * @throws XRuntime If the select query fails.
     * @roseuid 37B201CF002E
     */
    public Statement select(Connection conn, Hashtable where) throws XRuntime
    {
        return execQuery(select(where), conn);
    }

    /**
     * DB Independent Gets the next sequence number for a sequence.
     * Requires a table called SEQUENCE_GENERATOR(SEQUENCE_NAME VARCHAR(30,
     * MIN_VALUE NUMERIC(9), MAX_VALUE NUMERIC(9), INCREMENT_VALUE NUMERIC(9), CURRENT_VALUE NUMBERIC(9),
     * CYCLE_FLAG NUERIC(1))
     * @param seqName String
     * @param conn Connection
     * @return nextID - int
     *
     *
     */
    public static int getNextSequence(String seqName, Connection conn)
    {
        return getNextSequence(seqName, null, conn);
    }

    /**
     * DB Independent Gets the next sequence number for a sequence.
     * Requires a table called SEQUENCE_GENERATOR(SEQUENCE_NAME VARCHAR(30,
     * MIN_VALUE NUMERIC(9), MAX_VALUE NUMERIC(9), INCREMENT_VALUE NUMERIC(9), CURRENT_VALUE NUMBERIC(9),
     * CYCLE_FLAG NUERIC(1))
     * @param seqName String
     * @param conn Connection
     * @return nextID - int
     *
     *
     */
    public static int getNextSequence(String seqName, Date currentDateTime, Connection conn)
    {
        int id = 0;
        int min_id = 0;
        int max_id = 0;
        int increment = 0;
        int cycle_flag = 0;
        int found = 0;
        Date last_reset_date = null;
        Date next_reset_date = null;
        int reset_interval = 0;

        // Get a lock
        String locksql = "UPDATE SEQUENCE_GENERATOR SET CURRENT_VALUE = CURRENT_VALUE WHERE SEQUENCE_NAME = '" + seqName + "' ";
        execUpdate(locksql, conn);
        String sql = null;
        if(currentDateTime != null)
            sql = "select MIN_VALUE, MAX_VALUE, INCREMENT_VALUE, CURRENT_VALUE, CYCLE_FLAG, TRUNC(LAST_RESET_DATETIME) AS LAST_RESET_DATETIME, TRUNC(LAST_RESET_DATETIME)+RESET_INTERVAL_DAYS AS NEXT_RESET_DATETIME FROM SEQUENCE_GENERATOR WHERE SEQUENCE_NAME = '" + seqName + "'";
        else
            sql = "select MIN_VALUE, MAX_VALUE, INCREMENT_VALUE, CURRENT_VALUE, CYCLE_FLAG FROM SEQUENCE_GENERATOR WHERE SEQUENCE_NAME = '" + seqName + "'";

        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = execQuery(sql, conn);
            rs = stmt.getResultSet();
            if(rs.next())
            {
                min_id = rs.getInt(1);
                max_id = rs.getInt(2);
                increment = rs.getInt(3);
                id = rs.getInt(4);
                cycle_flag = rs.getInt(5);
                if(currentDateTime != null)
                {
                    last_reset_date = rs.getDate(6, com.addval.utils.AVConstants._GMT_CALENDAR);
                    next_reset_date = rs.getDate(7, com.addval.utils.AVConstants._GMT_CALENDAR);
                }

                found = 1;
            }
        }
        catch(SQLException e)
        {
            throw new XRuntime("com.addval.dbutils.TableManager.getNextID", e.getMessage(), e.getErrorCode());
        }
        finally
        {
            DBUtl.closeFinally(rs, stmt, (Logger) null);
        }

        boolean cycle_now_flag = false;
        if(currentDateTime != null)
        {
            if(last_reset_date == null)
                cycle_now_flag = true;
            else
            {
                if(currentDateTime.getTime() >= next_reset_date.getTime())
                    cycle_now_flag = true;
            }
        }

        if(cycle_now_flag == true)
            id = min_id;

        if(id < min_id)
            id = min_id;
        else
        {
            id = id + increment;
            if(id > max_id)
                id = min_id;
        }

        String updsql = null;
        if(cycle_now_flag == true)
            updsql = "UPDATE SEQUENCE_GENERATOR SET LAST_RESET_DATETIME = SYSDATE, CURRENT_VALUE = " + id + " WHERE SEQUENCE_NAME = '" + seqName + "' ";
        else
            updsql = "UPDATE SEQUENCE_GENERATOR SET CURRENT_VALUE = " + id + " WHERE SEQUENCE_NAME = '" + seqName + "' ";

        execUpdate(updsql, conn);
        return id;
    }

    /**
     * Gets the next sequence number for a sequence.
     * @param seqName String
     * @param conn Connection
     * @param serverType Server Type
     * @return nextID - int
     * @roseuid 378E2CC501F5
     */
    public static int getNextID(String seqName, Connection conn, String serverType)
    {
        int id = 0;
        if((serverType == null) || serverType.equals(com.addval.utils.AVConstants._ORACLE)|| serverType.equals(com.addval.utils.AVConstants._POSTGRES))
            id = getNextID(seqName, conn);
        else
            id = getNextSequence(seqName, conn);

        return id;
    }
}
