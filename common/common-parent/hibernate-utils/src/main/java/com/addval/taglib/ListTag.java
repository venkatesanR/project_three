//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\taglib\\ListTag.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.taglib;

import java.sql.ResultSet;
import java.io.IOException;
import com.addval.metadata.ColumnMetaData;
import java.util.Iterator;
import java.util.Vector;
import java.text.DecimalFormat;

import com.addval.metadata.ColumnDataType;
import com.addval.metadata.EditorMetaData;
import com.addval.dbutils.RSIterator;
import com.addval.dbutils.RSIterAction;
import javax.servlet.jsp.JspTagException;
import org.apache.commons.lang.StringEscapeUtils;

public class ListTag extends GenericBodyTag
{
	private String _position = "1";
	private int _pageSize = 0;
	private int _rowCount = 0;
	private boolean _paging = false;
	private String _rowColor = "row0";
	private String _whereClause;
	private boolean _footer = true;
    private boolean _enableMouseover = true;
    private String _addlink = "";
	private String _addlinkImage= "";
	private String _cancellink = "";
	private String _cancellinkImage= "";
	private boolean _hasViewPermission = true;
	private boolean _hasEditPermission = true;
	private String _viewrole = "";
	private String _editrole = "";
	private EditorMetaData _editorMetaData = null;
	private ResultSet _resultSet = null;
	private RSIterator _rsIterator = null;
	private RSIterAction _rsIterAction = new RSIterAction ("UNDEF");
	private int _nbrOfLinkColumns = 0;
	private boolean _multidelete = false;
	private String _deletelink = "";
	private String _deletelinkImage= "";
	private final String _UTF8 = "UTF-8";

    public boolean isMultiDelete() {
        return _multidelete;
    }
    public void setMultiDelete(boolean multidelete) {
        _multidelete = multidelete;
    }

    public boolean isEnableMouseover() {
        return _enableMouseover;
    }

    public void setEnableMouseover(boolean enableMouseover) {
        this._enableMouseover = enableMouseover;
    }


    /**
	 * Determines if the _footer property is true.
	 *
	 * @return   <code>true<code> if the _footer property is true
	 */
	public boolean getFooter()
	{
        return _footer;
	}

	/**
	 * Sets the value of the _footer property.
	 *
	 * @param aFooter the new value of the _footer property
	 */
	public void setFooter(boolean aFooter)
	{
		_footer = aFooter;
	}

	/**
	 * Access method for the _addlink property.
	 *
	 * @return   the current value of the _addlink property
	 */
	public String getAddlink()
	{
		return _addlink;
	}

	/**
	 * Sets the value of the _addlink property.
	 *
	 * @param aAddlink the new value of the _addlink property
	 */
	public void setAddlink(String aAddlink)
	{
		_addlink = aAddlink;
	}

	/**
	 * Access method for the _addlinkImage property.
	 *
	 * @return   the current value of the _addlinkImage property
	 */
	public String getAddlinkImage()
	{
		return _addlinkImage;
	}

	/**
	 * Sets the value of the _addlinkImage property.
	 *
	 * @param aAddlink the new value of the _addlinkImage property
	 */
	public void setAddlinkImage(String aAddlinkImage)
	{
		_addlinkImage = aAddlinkImage;
	}

	/**
	 * Access method for the _cancellink property.
	 *
	 * @return   the current value of the _cancellink property
	 */
	public String getCancellink()
	{
		return _cancellink;
	}

	/**
	 * Sets the value of the _cancellink property.
	 *
	 * @param aAddlink the new value of the _cancellink property
	 */
	public void setCancellink(String aCancellink)
	{
		_cancellink = aCancellink;
	}

	/**
	 * Access method for the _cancellinkImage property.
	 *
	 * @return   the current value of the _cancellinkImage property
	 */
	public String getCancellinkImage()
	{
		return _cancellinkImage;
	}

	/**
	 * Sets the value of the _cancellinkImage property.
	 *
	 * @param aAddlink the new value of the _cancellinkImage property
	 */
	public void setCancellinkImage(String aCancellinkImage)
	{
		_cancellinkImage = aCancellinkImage;
	}

	/**
	 * Access method for the _addlinkImage property.
	 *
	 * @return   the current value of the _addlinkImage property
	 */
	public String getDeletelinkImage()
	{
		return _deletelinkImage;
	}

	/**
	 * Sets the value of the _addlinkImage property.
	 *
	 * @param aAddlink the new value of the _addlinkImage property
	 */
	public void setDeletelinkImage(String aDeletelinkImage)
	{
		_deletelinkImage = aDeletelinkImage;
	}

	/**
	 * @param md
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D41100271
	 */
	public void setMetadata_id(String md) throws JspTagException
	{
        Object md_obj = pageContext.getAttribute ( md, getScope() );
        if ( md_obj!=null && md_obj instanceof EditorMetaData )
            _editorMetaData = (EditorMetaData) md_obj;
        else
            throw new IllegalArgumentException("ListTag: Invalid Object id.");
	}

	/**
	 * @param rs
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D411501BA
	 */
	public void setResultset_id(String rs) throws JspTagException
	{
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
	 * @roseuid 3B4D4119036E
	 */
	public void setPosition(String position)
	{
        _position = position;
	}

	/**
	 * @param pageAction
	 * @roseuid 3B4D41200242
	 */
	public void setPageaction(String pageAction)
	{
        _rsIterAction = new RSIterAction ( pageAction );
	}

	/**
	 * @param pageSize
	 * @roseuid 3B4D4127026A
	 */
	public void setPagesize(String pageSize)
	{
        try {
            _pageSize = Integer.parseInt( pageSize );
        }
        catch(Exception e) {
            throw new NumberFormatException("ListTag:Invalid pageSize");
        }
	}

	/**
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D412B0356
	 */
	private void setPage() throws JspTagException
	{
        try {
            _paging = true;
            _rsIterator = new RSIterator ( _resultSet.getStatement(), _position,  _rsIterAction,  _rowCount,  _pageSize );
            _whereClause = getWhere();
        }
        catch ( Exception e ) {
            release();
            throw new JspTagException( e.getMessage() );
        }
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D41390003
	 */
	public int doAfterBody() throws JspTagException
	{
        return SKIP_BODY;
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D413D00DB
	 */
	public int doStartTag() throws JspTagException
	{

		final String DELETE = "DELETE";

        try {
//          if ( !_paging )
                setPage();


			if ((_viewrole != null) && (_viewrole.length() > 0))
			{
				javax.servlet.http.HttpServletRequest httpreq = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();

				if (httpreq.isUserInRole(_viewrole))
				    _hasViewPermission = true;
				else
				    _hasViewPermission = false;
			}


			if ((_editrole != null) && (_editrole.length() > 0))
			{
				javax.servlet.http.HttpServletRequest httpreq = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();

				if (httpreq.isUserInRole(_editrole))
					_hasEditPermission = true;
				else
					_hasEditPermission = false;
			}



			if (_hasViewPermission)
			{
				// Flag to count linkColumns
				boolean countLinkColumns = false;
				while ( _rsIterator.next() && _editorMetaData.getDisplayableColumns() != null )
				{

					StringBuffer buildQuery  = new StringBuffer( _whereClause );
					StringBuffer keys = new StringBuffer();
					StringBuffer lookupStr = new StringBuffer();
					if (_editorMetaData.getKeyColumns() != null)
					{
						for (java.util.Iterator iter = _editorMetaData.getKeyColumns().iterator(); iter.hasNext();)
						{
							String columnName = ((ColumnMetaData)iter.next()).getName();
							keys.append ( "&" );
							keys.append ( columnName + "_KEY" );
							keys.append ( "=" );
							keys.append ( _rsIterator.getString( columnName ) );

							lookupStr.append ( "&" );
							lookupStr.append ( columnName + "_lookUp" );
							lookupStr.append ( "=" );
							lookupStr.append ( _rsIterator.getString( columnName ) );
						}
					}

                    int rollupGroupingCount = 0;
                    ColumnMetaData groupingColumnMetaData = null;
                    for (java.util.Iterator iter = _editorMetaData.getDisplayableColumns().iterator(); iter.hasNext();) {
                        String columnName = ((ColumnMetaData)iter.next()).getName();
                        String groupingColumnName = "";
                        if ( columnName.length() <= 28 )
	                        groupingColumnName = "G_" + columnName;
	                    else
	                    	groupingColumnName = "G_" + columnName.substring( 0 , 28 );
                        groupingColumnMetaData = _editorMetaData.getColumnMetaData( groupingColumnName );
                        if( groupingColumnMetaData != null ){
                            rollupGroupingCount += "1".equalsIgnoreCase( _rsIterator.getString( groupingColumnName ) ) ? 1 : 0;
                        }
                    }

                    _rowColor = toggleColor( _rowColor );
                    String rollupRow = rollupGroupingCount > 0 ? "_subtotal" : "";

                    pageContext.getOut().write ( "<tr id=\"row\" class= \""+_rowColor + rollupRow + "\" ");

                    if( isEnableMouseover() ) {
                        pageContext.getOut().write ( " onClick=\"setOnClick( this )\" onMouseOver = \"setOnMouseOver( this )\" onMouseOut = \"setOnMouseOut( this ) \" " );
                    }

                    pageContext.getOut().write ( ">" );

                    StringBuffer htmlString = new StringBuffer();
					String queryString = null;
					boolean isDeleteColumn = false;
					for (java.util.Iterator iter = _editorMetaData.getDisplayableColumns().iterator(); iter.hasNext();) {
						ColumnMetaData columnMetaData = ( ColumnMetaData ) iter.next();
						if (columnMetaData.getType() == ColumnDataType._CDT_LINK)  {

						if (!countLinkColumns)
							_nbrOfLinkColumns++;

						    if (_hasEditPermission)
						    {

								String linkJsp = columnMetaData.getRegexp();
								if (linkJsp == null || linkJsp.equals( "" ))
									linkJsp = columnMetaData.getValue();
								if (linkJsp == null || linkJsp.equals( "" ))
									throw new JspTagException( "Target Link file for columnName  " + columnMetaData.getName() + " is not specified in RegExp column of Table!" );

								// if the link file has a query string already
								// add & queryString and the keys else
								// add the ? and then the queryString and keys

								linkJsp += linkJsp.indexOf("?") != -1 ? "&" : "?";

								if(buildQuery.toString().length() > 0 )
									linkJsp +=  buildQuery.substring(1) + keys.toString();
								else
									linkJsp += keys.toString().length() > 0 ? keys.substring(1) : "";

								isDeleteColumn = (columnMetaData.getName().endsWith( DELETE ) || columnMetaData.getName().startsWith( DELETE ) );

								if(isMultiDelete() && isDeleteColumn){

									_deletelink = columnMetaData.getRegexp();
									String deleteQueryString  = keys.toString().length() > 0 ? keys.substring(1) : "";
									htmlString.append ( "<td align=\"center\">" );
									htmlString.append ( "<input type=\"checkbox\" name=\"" + columnMetaData.getName() + "\" value=\""+ deleteQueryString + "\" ");
									htmlString.append( " onClick=\"doActivate();" );
									htmlString.append( "\">" );
									htmlString.append( "</td>" );

								}
								else {

									htmlString.append ( "<td align=\"center\"><a href=\"" );
									htmlString.append ( "javascript:launchLink(\'"+ java.net.URLEncoder.encode( linkJsp, _UTF8 ) +"\');\"" );
									// If it is a link and the column name ends with or starts with DELETE, then add the javascript
									// for delete confirmation
									if (isDeleteColumn)
										htmlString.append( " onClick=\"return confirmDelete();\">" );
									else
										htmlString.append( " onClick=\"eventObj = event.srcElement;\" >" );

									htmlString.append( "<img style=\"cursor=hand\" src=\'"+columnMetaData.getFormat()+"\' alt=\'"+columnMetaData.getCaption()+"\' BORDER=\'0\' " );
									htmlString.append( "onclick=\"document.body.style.cursor=\'wait\'" );
									htmlString.append( "\">" );
									htmlString.append( "</a></td>" );

								}
							}
						}
						else {
							String columnValue = ( _rsIterator.getString(columnMetaData.getName())==null ? "" : _rsIterator.getString(columnMetaData.getName()) );

                            if(columnMetaData.getType() == ColumnDataType._CDT_DOUBLE ||
                               columnMetaData.getType() == ColumnDataType._CDT_INT    ||
                               columnMetaData.getType() == ColumnDataType._CDT_SHORT    ||
                               columnMetaData.getType() == ColumnDataType._CDT_FLOAT    ||
                               columnMetaData.getType() == ColumnDataType._CDT_LONG ) {

                                columnValue = formatDisplayValue( columnValue,columnMetaData.getFormat() );

                                htmlString.append( "<td style='text-align:right;'>" );
                            }
                            else {
                                htmlString.append( "<td>" );
                            }


							if (columnMetaData.isLinkable()) {

								final String EDIT 	= "_EDIT";
								final String MODIFY = "_MODIFY";
								final String ADD 	= "_ADD";
								final String CLONE 	= "_CLONE";
								final String COPY 	= "_COPY";
								final String DEL = "_DELETE";
						        final String LOG    = "_LOG";
						        String name = columnMetaData.getName();
						        boolean addSorting = false;
								if ((!name.endsWith( EDIT )  && !name.endsWith( MODIFY ) && !name.endsWith( ADD ) &&
										 !name.endsWith( CLONE ) && !name.endsWith( COPY )   && !name.endsWith( DEL ) && !name.endsWith( LOG ))
									   ) {
										addSorting = true;
								}

								if( _hasEditPermission || addSorting ){

								String link = columnMetaData.getValue();

								if (link == null || link.equals( "" ))
										throw new JspTagException( "The value for a linkable column :" + columnMetaData.getName() + " is not specified in COLUMN_VALUE column of Columns table!" );

								link += link.indexOf("?") != -1 ? "&" : "?";



								link += link.indexOf("?") != -1 ? "&" : "?";


								// If the link is to an edit page, the convention is to
								// append _KEY to the request parameters
								// The edit page link is identified by the absence of
								// INIT_LOOKUP in the link
								//
								// If the link is to a list page, the convention is to
								// append _lookUp to the request parameters
								// The list page link is identified by the presence of
								// INIT_LOOKUP in the link

								if (link.indexOf("INIT_LOOKUP") == -1)
								{

									// Uncommenting this will affect the edit functionality
									// keys.append ( "&" );
									// keys.append ( columnMetaData.getName() + "_KEY" );
									// keys.append ( "=" );
									// keys.append ( columnValue );

									if(buildQuery.toString().length() > 0 )
										link +=  buildQuery.substring(1) + keys.toString();
									else
										link += keys.toString().length() > 0 ? keys.substring(1) : "";
								} else
								{

									lookupStr.append ( "&" );
									lookupStr.append ( columnMetaData.getName() + "_lookUp" );
									lookupStr.append ( "=" );
									lookupStr.append ( columnValue );

									if(buildQuery.toString().length() > 0 )
										link +=  buildQuery.substring(1) + lookupStr.toString();
									else
										link += lookupStr.toString().length() > 0 ? lookupStr.substring(1) : "";

								}

								htmlString.append( "<a href=\"javascript:launchLink('" + java.net.URLEncoder.encode( link, _UTF8 ) + "')\" onClick=\"eventObj = event.srcElement;\" >" );
								htmlString.append( columnValue );
								htmlString.append( "</a>" );
							}
							}
							else{
								htmlString.append( columnValue );
								// 3 Dec 2004 Prasad - Do not escape the column value as HTML may be returned as a column value
								//htmlString.append( StringEscapeUtils.escapeHtml(columnValue) );
							}

							htmlString.append( "</td>");
						}
					}
					countLinkColumns = true;
					pageContext.getOut().write( htmlString.toString() );
					pageContext.getOut().write ( "</tr>" );
				}
			}
        }
        catch ( Exception e )
        {
            release();
            e.printStackTrace();
            throw new JspTagException( e.getMessage() );
        }

        return SKIP_BODY;
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D41410004
	 */
	public int doEndTag() throws JspTagException
	{
        try
        {
			StringBuffer htmlString = new StringBuffer();

		    if (_hasViewPermission)
		    {
				int colspan = _editorMetaData.getDisplayableColumns() == null ? 0 : _editorMetaData.getDisplayableColumns().size();

				if (_hasEditPermission == false)
				{
					colspan = colspan - _nbrOfLinkColumns;
				}

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

					htmlString.append( "<a href=\"javascript:setPagingAction('");
					htmlString.append( "_formName" );
					htmlString.append( "','");
					htmlString.append( RSIterAction._FIRST_STR );
					htmlString.append( "'," );
					htmlString.append( _rsIterator.getPageDesc().isFirst() );
					htmlString.append( ")\">" );
					htmlString.append( "<img style=\"cursor=hand\" src=\"../images/first.gif\" border=\"0\" alt=\"First Page\">" );
					htmlString.append( "</a>" );

					htmlString.append( "<a href=\"javascript:setPagingAction('");
					htmlString.append( "_formName" );
					htmlString.append( "','" );
					htmlString.append( RSIterAction._PREV_STR );
					htmlString.append( "',");
					htmlString.append( _rsIterator.getPageDesc().isFirst() );
					htmlString.append( ")\">" );
					htmlString.append( "<img style=\"cursor=hand\" src=\"../images/previous.gif\" border=\"0\" alt=\"Previous Page\">" );
					htmlString.append( "</a>" );


					htmlString.append( "<a href=\"javascript:setPagingAction('");
					htmlString.append( "_formName" );
					htmlString.append( "','" );
					htmlString.append( RSIterAction._NEXT_STR );
					htmlString.append( "'," );
					htmlString.append( _rsIterator.getPageDesc().isLast() );
					htmlString.append( ")\">" );
					htmlString.append( "<img style=\"cursor=hand\" src=\"../images/next.gif\" border=\"0\" alt=\"Next Page\">");
					htmlString.append( "</a>" );


					htmlString.append( "<a href=\"javascript:setPagingAction('");
					htmlString.append( "_formName" );
					htmlString.append( "','" );
					htmlString.append( RSIterAction._LAST_STR );
					htmlString.append( "'," );
					htmlString.append( _rsIterator.getPageDesc().isLast() );
					htmlString.append( ")\">" );
					htmlString.append( "<img style=\"cursor=hand\" src=\"../images/last.gif\" border=\"0\" alt=\"Last Page\">" );
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
					htmlString.append( "&nbsp[" );
					for (int page=1; page<=totpages ;page++)
					{
						if ( page == currPage )
						{
							htmlString.append( "<font color=\"#FF0000\" style=\"cursor: default;\">&nbsp" );
							htmlString.append( page );
							htmlString.append( "</font>" );
						}
						else
						{
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
					htmlString.append( "</nobr>" );
					htmlString.append( "</tr>"  );
				}
				htmlString.append( "<input type=\"hidden\" name=\"PAGING_ACTION\" value=\"" );
				htmlString.append( RSIterAction._FIRST_STR );
				htmlString.append( "\">" );
				htmlString.append( "<input type=\"hidden\" name=\"POSITION\" value=\"" );
				htmlString.append( _rsIterator.getPageDesc().getRangeMin() );
				htmlString.append( "\">" );

			}

			if ((_hasViewPermission) && (_hasEditPermission)) {

            	if ( (_addlink != null && !_addlink.equals( "" )) || (_cancellink != null && !_cancellink.equals( "" )) || (isMultiDelete() && _rsIterator.getPageDesc().getRowCount() > 0 ) ) {
					htmlString.append( "<tr><td class=\"row0\" colspan=\"99\">" );
					StringBuffer buildQuery  = new StringBuffer( _whereClause );

					if(_addlink != null && !_addlink.equals( "" )) {
						String linkJsp = _addlink;
						// if the link file has a query string already
						// add & queryString and the keys else
						// add the ? and then the queryString and keys
						linkJsp += linkJsp.indexOf("?") != -1 ? "&" : "?";
						if(buildQuery.toString().length() > 0 ) {
							  linkJsp +=  buildQuery.substring(1);
                        }

						if (_addlinkImage == null || _addlinkImage.length() == 0)
							_addlinkImage = "../images/add_new.gif";

                        htmlString.append ( "<a href=\"" );
						htmlString.append ( "javascript:launchLink(\'"+ java.net.URLEncoder.encode( linkJsp, _UTF8 ) +"\')" );
						htmlString.append( "\" onClick=\"eventObj = event.srcElement;\" >" );
						//Removed the alt statement - since if we override the linkImage, the alt is showing default Add New & Delete Value
//						htmlString.append( "<img style=\"cursor=hand\" src=\'" + _addlinkImage + "\' alt=\'Add New\' BORDER=\'0\' " );
						htmlString.append( "<img style=\"cursor=hand\" src=\'" + _addlinkImage + "\' BORDER=\'0\' " );
						htmlString.append( "onclick=\"document.body.style.cursor=\'wait\'" );
						htmlString.append( "\">" );
						htmlString.append( "</a>" );
					}

					if(_cancellink != null && !_cancellink.equals( "" )) {
						String linkJsp = _cancellink;
						// if the link file has a query string already
						// add & queryString and the keys else
						// add the ? and then the queryString and keys
						linkJsp += linkJsp.indexOf("?") != -1 ? "&" : "?";
						if(buildQuery.toString().length() > 0 ) {
							  linkJsp +=  buildQuery.substring(1);
                        }

						if (_cancellinkImage == null || _cancellinkImage.length() == 0)
							_cancellinkImage = "../images/cancel.gif";

						htmlString.append( "&nbsp;&nbsp;&nbsp;" );
                        htmlString.append ( "<a href=\"" );
						htmlString.append ( "javascript:launchLink(\'"+ java.net.URLEncoder.encode( linkJsp, _UTF8 ) +"\')" );
						htmlString.append( "\" onClick=\"eventObj = event.srcElement;\" >" );
						htmlString.append( "<img style=\"cursor=hand\" src=\'" + _cancellinkImage + "\' alt=\'Cancel\' BORDER=\'0\' " );
						htmlString.append( "onclick=\"document.body.style.cursor=\'wait\'" );
						htmlString.append( "\">" );
						htmlString.append( "</a>" );
					}


                    if(isMultiDelete() && _rsIterator.getPageDesc().getRowCount() > 0) {
						_deletelink += _deletelink.indexOf("?") != -1 ? "&" : "?";
						if(buildQuery.toString().length() > 0 ) {
							_deletelink +=  buildQuery.substring(1);
						}
						if (_deletelinkImage == null || _deletelinkImage.length() == 0)
							_deletelinkImage = "../images/delete_button.gif";

						htmlString.append( "&nbsp;&nbsp;&nbsp;" );
						htmlString.append ( "<a href=\"" );
						htmlString.append ( "javascript:doDelete(\'"+ _deletelink +"\')\">" );
//						htmlString.append( "<img style=\"cursor=hand\" src=\'" + _deletelinkImage + "\' alt=\'Delete\' BORDER=\'0\'>" );
						htmlString.append( "<img style=\"cursor=hand\" src=\'" + _deletelinkImage + "\' BORDER=\'0\'>" );
						htmlString.append( "</a>" );
					}
					htmlString.append( "</td></tr>" );
				}
			}

            pageContext.getOut().write( htmlString.toString() );
        }
        catch(IOException ioexception)
        {
            release();
            throw new JspTagException( ioexception.getMessage() );
        }
        return EVAL_PAGE;
	}

	/**
	 * @roseuid 3B4D414701CF
	 */
	public void release()
	{
        _editorMetaData = null;
        _resultSet      = null;
        _rsIterator     = null;
        super.release();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B4D415F01E8
	 */
	private String getWhere()
	{
        String queryString = "";
        for ( java.util.Enumeration enumeration = pageContext.getRequest().getParameterNames(); enumeration.hasMoreElements(); ) {
            String columnName  = (String) enumeration.nextElement();
            if ( columnName.endsWith( "_lookUp" ) ||
            	 columnName.endsWith( "_PARENT_KEY" ) ||
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
	 * @roseuid 3B4D4163025C
	 */
	private String toggleColor(String color)
	{
        return ( _rowColor.equals("row0") ? "row1" : "row0" );
	}

	/**
	 * @return Vector
	 * @roseuid 3B855E2400C0
	 */
	private Vector getLinkColumns()
	{
        Vector          columnsMetaData = new Vector();
        Iterator        iterator        = _editorMetaData.getColumnsMetaData().iterator();
        ColumnMetaData  columnMetaData  = null;

        while (iterator.hasNext())
        {
            columnMetaData = (ColumnMetaData)iterator.next();
            if (columnMetaData.getType() == ColumnDataType._CDT_LINK && _editorMetaData.isEditable()  &&
                ( columnMetaData.getName().endsWith("ADD")  ||
                    columnMetaData.getName().endsWith("EDIT") ||
                    columnMetaData.getName().endsWith("DELETE") )
                )
                columnsMetaData.add ( columnMetaData );
        }

        return columnsMetaData.size() > 0 ? columnsMetaData : null;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE51F90272
	 */
	public String getViewrole()
	{
        return _viewrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3EFE51F90286
	 */
	public void setViewrole(String aViewrole)
	{
        _viewrole = aViewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE51F902B9
	 */
	public String getEditrole()
	{
        return _editrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3EFE51F902CD
	 */
	public void setEditrole(String aEditrole)
	{
        _editrole = aEditrole;
	}

    private String formatDisplayValue(String value,String format) {
        if( value == null || value.trim().length() == 0 ) {
            return value;
        }
        if( format == null || format.trim().length() == 0 || format.indexOf("#") != -1 ) {
            return value;
        }
        DecimalFormat df = new DecimalFormat( format );
        return df.format( value );

    }
}
