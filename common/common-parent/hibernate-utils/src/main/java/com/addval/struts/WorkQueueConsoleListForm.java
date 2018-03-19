package com.addval.struts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import javax.servlet.http.HttpServletRequest;
import java.util.Vector;

public class WorkQueueConsoleListForm extends ListForm {

	private Vector wqMetaDatas = null;

	public WorkQueueConsoleListForm() {

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

		if (wqMetaDatas != null)
			return wqMetaDatas.toArray();

		return null;

    }

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		super.reset( mapping, request );
	}
}
