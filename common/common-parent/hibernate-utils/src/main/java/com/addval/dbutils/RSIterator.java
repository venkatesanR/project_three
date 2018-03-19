//Source file: D:\Projects\AVOptimizer\source\com\addval\dbutils\RSIterator.java

/* AddVal Technology Inc. */

package com.addval.dbutils;

import java.sql.SQLException;
import java.util.Date;
import com.addval.utils.XRuntime;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * @author 
 * @version 
 */
public class RSIterator 
{
    private int _currPos;
    private int _pageSize;
    private int _rowCount;
    private int _currRow;
    private Statement _stmt;
    private RSIterAction _action;
    private ResultSet _resSet;
    
    /**
     * Constructor. Creates an object and initializes the position
     * @param stmt Statement
     * @param  currPos String. The current position within the 
     * entire result set.
     * @param  action RSIterAction. Record set motion (forwrd, 
     * backward, etc.)
     * @param  rowCount int. Total number of rows in the record 
     * set.
     * @param  pageSize int.  Number of rows of the record set to 
     * be returned.
     * @return
     * @exception
     * @roseuid 378E276402B3
     */
    public RSIterator(Statement stmt, String currPos, RSIterAction action, int rowCount, int pageSize) 
    {
        try {
            _stmt      = stmt;
            _resSet    = stmt.getResultSet();
            _action    = action;
            if (currPos == null)
                currPos = "1";
            _currPos   = Integer.valueOf( currPos ).intValue();
            if ( _currPos < 1 )
                _currPos = 1;
            _pageSize  = pageSize;
            _rowCount  = rowCount;
            int firstRow = getFirstRowIndex()-1;
            for ( _currRow = 0; (_currRow < firstRow) && _resSet.next(); _currRow++ );
        }
        catch ( SQLException e ) {
            throw new XRuntime( getClass().getName()+".RSIterator", e.getMessage() );
        }
    }
    
    /**
     * Increments the internal _currRow variable -- provide we are
     * not at the end of the record set.
     * @param
     * @return true/false boolean. Based on if it was able to 
     * increment
     * @exception
     * @roseuid 378F99B102DD
     */
    public boolean next() 
    {
      try {
         return (++_currRow <= getLastRowIndex()) && _resSet.next();
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass().getName(), e.getMessage() );
      }
    }
    
    /**
     * Return the field value for a given column number (index)
     * @param colIndex  int
     * @return String value of the field.
     * @exception
     * @roseuid 378F99E60030
     */
    public String getString(int colIndex) 
    {
      try {
         return _resSet.getString( colIndex );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the field value for a given column name (string)
     * @param colIName String
     * @return String value of the field.
     * @exception
     * @roseuid 378F9A1B00B8
     */
    public String getString(String colName) 
    {
      try {
         return _resSet.getString( colName );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the long field value for a given column numer (index)
     * @param colIndex  int
     * @return Long value of the field.
     * @exception
     * @roseuid 378F9A3F02A5
     */
    public long getLong(int colIndex) 
    {
      try {
         return _resSet.getLong( colIndex );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the field value for a given column name (String)
     * @param collName String
     * @return Long value of the field.
     * @exception
     * @roseuid 378F9A3F02C3
     */
    public long getLong(String colName) 
    {
      try {
         return _resSet.getLong( colName );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the double field value for a given column number 
     * (index)
     * @param colIndex  int
     * @return Double value of the field.
     * @exception
     * @roseuid 378F9A580070
     */
    public double getDouble(int colIndex) 
    {
      try {
         return _resSet.getDouble( colIndex );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the double field value for a given column name
     * (String)
     * @param colIName String
     * @return Double value of the field.
     * @exception
     * @roseuid 378F9A58008E
     */
    public double getDouble(String colName) 
    {
      try {
         return _resSet.getDouble( colName );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the date field value for a given column name
     * (String)
     * @param collName String
     * @return Date value of the field.
     * @exception
     * @roseuid 37951AAD01AA
     */
    public Date getDate(String colName) 
    {
      try {
         return _resSet.getDate( colName );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Return the date field value for a given column number 
     * (index)
     * @param colIndex  int
     * @return Date value of the field.
     * @exception
     * @roseuid 37951AC00053
     */
    public Date getDate(int colIndex) 
    {
      try {
         return _resSet.getDate( colIndex );
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Returns a RSPageDesc object that describes the current
     * page.
     * @param
     * @return A page description object. RSPageDesc
     * @exception
     * @see RSPageDesc
     * @roseuid 378FC4710153
     */
    public RSPageDesc getPageDesc() 
    {
      return new RSPageDesc( _rowCount, getFirstRowIndex(), getLastRowIndex(), _pageSize );
    }
    
    /**
     * Closes the result set and the statement in the object.
     * @param
     * @return
     * @exception
     * @roseuid 37C575560018
     */
    public void close() 
    {
      try {
         _resSet.close();
         _stmt.close();
      }
      catch ( SQLException e ) {
         throw new XRuntime( getClass( ).getName( ), e.getMessage( ) );
      }
    }
    
    /**
     * Accessor for the Statement member variable
     * @param
     * @return Statement member variable - Statement
     * @exception
     * @roseuid 3867F01C027A
     */
    public Statement getStatement() 
    {
      return _stmt;
    }
    
    /**
     * Accessor for the Result Set member variable
     * @param
     * @return Result Set member variable - Resultset
     * @exception
     * @roseuid 3867F029008E
     */
    public ResultSet getResultSet() 
    {
      return _resSet;
    }
    
    /**
     * Returns the NEXT  first row possible given the current  
     * position of the cursor within the record set.
     * @param
     * @return Index to the next first row - int
     * @exception
     * @roseuid 378FA54F0207
     */
    private int getFirstRowIndex() 
    {
      int index = 1;
      if ( _action.isFirst( ) ) {
         index = 1;
      }
      else if ( _action.isLast( ) ) {
         int fullPages = _rowCount / _pageSize;
         index = fullPages * _pageSize + 1;
      }
      else if ( _action.isNext( ) ) {
         index = _currPos + _pageSize;
      }
      else if ( _action.isCurr( ) || ( _action.toString( ) ).equals( _action._UNDEF_STR ) ) {
         index = _currPos;
      }
      else if ( _action.isPrev( ) ) {
         index = _currPos - _pageSize;
      }


      //check bounds
      while ( index > _rowCount ) index -= _pageSize;
      if ( index < 1 )
        index = 1;

      return index;
    }
    
    /**
     * Returns the last row possible given the current  
     * position of the cursor within the record set.
     * @param
     * @return Indexx to the last  row - int
     * @exception
     * @roseuid 378FA5640257
     */
    private int getLastRowIndex() 
    {
      return Math.min( getFirstRowIndex( ) + _pageSize - 1, _rowCount );
    }
}
