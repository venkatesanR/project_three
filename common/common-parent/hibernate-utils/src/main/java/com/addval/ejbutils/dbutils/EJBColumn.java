//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBColumn.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBColumn.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import org.w3c.dom.Node;

/**
 * A column object that represents a specific column in the EJBResultSet.
 * EJBRecord will contain a collection of EJBColumns
 */
public class EJBColumn implements Serializable {
	private static final transient String _module = "EJBColumn";
	private String _name = null;
	private String _strValue = null;
	private long _longValue = 0;
	private double _dblValue = 0.0;
	private boolean _isNull = false;
	private boolean _isAvailable = false;
	private EJBDateTime _dateTimeValue = null;
	private Object _object = null;

	/**
	 * @param name
	 * @param longValue
	 * @param dblValue
	 * @param isNull
	 * @param dateTimeValue
	 * @roseuid 3AF7562C0086
	 */
	public EJBColumn(String name, long longValue, double dblValue, boolean isNull, EJBDateTime dateTimeValue) {

        _name           = name;
        _longValue      = longValue;
        _dblValue       = dblValue;
        _isNull         = isNull;
        _dateTimeValue  = dateTimeValue;
	}

	/**
	 * @param metaData
	 * @roseuid 3AF82F4B038E
	 */
	public EJBColumn(ColumnMetaData metaData) {

        _name = metaData.getName();
	}

	/**
	 * @roseuid 3E41A0E50362
	 */
	public void reset() {

		_strValue 		= null;
		_longValue 		= 0;
		_dblValue 		= 0.0;
		_isNull 		= false;
		_isAvailable 	= false;
		_dateTimeValue 	= null;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AFB0A6E02EA
	 */
	public String getName() {

        return _name;
	}

	/**
	 * @param v
	 * @roseuid 3AFB0AE3039C
	 */
	public void setName(String v) {

        _name = v;
        _isAvailable = true;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AF8356E0206
	 */
	public String getStrValue() {

        return _strValue;
	}

	/**
	 * @param value
	 * @roseuid 3AF755A10069
	 */
	public void setStrValue(String value) {

        _strValue = value;
        _isAvailable = true;
        if (value != null)
        	_isNull = false;
	}

	/**
	 * @return int
	 * @roseuid 3AF8356E026B
	 */
	public int getIntValue() {

        return new Long( _longValue ).intValue();
	}

	/**
	 * @param value
	 * @roseuid 3AF755BB0188
	 */
	public void setIntValue(int value) {

        _longValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return double
	 * @roseuid 3AF8356E02C5
	 */
	public double getDblValue() {

        return _dblValue;
	}

	/**
	 * @param value
	 * @roseuid 3AF755BB02DD
	 */
	public void setDblValue(double value) {

        _dblValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return boolean
	 * @roseuid 3AF8356E031F
	 */
	public boolean isNull() {

        return _isNull;
	}

	/**
	 * @roseuid 3AF755BB0391
	 */
	public void setNull() {

        _isNull = true;
        _isAvailable = true;
	}

	/**
	 * @param value
	 * @roseuid 3AF756180359
	 */
	public void setDateTimeValue(EJBDateTime value) {

        _dateTimeValue = value;
        _isAvailable = true;
        if (value != null)
        	_isNull = false;
	}

	/**
	 * @return com.addval.ejbutils.dbutils.EJBDateTime
	 * @roseuid 3AFB0AA602B8
	 */
	public EJBDateTime getDateTimeValue() {

        return _dateTimeValue;
	}

	/**
	 * @return long
	 * @roseuid 3AFC7CF20288
	 */
	public long getLongValue() {

        return _longValue;
	}

	/**
	 * @param value
	 * @roseuid 3AFC7CF901C0
	 */
	public void setLongValue(long value) {

        _longValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return float
	 * @roseuid 3B03073201BF
	 */
	public float getFloatValue() {

        return new Float( _dblValue ).floatValue();
	}

	/**
	 * @param value
	 * @roseuid 3B0307320223
	 */
	public void setFloatValue(float value) {

        _dblValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return char
	 * @roseuid 3B030732027D
	 */
	public char getCharValue() {

        return _strValue == null ? ' ' : _strValue.charAt( 0 );
	}

	/**
	 * @param value
	 * @roseuid 3B03073202CD
	 */
	public void setCharValue(char value) {

        _strValue = String.valueOf( value );
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return boolean
	 * @roseuid 3B0307320328
	 */
	public boolean getBoolValue() {

        return _longValue == 0 ? false : true;
	}

	/**
	 * @param value
	 * @roseuid 3B0307320382
	 */
	public void setBoolValue(boolean value) {

        _longValue = value ? -1 : 0;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return int
	 * @roseuid 3B032D7D00CB
	 */
	public int getDowValue() {

        return new Long( _longValue ).intValue();
	}

	/**
	 * @param value
	 * @roseuid 3B032D7D012F
	 */
	public void setDowValue(int value) {

        _longValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return byte
	 * @roseuid 3B032EE701D0
	 */
	public byte getByteValue() {

        return new Byte( _strValue ).byteValue();
	}

	/**
	 * @param value
	 * @roseuid 3B032EE70234
	 */
	public void setByteValue(byte value) {

        _strValue = String.valueOf( value );
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return short
	 * @roseuid 3B03325B02E9
	 */
	public short getShortValue() {

        return new Long( _longValue ).shortValue();
	}

	/**
	 * @param value
	 * @roseuid 3B03325B034D
	 */
	public void setShortValue(short value) {

        _longValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return int
	 * @roseuid 3B1A75AB00AB
	 */
	public int getTimeValue() {

        return new Long( _longValue ).intValue();
	}

	/**
	 * @param value
	 * @roseuid 3B1A75AB00FB
	 */
	public void setTimeValue(int value) {

        _longValue = value;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @param keyValue
	 * @roseuid 3B39AE2E02D9
	 */
	public void setKeyValue(String keyValue) {

        _strValue = keyValue;
        _isAvailable = true;
        _isNull = false;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B39AE360014
	 */
	public String getKeyValue() {

        return _strValue;
	}

	/**
	 * @return boolean
	 * @roseuid 3B696E860224
	 */
	public boolean isAvailable() {

		return _isAvailable;
	}

	public void setObject(Object object) {

		_object = object;
	}

	public Object getObject() {

		return _object;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C8D44A900E5
	 */
	public synchronized String toXML() {

		StringBuffer xml 	= new StringBuffer();
		final String SPACE 	= " ";

		xml.append( "<column" + SPACE );
		xml.append( "name="   + "\"" + getName() + "\"" + SPACE );
		xml.append( "isNull=" + "\"" + isNull()  + "\"" + SPACE );
		xml.append( ">" ).append( System.getProperty( "line.separator" ) );

		xml.append( _strValue != null ? _strValue : ( _dateTimeValue != null ? _dateTimeValue.toString() : (_longValue != 0 ? String.valueOf( _longValue ): String.valueOf( _dblValue ) ) ) );

		xml.append( "</column>" );

		return xml.toString();
	}

	/**
	 * @param node
	 * @roseuid 3C8D44A90117
	 */
	public void fromXML(Node node) {

	}
}
