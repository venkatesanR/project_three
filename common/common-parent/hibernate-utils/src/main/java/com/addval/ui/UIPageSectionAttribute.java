package com.addval.ui;

import java.io.Serializable;

public class UIPageSectionAttribute implements Serializable {

	private String pageName;
	private String editorType;
	private String componentId;
	private String componentType;

	private String attributeName;
	private String attributeType;
	private String attributeDataDelimiter;
	private String attributeDefaultValue;
	private String attributeMapping;
	private String attributeLabel;
	private String attributeWidth;
	private String attributeFormat;
	private String validateRegexp;
	private String validationMessages;
	private boolean udf = false;
	private boolean readonly = false;
	private boolean disabled = false;
	private String lookupLink = null;
	private String dropDownCacheName = null;

	private int tblRowIndex;
	private int tblColIndex;
	private int tblRowSpan;
	private int tblColSpan;
	private String tblLabelPos;

	private String gridColTxtAlign;
	private boolean gridKeyCol = false;
	private boolean gridDisplayableCol = false;

	private String containerStyleClass = null;
	private String controlStyleClass = null;
	private String privilegeExpr = null;

	public UIPageSectionAttribute() {

	}

	public UIPageSectionAttribute(UIPageSectionAttributeDto dto) {
		pageName = dto.getPageName();
		editorType = dto.getEditorType();
		componentId = dto.getComponentId();
		componentType = dto.getComponentType();

		attributeName = dto.getAttributeName();
		attributeType = dto.getAttributeType();
		attributeDataDelimiter = dto.getAttributeDataDelimiter();
		attributeMapping = dto.getAttributeMap();
		attributeDefaultValue = dto.getDefaultValue();
		attributeLabel = dto.getAttributeLabel();
		attributeWidth = dto.getAttributeWidth();
		attributeFormat = dto.getAttributeFormat();
		validateRegexp = dto.getValidateRegexp();
		validationMessages = dto.getValidationMessages();
		udf = (dto.getUdf() == 1);
		readonly = (dto.getReadonly() == 1);
		disabled = (dto.getDisabled() == 1);
		lookupLink = dto.getLookupLink();
		dropDownCacheName = dto.getDropDownCacheName();

		tblRowIndex = dto.getTblRowIndex();
		tblColIndex = dto.getTblColIndex();
		tblRowSpan = dto.getTblRowSpan();
		tblColSpan = dto.getTblColSpan();
		tblLabelPos = dto.getTblLabelPos();

		gridColTxtAlign = dto.getGridColTxtAlign();
		gridKeyCol = (dto.getGridKeyCol() == 1);
		gridDisplayableCol = (dto.getGridDisplayableCol() == 1);

		containerStyleClass = dto.getContainerStyleClass();
		controlStyleClass = dto.getControlStyleClass();
		privilegeExpr = dto.getPrivilegeExpr();
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getAttributeDataDelimiter() {
		return attributeDataDelimiter;
	}

	public void setAttributeDataDelimiter(String attributeDataDelimiter) {
		this.attributeDataDelimiter = attributeDataDelimiter;
	}

	public String getAttributeMapping() {
		return attributeMapping;
	}

	public void setAttributeMapping(String attributeMapping) {
		this.attributeMapping = attributeMapping;
	}

	public String getAttributeDefaultValue() {
		return attributeDefaultValue;
	}

	public void setAttributeDefaultValue(String attributeDefaultValue) {
		this.attributeDefaultValue = attributeDefaultValue;
	}

	public String getAttributeLabel() {
		return attributeLabel;
	}

	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	public String getAttributeWidth() {
		return attributeWidth;
	}

	public void setAttributeWidth(String attributeWidth) {
		this.attributeWidth = attributeWidth;
	}

	public String getAttributeFormat() {
		return attributeFormat;
	}

	public void setAttributeFormat(String attributeFormat) {
		this.attributeFormat = attributeFormat;
	}

	public String getValidateRegexp() {
		return validateRegexp;
	}

	public void setValidateRegexp(String validateRegexp) {
		this.validateRegexp = validateRegexp;
	}

	public String getValidationMessages() {
		return validationMessages;
	}

	public void setValidationMessages(String validationMessages) {
		this.validationMessages = validationMessages;
	}

	public boolean isUdf() {
		return udf;
	}

	public void setUdf(boolean udf) {
		this.udf = udf;
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

	public int getTblRowIndex() {
		return tblRowIndex;
	}

	public void setTblRowIndex(int tblRowIndex) {
		this.tblRowIndex = tblRowIndex;
	}

	public int getTblColIndex() {
		return tblColIndex;
	}

	public void setTblColIndex(int tblColIndex) {
		this.tblColIndex = tblColIndex;
	}

	public int getTblRowSpan() {
		return tblRowSpan;
	}

	public void setTblRowSpan(int tblRowSpan) {
		this.tblRowSpan = tblRowSpan;
	}

	public int getTblColSpan() {
		return tblColSpan;
	}

	public void setTblColSpan(int tblColSpan) {
		this.tblColSpan = tblColSpan;
	}

	public String getTblLabelPos() {
		return tblLabelPos;
	}

	public void setTblLabelPos(String tblLabelPos) {
		this.tblLabelPos = tblLabelPos;
	}

	public String getGridColTxtAlign() {
		return gridColTxtAlign;
	}

	public void setGridColTxtAlign(String gridColTxtAlign) {
		this.gridColTxtAlign = gridColTxtAlign;
	}

	public boolean isGridKeyCol() {
		return gridKeyCol;
	}

	public void setGridKeyCol(boolean gridKeyCol) {
		this.gridKeyCol = gridKeyCol;
	}

	public boolean isGridDisplayableCol() {
		return gridDisplayableCol;
	}

	public void setGridDisplayableCol(boolean gridDisplayableCol) {
		this.gridDisplayableCol = gridDisplayableCol;
	}

	public String getContainerStyleClass() {
		return containerStyleClass;
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

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(" [ ").append("attributeName").append("=").append(attributeName).append(" ] ");
		buf.append(" [ ").append("attributeType").append("=").append(attributeType).append(" ] ");
		buf.append(" [ ").append("attributeDataDelimiter").append("=").append(attributeDataDelimiter).append(" ] ");
		buf.append(" [ ").append("attributeDefaultValue").append("=").append(attributeDefaultValue).append(" ] ");
		buf.append(" [ ").append("attributeMapping").append("=").append(attributeMapping).append(" ] ");
		buf.append(" [ ").append("attributeLabel").append("=").append(attributeLabel).append(" ] ");
		buf.append(" [ ").append("attributeWidth").append("=").append(attributeWidth).append(" ] ");
		buf.append(" [ ").append("attributeFormat").append("=").append(attributeFormat).append(" ] ");
		buf.append(" [ ").append("validateRegexp").append("=").append(validateRegexp).append(" ] ");
		buf.append(" [ ").append("validationMessages").append("=").append(validationMessages).append(" ] ");
		buf.append(" [ ").append("udf").append("=").append(udf).append(" ] ");
		buf.append(" [ ").append("readonly").append("=").append(readonly).append(" ] ");
		buf.append(" [ ").append("disabled").append("=").append(disabled).append(" ] ");
		buf.append(" [ ").append("lookupLink").append("=").append(lookupLink).append(" ] ");
		buf.append(" [ ").append("dropDownCacheName").append("=").append(dropDownCacheName).append(" ] ");
		buf.append(" [ ").append("validateRegexp").append("=").append(validateRegexp).append(" ] ");

		if (UIComponentTypes.TABLE_LAYOUT.equalsIgnoreCase(getComponentType())) {
			buf.append(" [ ").append("tblLabelPos").append("=").append(tblLabelPos).append(" ] ");
			buf.append(" [ ").append("tblRowIndex").append("=").append(tblRowIndex).append(" ] ");
			buf.append(" [ ").append("tblColIndex").append("=").append(tblColIndex).append(" ] ");
			buf.append(" [ ").append("tblRowSpan").append("=").append(tblRowSpan).append(" ] ");
			buf.append(" [ ").append("tblColSpan").append("=").append(tblColSpan).append(" ] ");

		}
		if (UIComponentTypes.EXTENDED_GRID.equalsIgnoreCase(getComponentType())) {
			buf.append(" [ ").append("gridColTxtAlign").append("=").append(gridColTxtAlign).append(" ] ");
			buf.append(" [ ").append("gridKeyCol").append("=").append(gridKeyCol).append(" ] ");
			buf.append(" [ ").append("gridDisplayableCol").append("=").append(gridDisplayableCol).append(" ] ");

		}
		buf.append(" [ ").append("containerStyleClass").append("=").append(containerStyleClass).append(" ] ");
		buf.append(" [ ").append("controlStyleClass").append("=").append(controlStyleClass).append(" ] ");
		buf.append(" [ ").append("privilegeExpr").append("=").append(privilegeExpr).append(" ] ");
		return buf.toString();
	}

}
