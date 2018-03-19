/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.utils.trees;

import java.util.*;

/**
 * This abstract class defines all methods needed by SkudTree.
 *
 */
abstract public class AbstractSkudTreeHelper
{
	// These implement functionality required by SkudTree.
	private List<SkudTreeLeafNode> _leafNodesVisited;
	final protected List<SkudTreeLeafNode> getLeafNodesVisited() 
	{ 
		return sort( _leafNodesVisited ); 
	}
	final protected void clearLeafNodesVisited() { _leafNodesVisited.clear(); }
	final protected void onVisit(SkudTreeLeafNode aLeafNode) { _leafNodesVisited.add( aLeafNode); }

	/**
	 * Constructor
	 */
	public AbstractSkudTreeHelper()
	{
		_leafNodesVisited = new ArrayList<SkudTreeLeafNode>();
	}

    // sort the matching nodes based on the exact match count.
    protected List<SkudTreeLeafNode> sort(List<SkudTreeLeafNode> nodes)
    {
        Collections.sort(nodes, new Comparator<Object>() {
            public int compare(Object o1, Object o2)
            {
                SkudTreeLeafNode node1 = (SkudTreeLeafNode) o1;
                SkudTreeLeafNode node2 = (SkudTreeLeafNode) o2;
                int result;
                // first, the weighted score will decide the order
                result = node2.getScore().compareTo(node1.getScore());
                if (result != 0)
                	return result;
                // when the weighted score is the same, the leaf node decides the order (leafNode in this case should be a subclass of Comparable
                Object value1 = node1.getLeafNodeData();
                // if the leaf node date is a Double or string or any class implemening the Comparable, then further sort it
                if (!(value1 instanceof Comparable<?>))
                	return result;
                Comparable<Object> value2 = (Comparable<Object>)node2.getLeafNodeData();
                return value2.compareTo( value1 );
            }
        });
        return nodes;
    }
    
    /**
	 * This method is responsible for calling newLeafNode.setLeafNodeData(applicationDataObject),
	 * after performing any application-specific processing on the applicationDataObject.
	 * This method is invoked whenever SkudTree's putLeafNode method results in the insertion
	 * of a brand-new SkudTreeLeafNode at the specified keys[]. (I.e., there was no
	 * replacement of a previously-existing SkudTreeLeafNode.)
	 * At the time this method is called, the newLeafNode has already been inserted into
	 * the SkudTree.
	 *
	 * The simplest possible implementation is follows:
	 *			newLeafNode.setLeafNodeData(applicationDataObject);
	 *
	 * @param newLeafNode - the SkudTreeLeafNode that was just created & inserted in place of previousLeafNode.
	 * @param applicationDataObject - the application-specific data object.
	 * @param previousLeafNode - the SkudTreeLeafNode that was just replaced by newLeafNode.
	 */
	abstract public void onPutLeafNodeInsert( SkudTreeLeafNode newLeafNode, Object applicationDataObject);

	/**
	 * This method is responsible for calling newLeafNode.setLeafNodeData(applicationDataObject),
	 * after performing any application-specific processing on the applicationDataObject,
	 * given that leaf node replacement has occurred.
	 * This method is invoked whenever SkudTree's putLeafNode method results in the replacement
	 * of a previously-existing SkudTreeLeafNode at the specified keys[].
	 * At the time this method is called, the newLeafNode has already been inserted into
	 * the SkudTree, and previousLeafNode has been removed from the tree.
	 *
	 * The simplest possible implementation is appropriate when the application does not
	 * care about the distinction between "replace" and "insert" situations.  In that
	 * case, the only statement that is needed is:
	 *			newLeafNode.setLeafNodeData(applicationDataObject);
	 * or, possibly:
	 *			this.onPutLeafNodeInsert(newLeafNode, applicationDataObject);
	 *
	 * A more interesting case is when the application requires a SkudTreeLeafNode to
	 * maintain a Collection of multiple applicationDataObjects that have been "put" into
	 * the tree with the same keys[].  See SimpleMultiDataObjectSkudTreeHelper for an
	 * example of how this can be done.
	 *
	 * @param newLeafNode - the SkudTreeLeafNode that was just created & inserted in place of previousLeafNode.
	 * @param applicationDataObject - the application-specific data object.
	 * @param previousLeafNode - the SkudTreeLeafNode that was just replaced by newLeafNode.
	 */
	abstract public void onPutLeafNodeReplace( SkudTreeLeafNode newLeafNode, Object applicationDataObject, SkudTreeLeafNode previousLeafNode);

}
