package com.datastructures.linkedlist;

/*** 
 * @author vrengasamy
 *
 * @param <T>
 */
public interface LinkedListADT<Type> {
    public void add(Type data, int position);

    public void add(Type data);

    public void delete(int position);

    public void delete();

    public void flush();

    public int size();
}
