package com.addval.ui;

import java.io.Serializable;

import com.addval.utils.StrUtl;

public class SortableCol implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String colName;
	private String colLabel;
	private OrderByEnum orderByEnum;

	public SortableCol() {

	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColLabel() {
		return colLabel;
	}

	public void setColLabel(String colLabel) {
		this.colLabel = colLabel;
	}

	public OrderByEnum getOrderByEnum() {
		return orderByEnum;
	}

	public void setOrderByEnum(OrderByEnum orderByEnum) {
		this.orderByEnum = orderByEnum;
	}

	public void setOrderBy(String orderBy) {
		if (orderBy != null) {
			orderByEnum = StrUtl.equalsIgnoreCase("ASC", orderBy) ? OrderByEnum.ASC : OrderByEnum.DESC;
		}
	}

	public String getOrderBy() {
		if (orderByEnum != null) {
			return orderByEnum.name();
		}
		return null;
	}

	public SortableCol clone() {
		SortableCol newCol = new SortableCol();
		newCol.setColName(this.getColName());
		newCol.setColLabel(this.getColLabel());
		newCol.setOrderByEnum(this.getOrderByEnum());
		return newCol;
	}

}
