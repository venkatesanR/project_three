//Source file: E:\Projects\sampletag\source\src\com\addval\taglib\PagingTag.java

/* AddVal Technology Inc. */

package com.addval.taglib;

import java.sql.ResultSet;
import java.io.IOException;
import com.addval.dbutils.RSIterator;
import com.addval.dbutils.RSIterAction;
import javax.servlet.jsp.JspTagException;

/**
 * @author
 * @version
 */
public class PagingTag extends GenericBodyTag
{
    private String _position = "1";
    private int _pageSize = 0;
    private int _rowCount = 0;
    private RSIterator _rsIterator = null;
    private RSIterAction _rsIterAction = null;
    private ResultSet _resultSet = null;
    private String _formName;

    public PagingTag()
    {
    }

    /**
     * @param formName
     * @return void
     * @exception
     * @roseuid 3B44205C0374
     */
    public void setFormname(String formName)
    {
        _formName = formName;
    }

    /**
     * @param rs
     * @return void
     * @exception JspTagException
     * @roseuid 3B4420AB02EB
     */
    public void setResultset_id(String rs) throws JspTagException
    {
        Object rs_obj = pageContext.getAttribute( rs , getScope() );
        if (rs_obj != null &&  rs_obj instanceof ResultSet) {
            _resultSet = ( ResultSet ) rs_obj;
            String rowCount = (String)pageContext.getAttribute( "rowCount" , getScope() );
            _rowCount = ( rowCount != null) ? Integer.parseInt(rowCount) : 0;
        }
        else {
            throw new IllegalArgumentException(" Invalid Object id.");
       }
    }

    /**
     * @param position
     * @return void
     * @exception
     * @roseuid 3B4420AE03E0
     */
    public void setPosition(String position)
    {
        _position = position;
    }

    /**
     * @param pageAction
     * @return void
     * @exception
     * @roseuid 3B4420B20219
     */
    public void setPageaction(String pageAction)
    {
       _rsIterAction = new RSIterAction( pageAction );
    }

    /**
     * @param pageSize
     * @return void
     * @exception
     * @roseuid 3B4420B60251
     */
    public void setPagesize(String pageSize)
    {
        try {
            _pageSize = Integer.parseInt( pageSize );
        }
        catch(Exception e) {
            throw new NumberFormatException("Invalid pageSize");
        }
    }

    /**
     * @return int
     * @exception JspTagException
     * @roseuid 3B4420BA008A
     */
    public int doEndTag() throws JspTagException
    {
        try {
            _rsIterator = new RSIterator ( _resultSet.getStatement() , _position , _rsIterAction , _rowCount , _pageSize );
            StringBuffer htmlString = new StringBuffer();
            if (_rsIterator.getPageDesc().getRowCount() == 0) {
                htmlString.append( "<tr class=\"error\">" );
                htmlString.append( "<td>No record Found!!!</td> </tr>" );
            }
            else {
                htmlString.append( "<tr class=\"label\">" );
                htmlString.append( "<td>" );
                htmlString.append( "Records &nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( _rsIterator.getPageDesc().getRangeMin() );
                htmlString.append( "</font>" );
                htmlString.append( "&nbsp;-&nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( _rsIterator.getPageDesc().getRangeMax() );
                htmlString.append( "</font>" );
                htmlString.append( "&nbsp;of &nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( _rsIterator.getPageDesc().getRowCount() );
                htmlString.append( "</font>" );

                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/first.gif\" border=\"0\" alt=\"First Page\"" );
                htmlString.append( "onclick=\"javascript:setPagingAction('" );
                htmlString.append( _formName );
                htmlString.append( "','");
                htmlString.append( RSIterAction._FIRST_STR );
                htmlString.append( "'," );
                htmlString.append( _rsIterator.getPageDesc().isFirst() );
                htmlString.append( ")\">" );

                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/previous.gif\" border=\"0\" alt=\"Previous Page\"  ");
                htmlString.append( "onclick=\"javascript:setPagingAction('");
                htmlString.append( _formName );
                htmlString.append( "','");
                htmlString.append( RSIterAction._PREV_STR);
                htmlString.append( "',");
                htmlString.append( _rsIterator.getPageDesc().isFirst() );
                htmlString.append( ")\">" );

                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/next.gif\" border=\"0\" alt=\"Next Page\"  ");
                htmlString.append( "onclick=\"javascript:setPagingAction('" );
                htmlString.append( _formName );
                htmlString.append( "','" );
                htmlString.append( RSIterAction._NEXT_STR );
                htmlString.append( "'," );
                htmlString.append( _rsIterator.getPageDesc().isLast() );
                htmlString.append( ")\">" );

                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/last.gif\" border=\"0\" alt=\"Last Page\"  " );
                htmlString.append( "onclick=\"javascript:setPagingAction('" );
                htmlString.append( _formName );
                htmlString.append( "','" );
                htmlString.append( RSIterAction._LAST_STR );
                htmlString.append( "'," );
                htmlString.append( _rsIterator.getPageDesc().isLast() );
                htmlString.append( ")\">" );

                htmlString.append( "</td>" );
                htmlString.append( "</tr>" );


                int currPage = ( _rsIterator.getPageDesc().getRangeMin() / _rsIterator.getPageDesc().getPageSize() ) + 1;
                int totpages;
                if (_rsIterator.getPageDesc().getRowCount() % _rsIterator.getPageDesc().getPageSize() == 0)
                    totpages = _rsIterator.getPageDesc().getRowCount() / _rsIterator.getPageDesc().getPageSize();
                else
                    totpages = (_rsIterator.getPageDesc().getRowCount() / _rsIterator.getPageDesc().getPageSize() ) + 1;

                htmlString.append( "<tr class=\"label\">" );
                htmlString.append( "<td>" );
                htmlString.append( "Page No&nbsp;&nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( currPage );
                htmlString.append( "</font>" );
                htmlString.append( "&nbsp;of&nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( totpages );
                htmlString.append( "</font>" );
                htmlString.append( "&nbsp[" );

                for( int page=1; page<=totpages ;page++ ) {
                    if (page == currPage) {
                        htmlString.append( "<font color=\"#FF0000\" style=\"cursor: default;\">&nbsp" );
                        htmlString.append( page );
                        htmlString.append( "</font>" );
                    }
                    else {
                      int currPosition = ( page-1 ) * _rsIterator.getPageDesc().getPageSize() + 1;
                      htmlString.append( "&nbsp<a href=\"javascript:gotoPage(");
                      htmlString.append( "'" );
                      htmlString.append( RSIterAction._UNDEF_STR );
                      htmlString.append( "'," );
                      htmlString.append( currPosition );
                      htmlString.append( ")\">" );
                      htmlString.append( page );
                      htmlString.append( "</a>" );
                    }
                }
                htmlString.append( "&nbsp]" );
                htmlString.append( "</td>"  );
                htmlString.append( "</tr>"  );
            }
            htmlString.append( "<input type=\"hidden\" name=\"PAGING_ACTION\" value=\"" );
            htmlString.append( RSIterAction._FIRST_STR );
            htmlString.append( "\">" );

            htmlString.append( "<input type=\"hidden\" name=\"POSITION\" value=\"" );
            htmlString.append( _rsIterator.getPageDesc().getRangeMin() );
            htmlString.append( "\">" );

            pageContext.getOut().write( htmlString.toString() );
        }
        catch( IOException e ) {
            throw new JspTagException( "Tag Error:" + e.toString() );
        }
        catch( Exception e ) {
            throw new JspTagException( "Tag Error:" + e.toString() );
        }
        return EVAL_PAGE;
    }
}
