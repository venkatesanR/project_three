//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\taglib\\EditTag.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\taglib\\EditTag.java

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\taglib\\EditTag.java
/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.taglib;

import com.addval.metadata.EditorMetaData;
import java.sql.ResultSet;
import com.addval.metadata.ColumnDataType;
import javax.servlet.jsp.JspTagException;
import java.util.Vector;

import com.addval.metadata.ColumnMetaData;
import com.addval.environment.Environment;

import java.sql.SQLException;
import org.apache.commons.lang.StringEscapeUtils;

public class EditTag extends GenericBodyTag {
	private boolean _twoPerRow = true;
	private int _colsPerRow = 1;
	private static final String _EDIT = "_edit";
	private static final String _KEY = "_KEY";
	private boolean _hasViewPermission = true;
	private boolean _hasEditPermission = true;
	private String _viewrole = "";
	private String _editrole = "";
	private EditorMetaData _editorMetaData = null;
	private ResultSet _resultSet = null;
	private String _searchableMandatorySymbol = null;

	private static final int _MAX_TEXT_FIELD_SIZE = 100;

	/**
	 * @param md
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D42E4013D
	 */
	public void setMetadata_id(String md) throws JspTagException {

		Object md_obj = pageContext.getAttribute(md, getScope());

		if (md_obj != null && md_obj instanceof EditorMetaData)
			_editorMetaData = (EditorMetaData) md_obj;
		else
			throw new IllegalArgumentException("EditTag: EditorMetaData object was not set in the Page's context");
	}

	/**
	 * @param rs
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D42EA000F
	 */
	public void setResultset_id(String rs) throws JspTagException {

		Object rs_obj = pageContext.getAttribute(rs, getScope());

		if (rs_obj != null && rs_obj instanceof ResultSet)
			_resultSet = (ResultSet) rs_obj;
		else
			throw new IllegalArgumentException("EditTag: ResultSet object was not set in the Page's context");
	}

	/**
	 * @param twoPerRow
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3C849B370188
	 */
	public void setTwoPerRow(boolean twoPerRow) throws JspTagException {

		_twoPerRow = twoPerRow;
	}

	/**
	 * @param cols
	 * @roseuid 3CAA00BB0290
	 */
	public void setColsPerRow(int cols) {

		_colsPerRow = cols;
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D42FE0090
	 */
	public int doAfterBody() throws JspTagException {

		return SKIP_BODY;
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D43010135
	 */
	public int doStartTag() throws JspTagException {

		try {
			_searchableMandatorySymbol = Environment.getInstance("subsystem").getCnfgFileMgr().getPropertyValue("mandatory.symbol", "*");

			if ((_viewrole != null) && (_viewrole.length() > 0)) {
				javax.servlet.http.HttpServletRequest httpreq = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();

				if (httpreq.isUserInRole(_viewrole))
					_hasViewPermission = true;
				else
					_hasViewPermission = false;
			}

			if ((_editrole != null) && (_editrole.length() > 0)) {
				javax.servlet.http.HttpServletRequest httpreq = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();

				if (httpreq.isUserInRole(_editrole))
					_hasEditPermission = true;
				else
					_hasEditPermission = false;
			}

			if (_hasViewPermission && _hasEditPermission) {

				StringBuffer html = new StringBuffer();
				StringBuffer notNulls = new StringBuffer();
				Vector editColumns = getEditScreenColumns();
				int size = (editColumns == null) ? 0 : editColumns.size();

				ColumnMetaData columnMetaData = null;

				while (_resultSet.next()) {

					for (int index = 0; index < size; index++) {

						columnMetaData = (ColumnMetaData) editColumns.elementAt(index);
						html.append(getDisplay(columnMetaData, index));
						html.append(System.getProperty("line.separator"));
						String columnName = columnMetaData.getName() + _EDIT;
						String caption = columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption();
						// Changed on 06/17/2003 - Prasad
						// If the column is a key and it is being displayed to the user, then
						// check for Nullability. This code was added because editors that displayed
						// the key (non-integer keys) were not checking for non-nullability of these keys
						if ((columnMetaData.getType() != ColumnDataType._CDT_USER) && ((!columnMetaData.isEditKey() && !columnMetaData.isNullable()) || (columnMetaData.isEditKey() && columnMetaData.isEditKeyDisplayable() && !columnMetaData.isNullable()))) {
							if (columnMetaData.getComboSelect() != null)
								columnMetaData = _editorMetaData.getColumnMetaData(_editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));

							// String columnName = columnMetaData.isEditKey() ? columnMetaData.getName() + _KEY : columnMetaData.getName() + _EDIT;

							// Changed on 08/08/2003 - Ranes
							// Added the trim function to fix the problem when a space is given
							// as a search criteria and return key is presed.

							notNulls.append("if(!(frmObj.").append(columnName).append(")) return true;");

							notNulls.append("if( trim(frmObj.").append(columnName).append(".value)").append("==\"\")").append(" { ").append("alert(").append("\"Null values not allowed for ").append(caption).append("!\"").append(");").append("frmObj.").append(columnName).append(".focus();")
									.append("return false;").append("}");

						}// if ends
					}
				}

				html.append("<SCRIPT FOR = edit EVENT = onsubmit>").append("return checkNulls();").append("</SCRIPT>");

				html.append("<SCRIPT language=\"javascript\">").append("function checkNulls() {").append("frmObj = document.forms[0];").append(notNulls).append("return true;").append("}").append("</SCRIPT>");

				pageContext.getOut().write(html.toString());

			}

			return SKIP_BODY;
		}
		catch (Exception e) {

			throw new JspTagException(e.getMessage());
		}
	}

	/**
	 * @return int
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D4307004D
	 */
	public int doEndTag() throws JspTagException {

		try {

			return EVAL_PAGE;
		}
		catch (Exception e) {

			release();
			throw new JspTagException(e.getMessage());
		}
	}

	/**
	 * @roseuid 3B4D430A0106
	 */
	public void release() {

		_editorMetaData = null;
		_resultSet = null;
		super.release();
	}

	/**
	 * @return Vector
	 * @roseuid 3B4D430D0287
	 */
	private Vector getEditScreenColumns() {

		// Add all columns that are editable in the edit screen
		Vector columns = _editorMetaData.getColumnsMetaData();
		Vector editColumns = new Vector();
		ColumnMetaData columnMetaData = null;
		int size = (columns == null) ? 0 : columns.size();

		for (int index = 0; index < size; index++) {

			columnMetaData = (ColumnMetaData) columns.elementAt(index);

			// Only updatable columns will be shown in the Edit Screen
			if (columnMetaData.getType() != ColumnDataType._CDT_LINK && columnMetaData.isUpdatable()) {

				editColumns.add(columnMetaData);
			}
		}

		return editColumns;

		/***************
		 * OLD CODE as of 4/2/2002 *****************
		 *
		 * java.util.ArrayList columnList = new java.util.ArrayList(); java.util.Vector comboColumns = _editorMetaData.getComboColumns(); java.util.Hashtable selectColumns = new java.util.Hashtable();
		 *
		 * int size = comboColumns == null ? 0:comboColumns.size();
		 *
		 * ColumnMetaData columnMetaData = null;
		 *
		 * // Add all combo columns first
		 *
		 * for (int i = 0; i < size; ++i) { columnMetaData = (ColumnMetaData) comboColumns.elementAt(i);
		 *
		 * // In master tables we do want to show the description if (!columnMetaData.isEditKey()) if (columnMetaData.getComboSelect() != null) selectColumns.put(columnMetaData.getComboSelect(), columnMetaData); }
		 *
		 * for ( Iterator iterator =_editorMetaData.getColumnsMetaData().iterator ();iterator.hasNext (); ) { columnMetaData = ( ColumnMetaData ) iterator.next ();
		 *
		 * if ( ( columnMetaData.getType() != ColumnDataType._CDT_LINK ) && // SATYA ( columnMetaData.isDisplayable() || columnMetaData.isUpdatable() ) ( columnMetaData.isUpdatable() ) ) {
		 *
		 * if (!selectColumns.containsKey(columnMetaData.getName())) columnList.add( columnMetaData ); // if (columnMetaData.getComboSelect() != null) // columns.add( columnMetaData.getComboSelect() ); } } return columnList.iterator();
		 ***************/
	}

	/**
	 * @param columnMetaData
	 * @param index
	 * @return String
	 * @throws JspTagException
	 * @throws SQLException
	 * @throws javax.servlet.jsp.JspTagException
	 * @throws java.sql.SQLException
	 * @roseuid 3CA9F0D500F3
	 */
	private String getDisplay(ColumnMetaData columnMetaData, int index) throws JspTagException, SQLException {

		if (!columnMetaData.isUpdatable())
			throw new JspTagException("Column : " + columnMetaData.getName() + " is not an editable column");

		StringBuffer html = new StringBuffer();

		if (columnMetaData.isEditKey()) {
			if (columnMetaData.isEditKeyDisplayable()) {

				displayStartRow(html, index, null);

				displayStartColumn(html, "label");
				displayCaption(columnMetaData, html);
				displayEndColumn(html);

				displayStartColumn(html, "input");
				displayKey(columnMetaData, html);
				displayEndColumn(html);

				displayEndRow(html, index);
			}
			else {
				// If it is a key column, then put a hidden variable
				displayHidden(columnMetaData, html);
			}

		}
		else if (columnMetaData.getType() != ColumnDataType._CDT_USER) {

			displayStartRow(html, index, null);

			displayStartColumn(html, "label");
			displayCaption(columnMetaData, html);
			displayEndColumn(html);

			displayStartColumn(html, "input");
			displayInput(columnMetaData, html);
			displayEndColumn(html);

			displayEndRow(html, index);
		}

		return html.toString();
	}

	/**
	 * @param html
	 * @param index
	 * @param style
	 * @roseuid 3CA9FFF80177
	 */
	private void displayStartRow(StringBuffer html, int index, String style) {

		// If it is a multiple of the colsPerRow, then display the start <TR> tag
		if (index == 0 || index % _colsPerRow == 0) {

			html.append("<TR");
			if (style != null)
				html.append(" class='" + style + "'");
			html.append(">");
		}
	}

	/**
	 * @param html
	 * @param index
	 * @roseuid 3CAA001B038A
	 */
	private void displayEndRow(StringBuffer html, int index) {

		// If it is a multiple of the colsPerRow, then display the start <TR> tag
		if (index == 0 || index % _colsPerRow == 0)
			html.append("</TR>");
	}

	/**
	 * @param html
	 * @param style
	 * @roseuid 3CAA002102AC
	 */
	private void displayStartColumn(StringBuffer html, String style) {

		html.append("<TD");
		if (style != null)
			html.append(" class='" + style + "'");
		html.append(" nowrap>");
	}

	/**
	 * @param html
	 * @roseuid 3CAA00210324
	 */
	private void displayEndColumn(StringBuffer html) {

		html.append("</TD>");
	}

	/**
	 * @param columnMetaData
	 * @param html
	 * @throws SQLException
	 * @throws java.sql.SQLException
	 * @roseuid 3CAA00680254
	 */
	private void displayHidden(ColumnMetaData columnMetaData, StringBuffer html) throws SQLException {

		String columnName = columnMetaData.getName();
		String columnValue = _resultSet.getString(columnName) == null ? "" : _resultSet.getString(columnName);

		html.append("<INPUT type='hidden' name='" + columnName + _KEY + "' value='" + escapeHtml(columnValue) + "'>");
	}

	private void setReadOnlyText(String colName, String colValue, StringBuffer html) {
		html.append(colValue + "<INPUT type='hidden' name='" + colName + "' value='" + escapeHtml(colValue) + "'>");
	}

	/**
	 * @param columnMetaData
	 * @param html
	 * @throws SQLException
	 * @throws java.sql.SQLException
	 * @roseuid 3CAA0063008A
	 */
	private void displayKey(ColumnMetaData columnMetaData, StringBuffer html) throws SQLException {

		String columnName = columnMetaData.getName();
		String columnValue = _resultSet.getString(columnName) == null ? "" : _resultSet.getString(columnName);

		// If the key needs to be displayed in the Edit Screen, then it should be shown as a label
		// since we do not allow keys to be edited.
		// In the Add Screen however a new key needs to be entered and so it needs to be an input box
		String action = pageContext.getRequest().getParameter("kindOfAction");
		action = (action == null || action.length() == 0) ? "edit" : action;

		String format = columnMetaData.getFormat();
		int maxLen = columnMetaData.getFormatLength();
		int size = maxLen + 1;
		boolean readonly = true;

		// In the Add case, the key is always displayed as a simple text box
		if (action.equalsIgnoreCase("insert") || action.equalsIgnoreCase("clone")) {

			if ((columnMetaData.isSecured() == false) || (pageContext.getRequest().getAttribute(columnMetaData.getName() + "_unlock") != null))
				readonly = false;

			String defaultColumnValue = pageContext.getRequest().getParameter((columnName + "_lookUp"));

			// Changed on 08/08/2003 - Ranes
			// Now checking defaultColumnValue is not null and not empty
			// this has been added to fix incorrect clone functionality.

			if ((columnValue == null || columnValue.length() == 0) && defaultColumnValue != null && defaultColumnValue.length() != 0)
				columnValue = defaultColumnValue;
		}
		if (readonly) {
			setReadOnlyText(columnName + _EDIT, columnValue, html);
		}
		else if (columnMetaData.isCombo()) {
			displayCombo(columnMetaData, html);
		}
		else {
			html.append("<INPUT type='text' name='" + columnName + _EDIT + "' size='" + size + "' maxlength='" + maxLen + "' ");
			html.append(getValidationString(columnMetaData));
			html.append(" value='" + escapeHtml(columnValue) + "'  onKeyPress='if(event.keyCode == 13) return false;'>");
		}
	}

	/**
	 * @param columnMetaData
	 * @param html
	 * @roseuid 3CAA00920042
	 */
	private void displayCaption(ColumnMetaData columnMetaData, StringBuffer html) {
		html.append(columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption());

		if (!columnMetaData.isNullable())
			html.append(" <font class=\"mandatory\">" + _searchableMandatorySymbol + "</font>:");
	}

	/**
	 * @param columnMetaData
	 * @param html
	 * @throws SQLException
	 * @throws java.sql.SQLException
	 * @roseuid 3CAA005502ED
	 */
	private void displayInput(ColumnMetaData columnMetaData, StringBuffer html) throws SQLException {
		if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {
			displayFile(columnMetaData, html);
		}
		else if (columnMetaData.isCombo()) {
			displayCombo(columnMetaData, html);
		}
		else {
			displayTextBox(columnMetaData, html);
		}
	}

	/**
	 * @param columnMetaData
	 * @param html
	 * @throws SQLException
	 * @throws java.sql.SQLException
	 * @roseuid 3CAA151D03AB
	 */
	private void displayCombo(ColumnMetaData columnMetaData, StringBuffer html) throws SQLException {

		String action = pageContext.getRequest().getParameter("kindOfAction");
		action = (action == null || action.length() == 0) ? "edit" : action;

		String columnName = columnMetaData.getName();
		String columnValue = _resultSet.getString(columnName) == null ? "" : _resultSet.getString(columnName);

		boolean readonly = ((columnMetaData.isSecured() && (pageContext.getRequest().getAttribute(columnMetaData.getName() + "_unlock") == null)));

		if (action.equalsIgnoreCase("insert")) {
			// check if columnname _lookUp value is present in request.
			// If so set it as default
			String defaultColumnValue = pageContext.getRequest().getParameter((columnName + "_lookUp"));

			// Changed to use the Columns.column_value as the default value
			// March 25th Change to put a constant in case of insert.
			if (defaultColumnValue == null)
				defaultColumnValue = columnMetaData.getValue();

			if ((columnValue == null || columnValue.length() == 0) && defaultColumnValue != null && defaultColumnValue.length() > 0) {
				columnValue = defaultColumnValue;
			}

		}

		if (columnMetaData.getEjbResultSet() == null) {
			// Enter a hidden variable for the combo column
			// If comboselect name is different from the column name add a hidden variable
			if (columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName))
				html.append("<INPUT type='hidden' name='" + columnName + _EDIT + "' value='" + escapeHtml(columnValue) + "'>");
		}

		// Get the value of the combo box
		ColumnMetaData comboMetaData = columnMetaData;

		if (columnMetaData.getComboSelect() != null)
			comboMetaData = _editorMetaData.getColumnMetaData(_editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));

		String comboColumnName = comboMetaData.getName();
		String comboColumnValue = _resultSet.getString(comboMetaData.getName()) == null ? "" : _resultSet.getString(comboMetaData.getName());
		String format = comboMetaData.getFormat();
		int maxLen = comboMetaData.getFormatLength();
		int size = maxLen + 1;

		if (action.equalsIgnoreCase("insert")) {

			// check if columnname _lookUp value is present in request.
			// If so set it as default
			String defaultColumnValue = pageContext.getRequest().getParameter((comboColumnName + "_lookUp"));

			// Changed to use the Columns.column_value as the default value
			// March 25th Change to put a constant in case of insert.
			if (defaultColumnValue == null)
				defaultColumnValue = columnMetaData.getValue();

			if ((comboColumnValue == null || comboColumnValue.length() == 0) && defaultColumnValue != null && defaultColumnValue.length() != 0) {
				comboColumnValue = defaultColumnValue;
			}

		}

		if (columnMetaData.getEjbResultSet() == null) {

			// Put a textbox/textarea for the column itself
			if (readonly) {
				setReadOnlyText(comboColumnName + _EDIT, comboColumnValue, html);
			}
			else if (size < _MAX_TEXT_FIELD_SIZE) {

				html.append("<INPUT type='text' ");
				html.append("name='" + comboColumnName + _EDIT + "' ");
				html.append("value='" + escapeHtml(comboColumnValue) + "' ");
				html.append("size ='" + size + "'");
				html.append("maxLength ='" + maxLen + "' ");
				html.append(getValidationString(comboMetaData));
				html.append(" onKeyPress='if(event.keyCode == 13) return false;' >");

			}
			else {

				html.append("<TEXTAREA ");
				html.append("name='" + comboColumnName + _EDIT + "' ");
				html.append("rows='" + getTextareaRows(columnMetaData) + "' ");
				html.append("cols='" + getTextareaCols(columnMetaData) + "' ");
				html.append("maxLength ='" + maxLen + "' ");
				if (comboMetaData.getColumnInfo() != null && (comboMetaData.getColumnInfo().getType() != ColumnDataType._CDT_CLOB || comboMetaData.getColumnInfo().getType() != ColumnDataType._CDT_BLOB))
					html.append(getValidationString(comboMetaData));
				html.append(">" + escapeHtml(columnValue) + "</TEXTAREA>");

			}

			// Lookup string for the combo box column
			if ((columnMetaData.isSecured() == false) || (pageContext.getRequest().getAttribute(columnMetaData.getName() + "_unlock") != null))
				html.append(getComboLookupString(columnMetaData));

		}
		else {

			com.addval.ejbutils.dbutils.EJBResultSet comboResultSet = (com.addval.ejbutils.dbutils.EJBResultSet) columnMetaData.getEjbResultSet();
			comboResultSet.beforeFirst();
			com.addval.metadata.EditorMetaData optMetaData = ((com.addval.ejbutils.dbutils.EJBResultSetMetaData) comboResultSet.getMetaData()).getEditorMetaData();
			com.addval.ejbutils.dbutils.EJBStatement stmt = new com.addval.ejbutils.dbutils.EJBStatement(comboResultSet);
			com.addval.dbutils.RSIterator rsItem = new com.addval.dbutils.RSIterator(stmt, "1", new com.addval.dbutils.RSIterAction(com.addval.dbutils.RSIterAction._FIRST), comboResultSet.getRecords().size(), comboResultSet.getRecords().size() == 0 ? 10 : comboResultSet.getRecords().size());
			Vector displayableColumns = optMetaData.getDisplayableColumns();
			Vector allColumns = optMetaData.getColumnsMetaData();

			// Put a textbox for the column itself
			html.append("<SELECT ");
			html.append("name='" + columnName + _EDIT + "' >");

			if (columnMetaData.isNullable()) {
				// add a blank option
				html.append("<OPTION VALUE=''> </OPTION>");
			}

			while (rsItem.next()) {

				String optValue = "";
				String optDesc = "";
				String selected = "";

				for (int j = 0; j < displayableColumns.size(); j++) {
					ColumnMetaData optcolMetaData = (ColumnMetaData) displayableColumns.elementAt(j);
					String optcolName = optcolMetaData.getName();
					String optcolValue = rsItem.getString(optcolName);

					if (optcolName.equalsIgnoreCase(columnName)) {
						if (optcolValue == null)
							optcolValue = columnName + "is null";

						optValue = optcolValue;
					}

					if (optcolName.equalsIgnoreCase(comboColumnName)) {
						if (optcolValue == null)
							optcolValue = comboColumnName + "is null";

						optDesc = optcolValue;
					}
				}

				if (optValue.equals(columnValue))
					selected = "selected";
				else
					selected = "";

				html.append("<OPTION VALUE='" + optValue + "' " + selected + ">");
				html.append(optDesc);
				html.append("</OPTION>");
			}

			html.append("</SELECT>");
		}
	}

	/**
	 * @param columnMetaData
	 * @param html
	 * @throws SQLException
	 * @throws java.sql.SQLException
	 * @roseuid 3CAA151E006D
	 */
	private void displayTextBox(ColumnMetaData columnMetaData, StringBuffer html) throws SQLException {

		String columnName = columnMetaData.getName();
		String columnValue = _resultSet.getString(columnName) == null ? "" : _resultSet.getString(columnName);
		String format = columnMetaData.getFormat();
		int maxLen = columnMetaData.getFormatLength();
		int size = maxLen + 1;

		boolean readonly = ((columnMetaData.isSecured() && (pageContext.getRequest().getAttribute(columnMetaData.getName() + "_unlock") == null)));

		String action = pageContext.getRequest().getParameter("kindOfAction");
		action = (action == null || action.length() == 0) ? "edit" : action;

		if (action.equalsIgnoreCase("insert")) {

			// check if columnname _lookUp value is present in request.
			// If so set it as default
			String defaultColumnValue = pageContext.getRequest().getParameter((columnName + "_lookUp"));

			// Changed to use the Columns.column_value as the default value
			// March 25th Change to put a constant in case of insert.
			if (defaultColumnValue == null)
				defaultColumnValue = columnMetaData.getValue();

			if ((columnValue == null || columnValue.length() == 0) && defaultColumnValue != null && defaultColumnValue.length() != 0)
				columnValue = defaultColumnValue;
		}
		if (readonly) {
			setReadOnlyText(columnName + _EDIT, columnValue, html);
		}
		else if (size < _MAX_TEXT_FIELD_SIZE) {
			html.append("<INPUT type='text' name='" + columnName + _EDIT + "' size='" + size + "' maxlength='" + maxLen + "' ");
			html.append(getValidationString(columnMetaData));
			html.append(" value='" + escapeHtml(columnValue) + "' ");
			html.append(" onKeyPress='if(event.keyCode == 13) return false;'>");
		}
		else {
			html.append("<TEXTAREA name='" + columnName + _EDIT + "' rows='" + getTextareaRows(columnMetaData) + "' cols='" + getTextareaCols(columnMetaData) + "' maxlength='" + maxLen + "' ");
			html.append(getValidationString(columnMetaData));
			html.append(">" + escapeHtml(columnValue) + "</TEXTAREA>");

		}

		// If calendar is required only if it is not readonly
		if (!readonly && (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME)) {

			html.append("<a href=\"javascript:setUpdateFlag();javascript:launchCalendar('");
			html.append(columnName + _EDIT + "',");
			html.append("'" + format + "'");
			html.append(")\">");
			html.append("<img src='../images/calendar.gif' border=0 height='16' width='25' alt='Calendar'>");
			html.append("</a>");
			html.append("&nbsp;");
		}
	}

	private void displayFile(ColumnMetaData columnMetaData, StringBuffer html) {
		String columnName = columnMetaData.getName();
		int maxLen = columnMetaData.getFormatLength();
		int size = maxLen + 1;

		html.append("<INPUT type='file' name='");
		html.append(columnName + _EDIT);
		html.append("' size='").append(size);
		html.append("' maxlength='").append(maxLen);
		html.append("' >");
		html.append("&nbsp;");
	}

	/**
	 * @param columnMetaData
	 * @return String
	 * @roseuid 3CAA17AB0293
	 */
	private String getValidationString(ColumnMetaData columnMetaData) {
		StringBuffer validation = new StringBuffer();

		String format = columnMetaData.getFormat();
		double minVal = columnMetaData.getMinval();
		double maxVal = columnMetaData.getMaxval();
		String regExp = columnMetaData.getRegexp();
		String errMsg = columnMetaData.getErrorMsg();

		format = (format == null) ? "" : format;
		regExp = (regExp == null) ? "" : regExp;
		errMsg = (errMsg == null) ? "" : errMsg;

		// Javascript function to call :
		// check( field, format, minVal, maxVal, regExp, errorMsg )
		if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME)
			validation.append("onBlur=\"isValidDate( this, '" + format + "' )\" ");

		validation.append("onBlur=\"setUpdateFlag(); ");
		validation.append("return check(");
		validation.append("'" + columnMetaData.getName() + _EDIT + "',");
		validation.append("'" + format + "',");
		validation.append(minVal + ", ");
		validation.append(maxVal + ", ");
		validation.append("'" + regExp + "',");
		validation.append("'" + errMsg + "'");
		validation.append(");\"");

		return validation.toString();
	}

	/**
	 * @param columnMetaData
	 * @return String
	 * @roseuid 3CAA3C8A0001
	 */
	private String getComboLookupString(ColumnMetaData columnMetaData) {

		StringBuffer lookup = new StringBuffer();

		// Javascript function for launching a lookup window
		// launchLookup ( displayFieldName, editorName, order, selectFieldName, title )

		String display = (columnMetaData.getComboSelect() != null) ? columnMetaData.getComboSelect() + _EDIT : columnMetaData.getName() + _EDIT;
		String editor = columnMetaData.getComboTblName();
		String orderBy = (columnMetaData.getComboOrderBy() != null) ? columnMetaData.getComboOrderBy() : columnMetaData.getName();
		String select = columnMetaData.getName() + _EDIT;
		String title = columnMetaData.getCaption();

		lookup.append("<a href=\"javascript:launchLookup( ");
		lookup.append("'" + display + "',");
		lookup.append("'" + editor + "',");
		lookup.append("'" + orderBy + "',");
		lookup.append("'" + select + "',");
		lookup.append("'" + title + "'");
		lookup.append(")\" onClick=\"currObj =event.srcElement;\"><img src='../images/lookup.gif' border=0 alt='Lookup'></a> ");

		return lookup.toString();
	}

	/**
	 * @param columnMetaData
	 * @return String
	 * @throws JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D431102E6
	 */
	private String getTextBox(ColumnMetaData columnMetaData) throws JspTagException {

		/*********************************************
		 * OLD CODE **************************************************** REPLACED BY display* functions by Prasad on 4/2/2002
		 **********************************************************************************************************/

		StringBuffer htmlString = new StringBuffer();
		String columnName = columnMetaData.getName();
		String kindOfAction = pageContext.getRequest().getParameter("kindOfAction");

		if (kindOfAction == null)
			kindOfAction = "edit";

		try {
			boolean update = true;

			if (!columnMetaData.isUpdatable() && !kindOfAction.equalsIgnoreCase("insert"))
				update = false;

			String columnValue = (_resultSet.getString(columnName) == null ? "" : _resultSet.getString(columnName));
			String checkParameters = "\'" + columnName + "_edit" + "\',\'" + (columnMetaData.getFormat() == null ? "" : columnMetaData.getFormat()) + "\',\'" + Double.toString(columnMetaData.getMinval()) + "\',\'" + Double.toString(columnMetaData.getMaxval()) + "\',\'"
					+ (columnMetaData.getRegexp() == null ? "" : columnMetaData.getRegexp()) + "\',\'" + (columnMetaData.getErrorMsg() == null ? "" : columnMetaData.getErrorMsg()) + "\'";

			// Normally the selected column is same as the current column

			String selectColumnName = columnName;
			ColumnMetaData selectColumnMetaData = columnMetaData;
			String selectColumnValue = columnValue;
			String selectCheckParameters = checkParameters;
			String comboresetFunction = "";

			// If it is combo determine then the selected column could be different
			if (columnMetaData.isCombo()) {
				if (columnMetaData.getComboSelect() != null) {
					selectColumnName = columnMetaData.getComboSelect();

					int idx = _editorMetaData.getColumnIndex(columnMetaData.getComboSelect());

					selectColumnMetaData = _editorMetaData.getColumnMetaData(idx);

					selectColumnValue = (_resultSet.getString(selectColumnName) == null ? "" : _resultSet.getString(selectColumnName));

					selectCheckParameters = "\'" + selectColumnName + "_edit" + "\',\'" + (selectColumnMetaData.getFormat() == null ? "" : selectColumnMetaData.getFormat()) + "\',\'" + Double.toString(selectColumnMetaData.getMinval()) + "\',\'" + Double.toString(selectColumnMetaData.getMaxval())
							+ "\',\'" + (selectColumnMetaData.getRegexp() == null ? "" : selectColumnMetaData.getRegexp()) + "\',\'" + (selectColumnMetaData.getErrorMsg() == null ? "" : selectColumnMetaData.getErrorMsg()) + "\'";

				}

			}

			// LABEL
			htmlString.append("<td class=\"label\" nowrap>");
			if (selectColumnMetaData.getCaption() != null)
				htmlString.append(selectColumnMetaData.getCaption());
			else
				htmlString.append(selectColumnMetaData.getName());
			htmlString.append("</td>");

			// INPUT
			htmlString.append("<td class=\"input\" ><nobr>");
			if (columnMetaData.isCombo() && update) {
				// LOOKUP SQL
				// if(!columnMetaData.getName().toUpperCase().startsWith( _editorMetaData.getName().toUpperCase() + "_DESC" )){
				// StringBuffer sql = new StringBuffer();
				// sql.append( "select " );

				// if (columnMetaData.getComboSelect() != null)
				// sql.append( columnMetaData.getName() +","+ columnMetaData.getComboSelect() );
				// else
				// sql.append( columnMetaData.getName() );

				// sql.append( " from " );
				// if (columnMetaData.getComboTblName() !=null )
				// sql.append( columnMetaData.getComboTblName() );
				// else
				// throw new JspTagException( "EditTag Error: Combo TableName is not Defined for "+columnMetaData.getName());

				// String lookUpSQL = sql.toString ();
				// LOOKUP
				htmlString.append("<a href =\"");

				htmlString.append("javascript:launchLookup ( ");
				htmlString.append("'");

				if (columnMetaData.getComboSelect() != null)
					htmlString.append(columnMetaData.getComboSelect() + "_edit");
				else
					htmlString.append(columnName + "_edit");

				htmlString.append("','");
				htmlString.append(columnMetaData.getComboTblName());
				htmlString.append("','");
				if (columnMetaData.getComboOrderBy() != null)
					htmlString.append(columnMetaData.getComboOrderBy());
				else
					htmlString.append(columnName);
				htmlString.append("','");

				htmlString.append(columnName + "_edit");

				htmlString.append("','");
				htmlString.append(columnName);
				htmlString.append("') \">");

				htmlString.append("<img src=\"../images/lookup.gif\" border=0 alt=\"Lookup\"></a> ");

				// if comboselect name is different from the column name add a hidden variable

				if (columnMetaData.getComboSelect() != null) {
					if (!columnMetaData.getComboSelect().equalsIgnoreCase(columnName)) {

						htmlString.append("<input ");
						htmlString.append(" type =\"hidden\" ");
						htmlString.append(" name =\"");
						htmlString.append(columnName + "_edit");
						htmlString.append("\"");
						htmlString.append(" value =\"");
						htmlString.append(escapeHtml(columnValue));
						htmlString.append("\"");
						htmlString.append("> ");

						comboresetFunction = "document.forms[0]." + columnName + "_edit.value" + "=" + "'';";
					}
				}

				// }

				// VALIDATION
				StringBuffer validation = new StringBuffer();
				validation.append(" onChange =\"setUpdateFlag();" + comboresetFunction + "return check(" + selectCheckParameters + ")\"");
				// TEXTBOX

				htmlString.append("<input ");
				htmlString.append(" type =\"text\" ");
				htmlString.append(" name =\"");
				htmlString.append(selectColumnName + "_edit");
				htmlString.append("\"");
				htmlString.append(" value =\"");
				htmlString.append(selectColumnName);
				htmlString.append("\"");
				htmlString.append(" size =\"");
				if (selectColumnMetaData.getFormat() != null) {
					int maxLen = selectColumnMetaData.getFormatLength();
					htmlString.append(String.valueOf(maxLen + 1));
					htmlString.append("\"");
					htmlString.append(" maxlength =\"");
					htmlString.append(String.valueOf(maxLen));
					htmlString.append("\" ");
				}
				htmlString.append(validation.toString());
				htmlString.append(">");
				// DESCRIPTION
				/*
				the function EditorMetaData.isColumnValid(String) should be available soon

				                if ( ( columnMetaData.getComboSelect() != null ) && _editorMetaData.isColumnValid(columnMetaData.getComboSelect()) ) {
				                    htmlString.append( "<label class=\"infotype\" id='" );
				                    htmlString.append( columnName+"_edit_label" );
				                    htmlString.append( "'>" );
				                    htmlString.append( _resultSet.getString( columnMetaData.getComboSelect() ) );
				                    htmlString.append( "</label>" );
				                }
				*/
			}
			else if (columnMetaData.isCombo() && !update) {
				htmlString.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				// TEXTBOX WITH READONLY

				htmlString.append("<input ");
				htmlString.append(" type =\"text\" ");
				htmlString.append(" name =\"");
				htmlString.append(selectColumnName + "_readonly");
				htmlString.append("\"");
				htmlString.append(" value =\"");

				// htmlString.append( _rsIterator.getString( columnName ) );
				htmlString.append(escapeHtml(columnValue));

				htmlString.append("\"");
				if (selectColumnMetaData.getFormat() != null) {
					int maxLen = selectColumnMetaData.getFormatLength();
					htmlString.append(" size =\"");
					htmlString.append(String.valueOf(maxLen + 1));
					htmlString.append("\"");
					htmlString.append(" maxlength =\"");
					htmlString.append(String.valueOf(maxLen));
					htmlString.append("\" ");
				}
				htmlString.append("READONLY");
				htmlString.append(">");
				// DESCRIPTION
				/*
				the function EditorMetaData.isColumnValid(String) should be available soon

				                if ( ( columnMetaData.getComboSelect() != null ) && _editorMetaData.isColumnValid(columnMetaData.getComboSelect()) ) {
				                    htmlString.append ( "<label class=\"infotype\" id='" );
				                    htmlString.append ( columnName+"_label" );
				                    htmlString.append ( "'>" );
				                    htmlString.append( _rsIterator.getString( columnMetaData.getComboSelect() ) );
				                    htmlString.append ( "</label>" );
				                }
				*/
			}
			else if (update) {
				// VALIDATION
				StringBuffer validation = new StringBuffer();
				validation.append(" onChange =\"setUpdateFlag();return check(" + checkParameters + ")\"");

				if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME) {
					validation.append(" onBlur=\"isValidDate( this )\"");
					// CALENDAR
					htmlString.append("<a href= \"javascript:setUpdateFlag();javascript:launchCalendar('");
					htmlString.append(columnName + "_edit");
					htmlString.append("','" + columnMetaData.getFormat() + "')\">");
					htmlString.append("<img src=\"../images/calendar.gif\" border=0 height=\"16\" width=\"25\" alt=\"Calendar\">");
					htmlString.append("</a>");
					htmlString.append("&nbsp;");
				}
				else {
					htmlString.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				}
				// TEXTBOX
				htmlString.append("<input ");
				htmlString.append(" type =\"text\" ");
				htmlString.append(" name =\"");
				htmlString.append(columnName + "_edit");
				htmlString.append("\"");
				htmlString.append(" value =\"");

				// htmlString.append( _rsIterator.getString( columnName ) );
				htmlString.append(escapeHtml(columnValue));

				htmlString.append("\"");
				if (columnMetaData.getFormat() != null) {
					int maxLen = columnMetaData.getFormatLength();
					htmlString.append(" size =\"");
					htmlString.append(String.valueOf(maxLen + 1));
					htmlString.append("\"");
					htmlString.append(" maxlength =\"");
					htmlString.append(String.valueOf(maxLen));
					htmlString.append("\" ");
				}
				htmlString.append(validation.toString());
				htmlString.append(">");
			}
			else {
				htmlString.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				// TEXTBOX WITH READONLY
				htmlString.append("<input ");
				htmlString.append(" type =\"text\" ");
				htmlString.append(" name =\"");
				htmlString.append(columnName + "_readonly");
				htmlString.append("\"");
				htmlString.append(" value =\"");

				// htmlString.append( _rsIterator.getString( columnName ) );
				htmlString.append(escapeHtml(columnValue));

				htmlString.append("\"");
				if (columnMetaData.getFormat() != null) {
					int maxLen = columnMetaData.getFormatLength();
					htmlString.append(" size =\"");
					htmlString.append(String.valueOf(maxLen + 1));
					htmlString.append("\"");
					htmlString.append(" maxlength =\"");
					htmlString.append(String.valueOf(maxLen));
					htmlString.append("\" ");
				}
				htmlString.append("READONLY");
				htmlString.append(">");
			}
			// CLOSE TD
			htmlString.append("</nobr></td>");
			return htmlString.toString();
		}
		catch (Exception e) {
			System.out.println("Exception in EditTag.getTextBox: " + e.getMessage());
			throw new JspTagException(e.getMessage());
		}
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE543E01BA
	 */
	public String getViewrole() {
		return _viewrole;
	}

	/**
	 * @param aViewrole
	 * @roseuid 3EFE543E0214
	 */
	public void setViewrole(String aViewrole) {
		_viewrole = aViewrole;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3EFE543E0296
	 */
	public String getEditrole() {
		return _editrole;
	}

	/**
	 * @param aEditrole
	 * @roseuid 3EFE543E02DC
	 */
	public void setEditrole(String aEditrole) {
		_editrole = aEditrole;
	}

	private String escapeHtml(String value) {

		if (value == null)
			return null;
		// return StringEscapeUtils.escapeHtml(value);
		// as EscapeUtils doesnot encode ' as per html4 standards
		return StringEscapeUtils.escapeHtml(value).replaceAll("'", "&#39;");
	}

	// method added to make the class compilable both with jdk1.3 and jdk 1.4
	private String replaceAll(String oString, String rString, String input) {
		int index = -1;
		int length = oString.length();
		while ((index = input.indexOf(oString)) != -1)
			input = input.substring(0, index) + rString + input.substring(index + length);
		return input;
	}

	private int getTextareaRows(ColumnMetaData columnMetaData) {
		if (columnMetaData.getTextAreaRows() != null) {
			return columnMetaData.getTextAreaRows().intValue();
		}
		int noOfRows = 0;
		int maxLength = columnMetaData.getFormatLength();
		int cols = columnMetaData.getTextAreaCols() != null ? columnMetaData.getTextAreaCols().intValue() : 0;
		if (cols > 0) {
			noOfRows = maxLength / cols;
		}
		else {
			noOfRows = maxLength / (_MAX_TEXT_FIELD_SIZE / 4);
		}
		return noOfRows;
	}

	private int getTextareaCols(ColumnMetaData columnMetaData) {
		if (columnMetaData.getTextAreaCols() != null) {
			return columnMetaData.getTextAreaCols().intValue();
		}
		int noOfCols = 0;
		int maxLength = columnMetaData.getFormatLength();
		int cols = columnMetaData.getTextAreaCols() != null ? columnMetaData.getTextAreaCols().intValue() : 0;
		if (cols > 0) {
			noOfCols = cols;
		}
		else {
			noOfCols = _MAX_TEXT_FIELD_SIZE / 4;
		}
		if (noOfCols > maxLength) {
			noOfCols = maxLength;
		}
		return noOfCols;
	}
}
