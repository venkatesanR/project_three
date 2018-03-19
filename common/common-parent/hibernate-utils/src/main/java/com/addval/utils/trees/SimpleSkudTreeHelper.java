//Source file: C:\\Projects\\Common\\source\\com\\addval\\utils\\trees\\SimpleSkudTreeHelper.java

package com.addval.utils.trees;

import java.util.*;

/**
 * This class serves two purposes:
 * (1) It demonstrates a simple concrete implemention of AbstractSkudTreeHelper's
 *		onPutLeafNodeInsert and onPutLeafNodeReplace methods.
 * (2) It is actually instantiated and used with SkudTree for internal purposes,
 *		but those purposes do NOT include calls to onPutLeafNodeInsert or onPutLeafNodeReplace.
 *		(They only involve calls to the "final" methods, onVisit and getLeafNodesVisited.)
 *
 * This class MAY be directly useful to an application, when the following are true:
 * (1) The application does not need to store multiple applicationDataObjects under the same keys[];
 * (2) The application does not care whether a putLeafNode operation results in the replacement
 *		of a previously-existing leaf node.
 *
 * Otherwise the application should provide its own instance of a class that
 * extends AbstractSkudTreeHelper.  Such appplication-specific instance is made available
 * to the SkudTree methods that need it (e.g., putLeafNode) as an argument to the method.
 */
public class SimpleSkudTreeHelper extends AbstractSkudTreeHelper
{

	/**
	 * This simple implementation merely sets the specified applicationDataObject into
	 * the newLeafNode.
	 *
	 * @param newLeafNode - the SkudTreeLeafNode that was just created & inserted in place of previousLeafNode.
	 * @param applicationDataObject - the application-specific data object.
	 */
	public void onPutLeafNodeInsert( SkudTreeLeafNode newLeafNode, Object applicationDataObject)
	{
		newLeafNode.setLeafNodeData(applicationDataObject);
	}

	/**
	 * This simple implementation does not distinguish between putLeafNode "Insert" and "Replace"
	 * situations.
	 *
	 * @param newLeafNode - the SkudTreeLeafNode that was just created & inserted in place of previousLeafNode.
	 * @param applicationDataObject - the application-specific data object.
	 * @param previousLeafNode - the SkudTreeLeafNode that was just replaced by newLeafNode.
	 */
	public void onPutLeafNodeReplace( SkudTreeLeafNode newLeafNode, Object applicationDataObject, SkudTreeLeafNode previousLeafNode)
	{
		this.onPutLeafNodeInsert( newLeafNode, applicationDataObject);

	}

}
