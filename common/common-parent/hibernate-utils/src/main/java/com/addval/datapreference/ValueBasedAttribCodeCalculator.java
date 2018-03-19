/*
 * ValueBasedAttribCodeCalculator.java
 *
 * Created on January 4, 2008, 1:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

import com.addval.utils.*;

/**
 * To compute the attrib code, this class concatinates
 * the value to the dimension name
 *
 * @author ravi
 */
public class ValueBasedAttribCodeCalculator implements IAttribCodeCalculator 
{
    
    /** Creates a new instance of ValueBasedAttribCodeCalculator */
    public ValueBasedAttribCodeCalculator() 
    {
    }
    
    public String determineAttribCode(AttribDimInfo dimInfo, Object attribValue)
    {        
        if(dimInfo == null ||attribValue == null || StrUtl.equals(attribValue.toString(), dimInfo._wildCard))return null;
        else return dimInfo.getDim() + "=" + attribValue.toString();
    }
    
    public long determineAttribScore(AttribDimInfo dimInfo, AttribPreferenceDef prefDef, Object attribValue, Object otherInfo)
    {
        if(dimInfo == null || attribValue == null || StrUtl.equals(attribValue.toString(), dimInfo._wildCard))return 0;
        else if(prefDef != null) return prefDef.getPreference();
        else return 0;
    }        
}
