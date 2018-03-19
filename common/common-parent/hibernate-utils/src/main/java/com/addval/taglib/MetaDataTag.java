/* AddVal Technology Inc. */

package com.addval.taglib;

import com.addval.metadata.EditorMetaData;
import javax.servlet.jsp.JspTagException;

/**
 * @author
 * @version
 */
public class MetaDataTag extends GenericBodyTag
{
    private EditorMetaData _editorMetaData;

    /**
     * @param obj
     * @return void
     * @exception
     * @roseuid 3B42E15402E4
     */
    public void setObject(Object obj)
    {
        if (obj instanceof EditorMetaData)
            _editorMetaData = ( EditorMetaData ) obj;
        else
            throw new IllegalArgumentException( "Object should be the instance of com.addval.metadata.EditorMetaData." );
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42E1CA017C
     */
    public int doEndTag() throws JspTagException
    {
        if (getId() != null )
            pageContext.setAttribute( getId(),  _editorMetaData,  getScope());
        release();
        return EVAL_PAGE;
    }
}
