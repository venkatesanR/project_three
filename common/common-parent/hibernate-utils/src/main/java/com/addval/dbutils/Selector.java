//Source file: c:\\projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\Selector.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.Collection;
import java.sql.SQLException;

public class Selector 
{
    
    /**
     * @param rs
     * @param iteratorCriteria
     * @param iteratorStatus
     * @param converter
     * @param conn
     * @param objectToReturn
     * @throws SQLException
     * @roseuid 3C58F7F2038C
     */
    public static void buildData(ResultSet rs, IteratorCriteria iteratorCriteria, IteratorStatus iteratorStatus, RStoObjectConverter converter, Connection conn, Object objectToReturn) throws SQLException 
    {

      if ( rs != null )
      {
         long firstRecord = 1;
         long numberOfRecords = 0;
         long meter = 1;
         long maxRecordNumber = Long.MAX_VALUE;

         //advance to the first record
         boolean more = true;
         if ( !rs.next() ) { more = false; }

         if ( iteratorCriteria != null ) {

            firstRecord = iteratorCriteria.getStartRecord();

            while ( ( meter < firstRecord ) && more ) {
               //call in skip mode
               meter++;
               if ( converter.buildElement(rs, objectToReturn, true) ) {
                  if ( !rs.next() ) { more = false; }
               }
            }

            if ( iteratorCriteria.getNumberOfRecords() == 0 ) {
               maxRecordNumber = Long.MAX_VALUE;
            }
            else {
               maxRecordNumber = firstRecord + iteratorCriteria.getNumberOfRecords();
            }

         }

         while ( ( meter < maxRecordNumber ) && more ) {
            //call to build
            meter++;
            numberOfRecords++;
            if ( converter.buildElement (rs, objectToReturn, false) ) {
               if ( !rs.next() ) { more = false; }
            }
         }

         if ( iteratorStatus != null ) {
            iteratorStatus.setPrevious ( firstRecord > 1 );
            iteratorStatus.setNext ( more );

            iteratorStatus.setStartPosition ( firstRecord );
            iteratorStatus.setPageSize ( numberOfRecords );
         }
      }     
    }
    
    /**
     * @param sql
     * @param iteratorCriteria
     * @param iteratorStatus
     * @param converter
     * @param conn
     * @param objectToReturn
     * @throws SQLException
     * @roseuid 3BF01C6A03C7
     */
    public static void buildData(String sql, IteratorCriteria iteratorCriteria, IteratorStatus iteratorStatus, RStoObjectConverter converter, Connection conn, Object objectToReturn) throws SQLException 
    {
      Statement stmt = null;
      try
      {
         stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery ( sql );
         buildData(rs, iteratorCriteria, iteratorStatus, converter, conn, objectToReturn);
      }
      finally
        {
            if ( stmt != null )  stmt.close();
        }     
    }
    
    /**
     * @param sql
     * @param converter
     * @param conn
     * @param objectToReturn
     * @throws SQLException
     * @roseuid 3BF0856C019A
     */
    public static void buildData(String sql, RStoObjectConverter converter, Connection conn, Object objectToReturn) throws SQLException 
    {
      Statement stmt = null;
      try
      {
         stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery ( sql );
         if ( rs != null && rs.next() )
         {
            converter.buildElement ( rs, objectToReturn, true );
         }
      }
        finally
        {
            if ( stmt != null )  stmt.close();
        }     
    }
}
