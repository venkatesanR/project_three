/*
 * DefaultAttribCodeCalculator.java
 *
 * Created on May 4, 2007, 11:14 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.utils.datapreferences;

import com.addval.utils.StrUtl;

/**
 *
 * @author ravi
 */
public class DefaultAttribCodeCalculator implements IAttribCodeCalculator
{
    
    /** Creates a new instance of DefaultAttribCodeCalculator */
    public DefaultAttribCodeCalculator() 
    {
    }
    
    /**
     * compares attribValue toString and to the wild card and determines if attrib code
     * can be returned. else returns null
     * 
     * @param dimInfo Attribute Dimension for the value
     * @param attribValue Value for the attribute dimension. The default implementation uses the toString() method on the object value for comparison
     *
     * @return applicable attribute code or null. The default implementation returns the attribute dimension as the code
     * if attribValue is not null and is not attribValue.toString() is not equal to the wild card.
     */
    public String determineAttribCode(AttribDimInfo dimInfo, Object attribValue)
    {
        if(attribValue == null || StrUtl.equals(attribValue.toString(), dimInfo._wildCard))return null;
        else return dimInfo.getDim();
    }
    
    public long determineAttribScore(AttribDimInfo dimInfo, AttribPreferenceDef prefDef, Object attribValue, Object otherInfo)
    {
        if(attribValue == null || StrUtl.equals(attribValue.toString(), dimInfo._wildCard))return 0;
        else if(prefDef != null) return prefDef.getPreference();
        else return 0;
    }
    
}
