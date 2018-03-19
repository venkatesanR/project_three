package com.addval.utils;

public interface AVDecorator<T> {

	public void sum(T obj);

	public void avg();

	public Boolean getReadOnly();

	public void setReadOnly(boolean readOnly);

	public void reset();

	public Boolean getSubTotal();

	public void setSubTotal(boolean subTotal);

}