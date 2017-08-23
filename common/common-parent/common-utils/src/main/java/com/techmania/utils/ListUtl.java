/*
 * ListUtl.java
 *
 * Created on October 6, 2003, 5:04 PM
 */

package com.techmania.utils;

import java.util.*;

/**
 *
 * @author ravi
 */
public class ListUtl {

	/** Creates a new instance of ListUtl */
	public ListUtl() {
	}

	public static boolean isEmpty(Collection list) {
		return (list == null || list.isEmpty());
	}

	public static Object first(List list) {
		if (isEmpty(list))
			return null;
		return list.get(0);
	}

	public static Object last(List list) {
		if (isEmpty(list))
			return null;
		return list.get(list.size() - 1);
	}

	/**
	 * returns empty iterator if list is null
	 */
	public static Iterator iterator(List list) {
		if (list != null)
			return list.iterator();

		return new Iterator() {
			public boolean hasNext() {
				return false;
			}

			public Object next() {
				return null;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

	}

	public static void setSize(List list, int newSize) {
		if (list.size() < newSize) {
			for (int i = list.size(); i < newSize; i++)
				list.add(null);
		} else if (list.size() > newSize) {
			list.subList(newSize, list.size()).clear();
		}
	}

	public static Object removeFirst(List list) {
		return (first(list) != null) ? list.remove(0) : null;
	}

	public static Object removeLast(List list) {
		return (last(list) != null) ? list.remove(list.size() - 1) : null;
	}

	/**
	 * Returns true if at least 1 element is common between the 2 collections
	 */
	public static boolean intersectionExists(Collection c1, Collection c2) {
		if (c1 == null || c2 == null || c1.isEmpty() || c2.isEmpty())
			return false;

		Iterator i1 = c1.iterator();
		while (i1.hasNext()) {
			if (c2.contains(i1.next()))
				return true;
		}
		return false;
	}

	/**
	 * Func to duplicate jdk 5 functionality
	 */

	public static String toString(Object[] a) {
		if (a == null)
			return "null";
		int iMax = a.length - 1;
		if (iMax == -1)
			return "[]";

		StringBuffer b = new StringBuffer();
		b.append('[');
		for (int i = 0;; i++) {
			b.append(String.valueOf(a[i]));
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}

}
