
/* AddVal Technology Inc. */

package com.addval.aswtaglib;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;

/**
 * @author
 * @version
 */
public class IfTag extends GenericBodyTag
{
    private boolean _flag;

    public IfTag()
    {
        _flag = true;
    }

    /**
     * @param obj
     * @return void
     * @exception
     * @roseuid 3B42E51402C7
     */
    public void setExpr(Object obj)
    {
        if (obj instanceof Boolean)
            setExpr( ( ( Boolean ) obj ).booleanValue() );
        else
            setExpr( parseBoolean( obj.toString(),  true ) );
    }

    /**
     * @param flag
     * @return void
     * @exception
     * @roseuid 3B42E538000C
     */
    public void setExpr(boolean flag)
    {
        _flag = flag;
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42E58A010E
     */
    public int doStartTag() throws JspTagException
    {
        return _flag ? 2 : 0;
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42E58A0140
     */
    public int doEndTag() throws JspTagException
    {
        if (_flag) {
            try {
                getBodyContent().writeOut( pageContext.getOut() );
            }
            catch( IOException ioexception ) {
                release();
                throw new JspTagException( ioexception.getMessage() );
            }
        }
        release();
        return EVAL_PAGE;
    }

    /**
     * @return void
     * @exception
     * @roseuid 3B42E58A0168
     */
    public void release()
    {
        _flag = true;
        super.release();
    }
}
