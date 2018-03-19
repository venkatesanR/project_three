//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\dbutils\\DBResultSet.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.sql.ResultSet;
import com.addval.utils.AVConstants;
import java.util.GregorianCalendar;
import java.sql.Statement;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.InputStream;
import java.sql.SQLWarning;
import java.sql.ResultSetMetaData;
import java.io.Reader;
import java.util.Map;
import java.sql.Ref;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Array;
import java.util.Calendar;

/**
 * @author AddVal Technology Inc.
 * This class is a wrapper around java.sql.ResultSet. It has been written
 * primarily for Date & Timestamp manipulations which are incorrectly obtained in
 * Oracle driver's implementation
 */
public class DBResultSet implements ResultSet {
	private ResultSet _rs = null;
	private Statement _stmt = null;

	/**
	 * @param rs
	 * @roseuid 3E5BEBE600BA
	 */
	public DBResultSet(ResultSet rs) {

		_rs = rs;
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97D0384
	 */
	public boolean next() throws SQLException {

		return _rs.next();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97D03AC
	 */
	public void close() throws SQLException {

		_stmt = null;
		_rs.close();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97D03D4
	 */
	public boolean wasNull() throws SQLException {

		return _rs.wasNull();
	}

	/**
	 * @param arg0
	 * @return java.lang.String
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E0014
	 */
	public String getString(int arg0) throws SQLException {

		return _rs.getString( arg0 );
	}

	/**
	 * @param arg0
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E0078
	 */
	public boolean getBoolean(int arg0) throws SQLException {

		return _rs.getBoolean( arg0 );
	}

	/**
	 * @param arg0
	 * @return byte
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E00DC
	 */
	public byte getByte(int arg0) throws SQLException {

		return _rs.getByte( arg0 );
	}

	/**
	 * @param arg0
	 * @return short
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E0140
	 */
	public short getShort(int arg0) throws SQLException {

		return _rs.getShort( arg0 );
	}

	/**
	 * @param arg0
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E01A4
	 */
	public int getInt(int arg0) throws SQLException {

		return _rs.getInt( arg0 );
	}

	/**
	 * @param arg0
	 * @return long
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E0209
	 */
	public long getLong(int arg0) throws SQLException {

		return _rs.getLong( arg0 );
	}

	/**
	 * @param arg0
	 * @return float
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E026D
	 */
	public float getFloat(int arg0) throws SQLException {

		return _rs.getFloat( arg0 );
	}

	/**
	 * @param arg0
	 * @return double
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E02D1
	 */
	public double getDouble(int arg0) throws SQLException {

		return _rs.getDouble( arg0 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E0335
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {

		return _rs.getBigDecimal( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @return byte[]
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97E03CB
	 */
	public byte[] getBytes(int arg0) throws SQLException {

		return _rs.getBytes( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Date
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F0047
	 */
	public Date getDate(int arg0) throws SQLException {

		return _rs.getDate( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Time
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F00AB
	 */
	public Time getTime(int arg0) throws SQLException {

		return _rs.getTime( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Timestamp
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F0110
	 */
	public Timestamp getTimestamp(int arg0) throws SQLException {

		return _rs.getTimestamp( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.InputStream
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F0188
	 */
	public InputStream getAsciiStream(int arg0) throws SQLException {

		return _rs.getAsciiStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.InputStream
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F01F6
	 */
	public InputStream getUnicodeStream(int arg0) throws SQLException {

		return _rs.getUnicodeStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.InputStream
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F0278
	 */
	public InputStream getBinaryStream(int arg0) throws SQLException {

		return _rs.getBinaryStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.lang.String
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F02E6
	 */
	public String getString(String arg0) throws SQLException {

		return _rs.getString( arg0 );
	}

	/**
	 * @param arg0
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F034A
	 */
	public boolean getBoolean(String arg0) throws SQLException {

		return _rs.getBoolean( arg0 );
	}

	/**
	 * @param arg0
	 * @return byte
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE97F03B9
	 */
	public byte getByte(String arg0) throws SQLException {

		return _rs.getByte( arg0 );
	}

	/**
	 * @param arg0
	 * @return short
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9800035
	 */
	public short getShort(String arg0) throws SQLException {

		return _rs.getShort( arg0 );
	}

	/**
	 * @param arg0
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9800099
	 */
	public int getInt(String arg0) throws SQLException {

		return _rs.getInt( arg0 );
	}

	/**
	 * @param arg0
	 * @return long
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9800107
	 */
	public long getLong(String arg0) throws SQLException {

		return _rs.getLong( arg0 );
	}

	/**
	 * @param arg0
	 * @return float
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE980016B
	 */
	public float getFloat(String arg0) throws SQLException {

		return _rs.getFloat( arg0 );
	}

	/**
	 * @param arg0
	 * @return double
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98001D9
	 */
	public double getDouble(String arg0) throws SQLException {

		return _rs.getDouble( arg0 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE980023D
	 */
	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {

		return _rs.getBigDecimal( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @return byte[]
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98002E8
	 */
	public byte[] getBytes(String arg0) throws SQLException {

		return _rs.getBytes( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Date
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE980034C
	 */
	public Date getDate(String arg0) throws SQLException {

		return _rs.getDate( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Time
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98003BA
	 */
	public Time getTime(String arg0) throws SQLException {

		return _rs.getTime( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Timestamp
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9810036
	 */
	public Timestamp getTimestamp(String arg0) throws SQLException {

		return _rs.getTimestamp( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.InputStream
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98100A4
	 */
	public InputStream getAsciiStream(String arg0) throws SQLException {

		return _rs.getAsciiStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.InputStream
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9810108
	 */
	public InputStream getUnicodeStream(String arg0) throws SQLException {

		return _rs.getUnicodeStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.InputStream
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9810177
	 */
	public InputStream getBinaryStream(String arg0) throws SQLException {

		return _rs.getBinaryStream( arg0 );
	}

	/**
	 * @return java.sql.SQLWarning
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98101DB
	 */
	public SQLWarning getWarnings() throws SQLException {

		return _rs.getWarnings();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE981020D
	 */
	public void clearWarnings() throws SQLException {

		_rs.clearWarnings();
	}

	/**
	 * @return java.lang.String
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9810235
	 */
	public String getCursorName() throws SQLException {

		return _rs.getCursorName();
	}

	/**
	 * @return java.sql.ResultSetMetaData
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE981025D
	 */
	public ResultSetMetaData getMetaData() throws SQLException {

		return _rs.getMetaData();
	}

	/**
	 * @param arg0
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9810285
	 */
	public Object getObject(int arg0) throws SQLException {

		return _rs.getObject( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98102F3
	 */
	public Object getObject(String arg0) throws SQLException {

		return _rs.getObject( arg0 );
	}

	/**
	 * @param arg0
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9810357
	 */
	public int findColumn(String arg0) throws SQLException {

		return _rs.findColumn( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.Reader
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98103C5
	 */
	public Reader getCharacterStream(int arg0) throws SQLException {

		return _rs.getCharacterStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.io.Reader
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9820042
	 */
	public Reader getCharacterStream(String arg0) throws SQLException {

		return _rs.getCharacterStream( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98200B0
	 */
	public BigDecimal getBigDecimal(int arg0) throws SQLException {

		return _rs.getBigDecimal( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.math.BigDecimal
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE982011E
	 */
	public BigDecimal getBigDecimal(String arg0) throws SQLException {

		return _rs.getBigDecimal( arg0 );
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9820182
	 */
	public boolean isBeforeFirst() throws SQLException {

		return _rs.isBeforeFirst();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98201AA
	 */
	public boolean isAfterLast() throws SQLException {

		return _rs.isAfterLast();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98201D2
	 */
	public boolean isFirst() throws SQLException {

		return _rs.isFirst();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98201FA
	 */
	public boolean isLast() throws SQLException {

		return _rs.isLast();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9820222
	 */
	public void beforeFirst() throws SQLException {

		_rs.beforeFirst();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9820254
	 */
	public void afterLast() throws SQLException {

		_rs.afterLast();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9820290
	 */
	public boolean first() throws SQLException {

		return _rs.first();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98202B9
	 */
	public boolean last() throws SQLException {

		return _rs.last();
	}

	/**
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98202F5
	 */
	public int getRow() throws SQLException {

		return _rs.getRow();
	}

	/**
	 * @param arg0
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE982031D
	 */
	public boolean absolute(int arg0) throws SQLException {

		return _rs.absolute( arg0 );
	}

	/**
	 * @param arg0
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9820381
	 */
	public boolean relative(int arg0) throws SQLException {

		return _rs.relative( arg0 );
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830007
	 */
	public boolean previous() throws SQLException {

		return _rs.previous();
	}

	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE983002F
	 */
	public void setFetchDirection(int arg0) throws SQLException {

		_rs.setFetchDirection( arg0 );
	}

	/**
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830093
	 */
	public int getFetchDirection() throws SQLException {

		return _rs.getFetchDirection();
	}

	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98300BB
	 */
	public void setFetchSize(int arg0) throws SQLException {

		_rs.setFetchSize( arg0 );
	}

	/**
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE983011F
	 */
	public int getFetchSize() throws SQLException {

		return _rs.getFetchSize();
	}

	/**
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830147
	 */
	public int getType() throws SQLException {

		return _rs.getType();
	}

	/**
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830170
	 */
	public int getConcurrency() throws SQLException {

		return _rs.getConcurrency();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830198
	 */
	public boolean rowUpdated() throws SQLException {

		return _rs.rowUpdated();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98301C0
	 */
	public boolean rowInserted() throws SQLException {

		return _rs.rowInserted();
	}

	/**
	 * @return boolean
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98301E8
	 */
	public boolean rowDeleted() throws SQLException {

		return _rs.rowDeleted();
	}

	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830210
	 */
	public void updateNull(int arg0) throws SQLException {

		_rs.updateNull( arg0 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830274
	 */
	public void updateBoolean(int arg0, boolean arg1) throws SQLException {

		_rs.updateBoolean( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9830314
	 */
	public void updateByte(int arg0, byte arg1) throws SQLException {

		_rs.updateByte( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98303AA
	 */
	public void updateShort(int arg0, short arg1) throws SQLException {

		_rs.updateShort( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9840063
	 */
	public void updateInt(int arg0, int arg1) throws SQLException {

		_rs.updateInt( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9840103
	 */
	public void updateLong(int arg0, long arg1) throws SQLException {

		_rs.updateLong( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98401A3
	 */
	public void updateFloat(int arg0, float arg1) throws SQLException {

		_rs.updateFloat( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9840239
	 */
	public void updateDouble(int arg0, double arg1) throws SQLException {

		_rs.updateDouble( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98402D9
	 */
	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {

		_rs.updateBigDecimal( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE984037A
	 */
	public void updateString(int arg0, String arg1) throws SQLException {

		_rs.updateString( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1[]
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9850032
	 */
	public void updateBytes(int arg0, byte arg1[]) throws SQLException {

		_rs.updateBytes( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98500D2
	 */
	public void updateDate(int arg0, Date arg1) throws SQLException {

		_rs.updateDate( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9850172
	 */
	public void updateTime(int arg0, Time arg1) throws SQLException {

		_rs.updateTime( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9850213
	 */
	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {

		_rs.updateTimestamp( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98502B3
	 */
	public void updateAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		_rs.updateAsciiStream( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98503B7
	 */
	public void updateBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {

		_rs.updateBinaryStream( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98600AC
	 */
	public void updateCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {

		_rs.updateCharacterStream( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9860188
	 */
	public void updateObject(int arg0, Object arg1, int arg2) throws SQLException {

			_rs.updateObject( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9860264
	 */
	public void updateObject(int arg0, Object arg1) throws SQLException {

		_rs.updateObject( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9860304
	 */
	public void updateNull(String arg0) throws SQLException {

		_rs.updateNull( arg0 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9860369
	 */
	public void updateBoolean(String arg0, boolean arg1) throws SQLException {

		_rs.updateBoolean( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9870021
	 */
	public void updateByte(String arg0, byte arg1) throws SQLException {

		_rs.updateByte( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98700C1
	 */
	public void updateShort(String arg0, short arg1) throws SQLException {

		_rs.updateShort( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9870161
	 */
	public void updateInt(String arg0, int arg1) throws SQLException {

		_rs.updateInt( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9870201
	 */
	public void updateLong(String arg0, long arg1) throws SQLException {

		_rs.updateLong( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98702A2
	 */
	public void updateFloat(String arg0, float arg1) throws SQLException {

		_rs.updateFloat( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9870342
	 */
	public void updateDouble(String arg0, double arg1) throws SQLException {

		_rs.updateDouble( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9880004
	 */
	public void updateBigDecimal(String arg0, BigDecimal arg1) throws SQLException {

		_rs.updateBigDecimal( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98800AE
	 */
	public void updateString(String arg0, String arg1) throws SQLException {

		_rs.updateString( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1[]
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9880159
	 */
	public void updateBytes(String arg0, byte arg1[]) throws SQLException {

		_rs.updateBytes( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98801F9
	 */
	public void updateDate(String arg0, Date arg1) throws SQLException {

		_rs.updateDate( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98802A3
	 */
	public void updateTime(String arg0, Time arg1) throws SQLException {

		_rs.updateTime( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9880343
	 */
	public void updateTimestamp(String arg0, Timestamp arg1) throws SQLException {

		_rs.updateTimestamp( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE989002E
	 */
	public void updateAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException {

		_rs.updateAsciiStream( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9890132
	 */
	public void updateBinaryStream(String arg0, InputStream arg1, int arg2) throws SQLException {

		_rs.updateBinaryStream( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9890218
	 */
	public void updateCharacterStream(String arg0, Reader arg1, int arg2) throws SQLException {

		_rs.updateCharacterStream( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE9890309
	 */
	public void updateObject(String arg0, Object arg1, int arg2) throws SQLException {

		_rs.updateObject( arg0, arg1, arg2 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98903E5
	 */
	public void updateObject(String arg0, Object arg1) throws SQLException {

		_rs.updateObject( arg0, arg1 );
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A00A7
	 */
	public void insertRow() throws SQLException {

		_rs.insertRow();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A00CF
	 */
	public void updateRow() throws SQLException {

		_rs.updateRow();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A00F7
	 */
	public void deleteRow() throws SQLException {

		_rs.deleteRow();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A011F
	 */
	public void refreshRow() throws SQLException {

		_rs.refreshRow();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A0148
	 */
	public void cancelRowUpdates() throws SQLException {

		_rs.cancelRowUpdates();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A0170
	 */
	public void moveToInsertRow() throws SQLException {

		_rs.moveToInsertRow();
	}

	/**
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A01A2
	 */
	public void moveToCurrentRow() throws SQLException {

		_rs.moveToCurrentRow();
	}

	/**
	 * @return java.sql.Statement
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A01C0
	 */
	public Statement getStatement() throws SQLException {

		return _stmt;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A01F2
	 */
	public Object getObject(int arg0, Map arg1) throws SQLException {

		return _rs.getObject( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Ref
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A029C
	 */
	public Ref getRef(int arg0) throws SQLException {

		return _rs.getRef( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Blob
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A030A
	 */
	public Blob getBlob(int arg0) throws SQLException {

		return _rs.getBlob( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Clob
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A036E
	 */
	public Clob getClob(int arg0) throws SQLException {

		return _rs.getClob( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Array
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98A03D2
	 */
	public Array getArray(int arg0) throws SQLException {

		return _rs.getArray( arg0 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.lang.Object
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B0059
	 */
	public Object getObject(String arg0, Map arg1) throws SQLException {

		return _rs.getObject( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Ref
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B0103
	 */
	public Ref getRef(String arg0) throws SQLException {

		return _rs.getRef( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Blob
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B0171
	 */
	public Blob getBlob(String arg0) throws SQLException {

		return _rs.getBlob( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Clob
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B01D5
	 */
	public Clob getClob(String arg0) throws SQLException {

		return _rs.getClob( arg0 );
	}

	/**
	 * @param arg0
	 * @return java.sql.Array
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B0243
	 */
	public Array getArray(String arg0) throws SQLException {

		return _rs.getArray( arg0 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Date
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B02A7
	 */
	public Date getDate(int arg0, Calendar arg1) throws SQLException {

		// Since there is a problem with the Oracle JDBC driver that it ignores
		// the Calendar argument, this method is overwritten
		return _rs.getDate( arg0 ) != null ? new Date( getTotalMillis( _rs.getDate( arg0 ), null, arg1 ) ) : null;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Date
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98B0352
	 */
	public Date getDate(String arg0, Calendar arg1) throws SQLException {

		// Since there is a problem with the Oracle JDBC driver that it ignores
		// the Calendar argument, this method is overwritten
		return _rs.getDate( arg0 ) != null ? new Date( getTotalMillis( _rs.getDate( arg0 ), null, arg1 ) ) : null;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Time
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98C0014
	 */
	public Time getTime(int arg0, Calendar arg1) throws SQLException {

		return _rs.getTime( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Time
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98C00D2
	 */
	public Time getTime(String arg0, Calendar arg1) throws SQLException {

		return _rs.getTime( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Timestamp
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98C0172
	 */
	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {

		// Since there is a problem with the Oracle JDBC driver that it ignores
		// the Calendar argument, this method is overwritten
		//return _rs.getDate( arg0 ) != null ? new Timestamp( getTotalMillis( _rs.getDate( arg0 ), _rs.getTime( arg0 ), arg1 ) ) : null;
		return _rs.getTimestamp( arg0 ) != null ? new Timestamp( getTotalMillis( new Date( _rs.getTimestamp( arg0 ).getTime() ) , null, arg1 ) ) : null;
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return java.sql.Timestamp
	 * @throws java.sql.SQLException
	 * @roseuid 3E5BE98C0213
	 */
	public Timestamp getTimestamp(String arg0, Calendar arg1) throws SQLException {

		// Since there is a problem with the Oracle JDBC driver that it ignores
		// the Calendar argument, this method is overwritten
		//return _rs.getTimestamp( arg0, arg1 );
		return _rs.getTimestamp( arg0 ) != null ? new Timestamp( getTotalMillis( new Date( _rs.getTimestamp( arg0 ).getTime() ), null, arg1 ) ) : null;
		//return _rs.getDate( arg0 ) != null ? new Timestamp( getTotalMillis( _rs.getDate( arg0 ), _rs.getTime( arg0 ), arg1 ) ) : null;
	}

	/**
	 * @param stmt
	 * @roseuid 3E5C0C71016D
	 */
	protected void setStatement(Statement stmt) {

		_stmt = stmt;
	}

	/**
	 * @param date
	 * @param time
	 * @param calendar
	 * @return long
	 * @roseuid 3E5FD57501CD
	 */
	protected static long getTotalMillis(Date date, Time time, Calendar calendar) {

		// Get Date Millis
		long dateMillis = date.getTime();

		// Get Time Millis
		long timeMillis = time == null ? 0 : time.getTime();

		//System.out.println( "Date Millis is " + dateMillis );
		//System.out.println( "Time Millis is " + timeMillis );

		// This is to offset the effect of local machine time
		long millis = dateMillis + AVConstants._LOCAL_TIMEZONE_OFFSET +
					  (time == null ? 0 : (timeMillis + AVConstants._LOCAL_TIMEZONE_OFFSET));

		//System.out.println( "Total Millis before is : " + millis );

		// This is to offset DST for the given date
		if (AVConstants._LOCAL_TIMEZONE_USEDST) {

			// Java sql.getDate() applies the offset based on DST on the input date varible
			// But for getTime() since this is always 1/1/1970, so we check if DST is applicable for 1/1/1970 in
			// that timezone

			int addOffset = AVConstants._LOCAL_TIMEZONE.inDaylightTime( date ) ? AVConstants._LOCAL_DSTSAVINGS : 0;
			millis += addOffset;

			//System.out.println( "Offset : " + addOffset );

			if (time != null) {
				addOffset = AVConstants._LOCAL_TIMEZONE.inDaylightTime( time ) ? AVConstants._LOCAL_DSTSAVINGS : 0;
				millis += addOffset;
				//System.out.println( "addOffset : " + addOffset );
			}

		}

		//System.out.println( "Total Millis after is : " + millis );

		// At this stage we have exactly the date data stored in the database
		// Now we need to apply the offsets for the required timezone

		if (!calendar.getTimeZone().hasSameRules( AVConstants._GMT_TIMEZONE )) {

			GregorianCalendar gmtCalendar = new GregorianCalendar( AVConstants._GMT_TIMEZONE );
			gmtCalendar.setTime( new Date( millis ) );


			calendar.set( Calendar.DATE			, gmtCalendar.get( Calendar.DATE 		) );
			calendar.set( Calendar.MONTH		, gmtCalendar.get( Calendar.MONTH 		) );
			calendar.set( Calendar.YEAR			, gmtCalendar.get( Calendar.YEAR 		) );
			calendar.set( Calendar.HOUR_OF_DAY	, gmtCalendar.get( Calendar.HOUR_OF_DAY ) );
			calendar.set( Calendar.MINUTE		, gmtCalendar.get( Calendar.MINUTE 		) );
			calendar.set( Calendar.SECOND		, gmtCalendar.get( Calendar.SECOND 		) );

			millis = calendar.getTime().getTime();

		}

		return millis;
	}

	public java.net.URL getURL(int columnIndex) throws java.sql.SQLException {
		return _rs.getURL(columnIndex);
	}

	public java.net.URL getURL(String columnName) throws java.sql.SQLException {
		return _rs.getURL(columnName);
	}

	public void updateArray(String columnName, java.sql.Array x) throws java.sql.SQLException {
		_rs.updateArray(columnName, x);
	}

	public void updateArray(int columnIndex, java.sql.Array x) throws java.sql.SQLException {
		_rs.updateArray(columnIndex, x);
	}

	public void updateBlob(int columnIndex, java.sql.Blob x) throws java.sql.SQLException {
		_rs.updateBlob(columnIndex, x);
	}

	public void updateBlob(String columnName, java.sql.Blob x) throws java.sql.SQLException {
		_rs.updateBlob(columnName, x);
	}

	public void updateClob(String columnName, java.sql.Clob x) throws java.sql.SQLException {
		_rs.updateClob(columnName, x);
	}

	public void updateClob(int columnIndex, java.sql.Clob x) throws java.sql.SQLException {
		_rs.updateClob(columnIndex, x);
	}

	public void updateRef(int columnIndex, java.sql.Ref x) throws java.sql.SQLException {
		_rs.updateRef(columnIndex, x);
	}

	public void updateRef(String columnName, java.sql.Ref x) throws java.sql.SQLException {
		_rs.updateRef(columnName, x);
	}


/* Needed for Java 1.6 migration */

	public void updateNClob(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateNClob(columnName, rdr);
	}

	public void updateNClob(int columnIndex, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateNClob(columnIndex, rdr);
	}

	public void updateClob(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateClob(columnName, rdr);
	}

	public void updateClob(int columnIndex, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateClob(columnIndex, rdr);
	}

	public void updateBlob(String columnName, java.io.InputStream rdr) throws java.sql.SQLException {
		_rs.updateBlob(columnName, rdr);
	}

	public void updateBlob(int columnIndex, java.io.InputStream rdr) throws java.sql.SQLException {
		_rs.updateBlob(columnIndex, rdr);
	}


	public void updateCharacterStream(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateCharacterStream(columnName, rdr);
	}

	public void updateCharacterStream(int columnIndex, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateCharacterStream(columnIndex, rdr);
	}

	public void updateBinaryStream(String columnName, java.io.InputStream rdr) throws java.sql.SQLException {
		_rs.updateBinaryStream(columnName, rdr);
	}

	public void updateBinaryStream(int columnIndex, java.io.InputStream rdr) throws java.sql.SQLException {
		_rs.updateBinaryStream(columnIndex, rdr);
	}


	public void updateAsciiStream(String columnName, java.io.InputStream rdr) throws java.sql.SQLException {
		_rs.updateAsciiStream(columnName, rdr);
	}

	public void updateAsciiStream(int columnIndex, java.io.InputStream rdr) throws java.sql.SQLException {
		_rs.updateAsciiStream(columnIndex, rdr);
	}

	public void updateNCharacterStream(String columnName, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateNCharacterStream(columnName, rdr);
	}

	public void updateNCharacterStream(int columnIndex, java.io.Reader rdr) throws java.sql.SQLException {
		_rs.updateNCharacterStream(columnIndex, rdr);
	}




	public void updateNClob(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateNClob(columnName, rdr, l);
	}

	public void updateNClob(int columnIndex, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateNClob(columnIndex, rdr, l);
	}

	public void updateClob(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateClob(columnName, rdr, l);
	}

	public void updateClob(int columnIndex, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateClob(columnIndex, rdr, l);
	}

	public void updateBlob(String columnName, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		_rs.updateBlob(columnName, rdr, l);
	}

	public void updateBlob(int columnIndex, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		_rs.updateBlob(columnIndex, rdr, l);
	}


	public void updateCharacterStream(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateCharacterStream(columnName, rdr, l);
	}

	public void updateCharacterStream(int columnIndex, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateCharacterStream(columnIndex, rdr, l);
	}

	public void updateBinaryStream(String columnName, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		_rs.updateBinaryStream(columnName, rdr, l);
	}

	public void updateBinaryStream(int columnIndex, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		_rs.updateBinaryStream(columnIndex, rdr, l);
	}


	public void updateAsciiStream(String columnName, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		_rs.updateAsciiStream(columnName, rdr, l);
	}

	public void updateAsciiStream(int columnIndex, java.io.InputStream rdr, long l) throws java.sql.SQLException {
		_rs.updateAsciiStream(columnIndex, rdr, l);
	}

	public void updateNCharacterStream(String columnName, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateNCharacterStream(columnName, rdr, l);
	}

	public void updateNCharacterStream(int columnIndex, java.io.Reader rdr, long l) throws java.sql.SQLException {
		_rs.updateNCharacterStream(columnIndex, rdr, l);
	}

	public java.io.Reader getNCharacterStream(int i) throws java.sql.SQLException {
		return _rs.getNCharacterStream(i);
	}

	public java.io.Reader getNCharacterStream(String s) throws java.sql.SQLException {
		return _rs.getNCharacterStream(s);
	}

	public String getNString(int i) throws java.sql.SQLException {
		return _rs.getNString(i);
	}

	public String getNString(String s) throws java.sql.SQLException {
		return _rs.getNString(s);
	}

	public void updateSQLXML(int columnIndex, java.sql.SQLXML x) throws java.sql.SQLException {
		_rs.updateSQLXML(columnIndex, x);
	}

	public void updateSQLXML(String columnName, java.sql.SQLXML x) throws java.sql.SQLException {
		_rs.updateSQLXML(columnName, x);
	}

	public java.sql.SQLXML getSQLXML(int i) throws java.sql.SQLException {
		return _rs.getSQLXML(i);
	}

	public java.sql.SQLXML getSQLXML(String s) throws java.sql.SQLException {
		return _rs.getSQLXML(s);
	}


	public java.sql.NClob getNClob(int i) throws java.sql.SQLException {
		return _rs.getNClob(i);
	}

	public java.sql.NClob getNClob(String s) throws java.sql.SQLException {
		return _rs.getNClob(s);
	}


	public void updateNClob(int columnIndex, java.sql.NClob rdr) throws java.sql.SQLException {
		_rs.updateNClob(columnIndex, rdr);
	}

	public void updateNClob(String columnName, java.sql.NClob rdr) throws java.sql.SQLException {
		_rs.updateNClob(columnName, rdr);
	}

	public void updateNString(String columnName, String str) throws java.sql.SQLException {
		_rs.updateNString(columnName, str);
	}

	public void updateNString(int columnIndex, String str) throws java.sql.SQLException {
		_rs.updateNString(columnIndex, str);
	}

	public boolean isClosed() throws java.sql.SQLException {
		return _rs.isClosed();
	}

	public int getHoldability() throws java.sql.SQLException {
		return _rs.getHoldability();
	}

	public void updateRowId(String columnName, java.sql.RowId id) throws java.sql.SQLException {
		_rs.updateRowId(columnName, id);
	}

	public void updateRowId(int columnIndex, java.sql.RowId id) throws java.sql.SQLException {
		_rs.updateRowId(columnIndex, id);
	}

	public java.sql.RowId getRowId(int columnIndex) throws java.sql.SQLException {
		return _rs.getRowId(columnIndex);
	}

	public java.sql.RowId getRowId(String column) throws java.sql.SQLException {
		return _rs.getRowId(column);
	}

	public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
		return _rs.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
		return _rs.unwrap(iface);
	}

}
