package com.addval.springstruts;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class WorkQueueMonitorForm extends ActionForm {
	private static final long serialVersionUID = 0L;
	private Vector wqMetaDatas;

	public WorkQueueMonitorForm() {
		wqMetaDatas = null;
	}

	public Vector getWqMetaDatas() {
		return wqMetaDatas;
	}

	public void setWqMetaDatas(Vector wqMetaDatas) {
		this.wqMetaDatas = wqMetaDatas;
	}

	public void reset(ActionMapping actionmapping, HttpServletRequest httpservletrequest) {
	}
}
