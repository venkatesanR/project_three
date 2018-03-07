/*
 * OkudTree.java
 *
 * Created on March 1, 2006, 11:42 AM
 *
 * CopyRight AddVal Technology Inc.
 */

package com.addval.trees;

import java.util.*;
import com.addval.utils.*;
import com.addval.utils.externalcache.IExternalCache;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

/**
 * OkudTree is similar to SkudTree in that every node is a map that holds sub maps.
 * OkudTree is designed to address some limitations of SkudTree and give the programmer
 * more flexibility in creating and managing the tree.
 * SkudTree only allows Strings as keys. OkudTree allows any object to act as a key provided
 * the key has proper implementation of certain methods.
 * A key can be of any class that has a proper implementation of equals() and hashCode() method
 * or a class that implements Comparable interface.
 * An OkudTree has a fixed set of levels that are predefined similar to SkudTree.
 * However each level can be uniquely defined using the class OkudTreeLevel.
 * Nodes at level are implemented either as HashMaps or TreeMaps based on how the
 * level is defined. If a level is range searchable, the key must implement Comparable interface.
 * A level can be defined as discardable in which case if memory is required
 * information in the tree from that level down will be discarded. Top level cannot be discardable.
 * Helper class must implement methods to properly rebuild discarded branches of the tree or search
 * results will be inaccurate.
 *
 * OkudTree can be created as lazy load meaning the tree is built up on demand. However the tree is
 * initialized to the top level. Helper class must implement all required methods for lazy loaded tree.
 *
 * OkudTree allows 2 different types of search. 1. Equals search - an exact key match. All types
 * of levels allow for this type of search.
 * 2. Range search - min, max and range searches. Only allowed for those levels that are defined as
 * range searchable. Levels that are range searchable cannot be auto searched for wildcard matches.
 * There must exist an equal criterion with wild card as part of the search criteria. Range search
 * and equal search cannot be performed at the same time on this level.
 * The search criteria do not need to define search parameters for all levels. When a level is not
 * specified in the search criteria, all nodes will be considered in that branch at that level.
 *
 * For this class to be fully serializable, all keys and leaf nodes must be serializable.
 *
 * This implementation also allows for ranking of leaf nodes based on preset
 * scores for the levels and key values.
 *
 * @author ravi
 */
public class OkudTree
{
    private static final Logger _logger = Logger.getLogger(OkudTree.class);

    /**
     * Root node of the Tree
     */
    protected OkudTreeNode _root;

    /**
     * The ordered list of OKudTreeLevel objects associated with each "level" of the tree.
     * It is intended that each level will be descriptive of the key values to be inserted.
     * The number of levelNames specified determines the uniform depth of the OkudTree.
     */
    protected OkudTreeLevel[] _levels;

    /**
     * When _enforceSerializable is true, all application leaf node data Objects are checked for
     * serializability before insertion into the tree.
     */
    protected boolean _enforceSerializable = false;

    /**
     * When _enforcedApplicationDataObjectInstance is non-null, all applicationDataObjects
     * are checked before insertion into the tree that they are an instance of this example
     * Object's run-time class.
     */
    protected Object _enforcedApplicationDataObjectInstance;

    /**
     * Tree helper to help to add leaf node data, build on demand, etc.
     */
    protected IOkudTreeHelper _helper;
    protected IExtendedOkudTreeHelper _internalHelper;

    /**
     * Use key cache flag. When true, a key cache is maintained and used in the tree
     * to reduce memory foot print while building the tree. For example,
     * If the tree has origin and destination as levels and both or of type airport and
     * there are 9000 airports, if a key cache is not used, there is a possibility
     * that we'll hold 9000 * 9000 strings in memory. But when the key cache is used,
     * only 9000 strings are held in memory as they are reused in the tree as keys.
     * However, using key cache may impact performance at the time of building the tree.
     *
     */
    protected boolean _useKeyCache = false;

    /**
     * Key cache. Holds set of keys used in the tree.
     */
    protected OkudTreeKeyCache _keyCache;

    /**
     * Flag to indicate the tree is lazy loaded.
     * If the flag is true the tree constructor calls helper method getTopLevelKeys()
     * to initialize the top level nodes.
     */
    protected boolean _lazyLoad = false;

    /**
     * Cache for LRU
     */
    protected AVLinkedList[] _levelLruCache;

    /**
     * Normally, the top level nodes are all loaded at once thru getTopLevelKeys
     * when lazyload is turned on. Set this to true when the top level is built
     * incrementally through search. Note that this is incompatible for searching
     * without top level search keys when the top level is not fully populated.
     */
    protected boolean _topLevelIncrementalLoad = false;

    /**
     * When searching breadth first, empty nodes are identified at a level and the
     * helper called to populate those. However, multiple threads may initiate the load
     * of the same node. To prevent that use a sync set to identify nodes that need to be populated in a thread
     * if different from nodes identified in another thread
     *
     */
    protected final static Set<AOkudTreeNode> _cachedEmptyNodes = Collections.synchronizedSet(new HashSet<AOkudTreeNode>());

    /**
     * And flag to indicate that at least one level uses an external cache. When true it is expected that an external cache
     * name and cache utility are set.
     */
    private boolean _useExternalCache = false;

    /**
     *  External cache name.
     */
    protected String _externalCacheName;

    /**
     * External cache utility
     */
    protected transient IExternalCache _externalCacheUtility;

    /**
     * A semaphore to limit the load on the system. Controls how many concurrent getLevelKeys/populate methods
     * can be called on the helper concurrently. <=0 is unlimited
     */
    private int _maxConcurrentPopulates = 0;

    private ConcurrentCounter _concurrentCounter = new ConcurrentCounter(Integer.MAX_VALUE/2);
    
    private long refreshtimeOutInMillisec= 400000L; //Create/Refresh 400s 
    private long searchTimeOutInMillisec= 30000L; //search 30s


	/**
     * Creates tree with external cache when useExternalCache is true and an external cache name and utility provided.
     *
     * @param levels - Array of OkudTreeLevel objects defining levels in the tree
     * @param helper - An instance of IOkudTreeHelper used to build the tree
     * @param enforceSerializable - Flag indicating that the data in leaf node is serializable
     * @param enforcedApplicationDataObjectInstance - When not null enforces that the data object in leaf node is of this instance
     * @param useKeyCache - Flag when true tells the tree to use cached keys for building tree
     * @param lazyLoad - Flag when true indicates that the tree will be initialized on demand
     * @param useExternalCache - flag informing whether external cache is used
     * @param externalCacheName - name of the external cache
     * @param externalCacheUtility - external cache utility that implements IExternalCache
     */
    public OkudTree(OkudTreeLevel[] levels,
                    IOkudTreeHelper helper,
                    boolean enforceSerializable,
                    Object enforcedApplicationDataObjectInstance,
                    boolean useKeyCache,
                    boolean lazyLoad,
                    boolean useExternalCache,
                    String externalCacheName,
                    IExternalCache externalCacheUtility)
    {
        this.initialize(levels, helper, enforceSerializable, enforcedApplicationDataObjectInstance,
                useKeyCache, lazyLoad, useExternalCache, externalCacheName, externalCacheUtility);

        //when _topLevelIncrementalLoad = true this call need not add all the top level nodes
        if(_lazyLoad){
            createTopLevelNodes();
        }
    }

    /**
     * Creates a new instance of OkudTree.
     * @param levels - Array of OkudTreeLevel objects defining levels in the tree
     * @param helper - An instance of IOkudTreeHelper used to build the tree
     * @param enforceSerializable - Flag indicating that the data in leaf node is serializable
     * @param enforcedApplicationDataObjectInstance - When not null enforces that the data object in leaf node is of this instance
     * @param useKeyCache - Flag when true tells the tree to use cached keys for building tree
     * @param lazyLoad - Flag when true indicates that the tree will be initialized on demand
     */
    public OkudTree(OkudTreeLevel[] levels,
                    IOkudTreeHelper helper,
                    boolean enforceSerializable,
                    Object enforcedApplicationDataObjectInstance,
                    boolean useKeyCache,
                    boolean lazyLoad)
    {
        this.initialize(levels, helper, enforceSerializable, enforcedApplicationDataObjectInstance,
                useKeyCache, lazyLoad, false, null, null);

        //when _topLevelIncrementalLoad = true this call need not add all the top level nodes
        if(_lazyLoad){
            createTopLevelNodes();
        }

    }

	public long getRefreshtimeOutInMillisec() {
		return refreshtimeOutInMillisec;
	}

	public void setRefreshtimeOutInMillisec(long refreshtimeOutInMillisec) {
		this.refreshtimeOutInMillisec = refreshtimeOutInMillisec;
	}

	public long getSearchTimeOutInMillisec() {
		return searchTimeOutInMillisec;
	}

	public void setSearchTimeOutInMillisec(long searchTimeOutInMillisec) {
		this.searchTimeOutInMillisec = searchTimeOutInMillisec;
	}

    private void initialize(OkudTreeLevel[] levels,
                    IOkudTreeHelper helper,
                    boolean enforceSerializable,
                    Object enforcedApplicationDataObjectInstance,
                    boolean useKeyCache,
                    boolean lazyLoad,
                    boolean useExternalCache,
                    String externalCacheName,
                    IExternalCache externalCacheUtility)
    {
        //validate params
        if(levels == null || levels.length < 1){
            throw new TreeException("OkudTree constructor",
					"OkudTreeLevel[] levels argument is null or has zero length");
        }

        //ensure there are no duplicate level names
        //and to determine if lru cache needed
        //and to determine if external cache is used at any level
        boolean lruCacheNeeded = false;
        int lastLruLevelIndex = 0;
        HashSet levelset = new HashSet(levels.length);
        for(int i = 0; i < levels.length; i++){
            if(!levelset.add(levels[i].getName()) || i != levels[i].getLevelIndex())
                throw new TreeException("Duplicate/missing level name/index (" + levels[i].getName() +
                    ") found at level " + (i+1));

            if(levels[i].isLruCacheNeeded()){
                lruCacheNeeded = true;
                lastLruLevelIndex = i;
            }
        }

        if(levels[0].isDiscardable()){
            throw new TreeException("Top level must not be discardable");
        }

        if(levels[levels.length - 1].isCreateLevelKeysOnly()){
            throw new TreeException("The flag createLevelKeysOnly must be false for the last level");
        }

        if(helper == null)throw new TreeException("OkudTree constructor", "Tree helper cannot be null");

        //lru cache
        if(lruCacheNeeded){
            _levelLruCache = new AVLinkedList[lastLruLevelIndex+1];
            for(int i = 0; i < lastLruLevelIndex+1; i++){
                if(levels[i].isLruCacheNeeded())_levelLruCache[i] = new AVLinkedList();
            }
        }

        if(useKeyCache){
            _useKeyCache = useKeyCache;
            _keyCache = new OkudTreeKeyCache();
        }

        _lazyLoad = lazyLoad;
        _helper = helper;
        _internalHelper = new InternalExtendedHelper(_helper, _concurrentCounter);

        _levels = levels;
        _enforceSerializable = enforceSerializable;
        _enforcedApplicationDataObjectInstance = enforcedApplicationDataObjectInstance;

        _useExternalCache = useExternalCache;
        _externalCacheName = externalCacheName;
        _externalCacheUtility = externalCacheUtility;

        _root = new OkudTreeNode(null, null, createMap(_levels[0]), this, 0);
                //new OkudTreeNode(null, null, createMap(_levels[0]), false, false); //parent is null, key is null
    }

    /**
     * This method is called in the constructor when the tree is to be lazy loaded.
     * This method will create all top level nodes.
     */
    private void createTopLevelNodes()
    {
        //when external persistent cache is used
        //the top level nodes may be already available in the external cache
        if(_root.isUseExternalCache()){
            Map childMap = _root.getChildMap();
            if(childMap != null && !childMap.isEmpty())return;
        }

        List keys = _internalHelper.getTopLevelKeys(_levels[0]);
        if(ListUtl.isEmpty(keys)){
            //user is responsible initializing all top level nodes through putLeafNode method
            //throw new TreeException("OkudTree.createTopLevelNodes", "Key list for top level cannot be empty");
            return;
        }

        Object key = null;
        AOkudTreeNode node = null;

        for(Iterator iter = keys.iterator(); iter.hasNext();){
            key = iter.next();
            if(_useKeyCache)key = this._keyCache.getKey(_levels[0], key);
            if(_levels.length > 1){
                node = new OkudTreeNode(_root, key, createMap(_levels[1]), this, 1);
            }
            else{
                //leaf node
                node = new OkudTreeLeafNode(_root, key);
            }
            _internalHelper.onCreateNode(this, node, _levels[0]);
            _root.put(key, node);
        }
    }

    /**
     * Creates next level of nodes - child nodes of node but no other levels are populated
     *
     * @param level
     * @param node
     */
    protected void createNodesAtLevel(OkudTreeLevel level, OkudTreeNode node)
    {
        createNodesAtLevel(_internalHelper.getLevelKeys(this, node, level), level, node);
    }

    /**
     * Creates next level of nodes - child nodes of node with all the keys but no other levels are populated
     *
     * @param keys
     * @param level
     * @param node
     */
    public final void createNodesAtLevel(Collection keys, OkudTreeLevel level, OkudTreeNode node)
    {
        //we don't want to throw an exception even though the branch is incomplete
        //as during the course of the app, data might have been deleted from the db
        //rendering the nodes above invalid
        if(ListUtl.isEmpty(keys))return;

        Object key = null;
        AOkudTreeNode localnode = null;
        OkudTreeLevel nextLevel = null;

        if(level.getLevelIndex() < _levels.length - 1){
            //not the last level
            nextLevel = _levels[level.getLevelIndex() + 1];
        }
        
        boolean lockObtained =true;        
       
        try {
			lockObtained = node.lock(refreshtimeOutInMillisec);		
            if (lockObtained) {	
                for(Iterator iter = keys.iterator(); iter.hasNext();){
                    key = iter.next();
                    if(node.get(key) != null)continue;
                    if(_useKeyCache)key = this._keyCache.getKey(level, key);
                    //if not last level then create node else leaf node
                    if(nextLevel != null){
                        localnode = new OkudTreeNode(node, key, createMap(nextLevel), this, nextLevel.getLevelIndex());
                    }
                    else{
                        //leaf node
                        localnode = new OkudTreeLeafNode(node, key);
                    }
                    _internalHelper.onCreateNode(this, localnode, level);
                     node.put(key, localnode);
                }            
            }
            else{
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for cache update. Please Retry request.");
            }
        }
        catch (InterruptedException e) {
        	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for cache update. Please Retry request.");
		}
        finally {
        	if (lockObtained) {
        		node.unlock();
        	}
        }    


        /*synchronized(node){
            for(Iterator iter = keys.iterator(); iter.hasNext();){
                key = iter.next();
                if(node.get(key) != null)continue;
                if(_useKeyCache)key = this._keyCache.getKey(level, key);
                //if not last level then create node else leaf node
                if(nextLevel != null){
                    localnode = new OkudTreeNode(node, key, createMap(nextLevel), this, nextLevel.getLevelIndex());
                }
                else{
                    //leaf node
                    localnode = new OkudTreeLeafNode(node, key);
                }
                _internalHelper.onCreateNode(this, localnode, level);
                 node.put(key, localnode);
            }
        }*/
    }

    /**
     * Returns number of levels of the tree
     *
     * @return number of tree levels
     */
    public int getNumLevels()
    {
        return _levels.length;
    }

    /**
     * Returns OkudTreeLevel at the index specified
     *
     * @param levelIndex - 0 based level index
     * @return level requested
     * @throws ArrayIndexOutOfBounds if request level index is invalid
     */
    public OkudTreeLevel getLevel(int levelIndex)
    {
        return _levels[levelIndex];
    }

    /**
     * Returns the helper instance passed in at the construction time.
     */
    public IOkudTreeHelper getHelper()
    {
        return _helper;
    }

    /**
     * Returns OkudTreeLevel[] array used to construct this tree
     *
     * @return levels
     */
    public OkudTreeLevel[] getLevels()
    {
        return _levels;
    }

   /**
	 * Returns rootNode used to construct this tree
	 *
	 * @return rootNode
	 */
	public OkudTreeNode getRootNode()
	{
		return _root;
    }

    /**
     * Create either a TreeMap or HashMap at a level
     * based on whether the level is range searchable or not.
     */
    protected Object createMap(OkudTreeLevel level)
    {
        Map map = null;

        if(level.isRangeSearchable()){
            map = new TreeMap();
        }
        else{
            map = new HashMap();
        }

        if(level.isLruCacheNeeded()){
            return addToLruCache(level, map);
        }

        return map;
    }

    /**
     * Create branches until a level has no keys
     *
     * @param keys list of keys in the order of levels starting at level 0
     */
    public void createBranches( List<Object[]> keys)
    {
        if(ListUtl.isEmpty(keys) || keys.size() > this.getNumLevels()){
            throw new TreeException("OkudTree.createBranches: keys list size must be > 0 and <= number of levels");
        }
        createBranches(new Object[this.getNumLevels()], 0, keys); //recursion
    }

    /**
     *
     * @param singleKeys
     * @param levelIndex
     * @param keys
     */
    protected void createBranches(Object[] singleKeys, int levelIndex, List<Object[]> keys)
    {
        if(levelIndex == this.getNumLevels()){
            this.createBranches(singleKeys);
            return;
        }

        Object[] keysAtALevel = keys.get(levelIndex);

        if(keysAtALevel == null || keysAtALevel.length == 0){
            this.createBranches(singleKeys);
            return;
        }

        for(int i = 0; i < keysAtALevel.length; i++){
            singleKeys[levelIndex] = keys.get(levelIndex)[i];
            createBranches(singleKeys, levelIndex + 1, keys); //recursion
        }
    }

    /**
     * This method will creating multiple branches at a level by passing multiple keys at a level
     * For each permutation of keys, putLeafNode( Object[] keys, Object applicationDataObject) is called
     * @param keys list of size numLevels with possibly multiple keys at a level
     * @param applicationDataObject
     */
    public void putLeafNode( List<Object[]> keys, Object applicationDataObject)
    {
        if(ListUtl.isEmpty(keys) || keys.size() != this.getNumLevels()){
            throw new TreeException("OkudTree.putLeafNode: keys list size must match the number of levels");
        }
        putLeafNode(new Object[this.getNumLevels()], 0, keys, applicationDataObject); //recursion
    }

    /**
     * Iterator to set the key at the levelIndex and call final putLeafNode method when all level keys are set
     *
     * @param singleKeys array of one key at a level
     * @param levelIndex current level to which a key values must be set
     * @param keys original key list with possibly multiple keys at a level
     * @param applicationDataObject application data to be set at the lead node
     */
    protected void putLeafNode(Object[] singleKeys, int levelIndex, List<Object[]> keys, Object applicationDataObject)
    {
        if(levelIndex == this.getNumLevels()){
            this.putLeafNode(singleKeys, applicationDataObject);
            return;
        }

        for(int i = 0; i < keys.get(levelIndex).length; i++){
            singleKeys[levelIndex] = keys.get(levelIndex)[i];
            putLeafNode(singleKeys, levelIndex + 1, keys, applicationDataObject); //recursion
        }
    }

    /**
     * This method allows populating the tree by adding required branched and
     * creating a leaf node or replacing data in a leaf node based on the keys passed.
     * The length of array keys must match the number of levels of the tree.
     * The key at any level can be null. If the key is null at a level the method
     * tries to substitute the wild card key of that level if specified. However, if a level key
     * is null and the level doesn't have a wild card key specified and the level is range
     * searchable then this method will fail.
     *
     * Specified helper is used to set the data at the leaf node.
     *
     * @param keys - array of level keys for a new leaf node
     * @param applicationDataObject - application data to be set at the leaf node matching keys
     */
    public void putLeafNode( Object[] keys, Object applicationDataObject)
    {
        if (this.getEnforceSerializable() && !(applicationDataObject instanceof java.io.Serializable))
        {
                throw new TreeException("OkudTree.putLeafNode(Object[],Object)",
                                "Object applicationDataObject argument is not Serializable");
        }

        if (!isAllowableApplicationDataObjectType(applicationDataObject))
        {
                throw new TreeException("SkudTree.putLeafNode(Object[],Object)",
                                "Object applicationDataObject argument is not an instance of the previously-specified Class");
        }

        if(keys.length != this.getNumLevels()){
            throw new TreeException("OkudTree.putLeafNode(Object[],Object)",
					"Length of keys array does not match number of levels in the tree");
        }

	//add to existing or create new leaf node
        getOrCreateLeafNode(keys, applicationDataObject);

    }

    /**
     * Allows the truncation of a branch or branches of the tree.
     * For example, consider a tree with 3 levels with values - origin (o1, o2), destination (d1, d2),
     * product (p1, p2) and assume it is a complete tree and every node has all the possible sub nodes.
     * Now if say certain leaf nodes are obsolete or need refreshing under [o1, d1], pass these 2 keys
     * to this method. The method will remove all branches under [o1, d1] - that means [o1, d1, p1] and
     * [o1, d1, p2].
     * Of course, the tree and the level below must be lazy loadable.
     * If the length of keys array is equal to the number of levels and if a match is found then
     * the matching leaf node is removed
     *
     * @param keys  Identify branches that are to be truncated. The size of this array can be less than
     * the number of levels but the keys must be sequential from level 0.
     *
     * @param createBranch If true a new branch matching the keys will be created if none existed before. Useful
     * for cache refresh - adding new branches and auto loading data for that new branch on demand.
     *
     * @return boolean True if branches matching the keys were found and truncated
     *
     */
    public boolean truncate(Object[] keys, boolean createBranch)
    {
        if(keys == null || keys.length == 0)throw new TreeException("OkudTree.truncate() expects at least one key starting at level 0");
        if(keys.length > this.getNumLevels())throw new TreeException("OkudTree.truncate() keys array length cannot be greater than # of levels");

        Object key = null;
        OkudTreeNode currentNode = _root;
        OkudTreeNode node = null;
        int numLevels = this.getNumLevels();
        OkudTreeLevel nextLevel = null;
        OkudTreeLevel level = null;
        boolean ret = true;

        for(int i = 0; i < keys.length; i++){
            key = keys[i];
            level = this.getLevel(i);

            if(i < numLevels - 1){
                node = (OkudTreeNode)currentNode.get(key);
                if(node == null){
                    if(createBranch){
                        //check if a child map already exists for the key, if not create one
                        Map childMap = currentNode.getChildMap();

                        if(childMap == null){
                            Object map = this.createMap(level);
                            currentNode.setChildMap(map);
                        }
                        //get the previously set map to ensure actually cached childmap is retrieved
                        childMap = currentNode.getChildMap();

                        if(childMap != null){
                            if(_useKeyCache)key = this._keyCache.getKey(level, key);
                            nextLevel = this.getLevel(i + 1);
                            //if not last level then create node else leaf node
                            OkudTreeNode localnode = new OkudTreeNode(currentNode, key, createMap(nextLevel), this, nextLevel.getLevelIndex());
                            _internalHelper.onCreateNode(this, localnode, level);
                            currentNode.put(key, localnode);
                            currentNode = localnode;
                        }
                    }
                    ret = false;
                } //node == null
                else currentNode = node;
            }
            else{
                OkudTreeLeafNode leafNode = (OkudTreeLeafNode)currentNode.get(key);
                if(leafNode != null){
                    leafNode.setLeafNodeData(null);
                }
                else{
                    if(createBranch){
                        Map childMap = currentNode.getChildMap();
                        if(childMap == null){
                            Object map = this.createMap(level);
                            currentNode.setChildMap(map);
                        }
                        //get the previously set map to ensure actually cached childmap is retrieved
                        childMap = currentNode.getChildMap();
                        if(childMap != null){
                            if(_useKeyCache)key = _keyCache.getKey(level, key);
                            leafNode = new OkudTreeLeafNode(currentNode, key);
                            _internalHelper.onCreateNode(this, leafNode, level);
                            currentNode.put(key, leafNode);
                        }
                    }
                    ret = false;
                }

                currentNode = null;//leafnode is the current node, so don't want clear the parent node's child map
            } //else handle leaf node
        } //end for keys

        if(currentNode != null){
        	currentNode.setSearchTimeOutInMillisec(getSearchTimeOutInMillisec());
            currentNode.clearChildMap();
            currentNode.setTerminal(false);
        }

        return ret;
    }
	/**
     * Allows the truncation of a branch or branches of the tree.
     * It satsifies all the cases mentioned in truncate method along with the below additional complex cases.
     * For example, consider a tree with 2 top levels  - Origin, Destination
     *
     * Case 1 :
     * =======
     *   Initilaly the tree is loaded with first top level [Origin] alone.The second top level is not loaded still now like below.
     *
     *    Origin      Destination
     *    --------    -----------
     * 	    A           -
     *	  --------    -----------
     *  Now if the truncation request comes for A to B then it wont create the empty Node in tree to avoid no match case for A-C,A-D,A-E.
     *  Since once the empty node created it wont reload other sibblings.
     *
     *  Case 2 :
     *  ========
     *	 The tree was loaded with both top level combinations like below.
     *
     *		Origin      Destination
     *		------       ----------
     *		  A            B
     * 		  A            C
     *		--------    -----------
     *  Now if the truncation request comes for A to D then it will create the empty Node[A-D] in tree.
     *	So the final dynamic level will be loaded on demand.
     *
     * @param keys  Identify branches that are to be truncated. The size of this array can be less than
     * the number of levels but the keys must be sequential from level 0.
     *
     * @param createBranch If true a new branch matching the keys will be created if none existed before. Useful
     * for cache refresh - adding new branches and auto loading data for that new branch on demand.
     *
     * @return boolean True if branches matching the keys were found and truncated
     *
     */
    public boolean truncate2(Object[] keys, boolean createBranch)
    {
        if(keys == null || keys.length == 0)throw new TreeException("OkudTree.truncate() expects at least one key starting at level 0");
        if(keys.length > this.getNumLevels())throw new TreeException("OkudTree.truncate() keys array length cannot be greater than # of levels");

        Object key = null;
        OkudTreeNode currentNode = _root;
        OkudTreeNode node = null;
        int numLevels = this.getNumLevels();
        OkudTreeLevel nextLevel = null;
        OkudTreeLevel level = null;
        boolean ret = true;

        for(int i = 0; i < keys.length; i++){
            key = keys[i];
            level = this.getLevel(i);

            if(i < numLevels - 1){
                node = (OkudTreeNode)currentNode.get(key);
                if(node == null){
                    if(createBranch){
                        //check if a child map already exists for the key, if not create one
                        Map childMap = currentNode.getChildMap();

                        if(childMap == null && i == 0){
                            Object map = this.createMap(level);
                            currentNode.setChildMap(map);
                        }
                        //get the previously set map to ensure actually cached childmap is retrieved
                        childMap = currentNode.getChildMap();

                        if(childMap != null && childMap.size() > 0){
                            if(_useKeyCache)key = this._keyCache.getKey(level, key);
                            nextLevel = this.getLevel(i + 1);
                            //if not last level then create node else leaf node
                            OkudTreeNode localnode = new OkudTreeNode(currentNode, key, createMap(nextLevel), this, nextLevel.getLevelIndex());
                            _internalHelper.onCreateNode(this, localnode, level);
                            currentNode.put(key, localnode);
                            currentNode = localnode;
                        }
                    }
                    ret = false;
                } //node == null
                else currentNode = node;
            }
            else{
                OkudTreeLeafNode leafNode = (OkudTreeLeafNode)currentNode.get(key);
                if(leafNode != null){
                    leafNode.setLeafNodeData(null);
                }
                else{
                    if(createBranch){
                        Map childMap = currentNode.getChildMap();
                        if(childMap == null){
                            Object map = this.createMap(level);
                            currentNode.setChildMap(map);
                        }
                        //get the previously set map to ensure actually cached childmap is retrieved
                        childMap = currentNode.getChildMap();
                        if(childMap != null){
                            if(_useKeyCache)key = _keyCache.getKey(level, key);
                            leafNode = new OkudTreeLeafNode(currentNode, key);
                            _internalHelper.onCreateNode(this, leafNode, level);
                            currentNode.put(key, leafNode);
                        }
                    }
                    ret = false;
                }

                currentNode = null;//leafnode is the current node, so don't want clear the parent node's child map
            } //else handle leaf node
        } //end for keys

        if(currentNode != null){
        	currentNode.setSearchTimeOutInMillisec(getSearchTimeOutInMillisec());
            currentNode.clearChildMap();
            currentNode.setTerminal(false);
        }

        return ret;
    }

    /**
     * The method will truncate all branches under the specified level.
     * For example, if the levels in the tree are
     * Origin(idx = 0), Destination(idx = 1) and Container (idx  = 2)
     * and this method is call with fromIndex = 1 then  all levels below Origin
     * will be discarded mean no Destination keys below each Origin
     *
     * When the fromLevel = numLevels then the leaf node data will be set to null
     *
     * @param fromLevel level index is 0 based (valid value 0 to numLevels)
     * @return true if the truncate is successful false if the level < 0 or > number of levels
     */
    public boolean truncate(int fromLevel)
    {
        if(fromLevel < 0 || fromLevel > this.getNumLevels())return false;
        truncate(fromLevel, 0, _root);
        return true;
    }

    private void truncate(int truncateLevel, int currentLevel, AOkudTreeNode node)
    {
        if(truncateLevel == currentLevel){
            if(currentLevel < this.getNumLevels()){
            	 ((OkudTreeNode)node).setSearchTimeOutInMillisec(getSearchTimeOutInMillisec());
                //not leaf node
                ((OkudTreeNode)node).clearChildMap();
                //set terminal to false so that a search will initiate repopulation of this branch
                ((OkudTreeNode)node).setTerminal(false);
            }
            else{
                //leaf node
                ((OkudTreeLeafNode)node).setLeafNodeData(null);
            }
        }
        else{ //currentLevel < truncateLevel
            Collection branches = ((OkudTreeNode)node).values();
            if(!ListUtl.isEmpty(branches)){
                for(Iterator iter = branches.iterator(); iter.hasNext();){
                    truncate(truncateLevel, currentLevel + 1, (AOkudTreeNode)iter.next());
                }
            }
        }
    }

    /**
     * Creates new branch if does not exist
     *
     * @param keys
     * @return true if the branch is created, false if already exists
     */
    public boolean createBranches(Object[] keys)
    {
        if(keys == null || keys.length == 0)throw new TreeException("OkudTree.createBranch() expects at least one key starting at level 0");
        if(keys.length > this.getNumLevels())throw new TreeException("OkudTree.createBranch() keys array length cannot be greater than # of levels");

        Object key = null;
        OkudTreeNode currentNode = _root;
        OkudTreeNode node = null;
        int numLevels = this.getNumLevels();
        OkudTreeLevel nextLevel = null;
        OkudTreeLevel level = null;
        boolean ret = false;

        for(int i = 0; i < keys.length; i++){
            key = keys[i];
            //if the key is null, no further child branches can be created
            if(key == null)break;

            level = this.getLevel(i);

            if(i < numLevels - 1){
                node = (OkudTreeNode)currentNode.get(key);
                if(node == null){
                    //check if a child map already exists for the key, if not create one
                    Map childMap = currentNode.getChildMap();

                    if(childMap == null){
                        Object map = this.createMap(level);
                        currentNode.setChildMap(map);
                    }
                    //get the previously set map to ensure actually cached childmap is retrieved
                    childMap = currentNode.getChildMap();

                    if(childMap != null){
                        if(_useKeyCache)key = this._keyCache.getKey(level, key);
                        nextLevel = this.getLevel(i + 1);
                        //if not last level then create node else leaf node
                        OkudTreeNode localnode = new OkudTreeNode(currentNode, key, createMap(nextLevel), this, nextLevel.getLevelIndex());
                        _internalHelper.onCreateNode(this, localnode, level);
                        currentNode.put(key, localnode);
                        currentNode = localnode;
                    }
                    ret = true;
                } //node == null
                else currentNode = node;
            }
            else{
                OkudTreeLeafNode leafNode = (OkudTreeLeafNode)currentNode.get(key);
                if(leafNode != null){
                    leafNode.setLeafNodeData(null);
                    return ret;
                }
                else{
                    Map childMap = currentNode.getChildMap();
                    if(childMap == null){
                        Object map = this.createMap(level);
                        currentNode.setChildMap(map);
                    }
                    //get the previously set map to ensure actually cached childmap is retrieved
                    childMap = currentNode.getChildMap();
                    if(childMap != null){ //this should never fail
                        if(_useKeyCache)key = _keyCache.getKey(level, key);
                        leafNode = new OkudTreeLeafNode(currentNode, key);
                        _internalHelper.onCreateNode(this, leafNode, level);
                        currentNode.put(key, leafNode);
                        ret = true;
                    }

                    return ret;
                }
            }
        } //end for keys

        return ret;
    }

    /**
     * Add data to the leaf node if found or creates a new leaf node and add to it
     */
    protected void getOrCreateLeafNode(Object[] keys, Object applicationDataObject)
    {
        if(keys == null || keys.length != this._levels.length){
            throw new TreeException("OkudTree.getOrCreateLeafNode(Object[])",
					"Object[] keys argument is null or has wrong length");
        }

        Object key = null;
        OkudTreeNode currentNode = _root;
        OkudTreeNode node = null;
        OkudTreeLevel level = null;

        //Hold references of childmaps until method exits to prevent discarding of
        //childmaps from nodes in those cases where a level is discardable
        ArrayList childMaps = new ArrayList(_levels.length);
        Object childMap = null;

        for(int i = 0; i < keys.length - 1; i++){
            level = this._levels[i];
            key = keys[i];
            if(key == null && level.getWildcardKey() != null)key = level.getWildcardKey();

            boolean lockObtained =true;        
            
            try {
    			lockObtained = currentNode.lock(refreshtimeOutInMillisec);		
                if (lockObtained) {	
                	node = (OkudTreeNode)currentNode.get(key);

                    if(node == null){
                        //create a new branch for the key (the value for the key could have been discarded)
                        childMap = createMap(_levels[i+1]);
                        childMaps.add(childMap);
                        if(_useKeyCache)key = _keyCache.getKey(level, key);
                        node = new OkudTreeNode(currentNode, key, childMap, this, i+1);
                        _internalHelper.onCreateNode(this, node, level);
                        currentNode.put(key, node);
                    }

                    node.setTerminal(false);
                }
                else{
                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for cache update(l). Please Retry request.");
                }
            }
            catch (InterruptedException e) {
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for cache update. Please Retry request.");
    		}
            finally {
            	if (lockObtained) {
            		currentNode.unlock();
            	}
            }
            /*synchronized(currentNode){
                node = (OkudTreeNode)currentNode.get(key);

                if(node == null){
                    //create a new branch for the key (the value for the key could have been discarded)
                    childMap = createMap(_levels[i+1]);
                    childMaps.add(childMap);
                    if(_useKeyCache)key = _keyCache.getKey(level, key);
                    node = new OkudTreeNode(currentNode, key, childMap, this, i+1);
                    _internalHelper.onCreateNode(this, node, level);
                    currentNode.put(key, node);
                }

                node.setTerminal(false); //put leaf node forces the node to be non-terminal
            }//end sync */

            currentNode = node;

        } //end for keys
        
        boolean lockObtained =true;        
        
        try {
			lockObtained = currentNode.lock(refreshtimeOutInMillisec);		
            if (lockObtained) {	
            	 //get or add new leaf node
                key = keys[keys.length - 1];
                level = _levels[keys.length - 1];
                OkudTreeLeafNode leafNode = (OkudTreeLeafNode)currentNode.get(key);

                if(leafNode == null){
                    if(_useKeyCache)key = _keyCache.getKey(level, key);
                    leafNode = new OkudTreeLeafNode(currentNode, key);
                    _internalHelper.onCreateNode(this, leafNode, level);
                }

                _internalHelper.addDataToLeafNode(leafNode, applicationDataObject);
                //this is needed when either a new leaf node is created or if external cache is used
                currentNode.put(key, leafNode);
            }
            else{
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for cache update(l). Please Retry request.");
            }
        }
        catch (InterruptedException e) {
        	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for cache update. Please Retry request.");
		}
        finally {
        	if (lockObtained) {
        		currentNode.unlock();
        	}
        }

        /*synchronized(currentNode){
            //get or add new leaf node
            key = keys[keys.length - 1];
            level = _levels[keys.length - 1];
            OkudTreeLeafNode leafNode = (OkudTreeLeafNode)currentNode.get(key);

            if(leafNode == null){
                if(_useKeyCache)key = _keyCache.getKey(level, key);
                leafNode = new OkudTreeLeafNode(currentNode, key);
                _internalHelper.onCreateNode(this, leafNode, level);
            }

            _internalHelper.addDataToLeafNode(leafNode, applicationDataObject);
            //this is needed when either a new leaf node is created or if external cache is used
            currentNode.put(key, leafNode);
        } //end sync */

    }

    /**
     * Returns the leaf node if found or returns null. This method can be used
     * to delete/update leaf node data
     *
     * @param keys - array of level keys to find a leaf node.
     */
    public OkudTreeLeafNode getLeafNode(Object[] keys)
    {
        if(keys == null || keys.length != this._levels.length){
            throw new TreeException("OkudTree.getLeafNode(Object[])",
					"Object[] keys argument is null or has wrong length");
        }

        Object key = null;
        OkudTreeNode currentNode = _root;
        OkudTreeNode node = null;
        OkudTreeLevel level = null;

        for(int i = 0; i < keys.length - 1; i++){
            level = this._levels[i];
            key = keys[i];
            if(key == null && level.getWildcardKey() != null)key = level.getWildcardKey();

            node = (OkudTreeNode)currentNode.get(key);

            //no further branch matching keys found, return null
            if(node == null)return null;

            currentNode = node;
        } //end for keys

        //get the leaf node
        key = keys[keys.length - 1];
        level = _levels[keys.length - 1];

        OkudTreeLeafNode leafNode = (OkudTreeLeafNode)currentNode.get(key);

        return leafNode;
    }

    /**
     * Protected helper that determines whether the application-specified leaf data Object
     * has a run-time type that is allowable for insertion into a OkudTreeLeafNode.
     *
     * @return - whether the Object's run-time type is allowable.
     */
    protected boolean isAllowableApplicationDataObjectType(Object obj)
    {
        if (_enforcedApplicationDataObjectInstance == null)return true;
        else if (obj == null)return false;
        else return _enforcedApplicationDataObjectInstance.getClass().isInstance(obj);
    }

    /**
     * Accessor method. Returns the _enforceSerializable property.
     *
     * @return - the _enforceSerializable property.
     */
    public boolean getEnforceSerializable() { return _enforceSerializable; }

    /**
     * @return - a set of OkudTreeLeafNodes that match the criteria
     */
    public Set search(OkudTreeSearch criteria)
    {
        //consolidate range keys
        for(int i = 0; i < this.getNumLevels(); i++){
            if(_levels[i].isRangeSearchable() && criteria.getSearchKeys(i) != null
                    && criteria.getSearchKeys(i).getSearchType() == OkudTreeSearch._SEARCH_RANGE){
                ((OkudTreeSearch.RangeSearchKeys)criteria.getSearchKeys(i)).consolidateKeys();
            }
        }

        Set<AOkudTreeNode> resultNodes = new HashSet<AOkudTreeNode>();

        if(criteria.isBreadthFirst()){
            if(!(_helper instanceof IExtendedOkudTreeHelper)){
                throw new TreeException("Breadth First search requires helper class to implement IExtendedOkudTreeHelper");
            }
            resultNodes.add(_root);
            return this.searchLevelBreadthFirst(0, criteria, resultNodes, _internalHelper);
        }
        else{
            searchLevel(0, _root, criteria, resultNodes);
            return resultNodes;
        }
    }

    /**
     * Recursively invokes the method for all levels to compile a list of leaf nodes that match
     * the given criteria.
     */
    protected void searchLevel(int levelIndex, AOkudTreeNode inode, OkudTreeSearch criteria, Set resultNodes)
    {
        //if the last level is reached then add leaf nodes to the list
        if(levelIndex == _levels.length){
            //if leaf node data is null, call the helper to populate
            //only if the the heler implements IExtendedOkudTreeHelper
            //the helper class may choose to ignore the callback
            if(criteria.isAutoPopulate() && !inode.isTerminal()){
                OkudTreeLeafNode lnode = (OkudTreeLeafNode)inode;
                boolean lockObtained =true;        
                
                try {
        			lockObtained = lnode.lock(searchTimeOutInMillisec);		
                    if (lockObtained) {
                    	if(lnode.getLeafNodeData() == null && (_helper instanceof IExtendedOkudTreeHelper)){
                            _internalHelper.populateLeafNode(this, lnode, _levels[levelIndex - 1]);
                            //check the version in hand is the one populated
                            if(this.isUseExternalCache() && lnode.getLeafNodeData() == null){
                                //get the version in the external cache thru search
                                List<Object> keys = lnode.getKeysList();
                                if(!ListUtl.isEmpty(keys)){
                                    resultNodes.add(this.getLeafNode(keys.toArray()));
                                    return;
                                }
                            }
                        }
                    }
                    else{
                    	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                    }
                }
                catch (InterruptedException e) {
                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
        		}
                finally {
                	if (lockObtained) {
                		lnode.unlock();
                	}
                }
                /*synchronized(lnode){
                    if(lnode.getLeafNodeData() == null && (_helper instanceof IExtendedOkudTreeHelper)){
                        _internalHelper.populateLeafNode(this, lnode, _levels[levelIndex - 1]);
                        //check the version in hand is the one populated
                        if(this.isUseExternalCache() && lnode.getLeafNodeData() == null){
                            //get the version in the external cache thru search
                            List<Object> keys = lnode.getKeysList();
                            if(!ListUtl.isEmpty(keys)){
                                resultNodes.add(this.getLeafNode(keys.toArray()));
                                return;
                            }
                        }
                    }
                }*/
            }
            resultNodes.add(inode);
            return;
        }

        OkudTreeNode node = (OkudTreeNode)inode;
        Map childMap = null;
        LinkedListNode llnode = null;
        OkudTreeLevel level = _levels[levelIndex];

        if (level.isLruCacheNeeded()) {
			boolean lockObtained =true;        
		       
	        try {
				lockObtained = node.lock(searchTimeOutInMillisec);		
	            if (lockObtained) {	
	            	llnode = node.getChildMapLinkedListNode();
					if (llnode != null) {
						childMap = node.getChildMap();					
						node.setChildMap(this.revalueLruData(level, llnode));					
					}
	            }
            else{
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
            }
        }
        catch (InterruptedException e) {
        	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
		}
        finally {
        	if (lockObtained) {
        		node.unlock();
        	}
        }
			/*synchronized (node) { 
				llnode = node.getChildMapLinkedListNode();
				if (llnode != null) {
					childMap = node.getChildMap();					
					node.setChildMap(this.revalueLruData(level, llnode));					
				}
			}*/
		}
		else {
			childMap = node.getChildMap(); 
		}

       /* synchronized(node){
            if(level.isLruCacheNeeded()){
                llnode = node.getChildMapLinkedListNode();
                if(llnode != null){
                    childMap = node.getChildMap();
                    node.setChildMap(this.revalueLruData(level, llnode));
                }
            }
            else{
                childMap = node.getChildMap();
            }
        } */

        OkudTreeSearch.ASearchKeys searchKeys = criteria.getSearchKeys(levelIndex);
        AOkudTreeNode childNode = null;
        Object key = null;
        Collection matchedNodes = null;

        //check if children were discarded or to be lazy loaded, if yes then use helper to reload
        //No need to search for first level as they cannot be discarded
        Object childMapObj = null;
        if(levelIndex > 0 && (childMap == null || childMap.isEmpty())){
            if(criteria.isAutoPopulate()){
            	boolean lockObtained =true;        
                
                try {
        			lockObtained = node.lock(searchTimeOutInMillisec);		
                    if (lockObtained) {
                    	 childMap = node.getChildMap();
                         if(childMap == null || childMap.isEmpty()){
                             childMapObj = this.createMap(level);
                             node.setChildMap(childMapObj);

                             if(!node.isTerminal()){
                                 if(level.isCreateLevelKeysOnly()){
                                     this.createNodesAtLevel(level, node);
                                 }
                                 else{
                                     _internalHelper.populate(this, node, level);
                                 }
                             }
                         }
                    }
                    else{
                    	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                    }
                }
                catch (InterruptedException e) {
                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
        		}
                finally {
                	if (lockObtained) {
                		node.unlock();
                	}
                }
            	/*synchronized(node){
                    childMap = node.getChildMap();
                    if(childMap == null || childMap.isEmpty()){
                        childMapObj = this.createMap(level);
                        node.setChildMap(childMapObj);

                        if(!node.isTerminal()){
                            if(level.isCreateLevelKeysOnly()){
                                this.createNodesAtLevel(level, node);
                            }
                            else{
                                _internalHelper.populate(this, node, level);
                            }
                        }
                    }
                }*/
            }
            else return;
        }

        if(searchKeys == null){
            if(criteria.isInclusiveSearch(levelIndex)){
                //if no search keys at this level and inclusive search for this
                //level is true then include all children at this level

                //new collection is required to avoid cases where the level is discardable
                //and to avoid concurrency exception when values are GCed.
                //or if the key have changed in dynamicload scenario
               
            	boolean lockObtained =true;        
                
                try {
        			lockObtained = node.lock(searchTimeOutInMillisec);		
                    if (lockObtained) {	
                    	 matchedNodes = new ArrayList(node.values());
                    }
                    else{
                    	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                    }
                }
                catch (InterruptedException e) {
                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
        		}
                finally {
                	if (lockObtained) {
                		node.unlock();
                	}
                }
            	/*synchronized(node){
                    matchedNodes = new ArrayList(node.values());
                }*/ //end sync

                if(!ListUtl.isEmpty(matchedNodes)){
                    for(Iterator iter = matchedNodes.iterator(); iter.hasNext();){
                        childNode = (AOkudTreeNode)iter.next();
                        searchLevel(levelIndex + 1, childNode, criteria, resultNodes);
                    }
                }
            } //inclusive search = true
            else{ //only match wildcard if set
                //match level's wild card if set
                if(level.hasWildcard()){
                    childNode = (AOkudTreeNode)node.get(level.getWildcardKey());
                    if(childNode != null)searchLevel(levelIndex + 1, childNode, criteria, resultNodes);
                }
            }
        }
        else{
            //always do equal key search first (range or otherwise)
            OkudTreeSearch.ASearchKeys ask = (OkudTreeSearch.ASearchKeys)searchKeys;
            //match level's wild card if set
            if(level.hasWildcard()){
                childNode = (AOkudTreeNode)node.get(level.getWildcardKey());
                if(childNode != null)searchLevel(levelIndex + 1, childNode, criteria, resultNodes);
            }
            if(ask.getSearchKeys() != null){ //this could be null for range search
                for(Iterator iter = ask.getSearchKeys().iterator(); iter.hasNext();){
                    key = iter.next();
                    if(!level.isMultiMatch()){
                        childNode = (AOkudTreeNode)node.get(key);
                        if(childNode != null)searchLevel(levelIndex + 1, childNode, criteria, resultNodes);
                    }
                    else{
                        matchedNodes = node.getAll(key);
                        if(!ListUtl.isEmpty(matchedNodes)){
                            for(Iterator nodeiter = matchedNodes.iterator(); nodeiter.hasNext();){
                                childNode = (AOkudTreeNode)nodeiter.next();
                                searchLevel(levelIndex + 1, childNode, criteria, resultNodes);
                            }
                        }
                    }
                }
            }

            //do the range search
            if(searchKeys.getSearchType() == OkudTreeSearch._SEARCH_RANGE){
                //range search is only valid for range searchable level
                if(!level.isRangeSearchable()){
                    throw new TreeException("OkudTree.searchLevel",
                            "Range search not allowed for non range searchable level - " + level);
                }

                OkudTreeSearch.RangeSearchKeys rsk = (OkudTreeSearch.RangeSearchKeys)searchKeys;
                SortedMap resultMap = null;

                if (rsk.getMinSearchKey() != null)
                    matchedNodes = getRangeMatch(node, rsk.getMinSearchKey(), true);
                else
                    matchedNodes = getRangeMatch(node, rsk.getMaxSearchKey(), false);


              /*  if(rsk.getMinSearchKey() != null){ //match equal or greater than min
                    if(rsk.getMaxSearchKey() != null){ //match less than max
                        synchronized(node){
                            resultMap = node.subMap(rsk.getMinSearchKey(), rsk.getMaxSearchKey());
                            if(resultMap != null && !resultMap.isEmpty())matchedNodes = new ArrayList(resultMap.values());
                        }
                    }
                    else{
                        synchronized(node){
                            resultMap = node.tailMap(rsk.getMinSearchKey());
                            if(resultMap != null && !resultMap.isEmpty())matchedNodes = new ArrayList(resultMap.values());
                        }
                    }
                }
                else if(rsk.getMaxSearchKey() != null){ //max is specified, match less than
                    synchronized(node){
                        resultMap = node.headMap(rsk.getMaxSearchKey());
                        if(resultMap != null && !resultMap.isEmpty())matchedNodes = new ArrayList(resultMap.values());
                    }
                }*/


				for (Iterator iter = matchedNodes.iterator(); iter.hasNext();) {
					childNode = (AOkudTreeNode) iter.next();
					searchLevel(levelIndex + 1, childNode, criteria,
							resultNodes);
				}
			} // else range search
		} // else search for keys specified
	}

    /**
     * Performs breadth first search using IExtendedOkudTreeHelper. Called recursively
     *
     * @param levelIndex
     * @param criteria
     * @param foundNodes
     * @param helper
     * @return
     */
    protected Set<AOkudTreeNode> searchLevelBreadthFirst(int levelIndex, OkudTreeSearch criteria, Set<AOkudTreeNode> foundNodes, IExtendedOkudTreeHelper helper)
    {
        //if the last level is reached then add leaf nodes to the list
        if(levelIndex == _levels.length){
            if(criteria.isAutoPopulate()){
                //look for leafnode where the data is null
                Set<AOkudTreeNode> leafNodes = new HashSet<AOkudTreeNode>();
                Set<AOkudTreeNode> otherLeafNodes = new HashSet<AOkudTreeNode>();
                OkudTreeLeafNode lnode;
                for(AOkudTreeNode anode : foundNodes){
                    lnode = (OkudTreeLeafNode)anode;
                    //a sync on lnode is good for below block - @TBD
                    if(!lnode.isTerminal() && lnode.getLeafNodeData() == null){
                        if(_cachedEmptyNodes.add(lnode)){
                            leafNodes.add(lnode);
                        }
                        else{
                            otherLeafNodes.add(lnode);
                        }
                    }
                }

                if(!ListUtl.isEmpty(leafNodes)){
                    helper.populate(this, leafNodes, _levels[levelIndex - 1]);
                    //removed loaded nodes from cached empty nodes
                    for(AOkudTreeNode anode : leafNodes)_cachedEmptyNodes.remove(anode);

                    //if the tree is using external cache, do the search again if leafnodes have no data
                    //this can happen when the # of elements in memory is less than required to hold search results
                    if(this.isUseExternalCache()){
                        for(AOkudTreeNode anode : foundNodes){
                            lnode = (OkudTreeLeafNode)anode;
                            //just check for one result leafnode
                            if(lnode.getLeafNodeData() == null && _cachedEmptyNodes.add(lnode)){
                                try{
                                    OkudTreeSearch clonedSearch = (OkudTreeSearch)criteria.clone();
                                    clonedSearch.setAutoPopulate(false);
                                    return this.search(criteria);
                                }
                                catch(Exception e){
                                    throw new TreeException(e.getMessage());
                                }
                            }
                        }
                    }
                }

                if(!otherLeafNodes.isEmpty())this.waitForPopulate(criteria, otherLeafNodes);
            }

            return foundNodes;
        }

        Set<AOkudTreeNode> emptyNodes = new HashSet<AOkudTreeNode>(); //has elements that are not already in _cachedEmptyNodes
        Set<AOkudTreeNode> otherEmptyNodes = new HashSet<AOkudTreeNode>(); //has elements including that are already in _cachedEmptyNodes
        OkudTreeLevel level = _levels[levelIndex];
        Set<AOkudTreeNode> resultNodes = new HashSet<AOkudTreeNode>();
        OkudTreeSearch.ASearchKeys searchKeys = criteria.getSearchKeys(levelIndex);

        //loop through result nodes to collect all the nodes that matched and need populating
        for(AOkudTreeNode anode : foundNodes){
           
        	boolean lockObtained =true;        
            
            try {
    			lockObtained = anode.lock(searchTimeOutInMillisec);		
                if (lockObtained) {	
                	 OkudTreeNode node = (OkudTreeNode)anode;
                     Map childMap = null;
                     LinkedListNode llnode = null;

                     if(level.isLruCacheNeeded()){
                         llnode = node.getChildMapLinkedListNode();
                         if(llnode != null){
                             childMap = node.getChildMap();
                             node.setChildMap(this.revalueLruData(level, llnode));
                         }
                     }
                     else{
                         childMap = node.getChildMap();
                     }

                     //check if children were discarded or to be lazy loaded, if yes then use helper to reload
                     //No need to search for first level as they cannot be discarded
                     Object childMapObj = null;
                     if(levelIndex > 0 && (childMap == null || childMap.isEmpty())){
                         if(criteria.isAutoPopulate()){
                             childMapObj = this.createMap(level);
                             node.setChildMap(childMapObj);
                             if(!node.isTerminal()){
                                 if(_cachedEmptyNodes.add(node)){
                                     emptyNodes.add(node);
                                 }
                                 else{
                                     otherEmptyNodes.add(node);
                                 }
                             }
                         }
                     }
                }
                else{
                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                }
            }
            catch (InterruptedException e) {
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
    		}
            finally {
            	if (lockObtained) {
            		anode.unlock();
            	}
            }
        	/*synchronized(anode){
                OkudTreeNode node = (OkudTreeNode)anode;
                Map childMap = null;
                LinkedListNode llnode = null;

                if(level.isLruCacheNeeded()){
                    llnode = node.getChildMapLinkedListNode();
                    if(llnode != null){
                        childMap = node.getChildMap();
                        node.setChildMap(this.revalueLruData(level, llnode));
                    }
                }
                else{
                    childMap = node.getChildMap();
                }

                //check if children were discarded or to be lazy loaded, if yes then use helper to reload
                //No need to search for first level as they cannot be discarded
                Object childMapObj = null;
                if(levelIndex > 0 && (childMap == null || childMap.isEmpty())){
                    if(criteria.isAutoPopulate()){
                        childMapObj = this.createMap(level);
                        node.setChildMap(childMapObj);
                        if(!node.isTerminal()){
                            if(_cachedEmptyNodes.add(node)){
                                emptyNodes.add(node);
                            }
                            else{
                                otherEmptyNodes.add(node);
                            }
                        }
                    }
                }
            }*/
        } //for each result node

        if(!ListUtl.isEmpty(emptyNodes)){
            if(level.isCreateLevelKeysOnly()){
                helper.populateLevelKeys(this, emptyNodes, level);
            }
            else{
                helper.populate(this, emptyNodes, level);
            }

            //removed loaded nodes from cached empty nodes
            for(AOkudTreeNode eNode : emptyNodes)_cachedEmptyNodes.remove(eNode);

        }

        //have to wait for nodes that are being populated by any other thread
        if(!otherEmptyNodes.isEmpty())waitForPopulate(criteria, otherEmptyNodes);

        for(AOkudTreeNode anode : foundNodes){
            OkudTreeNode node = (OkudTreeNode)anode;
            if(node.isTerminal())continue; //as we may have populated this node in the above block, check for terminal node
            AOkudTreeNode childNode = null;
            Object key = null;
            Collection matchedNodes = null;
            if(criteria.isAutoPopulate())this.waitForPopulate(criteria, node);
            if(searchKeys == null){
                if(criteria.isInclusiveSearch(levelIndex)){
                    //if no search keys at this level and inclusive search for this
                    //level is true then include all children at this level

                    //new collection is required to avoid cases where the level is discardable
                    //and to avoid concurrency exception when values are GCed.
                    //or if the key have changed in dynamicload scenario
                	  boolean lockObtained =true;        
                      
                      try {
              			lockObtained = node.lock(searchTimeOutInMillisec);		
                          if (lockObtained) {
                        	  resultNodes.addAll(node.values());
                          }
                          else{
                          	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                          }
                      }
                      catch (InterruptedException e) {
                      	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
              		  }
                      finally {
                    	  if (lockObtained) {
                    		  node.unlock();
                    	  }
                      }
                	/*synchronized(node){
                        resultNodes.addAll(node.values());
                    } *///end sync
                	
                } //inclusive search = true
                else{ //only match wildcard if set
                    //match level's wild card if set
                    if(level.hasWildcard()){
                        childNode = (AOkudTreeNode)node.get(level.getWildcardKey());
                        if(childNode != null)resultNodes.add(childNode);
                    }
                }
            }
            else{
                //always do equal key search first (range or otherwise)
                OkudTreeSearch.ASearchKeys ask = (OkudTreeSearch.ASearchKeys)searchKeys;
                //match level's wild card if set
                if(level.hasWildcard()){
                    childNode = (AOkudTreeNode)node.get(level.getWildcardKey());
                    if(childNode != null)resultNodes.add(childNode);
                }
                if(ask.getSearchKeys() != null){ //this could be null for range search
                    for(Iterator iter = ask.getSearchKeys().iterator(); iter.hasNext();){
                        key = iter.next();
                        if(!level.isMultiMatch()){
                            childNode = (AOkudTreeNode)node.get(key);
                            if(childNode != null)resultNodes.add(childNode);
                        }
                        else{
                            matchedNodes = node.getAll(key);
                            if(!ListUtl.isEmpty(matchedNodes)){
                                resultNodes.addAll(matchedNodes);
                            }
                        }
                    }
                }

                //do the range search
                if(searchKeys.getSearchType() == OkudTreeSearch._SEARCH_RANGE){
                    //range search is only valid for range searchable level
                    if(!level.isRangeSearchable()){
                        throw new TreeException("OkudTree.searchLevel",
                                "Range search not allowed for non range searchable level - " + level);
                    }

                    OkudTreeSearch.RangeSearchKeys rsk = (OkudTreeSearch.RangeSearchKeys)searchKeys;
                    SortedMap resultMap = null;

                    if(rsk.getMinSearchKey() != null){ //match equal or greater than min
                        if(rsk.getMaxSearchKey() != null){ //match less than max
                        	boolean lockObtained =true;        
                            
                            try {
                    			lockObtained = node.lock(searchTimeOutInMillisec);		
                                if (lockObtained) {
                                	 resultMap = node.subMap(rsk.getMinSearchKey(), rsk.getMaxSearchKey());
                                     if(resultMap != null && !resultMap.isEmpty())resultNodes.addAll(resultMap.values());
                                }
                                else{
                                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                                }
                            }
                            catch (InterruptedException e) {
                            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                    		}
                            finally {
                            	if (lockObtained) {
                            		node.unlock();
                            	}
                            }
                        	/*synchronized(node){
                                resultMap = node.subMap(rsk.getMinSearchKey(), rsk.getMaxSearchKey());
                                if(resultMap != null && !resultMap.isEmpty())resultNodes.addAll(resultMap.values());
                            }*/
                        }
                        else{
                        	boolean lockObtained =true;        
                            
                            try {
                    			lockObtained = node.lock(searchTimeOutInMillisec);
                    			if (lockObtained) {
                    				 resultMap = node.tailMap(rsk.getMinSearchKey());
                                     if(resultMap != null && !resultMap.isEmpty())resultNodes.addAll(resultMap.values());//matchedNodes = new ArrayList(resultMap.values());
                    			}
                    			else{
                                	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                                }
                            }
                            catch (InterruptedException e) {
                            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                    		}
                            finally {
                            	if (lockObtained) {
                            		node.unlock();
                            	}
                            }
                            /*synchronized(node){
                                resultMap = node.tailMap(rsk.getMinSearchKey());
                                if(resultMap != null && !resultMap.isEmpty())resultNodes.addAll(resultMap.values());//matchedNodes = new ArrayList(resultMap.values());
                            }*/
                        }
                    }
                    else if(rsk.getMaxSearchKey() != null){ //max is specified, match less than
                    	boolean lockObtained =true;        
                        
                        try {
                			lockObtained = node.lock(searchTimeOutInMillisec);
                			if (lockObtained) {
                				resultMap = node.headMap(rsk.getMaxSearchKey());
                                if(resultMap != null && !resultMap.isEmpty())resultNodes.addAll(resultMap.values());//matchedNodes = new ArrayList(resultMap.values());
                			}
                			else{
                            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                            }
                        }
                        catch (InterruptedException e) {
                        	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for Search. Please Retry request.");
                		}
                        finally {
                        	if (lockObtained) {
                        		node.unlock();
                        	}
                        }
                    	/*synchronized(node){
                            resultMap = node.headMap(rsk.getMaxSearchKey());
                            if(resultMap != null && !resultMap.isEmpty())resultNodes.addAll(resultMap.values());//matchedNodes = new ArrayList(resultMap.values());
                        }*/
                    }

                } //else range search
            } //else search for keys specified
        } //end for each result node

        if(!ListUtl.isEmpty(resultNodes)){
            return this.searchLevelBreadthFirst(levelIndex + 1, criteria, resultNodes, helper);
        }
        else return resultNodes;
    }

    /**
     * Wait method used in breadth first search when other threads are loading some of the result nodes
     * @param emptyNodes
     * @param allEmptyNodes
     */
    private void waitForPopulate(OkudTreeSearch criteria, Set<AOkudTreeNode> allEmptyNodes)
    {
        Set<AOkudTreeNode> emptyNodes = new HashSet<AOkudTreeNode>();

        long loopCount = criteria.getWaitForPopulateMillSeconds() / 30; //each loop is is 30 ms for a total of ~10 seconds
        while(!allEmptyNodes.isEmpty() && loopCount > 0){
            loopCount--;
            for(AOkudTreeNode eNode : allEmptyNodes){
                if(!_cachedEmptyNodes.contains(eNode)){ //if the empty node is already processed by other threads, remove it
                    emptyNodes.add(eNode);
                }
            }
            allEmptyNodes.removeAll(emptyNodes);
            if(!allEmptyNodes.isEmpty())try{Thread.currentThread().sleep(10);}catch(Exception e){} //no loss of
        }
        if(!allEmptyNodes.isEmpty())_logger.warn("Wait time exceeded while waiting for other threads in searchLevelBreadthFirst");
    }

    /**
     * Wait for a single node to be populated with a timeout of ~10 sec
     *
     * @param anode
     */
    private void waitForPopulate(OkudTreeSearch criteria, AOkudTreeNode anode)
    {
        if(!_cachedEmptyNodes.contains(anode))return;

        long loopCount = criteria.getWaitForPopulateMillSeconds() / 30; //each loop is is 10 ms for a total of ~10 seconds
        while(loopCount > 0){
            loopCount--;
            if(!_cachedEmptyNodes.contains(anode))return;
            else{
                try{Thread.currentThread().sleep(10);}catch(Exception e){} //no loss of
            }
        }
        if(_cachedEmptyNodes.contains(anode))_logger.warn("Wait time exceeded while waiting for other threads in searchLevelBreadthFirst loading node");
    }


    /**
     * Standard toString() method, which does not include output for tree's leaf nodes
     */
    @Override
    public String toString()
    {
        return toString( false);
    }

    /**
     * Auxiliary toString(boolean) method, which can include output for tree's leaf nodes.
     */
    public String toString(boolean includeLeafNodes)
    {
        String lineSep = System.getProperty( "line.separator");
        StringBuffer sbf = new StringBuffer(lineSep).append("OkudTree[ ");

        sbf.append("levelNames=").append( Arrays.asList(this._levels) ).append("; ");
        sbf.append(lineSep);
        sbf.append("enforceSerializable=").append( _enforceSerializable ).append("; ");
        if(_enforcedApplicationDataObjectInstance != null)sbf.append("enforcedApplicationDataObjectInstance class=").append( _enforcedApplicationDataObjectInstance.getClass().getName() ).append("; ");
        sbf.append("lazyLoad=").append(_lazyLoad).append("; ");
        if (includeLeafNodes){
            printNode(sbf, _root, 0);
        }

        sbf.append(lineSep).append("]OkudTree");
        return sbf.toString() ;
    }

    protected void printNode(StringBuffer sbf, AOkudTreeNode node, int indent)
    {
        final String lineSep = System.getProperty("line.separator");
        sbf.append(lineSep);
        for(int i = 0; i < indent; i++)sbf.append("  ");
        if(indent > 0)sbf.append(this.getLevel(indent - 1).getName()).append(", ");
        indent++;
        sbf.append(node);
        if(node instanceof OkudTreeNode){
            OkudTreeNode bnode = (OkudTreeNode)node;
            Collection values = bnode.values();
            if(values != null){
                for(Iterator iter = values.iterator(); iter.hasNext();){
                    printNode(sbf, (AOkudTreeNode)iter.next(), indent);
                }
            }
        }
    }

    /**
     * This method can be called to clear the key cache once the
     * tree is fully built to realize any memory used by the cache object itself.
     * This will not of course free the keys.
     * Do not call this method if the tree is to be lazy loaded or if any level is
     * discardable. That will cause the memory foot print of the tree to be larger
     * as keys cannot be reused.
     */
    public void clearKeyCache()
    {
        if(_keyCache != null)_keyCache.clearCache();
    }

    /**
     * Creates a new OkudTreeSearch object and returns it
     */
    public OkudTreeSearch createSearch()
    {
        return new OkudTreeSearch(this._levels);
    }

    /**
     * Retrieve and return a key from the key cache if available
     * else returns the same key object that is passed in
     * Use this method to reduce the memory footprint
     */
    public Object getKeyFromKeyCache(String levelType, Object key)
    {
        return _useKeyCache ? _keyCache.getKey(levelType, key) : key;
    }

    /**
     * move recently used node to top of the lru cache
     */
    protected LinkedListNode revalueLruData(OkudTreeLevel level, LinkedListNode node)
    {
        AVLinkedList list = _levelLruCache[level.getLevelIndex()];
        LruDataObj data = (LruDataObj)node.getValue();
        
        boolean lockObtained =true;        
        
        try {
			lockObtained = list.lock(searchTimeOutInMillisec);		
            if (lockObtained) {
            	 if(data.isInLruList())list.moveToFirst(node);
                 else{
                     return addToLruCache(level, (Map)data.getData());
                 }
            }
            else{
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for reval LRU. Please Retry request.");
            }
        }
        catch (InterruptedException e) {
        	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for reval LRU. Please Retry request.");
		}
        finally {
        	if (lockObtained) {
        		list.unlock();
        	}
        }
        /*synchronized(list){
            if(data.isInLruList())list.moveToFirst(node);
            else{
                return addToLruCache(level, (Map)data.getData());
            }
        }*///end sync

        return node;
    }

    /**
     * Add to new data to lru cache
     */
    protected LinkedListNode addToLruCache(OkudTreeLevel level, Map map)
    {
        AVLinkedList list = _levelLruCache[level.getLevelIndex()];
        LruDataObj data = new LruDataObj(map, true);
        LinkedListNode node = null;
        boolean dequeued = false;
        
        boolean lockObtained =true;        
        
        try {
			lockObtained = list.lock(searchTimeOutInMillisec);		
            if (lockObtained) {
            	node = list.addFirst(data);
                if(list.size() > level.getMaxNodesAtLevel()){
                    data = (LruDataObj)list.dequeue();
                    data.setInLruList(false);
                    dequeued = true;
                }
            }
            else{
            	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for add LRU. Please Retry request.");
            }
        }
        catch (InterruptedException e) {
        	throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for add LRU. Please Retry request.");
		}
        finally {
        	if (lockObtained) {
        		list.unlock();
        	}
        }
       /* synchronized(list){
            node = list.addFirst(data);
            if(list.size() > level.getMaxNodesAtLevel()){
                data = (LruDataObj)list.dequeue();
                data.setInLruList(false);
                dequeued = true;
            }
        }*///end sync
        //keep logging outside sync block
        if(dequeued && _logger.isTraceEnabled())_logger.trace("Lru node dequeued at level " + level.getName());

        return node;
    }

    /**
     * @return the _topLevelIncrementalLoad
     */
    public boolean isTopLevelIncrementalLoad() {
        return _topLevelIncrementalLoad;
    }

    /**
     * @param topLevelIncrementalLoad the _topLevelIncrementalLoad to set
     */
    public void setTopLevelIncrementalLoad(boolean topLevelIncrementalLoad) {
        this._topLevelIncrementalLoad = topLevelIncrementalLoad;
    }

    /**
     * @return the _useExternalCache
     */
    public boolean isUseExternalCache() {
        return _useExternalCache;
    }

    public String getExternalCacheName() {
        return _externalCacheName;
    }

    /**
     * @return the _externalCacheUtility
     */
    public IExternalCache getExternalCacheUtility() {
        return _externalCacheUtility;
    }

    /**
     * @param externalCacheUtility the _externalCacheUtility to set
     */
    public void setExternalCacheUtility(IExternalCache externalCacheUtility) {
        this._externalCacheUtility = externalCacheUtility;
    }

    /**
     * @return the _maxConcurrentPopulates
     */
    public int getMaxConcurrentPopulates() {
        return _maxConcurrentPopulates;
    }

    /**
     * @param maxConcurrentPopulates the _maxConcurrentPopulates to set
     */
    public void setMaxConcurrentPopulates(int maxConcurrentPopulates) {
        if(maxConcurrentPopulates <= 0)maxConcurrentPopulates = Integer.MAX_VALUE/2;
        this._maxConcurrentPopulates = maxConcurrentPopulates;
        _concurrentCounter = new ConcurrentCounter(maxConcurrentPopulates);
        ((InternalHelper)_internalHelper).setCounter(_concurrentCounter);
    }

    protected static class LruDataObj{
        private boolean _inLruList;
        private Object _data;
        LruDataObj(Object data, boolean inLruList){
            setInLruList(inLruList);
            setData(data);
        }

        public String toString(){
            return "[" + isInLruList() + ", " + getData() + "]";
        }

        public boolean isInLruList() {
            return _inLruList;
        }

        public void setInLruList(boolean inLruList) {
            this._inLruList = inLruList;
        }

        public Object getData() {
            return _data;
        }

        public void setData(Object data) {
            this._data = data;
        }
    }

    protected static class ConcurrentCounter{
        private final Semaphore _counter;

        public ConcurrentCounter(int maxConcurrent){
            _counter = new Semaphore(maxConcurrent, true);
        }

        public boolean acquire(){
            return _counter.tryAcquire();
        }

        public void release(){
            _counter.release();
        }
    }

    protected static class InternalHelper implements IOkudTreeHelper{
        private final IOkudTreeHelper _okudHelper;
        protected ConcurrentCounter _counter;
        protected final String _resourceException = "Max calls to the tree helper exceeded, please try again.";

        public InternalHelper(IOkudTreeHelper extHelper){
            _okudHelper = extHelper;
        }

        public void addDataToLeafNode(OkudTreeLeafNode leafNode, Object applicationDataObject) {
            _okudHelper.addDataToLeafNode(leafNode, applicationDataObject);
        }

        public void onCreateNode(OkudTree tree, AOkudTreeNode node, OkudTreeLevel level) {
            _okudHelper.onCreateNode(tree, node, level);
        }

        public void populate(OkudTree tree, OkudTreeNode node, OkudTreeLevel level) {
            if(!_counter.acquire())throw new TreeException(_resourceException);
            try{
                _okudHelper.populate(tree, node, level);
            }
            finally{
                _counter.release();
            }
        }

        public List getTopLevelKeys(OkudTreeLevel level) {
            //no need to check counter here
            return _okudHelper.getTopLevelKeys(level);
        }

        public List getLevelKeys(OkudTree tree, OkudTreeNode node, OkudTreeLevel level) {
            if(!_counter.acquire())throw new TreeException(_resourceException);
            try{
                return _okudHelper.getLevelKeys(tree, node, level);
            }
            finally{
                _counter.release();
            }
        }

        /**
         * @return the _okudHelper
         */
        public IOkudTreeHelper getOkudHelper() {
            return _okudHelper;
        }

        /**
         * @return the _counter
         */
        public ConcurrentCounter getCounter() {
            return _counter;
        }

        /**
         * @param counter the _counter to set
         */
        public void setCounter(ConcurrentCounter counter) {
            this._counter = counter;
        }


    }

    protected static class InternalExtendedHelper extends InternalHelper implements IExtendedOkudTreeHelper{
        private IExtendedOkudTreeHelper _extHelper;

        public InternalExtendedHelper(IOkudTreeHelper extHelper){
            super(extHelper);
            if(extHelper instanceof IExtendedOkudTreeHelper)_extHelper = (IExtendedOkudTreeHelper)extHelper;
        }

        public InternalExtendedHelper(IOkudTreeHelper extHelper, ConcurrentCounter counter){
            this(extHelper);
            _counter = counter;
        }

        public OkudTreeLevel[] makeLevels() {
            if(_extHelper != null){
                return ((IExtendedOkudTreeHelper)getExtHelper()).makeLevels();
            }
            return null;
        }

        public OkudTree createTree() {
            if(_extHelper != null){
                return ((IExtendedOkudTreeHelper)getExtHelper()).createTree();
            }
            return null;
        }

        /**
         * @return the _extHelper
         */
        public IExtendedOkudTreeHelper getExtHelper() {
            return _extHelper;
        }

        public void populateLevelKeys(OkudTree tree, Collection<AOkudTreeNode> nodes, OkudTreeLevel level) {
            if(_extHelper == null)return;
            if(!_counter.acquire())throw new TreeException(_resourceException);
            try{
                _extHelper.populateLevelKeys(tree, nodes, level);
            }
            finally{
                _counter.release();
            }
        }

        public void populate(OkudTree tree, Collection<AOkudTreeNode> nodes, OkudTreeLevel level) {
            if(_extHelper == null)return;
            if(!_counter.acquire())throw new TreeException(_resourceException);
            try{
                _extHelper.populate(tree, nodes, level);
            }
            finally{
                _counter.release();
            }
        }

        public void populateLeafNode(OkudTree tree, OkudTreeLeafNode node, OkudTreeLevel level) {
            if(_extHelper == null)return;
            if(!_counter.acquire())throw new TreeException(_resourceException);
            try{
                _extHelper.populateLeafNode(tree, node, level);
            }
            finally{
                _counter.release();
            }
        }

        public List<Object[]> makeKeys(IOkudTreeDTO dto) {
            if(_extHelper != null){
                return ((IExtendedOkudTreeHelper)getExtHelper()).makeKeys(dto);
            }
            return null;
        }
    }

	private Collection<AOkudTreeNode> getRangeMatch(AOkudTreeNode node,
			Comparable key, boolean isMinKey) {
		Collection<AOkudTreeNode> matchedNodes = new ArrayList<AOkudTreeNode>();
		Map cmap = ((OkudTreeNode) node).getChildMap();
		Map.Entry entry;
		for (Iterator iter = cmap.entrySet().iterator(); iter.hasNext();) {
			entry = (Map.Entry) iter.next();
			Comparable treeKey = (Comparable) entry.getKey();
			if (isMinKey) {
				if (treeKey.compareTo(key) <= 0) {
					matchedNodes.add((AOkudTreeNode) entry.getValue());
				}
			} else if (treeKey.compareTo(key) >= 0) {
				matchedNodes.add((AOkudTreeNode) entry.getValue());
			}
		}
		return matchedNodes;
	}
}
