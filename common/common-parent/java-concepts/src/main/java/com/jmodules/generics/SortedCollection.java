package com.jmodules.generics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SortedCollection<E extends Comparable<? super E>> {
	private List<E> input;

	public SortedCollection(Collection<E> all) {
		this.input = (List<E>) all;
	}

	public List<E> sort() {
		Collections.sort(input);
		return input;
	}
}
