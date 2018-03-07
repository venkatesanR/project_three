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


import java.util.Comparator;

import org.apache.log4j.Logger;


public class DefaultNamedCacheInitializer implements NamedCacheInitializer
{
    private String _objectName;
    private String _objectType;
    private String _keyName;
    private boolean _storeAsList = false;
    private String _sortName;
    private Comparator<Object> _comparator;
    private Environment _env = null;
    private Boolean lazyCacheLoad = false;
    protected boolean _useExternalCache = false;
    protected boolean _allowGetAllFromExternalCache = false;
    private static final transient Logger _logger = LogMgr.getLogger( DefaultNamedCacheInitializer.class);

    public Environment getEnvironment()
    {
        return _env;
    }

    public void setEnvironment(Environment env)
    {
        _env = env;
    }

    public void setObjectName(String aName)
    {
        _objectName = aName;
    }

    public String getObjectName()
    {
        return _objectName;
    }

    public void setObjectType(String aType)
    {
        _objectType = aType;
    }

    public String getObjectType()
    {
        return _objectType;
    }

    public void setKeyName(String aName)
    {
        _keyName = aName;
    }

    public String getKeyName()
    {
        return _keyName;
    }

    public void setStoreAsList(boolean aStoreAsList)
    {
        _storeAsList = aStoreAsList;
    }

    public boolean getStoreAsList()
    {
        return _storeAsList;
    }

    public String getSortName()
    {
        return _sortName;
    }

    public void setSortName(String aName)
    {
        _sortName = aName;
    }

    public void setComparator(Comparator aComparator)
    {
        _comparator = aComparator;
    }

    public Comparator getComparator()
    {
        return _comparator;
    }

    protected Logger getLogger()
    {
    	return _logger;
    }

    /**
     * Method to bootstrap cache initialization through spring bean config with attribute "init-method".
     * Will auto initialize when the bean gets instantiated. No need for SpringAwareCacheLoader
     * This will allow for specifying cache dependcies (one cache depending on another) within
     * the spring config files. Dependency on another bean can be specified through attribute "depends-on"
     * Example usage
     * <bean id="cache2" class="com.addval.Cache2Utility" init-method="initialize" depends-on="cache1">
     * 
     * @throws com.addval.utils.CacheException
     */
    public void initialize() throws CacheException
    {
        //register self
        this.getEnvironment().getCacheMgr().registerCacheInitializer(this.getObjectName(), this);
        
        //do not bootstrap initialize the cache if lazy cache load is enabled
        if(this.isLazyCacheLoad())return;

        //refresh is false here as it is the first time
        Object cache = this.populateData(this.getEnvironment().getCacheMgr(), this.getObjectName(), false);

        this.getEnvironment().getCacheMgr().add(this.getObjectName(), cache, this.isUseExternalCache());
    }
    
    public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException
    {
    	// just to log the name of cache
    	getLogger().debug( "Populate Data for cache " + objectName + " called" );
        return null;
    }

    public Boolean isLazyCacheLoad()
    {
        return lazyCacheLoad;
    }

    public void setLazyCacheLoad(Boolean lazyCacheLoad)
    {
        this.lazyCacheLoad = lazyCacheLoad;
    }

	public Object populateData(CacheMgr cache, CacheRefreshDetail cacheRefreshDetail) throws CacheException 
	{
		// derived classes may use this method to incrementally update the cache
		return populateData(cache, cacheRefreshDetail.getObjectName(), cacheRefreshDetail.isIncremental() );
	}

    public boolean isUseExternalCache() {
        return _useExternalCache;
    }

    public boolean isAllowGetAllFromExternalCache() {
        return _allowGetAllFromExternalCache;
    }
}
