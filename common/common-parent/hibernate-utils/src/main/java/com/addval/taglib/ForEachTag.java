//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\taglib\\ForEachTag.java

//Source file: c:\\projects\\Common\\src\\client\\source\\com\\addval\\taglib\\ForEachTag.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.taglib;

import com.addval.metadata.EditorMetaData;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.utils.AVConstants;
import java.io.IOException;
import java.util.Vector;

import com.addval.metadata.ColumnDataType;
import javax.servlet.jsp.JspTagException;
import com.addval.metadata.ColumnMetaData;
import com.addval.dbutils.RSIterator;
import com.addval.dbutils.RSIterAction;
import com.addval.environment.Environment;

public class ForEachTag extends GenericBodyTag
{
	private int _type;
	private boolean _casted = false;
	private Object _obj;
	private int _index = 0;
	private boolean _isEditable = true;
	private boolean _isDeletable = true;
	private String _details = "";
	private String _viewrole = "";
	private String _editrole = "";
	private boolean _hasViewPermission = true;
	private boolean _hasEditPermission = true;
    private int _searchColumnsPerRow;
	private String _searchableMandatorySymbol = null;




	/**
	 * @roseuid 3B8432C30350
	 */
	public ForEachTag()
	{
       _searchableMandatorySymbol = Environment.getInstance().getCnfgFileMgr().getPropertyValue("mandatory.symbol", "*");
	}

	public int getSearchColumnsPerRow() { return _searchColumnsPerRow; }

	public void setSearchColumnsPerRow( int aSearchColumnsPerRow ) { _searchColumnsPerRow = aSearchColumnsPerRow; }

	/**
	 * Determines if the _isEditable property is true.
	 *
	 * @return   <code>true<code> if the _isEditable property is true
	 */
	public boolean getIsEditable()
	{
        return _isEditable;
	}

	/**
	 * Sets the value of the _isEditable property.
	 *
	 * @param aIsEditable the new value of the _isEditable property
	 */
	public void setIsEditable(boolean aIsEditable)
	{
        _isEditable = aIsEditable;
	}

	/**
	 * Determines if the _isDeletable property is true.
	 *
	 * @return   <code>true<code> if the _isDeletable property is true
	 */
	public boolean getIsDeletable()
	{
        return _isDeletable;
	}

	/**
	 * Sets the value of the _isDeletable property.
	 *
	 * @param aIsDeletable the new value of the _isDeletable property
	 */
	public void setIsDeletable(boolean aIsDeletable)
	{
        _isDeletable = aIsDeletable;
	}

	/**
	 * @param name
	 * @roseuid 3B42E66500CD
	 */
	public void setName(String name)
	{
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
        else if (name.equalsIgnoreCase( "LOOKUP_SEARCHABLE" ))
        {
            _type = TagConstants._LOOKUP_SEARCHABLE;
        }
        else
            throw new IllegalArgumentException ( "Invalid Name.");
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B42E6A40131
	 */
	public int doAfterBody() throws JspTagException
	{
        return SKIP_BODY;
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B42E6A4013B
	 */
	public int doStartTag() throws JspTagException
	{
        try
        {
            casting();



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


            pageContext.getOut().write( "<tr class=\"label\">" );

            if ( _obj != null )
            {
                java.util.Enumeration enumeration = ( java.util.Enumeration ) _obj;
                String htmlString = new String();

                switch (_type)
                {
                    case TagConstants._LOOKUP_SEARCHABLE:
                        if (_hasViewPermission) {
                            ColumnMetaData columnMetaData = null;

                            String operator = null;
                            String fieldName = null;
                            String fieldValue = null;
                            int columnsPerRow = getSearchColumnsPerRow();

                            while ( enumeration.hasMoreElements() ) {

                                columnMetaData = ( ColumnMetaData ) enumeration.nextElement();
                                fieldName = columnMetaData.getName();
                                fieldValue = null;

                                if(fieldValue == null && pageContext.getRequest().getParameter( fieldName+"_edit" ) != null){
                                    fieldValue = (String) pageContext.getRequest().getParameter( fieldName+"_edit" );
                                }
                                if(fieldValue == null && pageContext.getRequest().getParameter( fieldName+"_lookUp" ) != null) {
                                    fieldValue = (String) pageContext.getRequest().getParameter( fieldName+"_lookUp" );
                                }
                                if(fieldValue == null && pageContext.getRequest().getParameter( fieldName+"_look" ) != null) {
                                    fieldValue = (String) pageContext.getRequest().getParameter( fieldName+"_look" );
                                }
                                if(fieldValue == null && pageContext.getRequest().getParameter( fieldName+"_search" ) != null) {
                                    fieldValue = (String) pageContext.getRequest().getParameter( fieldName+"_search" );
                                }

                                if(fieldValue == null)
                                    fieldValue = "";
                                else if(fieldValue.endsWith("%") )
                                    fieldValue = fieldValue.substring(0,fieldValue.length()-1);


                                if ( columnMetaData.getType() == ColumnDataType._CDT_NOTYPE )
                                    throw new JspTagException( "ForEachTag Error: Column Type is not Defined for "+fieldName);

                                if (columnMetaData.isCombo() ||
                                    columnMetaData.getType() == ColumnDataType._CDT_STRING ||
                                    columnMetaData.getType() == ColumnDataType._CDT_CLOB   ||
                            		columnMetaData.getType() == ColumnDataType._CDT_BLOB   ||
                                    columnMetaData.getType() == ColumnDataType._CDT_LONG   ||
                                    columnMetaData.getType() == ColumnDataType._CDT_DOUBLE ||
                                    columnMetaData.getType() == ColumnDataType._CDT_INT    ||
                                    columnMetaData.getType() == ColumnDataType._CDT_DATE   ||
                                    columnMetaData.getType() == ColumnDataType._CDT_DATETIME ) {
                                    htmlString += getSearchTextBox ( columnMetaData, fieldValue);

                                    columnsPerRow--;
                                }

                                if( columnsPerRow == 0 ) {
                                    columnsPerRow = getSearchColumnsPerRow();
                                    htmlString += "</tr><tr>";
                                }
                            }
                            if(columnsPerRow < getSearchColumnsPerRow()) {
                                for ( int j=columnsPerRow; j>1; --j ){
                                    htmlString += "<td>&nbsp;</td><td>&nbsp;</td>";
                                }
                            }
                            else if(columnsPerRow == getSearchColumnsPerRow() && htmlString.endsWith("</tr><tr>")) {
                                htmlString = htmlString.substring(0,htmlString.length()- "</tr><tr>".length() );
                            }
                            htmlString += "<td>&nbsp;</td><td align='right'>";
                            htmlString += "<a href=\"javascript:doSubmit()\">";
                            htmlString += "<img src=\"../images/search.gif\" border=\"0\" alt=\"Search\" >";
                            htmlString += "</a></td>" ;

                        }
                        else {
                            htmlString += "<td class=\"error\">";
                            htmlString += "<center><B>No permission to view this page</B></center>";
                            htmlString += "</td>";
						}
                        break;
                    case TagConstants._SEARCHABLE:

						if (_hasViewPermission)
						{
                            int columnsPerRow = getSearchColumnsPerRow();
							while ( enumeration.hasMoreElements() ) {
								ColumnMetaData columnMetaData = ( ColumnMetaData ) enumeration.nextElement();

								// Look in attribute first. Only if it is not there lookup the parameters
								// Satya changed it after implementing the FormInterceptor interface

								String columnName  		 = columnMetaData.getName();
								String columnValue = pageContext.getRequest().getAttribute( columnName+"_lookUp" ) != null ? (String) pageContext.getRequest().getAttribute( columnName+"_lookUp" ) : "";
								String operator    = pageContext.getRequest().getAttribute( columnName+"operator_lookUp" ) !=null ? (String) pageContext.getRequest().getAttribute( columnName+"operator_lookUp" ) : "";


								if (columnValue.equals(""))
								{
									columnValue 		 = pageContext.getRequest().getParameter( columnName+"_lookUp" ) != null ? pageContext.getRequest().getParameter( columnName+"_lookUp" ) : "";
									operator    		 = pageContext.getRequest().getParameter( columnName+"operator_lookUp" ) !=null ? pageContext.getRequest().getParameter( columnName+"operator_lookUp" ) : "";
								}

                                if(columnValue.endsWith("%") )
                                    columnValue = columnValue.substring(0,columnValue.length()-1);

								if ( columnMetaData.getType() == ColumnDataType._CDT_NOTYPE )
									throw new JspTagException( "ForEachTag Error: Column Type is not Defined for "+columnName);

								if ( columnMetaData.getType() == ColumnDataType._CDT_CLOB ||  columnMetaData.getType() == ColumnDataType._CDT_BLOB || columnMetaData.getType() == ColumnDataType._CDT_STRING || columnMetaData.isCombo() ) {
									htmlString += getTextBox ( columnMetaData, columnValue);
                                    columnsPerRow--;
								}
								else if ( columnMetaData.getType() == ColumnDataType._CDT_LONG   ||
										  columnMetaData.getType() == ColumnDataType._CDT_DOUBLE ||
										  columnMetaData.getType() == ColumnDataType._CDT_INT    ||
										  columnMetaData.getType() == ColumnDataType._CDT_DATE   ||
										  columnMetaData.getType() == ColumnDataType._CDT_DATETIME )
								{
									htmlString += getComboTextBox ( columnMetaData, operator, columnValue );
                                    columnsPerRow--;
								}

                                if( columnsPerRow == 0 ) {
                                    columnsPerRow = getSearchColumnsPerRow();
                                    htmlString += "</tr><tr>";
                                }
							}
                            if(columnsPerRow < getSearchColumnsPerRow()) {
                                for ( int j=columnsPerRow; j>1; --j ){
                                    htmlString += "<td>&nbsp;</td><td>&nbsp;</td>";
                                }
                            }
                            else if(columnsPerRow == getSearchColumnsPerRow() && htmlString.endsWith("</tr><tr>")) {
                                htmlString = htmlString.substring(0,htmlString.length()- "</tr><tr>".length() );
                            }

							htmlString += "<td>&nbsp;</td><td align='right'>";
							htmlString += "<a href=\"javascript:doSubmit()\">";
							htmlString += "<img src=\"../images/search.gif\" border=\"0\" alt=\"Search\" >";
							htmlString += "</a></td>" ;
						}
                        else{
                            htmlString += "<td class=\"error\">";
                            htmlString += "<center><B>No permission to view this page</B></center>";
                            htmlString += "</td>";
						}
                        break;

                    case TagConstants._AGGREGATABLE:

						if (_hasViewPermission)
						{
							EditorMetaData editorMetaData = ( EditorMetaData )pageContext.getAttribute( getId(), getScope() );

							if (editorMetaData.getAggregatableColumns() != null) {

								ColumnMetaData  columnMetaData  = null;
								boolean         isChecked       = false;
								enumeration                            = editorMetaData.getAggregatableColumns().elements();

								while (enumeration.hasMoreElements()) {

									columnMetaData  = (ColumnMetaData)enumeration.nextElement();
									isChecked       = pageContext.getRequest().getParameter( columnMetaData.getName() + "_group" ) != null;

									htmlString += "<td>";
									htmlString += getCheckBox ( columnMetaData , isChecked );
									htmlString += "</td>";
								}
							}
						}

                        break;

                    case TagConstants._DISPLAYABLE:
						if (_hasViewPermission)
						{
							while ( enumeration.hasMoreElements() )
							{
								ColumnMetaData columnMetaData = ( ColumnMetaData ) enumeration.nextElement();
								htmlString += getLabelSort ( columnMetaData );
							}
						}
                }
                pageContext.getOut().write ( htmlString );

                pageContext.getOut().write( "</tr>" );
            }

            return SKIP_BODY;
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
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B42E6A4014F
	 */
	public int doEndTag() throws JspTagException
	{
        try
        {
            return EVAL_PAGE;
        }
        catch( Exception e ) {
            release();
            throw new JspTagException( e.getMessage() );
        }
	}

	/**
	 * @roseuid 3B42E6A40177
	 */
	public void release()
	{

        _obj    = null;
        _type   = 0;
        super.release();
	}

	/**
	 * @throws IllegalArgumentException
	 * @throws java.lang.IllegalArgumentException
	 * @roseuid 3B42E6C901AD
	 */
	private void casting() throws IllegalArgumentException
	{
        _casted = true;
        Object e_obj = pageContext.getAttribute( getId(), getScope() );
        if ( e_obj != null && e_obj instanceof EditorMetaData )
        {
            EditorMetaData editorMetaData = ( EditorMetaData ) e_obj;
            switch (_type)
            {
                case TagConstants._LOOKUP_SEARCHABLE:
                     _obj  = editorMetaData.getSearchableColumns() == null ? null : editorMetaData.getSearchableColumns().elements();
                    break;
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
    private String getSearchTextBox(ColumnMetaData columnMetaData, String value) {
        StringBuffer htmlString = new StringBuffer( "<td class=\"label\"><nobr>" );
        String columnName = columnMetaData.getName();
        String columnCaption = columnMetaData.getCaption();
        String checkParameters = "\'" + columnName+"_lookUp" + "\',\'" +
                                 ( columnMetaData.getFormat()==null ? "" : columnMetaData.getFormat() ) + "\',\'" +
                                 Double.toString(columnMetaData.getMinval()) + "\',\'" +
                                 Double.toString(columnMetaData.getMaxval()) + "\',\'" +
                                 ( columnMetaData.getRegexp()==null ? "" : columnMetaData.getRegexp() ) + "\',\'" +
                                 ( columnMetaData.getErrorMsg()==null ? "" : columnMetaData.getErrorMsg() ) + "\'";

		htmlString.append( columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption() );

		// has been added for searchableMandatory fields
		if	(columnMetaData.isSearchableMandatory()) {
			htmlString.append(	" <font class=\"mandatory\">" + _searchableMandatorySymbol + "</font>:"	);
		}


        //ComboBox
        boolean isSelectControl = (columnMetaData.getEjbResultSet() != null);
        boolean isCalendarControl = ( columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME );
        boolean isSecuredControl = ((columnMetaData.isSecured()) && (pageContext.getRequest().getAttribute( columnName+"_unlock" ) == null) );
        if(isSelectControl) {
            EJBResultSet comboResultSet  = (EJBResultSet) columnMetaData.getEjbResultSet();
            comboResultSet.beforeFirst();
            EditorMetaData optMetaData = ((EJBResultSetMetaData) comboResultSet.getMetaData()).getEditorMetaData();
            EJBStatement stmt = new EJBStatement ( comboResultSet );
            RSIterator   rsItem  = new RSIterator(stmt, "1", new RSIterAction(RSIterAction._FIRST), comboResultSet.getRecords().size(),  comboResultSet.getRecords().size()==0?10:comboResultSet.getRecords().size());
            Vector  displayableColumns = optMetaData.getDisplayableColumns();

            htmlString.append( "&nbsp;" );
            htmlString.append( "<SELECT " );
            htmlString.append( "name='" + columnName + "_lookUp" + "' >" );
            // always add a blank option
            htmlString.append("<OPTION VALUE=''> </OPTION>");
            String optValue = "";
            String optDesc  = "";
            String selected = "";
            String optcolName = null;
            String optcolValue = null;
            ColumnMetaData optcolMetaData   = null;

            while (rsItem.next()) {
                for (int j=0; j<displayableColumns.size(); j++) {
                    optcolMetaData   = ( ColumnMetaData ) displayableColumns.elementAt(j);
                    optcolName 	=  optcolMetaData.getName();
                    optcolValue 	= rsItem.getString(optcolName);

                    if (optcolValue == null)
                        optcolValue = "";

                    if (optcolName.equalsIgnoreCase(columnName))
                        optValue = optcolValue;

                    if (optcolName.equalsIgnoreCase(columnName))
                        optDesc = optcolValue;
                }
                selected = optValue.equals(value)?"selected":"";

                htmlString.append( "<OPTION VALUE='" + optValue + "' " + selected + ">" );
                htmlString.append( optDesc );
                htmlString.append("</OPTION>");
            }
            htmlString.append( "</SELECT>" );
        }
        else if(isSecuredControl) {

            htmlString.append( "&nbsp;" );
            htmlString.append( value );
            htmlString.append( "<input " );
            htmlString.append( " type =\"hidden\" " );
            htmlString.append( " name =\"" );
            htmlString.append( columnName+"_lookUp" );
            htmlString.append( "\"" );
            htmlString.append( " value =\"" );
            htmlString.append( value );
            htmlString.append( "\"" );
            htmlString.append ( ">" );
        }
        else {
            htmlString.append ( "&nbsp;" );
            htmlString.append ( "<input "            );
            htmlString.append ( " type =\"text\" "   );
            htmlString.append ( " name =\""          );
            htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "\""                 );
            htmlString.append ( " value =\""         );
            htmlString.append ( value                );
            htmlString.append ( "\""                 );
            if(isCalendarControl) {

                htmlString.append ( " onBlur=\"isValidDate( this, '" + columnMetaData.getFormat() + "' )\"" );
                htmlString.append ( ">" );
                htmlString.append ("<a href=\"javascript:launchCalendar('");
                htmlString.append ( columnName+"_lookUp" );
                htmlString.append ( "','" + columnMetaData.getFormat() + "')\">" );
                htmlString.append ( "<img src=\"../images/calendar.gif\" border=0 height=\"16\" width=\"25\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" >" );
                htmlString.append ( "</a>" );
            }
            else {
                htmlString.append ( " onChange =\"return check("+checkParameters+")\"" );
                if ( columnMetaData.getFormat() != null ) {
                    int maxLen = columnMetaData.getFormatLength();
                    htmlString.append( " size =\"" );
                    htmlString.append( String.valueOf ( maxLen > 99 ? 100 : maxLen + 1 ) );
                    htmlString.append( "\"" );
                    htmlString.append( " maxlength =\"" );
                    htmlString.append( String.valueOf ( maxLen ) );
                    htmlString.append( "\""                     );
                }
                htmlString.append ( ">" );
            }

        }
        htmlString.append( "</nobr></td>" );
        return htmlString.toString ();
    }

	/**
	 * @param columnMetaData
	 * @param value
	 * @return String
	 * @roseuid 3B42E6FB01FF
	 */
	private String getTextBox(ColumnMetaData columnMetaData, String value)
	{
        StringBuffer htmlString = new StringBuffer(  );
        String columnName = columnMetaData.getName();
        String columnCaption = columnMetaData.getCaption();
        String checkParameters = "\'" + columnName+"_lookUp" + "\',\'" +
                                 ( columnMetaData.getFormat()==null ? "" : columnMetaData.getFormat() ) + "\',\'" +
                                 Double.toString(columnMetaData.getMinval()) + "\',\'" +
                                 Double.toString(columnMetaData.getMaxval()) + "\',\'" +
                                 ( columnMetaData.getRegexp()==null ? "" : columnMetaData.getRegexp() ) + "\',\'" +
                                 ( columnMetaData.getErrorMsg()==null ? "" : columnMetaData.getErrorMsg() ) + "\'";


		// Normally the selected column is same as the current column

		String selectColumnName   			= columnName;
		ColumnMetaData selectColumnMetaData = columnMetaData;
		String selectColumnValue            = value;
		String selectCheckParameters        = checkParameters;
		String comboresetFunction 			= "";

		EditorMetaData editorMetaData = ( EditorMetaData )pageContext.getAttribute( getId(), getScope() );

		// If it is combo determine then the selected column could be different
		if (columnMetaData.isCombo())
		{
			if ( columnMetaData.getComboSelect() != null )
			{
	            if (!columnMetaData.getComboSelect().equalsIgnoreCase(columnName))
				{
					selectColumnName 	 = columnMetaData.getComboSelect();

					int idx 			 = editorMetaData.getColumnIndex(columnMetaData.getComboSelect());

					selectColumnMetaData = editorMetaData.getColumnMetaData(idx);

					selectColumnValue    = pageContext.getRequest().getParameter( columnMetaData.getComboSelect()+"_lookUp" ) != null ? pageContext.getRequest().getParameter( columnMetaData.getComboSelect()+"_lookUp" ) : "";

					if (selectColumnValue.equals(""))
					{
						// check if there is an attribute
						selectColumnValue    = pageContext.getRequest().getAttribute( columnMetaData.getComboSelect()+"_lookUp" ) != null ? (String) pageContext.getRequest().getAttribute( columnMetaData.getComboSelect()+"_lookUp" ) : "";
					}

					selectCheckParameters = "\'" + selectColumnName+"_lookUp" + "\',\'" +
									 ( selectColumnMetaData.getFormat()==null ? "" : selectColumnMetaData.getFormat() ) + "\',\'" +
									 Double.toString(selectColumnMetaData.getMinval()) + "\',\'" +
									 Double.toString(selectColumnMetaData.getMaxval()) + "\',\'" +
									 ( selectColumnMetaData.getRegexp()==null ? "" : selectColumnMetaData.getRegexp() ) + "\',\'" +
									 ( selectColumnMetaData.getErrorMsg()==null ? "" : selectColumnMetaData.getErrorMsg() ) + "\'";
				}
			}

		}

		htmlString.append( "<td class=\"label\"><nobr>" );

		htmlString.append( selectColumnMetaData.getCaption() == null ? selectColumnMetaData.getName() : selectColumnMetaData.getCaption() );

		// has been added for searchableMandatory fields
		if	(selectColumnMetaData.isSearchableMandatory()) {
			htmlString.append(	" <font class=\"mandatory\">" + _searchableMandatorySymbol + "</font>:"	);
		}

        if ( columnMetaData.isCombo() &&
                ( (columnMetaData.isSecured() == false) ||
                  (pageContext.getRequest().getAttribute( columnMetaData.getName()+"_unlock" ) != null)
                )
           )
        {

            if (columnMetaData.getEjbResultSet() == null)
            {


                htmlString.append( "<a href =\"" );

                htmlString.append( "javascript:launchLookup ( " );
                htmlString.append( "'" );

                if ( columnMetaData.getComboSelect() != null )
                htmlString.append( columnMetaData.getComboSelect()+"_lookUp" );
                    else
                htmlString.append( columnName+"_lookUp" );

                htmlString.append( "','" );
                htmlString.append ( columnMetaData.getComboTblName() );
                htmlString.append( "','" );
                if (columnMetaData.getComboOrderBy() != null)
                htmlString.append( columnMetaData.getComboOrderBy() );
                else
                htmlString.append( columnName );
                htmlString.append( "','" );
                htmlString.append( columnName+"_lookUp" );
                htmlString.append( "','" );
                htmlString.append( columnCaption );
                htmlString.append( "') \"  onClick=\"currObj =event.srcElement;\" >" );

                    htmlString.append( "<img src=\"../images/lookup.gif\" border=0 alt=\"Lookup\"></a> " );
            }
        }
		htmlString.append( "</nobr></td>" );
		htmlString.append( "<td class=\"content\"><nobr>" );


		// if comboselect name is different from the column name add a hidden variable
        if ( columnMetaData.isCombo() )
        {
            if ( columnMetaData.getComboSelect() != null )
            {
                if (!columnMetaData.getComboSelect().equalsIgnoreCase(columnName))
                {

			if (columnMetaData.getEjbResultSet() == null)
			{

			        htmlString.append( "<input " );
			        htmlString.append( " type =\"hidden\" " );
			        htmlString.append( " name =\"" );
			        htmlString.append( columnName+"_lookUp" );
			        htmlString.append( "\"" );
        			htmlString.append( " value =\"" );
        			htmlString.append( value );
        			htmlString.append( "\"" );
		            htmlString.append ( ">" );


				    comboresetFunction = "document.forms[0]." + columnName + "_lookUp.value" + "=" + "'';";
			}

		 }
	     }

	}


	if (columnMetaData.getEjbResultSet() == null)
	{
		htmlString.append( "&nbsp;" );
		htmlString.append( "<input " );
		htmlString.append( " type =\"text\" " );
		htmlString.append( " name =\"" );
		htmlString.append(  selectColumnName +"_lookUp" );
		htmlString.append( "\"" );
		htmlString.append( " value =\"" );
		htmlString.append( selectColumnValue  );
		htmlString.append( "\"" );
		htmlString.append( " onChange =\"" +comboresetFunction+"return check("+selectCheckParameters+")\"" );
		if (selectColumnMetaData.getFormat() !=null )
		{
			int maxLen = selectColumnMetaData.getFormatLength ();

		    htmlString.append( " size =\"" );
		    htmlString.append( String.valueOf ( maxLen > 99 ? 100 : maxLen + 1 ) );
		    htmlString.append( "\"" );
		    htmlString.append( " maxlength =\"" );
		    htmlString.append( String.valueOf ( maxLen ) );
		    htmlString.append( "\" " );
		}

			if ((selectColumnMetaData.isSecured()) && (pageContext.getRequest().getAttribute( columnMetaData.getName()+"_unlock" ) == null) )
			{
		    htmlString.append( " style='border:none;background-color:#ffffff;' readonly =\"true\"" );
			}

		if ( selectColumnMetaData.getType() == ColumnDataType._CDT_DATE ||
		     selectColumnMetaData.getType() == ColumnDataType._CDT_DATETIME )
		{

		    htmlString.append ( " onBlur=\"isValidDate( this, '" + columnMetaData.getFormat() + "' )\"" );
		    htmlString.append ( ">" );
			//CALENDAR
			htmlString.append ("<a href=\"javascript:launchCalendar('");
			htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "','"+ columnMetaData.getFormat() + "')\">" );
		    htmlString.append ( "<img src=\"../images/calendar.gif\" border=0 height=\"16\" width=\"25\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" >" );
		    htmlString.append ( "</a>" );
		}
		else
		{
		    htmlString.append ( ">" );
		}

		htmlString.append( "</nobr>" );
	} else
	{

		com.addval.ejbutils.dbutils.EJBResultSet comboResultSet  = (com.addval.ejbutils.dbutils.EJBResultSet) columnMetaData.getEjbResultSet();
		comboResultSet.beforeFirst();
		com.addval.metadata.EditorMetaData optMetaData = ((com.addval.ejbutils.dbutils.EJBResultSetMetaData) comboResultSet.getMetaData()).getEditorMetaData();
    		com.addval.ejbutils.dbutils.EJBStatement    stmt         = new com.addval.ejbutils.dbutils.EJBStatement ( comboResultSet );
    		com.addval.dbutils.RSIterator   rsItem  = new com.addval.dbutils.RSIterator(stmt, "1", new com.addval.dbutils.RSIterAction(com.addval.dbutils.RSIterAction._FIRST), comboResultSet.getRecords().size(),  comboResultSet.getRecords().size()==0?10:comboResultSet.getRecords().size());
    		java.util.Vector  displayableColumns = optMetaData.getDisplayableColumns();
    		java.util.Vector  allColumns = optMetaData.getColumnsMetaData();


		// Put a textbox for the column itself
		htmlString.append( "&nbsp;" );
		htmlString.append( "<SELECT " );
		htmlString.append( "name='" + columnName + "_lookUp" + "' >" );



		// always add a blank option
		htmlString.append("<OPTION VALUE=''> </OPTION>");



		while (rsItem.next())
		{

			String optValue = "";
			String optDesc  = "";
			String selected = "";

			for (int j=0; j<displayableColumns.size(); j++)
			{
		     		ColumnMetaData optcolMetaData   = ( ColumnMetaData ) displayableColumns.elementAt(j);
		     		String	       optcolName 	=  optcolMetaData.getName();
		     		String         optcolValue 	= rsItem.getString(optcolName);

		     		if (optcolValue == null)
		     		  optcolValue = "";

     				if (optcolName.equalsIgnoreCase(columnName))
					optValue = optcolValue;


				if (optcolName.equalsIgnoreCase(selectColumnName))
					optDesc = optcolValue;
			}

			if (optValue.equals(value))
				selected = "selected";
			else
				selected = "";

			htmlString.append( "<OPTION VALUE='" + optValue + "' " + selected + ">" );
			htmlString.append( optDesc );
			htmlString.append("</OPTION>");
	    	}

		htmlString.append( "</SELECT>" );
	}

	// if combo column and if (it is not a secured column or a secured column with an request attribute set to unlock by the security manager
	//
	//        )
	// then

        htmlString.append( "</td>" );

        return htmlString.toString ();
	}

	/**
	 * @param columnMetaData
	 * @param operator
	 * @param value
	 * @return String
	 * @roseuid 3B42E70D0074
	 */
	private String getComboTextBox(ColumnMetaData columnMetaData, String operator, String value)
	{
        StringBuffer htmlString = new StringBuffer();
        String columnName = columnMetaData.getName();
        String checkParameters = "\'" + columnName+"_lookUp" + "\',\'" +
                                 ( columnMetaData.getFormat()==null ? "" : columnMetaData.getFormat() ) + "\',\'" +
                                 Double.toString(columnMetaData.getMinval()) + "\',\'" +
                                 Double.toString(columnMetaData.getMaxval()) + "\',\'" +
                                 ( columnMetaData.getRegexp()==null ? "" : columnMetaData.getRegexp() ) + "\',\'" +
                                 ( columnMetaData.getErrorMsg()==null ? "" : columnMetaData.getErrorMsg() ) + "\'";

		htmlString.append( "<td class=\"label\"><nobr>" );
		htmlString.append( columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption() );

		// has been added for searchableMandatory fields
		if	(columnMetaData.isSearchableMandatory()) {
			htmlString.append(	" <font class=\"mandatory\">" + _searchableMandatorySymbol + "</font>:"	);
		}

		htmlString.append( "&nbsp;" );
        htmlString.append( "<select " );
        htmlString.append( " name =\"" );
        htmlString.append( columnName+"operator_lookUp" );
        htmlString.append( "\"" );
        htmlString.append( " >" );

        if (operator.equals( String.valueOf( AVConstants._EQUAL ) ))
            htmlString.append( " <option value=\""+AVConstants._EQUAL+"\" selected>"+
            AVConstants.getHtmlCompOperatorMap().get(String.valueOf( AVConstants._EQUAL_SIGN ) ) );
        else
            htmlString.append( " <option value=\""+AVConstants._EQUAL+"\" >"+
            AVConstants.getHtmlCompOperatorMap().get(String.valueOf ( AVConstants._EQUAL_SIGN ) ) );

        if (operator.equals( String.valueOf( AVConstants._GREATER ) ))
            htmlString.append( " <option value=\""+AVConstants._GREATER+"\" selected>"+
            AVConstants.getHtmlCompOperatorMap().get(String.valueOf( AVConstants._GREATER_SIGN ) ) );
        else
            htmlString.append( " <option value=\""+AVConstants._GREATER+"\">"+
            AVConstants.getHtmlCompOperatorMap().get(String.valueOf ( AVConstants._GREATER_SIGN ) ) );

        if (operator.equals( String.valueOf( AVConstants._GREATER_EQUAL ) ))
            htmlString.append( " <option value=\""+AVConstants._GREATER_EQUAL+"\" selected>"+
            AVConstants.getHtmlCompOperatorMap().get(AVConstants._GREATER_EQUAL_SIGN ) );
        else
            htmlString.append( " <option value=\""+AVConstants._GREATER_EQUAL+"\" >"+
            AVConstants.getHtmlCompOperatorMap().get(AVConstants._GREATER_EQUAL_SIGN ) );

        if (operator.equals( String.valueOf( AVConstants._LESSER ) ) )
            htmlString.append( " <option value=\""+AVConstants._LESSER+"\" selected>"+
            AVConstants.getHtmlCompOperatorMap().get(String.valueOf( AVConstants._LESSER_SIGN ) ) );
        else
            htmlString.append( " <option value=\""+AVConstants._LESSER+"\">"+
            AVConstants.getHtmlCompOperatorMap().get(String.valueOf( AVConstants._LESSER_SIGN ) ) );

        if (operator.equals( String.valueOf( AVConstants._LESSER_EQUAL ) ) )
            htmlString.append( " <option value=\""+AVConstants._LESSER_EQUAL+"\" selected>"+
            AVConstants.getHtmlCompOperatorMap().get(AVConstants._LESSER_EQUAL_SIGN ) );
        else
            htmlString.append( " <option value=\""+AVConstants._LESSER_EQUAL+"\" >"+
            AVConstants.getHtmlCompOperatorMap().get(AVConstants._LESSER_EQUAL_SIGN ) );

        htmlString.append ( "</select> " );

		htmlString.append( "</nobr></td>" );
		htmlString.append( "<td class=\"content\"><nobr>" );

        _index++;

		htmlString.append ( "&nbsp;" );
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
        	int maxLen = columnMetaData.getFormatLength();
            htmlString.append( " size =\"" );
            htmlString.append( String.valueOf ( maxLen > 99 ? 100 : maxLen + 1 ) );
            htmlString.append( "\"" );
            htmlString.append( " maxlength =\"" );
            htmlString.append( String.valueOf ( maxLen ) );
            htmlString.append( "\""                     );
        }

        if ( columnMetaData.getType() == ColumnDataType._CDT_DATE ||
             columnMetaData.getType() == ColumnDataType._CDT_DATETIME )
        {
            htmlString.append ( " onBlur=\"isValidDate( this, '" + columnMetaData.getFormat() + "' )\"" );
            htmlString.append ( ">" );
			//CALENDAR
			htmlString.append ("<a href=\"javascript:launchCalendar('");
			htmlString.append ( columnName+"_lookUp" );
            htmlString.append ( "','"+ columnMetaData.getFormat() + "')\">" );
            htmlString.append ( "<img src=\"../images/calendar.gif\" border=0 height=\"16\" width=\"25\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" >");
            htmlString.append ( "</a>" );
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
	 * @roseuid 3B42E71E0141
	 */
	private String getCheckBox(ColumnMetaData columnMetaData, boolean isChecked)
	{

        _index++;
        StringBuffer htmlString = new StringBuffer ();
		htmlString.append( columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption() );

		// has been added for searchableMandatory fields
		if	(columnMetaData.isSearchableMandatory()) {
			htmlString.append(	" <font class=\"mandatory\">" + _searchableMandatorySymbol + "</font>:"	);
		}


        htmlString.append( "<input " );
        htmlString.append( "type =\"checkbox\" " );
        htmlString.append( " name =\"" );
        htmlString.append( columnMetaData.getName()+"_group" );
        htmlString.append( "\"" );
        htmlString.append( " value =\"" );
        htmlString.append( columnMetaData.getComboSelect() == null ? columnMetaData.getName() : columnMetaData.getName() + "||" + columnMetaData.getComboSelect() );
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
	 * @roseuid 3B42E73C002C
	 */
	private String getLabelSort(ColumnMetaData columnMetaData)
	{

        StringBuffer htmlString = new StringBuffer ( "" );
		String  name 		= columnMetaData.getName();

		final String EDIT 	= "_EDIT";
		final String MODIFY = "_MODIFY";
		final String ADD 	= "_ADD";
		final String CLONE 	= "_CLONE";
		final String COPY 	= "_COPY";
		final String DELETE = "_DELETE";
        final String LOG    = "_LOG";

		boolean addSorting  = false;

		if ((!name.endsWith( EDIT )  && !name.endsWith( MODIFY ) && !name.endsWith( ADD ) &&
			 !name.endsWith( CLONE ) && !name.endsWith( COPY )   && !name.endsWith( DELETE ) && !name.endsWith( LOG ))
		   ) {
			addSorting = true;
		}

		if (((addSorting == false) && (_hasEditPermission)) || (addSorting == true))
		{
			_index++;
			htmlString.append( "<th>" );



			if (addSorting || _hasEditPermission) {

				htmlString.append( "<a class=\"header\"" );
				htmlString.append( "href=\"javascript:setSortingAction('" );
				htmlString.append( columnMetaData.getName() );
				htmlString.append( "')\">" );
			}

/*
			if (columnMetaData.getCaption() !=null)
				htmlString.append( columnMetaData.getCaption() );
			else
				htmlString.append( columnMetaData.getName() );
*/
			htmlString.append( columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption() );


			if (addSorting || _hasEditPermission) {

				htmlString.append( "</a>" );
			}

			htmlString.append( "</th>" );
		}

        return htmlString.toString();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE4F050334
	 */
	public String getViewrole()
	{
        return _viewrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3EFE4F0503AC
	 */
	public void setViewrole(String aViewrole)
	{
        _viewrole = aViewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE4F0600F0
	 */
	public String getEditrole()
	{
        return _editrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3EFE4F06017C
	 */
	public void setEditrole(String aEditrole)
	{
        _editrole = aEditrole;
	}

	/**
	 * Access method for the _details property.
	 * @return   the current value of the _details property
	 * @roseuid 3EFE4F6600BC
	 */
	public String getDetails()
	{
        return _details;
	}

	/**
	 * Sets the value of the _details property.
	 * @param aDetails the new value of the _details property
	 * @roseuid 3EFE4F66015C
	 */
	public void setDetails(String aDetails)
	{
        _details = aDetails;
	}
}
