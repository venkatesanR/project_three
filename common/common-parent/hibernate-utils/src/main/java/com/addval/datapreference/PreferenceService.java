/*
 * PreferenceService.java
 *
 * Created on April 30, 2007, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

import java.util.*;

import com.addval.utils.StrUtl;

/**
 *
 * @author ravi
 */
public class PreferenceService extends APreferenceService
{   
    /** 
     * Ordered list of DataPreferenceInfo objects ordered by pref
     *
     */
    protected ArrayList _prefList;
    
    /**
     * Max preference value. This will result in a max score
     * For example, if there are 10 pref levels defined then max pref score would be 2^(10-1)
     */
    protected long _maxPrefValue = 0;
    
    /** Creates a new instance of PreferenceService */
    public PreferenceService(PreferenceInfo prefInfo, Map attribDimInfoMap) 
    {
        super(prefInfo, attribDimInfoMap);
        
        //as this implementation is assign the highest possible preference
        //for a record, rearrange pref definitions for easy match
        organizePrefInfo();
    }
    
    protected void organizePrefInfo()
    {
        Map prefDefMap = _prefInfo.getAttribPrefDefMap();        
        ArrayList defList = new ArrayList(prefDefMap.size());

        //collect all AttribitePrefDef objects from map values
        for(Iterator iter = prefDefMap.values().iterator(); iter.hasNext();){
            List defs = (List)iter.next();
            if(defs != null)defList.addAll(defs);
        }

        Collections.sort(defList); //sorts by pref
        AttribPreferenceDef def = null;
        
        //the last member's preference will indicate the max pref number (least preferred)
        def = (AttribPreferenceDef) defList.get(defList.size() - 1);
        _maxPrefValue = def.getPreference();
        
        long prevPref = -1;        
        _prefList = new ArrayList();        
        DataPreferenceInfo prefInfo = null;
        
        //group attribs by pref
        for(Iterator iter = defList.iterator(); iter.hasNext();){
            def = (AttribPreferenceDef)iter.next();
            if(def.getPreference() != prevPref){
                prefInfo = new DataPreferenceInfo();
                prefInfo.setPreference(def.getPreference());
                prefInfo.setMatchAny(def.getMatchAny() != 0);
                _prefList.add(prefInfo);
                prevPref= def.getPreference();
            }
            prefInfo.addAttribute(def.getAttribCode());
        }
    }
    
    /**
     * 
     * @param dimValueMap key: dimension(String), value - value for the dimension(Object)
     */
    public long calculatePreference(Map dimValueMap, Object otherInfo)
    {        
        return this._prefInfo.getPrefCalculator().calculatePreference(this, dimValueMap, otherInfo);
    }
            
    /**
     * @param dimValueMap key - attrib dim name, value - AttribValueInfo object 
     * @return set of matched attributes to the object values
     */
    public Set determineAttribCodeSet(Map dimValueMap)
    {
        HashSet codeSet = new HashSet(dimValueMap.size());
        
        Map.Entry entry;
        AttribDimInfo dimInfo;
        String attribCode;
        AttribValueInfo value;
        
        for(Iterator iter = dimValueMap.entrySet().iterator(); iter.hasNext();){
            entry = (Map.Entry)iter.next();
            dimInfo = (AttribDimInfo)_attribDimInfoMap.get(entry.getKey());
            value = (AttribValueInfo)entry.getValue();
            //either the value is non existent for the dim or the attrib code is already determined
            if(value == null)continue;
            if(!StrUtl.isEmpty(value.getAttribCode())){
                codeSet.add(value.getAttribCode());
                continue;
            }
            
            //if found a relevant dimInfo in the preference definition
            if(dimInfo != null){
                attribCode = dimInfo.getAttribCodeCalculator().determineAttribCode(dimInfo, value.getValue());
                if(attribCode != null)codeSet.add(attribCode);
            }
        }
        
        return codeSet;
    }

    public List getPrefList() {
        return _prefList;
    }
    
    public String toString()
    {
        String lineSep = System.getProperty("line.separator");
        return lineSep + "PreferenceService[prefList=" + lineSep  +
            this.getPrefList() + lineSep +
            "; prefInfo=" + lineSep + this.getPrefInfo() + lineSep +
            "; attribDimInfoMap=" + lineSep + this.getAttribDimInfoMap() + "]";            
    }

    public long getMaxPrefValue() {
        return _maxPrefValue;
    }
}
