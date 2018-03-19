package com.addval.springstruts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

public class WorkQueueConsoleForm extends BaseForm {

	private static final long serialVersionUID = -7959998440620277175L;
	private Vector wqMetaDatas = null;

	public WorkQueueConsoleForm() {
		super();
		setWqMetaDatas(null);
	}

	public Vector getWqMetaDatas() {
		return wqMetaDatas;
	}

	public void setWqMetaDatas(Vector wqMetaDatas) {
		this.wqMetaDatas = wqMetaDatas;
	}

	public Object[] getQmetadataList() {
		if (wqMetaDatas != null){
			return wqMetaDatas.toArray();
		}
		return null;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}
}
