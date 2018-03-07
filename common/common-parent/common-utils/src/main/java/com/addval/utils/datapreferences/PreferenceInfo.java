/*
 * PreferenceInfo.java
 *
 * Created on May 2, 2007, 11:24 AM
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
public class PreferenceInfo implements java.io.Serializable
{
    protected Integer _id;
    protected String _name;
    protected int _scoreBased = 0; //0 - false != 0 true
    protected String _calcImplClass;
    /**
     * key - attrib code,
     * value - List of AttribPreferenceDef objects as when the preference scheme is not score based
     * then an attribute code may appear under more than one preference level
     * for example pref(origin, commod) = 1 and pref(origin, commod group) = 2
     */
    protected Map _attribPrefDefMap;
    protected IPrefCalculator _prefCalculator;
    
    /** Creates a new instance of PreferenceInfo */
    public PreferenceInfo() {
    }

    public Integer getId() {
        return _id;
    }

    public void setId(Integer id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }
    
    public int getScoreBased() {
        return _scoreBased;
    }
    
    public void setScoreBased(int scoreBased) {
        this._scoreBased = scoreBased;
    }
    
   //though the method's preferred name is isScoreBased, it seems to through off WrapDynaBean!
    public boolean isPrefScoreBased() {
        return _scoreBased != 0;
    }
    
    public String getCalcImplClass() {
        return _calcImplClass;
    }

    public void setCalcImplClass(String calcImplClass) {
        this._calcImplClass = calcImplClass;
    }
    
    public String toString()
    {
        return "PreferenceInfo[id=" + _id +
            "; name=" + _name +
            "; scoreBased=" + _scoreBased +
            "; implClass=" + _calcImplClass + 
            "; prefMap=" + _attribPrefDefMap + "]";
    }

    public Map getAttribPrefDefMap() {
        return _attribPrefDefMap;
    }

    public void setAttribPrefDefMap(Map attribPrefDefMap) {
        this._attribPrefDefMap = attribPrefDefMap;
    }

    public IPrefCalculator getPrefCalculator() {
        return _prefCalculator;
    }

    public void setPrefCalculator(IPrefCalculator prefCalculator) {
        this._prefCalculator = prefCalculator;
    }
    
    public List getAttribPrefDefList(String attribCode)
    {
        return _attribPrefDefMap == null ? null : (List)_attribPrefDefMap.get(attribCode);
    }        
}
