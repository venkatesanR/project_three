package com.addval.ui;

import java.io.Serializable;

import com.addval.utils.StrUtl;

public class GridCol implements Serializable {
	private static final long serialVersionUID = 742865048068501521L;
	public static String newline = System.getProperty("line.separator");

	private String colName = null;
	private String colDataType = null;
	private String colDataDelimiter = null;
	private String colLabel = null;
	private String width = null;
	private String colFormat = null;
	private String textAlign = "left"; // Default
	private boolean readOnly = false;
	private String validate = null;
	private String validationMessages = null;
	private boolean disabled = false;

	private boolean key = false;
	private boolean displayable = true;

	private String lookupLink = null;
	private String dropDownCacheName = null;

	private String mappingName = null;
	private boolean udf = false;
	private String defaultValue = null;

	private String controlStyleClass = null;
	private String containerStyleClass = null;

	private String privilegeExpr = null;

	private String colLabelKey = null;
	private String validationMessagesKey = null;
	private String additionalParams = null;
	private OrderByEnum orderByEnum = OrderByEnum.ASC; // Default

	public GridCol() {
	}

	public GridCol(String colName, String colDataType, String colLabel, boolean readOnly, String width, String textAlign, String validate) {
		setColName(colName);
		setColDataType(colDataType);
		setColLabel(colLabel);
		setReadOnly(readOnly);
		setWidth(width);
		setTextAlign(textAlign);
		setValidate(validate);
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColLabel() {
		return colLabel;
	}

	public String getColDataType() {
		return colDataType;
	}

	public void setColDataType(String colDataType) {
		this.colDataType = colDataType;
	}

	public String getColDataDelimiter() {
		return colDataDelimiter;
	}

	public void setColDataDelimiter(String colDataDelimiter) {
		this.colDataDelimiter = colDataDelimiter;
	}

	public void setColLabel(String colLabel) {
		this.colLabel = colLabel;
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

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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

	public String getMappingName() {
		return mappingName;
	}

	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public boolean isDisplayable() {
		return displayable;
	}

	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
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

	public String getControlStyleClass() {
		return controlStyleClass;
	}

	public void setControlStyleClass(String controlStyleClass) {
		this.controlStyleClass = controlStyleClass;
	}

	public String getContainerStyleClass() {
		return containerStyleClass;
	}

	public void setContainerStyleClass(String containerStyleClass) {
		this.containerStyleClass = containerStyleClass;
	}

	public String getPrivilegeExpr() {
		return privilegeExpr;
	}

	public void setPrivilegeExpr(String privilegeExpr) {
		this.privilegeExpr = privilegeExpr;
	}

	public String getAdditionalParams() {
		return additionalParams;
	}

	public void setAdditionalParams(String additionalParams) {
		this.additionalParams = additionalParams;
	}

	public boolean isHidden() {
		return ControlTypes._HIDDEN_TYPE.equalsIgnoreCase(getColDataType());
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

	public void setOrderBy(String orderBy) {
		if (orderBy != null) {
			orderByEnum = StrUtl.equalsIgnoreCase("ASC", orderBy) ? OrderByEnum.ASC : OrderByEnum.DESC;
		}
	}

	public String getOrderBy() {
		if (orderByEnum != null) {
			return orderByEnum.name();
		}
		return null;
	}

	public OrderByEnum getOrderByEnum() {
		return orderByEnum;
	}

	public void setOrderByEnum(OrderByEnum orderByEnum) {
		if (orderByEnum != null) {
			this.orderByEnum = orderByEnum;
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("colName").append("=").append(colName).append(newline);
		buf.append("colDataType").append("=").append(colDataType).append(newline);
		buf.append("colDataDelimiter").append("=").append(colDataDelimiter).append(newline);
		buf.append("colLabel").append("=").append(colLabel).append(newline);
		buf.append("width").append("=").append(width).append(newline);
		buf.append("colFormat").append("=").append(colFormat).append(newline);
		buf.append("textAlign").append("=").append(textAlign).append(newline);
		buf.append("readOnly").append("=").append(readOnly).append(newline);
		buf.append("disabled").append("=").append(disabled).append(newline);
		buf.append("validate").append("=").append(validate).append(newline);
		buf.append("validationMessages").append("=").append(validationMessages).append(newline);
		buf.append("key").append("=").append(key).append(newline);
		buf.append("displayable").append("=").append(displayable).append(newline);
		buf.append("lookupLink").append("=").append(lookupLink).append(newline);
		buf.append("dropDownCacheName").append("=").append(dropDownCacheName).append(newline);
		buf.append("mappingName").append("=").append(mappingName).append(newline);
		buf.append("udf").append("=").append(udf).append(newline);
		buf.append("defaultValue").append("=").append(defaultValue).append(newline);
		buf.append("controlStyleClass").append("=").append(controlStyleClass).append(newline);
		buf.append("containerStyleClass").append("=").append(containerStyleClass).append(newline);
		buf.append("privilegeExpr").append("=").append(privilegeExpr).append(newline);
		buf.append("additionalParams").append("=").append(additionalParams).append(newline);
		buf.append("orderBy").append("=").append(orderByEnum.getOrderBy()).append(newline);
		return buf.toString();
	}

	public String toXML(boolean editable) {
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
		if (!"left".equalsIgnoreCase(textAlign)) {
			buf.append(" textAlign").append("=\"").append(textAlign).append("\"");
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
			buf.append(" validate").append("=\"").append(validate).append("\"");
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
		if (!StrUtl.isEmptyTrimmed(controlStyleClass)) {
			buf.append(" controlStyleClass").append("=\"").append(controlStyleClass).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(containerStyleClass)) {
			buf.append(" containerStyleClass").append("=\"").append(containerStyleClass).append("\"");
		}
		if (editable && readOnly) {
			buf.append(" readOnly").append("=\"").append(readOnly).append("\"");
		}

		if (disabled) {
			buf.append(" disabled").append("=\"").append(disabled).append("\"");
		}
		if (udf) {
			buf.append(" udf").append("=\"").append(udf).append("\"");
		}
		if (!displayable) {
			buf.append(" displayable").append("=\"").append(displayable).append("\"");
		}
		if (key) {
			buf.append(" key").append("=\"").append(key).append("\"");
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
