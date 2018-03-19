package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;

public class WorkQueueFormInterceptor extends DefaultFormInterceptor {

	private static final long serialVersionUID = -6323460440305919766L;

	public WorkQueueFormInterceptor() {
		super();
	}

	public void beforeDataLookup(String actiontype, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, EJBCriteria ejbCriteria) {
		super.beforeDataLookup(actiontype, mapping, form, request, response, ejbCriteria);
	}

}
