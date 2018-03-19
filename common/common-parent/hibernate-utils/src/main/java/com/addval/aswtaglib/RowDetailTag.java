package com.addval.aswtaglib;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.jsp.JspTagException;

import com.addval.dbutils.RSIterAction;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.springstruts.ResourceUtils;
import com.addval.utils.StrUtl;

public class RowDetailTag extends GenericBodyTag {
	private ASWTagUtils tagUtils = null;
	private EditorMetaData editorMetaData = null;
	private ResultSet resultSet = null;
	private Integer recordCount = 0;
	private int rowId = 0;
	private String whereClause = null;

	public RowDetailTag() {
		tagUtils = new ASWTagUtils(ASWTagUtils._LIST);
	}

	public void setMetaRefId(String metaRefId) throws JspTagException {
		Object object = pageContext.getAttribute(metaRefId, getScope());
		if (object != null && object instanceof EditorMetaData) {
			editorMetaData = (EditorMetaData) object;
		}
		else {
			throw new IllegalArgumentException("RowDetailTag: MetaRefId " + metaRefId + " invalid.");
		}
	}

	public void setDataRefId(String dataRefId) throws JspTagException {
		Object object = pageContext.getAttribute(dataRefId, getScope());
		if (object != null && object instanceof ResultSet) {
			resultSet = (ResultSet) object;
			recordCount = (Integer) pageContext.getAttribute("recordcount", getScope());
		}
	}

	public void setRowid(String rowid) {
		try {
			this.rowId = Integer.parseInt(rowid);
		}
		catch (Exception e) {
			throw new NumberFormatException("GridTag:Invalid rowid");
		}
	}

	public int doStartTag() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			Vector<ColumnMetaData> displayableColumns = editorMetaData.getDisplayableColumns();
			String columnCaption = null;
			String columnName = null;
			String columnValue = null;
			String pageLink = null;
			StringBuffer buildQuery = null;
			StringBuffer keyQuery = null;
			StringBuffer lookupQuery = null;
			boolean isDeleteColumn = false;
			final String _UTF8 = "UTF-8";

			StringBuffer htmlLinks = new StringBuffer();
			String previewLabel = ResourceUtils.message(pageContext, "editor.preview.title", "Preview");
			boolean previewExpand = pageContext.getRequest().getParameter("previewExpand") != null ? (new Boolean(pageContext.getRequest().getParameter("previewExpand"))).booleanValue() : true;

			if (displayableColumns != null && rowId > 0 && rowId <= recordCount && resultSet.absolute(rowId)) {
				Vector<ColumnMetaData> keyColumns = editorMetaData.getKeyColumns();
				whereClause = buildWhereClause();
				buildQuery = new StringBuffer(whereClause);
				keyQuery = new StringBuffer();
				lookupQuery = new StringBuffer();
				if (keyColumns != null) {
					for (ColumnMetaData columnMetaData : keyColumns) {
						columnName = columnMetaData.getName();
						keyQuery.append("&").append(columnName + "_KEY").append("=").append(resultSet.getString(columnName));
						lookupQuery.append("&").append(columnName + "_lookUp").append("=").append(resultSet.getString(columnName));
					}
				}
				html.append("<div id=\"" + editorMetaData.getName() + "_PREVIEW\">").append(newline);
				html.append(tagUtils.getGroupBoxTop("columnC", "<p>" + previewLabel + "</p>", "display: block;", "width:29%;"));
				html.append("<div class=\"outerborder\" style=\"overflow-x:hidden;\">");
				html.append("<table width=\"95%\" cellspacing=\"4px\" cellpadding=\"0\" border=\"0\" align=\"center\" summary=\"\">");
				html.append("<tbody>");
				for (ColumnMetaData columnMetaData : displayableColumns) {
					if (!columnMetaData.isAdvancedDisplay()) {
						continue;
					}
					columnCaption = tagUtils.getCaption(pageContext, columnMetaData);
					if (columnMetaData.getType() == ColumnDataType._CDT_LINK) {
						pageLink = columnMetaData.getRegexp();
						if (StrUtl.isEmptyTrimmed(pageLink)) {
							pageLink = columnMetaData.getValue();
						}
						if (StrUtl.isEmptyTrimmed(pageLink)) {
							throw new JspTagException("Target Link file for columnName  " + columnMetaData.getName() + " is not specified in RegExp column of Table!");
						}
						pageLink += pageLink.indexOf("?") != -1 ? "&" : "?";
						if (buildQuery.toString().length() > 0) {
							pageLink += buildQuery.substring(1) + keyQuery.toString();
						}
						else if (keyQuery.toString().length() > 0) {
							pageLink += keyQuery.substring(1);
						}
						isDeleteColumn = tagUtils.isDeleteColumn(columnMetaData);
						if(isDeleteColumn){
							htmlLinks.append("<div class=\"buttonGrayOnWhite\" onclick=\"javascript:if(confirmDelete()){launchLink(\'" + pageLink + "\');}\" >");
						}
						else {
							htmlLinks.append("<div class=\"buttonGrayOnWhite\" onclick=\"javascript:launchLink(\'" + pageLink + "\');\" >");
						}
						htmlLinks.append("<a href=\"#\" onclick=\"return false;\"");
						// htmlLinks.append("<img style=\"cursor=hand\" src=\'" + columnMetaData.getFormat() + "\' alt=\'" + columnMetaData.getCaption() + "\' BORDER=\'0\' />");
						htmlLinks.append(columnCaption);
						htmlLinks.append("</a>");
						htmlLinks.append("<div class=\"rightImg\"></div></div>").append(newline);
					}
					else if (columnMetaData.isLinkable()) {
						columnValue = (resultSet.getString(columnMetaData.getName()) == null ? "" : resultSet.getString(columnMetaData.getName()));
						pageLink = columnMetaData.getValue();
						if (StrUtl.isEmptyTrimmed(pageLink)) {
							throw new JspTagException("The value for a linkable column :" + columnMetaData.getName() + " is not specified in COLUMN_VALUE column of Columns table!");
						}
						pageLink += pageLink.indexOf("?") != -1 ? "&" : "?";

						if (pageLink.indexOf("INIT_LOOKUP") == -1) {
							if (buildQuery.toString().length() > 0) {
								pageLink += buildQuery.substring(1) + keyQuery.toString();
							}
							else if (keyQuery.toString().length() > 0) {
								pageLink += keyQuery.substring(1);
							}
						}
						else {
							lookupQuery.append("&").append(columnMetaData.getName() + "_lookUp").append("=").append(columnValue);
							if (buildQuery.toString().length() > 0) {
								pageLink += buildQuery.substring(1) + lookupQuery.toString();
							}
							else if (lookupQuery.toString().length() > 0) {
								pageLink += lookupQuery.substring(1);
							}
						}
						htmlLinks.append("<div class=\"buttonGrayOnWhite\">");
						htmlLinks.append("<a href=\"javascript:launchLink('" + URLEncoder.encode(pageLink, _UTF8) + "')\" onClick=\"eventObj = event.srcElement;\" >").append(columnCaption).append("</a>");
						htmlLinks.append("<div class=\"rightImg\"></div></div>").append(newline);
					}
					else {
						html.append("<tr>");
						columnValue = (resultSet.getString(columnMetaData.getName()) == null ? "" : resultSet.getString(columnMetaData.getName()));
						html.append("<td width=\"30%\" class=\"normal\" nowrap>").append(columnCaption).append("</td>");
						html.append("<td width=\"70%\" class=\"shadedRow\"");
						/*
						 In Preview section all the data should be left aligned.
						if (tagUtils.isNumericColumn(columnMetaData)) {
							html.append("style='text-align:right;white-space: nowrap;'");
						}
						else {
							html.append("style='white-space: nowrap;'");
						}
						*/
						html.append("style='white-space: normal;'");

						html.append(">").append( columnValue ).append("</td>");
						html.append("</tr>");
					}
				}
				html.append("</tbody>");
				html.append("</table>");
				html.append("</div>");
				if (htmlLinks.toString().length() > 0) {
					html.append(newline).append("<div class=\"buttonClear_bottomLeft\">").append(newline);
					html.append(htmlLinks.toString());
					html.append("</div>").append(newline);
				}
				html.append(tagUtils.getGroupBoxBottom());
				html.append("</div>");

				String previewExpandCollapseLRImgId = editorMetaData.getName() + "_PREVIEW_EXPAND_COLLAPSE_LR";
				String bodyFindExpr = "div[id^=" + editorMetaData.getName() + "_PREVIEW]";
				String imgFindExpr = "img[id^=" + previewExpandCollapseLRImgId + "]";
				String widthExpr = "div.columnB";
				html.append("<div id=\"verticaltextpreview\" class=\"verticaltext\" onClick=\"showHidePreview('" + bodyFindExpr + "','" + imgFindExpr + "','" + widthExpr + "','97%','67%');return false;\"><p><img id=\"" + previewExpandCollapseLRImgId + "\" src=\"../images/collapseright.jpg\"/><nobr>" + previewLabel + "</nobr></p></div>");
				html.append("<script>").append(newline);

				html.append("$$('div.columnB').each(function(element){").append(newline);
				html.append("element.style.width = \"67%\";").append(newline);
				html.append("});").append(newline);

				if(!previewExpand){
					html.append("var previewObj = $$('div[id=verticaltextpreview]').first();");
					html.append("if(previewObj){previewObj.click();}");
				}
				html.append("</script>");
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
				throw new JspTagException("RowDetailTag Error:" + ioe.toString());
			}
		}
	}

	public int doEndTag() throws JspTagException {
		try {
			return EVAL_PAGE;
		}
		catch (Exception e) {
			release();
			throw new JspTagException(e.getMessage());
		}
	}

	public void release() {
		editorMetaData = null;
		resultSet = null;
		super.release();
	}

	private String buildWhereClause() {
		StringBuffer buildQuery = new StringBuffer();
		Enumeration enumeration = pageContext.getRequest().getParameterNames();
		String columnName = null;
		String columnValue = null;
		while (enumeration.hasMoreElements()) {
			columnName = (String) enumeration.nextElement();
			if (columnName.equals("PAGING_ACTION")) {
				buildQuery.append("&").append(columnName).append("=").append(RSIterAction._CURR_STR);
			}
			else if (columnName.endsWith("_lookUp") || columnName.endsWith("_PARENT_KEY") || columnName.endsWith("operator_lookUp") && !columnName.equals("EDITOR_NAME") && !columnName.equals("EDITOR_TYPE") || columnName.equals("POSITION") || columnName.equals("DETAILS")) {
				columnValue = pageContext.getRequest().getParameter(columnName);
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = pageContext.getRequest().getParameter(columnName).trim();
					buildQuery.append("&").append(columnName).append("=").append(columnValue);
				}
			}
		}
		buildQuery.append("&EDITOR_NAME=").append(editorMetaData.getName());
		buildQuery.append("&EDITOR_TYPE=").append(editorMetaData.getType());
		return buildQuery.toString();
	}

	private String getPreviewBodyIdPrefix() {
		return editorMetaData.getName() + "_PREVIEW_";
	}

}
