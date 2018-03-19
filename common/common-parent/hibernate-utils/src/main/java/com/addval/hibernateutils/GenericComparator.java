package com.addval.hibernateutils;

import java.util.Comparator;

public class GenericComparator implements Comparator<Comparable>{

	public GenericComparator(){
		
	}
	public int compare(Comparable o1, Comparable o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o2 == null) {
			return 1;
		}
		if (o1 == null) {
			return -1;
		}
		return o1.compareTo(o2);
	}
}
