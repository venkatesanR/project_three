//Source file: C:\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DBColumnInfo.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import com.addval.utils.XRuntime;
import java.sql.ResultSet;

/**
 * Contains complete information about a column (field) in
 * a database.
 * JDBC connections can return a DatabaseMetaData object.
 * This object contains all the information about a database.
 * We could create a ResultSet from the DatabaseMetaData
 * object and iterate through it to obtain all the information
 * about the database.
 *
 * Example code:
 * <pre>
 * DatabaseMetaData dbmd = con.getMetaData();
 * ResultSet rs = dbms.getSchemas();
 * while (rs.next()) {
 *    String s = rs.getString(1);
 *    // ...
 * }
 * </pre>
 *
 * When a ResultSet is returned from the DatabaseMetaData
 *  it contains all the information about table and the fields. For example
 * TABLE_CAT,  TABLE_SCHEM, TABLE_NAME, etc.,
 * This information from the ResultSet is stored in this class.
 *
 * @author Sankar Dhanushkodi
 * @revision $Revision$
 */
public class DBColumnInfo {
	private static final int _TABLE_CAT = 1;
	private static final int _TABLE_SCHEM = 2;
	private static final int _TABLE_NAME = 3;
	private static final int _COLUMN_NAME = 4;
	private static final int _DATA_TYPE = 5;
	private static final int _TYPE_NAME = 6;
	private static final int _COLUMN_SIZE = 7;
	private static final int _BUFFER_LENGTH = 8;
	private static final int _DECIMAL_DIGITS = 9;
	private static final int _NUM_PREC_RADIX = 10;
	private static final int _NULLABLE = 11;
	private static final int _REMARKS = 12;
	private static final int _COLUMN_DEF = 13;
	private static final int _SQL_DATA_TYPE = 14;
	private static final int _SQL_DATETIME_SUB = 15;
	private static final int _CHAR_OCTET_LENGTH = 16;
	private static final int _ORDINAL_POSITION = 17;
	private static final int _IS_NULLABLE = 18;
	private static final String _ID_COL_SUFFIX = "_ID";
	private static final String _KEY_COL_SUFFIX = "_KEY";
	private static final String _SEQ_SUFFIX = "_SEQ";
	private String _tableCat;
	private String _tableSchema;
	private String _tableName;
	private String _columnName;
	private short _dataType;
	private String _typeName;
	private int _columnSize;
	private int _decimalDigits;
	private int _numPrecRadix;
	private int _nullable;
	private String _remarks;
	private String _columnDef;
	private int _sqlDataType;
	private int _sqlDateTimeSub;
	private int _charOctetLength;
	private int _ordinalPosition;
	private String _isNullable;

	/**
	 * This member can be used to override the column to be an ID column
	 */
	private boolean _isIDColumn;
	private Converter _converter = null;

	/**
	 * Constructor. Creates an instance of the object based on
	 * the values of the input Result Set
	 * @param rs ResultSet. (Obtained from a DatabaseMetaData)
	 * @exception
	 * @roseuid 376EAA8900DD
	 */
	public DBColumnInfo(ResultSet rs) {
      try {
         _tableCat          = rs.getString  (_TABLE_CAT          );
         _tableSchema       = rs.getString  (_TABLE_SCHEM        );
         _tableName         = rs.getString  (_TABLE_NAME         ).toUpperCase();
         _columnName        = rs.getString  (_COLUMN_NAME        ).toUpperCase();
         _dataType          = rs.getShort   (_DATA_TYPE          );
         _typeName          = rs.getString  (_TYPE_NAME          );
         _columnSize        = rs.getInt     (_COLUMN_SIZE        );
         _decimalDigits     = rs.getInt     (_DECIMAL_DIGITS     );
         _numPrecRadix      = rs.getInt     (_NUM_PREC_RADIX     );
         _nullable          = rs.getInt     (_NULLABLE           );
         _remarks           = rs.getString  (_REMARKS            );
         _columnDef         = rs.getString  (_COLUMN_DEF         );
         _sqlDataType       = rs.getInt     (_SQL_DATA_TYPE      );
         _sqlDateTimeSub    = rs.getInt     (_SQL_DATETIME_SUB   );
         _charOctetLength   = rs.getInt     (_CHAR_OCTET_LENGTH  );
         _ordinalPosition   = rs.getInt     (_ORDINAL_POSITION   );
         _isNullable        = rs.getString  (_IS_NULLABLE        );
         _converter         = makeConverter ( _dataType          );
         _isIDColumn		= false;
      }
      catch ( java.sql.SQLException e ) {
         throw new XRuntime( getClass().getName(), e.getMessage() );
      }
	}

	/**
	 * Accessor method to the table catalog.
	 * @param
	 * @return table catalog - String
	 * @exception
	 * @roseuid 378A23F00032
	 */
	public String getTableCat() {
      return _tableCat;
	}

	/**
	 * Accessor method to the table schema.
	 * @param
	 * @return table schema - String
	 * @exception
	 * @roseuid 378A23F003E0
	 */
	public String getTableSchema() {
      return _tableSchema;
	}

	/**
	 * Accessor method to the table name
	 * @param
	 * @return table name - String
	 * @exception
	 * @roseuid 378A23F103A5
	 */
	public String getTableName() {
      return _tableName;
	}

	/**
	 * Accessor method to the column name
	 * @param
	 * @return column name - String
	 * @exception
	 * @roseuid 378A23F2037F
	 */
	public String getColumnName() {
      return _columnName;
	}

	/**
	 * Accessor method to the column data type.
	 * @param
	 * @return data type - short
	 * @exception
	 * @roseuid 378A23F3034E
	 */
	public short getDataType() {
      return _dataType;
	}

	/**
	 * Accessor method to the table catalog.
	 * @param
	 * @return table catalog - String
	 * @exception
	 * @roseuid 378A23F40331
	 */
	public String getTypeName() {
      return _typeName;
	}

	/**
	 * @return int
	 * @roseuid 378A23F50333
	 */
	public int getColumnSize() {
      return _columnSize;
	}

	/**
	 * Accessor method to the decima digits
	 * @param
	 * @return decimal digits - int
	 * @exception
	 * @roseuid 378A23F60334
	 */
	public int getDecimalDigits() {
      return _decimalDigits;
	}

	/**
	 * Accessor method to the radix, which is typically either 10
	 * or 2.
	 * @param
	 * @return radix int
	 * @exception
	 * @roseuid 378A23F70336
	 */
	public int getNumPrecRadix() {
      return _numPrecRadix;
	}

	/**
	 * Accessor method to the indicate if the column can be
	 * NULL.
	 * @param
	 * @return columnNoNulls - NULL values might not be allowed
	 * @return columnNullable - NULL values are definitely
	 * allowed
	 * @return columnNullableUnknown - Whether NULL values
	 * are allowed is unkown.
	 * @exception
	 * @roseuid 378A23F8034B
	 */
	public int getNullable() {
      return _nullable;
	}

	/**
	 * Accessor method to the remarks (explanatory comment
	 * on the column; may be NULL)
	 * @param
	 * @return remarks String
	 * @exception
	 * @roseuid 378A23F9036B
	 */
	public String getRemarks() {
      return _remarks;
	}

	/**
	 * Accessor method to the column default value. May be
	 * NULL.
	 * @param
	 * @return column default value - String
	 * @exception
	 * @roseuid 378A23FA039E
	 */
	public String getColumnDef() {
      return _columnDef;
	}

	/**
	 * Accessor to SQL Data Type. Currently not supported.
	 * @param
	 * @return SQL data type - int
	 * @exception
	 * @roseuid 378A23FB03DC
	 */
	public int getSqlDataType() {
      return _sqlDataType;
	}

	/**
	 * Accessor to SQL Date Time. Currently unused
	 * @param
	 * @return SQL Date Time - int
	 * @exception
	 * @roseuid 378A23FD00B3
	 */
	public int getSqlDateTimeSub() {
      return _sqlDateTimeSub;
	}

	/**
	 * Accessor to Octet Length. Indicating the maximum number
	 * of bytes in the column (for char types only)
	 * @param
	 * @return Octet Length - int
	 * @exception
	 * @roseuid 378A23FE0245
	 */
	public int getCharOctetLength() {
      return _charOctetLength;
	}

	/**
	 * Accessor to Ordinal Position. Indicating the index of the
	 * column in the table. The first column is 1, the second
	 * column is 2, and so one
	 * @param
	 * @return Index of the column - int
	 * @exception
	 * @roseuid 378A2400007C
	 */
	public int getOrdinalPosition() {
      return _ordinalPosition;
	}

	/**
	 * Accessor to Is Nullable. "NO" indicating that the column
	 * definitely does not allow NULL values. "YES" indicating
	 * that the column might allow NULL values or empty
	 * ("") string indicating that the nullability is unknown.
	 * @param
	 * @return YES/NO - String
	 * @exception
	 * @roseuid 378A24010308
	 */
	public String getIsNullable() {
      return getIsNullable();
	}

	/**
	 * Accessor to the Converter object.
	 * @param
	 * @return Converter
	 * @exception
	 * @roseuid 378A245E0031
	 */
	public Converter getConverter() {
      return _converter;
	}

	/**
	 * Checks for columns that can have NULL values
	 * @param
	 * @return true/false boolean
	 * @exception
	 * @roseuid 378A343D019B
	 */
	public boolean isNullable() {
      return ( _nullable == java.sql.DatabaseMetaData.columnNullable );
	}

	/**
	 * Returns the class as a string. Useful for debugging purpose.
	 * @param
	 * @return Class as a string - String
	 * @exception
	 * @roseuid 378A41760121
	 */
	public String toString() {
      StringBuffer s = new StringBuffer( "----------\nDBColumnInfo\n" );
      s.append( "_tableCat   = " + _tableCat   + "\n" );
      s.append( "_tableSchema     = " + _tableSchema     + "\n" );
      s.append( "_tableName       = " + _tableName       + "\n" );
      s.append( "_columnName      = " + _columnName      + "\n" );
      s.append( "_dataType        = " + _dataType        + "\n" );
      s.append( "_typeName        = " + _typeName        + "\n" );
      s.append( "_columnSize      = " + _columnSize      + "\n" );
      s.append( "_decimalDigits   = " + _decimalDigits   + "\n" );
      s.append( "_numPrecRadix    = " + _numPrecRadix    + "\n" );
      s.append( "_nullable        = " + _nullable        + "\n" );
      s.append( "_remarks         = " + _remarks         + "\n" );
      s.append( "_columnDef       = " + _columnDef       + "\n" );
      s.append( "_sqlDataType     = " + _sqlDataType     + "\n" );
      s.append( "_sqlDateTimeSub  = " + _sqlDateTimeSub  + "\n" );
      s.append( "_charOctetLength = " + _charOctetLength + "\n" );
      s.append( "_ordinalPosition = " + _ordinalPosition + "\n" );
      s.append( "_isNullable      = " + _isNullable      + "\n" );
      s.append( "_converter       = " + _converter       + "\n" );
      s.append( "-----------------\n"                           );
      return s.toString();
	}

	/**
	 * @roseuid 39D8F13002F1
	 */
	public void setIDColumn() {

		_isIDColumn = true;
	}

	/**
	 * Returns true if this column is identity column in this table.
	 * @param:
	 * @return true/false
	 * @exception:
	 * @roseuid 378CC3A000C3
	 */
	public boolean isIDColumn() {

      // Valid ID colums for e.g. Users or User = user_id, user_key
      return   _isIDColumn ||
      			  ( (_columnName.compareToIgnoreCase( _tableName + _ID_COL_SUFFIX  ) == 0) ||
      			    (_columnName.compareToIgnoreCase( _tableName + _KEY_COL_SUFFIX ) == 0) ||
      			    (_columnName.compareToIgnoreCase( _tableName.substring( 0, _tableName.length() - 1) + _ID_COL_SUFFIX  ) == 0) ||
      			    (_columnName.compareToIgnoreCase( _tableName.substring( 0, _tableName.length() - 1) + _KEY_COL_SUFFIX ) == 0)
      			   )
               && ( !isNullable() )
               && ( _dataType == java.sql.Types.DECIMAL );
	}

	/**
	 * Returns sequence name for this table/column.
	 * @param:
	 * @return Sequence Name - String
	 * @exception:
	 * @roseuid 378CC3EC0131
	 */
	public String getSeqName() {
      return _tableName + _SEQ_SUFFIX;
	}

	/**
	 * Converts various SQL data types to simple type such
	 * as int, long, String.
	 * @param dataType int
	 * @return Converter
	 * @exception:
	 * @roseuid 378A2C1501C1
	 */
	private Converter makeConverter(int dataType) {

        // JDBC TYPE                            ORCL TYPE
        //java.sql.Types.LONGVARCHAR  = -1
        //java.sql.Types.VARCHAR      = 12      VARCHAR2
        //java.sql.Types.CHAR         = 1
        //java.sql.Types.VARBINARY    = -3
        //java.sql.Types.LONGVARBINARY= -4
        //java.sql.Types.BIT          = -7
        //java.sql.Types.BLOB         = 2004
        //java.sql.Types.ARRAY        = 2003
        //java.sql.Types.BINARY       = -2
        //java.sql.Types.CLOB         = 2005
        //java.sql.Types.JAVA_OBJECT  = 2000
        //java.sql.Types.STRUCT       = 2002
        //java.sql.Types.BIGINT       = -5
        //java.sql.Types.DECIMAL      = 3       NUMBER(38)/INTEGER /NUMBER(9)
        //java.sql.Types.DOUBLE       = 8
        //java.sql.Types.FLOAT        = 6
        //java.sql.Types.INTEGER      = 4
        //java.sql.Types.NUMERIC      = 2
        //java.sql.Types.SMALLINT     = 5
        //java.sql.Types.TINYINT      = -6
        //java.sql.Types.REAL         = 7
        //java.sql.Types.DATE         = 91
        //java.sql.Types.TIME         = 92
        //java.sql.Types.TIMESTAMP    = 93
        //java.sql.Types.DISTINCT     = 2001
        //java.sql.Types.NULL         = 0
        //java.sql.Types.OTHER        = 1111
        //java.sql.Types.REF          = 2006

      Converter conv = null;

      switch ( dataType ) {
         // string types
         case java.sql.Types.LONGVARCHAR  :
         case java.sql.Types.VARCHAR      :
         case java.sql.Types.CHAR    :
         case java.sql.Types.VARBINARY    :
         case java.sql.Types.LONGVARBINARY:
             conv = new StringConverter( _tableName, _columnName, isNullable() );
             break;

         // binary types
         case java.sql.Types.BIT         :
         case java.sql.Types.BLOB        :
         case java.sql.Types.ARRAY       :
         case java.sql.Types.BINARY      :
         case java.sql.Types.CLOB        :
         case java.sql.Types.JAVA_OBJECT :
         case java.sql.Types.STRUCT      :
             conv = new Converter( _tableName, _columnName, isNullable() );
             break;

         // numeric types
         case java.sql.Types.BIGINT      :
         case java.sql.Types.DECIMAL     :
         case java.sql.Types.DOUBLE      :
         case java.sql.Types.FLOAT       :
         case java.sql.Types.INTEGER     :
         case java.sql.Types.NUMERIC     :
         case java.sql.Types.SMALLINT    :
         case java.sql.Types.TINYINT     :
         case java.sql.Types.REAL        :
            conv = new IntConverter( _tableName, _columnName, isNullable() );
            break;

         // date types
         case java.sql.Types.DATE        :
         case java.sql.Types.TIME        :
         case java.sql.Types.TIMESTAMP   :
            conv = new DateConverter( _tableName, _columnName, isNullable() );
            break;

         // other types
         case java.sql.Types.DISTINCT    :
         case java.sql.Types.NULL        :
         case java.sql.Types.OTHER       :
         case java.sql.Types.REF         :
            conv = new Converter( _tableName, _columnName, isNullable() );
            break;

         default:
            throw new XRuntime( getClass().getName(), "Unknown Sql data type " + getTypeName() + " - " + getTableCat() + " - " + getTableSchema() + " - " + getTableName() + " - " + getDataType() );
      } // swithc
      return conv;
	}
}
