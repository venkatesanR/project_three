package com.techmania.common.util.misc;

import com.techmania.common.util.misc.SortableCol;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public interface AVComparator<T> extends Comparator<T> {
	/*
	 * Return List of Method names which we have to support sub total.
	 */
	public List<String> getSubTotalColumns();

	public HashMap<String, String> getSortMap();

	public void setSortBy(String sortBy);

	public boolean isSubTotalEnabled();

	// Support Multi Sorting feature
	public List<SortableCol> getSortableColumns();

	public void setSortableColumns(List<SortableCol> sortableColumns);

	public List<SortableCol> getDefaultSortableColumns();

	public void setDefaultSortableColumns(List<SortableCol> sortableColumns);

	public Collection multiSort(List<T> source);
}
