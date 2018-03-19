/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */
package com.addval.jmsutils;

import com.addval.utils.GenUtl;


public class MessageProperty
{
    public static final String _STRING = "STRING";
    public static final String _LONG = "LONG";
    public static final String _FLOAT = "FLOAT";
    public static final String _DOUBLE = "DOUBLE";
    public static final String _INT = "INT";
    public static final String _BYTE = "BYTE";
    public static final String _OBJECT = "OBJECT";
    public static final String _SHORT = "SHORT";
    public static final String _BOOLEAN = "BOOLEAN";
    private String _name = null;
    private Object _value = null;
    private String _type = null;

    public MessageProperty(String name, Object value, String type)
    {
        _name = name;
        _value = value;
        _type = type;
    }

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
        this._name = name;
    }

    public Object getValue()
    {
        return _value;
    }

    public Object getObjectValue()
    {
        return _value;
    }

    public String getStringValue()
    {
        if(_value != null)
            return _value.toString();

        return null;
    }

    public long getLongValue()
    {
        if(_value == null)
            return 0;

        return Long.parseLong(_value.toString());
    }

    public int getIntValue()
    {
        if(_value == null)
            return 0;

        return Integer.parseInt(_value.toString());
    }

    public double getDoubleValue()
    {
        if(_value == null)
            return 0;

        return Double.parseDouble(_value.toString());
    }

    public float getFloatValue()
    {
        if(_value == null)
            return 0;

        return Float.parseFloat(_value.toString());
    }

    public boolean getBooleanValue()
    {
        if(_value == null)
            return false;

        return Boolean.getBoolean(_value.toString());
    }

    public byte getByteValue()
    {
        if(_value == null)
            return 0;

        return Byte.parseByte(_value.toString());
    }

    public short getShortValue()
    {
        if(_value == null)
            return 0;

        return Short.parseShort(_value.toString());
    }

    public void setValue(Object value)
    {
        _value = value;
    }

    public String getType()
    {
        return _type;
    }

    public void setType(String type)
    {
        this._type = type;
    }

    public String toString()
    {
    	StringBuffer buffer = new StringBuffer( "\tName\t" ).append( _name ).append( "\tType\t" ).append( _type ).append( "\tValue\t" ).append( _value );
    	return buffer.toString();
    }
}
