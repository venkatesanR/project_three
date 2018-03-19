package com.addval.aswtaglib;

import javax.servlet.jsp.JspTagException;

public class PageContextTag extends GenericBodyTag {
	private Object object;

	public void setObject(Object object) {
		this.object = object;
	}

	public int doEndTag() throws JspTagException {
		if (getId() != null) {
			pageContext.setAttribute(getId(), object, getScope());
		}
		release();
		return EVAL_PAGE;
	}
}
