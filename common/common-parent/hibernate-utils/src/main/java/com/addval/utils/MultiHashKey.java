/*
 * MultiHashKey.java
 *
 * Created on July 18, 2003, 10:02 AM
 */

package com.addval.utils;

import java.util.List;
import java.util.Iterator;
import java.io.Serializable;

/**
 * This class can be used as a key in any hash collection
 * where there key consists of multiple parts
 * This class is more efficient than using a list as a key
 * The order of the multiple parts must be the same for all keys
 * and all parts must have a proper equals and hashCode implementation
 * part key list must not be modified after the key is constructed
 * @author  ravi
 */
public class MultiHashKey implements java.io.Serializable
{
    private List _partKeys;
    private int _hashCode;
    private int _size;

    /** Creates a new instance of MultiHashKey */
    public MultiHashKey(List partKeys) 
    {
            if(GenUtl.listIsEmpty(partKeys))
                    throw new IllegalArgumentException("MultiHashKey cannot be constructed with empty key list");

            _partKeys = partKeys;
            _size = _partKeys.size();
            calcHashCode();
    }

    private void calcHashCode()
    {
            _hashCode = 1;
            for(Iterator iter = _partKeys.iterator(); iter.hasNext();){
                    Object obj = iter.next();
                    _hashCode = _hashCode * 31 + (obj == null ? 0 : obj.hashCode());
            }
    }

    public int hashCode()
    {
            return _hashCode;
    }

    public int size()
    {
            return _size;
    }

    @Override
    public boolean equals(Object obj)
    {
            if(obj == this)return true;
            if(obj == null || !(obj instanceof com.addval.utils.MultiHashKey))return false;

            MultiHashKey other = (MultiHashKey)obj;
            if(_size != other._size)return false;

            for(int i = 0; i < _size; i++){
                    if(!GenUtl.equals(this._partKeys.get(i), other._partKeys.get(i)))return false;
            }

            return true;		
    }

    @Override
    public String toString()
    {
            return "MultiHashKey[" + _partKeys + "]";
    }	
    
    private static final long serialVersionUID = 1321L;
}
