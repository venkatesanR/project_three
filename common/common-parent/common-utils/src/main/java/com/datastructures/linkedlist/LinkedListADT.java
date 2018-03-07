package com.datastructures.linkedlist;

/*** 
 * @author vrengasamy
 *
 * @param <T>
 */
public abstract class LinkedListADT<T> {

	public abstract void add(T data, int position);

	public abstract void add(T data);

	public abstract void delete(int position);

	public abstract void delete();

	public abstract void flush();
	
	public abstract int size();
}
