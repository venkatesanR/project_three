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

import com.addval.utils.LogFileMgr;
import com.addval.utils.LogMgr;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.StringTokenizer;


/**
 * @author  ravi
 */
public class DBUtl
{
    public static final String _SQL_STR_QUOTE = "'";
    public static final String _SQL_STR_NULL = "NULL";
    private static final transient Logger _logger = LogMgr.getLogger( DBUtl.class );
    /**
     * @param stmt
     * @param logFileMgr
     * @roseuid 3EF36C12027C
     */
    public static void closeFinally(Statement stmt, Logger logger)
    {
        closeFinally(null, stmt, logger);
    }

    public static void closeFinally(ResultSet rs, Statement stmt, Logger logger)
    {
        closeFinally(rs, stmt, null, null, logger);
    }

    public static void closeFinally(ResultSet rs, Statement stmt, Connection conn, DBPoolMgr dbPoolMgr, Logger logger)
    {
        if(rs != null)
        {
            try
            {
                rs.close();
            }
            catch(SQLException e)
            {
                if(logger != null)
                    logger.error(e);
            }
        }

        if(stmt != null)
        {
            try
            {
            	if ( stmt instanceof DBStatement && conn instanceof DBConnection) {
            		((DBStatement)stmt).close(((DBConnection)conn).getConnId());
            	} else {
            		stmt.close();
            	}
            }
            catch(SQLException e)
            {
                if(logger != null)
                    logger.error(e);
            }
        }

        if(conn != null)
        {
            try
            {
                if(dbPoolMgr != null)
                    dbPoolMgr.releaseConnection(conn);
                else
                    conn.close();
            }
            catch(Exception e)
            {
                if(logger != null)
                    logger.error(e);
            }
        }
    }

    /**
     * @param stmt
     * @param logFileMgr
     * @roseuid 3EF36C12027C
     */
    public static void closeFinally(Statement stmt, LogFileMgr logger)
    {
        closeFinally(null, stmt, logger);
    }

    public static void closeFinally(ResultSet rs, Statement stmt, LogFileMgr logger)
    {
        closeFinally(rs, stmt, null, null, logger);
    }

    public static void closeFinally(ResultSet rs, Statement stmt, Connection conn, DBPoolMgr dbPoolMgr, LogFileMgr logger)
    {
        if(rs != null)
        {
            try
            {
                rs.close();
            }
            catch(SQLException e)
            {
                if(logger != null)
                    logger.logError(e);
            }
        }

        if(stmt != null)
        {
            try
            {
            	if ( stmt instanceof DBStatement && conn instanceof DBConnection) {
            		((DBStatement)stmt).close(((DBConnection)conn).getConnId());
            	} else {
            		stmt.close();
            	}
            }
            catch(SQLException e)
            {
                if(logger != null)
                    logger.logError(e);
            }
        }

        if(conn != null)
        {
            try
            {
                if(dbPoolMgr != null)
                    dbPoolMgr.releaseConnection(conn);
                else
                    conn.close();
            }
            catch(Exception e)
            {
                if(logger != null)
                    logger.logError(e);
            }
        }
    }

    /**
     * @param val
     * @return java.lang.String
     * @roseuid 3EF36C7103AF
     */
    public static String sqlStr(String val)
    {
        if(StrUtl.isEmpty(val))
            return _SQL_STR_NULL;

        return escapeQuotes(val);
    }

    /**
     * @param val
     * @return java.lang.String
     * @roseuid 3EF36C72002B
     */
    public static String sqlStrTrimmed(String val)
    {
        if(StrUtl.isEmptyTrimmed(val))
            return _SQL_STR_NULL;

        return escapeQuotes(val.trim());
    }

    /**
     * @param inString
     * @return java.lang.String
     * @roseuid 3EF36C720090
     */
    private static String escapeQuotes(String inString)
    {
        StringBuffer outString = new StringBuffer();
        String token = null;
        outString.append(_SQL_STR_QUOTE);
        if(inString != null)
        {
            StringTokenizer st = new StringTokenizer(inString, _SQL_STR_QUOTE, true);
            while(st.hasMoreTokens())
            {
                token = st.nextToken();
                if(token.equals(_SQL_STR_QUOTE))
                    outString.append(_SQL_STR_QUOTE).append(_SQL_STR_QUOTE);
                else
                    outString.append(token);
            }
        }

        outString.append(_SQL_STR_QUOTE);
        return outString.toString();
    }

	public static String clobToString(Clob clob, String columnName) throws SQLException
	{
        if (clob == null) 
        	return null;
    	if (clob.length() > Integer.MAX_VALUE)
    		throw new XRuntime( "DBUtl.clobToString()", columnName + " (Clob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + clob.length() );        
    	byte[] fileByteArr = new byte[(int) clob.length()];
        InputStream inStream = clob.getAsciiStream();
        try {
            inStream.read(fileByteArr);
            if (inStream != null) 
                inStream.close();
        }
        catch (IOException ioe) {
        	_logger.error( "Error while reading CLOB Column " + columnName, ioe ); 
        	throw new XRuntime("DBUtl.clobToString()",  "Error while reading CLOB Column " + columnName );
		}
        finally {
        	try {
	        	if (inStream != null)
	        		inStream.close();
        	}
            catch (IOException ioe) {
            	_logger.error( "Error while closing CLOB Column reader " + columnName, ioe ); 
            	throw new XRuntime(  "DBUtl.clobToString()", "Error while Closing CLOB Column reader " + columnName );
    		}
        }
        return new String( fileByteArr );
	}

	public static String blobToString(Blob blob, String columnName) throws SQLException
	{
        if (blob == null) 
        	return null;
    	if (blob.length() > Integer.MAX_VALUE)
    		throw new XRuntime( "DBUtl.blobToString()", columnName + " (Blob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + blob.length() );        
    	byte[] fileByteArr = new byte[(int) blob.length()];
        InputStream inStream = blob.getBinaryStream();
        try {
            inStream.read(fileByteArr);
            if (inStream != null) 
                inStream.close();
        }
        catch (IOException ioe) {
        	_logger.error( "Error while reading BLOB Column " + columnName, ioe ); 
        	throw new XRuntime("DBUtl.blobToString()",  "Error while reading BLOB Column " + columnName );
		}
        finally {
        	try {
	        	if (inStream != null)
	        		inStream.close();
        	}
            catch (IOException ioe) {
            	_logger.error( "Error while closing BLOB Column reader " + columnName, ioe ); 
            	throw new XRuntime(  "DBUtl.blobToString()", "Error while Closing BLOB Column reader " + columnName );
    		}
        }
        return new String( fileByteArr );
	}
	public static StringReader stringToClob(String value, String columnName) 
	{
		if (value == null) {
            return null;
		}
    	if (value.length() > Integer.MAX_VALUE)
    		throw new XRuntime( "DBUtl.stringToClob()", columnName + " (Clob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + value.length() );        
        return new StringReader( value );
	}
}
