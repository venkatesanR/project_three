/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.addval.externalcache;

import java.net.URL;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * A simple Ehcache based external cache. Configure this as a spring bean and
 * specify the bean name in OkudTree configuration xml.
 * 
 * @author ravi.nandiwada
 */
public class EhcacheUtility implements IExternalCache {
	private CacheManager _cm;

	public EhcacheUtility(String configFile) {
		_cm = new CacheManager(this.getCacheConfiguration(configFile));
	}

	public EhcacheUtility(URL configUrl) {
		_cm = new CacheManager(configUrl);
	}

	public EhcacheUtility() {
		_cm = new CacheManager();
	}

	private URL getCacheConfiguration(String cacheConfigFile) {
		URL url = this.getClass().getClassLoader().getResource(cacheConfigFile);
		if (url == null)
			url = this.getClass().getResource(cacheConfigFile);
		return url;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (_cm != null) {
				_cm.shutdown();
			}
		} catch (Exception e) {
			// ignore
		}
		super.finalize();
	}

	public Object get(String cacheName, Object key) {
		Element elem = getCache(cacheName).get(key);
		return elem == null ? null : elem.getObjectValue();
	}

	public void put(String cacheName, Object key, Object value) {
		getCache(cacheName).put(new Element(key, value));
	}

	private Ehcache getCache(String cacheName) {
		return _cm.getEhcache(cacheName);
	}

	public void remove(String cacheName, Object key) {
		getCache(cacheName).remove(key);
	}

	public void getWriteLock(String cacheName, Object key) {
		if (!getCache(cacheName).isWriteLockedByCurrentThread(key)) {
			getCache(cacheName).acquireWriteLockOnKey(key);
		}
	}

	public void releaseWriteLock(String cacheName, Object key) {
		if (getCache(cacheName).isWriteLockedByCurrentThread(key)) {
			getCache(cacheName).releaseWriteLockOnKey(key);
		}
	}

	public void shutdown() {
		try {
			_cm.shutdown();
		} catch (Exception e) {

		}
	}

	public void flush(String cacheName) {
		getCache(cacheName).flush();
	}

	public List getKeys(String cacheName) {
		return getCache(cacheName).getKeys();
	}

	public Map get(String cacheName) {
		Ehcache ehc = getCache(cacheName);
		if (ehc == null)
			return null;

		return ehc.getAllWithLoader(ehc.getKeys(), null);
	}

	public boolean containsKey(String cacheName, Object key) {
		return this.getCache(cacheName).isKeyInCache(key);
	}

	public int size(String cacheName) {
		return this.getCache(cacheName).getSize();
	}

	public void clear(String cacheName) {
		this.getCache(cacheName).removeAll();
	}

}
