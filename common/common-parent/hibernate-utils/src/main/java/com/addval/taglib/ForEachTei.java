
/* AddVal Technology Inc. */

package com.addval.taglib;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author
 * @version
 */
public class ForEachTei extends TagExtraInfo
{

    /**
     * @param tagData
     * @return VariableInfo[]
     * @exception
     * @roseuid 3B42E7F00111
     */
    public VariableInfo[] getVariableInfo(TagData tagData)
    {
           String item = tagData.getAttributeString( "item" );
           String type = "java.lang.String";
           return (new VariableInfo[] { new VariableInfo( item, type, true, 0) } );
    }
}
