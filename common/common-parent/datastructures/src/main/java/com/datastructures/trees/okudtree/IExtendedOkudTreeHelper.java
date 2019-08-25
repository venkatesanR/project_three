/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.datastructures.trees.okudtree;

import java.util.Collection;
import java.util.List;

/**
 * @author ravi.nandiwada
 */
public interface IExtendedOkudTreeHelper extends IOkudTreeHelper {
    public OkudTreeLevel[] makeLevels();

    public OkudTree createTree();

    public void populateLevelKeys(OkudTree tree, Collection<AOkudTreeNode> nodes, OkudTreeLevel level);

    public void populate(OkudTree tree, Collection<AOkudTreeNode> nodes, OkudTreeLevel level);

    public void populateLeafNode(OkudTree tree, OkudTreeLeafNode node, OkudTreeLevel level);

    public List<Object[]> makeKeys(IOkudTreeDTO dto);
}
