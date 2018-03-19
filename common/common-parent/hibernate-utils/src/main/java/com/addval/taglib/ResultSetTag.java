
/* AddVal Technology Inc. */

package com.addval.taglib;

import java.sql.ResultSet;
import javax.servlet.jsp.JspTagException;

/**
 * @author
 * @version
 */
public class ResultSetTag extends GenericBodyTag
{
    private ResultSet _resultSet;
    private String _rowCount;

    /**
     * @param obj
     * @return void
     * @exception
     * @roseuid 3B42E2000205
     */
    public void setObject(Object obj)
    {
        if (obj instanceof ResultSet)
            _resultSet = ( ResultSet ) obj;
        else
            throw new IllegalArgumentException ( "Object should be the instance of type java.sql.ResultSet." );
    }

    /**
     * @param rowCount
     * @return void
     * @exception
     * @roseuid 3B43F522023C
     */
    public void setRowcount(int rowCount)
    {
        _rowCount = String.valueOf( rowCount );
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B42E204002A
     */
    public int doEndTag() throws JspTagException
    {
        if ( getId() != null )
            pageContext.setAttribute( getId(),  _resultSet,  getScope() );
        pageContext.setAttribute( "rowCount",  _rowCount,  getScope() );
        release();
     return EVAL_PAGE;
    }
}
