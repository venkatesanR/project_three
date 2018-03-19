/* AddVal Technology Inc. */

package com.addval.taglib;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;

/**
 * @author
 * @version
 */
public class EditTei extends TagExtraInfo
{

    /**
     * @param tagData
     * @return VariableInfo[]
     * @exception
     * @roseuid 3B499A0D022A
     */
    public VariableInfo[] getVariableInfo(TagData tagData)
    {
            String item = tagData.getAttributeString( "item" );
            String type = "java.lang.String";
            return ( new VariableInfo[] { new VariableInfo( item, type, true, 0 ) } );
    }
}
