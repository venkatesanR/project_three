package com.addval.aswtaglib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.util.LabelValueBean;

import com.addval.aswtaglib.GenericBodyTag;
import com.addval.environment.Environment;
import com.addval.springstruts.ResourceUtils;
import com.addval.utils.StrUtl;

public class MultiRowTag extends GenericBodyTag {
	public String newline = System.getProperty("line.separator");

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
	private boolean fieldsetRequired = true;

	public MultiRowTag() {
		loadDefaultProperties();
	}

	private void loadDefaultProperties() {
		_errorMessage = new Properties();
		_errorMessage.setProperty("error.msg.cant_find_bean", "Could not find bean {0} in scope {1}");
		_errorMessage.setProperty("error.msg.invalid_bean", "The bean that you gave is not a Collection : {0}");
		_errorMessage.setProperty("error.msg.illegal_access_exception_in_DataHolder", "IllegalAccessException trying to fetch property {0} on DataHolder");
		_errorMessage.setProperty("error.msg.invocation_target_exception_in_DataHolder", "InvocationTargetException trying to fetch property {0} on DataHolder");
		_errorMessage.setProperty("error.msg.nosuchmethod_exception_in_DataHolder", "NoSuchMethodException trying to fetch property {0} on Data Holder");
		_errorMessage.setProperty("error.msg.illegal_access_exception_in_formBean", "IllegalAccessException trying to fetch property {0} on Form Bean");
		_errorMessage.setProperty("error.msg.invocation_target_exception_in_formBean", "InvocationTargetException trying to fetch property {0} on Form Bean");
		_errorMessage.setProperty("error.msg.nosuchmethod_exception_in_formBean", "NoSuchMethodException trying to fetch property {0} on Form Bean");
		_errorMessage.setProperty("error.msg.duplicate_column", "field Name {0} exist more than ones in Multirow taglib : {1}");
		_errorMessage.setProperty("error.msg.heading_missing", "Table heading is missing in {1}.properies file for Column Name {0}");
		_errorMessage.setProperty("error.msg.empty_columnname", "No column name is specied to display");
	}

	public String getHtmlOut() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			getObjects();
			init();
			html.append("<Script language = 'JavaScript'>").append(newline);
			html.append("function create_").append(_name).append("() {").append(newline);
			html.append(initializeMultiRow());
			html.append(loadRows());
			html.append(_multiRowEditorName).append(".afterInitialize()").append(";").append(newline);
			html.append(newline).append("}");
			html.append(newline).append("</script>").append(newline);
			html.append(createFieldSet());
		}
		catch (Exception e) {
			html.append(e.getMessage());
		}
		return html.toString();
	}

	public int doStartTag() throws JspTagException {
		try {
			pageContext.getOut().write(getHtmlOut().toString());
			return SKIP_BODY;
		}
		catch (Exception e) {
			try {
				pageContext.getOut().write(e.getMessage());
				return SKIP_BODY;
			}
			catch (IOException io) {
				throw new JspTagException("ODMultiRow tag Error:" + io.getMessage());
			}
		}
	}

	public int doEndTag() throws JspTagException {
		return EVAL_PAGE;
	}

	private void getObjects() throws JspException {
		Object collection = lookup(pageContext, _name, "page");
		if (collection == null) {
			Object[] objs = { _formName, _scope };
			throw new JspException(formatErrorMessage("error.msg.cant_find_bean", objs));
		}
		if (collection instanceof List)
			_dataHolderList = (List) collection;
		else {
			Object[] objs = { collection };
			throw new JspException(formatErrorMessage("error.msg.invalid_bean", objs));
		}

		_formBean = lookup(pageContext, _formName, _scope);
		if (_formBean == null) {
			Object[] objs = { _formName, _scope };
			throw new JspException(formatErrorMessage("error.msg.cant_find_bean", objs));
		}

		try {
			_subsystem = (String) PropertyUtils.getSimpleProperty(_formBean, _SUBSYSTEM);
		}
		catch (IllegalAccessException e) {
			Object[] objs = { _SUBSYSTEM };
			throw new JspException(formatErrorMessage("error.msg.illegal_access_exception_in_formBean", objs));
		}
		catch (InvocationTargetException e) {
			Object[] objs = { _SUBSYSTEM };
			throw new JspException(formatErrorMessage("error.msg.invocation_target_exception_in_formBean", objs));
		}
		catch (NoSuchMethodException e) {
			Object[] objs = { _SUBSYSTEM };
			throw new JspException(formatErrorMessage("error.msg.nosuchmethod_exception_in_formBean", objs));
		}
	}

	private String formatErrorMessage(String error, Object obj[]) {
		StringBuffer html = new StringBuffer();
		html.append("<font color='red' >").append(MessageFormat.format(_errorMessage.getProperty(error), obj)).append("</font>");
		return html.toString();
	}

	private Object lookup(PageContext pageContext, String name, String scope) throws JspException {
		Object bean = null;
		if (scope == null) {
			bean = pageContext.findAttribute(name);
		}
		else if (scope.equalsIgnoreCase("page")) {
			bean = pageContext.getAttribute(name, PageContext.PAGE_SCOPE);
		}
		else if (scope.equalsIgnoreCase("request")) {
			bean = pageContext.getAttribute(name, PageContext.REQUEST_SCOPE);
		}
		else if (scope.equalsIgnoreCase("session")) {
			bean = pageContext.getAttribute(name, PageContext.SESSION_SCOPE);
		}
		else if (scope.equalsIgnoreCase("application")) {
			bean = pageContext.getAttribute(name, PageContext.APPLICATION_SCOPE);
		}
		else {
			Object[] objs = { name, scope };
			String msg = MessageFormat.format(_errorMessage.getProperty("error.msg.cant_find_bean"), objs);
			throw new JspException(msg);
		}
		return bean;
	}

	private void init() throws JspException {
		_columnList = new ArrayList();
		_uniqueId = _name.toUpperCase();
		_multiRowEditorName = "listOf" + _uniqueId;
		_prop = Environment.getInstance(_subsystem).getCnfgFileMgr().getProperties();

		if (_deleteOption != null && _deleteOption.equalsIgnoreCase(_FALSE)) {
			_deleteIconNeeded = false;
		}
		if (_addOption != null && _addOption.equalsIgnoreCase(_FALSE)) {
			_addIconNeeded = false;
		}
		if (_headingOption != null && _headingOption.equalsIgnoreCase(_FALSE)) {
			_headingNeeded = false;
		}
		if (_rowMoveOption != null && _rowMoveOption.equalsIgnoreCase(_TRUE)) {
			_rowMoveNeeded = true;
		}
		if (_addButtonOutsideFieldset != null && _addButtonOutsideFieldset.equalsIgnoreCase(_TRUE)) {
			_isAddButtonOutsideFieldset = true;
		}
		if (readOnly != null && readOnly.equalsIgnoreCase(_TRUE)) {
			allReadOnly = true;
		}
		else {
			allReadOnly = false;
		}
		StringTokenizer tokens = new StringTokenizer(_fieldNames, ",");
		String str = null;
		while (tokens.hasMoreTokens()) {
			str = ((String) tokens.nextElement()).trim();
			if (_columnList.contains(str)) {
				Object[] objs = { str, _subsystem };
				throw new JspException(formatErrorMessage("error.msg.duplicate_column", objs));
			}
			_columnList.add(str);
		}
		_totalColumnList = new ArrayList(_columnList);
		List addditionlColumnList = additionalColumnDetails();
		for (int i = 0; i < addditionlColumnList.size(); i++) {
			str = (String) addditionlColumnList.get(i);
			if (_totalColumnList.contains(str)) {
				Object[] objs = { str, _subsystem };
				throw new JspException(formatErrorMessage("error.msg.duplicate_column", objs));
			}
			_totalColumnList.add(str);
		}
		if (_totalColumnList.size() == 0) {
			throw new JspException(formatErrorMessage("error.msg.empty_columnname", null));
		}
	}

	protected String initializeMultiRow() {
		StringBuffer html = new StringBuffer();
		html.append(_multiRowEditorName).append(" = new MultiRowEditor ('");
		html.append(_uniqueId).append(_FIELD_AREA).append("','");
		html.append(_uniqueId).append(_ROW).append("',");
		html.append(" new Array ( ");
		html.append(getFieldColumnIds());
		html.append(" ), ");
		html.append(" new Array ( ");
		html.append(getFieldColumnNames());
		html.append(" ) )").append(";").append(newline);
		if (_deleteIconNeeded && !allReadOnly) {
			html.append(_multiRowEditorName).append(".deleteName = '").append(_uniqueId).append(_DELETE).append("'").append(";").append(newline);
		}
		html.append(_multiRowEditorName).append(".sizeFieldId = '").append(_uniqueId).append(_ROW_SIZE).append("'").append(";").append(newline);
		html.append(_multiRowEditorName).append(".fieldNamePrefix = '").append(_fieldNamePrefix).append("'").append(";").append(newline);
		if (_addIconNeeded && !allReadOnly) {
			html.append(_multiRowEditorName).append(".registerAddControl (document.all.").append(_uniqueId).append(_ADD).append(")").append(";").append(newline);
		}

		if (_rowMoveNeeded && !allReadOnly) {
			html.append(_multiRowEditorName).append(".moveUpName = '").append(_uniqueId).append(_MOVEUP).append("'").append(";").append(newline);
			html.append(_multiRowEditorName).append(".moveDownName = '").append(_uniqueId).append(_MOVEDOWN).append("'").append(";").append(newline);
		}
		return html.toString();
	}

	protected String getFieldColumnIds() {
		StringBuffer html = new StringBuffer();
		for (int i = 0; i < _totalColumnList.size(); i++) {
			if (i != 0) {
				html.append(",");
			}
			html.append(" '").append(_uniqueId).append(((String) _totalColumnList.get(i)).toUpperCase()).append("'");
		}
		return html.toString();
	}

	protected String getFieldColumnNames() {
		StringBuffer html = new StringBuffer();
		for (int i = 0; i < _totalColumnList.size(); i++) {
			if (i != 0) {
				html.append(",");
			}
			html.append(" '").append(_totalColumnList.get(i)).append("'");
		}
		return html.toString();
	}

	protected String loadRows() throws JspException {
		StringBuffer html = new StringBuffer();
		for (int i = 0; i < _dataHolderList.size(); i++) {
			Object bean = _dataHolderList.get(i);
			html.append(_multiRowEditorName).append(".loadElement ( new Array (");
			html.append(getFieldColumnValues(bean));
			boolean isNonEditable = false;
			boolean isNonDeletable = false;
			try {
				isNonEditable = ((Boolean) PropertyUtils.getSimpleProperty(bean, "nonEditable")).booleanValue();
			}
			catch (Exception e) {
			}
			try {
				isNonDeletable = ((Boolean) PropertyUtils.getSimpleProperty(bean, "nonDeletable")).booleanValue();
			}
			catch (Exception e) {
			}
			html.append(" ), ").append(isNonEditable).append(", ").append(isNonDeletable).append(");").append(newline);
		}
		return html.toString();
	}

	protected String getFieldColumnValues(Object holder) throws JspException {
		StringBuffer html = new StringBuffer();
		for (int i = 0; i < _totalColumnList.size(); i++) {
			if (i != 0) {
				html.append(",");
			}
			String columnName = (String) _totalColumnList.get(i);
			String value = null;
			try {
				Object obj = PropertyUtils.getSimpleProperty(holder, columnName);
				value = (obj == null) ? "" : obj.toString();
			}
			catch (IllegalAccessException e) {
				Object[] objs = { columnName };
				throw new JspException(formatErrorMessage("error.msg.illegal_access_exception_in_DataHolder", objs));
			}
			catch (InvocationTargetException e) {
				Object[] objs = { columnName };
				throw new JspException(formatErrorMessage("error.msg.invocation_target_exception_in_DataHolder", objs));
			}
			catch (NoSuchMethodException e) {
				Object[] objs = { columnName };
				throw new JspException(formatErrorMessage("error.msg.nosuchmethod_exception_in_DataHolder", objs));
			}

			html.append(" \"").append(value).append("\"");
		}
		return html.toString();
	}

	protected String createFieldSet() throws JspException {
		StringBuffer html = new StringBuffer();
		_imagesPath = _prop.getProperty(_name + "." + "imagePath", "../images");
		_lookupImage = _prop.getProperty(_name + "." + "lookupImage", "lookup.gif");

		boolean hasFieldset = false;
		String fieldsetCaption = "";
		if (getFieldsetRequired()) {
			try{
				fieldsetCaption = ResourceUtils.message(pageContext, _name + "." + "fieldset.caption", _prop.getProperty(_name + "." + "fieldset.caption", ""));
			}
			catch(Exception e){
				fieldsetCaption = _prop.getProperty(_name + "." + "fieldset.caption", "");
			}
			hasFieldset = (fieldsetCaption.length() != 0);
		}

		html.append("<input type='hidden' name='").append(_fieldNamePrefix).append(".size' id='").append(_uniqueId).append(_ROW_SIZE).append("' value='0'/>");
		StringBuffer tempHtml = new StringBuffer();

		tempHtml.append("<fieldset title='").append(fieldsetCaption).append("' class='fieldset1'>").append("<legend class='centerlabel'>").append(fieldsetCaption).append("</legend>");
		if (hasFieldset && !_isAddButtonOutsideFieldset) {
			html.append(tempHtml);
		}
		html.append("<table cellpadding='0'><tr><td>");
		if (hasFieldset && _isAddButtonOutsideFieldset) {
			html.append(tempHtml);
		}
		html.append("<table class='listTable MultiRowEditor' cellpadding='0' cellSpacing='0' ");
		if (!hasFieldset) {
			html.append(" class=fieldset1 ");
		}
		html.append(">");
		// Table Heading displayed will be decided by _headingNeeded boolean value
		if (_headingNeeded) {
			html.append("<thead>");
			html.append("<tr>");
			if (_rowMoveNeeded && !allReadOnly) {
				html.append("<th>&nbsp;</th>");
			}
			html.append(getTableHeadings());
			if (_deleteIconNeeded && !allReadOnly) {
				html.append("<th><div>&nbsp;</div></th>");
			}
			html.append("</tr>");
			html.append("</thead>");
		}
		html.append("<tbody id='").append(_uniqueId).append(_FIELD_AREA).append("' cellspacing='0' cellpadding='0'>");
		html.append("<tr id='").append(_uniqueId).append(_ROW).append("'>");
		html.append(addColumnElement());
		int addButtonColspan = _noOfHeadings;
		if (_deleteIconNeeded) {
			addButtonColspan += 1;
		}

		// Delete icon display will be decided by _deleteIconNeeded boolean value
		if (_deleteIconNeeded && !allReadOnly) {
			html.append("<td>");
			html.append("<img name='").append(_uniqueId).append(_DELETE).append("' src='").append(_imagesPath).append("/delete.gif' border=\"0\" alt='Delete' style='cursor:pointer'>");
			html.append("</td>");
		}
		html.append("</tr>");
		html.append("</tbody>");
		html.append("</table>");
		if (hasFieldset && _isAddButtonOutsideFieldset) {
			html.append("</fieldset>");
		}
		html.append("</td></tr>");
		// Row Adding icon display will be decided by _addIconNeeded boolean value
		if (_addIconNeeded && !allReadOnly) {
			String addNewText = ResourceUtils.message(pageContext, "editor.list.addnew", "Add New");
			html.append("<tr>");
			html.append("<td class=\"addnew\" colspan='").append(addButtonColspan).append("'>");
			html.append("<div class=\"buttonGrayOnGray\" id=\""+ _uniqueId + _ADD +"\">");
			html.append("<a href=\"#\" onclick=\"return false;\">"+ addNewText +"</a>");
			html.append("<div class=\"rightImg\" id=\"rightImgaddnew\"></div></div>");
			html.append("</td>");
			html.append("</tr>");
		}
		html.append("</table>");
		if (hasFieldset && !_isAddButtonOutsideFieldset) {
			html.append("</fieldset>");
		}
		return html.toString();
	}

	protected String addColumnElement() throws JspException {
		StringBuffer html = new StringBuffer();
		String columnName = null;
		// code to include row move control
		if (_rowMoveNeeded && !allReadOnly) {
			html.append("<td><table><tr><td valign='top' style='cursor:hand' >").append("<img name='").append(_uniqueId).append(_MOVEUP).append("' src='").append(_imagesPath).append("/up_arrow.gif' border=0 alt='Move Up'>&nbsp;").append("</td></tr>");
			html.append("<tr><td>").append("<img name='").append(_uniqueId).append(_MOVEDOWN).append("' src='").append(_imagesPath).append("/down_arrow.gif' border=0 alt='Move Down'>&nbsp;").append("</td></tr></table></td>");
		}
		boolean isFirstNonHiddenTd = true;
		String hiddenColumnName = null;
		String hiddenType = null;
		for (int i = 0; i < _columnList.size(); i++) {
			columnName = (String) _columnList.get(i);
			String type = _prop.getProperty(_name + "." + columnName + ".field.type", "TEXT").trim();
			if (!type.equalsIgnoreCase("HIDDEN")) {
				html.append("<td class='label'>");
			}

			// Let us dump all the hidden types of this multi row in the first TD of the row.
			if (isFirstNonHiddenTd && !type.equalsIgnoreCase("HIDDEN")) {
				for (int j = 0; j < _columnList.size(); j++) {
					hiddenColumnName = (String) _columnList.get(j);
					hiddenType = _prop.getProperty(_name + "." + hiddenColumnName + ".field.type", "TEXT").trim();
					if (hiddenType.equalsIgnoreCase("HIDDEN")) {
						html.append(addHidden(hiddenColumnName));
					}
				}
				isFirstNonHiddenTd = false;
			}
			if (type.equalsIgnoreCase("SELECT")) {
				html.append(addSelect(columnName));
			}
			else if (type.equalsIgnoreCase("CHECKBOX")) {
				html.append(addCheckbox(columnName));
			}
			else if (type.equalsIgnoreCase("TEXTAREA")) {
				html.append(addTextArea(columnName));
			}
			else if (type.equalsIgnoreCase("IMAGE")) {
				html.append(addImage(columnName));
			}
			else if (type.equalsIgnoreCase("DATE")) {
				html.append(addDateField(columnName));
			}
			else if (!type.equalsIgnoreCase("HIDDEN")) {
				html.append(addTextField(columnName));
			}
			if (!type.equalsIgnoreCase("HIDDEN")) {
				html.append("</td>");
			}
		}
		html.append(additionalColumnHtmlTemplate());
		return html.toString();
	}

	protected String getTableHeadings() throws JspException {
		StringBuffer html = new StringBuffer();
		// String lastHeading = "";
		int columnSize = _totalColumnList.size();
		boolean hiddenField = false;
		for (int i = 0; i < columnSize; i++) {
			String columnName = (String) _totalColumnList.get(i);
			String heading = "";
			try{
				heading = ResourceUtils.message(pageContext, _name + "." + columnName + ".caption", _prop.getProperty(_name + "." + columnName + ".caption", "").trim());
			}catch(Exception e){
				heading = _prop.getProperty(_name + "." + columnName + ".caption", "").trim();
			}
			hiddenField = heading.equalsIgnoreCase("hidden");
			if (heading.length() == 0) {
				throw new JspException(formatErrorMessage("error.msg.heading_missing", new Object[] { columnName, _subsystem }));
			}
			String required = "";
			if (_prop.getProperty(_name + "." + columnName + ".column.required", "false").equalsIgnoreCase("true")) {
				required = "<span class=\"required\">" + _prop.getProperty("mandatory.symbol", "*") + "</span>";
			}
			if (!hiddenField) {
				html.append(addHeading(heading, required));
				_noOfHeadings++;
				// lastHeading = heading;
			}
		}
		return html.toString();
	}

	private String addHeading(String heading, String required) {
		StringBuffer html = new StringBuffer();
		html.append("<th><nobr><div>").append(required).append(heading).append("<div></nobr></th>");
		return html.toString();
	}

	protected String addHidden(String columnName) {
		StringBuffer html = new StringBuffer();
		String value = _prop.getProperty(_name + "." + columnName + ".field.value", "").trim();
		html.append("<input type='hidden' name='").append(getFieldNamePrefix()).append("[0].").append(columnName).append("' id='").append(getUniqueId()).append(columnName.toUpperCase()).append("' value='").append(value).append("'>");
		return html.toString();
	}

	protected String addSelect(String columnName) throws JspException {
		StringBuffer html = new StringBuffer();
		List comboOptions = null;
		String selectOptionsProperty = _prop.getProperty(_name + "." + columnName + ".field.collectionList", columnName + "s").trim();
		String selectedValue = "";
		String textOnChange = _prop.getProperty(_name + "." + columnName + ".field.onChange", "").trim();
		try {
			comboOptions = (List) PropertyUtils.getSimpleProperty(_formBean, selectOptionsProperty);
		}
		catch (IllegalAccessException e) {
			Object[] objs = { selectOptionsProperty };
			throw new JspException(formatErrorMessage("error.msg.illegal_access_exception_in_formBean", objs));
		}
		catch (InvocationTargetException e) {
			Object[] objs = { selectOptionsProperty };
			throw new JspException(formatErrorMessage("error.msg.invocation_target_exception_in_formBean", objs));
		}
		catch (NoSuchMethodException e) {
			Object[] objs = { selectOptionsProperty };
			throw new JspException(formatErrorMessage("error.msg.nosuchmethod_exception_in_formBean", objs));
		}

		String lookup = "";
		try{
			lookup = ResourceUtils.message(pageContext, _name + "." + columnName + ".lookup", _prop.getProperty(_name + "." + columnName + ".lookup", "").trim());
		}catch(Exception e){
			lookup = _prop.getProperty(_name + "." + columnName + ".lookup", "").trim();
		}
		String lookupOnClick = _prop.getProperty(_name + "." + columnName + ".lookup.onclick", "").trim();

		if (!StrUtl.isEmptyTrimmed(lookup) && !allReadOnly) {
			html.append("<a ");
			if (lookupOnClick.length() > 0) {
				html.append("onclick = \"").append(lookupOnClick).append("\" ");
			}
			html.append(" href=\"javascript:").append(lookup).append("\"> <img src='").append(_imagesPath).append("/").append(_lookupImage).append("' border=0 alt='Lookup'></a>");
		}
		html.append("<select name='").append(_fieldNamePrefix).append("[0].").append(columnName).append("' id='").append(_uniqueId).append(columnName.toUpperCase()).append("'");
		if (new Boolean(_prop.getProperty(_name + "." + columnName + ".field.readOnly", "FALSE").trim()).booleanValue() || allReadOnly) {
			html.append(" disabled ");
		}
		if (textOnChange.length() != 0) {
			html.append(" onChange=\"").append(textOnChange).append("\"");
		}
		html.append(">");
		try {
			selectedValue = _prop.getProperty(_name + "." + columnName + ".field.select", "").trim();
		}
		catch (Exception e) {
		}
		for (int i = 0; i < comboOptions.size(); i++) {
			LabelValueBean labelValue = (LabelValueBean) comboOptions.get(i);
			html.append("<option value =\"").append(labelValue.getValue()).append("\"");
			if (labelValue.getValue().equalsIgnoreCase(selectedValue))
				html.append(" selected ");
			html.append(">").append(labelValue.getLabel()).append("</options>");
		}
		html.append("</select>");
		return html.toString();
	}

	protected String addCheckbox(String columnName) {
		StringBuffer html = new StringBuffer();
		html.append("<input type='checkbox' name='").append(_fieldNamePrefix).append("[0].").append(columnName).append("' id='").append(_uniqueId).append(columnName.toUpperCase()).append("' ");
		if (new Boolean(_prop.getProperty(_name + "." + columnName + ".field.readOnly", "FALSE").trim()).booleanValue() || allReadOnly) {
			html.append(" disabled ");
		}

		if (new Boolean(_prop.getProperty(_name + "." + columnName + ".field.checked", "FALSE").trim()).booleanValue()) {
			html.append(" checked=true ");
		}
		String onClickEvent = _prop.getProperty(_name + "." + columnName + ".field.onclick", "").trim();
		if (onClickEvent.length() > 0) {
			html.append("onclick = \"").append(onClickEvent).append("\" ");
		}
		html.append(">");
		return html.toString();
	}

	protected String addTextField(String columnName) {
		StringBuffer html = new StringBuffer();
		String lookup = "";
		try {
			lookup = ResourceUtils.message(pageContext, _name + "." + columnName + ".lookup", _prop.getProperty(_name + "." + columnName + ".lookup", "").trim());
		} catch(Exception e) {
			lookup = _prop.getProperty(_name + "." + columnName + ".lookup", "").trim();
		}
		String maxLength = _prop.getProperty(_name + "." + columnName + ".field.maxlength", "20").trim();
		String size = _prop.getProperty(_name + "." + columnName + ".field.size", "20").trim();
		String value = _prop.getProperty(_name + "." + columnName + ".field.value", "").trim();
		String textOnBlur = "";
		try {
			textOnBlur = ResourceUtils.message(pageContext, _name + "." + columnName + ".field.onblur", _prop.getProperty(_name + "." + columnName + ".field.onblur", "").trim());
		} catch(Exception e) {
			textOnBlur = _prop.getProperty(_name + "." + columnName + ".field.onblur", "").trim();
		}
		String textOnKeyUp = "";
		try {
			textOnKeyUp = ResourceUtils.message(pageContext, _name + "." + columnName + ".field.onkeyup", _prop.getProperty(_name + "." + columnName + ".field.onkeyup", "").trim());
		} catch(Exception e) {
			textOnKeyUp = _prop.getProperty(_name + "." + columnName + ".field.onkeyup", "").trim();
		}
		String textOnChange = "";
		try {
			textOnChange = ResourceUtils.message(pageContext, _name + "." + columnName + ".field.onchange", _prop.getProperty(_name + "." + columnName + ".field.onchange", "").trim());
		} catch(Exception e) {
			textOnChange = _prop.getProperty(_name + "." + columnName + ".field.onchange", "").trim();
		}
		String lookupOnClick = _prop.getProperty(_name + "." + columnName + ".lookup.onclick", "").trim();
		String style = _prop.getProperty(_name + "." + columnName + ".field.style", "").trim();
		String styleClass = _prop.getProperty(_name + "." + columnName + ".field.styleClass", "").trim();
		boolean isReadOnly = _prop.getProperty(_name + "." + columnName + ".field.readOnly", "FALSE").trim().equalsIgnoreCase("true") || allReadOnly;
		String eventControl = _prop.getProperty(_name + "." + columnName + ".field.eventControl", "").trim();

		html.append("<input type='text' name='").append(_fieldNamePrefix).append("[0].").append(columnName).append("' id='").append(_uniqueId).append(columnName.toUpperCase()).append("' value='").append(value).append("' size=").append(size).append(" maxlength=").append(maxLength);
		if (styleClass.length() != 0) {
			html.append(" class=").append(styleClass);
		}
		if (isReadOnly) {
			html.append(" readonly ");
			style = style.length() != 0 ? ";background-color:#CCCCCC;" : "background-color:#CCCCCC;";
		}
		if (style.length() != 0) {
			html.append(" style='").append(style).append("' ");
		}
		if (textOnBlur.length() != 0) {
			html.append(" onblur=\"").append(textOnBlur).append("\"");
		}
		if (textOnKeyUp.length() != 0) {
			html.append(" onkeyup=\"").append(textOnKeyUp).append("\"");
		}
		if (textOnChange.length() != 0) {
			html.append(" onchange=\"").append(textOnChange).append("\"");
		}
		if (eventControl.length() != 0) {
			html.append(" ").append(eventControl).append(" ");
		}
		html.append(">");

		if (lookup != null && lookup.trim().length() != 0 && !allReadOnly) {
			html.append("<a ");
			if (lookupOnClick.length() > 0)
				html.append("onclick = \"").append(lookupOnClick).append("\" ");
			html.append(" href=\"javascript:").append(lookup).append("\" tabindex='-1' ><img src='").append(_imagesPath).append("/").append(_lookupImage).append("' border='0' alt='Lookup' ></a>");
		}

		return html.toString();
	}

	protected String addTextArea(String columnName) {
		StringBuffer html = new StringBuffer();
		String value = _prop.getProperty(_name + "." + columnName + ".field.value", "").trim();
		String cols = _prop.getProperty(_name + "." + columnName + ".field.cols", "25").trim();
		String rows = _prop.getProperty(_name + "." + columnName + ".field.rows", "2").trim();
		String style = _prop.getProperty(_name + "." + columnName + ".field.style", "").trim();
		String styleClass = _prop.getProperty(_name + "." + columnName + ".field.styleClass", "").trim();
		boolean isReadOnly = _prop.getProperty(_name + "." + columnName + ".field.readOnly", "FALSE").trim().equalsIgnoreCase("true") || allReadOnly;
		String eventControl = _prop.getProperty(_name + "." + columnName + ".field.eventControl", "").trim();
		html.append("<textarea name='").append(_fieldNamePrefix).append("[0].").append(columnName).append("' id='").append(_uniqueId).append(columnName.toUpperCase()).append("' cols=").append(cols).append(" rows=").append(rows);
		if (styleClass.length() != 0) {
			html.append(" class=").append(styleClass);
		}
		if (isReadOnly) {
			html.append(" readonly ");
			style = style.length() != 0 ? ";background-color:#CCCCCC;" : "background-color:#CCCCCC;";
		}
		if (style.length() != 0) {
			html.append(" style='").append(style).append("' ");
		}
		if (eventControl.length() != 0) {
			html.append(" ").append(eventControl).append(" ");
		}
		html.append(">").append(value).append("</textarea>");
		return html.toString();
	}

	protected String addImage(String columnName) {
		StringBuffer html = new StringBuffer();
		String eventControl = _prop.getProperty(_name + "." + columnName + ".field.eventControl", "").trim();
		String imageFile = _prop.getProperty(_name + "." + columnName + ".image.fileName", "").trim();

		html.append("<img src='").append(getImagesPath()).append("/").append(imageFile).append("' id='").append(getUniqueId()).append(columnName.toUpperCase()).append("' border=0 ");

		if (eventControl.length() != 0) {
			html.append(" ").append(eventControl).append(" ");
		}
		html.append(" >");
		return html.toString();
	}

	protected String addDateField(String columnName) {
		StringBuffer html = new StringBuffer();
		String dateFormat = _prop.getProperty(_name + "." + columnName + ".dateformat", _DEFAULT_DATE_FORMAT).trim();
		String value = _prop.getProperty(_name + "." + columnName + ".field.value", "").trim();
		boolean isReadOnly = _prop.getProperty(_name + "." + columnName + ".field.readOnly", "FALSE").trim().equalsIgnoreCase("true") || allReadOnly;
		String styleClass = _prop.getProperty(_name + "." + columnName + ".field.styleClass", "").trim();
		String style = _prop.getProperty(_name + "." + columnName + ".field.style", "").trim();
		int maxLength = dateFormat.length();
		if (!isReadOnly) {
			styleClass +=" WaterMark";
		}
		html.append("<input type='text' name='").append(_fieldNamePrefix).append("[0].").append(columnName).append("' id='").append(_uniqueId).append(columnName.toUpperCase()).append("' value='").append(value).append("' size=").append(maxLength).append(" maxlength=").append(maxLength);
		if (styleClass.length() != 0) {
			html.append(" class=").append(styleClass);
		}
		html.append(" WaterMarkText=\"").append(dateFormat).append("\"");
		if (isReadOnly) {
			html.append(" readonly ");
			style = style.length() != 0 ? ";background-color:#CCCCCC;" : "background-color:#CCCCCC;";
		}
		if (style.length() != 0) {
			html.append(" style='").append(style).append("' ");
		}
		html.append(" onBlur=\"return getDisplayDateFormatByID(this,'").append(dateFormat).append("')\">");
		/*
		if (!isReadOnly) {
			html.append("<img name='CAL_").append(_fieldNamePrefix).append("[0].").append(columnName).append("' src='").append(getImagesPath()).append("/calendar.gif' border=0 height='16' width='25' alt='Calendar' onMouseOver=\"this.style.cursor='hand'\"");
			html.append(" onClick=\"return newlaunchCalendar('previous','YES','").append(dateFormat).append("')\">");
		}
		*/
		return html.toString();
	}

	protected List additionalColumnDetails() {
		return new ArrayList();
	}

	protected String additionalColumnHtmlTemplate() {
		return new String();
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

	public String getSubsystem() {
		return _subsystem;
	}

	public void setSubsystem(String aSubsystem) {
		_subsystem = aSubsystem;
	}

	public void setName(String aName) {
		_name = aName;
	}

	public void setFormName(String aFormName) {
		_formName = aFormName;
	}

	public void setScope(String aScope) {
		_scope = aScope;
	}

	public void setFieldNamePrefix(String aFieldNamePrefix) {
		_fieldNamePrefix = aFieldNamePrefix;
	}

	public void setFieldNames(String aFieldNames) throws JspException {
		_fieldNames = aFieldNames;
	}

	public void setDeleteOption(String aDeleteOption) {
		_deleteOption = aDeleteOption;
	}

	public void setAddOption(String aAddOption) {
		_addOption = aAddOption;
	}

	public void setHeadingOption(String aHeadingOption) {
		_headingOption = aHeadingOption;
	}

	public Object getFormBean() {
		return _formBean;
	}

	public List getColumnNames() {
		return _columnList;
	}

	public List getDataHolderList() {
		return _dataHolderList;
	}

	public Properties getProperties() {
		return _prop;
	}

	public String getUniqueId() {
		return _uniqueId;
	}

	public String getFieldNamePrefix() {
		return _fieldNamePrefix;
	}

	public String getImagesPath() {
		return _imagesPath;
	}

	public String getMultiRowEditorName() {
		return _multiRowEditorName;
	}

	public void setRowMoveOption(String aRowMoveOption) {
		_rowMoveOption = aRowMoveOption;
	}

	public void setAddButtonOutsideFieldset(String aAddButtonOutsideFieldset) {
		_addButtonOutsideFieldset = aAddButtonOutsideFieldset;
	}

	public boolean getFieldsetRequired() {
		return fieldsetRequired;
	}

	public void setFieldsetRequired(boolean fieldsetRequired) {
		this.fieldsetRequired = fieldsetRequired;
	}

}
