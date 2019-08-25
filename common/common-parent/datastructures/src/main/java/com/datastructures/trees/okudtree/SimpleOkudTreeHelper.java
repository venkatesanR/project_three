/*
 * SimpleOkudTreeHelper.java
 *
 * Created on May 23, 2006, 7:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.datastructures.trees.okudtree;

import java.util.List;

/**
 * @author ravi
 */
public class SimpleOkudTreeHelper implements IOkudTreeHelper {

    /**
     * Creates a new instance of SimpleOkudTreeHelper
     */
    public SimpleOkudTreeHelper() {
    }

    public void addDataToLeafNode(OkudTreeLeafNode leafNode, Object applicationDataObject) {
        leafNode.setLeafNodeData(applicationDataObject);
    }

    public void onCreateNode(OkudTree tree, AOkudTreeNode node, OkudTreeLevel level) {
        long score = 0;
        if (node.getParent() != null) {
            score = node.getParent().getScore();
        }

        if (node.getKey() != null) {
            if (level.getWildcardKey() == null ||
                    !level.getWildcardKey().equals(node.getKey())) {
                score += level.getScore();
            }
        }

        node.setScore(score);
    }

    public void populate(OkudTree tree, OkudTreeNode node, OkudTreeLevel level) {
        //don't do anything to as this helper doesnot support discardable levels
    }


    public List getTopLevelKeys(OkudTreeLevel level) {
        //lazy load is not supported
        return null;
    }

    public List getLevelKeys(OkudTree tree, OkudTreeNode node, OkudTreeLevel level) {
        //lazy load is not supported
        return null;
    }
}
