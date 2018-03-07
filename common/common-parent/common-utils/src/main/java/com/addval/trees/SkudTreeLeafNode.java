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

package com.addval.trees;

import java.io.Serializable;

import java.util.*;

/**
 * All leaf nodes in a SkudTree are of this type. Two properties are maintained:
 * (1) String[] _keysToThisLeaf is automatically created by SkudTree, at time of insertion;
 * (2) leafNodeDataObject is maintained by the application-provided implementation
 *		of AbstractSkudTreeHelper. See the AbstractSkudTreeHelper class for further explanation.
 * (3) matchCount - contain the number of exact match made with the search keys. ANY match is not considered
 * (4) score - each level may be assigned a weightage which will be added up for any exact match with the searchKeys.
 * 				please take a look at SkudTree for usage.
 */
public class SkudTreeLeafNode implements Serializable
{
	private static final long serialVersionUID = -7715882767416068574L;

	/**
	 * The application-specified Object associated with this leaf node.
	 * Is usually used to store application-specific data for retrieval from the tree.
	 * The application is responsible for knowing and controlling the run-time
	 * class of the actual leafNodeDataObject. The application will usually
	 * accomplish this by providing an application-specific instance of
	 * SkudTreeHelper for use in conjunction with SkudTree methods.
	 */
	private Object _leafNodeDataObject;

	/**
	 * The ordered String values that comprise a complete and unique "path" through
	 * the tree to this leaf node.
	 */
	private String[] _keysToThisLeaf;

    private Integer matchCount = 0;
    
    private Integer score = 0;

    /**
	 * Constructor
	 *
	 * @param keys - The keys that define the path to the new leaf node within the tree.
	 * @param leafNodeData - Application-specific Object for which SkudTreeLeafNode will hold a reference.
	 */
	public SkudTreeLeafNode( String[] keys)
	{
		_keysToThisLeaf = keys;
		_leafNodeDataObject = null;
	}

	public void setScore(Integer score)
    {
        this.score = score;
    }

    public Integer getScore()
    {
    	if (score == null)
    		score = 0;
        return score;
    }

    public void setMatchCount(Integer matchCount)
    {
        this.matchCount = matchCount;
    }

    public Integer getMatchCount()
    {
        return matchCount;
    }
	 /**
	 * Gets the array of actual String values that form the complete and unique "path" to
	 * this leaf node within the parent SkudTree.
	 *
	 * @return	String[] forming the complete and unique "path" to this leaf node
	 */
	public String[] getKeys()
	{
		return _keysToThisLeaf;
	}

	/**
	 * Gets the application-specified Object that is associated with this leaf node.
	 *
	 * @return	the application-specified data object for this leaf node
	 */
	public Object getLeafNodeData()
	{
		return _leafNodeDataObject;
	}

	/**
	 * Sets the application-specified Object that is associated with this leaf node.
	 *
	 * @param	the application-specified data object to store in this leaf node
	 */
	public void  setLeafNodeData(Object applicationDataObject)
	{
		_leafNodeDataObject = applicationDataObject;
	}

	/**
	 * Standard toString() method.
	 */
	public String toString()
	{
		String lineSep = System.getProperty( "line.separator");
		StringBuffer sbf = new StringBuffer(lineSep).append("SkudTreeLeafNode[ ");

		sbf.append("keysToThisLeaf=").append( Arrays.asList(_keysToThisLeaf) ).append("; ");
		sbf.append(lineSep);
		sbf.append("applicationDataObject.getClass().getName()=").append( _leafNodeDataObject.getClass().getName() ).append("; ");
		sbf.append("matchCount=").append( matchCount ).append("; ");
		sbf.append("score=").append( score ).append("; ");
		sbf.append(lineSep);
		sbf.append("applicationDataObject=").append( _leafNodeDataObject );

		sbf.append(lineSep).append("]SkudTreeLeafNode");
		return sbf.toString() ;
	}
}
