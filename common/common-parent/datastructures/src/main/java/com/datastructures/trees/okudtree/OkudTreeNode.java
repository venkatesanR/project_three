/*
 * OkudTreeNode.java
 *
 * Created on May 12, 2006, 4:32 PM
 *
 */

package com.datastructures.trees.okudtree;

import com.techmania.common.exceptions.AFLSCacheException;
import com.techmania.common.util.collectionutil.LinkedListNode;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * This class represents all nodes of an OkudTree except leaf nodes.
 *
 * @author ravi
 */
public class OkudTreeNode extends AOkudTreeNode {
    /**
     * The sub tree of this node with this node as it's parent
     */
    protected Object _childMap;

    /**
     * Indicates if the sub tree of this node is discardable. When
     * discardable is true then the sub tree is held as a soft reference.
     */
    protected boolean _childDiscardable;

    /**
     * Indicates whether LRU cache is used
     */
    protected boolean _lruCacheUsed;

    /**
     * Tree level for this node
     */
    protected transient OkudTreeLevel _level;

    protected int _levelIndex = -1;

    /**
     * flag indicating usage of external cache set through the level info
     */
    protected boolean _useExternalCache = false;

    private long searchTimeOutInMillisec = 30000L; //search 30s


    /**
     * Creates a new instance of OkudTreeNode
     *
     * @param parent           - parent node of this node, null for root
     * @param key              - key identifying this node, null for root
     * @param childMap         - Sub tree from this node (of type HashMap or TreeMap or LinkedListNode)
     * @param childDiscardable - indicates whether the sub tree can be discarded
     * @param lruCacheUsed     - indicates lru cache used for child map
     */
    public OkudTreeNode(OkudTreeNode parent, Object key, Object childMap, boolean childDiscardable,
                        boolean lruCacheUsed) {
        super(parent, key);
        _childDiscardable = childDiscardable;
        _lruCacheUsed = lruCacheUsed;
        if (_childDiscardable) {
            _childMap = new SoftReference(childMap);
        } else {
            _childMap = childMap;
        }

        _terminal = false;

        if (parent == null) {
            _levelIndex = 0;
        } else {
            _levelIndex = parent.getLevelIndex() + 1;
        }
    }

    public OkudTreeNode(OkudTreeNode parent, Object key, Object childMap, OkudTree tree, int levelIndex) {
        super(parent, key, tree);

        _levelIndex = levelIndex;
        _level = tree.getLevel(_levelIndex);

        if (tree.isUseExternalCache() && _level.isUseExternalCache()) {
            _useExternalCache = true;
            this.makeExternalCacheKey();
            //if external cache is used and persistence is enabled then we don't want to override it
            if (this.getChildMap() == null) {
                this.addToExternalCache(childMap);
            }
        } else if (_level.isDiscardable()) {
            _childDiscardable = true;
            _lruCacheUsed = _level.isDiscardLRU();
            _childMap = new SoftReference(childMap);
        } else {
            _childMap = childMap;
        }

        _terminal = false;

    }

    public long getSearchTimeOutInMillisec() {
        return searchTimeOutInMillisec;
    }

    public void setSearchTimeOutInMillisec(long searchTimeOutInMillisec) {
        this.searchTimeOutInMillisec = searchTimeOutInMillisec;
    }

    @Override
    public String toString() {
        return "Node[{" + _key + "}, " + _score + "]";
    }

    public final Map getChildMap() {
        Map<Object, AOkudTreeNode> resultMap;

        if (_useExternalCache) {
            resultMap =
                    (Map<Object, AOkudTreeNode>) getTree().getExternalCacheUtility().get(getTree().getExternalCacheName(), this.getExternalCacheKey());
        } else if (_childDiscardable) {
            Object obj = ((SoftReference) _childMap).get();
            if (obj == null) {
                return null;
            }
            if (_lruCacheUsed) {
                resultMap =
                        (Map<Object, AOkudTreeNode>) ((OkudTree.LruDataObj) ((LinkedListNode) obj).getValue()).getData();
            } else {
                resultMap = (Map<Object, AOkudTreeNode>) obj;
            }
        } else {
            resultMap = (Map<Object, AOkudTreeNode>) _childMap;
        }

        return this.setParentNode(resultMap);
    }

    public LinkedListNode getChildMapLinkedListNode() {
        if (_childDiscardable && _lruCacheUsed) {
            Object obj = ((SoftReference) _childMap).get();
            return (LinkedListNode) obj;
        }

        return null;
    }


    void setChildMap(Object childMap) {
        if (childMap == null) {
            _childMap = null;
        } else if (_useExternalCache) {
            this.addToExternalCache(childMap);
        } else if (_childDiscardable) {
            _childMap = new SoftReference(childMap);
        } else {
            _childMap = childMap;
        }
    }

    /**
     * Match key to branches of this node and return matched node or return null.
     *
     * @param key - key to match
     * @return matched sub node
     */
    public Object get(Object key) {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        return cmap.get(key);
    }

    /**
     * removes specific entry from the childMap if found
     *
     * @param key
     * @return
     */
    public Object remove(Object key) {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        boolean contains = cmap.containsKey(key);
        Object ret = null;
        if (contains) {
            ret = cmap.remove(key);
        }

        //have to put it back in external cache
        if (contains) {
            this.addToExternalCache(cmap);
        }

        return ret;
    }


    /**
     * Instead of depending on the underlying map to return a single match
     * this method will iterate through all the entries in the map and return
     * all matched values. May return null.
     *
     * @param key - key to match
     * @return matched sub node list
     */
    public List getAll(Object key) {
        Map cmap = getChildMap();
        if (cmap == null || cmap.isEmpty()) {
            return null;
        }

        List<AOkudTreeNode> matchedValues = new ArrayList<AOkudTreeNode>();
        Map.Entry entry;
        for (Iterator iter = cmap.entrySet().iterator(); iter.hasNext(); ) {
            entry = (Map.Entry) iter.next();
            if (entry.getKey().equals(key)) {
                matchedValues.add((AOkudTreeNode) entry.getValue());
            }
        }

        return matchedValues;
    }

    /**
     * Adds or replaces a branch with the value passed for the key
     *
     * @param key   - key for the new sub branch
     * @param value - New sub branch
     * @return returns returns previous sub branch if found or null
     */
    public synchronized Object put(Object key, Object value) {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        Object retObj = cmap.put(key, value);

        //have to put it back in external cache
        this.addToExternalCache(cmap);

        return retObj;
    }

    /**
     * Returns a collection of sub branches (nodes)
     */
    public Collection values() {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        return cmap.values();
    }

    /**
     * Matches from fromKey to toKey in the sorted sub branches (nodes)
     * and return all matching sub branches (nodes) including fromKey but
     * excluding toKey.
     *
     * @param fromKey - from the range starting at fromKey to match
     * @param toKey   - to the end of the range endKey to match
     * @return SortedMap - matched sub map
     * @throws UnsupportedOperationException - if the level of this node is not range searchable
     */
    public SortedMap subMap(Object fromKey, Object toKey) {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        if (!(cmap instanceof SortedMap)) {
            throw new UnsupportedOperationException("subMap(fromKey, toKey)");
        }
        return ((SortedMap) cmap).subMap(fromKey, toKey);
    }

    /**
     * Matches to toKey in the sorted sub branches (nodes)
     * and return all matching sub branches (nodes) up to but
     * excluding toKey.
     *
     * @param toKey - to the end of the range endKey to match
     * @return SortedMap - matched sub map
     * @throws UnsupportedOperationException - if the level of this node is not range searchable
     */
    public SortedMap headMap(Object toKey) {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        if (!(cmap instanceof SortedMap)) {
            throw new UnsupportedOperationException("headMap(toKey)");
        }
        return ((SortedMap) cmap).headMap(toKey);
    }

    /**
     * Matches from fromKey in the sorted sub branches (nodes)
     * and return all matching sub branches (nodes) including fromKey
     *
     * @param fromKey - from the range starting at fromKey to match
     * @return SortedMap - matched sub map
     * @throws UnsupportedOperationException - if the level of this node is not range searchable
     */
    public SortedMap tailMap(Object fromKey) {
        Map cmap = getChildMap();
        if (cmap == null) {
            return null;
        }
        if (!(cmap instanceof SortedMap)) {
            throw new UnsupportedOperationException("tailMap(fromKey)");
        }
        return ((SortedMap) cmap).tailMap(fromKey);
    }

    /**
     * Clears child map
     */
    public void clearChildMap() {
        boolean lockObtained = true;
        try {
            lockObtained = this.lock(searchTimeOutInMillisec);
            if (lockObtained) {
                Map cmap = getChildMap();
                if (cmap != null) {
                    cmap.clear();
                    //have to put it back in external cache
                    this.addToExternalCache(cmap);
                }
            } else {
                throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for clear leaf node. Please " +
                        "Retry request.");
            }
        } catch (InterruptedException e) {
            throw new AFLSCacheException("In Okud Tree", "Timed out while waiting for clear leaf node. Please Retry " +
                    "request.");
        } finally {
            if (lockObtained) {
                this.unlock();
            }
        }
    }

    public boolean isUseExternalCache() {
        return this._useExternalCache;
    }

    /**
     * This is sort of expensive, so if you are invalidating data at child level
     * then use external cache at this level
     *
     * @param childNode
     */
    void updateExternalCache(AOkudTreeNode childNode) {
        if (_useExternalCache) {
            if (childNode != null) {
                Map cmap = getChildMap();
                if (cmap != null) {
                    cmap.put(childNode.getKey(), childNode);
                    this.addToExternalCache(cmap);
                }
            }
        }
        //but if the tree uses external cache than a higher level node must be managed thru ext cache
        //but this is somewhat expensive
        else if (this.getTree().isUseExternalCache() && this.getParent() != null) {
            OkudTreeNode parent = (OkudTreeNode) this.getParent();
            if (parent != null) {
                parent.updateExternalCache(this);
            }
        }
    }

    final void addToExternalCache(Object cmap) {
        try {
            if (_useExternalCache) {
                this.getCacheLock();
                this.getTree().getExternalCacheUtility().put(this.getTree().getExternalCacheName(),
                        this.getExternalCacheKey(), cmap);
            }
        } finally {
            this.releaseCacheLock();
        }
    }

    void getCacheLock() {
        //blocking
        if (_useExternalCache) {
            this.getTree().getExternalCacheUtility().getWriteLock(this.getTree().getExternalCacheName(),
                    this.getExternalCacheKey());
        }
    }

    void releaseCacheLock() {
        if (_useExternalCache) {
            this.getTree().getExternalCacheUtility().releaseWriteLock(this.getTree().getExternalCacheName(),
                    this.getExternalCacheKey());
        }
    }

    /**
     * @return the _levelIndex
     */
    public int getLevelIndex() {
        return _levelIndex;
    }

    /**
     * @param levelIndex the _levelIndex to set
     */
    void setLevelIndex(int levelIndex) {
        this._levelIndex = levelIndex;
    }

    public OkudTreeLevel getLevel() {
        return _level;
    }

    /**
     * Deletes self from parent node's child map
     *
     * @return self if delete is successful
     */
    public AOkudTreeNode deleteSelf() {
        OkudTreeNode pnode = (OkudTreeNode) this.getParent();
        if (pnode != null) {
            return (AOkudTreeNode) pnode.remove(_key);
        }
        return null;
    }

    private static final long serialVersionUID = 1010L;
}
