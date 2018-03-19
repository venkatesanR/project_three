package com.addval.aswtaglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import com.addval.utils.StrUtl;

public class UIStyle extends GenericBodyTag {
	private static final long serialVersionUID = -5011919041512508846L;
	private String newline = System.getProperty("line.separator");
	private String groupBox;
	private String title;
	private String style;
	private boolean footer = false;
	private boolean roundedCorner = true;
	private String expandCollapseImgId;
	private String expandCollapseBodyExpr;

	public void setGroupBox(String groupBox) {
		this.groupBox = groupBox;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setFooter(boolean footer) {
		this.footer = footer;
	}

	public void setRoundedCorner(boolean roundedCorner) {
		this.roundedCorner = roundedCorner;
	}

	public void setExpandCollapseImgId(String expandCollapseImgId) {
		this.expandCollapseImgId = expandCollapseImgId;
	}

	public void setExpandCollapseBodyExpr(String expandCollapseBodyExpr) {
		this.expandCollapseBodyExpr = expandCollapseBodyExpr;
	}

	public int doStartTag() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			if ("B".equalsIgnoreCase(groupBox)) {
				html.append(newline + "<DIV class=\"columnB\"");
				if (!StrUtl.isEmptyTrimmed(style)) {
					html.append(" style=\"" + style + "\"");
				}
				html.append(">" + newline);
				html.append("<DIV class=\"groupBox\">" + newline);

				if (!StrUtl.isEmptyTrimmed(title)) {
					html.append("<DIV class=\"boxTitle\">" + newline);
				}
				if (roundedCorner) {
					html.append("<DIV class=\"cornerTopLeft\"></DIV><DIV class=\"cornerTopRight\"></DIV>" + newline);	
				}
			}
			if ("C".equalsIgnoreCase(groupBox)) {
				html.append(newline + "<DIV class=\"columnC\"");
				if (!StrUtl.isEmptyTrimmed(style)) {
					html.append(" style=\"" + style + "\"");
				}
				html.append(">");
				html.append("<DIV class=\"groupBox\">" + newline);
				if (!StrUtl.isEmptyTrimmed(title)) {
					html.append("<DIV class=\"boxTitle\">" + newline);
				}
				html.append("<DIV class=\"cornerTopLeft\"></DIV><DIV class=\"cornerTopRight\"></DIV>");
			}
			if ("D".equalsIgnoreCase(groupBox)) {
				html.append(newline + "<DIV class=\"columnD\"");
				if (!StrUtl.isEmptyTrimmed(style)) {
					html.append(" style=\"" + style + "\"");
				}
				html.append(">" + newline);
				html.append("<DIV class=\"groupBox\">" + newline);

				if (!StrUtl.isEmptyTrimmed(title)) {
					html.append("<DIV class=\"boxTitle\">" + newline);
				}
				if (roundedCorner) {
					html.append("<DIV class=\"cornerTopLeft\"></DIV><DIV class=\"cornerTopRight\"></DIV>" + newline);
				}
			}
			pageContext.getOut().write(html.toString());
			return SKIP_BODY;
		}
		catch (Exception e) {
			try {
				pageContext.getOut().write(e.toString());
				return SKIP_BODY;
			}
			catch (IOException ioe) {
				throw new JspTagException("UIStyle Error:" + ioe.toString());
			}
		}
	}

	public int doEndTag() throws JspTagException {
		try {
			StringBuffer html = new StringBuffer();
			if ("B".equalsIgnoreCase(groupBox)) {
				if (!StrUtl.isEmptyTrimmed(title)) {
					html.append("<P>");
					if (!StrUtl.isEmptyTrimmed(expandCollapseBodyExpr) && !StrUtl.isEmptyTrimmed(expandCollapseImgId)) {
						html.append("<span ");
						html.append("onClick=\"showHide('" + expandCollapseBodyExpr + "','img[id=" + expandCollapseImgId + "]');return false;\"><nobr><img id=\"" + expandCollapseImgId + "\" src=\"../images/collapse1.jpg\"/>");
						html.append("</span>");
					}
					html.append(title);
					html.append("</P>" + newline);
					html.append("</DIV>");
				}
			}
			if ("C".equalsIgnoreCase(groupBox)) {
				if (!StrUtl.isEmptyTrimmed(title)) {
					html.append("<P>");
					html.append(title);
					html.append("</P>");
					html.append("</DIV>");
				}
			}
			if ("D".equalsIgnoreCase(groupBox)) {
				if (!StrUtl.isEmptyTrimmed(title)) {
					html.append("<div class=\"filter\"");
					if (!StrUtl.isEmptyTrimmed(expandCollapseBodyExpr) && !StrUtl.isEmptyTrimmed(expandCollapseImgId)) {
						html.append("onClick=\"showHide('" + expandCollapseBodyExpr + "','img[id=" + expandCollapseImgId + "]');return false;\"><nobr><img id=\"" + expandCollapseImgId + "\" src=\"../images/collapse1.jpg\"/>");
					}
					else {
						html.append("><nobr>");
					}
					html.append(title);
					html.append("</nobr>");
					html.append("</div>" + newline);
					html.append("</DIV>");
				}
			}
			if (footer) {
				if (roundedCorner) {
					html.append("<DIV class=\"groupBoxFooter\"><DIV class=\"groupBoxFooterLeftImg\"></DIV><DIV class=\"groupBoxFooterRightImg\"></DIV></DIV>");
				}
				html.append("</DIV>");
				html.append("</DIV>");
			}
			pageContext.getOut().write(html.toString());
			return EVAL_PAGE;
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
		finally {
			release();
		}
	}

	public void release() {
		groupBox = null;
		title =null;
		style = null;
		footer = false;
		roundedCorner = true;
		expandCollapseImgId = null;
		expandCollapseBodyExpr = null;
		super.release();
	}
}
