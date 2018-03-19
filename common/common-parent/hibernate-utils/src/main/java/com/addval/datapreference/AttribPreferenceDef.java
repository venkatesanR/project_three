/*
 * AttribPreferenceDef.java
 *
 * Created on May 2, 2007, 11:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

import com.addval.utils.MathUtl;

/**
 *
 * @author ravi
 */
public class AttribPreferenceDef implements java.io.Serializable, Comparable
{
    protected Integer _prefId;
    protected String _attribCode;
    protected String _attribDim;
    protected long _preference = 0;
    protected int _matchAny = 0;
    
    
    /** Creates a new instance of AttribPreferenceDef */
    public AttribPreferenceDef() {
    }

    public Integer getPrefId() {
        return _prefId;
    }

    public void setPrefId(Integer prefId) {
        this._prefId = prefId;
    }

    public String getAttribCode() {
        return _attribCode;
    }

    public void setAttribCode(String attribCode) {
        this._attribCode = attribCode;
    }

    public String getAttribDim() {
        return _attribDim;
    }

    public void setAttribDim(String attribDim) {
        this._attribDim = attribDim;
    }

    public long getPreference() {
        return _preference;
    }

    public void setPreference(long preference) {
        this._preference = preference;
    }
    
    public String toString()
    {
        return "AttribPreferenceDef[code=" + _attribCode +
            "; prefId=" + _prefId +
            "; dim=" + _attribDim +
            "; pref/score=" + _preference + 
            "; matchAny=" + _matchAny + "]";
    }
    
    /** if pref is same return 0 */
    public int compareTo(Object obj)
    {
        return MathUtl.compare(this.getPreference(), ((AttribPreferenceDef)obj).getPreference());
    }

    /**
     * @return the _matchAny
     */
    public int getMatchAny() {
        return _matchAny;
    }

    /**
     * @param matchAny the _matchAny to set
     */
    public void setMatchAny(int matchAny) {
        this._matchAny = matchAny;
    }
        
}
