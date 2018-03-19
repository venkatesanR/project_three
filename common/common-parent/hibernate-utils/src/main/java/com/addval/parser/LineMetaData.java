//Source file: d:\\projects\\aerlingus\\source\\com\\addval\\parser\\LineMetaData.java

//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\parser\\LineMetaData.java

package com.addval.parser;

import com.addval.utils.XRuntime;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Properties;
import java.util.StringTokenizer;

public class LineMetaData extends LineType implements Constants 
{
    private String _tableName = null;
    private String _seqName = null;
    private String _dateFormat = null;
    private String _dbColumns[] = null;
    private String _dbColumnTypes[] = null;
    private String _sql = null;
    private static final String _module = "LineMetaData";
    private boolean _dateFormatError = false;
    private static final String _TABLENAME = "tableName";
    private static final String _SEQ_NAME = "seqName";
    private static final String _DATE_FORMAT = "dateFormat";
    private static final String _DB_COLUMNS = "dbColumns";
    private static final String _DB_COLUMN_TYPES = "dbColumnTypes";
    
    /**
     * Access method for the _tableName property.
     * 
     * @return   the current value of the _tableName property
     */
    public String getTableName() 
    {
        return _tableName;     
    }
    
    /**
     * Sets the value of the _tableName property.
     * 
     * @param aTableName the new value of the _tableName property
     */
    public void setTableName(String aTableName) 
    {
        _tableName = aTableName;     
    }
    
    /**
     * Access method for the _seqName property.
     * 
     * @return   the current value of the _seqName property
     */
    public String getSeqName() 
    {
        return _seqName;     
    }
    
    /**
     * Sets the value of the _seqName property.
     * 
     * @param aSeqName the new value of the _seqName property
     */
    public void setSeqName(String aSeqName) 
    {
        _seqName = aSeqName;     
    }
    
    /**
     * Access method for the _dateFormat property.
     * 
     * @return   the current value of the _dateFormat property
     */
    public String getDateFormat() 
    {
        return _dateFormat;     
    }
    
    /**
     * Sets the value of the _dateFormat property.
     * 
     * @param aDateFormat the new value of the _dateFormat property
     */
    public void setDateFormat(String aDateFormat) 
    {
        _dateFormat = aDateFormat;     
    }
    
    /**
     * Access method for the _dbColumns property.
     * 
     * @return   the current value of the _dbColumns property
     */
    public String[] getDbColumns() 
    {
        return _dbColumns;     
    }
    
    /**
     * Sets the value of the _dbColumns property.
     * 
     * @param aDbColumns the new value of the _dbColumns property
     */
    public void setDbColumns(String[] aDbColumns) 
    {
        _dbColumns = aDbColumns;     
    }
    
    /**
     * Access method for the _dbColumnTypes property.
     * 
     * @return   the current value of the _dbColumnTypes property
     */
    public String[] getDbColumnTypes() 
    {
        return _dbColumnTypes;     
    }
    
    /**
     * Sets the value of the _dbColumnTypes property.
     * 
     * @param aDbColumnTypes the new value of the _dbColumnTypes property
     */
    public void setDbColumnTypes(String[] aDbColumnTypes) 
    {
        _dbColumnTypes = aDbColumnTypes;     
    }
    
    /**
     * Access method for the _sql property.
     * 
     * @return   the current value of the _sql property
     */
    public String getSql() 
    {
        return _sql;     
    }
    
    /**
     * Sets the value of the _sql property.
     * 
     * @param aSql the new value of the _sql property
     */
    public void setSql(String aSql) 
    {
        _sql = aSql;     
    }
    
    /**
     * Access method for the _module property.
     * 
     * @return   the current value of the _module property
     */
    public static String getModule() 
    {
        return _module;     
    }
    
    /**
     * Determines if the _dateFormatError property is true.
     * 
     * @return   <code>true<code> if the _dateFormatError property is true
     */
    public boolean getDateFormatError() 
    {
        return _dateFormatError;     
    }
    
    /**
     * Sets the value of the _dateFormatError property.
     * 
     * @param aDateFormatError the new value of the _dateFormatError property
     */
    public void setDateFormatError(boolean aDateFormatError) 
    {
        _dateFormatError = aDateFormatError;     
    }
    
    /**
     * Access method for the _TABLENAME property.
     * 
     * @return   the current value of the _TABLENAME property
     */
    public static String getTABLENAME() 
    {
        return _TABLENAME;     
    }
    
    /**
     * Access method for the _SEQ_NAME property.
     * 
     * @return   the current value of the _SEQ_NAME property
     */
    public static String getSEQ_NAME() 
    {
        return _SEQ_NAME;     
    }
    
    /**
     * Access method for the _DATE_FORMAT property.
     * 
     * @return   the current value of the _DATE_FORMAT property
     */
    public static String getDATE_FORMAT() 
    {
        return _DATE_FORMAT;     
    }
    
    /**
     * Access method for the _DB_COLUMNS property.
     * 
     * @return   the current value of the _DB_COLUMNS property
     */
    public static String getDB_COLUMNS() 
    {
        return _DB_COLUMNS;     
    }
    
    /**
     * Access method for the _DB_COLUMN_TYPES property.
     * 
     * @return   the current value of the _DB_COLUMN_TYPES property
     */
    public static String getDB_COLUMN_TYPES() 
    {
        return _DB_COLUMN_TYPES;     
    }
    
    /**
     * @roseuid 3D6B0B080088
     */
    private void prepareSQL() 
    {
        if (_tableName == null)
            return;
        StringBuffer buffer = new StringBuffer( _INSERT );
        buffer.append( _SPACE ).append( _INTO ).append( _SPACE )
              .append( _tableName ).append( _SPACE )
              .append( _OPEN_BRACKET ).append( getDBColumnNames() ).append( _CLOSE_BRACKET )
              .append( _SPACE ).append( _VALUES ).append( _SPACE )
              .append( _OPEN_BRACKET ).append( getQuestionMarks() ).append( _CLOSE_BRACKET );
        _sql = buffer.toString();     
    }
    
    /**
     * @return String[]
     * @roseuid 3D6B0B070308
     */
    public String[] getDBColumns() 
    {
        return _dbColumns;     
    }
    
    /**
     * @return String[]
     * @roseuid 3D6F4B5603B6
     */
    public String[] getColumnTypes() 
    {
        return _dbColumnTypes;     
    }
    
    /**
     * @return int
     * @roseuid 3D6B0B070312
     */
    public int getColumnSize() 
    {
        return getDBColumns().length;     
    }
    
    /**
     * method is automatically called from the super class thro the
     * MessageFiniteStateMachine
     * @throws com.addval.parser.InvalidInputException
     * @roseuid 3E152A13036F
     */
    protected void initValues() throws InvalidInputException 
    {
        super.initValues();
        // if it is a comment line no further processing is necessary
        if (isCommentLine())
            return;
        _tableName     = get( _TABLENAME   );
        _seqName       = get( _SEQ_NAME    );
        _dateFormat    = get( _DATE_FORMAT );
// make this as a private variable
        String _dbColumnNames = get( _DB_COLUMNS, "" ).trim();
        _dbColumns     = Utils.splitLine( _dbColumnNames );
        _dbColumnTypes = Utils.splitLine( get( _DB_COLUMN_TYPES ));
        checkVariables();
        prepareSQL();     
    }
    
    /**
     * check mainly whether the dateFormat is specified. But don't throw error in case
     * not specified
     * Date format needs to be specified, only when DB output is desired. So wait till
     * knowing whether the output is to DB or to file
     * @roseuid 3E152A1303DD
     */
    private void checkVariables() 
    {
        if (getDateFormat() != null)
            return;
        // if one of the columnTypes is a DATE or DATETIME for TIMESTAMP field, then dateFormat is to be specified
        for (int i=0; i<_dbColumnTypes.length; i++) {
            if (!_dbColumnTypes[i].equalsIgnoreCase( _DATE ) && !_dbColumnTypes[i].equalsIgnoreCase( "DATETIME" ) && !_dbColumnTypes[i].equalsIgnoreCase( "TIMESTAMP" ))
                continue;
            _dateFormatError = true;
            break;
        }     
    }
    
    /**
     * @return String
     * @roseuid 3E152A140013
     */
    private String getDBColumnNames() 
    {
//        if (_dbColumnNames == null)
//            throw new RuntimeException( "When tableName is specified, dbColumnNames also is to be specified - " + getType() );
//        return _dbColumnNames;
        String columnNames = get( _DB_COLUMNS );
        if (columnNames == null)
            throw new RuntimeException( "When tableName is specified, dbColumnNames also is to be specified - " + getType() );
        return columnNames;     
    }
    
    /**
     * @return String
     * @roseuid 3E152A140027
     */
    private String getQuestionMarks() 
    {
        StringBuffer buffer = new StringBuffer();
        int size = getColumnSize();
        for (int i=0; i<size; i++)
            buffer.append( _COMMA ).append( _QUESTION_MARK );
        return buffer.substring( 1 );     
    }
    
    /**
     * @return String
     * @roseuid 3E152A140081
     */
    public String getSQL() 
    {
        return _sql;     
    }
    
    /**
     * @return boolean
     * @roseuid 3E152A140095
     */
    public boolean hasDateFormatError() 
    {
        return _dateFormatError;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3E152A1400C7
     */
    public String toString() 
    {
        StringBuffer buffer = new StringBuffer( super.toString() );
        return buffer.append( Utils.toString( this ) ).toString();     
    }
}
