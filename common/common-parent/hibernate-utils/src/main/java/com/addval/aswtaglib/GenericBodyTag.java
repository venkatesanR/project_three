package com.addval.aswtaglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

public abstract class GenericBodyTag extends GenericTag implements BodyTag {
	protected String newline = System.getProperty("line.separator");
	protected static final String tab = "\t";

	protected BodyContent _bodyContent;

	public GenericBodyTag() {
		_bodyContent = null;
	}

	public void setBodyContent(BodyContent bodyContent) {
		_bodyContent = bodyContent;
	}

	public BodyContent getBodyContent() {
		return _bodyContent;
	}

	public void doInitBody() throws JspTagException {
	}

	public int doAfterBody() throws JspTagException {
		return SKIP_BODY;
	}

	public int doStartTag() throws JspTagException {
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() throws JspTagException {
		return EVAL_PAGE;
	}

	public void release() {
		_bodyContent = null;
		super.release();
	}

	protected boolean parseBoolean(String bool, boolean flag) {
		if (bool != null)
			return bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("true");
		else
			return flag;
	}
}
