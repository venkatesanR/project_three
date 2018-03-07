/*
 * DataPreferenceInfo.java
 *
 * Created on May 3, 2007, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.datapreferences;

import java.util.*;

/**
 *
 * @author ravi
 */
public class DataPreferenceInfo implements java.io.Serializable 
{
    protected long _preference;
    protected boolean _matchAny = false;
    protected HashSet _attribSet; //set of attribute code strings
    
    /** Creates a new instance of DataPreferenceInfo */
    public DataPreferenceInfo() 
    {
    }
    
    /** Creates a new instance of DataPreferenceInfo */
    public DataPreferenceInfo(long pref) 
    {
        _preference = pref;
    }
    
    public long getPreference() {
        return _preference;
    }

    public void setPreference(long preference) {
        this._preference = preference;
    }

    public Set getAttribSet() {
        return _attribSet;
    }
    
    public void addAttribute(String attrib)
    {
        if(_attribSet == null)_attribSet = new HashSet();
        _attribSet.add(attrib);
    }
    
    public String toString()
    {
        return "DataPreferenceInfo[pref=" + _preference +
            ", matchAny=" + _matchAny +
            ", attribs=" + _attribSet + "]";
    }

    /**
     * @return the _matchAny
     */
    public boolean isMatchAny() {
        return _matchAny;
    }

    /**
     * @param matchAny the _matchAny to set
     */
    public void setMatchAny(boolean matchAny) {
        this._matchAny = matchAny;
    }
}
