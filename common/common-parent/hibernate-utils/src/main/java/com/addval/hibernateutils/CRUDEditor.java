package com.addval.hibernateutils;



public interface CRUDEditor<M> {

	public String getId();

	public void setId(String key);

	public M getBean();
	

}
