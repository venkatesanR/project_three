/*
 * ScoreBasedPreferenceService.java
 *
 * Created on April 30, 2007, 4:52 PM
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
public class ScoreBasedPreferenceService extends APreferenceService
{
    
    /** Creates a new instance of ScoreBasedPreferenceService */
    public ScoreBasedPreferenceService(PreferenceInfo prefInfo, Map attribDimInfoMap) 
    {
        super(prefInfo, attribDimInfoMap);
    }
    
    /**
     * Sum scores for all non null non wildcard dims
     * @param dimValueMap key: dimension(String), value - AttribValueInfo object value for the dimension(Object)
     */
    public long calculatePreference(Map dimValueMap, Object otherInfo)
    {
        return this._prefInfo.getPrefCalculator().calculatePreference(this, dimValueMap, otherInfo);
    }  
    
    public String toString()
    {
        String lineSep = System.getProperty("line.separator");
        return lineSep + "ScoreBasedPreferenceService[prefInfo=" + lineSep + 
            this.getPrefInfo() + lineSep +
            "; attribDimInfoMap=" + lineSep + this.getAttribDimInfoMap() + "]";            
    }    
}
