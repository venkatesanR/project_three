//Source file: C:\\Projects\\Common\\source\\com\\addval\\utils\\trees\\LeafListSkudTreeHelper.java

package com.datastructures.trees.skudtree;

import com.datastructures.trees.skudtree.SimpleSkudTreeHelper;
import com.datastructures.trees.skudtree.SkudTreeLeafNode;

import java.util.*;

/**
 * This class demonstrates an approach that can be used when an application
 * needs to be able to store and retrieve multiple appplicationDataObjects
 * under the same SkudTree keys[].
 * <p>
 * See SimpleSkudTreeHelper for an implementation of the approach where,
 * instead of being "added" to a SkudTreeLeafNode, application objects
 * are subject to simple replacement.  I.e., if two different application
 * objects are passed to putLeafNode with the same keys[], the first one
 * inserted will be replaced when the second one is inserted.
 */
public class LeafListSkudTreeHelper extends SimpleSkudTreeHelper {
    /**
     * Overrides SimpleSkudTreeHelper
     * <p>
     * Instead of setting applicationDataObject directly into the newLeafNode,
     * we create a List containing the applicationDataObject, and set the List
     * into the newLeafNode.
     *
     * @param newLeafNode           - the SkudTreeLeafNode that was just created & inserted in place of
     *                              previousLeafNode.
     * @param applicationDataObject - the application-specific data object.
     */
    public void onPutLeafNodeInsert(SkudTreeLeafNode newLeafNode, Object applicationDataObject) {
        List newLeafNodesList = new ArrayList();
        newLeafNodesList.add(applicationDataObject);
        newLeafNode.setLeafNodeData(newLeafNodesList);
    }

    /**
     * Overrides SimpleSkudTreeHelper
     * <p>
     * Retrieve the replaced node's List, add applicationDataObject to it, then set the
     * List into the newLeafNode.
     *
     * @param newLeafNode           - the SkudTreeLeafNode that was just created & inserted in place of
     *                              previousLeafNode.
     * @param applicationDataObject - the application-specific data object.
     * @param previousLeafNode      - the SkudTreeLeafNode that was just replaced by newLeafNode.
     */
    public void onPutLeafNodeReplace(SkudTreeLeafNode newLeafNode, Object applicationDataObject,
                                     SkudTreeLeafNode previousLeafNode) {
        List newLeafNodesList = (List) previousLeafNode.getLeafNodeData();
        newLeafNodesList.add(applicationDataObject);
        newLeafNode.setLeafNodeData(newLeafNodesList);
    }

}
