//Source file: c:\\projects\\Common\\src\\client\\source\\com\\addval\\taglib\\TableRowTag.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.taglib;

import com.addval.metadata.EditorMetaData;
import java.io.IOException;
import com.addval.utils.AVConstants;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.ColumnDataType;
import javax.servlet.jsp.JspTagException;

/**
 * Displays a single row
 */
public class TableRowTag extends GenericBodyTag {
    private String _item;
    private int _type;
    private boolean _casted = false;
    private Object _obj;
    private int _index = 0;

    /**
     * @param item
     * @roseuid 3B6F2F740046
     */
    public void setItem(String item) {
        _item = item;
    }

    /**
     * @param name
     * @roseuid 3B6F2F750034
     */
    public void setName(String name) {
        if (name.equalsIgnoreCase( "SEARCHABLE" ))
        {
            _type = TagConstants._SEARCHABLE;
        }
        else if (name.equalsIgnoreCase( "AGGREGATABLE" ))
        {
            _type = TagConstants._AGGREGATABLE;
        }
        else if (name.equalsIgnoreCase( "DISPLAYABLE" ))
        {
            _type = TagConstants._DISPLAYABLE;
        }
        else
            throw new IllegalArgumentException ( "Invalid Name.");
    }

    /**
     * @return int
     * @throws JspTagException
     * @roseuid 3B6F2F76025C
     */
    public int doAfterBody() throws JspTagException {
        return SKIP_BODY;
    }

    /**
     * @return int
     * @throws JspTagException
     * @roseuid 3B6F2F770019
     */
    public int doStartTag() throws JspTagException {
        try
        {
            int rv;
            casting();
//            pageContext.getOut().write( "<tr>" );

            if ( _obj != null )
            {
                java.util.Enumeration enumeration = ( java.util.Enumeration ) _obj;
                String htmlString = new String();

                switch (_type)
                {
                    case TagConstants._SEARCHABLE:

						int meter = 0;
                        while ( enumeration.hasMoreElements() )
                        {
                            ColumnMetaData columnMetaData = ( ColumnMetaData ) enumeration.nextElement();

                            String columnName  = columnMetaData.getName();
                            String columnValue = pageContext.getRequest().getParameter( columnName+"_lookUp" ) != null ? pageContext.getRequest().getParameter( columnName+"_lookUp" ) : "";
                            String operator    = pageContext.getRequest().getParameter( columnName+"operator_lookUp" ) !=null ? pageContext.getRequest().getParameter( columnName+"operator_lookUp" ) : "";

                            if ( columnMetaData.getType() == ColumnDataType._CDT_NOTYPE )
                                throw new JspTagException( "ForEachTag Error: Column Type is not Defined for "+columnName);

                            if ( columnMetaData.getType() == ColumnDataType._CDT_STRING )
                            {
                                //System.out.println ( "CDT_STRING "+columnName+": "+columnValue );
                                htmlString += getTextBox ( columnMetaData, columnValue );
                            }
                            else if ( columnMetaData.getType() == ColumnDataType._CDT_LONG   ||
                                      columnMetaData.getType() == ColumnDataType._CDT_DOUBLE ||
                                      columnMetaData.getType() == ColumnDataType._CDT_INT    ||
                                      columnMetaData.getType() == ColumnDataType._CDT_DATE   ||
                                      columnMetaData.getType() == ColumnDataType._CDT_DATETIME )
                            {
                                //System.out.println ( columnMetaData.getTypeName()+" "+columnName+": "+columnValue );
                                htmlString += getComboTextBox ( columnMetaData, operator, columnValue );
                            }

                            meter++;
                            if ( meter > 2 )
                            {
								meter = 0;
								htmlString += "</tr><tr>";
							}
                        }

						for ( int j=0; j<2-meter; j++ )
							htmlString += "<td> </td>";

                        htmlString += "<td align='right'>";
                        htmlString += "<a href=\"javascript:doSubmit()\">";
                        htmlString += "<img src=\"../images/search.gif\" border=\"0\" alt=\"Search\" >";
                        htmlString += "</a></td>" ;

                        break;

                    case TagConstants._AGGREGATABLE:
/*
                        while ( enumeration.hasMoreElements() )
                        {
                            ColumnMetaData columnMetaData = (ColumnMetaData) enumeration.nextElement();
                            boolean isChecked = pageContext.getRequest().getParameter( columnMetaData.getName()+"_group" ) != null ? true : false;

                            htmlString += getCheckBox ( columnMetaData , isChecked );
                        }

                        Object e_obj = pageContext.getAttribute( getId() ,getScope() );
                        EditorMetaData editorMetaData = ( EditorMetaData ) e_obj;
                        if (editorMetaData.getSearchableColumns() == null)
                        {
                            htmlString += "</td><td>";
                            htmlString += "<a href=\"javascript:doSubmit()\">";
                            htmlString += "<img src=\"../images/search.gif\" border=\"0\" alt=\"Search\" >";
                            htmlString += "</a>";
                        }
                        htmlString += "</td>";
*/
                        break;

                    case TagConstants._DISPLAYABLE:

                        while ( enumeration.hasMoreElements() )
                        {
                            ColumnMetaData columnMetaData = ( ColumnMetaData ) enumeration.nextElement();
                            htmlString += getLabelSort ( columnMetaData );
                        }
                }
                pageContext.setAttribute ( _item, htmlString );

                rv = EVAL_BODY_BUFFERED;
            }
            else
            {
                rv = SKIP_BODY;
            }
            return rv;
        }
        catch(Exception e)
        {
            try
            {
                pageContext.getOut().write ( e.toString() );
                return SKIP_BODY;
            }
            catch( IOException ioe )
            {
                throw new JspTagException( "TableRowTag Error:" + ioe.toString() );
            }
        }
    }

    /**
     * @return int
     * @throws JspTagException
     * @roseuid 3B6F2F770109
     */
    public int doEndTag() throws JspTagException {
        try
        {
            if (getBodyContent() != null)
                getBodyContent().writeOut(pageContext.getOut() );

//            pageContext.getOut().write( "</tr>" );
        }
        catch( IOException ioexception ) {
            release();
            throw new JspTagException( ioexception.getMessage() );
        }
        if( _item != null )
            pageContext.removeAttribute( _item );

        return EVAL_PAGE;
    }

    /**
     * @roseuid 3B6F2F77029A
     */
    public void release() {
        _item   = null;
        _obj    = null;
        _type   = 0;
        super.release();
    }

    /**
     * @roseuid 3B6F2F7703C6
     */
    private void casting() {
        _casted = true;
        Object e_obj = pageContext.getAttribute( getId(), getScope() );
        if ( e_obj != null && e_obj instanceof EditorMetaData )
        {
            EditorMetaData editorMetaData = ( EditorMetaData ) e_obj;
            switch (_type)
            {
                case TagConstants._SEARCHABLE:
                     _obj  = editorMetaData.getSearchableColumns() == null ? null : editorMetaData.getSearchableColumns().elements();
                    break;
                case TagConstants._AGGREGATABLE:
                    _obj  = editorMetaData.getAggregatableColumns() == null ? null : editorMetaData.getAggregatableColumns().elements();
                    break;
                case TagConstants._DISPLAYABLE:
                    _obj  = editorMetaData.getDisplayableColumns() == null ? null : editorMetaData.getDisplayableColumns().elements();
                    break;
            }
        }
        else
        {
            throw new IllegalArgumentException("TableRowTag:Invalid Object." );
        }
    }

    /**
     * @param columnMetaData
     * @param value
     * @return String
     * @throws JspTagException
     * @roseuid 3B6F2F780147
     */
    private String getTextBox(ColumnMetaData columnMetaData, String value) throws JspTagException {
        StringBuffer htmlString = new StringBuffer( "<td class=\"label\"><nobr>" );
        String columnName = columnMetaData.getName();
        String checkParameters = "\'" + columnName+"_lookUp" + "\',\'" +
                                 ( columnMetaData.getFormat()==null ? "" : columnMetaData.getFormat() ) + "\',\'" +
                                 Double.toString(columnMetaData.getMinval()) + "\',\'" +
                                 Double.toString(columnMetaData.getMaxval()) + "\',\'" +
                                 ( columnMetaData.getRegexp()==null ? "" : columnMetaData.getRegexp() ) + "\',\'" +
                                 ( columnMetaData.getErrorMsg()==null ? "" : columnMetaData.getErrorMsg() ) + "\'";

        if (columnMetaData.getCaption() != null )
            htmlString.append( columnMetaData.getCaption() );
        else
            htmlString.append( columnMetaData.getName() );

        if ( columnMetaData.isCombo() )
        {
            htmlString.append( "<a href =\"" );
            htmlString.append( "javascript:launchsubmit ( " );
            htmlString.append( "'" );
            htmlString.append( columnName+"_lookUp" );
            htmlString.append( "','" );
//            htmlString.append( lookUpSQL );
            htmlString.append ( columnMetaData.getComboTblName() );
            htmlString.append( "','" );
            if (columnMetaData.getComboOrderBy() != null)
                htmlString.append( columnMetaData.getComboOrderBy() );
            else
                htmlString.append( columnMetaData.getName() );
            htmlString.append( "','" );
            if (columnMetaData.getCaption() != null)
                htmlString.append( columnMetaData.getCaption() );
            else
                htmlString.append( columnMetaData.getName() );
            htmlString.append( "','" );
            htmlString.append( columnMetaData.getName () );
            htmlString.append( "') \">" );
            htmlString.append( "<img src=\"../images/lookup.gif\" border=0 alt=\"Lookup\"></a> " );
        }
        htmlString.append( "<input " );
        htmlString.append( " type =\"text\" " );
        htmlString.append( " name =\"" );
        htmlString.append( columnName+"_lookUp" );
        htmlString.append( "\"" );
        htmlString.append( " value =\"" );
        htmlString.append( value );
        htmlString.append( "\"" );
        htmlString.append( " onChange =\"return check("+checkParameters+")\"" );
        if (columnMetaData.getFormat() !=null )
        {
            htmlString.append( " size =\"" );
            htmlString.append( String.valueOf ( columnMetaData.getFormat().length () + 1 ) );
            htmlString.append( "\"" );
            htmlString.append( " maxlength =\"" );
            htmlString.append( String.valueOf ( columnMetaData.getFormat().length () ) );
            htmlString.append( "\" " );
        }

        if ( columnMetaData.getType() == ColumnDataType._CDT_DATE ||
             columnMetaData.getType() == ColumnDataType._CDT_DATETIME )
        {
            htmlString.append ( " onBlur=\"return getDisplayDateFormat(this.name)\"" );
            htmlString.append ( ">" );
//CALENDAR
            htmlString.append ( "&nbsp;" );
            htmlString.append ( "<img src=\"../images/calendar.gif\" border=0 height=\"18\" width=\"20\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" onClick=\"return launchMyCalendar('" );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "'," );
            htmlString.append ( "getIndex('" );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "'),'" );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "')\"" );
        }
        else
        {
            htmlString.append ( ">" );
        }

        htmlString.append( "</nobr></td>" );
        return htmlString.toString ();
    }

    /**
     * @param columnMetaData
     * @param operator
     * @param value
     * @return String
     * @roseuid 3B6F2F79021A
     */
    private String getComboTextBox(ColumnMetaData columnMetaData, String operator, String value) {
        StringBuffer htmlString = new StringBuffer( "<td class=\"label\"><nobr>" );
        String columnName = columnMetaData.getName();
        String checkParameters = "\'" + columnName+"_lookUp" + "\',\'" +
                                 ( columnMetaData.getFormat()==null ? "" : columnMetaData.getFormat() ) + "\',\'" +
                                 Double.toString(columnMetaData.getMinval()) + "\',\'" +
                                 Double.toString(columnMetaData.getMaxval()) + "\',\'" +
                                 ( columnMetaData.getRegexp()==null ? "" : columnMetaData.getRegexp() ) + "\',\'" +
                                 ( columnMetaData.getErrorMsg()==null ? "" : columnMetaData.getErrorMsg() ) + "\'";

        if (columnMetaData.getCaption() !=null)
            htmlString.append( columnMetaData.getCaption() );
        else
            htmlString.append( columnMetaData.getName() );

        htmlString.append( "<select " );
        htmlString.append( " name =\"" );
        htmlString.append( columnName+"operator_lookUp" );
        htmlString.append( "\"" );
        htmlString.append( " >" );

        if (operator.equals( String.valueOf( AVConstants._EQUAL ) ))
            htmlString.append( " <option value=\""+AVConstants._EQUAL+"\" selected>"+ String.valueOf( AVConstants._EQUAL_SIGN ) );
        else
            htmlString.append( " <option value=\""+AVConstants._EQUAL+"\" >"+String.valueOf ( AVConstants._EQUAL_SIGN ) );

        if (operator.equals( String.valueOf( AVConstants._GREATER ) ))
            htmlString.append( " <option value=\""+AVConstants._GREATER+"\" selected>"+String.valueOf( AVConstants._GREATER_SIGN ) );
        else
            htmlString.append( " <option value=\""+AVConstants._GREATER+"\">"+ String.valueOf ( AVConstants._GREATER_SIGN ) );

        if (operator.equals( String.valueOf( AVConstants._GREATER_EQUAL ) ))
            htmlString.append( " <option value=\""+AVConstants._GREATER_EQUAL+"\" selected>"+AVConstants._GREATER_EQUAL_SIGN );
        else
            htmlString.append( " <option value=\""+AVConstants._GREATER_EQUAL+"\" >"+AVConstants._GREATER_EQUAL_SIGN );

        if (operator.equals( String.valueOf( AVConstants._LESSER ) ) )
            htmlString.append( " <option value=\""+AVConstants._LESSER+"\" selected>"+String.valueOf( AVConstants._LESSER_SIGN ) );
        else
            htmlString.append( " <option value=\""+AVConstants._LESSER+"\">"+String.valueOf( AVConstants._LESSER_SIGN ) );

        if (operator.equals( String.valueOf( AVConstants._LESSER_EQUAL ) ) )
            htmlString.append( " <option value=\""+AVConstants._LESSER_EQUAL+"\" selected>"+AVConstants._LESSER_EQUAL_SIGN );
        else
            htmlString.append( " <option value=\""+AVConstants._LESSER_EQUAL+"\" >"+AVConstants._LESSER_EQUAL_SIGN );

        htmlString.append ( "</select> " );

        _index++;

        htmlString.append ( "<input "            );
        htmlString.append ( " type =\"text\" "   );
        htmlString.append ( " name =\""          );
        htmlString.append ( columnName+"_lookUp" );
        htmlString.append ( "\""                 );
        htmlString.append ( " value =\""         );
        htmlString.append ( value                );
        htmlString.append ( "\""                 );
        htmlString.append ( " onChange =\"return check("+checkParameters+")\"" );
        if ( columnMetaData.getFormat() !=null )
        {
            htmlString.append( " size =\"" );
            htmlString.append( String.valueOf ( columnMetaData.getFormat ().length () + 1 ) );
            htmlString.append( "\"" );
            htmlString.append( " maxlength =\"" );
            htmlString.append( String.valueOf ( columnMetaData.getFormat ().length () + 1 ) );
            htmlString.append( "\""                     );
        }

        if ( columnMetaData.getType() == ColumnDataType._CDT_DATE ||
             columnMetaData.getType() == ColumnDataType._CDT_DATETIME )
        {
            htmlString.append ( " onBlur=\"return getDisplayDateFormat(this.name)\"" );
            htmlString.append ( ">" );
//CALENDAR
            htmlString.append ( "&nbsp;" );
            htmlString.append ( "<img src=\"../images/calendar.gif\" border=0 height=\"18\" width=\"20\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" onClick=\"return launchMyCalendar('" );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "'," );
            htmlString.append ( "getIndex('" );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "'),'" );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "')\"" );
        }
        else
        {
            htmlString.append ( ">" );
        }

        htmlString.append ( "</nobr></td>" );
        return htmlString.toString();
    }

    /**
     * @param columnMetaData
     * @param isChecked
     * @return String
     * @roseuid 3B6F2F7A03DE
     */
    private String getCheckBox(ColumnMetaData columnMetaData, boolean isChecked) {
        _index++;
        StringBuffer htmlString = new StringBuffer ();
        if (columnMetaData.getCaption() !=null )
            htmlString.append( columnMetaData.getCaption() );
        else
            htmlString.append( columnMetaData.getName() );

        htmlString.append( "<input " );
        htmlString.append( "type =\"checkbox\" " );
        htmlString.append( " name =\"" );
        htmlString.append( columnMetaData.getName()+"_group" );
        htmlString.append( "\"" );
        htmlString.append( " value =\"" );
        htmlString.append( columnMetaData.getName()+"||"+columnMetaData.getComboSelect() );
        htmlString.append( "\" " );
        if (isChecked)
            htmlString.append ( "checked" );

        htmlString.append ( " > " );

        htmlString.append( "<input " );
        htmlString.append( " type=\"hidden\"" );
        htmlString.append( " name=\"GROUP_BY\"" );
        htmlString.append( " value=\"" );
        htmlString.append( columnMetaData.getName() );
        htmlString.append( "\">" );
        return htmlString.toString ();
    }

    /**
     * @param columnMetaData
     * @return String
     * @roseuid 3B6F2F7B0340
     */
    private String getLabelSort(ColumnMetaData columnMetaData) {
        _index++;
        StringBuffer htmlString = new StringBuffer ( "<th>" );
        htmlString.append( "<a class=\"header\"" );
        htmlString.append( "href=\"javascript:setSortingAction('" );
        htmlString.append( columnMetaData.getName() );
        htmlString.append( "')\">" );
        if (columnMetaData.getCaption() !=null)
            htmlString.append( columnMetaData.getCaption() );
        else
            htmlString.append( columnMetaData.getName() );
        htmlString.append( "</a>" );
        htmlString.append( "</th>" );
        return htmlString.toString();
    }
}
