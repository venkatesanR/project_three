//Source file: c:\\projects\\Common\\src\\client\\source\\com\\addval\\taglib\\IteratorTag.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.taglib;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.sql.ResultSet;
import com.addval.dbutils.RSIterator;
import com.addval.dbutils.RSIterAction;

/**
 * Displays multiple rows.
 */
public class IteratorTag extends GenericBodyTag {
    private String _item;
    private EditorMetaData _editorMetaData = null;
    private ResultSet _resultSet = null;
    private String _position = "1";
    private int _pageSize = 0;
    private int _rowCount = 0;
    private boolean _paging = false;
    private String _editJsp;
    private RSIterator _rsIterator = null;
    private RSIterAction _rsIterAction = new RSIterAction("UNDEF");
    private String _rowColor = "row0";
    private String _whereClause;
    private String _isEditable = "false";
    private String _isDeletable = "false";
    private boolean _footer = true;

    /**
     * Access method for the _rowColor property.
     *
     * @return   the current value of the _rowColor property
     */
    public String getRowColor() {
        return _rowColor;
    }

    /**
     * Sets the value of the _rowColor property.
     *
     * @param aRowColor the new value of the _rowColor property
     */
    public void setRowColor(String aRowColor) {
        _rowColor = aRowColor;
        }

    /**
     * Access method for the _isEditable property.
     *
     * @return   the current value of the _isEditable property
     */
    public String getIsEditable() {
        return _isEditable;
    }

    /**
     * Sets the value of the _isEditable property.
     *
     * @param aIsEditable the new value of the _isEditable property
     */
    public void setIsEditable(String aIsEditable) {
        _isEditable = aIsEditable;
        }

    /**
     * Access method for the _isDeletable property.
     *
     * @return   the current value of the _isDeletable property
     */
    public String getIsDeletable() {
        return _isDeletable;
    }

    /**
     * Sets the value of the _isDeletable property.
     *
     * @param aIsDeletable the new value of the _isDeletable property
     */
    public void setIsDeletable(String aIsDeletable) {
        _isDeletable = aIsDeletable;
        }

    /**
     * Determines if the _footer property is true.
     *
     * @return   <code>true<code> if the _footer property is true
     */
    public boolean getFooter() {
        return _footer;
        }

    /**
     * Sets the value of the _footer property.
     *
     * @param aFooter the new value of the _footer property
     */
    public void setFooter(boolean aFooter) {
        _footer = aFooter;
        }

    /**
     * @param item
     * @roseuid 3B6EFE7A0341
     */
    public void setItem(String item) {
        _item = item;
    }

    /**
     * @param md
     * @roseuid 3B6EFE7A03B9
     */
    public void setMetadata_id(String md) {
        Object md_obj = pageContext.getAttribute ( md, getScope() );
        if ( md_obj!=null && md_obj instanceof EditorMetaData )
            _editorMetaData = (EditorMetaData) md_obj;
        else
            throw new IllegalArgumentException("ListTag: Invalid Object id.");
    }

    /**
     * @param rs
     * @roseuid 3B6EFE7B002B
     */
    public void setResultset_id(String rs) {
        Object rs_obj = pageContext.getAttribute( rs,  getScope() );
        if (rs_obj != null &&  rs_obj instanceof ResultSet)
        {
            _resultSet = ( ResultSet ) rs_obj;
            String rowCount = ( String ) pageContext.getAttribute( "rowCount",  getScope() );
            _rowCount = ( rowCount!=null ? Integer.parseInt(rowCount) : 0 );

            // System.out.println ( "in setResultset_id  rowCount=" + _rowCount );
        }
        else
        {
            throw new IllegalArgumentException("ListTag:Invalid Object id." );
        }
    }

    /**
     * @param position
     * @roseuid 3B6EFE7B0053
     */
    public void setPosition(String position) {
        _position = position;
    }

    /**
     * @param pageAction
     * @roseuid 3B6EFE7B0072
     */
    public void setPageaction(String pageAction) {
        _rsIterAction = new RSIterAction ( pageAction );
    }

    /**
     * @param pageSize
     * @roseuid 3B6EFE7B00FD
     */
    public void setPagesize(String pageSize) {
        try
        {
            _pageSize = Integer.parseInt( pageSize );
        }
        catch(Exception e)
        {
            throw new NumberFormatException("ListTag:Invalid pageSize");
        }
    }

    /**
     * @throws JspTagException
     * @roseuid 3B6EFE7B012F
     */
    private void setPage() throws JspTagException {
        try
        {
            _paging = true;
            _rsIterator = new RSIterator ( _resultSet.getStatement(), _position,  _rsIterAction,  _rowCount,  _pageSize );
            _whereClause = getWhere();
        }
        catch ( Exception e )
        {
            throw new JspTagException( e.getMessage() );
        }
    }

    /**
     * @param editJsp
     * @roseuid 3B6EFE7B0144
     */
    public void setEditjsp(String editJsp)
    {
        _editJsp = editJsp;
    }

    /**
     * @return int
     * @throws JspTagException
     * @roseuid 3B6EFE7B016D
     */
    public int doAfterBody() throws JspTagException
    {
        try
        {
//            pageContext.getOut().write ( "</tr>" );
            if (_rsIterator.next() && _editorMetaData.getDisplayableColumns() != null)
            {
                _rowColor = toggleColor( _rowColor );
//                pageContext.getOut().write ( "<tr class= \""+_rowColor+"\">" );

                pageContext.setAttribute( _item, prepareRow() );

                return EVAL_BODY_BUFFERED;
            }
            else {
                return SKIP_BODY;
            }
        }
        catch ( Exception ioexception )
        {
            release();
            throw new JspTagException( ioexception.getMessage() );
        }
    }

    /**
     * @return int
     * @throws JspTagException
     * @roseuid 3B6EFE7B018A
     */
    public int doStartTag() throws JspTagException {
//        if ( !_paging )
            setPage();

        if ( _rsIterator.next() && _editorMetaData.getDisplayableColumns() != null )
        {
            return EVAL_BODY_BUFFERED;
        }
        else
        {
            return SKIP_BODY;
        }
    }

    /**
     * @return int
     * @throws JspTagException
     * @roseuid 3B6EFE7B019E
     */
    public int doEndTag() throws JspTagException {
        try
        {
            if ( getBodyContent() != null )
                getBodyContent().writeOut ( pageContext.getOut() );
            StringBuffer htmlString = new StringBuffer();
            int colspan = _editorMetaData.getDisplayableColumns() == null ? 0 : _editorMetaData.getDisplayableColumns().size();
            if ( _rsIterator.getPageDesc().getRowCount() == 0 )
            {
                htmlString.append( "<tr class=\"error\">" );
                if (_editorMetaData.isEditable())
                    htmlString.append( "<td colspan=\""+(colspan+ 2) + "\">" );
                else
                    htmlString.append( "<td colspan=\""+colspan+ "\">" );
                htmlString.append( "No record Found!!!</td> </tr>" );
            }
            else if ( _footer )
            {
                htmlString.append( "<tr onselectstart=\"return false;\" >" );
                if (_editorMetaData.isEditable())
                    htmlString.append( "<td class=\"label\" colspan=\""+(colspan+ 2)+ "\">" );
                else
                    htmlString.append( "<td class=\"label\" colspan=\""+colspan+ "\">" );
                htmlString.append( "<nobr>" );
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
                htmlString.append( "</nobr>" );
                htmlString.append( "<nobr>" );
                htmlString.append( "<a href=\"javascript:setPagingAction('" );
                htmlString.append( "_formName" );
                htmlString.append( "','");
                htmlString.append( RSIterAction._FIRST_STR );
                htmlString.append( "'," );
                htmlString.append( _rsIterator.getPageDesc().isFirst() );
                htmlString.append( ")\">" );
                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/first.gif\" border=\"0\" alt=\"First Page\" />" );
                htmlString.append( "</a>" );
                htmlString.append( "<a href=\"javascript:setPagingAction('" );
                htmlString.append( "_formName" );
                htmlString.append( "','" );
                htmlString.append( RSIterAction._PREV_STR );
                htmlString.append( "',");
                htmlString.append( _rsIterator.getPageDesc().isFirst() );
                htmlString.append( ")\">" );
                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/previous.gif\" border=\"0\" alt=\"Previous Page\"  />" );
                htmlString.append( "</a>" );
                htmlString.append( "<a href=\"javascript:setPagingAction('" );
                htmlString.append( "_formName" );
                htmlString.append( "','" );
                htmlString.append( RSIterAction._NEXT_STR );
                htmlString.append( "'," );
                htmlString.append( _rsIterator.getPageDesc().isLast() );
                htmlString.append( ")\">" );
                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/next.gif\" border=\"0\" alt=\"Next Page\" /> ");
                htmlString.append( "</a>" );
                htmlString.append( "<a href=\"javascript:setPagingAction('" );
                htmlString.append( "_formName" );
                htmlString.append( "','" );
                htmlString.append( RSIterAction._LAST_STR );
                htmlString.append( "'," );
                htmlString.append( _rsIterator.getPageDesc().isLast() );
                htmlString.append( ")\">" );
                htmlString.append( "<img style=\"cursor=hand\" src=\"../images/last.gif\" border=\"0\" alt=\"Last Page\"  />" );
                htmlString.append( "</a>" );
                int currPage = ( _rsIterator.getPageDesc().getRangeMin() / _rsIterator.getPageDesc().getPageSize() ) + 1;
                int totpages;
                if (_rsIterator.getPageDesc().getRowCount() % _rsIterator.getPageDesc().getPageSize() == 0)
                    totpages = _rsIterator.getPageDesc().getRowCount() / _rsIterator.getPageDesc().getPageSize();
                else
                    totpages = (_rsIterator.getPageDesc().getRowCount() / _rsIterator.getPageDesc().getPageSize() ) + 1;
                htmlString.append( "</nobr>" );
                htmlString.append( "<nobr>" );
                htmlString.append( "&nbsp;&nbsp;Page&nbsp;&nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( currPage );
                htmlString.append( "</font>" );
                htmlString.append( "&nbsp;of&nbsp;" );
                htmlString.append( "<font color=\"#0000FF\">" );
                htmlString.append( totpages );
                htmlString.append( "</font>" );
                htmlString.append( "&nbsp;[" );

				int minpage = 1;
				int maxpage = totpages;

				if ((currPage-5) > 1)
				{
					minpage = currPage - 5;
				}

				if ((currPage+5) < totpages)
				{
					maxpage = currPage + 5;
				}

                for (int page=minpage; page<=maxpage ;page++)
                {
                    if ( page == currPage )
                    {
                        htmlString.append( "<font color=\"#FF0000\" style=\"cursor: default;\">&nbsp;" );
                        htmlString.append( page );
                        htmlString.append( "</font>" );
                    }
                    else
                    {
                        int currPosition = ( page-1 ) * _rsIterator.getPageDesc().getPageSize() + 1;
                        htmlString.append( "&nbsp;<a href=\"javascript:gotoPage(");
                        htmlString.append( "'" );
                        htmlString.append( RSIterAction._UNDEF_STR );
                        htmlString.append( "'," );
                        htmlString.append( currPosition );
                        htmlString.append( ")\">" );
                        htmlString.append( page );
                        htmlString.append( "</a>" );
                    }
                }
                htmlString.append( "&nbsp;]" );
                htmlString.append( "</td>"  );
                htmlString.append( "</nobr>" );
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
        catch(IOException ioexception)
        {
            release();
            throw new JspTagException( ioexception.getMessage() );
        }
        finally
        {
            if (_item != null)
                pageContext.removeAttribute( _item );
        }
        return EVAL_PAGE;
    }

    /**
     * @roseuid 3B6EFE7B01B2
     */
    public void release() {
        _item           = null;
        _editorMetaData = null;
        _resultSet      = null;
        _rsIterator     = null;
        super.release();
    }

    /**
     * @return java.lang.String
     * @roseuid 3B6EFE7B01C6
     */
    private String getWhere() {
        String queryString = "";
        for ( java.util.Enumeration enumeration = pageContext.getRequest().getParameterNames(); enumeration.hasMoreElements(); ) {
            String columnName  = (String) enumeration.nextElement();
            if ( columnName.endsWith( "_lookUp" ) ||
                 columnName.endsWith( "operator_lookUp" ) &&
                 !columnName.equals( "EDITOR_NAME" ) &&
                 !columnName.equals( "EDITOR_TYPE" ) ||
                 columnName.equals( "POSITION" ) ||
                 columnName.equals( "DETAILS" )
               )
            {
                String columnValue = pageContext.getRequest().getParameter( columnName );
                if (columnValue != null && columnValue.length() != 0)
                    queryString += "&" + columnName + "=" + pageContext.getRequest().getParameter( columnName ). trim();
            }
            else if ( columnName.equals("PAGING_ACTION") )
            {
                queryString += "&" + columnName + "=" + RSIterAction._CURR_STR;
            }
        }
        queryString += "&EDITOR_NAME=" + _editorMetaData.getName();
        queryString += "&EDITOR_TYPE=" + _editorMetaData.getType();

        // System.out.println ( "in getWhere()      queryString: " + queryString );
        return queryString;
    }

    /**
     * @param color
     * @return java.lang.String
     * @roseuid 3B6EFE7B01DA
     */
    private String toggleColor(String color) {
        return ( _rowColor.equals("row0") ? "row1" : "row0" );
    }

    /**
     * @return String
     * @throws JspTagException
     * @roseuid 3B6F0A1B00F3
     */
    private String prepareRow() throws JspTagException {
        try
        {
            StringBuffer htmlString = new StringBuffer();
            for (java.util.Iterator iter = _editorMetaData.getDisplayableColumns().iterator(); iter.hasNext();)
            {
                ColumnMetaData columnMetaData = ( ColumnMetaData ) iter.next();
                String columnValue = ( _rsIterator.getString(columnMetaData.getName())==null ? "" : _rsIterator.getString(columnMetaData.getName()) );
                htmlString.append ( "<td>" );
                htmlString.append ( columnValue );
                htmlString.append ( "</td>" );
            }

            if ( _isEditable.equalsIgnoreCase("true") || _isDeletable.equalsIgnoreCase("true") )
            {
                StringBuffer buildQuery = new StringBuffer( _whereClause );
                StringBuffer deleteQuery  = new StringBuffer ();
                if (_editorMetaData.getKeyColumns() != null)
                {
                    for (java.util.Iterator iter = _editorMetaData.getKeyColumns().iterator(); iter.hasNext();)
                    {
                        String columnName = ((ColumnMetaData)iter.next()).getName();
                        buildQuery.append( "&" );
                        buildQuery.append( columnName + "_KEY" );
                        buildQuery.append( "=" );
                        buildQuery.append( _rsIterator.getString( columnName ) );
                        deleteQuery.append( "||" );
                        deleteQuery.append( columnName );
                        deleteQuery.append( "=" );
                        deleteQuery.append( _rsIterator.getString( columnName ) );
                    }
                }
                String queryString = _editJsp;
                if (!buildQuery.toString().equals( "" ))
                    queryString += "?"+buildQuery.toString().substring( 1 );
                String deleteKey = deleteQuery.toString ().equals( "" ) ? "" : deleteQuery.toString().substring( 2 );
                if ( _isEditable.equalsIgnoreCase("true") )
                {
                    htmlString.append( "<td>" );
                    htmlString.append( "<img  style=\"cursor=hand\" src = \'../images/edit.gif\' BORDER=\'0\' WIDTH=\'20\' HEIGHT=\'13\' alt=\'Edit Record\' " );
                    htmlString.append( "onclick=\"document.body.style.cursor=\'wait\';javascript:launchEdit('" );
                    htmlString.append( queryString );
                    htmlString.append( "')\">" );
                    htmlString.append( "</td>" );
                }
                if ( _isDeletable.equalsIgnoreCase("true") )
                {
                    htmlString.append( "<td>"  );
                    htmlString.append( "<img  style=\"cursor=hand\" src = \'../images/delete.gif\' BORDER=\'0\' WIDTH=\'20\' HEIGHT=\'13\' alt=\'Delete Record\' " );
                    htmlString.append( "onclick=\"document.body.style.cursor=\'wait\';javascript:deleteRecord('" );
                    htmlString.append( deleteKey );
                    htmlString.append( "')\">" );
                    htmlString.append( "</td>" );
                }
            }

            return htmlString.toString();
        }
        catch ( Exception exception)
        {
            release();
            throw new JspTagException( exception.getMessage() );
        }
    }

    /**
     * @throws JspTagException
     * @roseuid 3B6F15E0033A
     */
    public void doInitBody() throws JspTagException {
        try
        {
            _rowColor = toggleColor( _rowColor );
//            pageContext.getOut().write ( "<tr class= \""+_rowColor+"\">" );

            pageContext.setAttribute ( _item, prepareRow() );
        }
        catch ( Exception ioexception )
        {
            release();
            throw new JspTagException( ioexception.getMessage() );
        }
    }
}
