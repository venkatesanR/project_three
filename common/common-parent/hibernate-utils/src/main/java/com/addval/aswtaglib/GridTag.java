package com.addval.aswtaglib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.apache.commons.lang.StringUtils;

import com.addval.dbutils.RSIterAction;
import com.addval.dbutils.RSIterator;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.springstruts.ResourceUtils;
import org.apache.struts.util.LabelValueBean;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;

public class GridTag extends GenericBodyTag {
	private static final long serialVersionUID = -2429505815655147000L;
	private ASWTagUtils tagUtils = null;
	private EditorMetaData editorMetaData = null;
	private ResultSet resultSet = null;
	private Integer recordCount = 0;
	private RSIterator rsIterator = null;
	private RSIterAction rsIterAction = new RSIterAction("UNDEF");
	private int pageSize = 0;
	private String position = "1";
	private String addlink = null;
	private String cancellink = null;
	private String deletelink = "";
	private String whereClause = null;
	private boolean editPermission = true;
	private boolean viewPermission = false;
	private final String _UTF8 = "UTF-8";
	private final int PAGE_MIN_SIZE = 25;
	private final int PAGE_MAX_SIZE = 100;
	private int _MAX_RECORDS = 500;
	private HashMap<String, Integer> columnWidthMap = null;
	private boolean refeshButton = false;
	private boolean rowButtonsPlacementLeft = true; 
	
	public GridTag() {
		tagUtils = new ASWTagUtils(ASWTagUtils._LIST);
	}

	public void setMaxRecords(int aNbrRecords) {
		_MAX_RECORDS = aNbrRecords;
	}

	public int getMaxRecords() {
		return _MAX_RECORDS;
	}

	public void setMetaRefId(String metaRefId) throws JspTagException {
		Object object = pageContext.getAttribute(metaRefId, getScope());
		if (object != null && object instanceof EditorMetaData) {
			editorMetaData = (EditorMetaData) object;
		}
		else {
			throw new IllegalArgumentException("GridTag: MetaRefId " + metaRefId + " invalid.");
		}
	}

	public void setDataRefId(String dataRefId) throws JspTagException {
		Object object = pageContext.getAttribute(dataRefId, getScope());
		if (object != null && object instanceof ResultSet) {
			resultSet = (ResultSet) object;
			recordCount = (Integer) pageContext.getAttribute("recordcount", getScope());
		}
	}

	public void setPageaction(String pageAction) {
		rsIterAction = new RSIterAction(pageAction);
	}

	public void setPagesize(String pageSize) {
		try {
			this.pageSize = Integer.parseInt(pageSize);
		}
		catch (Exception e) {
			throw new NumberFormatException("GridTag:Invalid pageSize");
		}
	}

	private int getPagesize() {
		return this.pageSize > 0 ? this.pageSize : 25;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setAddlink(String addlink) {
		this.addlink = addlink;
	}

	public void setCancellink(String cancellink) {
		this.cancellink = cancellink;
	}

	public boolean isEditPermission() {
		return editPermission;
	}

	public void setEditPermission(boolean editPermission) {
		this.editPermission = editPermission;
	}
	public boolean isRefeshButton() {
		return refeshButton;
	}

	public void setRefeshButton(boolean refeshButton) {
		this.refeshButton = refeshButton;
	}

	public boolean isRowButtonsPlacementLeft() {
		return rowButtonsPlacementLeft;
	}

	public void setRowButtonsPlacementLeft(boolean rowButtonsPlacementLeft) {
		this.rowButtonsPlacementLeft = rowButtonsPlacementLeft;
	}

	public int doStartTag() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			if(resultSet != null){
				rsIterator = new RSIterator(resultSet.getStatement(), position, rsIterAction, recordCount, getPagesize());
			}
			boolean isViewPermission = editorMetaData.getColumnMetaData(editorMetaData.getName() +"_VIEW") != null;
			whereClause = buildWhereClause();
			Vector<ColumnMetaData> displayableColumns = editorMetaData.getDisplayableColumns();
			Vector<ColumnMetaData> keyColumns = editorMetaData.getKeyColumns();
			boolean hasBaseDisplayLink = editorMetaData.hasBaseDisplayLink();
			boolean hasAdvancedDisplay = editorMetaData.hasAdvancedDisplay();
			boolean hasRecords = (rsIterator != null && rsIterator.getPageDesc().getRowCount() > 0);
			boolean isChild = isChild();
			boolean isForLookup = isForLookup();
			boolean isMassUpdate = pageContext.getRequest().getParameter("MassUpdate") != null ? new Boolean(pageContext.getRequest().getParameter("MassUpdate")).booleanValue() : false;
			boolean isMultirowSelector = pageContext.getRequest().getParameter("MultirowSelector") != null ? new Boolean(pageContext.getRequest().getParameter("MultirowSelector")).booleanValue() : false;

			String heading = ResourceUtils.editorTitle(pageContext, editorMetaData.getName(), editorMetaData.getDesc());
			if (StrUtl.isEmptyTrimmed(heading)) {
				heading = editorMetaData.getName();
			}
			boolean titleSuffixCustomized = Boolean.parseBoolean(ResourceUtils.message(pageContext, editorMetaData.getName() + ".list.heading.suffix.customized", "false"));
			if ( !titleSuffixCustomized ) {
				heading += ResourceUtils.message(pageContext, "editor.list.heading.suffix", " - Results");
			}
			else {
				heading += ResourceUtils.message(pageContext, editorMetaData.getName() + ".list.heading.suffix", "");
			}

			String columnName = null;
			StringBuffer buildQuery = null;
			StringBuffer keyQuery = null;
			StringBuffer lookupQuery = null;
			String previewId = getPreviewId();
			String currPreviewId = null;
			boolean selectedRow = false;
			boolean isFirstRow = true;
			boolean rowSelector = pageContext.getRequest().getParameter("rowSelector") != null ? new Boolean(pageContext.getRequest().getParameter("rowSelector")).booleanValue() : false;
			boolean disableSelector = pageContext.getRequest().getParameter("disableSelector") != null ? new Boolean(pageContext.getRequest().getParameter("disableSelector")).booleanValue() : false;

			if(hasAdvancedDisplay || hasBaseDisplayLink){
				rowSelector = true;
			}
			if(disableSelector){
              rowSelector = false;
            }

			setHeaderWidth();

			if (isChild) {
				html.append(tagUtils.getGroupBoxTop("columnB"));
				html.append("<br/><div class=\"childTab\">");
			}
			else if(rsIterator != null && !isForLookup()){
				String refeshButtonHTML = "";
				if(isRefeshButton()){
					refeshButtonHTML ="<div class=\"buttonClear_topRight noTopMargin\">";
					refeshButtonHTML +="<div class=\"buttonBlueOnBlue\" onclick=\"javascript:doRefresh()\"><A onclick=\"return false;\" href=\"#\">Refresh</A><DIV class=\"rightImg\"></DIV></div>";
					refeshButtonHTML +="</div>";	
				}
				
				boolean displaylistSearchCollapseFlag = pageContext.getRequest().getParameter("displaylistSearchCollapseFlag") != null ? (new Boolean(pageContext.getRequest().getParameter("displaylistSearchCollapseFlag"))).booleanValue() : false;
				html.append("<input type=\"hidden\" name=\"displaylistSearchCollapseFlag\" tabIndex=\"-1\" value=\"").append(displaylistSearchCollapseFlag).append("\" />");
				
				String displayListExpandCollapseImgId = editorMetaData.getName() + "_DISPLAYLIST_EXPAND_COLLAPSE";
				String bodyFindExpr = "div[id^=" + getDisplayListBodyIdPrefix() + "]";
				String imgFindExpr = "img[id=" + displayListExpandCollapseImgId + "]";
				
				StringBuffer headingHTML = new StringBuffer(); 
				headingHTML.append("<div class=\"displaylist\" onClick=\"document.forms[0].displaylistSearchCollapseFlag.value=($$(\'"+ imgFindExpr +"\').first().src.indexOf('collapse') != -1);showHide('" + bodyFindExpr + "','" + imgFindExpr + "');if(parent.doResizeOnLoad){parent.doResizeOnLoad(parent.window.frames[window.name].name);}");
				if(isRefeshButton()){
					headingHTML.append("if(document.forms[0].displaylistSearchCollapseFlag.value == 'false'){doRefresh();}");
					//1 While expand : Refresh automatically instead of auto refresh periodically - performance.
					//2 While collapse : If the page loaded in the iFrame then call parent oResizeOnLoad. //Ocean Price QuoteSummaryPage.tml,"export_list_struts.jsp"
				}
				headingHTML.append(";return false;\"><img style=\"float:left;margin:5px 5px 0px 0px;\" id=\"" + displayListExpandCollapseImgId + "\" src=\"../images/collapse1.jpg\"/></div>"+ refeshButtonHTML +"<p>" + heading + "</p>");
				
				html.append(tagUtils.getGroupBoxTop("columnB", headingHTML.toString()));
				
			}

			if (rsIterator != null && displayableColumns != null) {
				if(!isForLookup()){
					html.append("&nbsp;<div class=\"outerborder\" id=\"").append(getDisplayListBodyIdPrefix()).append("\" >");
				}
				html.append(getPaginationSection(hasRecords, isForLookup));
				html.append(newline);
				html.append("<div class=\"datagridcontainer\" id=\"" + getGridBodyIdPrefix() + "CONTAINER" + "\">");
				html.append(newline);
				html.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"listTable resizable\" id=\"" + editorMetaData.getName() + "_TABLE" + "\">");
				html.append("<thead>");
				html.append("<tr>");

				if (hasBaseDisplayLink && (isEditPermission() || isViewPermission) && rowSelector) {
					html.append("<th id=\"ROWSELECT\"");
					html.append(getHeaderWidth("ROWSELECT"));
					html.append(" >");
					html.append("&nbsp;</th>");
				}

				Vector<ColumnMetaData> displayableLinkColumns = new Vector<ColumnMetaData>();
				Vector<ColumnMetaData> displayableNonLinkColumns =  new Vector<ColumnMetaData>();

				boolean userPresentable = Boolean.valueOf(ResourceUtils.message(pageContext, editorMetaData.getName() + ".heading.userpresentable", "true"));
				for (ColumnMetaData columnMetaData : displayableColumns) {
					// Change the caption.
					if(userPresentable){
						String[] captions = StringUtils.split(columnMetaData.getColumnInfo().getCaption(), AVConstants._COLUMN_CAPTION_DELIMITER);
						if ( captions.length == 1 ){
							columnMetaData.getColumnInfo().setCaption(tagUtils.toUserPresentable(columnMetaData.getCaption()));
						}
					}
					if (columnMetaData.getType() == ColumnDataType._CDT_LINK) {
						displayableLinkColumns.add(columnMetaData);
					}
					else {
						displayableNonLinkColumns.add(columnMetaData);
					}
				}

				if(isRowButtonsPlacementLeft()){
					setTHHTML(displayableLinkColumns,hasRecords,isMassUpdate,isMultirowSelector,html);
					setTHHTML(displayableNonLinkColumns,hasRecords,false,false, html);
				}
				else {
					setTHHTML(displayableColumns,hasRecords,isMassUpdate,isMultirowSelector,html);
				}

				/* No Arrow Indicator
				if (hasAdvancedDisplay) {
					html.append("<th>&nbsp;</th>");
				}
				*/
				html.append("</tr>");
				html.append("</thead>");
				html.append("<tbody>");

				StringBuffer rowHtml = new StringBuffer();
				StringBuffer rowLinkValues = new StringBuffer();

				while (rsIterator.next()) {
					buildQuery = new StringBuffer(whereClause);
					keyQuery = new StringBuffer();
					lookupQuery = new StringBuffer();
					if (keyColumns != null) {
						for (ColumnMetaData columnMetaData : keyColumns) {
							columnName = columnMetaData.getName();
							keyQuery.append("&").append(columnName + "_KEY").append("=").append(rsIterator.getString(columnName));
							lookupQuery.append("&").append(columnName + "_lookUp").append("=").append(rsIterator.getString(columnName));
						}
					}
					currPreviewId = keyQuery.toString();
					currPreviewId = StringUtils.replace(currPreviewId, "&", "|");
					currPreviewId = StringUtils.replace(currPreviewId, "=", ":");
					selectedRow = (currPreviewId.length() > 0 ) ? currPreviewId.equalsIgnoreCase(previewId) : false;
					rowHtml = new StringBuffer();
					rowLinkValues = new StringBuffer();

					if(isRowButtonsPlacementLeft()){
						setTDHTML(displayableLinkColumns,buildQuery,keyQuery,lookupQuery,currPreviewId,isMassUpdate,isMultirowSelector,rowHtml,rowLinkValues);
						setTDHTML(displayableNonLinkColumns,buildQuery,keyQuery,lookupQuery,currPreviewId,false,false,rowHtml,rowLinkValues);
					}
					else {
						setTDHTML(displayableColumns,buildQuery,keyQuery,lookupQuery,currPreviewId,isMassUpdate,isMultirowSelector,rowHtml,rowLinkValues);
					}

					/* No Arrow Indicator
					if (hasAdvancedDisplay) {
						rowHtml.append("<td class=\"arrow\">");
						if (selectedRow) {
							rowHtml.append("<div class=\"arrowIcon\"/>");
						}
						rowHtml.append("</td>");
					}
					*/

					if (rowHtml.length() > 0) {
						if(rowSelector){
							html.append("<tr style=\"cursor:hand\" ");
						}
						else {
							html.append("<tr ");
						}
						if (isFirstRow) {
							html.append(" id=\"FIRSTROW\"");
							isFirstRow = false;
						}
						if (selectedRow) {
							html.append(" class=\"selected\"");
						}
						if (hasAdvancedDisplay) {
							html.append(" onClick=\"javascript:launchPreview(this,'").append(currPreviewId).append("')\"");
						}
						if ((hasBaseDisplayLink && (isEditPermission() || isViewPermission)) && !hasAdvancedDisplay) {
							html.append(" onClick=\"javascript:setSelectedRow(this)\"");
						}
						if (isForLookup) {
							html.append(" onClick=\"selectedRow(this)\"");
						}
						html.append(" onContextmenu=\"rowContextMenu('"+getGridBodyIdPrefix() + "CONTAINER" + "',this)\"");

						html.append(">");
						// rowSelector flag In case multi select and do some action. (Eg.. Update status for the selected row.)
						if (hasBaseDisplayLink && (isEditPermission() || isViewPermission) && rowSelector) {
							String selectedRowValue = "{}";
							if (rowLinkValues.length() > 0) {
								selectedRowValue = "{" + rowLinkValues.substring(1) + "}";
							}
							html.append("<td width=\"5px;\"><input type=\"radio\" name=\"selectedRowId\" id=\"selectedRowId\" value=\"").append(selectedRowValue).append("\" ");
							if (hasAdvancedDisplay) {
								html.append(" onClick=\"javascript:launchPreview(this.parentNode.parentNode,'").append(currPreviewId).append("')\"");
							}
							if (selectedRow) {
								html.append(" CHECKED=\"checked\"");
							}
							html.append("/></td>");
						}
						html.append(rowHtml.toString());
						html.append("</tr>");
					}
				}

				if (!hasRecords) {
					int colspan = displayableColumns.size();
					html.append("<tr>");
					html.append("<td class=\"error\" colspan=\"").append(colspan).append("\">").append(ResourceUtils.message(pageContext, "editor.list.norecordfound", "No record Found!!!")).append("</td>");
					html.append("</tr>");
				}
				html.append("</tbody>");
				html.append("</table>").append(newline);
				html.append("</div>").append(newline);
				if(isChild){
					html.append("</div>").append(newline);
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
				throw new JspTagException("GridTag Error:" + ioe.toString());
			}
		}
	}

	private void setTHHTML(Vector<ColumnMetaData> displayableColumns,boolean hasRecords,boolean isMassUpdate,boolean isMultirowSelector,StringBuffer html) throws Exception {
		boolean isDeleteColumn = false;
		boolean isSortable = false;
		String columnCaption = null;
		String sortName = getSortName();
		int sortOrder = getSortOrder();

		String selectAllLabel = ResourceUtils.message(pageContext, "editor.list.selectAll.label", "Select All");
		if (hasRecords && (isMassUpdate || isMultirowSelector) && isEditPermission() && !(editorMetaData.isMultiDeletePriv())) {
			html.append("<th style='text-align:center;'>");
			html.append("<div>");
			html.append("<input type=\"checkbox\" tabIndex=\"-1\" name=\"SELECT_ALL_DELETE\" onclick=\"doSelectAllDelete()\" />"+selectAllLabel);
			html.append("</div>");
			html.append("</th>");
		}

		for (ColumnMetaData columnMetaData : displayableColumns) {
			if (columnMetaData.getType() == ColumnDataType._CDT_LINK && !columnMetaData.getName().equalsIgnoreCase(editorMetaData.getName() +"_VIEW")) {
				if (!isEditPermission()) {
					continue;
				}
			}
			isDeleteColumn = tagUtils.isDeleteColumn(columnMetaData);
			if (!columnMetaData.isBaseDisplay() && !(isDeleteColumn && editorMetaData.isMultiDeletePriv() )) {
				continue;
			}
			html.append("<th id=\"").append(columnMetaData.getName()).append("\"");
			columnCaption = tagUtils.getCaption(pageContext, columnMetaData);
			html.append(getHeaderWidth(columnMetaData.getName()));
			isSortable = tagUtils.isSortable(columnMetaData);
			columnCaption = tagUtils.getCaption(pageContext, columnMetaData);
			if (isSortable) {
				html.append(">");
				html.append("<a href=\"javascript:setSortingAction('").append(columnMetaData.getName()).append("')\">");
				html.append("<div class=\"title\">");
				html.append(columnCaption);
				if (!columnMetaData.getName().equalsIgnoreCase(sortName)) {
					html.append("<img src=\'../images/sortable.png\' />");
				}
				else {
					if (sortOrder == AVConstants._ASC) {
						html.append("<img src=\'../images/sort-asc.png\'/>");
					}
					else {
						html.append("<img src=\'../images/sort-desc.png\'/>");
					}
				}
				html.append("</div>");
				html.append("</a>");
			}
			else if(hasRecords && isDeleteColumn && editorMetaData.isMultiDeletePriv()){
				html.append(" style='text-align:center;'>");
				html.append("<div>");
				html.append("<input type=\"checkbox\" tabIndex=\"-1\" name=\"SELECT_ALL_DELETE\" onclick=\"doSelectAllDelete()\" />"+selectAllLabel);
				html.append("</div>");
			}
			else {
				html.append(" style='text-align:center;'>");
				html.append("<div class=\"title\">");
				html.append(columnCaption).append("<img src=\"../images/spacer.gif\" tabIndex=\"-1\" width=\"1\" height=\"16\" />");
				html.append("</div>");
			}
			html.append("</th>");
		}
	}
	private void setTDHTML(Vector<ColumnMetaData> displayableColumns,StringBuffer buildQuery,StringBuffer keyQuery,StringBuffer lookupQuery,String currPreviewId,boolean isMassUpdate,boolean isMultirowSelector,StringBuffer rowHtml,StringBuffer rowLinkValues) throws Exception {
		boolean isDeleteColumn = false;
		String columnCaption = null;
		String pageLink = null;
		String columnValue = null;
		String displayStyle = null;
		String componentPrefix = pageContext.getRequest().getParameter("componentPrefix") != null ? pageContext.getRequest().getParameter("componentPrefix") : "";

		if ((isMassUpdate || isMultirowSelector) && isEditPermission() && !(editorMetaData.isMultiDeletePriv())) {
			String rowKey = keyQuery.toString().length() > 0 ? keyQuery.substring(1) : "";
			rowHtml.append("<td align=\"center\">");
			rowHtml.append("<input type=\"checkbox\" tabIndex=\"-1\" name=\"" + editorMetaData.getName() + "_DELETE\" value=\"" + rowKey + "\" ");
			rowHtml.append(" onClick=\"doSelectDelete();\">");
			rowHtml.append("</td>");
		}

		for (ColumnMetaData columnMetaData : displayableColumns) {
			isDeleteColumn = tagUtils.isDeleteColumn(columnMetaData);
			if (!columnMetaData.isBaseDisplay() && !columnMetaData.isBaseDisplayLink()) {
				continue;
			}
			columnCaption = tagUtils.getCaption(pageContext, columnMetaData);
			if (columnMetaData.getType() == ColumnDataType._CDT_LINK) {
				if (isEditPermission() || columnMetaData.getName().equalsIgnoreCase(editorMetaData.getName() +"_VIEW")) {
					pageLink = columnMetaData.getRegexp();
					if (StrUtl.isEmptyTrimmed(pageLink)) {
						pageLink = columnMetaData.getValue();
					}
					if (StrUtl.isEmptyTrimmed(pageLink)) {
						throw new JspTagException("Target Link file for columnName  " + columnMetaData.getName() + " is not specified in RegExp column of Table!");
					}
					pageLink += pageLink.indexOf("?") != -1 ? "&" : "?";
					if (buildQuery.toString().length() > 0) {
						pageLink += buildQuery.substring(1) + keyQuery.toString() + "&previewId=" + currPreviewId;
					}
					else if (keyQuery.toString().length() > 0) {
						pageLink += keyQuery.substring(1) + "&previewId=" + currPreviewId;
					}
					if(!StrUtl.isEmptyTrimmed(componentPrefix)){
						pageLink +="&componentPrefix=" + componentPrefix;
					}
					

					if(columnMetaData.isBaseDisplayLink()){
						String key = columnMetaData.getCaption();
						key = StringUtils.replace(key, " ", "_"); // Replace space by underscore. Error while eval (common_list_script.jsp - launchSelectedRowLink)
						rowLinkValues.append(",").append("'").append(key).append("'").append(":'").append(pageLink).append("'");
					}
					else if(isDeleteColumn && !editorMetaData.isMultiDeletePriv()) {
						rowHtml.append("<td align=\"center\">");
						rowHtml.append("<a href=\"javascript:launchLink(\'" + URLEncoder.encode(pageLink, _UTF8) + "\');\"");
						rowHtml.append(" onClick=\"return confirmDelete();\">");
						rowHtml.append("<img style=\"cursor=hand\" src=\'" + columnMetaData.getFormat() + "\' alt=\'" + columnCaption + "\' BORDER=\'0\' />");
						rowHtml.append("</a>");
						rowHtml.append("</td>");
					}
					else if(columnMetaData.isBaseDisplay() && !(isDeleteColumn && editorMetaData.isMultiDeletePriv())) {
						rowHtml.append("<td align=\"center\">");
						rowHtml.append("<a href=\"javascript:launchLink(\'" + URLEncoder.encode(pageLink, _UTF8) + "\');\"");
						rowHtml.append(" onClick=\"eventObj = event.srcElement;\">");
						rowHtml.append("<img style=\"cursor=hand\" src=\'" + columnMetaData.getFormat() + "\' alt=\'" + columnCaption + "\' BORDER=\'0\' />");
						rowHtml.append("</a>");
						rowHtml.append("</td>");
					}
					if(isDeleteColumn && editorMetaData.isMultiDeletePriv()){
						rowHtml.append("<td align=\"center\">");
						deletelink = columnMetaData.getRegexp();
						String deleteQueryString = keyQuery.toString().length() > 0 ? keyQuery.substring(1) : "";
						rowHtml.append("<input type=\"checkbox\" tabIndex=\"-1\" name=\"" + columnMetaData.getName() + "\" value=\"" + deleteQueryString + "\" ");
						rowHtml.append(" onClick=\"doSelectDelete();\">");
						rowHtml.append("</td>");
					}
				}
			}
			else if (columnMetaData.isLinkable() && (isEditPermission() || columnMetaData.getName().equalsIgnoreCase(editorMetaData.getName() +"_VIEW"))) {
				//if (isEditPermission() || columnMetaData.getName().equalsIgnoreCase(editorMetaData.getName() +"_VIEW")) {
					columnValue = (rsIterator.getString(columnMetaData.getName()) == null ? "" : rsIterator.getString(columnMetaData.getName()));
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
					rowHtml.append("<td>");
					rowHtml.append("<a href=\"javascript:launchLink('" + URLEncoder.encode(pageLink, _UTF8) + "')\" onClick=\"eventObj = event.srcElement;\" >").append(columnValue).append("</a>");
					rowHtml.append("</td>");
				//}
			}
			else {
				columnValue = (rsIterator.getString(columnMetaData.getName()) == null ? "" : rsIterator.getString(columnMetaData.getName()));
				displayStyle = (!StrUtl.isEmptyTrimmed(columnMetaData.getDisplayStyle())) ? columnMetaData.getDisplayStyle() : "";
				if(!StrUtl.isEmptyTrimmed( columnMetaData.getTextAlign() )){
					displayStyle +=";text-align:" + columnMetaData.getTextAlign() +";'";
				}
				else if (tagUtils.isNumericColumn(columnMetaData)) {
					displayStyle +=";text-align:right;";
				}
				rowHtml.append("<td");
				if(!StrUtl.isEmptyTrimmed(columnMetaData.getDisplayCSS() )){
					rowHtml.append(" class='"+ columnMetaData.getDisplayCSS() +"'");
				}
				if(!StrUtl.isEmptyTrimmed(displayStyle)){
					rowHtml.append(" style='"+ displayStyle +"'");
				}
				rowHtml.append(">");
				if ("".equals(columnValue)) {
					columnValue = "&nbsp;";
				}
				rowHtml.append(columnValue).append("</td>");
			}
		}
	}
	public int doEndTag() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			List<LabelValueBean> contextMenu = new ArrayList<LabelValueBean>();
			boolean isViewPermission = editorMetaData.getColumnMetaData(editorMetaData.getName() +"_VIEW") != null;
			StringBuffer buildQuery = new StringBuffer(whereClause);
			String addBtnHtml = null;
			if(rsIterator == null) { //AddNew by default before search operation.
				addBtnHtml = getAddLink(buildQuery);
			}
			String cancelBtnHtml = getCancelLink(buildQuery);
			String multiDeleteBtnHtml = getMultiDeleteLink(buildQuery);
			boolean hasBaseDisplayLink = editorMetaData.hasBaseDisplayLink();
			boolean hasRecords = (rsIterator != null && rsIterator.getPageDesc().getRowCount() > 0);
			boolean isDeleteColumn = false;
			if (addBtnHtml != null || cancelBtnHtml != null) {
				html.append("<div class=\"buttonClear_bottomLeft w50\">").append(newline);
				if (addBtnHtml != null) {
					html.append(addBtnHtml);
				}
				if (cancelBtnHtml != null) {
					html.append(cancelBtnHtml);
				}
				html.append(newline).append("</div>");
			}

			if(hasRecords && (isEditPermission() || isViewPermission)){
				StringBuffer htmlR = new StringBuffer();
				if(multiDeleteBtnHtml != null){
					htmlR.append(multiDeleteBtnHtml);
				}
				if(hasBaseDisplayLink){
					Vector<ColumnMetaData> displayableColumns = editorMetaData.getDisplayableColumns();
					ColumnMetaData columnMetaData = null;
					String columnMessage = null;
					for (int i = displayableColumns.size() - 1; i >= 0; i--) {
						columnMetaData = displayableColumns.get(i);
						if (!columnMetaData.isBaseDisplayLink()) {
							continue;
						}
						String columnCaption = tagUtils.getCaption(pageContext, columnMetaData);
						String columnEnglishCaption = tagUtils.getCaption(columnMetaData);
						String buttonId = "";
						if (columnMetaData.getType() == ColumnDataType._CDT_LINK) {
						    columnMessage = columnMetaData.getErrorMsg();
							isDeleteColumn = tagUtils.isDeleteColumn(columnMetaData);
							if( (isEditPermission() && ((isDeleteColumn && multiDeleteBtnHtml == null) || !isDeleteColumn) ) || columnMetaData.getName().equalsIgnoreCase(editorMetaData.getName() +"_VIEW")){
								buttonId = "BTN" + columnMetaData.getName();
								htmlR.append("<div id=\"").append(buttonId).append("\" class=\"buttonGrayOnWhite\" onclick=\"javascript:launchSelectedRowLink('").append(columnEnglishCaption).append("','").append(columnMessage).append("')\" >");
								htmlR.append("<a href=\"#\" onclick=\"return false;\">").append(columnCaption).append("</a>");
								htmlR.append("<div class=\"rightImg\">").append("</div>").append("</div>");
								contextMenu.add(0,new LabelValueBean(columnCaption,"($$(\"div[id^="+ buttonId +"]\").first()).click()") );
							}
						}
					}
				}
				if (htmlR.length() > 0) {
					html.append("<div class=\"buttonClear_bottomRight w50\">").append(newline);
					html.append(htmlR.toString());
					html.append(newline).append("</div>");
				}
			}
			if(rsIterator != null && !isForLookup()){
				html.append("</div>").append(newline);
			}

			boolean isChild = isChild();
			if (isChild) {
				html.append(tagUtils.getGroupBoxBottomWithoutFooter());
			}
			else if(rsIterator != null  && !isForLookup()){
				html.append(tagUtils.getGroupBoxBottom());
			}
			boolean disablecontextMenu = new Boolean(ResourceUtils.message(pageContext, editorMetaData.getName() + ".disablecontextMenu", "false")).booleanValue();
			if( contextMenu.size() > 0 && !disablecontextMenu){
				html.append("<SCRIPT language=\"javascript\">").append(newline);
				html.append(tagUtils.getContextMenuScript(getGridBodyIdPrefix() + "CONTAINER",contextMenu));
				html.append("</SCRIPT>").append(newline);
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
		editorMetaData = null;
		resultSet = null;
		rsIterator = null;
		super.release();
	}

	private boolean isChild() {
		String childIndex = pageContext.getRequest().getParameter("childIndex");
		return !StrUtl.isEmptyTrimmed(childIndex);
	}

	private String getResizableColumnsWidth() {
		String columnsWidth = (String) pageContext.getRequest().getAttribute(getResizableColumnsWidthName());
		if (columnsWidth == null) {
			columnsWidth = pageContext.getRequest().getParameter(getResizableColumnsWidthName());
		}
		if (columnsWidth == null) {
			columnsWidth = "";
		}
		return columnsWidth;
	}

	private void setHeaderWidth() {
		columnWidthMap = new HashMap<String, Integer>();
		String columnsWidth = getResizableColumnsWidth();
		if (!StrUtl.isEmptyTrimmed(columnsWidth)) {
			String colsWidth[] = columnsWidth.split(",");
			String pair[] = null;
			for (String colWidth : colsWidth) {
				pair = colWidth.split(":");
				if (pair.length == 2) {
					columnWidthMap.put(pair[0], Integer.valueOf(pair[1]));
				}
			}
		}
	}

	private String getHeaderWidth(String columnName) {
		if (columnWidthMap.containsKey(columnName)) {
			return " width=\"" + columnWidthMap.get(columnName) + "px;\"";
		}
		return "";
	}

	private String getResizableColumnsWidthName() {
		// Same name should maintain in CustomListProcessAction.java , aswcolumnresize.js and aswlookdown.js
		return editorMetaData.getName() + "_TABLE" + "resizableColumnsWidth";
	}

	private String buildWhereClause() {
		StringBuffer buildQuery = new StringBuffer();
		String columnName = null;
		String columnValue = null;
		for (Enumeration enumeration = pageContext.getRequest().getParameterNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.equals("PAGING_ACTION")) {
				buildQuery.append("&").append(columnName).append("=").append(RSIterAction._CURR_STR);
			}
			else if (columnName.startsWith("searchColumns") || columnName.endsWith("_lookUp") || columnName.endsWith("_PARENT_KEY") || columnName.endsWith("operator_lookUp") && !columnName.equals("EDITOR_NAME") && !columnName.equals("EDITOR_TYPE") || columnName.equals("POSITION") || columnName.equals("DETAILS") || columnName.equals("PAGESIZE") || columnName.equals("IncludeHeaderFooter") || columnName.equals("rowSelector")) {
				columnValue = pageContext.getRequest().getParameter(columnName);
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = pageContext.getRequest().getParameter(columnName).trim();
					buildQuery.append("&").append(columnName).append("=").append(columnValue);
				}
			}
		}

		buildQuery.append("&EDITOR_NAME=").append(editorMetaData.getName());
		buildQuery.append("&EDITOR_TYPE=").append(editorMetaData.getType());

		String searchFilterName = pageContext.getRequest().getParameter("searchFilterName") != null ? pageContext.getRequest().getParameter("searchFilterName") : "";
		buildQuery.append("&searchFilterName=").append(searchFilterName);

		String templateName = getTemplateName();
		buildQuery.append("&templateName=").append(templateName);

		boolean minimizeFilter = pageContext.getRequest().getParameter( "minimizeFilter" ) != null ? (new Boolean( pageContext.getRequest().getParameter( "minimizeFilter" ) )).booleanValue() : true;
		buildQuery.append("&minimizeFilter=").append(minimizeFilter);

		boolean advancedSearchExpandFlag = pageContext.getRequest().getParameter( "advancedSearchExpandFlag" ) != null ? (new Boolean( pageContext.getRequest().getParameter( "advancedSearchExpandFlag" ) )).booleanValue() : false;
		buildQuery.append("&advancedSearchExpandFlag=").append(advancedSearchExpandFlag);

		return buildQuery.toString();
	}

	private String getPreviewId() {
		return pageContext.getRequest().getParameter("previewId");
	}

	private boolean isForLookup() {
		return getId().toUpperCase().indexOf("LOOKUP") != -1;
	}

	private String getGridBodyIdPrefix() {
		return editorMetaData.getName() + "_GRID_";
	}

	public String getSortName() {
		String sortName = pageContext.getRequest().getParameter("SORT_NAME");
		if (StrUtl.isEmptyTrimmed(sortName)) {
			sortName = pageContext.getRequest().getParameter("sort_Name");
		}
		if (StrUtl.isEmptyTrimmed(sortName)) {
			sortName = (String) pageContext.getRequest().getAttribute("SORT_NAME");
		}
		return sortName;
	}

	public int getSortOrder() {
		String sortOrder = pageContext.getRequest().getParameter("SORT_ORDER");
		if (StrUtl.isEmptyTrimmed(sortOrder)) {
			sortOrder = pageContext.getRequest().getParameter("sort_Order");
		}
		if (StrUtl.isEmptyTrimmed(sortOrder)) {
			sortOrder = (String) pageContext.getRequest().getAttribute("SORT_ORDER");
		}
		return "DESC".equalsIgnoreCase(sortOrder) ? AVConstants._DESC : AVConstants._ASC;
	}

	private String getPaginationSection(boolean hasRecords, boolean isForLookup)  throws JspException, UnsupportedEncodingException  {
		boolean showPager = editorMetaData.isFooterPriv() || isForLookup;

		StringBuffer htmlL = new StringBuffer();
		StringBuffer htmlR = new StringBuffer();
		StringBuffer html2 = new StringBuffer();

		if (hasRecords && showPager) {
			int rowCount = rsIterator.getPageDesc().getRowCount();
			int currPageSize = rsIterator.getPageDesc().getPageSize();
			int currPage = (rsIterator.getPageDesc().getRangeMin() / currPageSize) + 1;
			int totalPages = (rowCount % currPageSize == 0) ? (rowCount / currPageSize) : (rowCount / currPageSize) + 1;
			String returningLabel = ResourceUtils.message(pageContext, "editor.list.returning", "Returning");
			String strpageLabel = ResourceUtils.message(pageContext, "editor.list.page", "Page");
			String strviewLabel = ResourceUtils.message(pageContext, "editor.list.view", "View");
			String exportInfoLabel = ResourceUtils.message(pageContext, "editor.list.exportinfo", "Export to view all records");
			String perPage= ResourceUtils.message(pageContext, "editor.list.perpage", " per page");
			String ofLabel= ResourceUtils.message(pageContext, "editor.list.oflabel", "of");

			htmlL.append(returningLabel).append("&nbsp;");
			htmlL.append(rsIterator.getPageDesc().getRangeMin());
			htmlL.append("&nbsp;-&nbsp;");
			htmlL.append(rsIterator.getPageDesc().getRangeMax());
			htmlL.append("&nbsp;").append(ofLabel).append("&nbsp;");
			htmlL.append(rsIterator.getPageDesc().getRowCount());
			htmlL.append("&nbsp;");
			if (hasRecords && editorMetaData.isExportPriv() && !isForLookup && (rowCount == getMaxRecords())) {
				htmlL.append(exportInfoLabel).append("&nbsp;");
			}
			htmlL.append("&nbsp;");
			htmlL.append("<a href=\"javascript:setPagingAction(");
			htmlL.append("'");
			htmlL.append(RSIterAction._FIRST_STR);
			htmlL.append("',");
			htmlL.append(rsIterator.getPageDesc().isFirst());
			htmlL.append(",'");
			htmlL.append(rsIterator.getPageDesc().getRangeMin());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getRowCount());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getPageSize());
			htmlL.append("')\">");
			htmlL.append("&#171;");
			htmlL.append("</a>");
			htmlL.append("&nbsp;");

			htmlL.append("<a href=\"javascript:setPagingAction(");
			htmlL.append("'");
			htmlL.append(RSIterAction._PREV_STR);
			htmlL.append("',");
			htmlL.append(rsIterator.getPageDesc().isFirst());
			htmlL.append(",'");
			htmlL.append(rsIterator.getPageDesc().getRangeMin());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getRowCount());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getPageSize());
			htmlL.append("')\">");
			htmlL.append("&#8249;");
			htmlL.append("</a>");

			htmlL.append("&nbsp;");

			htmlL.append(strpageLabel).append("&nbsp;");

			htmlL.append("<SELECT ").append("name=\"GOTOPAGE\"").append(" ONCHANGE=\"javascript:gotoPage('").append(RSIterAction._UNDEF_STR).append("',this.value").append(")\"").append(">");
			int currPos = -1;
			for (int page = 1; page <= totalPages; page++) {
				currPos = (page - 1) * rsIterator.getPageDesc().getPageSize() + 1;
				htmlL.append("<OPTION VALUE='").append(currPos).append("' ").append((page == currPage) ? "selected" : "").append(">");
				htmlL.append(page);
				htmlL.append("</OPTION>");
			}

			htmlL.append("</SELECT>&nbsp;&nbsp;");

			htmlL.append("<a href=\"javascript:setPagingAction(");
			htmlL.append("'");
			htmlL.append(RSIterAction._NEXT_STR);
			htmlL.append("',");
			htmlL.append(rsIterator.getPageDesc().isLast());
			htmlL.append(",'");
			htmlL.append(rsIterator.getPageDesc().getRangeMin());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getRowCount());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getPageSize());
			htmlL.append("')\">");
			htmlL.append("&#8250;");
			htmlL.append("</a>");
			htmlL.append("&nbsp;");

			htmlL.append("<a href=\"javascript:setPagingAction(");
			htmlL.append("'");
			htmlL.append(RSIterAction._LAST_STR);
			htmlL.append("',");
			htmlL.append(rsIterator.getPageDesc().isLast());
			htmlL.append(",'");
			htmlL.append(rsIterator.getPageDesc().getRangeMin());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getRowCount());
			htmlL.append("','");
			htmlL.append(rsIterator.getPageDesc().getPageSize());
			htmlL.append("')\">");
			htmlL.append("&#187;");
			htmlL.append("</a>");

			htmlR.append(strviewLabel).append("&nbsp;");

			List<Integer> pageSizes = new ArrayList<Integer>();
			for (int pSize = PAGE_MIN_SIZE; pSize <= PAGE_MAX_SIZE; pSize = pSize + PAGE_MIN_SIZE) {
				pageSizes.add(new Integer(pSize));
			}
			int pageSize = editorMetaData.getPageSize() > 0 ? editorMetaData.getPageSize() : 25;
            if (!pageSizes.contains(new Integer(pageSize))) {
            	pageSizes.add(new Integer(pageSize));
            }
			if (!pageSizes.contains(new Integer(currPageSize))) {
				pageSizes.add(new Integer(currPageSize));
			}
			tagUtils.sortInteger(pageSizes);
			htmlR.append("<SELECT ").append("name=\"PAGESIZE\"").append(" ONCHANGE=\"javascript:changePageSize(this.value)\"").append(">").append(newline);
			for (Integer pSize : pageSizes) {
				htmlR.append("<OPTION VALUE='").append(pSize).append("'").append((pSize == currPageSize) ? "selected" : "").append(">").append(String.valueOf(pSize).length() < 3 ? "&nbsp;&nbsp;" : "").append(pSize).append(" ").append(perPage).append("</OPTION>").append(newline);
			}
			htmlR.append("</SELECT>");

			htmlR.append("<input type=\"hidden\" name=\"PAGING_ACTION\" value=\"").append(RSIterAction._FIRST_STR).append("\">");
			htmlR.append("<input type=\"hidden\" name=\"POSITION\" value=\"").append(rsIterator.getPageDesc().getRangeMin()).append("\">");
		}

		if (!isForLookup) {
			String heading = ResourceUtils.editorTitle(pageContext, editorMetaData.getName(), editorMetaData.getDesc());
			if (StrUtl.isEmptyTrimmed(heading)) {
				heading = editorMetaData.getName();
			}

			if (editorMetaData.isCustomDisplayPriv()) {
				String saveTemplateLabel = ResourceUtils.message(pageContext, "editor.list.savetemplate", "Save Template");
				String tplName = getTemplateName();
				String selected = "";
				List<String> sharedTemplateNames = editorMetaData.getSharedUserTemplateNames();
				List<String> templateNames = editorMetaData.getUserTemplateNames();
				if (templateNames != null && templateNames.size() > 0) {
					html2.append("<SELECT name='templateName'  id='templateName' onChange=\"doApplyTemplate();\" class=\"template\">");
					html2.append("<OPTION VALUE=''> </OPTION>");
					for (String templateName : templateNames) {
						selected = templateName.equals(tplName) ? "selected" : "";
						html2.append("<OPTION ");
						html2.append(" VALUE='" + templateName + "' " + selected);
						if (sharedTemplateNames != null && sharedTemplateNames.contains(templateName)) {
							html2.append(" class='inactive'");
						}
						html2.append(">");
						html2.append(templateName).append("</OPTION>");
					}
					html2.append("</SELECT>");
				}
				else {
					html2.append("<input");
					html2.append(" type=\"hidden\"");
					html2.append(" name=\"templateName\"");
					html2.append(" id=\"templateName\"");
					html2.append(" value=\"\"");
					html2.append("/>");
				}
				html2.append("<input");
				html2.append(" type=\"hidden\"");
				html2.append(" name=\"" + getResizableColumnsWidthName() + "\"");
				html2.append(" id=\"" + getResizableColumnsWidthName() + "\"");
				html2.append(" value=\"");
				html2.append(getResizableColumnsWidth());
				html2.append("\"");
				html2.append("/>");
				html2.append("<div class=\"buttonGrayOnWhite\" onclick=\"javascript:launchModelEditorTemplate('").append(heading).append("');\"><a href=\"#\" onclick=\"return false;\" style=\"cursor:hand;\">").append(saveTemplateLabel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
			}
			if (hasRecords && editorMetaData.isExportPriv()) {
				String exportLabel = ResourceUtils.message(pageContext, "editor.list.export", "Export");
				html2.append("<div class=\"buttonGrayOnWhite\" onclick=\"javascript:currObj=event.srcElement;launchModelExportLookup('").append(heading).append("');\" ><a href=\"#\" onclick=\"return false;\">").append(exportLabel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
			}
			if (editorMetaData.isScheduledExportPriv()) {
				String scheduleexportLabel = ResourceUtils.message(pageContext, "editor.list.scheduleexport", "Schedule Export");
				html2.append("<div class=\"buttonGrayOnWhite\" onclick=\"javascript:currObj=event.srcElement;launchModelScheduleExportLookup('").append(heading).append("');\" ><a id=\"scheduleExportButton\" href=\"#\" onclick=\"return false;\">").append(scheduleexportLabel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
			}
			StringBuffer buildQuery = new StringBuffer(whereClause);
			String addBtnHtml = getAddLink(buildQuery);

			if(addBtnHtml != null){
				html2.append(addBtnHtml);
			}
		}

		StringBuffer html = new StringBuffer();
		if (htmlL.length() + htmlR.length() + html2.length()  > 0) {
			html.append(newline);
			html.append("<table class=\"pager\">").append(newline);
			if (html2.length() > 0) {
				html.append("<tr>");
				html.append("<td class=\"rightpart\" colspan=\"2\"><nobr>");
				html.append(html2.toString());
				html.append("</nobr></td>");
				html.append("</tr>");
			}
			if (htmlL.length() + htmlR.length()> 0) {
				html.append("<tr>");
				if (htmlL.length()> 0) {
					html.append("<td class=\"leftpart\"><nobr>");
					html.append(htmlL.toString());
					html.append("</nobr></td>");
				}
				if (htmlR.length()> 0) {
					html.append("<td class=\"rightpart\"><nobr>");
					html.append(htmlR.toString());
					html.append("</nobr></td>");
				}
				html.append("</tr>");
			}
			html.append("</table>").append(newline);
		}

		return html.toString();
	}

	private String getAddLink(StringBuffer buildQuery) throws JspException, UnsupportedEncodingException {
		if (isAddNewPriv() && isEditPermission()) {
			StringBuffer html = new StringBuffer();
			String pageLink = addlink;
			pageLink += pageLink.indexOf("?") != -1 ? "&" : "?";
			if (buildQuery.toString().length() > 0) {
				pageLink += buildQuery.substring(1);
			}

			String linkText = ResourceUtils.message(pageContext, "editor.list.addnew", "Add New");
			if (!StrUtl.isEmptyTrimmed(editorMetaData.getAddlinkImage())) {
				linkText = editorMetaData.getAddlinkImage();
			}
			boolean hasBaseSearch = editorMetaData.hasBaseSearch(); 

			String addNewStyle=(rsIterator != null)?"buttonGrayOnWhite" : "buttonGrayOnGray";
			if(hasBaseSearch){
				addNewStyle ="";
			}
			html.append("<div id=\"BTNAddNew\" class=\""+addNewStyle+"\" onclick=\"javascript:launchLink(\'" +pageLink + "\')\">");
			html.append("<a href=\"#\" onclick=\"return false;\">");
			if(!hasBaseSearch){
				html.append(linkText);
			}
			html.append("</a>");
			html.append("<div class=\"rightImg\">").append("</div>").append("</div>");
			return html.toString();
		}
		return null;
	}

	private String getCancelLink(StringBuffer buildQuery) throws JspException, UnsupportedEncodingException {
		boolean cancelPriv = (editorMetaData.isCancelPriv() && !StrUtl.isEmptyTrimmed(this.cancellink));
		if (cancelPriv) {
			StringBuffer html = new StringBuffer();
			String pageLink = cancellink;
			pageLink += pageLink.indexOf("?") != -1 ? "&" : "?";
			if (buildQuery.toString().length() > 0) {
				pageLink += buildQuery.substring(1);
			}

			String linkText = ResourceUtils.message(pageContext, "editor.list.cancel", "Cancel");
			if (!StrUtl.isEmptyTrimmed(editorMetaData.getCancellinkImage())) {
				linkText = editorMetaData.getCancellinkImage();
			}

			html.append("<div class=\"buttonGrayOnWhite\" onclick=\"javascript:launchLink(\'" + pageLink + "\')\" >");
			html.append("<a href=\"#\" onclick=\"return false;\">");
			html.append(linkText);
			html.append("</a>");
			html.append("<div class=\"rightImg\">").append("</div>").append("</div>");
			return html.toString();
		}
		return null;
	}

	private String getMultiDeleteLink(StringBuffer buildQuery) throws JspException {
		boolean multiDeletePriv = (editorMetaData.isMultiDeletePriv() && (rsIterator != null && rsIterator.getPageDesc().getRowCount() > 0)) && !isForLookup();
		if (multiDeletePriv && !StrUtl.isEmptyTrimmed(deletelink)) {
			String linkText = ResourceUtils.message(pageContext, "editor.list.delete", "Delete");
			if (!StrUtl.isEmptyTrimmed(editorMetaData.getDeletelinkImage())) {
				linkText = editorMetaData.getDeletelinkImage();
			}
			String doDeleteFn ="";
			if(deletelink.startsWith("javascript")){
				doDeleteFn = deletelink; 
			}
			else {
				String pageLink = deletelink;
				pageLink += pageLink.indexOf("?") != -1 ? "&" : "?";
				if (buildQuery.toString().length() > 0) {
					pageLink += buildQuery.substring(1);
				}
				doDeleteFn= "javascript:doDelete(\'" + pageLink + "\')";
			}
			StringBuffer html = new StringBuffer();
			html.append("<div id=\"multideletebtn\" class=\"buttonGrayOnWhite\" onclick=\""+ doDeleteFn + "\" >");
			html.append("<a href=\"#\" onclick=\"return false;\">");
			html.append(linkText);
			html.append("</a>");
			html.append("<div class=\"rightImg\">").append("</div>").append("</div>");
			return html.toString();
		}
		return null;
	}
	private boolean isAddNewPriv(){
		return (editorMetaData.isAddNewPriv() && !StrUtl.isEmptyTrimmed(this.addlink));
	}
	private String getDisplayListBodyIdPrefix() {
		return editorMetaData.getName() + "_DISPLAYLIST_BODY";
	}

	private String getTemplateName() {
		if(pageContext.getRequest().getParameter("templateName") == null){
			String userId = ((HttpServletRequest) pageContext.getRequest()).getUserPrincipal().getName();
			String userTemplateKey = getUserEditorKey(editorMetaData.getName(), editorMetaData.getType(), userId);
			Map<String, String> userTemplates = (Map<String, String>) pageContext.getSession().getAttribute("USER_TEMPLATES");
			if (userTemplates != null && userTemplates.containsKey(userTemplateKey)) {
				return userTemplates.get(userTemplateKey);
			}
		}
		return pageContext.getRequest().getParameter("templateName");
	}
	private String getUserEditorKey(String editorName, String editorType, String userId) {
		return editorName.toUpperCase() + "-" + editorType.toUpperCase() + "-" + ((userId == null) ? "DEFAULT" : userId.toUpperCase());
	}
}
