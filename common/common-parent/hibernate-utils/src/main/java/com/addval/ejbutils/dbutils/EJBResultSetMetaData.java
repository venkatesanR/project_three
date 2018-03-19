//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBResultSetMetaData.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBResultSetMetaData.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import java.sql.ResultSetMetaData;
import java.io.Serializable;
import java.util.Iterator;
import com.addval.ejbutils.utils.EJBXFeatureNotImplemented;
import com.addval.utils.AVConstants;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnMetaData;
import java.util.Vector;

/**
 * This is an serializable object that implements the java.sql.ResultSetMetaData
 * interface. EJBResultSet would return the EJBResultSetMetaData when the
 * getMetaData() method is called.
 */
public class EJBResultSetMetaData implements ResultSetMetaData, Serializable {
	private static final transient String _module = "EJBResultSetMetaData";
	private EditorMetaData _editorMetaData = null;

	/**
	 * @param editorMetaData
	 * @roseuid 3AF83F750095
	 */
	public EJBResultSetMetaData(EditorMetaData editorMetaData) {

        _editorMetaData = editorMetaData;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3AF83FD9037E
	 */
	public EJBCriteria getNewCriteria() {

        return new EJBCriteria( _editorMetaData.getName(), _editorMetaData.getType(), _editorMetaData.getColumnsMetaData() );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F7500E5
	 */
	public String getCatalogName(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F75010D
	 */
	public String getColumnClassName(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @return int
	 * @roseuid 3AF83F750149
	 */
	public int getColumnCount() {

        return _editorMetaData.getColumnCount();
	}

	/**
	 * @param column
	 * @return int
	 * @roseuid 3AF83F750185
	 */
	public int getColumnDisplaySize(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F7501B8
	 */
	public String getColumnLabel(int column) {

        // EditorMetaData validates the column
        return _editorMetaData.getColumnMetaData( column ).getCaption();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF83F7602F9
	 */
	public String getColumnLabel(String columnName) {

        return getColumnLabel( getColumnIndex( columnName ) );
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8A9980045
	 */
	public String getColumnName(String columnName) {

        return getColumnName( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F7501E0
	 */
	public String getColumnName(int column) {

        // EditorMetaData validates the column
        return _editorMetaData.getColumnMetaData( column ).getName();
	}

	/**
	 * @param column
	 * @return int
	 * @roseuid 3AF83F750258
	 */
	public int getColumnType(int column) {

        return _editorMetaData.getColumnMetaData( column ).getType();
	}

	/**
	 * @param columnName
	 * @return int
	 * @roseuid 3AF83F760368
	 */
	public int getColumnType(String columnName) {

        return getColumnType( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F750280
	 */
	public String getColumnTypeName(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return int
	 * @roseuid 3AF83F75030C
	 */
	public int getPrecision(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return int
	 * @roseuid 3AF83F75033E
	 */
	public int getScale(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F750398
	 */
	public String getTableName(int column) {

		String tblName = null;

		if (_editorMetaData.getProcedure() != null) {

			if (_editorMetaData.getProcedure().startsWith( "CALL " ))
				tblName = _editorMetaData.getSource();
			else
				tblName = _editorMetaData.getProcedure();
		}
		else if (_editorMetaData.getSourceName() != null){
			tblName = _editorMetaData.getSourceName();
		}
		else if (_editorMetaData.getSourceSql() != null){
			tblName = _editorMetaData.getSourceSql();
		}
		else {
			tblName = _editorMetaData.getSource();
		}

        return tblName;
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8A9AF0098
	 */
	public String getTableName(String columnName) {

        return getTableName( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF83F760000
	 */
	public String getSchemaName(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F76006E
	 */
	public boolean isAutoIncrement(int column) {

        // If the column is a key that has an AUTO_INCREMENT property then the
        // column should be named COLUMN_KEY or COLUMN_ID and it should have the
        // column_key in the Editor_Type_Columns table set to TRUE.
        // For AUTO_INCREMENT columns, the column will not be generated in the sql
        // Alternately if the data for the column was not provided, then do not use
        // the column in the insert sql

        // if the column is a KEY field AND
        // named as TABLENAME_KEY or TABLENAME_ID AND
        // made non-updatable then the isAutoIncrement() returns true

        ColumnMetaData  columnMetaData  = _editorMetaData.getColumnMetaData( column );
        String          tblName         = getTableName( column );
        String          idName          = tblName + AVConstants._ID_SUFFIX;
        String          keyName         = tblName + AVConstants._KEY_SUFFIX;

        return columnMetaData.isKey() && (columnMetaData.getName().toUpperCase().equals( idName.toUpperCase() ) || columnMetaData.getName().toUpperCase().endsWith( keyName.toUpperCase() )) && (!columnMetaData.isUpdatable()) ;
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F760097
	 */
	public boolean isCaseSensitive(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F7600C9
	 */
	public boolean isCurrency(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F760191
	 */
	public boolean isDefinitelyWritable(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return int
	 * @roseuid 3AF83F7601E1
	 */
	public int isNullable(int column) {

        return _editorMetaData.getColumnMetaData( column ).isNullable() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
	}

	/**
	 * @param columnName
	 * @return int
	 * @roseuid 3AF8A9DA01E4
	 */
	public int isNullable(String columnName) {

        return isNullable( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F76023B
	 */
	public boolean isReadOnly(int column) {

        return !_editorMetaData.getColumnMetaData( column ).isUpdatable();
	}

	/**
	 * @param columnName
	 * @return boolean
	 * @roseuid 3AF8A9DA023F
	 */
	public boolean isReadOnly(String columnName) {

        return isReadOnly( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F760263
	 */
	public boolean isSearchable(int column) {

        return _editorMetaData.getColumnMetaData( column ).isSearchable();
	}

	/**
	 * @param columnName
	 * @return boolean
	 * @roseuid 3AF8A9DA0271
	 */
	public boolean isSearchable(String columnName) {

        return isSearchable( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF8AA2601B2
	 */
	public boolean isAggregatable(int column) {

        return _editorMetaData.getColumnMetaData( column ).isAggregatable();
	}

	/**
	 * @param columnName
	 * @return boolean
	 * @roseuid 3AF8A9FE0146
	 */
	public boolean isAggregatable(String columnName) {

        return isAggregatable( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F760295
	 */
	public boolean isSigned(int column) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF83F7602C7
	 */
	public boolean isWritable(int column) {

        return !isReadOnly( column );
	}

	/**
	 * @param columnName
	 * @return int
	 * @roseuid 3AF83F760321
	 */
	public int getColumnIndex(String columnName) {

        // ResultSetMetaData & EditorMetaData indexes start from 1
        return _editorMetaData.getColumnIndex( columnName );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8A9540278
	 */
	public String getFormat(int column) {

        return _editorMetaData.getColumnMetaData( column ).getFormat();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8A9600012
	 */
	public String getFormat(String columnName) {

        return getFormat( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8AA4D0230
	 */
	public String getUnit(int column) {

        return _editorMetaData.getColumnMetaData( column ).getUnit();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8AA4D0258
	 */
	public String getUnit(String columnName) {

        return getUnit( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8AA4E0029
	 */
	public String getRegExp(int column) {

        return _editorMetaData.getColumnMetaData( column ).getRegexp();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8AA4E0047
	 */
	public String getRegExp(String columnName) {

        return getRegExp( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8AA4E0213
	 */
	public String getErrorMsg(int column) {

        return _editorMetaData.getColumnMetaData( column ).getErrorMsg();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8AA4E024F
	 */
	public String getErrorMsg(String columnName) {

        return getErrorMsg( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return double
	 * @roseuid 3AF8AAB6026D
	 */
	public double getMinval(int column) {

        return _editorMetaData.getColumnMetaData( column ).getMinval();
	}

	/**
	 * @param columnName
	 * @return double
	 * @roseuid 3AF8AAB60295
	 */
	public double getMinval(String columnName) {

        return getMinval( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return double
	 * @roseuid 3AF8AAB602DB
	 */
	public double getMaxval(int column) {

        return _editorMetaData.getColumnMetaData( column ).getMaxval();
	}

	/**
	 * @param columnName
	 * @return double
	 * @roseuid 3AF8AAB60317
	 */
	public double getMaxval(String columnName) {

        return getMaxval( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return boolean
	 * @roseuid 3AF8AB6E02E0
	 */
	public boolean isCombo(int column) {

        return _editorMetaData.getColumnMetaData( column ).isCombo();
	}

	/**
	 * @param columnName
	 * @return boolean
	 * @roseuid 3AF8AB6E02FE
	 */
	public boolean isCombo(String columnName) {

        return isCombo( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8AAB60353
	 */
	public String getComboTblName(int column) {

        return _editorMetaData.getColumnMetaData( column ).getComboTblName();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8AAB60371
	 */
	public String getComboTblName(String columnName) {

        return getComboTblName( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8AAB603A3
	 */
	public String getComboSelect(int column) {

        return _editorMetaData.getColumnMetaData( column ).getComboSelect();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8AAB603CC
	 */
	public String getComboSelect(String columnName) {

        return getComboSelect( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return java.lang.String
	 * @roseuid 3AF8AC0F01E7
	 */
	public String getComboOrderBy(int column) {

        return _editorMetaData.getColumnMetaData( column ).getComboOrderBy();
	}

	/**
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AF8AC0F0205
	 */
	public String getComboOrderBy(String columnName) {

        return getComboOrderBy( getColumnIndex( columnName ) );
	}

	/**
	 * @param column
	 * @return com.addval.metadata.ColumnMetaData
	 * @roseuid 3AF8ABB000C8
	 */
	public ColumnMetaData getColumnMetaData(int column) {

        return _editorMetaData.getColumnMetaData( column );
	}

	/**
	 * @param columnName
	 * @return com.addval.metadata.ColumnMetaData
	 * @roseuid 3AF8ABC300B1
	 */
	public ColumnMetaData getColumnMetaData(String columnName) {

        return getColumnMetaData( getColumnIndex( columnName ) );
	}

	/**
	 * @return com.addval.metadata.EditorMetaData
	 * @roseuid 3AF8ABC300CF
	 */
	public EditorMetaData getEditorMetaData() {

        return _editorMetaData;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3B02E542034D
	 */
	public Vector getColumnsMetaData() {

        return _editorMetaData.getColumnsMetaData();
	}

/* Needed for Java 1.6 migration */
	public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

}
