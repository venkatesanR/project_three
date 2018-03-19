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

package com.addval.dbutils;

import com.addval.metadata.ColumnInfo;

import java.io.Serializable;


/**
 * @author AddVal Technology Inc.
 */
// DaoParam now has two additional attributes, isCollection and maxInCount.
// these two variables are useful when setting a IN parameter in the where clause.
// When the Papm name (method) returns a List, the isCollection will be true
// and the maxInSize indicates the maximum expected size of the List
// Jeyaraj - 13-Feb-2006.
public class DAOParam implements Serializable
{
    private static final String _IN = "IN";
    private static final String _OUT = "OUT";
    private String _name = null;
    private String _type = null;
    private String _sequenceName = null;
    private int _parameters = 0;
    private int _index;
    private int _columnType;
    private String _mode = null;
    private String _isCollection = null;
    private int _maxInCount = 0;
    /**
     * Specifies max size for a column of types varchar
     * Helps isolate code from db precision/column sizes
     * Default -1 indicates the value is not specified - so unbounded
     * Example: if column allows say max 6 (specified as maxSize="6") chars and the value passed in is "simplest"
     * the saved data will be "simple"
     *
     * Ravi- 3/9/06
     */
    private int _maxSize = -1;
    /**
     * Specifies precision for a floating point number
     * Helps isolate code from db precision/column sizes
     * Default -1 indicates the value is not specified - so unbounded
     * Example: if the column allows a max value of 999.99, you would specify precision="2"
     *
     * Ravi- 3/9/06
     */
    private int _precision = -1;

    public DAOParam()
    {
        //nothing
    }

    public DAOParam(DAOParam param)
    {
        setName(param.getName());
        setType(param.getType());
        setIsCollection(String.valueOf(param.getIsCollection()));
        setMaxInCount(param.getMaxInCount());
        setSequenceName(param.getSequenceName());
        setParameters(param.getParameters());
        setIndex(param.getIndex());
        setType(param.getType());
        setMode(param.getMode());
    }

    public int getParameters()
    {
        return _parameters;
    }

    public void setParameters(int parameters)
    {
        _parameters = parameters;
    }

    /**
     * Access method for the _sequenceName property.
     *
     * @return   the current value of the _sequenceName property
     */
    public String getSequenceName()
    {
        return _sequenceName;
    }

    /**
     * Sets the value of the _sequenceName property.
     *
     * @param aSequenceName the new value of the _sequenceName property
     */
    public void setSequenceName(String aSequenceName)
    {
        _sequenceName = aSequenceName;
    }

    /**
     * Access method for the _index property.
     *
     * @return   the current value of the _index property
     */
    public int getIndex()
    {
        return _index;
    }

    /**
     * Sets the value of the _index property.
     *
     * @param aIndex the new value of the _index property
     */
    public void setIndex(int aIndex)
    {
        _index = aIndex;
    }

    /**
     * Access method for the _mode property.
     *
     * @return   the current value of the _mode property
     */
    public String getMode()
    {
        return _mode;
    }

    /**
     * Sets the value of the _mode property.
     *
     * @param aMode the new value of the _mode property
     */
    public void setMode(String aMode)
    {
        _mode = aMode;
    }

    /**
     * Access method for the _name property.
     * @return   the current value of the _name property
     * @roseuid 3EAD609C0292
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the value of the _name property.
     * @param aName the new value of the _name property
     * @roseuid 3EAD609C029F
     */
    public void setName(String aName)
    {
        _name = aName;
    }

    /**
     * Access method for the _type property.
     * @return   the current value of the _type property
     * @roseuid 3EAD609C02A1
     */
    public String getType()
    {
        return _type;
    }

    /**
     * Sets the value of the _type property.
     * @param aType the new value of the _type property
     * @roseuid 3EAD609C02AF
     */
    public void setType(String aType)
    {
        _type = aType;
        ColumnInfo columnInfo = new ColumnInfo(_name, aType, null);
        _columnType = columnInfo.getType();
    }

    /**
     * Sets the value of the _isCollection property.
     * @param aCollection the new value of the _isCollection property
     */
    public void setIsCollection(String aCollection)
    {
        _isCollection = aCollection;
    }

    /**
     * Access method for the _isCollection property
     * @return the current value of the _isCollection property
     */
    protected boolean getIsCollection()
    {
        return (_isCollection != null) && _isCollection.toUpperCase().equalsIgnoreCase("TRUE");
    }

    /**
     * Sets the value of the _maxInCount property.
     * @param aMaxInCount the new value of the _maxInCount property
     */
    public void setMaxInCount(int aMaxInCount)
    {
        _maxInCount = aMaxInCount;
    }

    /**
     * Access method for the _maxInCount property
     * @return the current value of the _maxInCount property
     */
    public int getMaxInCount()
    {
        return _maxInCount;
    }

    /**
     * @return java.lang.String
     * @roseuid 3EAD609C02B1
     */
    public String toString()
    {
        StringBuffer param = new StringBuffer("Param( ");
        param.append("Name=").append(getName()).append(", ");
        param.append("Type=").append(getType()).append(", ");
        param.append("IsCollection=").append(getIsCollection()).append(", ");
        param.append("MaxInCount=").append(getMaxInCount()).append(", ");
        param.append("MaxSize=").append(getMaxSize()).append(", ");
        param.append("precision=").append(getPrecision()).append(")");
        return param.toString();
    }

    /**
     * @return boolean
     * @roseuid 3F25DB760232
     */
    public boolean hasOutMode()
    {
        return (_mode != null) && _mode.toUpperCase().endsWith(_OUT);
    }

    /**
     * @return boolean
     * @roseuid 3F25DB8700BB
     */
    public boolean hasInMode()
    {
        return (_mode == null) || _mode.toUpperCase().startsWith(_IN);
    }

    /**
     * @return int
     * @roseuid 3EAD609C02B2
     */
    protected int getColumnType()
    {
        return _columnType;
    }

    public int getMaxSize()
    {
        return _maxSize;
    }

    public void setMaxSize(int maxSize)
    {
        this._maxSize = maxSize;
    }

    public boolean hasMaxSize()
    {
        return _maxSize > -1;
    }

    public int getPrecision()
    {
        return _precision;
    }

    public void setPrecision(int precision)
    {
        this._precision = precision;
    }

    public boolean hasPrecision()
    {
        return _precision > -1;
    }
}
