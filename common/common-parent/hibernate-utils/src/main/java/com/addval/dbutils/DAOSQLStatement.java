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

import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;

import java.io.Serializable;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class DAOSQLStatement implements Serializable
{
    private static final transient org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger("SQL");
    private String _name = null;
    private int _noOfParameters;
    private Hashtable _sqls = null;
    private Vector _selectParams = null;
    private Vector _criteriaParams = null;
    private Hashtable _indexedSelectParams = null;

    /**
     * Access method for the _name property.
     *
     * @return   the current value of the _name property
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the value of the _name property.
     *
     * @param aName the new value of the _name property
     */
    public void setName(String aName)
    {
        _name = aName;
    }

    /**
     * Access method for the _noOfParameters property.
     *
     * @return   the current value of the _noOfParameters property
     */
    public int getNoOfParameters()
    {
        return _noOfParameters;
    }

    /**
     * Sets the value of the _noOfParameters property.
     *
     * @param aNoOfParameters the new value of the _noOfParameters property
     */
    public void setNoOfParameters(int aNoOfParameters)
    {
        _noOfParameters = aNoOfParameters;
    }

    /**
     * @return java.lang.String
     * @roseuid 3EA0733401C8
     */
    public String toString()
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SQL Name : " + getName() + System.getProperty("line.separator"));
        sql.append("SQL #Parameters : " + getNoOfParameters() + System.getProperty("line.separator"));
        if(_sqls != null)
        {
            Iterator iterator = _sqls.keySet().iterator();
            while(iterator.hasNext())
                sql.append(_sqls.get(iterator.next()));
        }
        else
            sql.append("No SQL Specified for the above name" + System.getProperty("line.separator"));

        sql.append("Select Param : " + System.getProperty("line.separator"));
        if(_selectParams != null)
        {
            Iterator iterator = _selectParams.iterator();
            while(iterator.hasNext())
                sql.append(iterator.next().toString()).append(System.getProperty("line.separator"));
        }
        else
            sql.append("No Select params" + System.getProperty("line.separator"));

        sql.append("Criteria Param : " + System.getProperty("line.separator"));
        if(_criteriaParams != null)
        {
            Iterator iterator = _criteriaParams.iterator();
            while(iterator.hasNext())
                sql.append(iterator.next().toString());
        }
        else
            sql.append("No Criteria params" + System.getProperty("line.separator"));

        return sql.toString();
    }

    /**
     * Access method for the _selectParams property.
     * @return   the current value of the _selectParams property
     * @roseuid 3EAD6071029F
     */
    public Vector getSelectParams()
    {
        return _selectParams;
    }

    /**
     * Access method for the _criteriaParams property.
     * @return   the current value of the _criteriaParams property
     * @roseuid 3EAD607102DE
     */
    public Vector getCriteriaParams()
    {
        return _criteriaParams;
    }

    protected Map getDaoSqls()
    {
        return _sqls;
    }

    /**
     * @param sql
     * @roseuid 3EAD61C6002E
     */
    public void addDAOSQL(DAOSQL sql)
    {
        if(_sqls == null)
            _sqls = new Hashtable();

        _sqls.put(sql.getDatabase().toUpperCase(), sql);
    }

    /**
     * @param param
     * @roseuid 3EAD61C6005D
     */
    public void addCriteriaParam(DAOParam param)
    {
        if(_criteriaParams == null)
            _criteriaParams = new Vector();

        _criteriaParams.add(param);
        param.setIndex(_criteriaParams.size());
    }

    /**
     * @param param
     * @roseuid 3EAD61C6008C
     */
    public void addSelectParam(DAOParam param)
    {
        if(_selectParams == null)
            _selectParams = new Vector();

        // This will set the index appropriately for the all java.sql.ResultSet and java.sql.Statement objects
        _selectParams.add(param);
        param.setIndex(_selectParams.size());
        if(_indexedSelectParams == null)
            _indexedSelectParams = new Hashtable();

        _indexedSelectParams.put(param.getName(), param);
    }

    /**
     *
     * @param sqlString
     * @param serverName
     */
    public void setSqlString(String sqlString, String serverName)
    {
        DAOSQL daosql = (DAOSQL) getDaoSqls().get(serverName);
        daosql.setSql(sqlString);
        getDaoSqls().put(serverName, daosql);
    }

    /**
     * @param database
     * @return java.lang.String
     * @roseuid 3EAD61C600BB
     */
    public String getSQL(String database)
    {
        String sql = null;
        if(StrUtl.isEmptyTrimmed(database))
            _logger.error("DAOSQLStatement.getSQL(serverType='" + database + "'): probable configuration error, no DB serverType specified, will not be able to retrieve SQL, [statement='" + getName() + "'], details follow:\n" + this);
        else if((_sqls == null) || _sqls.isEmpty())
            _logger.error("DAOSQLStatement.getSQL(serverType='" + database + "'): no SQL specified for any database serverType [statement='" + getName() + "'], details follow:\n" + this);
        else
        {
            DAOSQL daoSQL = (DAOSQL) _sqls.get(database.toUpperCase());
            if(daoSQL == null) {            	     
            	String eqvSyntaxDatabaseName = getEquivalentSyntaxDatabaseName(database);
            	if (eqvSyntaxDatabaseName != null) {
            		daoSQL = (DAOSQL) _sqls.get(eqvSyntaxDatabaseName.toUpperCase());
            	}
            }
            
            if (daoSQL == null) {            	
                _logger.error("DAOSQLStatement.getSQL(serverType='" + database + "'): no SQL specified for this database serverType [statement='" + getName() + "'], details follow:\n" + this);
            } else {
                sql = daoSQL.getSql();
            }
        }

        return sql;
    }

    /**
     * @param name
     * @return com.addval.dbutils.DAOParam
     * @roseuid 3EB197D10157
     */
    public DAOParam getSelectParamIndex(String name)
    {
        return (_indexedSelectParams != null) ? (DAOParam) _indexedSelectParams.get(name) : null;
    }

    /**
    * return a DAOParam object from Select
    */
    public DAOParam getSelectParam(String name)
    {
        if(_selectParams != null)
        {
            for(int i = 0; i < _selectParams.size(); ++i)
            {
                DAOParam prm = (DAOParam) _selectParams.get(i);
                if(prm.getName().equals(name))
                    return prm;
            }
        }

        return null;
    }

    /**
    * return a DAOParam object from Criteria
    */
    public DAOParam getCriteriaParam(String name)
    {
        if(_criteriaParams != null)
        {
            for(int i = 0; i < _criteriaParams.size(); ++i)
            {
                DAOParam prm = (DAOParam) _criteriaParams.get(i);
                if(prm.getName().equals(name))
                    return prm;
            }
        }

        return null;
    }

    public Hashtable getSQLS()
    {
        return _sqls;
    }


    /*
     *  This method will return rules for databases that share similar syntax for the most part
     *  
     *  for example if postgres return oracle
     *  
     *  else
     *  
     *  return null
     *  
     */
	public String getEquivalentSyntaxDatabaseName(String dbname) {
		String eqvSyntaxDatabase = null;
		
		if (dbname.equalsIgnoreCase(AVConstants._POSTGRES)) {
			eqvSyntaxDatabase = AVConstants._ORACLE;
		}
		else if (dbname.equalsIgnoreCase(AVConstants._SQLITE)) {
			eqvSyntaxDatabase = AVConstants._ORACLE;
		}

		return eqvSyntaxDatabase;
	}


}
