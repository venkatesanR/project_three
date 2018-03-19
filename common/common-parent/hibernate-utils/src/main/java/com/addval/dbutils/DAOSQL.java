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

import java.io.Serializable;


/**
 * @author AddVal Technology Inc.
 */
public class DAOSQL implements Serializable
{
    private String _sql = null;
    private String _database = null;
    private boolean _allowAnnotation = true;

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
     * Access method for the _database property.
     *
     * @return   the current value of the _database property
     */
    public String getDatabase()
    {
        return _database;
    }

    /**
     * Sets the value of the _database property.
     *
     * @param aDatabase the new value of the _database property
     */
    public void setDatabase(String aDatabase)
    {
        _database = aDatabase;
    }

    /**
     * @return java.lang.String
     * @roseuid 3EA99E5501E4
     */
    public String toString()
    {
        StringBuffer sql = new StringBuffer();
        sql.append("DB server type : " + getDatabase() + System.getProperty("line.separator"));
        sql.append("SQL : " + getSql() + System.getProperty("line.separator"));
        return sql.toString();
    }

    public String getAllowAnnotation()
    {
        return Boolean.toString(_allowAnnotation);
    }

    public void setAllowAnnotation(String trueFalse)
    {
        _allowAnnotation = Boolean.getBoolean(trueFalse);
    }

    public void isAllowAnnotation(boolean trueFalse)
    {
        _allowAnnotation = trueFalse;
    }

    public boolean isAllowAnnotation()
    {
        return _allowAnnotation;
    }
}
