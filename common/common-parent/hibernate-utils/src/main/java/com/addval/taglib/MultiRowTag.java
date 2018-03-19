//Source file: d:\\projects\\Common\\src\\common1.4\\source\\com\\addval\\taglib\\MultiRowTag.java

package com.addval.taglib;

import com.addval.environment.Environment;
import com.addval.utils.StrUtl;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.StringTokenizer;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.util.LabelValueBean;

/**
 * Displays MultiRow Editor
 * @author AddVal Technology Inc.
 */
public class MultiRowTag extends GenericBodyTag
{
	private String _name = null;
	private String _uniqueId = null;
	private String _formName = null;
	private String _fieldNames = null;
	private String _deleteOption = null;
	private String _addOption = null;
	private String _headingOption = null;
	private String _scope = null;
	private String _fieldNamePrefix = null;
	private String _multiRowEditorName = null;
	private String _imagesPath = null;
	private String _subsystem = null;
	private boolean _deleteIconNeeded = true;
	private boolean _addIconNeeded = true;
	private boolean _headingNeeded = true;
	private Properties _prop = null;
	private Properties _errorMessage = null;
	private static final String _FIELD_AREA = "FIELD_AREA_0";
	private static final String _ROW = "ROW_0";
	private static final String _ROW_SIZE = "ROW_SIZE_0";
	private static final String _ADD = "ADD_0";
	private static final String _DELETE = "DELETE_0";
	private static final String _FALSE = "false";
	private static final String _TRUE = "true";
	private static final String _SUBSYSTEM = "subsystem";
	private static final String _MOVEUP = "_MOVE_UP_0";
	private static final String _MOVEDOWN = "_MOVE_DOWN_0";
	private Object _formBean = null;
	private int _noOfHeadings;
	private List _columnList = null;
	private List _totalColumnList = null;
	private List _dataHolderList = null;
	private String _lookupImage = null;
	private String _rowMoveOption = null;
	private boolean _rowMoveNeeded = false;
	private String _addButtonOutsideFieldset = null;
	private boolean _isAddButtonOutsideFieldset = false;
	private static final String _DEFAULT_DATE_FORMAT = "dd/MM/yy";
    private String readOnly = null;
    private boolean allReadOnly = false;

	/**
	 * @roseuid 40415A1D00EA
	 */
	public MultiRowTag()
	{
		loadDefaultProperties();
	}

    public String getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

	public Properties getErrorMessage() {
		return _errorMessage;
	}

	public void setErrorMessage(Properties message) {
		_errorMessage = message;
	}

	public int getNoOfHeadings() {
		return _noOfHeadings;
	}

	public void setNoOfHeadings(int ofHeadings) {
		_noOfHeadings = ofHeadings;
	}

	/**
	 * Access method for the _subsystem property.
	 *
	 * @return   the current value of the _subsystem property
	 */
	public String getSubsystem()
	{
		return _subsystem;
	}

	/**
	 * Sets the value of the _subsystem property.
	 *
	 * @param aSubsystem the new value of the _subsystem property
	 */
	public void setSubsystem(String aSubsystem)
	{
		_subsystem = aSubsystem;
	}

	/**
	 * Sets the value of Name Property
	 *
	 * @param aName
	 * @roseuid 40415A1D0128
	 */
	public void setName(String aName)
	{
		_name = aName;
	}

	/**
	 * Sets the value of FormName Property
	 *
	 * @param aFormName
	 * @roseuid 40415A1D01C5
	 */
	public void setFormName(String aFormName)
	{
		_formName = aFormName;
	}

	/**
	 * Sets the value of Scope Property
	 *
	 * @param aScope
	 * @roseuid 40415A1D0261
	 */
	public void setScope(String aScope)
	{
		_scope = aScope;
	}

	/**
	 * Sets the value of FieldNamePrefix Property
	 *
	 * @param aFieldNamePrefix
	 * @roseuid 40415A1D02FD
	 */
	public void setFieldNamePrefix(String aFieldNamePrefix)
	{
		_fieldNamePrefix = aFieldNamePrefix;
	}

	/**
	 * @param aFieldNames
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40601627037A
	 */
	public void setFieldNames(String aFieldNames) throws JspException
	{
		_fieldNames = aFieldNames;
	}

	/**
	 * Sets the value of DeleteOption Property
	 *
	 * @param aDeleteOption
	 * @roseuid 40415A1E00BB
	 */
	public void setDeleteOption(String aDeleteOption)
	{
		_deleteOption = aDeleteOption;
	}

	/**
	 * Sets the value of AddOption Property
	 *
	 * @param aAddOption
	 * @roseuid 40415A1E0157
	 */
	public void setAddOption(String aAddOption)
	{
		_addOption = aAddOption;
	}

	/**
	 * Sets the value of HeadingOption Property
	 *
	 * @param aHeadingOption
	 * @roseuid 40415A1E0203
	 */
	public void setHeadingOption(String aHeadingOption)
	{
		_headingOption = aHeadingOption;
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 40415A1E029F
	 */
	public Object getFormBean()
	{
		return _formBean;
	}

	/**
	 * @return java.util.List
	 * @roseuid 40415A1E02EE
	 */
	public List getColumnNames()
	{
		return _columnList;
	}

	/**
	 * @return java.util.List
	 * @roseuid 40415A1E033C
	 */
	public List getDataHolderList()
	{
		 return _dataHolderList;
	}

	/**
	 * @return java.util.Properties
	 * @roseuid 40415A1E0399
	 */
	public Properties getProperties()
	{
		return _prop;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 40415A1F0000
	 */
	public String getUniqueId()
	{
		return _uniqueId;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 40415A1F004E
	 */
	public String getFieldNamePrefix()
	{
		return _fieldNamePrefix;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 40415A1F008C
	 */
	public String getImagesPath()
	{
		return _imagesPath;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 40415A1F00DA
	 */
	public String getMultiRowEditorName()
	{
		return _multiRowEditorName;
	}

	/**
	 * @param aRowMoveOption
	 * @roseuid 413C11630138
	 */
	public void setRowMoveOption(String aRowMoveOption)
	{
		_rowMoveOption = aRowMoveOption;
	}

	/**
	 * @param aAddButtonOutsideFieldset
	 * @roseuid 413C116603C8
	 */
	public void setAddButtonOutsideFieldset(String aAddButtonOutsideFieldset)
	{
		_addButtonOutsideFieldset = aAddButtonOutsideFieldset;
	}

	/**
	 * @return int
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 40415A1F0128
	 */
	public int doStartTag() throws JspTagException
	{
		try {
			getObjects();
			init();
			StringBuffer buffer = new StringBuffer();
			buffer.append( createMultiRow() );
			buffer.append( loadRows() );
			buffer.append( createFieldSet() );
			pageContext.getOut().write( buffer.toString() );
			return SKIP_BODY;
		}
		catch( Exception e ) {
			e.printStackTrace();
			try {
				pageContext.getOut().write ( e.getMessage() );
				return SKIP_BODY;
			}
			 catch( IOException io ) {
				 throw new JspTagException( "ODMultiRow tag Error:" + io.getMessage() );
			}
		}
	}

	/**
	 * @return int
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 40415A1F01C5
	 */
	public int doEndTag() throws JspTagException
	{
		return EVAL_PAGE;
	}

	/**
	 * This method prepare Javascript to create MultiRowEditor object
	 *
	 * @return java.lang.String
	 * @roseuid 40415A1F0251
	 */
	protected String createMultiRow()
	{
		StringBuffer createHtml = new StringBuffer();
		createHtml.append( "<Script language = 'JavaScript'>\n" );
		createHtml.append( "function create_" ).append( _name ).append( "() { \n" );
		createHtml.append( "var " ).append( _multiRowEditorName ).append( " = new MultiRowEditor ('" );
		createHtml.append( _uniqueId ).append( _FIELD_AREA ).append( "','" );
		createHtml.append( _uniqueId ).append( _ROW ).append( "'," );
		createHtml.append( " new Array ( " );

		createHtml.append( getFieldColumnIds() );

		createHtml.append( " ), " );
		createHtml.append( " new Array ( " );

		createHtml.append( getFieldColumnNames() );

		createHtml.append( " ) )\n" );

		if (_deleteIconNeeded && !allReadOnly)
			createHtml.append( _multiRowEditorName ).append( ".deleteName = '" ).append( _uniqueId ).append( _DELETE ).append( "'\n" );

		createHtml.append( _multiRowEditorName ).append( ".sizeFieldId = '" ).append( _uniqueId ).append( _ROW_SIZE ).append( "'\n" );
		createHtml.append( _multiRowEditorName ).append( ".fieldNamePrefix = '" ).append( _fieldNamePrefix ).append( "'\n" );

		if (_addIconNeeded && !allReadOnly)
			createHtml.append( _multiRowEditorName ).append( ".registerAddControl (document.all." ).append( _uniqueId ).append( _ADD ).append(")").append( "\n" );

		if (_rowMoveNeeded && !allReadOnly){
			createHtml.append( _multiRowEditorName ).append( ".moveUpName = '" ).append( _uniqueId ).append( _MOVEUP ).append( "'\n" );
			createHtml.append( _multiRowEditorName ).append( ".moveDownName = '" ).append( _uniqueId ).append( _MOVEDOWN ).append( "'\n" );
		}

		return createHtml.toString();
	}

	/**
	 * this will load intial data to MultiRowEditor
	 *
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A1F02BF
	 */
	protected String loadRows() throws JspException
	{
		StringBuffer loadRows = new StringBuffer();
		String temp = "{";

		for( int i = 0; i < _dataHolderList.size(); i++ )
		{
			Object bean = _dataHolderList.get(i);
			loadRows.append( _multiRowEditorName ).append( ".loadElement ( new Array (" );

			loadRows.append( getFieldColumnValues( bean ) );
			boolean isNonEditable = false;
			boolean isNonDeletable = false;
			try {
				isNonEditable = ((Boolean)PropertyUtils.getSimpleProperty( bean, "nonEditable" )).booleanValue();
			}
			catch (Exception e) {
			}
			try {
				isNonDeletable = ((Boolean)PropertyUtils.getSimpleProperty( bean, "nonDeletable" )).booleanValue();
			}
			catch (Exception e) {
			}
			loadRows.append( " ), " ).append( isNonEditable ).append(", ").append( isNonDeletable ).append( ");" ).append( "\n" );
		}
		loadRows.append( "} </script>" ).append( "\n" );
		return loadRows.toString();
	}

	/**
	 * This method prepare HTML template for Multi Row  Editor
	 *
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A1F036B
	 */
	protected String createFieldSet() throws JspException
	{
		StringBuffer fieldHtml = new StringBuffer();

		_imagesPath = _prop.getProperty( _name + "." + "imagePath", "../images" );
		_lookupImage = _prop.getProperty( _name + "." + "lookupImage", "lookup.gif" );
		String fieldsetCaption =  _prop.getProperty( _name + "." + "fieldset.caption", "" );
		boolean fieldsetNeeded = false;
		if (fieldsetCaption.length() != 0)
			fieldsetNeeded = true;

		fieldHtml.append( "<input type='hidden' name='" ).append( _fieldNamePrefix ).append( ".size' id='" ).append( _uniqueId ).append( _ROW_SIZE ).append( "' value='0'/>" );

		StringBuffer tempBuffer = new StringBuffer();

		tempBuffer.append( "<fieldset title='" ).append( fieldsetCaption )
					.append( "' class='fieldset1'>" ).append( "\n" )
					.append( "<legend class='centerlabel'>" ).append( fieldsetCaption )
					.append( "</legend>" ).append( "\n" );


		if (fieldsetNeeded && !_isAddButtonOutsideFieldset)
			fieldHtml.append( tempBuffer );

		fieldHtml.append( "<table cellpadding='0'><tr><td>\n");

		if (fieldsetNeeded && _isAddButtonOutsideFieldset)
			fieldHtml.append( tempBuffer );

		fieldHtml.append( "<table cellpadding='0' ");
		if (!fieldsetNeeded)
			fieldHtml.append( " class=fieldset1 ");
		fieldHtml.append( ">\n" );

		// Table Heading displayed will be decided by _headingNeeded boolean value
		if (_headingNeeded) {
			fieldHtml.append( "<thead>" ).append( "\n" );
			fieldHtml.append( "<tr>" ).append( "\n" );

			if (_rowMoveNeeded && !allReadOnly)
			 	fieldHtml.append( "<th></th>" ).append( "\n");

			fieldHtml.append( getTableHeadings() );

			if (_deleteIconNeeded && !allReadOnly)
				fieldHtml.append( "<th></th>" ).append( "\n" );

			fieldHtml.append( "</tr>" ).append( "\n" );
			fieldHtml.append( "</thead>" ).append( "\n" );
		}

		fieldHtml.append( "<tbody id='" ).append( _uniqueId ).append( _FIELD_AREA ).append( "' cellspacing='0' cellpadding='0'>" ).append( "\n" );
		fieldHtml.append( "<tr id='" ).append( _uniqueId ).append( _ROW ).append("'>").append( "\n" );

		fieldHtml.append( addColumnElement() );
		int addButtonColspan = _noOfHeadings;
		if (_deleteIconNeeded)
			addButtonColspan += 1;

		// Delete icon display  will be decided by _deleteIconNeeded boolean value
		if (_deleteIconNeeded && !allReadOnly ) {
			fieldHtml.append( "<td valign='bottom' style='cursor:hand'>" );
			fieldHtml.append( "<img name='" ).append( _uniqueId ).append( _DELETE )
				.append( "' src='" ).append( _imagesPath ).append( "/delete.gif' border=0 alt='Delete'>" ).append( "\n" );
			fieldHtml.append( "</td>" ).append( "\n" );
		}

		fieldHtml.append( "</tr>" ).append( "\n" );
		fieldHtml.append( "</tbody>" ).append( "\n" );
		fieldHtml.append( "</table>" ).append( "\n" );

		if (fieldsetNeeded && _isAddButtonOutsideFieldset)
			fieldHtml.append( "</fieldset>" ).append( "\n" );

		fieldHtml.append( "</td></tr>" ).append( "\n" );


		// Row Adding icon display  will be decided by _addIconNeeded boolean value
		if (_addIconNeeded && !allReadOnly) {
			fieldHtml.append( "<tr>").append( "\n" );
			fieldHtml.append( "<td colspan='" ).append( addButtonColspan ).append("' style='text-align:right'>" ).append( "\n" );
			fieldHtml.append( "<button class='button' id='" ).append(_uniqueId).append( _ADD ).append( "'>" ).append( "\n" );
			fieldHtml.append( "<img id='" ).append( _uniqueId ).append( "ADD_IMG' src='" ).append( _imagesPath ).append( "/add_new.gif' border=0 alt='Add New'>" ).append( "\n" );
			fieldHtml.append( "</button>" ).append( "\n" );
			fieldHtml.append( "</td>" ).append( "\n" );
			fieldHtml.append( "</tr>" ).append( "\n" );
		}

		fieldHtml.append( "</table>" ).append( "\n" );

		if (fieldsetNeeded && !_isAddButtonOutsideFieldset)
			fieldHtml.append( "</fieldset>" ).append( "\n" );

		return fieldHtml.toString();
	}

	/**
	 * This method add select box to the HMTL template. The select option collection
	 * will be taken from
	 * form bean eg. if the field name is 'origin', the  method 'getOrigins()' sholud
	 * be in form bean
	 *
	 * @param columnName
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A20003E
	 */
	protected String addSelect(String columnName) throws JspException
	{
		StringBuffer buffer = new StringBuffer();
		List comboOptions = null;
		String selectOptionsProperty = _prop.getProperty( _name + "." +columnName + ".field.collectionList", columnName+"s" ).trim();
		String selectedValue = "";
		String textOnChange = _prop.getProperty(_name + "." + columnName + ".field.onChange", "" ).trim();

		try {
			comboOptions = (List) PropertyUtils.getSimpleProperty( _formBean, selectOptionsProperty ) ;
		}
		catch( IllegalAccessException e ) {
			Object[] objs = {selectOptionsProperty};
			throw new JspException( formatErrorMessage( "error.msg.illegal_access_exception_in_formBean", objs ) );
		}
		catch( InvocationTargetException e ) {
			Object[] objs = {selectOptionsProperty};
			throw new JspException( formatErrorMessage( "error.msg.invocation_target_exception_in_formBean" , objs ) );
		}
		catch( NoSuchMethodException e ) {
			Object[] objs = {selectOptionsProperty};
			throw new JspException( formatErrorMessage( "error.msg.nosuchmethod_exception_in_formBean", objs ) );
		}

		String lookup = _prop.getProperty( _name + "." + columnName +".lookup", "" );
		String lookupOnClick = _prop.getProperty( _name + "." + columnName + ".lookup.onclick","" ).trim();
		boolean showlookupalways = _prop.getProperty( _name + "." + columnName + ".showlookupalways","false" ).trim().equalsIgnoreCase( "true" );

		if ( !StrUtl.isEmptyTrimmed(lookup)) {
			if(showlookupalways || !allReadOnly){
				buffer.append( "<a ");
				if (lookupOnClick.length() > 0){
					buffer.append( "onclick = \""  ).append(lookupOnClick).append("\" ") ;
				}
				buffer.append( " href=\"javascript:").append( lookup ).append( "\"> <img src='" ).append( _imagesPath ).append( "/" ).append( _lookupImage ).append( "' border=0 alt='Lookup'></a>");
			}
		}

		buffer.append( "<select name='" ).append( _fieldNamePrefix ).append( "[0]." ).append( columnName )
				.append( "' id='" ).append(_uniqueId).append( columnName.toUpperCase() ).append( "'" );

		if(new Boolean( _prop.getProperty( _name + "." + columnName + ".field.readOnly","FALSE" ).trim() ).booleanValue() || allReadOnly)
			buffer.append( " disabled " );

		if (textOnChange.length() != 0)
			buffer.append( " onChange=\"" ).append( textOnChange ).append( "\"" );
		buffer.append( ">\n" );

		try {
			selectedValue = _prop.getProperty(_name + "." + columnName + ".field.select", "").trim();
		}
		catch(Exception e) { }

		for (int i = 0; i < comboOptions.size(); i++) {
			LabelValueBean labelValue = (LabelValueBean)comboOptions.get(i);
			buffer.append("<option value =\"").append(labelValue.getValue()).append("\"");
			if (labelValue.getValue().equalsIgnoreCase(selectedValue))
				buffer.append(" selected ");
			buffer.append(">").append(labelValue.getLabel()).append("</options>");
		}
		buffer.append( "</select>" );
		return buffer.toString();
	}

	/**
	 * This method will add Checkbox box to the HMTL template.
	 *
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 40415A200167
	 */
	protected String addCheckbox(String columnName)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( "<input type='checkbox' name='" ).append( _fieldNamePrefix ).append( "[0]." ).append( columnName )
				.append( "' id='" ).append(_uniqueId).append( columnName.toUpperCase() ).append("' ");

		if (new Boolean( _prop.getProperty( _name + "." + columnName + ".field.readOnly","FALSE" ).trim() ).booleanValue() || allReadOnly)
			buffer.append( " disabled " );

		if (new Boolean( _prop.getProperty( _name + "." + columnName + ".field.checked","FALSE" ).trim() ).booleanValue())
			buffer.append( " checked=true " );

        String onClickEvent = _prop.getProperty( _name + "." + columnName + ".field.onclick","" ).trim();
        if (onClickEvent.length() > 0) {
            buffer.append( "onclick = \""  ).append( onClickEvent ).append("\" ") ;
        }
		buffer.append( ">\n" );
		return buffer.toString();
	}

	/**
	 * This method will add TextField box to the HMTL template.If lookup is neededfor
	 * that field,we need to add one lookupId entry.
	 *
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 40415A200232
	 */
	protected String addTextField(String columnName)
	{
		StringBuffer buffer = new StringBuffer();
		String lookup = _prop.getProperty( _name + "." + columnName +".lookup", "" );

		String maxLength = _prop.getProperty( _name + "." + columnName + ".field.maxlength","20" ).trim();
		String size = _prop.getProperty( _name + "." + columnName + ".field.size","20" ).trim();
		String value = _prop.getProperty( _name + "." + columnName + ".field.value","" ).trim();
		String textOnBlur = _prop.getProperty( _name + "." + columnName + ".field.onblur","" ).trim();
		String lookupOnClick = _prop.getProperty( _name + "." + columnName + ".lookup.onclick","" ).trim();
		String style = _prop.getProperty( _name + "." + columnName + ".field.style","" ).trim();
		String styleClass = _prop.getProperty( _name + "." + columnName + ".field.styleClass","" ).trim();
		boolean isReadOnly = _prop.getProperty( _name + "." + columnName + ".field.readOnly","FALSE" ).trim().equalsIgnoreCase( "true" ) || allReadOnly;
		String eventControl = _prop.getProperty( _name + "." + columnName + ".field.eventControl","" ).trim();
		boolean showlookupalways = _prop.getProperty( _name + "." + columnName + ".showlookupalways","false" ).trim().equalsIgnoreCase( "true" );
		
		if ( !StrUtl.isEmptyTrimmed(lookup)) {
			if(showlookupalways || !allReadOnly){
				buffer.append( "<a ");
				if (lookupOnClick.length() > 0){
					buffer.append( "onclick = \""  ).append(lookupOnClick).append("\" ") ;
				}
				buffer.append( " href=\"javascript:").append( lookup ).append( "\"> <img src='" ).append( _imagesPath ).append( "/" ).append( _lookupImage ).append( "' border=0 alt='Lookup'></a>");
			}
		}

		buffer.append( "<input type='text' name='" ).append( _fieldNamePrefix ).append( "[0]." ).append( columnName )
				.append( "' id='" ).append(_uniqueId).append( columnName.toUpperCase() ).append( "' value='" ).append( value )
				.append( "' size=").append( size ).append( " maxlength=" ).append( maxLength );

		if (styleClass.length() != 0)
			buffer.append(" class=" ).append( styleClass );


		if (isReadOnly) {
			buffer.append( " readonly ");
			style = style.length() != 0 ? ";background-color:#CCCCCC;" : "background-color:#CCCCCC;";
		}

		if (style.length() != 0)
			buffer.append(" style='" ).append( style ).append("' ");

		if (textOnBlur.length() != 0)
			buffer.append( " onblur=\"" ).append( textOnBlur ).append( "\"" );

		if (eventControl.length() != 0)
			buffer.append( " " ).append( eventControl ).append( " " );

		buffer.append( ">\n" );
		return buffer.toString();
	}

	/**
	 * This method need to override if non-standard column in Multirow
	 * return collection Non standard Addtional column to display
	 *
	 * @return java.util.List
	 * @roseuid 40415A2002FD
	 */
	protected List additionalColumnDetails()
	{
		return new ArrayList();
	}

	/**
	 * This method need to override if non-standard column in Multirow
	 * returns string which contains html template of non standard field
	 *
	 * @return java.lang.String
	 * @roseuid 40415A20037A
	 */
	protected String additionalColumnHtmlTemplate()
	{
		return new String();
	}

	/**
	 * this method Initialize all the general property
	 *
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A2003D8
	 */
	private void init() throws JspException
	{
		_columnList = new ArrayList();
		_uniqueId = _name.toUpperCase();
		_multiRowEditorName = "listOf"+_uniqueId;
		_prop = Environment.getInstance( _subsystem ).getCnfgFileMgr().getProperties();

		if (_deleteOption != null && _deleteOption.equalsIgnoreCase( _FALSE ))
			_deleteIconNeeded = false;
		if (_addOption != null && _addOption.equalsIgnoreCase( _FALSE ))
			_addIconNeeded = false;
		if (_headingOption !=null && _headingOption.equalsIgnoreCase( _FALSE ))
			_headingNeeded = false;
		if (_rowMoveOption != null && _rowMoveOption.equalsIgnoreCase( _TRUE )  )
			_rowMoveNeeded = true;
		if (_addButtonOutsideFieldset != null && _addButtonOutsideFieldset.equalsIgnoreCase( _TRUE ))
			_isAddButtonOutsideFieldset = true;
        if( readOnly != null && readOnly.equalsIgnoreCase( _TRUE )) {
            allReadOnly = true;
        }
        else {
            allReadOnly = false;
        }

		StringTokenizer tokens = new StringTokenizer( _fieldNames, "," );
		String str = null;
		while (tokens.hasMoreTokens()) {
			str = ((String)tokens.nextElement()).trim();
			if (_columnList.contains( str )) {
				Object[] objs = { str, _subsystem };
				throw new JspException( formatErrorMessage( "error.msg.duplicate_column", objs ) );
			}
			_columnList.add( str );
		}
		_totalColumnList = new ArrayList( _columnList );

		List addditionlColumnList = additionalColumnDetails();
		for (int i = 0; i < addditionlColumnList.size(); i++){
			str = (String)addditionlColumnList.get( i );
			if (_totalColumnList.contains( str )) {
				Object[] objs = { str, _subsystem };
				throw new JspException( formatErrorMessage( "error.msg.duplicate_column", objs ) );
			}
			_totalColumnList.add( str );
		}
		if (_totalColumnList.size() == 0)
			throw new JspException( formatErrorMessage( "error.msg.empty_columnname", null) );
	}

	/**
	 * This method will add a heading
	 *
	 * @param heading
	 * @param required
	 * @return java.lang.String
	 * @roseuid 40415A2100AB
	 */
	private String addHeading(String heading, String required)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( "<th><nobr>" ).append( heading ).append( required ).append( "</nobr></th>" ).append( "\n" );
		return buffer.toString();
	}

	/**
	 * This method will prepare HTML Template for Field Element
	 *
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A2101E4
	 */
	protected String addColumnElement() throws JspException
	{
		StringBuffer buffer = new StringBuffer();
		String columnName = null;
		boolean isNewCell = true;

		//code to include row move control
		if (_rowMoveNeeded && !allReadOnly){
			buffer.append( "<td><table><tr><td valign='top' style='cursor:hand' >\n" )
					.append( "<img name='").append( _uniqueId ).append( _MOVEUP )
					.append( "' src='" ).append( _imagesPath ).append( "/up_arrow.gif' border=0 alt='Move Up'>&nbsp;" ).append( "\n" )
					.append( "</td></tr>" );
			buffer.append( "<tr><td valign='top' style='cursor:hand' >\n" )
					.append( "<img name='").append( _uniqueId ).append( _MOVEDOWN )
					.append( "' src='" ).append( _imagesPath ).append( "/down_arrow.gif' border=0 alt='Move Down'>&nbsp;" ).append( "\n" )
					.append( "</td></tr></table></td>\n" );
		}

		for ( int i = 0; i < _columnList.size(); i++ )
		{
			columnName = (String)_columnList.get( i );
			String type = _prop.getProperty( _name + "." + columnName + ".field.type","TEXT" ).trim();

			if (isNewCell) buffer.append( "<td class='label'>" );
			isNewCell = true;
			if (type.equalsIgnoreCase( "SELECT" )) {
				buffer.append( addSelect( columnName ) );
			}
			else if( type.equalsIgnoreCase( "CHECKBOX")  ) {
				buffer.append( addCheckbox( columnName ) );
			}
			else if( type.equalsIgnoreCase( "TEXTAREA" ) ) {
				buffer.append( addTextArea( columnName ) );
			}
			else if( type.equalsIgnoreCase( "IMAGE" ) ) {
				buffer.append( addImage( columnName ) );
			}
			else if( type.equalsIgnoreCase( "DATE" ) ) {
				buffer.append( addDateField( columnName ) );
			}
			else if( type.equalsIgnoreCase( "HIDDEN" )  ){
				buffer.append( addHidden( columnName ) );
				isNewCell = false;
			}
			else {
				buffer.append( addTextField( columnName ) );
			}
			if (isNewCell || i == _columnList.size()-1)
				buffer.append( "</td>" );
		}
		buffer.append( additionalColumnHtmlTemplate() );
		return buffer.toString();
	}

	/**
	 * This method will prepare table heading for HTML template
	 *
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A2102AF
	 */
	protected String getTableHeadings() throws JspException
	{
		StringBuffer buffer = new StringBuffer();
//		String lastHeading = "";
		int columnSize = _totalColumnList.size();
		boolean hiddenField = false;
		for ( int i = 0; i < columnSize; i++ )
		{
			String columnName = (String)_totalColumnList.get( i );
			String heading = _prop.getProperty( _name + "." + columnName +".caption", "" ).trim();

			hiddenField = heading.equalsIgnoreCase("hidden");

			if ( heading.length() == 0 )
				throw new JspException( formatErrorMessage( "error.msg.heading_missing", new Object[]{ columnName, _subsystem } ) );
			String required =  "";
				if ( _prop.getProperty( _name + "." + columnName + ".column.required","false" ).equalsIgnoreCase("true") )
			required = "<font class=\"mandatory\">" +  _prop.getProperty( "mandatory.symbol", "*" ) + "</font>";
			if (!hiddenField ){
				buffer.append( addHeading( heading, required ) );
				_noOfHeadings++;
//				lastHeading = heading;
			}
		}
		return buffer.toString();
	}

	/**
	 * This method generate string seperated by comma for loadElement for
	 * MultiRowEditor,
	 * retriving the value from the Data holder
	 *
	 * @param holder
	 * @return java.lang.String
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A21036B
	 */
	protected String getFieldColumnValues(Object holder) throws JspException
	{
		StringBuffer buffer = new StringBuffer();

		for ( int i = 0; i < _totalColumnList.size(); i++ ) {
			if ( i != 0 )
				 buffer.append( "," );
			String columnName = (String)_totalColumnList.get( i );
			String value = null;

			try {
				Object obj = PropertyUtils.getSimpleProperty( holder, columnName );
				if( obj == null )
					value = "";
				else
					value = obj.toString();
			}
			catch( IllegalAccessException e ) {
				Object[] objs = {columnName};
				throw new JspException( formatErrorMessage( "error.msg.illegal_access_exception_in_DataHolder", objs ) );
			}
			catch( InvocationTargetException e ) {
				Object[] objs = {columnName};
				throw new JspException( formatErrorMessage( "error.msg.invocation_target_exception_in_DataHolder", objs ) );
			}
			catch( NoSuchMethodException e ) {
				Object[] objs = {columnName};
				throw new JspException( formatErrorMessage( "error.msg.nosuchmethod_exception_in_DataHolder", objs ) );
			}

			buffer.append( " \"" ).append( value ).append( "\"" );
		}
		return buffer.toString();
	}

	/**
	 * This method gives the field name seperated by comma to prepare arayOfFieldNames
	 * for MultiRowEditor
	 *
	 * @return java.lang.String
	 * @roseuid 40415A2200AB
	 */
	protected String getFieldColumnNames()
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < _totalColumnList.size(); i++) {
			if (i != 0)
				 buffer.append( "," );
			buffer.append( " '" ).append( _totalColumnList.get( i ) ).append( "'" );
		}
		return buffer.toString();
	}

	/**
	 * This method gives the field name Ids seperated by comma to prepare arrayOfIds
	 * for MultiRowEditor
	 *
	 * @return java.lang.String
	 * @roseuid 40415A220119
	 */
	protected String getFieldColumnIds()
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < _totalColumnList.size(); i++) {
			if (i != 0)
				buffer.append( "," );
			buffer.append( " '" ).append( _uniqueId ).append( ((String)_totalColumnList.get( i ) ).toUpperCase() ).append( "'" );
		}
		return buffer.toString();
	}

	/**
	 * @param pageContext
	 * @param name
	 * @param scope
	 * @return java.lang.Object
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A220177
	 */
	private Object lookup(PageContext pageContext, String name, String scope) throws JspException
	{
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
			String msg = MessageFormat.format( _errorMessage.getProperty( "error.msg.cant_find_bean" ), objs );
			throw new JspException( msg );
		}
		return bean;
	}

	/**
	 * This method initials the object formbean and DataHolder collection from page
	 *
	 * @throws javax.servlet.jsp.JspException
	 * @roseuid 40415A220399
	 */
	protected void getObjects() throws JspException
	{
		Object collection = lookup( pageContext, _name, "page");
		if (collection == null)
		{
			Object[] objs = {_formName, _scope};
			throw new JspException( formatErrorMessage( "error.msg.cant_find_bean", objs ) );
		}
		if (collection instanceof List)
			_dataHolderList = (List)collection;
		else {
			Object[] objs = {collection};
			throw new JspException( formatErrorMessage( "error.msg.invalid_bean", objs ) );
		}

		_formBean =  lookup( pageContext, _formName, _scope);
		if (_formBean == null){
			Object[] objs = {_formName, _scope};
			throw new JspException( formatErrorMessage( "error.msg.cant_find_bean", objs ) );
		}

		try {
			_subsystem = (String) PropertyUtils.getSimpleProperty( _formBean, _SUBSYSTEM ) ;
		}
		catch( IllegalAccessException e ) {
			Object[] objs = { _SUBSYSTEM };
			throw new JspException( formatErrorMessage( "error.msg.illegal_access_exception_in_formBean", objs ) );
		}
		catch( InvocationTargetException e ) {
			Object[] objs = { _SUBSYSTEM };
			throw new JspException( formatErrorMessage( "error.msg.invocation_target_exception_in_formBean" , objs ) );
		}
		catch( NoSuchMethodException e ) {
			Object[] objs = { _SUBSYSTEM };
			throw new JspException( formatErrorMessage( "error.msg.nosuchmethod_exception_in_formBean", objs ) );
		}
	}

	/**
	 * This method will load the Error message
	 * @roseuid 40415A23006D
	 */
	private void loadDefaultProperties()
	{
		_errorMessage = new Properties();
		_errorMessage.setProperty( "error.msg.cant_find_bean", "Could not find bean {0} in scope {1}" );
		_errorMessage.setProperty( "error.msg.invalid_bean", "The bean that you gave is not a Collection : {0}" );
		_errorMessage.setProperty( "error.msg.illegal_access_exception_in_DataHolder", "IllegalAccessException trying to fetch property {0} on DataHolder" );
		_errorMessage.setProperty( "error.msg.invocation_target_exception_in_DataHolder", "InvocationTargetException trying to fetch property {0} on DataHolder" );
		_errorMessage.setProperty( "error.msg.nosuchmethod_exception_in_DataHolder", "NoSuchMethodException trying to fetch property {0} on Data Holder" );
		_errorMessage.setProperty( "error.msg.illegal_access_exception_in_formBean", "IllegalAccessException trying to fetch property {0} on Form Bean" );
		_errorMessage.setProperty( "error.msg.invocation_target_exception_in_formBean", "InvocationTargetException trying to fetch property {0} on Form Bean" );
		_errorMessage.setProperty( "error.msg.nosuchmethod_exception_in_formBean", "NoSuchMethodException trying to fetch property {0} on Form Bean" );
		_errorMessage.setProperty( "error.msg.duplicate_column", "field Name {0} exist more than ones in Multirow taglib : {1}" );
		_errorMessage.setProperty( "error.msg.heading_missing", "Table heading is missing in {1}.properies file for Column Name {0}" );
		_errorMessage.setProperty( "error.msg.empty_columnname", "No column name is specied to display"  );
	}

	/**
	 * This method will format the Error message
	 *
	 * @param error
	 * @param obj[]
	 * @return java.lang.String
	 * @roseuid 40415A2300CB
	 */
	private String formatErrorMessage(String error, Object obj[])
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( "<font color='red' >").append( MessageFormat.format( _errorMessage.getProperty( error ), obj ) ).append( "</font>");
		return  buffer.toString();
	}

	/**
	 * This method will add hidden field to the HTML Template
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 413C12A7002E
	 */
	protected String addHidden(String columnName)
	{
		StringBuffer buffer = new StringBuffer();
		String value = _prop.getProperty( _name + "." + columnName + ".field.value","" ).trim();
		buffer.append( "<input type='hidden' name='" ).append( getFieldNamePrefix() ).append( "[0]." ).append( columnName )
				.append( "' id='" ).append( getUniqueId() ).append( columnName.toUpperCase() ).append( "' value='" )
				.append( value).append( "'>\n" );
		return buffer.toString();
	}

	/**
	 * This method will add TextArea to the HTML Template
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 413C12DC004E
	 */
	protected String addTextArea(String columnName)
	{
		StringBuffer buffer = new StringBuffer();
		String value = _prop.getProperty( _name + "." + columnName + ".field.value","" ).trim();
		String cols = _prop.getProperty( _name + "." + columnName + ".field.cols","25" ).trim();
		String rows = _prop.getProperty( _name + "." + columnName + ".field.rows","2" ).trim();
		String style = _prop.getProperty( _name + "." + columnName + ".field.style","" ).trim();
		String styleClass = _prop.getProperty( _name + "." + columnName + ".field.styleClass","" ).trim();
		boolean isReadOnly = _prop.getProperty( _name + "." + columnName + ".field.readOnly","FALSE" ).trim().equalsIgnoreCase( "true" ) || allReadOnly;
		String eventControl = _prop.getProperty( _name + "." + columnName + ".field.eventControl","" ).trim();

		buffer.append( "<textarea name='" ).append( _fieldNamePrefix ).append( "[0]." ).append( columnName )
				.append( "' id='" ).append(_uniqueId).append( columnName.toUpperCase() )
				.append( "' cols=").append( cols ).append( " rows=" ).append( rows );

		if (styleClass.length() != 0)
			buffer.append(" class=" ).append( styleClass );


		if (isReadOnly) {
			buffer.append( " readonly ");
			style = style.length() != 0 ? ";background-color:#CCCCCC;" : "background-color:#CCCCCC;";
		}

		if (style.length() != 0)
			buffer.append(" style='" ).append( style ).append("' ");

		if (eventControl.length() != 0)
			buffer.append( " " ).append( eventControl ).append( " " );

		buffer.append( ">" ).append( value ).append( "</textarea>\n" );
		return buffer.toString();
	}

	/**
	 * This method will add image to the HTML Template
	 * @param columnName
	 * @return java.lang.String
	 * @roseuid 413C12DD038A
	 */
	protected String addImage(String columnName)
	{
		StringBuffer buffer = new StringBuffer();
		String eventControl = _prop.getProperty( _name + "." + columnName + ".field.eventControl","" ).trim();
		String imageFile	= _prop.getProperty( _name + "." + columnName + ".image.fileName","" ).trim();

		buffer.append( "<img src='" ).append( getImagesPath() ).append( "/" ).append( imageFile )
				.append( "' id='" ).append( getUniqueId() ).append( columnName.toUpperCase() )
				.append( "' border=0 " );

		if (eventControl.length() != 0)
			buffer.append( " " ).append( eventControl ).append( " " );

		buffer.append( " >\n" );
		return buffer.toString();
	}

	protected String addDateField(String columnName)
	{
		StringBuffer buffer = new StringBuffer();

		String dateFormat   = _prop.getProperty( _name + "." + columnName + ".dateformat", _DEFAULT_DATE_FORMAT ).trim();
		String value		= _prop.getProperty( _name + "." + columnName + ".field.value","" ).trim();
        boolean isReadOnly = _prop.getProperty( _name + "." + columnName + ".field.readOnly","FALSE" ).trim().equalsIgnoreCase( "true" ) || allReadOnly;
        String styleClass = _prop.getProperty( _name + "." + columnName + ".field.styleClass","" ).trim();
        String style = _prop.getProperty( _name + "." + columnName + ".field.style","" ).trim();
		int maxLength	   = dateFormat.length();

		buffer.append( "<input type='text' name='" ).append( _fieldNamePrefix ).append( "[0]." ).append( columnName )
				.append( "' id='" ).append(_uniqueId).append( columnName.toUpperCase() ).append( "' value='" ).append( value )
				.append( "' size=").append( maxLength  ).append( " maxlength=" ).append( maxLength );

        if (styleClass.length() != 0) {
            buffer.append(" class=" ).append( styleClass );
        }
        if (isReadOnly) {
            buffer.append( " readonly ");
            style = style.length() != 0 ? ";background-color:#CCCCCC;" : "background-color:#CCCCCC;";
        }
        if (style.length() != 0) {
            buffer.append(" style='" ).append( style ).append("' ");
        }
        buffer.append( " onBlur=\"return getDisplayDateFormatByID(this,'").append( dateFormat ).append( "')\">");

        if(!isReadOnly) {
            buffer.append( "<img name='CAL_").append( _fieldNamePrefix ).append( "[0]." ).append( columnName ).append("' src='").append( getImagesPath() )
                    .append( "/calendar.gif' border=0 height='16' width='25' alt='Calendar' onMouseOver=\"this.style.cursor='hand'\"")
                    .append( " onClick=\"return newlaunchCalendar('previous','YES','").append( dateFormat ).append( "')\">\n");
        }
		return buffer.toString();
	}
}
