package com.addval.aswtaglib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;



import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.addval.dbutils.RSIterator;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.dbutils.EJBStatement;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.springstruts.ResourceUtils;
import org.apache.struts.util.LabelValueBean;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;

public class ASWTagUtils {
	protected String newline = System.getProperty("line.separator");

	private static final int _MAX_TEXT_FIELD_SIZE = 100;
	private static final int _MAX_TEXT_SEARCH_FIELD_SIZE = 50;
	public static final String _SEARCH = "SEARCH";
	public static final String _SAVEFILTER = "SAVEFILTER";
	public static final String _LIST = "LIST";
	public static final String _EDIT = "EDIT";
	public static final String _KEY = "KEY";
	private String tagType = null; // SEARCH

	public ASWTagUtils(String tagType) {
		this.tagType = tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getSearchMandatory(ColumnMetaData columnMetaData) {
		return columnMetaData.isSearchableMandatory() ? "<span class=\"required\">*</span>" : "";
	}

	public String getUpdateMandatory(ColumnMetaData columnMetaData) {
		return !columnMetaData.isNullable() ? "<span class=\"required\">*</span>" : "";
	}

	public String getCaption(ColumnMetaData columnMetaData) {
		return columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption();
	}

	public String getCaption(PageContext pageContext, ColumnMetaData columnMetaData) throws JspException {
		String caption = ResourceUtils.columnCaption(pageContext, columnMetaData.getName(), columnMetaData.getCaptions());
		if (StrUtl.isEmptyTrimmed(caption)) {
			caption = columnMetaData.getName();
		}
		String[] captions = StringUtils.split(caption, AVConstants._COLUMN_CAPTION_DELIMITER);
		if ( captions.length == 3 ) {
			if (_SEARCH.equalsIgnoreCase(this.tagType)) {
				return captions[0];
			}
			else if (_LIST.equalsIgnoreCase(this.tagType)) {
				return captions[1];
			}
			else {
				return captions[2];
			}
		}
		return captions[0];
	}

	public int getEditSectionCount(Vector<ColumnMetaData> updatableColumns) {
		int secCount = 1;
		for (ColumnMetaData columnMetaData : updatableColumns) {
			if (!StrUtl.isEmptyTrimmed(columnMetaData.getUpdateGroup())) {
				if (Integer.parseInt(columnMetaData.getUpdateGroup()) > secCount) {
					secCount = Integer.parseInt(columnMetaData.getUpdateGroup());
				}
			}
		}
		return secCount;
	}

	public String getEditSectionTitle(PageContext pageContext, EditorMetaData editorMetaData, int secIndex, String actionDesc) throws JspException {
		String sectionTitle = null;
		String updateSectionTitles = editorMetaData.getUpdateSectionTitles();
		if (!StrUtl.isEmptyTrimmed(updateSectionTitles)) {
			String sectionTitles[] = StringUtils.split(updateSectionTitles, ",");
			if ((secIndex - 1) < sectionTitles.length) {
				 sectionTitle= sectionTitles[(secIndex - 1)];
				 String key=editorMetaData.getName()+ "."+ "Section-" + secIndex;
 				 sectionTitle = ResourceUtils.editorTitle(pageContext,key, sectionTitle);
				}
		}
		if (StrUtl.isEmptyTrimmed(sectionTitle) && secIndex == 1) {
			String secTitle = ResourceUtils.editorTitle(pageContext, editorMetaData.getName(), editorMetaData.getDesc());
			sectionTitle = secTitle + " - " + actionDesc; // If EDITOR_UPDATE_SECTION_TITLES is not configured.
		}
		return StrUtl.isEmptyTrimmed(sectionTitle) ? "Section -" + secIndex : sectionTitle;
	}

	public String getEditSectionTitle(PageContext pageContext, EditorMetaData editorMetaData, int secIndex) throws JspException {
		String sectionTitle = null;
		String updateSectionTitles = editorMetaData.getUpdateSectionTitles();
		if (!StrUtl.isEmptyTrimmed(updateSectionTitles)) {
			String sectionTitles[] = StringUtils.split(updateSectionTitles, ",");
			if ((secIndex - 1) < sectionTitles.length) {
				sectionTitle = sectionTitles[(secIndex - 1)];
			}
		}

		if (StrUtl.isEmptyTrimmed(sectionTitle))
			sectionTitle = ResourceUtils.message(pageContext, "editor.section.title", "Section -") + secIndex;
		else
			sectionTitle = ResourceUtils.message(pageContext, sectionTitle, sectionTitle);

		return sectionTitle;
	}

	public Integer getEditSectionColsPerRow(EditorMetaData editorMetaData, int secIndex) {
		int colsPerRow = 1;
		String updateColsPerRow = editorMetaData.getUpdateColsPerRow();
		if (!StrUtl.isEmptyTrimmed(updateColsPerRow)) {
			String updateColsPerRows[] = StringUtils.split(updateColsPerRow, ",");
			if ((secIndex - 1) < updateColsPerRows.length) {
				colsPerRow = Integer.parseInt(updateColsPerRows[(secIndex - 1)]);
			}
		}
		return colsPerRow > 1 ? new Integer(colsPerRow) : new Integer(1);
	}

	public ArrayList<String> getChildActions(EditorMetaData editorMetaData) {
		ArrayList<String> childActions = new ArrayList<String>();
		if (editorMetaData.hasChild() && !StrUtl.isEmptyTrimmed(editorMetaData.getChildActions())) {
			String[] childactions = StringUtils.split(editorMetaData.getChildActions(), ",");
			for (int i = 0; i < childactions.length; i++) {
				childActions.add(childactions[i]);
			}
		}
		return childActions;
	}

	public boolean isNumericColumn(ColumnMetaData columnMetaData) {
		if (columnMetaData.getType() == ColumnDataType._CDT_DOUBLE || columnMetaData.getType() == ColumnDataType._CDT_INT || columnMetaData.getType() == ColumnDataType._CDT_SHORT || columnMetaData.getType() == ColumnDataType._CDT_FLOAT || columnMetaData.getType() == ColumnDataType._CDT_LONG) {
			return true;
		}
		return false;
	}

	public boolean isSortable(ColumnMetaData columnMetaData) {
		String columnName = columnMetaData.getName();
		final String EDIT = "_EDIT";
		final String MODIFY = "_MODIFY";
		final String ADD = "_ADD";
		final String CLONE = "_CLONE";
		final String COPY = "_COPY";
		final String DELETE = "_DELETE";
		final String LOG = "_LOG";
		if ((!columnName.endsWith(EDIT) && !columnName.endsWith(MODIFY) && !columnName.endsWith(ADD) && !columnName.endsWith(CLONE) && !columnName.endsWith(COPY) && !columnName.endsWith(DELETE) && !columnName.endsWith(LOG))) {
			return true;
		}
		return false;
	}

	public boolean isDeleteColumn(ColumnMetaData columnMetaData) {
		final String DELETE = "DELETE";
		return (columnMetaData.getName().endsWith(DELETE) || columnMetaData.getName().startsWith(DELETE));
	}

	public String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return StringEscapeUtils.escapeHtml(value).replaceAll("'", "&#39;");
	}

	public String getViewHtmlControl(PageContext pageContext, EditorMetaData editorMetaData, ColumnMetaData columnMetaData, String columnValue, boolean isSecured) throws JspException, JspTagException {
		StringBuffer html = new StringBuffer();
		String columnName = columnMetaData.getName();
		if (columnMetaData.isEditKey() && !columnMetaData.isEditKeyDisplayable()) {
		}
		else if (columnMetaData.isEditKey() && isSecured) {
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {
		}
		else if (columnMetaData.getEjbResultSet() == null) {
			int textBoxSize =(columnMetaData.getTextBoxSize() != null)?columnMetaData.getTextBoxSize().intValue(): columnMetaData.getFormatLength() + 1;
			int maxTextFieldSize = Integer.parseInt(ResourceUtils.message(pageContext, editorMetaData.getName() + "." + columnMetaData.getName() + ".size", String.valueOf(_MAX_TEXT_FIELD_SIZE) ));
			if (textBoxSize < maxTextFieldSize) {
				html.append(columnValue);
			}
			else {
				html.append(getTextAreaControl(columnMetaData, addColumnSuffix(columnName), columnValue, true, null));
			}
			
		}
		else {
			html.append(getDropDownDesc(columnMetaData, columnValue));
		}
		return html.toString();
	}

	private String getDropDownDesc(ColumnMetaData columnMetaData, String columnValue) {
		String selectColumnName = columnMetaData.getName();
		if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnMetaData.getName())) {
			selectColumnName = columnMetaData.getComboSelect();
		}
		EJBResultSet comboResultSet = (EJBResultSet) columnMetaData.getEjbResultSet();
		comboResultSet.beforeFirst();
		EditorMetaData optMetaData = ((EJBResultSetMetaData) comboResultSet.getMetaData()).getEditorMetaData();
		EJBStatement stmt = new EJBStatement(comboResultSet);
		RSIterator rsItem = new RSIterator(stmt, "1", new com.addval.dbutils.RSIterAction(com.addval.dbutils.RSIterAction._FIRST), comboResultSet.getRecords().size(), comboResultSet.getRecords().size() == 0 ? 10 : comboResultSet.getRecords().size());
		Vector displayableColumns = optMetaData.getDisplayableColumns();
		String optValue = "";
		String optDesc = "";
		String optcolName = "";
		String optcolValue = "";
		ColumnMetaData optcolMetaData = null;
		while (rsItem.next()) {
			optValue = "";
			optDesc = "";
			for (int j = 0; j < displayableColumns.size(); j++) {
				optcolMetaData = (ColumnMetaData) displayableColumns.elementAt(j);
				optcolName = optcolMetaData.getName();
				optcolValue = rsItem.getString(optcolName);
				if (optcolValue == null) {
					optcolValue = "";
				}
				if (optcolName.equalsIgnoreCase(columnMetaData.getName())) {
					optValue = optcolValue;
				}
				if (optcolName.equalsIgnoreCase(selectColumnName)) {
					optDesc = optcolValue;
				}
			}
			if (optValue.equals(columnValue)) {
				return optDesc;
			}
		}
		return "";
	}

	public String getEditHtmlControl(PageContext pageContext, EditorMetaData editorMetaData, ColumnMetaData columnMetaData, String columnValue, boolean isSecured) throws JspException, JspTagException {
		StringBuffer html = new StringBuffer();
		String columnName = columnMetaData.getName();
		setTagType(ASWTagUtils._EDIT);

		if (columnMetaData.isEditKey() && !columnMetaData.isEditKeyDisplayable()) {
			setTagType(ASWTagUtils._KEY);
			html.append(getHiddenControl(columnMetaData, addColumnSuffix(columnName), columnValue));
		}
		else if (columnMetaData.isEditKey() && isSecured) {
			String columnDesc = columnValue;
			if (columnMetaData.isCombo() && columnMetaData.getEjbResultSet() != null && !StrUtl.isEmptyTrimmed(columnValue)) {
				columnDesc = getDropDownDesc(columnMetaData, columnValue);
				columnDesc = !StrUtl.isEmptyTrimmed(columnDesc) ? columnDesc : columnValue;
			}
			html.append(columnDesc).append(getHiddenControl(columnMetaData, addColumnSuffix(columnName), columnValue));
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {
			html.append(getFileControl(columnMetaData, addColumnSuffix(columnName), columnValue));
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_BOOLEAN) {
			html.append(getCheckBoxControl(columnMetaData, addColumnSuffix(columnName), columnValue));
		}
		else {
			String checkParameters = getCheckParameters(pageContext, columnMetaData, addColumnSuffix(columnName));
			String comboresetFunction = "";
			ColumnMetaData selectColumnMetaData = null;
			if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName)) {
				selectColumnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
				checkParameters = getCheckParameters(pageContext, selectColumnMetaData, addColumnSuffix(columnMetaData.getComboSelect()));
				if (columnMetaData.getEjbResultSet() == null) {
					comboresetFunction = "document.forms[0]." + addColumnSuffix(columnName) + ".value" + "=" + "'';";
				}
			}
			if (columnMetaData.getEjbResultSet() == null) {
				String onChangeFn = comboresetFunction + "setUpdateFlag();check(" + checkParameters + ")";

				int textBoxSize =(columnMetaData.getTextBoxSize() != null)?columnMetaData.getTextBoxSize().intValue(): columnMetaData.getFormatLength() + 1;
				int maxTextFieldSize = Integer.parseInt(ResourceUtils.message(pageContext, editorMetaData.getName() + "." + columnMetaData.getName() + ".size", String.valueOf(_MAX_TEXT_FIELD_SIZE) ));

				if (textBoxSize < maxTextFieldSize) {
					html.append(getTextBoxControl(pageContext, columnMetaData, addColumnSuffix(columnName), columnValue, isSecured, onChangeFn,null));
				}
				else {
					html.append(getTextAreaControl(columnMetaData, addColumnSuffix(columnName), columnValue, isSecured, onChangeFn));
				}
			}
			else {
				html.append(getDropDownControl(pageContext, columnMetaData, addColumnSuffix(columnName), columnValue));
			}

			if ((columnMetaData.isCombo() && columnMetaData.isSecured() == false && columnMetaData.getEjbResultSet() == null)) {
				String comboSelect = (columnMetaData.getComboSelect() != null) ? addColumnSuffix(columnMetaData.getComboSelect()) : "";
				html.append(getLookupControl(pageContext, columnMetaData, addColumnSuffix(columnName), comboSelect));
			}

			if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName) && columnMetaData.getEjbResultSet() == null) {
				html.append(getHiddenControl(columnMetaData, addColumnSuffix(columnName), columnValue));
			}

		}
		return html.toString();
	}

	public String getSearchHtmlControl(PageContext pageContext, EditorMetaData editorMetaData, ColumnMetaData columnMetaData, String operatorValue, String columnValue, boolean isSecured, boolean isFromLookup) throws JspException, JspTagException {
		String columnName = columnMetaData.getName();
		if (columnMetaData.getType() == ColumnDataType._CDT_NOTYPE) {
			throw new JspTagException("Error: Column Type is not Defined for " + columnName);
		}
		StringBuffer html = new StringBuffer();
		String checkParameters = getCheckParameters(pageContext, columnMetaData, addColumnSuffix(columnName));

		if (columnMetaData.getType() == ColumnDataType._CDT_CLOB || columnMetaData.getType() == ColumnDataType._CDT_BLOB || columnMetaData.getType() == ColumnDataType._CDT_STRING || columnMetaData.isCombo()) {
			String comboresetFunction = "";
			ColumnMetaData selectColumnMetaData = null;
			if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName)) {
				selectColumnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
				checkParameters = getCheckParameters(pageContext, selectColumnMetaData, addColumnSuffix(columnMetaData.getComboSelect()));
				if (columnMetaData.getEjbResultSet() == null) {
					comboresetFunction = "document.forms[0]." + addColumnSuffix(columnName) + ".value" + "=" + "'';";
				}
			}

			if (columnMetaData.getEjbResultSet() == null) {
				String onChangeFn = comboresetFunction + "check(" + checkParameters + ")";
				html.append(getTextBoxControl(pageContext, columnMetaData, addColumnSuffix(columnName), columnValue, isSecured, onChangeFn,null));
				if (!StrUtl.isEmptyTrimmed(operatorValue)) {
					html.append(getHiddenControl(columnMetaData, columnName + "operator_lookUp", operatorValue));
				}
			}
			else {
				html.append(getDropDownControl(pageContext, columnMetaData, addColumnSuffix(columnName), columnValue));
			}

			if (!isFromLookup && (columnMetaData.isCombo() && columnMetaData.isSecured() == false && columnMetaData.getEjbResultSet() == null)) {
				String comboSelect = (columnMetaData.getComboSelect() != null) ? addColumnSuffix(columnMetaData.getComboSelect()) : "";
				html.append(getLookupControl(pageContext, columnMetaData, addColumnSuffix(columnName), comboSelect));
			}

			if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName) && columnMetaData.getEjbResultSet() == null) {
				html.append(getHiddenControl(columnMetaData, addColumnSuffix(columnName), columnValue));
			}
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_LONG || columnMetaData.getType() == ColumnDataType._CDT_DOUBLE || columnMetaData.getType() == ColumnDataType._CDT_INT) {
			String onChangeFn = "check(" + checkParameters + ")";

			html.append(getNumericOperatorControl(columnMetaData.getName(), operatorValue));
			html.append(getTextBoxControl(pageContext, columnMetaData, addColumnSuffix(columnName), columnValue, isSecured, onChangeFn,"float:none;"));
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME) {
			String onChangeFn = "check(" + checkParameters + ")";
			boolean operatorControl = Boolean.valueOf( ResourceUtils.message(pageContext, columnMetaData.getName() + ".OperatorControl", "true"));
			if(operatorControl){
				html.append(getOperatorControl(columnMetaData.getName(), operatorValue));	
			}
			html.append(getTextBoxControl(pageContext, columnMetaData, addColumnSuffix(columnName), columnValue, isSecured, onChangeFn,"float:none;"));
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_BOOLEAN) {
			html.append(getCheckBoxControl(columnMetaData, addColumnSuffix(columnName), columnValue));
		}

		return html.toString();
	}

	private String getDropDownControl(PageContext pageContext, ColumnMetaData columnMetaData, String columnName, String columnValue) throws JspException {
		String selectColumnName = columnMetaData.getName();
		if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnMetaData.getName())) {
			selectColumnName = columnMetaData.getComboSelect();
		}

		StringBuffer html = new StringBuffer();
		EJBResultSet comboResultSet = (EJBResultSet) columnMetaData.getEjbResultSet();
		comboResultSet.beforeFirst();
		EditorMetaData optMetaData = ((EJBResultSetMetaData) comboResultSet.getMetaData()).getEditorMetaData();
		EJBStatement stmt = new EJBStatement(comboResultSet);
		RSIterator rsItem = new RSIterator(stmt, "1", new com.addval.dbutils.RSIterAction(com.addval.dbutils.RSIterAction._FIRST), comboResultSet.getRecords().size(), comboResultSet.getRecords().size() == 0 ? 10 : comboResultSet.getRecords().size());
		Vector displayableColumns = optMetaData.getDisplayableColumns();
		// Vector allColumns = optMetaData.getColumnsMetaData();

		html.append("<SELECT ");
		html.append("id='" + columnName + "' ");
		html.append("name='" + columnName + "' ");
		html.append(addJSEvents(columnMetaData));
		html.append(">");
		

		String blankOption = "AUTO";
		if (_EDIT.equalsIgnoreCase(this.tagType)) {
			blankOption = ResourceUtils.message(pageContext, columnMetaData.getName() + ".blankOption", "AUTO");
		}
		if (_SEARCH.equalsIgnoreCase(this.tagType)) {
			blankOption = ResourceUtils.message(pageContext, columnMetaData.getName() + ".searchBlankOption", "AUTO");
		}

		if ("AUTO".equalsIgnoreCase(blankOption)) {
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
				if (optcolValue == null) {
					optcolValue = "";
				}
				if (optcolName.equalsIgnoreCase(columnMetaData.getName())) {
					optValue = optcolValue;
				}
				if (optcolName.equalsIgnoreCase(selectColumnName)) {
					optDesc = optcolValue;
				}
			}
			selected = optValue.equals(columnValue) ? "selected" : "";

			html.append("<OPTION VALUE='" + optValue + "' " + selected + ">");
			html.append(optDesc);
			html.append("</OPTION>");
		}

		html.append("</SELECT>");

		return html.toString();
	}

	private String getTextAreaControl(ColumnMetaData columnMetaData, String columnName, String columnValue, boolean isSecured, String onChangeFn) {
		int noOfRows = 0;
		int noOfCols = 0;
		if (columnMetaData.getTextAreaRows() != null && columnMetaData.getTextAreaCols() != null) {
			noOfRows = columnMetaData.getTextAreaRows().intValue();
			noOfCols = columnMetaData.getTextAreaCols().intValue();
		}
		else {
			int maxLength = columnMetaData.getFormatLength();
			noOfRows = maxLength / (_MAX_TEXT_FIELD_SIZE / 4);
			noOfCols = _MAX_TEXT_FIELD_SIZE / 4;
			if (noOfCols > maxLength) {
				noOfCols = maxLength;
			}
		}

		StringBuffer html = new StringBuffer();
		int maxLen = columnMetaData.getFormatLength();
		html.append("<TEXTAREA ");
		html.append(" name =\"").append(columnName).append("\"");
		html.append(" id =\"").append(columnName).append("\"");
		html.append(" rows =\"").append(noOfRows).append("\"");
		html.append(" cols =\"").append(noOfCols).append("\"");
		html.append(" maxlength =\"").append(String.valueOf(maxLen)).append("\"");
		html.append(" onblur =\"" + onChangeFn + "\"");
		if ( isSecured ){
			html.append(" style='border:none;background-color:#ffffff;' readonly =\"true\" ");
		}
		html.append(">");
		html.append(columnValue);
		html.append("</TEXTAREA>");
		return html.toString();
	}

	private String getTextBoxControl(PageContext pageContext, ColumnMetaData columnMetaData, String columnName, String columnValue, boolean isSecured, String onChangeFn,String styleOvd) {
		StringBuffer html = new StringBuffer();
		String format = (columnMetaData.getFormat() == null ? "" : columnMetaData.getFormat());
		String colCaption = null;
		try {
			colCaption = ResourceUtils.columnCaption(pageContext, columnMetaData.getName(), (columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption()));
		} catch(Exception e) {
			colCaption = (columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption());
		}
		html.append("<input ");
		html.append(" name =\"").append(columnName).append("\"");
		html.append(" id =\"").append(columnName).append("\"");
		html.append(" value =\"").append(columnValue).append("\"");

		if(!StrUtl.isEmptyTrimmed(styleOvd)){
			html.append(" style =\"").append(styleOvd).append("\"");
		}

		Integer textBoxSize = null;
		Integer textBoxMaxlength = null;

		if(columnMetaData.getTextBoxSize() != null){
			textBoxSize = columnMetaData.getTextBoxSize();
			if(columnMetaData.getTextBoxMaxlength() != null){
				textBoxMaxlength = columnMetaData.getTextBoxMaxlength();
			}
		}
		else if (!StrUtl.isEmptyTrimmed(format)) {
			textBoxMaxlength= Integer.valueOf( columnMetaData.getFormatLength() );
			textBoxSize = (textBoxMaxlength > _MAX_TEXT_FIELD_SIZE ) ? Integer.valueOf(_MAX_TEXT_FIELD_SIZE) : textBoxMaxlength;
			if (_SEARCH.equalsIgnoreCase(this.tagType)) {
				textBoxSize = (textBoxSize > _MAX_TEXT_SEARCH_FIELD_SIZE ) ? Integer.valueOf(_MAX_TEXT_SEARCH_FIELD_SIZE) : textBoxSize;
			}
		}

		if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME) {
			textBoxSize +=1;
		}

		if(textBoxSize != null){
			html.append(" size =\"").append(String.valueOf(textBoxSize)).append("\"");
		}

		if(textBoxMaxlength != null){
			html.append(" maxlength =\"").append(String.valueOf(textBoxMaxlength)).append("\"");
		}

		if (isSecured) {
			html.append(" tabIndex=\"-1\"");
			html.append(" style='border:none;background-color:#ffffff;' readonly =\"true\" />");
		}
		else {
			if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME) {
				html.append(" onBlur=\"isValidDate( this, '" + format + "' , '" + colCaption + "')\"");
				html.append(" class=\"WaterMark\"");
				html.append(" WaterMarkText=\"").append(format).append("\"");
				html.append("/>");
				// CALENDAR
				// html.append("<img style=\"position:relative;right:27px;top:1px;\" src=\"../images/calendar.gif\" border=0 height=\"16\" width=\"25\" alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" onClick=\"return launchCalendar( '").append(columnName).append("','").append( format
				// ).append("',this)\" />");
				//html.append("<img tabIndex=\"-1\" src=\"../images/calendar.gif\" border=\"0\"  alt=\"Calendar\" onMouseOver=\"this.style.cursor='hand'\" onClick=\"return launchCalendar( '").append(columnName).append("','").append(format).append("',this)\" />");
			}
			else {
				if ( columnMetaData.getColumnInfo().getUnit() != null && AVConstants._TIME_FORMATS.contains(columnMetaData.getColumnInfo().getUnit()) ) {
					html.append(" class=\"WaterMark\"");
					if ( format.length() == 5 ) {
						html.append(" WaterMarkText=\"").append(AVConstants._HHMM_colon).append("\"");
					} else {
						html.append(" WaterMarkText=\"").append(AVConstants._HHMM).append("\"");
					}
				}

				html.append(" onBlur =\"" + onChangeFn + "\"");
				if (_EDIT.equalsIgnoreCase(this.tagType)) {
					html.append(" onChange=\"changeControlState(this);\"");
				}
				html.append("/>");
			}
		}
		return html.toString();
	}

	private String getOperatorControl(String columnName, String operatorValue) {
		StringBuffer html = new StringBuffer();
		html.append("<select class=\"operator\" ");
		html.append(" name =\"").append(columnName + "operator_lookUp").append("\"");
		html.append(" id =\"").append(columnName + "operator_lookUp").append("\"");
		html.append(" >");

		if (operatorValue.equals(String.valueOf(AVConstants._EQUAL))) {
			html.append(" <option value=\"" + AVConstants._EQUAL + "\" selected>" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._EQUAL_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._EQUAL + "\" >" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._EQUAL_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._NOT_EQUAL))) {
			html.append(" <option value=\"" + AVConstants._NOT_EQUAL + "\" selected>" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._NOT_EQUAL_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._NOT_EQUAL + "\" >" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._NOT_EQUAL_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._GREATER))) {
			html.append(" <option value=\"" + AVConstants._GREATER + "\" selected>" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._GREATER_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._GREATER + "\">" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._GREATER_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._GREATER_EQUAL))) {
			html.append(" <option value=\"" + AVConstants._GREATER_EQUAL + "\" selected>" + AVConstants.getHtmlCompOperatorMap().get(AVConstants._GREATER_EQUAL_SIGN));
		}
		else {
			html.append(" <option value=\"" + AVConstants._GREATER_EQUAL + "\" >" + AVConstants.getHtmlCompOperatorMap().get(AVConstants._GREATER_EQUAL_SIGN));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._LESSER))) {
			html.append(" <option value=\"" + AVConstants._LESSER + "\" selected>" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._LESSER_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._LESSER + "\">" + AVConstants.getHtmlCompOperatorMap().get(String.valueOf(AVConstants._LESSER_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._LESSER_EQUAL))) {
			html.append(" <option value=\"" + AVConstants._LESSER_EQUAL + "\" selected>" + AVConstants.getHtmlCompOperatorMap().get(AVConstants._LESSER_EQUAL_SIGN));
		}
		else {
			html.append(" <option value=\"" + AVConstants._LESSER_EQUAL + "\" >" + AVConstants.getHtmlCompOperatorMap().get(AVConstants._LESSER_EQUAL_SIGN));
		}
		html.append("</select>");
		return html.toString();
	}

	private String getNumericOperatorControl(String columnName, String operatorValue) {
		StringBuffer html = new StringBuffer();
		html.append("<select class=\"operator\" ");
		html.append(" id =\"").append(columnName + "operator_lookUp").append("\"");
		html.append(" name =\"").append(columnName + "operator_lookUp").append("\"");
		html.append(" >");

		if (operatorValue.equals(String.valueOf(AVConstants._EQUAL))) {
			html.append(" <option value=\"" + AVConstants._EQUAL + "\" selected>" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._EQUAL_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._EQUAL + "\" >" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._EQUAL_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._NOT_EQUAL))) {
			html.append(" <option value=\"" + AVConstants._NOT_EQUAL + "\" selected>" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._NOT_EQUAL_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._NOT_EQUAL + "\" >" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._NOT_EQUAL_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._GREATER))) {
			html.append(" <option value=\"" + AVConstants._GREATER + "\" selected>" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._GREATER_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._GREATER + "\">" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._GREATER_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._GREATER_EQUAL))) {
			html.append(" <option value=\"" + AVConstants._GREATER_EQUAL + "\" selected>" + AVConstants.getHtmlNumericCompOperatorMap().get(AVConstants._GREATER_EQUAL_SIGN));
		}
		else {
			html.append(" <option value=\"" + AVConstants._GREATER_EQUAL + "\" >" + AVConstants.getHtmlNumericCompOperatorMap().get(AVConstants._GREATER_EQUAL_SIGN));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._LESSER))) {
			html.append(" <option value=\"" + AVConstants._LESSER + "\" selected>" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._LESSER_SIGN)));
		}
		else {
			html.append(" <option value=\"" + AVConstants._LESSER + "\">" + AVConstants.getHtmlNumericCompOperatorMap().get(String.valueOf(AVConstants._LESSER_SIGN)));
		}

		if (operatorValue.equals(String.valueOf(AVConstants._LESSER_EQUAL))) {
			html.append(" <option value=\"" + AVConstants._LESSER_EQUAL + "\" selected>" + AVConstants.getHtmlNumericCompOperatorMap().get(AVConstants._LESSER_EQUAL_SIGN));
		}
		else {
			html.append(" <option value=\"" + AVConstants._LESSER_EQUAL + "\" >" + AVConstants.getHtmlNumericCompOperatorMap().get(AVConstants._LESSER_EQUAL_SIGN));
		}
		html.append("</select>");
		return html.toString();
	}

	public String getHiddenControl(ColumnMetaData columnMetaData, String columnName, String columnValue) {
		StringBuffer html = new StringBuffer();
		html.append("<input ");
		html.append(" type=\"hidden\" ");
		html.append(" name=\"").append(columnName).append("\"");
		html.append(" id=\"").append(columnName).append("\"");
		html.append(" value=\"").append(columnValue).append("\"");
		html.append("/>");
		return html.toString();
	}

	public String getCheckBoxControl(ColumnMetaData columnMetaData, String columnName, String columnValue) {
		String YN[] = columnMetaData.getValue().split(":");
		String checked = "";
		if (!StrUtl.isEmptyTrimmed(columnValue) && columnValue.equalsIgnoreCase(YN[0])) {
			checked = "checked";
		}
		else {
			columnValue = YN[0];
		}

		StringBuffer html = new StringBuffer();
		html.append("<input ");
		html.append(" type=\"checkbox\" ");
		html.append(" name=\"").append(columnName).append("\"");
		html.append(" id=\"").append(columnName).append("\"");
		html.append(" tabIndex=\"-1\"");
		html.append(" value=\"").append(columnValue).append("\"");
		html.append(" onClick=\"changeControlState(this);\"");
		html.append(" ").append(checked);
		html.append("/>");
		return html.toString();
	}

	public String getFileControl(ColumnMetaData columnMetaData, String columnName, String columnValue) {
		StringBuffer html = new StringBuffer();
		html.append("<input ");
		html.append(" type=\"file\"");
		html.append(" name=\"").append(columnName).append("\"");
		html.append(" id=\"").append(columnName).append("\"");
		html.append(" size=\"").append(columnMetaData.getFormatLength() + 1).append("\"");
		html.append(" maxlength=\"").append(columnMetaData.getFormatLength()).append("\"");
		html.append("/>");
		return html.toString();
	}

	private String getLookupControl(PageContext pageContext, ColumnMetaData columnMetaData, String columnName, String comboSelect) throws JspException {
		String dataDelimiter = ResourceUtils.message(pageContext, columnMetaData.getComboTblName() + ".dataDelimiter", "");
		String delimitedValuesMaxCount = ResourceUtils.message(pageContext, columnMetaData.getComboTblName() + ".delimitedValuesMaxCount", "3");
		boolean isReferenceLkp = false;
		String columnCaption = this.getCaption(pageContext, columnMetaData);
		StringBuffer html = new StringBuffer();
		html.append("<img tabIndex=\"-1\" src=\"../images/lookup.gif\" border=\"0\" alt=\"Lookup\" onMouseOver=\"this.style.cursor='hand'\" onClick=\"currObj=event.srcElement;javascript:launchModelLookup(");
		if (!StrUtl.isEmptyTrimmed(comboSelect)) {
			html.append("'").append(comboSelect).append("'");
		}
		else {
			html.append("'").append(columnName).append("'");
		}
		html.append(",'").append(columnMetaData.getComboTblName()).append("'");
		if (columnMetaData.getComboOrderBy() != null) {
			html.append(",'").append(columnMetaData.getComboOrderBy()).append("'");
		}
		else {
			html.append(",'").append(columnName).append("'");
		}
		html.append(",'").append(columnName).append("'");
		html.append(",'").append(columnCaption).append("'");
		html.append(",").append(isReferenceLkp);
		html.append(",'").append(dataDelimiter).append("'");
		html.append(",'").append(delimitedValuesMaxCount).append("'");
		html.append(");return false;\" />");
		// html.append("<img style=\"position:relative;right:18px;top:1px;\" src=\"../images/lookup.gif\" border=0 alt=\"Lookup\" /></a> ");
		return html.toString();
	}

	/*
		private String getCheckParameters(ColumnMetaData columnMetaData, String columnName) {
			String format = (columnMetaData.getFormat() == null ? "" : columnMetaData.getFormat());
			String regexp = (columnMetaData.getRegexp() == null ? "" : columnMetaData.getRegexp());
			String errorMsg = (columnMetaData.getErrorMsg() == null ? "" : columnMetaData.getErrorMsg());
			return "\'" + columnName + "\',\'" + format + "\',\'" + Double.toString(columnMetaData.getMinval()) + "\',\'" + Double.toString(columnMetaData.getMaxval()) + "\',\'" + regexp + "\',\'" + errorMsg + "\'";
		}
	*/

	private String getCheckParameters(PageContext pageContext, ColumnMetaData columnMetaData, String columnName) throws JspException, JspTagException {
		String format = (columnMetaData.getFormat() == null ? "" : columnMetaData.getFormat());
		String regexp = (columnMetaData.getRegexp() == null ? "" : columnMetaData.getRegexp());
		String errorMsg = (columnMetaData.getErrorMsg() == null ? "" : columnMetaData.getErrorMsg());
		if ( this.tagType.equals(_EDIT) ) {
			if ( columnMetaData.getEditRegExp() != null ){
				regexp = columnMetaData.getEditRegExp();
			}
			if ( columnMetaData.getEditErrorMsg() != null ){
				errorMsg = columnMetaData.getEditErrorMsg();
			}
		}
		errorMsg = ResourceUtils.columnErrorMessage(pageContext, columnMetaData.getName(), errorMsg);

		return "\'" + columnName + "\',\'" + format + "\',\'" + Double.toString(columnMetaData.getMinval()) + "\',\'" + Double.toString(columnMetaData.getMaxval()) + "\',\'" + regexp + "\',\'" + errorMsg + "\'";
	}

	private String addColumnSuffix(String columnName) {
		if (_SEARCH.equalsIgnoreCase(tagType)) {
			return columnName + "_lookUp";
		}
		else if (_EDIT.equalsIgnoreCase(tagType)) {
			return columnName + "_edit";
		}
		else if (_KEY.equalsIgnoreCase(tagType)) {
			return columnName + "_KEY";
		}
		return columnName;
	}

	public void sortString(List<String> values) {
		Collections.sort(values, new Comparator<String>() {
			public int compare(String arg1, String arg2) {
				if (arg1 == arg2) {
					return 0;
				}
				if (arg2 == null) {
					return 1;
				}
				if (arg1 == null) {
					return -1;
				}
				return arg1.toUpperCase().compareTo(arg2.toUpperCase());
			}
		});
	}

	public void sortInteger(List<Integer> values) {
		Collections.sort(values, new Comparator<Integer>() {
			public int compare(Integer arg1, Integer arg2) {
				return (arg2 > arg1 ? -1 : (arg1 == arg2 ? 0 : 1));
			}
		});
	}

	public String getGroupBoxTop(String type, String heading, String style, String boxStyleOvd) {
		StringBuffer html = new StringBuffer();
		html.append("<div class=\"").append(type).append("\"");
		if (!StrUtl.isEmptyTrimmed(boxStyleOvd)) {
			html.append(" style=\"").append(boxStyleOvd).append("\"");
		}
		html.append(">").append(newline);
		html.append("<div class=\"groupBox\"");
		if (!StrUtl.isEmptyTrimmed(style)) {
			html.append(" style=\"").append(style).append("\"");
		}
		html.append(">").append(newline);
		if (!StrUtl.isEmptyTrimmed(heading)) {
			html.append("<div class=\"boxTitle\">").append(newline);
			html.append("<div class=\"cornerTopLeft\"></div><div class=\"cornerTopRight\"></div>").append(newline);
			html.append(heading).append("</div>");
		}
		return html.toString();
	}

	public String getGroupBoxTop(String type, String heading) {
		return getGroupBoxTop(type, heading, null);
	}

	public String getGroupBoxTop(String type, String heading, String style) {
		return getGroupBoxTop(type, heading, style, null);
	}

	public String getGroupBoxBottom() {
		StringBuffer html = new StringBuffer();
		html.append("<div class=\"groupBoxFooter\"><div class=\"groupBoxFooterLeftImg\"></div><div class=\"groupBoxFooterRightImg\"></div></div>").append(newline);
		html.append("</div>").append(newline);
		html.append("</div>").append(newline);
		return html.toString();
	}

	public String getGroupBoxBottomWithoutFooter() {
		StringBuffer html = new StringBuffer();
		html.append("</div>").append(newline);
		html.append("</div>").append(newline);
		return html.toString();
	}

	public String getGroupBoxTop(String type) {
		return getGroupBoxTop(type, null);
	}

	public String getUserGroupName(HttpServletRequest request) {
		HttpSession currSession = request.getSession(false);
		Hashtable userProfile = (Hashtable) currSession.getAttribute("USER_PROFILE");
		if (userProfile.containsKey("USER_GROUPS")) {
			return (String) userProfile.get("USER_GROUPS");
		}
		return null;
	}

	/**
	 * Capitalizes the string, and inserts a space before each upper case character (or sequence of upper case characters). Thus "userId" becomes "User Id", etc. Also, converts underscore into space (and capitalizes the following word), thus "user_id" also becomes "User Id".
	 */

	public static String toUserPresentable(String id) {
		if (id == null) {
			return id;
		}
		if (id.indexOf("<") != -1) {
			return id; // Check if caption has any HTML control
		}

		StringBuilder builder = new StringBuilder(id.length() * 2);
		char[] chars = id.toCharArray();
		boolean postSpace = true;
		boolean upcaseNext = true;
		boolean upperCase =false;
		boolean ignoreChar= false;
		String currStr = null;
		for (char ch : chars) {
			if (upcaseNext) {
				builder.append(Character.toUpperCase(ch));
				upcaseNext = false;
				continue;
			}
			if (ch == '_') {
				builder.append(' ');
				upcaseNext = true;
				continue;
			}
			upperCase = Character.isUpperCase(ch);
			currStr =  builder.toString();
			ignoreChar = currStr.endsWith("/") || currStr.endsWith("-") || currStr.endsWith(":") || currStr.endsWith("(") || currStr.endsWith(")");
			if (upperCase && !postSpace && !ignoreChar) {
				builder.append(' ');
			}
			if (ch == '(' && !postSpace) {
				builder.append(' ');
			}
			builder.append(ch);
			postSpace = upperCase || ch == ' ';
		}
		return builder.toString();
	}

	public String getFormValidScript(PageContext pageContext, EditorMetaData editorMetaData) throws JspException {
		StringBuffer script = new StringBuffer();
		Vector<ColumnMetaData> updatableColumns = editorMetaData.getUpdatableColumns();
		if (updatableColumns != null) {
			String caption = null;
			String columnName = null;
			for (ColumnMetaData columnMetaData : updatableColumns) {
				caption = getCaption(pageContext, columnMetaData);
				columnName = columnMetaData.getName();
				setTagType(ASWTagUtils._EDIT);
				if (columnMetaData.isEditKey() && !columnMetaData.isEditKeyDisplayable()) {
					setTagType(ASWTagUtils._KEY);
				}
				if (columnMetaData.getComboSelect() != null) {
					columnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
				}
				String checkParameters = getCheckParameters(pageContext, columnMetaData, addColumnSuffix(columnName));
				if (columnMetaData.getType() == ColumnDataType._CDT_CLOB || columnMetaData.getType() == ColumnDataType._CDT_BLOB || columnMetaData.getType() == ColumnDataType._CDT_STRING || columnMetaData.isCombo()) {
					//String comboresetFunction = "";
					ColumnMetaData selectColumnMetaData = null;
					if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName)) {
						selectColumnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
						checkParameters = getCheckParameters(pageContext, selectColumnMetaData, addColumnSuffix(columnMetaData.getComboSelect()));
						/*
						if (columnMetaData.getEjbResultSet() == null) {
							comboresetFunction = "document.forms[0]." + addColumnSuffix(columnName) + ".value" + "=" + "'';";
						}
						*/
					}
					if (columnMetaData.getEjbResultSet() == null) {
						String onChangeFn = "check(" + checkParameters + ")";
						script.append("if(!").append(onChangeFn).append("){").append(newline);
						script.append("return false;").append(newline);
						script.append("}").append(newline);
					}
				}
				else if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME) {
					String format = (columnMetaData.getFormat() == null ? "" : columnMetaData.getFormat());
					String colCaption = (columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption());
					String onChangeFn = "isValidDate(form." + columnName + "_edit,'" + format + "','" + colCaption + "')";
					script.append("if(!").append(onChangeFn).append("){").append(newline);
					script.append("return false;").append(newline);
					script.append("}").append(newline);
				}
				else if (columnMetaData.getType() == ColumnDataType._CDT_LONG || columnMetaData.getType() == ColumnDataType._CDT_DOUBLE || columnMetaData.getType() == ColumnDataType._CDT_INT) {
					String onChangeFn = "check(" + checkParameters + ")";
					script.append("if(!").append(onChangeFn).append("){").append(newline);
					script.append("return false;").append(newline);
					script.append("}").append(newline);
				}
			}
		}		
		/*
		Vector<ColumnMetaData> comboColumns = editorMetaData.getComboColumns();
		if (comboColumns != null) {
			StringBuffer parameters = null;
			for (ColumnMetaData columnMetaData : comboColumns) {
				if (!columnMetaData.isKey() && columnMetaData.isUpdatable() && columnMetaData.getComboSelect() != null && (columnMetaData.isComboSelectTag() == false)) {
					if (!columnMetaData.getName().equalsIgnoreCase(columnMetaData.getComboSelect())) {
						parameters = new StringBuffer();
						parameters.append("'").append(columnMetaData.getComboSelect()).append("_edit','").append(columnMetaData.getComboTblName()).append("',");
						if (columnMetaData.getComboOrderBy() != null) {
							parameters.append("'").append(columnMetaData.getComboOrderBy()).append("',");
						}
						else {
							parameters.append("'").append(columnMetaData.getName()).append("',");
						}
						parameters.append("'").append(columnMetaData.getName()).append("_edit',");
						parameters.append("'").append(columnMetaData.getName()).append("',");
						parameters.append("'1'");
						script.append("if ((document.forms[0].").append(columnMetaData.getComboSelect()).append("_edit.value.length > 0) && (document.forms[0].").append(columnMetaData.getName()).append("_edit.value.length == 0)){").append(newline);
						script.append("var lkpStatus = launchLookupXml(").append(parameters).append(");").append(newline);
						script.append("if (lkpStatus == 0){").append(newline);
						String caption = this.getCaption(pageContext, columnMetaData);
						String invalid = ResourceUtils.message(pageContext, "error.invalid", "Invalid ");
						script.append("alert(\"").append(invalid).append(caption).append("\");").append(newline);
						script.append("return false;").append(newline);
						script.append("}").append(newline);
						script.append("if (lkpStatus > 1){").append(newline);

						String morethanonematch = ResourceUtils.message(pageContext, "error.morethanonematch", "More than one match found for ");
						String pleaseselectone = ResourceUtils.message(pageContext, "error.pleaseselectone", "Please select one. ");

						script.append("alert(\"").append(morethanonematch).append(caption).append(".").append(pleaseselectone).append("\");").append(newline);
						script.append("return false;").append(newline);
						script.append("}").append(newline);
						script.append("}").append(newline);
					}
				}
			}
		}
		*/
		return script.toString();
	}

	public String getSearchFormValidScript(PageContext pageContext, EditorMetaData editorMetaData, boolean isFromLookup) throws JspException {
		StringBuffer script = new StringBuffer();

		Vector<ColumnMetaData> searchableColumns = editorMetaData.getSearchableColumns();
		if (searchableColumns != null && !isFromLookup) {
			String caption = null;
			String columnName = null;

			for (ColumnMetaData columnMetaData : searchableColumns) {
				if (!columnMetaData.isBaseSearch() && !columnMetaData.isAdvancedSearch()) {
					continue;
				}
				caption = getCaption(pageContext, columnMetaData);
				columnName = columnMetaData.getName();

				if (columnMetaData.getComboSelect() != null) {
					columnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
				}
				String checkParameters = getCheckParameters(pageContext, columnMetaData, addColumnSuffix(columnName));
				if (columnMetaData.getType() == ColumnDataType._CDT_CLOB || columnMetaData.getType() == ColumnDataType._CDT_BLOB || columnMetaData.getType() == ColumnDataType._CDT_STRING || columnMetaData.isCombo()) {
					//String comboresetFunction = "";
					ColumnMetaData selectColumnMetaData = null;
					if (columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName)) {
						selectColumnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
						checkParameters = getCheckParameters(pageContext, selectColumnMetaData, addColumnSuffix(columnMetaData.getComboSelect()));
						/*
						if (columnMetaData.getEjbResultSet() == null) {
							comboresetFunction = "document.forms[0]." + addColumnSuffix(columnName) + ".value" + "=" + "'';";
						}
						*/
					}
					if (columnMetaData.getEjbResultSet() == null) {
						String onChangeFn = "check(" + checkParameters + ")";
						script.append("if(!").append(onChangeFn).append("){").append(newline);
						script.append("return false;").append(newline);
						script.append("}").append(newline);
					}
				}
				else if (columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME) {
					String format = (columnMetaData.getFormat() == null ? "" : columnMetaData.getFormat());
					String colCaption = (columnMetaData.getCaption() == null ? columnMetaData.getName() : columnMetaData.getCaption());
					String onChangeFn = "isValidDate(form." + columnName + "_lookUp,'" + format + "','" + colCaption + "')";
					script.append("if(!").append(onChangeFn).append("){").append(newline);
					script.append("return false;").append(newline);
					script.append("}").append(newline);
				}
				else if (columnMetaData.getType() == ColumnDataType._CDT_LONG || columnMetaData.getType() == ColumnDataType._CDT_DOUBLE || columnMetaData.getType() == ColumnDataType._CDT_INT) {
					String onChangeFn = "check(" + checkParameters + ")";
					script.append("if(!").append(onChangeFn).append("){").append(newline);
					script.append("return false;").append(newline);
					script.append("}").append(newline);
				}
			}
		}
		return script.toString();
	}
	
	public String getContextMenuScript(String containerId, List<LabelValueBean> contextMenu) {
		String contextMenuVar = "contextMenu" + containerId;
		String contextMenuItem = contextMenuVar + ".addItem({label: '%s',callback: function () {%s}});";

		StringBuffer out = new StringBuffer();
		out.append("var ").append(contextMenuVar).append(" = new Control.ContextMenu('").append(containerId).append("',{delayCallback:false,animation:false,leftClick: false,ietruebody: function() {return (document.compatMode && document.compatMode!= 'BackCompat')? document.documentElement : document.body;},beforeOpen: function(event){var left = Event.pointerX(event);var top = Event.pointerY(event);top -= ietruebody().scrollTop;document.elementFromPoint(left, top).click();}});");
		for (LabelValueBean labelValue : contextMenu) {
			out.append(String.format(contextMenuItem, labelValue.getLabel(), labelValue.getValue()));
		}
		return out.toString();
	}

	//TODO later We can support all input fields Note : Any duplicate events like onblur validate function. FIX : merge javascripts for that specific event. 
	private String addJSEvents(ColumnMetaData columnMetaData){

		StringBuffer events = new StringBuffer();
		if(columnMetaData.getJSEvents() != null){
			HashMap<String,String> jsEvents = columnMetaData.getJSEvents();
			String event = null;
			for (Iterator iterator = jsEvents.keySet().iterator(); iterator.hasNext();) {
				event = (String) iterator.next();
				events.append(" "+ event +"=\"javascript:" + jsEvents.get(event) + ";\"");
			}
		}
		return events.toString();
	}
}
