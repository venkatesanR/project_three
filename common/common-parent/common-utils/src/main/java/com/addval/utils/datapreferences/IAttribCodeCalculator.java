/*
 * IAttribCodeCalculator.java
 *
 * Created on April 30, 2007, 4:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.datapreferences;

/**
 * Interface for preference/score calculation of a data entity's attribute
 *
 * @author ravi
 */
public interface IAttribCodeCalculator {
    
    /**
     * Should return null if attrib value is null or matched the wildcard
     */
    public String determineAttribCode(AttribDimInfo dimInfo, Object attribValue);
    
    /**
     * only used for score based preference. If the selection is preference based,
     * the implementation can throw an UnsupportedOperation exception
     * @param otherInfo can be used to customize score computation by passing in the application 
     * specific information
     */
    public long determineAttribScore(AttribDimInfo dimInfo, AttribPreferenceDef prefDef, Object attribValue, Object otherInfo);
}
