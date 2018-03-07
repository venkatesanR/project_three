/*
 * ScoreBasedPreferenceCalculator.java
 *
 * Created on April 30, 2007, 4:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.datapreferences;

import java.util.*;
import com.addval.utils.ListUtl;

/**
 *
 * @author ravi
 */
public class ScoreBasedPreferenceCalculator implements IPrefCalculator
{
    protected ScoreBasedPreferenceService _prefService;
    
    /** Creates a new instance of ScoreBasedPreferenceCalculator */
    public ScoreBasedPreferenceCalculator() {
    }
    
    public long calculatePreference(APreferenceService prefService, Map dimValueMap, Object otherInfo)
    {
       long score = 0;
       
       AttribDimInfo dimInfo;
       Map.Entry entry;
       AttribPreferenceDef prefDef;
       String attribCode;
       AttribValueInfo value;
       
       for(Iterator iter = dimValueMap.entrySet().iterator(); iter.hasNext();){
           entry = (Map.Entry)iter.next();
           dimInfo = (AttribDimInfo)prefService.getAttribDimInfoMap().get(entry.getKey());
           value = (AttribValueInfo)entry.getValue();
           
           if(dimInfo == null || value == null)continue; //this dim is not part of preference spec
           
           attribCode = value.getAttribCode();
           if(attribCode == null)attribCode = dimInfo.getAttribCodeCalculator().determineAttribCode(dimInfo, value.getValue());
           if(attribCode == null)continue;
           
           prefDef = (AttribPreferenceDef)ListUtl.first(prefService.getPrefInfo().getAttribPrefDefList(attribCode));
           score += dimInfo.getAttribCodeCalculator().determineAttribScore(dimInfo, prefDef, value.getValue(), otherInfo);
       }
       
       return score;
    }       
}
