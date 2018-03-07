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
 * Each instance of this class implements a String-Keyed, Uniform-Depth tree.
 *
 * The number of levels in the tree, and each level's name, is specified at time
 * of construction.  Also specified at time of construction: whether a "wildcard" string
 * is to be supported; whether application-specified data objects are subject to certain
 * run-time type restrictions.
 *
 * The tree's root node and all intermediate/non-leaf nodes are Maps.
 * The leaf nodes are all SkudTreeLeafNode, which includes an Object (_leafNodeDataObject)
 * property for storing a reference to an application-specific data object.
 * The application must also supply an instance of AbstractSkudTreeHelper to
 * certain method calls. The AbstractSkudTreeHelper is responsible for establishing and
 * maintaining the relationship between a SkudTreeLeafNode and the (potentially more than
 * one) application data Object(s) that are inserted into that SkudTreeLeafNode.
 *
 * To ensure the state integrity of the SkudTree is maintained, all properties
 * are specified at time of construction and cannot be changed thereafter.
 *
 * The motivation for this package is to support cacheable read-only lookups against
 * trees that contain static or relatively-static application data.  The current
 * implementation does not support deletion of leaf nodes or other types of "pruning".
 *
 * There is one method for inserting into the tree:
 *		void putLeafNode( String[] keys, Object applicationDataObject, AbstractSkudTreeHelper helper)
 * This is the only method that builds/modifies tree structure and/or leaf nodes.
 *
 * There are two tree search methods:
 *		SkudTreeLeafNode getLeafNode( String[] keys)
 *		List getLeafNodes( Object[] searchKeys)
 * getLeafNode searches for a leaf node having the same specified keys[]: no "wildcards", no "groups".
 *		It will return either one matching SkudTreeLeafNode, or null.
 * getLeafNodes takes an Object[] searchKeys, and supports "wildcards" and "groups".
 *		It will return a List, which may be empty, or may have 1 or more matching SkudTreeLeafNodes.
 *		Each element of Object[] searchKeys must be either (1) a String or (2) a Collection of Strings.
 *		If Object[i] is a String, the search is governed by String comparison with "wildcard" support.
 *		If Object[i] is a Collection, the search will include a non-wildcard String comparison for each String therein.
 *
 * Example code:
 *
 *		String[] myLevels = {"Country", "State", "City"};
 *		SkudTree tree = new SkudTree(
 *				myLevels, 			// the tree's level names (which determines the number of levels, too
 *				false, 				// do not enforce applic data Objects must be Serializable
 *				null, 				// do not enforce applic data Objects must be instanceof specified object
 *				"ANY"				// use this as a "wildcard" when calling getLeafNodes
 *				);
 *
 *		AbstractSkudTreeHelper myHelper = new SimpleSkudTreeHelper();
 *
 *		String myApplicationData;
 *
 *		myApplicationData = "My first piece of application data";
 *		tree.putLeafNode(
 *				new String[] {"US", "CA", "Cupertino"},		 	// the keys[]
 *				myApplicationData, 								// the application data
 *				myHelper										// application-specific helper class
 *				);
 *
 *		myApplicationData = "My second piece of application data";
 *		tree.putLeafNode(new String[] {"US", "CA", "San Jose"}, myApplicationData, myHelper);
 *
 *		myApplicationData = "My third piece of application data";
 *		tree.putLeafNode(new String[] {"US", "WA", "Seattle"}, myApplicationData, myHelper);
 *
 *		// The tree now has 3 leaf nodes.  Do a search for all "CA" matches.
 *
 *		List matchingLeafs = myTree.getLeafNodes( new String[] {"ANY", "CA", "ANY"} );
 *
 *		// matchingLeafs now contains two SkudTreeLeafNodes.
 *
 *		for (Iterator iter = matchingLeafs.iterator(); iter.hasNext(); )
 *		{
 *			SkudTreeLeafNode leaf = (SkudTreeLeafNode) iter.next();
 *			String[] keys = leaf.getKeys();
 *			String myAppData = (String) leaf.getLeafNodeData();
 *			System.out.println("myAppData=" + myAppData + ", keys=" + Arrays.asList(keys));
 *		}
 *
 *		// SkudTree has a toString(boolean) method that supports output of the entire tree
 *		System.out.println("myTree=" + myTree.toString(true));
 */
public class SkudTree implements Serializable
{
	private static final long serialVersionUID = -4594517252836593559L;

	/**
	 * The root HashMap for the tree. It corresponds to level 0, i.e., the top level.
	 */
	protected HashMap _root;

	/**
	 * The unique ordered names associated with each "level" of the tree.
	 * It is intended that each levelName will be descriptive of the key values to be inserted.
	 * For example, if you intend to construct a SkudTree where the leaf nodes will be
	 * keyed by state & city names, such as ("California", "San Jose"), then you might want
	 * to specify ("State", "City") as the levelNames.  The number of levelNames specified
	 * determines the uniform depth of the SkudTree.
	 */
	private final String[] _levelNames;

	/**
	 * When _enforceSerializable is true, all application leaf node data Objects are checked for
	 * serializability before insertion into the tree.
	 */
	private final boolean _enforceSerializable;

	/**
	 * When _enforcedApplicationDataObjectInstance is non-null, all applicationDataObjects
	 * are checked before insertion into the tree that they are an instance of this example
	 * Object's run-time class.
	 */
	private final Object _enforcedApplicationDataObjectInstance;

	/**
	 *	To disable "wildcarding", specify null wildcardString at time of construction.
	 */
	private final String _wildcardString;

	private Integer[] _levelWeightages = null;

	/**
     * Constructor
     *
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     */
    public SkudTree(String[] levelNames) throws TreeException
    {
    	// the data object need not be Serializable
        this(levelNames, false );
    }

    /**
     * Constructor
     *
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param enforceSerializable - if true, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be Serializable.
     */
    public SkudTree(String[] levelNames, boolean enforceSerializable) throws TreeException
    {
    	// allow any type of Object to get stored in the leaf
        this(levelNames, enforceSerializable, null);
    }

	/**
     * Constructor
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param weightages - array of Integer weightage for every Level. length must be equal to the Level's size
     */
    public SkudTree(String[] levelNames, Integer[] weightages) throws TreeException
    {
    	// default to "ANY" as the wildCard string
        this(levelNames, "ANY", weightages );
    }

	/**
     * Constructor
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param wildcardString - e.g., "ANY".  In getLeafNodes methods, if either the search key or a tree path key is this value, it is considered a match.
     */
    public SkudTree(String[] levelNames, String wildcardString) throws TreeException
    {
    	// if not specified it is considered as no weightage
        this(levelNames, wildcardString, null );
    }

    /**
     * Constructor
     *
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param wildcardString - e.g., "ANY".  In getLeafNodes methods, if either the search key or a tree path key is this value, it is considered a match.
     * @param weightages - array of Integer weightage for every Level. length must be equal to the Level's size
     */
    public SkudTree(String[] levelNames, String wildcardString, Integer[] weightages) throws TreeException
    {
    	// default to "ANY" as the wildCard string
    	this(levelNames, false, null, wildcardString, weightages );
    }

    /**
     * Constructor
     *
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param enforceSerializable - if true, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be Serializable.
     * @param enforcedApplicationDataObjectInstance - if non-null, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be an instance of this object's run-time type.
     */
    public SkudTree(String[] levelNames, boolean enforceSerializable, Object enforcedApplicationDataObjectInstance) throws TreeException
    {
    	// default to "ANY" as the wildCard string
    	this(levelNames, enforceSerializable, enforcedApplicationDataObjectInstance, "ANY" );
    }

    /**
     * Constructor
     *
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param enforceSerializable - if true, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be Serializable.
     * @param enforcedApplicationDataObjectInstance - if non-null, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be an instance of this object's run-time type.
     * @param weightages - array of Integer weightage for every Level. length must be equal to the Level's size
     */
    public SkudTree(String[] levelNames, boolean enforceSerializable, Object enforcedApplicationDataObjectInstance, Integer[] weightages) throws TreeException
    {
    	// no weightages
    	this(levelNames, enforceSerializable, enforcedApplicationDataObjectInstance, "ANY", weightages);
    }

    /**
     * Constructor
     *
     * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
     * @param enforceSerializable - if true, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be Serializable.
     * @param enforcedApplicationDataObjectInstance - if non-null, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be an instance of this object's run-time type.
     * @param wildcardString - e.g., "ANY".  In getLeafNodes methods, if either the search key or a tree path key is this value, it is considered a match.
     * @param weightages - array of Integer weightage for every Level. length must be equal to the Level's size
     */
    public SkudTree(String[] levelNames, boolean enforceSerializable, Object enforcedApplicationDataObjectInstance, String wildcardString, Integer[] weightages) throws TreeException
    {
    	this( levelNames, enforceSerializable, enforcedApplicationDataObjectInstance, wildcardString );
        setLevelWeightages(weightages);
    }

	/**
	 * Constructor
	 *
	 * @param levelNames - specifies the ordered names for the new tree's levels, and thus also determines the uniform number of levels in the tree.
	 * @param enforceSerializable - if true, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be Serializable.
	 * @param enforcedApplicationDataObjectInstance - if non-null, SkudTree enforces the Object applicationDataObject passed to putLeafNode method to be an instance of this object's run-time type.
	 * @param wildcardString - e.g., "ANY".  In getLeafNodes methods, if either the search key or a tree path key is this value, it is considered a match.
	 */
	public SkudTree( 	String[] levelNames,
						boolean enforceSerializable,
						Object enforcedApplicationDataObjectInstance,
						String wildcardString
					) throws TreeException
	{
		if(levelNames==null || levelNames.length < 1) {
			throw new TreeException("SkudTree constructor",
					"String[] levelNames argument is null or has zero length");
		}

		// Check for duplicated level names
		if (levelNames.length > 1) {
			for (int iUpper = 0; iUpper < levelNames.length - 1; iUpper++) {
				for (int iLower = iUpper + 1; iLower < levelNames.length; iLower++) {
					if(levelNames[iUpper].equals( levelNames[iLower] )) {
						throw new TreeException("SkudTree constructor",
								"String[] levelNames argument has a duplicated value, " + levelNames[iUpper]);
					}
				}
			}
		}

		// Check for null, blank, or all-whitespace level names
		for (int iLevel = 0; iLevel < levelNames.length - 1; iLevel++) {
			if (levelNames[iLevel] == null || levelNames[iLevel].length() == 0) {
				throw new TreeException("SkudTree constructor",
						"String[] levelNames argument has a null or blank value at index " + iLevel);
			}
			boolean isAllWhitespace = true;
			int iChar;
			for (iChar = 0; iChar < levelNames[iLevel].length(); iChar++) {
				isAllWhitespace &= Character.isWhitespace( levelNames[iLevel].charAt(iChar) );
			}
			if (isAllWhitespace) {
				throw new TreeException("SkudTree constructor",
						"String[] levelNames argument has an all-whitespace value at index " + iChar);
			}
		}

		// Everything is OK.
		_levelNames = levelNames;
		_enforceSerializable = enforceSerializable;
		_enforcedApplicationDataObjectInstance = enforcedApplicationDataObjectInstance;
		_wildcardString = wildcardString;

		_root = new HashMap();
	}

    protected void setLevelWeightages(Integer[] weightages)
    {
        if((weightages != null) && (weightages.length != getNumLevels()))
            throw new TreeException("SkudTree.setLevelWeoghtage(Integer[])", "wieghtages should be same length as the Number of Levels " + getNumLevels());
        _levelWeightages = weightages;
    }

    protected Integer getWeightage(Integer index)
    {
        if(_levelWeightages == null)
            return 1;

        if(_levelWeightages[index] == null)
            return 1;

        return _levelWeightages[index];
    }
    
	/**
	 * Accessor method.  Returns the array of the tree's level names (e.g., "Country", "State", "City")
	 *
	 * @return - String[] of the tree's level names
	 */
	public String[] getLevelNames() { return _levelNames; }

	/**
	 * Accessor method.  Returns the number of levels in the tree.
	 *
	 * @return - number of levels in the tree
	 */
	public int getNumLevels() { return _levelNames.length; }

	/**
	 * Accessor method. Returns the _enforceSerializable property.
	 *
	 * @return - the _enforceSerializable property.
	 */
	public boolean getEnforceSerializable() { return _enforceSerializable; }

	/**
	 * Accessor method. Returns a reference to the _enforcedApplicationDataObjectInstance Object
	 * that was specified at time of construction.
	 *
	 * @return - reference to the _enforcedApplicationDataObjectInstance Object
	 */
	public Object getEnforcedApplicationDataObjectInstance() { return _enforcedApplicationDataObjectInstance; }


	/**
	 * Accessor method. Returns the _wildcardString Object property.
	 *
	 * @return - the _wildcardString Object property.
	 */
	public String getWildcardString() { return _wildcardString; }

	/**
	 * Creates a new SkudTreeLeafNode and inserts it into the tree under the specified keys.
	 *
	 * The specified "helper" class is responsible for attaching or inserting the specified
	 * applicationDataObject into the new SkudTreeLeafNode.
	 *
	 * Tree structure (e.g., intermediate Map nodes) will be automatically created as needed.
	 * If a SkudTreeLeafNode already exists having the same keys, the new lSkudTreeLeafNode
	 * will replace the existing one.
	 *
	 * @param keys - The ordered key values that will form the "path" through the tree to the SkudTreeLeafNode.
	 * @param applicationDataObject - The application-specified data object to be stored in the SkudTreeLeafNode.
	 * @param helper - The application-specified instance of AbstractSkudTreeHelper.
	 */
	public void putLeafNode( String[] keys, Object applicationDataObject, AbstractSkudTreeHelper helper) throws TreeException
	{
		if (this.getEnforceSerializable() && !(applicationDataObject instanceof Serializable))
		{
			throw new TreeException("SkudTree.putLeafNode(String[],Object,SkudTreeHelper)",
					"Object applicationDataObject argument is not Serializable");
		}

		if (!isAllowableApplicationDataObjectType(applicationDataObject))
		{
			throw new TreeException("SkudTree.putLeafNode(String[],Object,SkudTreeHelper)",
					"Object applicationDataObject argument is not an instance of the previously-specified Class");
		}

		// Get the bottom-most map node having specified keys[], adding tree structure if needed.

		HashMap lowestMapNode = getLowestMapNode( keys, true);

		// Construct the new leaf node, then add it to the bottom-most map using the last value in keys[].
		// Note that HashMap's "put" method returns a non-null ONLY if it already contained an object
		// object with the same key; i.e. it returns the "replaced" object, or null if no object was replaced.

		SkudTreeLeafNode newLeafNode = new SkudTreeLeafNode( keys);
		SkudTreeLeafNode oldLeafNode = (SkudTreeLeafNode) lowestMapNode.put( keys[keys.length-1], newLeafNode);

		// Invoke the appropriate helper call-back method

		if (oldLeafNode == null)
		{
			helper.onPutLeafNodeInsert( newLeafNode, applicationDataObject);
		}
		else
		{
			helper.onPutLeafNodeReplace( newLeafNode, applicationDataObject, oldLeafNode);
		}
		setScores( keys, newLeafNode );
	}

	protected void setScores(String[] keys, SkudTreeLeafNode leafNode) 
	{
		int score = 0;
		int count = 0;
		for (int index=0; index<keys.length; index++) {
			String key = keys[index];
			if (key == null || key.equals( "ANY" ))
				continue;
			count++;
			score += getWeightage(index);
		}
		leafNode.setScore(score);
		leafNode.setMatchCount( count );
	}

	/**
	 * Private helper that determines whether the application-specified leaf data Object
	 * has a run-time type that is allowable for insertion into a SkudTreeLeafNode.
	 *
	 * @return - whether the Object's run-time type is allowable.
	 */
	private boolean isAllowableApplicationDataObjectType(Object obj)
	{
		if (_enforcedApplicationDataObjectInstance == null)
			return true;
		else if (obj == null)
			return false;
		else
			return _enforcedApplicationDataObjectInstance.getClass().isInstance(obj);
	}

    private boolean wildcardSupported()
    {
    	return getWildcardString() != null;
    }

	/**
	 * This helper method returns the "bottom" map node for the specified keys[].
	 *
	 * If addMapNodesIfNeeded == true (as when called by putLeafNode), it will add tree structure
	 * as needed, and return a non-null.
	 *
	 * If addMapNodesIfNeeded == false (as when called by gutLeafNode), it may return a null.
	 *
	 * @param keys - The ordered key values that will form the "path" through the tree to the SkudTreeLeafNode.
	 * @param addMapNodesIfNeeded - specifies whether any "missing" intermediate map nodes should be added to the tree structure
	 * @return - reference to the bottom-most HashMap that matches the specified keys[].
	 */
	private HashMap getLowestMapNode( String[] keys, boolean addMapNodesIfNeeded) throws TreeException
	{
		if(keys==null || keys.length != this.getNumLevels())
		{
			throw new TreeException("SkudTree.getLowestMapNode(String[],boolean)",
					"String[] keys argument is null or has wrong length");
		}

		// Search down to the last non-leaf node, i.e., the lowest-level HashMap object.
		// Build tree structure as needed along the way, if allowed; otherwise return null.
		// (Note that _root is guaranteed to be non-null.)

		HashMap currLevelMap = _root;
		int numLevelsMinusOne = this.getNumLevels() - 1;
		for (int iLevel = 0; iLevel < numLevelsMinusOne; iLevel++)
		{
			if(currLevelMap != null && currLevelMap.containsKey( keys[iLevel] ))
			{
				currLevelMap = (HashMap) currLevelMap.get( keys[iLevel] );
			}
			else if (addMapNodesIfNeeded)
			{
				// Do not allow Map nodes to be keyed by a null or zero-length String
				if(keys[iLevel]==null || keys[iLevel].length() == 0)
				{
					throw new TreeException("SkudTree.getLowestMapNode(String[],boolean)",
							"String[] keys has null or zero-length value at index="+iLevel);
				}
				// Insert the new Map node with the specified key
				HashMap newMapNode = new HashMap();
				currLevelMap.put( keys[iLevel], newMapNode);
				currLevelMap = newMapNode;
			}
			else
			{
				return null;
			}
		}
		return currLevelMap;
	}

	/**
	 * Searches the SkudTree using specified keys (one key String for each level of the tree),
	 * returns the SkudTreeLeafNode (or null if none was found).
	 *
	 * Note that this method does not recognize or support "wildcards", even if wildcarding
	 * is enabled for this tree. This method is intended to provide an unambiguous method
	 * for retrieving a leaf node, using the exact same keys by which it is stored in the tree.
	 *
	 * @param keys - The ordered key strings to use when searching the tree.
	 * @return - The leaf node; or null if none found for specified keys.
	 */
	public SkudTreeLeafNode getLeafNode( String[] keys) throws TreeException
	{
		if(keys==null || keys.length != this.getNumLevels())
		{
			throw new TreeException("SkudTree.getLeafNode(String[])",
					"String[] keys argument is null or has wrong length");
		}

		// Get the bottom-most map node having specified keys[], or null if it does not exist.

		HashMap lowestMapNode = getLowestMapNode( keys, false);

		SkudTreeLeafNode theLeafNode;
		if (lowestMapNode == null)
		{
			theLeafNode = null;
		}
		else
		{
			// Look for the node within the map, using the last of the keys.
			theLeafNode = (SkudTreeLeafNode) lowestMapNode.get( keys[keys.length-1] );
		}

		return theLeafNode;
	}

	/**
	 * This method, which supports "wildcard" keys and multiple key values ("groups"),
	 * returns a List of all matching SkudTreeLeafNodes.
	 *
	 * Each element of Object[] searchKeys represents the search specification for one level of the tree.
	 * If it is an instanceof String, then it is either the wildcard, or a single specified key value
	 * If it is an instanceof List, then it is a list of 1 or more specified key value
	 *
	 * @param searchKeys - The ordered key strings to use when searching the tree, as described above.
	 * @return - List of matching SkudTreeLeafNodes.
	 */
	public List getLeafNodes( Object[] searchKeys) throws TreeException
	{
		if(searchKeys==null || searchKeys.length != this.getNumLevels())
		{
			throw new TreeException("SkudTree.getLeafNodes(Object[])",
					"Object[] searchKeys argument is null or has wrong length");
		}

		AbstractSkudTreeHelper helper = new SimpleSkudTreeHelper();

		// Use the private recursive method to do the work.
		// Start the recursion at the root of the tree (currentLevel=0).

		visitAllMatchingLeafNodes( searchKeys, helper, _root, 0);

		return helper.getLeafNodesVisited();
	}

	/**
	 * This method determines whether two keys "match".  It supports wildcards in both the
	 * searchKey and the treeNodeKey.  It also supports a Collection of specified String values
	 * as the searchKey.  All String comparisons are CASE SENSITIVE.
	 *
	 * searchKey represents the search specification for one level of the tree.
	 * If it is an instanceof String, then it is either the wildcard, or a single specified key value.
	 * If it is an instanceof Collection, then it is a Collection of 1 or more specified String key values.
	 * (Note: within cargores, this is used to support "filter groups".)
	 *
	 * treeNodeKey is a String that actually is used within the tree as a key within a Map.
	 * It can be the wildcard, or a single specified key value.
	 *
	 * @param searchKey - an externally-specified searchKey value
	 * @param treeNodeKey - the String value of a key that actually exists in the tree
	 * @return - true if keys match
	 */
	protected boolean keysMatch( Object searchKey, String treeNodeKey)
	{
		// Note: treeNodeKey is guaranteed to a non-null, non-zero-length String

		if (wildcardSupported() && treeNodeKey.equals(_wildcardString))
			return true;
		if (searchKey == null)
			return false;
		if (searchKey instanceof String)
			return (searchKey.equals(treeNodeKey) || (wildcardSupported() && searchKey.equals(_wildcardString) ));
		if (searchKey instanceof Collection)
			return ( ((Collection)searchKey).contains(treeNodeKey) );
		throw new TreeException("SkudTree.keysMatch(Object[],String)",
					"Object searchKeys argument is not an instanceof String or Collection, searchKey="+searchKey);
	}

	/**
	 * Traverses the tree, visiting every existing leaf node.  Due to SkudTree's internal
	 * use of HashMap to implement the tree, the order of visitation is indeterminate.
	 *
	 * At each leaf node, the specified SkudTreeHelper's "visit(SkudTreeLeafNode)"
	 * method is called.  The calling application is responsible for providing a concrete
	 * instance of SkudTreeHelper.
	 *
	 * @param helper - the instance of SkudTreeHelper to use for "visiting" leaf nodes.
	 */
	public void visitAllLeafNodes( AbstractSkudTreeHelper helper)
	{
		// Use the private recursive method to do the work.
		// Start the recursion at the root of the tree (currentLevel=0).

		visitAllLeafNodes(helper, _root, 0);
	}

	/**
	 * Recursive method for traversing the tree, visiting every existing leaf node.
	 *
	 * @param helper - the instance of SkudTreeHelper to use for "visiting" leaf nodes.
	 * @param currMapNode - the current intermediate (i.e. non-leaf) HashMap node
	 * @param currentLevel - the current "level" of the tree, with "root" level = 0
	 */
	private void visitAllLeafNodes( AbstractSkudTreeHelper helper, HashMap currentMapNode, int currentLevel)
	{
		// Loop over each value Object in the currentMapNode.

		for (Iterator iter = currentMapNode.values().iterator(); iter.hasNext(); )
		{
			if (currentLevel == this.getNumLevels() - 1)
			{
				// Case (1): We're at the "bottom" of the tree.  Visit all the leaf nodes contained in currMapNode.
				SkudTreeLeafNode leaf = (SkudTreeLeafNode) iter.next();
				helper.onVisit(leaf);
			}
			else
			{
				// Case (2): We're NOT at the bottom.  Recurse over each of the non-leaf nodes in currMapNode
				HashMap nextLowerNode = (HashMap) iter.next();
				visitAllLeafNodes(helper, nextLowerNode, currentLevel+1);
			}
		}
	}

	/**
	 * Recursive method for traversing the tree, visiting every leaf node that matches the keys..
	 *
	 * @param keys - the externally-specified keys to use when matching to keys stored in the tree.
	 * @param helper - the instance of SkudTreeHelper to use for "visiting" leaf nodes.
	 * @param currMapNode - the current intermediate (i.e. non-leaf) HashMap node
	 * @param currentLevel - the current "level" of the tree, with "root" level = 0
	 */
	protected void visitAllMatchingLeafNodes( Object[] searchKeys, AbstractSkudTreeHelper helper, Map currentMapNode, int currentLevel)
	{
		// Loop over each treeNodeKey in the currentMapNode, and check if it matches
		// the searchKey for the currentLevel.

		Set nodeKeys = currentMapNode.keySet();
		for (Iterator iter = nodeKeys.iterator(); iter.hasNext(); )
		{
			String treeNodeKey = (String) iter.next();
			if (keysMatch( searchKeys[currentLevel], treeNodeKey))
			{
				// We have a match!

				if (currentLevel == this.getNumLevels() - 1)
				{
					// Case (1): We're at the "bottom" of the tree.  Visit the matching leaf node.

					SkudTreeLeafNode leaf = (SkudTreeLeafNode) currentMapNode.get(treeNodeKey);
					helper.onVisit(leaf);
				}
				else
				{
					// Case (2): We're NOT at the "bottom" of the tree.  Recurse on the nextLowerNode.

					HashMap nextLowerNode = (HashMap) currentMapNode.get(treeNodeKey);
					visitAllMatchingLeafNodes( searchKeys, helper, nextLowerNode, currentLevel+1);
				}
			}
		}
	}

	/**
	 * Standard toString() method, which does not include output for tree's leaf nodes
	 */
	public String toString()
	{
		return toString( false);
	}

	/**
	 * Auxilliary toString(boolean) method, which can include output for tree's leaf nodes.
	 * Uses the package's SimpleSkudTreeHelper to obtain a List of leaf nodes to
	 * include in the output, if indicated.
	 */
	public String toString(boolean includeLeafNodes)
	{
		String lineSep = System.getProperty( "line.separator");
		StringBuffer sbf = new StringBuffer(lineSep).append("SkudTree[ ");

		sbf.append("this.hashCode()=").append( this.hashCode() ).append("; ");
		sbf.append("levelNames=").append( Arrays.asList(_levelNames) ).append("; ");
		sbf.append(lineSep);
		sbf.append("wildcardString=").append( _wildcardString ).append("; ");
		sbf.append("enforceSerializable=").append( _enforceSerializable ).append("; ");
		sbf.append("enforcedApplicationDataObjectInstance class=").append( _enforcedApplicationDataObjectInstance.getClass().getName() ).append("; ");
		sbf.append(lineSep);

		if (includeLeafNodes)
		{
			SimpleSkudTreeHelper helper = new SimpleSkudTreeHelper();
			this.visitAllLeafNodes( helper);
			List leafNodesVisited = helper.getLeafNodesVisited();
			sbf.append("leafNodesVisited.size()=").append( leafNodesVisited.size() ).append("; ");
			for (ListIterator iter = leafNodesVisited.listIterator(); iter.hasNext(); )
			{
				SkudTreeLeafNode leafNode = (SkudTreeLeafNode) iter.next();
				sbf.append(leafNode.toString());
			}
		}

		sbf.append(lineSep).append("]SkudTree");
		return sbf.toString() ;
	}

}
