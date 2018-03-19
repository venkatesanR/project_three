package com.addval.aswtaglib;

import java.util.Vector;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

public class GenericTag extends TagSupport {
	private int _scope;

	public GenericTag() {
		this._scope = PageContext.PAGE_SCOPE; // 1
	}

	public void setScope(String scope) {
		if (scope.equalsIgnoreCase("page")) {
			this._scope = PageContext.PAGE_SCOPE; // 1
		}
		else if (scope.equalsIgnoreCase("request")) {
			this._scope = PageContext.REQUEST_SCOPE; // 2
		}
		else if (scope.equalsIgnoreCase("session")) {
			this._scope = PageContext.SESSION_SCOPE; // 3
		}
		else if (scope.equalsIgnoreCase("application")) {
			this._scope = PageContext.APPLICATION_SCOPE; // 4
		}
		else {
			this._scope = PageContext.PAGE_SCOPE;
		}
	}

	public void setScope(int scope) {
		this._scope = scope;
	}

	public int getScope() {
		return this._scope;
	}

	protected Object getAttribute(String att) {
		if (pageContext == null || att == null) {
			return null;
		}

		if (att.startsWith("page.")) {
			String pag = att.substring("page.".length());
			if (pag == null) {
				return null;
			}
			else {
				return pageContext.getAttribute(pag, PageContext.PAGE_SCOPE);
			}
		}
		else if (att.startsWith("request.")) {
			String req = att.substring("request.".length());
			if (req == null) {
				return null;
			}
			else {
				return pageContext.getAttribute(req, PageContext.REQUEST_SCOPE);
			}
		}
		else if (att.startsWith("session.")) {
			String ses = att.substring("session.".length());
			if (ses == null) {
				return null;
			}
			else {
				return pageContext.getAttribute(ses, PageContext.SESSION_SCOPE);
			}
		}
		else if (att.startsWith("application.")) {
			String app = att.substring("application.".length());
			if (app == null) {
				return null;
			}
			else {
				return pageContext.getAttribute(app, PageContext.APPLICATION_SCOPE);
			}
		}
		else {
			return pageContext.getAttribute(att);
		}
	}

	public static final Tag findAncestorWithClass(Tag tag, String className) {
		try {
			return TagSupport.findAncestorWithClass(tag, Class.forName(className));
		}
		catch (Exception _ex) {
			return null;
		}
	}

	public final Tag[] getAncestors() {
		Vector vec = new Vector();
		for (Object obj = this; (obj = ((Tag) (obj)).getParent()) != null;) {
			vec.addElement(obj);
		}
		int i = vec.size();
		Tag tags[] = new Tag[i];
		if (i != 0) {
			vec.copyInto(tags);
		}
		return tags;
	}

	public Tag[] getAncestors(Class className) {
		Vector vec = new Vector();
		for (Object obj = this; (obj = TagSupport.findAncestorWithClass(((Tag) (obj)), className)) != null;) {
			vec.addElement(obj);
		}

		int i = vec.size();
		Tag tags[] = new Tag[i];
		if (i != 0) {
			vec.copyInto(tags);
		}
		return tags;
	}

	public Tag[] getAncestors(String className) {
		try {
			return getAncestors(Class.forName(className));
		}
		catch (Exception _ex) {
			return new Tag[0];
		}
	}

	public void release() {
		_scope = pageContext.PAGE_SCOPE;
		super.release();
	}
}
