/*
 * TestOkudTree.java
 *
 * Created on May 23, 2006, 7:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.trees;

import java.util.*;
import com.addval.utils.Duration;

/**
 *
 * @author ravi
 */
public class TestOkudTree {

	int _cnt = 100;

	/** Creates a new instance of TestOkudTree */
	public TestOkudTree() {
	}

	public static void main(String[] args) throws Exception {
		TestOkudTree test = new TestOkudTree();
		int cnt = 5;
		int numiter = 1;
		if (args.length > 0) {
			cnt = Integer.parseInt(args[0]);
		}

		if (args.length > 1) {
			numiter = Integer.parseInt(args[1]);
		}

		for (int i = 0; i < numiter; i++)
			test.testTree(cnt);
		// test.testTree(300);
		// test.testTree(500);
		// test.testTree(1000);
	}

	public void testTree(int cnt) {
		_cnt = cnt;
		Duration d = new Duration();
		d.startNow();
		OkudTree tree = createTree();
		addDataToTree(tree);
		tree.clearKeyCache();
		d.endNow();
		System.out.println(tree.toString(false));
		// System.out.println(tree.toString(true));
		System.gc();
		System.out.println("Used mem = "
				+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0 + " K");
		d.startNow();
		searchTree(tree);
		d.endNow();
		System.out.println("Test Tree search time = " + d.getMilliSeconds() + " ms");

		d.startNow();
		searchTree(tree);
		d.endNow();
		System.out.println("Test Tree search time = " + d.getMilliSeconds() + " ms");

	}

	public void searchTree(OkudTree tree) {
		OkudTreeSearch crit = tree.createSearch();
		crit.addEqualSearchKey(0, "SFO");
		// crit.addEqualSearchKey(0, "OAK");
		// crit.addEqualSearchKey(1, "MSP");
		crit.addEqualSearchKey(1, "LHR");
		crit.addEqualSearchKey(2, "PERFU");
		crit.addRangeSearchKeys(2, "AVI", "PERFU");
		System.out.println(crit);
		Set res = tree.search(crit);
		System.out.println(res);
		System.out.println(crit);
	}

	public OkudTreeLevel[] makeLevels() {
		OkudTreeLevel[] levels = new OkudTreeLevel[3];

		OkudTreeLevel level = new OkudTreeLevel("Origin", "AIRPORT", 10, false);
		level.setLevelIndex(0);
		level.setWildcardKey("ANY");
		levels[0] = level;

		level = new OkudTreeLevel("Destination", "AIRPORT", 6, false);
		level.setLevelIndex(1);
		level.setWildcardKey("ANY");
		levels[1] = level;

		level = new OkudTreeLevel("Product", "VAP", 1, false);
		level.setLevelIndex(2);
		level.setWildcardKey("ANY");
		level.setRangeSearchable(true);
		levels[2] = level;

		return levels;
	}

	public OkudTree createTree() {
		OkudTree tree = new OkudTree(makeLevels(), new SimpleOkudTreeHelper(), false, null, true, false);

		return tree;
	}

	public void addDataToTree(OkudTree tree) {
		String[] origins = new String[] { "SFO", "LHR", "LAX", "BOS", "OAK", "MSP", "ANY" };
		String[] dests = origins;
		String[] prods = new String[] { "PERFU", "PERFL", "ANY", "AVI" };

		// String[] origins = new String[]{"SFO", "ANY"};
		// String[] dests = new String[]{"LHR", "ANY"};
		// String[] prods = new String[]{"PERFU", "ANY", "AVI"};

		for (int i = 0; i < origins.length; i++)
			for (int j = 0; j < dests.length; j++)
				for (int k = 0; k < prods.length; k++)
					tree.putLeafNode(makeKeys(origins[i], dests[j], prods[k]),
							makeDataObject(origins[i], dests[j], prods[k]));

		int cnt = _cnt;
		for (int i = 1; i <= cnt; i++) {
			String or = i + "AP";
			if (i == cnt)
				or = "ANY";
			// System.out.println("Used mem = " +
			// (Runtime.getRuntime().totalMemory() -
			// Runtime.getRuntime().freeMemory())/1024.0 + " K");
			for (int j = 1; j <= cnt; j++) {
				String ds = i + "AP";
				if (j == cnt)
					ds = "ANY";
				for (int k = 1; k <= 100; k++) {
					String pd = "PRODUCT::" + k;
					if (k == 100)
						pd = "ANY";

					tree.putLeafNode(makeKeys(or, ds, pd), makeDataObject(or, ds, pd));
				}
			}
		}
	}

	public Comparable[] makeKeys(String origin, String dest, String product) {
		Comparable[] keys = new Comparable[3];
		keys[0] = origin;
		keys[1] = dest;
		keys[2] = product;
		return keys;
	}

	public String makeDataObject(String o, String d, String p) {
		return o + "-" + d + "-" + p;
	}

}
