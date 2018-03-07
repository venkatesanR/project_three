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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * CacheMgr can be used as a placeholder for arbitrary objects. The objects are
 * stored with a key for lookup. As a special case, CacheMgr can maintain a
 * hashtable as the object being stored.
 *
 */
public class CacheMgr implements Serializable
{
    private static final transient Logger _logger = LogMgr.getLogger(CacheMgr.class);
    private static HashMap<String, CacheMgr> _instances = new HashMap<String, CacheMgr>();
    private final Map<String, Object> _items = new HashMap<String, Object>();
    private String _name;
    private Map<String, CacheInitializer> _initializers = new HashMap<String, CacheInitializer>();
    // this is init as true and on any cache failing, will become false. Need to see whether this sproperty could be moved to the initialisers
    private boolean _cacheLoaded = true;
    private boolean _useExternalCache=false;
    //External caching utility
    private IExternalCache _externalCache;

    /**
     * Access method for the _name property.
     *
     * @return the current value of the _name property
     */
    public String getName()
    {
        return _name;
    }

    public Object add(String objectName, Object object)
    {
    	return add(objectName,object,false);
    }
    /**
     * @param objectName
     * @param object
     * @roseuid 3BEABE95029A
     */
    public Object add(String objectName, Object object, boolean useExternalCache)
    {
        if(useExternalCache && getExternalCache() != null && Map.class.isInstance(object)){
            Map map = (Map)object;
            for(Map.Entry entry : (java.util.Set<Map.Entry>)map.entrySet()){
                this.getExternalCache().put(objectName, entry.getKey(), entry.getValue());
            }
        }
        else{
            synchronized(_items) {
            	_items.put(objectName, object);
            }
        }
        return object;
    }
    
    /**
     * @param objectName
     * @param object
     * @roseuid 3BEABE95029A
     */
    public Object add(String objectName, Object object, boolean useExternalCache,IExternalCache externalCache)
    {
        _logger.info("Cache " + objectName + " initialised and added to cache manager");
        if(useExternalCache && getExternalCache() != null && Map.class.isInstance(object)){
            Map map = (Map)object;
            for(Map.Entry entry : (java.util.Set<Map.Entry>)map.entrySet()){
            	externalCache.put(objectName, entry.getKey(), entry.getValue());
            }
        }
        else{
            synchronized(_items) {
                    _items.put(objectName, object);
            }
        }
        return object;
    }
    /**
     * @param objectName
     * @roseuid 3BEABF8D0283
     */
    public Object remove(String objectName)
    {
    	synchronized(_items) {
    		return _items.remove(objectName);
    	}
    }

    /**
     * @param objectName
     * @param key
     * @return java.lang.Object
     * @roseuid 3BEABFAB0127
     */
    public Object get(String objectName, String key)
    {
        if(!_cacheLoaded)
            throw new RuntimeException("Cache was not initialized properly.");

       //if external cache is used then get the cached object from the external cache
        CacheInitializer ci = _initializers.get(objectName);
        if(ci != null && this.getExternalCache() != null && ci.isUseExternalCache()){
            return this.getExternalCache().get(objectName, key);
        }

        Map<?,?> map = (Map<?,?>) get(objectName);
        return (map == null) ? null : map.get(key);
    }

    /**
     * @param objectName
     * @return java.lang.Object
     * @roseuid 3BEAC00F0208
     */
    public Object get(String objectName)
    {
        if(!_cacheLoaded)
            throw new RuntimeException("Cache was not initialized properly.");

        // Improperly-initialized caches are a frequent configuration problem,
        // usually causing a NullPointerException.
        // It is not necessarily an error if a requested cache does not exist,
        // so we cannot throw an Exception.
        // But we should at least log a warning that includes the specified
        // objectName, to help identify the cause when it is a configuration
        // problem.

        //if external cache is used this method will construct a copy of the map and return
        //note that external cache is only for maps and this is an expensive operation
        CacheInitializer ci = _initializers.get(objectName);
        if(ci != null && this.getExternalCache() != null && ci.isUseExternalCache()){
            if(!ci.isAllowGetAllFromExternalCache()){
                throw new RuntimeException("Requested cached object with name '" + objectName + "' is an external cache and the cache initializer prohibits access to the entire map.");
            }
            _logger.warn("Requested cached object with name '" + objectName + "' which is externally cached and therefore possibly an expensive call.");
            return this.getExternalCache().get(objectName);
        }

        Object object = _items.get(objectName); 
        if(object != null){
        	return object;
        }
        // if not initialized already initialize now
        synchronized(_items){
            object = _items.get(objectName); //check again as it is possible that another thread has loaded this cache as this thread is waiting to get the lock
            if(object != null){
            	return object;
            }
            object = refresh(objectName, ci, false );
        }
        if(object == null){
        	_logger.warn("Requested cached object with name '" + objectName + "' has not been initialized.");
        }
        return object;
    }

    /**
     * @param objectName
     * @param key
     * @return boolean
     * @roseuid 3BEB284400DB
     */
    public boolean contains(String objectName, String key)
    {
        if(!_cacheLoaded)
            throw new RuntimeException("Cache was not initialized properly.");

       //if external cache is used then check the key is in the external cache
        CacheInitializer ci = _initializers.get(objectName);
        if(ci != null && this.getExternalCache() != null && ci.isUseExternalCache()){
            return this.getExternalCache().containsKey(objectName, key);
        }

        Map<?,?> map = (Map<?,?>) get(objectName);
        return (map == null) ? false : map.containsKey(key);
    }

    /**
     * @param objectName
     * @return boolean
     * @roseuid 3BEB28440257
     */
    public boolean contains(String objectName)
    {
        if(!_cacheLoaded)
            throw new RuntimeException("Cache was not initialized properly.");

        return _items.containsKey(objectName);
    }

    /**
     * @param objectName
     * @return int
     * @roseuid 3BEAC06400BF
     */
    public int size(String objectName)
    {
        if(!_cacheLoaded)
            throw new RuntimeException("Cache was not initialized properly.");

       //if external cache is used this method will compute the size for in memory + off memory
        //note that external cache is only for maps and this is an expensive operation
        CacheInitializer ci = _initializers.get(objectName);
        if(ci != null && this.getExternalCache() != null && ci.isUseExternalCache()){
            if(!ci.isAllowGetAllFromExternalCache()){
                throw new RuntimeException("Requested cached object (SIZE) with name '" + objectName + "' is an external cache and the cache initializer prohibits access to the entire map.");
            }
            _logger.warn("Requested cached object (SIZE) with name '" + objectName + "' which is externally cached and therefore possibly an expensive call.");
            return this.getExternalCache().size(objectName);
        }

        Map<?,?> map = (Map<?,?>) get(objectName);
        return (map == null) ? 0 : map.size();
    }

    /**
     * @param name
     * @return com.addval.utils.CacheMgr
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @roseuid 3BEBFE6F030B
     */
    public static synchronized CacheMgr getInstance(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        return getInstance(new CnfgFileMgr(name).getProperties(), name);
    }

    /**
     * @param props
     * @param name
     * @return com.addval.utils.CacheMgr
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @roseuid 3E69524E039A
     */
    public static synchronized CacheMgr getInstance(Properties props, String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        CacheMgr c = getRawInstance(name);
        c.init(props);
        return c;
    }

    /**
     * @param props
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @roseuid 3C72B07E009A
     */
    private void init(Properties props) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        StringTokenizer st2;
        String ciName;
        String ciClassName;
        String ciObjectNames;
        String objectName;
        CacheInitializer ci;
        String initializers = props.getProperty("cache.initializers", "");
        if(initializers.equals("")) {
            System.err.println("No cache.initializers found for Cache:" + _name);
            return;
        }
        // this entry decides whether the ALL the caches are loaded during initial load (false) or on first access (true)
        // useful for Dev env, reducing server startup time. overrides the individual cache level setting
        boolean isAllLazyLoad = false;
        try
        {
            isAllLazyLoad = props.getProperty("cache.initializers.allLazyLoad", "false").toLowerCase().equals("true");
        }
        catch(Exception e)
        {
            // nothing to do if the property is not specified
        }

        StringTokenizer st = new StringTokenizer(initializers, ",");
        try
        {
            while(st.hasMoreTokens())
            {
                ciName = st.nextToken().trim();
                ciClassName = props.getProperty("cache.initializers." + ciName + ".classname", null);
                ciObjectNames = props.getProperty("cache.initializers." + ciName + ".objects", null);
                if(ciObjectNames == null)
                    // if no object name is specified, use the same name as
                    // initializer name
                    ciObjectNames = ciName;

                if(ciClassName == null)
                {
                	_logger.warn("No CacheInitializer found for " + ciName);
                    System.err.println("No CacheInitializer found for " + ciName);
                    continue;
                }
                boolean isLazyLoad = false;
                // this entry decides a cache is loaded loaded during initial load (false) or on first access (true).
                // overridden by the "isAllLazyLoad" property read above
                try
                {
                    isLazyLoad = props.getProperty("cache.initializers." + ciName + ".lazyLoad", "false").toLowerCase().equals("true");
                }
                catch(Exception e)
                {
                    // nothing to do if the property is not specified
                }
                st2 = new StringTokenizer(ciObjectNames, ",");
        		//First register all the caches to support cache dependency independent of cache definition order.
                while(st2.hasMoreTokens()){
                    objectName = st2.nextToken().trim();
                    /*
                     * To support comma separated values of caches for the CacheInitializer
                     * eg.
                     * 	cache.initializers.nvpairs.classname=com.addval.cargobkg.NvPairReferenceDataReader
                     *  cache.initializers.nvpairs.objects=AirportCountry,OwnerCodes,ServiceCodes,DgrSphCode,AutoCancelExemptSphCodes,SphCode,CurrencyCodes,AppParameters
                     */
                    ci = (CacheInitializer) Class.forName(ciClassName).newInstance();
                    ci.setLazyCacheLoad(isAllLazyLoad || isLazyLoad);
                    // just store the cache initializers
                    registerCacheInitializer(objectName, ci);
                } // while
                loadCaches( false );
            } // while

        }
        catch(CacheException ce)
        {
            _cacheLoaded = false;
            System.out.println(ce.getMessage());
            ce.printStackTrace();
            _logger.error(ce);
            throw new RuntimeException("Cache initialization failed: " + ce.getMessage());
        }
        catch(ClassNotFoundException cnfe)
        {
            _cacheLoaded = false;
            throw cnfe;
        }
        catch(InstantiationException ie)
        {
            _cacheLoaded = false;
            throw ie;
        }
        catch(IllegalAccessException ile)
        {
            _cacheLoaded = false;
            throw ile;
        }
        catch(Exception e)
        {
            _cacheLoaded = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Cache initialization failed: " + e.getMessage());
        }
    }

    // method do the actual initialization of the caches
    public void loadCaches(boolean isRefresh) throws CacheException
    {
        for (CacheInitializer ci : _initializers.values())
        	loadCache(ci, isRefresh);
    }

    private Object loadCache(CacheInitializer ci, boolean isRefresh) throws CacheException
    {
    	String objectName = ci.getObjectName();
        if(ci.isLazyCacheLoad()) {
            _logger.info("Cache " + objectName + " is set as LazyLoad and so will be inititlaised on first access. Skipping populateData");
        	return null;
        }
		if(contains( objectName )){
			_logger.debug(" Cache Initializer is already configured = " + objectName + " by another module");
			return _items.get( objectName );
		}
        Duration duration = new Duration();
        duration.startNow();
		Object object = ci.populateData(this, objectName, isRefresh);
		add( objectName, object, ci.isUseExternalCache() );
		duration.endNow();
		_logger.info(getName() + " Cache '" + objectName + "' ExecTime : " + duration.getMilliSeconds() + " ms");
		return object;
    }

    /**
     * @param objectName
     * @roseuid 3C72BAE3024E
     */
    public Object refresh(String objectName)
    {
        return refresh(objectName, _initializers.get(objectName));
    }
    public Object refresh(String objectName,boolean useExternalCache,IExternalCache externalCache)
    {
    	
        return refresh(objectName, _initializers.get(objectName),true,true,externalCache);
    }
    
    private Object refresh(String objectName, CacheInitializer ci)
    {
    	return refresh( objectName, ci, true );
    }

    private Object refresh(String objectName, CacheInitializer ci, boolean isRefresh)
    {
        if(ci == null){
            return null;
        }
        try
        {
            //if using external cache, clear it
            if(ci.isUseExternalCache() && this.getExternalCache() != null){
                this.getExternalCache().clear(objectName);
            }

            Duration duration = new Duration();
            duration.startNow();

            Object object = ci.populateData(this, objectName, isRefresh);
            add(objectName,object, ci.isUseExternalCache());

    		duration.endNow();
    		_logger.info(getName() + " Cache '" + objectName + "' ExecTime : " + duration.getMilliSeconds() + " ms");
            return object;
        }
        catch(CacheException ce)
        {
            _logger.error(ce);
            throw new RuntimeException("Cache refresh failed for - " + objectName + " with msg: " + ce.getMessage());
        }
    }
/*
  	public void refreshAll()
    {
    	try {
    		loadCaches( true );
    	}
    	catch(Exception e) {
            _logger.error(e);
            throw new RuntimeException("Cache refresh failed."+ e.getMessage(), e);
    	}
    }
*/
    
    private Object refresh(String objectName, CacheInitializer ci, boolean isRefresh,boolean useExternalCache,IExternalCache externalCache)
    {
        if(ci == null)
            return null;

        try
        {
            synchronized(ci){
                //if using external cache, clear it
                if(((ci.isUseExternalCache() && this.getExternalCache() != null)||externalCache!=null)||useExternalCache){
                	if( this.getExternalCache()!=null)
                		this.getExternalCache().clear(objectName);
                	else if(externalCache!=null)
                		externalCache.clear(objectName);
                }
                if(useExternalCache)
                return add(objectName, ci.populateData(this, objectName, isRefresh ), (ci.isUseExternalCache()||useExternalCache),externalCache);
                else
                	 return add(objectName, ci.populateData(this, objectName, isRefresh ), (ci.isUseExternalCache()||useExternalCache));
            }
        }
        catch(CacheException ce)
        {
            _logger.error(ce);
            throw new RuntimeException("Cache refresh failed for - " + objectName + " with msg: " + ce.getMessage());
        }
    }

    public void refreshAll()
    {
    	try {
	        for (CacheInitializer ci : _initializers.values()){
	        	refresh(ci.getObjectName(), ci);
	        }
	    }
    	catch(Exception e) {
            _logger.error(e);
            throw new RuntimeException("Cache refresh failed."+ e.getMessage(), e);
    	}
    }
  	
    public static Object refresh(CacheRefreshDetail cacheRefreshDetail) throws CacheException
    {
    	String cacheName = cacheRefreshDetail.getCacheName();
    	String objectName = cacheRefreshDetail.getObjectName();
    	if (cacheName == null) {
    		_logger.warn( "No cache name is specified to refresh object " + objectName );
    		return null;
    	}
    	CacheMgr manager = getRawInstance( cacheName );
    	CacheInitializer ci = manager.getCacheInitializers().get( objectName );
    	if(ci == null){
    		_logger.warn("No CacheInitializer found for " + cacheName);
            System.err.println("No CacheInitializer found for " + cacheName);
            return null;
    	}
    	
        Duration duration = new Duration();
        duration.startNow();

        Object object = ci.populateData(manager, cacheRefreshDetail);
        manager.add(objectName,object,ci.isUseExternalCache());

		duration.endNow();
		_logger.info(cacheName + " Cache '" + objectName + "' ExecTime : " + duration.getMilliSeconds() + " ms");
        return object;
    }

    // create a cache instance without initialization
    public static synchronized CacheMgr getRawInstance(String name)
    {
        CacheMgr c = _instances.get(name);
        if(c != null)
            return c;

        c = new CacheMgr();
        c._name = name;
        _instances.put(name, c);
        return c;
    }

    public static synchronized boolean  hasInstance(String name) {
    	return _instances.containsKey(name);
    }

    public void registerCacheInitializer(String objectName, CacheInitializer ci)
    {
    	ci.setObjectName( objectName );
		getCacheInitializers().put(objectName, ci);
    }

    public Map<String, CacheInitializer> getCacheInitializers()
    {
        return _initializers;
    }

    /**
     * @return the _externalCache
     */
    public IExternalCache getExternalCache() {
        return _externalCache;
    }

    /**
     * @param externalCache the _externalCache to set
     */
    public void setExternalCache(IExternalCache externalCache) {
        this._externalCache = externalCache;
    }
}
