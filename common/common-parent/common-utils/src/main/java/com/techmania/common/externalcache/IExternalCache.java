/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techmania.common.externalcache;

import java.util.List;
import java.util.Map;

/**
 * @author ravi.nandiwada
 */
public interface IExternalCache {
    /**
     * Get from a named cache
     *
     * @param cacheName
     * @param key
     * @return
     */
    public Object get(String cacheName, Object key);

    /**
     * Put object in named cache
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public void put(String cacheName, Object key, Object value);

    /**
     * Remove the cache object mapped to the key
     *
     * @param cacheName
     * @param key
     */
    public void remove(String cacheName, Object key);

    /**
     * Get Write lock if the purpose is to modify the value for the key
     *
     * @param cacheName
     * @param key
     */
    public void getWriteLock(String cacheName, Object key);

    /**
     * Must release the write lock after modification (usually in a finally block)
     *
     * @param cacheName
     * @param key
     */
    public void releaseWriteLock(String cacheName, Object key);

    /**
     * Calling system should call shutdown
     */
    public void shutdown();

    /**
     * Allows cache manager to flush cache elements to off memory storage
     */
    public void flush(String cacheName);

    /**
     * Returns the list of keys for the named cache
     *
     * @param cacheName
     * @return
     */
    public List getKeys(String cacheName);

    /**
     * Retrieve all cached elements and return the named cache
     * Note that this could be an expensive operation
     *
     * @param cacheName
     * @return
     */
    public Map get(String cacheName);

    /**
     * Returns true if the key exists in the cache
     *
     * @param cacheName
     * @param key
     * @return
     */
    public boolean containsKey(String cacheName, Object key);

    /**
     * Returns the size of the cache (in memory + off memory)
     * Note that this could be an expensive operation
     *
     * @param cacheName
     * @return
     */
    public int size(String cacheName);

    /**
     * Deletes all cache entries of the named cache
     *
     * @param cacheName
     */
    public void clear(String cacheName);
}
