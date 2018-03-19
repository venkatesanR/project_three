package com.addval.hibernateutils;

import org.hibernate.criterion.DetachedCriteria;

public interface CRUDCriteria {

	public DetachedCriteria getCriteria();
	
	public void setFilter(String jsonString);

	public String getFilter();

}
