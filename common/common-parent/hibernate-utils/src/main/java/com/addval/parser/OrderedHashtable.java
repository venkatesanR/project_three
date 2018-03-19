//Source file: F:\\proj\\aerlingus\\source\\com\\addval\\parser\\OrderedHashtable.java



//Source file: D:\\PROJECTS\\AERLINGUS\\SOURCE\\com\\addval\\pax\\utils\\OrderedHashtable.java



package com.addval.parser;



import java.util.Dictionary;

import java.util.Map;

import java.io.Serializable;

import java.util.Collections;

import java.util.Hashtable;

import java.util.Vector;

import java.util.Set;

import java.util.Enumeration;

import java.util.Iterator;

import java.util.Collection;

import java.util.AbstractSet;



/**

This class extends the Dictionary Object and implements Map, Cloneable,

Serializable simply to substitute the Hashtable wherever necessary.

(Hashtable also is a subclass of Dictionary)

improved Hashtable. Has almost all the useful methods implemented.

Importantly this Hashtable will keep the order of the object put into it

unlike the java.util.Hashtable which would rearrange the input objects based

on their hashCode values.

Additional methods are there to sort the entries and to return the entries

directly as Iterator.

WARNING : Use this only when there is a absolute need to

retrieve the elements in the order they were put into the Hash

or to have sorting facility.

This object instantiation took about 200 ms more than a Hashtable

instantiation. With about 100 random put, get and remove operations there is NO

diff in performance.

For about 1000 randon put, get and remove operations  this class took about 50

ms more than the Hashtable.

Sorting timining

----------------

for a 100  element Hash it took about   5 ms

for a 200  element Hash it took about  10 ms

for a 300  element Hash it took about  10 ms

for a 400  element Hash it took about  15 ms

for a 500  element Hash it took about  20 ms

for a 600  element Hash it took about  50 ms

for a 1000 element Hash it took about 130 ms

 */

public class OrderedHashtable extends Dictionary implements Map, Cloneable, Serializable

{

    private static final String _module = "OrderedHashtable";

    private Vector _keys = null;

    private Vector _values = null;



    /**

    @param map

    @roseuid 3D6B0B1203E0

     */

    public OrderedHashtable(Map map)

    {

        this();

        putAll( map );

    }



    /**

    @param capacity

    @param loadFactor

    @roseuid 3D6B0B1203C3

     */

    public OrderedHashtable(int capacity, float loadFactor)

    {

        _keys = new Vector( capacity, (int)loadFactor );

        _values = new Vector( capacity, (int)loadFactor );

    }



    /**

    @param capacity

    @roseuid 3D6B0B1203B8

     */

    public OrderedHashtable(int capacity)

    {

        this( capacity, 0 );

    }



    /**

    @roseuid 3D6B0B1203AE

     */

    public OrderedHashtable()

    {

        this( 10 );

    }



    /**

    Access method for the _module property.



    @return   the current value of the _module property

     */

    public static String getModule()

    {

        return _module;

    }



    /**

    @param key

    @param value

    @return Object

    @roseuid 3D6B0B1300CA

     */

    public Object put(Object key, Object value)

    {

        int index = _keys.indexOf( key );

        if (index != -1) {

            _keys.setElementAt( key, index );

            return _values.set( index, value );

        }

        _keys.add( key );

        _values.add( value );

        return null;

    }



    /**

    @param key

    @return Object

    @roseuid 3D6B0B1300DF

     */

    public Object get(Object key)

    {

        int index = _keys.indexOf( key );

        if (index == -1)

            return null;

        return _values.get( index );

    }



    /**

    @param key

    @return Object

    @roseuid 3D6B0B1300F3

     */

    public Object remove(Object key)

    {

        int index = _keys.indexOf( key );

        if (index == -1)

            return null;

        _keys.removeElementAt( index );

        return _values.remove( index );

    }



    /**

    @roseuid 3D6B0B130106

     */

    public void clear()

    {

        _keys.clear();

        _values.clear();

    }



    /**

    @param key

    @return boolean

    @roseuid 3D6B0B13011A

     */

    public boolean contains(Object key)

    {

        return containsKey( key );

    }



    /**

    @param key

    @return boolean

    @roseuid 3D6B0B13012E

     */

    public boolean containsKey(Object key)

    {

        return _keys.contains( key );

    }



    /**

    @param value

    @return boolean

    @roseuid 3D6B0B130142

     */

    public boolean containsValue(Object value)

    {

        return _values.contains( value );

    }



    /**

    @return boolean

    @roseuid 3D6B0B130156

     */

    public boolean isEmpty()

    {

        return _keys.isEmpty();

    }



    /**

    @return int

    @roseuid 3D6B0B130160

     */

    public int size()

    {

        return _keys.size();

    }



    /**

    @roseuid 3D6B0B130174

     */

    public void rehash()

    {

        // do nothing

    }



    /**

    @return java.util.Set

    @roseuid 3D6B0B13017E

     */

    public Set entrySet()

    {

        return keySet();

    }



    /**

    @return java.util.Enumeration

    @roseuid 3D6B0B130247

     */

    public Enumeration keys()

    {

        return Collections.enumeration( _keys );

    }



    /**

    @return java.util.Set

    @roseuid 3D6B0B130319

     */

    public Set keySet()

    {

        return (Set)new KeySet();

    }



    /**

    @return java.util.Iterator

    @roseuid 3D6B0B1303E1

     */

    public Iterator keysIterator()

    {

        return _keys.iterator();

    }



    /**

    @return Object[]

    @roseuid 3D6B0B1400CC

     */

    public Object[] toKeyArray()

    {

        return _keys.toArray();

    }



    /**

    @param object[]

    @return Object[]

    @roseuid 3D71ADE4018F

     */

    public Object[] toKeyArray(Object object[])

    {

        return _keys.toArray( object );

    }



    /**

    @return Object[]

    @roseuid 3D6B0B1400D6

     */

    public Object[] toKeySortedArray()

    {

        sort();

        return _keys.toArray();

    }



    /**

    @return java.util.Enumeration

    @roseuid 3D6B0B1400E0

     */

    public Enumeration elements()

    {

        return Collections.enumeration( _values );

    }



    /**

    @return java.util.Collection

    @roseuid 3D6B0B1401B2

     */

    public Collection values()

    {

        return (Collection)_values;

    }



    /**

    @return java.util.Iterator

    @roseuid 3D6B0B140284

     */

    public Iterator valuesIterator()

    {

        return _values.iterator();

    }



    /**

    @return Object[]

    @roseuid 3D6B0B14034C

     */

    public Object[] toValueArray()

    {

        return _values.toArray();

    }



    /**

    @param object[]

    @return Object[]

    @roseuid 3D71ADAC0315

     */

    public Object[] toValueArray(Object object[])

    {

        return _values.toArray( object );

    }



    /**

    @return Object[]

    @roseuid 3D6B0B140356

     */

    public Object[] toValueSortedArray()

    {

        sort();

        return _values.toArray();

    }



    /**

    for Reliable Sorting, it is better that the key object implements the

    toString() method effectively to order them properly as expected.

    @return OrderedHashtable

    @roseuid 3D6B0B14036A

     */

    public OrderedHashtable sort()

    {

        return sort( true );

    }



    /**

    @param isAscending

    @return OrderedHashtable

    @roseuid 3D6B0B140374

     */

    public OrderedHashtable sort(boolean isAscending)

    {

        return sort( isAscending, true );

    }



    /**

    for Reliable Sorting, it is better that the key object implements the

    toString() method effectively to order them properly as expected.

    @param isAscending

    @param isString

    @param isCaseSensitive

    @return OrderedHashtable

    @roseuid 3D6B0B140389

     */

    public OrderedHashtable sort(boolean isAscending, boolean isCaseSensitive)

    {

        return sort( 0, size()-1, isAscending, isCaseSensitive );

    }



    /**

    @param start

    @param end

    @param isAscending

    @param isCaseSensitive

    @return OrderedHashtable

    @roseuid 3D6B0B1403A7

     */

    private OrderedHashtable sort(int start, int end, boolean isAscending, boolean isCaseSensitive)

    {

        if (start == end)

            return this;

        int middle = (start + end) / 2;

        sort( start, middle, isAscending, isCaseSensitive );

        sort( middle + 1, end, isAscending, isCaseSensitive );

        int length = end - start + 1;

        Vector newVector = new Vector();

        Vector newValues = new Vector();

        for (int i = 0; i < length; i++) {

            newVector.add(_keys.get(start + i));

            newValues.add(_values.get(start + i));

        }

        int j = 0;

        int k = middle - start + 1;

        Object key = null;

        Object value = null;

        Object key2 = null;

        int val = -1;

        for (int l = 0; l < length; l++) {

            if (k > end - start) {

                replace( newVector.get(j), newValues.get( j++ ), l + start);

                continue;

            }

            if (j > middle - start) {

                replace( newVector.get(k), newValues.get( k++ ), l + start);

                continue;

            }



            key   = newVector.get( j );

            key2  = newVector.get( k );

            if (key instanceof Integer && key2 instanceof Integer)

                val = new Integer(key.toString()).intValue() - new Integer( key2.toString() ).intValue();

            else if (isCaseSensitive)

                val = key.toString().compareTo( key2.toString() );

            else

                val = key.toString().compareToIgnoreCase( key2.toString() );



            if ((val < 0 && isAscending) || (val > 0 && !isAscending))

                replace( newVector.get( j ), newValues.get( j++ ), l + start);

            else

                replace( newVector.get( k ), newValues.get( k++ ), l + start);

        }

        return this;

    }



    /**

    @return String

    @roseuid 3D6B0B1403D0

     */

    public String toString()

    {

        StringBuffer buffer = new StringBuffer();

        int keySize = _keys.size();

        for (int i=0; i<keySize; i++)

            buffer.append( ", " ).append( _keys.get( i ) ).append( "=" ).append( _values.get( i ) );



        return new StringBuffer( "{" ).append( buffer.substring( 2 ) ).append( "}" ).toString();

    }



    /**

    @param key

    @param value

    @param position

    @roseuid 3D6B0B1403E3

     */

    private void replace(Object key, Object value, int position)

    {

        _keys.setElementAt( key, position );

        _values.setElementAt( value, position );

    }



    /**

    @param arg0

    @param object

    @return boolean

    @roseuid 3D6B0B15001A

     */

    public boolean equals(Object object)

    {

        return this.equals( object );

    }



    /**

    @return int

    @roseuid 3D6B0B15002E

     */

    public int hashCode()

    {

        int keySize = size();

        int hashCode = 0;

        for (int i=0; i<keySize; i++ )

            hashCode += _keys.get( i ).hashCode() + _values.get( i ).hashCode();



        return hashCode;

    }



    /**

    @param args[]

    @roseuid 3D6B0B150041

     */

    private static void main(String args[])

    {



    }



    /**

    @param map

    @roseuid 3D6B1A5803C4

     */

    public void putAll(Map map)

    {

        _keys.addAll( map.keySet() );

        _values.addAll( map.values() );

    }



    private class KeySet extends AbstractSet

    {



        /**

        @roseuid 3D6B0B150240

         */

        private KeySet()

        {

            super();

        }



        /**

        @return int

        @roseuid 3D6B0B150254

         */

        public int size()

        {

            return _keys.size();

        }



        /**

        @return java.util.Iterator

        @roseuid 3D6B0B15025E

         */

        public Iterator iterator()

        {

            return _keys.iterator();

        }

    }

}

