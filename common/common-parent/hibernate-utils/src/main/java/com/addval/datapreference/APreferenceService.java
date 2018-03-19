/*
 * APreferenceService.java
 *
 * Created on April 30, 2007, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

import java.util.*;
import com.addval.utils.StrUtl;
import com.addval.utils.ListUtl;

/**
 *
 * @author ravi
 */
public abstract class APreferenceService 
{
    protected PreferenceInfo _prefInfo;
    
    /**
     * key = attrib dim, value = AttribDimInfo object
     */
    protected Map _attribDimInfoMap;
        
    /**
     * Default attribute code calculator
     */
    protected IAttribCodeCalculator _defaultAttribCodeCalculator;
               
    /**
     * Creates a new instance of APreferenceService
     */
    public APreferenceService(PreferenceInfo prefInfo, Map attribDimInfoMap) 
    {
        _prefInfo = prefInfo;
        _attribDimInfoMap = attribDimInfoMap;
        _defaultAttribCodeCalculator = new DefaultAttribCodeCalculator();       
        createAttribCodeCalcImplClasses();
        createPreferenceCalculator();
    }
    
    /**
     * @param dimValueMap key: dimension(String), value - AttribValueInfo - value for the dimension(Object)
     */
    public abstract long calculatePreference(Map dimValueMap, Object otherInfo);
    
    public long computeAttribScore(String attribDim, Object attribValue, Object otherInfo)
    {
        AttribDimInfo dimInfo = (AttribDimInfo)_attribDimInfoMap.get(attribDim);
        //get attrib code
        String attribCode = dimInfo.getAttribCodeCalculator().determineAttribCode(dimInfo, attribValue);
                
        return dimInfo == null || attribCode == null ? 0 : dimInfo.getAttribCodeCalculator().determineAttribScore(dimInfo, 
                (AttribPreferenceDef)ListUtl.first(_prefInfo.getAttribPrefDefList(attribCode)), //for a score based scheme, each attrib pref is defined only once
                attribValue, otherInfo);
    }
    
    protected void createAttribCodeCalcImplClasses()
    {
        try{
            AttribDimInfo dimInfo;
            IAttribCodeCalculator codeCalcr;
            
            for(Iterator iter = _attribDimInfoMap.values().iterator(); iter.hasNext();){
                dimInfo = (AttribDimInfo)iter.next();
                if(!StrUtl.isEmptyTrimmed(dimInfo.getAttribCodeCalcImplClass())){
                    Class calcClass = Class.forName(dimInfo.getAttribCodeCalcImplClass());                    
                    dimInfo.setAttribCodeCalculator((IAttribCodeCalculator)calcClass.newInstance());
                }
                else{
                    dimInfo.setAttribCodeCalculator(_defaultAttribCodeCalculator);
                }
            }            
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }        
    } 
    
    protected void createPreferenceCalculator()
    {
        try{
            if(!StrUtl.isEmptyTrimmed(_prefInfo.getCalcImplClass())){
                Class calcClass = Class.forName(_prefInfo.getCalcImplClass());
                _prefInfo.setPrefCalculator((IPrefCalculator)calcClass.newInstance());
            }
            else{ //set default impl
                if(_prefInfo.isPrefScoreBased()){
                    _prefInfo.setPrefCalculator(new ScoreBasedPreferenceCalculator());
                }
                else{
                    _prefInfo.setPrefCalculator(new PreferenceCalculator());
                }
            }
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public Map getAttribDimInfoMap() {
        return _attribDimInfoMap;
    }

    public PreferenceInfo getPrefInfo() {
        return _prefInfo;
    }
}
