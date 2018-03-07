/*
 * AOkudTreeNode.java
 *
 * Created on May 12, 2006, 4:44 PM
 *
 */

package com.addval.trees;

import com.addval.utils.ListUtl;
import com.addval.utils.MultiHashKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An abstract representation of a node (also a leaf node) in an OkudTree.
 * @author ravi
 */
public abstract class AOkudTreeNode implements Comparable, Serializable
{
    /** Parent node, null for root */
    protected transient AOkudTreeNode _parentNode;
    
    /** Score for this node */
    protected long _score = 0;
       
    /** Key for this node in the branch */
    protected Object _key;
    
    /**
     * External cache key. This node itself is never serialized, only the child map
     */
    protected MultiHashKey _externalCacheKey;  
    
    protected transient OkudTree _tree;
    
    /**
     * This flag is used to decide whether populate/getLevelKeys is called
     * for children of this node. If true those methods will be called, if false, 
     * not called and the search along the branch terminates at this node. 
     * This flag should be set by the helper class.
     */
    protected boolean _terminal; 
	protected Lock nodeLock = null;   
    public AOkudTreeNode() 
    {
    	
    }
    public AOkudTreeNode(AOkudTreeNode parent, Object key) 
    {
        _parentNode = parent;
        _key = key;
        nodeLock = new ReentrantLock();
    }
    
    public AOkudTreeNode(AOkudTreeNode parent, Object key, OkudTree tree) 
    {
        this(parent, key);
        _tree = tree;
    }    
   
    public AOkudTreeNode getParent()
    {
        return _parentNode;
    }
            
    public Object getKey()
    {
        return _key;
    }
    
    public long getScore()
    {
        return _score;
    }
    
    public void setScore(long score)
    {
        _score = score;
    }
    
    @Override
    public String toString()
    {
        return "" + _key;
    }

    /**
     * Default sort by score with highest score at the top
     * @param other
     * @return
     */
    public int compareTo(Object other){
        return compareTo((AOkudTreeNode)other);
    }
    
    /**
     * Default sort by score with highest score at the top
     * @param other
     * @return
     */
    public int compareTo(AOkudTreeNode other){
        return com.addval.utils.MathUtl.sign(other.getScore() - this.getScore());
    }

    /**
     * Compiles an array of keys starting with level index 0 to the keys to this node
     *
     * @return Array of keys to this node with array index 0 corresponding to level index 0
     */
    public Object[] getKeys()
    {
        return this.getKeysList().toArray();
    }

    /**
     * Compiles a list of keys starting with level index 0 to the keys to this node
     *
     * @return List of keys to this node with the first element corresponding to level index 0
     */
    public List<Object> getKeysList()
    {
        ArrayList<Object> keys = new ArrayList<Object>();
        
        keys.add(_key);
        
        AOkudTreeNode node = this.getParent();
        
        while(node != null){
            if(node.getParent() != null)keys.add(node.getKey());
            node = node.getParent();
        }
        
        keys.trimToSize();
        
        Collections.reverse(keys);
        
        return keys;        
    }
    
    /**
     * 
     * @return external cache key when external cache is used for the node's level else return null
     */
    public MultiHashKey getExternalCacheKey()
    {
        return _externalCacheKey;
    }
    
    
    /**
     * This method will throw IllegalArgumentException if root note is passed
     * @param node
     * @return 
     */
    protected Object makeExternalCacheKey()
    {
        _externalCacheKey = new MultiHashKey(this.getKeysList());
        return _externalCacheKey;
    }   
    
    protected void setParent(AOkudTreeNode parentNode)
    {
        this._parentNode = parentNode;
    }
    
    /**
     * return the param obj after setting the parent to this when the object is a node
     * @param obj
     * @return 
     */
    protected Object setParentNode(Object obj)
    {
        if(obj != null && (obj instanceof AOkudTreeNode) && ((AOkudTreeNode)obj).getParent() == null){
            ((AOkudTreeNode)obj).setParent(this);
        }
            
        return obj;
    }   
    
    /**
     * Sets self as parent node in all the child nodes if not set
     * Checks one child node if parent is set and if set then assumes all children
     * have parent node set. Use this when de-serialization occurs
     * 
     * @param valueNodes
     * @return 
     */
    protected List<AOkudTreeNode> setParentNode(List<AOkudTreeNode> valueNodes)
    {
        //if one value node has parent set then all other have it set too
        //note: The values in childmap are not expected to be null
        if(ListUtl.isEmpty(valueNodes) || valueNodes.get(0).getParent() != null)return valueNodes;               
        
        for(AOkudTreeNode node : valueNodes)node.setParent(this);
        
        return valueNodes;        
    }
    
    /**
     * Sets self as parent node in all the child nodes if not set
     * Checks one child node if parent is set and if set then assumes all children
     * have parent node set. Use this when de-serialization occurs
     * 
     * @param childMap
     * @return 
     */
    protected Map<Object, AOkudTreeNode> setParentNode(Map<Object, AOkudTreeNode> childMap)
    {
        if(childMap == null || childMap.isEmpty())return childMap;
        
        //if one child has parent set then all have it set too
        Iterator<AOkudTreeNode> iter = childMap.values().iterator();
        AOkudTreeNode node = iter.next();
        if(node.getParent() != null)return childMap;
        else node.setParent(this);
        
        while(iter.hasNext()){
            iter.next().setParent(this);
        }
        
        return childMap;
    }
    
    /**
     * mutating but the tree is transient
     * @return the _tree
     */
    public OkudTree getTree() {        
        if (_tree != null)return _tree;
        else if(_parentNode != null){
            _tree = _parentNode.getTree();
        }
        
        return _tree;
    }

    /**
     * @param tree the _tree to set
     */
    public void setTree(OkudTree tree) {
        this._tree = tree;
    }     
    
    public boolean isTerminal() {
        return _terminal;
    }

    public void setTerminal(boolean terminal) {
        this._terminal = terminal;
    }
    
    /**
     * implemented in extended classed to remove a specific node from the parent's map
     */
    public abstract AOkudTreeNode deleteSelf();
    
    private static final long serialVersionUID = 1000L;  
    
    public boolean lock(long timeOutInMillis) throws InterruptedException {
        return nodeLock.tryLock(timeOutInMillis, TimeUnit.MILLISECONDS);
    }
    
    public void unlock() {
        nodeLock.unlock();
    }
}
