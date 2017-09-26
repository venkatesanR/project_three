package com.datastructures.linkedlist;

/**
 * revathi_balamurugan@trimble.com
 * 
 * @author vrengasamy
 *
 * @param <T>
 */
public abstract class LinkedListADT<T> {
	protected int count;

	protected Node head;

	public abstract void add(Object data, int position);

	public abstract void add(Object data);

	public abstract void delete(int position);

	public abstract void delete();

	public int size() {
		return count;
	}

	public abstract void flush();
}
