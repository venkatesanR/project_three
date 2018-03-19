//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBResultSet.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBResultSet.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import java.sql.ResultSet;
import com.addval.ejbutils.utils.EJBXFeatureNotImplemented;
import com.addval.metadata.RecordStatus;
import java.sql.Array;
import java.sql.Date;
import java.io.InputStream;
import com.addval.utils.XRuntime;
import java.util.Iterator;
import com.addval.metadata.ColumnMetaData;
import org.w3c.dom.NamedNodeMap;
import java.sql.Statement;
import java.util.Vector;
import java.util.Hashtable;
import org.w3c.dom.Node;
import java.math.BigDecimal;
import java.sql.Blob;
import java.io.Reader;
import java.sql.Clob;
import java.util.Calendar;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.SQLWarning;
import java.sql.SQLException;

/**
 * This is an serializable object that implements the java.sql.ResultSet
 * interface. This object is passed between the EJBTableManagerBean and any of its
 * clients. For details about the documentation on each of the methods, please
 * refer to the documentation on java.sql.ResultSet interface.
 */
public final class EJBResultSet implements Serializable, ResultSet {
	private final String newLine = System.getProperty( "line.separator" );
	private static final transient String _module = "EJBResultSet";
	private static final transient int _defaultSize = 10;
	private transient int _currIndex = -1;
	//private Statement _stmt = null;
	private EJBResultSetMetaData _metaData = null;
	private Vector _records = null;
	private EJBCriteria _criteria = null;

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {

		in.defaultReadObject();
        _currIndex = -1;
    }

	/**
	 * Construct a new EJBResultSet using the specified EJBResultSetMetaData
	 * @param metaData
	 * @roseuid 3B656B1C009F
	 */
	public EJBResultSet(EJBResultSetMetaData metaData) {

        this();
        _metaData = metaData;
	}

	/**
	 * Construct a new EJBResultSet using the specified EJBResultSetMetaData and
	 * EJBCriteria
	 * @param metaData
	 * @param criteria
	 * @roseuid 3B09BE510193
	 */
	public EJBResultSet(EJBResultSetMetaData metaData, EJBCriteria criteria) {

        this( metaData );
        _criteria = criteria;
	}

	/**
	 * Construct a new EJBResultSet using the specified collection of EJBRecords.
	 * @param records
	 * @roseuid 3AEDD9550379
	 */
	public EJBResultSet(Vector records) {

		_currIndex = -1;
        _records = records;
	}

	/**
	 * Default constructor for EJBResultSet.
	 * @roseuid 3B634F900300
	 */
	public EJBResultSet() {

		_currIndex = -1;
        _records = new Vector( _defaultSize );
	}

	/**
	 * @param stmt
	 * @roseuid 3AF83E0A01DE
	 */
	protected void setStatement(Statement stmt) {

        //_stmt = stmt;
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param row
	 * @return boolean
	 * @roseuid 3AE9FF8901AB
	 */
	public boolean absolute(int row) {

        // The java.sql.ResultSet allows negative row Indexes
        if (isIndexValid( Math.abs( row ) - 1 ))
            _currIndex = (row > 0) ? row - 1 : getSize() + row;
        else
            throw new XRuntime( _module, "Invalid Row Index : " + String.valueOf( row ) );

        return true;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @roseuid 3AE9FF8901D3
	 */
	public void afterLast() {

        _currIndex = getSize();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @roseuid 3AE9FF8901F1
	 */
	public void beforeFirst() {

        _currIndex = -1;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @roseuid 3AE9FF89020F
	 */
	public void cancelRowUpdates() {

        EJBRecord record = null;
        int       size   = getSize();
        int       index  = 0;

        while (index < size) {

            record = getRecord( index );

            // If the record was created newly and the user is trying to cancel these inserts
            // those inserted recrods need not go to the server
            if (record.getStatus() == RecordStatus._RMS_INSERTED) {

                _records.remove( index );
                index--;
                size--;
            }
            else {

                record.setStatus( RecordStatus._RMS_UNCHANGED );
            }

            index++;
        }
	}

	/**
	 * Not Implemented
	 * @roseuid 3AE9FF890223
	 */
	public void clearWarnings() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @roseuid 3AE9FF890241
	 */
	public void close() {

        _records    = null;
        _currIndex  = -1;
		//this._stmt = null;
	}

	/**
	 * This method will be called if the user needs to create a row that is to be
	 * deleted from the database. Hence this method actually creates a row with the
	 * row status RMS_DELETED. The user will then need to set the values of the key
	 * columns  in this row.
	 *
	 * If a row already exists, then the user need to call setRowStatus on that row
	 * with the status being RMS_DELETED.
	 * @roseuid 3AE9FF89025F
	 */
	public void deleteRow() {

		Hashtable params = null;
        deleteRow( params );
	}

	/**
	 * This overloaded method is similar to the deleteRow(), but takes in the values
	 * of the columns to be set in the record.
	 * @param params
	 * @roseuid 3B6345FC023B
	 */
	public void deleteRow(Hashtable params) {

        if (getRecords() == null)
            createRecords();

        getRecords().add( createNewRecord( params ) );

        // Positions the cursor on the last created row
        last();

        // Set the status to RMS_DELETED
        setRowStatus( RecordStatus._RMS_DELETED );
	}

	/**
	 * @param node
	 * @roseuid 3C9677950348
	 */
	public void deleteRow(Node node) {

		deleteRow();
		fromXML( node );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return int
	 * @roseuid 3B03E39803DD
	 */
	public int findColumn(String columnName) {

        return _metaData.getColumnIndex( columnName );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF89027D
	 */
	public boolean first() {

        boolean valid = isIndexValid( 0 );

        _currIndex = valid ? 0 : _currIndex;

        return valid;
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.sql.Array
	 * @roseuid 3AE9FF8902B9
	 */
	public Array getArray(String columnName) {

        return getArray( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.sql.Array
	 * @roseuid 3AE9FF89029B
	 */
	public Array getArray(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.io.InputStream
	 * @roseuid 3AE9FF8902D7
	 */
	public InputStream getAsciiStream(String columnName) {

        return getAsciiStream( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.io.InputStream
	 * @roseuid 3AE9FF8C0368
	 */
	public InputStream getAsciiStream(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.math.BigDecimal
	 * @roseuid 3AE9FF8902EB
	 */
	public BigDecimal getBigDecimal(String columnName) {

        return getBigDecimal( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.math.BigDecimal
	 * @roseuid 3AE9FF8C03A4
	 */
	public BigDecimal getBigDecimal(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param column
	 * @param x
	 * @return java.math.BigDecimal
	 * @roseuid 3B02C7E1001D
	 */
	public BigDecimal getBigDecimal(int column, int x) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @param x
	 * @return java.math.BigDecimal
	 * @roseuid 3B0334AD0257
	 */
	public BigDecimal getBigDecimal(String columnName, int x) {

        return getBigDecimal( findColumn( columnName ), x );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.io.InputStream
	 * @roseuid 3AE9FF8C03D6
	 */
	public InputStream getBinaryStream(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.io.InputStream
	 * @roseuid 3AE9FF890309
	 */
	public InputStream getBinaryStream(String columnName) {

        return getBinaryStream( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.sql.Blob
	 * @roseuid 3AE9FF8D002A
	 */
	public Blob getBlob(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.sql.Blob
	 * @roseuid 3AE9FF890327
	 */
	public Blob getBlob(String columnName) {

        return getBlob( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return boolean
	 * @roseuid 3AE9FF8D0066
	 */
	public boolean getBoolean(int columnIndex) {

        return new EJBCharConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getBoolean();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return boolean
	 * @roseuid 3AE9FF89034F
	 */
	public boolean getBoolean(String columnName) {

        return getBoolean( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return byte
	 * @roseuid 3AE9FF89036D
	 */
	public byte getByte(String columnName) {

        return getByte( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return byte
	 * @roseuid 3AE9FF8D0098
	 */
	public byte getByte(int columnIndex) {

        return new EJBCharConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getByte();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return byte[]
	 * @roseuid 3AE9FF8D00D4
	 */
	public byte[] getBytes(int columnIndex) {

        return new EJBStringConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getBytes();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return byte[]
	 * @roseuid 3AE9FF8903BD
	 */
	public byte[] getBytes(String columnName) {

        return getBytes( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.io.Reader
	 * @roseuid 3AE9FF8903DB
	 */
	public Reader getCharacterStream(String columnName) {

        return getCharacterStream( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.io.Reader
	 * @roseuid 3AE9FF8D0106
	 */
	public Reader getCharacterStream(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.sql.Clob
	 * @roseuid 3AE9FF8A0011
	 */
	public Clob getClob(String columnName) {

        return getClob( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.sql.Clob
	 * @roseuid 3AE9FF8D0142
	 */
	public Clob getClob(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @return int
	 * @roseuid 3AE9FF8A003A
	 */
	public int getConcurrency() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @return java.lang.String
	 * @roseuid 3AE9FF8A0058
	 */
	public String getCursorName() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Returns the Day of week represented as "Monday", "Tuesday" ...
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AE9FF8A0076
	 */
	public String getDOW(String columnName) {

        return getDOW( findColumn( columnName ) );
	}

	/**
	 * Returns the Day of week represented as "Monday", "Tuesday" ...
	 * @param columnIndex
	 * @return java.lang.String
	 * @roseuid 3AE9FF8A0094
	 */
	public String getDOW(int columnIndex) {

        return new EJBDowConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getString();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return java.sql.Date
	 * @roseuid 3AE9FF8A00BC
	 */
	public Date getDate(String columnName) {

        return getDate( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return java.sql.Date
	 * @roseuid 3AE9FF8D017E
	 */
	public Date getDate(int columnIndex) {

        return new Date( new EJBDateConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getDate().getTime() );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @param cal
	 * @return java.sql.Date
	 * @roseuid 3AE9FF8A00DA
	 */
	public Date getDate(String columnName, Calendar cal) {

        return getDate( findColumn( columnName ), cal );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @param cal
	 * @return java.sql.Date
	 * @roseuid 3AE9FF8D01BA
	 */
	public Date getDate(int columnIndex, Calendar cal) {

        return new Date( new EJBDateConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getDate( cal ).getTime() );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return double
	 * @roseuid 3AE9FF8A00F8
	 */
	public double getDouble(String columnName) {

        return getDouble( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return double
	 * @roseuid 3AE9FF8D01EC
	 */
	public double getDouble(int columnIndex) {

        return new EJBDoubleConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getDouble();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return int
	 * @roseuid 3AE9FF8A0120
	 */
	public int getFetchDirection() {

        return FETCH_FORWARD;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return int
	 * @roseuid 3AE9FF8A013E
	 */
	public int getFetchSize() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return float
	 * @roseuid 3AE9FF8A0166
	 */
	public float getFloat(String columnName) {

        return getFloat( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return float
	 * @roseuid 3AE9FF8D0229
	 */
	public float getFloat(int columnIndex) {

        return new EJBFloatConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getFloat();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return int
	 * @roseuid 3AE9FF8A0184
	 */
	public int getInt(String columnName) {

        return getInt( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return int
	 * @roseuid 3AE9FF8D0265
	 */
	public int getInt(int columnIndex) {

        return new EJBIntConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getInt();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return long
	 * @roseuid 3AE9FF8A01AC
	 */
	public long getLong(String columnName) {

        return getLong( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return long
	 * @roseuid 3AE9FF8D02A1
	 */
	public long getLong(int columnIndex) {

        return new EJBLongConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getLong();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return java.sql.ResultSetMetaData
	 * @roseuid 3AE9FF8A01CA
	 */
	public ResultSetMetaData getMetaData() {

        return _metaData;
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.lang.Object
	 * @roseuid 3AE9FF8A01F2
	 */
	public Object getObject(String columnName) {

        return getObject( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.lang.Object
	 * @roseuid 3AE9FF8D02DD
	 */
	public Object getObject(int columnIndex) {
		return getRecord().getColumn( columnIndex ).getObject();
//        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @param map
	 * @return java.lang.Object
	 * @roseuid 3AE9FF8A0210
	 */
	public Object getObject(String columnName, Map map) {

        return getObject( findColumn( columnName ), map );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @param map
	 * @return java.lang.Object
	 * @roseuid 3AE9FF8D0319
	 */
	public Object getObject(int columnIndex, Map map)
	{
		return getRecord().getColumn( columnIndex ).getObject();
//        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param i
	 * @return java.sql.Ref
	 * @roseuid 3AE9FF8A0238
	 */
	public Ref getRef(int i) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.sql.Ref
	 * @roseuid 3AE9FF8A0260
	 */
	public Ref getRef(String columnName) {

        return getRef( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return int
	 * @roseuid 3AE9FF8A027E
	 */
	public int getRow() {

        // returns the current row index
        return _currIndex + 1;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return short
	 * @roseuid 3AE9FF8A02A6
	 */
	public short getShort(String columnName) {

        return getShort( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return short
	 * @roseuid 3AE9FF8D0355
	 */
	public short getShort(int columnIndex) {

        return new EJBShortConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getShort();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return java.sql.Statement
	 * @roseuid 3AE9FF8A02CE
	 */
	public Statement getStatement() {

        //return _stmt;
		return new EJBStatement(this);
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 3AE9FF8A02ED
	 */
	public String getString(String columnName) {

        return getString( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return java.lang.String
	 * @roseuid 3AE9FF8D0391
	 */
	public String getString(int columnIndex) {

        return EJBConverterFactory.getConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getString();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return java.sql.Time
	 * @roseuid 3AE9FF8A0315
	 */
	public Time getTime(String columnName) {

        return getTime( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return java.sql.Time
	 * @roseuid 3AE9FF8D03CD
	 */
	public Time getTime(int columnIndex) {

        return new EJBTimeConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getTime();
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @param cal
	 * @return java.sql.Time
	 * @roseuid 3AE9FF8A033D
	 */
	public Time getTime(String columnName, Calendar cal) {

        return getTime( findColumn( columnName ), cal );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @param cal
	 * @return java.sql.Time
	 * @roseuid 3AE9FF8E0021
	 */
	public Time getTime(int columnIndex, Calendar cal) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnIndex
	 * @return java.sql.Timestamp
	 * @roseuid 3AE9FF8E005D
	 */
	public Timestamp getTimestamp(int columnIndex) {

        return new EJBDateTimeConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getTimestamp();
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @param cal
	 * @return java.sql.Timestamp
	 * @roseuid 3AE9FF8A038D
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal) {

        return getTimestamp( findColumn( columnName ), cal );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @return java.sql.Timestamp
	 * @roseuid 3AE9FF8A0365
	 */
	public Timestamp getTimestamp(String columnName) {

        return getTimestamp ( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @param cal
	 * @return java.sql.Timestamp
	 * @roseuid 3AE9FF8E0099
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal) {

        return new EJBDateTimeConverter( getRecord().getColumn( columnIndex ), _metaData.getColumnMetaData( columnIndex ) ).getTimestamp( cal );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return int
	 * @roseuid 3AE9FF8A03B5
	 */
	public int getType() {

        return TYPE_SCROLL_INSENSITIVE;
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @return java.io.InputStream
	 * @roseuid 3AE9FF8A03DD
	 */
	public InputStream getUnicodeStream(String columnName) {

        return getUnicodeStream( findColumn( columnName ) );
	}

	/**
	 * Not Implemented
	 * @param columnIndex
	 * @return java.io.InputStream
	 * @roseuid 3B02DE4701A9
	 */
	public InputStream getUnicodeStream(int columnIndex) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @return java.sql.SQLWarning
	 * @roseuid 3AE9FF8B001D
	 */
	public SQLWarning getWarnings() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * This methods creates a new row, sets it status to RMS_INSERTED and positions
	 * the cursor on the newly inserted row.
	 * @roseuid 3AE9FF8B0045
	 */
	public void insertRow() {

		Hashtable params = null;
        insertRow( params );
	}

	/**
	 * This method is similar to the insert() method, but takes in the values to be
	 * inserted.
	 * @param params
	 * @roseuid 3B6348C70280
	 */
	public void insertRow(Hashtable params) {

        if (getRecords() == null)
            createRecords();

        // changed - Jeyaraj - 14 Aug 2001
        // The EJBRecords vector is added with the created record
        // in method createNewRecord(Hashtable params)

        // changed - Prasad - 14 Aug 2001
        // Changed back to original form. The records are added only
        // in this method, the createNewRecord is changed to not call
        // updateString. Instead setValue on the column directly.
        // This is required because updateRow and deleteRow also call
        // createNewRecord( params )

        getRecords().add( createNewRecord( params ) );
        //createNewRecord( params );

        // Positions the cursor on the last inserted row
        last();

        // Set the status to RMS_INSERTED
        setRowStatus( RecordStatus._RMS_INSERTED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param node
	 * @roseuid 3C96779502EE
	 */
	public void insertRow(Node node) {

		insertRow();
		fromXML( node );
        // Set the status to RMS_INSERTED, because fromXML calls updateString
        // which sets the Row Status to RMS_UPDATED, so we are setting it to
        // RMS_INSERTED here
        setRowStatus( RecordStatus._RMS_INSERTED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B006D
	 */
	public boolean isAfterLast() {

        return _currIndex >= getSize();
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B0095
	 */
	public boolean isBeforeFirst() {

        return _currIndex < 0;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B00BD
	 */
	public boolean isFirst() {

        return _currIndex == 0;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B00E5
	 */
	public boolean isLast() {

        return _currIndex == getSize() - 1;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B010D
	 */
	public boolean last() {

        int     last = getSize() - 1;
        boolean rv   = isIndexValid( last );

        _currIndex = rv ? last : _currIndex;

        return rv;
	}

	/**
	 * Not Implemented
	 * @roseuid 3AE9FF8B013F
	 */
	public void moveToCurrentRow() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @roseuid 3AE9FF8B0167
	 */
	public void moveToInsertRow() {

        last();

        if (!rowInserted())
            throw new XRuntime( _module, "Not a Newly Inserted Row" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B018F
	 */
	public boolean next() {

        boolean rv = isIndexValid( _currIndex + 1 );

        _currIndex = rv ? _currIndex + 1 : _currIndex;

        return rv;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B01C2
	 */
	public boolean previous() {

        boolean rv = isIndexValid( _currIndex - 1 );

        _currIndex = rv ? _currIndex - 1 : _currIndex;

        return rv;
	}

	/**
	 * Not Implemented
	 * @roseuid 3AE9FF8B01EA
	 */
	public void refreshRow() {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param rows
	 * @return boolean
	 * @roseuid 3AE9FF8B021C
	 */
	public boolean relative(int rows) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B0244
	 */
	public boolean rowDeleted() {

        return getRecord().getStatus() == RecordStatus._RMS_DELETED;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B026C
	 */
	public boolean rowInserted() {

        return getRecord().getStatus() == RecordStatus._RMS_INSERTED;
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8B029E
	 */
	public boolean rowUpdated() {

        return getRecord().getStatus() == RecordStatus._RMS_UPDATED;
	}

	/**
	 * Not Implemented
	 * @param direction
	 * @roseuid 3AE9FF8B02C6
	 */
	public void setFetchDirection(int direction) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param rows
	 * @roseuid 3AE9FF8B02F8
	 */
	public void setFetchSize(int rows) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Not Implemented
	 * @param columnName
	 * @param x
	 * @param length
	 * @roseuid 3AE9FF8B032A
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length) {

        updateAsciiStream( findColumn( columnName ), x, length );
	}

	/**
	 * @param column
	 * @param x
	 * @param length
	 * @roseuid 3AE9FF8E00D6
	 */
	public void updateAsciiStream(int column, InputStream x, int length) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8B0352
	 */
	public void updateBigDecimal(String columnName, BigDecimal x) {

        updateBigDecimal( findColumn( columnName ), x );
	}

	/**
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E0112
	 */
	public void updateBigDecimal(int column, BigDecimal x) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8B0384
	 */
	public void updateBinaryStream(String columnName, InputStream x) {

        updateBinaryStream( findColumn( columnName ), x );
	}

	/**
	 * @param column
	 * @param value
	 * @param x
	 * @roseuid 3B03E3F301D5
	 */
	public void updateBinaryStream(int column, InputStream value, int x) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * @param columnName
	 * @param value
	 * @param x
	 * @roseuid 3B03E3F30207
	 */
	public void updateBinaryStream(String columnName, InputStream value, int x) {

        updateBinaryStream( findColumn( columnName ), value, x );
	}

	/**
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E014E
	 */
	public void updateBinaryStream(int column, InputStream x) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8B03B6
	 */
	public void updateBoolean(String columnName, boolean x) {

        updateBoolean( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E0194
	 */
	public void updateBoolean(int column, boolean x) {

        new EJBCharConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setBoolean( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8B03DE
	 */
	public void updateByte(String columnName, byte x) {

        updateByte( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E01D0
	 */
	public void updateByte(int column, byte x) {

        new EJBCharConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setByte( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x[]
	 * @roseuid 3AE9FF8C0028
	 */
	public void updateBytes(String columnName, byte x[]) {

        updateBytes( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x[]
	 * @roseuid 3AE9FF8E020C
	 */
	public void updateBytes(int column, byte x[]) {

        new EJBStringConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setBytes( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * @param columnName
	 * @param x
	 * @param length
	 * @roseuid 3AE9FF8C005A
	 */
	public void updateCharacterStream(String columnName, Reader x, int length) {

        updateCharacterStream( findColumn( columnName ), x, length );
	}

	/**
	 * @param column
	 * @param x
	 * @param length
	 * @roseuid 3AE9FF8E0252
	 */
	public void updateCharacterStream(int column, Reader x, int length) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C008D
	 */
	public void updateDate(String columnName, Date x) {

        updateDate( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E028E
	 */
	public void updateDate(int column, Date x) {

        new EJBDateConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setDate( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C00BF
	 */
	public void updateDouble(String columnName, double x) {

        updateDouble( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E02CA
	 */
	public void updateDouble(int column, double x) {

        new EJBDoubleConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setDouble( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C00F1
	 */
	public void updateFloat(String columnName, float x) {

        updateFloat( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E0310
	 */
	public void updateFloat(int column, float x) {

        new EJBDoubleConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setDouble( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C0123
	 */
	public void updateInt(String columnName, int x) {

        updateInt( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E034C
	 */
	public void updateInt(int column, int x) {

        new EJBIntConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setInt( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C015F
	 */
	public void updateLong(String columnName, long x) {

        updateLong( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8E0393
	 */
	public void updateLong(int column, long x) {

        new EJBLongConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setLong( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @roseuid 3AE9FF8C0191
	 */
	public void updateNull(String columnName) {

        updateNull( findColumn( columnName ) );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @roseuid 3AE9FF8E03CF
	 */
	public void updateNull(int column) {

        EJBConverterFactory.getConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setValue( null );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * @param columnName
	 * @param x
	 * @param scale
	 * @roseuid 3AE9FF8C01C3
	 */
	public void updateObject(String columnName, Object x, int scale) {

        updateObject( findColumn( columnName ), x, scale );
	}

	/**
	 * @param column
	 * @param x
	 * @param scale
	 * @roseuid 3AE9FF8F002D
	 */
	public void updateObject(int column, Object x, int scale) {

        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * This methods creates a new row, sets it status to RMS_INSERTED and positions
	 * the cursor on the newly inserted row.
	 * @roseuid 3AE9FF8C01F5
	 */
	public void updateRow() {

		Hashtable params = null;
        updateRow( params );
	}

	/**
	 * Update the EJBResultSet with the values from the given Hashtable. The key of
	 * the Hashtable should be a valid column in the EJBResultSetMetaData and the
	 * values should be formatted String (as specified in the format property in
	 * ColumnMetaData) representation of the column value
	 * @param params
	 * @roseuid 3B634CD4005A
	 */
	public void updateRow(Hashtable params) {

        if (getRecords() == null)
            createRecords();

        getRecords().add( createNewRecord( params ) );

        // Positions the cursor on the last inserted row
        last();

        // Set the status to RMS_UPDATED
        setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Update the EJBResultSet with the values from the given org.w3c.dom.Node object
	 * that represents an XML document
	 * @param node
	 * @roseuid 3C9677950316
	 */
	public void updateRow(Node node) {

		updateRow();
		fromXML( node );
	}

	/**
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C0227
	 */
	public void updateObject(String columnName, Object x) {

        updateObject( findColumn( columnName ), x );
	}

	/**
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8F0073
	 */
	public void updateObject(int column, Object x) {
		getRecord().getColumn( column ).setObject( x );
		// Only if the status is not set, then set it.
		// This is because if the user called insertRow() [No parameters],
		// the status will be set and then you do not want to change it
		if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
			setRowStatus( RecordStatus._RMS_UPDATED );
//        throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C0263
	 */
	public void updateShort(String columnName, short x) {

        updateShort( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8F013B
	 */
	public void updateShort(int column, short x) {

        new EJBShortConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setShort( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C0295
	 */
	public void updateString(String columnName, String x) {

        updateString( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8F01C7
	 */
	public void updateString(int column, String x) {

        EJBConverterFactory.getConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setValue( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C02C7
	 */
	public void updateTime(String columnName, Time x) {

        updateTime( findColumn( columnName ), x );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8F020D
	 */
	public void updateTime(int column, Time x) {

        new EJBTimeConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setTime( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * @param columnName
	 * @param x
	 * @roseuid 3AE9FF8C0303
	 */
	public void updateTimestamp(String columnName, Timestamp x) {

        updateTimestamp( findColumn( columnName ), x );
	}

	/**
	 * @param column
	 * @param x
	 * @roseuid 3AE9FF8F0253
	 */
	public void updateTimestamp(int column, Timestamp x) {

        new EJBDateTimeConverter( getRecord().getColumn( column ), _metaData.getColumnMetaData( column ) ).setTimestamp( x );
        // Only if the status is not set, then set it.
        // This is because if the user called insertRow() [No parameters],
        // the status will be set and then you do not want to change it
        if (getRecord().getStatus() == RecordStatus._RMS_UNCHANGED)
        	setRowStatus( RecordStatus._RMS_UPDATED );
	}

	/**
	 * Refer to documentation on java.sql.ResultSet
	 * @return boolean
	 * @roseuid 3AE9FF8C0336
	 */
	public boolean wasNull() {

        return getRecord().getColumn().isNull();
	}

	/**
	 * @param index
	 * @return boolean
	 * @roseuid 3AEF2CF50378
	 */
	private boolean isIndexValid(int index) {

        return !(index < 0 || index >= getSize());
	}

	/**
	 * @return int
	 * @roseuid 3AF6BCEF02B2
	 */
	private int getSize() {

        return _records == null ? 0 : _records.size();
	}

	/**
	 * @roseuid 3B66CD640272
	 */
	private void createRecords() {

        _records = new Vector( _defaultSize );
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBRecord
	 * @roseuid 3AEDE14602FA
	 */
	private EJBRecord createNewRecord() {

        return new EJBRecord( createNewColumns() );
	}

	/**
	 * @param params
	 * @return com.addval.ejbutils.dbutils.EJBRecord
	 * @roseuid 3B6349CD010A
	 */
	private EJBRecord createNewRecord(Hashtable params) {

        if (_metaData == null)
            throw new XRuntime( _module, "The EJBResultSetMetaData for this resultset is not set" );

        EJBRecord    record     = createNewRecord();
        EJBConverter converter  = null;
        String       value      = null;
        int          size       = _metaData.getColumnCount();

        if (params != null) {

            for (int index = 1; index <= size; index++) {

                value   = (String)params.get( _metaData.getColumnName( index ) );
                if (value != null)
					EJBConverterFactory.getConverter( record.getColumn( index ), _metaData.getColumnMetaData( index ) ).setValue( value );
            }
        }

        return record;
	}

	/**
	 * @return java.util.Vector
	 * @roseuid 3AF823FB00AF
	 */
	private Vector createNewColumns() {

        Vector   columns  = new Vector( _metaData.getColumnCount() );
        Iterator iterator = _metaData.getColumnsMetaData().iterator();

        while (iterator.hasNext()) {

            columns.add( new EJBColumn( (ColumnMetaData)iterator.next() ) );
        }

        return columns;
	}

	/**
	 * @roseuid 3AEDE16401C7
	 */
	private void removeRecord() {

        _records.remove( _currIndex );
        _currIndex--;
	}

	/**
	 * @param status
	 * @roseuid 3B02E6730098
	 */
	private void setRowStatus(int status) {

        getRecord().setStatus( status );
	}

	/**
	 * Returns the EJBRecord at the given index.
	 * @param index
	 * @return com.addval.ejbutils.dbutils.EJBRecord
	 * @roseuid 3AEE1787003E
	 */
	public EJBRecord getRecord(int index) {

        return getRecords() == null ? null : (EJBRecord)getRecords().elementAt( index );
	}

	/**
	 * Returns the EJBRecord at the current index
	 * @return com.addval.ejbutils.dbutils.EJBRecord
	 * @roseuid 3B02BD9901E5
	 */
	public EJBRecord getRecord() {

        return getRecord( _currIndex );
	}

	/**
	 * Returns all the records in the EJBResultSet as a collection of EJBRecords
	 * @return java.util.Vector
	 * @roseuid 3AEF2CDF0019
	 */
	public Vector getRecords() {

        return _records;
	}

	/**
	 * Sets the EJBCriteria that can be used to produce the EJBResultSet
	 * @param criteria
	 * @roseuid 3B02ED750279
	 */
	public void setEJBCriteria(EJBCriteria criteria) {

        _criteria = criteria;
	}

	/**
	 * Gets the EJBCriteria object that produced the EJBResultSet
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3B1A6A59016C
	 */
	public EJBCriteria getEJBCriteria() {

        return _criteria;
	}

	/**
	 * Sets the EJBRecords for the EJBResultSet
	 * @param records
	 * @roseuid 3B656B3C022C
	 */
	public void setRecords(Vector records) {

       	_records = records;
	}

	/**
	 * Returns the EJBResultSetMetaData object for the EJBResultSet
	 * @return com.addval.ejbutils.dbutils.EJBResultSetMetaData
	 * @roseuid 3B687AF4002F
	 */
	public EJBResultSetMetaData getEJBResultSetMetaData() {

        return _metaData;
	}

	/**
	 * Overriden toString() method that returns a String representation fo the
	 * EJBResultSet
	 * @return java.lang.String
	 * @roseuid 3C966A3301F7
	 */
	public synchronized String toString() {

		StringBuffer str 			= new StringBuffer();
		Vector 		 customColumns 	= _metaData.getEditorMetaData().getCustomColumns();
		int 		 columnCount	= _metaData.getColumnCount() - (customColumns == null ? 0 : customColumns.size());
		final String SPACE			= " ";
		int			 rowIndex		= 0;

		str.append( "<RECORDSET>" ).append( newLine );

		// Reset the Record Set to the first position
		beforeFirst();
		try {
			while (next()) {

				str.append( "\tROW #" + rowIndex++ ).append( newLine );

				for (int index = 1; index <= columnCount; index++ ) {

					str.append( "\t\t" + getMetaData().getColumnName( index ) );
					str.append( "=\"" + getString( index ) + "\"" + SPACE );
					str.append( newLine );
				}
			}
		}
		catch (Exception e) {

			throw new XRuntime( _module, e.getMessage() );
		}
		str.append( "</RECORDSET>" );

		// Reset the Record Set back
		beforeFirst();

		return str.toString();
	}

	/**
	 * Utility function that returns an XML representation of the EJBResultSet object
	 * @return java.lang.String
	 * @throws java.sql.SQLException
	 * @roseuid 3C8D37D4016E
	 */
	public synchronized String toXML() throws SQLException {

		StringBuffer xml 			= new StringBuffer();
		Vector 		 customColumns 	= _metaData.getEditorMetaData().getCustomColumns();
 		int 		 columnCount	= _metaData.getColumnCount() - (customColumns == null ? 0 : customColumns.size());
 		final String SPACE			= " ";

		xml.append( "<recordset>" ).append( newLine );

		// Reset the Record Set to the first position
		beforeFirst();
		while (next()) {

			xml.append( "<row " );

			for (int index = 1; index <= columnCount; index++ ) {

				xml.append( getMetaData().getColumnName( index ) );
				xml.append( "=\"" + getString( index ) + "\"" + SPACE );
			}

			xml.append( "/>" ).append( newLine );
		}

		xml.append( "</recordset>" );

		// Reset the Record Set back
		beforeFirst();

		return xml.toString();
	}

	public synchronized String toXMLData() {
	    StringBuffer chartXML = new StringBuffer();

		try {
			chartXML.append( "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		    chartXML.append( newLine ).append( "<RECORDSET>" );
		    if( getSize() > 0 ){
			    beforeFirst();
			    Vector 		 customColumns 	= _metaData.getEditorMetaData().getCustomColumns();
			    int 		 columnCount	= _metaData.getEditorMetaData().getColumnCount() - (customColumns == null ? 0 : customColumns.size());

			    String columnName = null;
			    String columnValue = null;
			    StringBuffer newRow = new StringBuffer();
			    boolean isRollUpRow = false;
			    while( next()) {
			        newRow = new StringBuffer();
			        newRow.append( newLine ).append( "<ROW>" );
			        isRollUpRow = false;
			        for (int index = 1; index <= columnCount; index++ ) {
			            columnName =getMetaData().getColumnName( index );
			            columnValue = getString(index);
			            if( columnName.startsWith("G_") ){
			                if( Integer.parseInt( columnValue ) > 0){
			                    isRollUpRow = true;
			                    break;
			                }
			            }
			            else{
			                columnValue = (columnValue != null)? columnValue : "";
			                newRow.append( newLine ).append( "<" ).append( columnName ).append( ">").append( columnValue).append( "</" ).append( columnName ).append( ">") ;
			            }
			        }
			        newRow.append( newLine ).append( "</ROW>" );
			        if( isRollUpRow ){
			            continue;
			        }
			        chartXML.append( newRow );
			    }
			    chartXML.append( newLine ).append( "</RECORDSET>" );
				// Reset the Record Set back
				beforeFirst();
		    }
		    else {
		    	chartXML.append( "<ROW/>" ).append( "</RECORDSET>" );
		    }

		}
		catch(SQLException ex){
			beforeFirst();
			chartXML = new StringBuffer();
			ex.printStackTrace();
		}
		return chartXML.toString();
	}

	/**
	 * Utility function that populates the EJBResultSet given an XML document
	 * @param node
	 * @roseuid 3C8D37DD0117
	 */
	public void fromXML(Node node) {

		// The data from this node will be added to the lastly added node
		// This method will not move the cursor the last row or change the status
		// of the last row

		// If the node has no attributes then return or if it not
		// an element node
		if (node == null || node.getNodeType() != Node.ELEMENT_NODE)
			throw new XRuntime( _module, "Node is not an element node : " + (node == null ? "NULL" : String.valueOf( node.getNodeType() )) );

		//if (node.hasAttributes()) {

			NamedNodeMap map 		= node.getAttributes();
			int 		 size 		= map.getLength();
			Node 		 childNode  = null;
			String 		 name 		= null;
			String 		 value 		= null;

			for (int index = 0; index < size; index++) {

				childNode = map.item( index );
				//if (childNode.getType() != Node.ATTRIBUTE_NODE)
				//	throw new XRuntime( _module, "Element's child nodes are not attributes : " + node.getNodeType() );

				name  = childNode.getNodeName().toUpperCase();
				value = childNode.getNodeValue();
				value = (value != null && value.equalsIgnoreCase( "null" )) ? null : value;

				// Set the value of the column if the column exists in the metadata
				if (_metaData.getEditorMetaData().isColumnValid( name ))
					updateString( name, value );
			}
		//}
	}

	public java.net.URL getURL(int columnIndex) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.net.URL getURL(String columnName) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateArray(String columnName, java.sql.Array x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateArray(int columnIndex, java.sql.Array x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBlob(int columnIndex, java.sql.Blob x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBlob(String columnName, java.sql.Blob x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateClob(String columnName, java.sql.Clob x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateClob(int columnIndex, java.sql.Clob x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateRef(int columnIndex, java.sql.Ref x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateRef(String columnName, java.sql.Ref x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}


/* Needed for Java 1.6 migration */

	public void updateNClob(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNClob(int i, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateClob(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateClob(int i, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBlob(String columnName, java.io.InputStream x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBlob(int i, java.io.InputStream x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateCharacterStream(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateCharacterStream(int i, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateAsciiStream(String columnName, java.io.InputStream rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateAsciiStream(int i, java.io.InputStream rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNCharacterStream(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNCharacterStream(int i, java.io.Reader rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNClob(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNClob(int i, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateClob(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateClob(int i, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBlob(String columnName, java.io.InputStream x, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBlob(int i, java.io.InputStream x, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateCharacterStream(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateCharacterStream(int i, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNCharacterStream(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNCharacterStream(int i, java.io.Reader rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}


	public void updateAsciiStream(String columnName, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateAsciiStream(int i, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBinaryStream(String columnName, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateBinaryStream(int i, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.io.Reader getNCharacterStream(int i) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.io.Reader getNCharacterStream(String s) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public String getNString(int i) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public String getNString(String s) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.SQLXML getSQLXML(int i) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.SQLXML getSQLXML(String s) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.NClob getNClob(int i) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.NClob getNClob(String s) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}


	public void updateSQLXML(int columnIndex, java.sql.SQLXML x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateSQLXML(String columnName, java.sql.SQLXML x) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNClob(String columnName, java.sql.NClob rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNClob(int columnIndex, java.sql.NClob rdr) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNString(String columnName, String str) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateNString(int columnIndex, String str) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public boolean isClosed() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public int getHoldability() {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateRowId(String columnName, java.sql.RowId id) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public void updateRowId(int columnIndex, java.sql.RowId id) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.RowId getRowId(int columnIndex) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

	public java.sql.RowId getRowId(String column) throws java.sql.SQLException {
		throw new EJBXFeatureNotImplemented( _module, "Feature Not Implemented" );
	}

}
