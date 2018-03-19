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

package com.addval.utils;

import com.addval.dbutils.DAOSQLStatement;

import com.addval.utils.CacheException;

import java.util.Map;


/**
 * This class extends DefaultNamedCacheInitializer so as to promote standardized configurability under Spring.
 *
 * This class provides two additional member attributes that can be injected via Spring:
 *
 * sqlName = the case-sensitive "name" of a DAOSQLStatement that is the SQL query to be performed for initializing a particular data cache.  Example:  "lookupContainerTypes"
 *
 * daoCacheName = the name of an existing, already-initialized cached object, which must be a Map (such as "CargoresDAOSQL" cached object)
 *                                that uses String key = the desired DAOSQLStatement's "name", and object = the DAOSQLStatement having the specified name.
 *
 * This class also provides a new, standardized getDAOSQLStatement(), so that:
 * (a) The application sub-class does not have to provide its own method to do this
 * (b) We can guarantee better, more complete error-checking for typical Spring configuration problems in this standardized method.
 *
 * Example usage within a server-side applicationContext.xml file:
 * ---------------------------------------------------------------
 *                <bean id="StationProfilesCacheInitializer" class="com.addval.cargores.StationProfilesCacheInitializer">
 *                        <property name="environment" ref="environment"/>
 *                        <property name="objectName" value="StationProfiles"/>
 *                        <property name="sqlName" value="LookupStationProfiles"/>
 *                        <property name="daoCacheName" value="CargoresDAOSQL"/>
 *                </bean>
 *
 * In the above example, StationProfilesCacheInitializer is assumed to extend DefaultNamedCacheInitializerUsingCachedDAOSQL.
 * ref="environment                                        is used to inject the standard Cargores Environment (which is no long SUBSYSTEM dependent)
 * value="StationProfiles"                         specifies the name of the data cache that will be initialized by StationProfilesCacheInitializer
 * value="LookupStationProfiles"         is the name of the DAOSQLStatement that is the SQL query used to initialize the "StationProfiles" cache
 * value="CargoresDAOSQL"                        is the name of the already-initialized DAOSQL cached Map, that contains the "LookupStationProfiles" DAOSQLStatement
 *
 */
public class NamedCacheInitializerUsingCachedDAOSQL extends DefaultNamedCacheInitializer
{
    private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(NamedCacheInitializerUsingCachedDAOSQL.class);
    protected String _daoCacheName = null;
    protected String _sqlName = null;

    public void setDaoCacheName(String daoCacheName)
    {
        _daoCacheName = daoCacheName;
    }

    public String getDaoCacheName()
    {
        return _daoCacheName;
    }

    public void setSqlName(String sqlName)
    {
        _sqlName = sqlName;
    }

    public String getSqlName()
    {
        return _sqlName;
    }

    /**
     * This is the method normally used when subclass uses only a single SQL statement, whose name is specified via setSqlName(String) method.
     */
    protected DAOSQLStatement getDAOSQLStatement() throws CacheException
    {
        return getDAOSQLStatement(getSqlName(), "sqlName");
    }

    /**
     * This method can be used if the subclass needs more than one SQL statement to perform its cache initialization.
     * For such a subclass, the subclass should override the setSqlName(String) method to throw a RuntimeException that notifies caller of which OTHER special attributes should be specified instead.
     * For an example, see com.addval.cargores.EvaluationThresholdCacheInitializer.
     */
    protected DAOSQLStatement getDAOSQLStatement(String sqlName, String sqlNameAttributeName) throws CacheException
    {
        if(StrUtl.isEmptyTrimmed(getDaoCacheName()))
            throw new CacheException("daoCacheName attribute has not been specified, is required.  Associated objectName=\"" + getObjectName() + "\".");

        if(StrUtl.isEmptyTrimmed(sqlName))
            throw new CacheException(sqlNameAttributeName + " attribute has not been specified, is required. Associated objectName=\"" + getObjectName() + "\".");

        if(getEnvironment() == null)
            throw new CacheException("environment attribute has not been specified, is required.  Associated objectName=\"" + getObjectName() + "\".");

        if(getEnvironment().getCacheMgr() == null)
            throw new CacheException("configuration/dependency problem, unable to get CacheMgr instance from specified environment; getEnvironment().getCacheMgr() returns null.  Associated objectName=\"" + getObjectName() + "\".");

        Object tempObj = getEnvironment().getCacheMgr().get(getDaoCacheName());
        if(tempObj == null)
            throw new CacheException("specified DAOSQL cache named \"" + getDaoCacheName() + "\" was not found.  Check that proper (case-sensitive) value has been specified for daoCacheName.  Check that \"" + getDaoCacheName() + "\" cache is being initialized correctly elsewhere.  Associated objectName=\"" + getObjectName() + "\".");

        Map daoSqlMap = null;
        if(tempObj instanceof Map)
            daoSqlMap = (Map) tempObj;
        else
            throw new CacheException("specified DAOSQL cache named \"" + getDaoCacheName() + "\" was found, but the cached object is not of proper type, expecting an instance of java.util.Map. Check that proper value has been specified for daoCacheName.  Associated objectName=\"" + getObjectName() + "\".");

        tempObj = daoSqlMap.get(sqlName);
        if(tempObj == null)
            throw new CacheException("specified DAOSQLStatement for attribute \"" + sqlNameAttributeName + "\" and value \"" + sqlName + "\" was not found within cache named \"" + getDaoCacheName() + "\".  Check the proper (case-sensitive) values have been specified for daoCacheName and \"" + sqlNameAttributeName + "\".  Check that DAOSQLStatement named \"" + sqlName + "\" exists within the *_dao_sql.xml file that is being used to initialize the \"" + getDaoCacheName() + "\" cache.  Associated objectName=\"" + getObjectName() + "\".");

        DAOSQLStatement daoSqlStatement = null;
        if(tempObj instanceof DAOSQLStatement)
            daoSqlStatement = (DAOSQLStatement) tempObj;
        else
            throw new CacheException("specified DAOSQLStatement named \"" + sqlName + "\" was found within cache named \"" + getDaoCacheName() + "\", but is not the expected type (com.addval.dbutils.DAOSQLStatement).  Associated objectName=\"" + getObjectName() + "\".");

        return daoSqlStatement;
    }
}
