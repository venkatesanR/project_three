package com.addval.aswtaglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

/**
 * This tag works hand in hand with the TableTag to display a list of
 * objects.  This describes a column of data in the TableTag.  There can
 * be any (reasonable) number of columns that make up the list.<p>
 * This tag does no work itself, it is simply a container of information.  The
 * TableTag does all the work based on the information provided in the
 * attributes of this tag.<p>
 * Usage:<p>
 * <display:column property="title"
 * href="/osiris/pubs/college/edit.page"
 * paramId="OID"
 * paramProperty="OID" />
 * Attributes:<p>
 * property       - the property method that is called to retrieve the
 * information to be displayed in this column.  This method
 * is called on the current object in the iteration for
 * the given row.  The property format is in typical struts
 * format for properties (required)
 * nulls          - by default, null values don't appear in the list, by
 * setting viewNulls to 'true', then null values will
 * appear as "null" in the list (mostly useful for debugging)
 * (optional)
 * width          - the width of the column (gets passed along to the html
 * td tag). (optional)
 * href           - if this attribute is provided, then the data that is
 * shown for this column is wrapped inside a <a href>
 * tag with the url provided through this attribute.
 * Typically you would use this attribute along with one
 * of the struts-like param attributes below to create
 * a dynamic link so that each row creates a different
 * URL based on the data that is being viewed. (optional)
 * paramId        - The name of the request parameter that will be dynamically
 * added to the generated href URL. The corresponding value
 * is defined by the paramProperty and (optional) paramName
 * attributes, optionally scoped by the paramScope attribute.
 * (optional)
 * paramName      - The name of a JSP bean that is a String containing the
 * value for the request parameter named by paramId (if
 * paramProperty is not specified), or a JSP bean whose
 * property getter is called to return a String (if
 * paramProperty is specified). The JSP bean is constrained
 * to the bean scope specified by the paramScope property,
 * if it is specified.  If paramName is omitted, then it is
 * assumed that the current object being iterated on is the
 * target bean. (optional)
 * paramProperty  - The name of a property of the bean specified by the
 * paramName attribute (or the current object being iterated
 * on if paramName is not provided), whose return value must
 * be a String containing the value of the request parameter
 * (named by the paramId attribute) that will be dynamically
 * added to this href URL. (optional)
 * paramScope     - The scope within which to search for the bean specified by
 * the paramName attribute. If not specified, all scopes are
 * searched.  If paramName is not provided, then the current
 * object being iterated on is assumed to be the target bean.
 * (optional)
 * maxLength      - If this attribute is provided, then the column's displayed
 * is limited to this number of characters.  An elipse (...)
 * is appended to the end if this column is linked, and the
 * user can mouseover the elipse to get the full text.
 * (optional)
 * maxWords       - If this attribute is provided, then the column's displayed
 * is limited to this number of words.  An elipse (...) is
 * appended to the end if this column is linked, and the user
 * can mouseover the elipse to get the full text. (optional)
 */
public class ColumnTag extends BodyTagSupport implements Cloneable {
	private String _property;
	private String _displayable;
	private String _nulls;
	private String _href;
	private String _paramId;
	private String _paramName;
	private String _paramProperty;
	private String _paramScope;
	private String _width;
	private String _align;
	private String _background;
	private String _bgcolor;
	private String _height;
	private String _nowrap;
	private String _valign;
	private String _clazz;
	private String _aclazz;
	private String _headerClazz;
	private String _value;
	private String _path;
	private String _forward;
	private String _param;
	private String _name;
	private String _title;
	private int _maxLength = 0;
	private int _maxWords = 0;
	private boolean rowActionButton;

	/**
	 * -------------------------------------------------------- Accessor methods
	 *
	 *
	 *
	 * @param forward
	 * @roseuid 3E70606903C7
	 */
	public void setForward(String forward) {
        _forward = forward;
	}

	/**
	 *
	 *
	 *
	 * @param param
	 * @roseuid 3E70606A007F
	 */
	public void setParam(String param) {
        _param = param;
	}

	/**
	 *
	 *
	 * @param name
	 * @roseuid 3E70606A0093
	 */
	public void setName(String name) {
        _name = name;
	}

	/**
	 *
	 *
	 *
	 * @param property
	 * @roseuid 3E70606A00A7
	 */
	public void setProperty(String property) {
       _property = property;
	}

	public void setDisplayable(String displayable) {
		_displayable = displayable;
	}

	/**
	 *
	 *
	 *
	 * @param nulls
	 * @roseuid 3E70606A00BB
	 */
	public void setNulls(String nulls) {
        _nulls = nulls;
	}

	/**
	 *
	 *
	 *
	 * @param href
	 * @roseuid 3E70606A00CF
	 */
	public void setHref(String href) {
        _href = href;
	}

	/**
	 *
	 *
	 *
	 * @param paramId
	 * @roseuid 3E70606A017A
	 */
	public void setParamId(String paramId) {
        _paramId = paramId;
	}

	/**
	 *
	 *
	 *
	 * @param paramName
	 * @roseuid 3E70606A0197
	 */
	public void setParamName(String paramName) {
        _paramName = paramName;
	}

	/**
	 *
	 *
	 *
	 * @param paramProperty
	 * @roseuid 3E70606A01AB
	 */
	public void setParamProperty(String paramProperty) {
        _paramProperty = paramProperty;
	}

	/**
	 *
	 *
	 *
	 * @param paramScope
	 * @roseuid 3E70606A01C0
	 */
	public void setParamScope(String paramScope) {
        _paramScope = paramScope;
	}

	/**
	 *
	 *
	 *
	 * @param maxLength
	 * @roseuid 3E70606A0210
	 */
	public void setMaxLength(int maxLength) {
        _maxLength = maxLength;
	}

	/**
	 *
	 *
	 *
	 * @param maxWords
	 * @roseuid 3E70606A0224
	 */
	public void setMaxWords(int maxWords) {
        _maxWords = maxWords;
	}

	/**
	 *
	 *
	 *
	 * @param width
	 * @roseuid 3E70606A0242
	 */
	public void setWidth(String width) {
        _width = width;
	}

	/**
	 *
	 *
	 *
	 * @param align
	 * @roseuid 3E70606A0256
	 */
	public void setAlign(String align) {
        _align = align;
	}

	/**
	 *
	 *
	 *
	 * @param background
	 * @roseuid 3E70606A026A
	 */
	public void setBackground(String background) {
        _background = background;
	}

	/**
	 *
	 *
	 *
	 * @param bgcolor
	 * @roseuid 3E70606A0288
	 */
	public void setBgcolor(String bgcolor) {
        _bgcolor = bgcolor;
	}

	/**
	 *
	 *
	 *
	 * @param height
	 * @roseuid 3E70606A029C
	 */
	public void setHeight(String height) {
        _height = height;
	}

	/**
	 *
	 *
	 *
	 * @param nowrap
	 * @roseuid 3E70606A02B0
	 */
	public void setNowrap(String nowrap) {
        _nowrap = nowrap;
	}

	/**
	 *
	 *
	 *
	 * @param valign
	 * @roseuid 3E70606A02C4
	 */
	public void setValign(String valign) {
        _valign = valign;
	}

	/**
	 *
	 *
	 *
	 * @param clazz
	 * @roseuid 3E70606A02EC
	 */
	public void setStyleClass(String clazz) {
        _clazz = clazz;
	}

	/**
	 *
	 *
	 *
	 * @param aclazz
	 * @roseuid 3E70606A031E
	 */
	public void setStyleAclass(String aclazz) {
        _aclazz = aclazz;
	}

	/**
	 *
	 *
	 *
	 * @param headerClazz
	 * @roseuid 3E70606A0332
	 */
	public void setHeaderStyleClass(String headerClazz) {
        _headerClazz = headerClazz;
	}

	/**
	 *
	 *
	 *
	 * @param value
	 * @roseuid 3E70606A0350
	 */
	public void setValue(String value) {
        _value = value;
	}

	/**
	 *
	 *
	 *
	 * @param path
	 * @roseuid 3E70606B01DF
	 */
	public void setPath(String path) {
        _path = path;
	}

	/**
	 * @param title
	 * @roseuid 3E7567430223
	 */
	public void setTitle(String title) {
        _title = title;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606A036E
	 */
	public String getForward() {
        return _forward ;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606A0382
	 */
	public String getParam() {
        return _param;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606A03A0
	 */
	public String getName() {
        return _name;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606A03B4
	 */
	public String getProperty() {
        return _property;
	}

	public String getDisplayable() {
		return _displayable;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606A03C8
	 */
	public String getNulls() {
        return _nulls;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606A03E6
	 */
	public String getHref() {
        return _href;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B0012
	 */
	public String getParamId() {
        return _paramId;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B003A
	 */
	public String getParamName() {
        return _paramName;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B004E
	 */
	public String getParamProperty() {
        return _paramProperty;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B006C
	 */
	public String getParamScope() {
        return _paramScope;
	}

	/**
	 * @return int
	 * @roseuid 3E70606B0080
	 */
	public int getMaxLength() {
        return _maxLength;
	}

	/**
	 * @return int
	 * @roseuid 3E70606B009F
	 */
	public int getMaxWords() {
        return _maxWords;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B00B3
	 */
	public String getWidth() {
        return _width;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B00C7
	 */
	public String getAlign() {
        return _align;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B00E5
	 */
	public String getBackground() {
        return _background;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B00F9
	 */
	public String getBgcolor() {
        return _bgcolor;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B0117
	 */
	public String getHeight() {
        return _height;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B0135
	 */
	public String getNowrap() {
        return _nowrap;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B0149
	 */
	public String getValign() {
        return _valign;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B0167
	 */
	public String getStyleClass() {
        return _clazz;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B017B
	 */
	public String getStyleAclass() {
        return _aclazz;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B01A3
	 */
	public String getHeaderStyleClass() {
        return _headerClazz;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B01C1
	 */
	public String getValue() {
        return _value;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E70606B0225
	 */
	public String getPath() {
        return _path;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3E75676B0068
	 */
	public String getTitle() {
        return _title;
	}

	public boolean isRowActionButton() {
		return rowActionButton;
	}

	public void setRowActionButton(boolean rowActionButton) {
		this.rowActionButton = rowActionButton;
	}

	/**
	 * --------------------------------------------------------- Tag API methods
	 * Passes attribute information up to the parent TableTag.<p>
	 * When we hit the end of the tag, we simply let our parent (which better
	 * be a TableTag) know what the user wants to do with this column.
	 * We do that by simple registering this tag with the parent.  This tag's
	 * only job is to hold the configuration information to describe this
	 * particular column.  The TableTag does all the work.
	 * @return int
	 * @throws javax.servlet.jsp.JspException if this tag is being used outside of a
	 * <display:list...> tag.
	 * @roseuid 3E70606B0243
	 */
	public int doEndTag() throws JspException {
        Object parent = this.getParent();

        if (parent == null) {
            throw new JspException( "Can not use column tag outside of a " +
                "TableTag. Invalid parent = null" );
        }

        if(!( parent instanceof TableTag ) ) {
            throw new JspException( "Can not use column tag outside of a " +
                "TableTag. Invalid parent = " + parent.getClass().getName() );
        }

    // Need to clone the ColumnTag before passing it to the TableTag as
    // the ColumnTags can be reused by some containers, and since we are
    // using the ColumnTags as basically containers of data, we need to
    // save the original values, and not the values that are being changed
    // as the tag is being reused...

        ColumnTag copy = this;

        try {

            copy = (ColumnTag)this.clone();
        }
        catch( CloneNotSupportedException e ) {}

        ((TableTag)parent).addColumn( copy );

        return super.doEndTag();
	}

	/**
	 * Takes all the column pass-through arguments and bundles them up as a
	 * string that gets tacked on to the end of the td tag declaration.<p>
	 * @return java.lang.String
	 * @roseuid 3E70606B02CF
	 */
	protected String getCellAttributes() {
        StringBuffer results = new StringBuffer();

        if (_clazz != null) {
            results.append( " class=\"" );
            results.append( _clazz );
            results.append( "\"" );
        }

        if (_width != null) {
            results.append( " width=\"" );
            results.append( _width );
            results.append( "\"" );
        }

        if (_align != null) {
            results.append( " align=\"" );
            results.append( _align );
            results.append( "\"" );
        }
        else
            results.append( " align=\"left\"" );

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

        if (_height != null) {
            results.append( " height=\"" );
            results.append( _height );
            results.append( "\"" );
        }

        if (_nowrap != null)
            results.append( " nowrap" );

        if (_valign != null) {
            results.append( " valign=\"" );
            results.append( _valign );
            results.append( "\"" );
        }
        else
            results.append( " valign=\"top\"" );

        return results.toString();
	}
}
