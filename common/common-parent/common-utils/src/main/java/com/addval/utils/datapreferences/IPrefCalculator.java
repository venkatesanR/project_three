/*
 * IPrefCalculator.java
 *
 * Created on April 30, 2007, 1:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.datapreferences;

/**
 * Interface for preference/score calculation of a data entity
 * This calculation is a function on the preference/score of all 
 * attributes of that data entity defined in the system.
 *
 * @author ravi
 */
public interface IPrefCalculator {
    
    /** 
     * returns score or preference when values of atrib dims are known
     * @param attribValueMap Map(key: attribute dim name, value: AttribValueInfo object attribute value or null if none)
     * @return score or preference
     */
    public long calculatePreference(APreferenceService prefService, java.util.Map dimValueMap, Object otherInfo);        
}
