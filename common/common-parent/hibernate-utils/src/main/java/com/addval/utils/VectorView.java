/*
 * VectorView.java
 *
 * Created on October 3, 2003, 11:39 AM
 */

package com.addval.utils;

import java.util.*;

/**
 * Class to be used for intermediate development stage
 * not disturb existing interfaces but continue new development
 * with List interfaces
 * Use this class to Wrap any List into a Vector so that
 * existing interfaces can work with your list like ArrayList.
 * Overrides all methods of parent class Vector
 * @author  ravi
 */
public class VectorView extends java.util.Vector 
{
	
	private java.util.List _list;
	
	/** Creates a new instance of VectorFacade */
	public VectorView(List list) 
	{
		super(0);
		if(list == null)throw new NullPointerException();
		_list = list;
	}
	
	public Object clone() 
	{
		VectorView obj = (VectorView)super.clone();
		obj._list = new ArrayList(this._list);
		return obj;
	}
	
	public boolean equals(Object obj) 
	{
		return _list.equals(obj);
	}
		
	public int hashCode() 
	{
		return _list.hashCode();
	}
	
	public String toString() 
	{
		return _list.toString();
	}
	
	public boolean add(Object obj) 
	{
		return _list.add(obj);
	}
	
	public boolean addAll(java.util.Collection collection) 
	{
		return _list.addAll(collection);
	}
	
	public void clear() 
	{
		_list.clear();
	}
	
	public boolean contains(Object obj) 
	{
		return _list.contains(obj);
	}
	
	public boolean containsAll(java.util.Collection collection) 
	{
		return _list.containsAll(collection);
	}
	
	public boolean isEmpty() 
	{
		return _list.isEmpty();
	}
	
	public java.util.Iterator iterator() 
	{
		return _list.iterator();
	}
	
	public boolean remove(Object obj) 
	{
		return _list.remove(obj);
	}
	
	public boolean removeAll(java.util.Collection collection) 
	{
		return _list.removeAll(collection);
	}
	
	public boolean retainAll(java.util.Collection collection) 
	{
		return _list.retainAll(collection);
	}
	
	public int size() 
	{
		return _list.size();
	}
	
	public Object[] toArray() 
	{
		return _list.toArray();
	}
	
	public Object[] toArray(Object[] obj) 
	{
		return _list.toArray(obj);
	}
	
	public void add(int index, Object obj) 
	{
		_list.add(index, obj);
	}
	
	public boolean addAll(int index, java.util.Collection collection) 
	{
		return _list.addAll(index, collection);
	}
	
	public Object get(int index) 
	{
		return _list.get(index);
	}
	
	public int indexOf(Object obj) 
	{
		return _list.indexOf(obj);
	}
	
	public int lastIndexOf(Object obj) 
	{
		return _list.lastIndexOf(obj);
	}
	
	public java.util.ListIterator listIterator() 
	{
		return _list.listIterator();
	}
	
	public java.util.ListIterator listIterator(int index) 
	{
		return _list.listIterator(index);
	}
	
	public Object remove(int index) 
	{
		return _list.remove(index);
	}
		
	public Object set(int index, Object obj) 
	{
		return _list.set(index, obj);
	}
	
	public java.util.List subList(int fromIndex, int toIndex) 
	{
		return _list.subList(fromIndex, toIndex);
	}
	
	public void addElement(Object obj) 
	{
		add(obj);
	}
	
	public int capacity() 
	{
		if(_list instanceof Vector){
			return ((Vector)_list).capacity();
		}
		
		return _list.size();
	}
	
	public void copyInto(Object[] obj) 
	{
		int size = _list.size();
		for(int i = 0; i < size; i++)obj[i] = get(i);
	}
	
	public Object elementAt(int index) 
	{
		return _list.get(index);
	}
	
	public java.util.Enumeration elements() 
	{
		return new Enumeration() {
			int count = 0;
			int elemCount = _list.size();
			public boolean hasMoreElements() {
				return count < elemCount;
			}

			public Object nextElement() {
				synchronized (VectorView.this) {
					if (count < elemCount) {
						return _list.get(count++);
					}
				}
				throw new NoSuchElementException("VectorView Enumeration");
			}
		};
	}
	
	public void ensureCapacity(int minCapacity) 
	{
		if(_list instanceof ArrayList){
			((ArrayList)_list).ensureCapacity(minCapacity);
		}
		else if(_list instanceof Vector){
			((Vector)_list).ensureCapacity(minCapacity);
		}
	}
	
	public Object firstElement() 
	{
		if (_list.size() == 0) {
			throw new NoSuchElementException();
		}
		return _list.get(0);
	}
	
	public int indexOf(Object obj, int index) 
	{		
		List list = _list.subList(index, _list.size());
		int idx = list.indexOf(obj);
		if(idx >= 0)return index + idx;
		return -1;		
	}
	
	public void insertElementAt(Object obj, int index) 
	{
		_list.add(index, obj);
	}
	
	public Object lastElement() 
	{
		if (_list.size() == 0) {
			throw new NoSuchElementException();
		}
		return _list.get(_list.size() - 1);
	}
	
	public int lastIndexOf(Object elem, int index) 
	{
		int size = _list.size();
		if (index >= size)
            throw new IndexOutOfBoundsException(index + " >= "+ size);

		if (elem == null) {
			for (int i = index; i >= 0; i--)
				if (get(i) == null)	
					return i;
		} 
		else {
			for (int i = index; i >= 0; i--)
				if (elem.equals(get(i)))
					return i;
		}
		return -1;
	}
	
	public void removeAllElements() 
	{
		clear();
	}
	
	public boolean removeElement(Object obj) 
	{
		return remove(obj);
	}
	
	public void removeElementAt(int index) 
	{
		remove(index);
	}
	
	public void setElementAt(Object obj, int index) 
	{
		_list.set(index, obj);
	}
	
	public void setSize(int newSize) 
	{
		if(_list.size() < newSize){
			for(int i = _list.size(); i < newSize; i++)_list.add(null);
		}
		else if(_list.size() > newSize){
			_list.subList(newSize, _list.size()).clear();
		}		
	}
	
	public void trimToSize() 
	{
		if(_list instanceof ArrayList){
			((ArrayList)_list).trimToSize();
		}
		else if(_list instanceof Vector){
			((Vector)_list).trimToSize();
		}
	}
	
	public List getInnerList()
	{
		return _list;
	}
	
}
