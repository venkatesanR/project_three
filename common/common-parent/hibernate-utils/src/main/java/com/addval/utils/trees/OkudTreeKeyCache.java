/*
 * OkudTreeKeyCache.java
 *
 * Created on May 24, 2006, 11:11 AM
 *
 */

package com.addval.utils.trees;

import java.util.*;

/**
 * This class holds keys for nodes of an OkudTree when key caching is enabled.
 * This cache can be discarded after the tree is fully created. However, if either the
 * tree is lazy loaded or an level is discardable, it is recommended to retain the cache
 * provided that the insert performance and memory are not the most critical.
 *
 * @author ravi
 */
public class OkudTreeKeyCache 
{
    /**
     * A map to hold sets of keys by level.
     * Key - level type
     * Value - TreeMap/HashMap of keys (key and value are the same Comparable key)
     * The Value is TreeMap if the level(level type) is range searchable
     * else it is a HashMap
     */
    protected HashMap _levelKeys;
    
    /** Creates a new instance of OkudTreeKeyCache */
    public OkudTreeKeyCache() 
    {
        _levelKeys = new HashMap();
    }
    
    /** Retrieves the cached key that matches passed key in the level
     * This method auto-adds the key
     * 
     * @param level - level of the OkudTree for which a key is required
     * @param key - key to be matched
     */
    public Object getKey(OkudTreeLevel level, Object key)
    {
        Map levelMap = (Map)_levelKeys.get(level.getLevelType());
        if(levelMap == null){
            if(level.isRangeSearchable()){
                levelMap = new TreeMap();
            }
            else{
                levelMap = new HashMap();
            }
            _levelKeys.put(level.getLevelType(), levelMap);
        }
        
        Object cachedKey = levelMap.get(key);
        if(cachedKey == null){
            levelMap.put(key, key);
            cachedKey = key;
        }
        
        return cachedKey;
    } 
    
    /** Retrieves the cached key that matches passed key in the level
     * This method does not auto add the key. But return the passed in key
     * back in case the key is not found in the cache
     * 
     * @param level type - level type
     * @param key - key to be matched
     */
    public Object getKey(String levelType, Object key)
    {
        Map levelMap = (Map)_levelKeys.get(levelType);
        if(levelMap == null)return key;
        Object cachedKey = levelMap.get(key);
        return cachedKey == null ? key : cachedKey;
    } 
    
    /** Clears the key cache */
    public void clearCache()
    {
        _levelKeys.clear();
    }
}
