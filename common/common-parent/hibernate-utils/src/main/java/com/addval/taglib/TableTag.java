//Source file: D:\\projects\\Common\\source\\src\\com\\addval\\taglib\\TableTag.java

package com.addval.taglib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.PropertyUtils;

import com.addval.springstruts.ResourceUtils;

/**
 *
 * This tag takes a list of objects and creates a table to display those
 * objects.  With the help of column tags, simply provide the name of
 * properties (get Methods) that are called against the objects in the list
 * that gets displayed
 *
 * This tag works very much like the struts iterator tag, most of the attributes
 * have the same name and functionality as the struts tag.
 *
 * Simple Usage:<p>
 *
 *   <addval:table name="table" >
 *     <addval:column property="title" />
 *     <addval:column property="code" />
 *     <addval:column property="dean" />
 *   </addval:table>
 *
 * More Complete Usage:<p>
 *
 *   <addval:table name="table" pagesize="100">
 *     <addval:column property="title"
 *                    href="/osiris/pubs/college/edit.page"
 *                      paramId="OID"
 *                    paramProperty="OID" />
 *     <addval:column property="code" />
 *     <addval:column property="primaryOfficer.name" title="Dean" />
 *     <addval:column property="active"  />
 *   </addval:table>
 *
 *
 * Attributes:<p>
 *
 *   name
 *   property
 *   scope
 *   length
 *   offset
 *   pagesize
 *   decorator
 *
 *
 * HTML Pass-through Attributes
 *
 * There are a number of additional attributes that just get passed through to
 * the underlying HTML table declaration.  With the exception of the following
 * few default values, if these attributes are not provided, they will not
 * be displayed as part of the <table ...> tag.
 *
 *   width          - defaults to "100%" if not provided
 *   border         - defaults to "0" if not provided
 *   cellspacing    - defaults to "0" if not provided
 *   cellpadding    - defaults to "2" if not provided
 *   align
 *   background
 *   bgcolor
 *   frame
 *   height
 *   hspace
 *   rules
 *   summary
 *   vspace
 *
 */
public class TableTag extends BodyTagSupport {
	private Object _list = null;
	private String _name = null;
	private String _property = null;
	private String _length = null;
	private String _offset = null;
	private String _scope = null;
	private String _pagesize = null;
	private String _decorator = null;
	private String _width = null;
	private String _border = null;
	private String _cellspacing = null;
	private String _cellpadding = null;
	private String _align = null;
	private String _background = null;
	private String _bgcolor = null;
	private String _frame = null;
	private String _height = null;
	private String _hspace = null;
	private String _rules = null;
	private String _summary = null;
	private String _vspace = null;
	private String _clazz = null;
	private String _aclazz = null;
	private String _paging = null;
	private String _requestURI = null;
	private String _page = null;
	private Properties _prop = null;
	private boolean _isCustomized = false;
    private String _editRole = "true";
    private boolean _enableMouseover = false;


	/**
	 * Used by various functions when the person wants to do paging
	 * Variables that get set by investigating the request parameters
	 */
	private int _pageNumber = 1;
	private String _title = null;
	private List _columns = new ArrayList (10);
	private List _customizeColumns = new ArrayList (10);
	private TableTag.Decorator _dec = null;
	private TableTag.Pager _pager = null;
//	private List _completeList = null;

    public boolean isEnableMouseover() {
        return _enableMouseover;
    }

    public void setEnableMouseover(boolean enableMouseover) {
        this._enableMouseover = enableMouseover;
    }

	/**
	 * -------------------------------------------------------- Accessor methods
	 *
	 * @param list
	 * @roseuid 3E7065F60352
	 */
	public void setList(Object list) {
        _list = list;
	}

	/**
	 * @param name
	 * @roseuid 3E7065F700D2
	 */
	public void setName(String name) {
        _name = name;
	}

	/**
	 * @param property
	 * @roseuid 3E7065F7030D
	 */
	public void setProperty(String property) {
        _property = property;
	}

	/**
	 * @param length
	 * @roseuid 3E7065F80098
	 */
	public void setLength(String length) {
        _length = length;
	}

	/**
	 * @param offset
	 * @roseuid 3E7065F802AB
	 */
	public void setOffset(String offset) {
        _offset = offset;
	}

	/**
	 * @param scope
	 * @roseuid 3E7065F90021
	 */
	public void setScope(String scope) {
        _scope = scope;
	}

	/**
	 * @param pagesize
	 * @roseuid 3E7065F90176
	 */
	public void setPagesize(String pagesize) {
        _pagesize = pagesize;
	}

	/**
	 * @param decorator
	 * @roseuid 3E7065F902FC
	 */
	public void setDecorator(String decorator) {
        _decorator = decorator;
	}

	/**
	 * @param width
	 * @roseuid 3E7065FA00CD
	 */
	public void setWidth(String width) {
        _width = width;
	}

	/**
	 * @param border
	 * @roseuid 3E7065FA01D1
	 */
	public void setBorder(String border) {
        _border = border;
	}

	/**
	 * @param cellspacing
	 * @roseuid 3E7065FA02FE
	 */
	public void setCellspacing(String cellspacing) {
        _cellspacing = cellspacing;
	}

	/**
	 * @param cellpadding
	 * @roseuid 3E7065FB0024
	 */
	public void setCellpadding(String cellpadding) {
        _cellpadding = cellpadding;
	}

	/**
	 * @param align
	 * @roseuid 3E7065FB018C
	 */
	public void setAlign(String align) {
        _align = align;
	}

	/**
	 * @param background
	 * @roseuid 3E7065FB02CD
	 */
	public void setBackground(String background) {
        _background = background;
	}

	/**
	 * @param bgcolor
	 * @roseuid 3E7065FC00A8
	 */
	public void setBgcolor(String bgcolor) {
        _bgcolor = bgcolor;
	}

	/**
	 * @param frame
	 * @roseuid 3E7065FC01C0
	 */
	public void setFrame(String frame) {
        _frame = frame;
	}

	/**
	 * @param height
	 * @roseuid 3E7065FC0242
	 */
	public void setHeight(String height) {
        _height = height;
	}

	/**
	 * @param hspace
	 * @roseuid 3E7065FC033D
	 */
	public void setHspace(String hspace) {
        _hspace = hspace;
	}

	/**
	 * @param rules
	 * @roseuid 3E7065FD0045
	 */
	public void setRules(String rules) {
        _rules = rules;
	}

	/**
	 * @param summary
	 * @roseuid 3E7065FD01AD
	 */
	public void setSummary(String summary) {
        _summary = summary;
	}

	/**
	 * @param vspace
	 * @roseuid 3E7065FD030C
	 */
	public void setVspace(String vspace) {
        _vspace = vspace;
	}

	/**
	 * @param clazz
	 * @roseuid 3E7065FE0050
	 */
	public void setStyleClass(String clazz) {
        _clazz = clazz;
	}

	/**
	 * @param aclazz
	 * @roseuid 3E7065FE0187
	 */
	public void setStyleAclass(String aclazz) {
        _aclazz = aclazz;
	}

	/**
	 * @param page
	 * @roseuid 3E7065FE032B
	 */
	public void setPage(String page) {
        _page = page;
	}

	/**
	 * @param title
	 * @roseuid 3E7566000052
	 */
	public void setTitle(String title) {
        _title = title;
	}

	/**
	 * @param requestURI
	 * @roseuid 3E7065FE03AE
	 */
	public void setRequestURI(String requestURI) {
        _requestURI = requestURI;
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 3E7065FF014C
	 */
	public Object getList() {
        return _list;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7065FF01F6
	 */
	public String getName() {
        return _name;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7065FF025B
	 */
	public String getProperty() {
        return _property;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7065FF0319
	 */
	public String getLength() {
        return _length;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7065FF03AF
	 */
	public String getOffset() {
        return _offset;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7065FF03C3
	 */
	public String getScope() {
        return _scope;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066000085
	 */
	public String getPagesize() {
        return _pagesize;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066000144
	 */
	public String getDecorator() {
        return _decorator;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70660001EE
	 */
	public String getWidth() {
        return _width;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E706600027A
	 */
	public String getBorder() {
        return _border;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E706600032E
	 */
	public String getCellspacing() {
        return _cellspacing;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E706601027B
	 */
	public String getCellpadding() {
        return _cellpadding;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E706601033A
	 */
	public String getAlign() {
        return _align;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066020042
	 */
	public String getBackground() {
        return _background;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70660200F6
	 */
	public String getBgcolor() {
        return _bgcolor;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70660201A1
	 */
	public String getFrame() {
        return _frame;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066020223
	 */
	public String getHeight() {
        return _height;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066020237
	 */
	public String getHspace() {
        return _hspace;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066020255
	 */
	public String getRules() {
        return _rules;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066020273
	 */
	public String getSummary() {
        return _summary;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70660202FF
	 */
	public String getVspace() {
        return _vspace;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70660203D1
	 */
	public String getStyleClass() {
        return _clazz;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066030007
	 */
	public String getStyleAclass() {
        return _aclazz;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E7066030025
	 */
	public String getRequestURI() {
        return _requestURI;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70660300E4
	 */
	public String getPage() {
        return _page;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E75662B0176
	 */
	public String getTitle() {
        return _title;
	}

	/**
	 * @roseuid 3E7066030152
	 */
	public void reset() {
        _pageNumber = 1;
	}

	/**
	 * Returns the offset that the person provided as an int.  If the user does
	 * not provide an offset, or we can't figure out what they are trying to
	 * tell us, then we default to 0.
	 * @return the number of objects that should be skipped while looping through
	 * the list to display the table
	 * @roseuid 3E70660301DE
	 */
	private int getOffsetValue() {
        int offsetValue = 0;

        if (_offset != null) {
            try {
                offsetValue = Integer.parseInt( _offset );
            }
            catch( NumberFormatException e ) {
                Integer offsetObject = (Integer)pageContext.findAttribute( _offset );
                if (offsetObject == null) {
                    offsetValue = 0;
                }
                else
                offsetValue = offsetObject.intValue();
            }
        }
        if (offsetValue < 0)
            offsetValue = 0;

        return offsetValue;
	}

	/**
	 * Returns the length that the person provided as an int.  If the user does
	 * not provide a length, or we can't figure out what they are trying to tell
	 * us, then we default to 0.
	 * @return the maximum number of objects that should be shown while looping
	 * through the list to display the values.  0 means show all objects.
	 * @roseuid 3E7066030292
	 */
	private int getLengthValue() {
        int lengthValue = 0;

        if (_length != null) {
            try {
                lengthValue = Integer.parseInt( _length );
            }
            catch( NumberFormatException e ) {
                Integer lengthObject = (Integer)pageContext.findAttribute( _length );
                if (lengthObject == null) {
                    lengthValue = 0;
                }
                else
                    lengthValue = lengthObject.intValue();
            }
        }
        if (lengthValue < 0)
            lengthValue = 0;

        return lengthValue;
	}

	/**
	 * Returns the pagesize that the person provided as an int.  If the user does
	 * not provide a pagesize, or we can't figure out what they are trying to tell
	 * us, then we default to 0.
	 * @return the maximum number of objects that should be shown on any one page
	 * when displaying the list.  Setting this value also indicates that the
	 * person wants us to manage the paging of the list.
	 * @roseuid 3E7066030347
	 */
	private int getPagesizeValue() {
        int pagesizeValue = 0;

        if (_pagesize != null) {
            try {
                pagesizeValue = Integer.parseInt( _pagesize );
            }
            catch( NumberFormatException e ) {
                Integer pagesizeObject = (Integer)pageContext.findAttribute( _pagesize );
                if(pagesizeObject == null) {
                    pagesizeValue = 0;
                }
                else
                    pagesizeValue = pagesizeObject.intValue();
            }
        }

        if (pagesizeValue < 0)
            pagesizeValue = 0;

        return pagesizeValue;
	}

	/**
	 * ---------------------------------------- Communication with interior tags
	 * Called by interior column tags to help this tag figure out how it is
	 * supposed to display the information in the List it is supposed to
	 * display
	 * @param obj an internal tag describing a column in this tableview
	 * @roseuid 3E706603035B
	 */
	protected void addColumn(ColumnTag obj) {

        _columns.add( obj );
	}

	protected void addCustomizeColumn(CustomizeColumnTag obj) {
		_customizeColumns.add( obj );
	}


	/**
	 * --------------------------------------------------------- Tag API methods
	 * When the tag starts, we just initialize some of our variables, and do a
	 * little bit of error checking to make sure that the user is not trying
	 * to give us parameters that we don't expect.
	 *
	 *
	 * @return int
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E7066040334
	 */
	public int doStartTag() throws JspException {
        _columns = new ArrayList( 10 );

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        _paging = _page;

        loadDefaultProperties();

        if (req.getParameter( "page" ) != null) {
            _pageNumber = 1;
            try {
                _pageNumber = Integer.parseInt( req.getParameter( "page" ) );
            }
            catch( NumberFormatException e ) {
                Integer pageNumberObject = (Integer)pageContext.findAttribute( req.getParameter( "page" ) );
                if(pageNumberObject == null) {
                    _pageNumber = 1;
                }
                else
                    _pageNumber = pageNumberObject.intValue();
            }
        }
        return super.doStartTag();
	}

	/**
	 * Draw the table.  This is where everything happens, we figure out what
	 * values we are supposed to be showing, we figure out how we are supposed
	 * to be showing them, then we draw them.
	 *
	 * @return int
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E70660500A1
	 */
	public int doEndTag() throws JspException {
        int returnValue = 0;

        try {
//            HttpServletResponse res = (HttpServletResponse) pageContext.getResponse();
            JspWriter out = pageContext.getOut();
            List viewableData = getViewableData();

            // Load our table decorator if it is requested
            _dec = loadDecorator();
            if(_dec != null)
                _dec.init( pageContext, viewableData );

            StringBuffer buf = new StringBuffer( 8000 );
            buf.append( getHTMLData( viewableData ) );
            returnValue = EVAL_PAGE;
            out.write( buf.toString() );
            out.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return returnValue;
	}

	/**
	 * @return java.util.List
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E7066050191
	 */
	private List getViewableData() throws JspException {
        List viewableData = new ArrayList();

        // Acquire the collection that we are going to iterator over...

        Object collection = _list;

        if (collection == null) {
            collection = lookup( pageContext, _name, _property, _scope, false );
        }

        // Should put a check here that allows the user to choose if they
        // want this to error out or not...  Right now if we can't find a pointer
        // to their list or if they give us a null list, we just create an
        // empty list.

        if (collection == null) {
            collection = new ArrayList();
        }
        // Constructor an iterator that we use to actually go through the list
        // they have provided (no matter what type of data collection set they
        // are using
        Iterator iterator = null;

        // Todo - these needs cleaned up, we only show lists....

        if (collection.getClass().isArray())
            collection = Arrays.asList( (Object[])collection );

        if (collection instanceof List)
            collection = (List)collection;
        else {
            Object[] objs = {collection};
            String msg =
            MessageFormat.format( _prop.getProperty( "error.msg.invalid_bean" ), objs );
            throw new JspException( msg );
        }

        iterator = ( (List)collection ).listIterator();

        // If they have asked for an subset of the list via the offset or length
        // attributes, then only fetch those items out of the master list.

        int offsetValue = getOffsetValue();
        int lengthValue = getLengthValue();

        for( int i = 0; i < offsetValue; i++ ) {
            if( iterator.hasNext() )
                iterator.next();
        }

        int cnt = 0;
        while (iterator.hasNext()) {
            if (lengthValue > 0 && ++cnt > lengthValue)
                break;
            viewableData.add( iterator.next() );
        }

        // If they have asked for just a page of the data, then use the
        // Pager to figure out what page they are after, etc...

        int pagesizeValue = getPagesizeValue();
        if (pagesizeValue > 0) {
            _pager = new Pager ( (List)collection, pagesizeValue, _prop );
            // tlaw 12-10-2001
            // added the if/else statement below to allow sending an empty list to
            // this part of the program.  seems to work.
            // the statement inside the if is still needed by this program.
            // everything else can be removed.
            if (( (List)collection ).size() > 0) {
                if (_paging !=null)
                    _pager.setCurrentPage( Integer.parseInt(_paging) );
                else
                    _pager.setCurrentPage( _pageNumber );
            }
            iterator = _pager.getListForCurrentPage().listIterator();

            viewableData.clear();
            while (iterator.hasNext()) {
                if (lengthValue > 0 && ++cnt > lengthValue)
                    break;
            viewableData.add( iterator.next() );
            }
        }
        return viewableData;
	}

	/**
	 * @param viewableData
	 * @return java.lang.StringBuffer
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E70660502D1
	 */
	private StringBuffer getHTMLData(List viewableData) throws JspException {

        StringBuffer buf = new StringBuffer( 8 );

        int rowcnt = 0;
        if (getTitle() != null && getTitle().equalsIgnoreCase( "true" ))
            buf.append( getTableAttributes() );
        Iterator iterator = viewableData.iterator();
        if(getIsCustomized() == true)
        {
            if (_customizeColumns != null && !_customizeColumns.isEmpty()) {
                CustomizeColumnTag customTag = (CustomizeColumnTag) _customizeColumns.get(0);
                ArrayList columnLabels  = customTag.getUsedColumnLabels();
                ArrayList columnExpression = customTag.getColumnExpressions();
                String operations = customTag.getColumnOperations();
                buf.append(getTitleInfo(columnLabels, columnExpression, operations));
            }
        }

        String style = _clazz;

        boolean isDynamicStyle = true;

        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (_dec != null) {
                _dec.initRow( obj, rowcnt, rowcnt + getOffsetValue() );
            }

            pageContext.setAttribute( "addval", obj );

			String rowStyle = null;

            // Start building the row to be displayed...
            if (_clazz != null && _aclazz != null) {
                style =  style.equalsIgnoreCase(_clazz) ? _aclazz : _clazz;
            }

			// check for a property called styleClass in the decorator
			// if present use that styleClass
            if ((isDynamicStyle) && (_dec != null))
            {
	            try {
	               rowStyle = (String) PropertyUtils.getProperty( _dec, "styleClass" );
	            }
	            catch( IllegalAccessException e ) {
	                Object[] objs = {_dec};
	                throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.illegal_access_exception" ), objs ) );
	            }
	            catch( InvocationTargetException e ) {
	                Object[] objs = {_dec};
	                throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.invocation_target_exception" ), objs ) );
	            }
	            catch( NoSuchMethodException e ) {
					isDynamicStyle = false;
	            }
			}


			if ((!isDynamicStyle) || (rowStyle == null))
			{
				rowStyle = style;
			}


            if(getIsCustomized() == true) {

                 _columns = new ArrayList();
                if (_customizeColumns != null && !_customizeColumns.isEmpty()) {
                    for(int i=0; i< _customizeColumns.size(); i++){
                        CustomizeColumnTag tag = (CustomizeColumnTag)_customizeColumns.get( i );
                        ArrayList usedColumns = tag.getUsedColumns();

                        for (int j = 0; j < usedColumns.size(); j++) {
                            ColumnTag colTag = new ColumnTag();
							String col = (String)usedColumns.get( j );
                            colTag.setProperty( col );
                            String value = (String)lookup( pageContext, "addval", col, null, true );
                            colTag.setValue( value );
                            colTag.setAlign( tag.getAlign( col ) );
                            colTag.setNowrap( tag.getNowrap() );
                            addColumn( colTag );
                        }

                        if(getEditRole().equalsIgnoreCase("true")) {
                            if(tag.getColumnOperations() != null && tag.getColumnOperations().trim().length() > 0){
                                StringTokenizer tokenizer = new StringTokenizer( tag.getColumnOperations(), "," );
                                while (tokenizer.hasMoreTokens()) {
                                    ColumnTag colTag = new ColumnTag();
                                    String property = (String)tokenizer.nextToken().trim();
                                    colTag.setProperty(property);
                                    String value = (String)lookup( pageContext, "addval",property, null, true );
                                    colTag.setValue(value);
                                    colTag.setAlign("center");
                                    colTag.setValign("center");
                                    addColumn(colTag);
                                }
                            }
                        }
                    }
                }
            }

            buf.append( "<tr " );
            buf.append( " class =\"" );
            buf.append( rowStyle + "\"" );

            /*
             * Color change & Launch link button while Clicking the custom screen Search List similar to the Master screen
             * a)Include below js in you  custom jsp <script language="JavaScript" src="../js/link_shortcut.js" ></script>
             * b) Add the Below Tags in your Custom Search List jsp.
				<DIV class=tableContainer id=data>
				<div id="linkdiv" style="z-index:1;visibility:hidden;position:absolute;background-color:#EFEFFF;border: outset 0pt;border-collapse: collapse;border-spacing: 1pt;">
				</div>
 				<addval:table name="rateLists" enableMouseover="true" scope="page" styleClass="row0" styleAclass="row1" requestURI="<%=requestURL%>" decorator="com.addval.prmui.ratingui.RateListDecorator" >
				</addval:table>
				</DIV>
             */
            if( isEnableMouseover() )
            	buf.append( " onClick=\"setOnClick( this )\" onMouseOver = \"setOnMouseOver( this )\" onMouseOut = \"setOnMouseOut( this ) \" " );

            buf.append( ">\n" );


            // Bounce through our columns and pull out the data from this object
            // that we are currently focused on (lives in "addval").

            for (int i = 0; i < _columns.size(); i++) {
                ColumnTag tag = (ColumnTag)_columns.get( i );
                String display = "true";

                if (tag.getDisplayable() != null)
                    display = (String)lookup( pageContext,tag.getDisplayable(), "page");

                if(display == null || display.equals("true")) {

                    buf.append( "<td " );
                    buf.append( tag.getCellAttributes() );
                    buf.append( ">" );

                    // Get the value to be displayed for the column

                    Object value = null;
                    if (tag.getValue() != null) {
                        value = tag.getValue();
                    }
                    else {
                        if (tag.getProperty().equals( "ff" )) {
                            value = String.valueOf( rowcnt );
                        }
                        else if (tag.getProperty().equals("image"))
                            value = getLinks( tag.getPath(), tag.getForward(), tag.getParam(), true );
                        else if (tag.getProperty().equals("text"))
                            value = getLinks( tag.getName(), tag.getForward(), tag.getParam(), false );
                        else {
                            value = lookup( pageContext, "addval",tag.getProperty(), null, true );
                        }
                    }

                    // By default, we show null values as empty strings, unless the
                    // user tells us otherwise.

                    if (value == null || value.equals( "null" )) {
                        if (tag.getNulls() == null || tag.getNulls().equals( "false" )) {
                            value = "";
                        }
                    }

                    // String to hold what's left over after value is chopped
                    String leftover = "";
                    boolean chopped = false;
                    String tempValue = "";
                    if (value != null) {
                       tempValue = value.toString();
                    }

                    // trim the string if a maxLength or maxWords is defined
                    if (tag.getMaxLength() > 0 && tempValue.length() > tag.getMaxLength()) {
                       leftover = "..." + tempValue.substring( tag.getMaxLength(), tempValue.length() );
                       value = tempValue.substring( 0, tag.getMaxLength() ) + "...";
                       chopped = true;
                    }
                    else if (tag.getMaxWords() > 0) {
                        StringBuffer tmpBuffer = new StringBuffer();
                        StringTokenizer st = new StringTokenizer( tempValue );
                        int numTokens = st.countTokens();
                        if(numTokens > tag.getMaxWords()) {
                            int x = 0;
                            while (st.hasMoreTokens() && ( x < tag.getMaxWords() )) {
                                tmpBuffer.append( st.nextToken() + " " );
                                x++;
                            }
                            leftover = "..." + tempValue.substring( tmpBuffer.length(), tempValue.length() );
                            tmpBuffer.append( "..." );
                            value = tmpBuffer;
                            chopped = true;
                        }
                    }

                    // TODO - This args stuff is very bad, it really needs cleaned up..

                    if (tag.getHref() != null) {

                        if(tag.getParamId() != null) {
                            String name = tag.getParamName();

                            if (name == null) {
                                name = "addval";
                            }

                            Object param =lookup( pageContext, name,tag.getParamProperty(), tag.getParamScope(), true );
                            // flag to determine if we should use a ? or a &
                            int index = tag.getHref().indexOf('?');
                            String separator = "";

                            if (index == -1)
                                separator = "?";
                            else
                                separator = "&";

                            // if value has been chopped, add leftover as title
                            if (chopped) {
                                value = "<a href=\"" + tag.getHref() + separator + tag.getParamId() +
                                    "=" + param + "\" title=\"" + leftover + "\">" + value + "</a>";
                            }
                            else {
                                value = "<a href=\"" + tag.getHref() + separator + tag.getParamId() +
                                    "=" + param + "\">" + value + "</a>";
                            }
                        }
                        else {
                            // if value has been chopped, add leftover as title
                            if (chopped) {
                                value = "<a href=\"" + tag.getHref() + "\" title=\"" + leftover + "\">" + value + "</a>";
                            }
                            else {
                                value = "<a href=\"" + tag.getHref() + "\">" + value + "</a>";
                            }
                        }
                    }
                    else {
                        if (chopped && tag.getHref() == null) {
                            buf.append( value.toString().substring( 0, value.toString().length() - 3 ) );
                            buf.append( "<a style=\"cursor: help;\" title=\"" + leftover + "\">" );
                            buf.append( value.toString().substring( value.toString().length() - 3,
                            value.toString().length() ) + "</a>" );
                        }
                        else {
                            buf.append( value );
                        }
                    }
                    buf.append( "</td>\n" );
                }
            }

            // Special case, if they didn't provide any columns, then just spit out
            // the object's string representation to the table.

            if (_columns.size() == 0) {
                buf.append( "<td class=\"tableCell\">" );
                buf.append( obj.toString() );
                buf.append( "</td>" );
             }

             buf.append( "</tr>\n" );

             if (_dec != null) {
                String rt = _dec.finishRow();
                if( rt != null ) buf.append( rt );
            }
            rowcnt++;
        }

        boolean isFooterRequired = _prop.getProperty( "paging.banner.show_footer" ).equals("true");
        if (rowcnt == 0) {
            if(getIsCustomized() == true)
            {
                if (_customizeColumns != null && !_customizeColumns.isEmpty()) {
                    CustomizeColumnTag customTag = (CustomizeColumnTag) _customizeColumns.get(0);
                    _columns  = customTag.getUsedColumns();
                }
            }

            buf.append( "<tr class=\"brdright\">" );
            buf.append( "<td class=\"text12red\" align=\"center\" colspan=\"" +
                ( _columns.size() + 1 ) + "\">" +
                _prop.getProperty( "paging.banner.no_items_found" ) + "</td></tr>" );
        }
        if (rowcnt != 0 && isFooterRequired){
            buf.append( getTableFooter() );
        }

        if (getTitle() != null && getTitle().equalsIgnoreCase( "true" ))
	        buf.append( "</table>\n" );

        if (_dec != null)
            _dec.finish();
        _dec = null;

        return buf;
	}

	/**
	 * Generates table footer with links for export commands.
	 *
	 *
	 * @return java.lang.String
	 * @roseuid 3E70660600AC
	 */
	private String getTableFooter() {
        StringBuffer buf = new StringBuffer( 1000 );
        int pagesizeValue = getPagesizeValue();

        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        String url = _requestURI;

        _paging = _page;

        if (url == null) {
            url = req.getRequestURI();
        }


        String pageNavigation = null;
        if( url.toUpperCase().indexOf( "JAVASCRIPT" ) == -1){
            url += (url.indexOf("?") != -1) ? "&" : "?";
            pageNavigation = url + "page={0,number,#}";
        }
        else{
            pageNavigation = url;
        }

        // Put the page stuff there if it needs to be there...

       if (pagesizeValue != 0 && _pager != null) {
            buf.append( "<tr><td width=\"100%\" colspan=\"" + _columns.size() );
            buf.append( "\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" " );
            buf.append( "cellpadding=\"0\"><tr class=\"tableRowAction\">" );
            buf.append( "<td align=\"left\" valign=\"bottom\" class=\"" );
            buf.append( "tableCellAction\">" );
            buf.append( _pager.getSearchResultsSummary() );
            buf.append( "</td>\n" );
            buf.append( "<td valign=\"bottom\" align=\"right\" class=\"" );
            buf.append( "tableCellAction\">\n" );
            buf.append( _pager.getPageNavigationBar( pageNavigation ) );
            buf.append( "</td>\n</tr></table></td></tr>\n" );
        }
        return buf.toString();
	}

	/**
	 * Takes all the table pass-through arguments and bundles them up as a
	 * string that gets tacked on to the end of the table tag declaration.<p>
	 * Note that we override some default behavior, specifically:<p>
	 * width        defaults to 100% if not provided
	 * border       defaults to 0 if not provided
	 * cellspacing  defaults to 1 if not provided
	 * cellpadding  defaults to 2 if not provided
	 *
	 *
	 * @return java.lang.String
	 * @roseuid 3E7066060192
	 */
	private String getTableAttributes() throws JspException{
        StringBuffer results = new StringBuffer();

        results.append( "<table " );

        String style = _clazz;

        if (_clazz != null && _aclazz != null) {
            results.append( " class=\"" );
            results.append( style );
            results.append( "\"" );
            style =  style.equalsIgnoreCase(_clazz) ? _aclazz : _clazz;
        }

        else if (_clazz != null) {
            results.append( " class=\"" );
            results.append( _clazz );
            results.append( "\"" );
        }
        else {
            results.append( " class=\"table\"" );
        }

        if (_width != null) {
            results.append( " width=\"" );
            results.append( _width );
            results.append( "\"" );
        }
        else {
            results.append( " width=\"100%\"" );
        }

        if(_border != null) {
            results.append( " border=\"" );
            results.append( _border );
            results.append( "\"" );
        }
        else {
            results.append( " border=\"0\"" );
        }

        if (_cellspacing != null) {
            results.append( " cellspacing=\"" );
            results.append( _cellspacing );
            results.append( "\"" );
        }
        else {
            results.append( " cellspacing=\"1\"" );
        }
        if (_cellpadding != null) {
            results.append( " cellpadding=\"" );
            results.append( _cellpadding );
            results.append( "\"" );
        }
        else {
            results.append( " cellpadding=\"2\"" );
        }

        if (_align != null) {
            results.append( " align=\"" );
            results.append( _align );
            results.append( "\"" );
        }

        if (_background != null) {
            results.append( " background=\"" );
            results.append( _background );
            results.append( "\"" );
        }

        if (_bgcolor != null) {
            results.append( " bgcolor=\"" );
            results.append( _bgcolor );
            results.append( "\"" );
        }

        if (_frame != null) {
            results.append( " frame=\"" );
            results.append( _frame );
            results.append( "\"" );
        }

        if (_height != null) {
            results.append( " height=\"" );
            results.append( _height );
            results.append( "\"" );
        }

        if (_hspace != null) {
            results.append( " hspace=\"" );
            results.append( _hspace );
            results.append( "\"" );
        }

        if (_rules != null) {
            results.append( " rules=\"" );
            results.append( _rules );
            results.append( "\"" );
        }

        if (_summary != null) {
            results.append( " summary=\"" );
            results.append( _summary );
            results.append( "\"" );
        }
        if (_vspace != null) {
            results.append( " vspace=\"" );
            results.append( _vspace );
            results.append( "\"" );
        }
        results.append( ">\n" );

        // If they don't want the header shown for some reason, then stop here.

        if( !_prop.getProperty( "basic.show.header" ).equals( "true" ) ) {
         return results.toString();
        }
        String propertyName = null;
        String caption = null;
        for( int i = 0; i < _columns.size(); i++ ) {
            ColumnTag tag = (ColumnTag)_columns.get( i );
            results.append( "<th" );
            if( tag.getWidth() != null )
                results.append( " width=\"" + tag.getWidth() + "\"" );

            if( tag.getAlign() != null )
                results.append( " align=\"" + tag.getAlign() + "\" " );

            results.append( ">" );
            propertyName = tag.getProperty();
            caption = ResourceUtils.columnCaption(pageContext,propertyName,(tag.getTitle() != null)?tag.getTitle():propertyName);
            results.append( caption );
            results.append( "</th>\n" );
        }

        // Special case, if they don't provide any columns, then just set
        // the title to a message, telling them to provide some...

        if( _columns.size() == 0 ) {
            results.append( "<td><b>" + _prop.getProperty( "error.msg.no_column_tags" ) +
        "</b></td>" );
        }

        results.append( "</tr>\n" );

        return results.toString();
	}

	/**
	 * This functionality is borrowed from struts, but I've removed some
	 * struts specific features so that this tag can be used both in a
	 * struts application, and outside of one.
	 * Locate and return the specified bean, from an optionally specified
	 * scope, in the specified page context.  If no such bean is found,
	 * return <code>null</code> instead.
	 * @param pageContext Page context to be searched
	 * @param name Name of the bean to be retrieved
	 * @param scope Scope to be searched (page, request, session, application)
	 * or <code>null</code> to use <code>findAttribute()</code> instead
	 * @return java.lang.Object
	 * @throws javax.servlet.jsp.JspException
	 * @exception JspException if an invalid scope name is requested
	 * @roseuid 3E7066060283
	 */
	private Object lookup(PageContext pageContext, String name, String scope) throws JspException {

        Object bean = null;
        if (scope == null)
            bean = pageContext.findAttribute( name );
        else if (scope.equalsIgnoreCase( "page" ))
            bean = pageContext.getAttribute( name, PageContext.PAGE_SCOPE );
        else if (scope.equalsIgnoreCase( "request" ))
            bean = pageContext.getAttribute( name, PageContext.REQUEST_SCOPE );
        else if (scope.equalsIgnoreCase( "session" ))
            bean = pageContext.getAttribute( name, PageContext.SESSION_SCOPE );
        else if (scope.equalsIgnoreCase( "application" ))
            bean = pageContext.getAttribute( name, PageContext.APPLICATION_SCOPE );
        else {
            Object[] objs = {name, scope};
            String msg =
            MessageFormat.format( _prop.getProperty( "error.msg.cant_find_bean" ), objs );
            throw new JspException( msg );
        }
        return ( bean );
	}

	/**
	 * This functionality is borrowed from struts, but I've removed some
	 * struts specific features so that this tag can be used both in a
	 * struts application, and outside of one.
	 * Locate and return the specified property of the specified bean, from
	 * an optionally specified scope, in the specified page context.
	 * @param pageContext Page context to be searched
	 * @param name Name of the bean to be retrieved
	 * @param property Name of the property to be retrieved, or
	 * <code>null</code> to retrieve the bean itself
	 * @param scope Scope to be searched (page, request, session, application)
	 * or <code>null</code> to use <code>findAttribute()</code> instead
	 * @param useDecorator
	 * @return java.lang.Object
	 * @throws javax.servlet.jsp.JspException
	 * @exception JspException if an invalid scope name is requested
	 * @exception JspException if the specified bean is not found
	 * @exception JspException if accessing this property causes an
	 * IllegalAccessException, IllegalArgumentException,
	 * InvocationTargetException, or NoSuchMethodException
	 * @roseuid 3E706607028E
	 */
	private Object lookup(PageContext pageContext, String name, String property, String scope, boolean useDecorator) throws JspException {

        if (useDecorator && _dec != null) {
        // First check the decorator, and if it doesn't return a value
        // then check the inner object...

            try {
                if (property == null)
                    return _dec;
                return ( PropertyUtils.getProperty( _dec, property ) );
            }
            catch( IllegalAccessException e ) {
                Object[] objs = {property, _dec};
                throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.illegal_access_exception" ), objs ) );
            }
            catch( InvocationTargetException e ) {
                Object[] objs = {property, _dec};
                throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.invocation_target_exception" ), objs ) );
            }
            catch( NoSuchMethodException e ) {
            }
        }

        // Look up the requested bean, and return if requested
        Object bean = lookup( pageContext, name, scope );
        if (property == null)
            return ( bean );

        if (bean == null) {
            Object[] objs = {name, scope};
            throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.cant_find_bean" ), objs ));
        }

        // Locate and return the specified property

        try {
            return ( PropertyUtils.getProperty( bean, property ) );
        }
        catch( IllegalAccessException e ) {
            Object[] objs = {property, name};
            throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.illegal_access_exception" ), objs ) );
        }
        catch( InvocationTargetException e ) {
            Object[] objs = {property, name};
            throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.invocation_target_exception" ), objs ) );
        }
        catch( NoSuchMethodException e ) {
            Object[] objs = {property, name};
            throw new JspException( MessageFormat.format( _prop.getProperty( "error.msg.nosuchmethod_exception" ), objs ) );
        }
	}

	/**
	 * If the user has specified a decorator, then this method takes care of
	 * creating the decorator If there are any problems loading the decorator
	 * then this will throw a JspException which will get propogatedup the page.
	 *
	 * @return com.addval.taglib.TableTag.Decorator
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E706608014F
	 */
	private TableTag.Decorator loadDecorator() throws JspException {
        if (_decorator == null || _decorator.length() == 0)
            return null;

        try {
            //Class c = Class.forName( _decorator );
            //Class c = Class.forName( _decorator, true, ClassLoader.getSystemClassLoader() );
			Thread t 		= 	Thread.currentThread();
			ClassLoader cl 	=	t.getContextClassLoader();
			Class c			= 	cl.loadClass(_decorator);

            return (Decorator)c.newInstance();
        }
        catch( Exception e ) {
            throw new JspException( e.toString() );
        }
	}

	/**
	 * Called by the setProperty tag to override some default behavior or text
	 * string.
	 *
	 * @param name
	 * @param value
	 * @roseuid 3E70660801F9
	 */
	protected void setProperty(String name, String value) {
        _prop.setProperty( name, value );
	}

	/**
	 * This sets up all the default properties for the table tag.
	 * @roseuid 3E706608039E
	 */
	private void loadDefaultProperties() {
        _prop = new Properties();

        _prop.setProperty( "basic.show.header", "true" );
        _prop.setProperty( "basic.msg.empty_list", "No data available." );
        _prop.setProperty( "paging.banner.show_footer", "true" );
        _prop.setProperty( "paging.banner.item_name", "item" );
        _prop.setProperty( "paging.banner.items_name", "items" );
        _prop.setProperty( "paging.banner.no_items_found", "No data available." );
        _prop.setProperty( "paging.banner.one_item_found", "1 {0} found." );
        _prop.setProperty( "paging.banner.some_items_found", "{0} {1} found, displaying {2} to {3}" );
        _prop.setProperty( "paging.banner.include_first_last", "true" );
        _prop.setProperty( "paging.banner.first_label", "&lt;&lt;" );
        _prop.setProperty( "paging.banner.last_label", "&gt;&gt;" );
        _prop.setProperty( "paging.banner.prev_label", "&lt;" );
        _prop.setProperty( "paging.banner.next_label", "&gt;" );
        _prop.setProperty( "paging.banner.group_size", "8" );

        _prop.setProperty( "error.msg.cant_find_bean", "Could not find bean {0} in scope {1}" );
        _prop.setProperty( "error.msg.invalid_bean", "The bean that you gave is not a Collection : {0}" );
        _prop.setProperty( "error.msg.no_column_tags", "Please provide column tags." );
        _prop.setProperty( "error.msg.illegal_access_exception", "IllegalAccessException trying to fetch property {0} on bean {1}" );
        _prop.setProperty( "error.msg.invocation_target_exception", "InvocationTargetException trying to fetch property {0} on bean {1}" );
        _prop.setProperty( "error.msg.nosuchmethod_exception", "NoSuchMethodException trying to fetch property {0} on bean {1}" );
        _prop.setProperty( "error.msg.invalid_decorator", "Decorator class having problem" );
        _prop.setProperty( "error.msg.invalid_page", "Invalid page ({0}) provided, value should be between 1 and {1}" );
	}

	/**
	 * Locate the image from the path specified by the user and prepare the
	 * queryString.
	 * @param path
	 * @param forward
	 * @param param
	 * @param bool
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E7066090056
	 */
	private String getLinks(String path, String forward, String param, boolean bool) throws JspException {
        String img = null;
        if (bool) {
            img = "<img src = " + path ;
            if (forward != null) {
                img += " OnClick=\"javascript:location.href= '"+forward;

                if (param != null)
                    img += queryString( param, img );
                img += "'\"";
            }
            img += " >";
        }
        else {
            img= "<a href='";
            if (forward != null) {
                img += forward;

                if (param !=null)
                    img += queryString( param, img );
            }
            img += "'>"+ path +"</a>";
        }
        return img;
	}

	/**
	 * @param param
	 * @param img
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 3E70660902CD
	 */
	private String queryString(String param, String img) throws JspException {
        String queryStr = null;
        StringTokenizer st = new StringTokenizer( param, "," );
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            int index = img.indexOf( "?" );
            Object obj =  lookup( pageContext, "addval",str, null, true ).toString();
            if (obj != null) {
                if (index != -1)
                    queryStr =  "&";
                else
                    queryStr = "?";
                queryStr += str +"=" + obj.toString() + "";
            }
        }
        return queryStr;
	}

	/**
	 * This Inner Class used for showing images and their relative links
	 */
	public static class Decorator {
		private Object _decObj = null;
		private int _decViewIndex = - 1;
		private int _decListIndex = - 1;
		private PageContext _decCtx = null;
		private List _decList = null;

		/**
		 * @roseuid 3E706610013D
		 */
		public Decorator() {

		}

		/**
		 * @param ctx
		 * @param list
		 * @roseuid 3E70661001B5
		 */
		public void init(PageContext ctx, List list) {
            _decCtx = ctx;
            _decList = list;
		}

		/**
		 * @param obj
		 * @param viewIndex
		 * @param listIndex
		 * @roseuid 3E7066100223
		 */
		public void initRow(Object obj, int viewIndex, int listIndex) {
            _decObj = obj;
            _decViewIndex = viewIndex;
            _decListIndex = listIndex;
		}

		/**
		 * @return java.lang.String
		 * @roseuid 3E7066100363
		 */
		public String finishRow() {
            return "";
		}

		/**
		 * @roseuid 3E7066100377
		 */
		public void finish() {

		}

		/**
		 * @return javax.servlet.jsp.PageContext
		 * @roseuid 3E7066110008
		 */
		public PageContext getPageContext() {
            return _decCtx;
		}

		/**
		 * @return java.util.List
		 * @roseuid 3E70661100D0
		 */
		public List getList() {
            return _decList;
		}

		/**
		 * @return java.lang.Object
		 * @roseuid 3E7066110260
		 */
		public Object getObject() {
            return _decObj;
		}

		/**
		 * @return int
		 * @roseuid 3E70661102E3
		 */
		public int getViewIndex() {
            return _decViewIndex;
		}

		/**
		 * @return int
		 * @roseuid 3E7066110365
		 */
		public int getListIndex() {
            return _decListIndex;
		}
	}

	/**
	 * Creating Page Navigation between the pages.
	 */
	class Pager extends Object {
		private int _pageSize;
		private int _pageCount;
		private int _currentPage;
		private Properties _prop = null;
		private List _masterList;

		/**
		 * Creates a Pager instance that will help you chop up a list
		 * into bite size pieces that are suitable for display.
		 *
		 * @param list
		 * @param pageSize
		 * @param prop
		 * @roseuid 3E70660C039A
		 */
		private Pager(List list, int pageSize, Properties prop) {
            super();
            if (list == null || pageSize < 1) {
                throw new IllegalArgumentException( "Bad arguments passed into  Pager() constructor" );
            }
            _prop = prop;
            _pageSize = pageSize;
            _masterList = list;
            _pageCount = computedPageCount();
            _currentPage = 1;
		}

		/**
		 * Returns the computed number of pages it would take to show all the
		 * elements in the list given the pageSize we are working with.
		 *
		 * @return int
		 * @roseuid 3E70660D008E
		 */
		private int computedPageCount() {
            int result = 0;
            if (( _masterList != null ) && ( _pageSize > 0 )) {
                int size = _masterList.size();
                int div = size / _pageSize;
                int mod = size % _pageSize;
                result = ( mod == 0 ) ? div : div + 1;
            }

            return result;
		}

		/**
		 * Returns the index into the master list of the first object that
		 * should appear on the current page that the user is viewing.
		 *
		 * @return int
		 * @roseuid 3E70660D00FC
		 */
		private int getFirstIndexForCurrentPage() {
            return getFirstIndexForPage( _currentPage );
		}

		/**
		 * Returns the index into the master list of the last object that should
		 * appear on the current page that the user is viewing.
		 *
		 * @return int
		 * @roseuid 3E70660D0110
		 */
		private int getLastIndexForCurrentPage() {
            return getLastIndexForPage( _currentPage );
		}

		/**
		 * Returns the index into the master list of the first object that
		 * should appear on the given page.
		 *
		 * @param page
		 * @return int
		 * @roseuid 3E70660D019C
		 */
		private int getFirstIndexForPage(int page) {
            return ( ( page - 1 ) * _pageSize );
		}

		/**
		 * Returns the index into the master list of the last object that should
		 * appear on the given page.
		 *
		 * @param page
		 * @return int
		 * @roseuid 3E70660D020B
		 */
		private int getLastIndexForPage(int page) {
            int firstIndex = getFirstIndexForPage( page );
            int pageIndex = _pageSize - 1;
            int lastIndex = _masterList.size() - 1;

            return Math.min( firstIndex + pageIndex, lastIndex );
		}

		/**
		 * Returns a subsection of the list that contains just the elements that
		 * are supposed to be shown on the current page the user is viewing.
		 *
		 * @return java.util.List
		 * @roseuid 3E70660D02C9
		 */
		private List getListForCurrentPage() {
            return getListForPage( _currentPage );
		}

		/**
		 * Returns a subsection of the list that contains just the elements that
		 * are supposed to be shown on the given page.
		 *
		 * @param page
		 * @return java.util.List
		 * @roseuid 3E70660D0391
		 */
		private List getListForPage(int page) {

            List list = new ArrayList( _pageSize + 1 );

            int firstIndex = getFirstIndexForPage( page );
            int lastIndex = getLastIndexForPage( page );

            for (int i = firstIndex; i <= lastIndex; i++) {
                list.add( _masterList.get( i ) );
            }

            return list;
		}

		/**
		 * Set's the page number that the user is viewing.
		 * @param page
		 * @throws IllegalArgumentException if the page provided is invalid.
		 * @roseuid 3E70660E00D6
		 */
		private void setCurrentPage(int page) {
            while( page > _pageCount)
                page = _pageCount;

            if (page < 1 || page > _pageCount) {
                Object[] objs = {new Integer( page ), new Integer( _pageCount )};
                throw new IllegalArgumentException( MessageFormat.format( _prop.getProperty( "error.msg.invalid_page" ), objs ) );
            }
            _currentPage = page;
		}

		/**
		 * Return the little summary message that lets the user know how many
		 * objects are in the list they are viewing, and where in the list they
		 * are currently positioned.  The message looks like:
		 * nnn <item(s)> found, displaying nnn to nnn.
		 * <item(s)> is replaced by either itemName or itemNames depending on if
		 * it should be signular or plurel.
		 *
		 * @return java.lang.String
		 * @roseuid 3E70660E01EE
		 */
		private String getSearchResultsSummary() {

            if (_masterList.size() == 0) {
                Object[] objs = {_prop.getProperty( "paging.banner.items_name" )};
                return MessageFormat.format(_prop.getProperty( "paging.banner.no_items_found" ), objs );
            }
            else if(_masterList.size() == 1) {
                Object[] objs = {_prop.getProperty( "paging.banner.item_name" )};
                return MessageFormat.format(
                _prop.getProperty( "paging.banner.one_item_found" ), objs );
            }
            else {
                Object[] objs = {new Integer( _masterList.size() ),
                    _prop.getProperty( "paging.banner.items_name" ),
                    new Integer( getFirstIndexForCurrentPage() + 1 ),
                    new Integer( getLastIndexForCurrentPage() + 1 )};

                return MessageFormat.format( _prop.getProperty( "paging.banner.some_items_found" ), objs );
            }
		}

		/**
		 * Returns a string containing the nagivation bar that allows the user
		 * to move between pages within the list.
		 * The urlFormatString should be a URL that looks like the following:
		 * http://.../somepage.page?page={0}
		 *
		 * @param urlFormatString
		 * @return java.lang.String
		 * @roseuid 3E70660E0284
		 */
		private String getPageNavigationBar(String urlFormatString) {
            MessageFormat form = new MessageFormat( urlFormatString.replace('\'','!') );
            String pageUrl = null;
            int maxPages = 8;

            try {
                maxPages = Integer.parseInt( _prop.getProperty( "paging.banner.group_size" ) );
            }
            catch( NumberFormatException e ) {
            }

            int currentPage = _currentPage;
            int pageCount = _pageCount;
            int startPage = 1;
            int endPage = maxPages;

            if (pageCount == 1 || pageCount == 0) {
                return " ";
            }

            if (currentPage < maxPages) {
                startPage = 1;
                endPage = maxPages;
                if (pageCount < endPage) {
                    endPage = pageCount;
                }
            }
            else {
                startPage = currentPage;
                while (startPage + maxPages > ( pageCount + 1 )) {
                    startPage--;
                }
                endPage = startPage + ( maxPages - 1 );
            }

            boolean includeFirstLast = _prop.getProperty( "paging.banner.include_first_last" ).equals( "true" );

            String msg = "";
            if (currentPage == 1) {
                if (includeFirstLast) {
                    msg += "[" + _prop.getProperty( "paging.banner.first_label" ) +
                    "/" + _prop.getProperty( "paging.banner.prev_label" ) + "] ";
                }
                else {
                    msg += "[" + _prop.getProperty( "paging.banner.prev_label" ) + "] ";
                }
            }
            else {
                Object[] objs = {new Integer( currentPage - 1 )};
                Object[] v1 = {new Integer( 1 )};
                if (includeFirstLast) {

                    msg += "[<a href=\"" + form.format( v1 ).replace('!','\'') + "\">" +
                    _prop.getProperty( "paging.banner.first_label" ) + "</a>/<a href=\"" +
                    form.format( objs ).replace('!','\'') + "\">" +
                    _prop.getProperty( "paging.banner.prev_label" ) + "</a>] ";
                }
                else {
                    msg += "[<a href=\"" + form.format( objs ).replace('!','\'') + "\">" +
                    _prop.getProperty( "paging.banner.prev_label" ) + "</a>] ";
                }
            }

            for (int i = startPage; i <= endPage; i++) {
                if (i == currentPage) {
                    msg += "<b>" + i + "</b>";
                }
                else {
                    Object[] v = {new Integer( i )};
                    msg += "<a href=\"" + form.format( v ).replace('!','\'') + "\">" + i + "</a>";
                }

                if (i != endPage) {
                    msg += ", ";
                }
                else {
                    msg += " ";
                }
            }

            if(currentPage == pageCount) {
                if (includeFirstLast) {
                    msg += "[" + _prop.getProperty( "paging.banner.next_label" ) +
                    "/" + _prop.getProperty( "paging.banner.last_label" ) + "] ";
                }
                else {
                    msg += "[" + _prop.getProperty( "paging.banner.next_label" ) + "] ";
                }
            }
            else {
                Object[] objs = {new Integer( currentPage + 1 )};
                Object[] v1 = {new Integer( pageCount )};
                if( includeFirstLast ) {
                    msg += "[<a href=\"" + form.format( objs ).replace('!','\'') + "\">" +
                            _prop.getProperty( "paging.banner.next_label" ) + "</a>/<a href=\"" +
                            form.format( v1 ).replace('!','\'') + "\">" +
                            _prop.getProperty( "paging.banner.last_label" ) + "</a>] ";
                }
                else {
                    msg += "[<a href=\"" + form.format( objs ).replace('!','\'') + "\">" +
                    _prop.getProperty( "paging.banner.next_label" ) + "</a>] ";
                }
            }
            return msg;
		}
	}

    private StringBuffer getTitleInfo(ArrayList usedColumnLabels, ArrayList columnExpressions, String operations) throws JspException {
        StringBuffer results = new StringBuffer();
        results.append( "<tr>\n " );
        String name = null;
        String caption = null;
        for( int i = 0; i < usedColumnLabels.size(); i++ ) {
            results.append( "<th align=\"center\"" );
            results.append( ">" );
            if (columnExpressions !=null && !columnExpressions.isEmpty()) {
                 name = (String)columnExpressions.get(i);
                 if (name != null && name.length() >0) {
                     results.append( "<a  href=\"javaScript:launchSort('");
                     results.append( name );
                     results.append( "')\">" );
                 }
            }
            caption = ResourceUtils.columnCaption(pageContext,name,(String)usedColumnLabels.get(i));
            results.append( caption );
            results.append( "</th>\n" );
        }

        if(getEditRole().equalsIgnoreCase("true")) {
            if(operations != null && operations.trim().length() > 0){
                StringTokenizer tokenizer = new StringTokenizer( operations, "," );
                String operator = null;
                while (tokenizer.hasMoreTokens()) {
                    results.append("<th align=\"center\">");
                    operator = (String)tokenizer.nextToken().trim();
                    operator = ResourceUtils.columnCaption(pageContext, operator,com.addval.utils.StrUtl.toProperCase(operator));
                    results.append( operator );
                    results.append("</th>");
                }
            }
        }

        results.append( "</tr>\n " );
        return results;
    }

	public boolean getIsCustomized()
	{
		return _isCustomized;
	}

	public void setIsCustomized(boolean customized)
	{
		_isCustomized = customized;
	}

    public String getEditRole()
    {
        return _editRole;
    }

    public void setEditRole(String aEditRole)
    {
        _editRole = aEditRole;
    }

	
}
