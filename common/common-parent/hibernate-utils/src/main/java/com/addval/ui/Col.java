package com.addval.ui;

import java.io.Serializable;

import com.addval.utils.StrUtl;

public class Col implements Serializable {
	public static String newline = System.getProperty("line.separator");
	private static final long serialVersionUID = -5339718079251624306L;
	private Table table = null;
	private String colName = null;
	private String mappingName = null;
	private String colDataType = null;
	private String colDataDelimiter = null;
	private String colLabel = null;
	private String width = null;
	private String colFormat = null;
	private String labelPos = "left"; // Possible values left / top /none / hidden /right
	private int rowspan = 0;
	private int colspan = 0;
	private String validate = null;
	private String validationMessages = null;
	private boolean readonly = false;
	private boolean disabled = false;

	private String containerStyleClass = null;
	private String controlStyleClass = null;
	private String privilegeExpr = null;

	private String lookupLink = null;
	private String dropDownCacheName = null;
	private boolean udf = false;
	private String defaultValue = null;

	private String colLabelKey = null;
	private String validationMessagesKey = null;
	private String additionalParams = null;
	
	public Col() {
	}

	public Col(String colName, String colDataType, String colLabel, String width, String labelPos, String validate, boolean readonly, int rowspan, int colspan, String containerStyleClass, String controlStyleClass, String lookupLink, String dropDownCacheName, boolean isUdf, Table table) {
		setColName(colName);
		setColDataType(colDataType);
		setColLabel(colLabel);
		setWidth(width);
		setLabelPos(labelPos);
		setValidate(validate);
		setReadonly(readonly);
		setRowspan(rowspan);
		setColspan(colspan);
		setContainerStyleClass(containerStyleClass);
		setControlStyleClass(controlStyleClass);
		setLookupLink(lookupLink);
		setDropDownCacheName(dropDownCacheName);
		setUdf(isUdf);
		setTable(table);
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColDataDelimiter() {
		return colDataDelimiter;
	}

	public void setColDataDelimiter(String colDataDelimiter) {
		this.colDataDelimiter = colDataDelimiter;
	}

	public String getMappingName() {
		return mappingName;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	public String getColDataType() {
		return colDataType;
	}

	public void setColDataType(String colDataType) {
		this.colDataType = colDataType;
	}

	public String getColLabel() {
		return colLabel;
	}

	public void setColLabel(String colLabel) {
		this.colLabel = colLabel;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getColFormat() {
		return colFormat;
	}

	public void setColFormat(String colFormat) {
		this.colFormat = colFormat;
	}

	public String getLabelPos() {
		return StrUtl.isEmptyTrimmed(labelPos) ? "none" : labelPos;
	}

	public void setLabelPos(String labelPos) {
		this.labelPos = labelPos;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public String getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(String validationMessages) {
		this.validationMessages = validationMessages;
	}

	public boolean isOperator() {
		return ControlTypes._OPERATOR_TYPE.equalsIgnoreCase(getColDataType()) && !isHidden();
	}

	public boolean isLabelLeft() {
		return "left".equalsIgnoreCase(getLabelPos()) && !isHidden() && !isOperator();
	}

	public boolean isLabelRight() {
		return "right".equalsIgnoreCase(getLabelPos()) && !isHidden() && !isOperator();
	}

	public boolean isLabelTop() {
		return "top".equalsIgnoreCase(getLabelPos()) && !isHidden() && !isOperator();
	}

	public boolean isLabelNone() {
		return "none".equalsIgnoreCase(getLabelPos()) && !isHidden() && !isOperator();
	}

	public boolean isColumnSet() {
		return !StrUtl.isEmptyTrimmed(getColName());
	}

	public boolean isTableSet() {
		return (getTable() != null);
	}

	public boolean isEmptySet() {
		return !(isColumnSet() || isTableSet());
	}

	public boolean isHidden() {
		return ControlTypes._HIDDEN_TYPE.equalsIgnoreCase(getColDataType());
	}

	public boolean isEditable() {
		return !readonly;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getContainerStyleClass() {
		return StrUtl.isEmptyTrimmed(containerStyleClass) ? "content" : containerStyleClass;
	}

	public void setContainerStyleClass(String containerStyleClass) {
		this.containerStyleClass = containerStyleClass;
	}

	public String getControlStyleClass() {
		return controlStyleClass;
	}

	public void setControlStyleClass(String controlStyleClass) {
		this.controlStyleClass = controlStyleClass;
	}

	public String getPrivilegeExpr() {
		return privilegeExpr;
	}

	public void setPrivilegeExpr(String privilegeExpr) {
		this.privilegeExpr = privilegeExpr;
	}

	public boolean isLinkLookup() {
		return !StrUtl.isEmptyTrimmed(lookupLink);
	}

	public String getLookupLink() {
		return lookupLink;
	}

	public void setLookupLink(String lookupLink) {
		this.lookupLink = lookupLink;
	}

	public String getDropDownCacheName() {
		return dropDownCacheName;
	}

	public void setDropDownCacheName(String dropDownCacheName) {
		this.dropDownCacheName = dropDownCacheName;
	}

	public boolean isUdf() {
		return udf;
	}

	public void setUdf(boolean udf) {
		this.udf = udf;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getColLabelKey() {
		return colLabelKey;
	}

	public void setColLabelKey(String colLabelKey) {
		this.colLabelKey = colLabelKey;
	}

	public String getValidationMessagesKey() {
		return validationMessagesKey;
	}

	public void setValidationMessagesKey(String validationMessagesKey) {
		this.validationMessagesKey = validationMessagesKey;
	}

	public String getAdditionalParams() {
		return additionalParams;
	}

	public void setAdditionalParams(String additionalParams) {
		this.additionalParams = additionalParams;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("colName").append("=").append(colName).append(newline);
		buf.append("colDataType").append("=").append(colDataType).append(newline);
		buf.append("colDataDelimiter").append("=").append(colDataDelimiter).append(newline);
		buf.append("colLabel").append("=").append(colLabel).append(newline);
		buf.append("width").append("=").append(width).append(newline);
		buf.append("colFormat").append("=").append(colFormat).append(newline);
		buf.append("labelPos").append("=").append(labelPos).append(newline);
		buf.append("rowspan").append("=").append(rowspan).append(newline);
		buf.append("colspan").append("=").append(colspan).append(newline);
		buf.append("validate").append("=").append(validate).append(newline);
		buf.append("validationMessages").append("=").append(validationMessages).append(newline);
		buf.append("readonly").append("=").append(readonly).append(newline);
		buf.append("disabled").append("=").append(disabled).append(newline);
		buf.append("containerStyleClass").append("=").append(containerStyleClass).append(newline);
		buf.append("controlStyleClass").append("=").append(controlStyleClass).append(newline);
		buf.append("lookupLink").append("=").append(lookupLink).append(newline);
		buf.append("dropDownCacheName").append("=").append(dropDownCacheName).append(newline);
		buf.append("udf").append("=").append(udf).append(newline);
		buf.append("defaultValue").append("=").append(defaultValue).append(newline);
		buf.append("privilegeExpr").append("=").append(privilegeExpr).append(newline);
		buf.append("additionalParams").append("=").append(additionalParams).append(newline);
		return buf.toString();
	}

	public String toXML() {
		StringBuffer buf = new StringBuffer();
		buf.append("<Col");
		if (!StrUtl.isEmptyTrimmed(colName)) {
			buf.append(" colName").append("=\"").append(colName).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(mappingName)) {
			String value = mappingName.replace("\"", "'");
			buf.append(" mappingName").append("=\"").append(value).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(colDataType)) {
			buf.append(" colDataType").append("=\"").append(colDataType).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(colLabel)) {
			String value = colLabel.replace("<", "&lt;");
			value = value.replace(">", "&gt;");
			buf.append(" colLabel").append("=\"").append(value).append("\"");
		}
		if (!"left".equalsIgnoreCase(labelPos)) {
			buf.append(" labelPos").append("=\"").append(labelPos).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(defaultValue)) {
			buf.append(" defaultValue").append("=\"").append(defaultValue).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(dropDownCacheName)) {
			String value = dropDownCacheName.replace("<", "&lt;");
			value = value.replace(">", "&gt;");
			buf.append(" dropDownCacheName").append("=\"").append(value).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(width)) {
			buf.append(" width").append("=\"").append(width).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(colFormat)) {
			buf.append(" colFormat").append("=\"").append(colFormat).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(validate)) {
			String value = validate.replace("\\&", "\\&amp;");
			buf.append(" validate").append("=\"").append(value).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(validationMessages)) {
			buf.append(" validationMessages").append("=\"").append(validationMessages).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(colDataDelimiter)) {
			buf.append(" colDataDelimiter").append("=\"").append(colDataDelimiter).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(privilegeExpr)) {
			buf.append(" privilegeExpr").append("=\"").append(privilegeExpr).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(privilegeExpr)) {
			buf.append(" additionalParams").append("=\"").append(additionalParams).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(containerStyleClass)) {
			buf.append(" containerStyleClass").append("=\"").append(containerStyleClass).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(controlStyleClass)) {
			buf.append(" controlStyleClass").append("=\"").append(controlStyleClass).append("\"");
		}
		if(readonly){
			buf.append(" readonly").append("=\"").append(readonly).append("\"");
		}
		if(disabled){
			buf.append(" disabled").append("=\"").append(disabled).append("\"");
		}
		if(udf){
			buf.append(" udf").append("=\"").append(udf).append("\"");
		}
		
		if (rowspan > 0) {
			buf.append(" rowspan").append("=\"").append(rowspan).append("\"");
		}
		if (colspan > 0) {
			buf.append(" colspan").append("=\"").append(colspan).append("\"");
		}
	
		if (!StrUtl.isEmptyTrimmed(lookupLink)) {
			buf.append(">").append(newline);
			buf.append("<lookupLink><![CDATA[").append(lookupLink).append("]]></lookupLink>").append(newline);
			buf.append("</Col>").append(newline);
		}
		else {
			buf.append("/>").append(newline);
		}
		return buf.toString();
	}
}
