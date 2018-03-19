package com.addval.model;

import java.io.Serializable;
import java.util.List;

public class EntityPaging<T> implements Serializable {
	/*
	 * Total Records : 1525
	 * PageSize : 500
	 * |-----500-----|-----500-----|-----500-----|-----25-----|
	 */
	private boolean hasPrevSet = false;
	private boolean hasNextSet = false;
	private Integer currSet = null; // 1,2,3,..
	private List<T> list;

	public EntityPaging() {

	}

	public boolean isHasPrevSet() {
		return hasPrevSet;
	}

	public void setHasPrevSet(boolean hasPrevSet) {
		this.hasPrevSet = hasPrevSet;
	}

	public boolean isHasNextSet() {
		return hasNextSet;
	}

	public void setHasNextSet(boolean hasNextSet) {
		this.hasNextSet = hasNextSet;
	}

	public Integer getCurrSet() {
		return currSet;
	}

	public void setCurrSet(Integer currSet) {
		this.currSet = currSet;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}
