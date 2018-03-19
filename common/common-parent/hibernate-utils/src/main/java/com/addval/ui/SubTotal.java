package com.addval.ui;

import java.io.Serializable;

public class SubTotal implements Serializable {
	private static final long serialVersionUID = -2707125012242359782L;
	private String subTotalName = null;
	private boolean subTotal = false;
	
	public SubTotal(){
		
	}
	public SubTotal(String subTotalName,boolean subTotal){
		setSubTotalName( subTotalName);
		setSubTotal( subTotal );
	}
	
	public String getSubTotalName() {
		return subTotalName;
	}
	public void setSubTotalName(String subTotalName) {
		this.subTotalName = subTotalName;
	}
	public boolean isSubTotal() {
		return subTotal;
	}
	public void setSubTotal(boolean subTotal) {
		this.subTotal = subTotal;
	}
	
}
