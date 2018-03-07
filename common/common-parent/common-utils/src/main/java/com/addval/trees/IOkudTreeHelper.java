/*
 * IOkudTreeHelper.java
 *
 * Created on May 23, 2006, 1:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.trees;

import java.util.List;

/**
 *
 * @author ravi
 */
public interface IOkudTreeHelper 
{
    /**
     * This method is called to add the data to the leaf node. It is expected that
     * the an applicatino specific helper will manage adding data to the leaf node.
     * This method is invoked through a call to putLeafNode on the OkudTree.
     * For example, a helper may build a list of several objects at a leaf node or
     * replace an existing data object or perform some other aggregation operation on the
     * leaf node data baseed on the current applicatinDataObject.
     *
     * @param leafNode The leaf node qualified by level keys where the data to be attached
     * @param applicationDataObject - Actual data to be attached to the leaf node
     */
    public void addDataToLeafNode(OkudTreeLeafNode leafNode, Object applicationDataObject);
    
    /**
     * This method is called after creation of every node and leaf node in all branched of an OkudTree.
     * It is expected that score and prefAttribCode may be set in this method. If scoring and preferences
     * are not used, the implementation can be empty     
     *
     * @param tree - OkudTree in which this new node is created
     * @param node - Newly created node - either an instance of OkudTreeNode or OkudTreeLeafNode
     * @param level - OkudTreeLevel at which this node is attached
     */
    public void onCreateNode(OkudTree tree, AOkudTreeNode node, OkudTreeLevel level);
    
    /**
     * To support discardable levels a helper must implement this method to 
     * populate the tree by calling tree.putLeafNode method in this method.
     * From the node and level one can derive qualifying keys up to this level.
     * The key information can be used to construct SQL to populate the children of the
     * node.
     *
     * @param tree - OkudTree that needs a branch created or recreated
     * @param node - Last node of an incomplete branch
     * @param level - OkudTreeLevel associated with the node
     */
    public void populate(OkudTree tree, OkudTreeNode node, OkudTreeLevel level);
    
    /**
     * For lazy load, OkudTree needs top level keys to precreate that level in the tree
     * This list of keys returned should be complete for the top level. If a tree is
     * identified as a lazy loaded tree, the helper class must implement this method.
     * A lazy loaded tree must have it's top level fully created, i.e., all possible
     * keys (includind wildcard key if any) must be identified at the construct time.
     * If the tree this helper supports is not lazy loaded, this method can simply return null;
     *
     * @param level - OkudTreeLevel for the top most level
     */
    public List getTopLevelKeys(OkudTreeLevel level);
    
    /**
     * Further extends lazy load to lower levels when this method is implemented
     * and one or more levels are marked true for "loadNextLevel".
     * @param tree OkudTree that needs new branches at the requested level
     * @param node Parent node to which the new branches will be attached (can also use the key from this node in sql to get keys for this level)
     * @param level Level for which new keys are required
     */
    public List getLevelKeys(OkudTree tree, OkudTreeNode node, OkudTreeLevel level);
}
