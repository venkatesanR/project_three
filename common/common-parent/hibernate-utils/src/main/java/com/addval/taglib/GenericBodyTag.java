
/* AddVal Technology Inc. */

package com.addval.taglib;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

/**
 * @author
 * @version
 */
public abstract class GenericBodyTag extends GenericTag implements BodyTag
{
    protected BodyContent _bodyContent;

    public GenericBodyTag()
    {
        _bodyContent = null;
    }

    /**
     * @param bodyContent
     * @return void
     * @exception
     * @roseuid 3B42DB78034C
     */
    public void setBodyContent(BodyContent bodyContent)
    {
        _bodyContent = bodyContent;
    }

    /**
     * @return BodyContent
     * @exception
     * @roseuid 3B42DB9A005B
     */
    public BodyContent getBodyContent()
    {
        return _bodyContent;
    }

    /**
     * @return void
     * @exception JspTagException
     * @roseuid 3B42DC0B0072
     */
    public void doInitBody() throws JspTagException
    {
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42DC860097
     */
    public int doAfterBody() throws JspTagException
    {
        return SKIP_BODY;
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42DC990397
     */
    public int doStartTag() throws JspTagException
    {
        return EVAL_BODY_BUFFERED;
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42DCA7027F
     */
    public int doEndTag() throws JspTagException
    {
        return EVAL_PAGE;
    }

    /**
     * @return void
     * @exception
     * @roseuid 3B42DCD80199
     */
    public void release()
    {
        _bodyContent = null;
        super.release( );
    }

    /**
     * @param bool
     * @param flag
     * @return boolean
     * @exception
     * @roseuid 3B4308880079
     */
    protected boolean parseBoolean(String bool, boolean flag)
    {
        if (bool != null)
            return bool.equalsIgnoreCase( "yes" ) || bool.equalsIgnoreCase( "true" );
        else
            return flag;
    }
}
