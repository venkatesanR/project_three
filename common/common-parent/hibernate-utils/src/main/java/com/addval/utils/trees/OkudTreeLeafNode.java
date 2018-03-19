/*
 * OkudTreeLeafNode.java
 *
 * Created on May 12, 2006, 4:36 PM
 *
 */

package com.addval.utils.trees;

/**
 * This class hold leaf node information and application data of a branch in the OkudTree.
 *
 * @author ravi
 */
public class OkudTreeLeafNode extends AOkudTreeNode
{        
    /**
     * The application-specified Object associated with this leaf node.
     * Is usually used to store application-specific data for retrieval from the tree.
     * The application is responsible for knowing and controlling the run-time
     * class of the actual leafNodeDataObject. The application will usually
     * accomplish this by providing an application-specific instance of
     * IOkudTreeHelper for use at the tree construct time.
     */
    protected Object _leafNodeData;

    
    /** Creates a new instance of OkudTreeLeafNode
     * 
     * @param parent - parent node of this leaf node
     * @param key - key identifying this lowest branch of the tree
     */
    public OkudTreeLeafNode(OkudTreeNode parent, Object key) 
    {
        super(parent, key);
    }
    
    public Object getLeafNodeData() {
        return _leafNodeData;
    }

    public void setLeafNodeData(Object leafNodeData) {
        this._leafNodeData = leafNodeData;
    }
    
    /**
     * Sets _leafNodeData to null and calls updateExternalCache which may update
     * external cache if used
     */
    public void clearLeafNodeData()
    {
        if(_leafNodeData != null){
            _leafNodeData = null;
            this.updateExternalCache();
        }
    }
    
    /**
     * call this if external cache is used and you want to make it sync up
     */
    public void updateExternalCache()
    {
        OkudTreeNode parent = (OkudTreeNode)this.getParent();
        if(parent != null)parent.updateExternalCache(this);        
    }
    
    @Override
    public String toString()
    {
        return "LeafNode[{" + _key + "}, " + _score + ", data=" + _leafNodeData + "]";
    }
    
    /**
     * Deletes self from parent node's child map
     * 
     * @return self if delete is successful
     */
    public AOkudTreeNode deleteSelf()
    {
        OkudTreeNode pnode = (OkudTreeNode)this.getParent();
        if(pnode != null){
            return (AOkudTreeNode)pnode.remove(_key);
        }
        return null;
    }
        
    private static final long serialVersionUID = 1020L;
        
}
