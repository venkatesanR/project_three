package com.addval.trees;

public interface IColumnDTO {
	public String getDataAttributeColumn();
	
	public void setDataAttributeColumn(String dataAttributeColumn);
	
	public String getDataAttributeDim();

	public void setDataAttributeDim(String dataAttributeDim);
	
	public Object getValue();
	
	public void setValue(Object value);
	
	public String getDataAttributeCode();
	
	public void setDataAttributeCode(String dataAttributeCode) ;
}
