package com.addval.struts;

public class UpdateColumn {
	private String columnName = null;
	private String columnOperator = "*";
	private String columnValue = null;

	public UpdateColumn(){
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
}
