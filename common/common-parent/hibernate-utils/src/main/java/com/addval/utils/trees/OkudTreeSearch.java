/*
 * OkudTreeSearch.java
 *
 * Created on May 23, 2006, 3:26 PM
 *
 */

package com.addval.utils.trees;

import java.io.Serializable;
import java.util.*;

/**
 * This class helps define search criteria to search an OkudTree.
 * User can specify search criteria at 0 or more levels of the tree (
 * 0 will return the entire set of leaf nodes).
 *
 * Search criteria for all levels need not be defined. When a level's search criteria
 * are not defined, then all sub nodes at that level are considered along the
 * search path.
 *
 * Any level can be searched for an exact match of one or more keys.
 * However, only those levels that are defined as range searchable
 * can be range searched (between a min and a max key).
 *
 * @author ravi
 */
public class OkudTreeSearch implements java.io.Serializable, Cloneable
{
    /** Constant indicating search for an exact match */
    public static final int _SEARCH_EQUAL = 1;

    /** Constant indicating a range search */
    public static final int _SEARCH_RANGE = 2;

    /** Number of levels in the tree */
    protected int _numLevels = 0;

    /** Tree to which this search applies */
    protected OkudTreeLevel[] _levels;

    /**
     * An array of ASearchKeys objects.
     * Each object can be either an instance of EqualSearchKeys or RangeSearchKeys.
     * The array is of length equal to the number of levels in the tree but search
     * criteria for all levels need not be defined. When a level's search criteria
     * are not defined, then all sub nodes at that level are considered along the
     * search path.
     */
    ASearchKeys[] _searchKeys;

    /**
     * When false, will not trigger an auto populate of the child branches
     * When true, will call populate() method of the helper class and will create
     * missing sub branches. This flag is only valid, if  a tree is lazy loaded or
     * if at least one branch is set to discardable.
     * Default: true
     */
    protected boolean _autoPopulate = true;

    /**
     * Internal map of levels by level name
     * key - level name, value - OkudTreeLevel object
     */
    protected HashMap _levelMap;

    /**
     * This flag is by level and with default true - which means when search keys
     * are not provided, all sub nodes are assumed matched. Caller may set a level
     * to false - which means the only way sub nodes can be includes is if the level
     * was defined with a wild card key. So, be extra cautious when setting the flag to false.
     * This flag is really to force only a wildcard match when no search key can be provided.
     * Of course, you can always pass the wildcard key as a search key instead of defining a
     * wildcard as part of the level definition.
     */
    protected boolean[] _inclusiveSearch;
    
    /**
     * Set this flag to true if breadth first search is desired. Please note that the helper
     * class must implment IExtendedOkudTreeHelper interface for breadth first search. The feature is supported when using
     * XML based configuration and XmlOkudTreeHelper.
     */
    private boolean _breadthFirst = false;
    
    /**
     * Set the value in milliseconds for a search thread to wait while other threads are populating matched node in this search
     * Only used for breadth first search
     * Default is 3000L
	 * [arun.bc] - Increased from 100 to 3000 to support slowness of database in case load (100+ concurrent users)
     */
    private long _waitForPopulateMillSeconds = 3000L;

    /**
     * Creates a new instance of OkudTreeSearch
     *
     * @param tree - tree to be searched used to validate search criteria
     */
    public OkudTreeSearch(OkudTreeLevel[] levels)
    {
        if(levels == null || levels.length < 1)throw new TreeException("OkudTreeSearch constructor", "At least 1 level is required");
        _levels = levels;
        _numLevels = _levels.length;
        _searchKeys = new ASearchKeys[_numLevels];
        _inclusiveSearch = new boolean[_numLevels];
        _levelMap = new HashMap(_numLevels);
        for(int i = 0; i < _numLevels; i++){
            _levelMap.put(_levels[i].getName(), _levels[i]);
            _inclusiveSearch[i] = true;
        }
    }

    /** Returns number of levels of the OkudTree */
    public int getNumLevels() {
        return _numLevels;
    }

    /**
     * Add a search key to search a level. If multiple keys have to be searched at that
     * level, call this method multiple times or call
     * addEqualSearchKeys(int levelIndex, Object[] searchkeys) or
     * addEqualSearchKeys(int levelIndex, Collection searchkeys).
     *
     * @param levelIndex - 0-based index of the level
     * @param searchKey - key to match at this level (proper hash implemented)
     */
    public OkudTreeSearch addEqualSearchKey(int levelIndex, Object searchKey)
    {
        ASearchKeys ask = (ASearchKeys)_searchKeys[levelIndex];
        if(ask == null){
            OkudTreeLevel level = _levels[levelIndex];
            if(level.isRangeSearchable()){
                ask = new RangeSearchKeys();
            }
            else{
                ask = new EqualSearchKeys();
            }
            _searchKeys[levelIndex] = ask;
        }
        ask.addSearchKey(searchKey);

        return this;
    }

    /**
     * Add a search key to search a level. If multiple keys have to be searched at that
     * level, call this method multiple times or call
     * addEqualSearchKeys(String levelName, Object[] searchkeys) or
     * addEqualSearchKeys(String levelName, Collection searchkeys).
     *
     * @param levelName - unique name of a level
     * @param searchKey - key to match at this level (proper hash implemented)
     *
     * @throws NullPointerException if level with level name does not exist
     */
    public OkudTreeSearch addEqualSearchKey(String levelName, Object searchKey)
    {
        return addEqualSearchKey(this.getLevel(levelName).getLevelIndex(), searchKey);
    }

    /**
     * Add an array of search keys to search a level.
     *
     * @param levelIndex - 0-based index of the level
     * @param searchKeys - Array of keys to match at this level (proper hash implemented)
     */
    public OkudTreeSearch addEqualSearchKeys(int levelIndex, Object[] searchKeys)
    {
        for(int i = 0; i < searchKeys.length; i++)addEqualSearchKey(levelIndex, searchKeys[i]);

        return this;
    }

    /**
     * Add an array of search keys to search a level.
     *
     * @param levelName - unique name of a level
     * @param searchKeys - Array of keys to match at this level (proper hash implemented)
     *
     * @throws NullPointerException if level with level name does not exist
     */
    public OkudTreeSearch addEqualSearchKeys(String levelName, Object[] searchKeys)
    {
        return addEqualSearchKeys(this.getLevel(levelName).getLevelIndex(), searchKeys);
    }

    /**
     * Add a collection of of search keys to search a level.
     *
     * @param levelIndex - 0-based index of the level
     * @param searchKeys - Collection of keys to match at this level (proper hash implemented)
     */
    public OkudTreeSearch addEqualSearchKeys(int levelIndex, Collection searchKeys)
    {
        for(Iterator iter = searchKeys.iterator(); iter.hasNext();)
                addEqualSearchKey(levelIndex, iter.next());

        return this;
    }

    /**
     * Add a collection of of search keys to search a level.
     *
     * @param levelIndex - unique name of a level
     * @param searchKeys - Collection of keys to match at this level (proper hash implemented)
     *
     * @throws NullPointerException if level with level name does not exist
     */
    public OkudTreeSearch addEqualSearchKeys(String levelName, Collection searchKeys)
    {
        return addEqualSearchKeys(this.getLevel(levelName).getLevelIndex(), searchKeys);
    }


    /**
     * Add a range of key values to search a level. At least one of min or max
     * must be specified and the level must be range searchable.
     *
     * @param levelIndex - 0-based index of the level
     * @param minSearchKey - match nodes at this level whose keys are equal to or greater than this min
     * @param maxSearchKey - match nodes at this level whose keys are less than this max
     *
     * @throws TreeException if the level specified by levelIndex is not range searchable
     */
    public OkudTreeSearch addRangeSearchKeys(int levelIndex, Comparable minSearchKey, Comparable maxSearchKey)
    {
        OkudTreeLevel level = _levels[levelIndex];
        if(!level.isRangeSearchable()){
            throw new TreeException("The level " + levelIndex + " is not range searchable");
        }

        RangeSearchKeys rsk  = (RangeSearchKeys)_searchKeys[levelIndex];
        if(rsk == null){
            rsk = new RangeSearchKeys(minSearchKey, maxSearchKey);
            _searchKeys[levelIndex] = rsk;
        }
        else{
            rsk.setRange(minSearchKey, maxSearchKey);
        }

        return this;
    }

    /**
     * Add a range of key values to search a level. At least one of min or max
     * must be specified and the level must be range searchable.
     *
     * @param levelIndex - 0-based index of the level
     * @param minSearchKey - match nodes at this level whose keys are equal to or greater than this min
     * @param maxSearchKey - match nodes at this level whose keys are less than this max
     *
     * @throws TreeException if the level specified by levelIndex is not range searchable
     * @throws NullPointerException if level with level name does not exist
     */
    public OkudTreeSearch addRangeSearchKeys(String levelName, Comparable minSearchKey, Comparable maxSearchKey)
    {
        return addRangeSearchKeys(this.getLevel(levelName).getLevelIndex(), minSearchKey, maxSearchKey);
    }

    /**
     * Returns search keys at requested level
     */
    ASearchKeys getSearchKeys(int levelIndex)
    {
        return _searchKeys[levelIndex];
    }

    /**
     * Returns search keys at requested level
     *
     * @throws NullPointerException if level with level name does not exist
     */
    ASearchKeys getSearchKeys(String levelName)
    {
        return getSearchKeys(this.getLevel(levelName).getLevelIndex());
    }

    /**
     * Returns list of keys or returns null if no keys or the search keys are not
     * "equal" search keys.
     */
    public Set getEqualSearchKeys(int levelIndex)
    {
        ASearchKeys  sks = this.getSearchKeys(levelIndex);
        if(sks == null || sks.getSearchType() != _SEARCH_EQUAL)return null;
        return ((EqualSearchKeys)sks).getSearchKeys();
    }

    /**
     * Returns list of keys or returns null if no keys or the search keys are not
     * "equal" search keys.
     *
     * @throws NullPointerException if level with level name does not exist
     */
    public Set getEqualSearchKeys(String levelName)
    {
        return getEqualSearchKeys(this.getLevel(levelName).getLevelIndex());
    }

    /**
     * Returns the min key or returns null if no keys or the search keys are not
     * "range" search keys
     */
    public Comparable getRangeMinSearchKey(int levelIndex)
    {
        ASearchKeys  sks = this.getSearchKeys(levelIndex);
        if(sks == null || sks.getSearchType() != _SEARCH_RANGE)return null;
        return ((RangeSearchKeys)sks).getMinSearchKey();
    }

    /**
     * Returns the min key or returns null if no keys or the search keys are not
     * "range" search keys
     *
     * @throws NullPointerException if level with level name does not exist
     */
    public Comparable getRangeMinSearchKey(String levelName)
    {
        return getRangeMinSearchKey(this.getLevel(levelName).getLevelIndex());
    }

    /**
     * Returns the max key or returns null if no keys or the search keys are not
     * "range" search keys.
     */
    public Comparable getRangeMaxSearchKey(int levelIndex)
    {
        ASearchKeys  sks = this.getSearchKeys(levelIndex);
        if(sks == null || sks.getSearchType() != _SEARCH_RANGE)return null;
        return ((RangeSearchKeys)sks).getMaxSearchKey();
    }

    /**
     * Returns the max key or returns null if no keys or the search keys are not
     * "range" search keys.
     *
     * @throws NullPointerException if level with level name does not exist
     */
    public Comparable getRangeMaxSearchKey(String levelName)
    {
        return getRangeMaxSearchKey(this.getLevel(levelName).getLevelIndex());
    }

    /**
     * Clears previously set search criteria
     */
    public void clear()
    {
        for(int i = 0; i < _numLevels; i++)_searchKeys[i] = null;
    }

    /**
     * Clear the search keys at a level
     * @param levelIndex
     * @exception ArrayIndexOutOfBoundsException  if levelIndex is out of [0 - #levels-1]
     */
    public void clear(int levelIndex){
    
    	_searchKeys[levelIndex] = null;
    }

    /**
     * Clear the search keys at a level
     * @param levelName
     * @exception ArrayIndexOutOfBoundsException  if levelIndex is out of [0 - #levels-1]
     */
    public void clear(String levelName){

    	this.clear(this.getLevel(levelName).getLevelIndex());
    }

    /**
     * Turn on/off inclusive search for a level. If the level doesn't define a wildcard
     * and the inclusive search for the level is turned off (false), the search will not
     * yield any result when the level search keys are not set
     *
     * @param levelIndex
     * @param val
     * @exception ArrayIndexOutOfBoundsException when levelIndex of out of bounds
     */
    public void setInclusiveSearch(int levelIndex, boolean val){
        _inclusiveSearch[levelIndex] = val;
    }

    /**
     * Turn on/off inclusive search for a level. If the level doesn't define a wildcard
     * and the inclusive search for the level is turned off (false), the search will not
     * yield any result when the level search keys are not set
     *
     * @param levelName
     * @param val
     */
    public void setInclusiveSearch(String levelName, boolean val){
        this.setInclusiveSearch(this.getLevel(levelName).getLevelIndex(), val);
    }

    public boolean isInclusiveSearch(int levelIndex){
        return _inclusiveSearch[levelIndex];
    }

    public boolean isInclusiveSearch(String levelName){
        return this.isInclusiveSearch(this.getLevel(levelName).getLevelIndex());
    }
    
    public String toString()
    {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("OkudTreeSearch[");
        ASearchKeys ask = null;
        for(int i = 0; i < this._numLevels; i++){
            ask = (ASearchKeys)_searchKeys[i];
            if(ask != null){
                sb.append(lineSep);
                sb.append("Level(").append(_levels[i].getName()).append(") ");
                sb.append(ask);
            }
        }
        sb.append("]");

        return sb.toString();
    }

    protected OkudTreeLevel getLevel(String name)
    {
        return (OkudTreeLevel)_levelMap.get(name);
    }

    /**
     * @return the _breadthFirst
     */
    public boolean isBreadthFirst() {
        return _breadthFirst;
    }

    /**
     * @param breadthFirst the _breadthFirst to set
     */
    public void setBreadthFirst(boolean breadthFirst) {
        this._breadthFirst = breadthFirst;
    }

    /**
     * @return the _waitForPopulateMillSeconds
     */
    public long getWaitForPopulateMillSeconds() {
        return _waitForPopulateMillSeconds;
    }

    /**
     * @param waitForPopulateMillSeconds the _waitForPopulateMillSeconds to set
     */
    public void setWaitForPopulateMillSeconds(long waitForPopulateMillSeconds) {
        this._waitForPopulateMillSeconds = waitForPopulateMillSeconds;
    }

    abstract static class ASearchKeys implements Serializable
    {
        protected int _searchType = 0;

        public ASearchKeys(int searchType)
        {
            _searchType = searchType;
        }

        public int getSearchType() {
            return _searchType;
        }

        public void setSearchType(int searchType) {
            this._searchType = searchType;
        }

        public abstract void addSearchKey(Object key);

        public abstract Set getSearchKeys();
    }

    static class EqualSearchKeys extends ASearchKeys
    {
        /**
         * To prevent duplicates we use a HashSet
         */
        protected HashSet _keys;

        EqualSearchKeys()
        {
            super(_SEARCH_EQUAL);
        }

        public void addSearchKey(Object key)
        {
            if(_keys == null)_keys = new HashSet();
            _keys.add(key);
        }

        public Set getSearchKeys()
        {
            return _keys;
        }

        public String toString()
        {
            return "EqualSearchKeys[" + _keys + "]";
        }
    } //end class EqualSearchKeys

    static class RangeSearchKeys extends ASearchKeys
    {
        protected Comparable _minSearchKey;
        protected Comparable _maxSearchKey;
        protected TreeSet _equalSearchKeys;

        RangeSearchKeys()
        {
            super(_SEARCH_RANGE);
        }

        /**
         * result include min but excludes max
         */
        RangeSearchKeys(Comparable minSearchKey, Comparable maxSearchKey)
        {
            super(_SEARCH_RANGE);
            setRange(minSearchKey, maxSearchKey);
        }

        void setRange(Comparable minSearchKey, Comparable maxSearchKey)
        {
            if(minSearchKey == null && maxSearchKey == null)
                throw new TreeException("RangeSearchKeys ctr", "At least one of min and max search keys should be non-null");

            if(minSearchKey != null && maxSearchKey != null && minSearchKey.compareTo(maxSearchKey) >= 0){
                throw new TreeException("RangeSearchKeys ctr", "Min search key must be less than max search key");
            }

            _minSearchKey = minSearchKey;
            _maxSearchKey = maxSearchKey;
        }

        public Comparable getMinSearchKey() {
            return _minSearchKey;
        }

        public Comparable getMaxSearchKey() {
            return _maxSearchKey;
        }

        void setMinSearchKey(Comparable key) {
            _minSearchKey = key;
        }

        void setMaxSearchKey(Comparable key) {
            _maxSearchKey = key;
        }

        /**
         * The keys must of type Comparable for equal search when searching
         * a range searchable level
         */
        public void addSearchKey(Object key){
            if(_equalSearchKeys == null)_equalSearchKeys = new TreeSet();
            _equalSearchKeys.add((Comparable)key);
        }

        public Set getSearchKeys()
        {
            return _equalSearchKeys;
        }

        /**
         * Removes redundant equal search keys if they fall within the range specified
         * when a range is specified even if the range is open ended.
         */
        public void consolidateKeys()
        {
            //if no range specified, return
            if(_minSearchKey == null && _maxSearchKey == null)return;
            if(_equalSearchKeys == null || _equalSearchKeys.isEmpty())return;

            //remove in range equal keys
            Comparable key;
            for(Iterator iter = _equalSearchKeys.iterator(); iter.hasNext();){
                key = (Comparable)iter.next();
                if((_maxSearchKey == null && key.compareTo(_minSearchKey) >= 0) ||
                        (_minSearchKey == null && key.compareTo(_maxSearchKey) < 0) ||
                        (key.compareTo(_minSearchKey) >= 0 &&
                            key.compareTo(_maxSearchKey) < 0)) iter.remove();
            }

        }

        public String toString()
        {
            return "RangeSearchKeys[min=" + _minSearchKey + ", max=" + _maxSearchKey +
                ", equ=" + _equalSearchKeys + "]";
        }

    } //end class RangeSearchKeys

    public boolean isAutoPopulate() {
        return _autoPopulate;
    }

    public void setAutoPopulate(boolean autoPopulate) {
        this._autoPopulate = autoPopulate;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        OkudTreeSearch search =  (OkudTreeSearch)super.clone();
        //copy arrays so that the new object can be used on its own
        search._inclusiveSearch = Arrays.copyOf(_inclusiveSearch, _inclusiveSearch.length);
        search._searchKeys = Arrays.copyOf(this._searchKeys, this._searchKeys.length);
        return search;
    }
}
