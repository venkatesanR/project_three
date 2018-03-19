//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\taglib\\FieldTag.java

package com.addval.taglib;

import java.io.IOException;

import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.ejbutils.server.EJBSTableManager;
import com.addval.environment.Environment;

import javax.servlet.jsp.JspTagException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.addval.metadata.*;

public class FieldTag extends GenericBodyTag {
	private String _columnName = null;
	private String _columnValue = "";
	private boolean _isEditable = true;
	private boolean _isCombo = false;
	private boolean _isReadOnly = false;
	private boolean _isCaption = false;
	private boolean _withCaption = false;
	private String _resetHidden = "";

	/**
	 * @param columnName
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADED0327
	 */
	public void setColumnName(String columnName) throws JspTagException {
        _columnName = columnName;
	}

	/**
	 * @param columnValue
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADEE01C0
	 */
	public void setColumnValue(String columnValue) throws JspTagException {
        _columnValue = columnValue;
	}

	/**
	 * @param isEditable
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADEE02BA
	 */
	public void setIsEditable(boolean isEditable) throws JspTagException {
        _isEditable = isEditable;
	}

	/**
	 * @param isCombo
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADEE03A0
	 */
	public void setIsCombo(boolean isCombo) throws JspTagException {
        _isCombo = isCombo;
	}

	/**
	 * @param isReadOnly
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADEF00BD
	 */
	public void setIsReadOnly(boolean isReadOnly) throws JspTagException {
        _isReadOnly = isReadOnly;
	}

	/**
	 * @param isCaption
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3CA1891F017F
	 */
	public void setIsCaption(boolean isCaption) throws JspTagException {
        _isCaption = isCaption;
	}

	/**
	 * @param withCaption
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3CA18921029A
	 */
	public void setWithCaption(boolean withCaption) throws JspTagException {
        _withCaption = withCaption;
	}

	/**
	 * @return int
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADEF01CB
	 */
	public int doStartTag() throws JspTagException {
        try {
            pageContext.getOut().write ( getInputTag() );
            return SKIP_BODY;
        }
        catch(Exception e) {
            try {
                pageContext.getOut().write ( e.toString() );
                return SKIP_BODY;
            }
            catch( IOException io ) {
                throw new JspTagException( "FieldTag Error:" + io.toString() );
            }
        }
	}

	/**
	 * @return String
	 * @throws java.lang.Exception
	 * @roseuid 3C99ADEF029E
	 */
	private String getInputTag() throws Exception {

        ColumnInfo columnInfo = getEjbsEditorMetaData().lookupColumnInfo( _columnName.toUpperCase() );
        if( _isCaption ){
            return columnInfo.getCaption() != null ? columnInfo.getCaption() :columnInfo.getName();
        }

        if( _isEditable )
            _columnName = columnInfo.getName() + "_edit";
        else
            _columnName = columnInfo.getName() + "_lookUp";

        if( _withCaption ) {
            StringBuffer html = new StringBuffer();
//Label
            html.append( "<td" )
                .append( " class=" )
                .append( "'label'>" )
                .append( columnInfo.getCaption() != null ? columnInfo.getCaption() :columnInfo.getName() )
                .append( "</td>" );
//Input
            html.append( "<td" )
                .append( " class=" )
                .append( "'input'>" )
                .append( columnInfo.isCombo() ? getComboBox( columnInfo ) : getTextBox( columnInfo ) )
                .append( "</td>" );
            return html.toString();
        }
        else{
            if( columnInfo.isCombo() )
                return getComboBox( columnInfo );
            else
                return getTextBox( columnInfo );
        }
	}

	/**
	 * @param columnInfo
	 * @return String
	 * @roseuid 3C99ADF003A3
	 */
	private String getTextBox(ColumnInfo columnInfo) {
//Input
        StringBuffer html = new StringBuffer();
        html.append( "<input " )
            .append( " type =\"text\"" )
            .append( " name =\"" )
            .append( _columnName )
            .append( "\"" )
            .append( " value =\"" )
            .append( _columnValue )
            .append( "\"" );
            if (columnInfo.getFormat() !=null ) {
                html.append( " size =\"" )
                    .append( columnInfo.getFormat().length () + 1 )
                    .append( "\"" )
                    .append( " maxlength =\"" )
                    .append( columnInfo.getFormat().length () )
                    .append( "\"" );
            }
            if( _isReadOnly )
                html.append( " READONLY >" );
            else
                html.append( getValidation( columnInfo ) )
                    .append( ">" );
//Calendar
        if( !_isReadOnly ) {
            if ( columnInfo.getType() == ColumnDataType._CDT_DATE || columnInfo.getType() == ColumnDataType._CDT_DATETIME ) {
                html.append( "<img src=\"../images/calendar.gif\" border=0 height=\"16\" width=\"25\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" ");
                if( _isEditable )
                    html.append(" onClick=\"setUpdateFlag();return launchCalendar('" );
                else
                    html.append(" onClick=\"return launchCalendar('" );
                html.append ( _columnName ).append ( "','" + columnInfo.getFormat() + "')\">" );
            }
        }
        return html.toString();
	}

	/**
	 * @param columnInfo
	 * @return String
	 * @throws java.lang.Exception
	 * @roseuid 3C99ADF1003E
	 */
	private String getComboBox(ColumnInfo columnInfo) throws Exception {
        StringBuffer html = new StringBuffer();
        if( _isCombo ) {
			//Populate Combo with DB Values
            String sql = getSQL( columnInfo );

            html.append( "<select name=\"").append( _columnName ).append( "\"" );
            if( _isEditable )
                html.append( " onChange =\"setUpdateFlag()\"");
            html.append( ">" );

            html.append( "<option value=\"\">&nbsp");

            if( sql.length() > 0 ) {
                int pos = sql.indexOf("LKP_");
                if( pos != -1 )
                    sql = sql.substring( 0, pos ) + sql.substring( pos + 4 );
                //System.out.println("SQL\t"+ sql);
                String              dbValue     = null;
                String              projectName = null;
                java.sql.Connection conn        = null;
                java.sql.Statement  stmt        = null;
                java.sql.ResultSet  rs          = null;
                try{
                    projectName = ( String ) pageContext.getServletConfig().getServletContext().getAttribute( "projectName" );

					// Get the column metadata for all columns in columnInfo
					EditorMetaData dummyEditorMetaData = new EditorMetaData( "FIELD_TAG" + columnInfo.getName(), null, "DEFAULT", null, null, false );
					ColumnMetaData columnMetaData = new ColumnMetaData( false, false, false, false, false, null, false, false );
					columnMetaData.setColumnInfo( columnInfo );
					dummyEditorMetaData.addColumnMetaData( columnMetaData );

					if (columnInfo.getComboSelect() != null) {
						
						ColumnInfo selectColumnInfo = getEjbsEditorMetaData().lookupColumnInfo( columnInfo.getComboSelect().toUpperCase() );
						columnMetaData = new ColumnMetaData( false, false, false, false, false, null, false, false );
						columnMetaData.setColumnInfo( selectColumnInfo );
						dummyEditorMetaData.addColumnMetaData( columnMetaData );
					}

                    rs = getEjbsTableManager().lookup( sql, dummyEditorMetaData );

                    while( rs.next() ) {

                        dbValue = rs.getString(1);
                        html.append( "<option value=\"")
                            .append( dbValue )
                            .append( "\"" );
                        if( _columnValue.equals( dbValue ) )
                            html.append( " selected>" );
                        else
                            html.append( ">" );
                        html.append( rs.getString(2) );
                    }
                }
                catch(java.sql.SQLException sqlEx ) {
                    throw new JspTagException( "FieldTag Error:" + sqlEx.toString() );
                }
                finally{
                    try {
                        if (rs != null   )  rs.close();
                        if (stmt != null ) stmt.close();
                        if (conn != null && projectName != null ) Environment.getInstance( projectName ).getDbPoolMgr().releaseConnection( conn );
                    }
                    catch (java.sql.SQLException se) {
                        throw new JspTagException( "FieldTag Error:" + se.toString() );
                    }
                }
            }
            html.append( "</select>" );
        }
        else if( columnInfo.getComboTblName() == null || _isReadOnly ) {
            return getTextBox( columnInfo );
        }
        else
        {
	            String displayFieldName = _columnName;


		        // Lookup string for the combo box column
		        html.append( getComboLookupString( columnInfo ) );

		        // Enter a hidden variable for the combo column
		        // If comboselect name is different from the column name add a hidden variable
		        if (columnInfo.getComboSelect() != null && !columnInfo.getComboSelect().equalsIgnoreCase( _columnName ))
		            html.append( "<INPUT type='hidden' name='" + displayFieldName + "' value='" + _columnValue + "'>" );

	        	// Get the value of the combo box
	        	ColumnInfo comboMetaData = columnInfo;

	        	if ( columnInfo.getComboSelect() != null )
	        	{
					comboMetaData = getEjbsEditorMetaData().lookupColumnInfo( columnInfo.getComboSelect().toUpperCase() );
				}

		        String	comboColumnName		= comboMetaData.getName();
		        String	comboColumnValue	= _columnValue;
		        String	format				= comboMetaData.getFormat();
		        int		maxLen				= (format == null) ? 10 : format.length();
			    int		size				= maxLen + 1;

                if( _isEditable )
                    displayFieldName = comboColumnName + "_edit";
                else
                    displayFieldName = comboColumnName + "_lookUp";


		        // Put a textbox for the column itself
		        html.append( "<INPUT type='text' " );
		        html.append( "name='" + displayFieldName + "' " );
		        html.append( "value='" + comboColumnValue + "' " );
		        html.append( "size ='" + size + "'" );
		        html.append( "maxLength ='" + maxLen + "' " );
		        html.append( getValidationString( comboMetaData ) );
		        html.append( ">" );

        }
        return html.toString();
	}

	/**
	 * @param columnInfo
	 * @return String
	 * @roseuid 3C99ADF102BE
	 */
	private String getValidation(ColumnInfo columnInfo) {
        StringBuffer validation = new StringBuffer();

        if( !_isReadOnly ) {

            if( _isEditable )
                validation.append( " onChange =\"setUpdateFlag();");
            else
                validation.append( " onChange =\"" )
                            .append( _resetHidden );

            validation.append( "return check(\'" )
                    .append( _columnName )
                    .append( "\',\'" )
                    .append( columnInfo.getFormat() == null ? "" : columnInfo.getFormat() )
                    .append( "\',\'" )
                    .append( columnInfo.getMinval() )
                    .append( "\',\'" )
                    .append( columnInfo.getMaxval() )
                    .append( "\',\'" )
                    .append( columnInfo.getRegexp() == null ? "" : columnInfo.getRegexp() )
                    .append( "\',\'" )
                    .append( columnInfo.getErrorMsg() == null ? "" : columnInfo.getErrorMsg() )
                    .append( "\'" )
                    .append( ")\"" );
            if ( columnInfo.getType() == ColumnDataType._CDT_DATE || columnInfo.getType() == ColumnDataType._CDT_DATETIME )
                validation.append( " onBlur=\"isDate( this )\"" );
        }
        return validation.toString();
	}

	/**
	 * @param columnInfo
	 * @return String
	 * @roseuid 3C99ADF10319
	 */
	private String getSQL(ColumnInfo columnInfo) {
        StringBuffer sql = new StringBuffer();
        if( columnInfo.getComboTblName() != null ) {

            sql.append( "SELECT " )
                .append( columnInfo.getName() );

            if( columnInfo.getComboSelect() != null )
                sql.append( "," ).append( columnInfo.getComboSelect() );

            sql.append( " FROM " )
                .append( columnInfo.getComboTblName() );

            if( columnInfo.getComboOrderBy() != null )
                sql.append( " ORDER BY " ).append( columnInfo.getComboOrderBy() );
        }
        return sql.toString();
	}

	/**
	 * @return int
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C99ADF1037D
	 */
	public int doEndTag() throws JspTagException {
        try {
            return EVAL_PAGE;
        }
        catch( Exception e ) {
            release();
            throw new JspTagException( "FieldTag Error:" + e.toString() );
        }
	}

	/**
	 * @roseuid 3C99ADF20067
	 */
	public void release() {
        _columnName  = null;
        _columnValue = "";
        _isEditable  = true;
        _isCombo     = false;
        super.release();
	}

	/**
	 * @param columnMetaData
	 * @return String
	 * @roseuid 3DE01BFA0266
	 */
	private String getValidationString(ColumnInfo columnMetaData) {

        StringBuffer validation = new StringBuffer();

        String  format = columnMetaData.getFormat  ();
        double  minVal = columnMetaData.getMinval  ();
        double  maxVal = columnMetaData.getMaxval  ();
        String  regExp = columnMetaData.getRegexp  ();
        String  errMsg = columnMetaData.getErrorMsg();

        format = (format == null) ? "" : format;
        regExp = (regExp == null) ? "" : regExp;
        errMsg = (errMsg == null) ? "" : errMsg;

        // Javascript function to call :
        //                  check( field, format, minVal, maxVal, regExp, errorMsg )
        if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME)
            validation.append ( "onBlur=\"isDate( this )\"" );

        validation.append( "onChange=\"setUpdateFlag(); " );
        validation.append( "return check("               );

        if( _isEditable )
	        validation.append( "'" + columnMetaData.getName() + "_edit" + "'," );
        else
	        validation.append( "'" + columnMetaData.getName() + "_lookUp" + "'," );


        validation.append( "'" + format + "'," );
        validation.append(       minVal + ", " );
        validation.append(       maxVal + ", " );
        validation.append( "'" + regExp + "'," );
        validation.append( "'" + errMsg + "'"  );
        validation.append( ");\"" );

        return validation.toString();
	}
	
	private Object getBean(String beanName){
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
		return context.getBean( beanName );
	}

	private EJBSEditorMetaData getEjbsEditorMetaData(){
		return (EJBSEditorMetaData) getBean("editorMetaDataServer");
	}
	
	private EJBSTableManager getEjbsTableManager(){
		return (EJBSTableManager) getBean("editorTableManager");
	}
	/**
	 * @param columnMetaData
	 * @return String
	 * @roseuid 3DE01BFA036A
	 */
	private String getComboLookupString(ColumnInfo columnMetaData) {

        StringBuffer lookup = new StringBuffer();

        // Javascript function for launching a lookup window
            // launchLookup ( displayFieldName, editorName, order, selectFieldName, title )


        String display = null;

        if( _isEditable )
			display = (columnMetaData.getComboSelect() != null) ? columnMetaData.getComboSelect() + "_edit" : columnMetaData.getName() + "_edit";
		else
			display = (columnMetaData.getComboSelect() != null) ? columnMetaData.getComboSelect() + "_lookUp" : columnMetaData.getName() + "_lookUp";


        String editor  = columnMetaData.getComboTblName();
        String orderBy = (columnMetaData.getComboOrderBy() != null) ? columnMetaData.getComboOrderBy() : columnMetaData.getName();

		String select = null;

        if( _isEditable )
	        select  = columnMetaData.getName() + "_edit";
	    else
	        select  = columnMetaData.getName() + "_lookUp";

        String title   = columnMetaData.getCaption();

        lookup.append( "<a href=\"javascript:launchLookup( " );
        lookup.append( "'" + display + "'," );
        lookup.append( "'" + editor  + "'," );
        lookup.append( "'" + orderBy + "'," );
        lookup.append( "'" + select  + "'," );
        lookup.append( "'" + title   + "'"  );
        lookup.append( ")\" onClick=\"currObj =event.srcElement;\"><img src='../images/lookup.gif' border=0 alt='Lookup'></a> " );

        return lookup.toString();
	}
}
