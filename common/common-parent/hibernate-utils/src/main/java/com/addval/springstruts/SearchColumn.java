package com.addval.springstruts;

public class SearchColumn {
	private String columnName = null;
	private String columnOperator = "=";
	private String columnValue = null;
	private String columnOption = null;
	private String lookupImg = null;
	private String calImg = null;
	private String condition = null;

	public SearchColumn(){
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnOperator() {
		return columnOperator;
	}

	public void setColumnOperator(String operator) {
		this.columnOperator = operator;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	public String getColumnOption() {
		return columnOption;
	}

	public void setColumnOption(String columnOption) {
		this.columnOption = columnOption;
	}

	public String getLookupImg() {
		return lookupImg;
	}

	public void setLookupImg(String lookupImg) {
		this.lookupImg = lookupImg;
	}

	public String getCalImg() {
		return calImg;
	}

	public void setCalImg(String calImg) {
		this.calImg = calImg;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}

