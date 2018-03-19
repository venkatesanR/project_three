package com.addval.springstruts;

public class UpdateColumn {
	private String columnName = null;
	private String columnOperator = "*";
	private String columnValue = null;
	private String columnOption = null;	
	private String lookupImg = null;
	private String calImg = null;
	

	public UpdateColumn(){
	}
	
	public UpdateColumn(String columnName,String operator,String columnValue){
		setColumnName(columnName);
		setColumnOperator(operator);
		setColumnValue(columnValue);
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
	
}
