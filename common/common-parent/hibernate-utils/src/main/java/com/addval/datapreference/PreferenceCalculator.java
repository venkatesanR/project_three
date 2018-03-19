/*
 * PreferenceCalculator.java
 *
 * Created on April 30, 2007, 4:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

import java.util.*;
import com.addval.utils.ListUtl;

/**
 *
 * @author ravi
 */
public class PreferenceCalculator implements IPrefCalculator 
{
    
    /** Creates a new instance of PreferenceCalculator */
    public PreferenceCalculator() {
    }
    
    public long calculatePreference(APreferenceService prefService, Map dimValueMap, Object otherInfo)
    {
        long preference = 0;
        
        //rank based pref service
        PreferenceService service = (PreferenceService)prefService;
        //the prefService must not be score based for this implementation
        List prefList = service.getPrefList();        
        Set attribCodes = service.determineAttribCodeSet(dimValueMap);
        
        return calculatePreference(service, attribCodes, otherInfo);        
    }
    
    public long calculatePreference2(APreferenceService prefService, java.util.Map dimAttribCodeMap, Object otherInfo)
    {
        return calculatePreference((PreferenceService)prefService, new HashSet(dimAttribCodeMap.values()), otherInfo); 
    }
    
    public long calculatePreference(PreferenceService prefService, Set attribCodes, Object otherInfo)
    {
        //System.out.println("attrib codes = " + attribCodes); //test

        long preference = 0;
        
        //the prefService must not be score based for this implementation
        List prefList = prefService.getPrefList();        
        
        DataPreferenceInfo prefInfo;
        //the list is ordered from pref 1 to n
        for(Iterator iter = prefList.iterator(); iter.hasNext();){
            prefInfo = (DataPreferenceInfo)iter.next();
            if(prefInfo.getPreference() > 0 && 
                    (
                        !prefInfo.isMatchAny() && attribCodes.containsAll(prefInfo.getAttribSet()) ||
                        prefInfo.isMatchAny() && ListUtl.intersectionExists(attribCodes, prefInfo.getAttribSet())
                    )
              ){
                //System.out.println("DataPreferenceInfo = " + prefInfo); //test
                
                preference += Math.pow(2, prefService.getMaxPrefValue() - prefInfo.getPreference());
            }
        }

        return preference;
    }    
}
