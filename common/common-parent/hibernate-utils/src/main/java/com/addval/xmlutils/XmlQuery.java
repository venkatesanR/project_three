//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\xmlutils\\XmlQuery.java

package com.addval.xmlutils;

import com.addval.utils.XRuntime;
import java.sql.ResultSetMetaData;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

public class XmlQuery {
	private static final String _module = "XmlQuery";
	private static final String _SELECT = "SELECT ";
	private static final String _FROM = " FROM ";
	private static final String _AS = " AS ";
	private static final String _AT = "@";
	private static final String _YES = "YES";
	private static final String _COMMA = ",";
	private static final String _SPACE = " ";
	private static final String _EQUAL = "=";
	private static final String _DQUOTE = "\"";
	private static final String _TAB = "\t";
	private static final String _DTAB = "\t\t";
	private static final String _NULL = "null";
	private String _rowIdAttrName = "num";
	private String _rowTag = "ROW";
	private String _rowsetTag = "ROWSET";
	private long _rowCount = 1;
	private boolean _tagCase = false;
	private boolean _useNullAttrId = false;
	private boolean _localRS = false;
	private ArrayList _attrColumns = new ArrayList ();
	private ResultSet _resultSet = null;
	private Statement _stmt = null;
	
	/**
	 * @param resultSet
	 * @roseuid 3DCE881E03A6
	 */
	public XmlQuery(ResultSet resultSet) {

        _resultSet = resultSet;		
	}
	
	/**
	 * constructor for compatability with XSU class
	 * @param conn
	 * @param resultSet
	 * @roseuid 3DCE881E02E7
	 */
	public XmlQuery(Connection conn, ResultSet resultSet) {

        this( resultSet );		
	}
	
	/**
	 * @param conn
	 * @param sql
	 * @roseuid 3DCE881E0265
	 */
	public XmlQuery(Connection conn, String sql) {

        setResultSet( conn, sql );
        _localRS = true;		
	}
	
	/**
	 * @param conn
	 * @param sql
	 * @roseuid 3DCE881F0022
	 */
	private void setResultSet(Connection conn, String sql) {

        if (sql == null)
            throw new XRuntime( _module, "SQL String should not be null!" );

        if (sql.trim().length() == 0)
            throw new XRuntime( _module, "SQL String is empty!" );

        if (conn == null)
            throw new XRuntime( _module, "Connection should not be null!" );

        try {
			_stmt      = conn.createStatement();
            _resultSet = _stmt.executeQuery( parseSql( sql ) );
        }
        catch(SQLException sqle) {
            throw new XRuntime( _module, sqle.getMessage() );
        }		
	}
	
	/**
	 * @return String
	 * @roseuid 3DCE881F0090
	 */
	public String getXmlString() {

        try {
            if (!_resultSet.next())
                return "";

            ResultSetMetaData rsmd  = _resultSet.getMetaData();
            int noOfColumns = rsmd.getColumnCount();

            String rsmdColumns[] = new String[noOfColumns];

            for (int index=1; index<=noOfColumns; index++)
                rsmdColumns[index-1] = rsmd.getColumnName( index );

            return getXml( rsmdColumns );
        }
        catch(SQLException sqle) {
            throw new XRuntime( _module, sqle.getMessage());
        }
        finally {
            // if the ResultSet was generated locally only, close it.
            try {
                if (_localRS && _resultSet != null) {
					if (_stmt != null) _stmt.close();
                    if (_resultSet != null) _resultSet.close();
                    _stmt = null;
                    _resultSet = null;
                }
            }
            catch(SQLException sqle) {
                // do nothing
            }
        }		
	}
	
	/**
	 * @param query
	 * @return String
	 * @roseuid 3DCE881F009A
	 */
	private String parseSql(String query) {
        query = query.toUpperCase();

        String columnList = columnList = query.substring( 7 , query.indexOf( _FROM ) );

        if (columnList.indexOf( _AS ) == -1 && columnList.indexOf( _AT ) == -1 )
            return query;

        StringBuffer sql       = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer( columnList, "," );

        while (tokens.hasMoreTokens()) {

            String tempString = tokens.nextToken().trim();
            int atIndex = tempString.indexOf( _AT );

            if (atIndex > 0 && tempString.indexOf( _AS ) > 0) {

                String columName = (tempString.substring( 0, tempString.indexOf( _AS ) )).trim();

                String attrName = tempString.substring( atIndex + 1, tempString.length() ).trim();

                _attrColumns.add( attrName );

                if (columName.equalsIgnoreCase( attrName ))
                    tempString = columName;
                else
                    tempString = tempString.substring( 0, atIndex ).concat( attrName );
            }

            sql.append( _COMMA ).append( tempString );
        }

        sql.replace( 0, 1, _SELECT );
        sql.append( query.substring( query.indexOf( _FROM ) ) );

        return sql.toString();		
	}
	
	/**
	 * @param rsmdColumns[]
	 * @return String
	 * @throws java.sql.SQLException
	 * @roseuid 3DCE881F00B8
	 */
	private String getXml(String rsmdColumns[]) throws SQLException {
        if (_tagCase) {
            _rowsetTag = _rowsetTag.toLowerCase();
            _rowTag    = _rowTag.toLowerCase();
            if (_rowIdAttrName != null)
                _rowIdAttrName = _rowIdAttrName.toLowerCase();
        }
        String newLine         = System.getProperty( "line.separator" );
        StringBuffer xmlString = new StringBuffer( "<?xml version = '1.0'?>" );
        int columnSize         = rsmdColumns.length;
        xmlString.append( newLine ).append( "<" ).append( _rowsetTag ).append( ">" );

        do {
            StringBuffer attrColumn = new StringBuffer();
            StringBuffer elemColumn = new StringBuffer();

            // print the row number, if required
            if (_rowIdAttrName != null) {
                attrColumn.append( _SPACE         )
                          .append( _rowIdAttrName ).append( _EQUAL    )
                          .append( _DQUOTE        ).append( _rowCount ).append( _DQUOTE );
            }

            // process all the columns
            for (int index=0;index<columnSize;index++) {
                String columnName  = rsmdColumns[index];
                String columnValue = (columnValue = _resultSet.getString( columnName )) != null ? columnValue : "";

                // if null values are not to be printed continue the next column
                if (columnValue.equals( "" ) && !_useNullAttrId)
                    continue;

                // convert the column names to lower case if set so.
                if (_tagCase)
                    columnName = columnName.toLowerCase();

                // if the column is specified as an attribute (@ symbol)
                // prepare it as an attribute
                if (_attrColumns.contains( columnName.toUpperCase() )) {
                    attrColumn.append( _SPACE  ).append( columnName  ).append( _EQUAL  )
                              .append( _DQUOTE ).append( columnValue ).append( _DQUOTE );
                    continue;
                }

                // otherwise this column is an element and so process it
                elemColumn.append( newLine ).append( _DTAB ).append( "<" ).append( columnName );
                if (columnValue.equals( "" )) {
                    // print the null indicator attribute
                    elemColumn.append( _SPACE   ).append( _NULL ).append( _EQUAL  )
                              .append( _DQUOTE  ).append( _YES  ).append( _DQUOTE ).append( "/>"     );
                    continue;
                }
                elemColumn.append( ">"  ).append( columnValue ).append( "</" ).append( columnName  ).append( ">" );
            }

            // all the columns in a row are now processed
            xmlString.append( newLine ).append( _TAB ).append( "<" ).append( _rowTag ).append( attrColumn );

            if (elemColumn.length() == 0) {
                xmlString.append( "/>" );
            }
            else {
                xmlString.append( ">"  ).append( elemColumn ).append( newLine ).append( _TAB )
                         .append( "</" ).append( _rowTag    ).append( ">" );
            }
            _rowCount++;
        } while (_resultSet.next());
        // xml closing tag
        xmlString.append( newLine ).append( "</"     ).append( _rowsetTag ).append( ">" );
        return xmlString.toString();		
	}
	
	/**
	 * @return long
	 * @roseuid 3DCE881F0180
	 */
	public long getNumRowsProcessed() {
        return _rowCount - 1;		
	}
	
	/**
	 * sets the name of the Row Id attribute
	 * @param rowIdAttrName
	 * @roseuid 3DCE881F018A
	 */
	public void setRowIdAttrName(String rowIdAttrName) {
        if (rowIdAttrName == null)
            _rowIdAttrName = null;
        else if (rowIdAttrName.trim().length() > 0)
            rowIdAttrName.toUpperCase();		
	}
	
	/**
	 * sets the name of the ROW tag (default is ROW)
	 * @param rowTag
	 * @roseuid 3DCE881F01A8
	 */
	public void setRowTag(String rowTag) {
        if (rowTag != null && rowTag.trim().length() > 0)
            _rowTag = rowTag.toUpperCase();		
	}
	
	/**
	 * sets the name of the ROWSET tag (default is ROWSET)
	 * @param rowsetTag
	 * @roseuid 3DCE881F01BC
	 */
	public void setRowsetTag(String rowsetTag) {
        if (rowsetTag != null && rowsetTag.trim().length() > 0)
            _rowsetTag = rowsetTag.toUpperCase();		
	}
	
	/**
	 * @roseuid 3DCE881F01DA
	 */
	public void useLowerCaseTagNames() {
        _tagCase = true;		
	}
	
	/**
	 * @roseuid 3DCE881F01E4
	 */
	public void useUpperCaseTagNames() {
        _tagCase = false;		
	}
	
	/**
	 * @param useNullAttrId
	 * @roseuid 3DCE881F01EE
	 */
	public void useNullAttributeIndicator(boolean useNullAttrId) {
        _useNullAttrId = useNullAttrId;		
	}
	
	/**
	 * @roseuid 3DCE881F020C
	 */
	public void close() {
        _rowIdAttrName      = null;
        _rowTag             = null;
        _rowsetTag          = null;
        _tagCase            = false;
        _useNullAttrId      = false;
        _attrColumns        = null;
        _rowCount           = 1;		
	}
}
