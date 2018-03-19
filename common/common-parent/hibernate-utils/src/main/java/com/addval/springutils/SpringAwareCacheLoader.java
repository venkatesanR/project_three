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

package com.addval.springutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.addval.environment.Environment;
import com.addval.utils.CacheInitializer;
import com.addval.utils.CacheMgr;
import com.addval.utils.DefaultNamedCacheInitializer;
import com.addval.utils.LogMgr;

public class SpringAwareCacheLoader implements ApplicationListener {
	private static final transient Logger _logger = LogMgr.getLogger(SpringAwareCacheLoader.class);
	private CacheMgr _cacheMgr = null;
	private List<CacheInitializer> _cacheInitList = null;
	private boolean lazyCacheLoad;
    private static boolean cacheLoaded = false;

	public SpringAwareCacheLoader() {
	}

	public CacheMgr getCacheManager() {
		return _cacheMgr;
	}

	public void setCacheManager(CacheMgr cacheMgr) {
		_cacheMgr = cacheMgr;
	}

	public boolean isLazyCacheLoad() {
		return lazyCacheLoad;
	}

	public void setLazyCacheLoad(boolean lazyCacheLoad) {
		this.lazyCacheLoad = lazyCacheLoad;
	}

	public List<CacheInitializer> getCacheInitializerList() {
		return _cacheInitList;
	}

	public void setCacheInitializerList(List<CacheInitializer> aCacheInitList) {
		_cacheInitList = aCacheInitList;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (!(event instanceof ContextRefreshedEvent))
			return;
		ApplicationContext ctx = ((ContextRefreshedEvent) event).getApplicationContext();
		if (_logger.isDebugEnabled()) {
			 _logger.debug(" Context Refreshed Event is triggered in SpringAwareCacheLoader");
			logBeanNames(ctx);
		}
		Environment localenvironment = null;
		List<String> localcachenames = null;
		try{
			localenvironment = (Environment) ctx.getBean("localenvironment");
			String localcachenamesStr = (String) ctx.getBean("localcachenames");
			if(localcachenamesStr != null){
				localcachenames = Arrays.asList(localcachenamesStr.split(","));
			}
		}
		catch(Exception ex){
			localenvironment=null;
			localcachenames=null;
		}

		// get all the cacheInitializers
		Map<String, CacheInitializer> map = ctx.getBeansOfType(CacheInitializer.class);
		Collection<CacheInitializer> cols = null;

		// if named cache list is configured with the loader then use that,
		// otherwise get all the NamedCacheInitializers configured and
		// attempt to load them
		if ((getCacheInitializerList() != null) && (getCacheInitializerList().size() > 0)){
			cols = getCacheInitializerList();
		}
		else{
			cols = map.values();
		}
		DefaultNamedCacheInitializer defaultNamedCacheInitializer = null;
		//First register all the caches to support cache dependency independent of cache definition order.
		for (CacheInitializer init : cols) {
			String objectName = init.getObjectName();
			if (getCacheManager().getCacheInitializers().containsKey(objectName))
				continue;
			if( localcachenames != null && localcachenames.contains( objectName )){
				if (init instanceof DefaultNamedCacheInitializer) {
					defaultNamedCacheInitializer = (DefaultNamedCacheInitializer) init;
					defaultNamedCacheInitializer.setEnvironment( localenvironment );
				}
			}
			getCacheManager().registerCacheInitializer(init.getObjectName(), init);
		}
		_logger.debug(" Cache Initializers configured = " + cols.size());
		// cache loading is set Lazy globally and applies for all caches at startup.
		if (isLazyCacheLoad()) {
			_logger.info( "Lazy init is set true for all caches and so initalization will happen during the first access.");
		}
		else {
			for (CacheInitializer init : cols) {
				// if a particular cache is set for Lazy load don't load it during startup
				if (init.isLazyCacheLoad())
					continue;
				String objectName = init.getObjectName();
				// non-lazy caches are loaded during server start up
				if( !getCacheManager().contains( objectName ) ){
					_logger.debug(" Cache: " + getCacheManager().getName() + " Object: " + objectName + " begin load");
					getCacheManager().get( objectName );
					_logger.debug(" Cache: " + getCacheManager().getName() + " Object: " + objectName + " completed load");
				}
				else {
					_logger.debug(" Cache Initializer is already configured = " + objectName + " by another module");
				}
			}
		}
		try{
			if(!cacheLoaded){
				// initialize the other caches that are specified in the properties file
				CacheMgr.getInstance(getCacheManager().getName());
				cacheLoaded = true;
			}
		}
		catch(Exception ex){
			_logger.error(" Error while loading Caches :" + getCacheManager().getName() + " from file.");
		}
	}

	private void logBeanNames(ApplicationContext ctx) {
		_logger.debug("Application Context " + ctx.getDisplayName() + " Bean Count: " + ctx.getBeanDefinitionCount());
		String[] names = ctx.getBeanDefinitionNames();
		for (int i = 0; i < names.length; ++i)
			_logger.debug("" + i + " ---> " + names[i]);

		_logger.debug("------------------: " + ctx.getBeanDefinitionCount());
	}
}
