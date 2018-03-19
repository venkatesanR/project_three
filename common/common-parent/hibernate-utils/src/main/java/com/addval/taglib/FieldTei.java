//Source file: E:\\Projects\\common\\source\\src\\com\\addval\\taglib\\FieldTei.java

package com.addval.taglib;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

public class FieldTei extends TagExtraInfo 
{
    
    /**
     * @param tagData
     * @return VariableInfo[]
     * @exception
     * @roseuid 3C99ADF3028F
     */
    public VariableInfo[] getVariableInfo(TagData tagData) 
    {
        String item = tagData.getAttributeString( "item" );
        return ( new VariableInfo[] { new VariableInfo( item, "java.lang.String", true, 0 ) } );     
    }
}
