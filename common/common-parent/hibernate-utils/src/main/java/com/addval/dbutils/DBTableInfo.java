//Source file: D:/users/prasad/Projects/Common/src/client/source/com/addval/dbutils/DBTableInfo.java

/* Copyright AddVal Technology Inc. */

package com.addval.dbutils;

import com.addval.utils.XRuntime;
import java.util.Hashtable;
import java.util.Enumeration;

/**
   Contains information about all the columns in a particular table.
   The column information is stored in a Hashtable.  The key in the Hashtable
   is the column name and the value is of type DBColInfo.
   @author Sankar Dhanushkodi
   @revision $Revision$
 */
public class DBTableInfo {
	private String _tableName;
	private Hashtable _columns;
	
	/**
	   Constructor.
	   @param tableName String
	   @return
	   @exception
	   @roseuid 376EB0310234
	 */
	public DBTableInfo(String tableName) {
      _tableName = tableName;
      _columns = new Hashtable();
   }
	
	/**
	   Access method for the tableName property.
	   @param
	   @return table name - String
	   @exception
	   @roseuid 376EB031034C
	 */
	public String getTableName() {
      return _tableName;
   }
	
	/**
	   Returns information about a column.
	   @param colName String
	   @return Column Information - DBColInfo
	   @exception
	   @roseuid 376EB0320127
	 */
	public DBColumnInfo getColumn(String colName) {
      return (DBColumnInfo)_columns.get( colName );
   }
	
	/**
	   Returns information about all the columns in a table.
	   Returns the Hashtable of column information as an
	   Enumeration type.
	   @param
	   @return Column information - Enumeration
	   @exception
	   @roseuid 378655BC006B
	 */
	public Enumeration getColumns() {
      return _columns.elements();
   }
	
	/**
	   Adds a column to the collection (Hashtable)
	   @param col ColInfoDB
	   @return
	   @exception
	   @roseuid 390F4519018F
	 */
	public void addColumn(DBColumnInfo col) {
      _columns.put( col.getColumnName(), col );
   }
	
	/**
	   Overrides the ID column for a table
	   @roseuid 39D8F27F0054
	 */
	public void setIDColumn(String colName) {

		DBColumnInfo dbColumnInfo = (DBColumnInfo)_columns.get( colName );
		if (dbColumnInfo == null)
			throw new XRuntime( "DBTableInfo.setIDColumn()", "Column does not exist in the table :" + colName );

		dbColumnInfo.setIDColumn();
	}
	
	/**
	   Returns the class as a string. Used for debugging
	   @param
	   @return  Class as a string - String
	   @exception
	   @roseuid 378A4165011C
	 */
	public String toString() {
      StringBuffer s = new StringBuffer( "DBTableInfo\ntableName = " + _tableName + "\n" );
      int    num = 0;
      for ( java.util.Enumeration e = _columns.elements(); e.hasMoreElements(); ) {
         num++;
         s.append( "\nColumn #" + num + "\n" + e.nextElement().toString() );
      }
      return s.toString();
   }
}
