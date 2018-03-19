//Source file: C:\\users\\prasad\\projects\\common\\src\\client\\source\\com\\addval\\dbutils\\DBSchemaInfo.java

/**
 * Copyright
 * AddVal Technology Inc.
 */


package com.addval.dbutils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.addval.utils.XRuntime;
import java.util.Hashtable;
import java.sql.Connection;

/**
 * Holds the schema information about tables/views in a
 * associated connection. Uses the DatabaseMetaData
 * object to get the schema information
 * @author Sankar Dhanushkodi
 * @revision $Revision$
 * @author AddVal Technology Inc.
 */
public class DBSchemaInfo
{
	private String _name;
	private Hashtable _tables = null;

	/**
	 * @param name
	 * @param types[]
	 * @param tablePattern
	 * @param conn
	 * @roseuid 3F3995B50128
	 */
	public DBSchemaInfo(String name, String types[], String tablePattern, Connection conn)
	{
        _name = name;
        _tables = new Hashtable();

        DatabaseMetaData    dbmd;
        ResultSet           tableDetail;
        ResultSet           columnDetail;
        String []           tableTypes = { "TABLE", "VIEW", "SYNONYM" };

        if (types != null && types.length > 0)
        	tableTypes = types;

        try {
           dbmd = conn.getMetaData();
           tableDetail  = dbmd.getTables( null, name, tablePattern != null && tablePattern.length() > 0 ? tablePattern : null, tableTypes );
           String           currTblName;
           while (tableDetail.next()) {
              currTblName = tableDetail.getString("TABLE_NAME");
              columnDetail = dbmd.getColumns(null, name, currTblName, null);

              DBTableInfo tbl = new DBTableInfo( currTblName.toUpperCase() );
              _tables.put( currTblName.toUpperCase(), tbl );

              while (columnDetail.next()) {
                 DBColumnInfo col = new DBColumnInfo( columnDetail );
                 tbl.addColumn( col );
              }
           }
        }
        catch (SQLException exp) {
           XRuntime RExp = new XRuntime(getClass().getName(),exp.getMessage());
           throw RExp;
        }
	}

	/**
	 * Constructor. Gets schema information about all the
	 * tables and views.
	 * @param:
	 * @param name
	 * @param conn
	 * @exception:
	 * @roseuid 376EB183017C
	 */
	public DBSchemaInfo(String name, Connection conn)
	{
		this( name, null, null, conn );
	}

	/**
	 * Access method for the table property.
	 * @param:
	 * @param tableName
	 * @return  the current value of the table property
	 * @exception:
	 * @roseuid 376EB18302C6
	 */
	public DBTableInfo getTable(String tableName)
	{
      return (DBTableInfo)_tables.get( tableName );
	}

	/**
	 * Accessor to the schema name.
	 * @param:
	 * @return the underlying schema name.
	 * @exception:
	 * @roseuid 3773D6B80345
	 */
	public String getSchemaName()
	{
      return _name;
	}

	/**
	 * Returns the class as a string. Useful for debugging.
	 * @param:
	 * @return the class as a string
	 * @exception:
	 * @roseuid 378A414500D0
	 */
	public String toString()
	{
      StringBuffer s = new StringBuffer( "DBSchemaInfo:\nSchemaName = " + _name + "\n" );
      int    num = 0;
      for ( java.util.Enumeration e = _tables.elements(); e.hasMoreElements(); ) {
         num++;
         s.append( "\ntable #" + num + "\n" + e.nextElement().toString() );
      }
      return s.toString();
	}
}
