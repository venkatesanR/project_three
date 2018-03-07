/*
 * OkudTreeLevel.java
 *
 * Created on May 11, 2006, 4:08 PM
 *
 */

package com.addval.trees;

import com.addval.utils.externalcache.IExternalCache;
import com.addval.utils.StrUtl;

/**
 * This class defines a level of a uniform depth OkudTree.
 * A level helps determine the nature of the tree including
 * the way the tree is stored in memory and how it is searched.
 *
 * @author ravi
 */
public class OkudTreeLevel implements java.io.Serializable
{
    /**
     * In a multilevel tree implementation multiple level keys may
     * source from the same underlying entity. For example, if the
     * tree has origin and destination as 2 levels, keys for both levels
     * could derive from the underlying entity airport. The level type
     * is used to optimize the memory usage through internal cache of keys
     */
    protected String _levelType;
    
    /**
     * The index of this level which is 0-based. The top level has index 0.
     */
    protected int _levelIndex = 0;
    
    /**
     * Unique name of this level. 
     */
    protected String _name;
    
    /**
     * Score for this level when the key at this level is not a wildcard.
     */
    protected long _score = 0;
    
    /**
     * This is the level from which all children can be discarded if heap space
     * has to be reclaimed. This level is implemented as a soft reference.
     */
    protected boolean _discardable = false;
    
    /**
     * When true, OkudTree will use the value in _wildcardKey (even if it is null)
     * as wildcard to search the tree
     */
    protected boolean _wildcard = false;
    
    /**
     * Wildcard key (e.g., for an airport ANY or NULL, etc.) for this level
     */
    protected Object _wildcardKey;
    
    /**
     * If rangeSearchable is true this level will be implemented as a TreeMap.
     * The keys at this level must be of type Comparable
     */
    protected boolean _rangeSearchable = false;
    
    /**
     * If discardable this flag can be set to true to use LRU algorithm to discard.
     * However, this flag is only an indicator and will not strictly enforce LRU
     */
    protected boolean _discardLRU = false;
    
    /**
     * If _discardLRU is true then you can specify the max nodes at this level
     * across all branches. At any given time, a max of these many nodes at this level
     * will be protected from reclaiming by garbage collector. If it 0 then it is equivalent to
     * not setting _discardLRU.
     */
    protected int _maxNodesAtLevel = 0;
    
    /**
     * If true, the helper should implement method getLevelKeys(tree, node, level)
     * and the OkudTree will call that method to get a set of next level keys in that
     * branch. The value for this flag must be false for the last level in the tree.
     */
    protected boolean _createLevelKeysOnly = false;
    
    /**
     * When the level key match may result in multiple results 
     * set this flag to true. For example, a sph code may match
     * multiple sph groups in the same sph group level. This flag affects
     * how the search is carried out. This may make the search less efficient.
     * Only valid for equal search
     */
    protected boolean _multiMatch = false;
    
    /**
     * If true, external cache will be used to store this level down until a lower level flag is true and cache management is
     * delegated to the external cache engine. When this flag is true, discardable and discardLRU 
     * have no effect on the behavior of the OkudTree cache. 
     */
    protected boolean _useExternalCache = false;
               
    /**
     * Creates a new instance of OkudTreeLevel
     * 
     * @param name - Unique name identifying the level
     * @param type - A non unique type of the level
     */
    protected OkudTreeLevel(String name, String type) 
    {
        if(StrUtl.isEmptyTrimmed(name))throw new IllegalArgumentException("OkudTree level name cannot be empty or null");
        _name = name;
        _levelType = type;
    }

    /**
     * Creates a new instance of OkudTreeLevel
     *
     * @param name - Unique name identifying the level
     * @param type - A non unique type of the level
     * @param score - score for this level
     * @param discardable - indicates whether this level can be discarded if needed
     */
    public OkudTreeLevel(String name, String type, long score, boolean discardable)
    {
        this(name, type);
        _score = score;
        _discardable = discardable;
    }
    
    /**
     *  Creates a new instance of OkudTreeLevel
     * 
     * @param name - Unique name identifying the level
     * @param type - A non unique type of the level
     * @param useExternalCache - set to true if the level nodes have to be maintained in external cache
     */
    public OkudTreeLevel(String name, String type, boolean useExternalCache)
    {
        this(name, type);
        _useExternalCache = useExternalCache;
    }
        
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof OkudTreeLevel))return false;
        return equals((OkudTreeLevel)o);
    }
    
    /**
     * If the name of the two levels match, then the levels are equal
     */
    public boolean equals(OkudTreeLevel level)
    {
        if(this == level)return true;
        if(level == null)return false;
        
        return StrUtl.equals(this._name, level._name);
    }

    public String getName() {
        return _name;
    }

    public long getScore() {
        return _score;
    }

    public boolean isDiscardable() {
        return _discardable;
    }
    
    public String toString()
    {
        return "OkudTreeLevel[" + _levelIndex + ", " + _name + ", " + _levelType + 
            ", " + _score + ", " + _discardable + ", "  + _discardLRU + ", " + _maxNodesAtLevel +
            ", " + _createLevelKeysOnly + ", " + isUseExternalCache() + "]";
    }

    public String getLevelType() {
        return _levelType;
    }

    public Object getWildcardKey() {
        return _wildcardKey;
    }

    public void setWildcardKey(Object wildcardKey) {
        this._wildcardKey = wildcardKey;
        //set _wildcard to true for backward compatibility
        //as this flag is added later to clearly identify 
        //whether to do a wildcard search in cases where the wildcard
        //is null
        _wildcard = true;
    }

    public int getLevelIndex() {
        return _levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this._levelIndex = levelIndex;
    }

    public boolean isRangeSearchable() {
        return _rangeSearchable;
    }
    
    public void setRangeSearchable(boolean rangeSearchable) {
        this._rangeSearchable = rangeSearchable;
    }    
    
    public boolean isDiscardLRU() {
        return _discardLRU;
    }
    
    public void setDiscardLRU(boolean discardLRU) {
        this._discardLRU = discardLRU;
    }    

    public int getMaxNodesAtLevel() {
        return _maxNodesAtLevel;
    }

    public void setMaxNodesAtLevel(int maxNodesAtLevel) {
        this._maxNodesAtLevel = maxNodesAtLevel;
    }  
    
    public boolean isLruCacheNeeded()
    {
        return !_useExternalCache && isDiscardable() && isDiscardLRU() && getMaxNodesAtLevel() > 0;
    }

    public boolean isCreateLevelKeysOnly() {
        return _createLevelKeysOnly;
    }

    public void setCreateLevelKeysOnly(boolean createLevelKeysOnly) {
        this._createLevelKeysOnly = createLevelKeysOnly;
    }

    public boolean hasWildcard() {
        return _wildcard;
    }

    public void setWildcard(boolean wildcard) {
        this._wildcard = wildcard;
    }

    public boolean isMultiMatch() {
        return _multiMatch;
    }

    public void setMultiMatch(boolean multiMatch) {
        this._multiMatch = multiMatch;
    }
      
    /**
     * @return the _useExternalCache
     */
    public boolean isUseExternalCache() {
        return _useExternalCache;
    }

    /**
     * @param useExternalCache the _useExternalCache to set
     */
    public void setUseExternalCache(boolean useExternalCache) {
        this._useExternalCache = useExternalCache;
    }
    
    private static final long serialVersionUID = 910L;    
}
