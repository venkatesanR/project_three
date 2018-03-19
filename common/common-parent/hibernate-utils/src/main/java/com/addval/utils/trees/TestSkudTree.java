//Source file: C:\\Projects\\Common\\source\\com\\addval\\utils\\trees\\TestSkudTree.java

package com.addval.utils.trees;

import java.util.*;

/**
 * Exception class used for reporting "usage errors" by classes in this package.
 *
 */
public class TestSkudTree
{
	public static void main( String[] args)
	{
		String[] myLevels = {"Country", "State", "City"};
		SkudTree myTree = new SkudTree( myLevels, true, new MyBaseDataObject("Example"), "ANY");
		SimpleSkudTreeHelper myHelper = new LeafListSkudTreeHelper();


		String[] someKeys = {"US", "CA", "Cupertino"};
		MyBaseDataObject myDataObject = new MyBaseDataObject("Data for Cupertino");
		myTree.putLeafNode( someKeys, myDataObject, myHelper);

		someKeys = new String[] {"US", "CA", "Redwood City"};
		myDataObject = new MyDerivedDataObject("My home town");
		myTree.putLeafNode( someKeys, myDataObject, myHelper);

		someKeys = new String[] {"US", "CA", "Fresno"};
		myDataObject = new MyDerivedDataObject("My home town");
		myTree.putLeafNode( someKeys, myDataObject, myHelper);

		someKeys = new String[] {"US", "OR", "Eugene"};
		myDataObject = new MyDerivedDataObject("Not a very exciting place");
		myTree.putLeafNode( someKeys, myDataObject, myHelper);

		someKeys = new String[] {"US", "OR", "Prineville"};
		myDataObject = new MyDerivedDataObject("Not a very exciting place");
		myTree.putLeafNode( someKeys, myDataObject, myHelper);

		someKeys = new String[] {"US", "CA", "ANY"};
		myDataObject = new MyDerivedDataObject("Any city in California");
		myTree.putLeafNode( someKeys, myDataObject, myHelper);
		System.out.println("\n========== myTree ============" + myTree.toString(true));


		// Simple search (wildcard, but no groups)
		List matchingLeafs = myTree.getLeafNodes( new String[] {"ANY", "CA", "ANY"} );
		System.out.println("\n========== Results from: getLeafNodes(ANY|CA|ANY)");
		for (Iterator iter = matchingLeafs.iterator(); iter.hasNext(); ) {
			SkudTreeLeafNode aLeaf = (SkudTreeLeafNode) iter.next();
			System.out.println(aLeaf);
		}

		// Complex search (with a group)
		Object[] searchKeys = new Object[3];
		searchKeys[0] = "ANY";
		searchKeys[1] = "ANY";
		ArrayList cities = new ArrayList();
		searchKeys[2] = cities;
		cities.add("Cupertino");
		cities.add("Prineville");
		cities.add("ANY");
		matchingLeafs = myTree.getLeafNodes( searchKeys );
		System.out.println("\n\n############### Results from: getLeafNodes(<COMPLEX>)");
		for (Iterator iter = matchingLeafs.iterator(); iter.hasNext(); ) {
			SkudTreeLeafNode aLeaf = (SkudTreeLeafNode) iter.next();
			System.out.println(aLeaf);
		}


		// Complex search (with a group)
		searchKeys = new Object[3];
		searchKeys[0] = "ANY";
		searchKeys[2] = "ANY";
		ArrayList states = new ArrayList();
		searchKeys[1] = states;
		states.add("CA");
		states.add("OR");
		matchingLeafs = myTree.getLeafNodes( searchKeys );
		System.out.println("\n\n############### Results from: getLeafNodes(<COMPLEX>)");
		for (Iterator iter = matchingLeafs.iterator(); iter.hasNext(); ) {
			SkudTreeLeafNode aLeaf = (SkudTreeLeafNode) iter.next();
			System.out.println(aLeaf);
		}


	}
}

class MyBaseDataObject implements java.io.Serializable
{
	public MyBaseDataObject(String s) { _str = s; }
	private String _str;
	public String toString() { return System.getProperty( "line.separator") + "MyBaseDataObject[ str=" + _str + "]"; }
}

class MyDerivedDataObject extends MyBaseDataObject
{
	public MyDerivedDataObject(String s) { super(s); }
	public String toString() { return System.getProperty( "line.separator") + "MyDerivedDataObject[ " + super.toString() + "\n]MyDerivedDataObject"; }
}
