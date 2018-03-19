package com.addval.ui;


public class UIPageSectionAttributeDto {

	private String pageName;
	private String editorType;
	private String componentId;
	private String componentType;

	private String attributeName;
	private String attributeType;
	private String attributeDataDelimiter;
	private String attributeMap;
	private String defaultValue;
	private String attributeLabel;
	private String attributeWidth;
	private String attributeFormat;
	private String validateRegexp;
	private String validationMessages;
	private int udf;
	private int readonly;
	private int disabled;
	private String lookupLink = null;
	private String dropDownCacheName = null;

	private int tblRowIndex;
	private int tblColIndex;
	private int tblRowSpan;
	private int tblColSpan;
	private String tblLabelPos;

	private String gridColTxtAlign;
	private int gridKeyCol;
	private int gridDisplayableCol;

	private String containerStyleClass = null;
	private String controlStyleClass = null;
	private String privilegeExpr = null;

    public UIPageSectionAttributeDto(){

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

	public String getAttributeMap() {
		return attributeMap;
	}

	public void setAttributeMap(String attributeMap) {
		this.attributeMap = attributeMap;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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

	public int getUdf() {
		return udf;
	}

	public void setUdf(int udf) {
		this.udf = udf;
	}

	public int getReadonly() {
		return readonly;
	}

	public void setReadonly(int readonly) {
		this.readonly = readonly;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
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

	public int getGridKeyCol() {
		return gridKeyCol;
	}

	public void setGridKeyCol(int gridKeyCol) {
		this.gridKeyCol = gridKeyCol;
	}

	public int getGridDisplayableCol() {
		return gridDisplayableCol;
	}

	public void setGridDisplayableCol(int gridDisplayableCol) {
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
	
}
