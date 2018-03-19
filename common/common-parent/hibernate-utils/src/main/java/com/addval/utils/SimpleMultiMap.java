/*
 * SimpleMultiMap.java
 *
 * Created on February 10, 2004, 2:49 PM
 */

package com.addval.utils;

import java.util.*;

/**
 *
 * @author  ravi
 */
public class SimpleMultiMap extends AbstractMap implements Map, Cloneable,	 java.io.Serializable
{
    /**
     * The  table data.
     */
    private transient Entry firstEntry;
	private transient Entry lastEntry;

    /**
     * The total number of mappings in the hash table.
     */
    private transient int count = 0;

    /**
     * The number of times this Map has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the SimpleMultiMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the SimpleMultiMap fail-fast.  (See ConcurrentModificationException).
     */
    private transient int modCount = 0;

    /**
     * Constructs a new, empty map .
     */
    public SimpleMultiMap() {
    }

    /**
     * Constructs a new map with the same mappings as the given map.  The
     * map is created with a capacity of twice the number of mappings in
     * the given map or 11 (whichever is greater), and a default load factor,
     * which is <tt>0.75</tt>.
     *
     * @param t the map whose mappings are to be placed in this map.
     */
    public SimpleMultiMap(Map t) {
	putAll(t);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map.
     */
    public int size() {
	return count;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
	return count == 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(Object value) {
		if(count == 0)return false;

		if (value==null) {
			for (Entry e = firstEntry ; e != null ; e = e.next) if (e.value==null)return true;	    
		} else {
			for (Entry e = firstEntry ; e != null ; e = e.next) if (value.equals(e.value))return true;
		}

		return false;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * 
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * @param key key whose presence in this Map is to be tested.
     */
    public boolean containsKey(Object key) {
		if(count == 0)return false;

		if (key==null) {
			for (Entry e = firstEntry ; e != null ; e = e.next) if (e.key==null)return true;	    
		} else {
			for (Entry e = firstEntry ; e != null ; e = e.next) if (key.equals(e.key))return true;
		}

		return false;
	}

    /**
     * Returns the value to which this map maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * @return the value to which this map maps the specified key.
     * @param key key whose associated value is to be returned.
     */
    public Object get(Object key) {
		if(count == 0)return null;

		if (key==null) {
			for (Entry e = firstEntry ; e != null ; e = e.next) if (e.key==null)return e.value;	    
		} else {
			for (Entry e = firstEntry ; e != null ; e = e.next) if (key.equals(e.key))return e.value;
		}

		return null;
    }

    /**
     * Associates the specified value with the specified key in this map.
	 * allows duplicate keys
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return the value passed in
     */
    public Object put(Object key, Object value) {
		
		if(lastEntry == null){		
			// Creates the new entry.		
			Entry e = new Entry(key, value, null);
			firstEntry = e;
			lastEntry = firstEntry;
		}
		else{
			lastEntry.next = new Entry(key, value, null);
			lastEntry = lastEntry.next;
		}
		count++;
		return value;
    }

    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *	       if there was no mapping for key.  A <tt>null</tt> return can
     *	       also indicate that the map previously associated <tt>null</tt>
     *	       with the specified key.
     */
    public Object remove(Object key) {
		if(count == 0)return null;
		
        if (key != null) {
            for (Entry e = firstEntry, prev = null; e != null; prev = e, e = e.next) {
                if (key.equals(e.key)) {
                    modCount++;
                    if (prev != null)
                        prev.next = e.next;
                    else
                        firstEntry = e.next;
					
					if(e.next == null){//matched last entry
						lastEntry = prev;
					}
					

                    count--;
                    Object oldValue = e.value;
                    e.value = null;
                    return oldValue;
                }
            }
        } else {
            for (Entry e = firstEntry, prev = null; e != null;  prev = e, e = e.next) {
                if (e.key == null) {
                    modCount++;
                    if (prev != null)
                        prev.next = e.next;
                    else
                        firstEntry = e.next;

					if(e.next == null){//matched last entry
						lastEntry = prev;
					}
					
                    count--;
                    Object oldValue = e.value;
                    e.value = null;
                    return oldValue;
                }
            }
        }

	return null;
    }

    /**
     * Copies all of the mappings from the specified map to this one.
     * 
     * These mappings replace any mappings that this map had for any of the
     * keys currently in the specified Map.
     *
     * @param t Mappings to be stored in this map.
     */
    public void putAll(Map t) {
	Iterator i = t.entrySet().iterator();
	while (i.hasNext()) {
	    Map.Entry e = (Map.Entry) i.next();
	    put(e.getKey(), e.getValue());
	}
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {	
	modCount++;
	firstEntry = null;
	lastEntry = null;
	count = 0;
    }

    /**
     * Returns a shallow copy of this <tt>SimpleMultiMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
    public Object clone() {
	try { 
	    SimpleMultiMap t = (SimpleMultiMap)super.clone();
		if(count != 0){
			t.firstEntry = (Entry) firstEntry.clone();		
			for(Entry e = t.firstEntry; e != null; e = e.next) t.lastEntry = e;			
		}
		t.count = count;
	    t.keySet = null;
	    t.entrySet = null;
        t.values = null;
	    t.modCount = 0;
	    return t;
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    // Views

    private transient Set keySet = null;
    private transient Set entrySet = null;
    private transient Collection values = null;

    /**
     * Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  The set supports element removal, which removes the
     * corresponding mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>, and
     * <tt>clear</tt> operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    public Set keySet() {
	if (keySet == null) {
	    keySet = new AbstractSet() {
		public Iterator iterator() {
		    return getSimpleIterator(KEYS);
		}
		public int size() {
		    return count;
		}
                public boolean contains(Object o) {
                    return containsKey(o);
                }
		public boolean remove(Object o) {
                    int oldSize = count;
                    SimpleMultiMap.this.remove(o);
		    return count != oldSize;
		}
		public void clear() {
		    SimpleMultiMap.this.clear();
		}
	    };
	}
	return keySet;
    }

    /**
     * Returns a collection view of the values contained in this map.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from this map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection values() {
	if (values==null) {
	    values = new AbstractCollection() {
                public Iterator iterator() {
		    return getSimpleIterator(VALUES);
                }
                public int size() {
                    return count;
                }
                public boolean contains(Object o) {
                    return containsValue(o);
                }
                public void clear() {
                    SimpleMultiMap.this.clear();
                }
            };
        }
	return values;
    }

    /**
     * Returns a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <tt>Map.Entry</tt>.  The
     * collection is backed by the map, so changes to the map are reflected in
     * the collection, and vice-versa.  The collection supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a collection view of the mappings contained in this map.
     * @see Map.Entry
     */
    public Set entrySet() {
	if (entrySet==null) {
	    entrySet = new AbstractSet() {
                public Iterator iterator() {
		    return getSimpleIterator(ENTRIES);
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();                                                            

                    for (Entry e = firstEntry; e != null; e = e.next)
                        if (e.equals(entry))
                            return true;
                    return false;
                }

		public boolean remove(Object o) {
					if(count == 0)return false;
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry entry = (Map.Entry)o;
                    Object key = entry.getKey();

                    for (Entry e = firstEntry, prev = null; e != null; prev = e, e = e.next) {
                        if (e.equals(entry)) {
                            modCount++;							
                            if (prev != null){
                                prev.next = e.next;
							}
                            else{
                                firstEntry = e.next;																
							}
							
							if(e.next == null){//matched last entry
								lastEntry = prev;
							}
							
                            count--;
                            e.value = null;
                            return true;
                        }
                    }
                    return false;
                }

                public int size() {
                    return count;
                }

                public void clear() {
                    SimpleMultiMap.this.clear();
                }
            };
        }

	return entrySet;
    }

    private Iterator getSimpleIterator(int type) {
	if (count == 0) {
	    return emptySimpleIterator;
	} else {
	    return new SimpleIterator(type);
	}
    }

    /**
     * SimpleMultiMap collision list entry.
     */
    private static class Entry implements Map.Entry {
	Object key;
	Object value;
	Entry next;

	Entry(Object key, Object value, Entry next) {
	    this.key = key;
	    this.value = value;
	    this.next = next;
	}

	protected Object clone() {
	    return new Entry(key, value,
			     (next==null ? null : (Entry)next.clone()));
	}

	// Map.Entry Ops 

	public Object getKey() {
	    return key;
	}

	public Object getValue() {
	    return value;
	}

	public Object setValue(Object value) {
	    Object oldValue = this.value;
	    this.value = value;
	    return oldValue;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof Map.Entry))
		return false;
	    Map.Entry e = (Map.Entry)o;

	    return (key==null ? e.getKey()==null : key.equals(e.getKey())) &&
	       (value==null ? e.getValue()==null : value.equals(e.getValue()));
	}
	
	public int hashCode() {
	    return (key == null ? 0 : key.hashCode()) + 31 * (value==null ? 0 : value.hashCode());
	}	

	public String toString() {
	    return key+"="+value;
	}
    }

    // Types of Iterators
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;

    private static EmptySimpleIterator emptySimpleIterator 
	= new EmptySimpleIterator();
					     
    private static class EmptySimpleIterator implements Iterator {
	
	EmptySimpleIterator() {
	    
	}

	public boolean hasNext() {
	    return false;
	}

	public Object next() {
	    throw new NoSuchElementException();
	}
	
	public void remove() {
	    throw new IllegalStateException();
	}

    }			
		    
    private class SimpleIterator implements Iterator {
		Entry entry = SimpleMultiMap.this.firstEntry;
		Entry lastReturned = null;
		int type;

	/**
	 * The modCount value that the iterator believes that the backing
	 * List should have.  If this expectation is violated, the iterator
	 * has detected concurrent modification.
	 */
	private int expectedModCount = modCount;

	SimpleIterator(int type) {
	    this.type = type;
	}

	public boolean hasNext() {
		Entry e = entry;		
	    if(e == null)return false;				
		return true;	    
	}

	public Object next() {
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();

	    Entry et = entry;
	
	    if (et != null) {
		Entry e = lastReturned = entry;
		entry = e.next;
		return type == KEYS ? e.key : (type == VALUES ? e.value : e);
	    }
	    throw new NoSuchElementException();
	}

	public void remove() {
	    if (lastReturned == null)
		throw new IllegalStateException();
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();


	    for (Entry e = SimpleMultiMap.this.firstEntry, prev = null; e != null;		 prev = e, e = e.next) {
		if (e == lastReturned) {
		    modCount++;
		    expectedModCount++;
		    if (prev == null)
			SimpleMultiMap.this.firstEntry = e.next;
		    else
			prev.next = e.next;
			
			if(e.next == null){//last entry
				SimpleMultiMap.this.lastEntry = prev;
			}
		    count--;
			
		    lastReturned = null;
			
		    return;
		}
	    }
	    throw new ConcurrentModificationException();
	}
    }

    /**
     * Save the state of the <tt>SimpleMultiMap</tt> instance to a stream (i.e.,
     * serialize it).
     *
     * @serialData The <i>capacity</i> of the SimpleMultiMap (the length of the
     *		   bucket array) is emitted (int), followed  by the
     *		   <i>size</i> of the SimpleMultiMap (the number of key-value
     *		   mappings), followed by the key (Object) and value (Object)
     *		   for each key-value mapping represented by the SimpleMultiMap
     * The key-value mappings are emitted in no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException
    {
	// Write out the threshold, loadfactor, and any hidden stuff
	s.defaultWriteObject();

	// Write out size (number of Mappings)
	s.writeInt(count);

        // Write out keys and values (alternating)	
	    Entry entry = firstEntry;
	    while (entry != null) {
		s.writeObject(entry.key);
		s.writeObject(entry.value);
		entry = entry.next;
	    }
    }

    private static final long serialVersionUID = 998898820763181101L;

    /**
     * Reconstitute the <tt>SimpleMultiMap</tt> instance from a stream (i.e.,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
         throws java.io.IOException, ClassNotFoundException
    {
	// Read in the threshold, loadfactor, and any hidden stuff
	s.defaultReadObject();

	// Read in size (number of Mappings)
	int size = s.readInt();

	// Read the keys and values, and put the mappings in the SimpleMultiMap
	for (int i=0; i<size; i++) {
	    Object key = s.readObject();
	    Object value = s.readObject();
	    put(key, value);
	}
    }
}
