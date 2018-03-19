package com.addval.aswtaglib;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.sf.json.JSONObject;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.springstruts.ResourceUtils;
import com.addval.ui.UIComponentMetadata;
import com.addval.utils.StrUtl;
import com.addval.utils.MetaDataUtils;

public class SearchTag extends GenericBodyTag {
	private static final long serialVersionUID = -6255773462483845080L;
	private ASWTagUtils tagUtils = null;
	private EditorMetaData editorMetaData = null;
	private String formName = null;
	private boolean editPermission = true;
	private boolean addNewPermission = true;
	private boolean shareToUserGroup = true;

	public SearchTag() {
		tagUtils = new ASWTagUtils(ASWTagUtils._SEARCH);
	}

	public void setMetaRefId(String metaRefId) throws JspTagException {
		Object object = pageContext.getAttribute(metaRefId, getScope());
		if (object != null && object instanceof EditorMetaData) {
			editorMetaData = (EditorMetaData) object;
		}
		else {
			throw new IllegalArgumentException("SearchTag: MetaRefId " + metaRefId + " invalid. ");
		}
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public boolean isEditPermission() {
		return editPermission;
	}

	public void setEditPermission(boolean editPermission) {
		this.editPermission = editPermission;
	}
		
	public boolean isAddNewPermission() {
		return addNewPermission;
	}

	public void setAddNewPermission(boolean addNewPermission) {
		this.addNewPermission = addNewPermission;
	}

	public boolean isShareToUserGroup() {
		return shareToUserGroup;
	}

	public void setShareToUserGroup(boolean shareToUserGroup) {
		this.shareToUserGroup = shareToUserGroup;
	}

	public int doStartTag() throws JspTagException {
		StringBuffer html = new StringBuffer();
		Hashtable<String, StringBuffer> sectionHash = new Hashtable<String, StringBuffer>();
		sectionHash.put("1", new StringBuffer());
		sectionHash.put("2", new StringBuffer());
		sectionHash.put("3", new StringBuffer());
		try {
			String filterLabel = ResourceUtils.message(pageContext, "editor.filter", "Filter");
			String updateLabel = ResourceUtils.message(pageContext, "editor.update", "Update");
			String resetLabel = ResourceUtils.message(pageContext, "editor.filter.reset", "Reset");

			Vector<ColumnMetaData> searchableColumns = editorMetaData.getSearchableColumns();

			if (searchableColumns != null) {

				String heading = ResourceUtils.editorTitle(pageContext, editorMetaData.getName(), editorMetaData.getDesc());
				if (StrUtl.isEmptyTrimmed(heading)) {
					heading = editorMetaData.getName();
				}

				// heading = !StrUtl.isEmptyTrimmed(editorMetaData.getDesc()) ? editorMetaData.getDesc() : editorMetaData.getName();
				heading += ResourceUtils.message(pageContext, "editor.filter.heading.suffix", " - Filter");

				boolean hasAdvancedMultiRowSearch = editorMetaData.hasAdvancedMultiRowSearch();
				Integer searchColsPerRow = editorMetaData.getSearchColsPerRow() > 0 ? Integer.valueOf(editorMetaData.getSearchColsPerRow()) : new Integer(3);

				Hashtable<String, Integer> colsPerRowHash = new Hashtable<String, Integer>();

				colsPerRowHash.put("1", searchColsPerRow);
				colsPerRowHash.put("2", searchColsPerRow);

				StringBuffer section = null;
				Integer colsPerRow = null;

				for (ColumnMetaData columnMetaData : searchableColumns) {
					if (!columnMetaData.isBaseSearch() && !columnMetaData.isAdvancedSearch()) {
						continue;
					}

					if (columnMetaData.isBaseSearch()) {
						section = sectionHash.get(String.valueOf(1));
						colsPerRow = colsPerRowHash.get(String.valueOf(1));
					}
					else if (columnMetaData.isAdvancedSearch()) {
						section = sectionHash.get(String.valueOf(2));
						colsPerRow = colsPerRowHash.get(String.valueOf(2));
					}

					section.append("<td><nobr>");
					section.append("<p>").append(tagUtils.getSearchMandatory(columnMetaData)).append(tagUtils.getCaption(pageContext, columnMetaData)).append("</p>");
					section.append(tagUtils.getSearchHtmlControl(pageContext, editorMetaData, columnMetaData, getOperatorValue(columnMetaData), getColumnValue(columnMetaData), isSecured(columnMetaData), isForLookup()));
					section.append("</nobr></td>");

					colsPerRow--;

					if (colsPerRow.equals(new Integer(0))) {
						colsPerRow = searchColsPerRow;
						section.append("</tr><tr>");
					}

					if (columnMetaData.isBaseSearch()) {
						colsPerRowHash.put("1", colsPerRow);
					}
					else if (columnMetaData.isAdvancedSearch()) {
						colsPerRowHash.put("2", colsPerRow);
					}
				}

				if (hasAdvancedMultiRowSearch) {
					MultiRowTag multiRowTag = new MultiRowTag();
					multiRowTag.setPageContext(pageContext);
					multiRowTag.setName("searchColumns");
					multiRowTag.setFormName(this.formName);
					multiRowTag.setAddButtonOutsideFieldset("true");
					multiRowTag.setFieldNamePrefix("searchColumns");
					multiRowTag.setHeadingOption("true");
					
					if(hasComboInAdvancedMultiRowSearch()){
						multiRowTag.setFieldNames("columnName,columnOperator,columnOption,columnValue,lookupImg,calImg");
					}
					else {
						multiRowTag.setFieldNames("columnName,columnOperator,columnValue,lookupImg,calImg");
					}

					StringBuffer multiRow = new StringBuffer();
					multiRow.append("<div class=\"columnB\" style=\"padding-left:0px;width:98%;\">").append(newline);
					multiRow.append(multiRowTag.getHtmlOut());
					multiRow.append("</div>").append(newline);

					sectionHash.get("3").append(multiRow.toString());
				}

				if (!isForLookup()) {
					StringBuffer columnDHeading= new StringBuffer();
					if((isAddNewPermission() && isEditPermission()) || editorMetaData.isBulkUploadPriv()){
						columnDHeading.append("<div class=\"buttonClear_topRight\">");
						if(editorMetaData.isBulkUploadPriv()){
							String linkText = ResourceUtils.message(pageContext, "editor.list.bulkupload", "Bulk Upload");
							if (!StrUtl.isEmptyTrimmed(editorMetaData.getBulkUploadImage())) {
								linkText = editorMetaData.getBulkUploadImage();
							}
							String editorName = editorMetaData.getName();
							String editorDesc = ResourceUtils.editorTitle(pageContext, editorMetaData.getName(), editorMetaData.getDesc());
							if (StrUtl.isEmptyTrimmed(editorDesc)) {
								editorDesc = editorMetaData.getName();
							}
							columnDHeading.append("<div class=\"buttonGrayOnGray\" onClick=\"javascipt:openBulkUploadWindow('"+editorName+"_LOADER','"+editorDesc+"');\"><a href=\"#\" onClick=\"return false;\">").append(linkText).append("</a><div class=\"rightImg\"></div></div>");	
						}
						if(isAddNewPermission() && isEditPermission()){
							String linkText = ResourceUtils.message(pageContext, "editor.list.addnew", "Add New");
							if (!StrUtl.isEmptyTrimmed(editorMetaData.getAddlinkImage())) {
								linkText = editorMetaData.getAddlinkImage();
							}
							columnDHeading.append("<div class=\"buttonGrayOnGray\" onClick=\'javascipt:($$(\"div[id^=BTNAddNew]\").first()).click();\'><a href=\"#\" onClick=\"return false;\">").append(linkText).append("</a><div class=\"rightImg\"></div></div>");	
						}
						columnDHeading.append("</div>");
					}
					String filterExpandCollapseImgId = editorMetaData.getName() + "_FILTER_EXPAND_COLLAPSE";
					String bodyFindExpr = "div[id^=" + getFilterBodyIdPrefix() + "]";
					String imgFindExpr = "img[id=" + filterExpandCollapseImgId + "]";
					columnDHeading.append("<div class=\"filter\" onClick=\"showHide('" + bodyFindExpr + "','" + imgFindExpr + "');return false;\"><nobr><img id=\"" + filterExpandCollapseImgId + "\" src=\"../images/collapse1.jpg\"/>" + heading + "</nobr></div>");
					html.append(tagUtils.getGroupBoxTop("columnD", columnDHeading.toString()));
				}

				section = sectionHash.get(String.valueOf(1));
				if (section.length() > 0) { // Base Filter
					if (isForLookup()) {
						html.append("<div id=\"").append(getFilterBodyIdPrefix()).append("\" >");
					}
					else {
						html.append("<div class=\"outerborder\" id=\"").append(getFilterBodyIdPrefix()).append("\" >");
					}

					html.append("<div class=\"formLayout noBottomMargin \">").append(newline);
					colsPerRow = colsPerRowHash.get(String.valueOf(1));
					html.append("<table class=\"tablelayout\">");
					html.append("<tr>");
					if (colsPerRow.equals(searchColsPerRow) && section.toString().endsWith("</tr><tr>")) {
						section = section.replace(section.lastIndexOf("</tr><tr>"), section.lastIndexOf("</tr><tr>") + "</tr><tr>".length(), "");
					}
					html.append(section.toString());
					html.append("</tr>");
					html.append("</table>");
					html.append("</div>").append(newline);
				}

				if (sectionHash.get(String.valueOf(2)).length() > 0 || sectionHash.get(String.valueOf(3)).length() > 0) {
					html.append("<div class=\"sectionTitle\">").append(newline);

					String hideadditionalCriteria = ResourceUtils.message(pageContext, "editor.filter.hide-additionalcriteria", "Hide Additional Criteria");
					String additionalCriteria = ResourceUtils.message(pageContext, "editor.filter.additionalcriteria", "Additional Criteria");
					boolean advancedSearchExpandFlag = pageContext.getRequest().getParameter("advancedSearchExpandFlag") != null ? (new Boolean(pageContext.getRequest().getParameter("advancedSearchExpandFlag"))).booleanValue() : false;

					html.append("<input type=\"hidden\" name=\"advancedSearchExpandFlag\" value=\"").append(advancedSearchExpandFlag).append("\" />");

					html.append("<div onclick=\"toggleSection('advancedSearch');document.forms[0].advancedSearchExpandFlag.value=(this.className != 'toggleImageClosed');\" id=\"advancedSearchImg\" title=\"").append(hideadditionalCriteria).append("\" class=\"toggleImageClosed\"></div>")
							.append(newline);
					html.append("<span>").append(additionalCriteria).append("</span>").append(newline);
					html.append("</div>").append(newline); // sectionTitle

					html.append("<div style=\"display: none; overflow: hidden; width: 98%; margin-left: 1%;\" id=\"advancedSearch\" class=\"colWrapper\">").append(newline);
					html.append("<div class=\"formLayout noBottomMargin\">").append(newline);
					section = sectionHash.get(String.valueOf(2));
					if (section.length() > 0) {
						colsPerRow = colsPerRowHash.get(String.valueOf(2));
						// Advanced filter columns here.
						html.append("<table class=\"tablelayout\">");
						html.append("<tr>");
						if (colsPerRow.equals(searchColsPerRow) && section.toString().endsWith("</tr><tr>")) {
							section = section.replace(section.lastIndexOf("</tr><tr>"), section.lastIndexOf("</tr><tr>") + "</tr><tr>".length(), "");
						}
						html.append(section.toString());
						html.append("</tr>");
						html.append("</table>").append(newline);
					}
					section = sectionHash.get(String.valueOf(3));
					if (section.length() > 0) {
						html.append(section.toString());
					}
					html.append("</div>").append(newline);
					html.append("</div>").append(newline);
				}

				boolean isMassUpdate = pageContext.getRequest().getParameter("MassUpdate") != null ? new Boolean(pageContext.getRequest().getParameter("MassUpdate")).booleanValue() : false;

				if (isMassUpdate && isEditPermission()) {
					html.append("<div class=\"sectionTitle\">").append(newline);
					String hideupdatecriteria = ResourceUtils.message(pageContext, "editor.update.hide-updatecriteria", "Hide Update Criteria");
					String updatecriteria = ResourceUtils.message(pageContext, "editor.update.updatecriteria", "Update Criteria");
					boolean updatecriteriaExpandFlag = pageContext.getRequest().getParameter("updatecriteriaExpandFlag") != null ? (new Boolean(pageContext.getRequest().getParameter("updatecriteriaExpandFlag"))).booleanValue() : false;

					html.append("<input type=\"hidden\" name=\"updatecriteriaExpandFlag\" value=\"").append(updatecriteriaExpandFlag).append("\" />");

					html.append("<div onclick=\"toggleSection('updatecriteria');document.forms[0].updatecriteriaExpandFlag.value=(this.className != 'toggleImageClosed');\" id=\"updatecriteriaImg\" title=\"").append(hideupdatecriteria).append("\" class=\"toggleImageClosed\"></div>").append(newline);
					html.append("<span>").append(updatecriteria).append("</span>").append(newline);
					html.append("</div>").append(newline); // sectionTitle

					html.append("<div style=\"display: none; overflow: hidden; width: 98%; margin-left: 1%;\" id=\"updatecriteria\" class=\"colWrapper\">").append(newline);
					html.append("<div class=\"formLayout noBottomMargin\">").append(newline);

					MultiRowTag multiRowTag = new MultiRowTag();
					multiRowTag.setPageContext(pageContext);
					multiRowTag.setName("updateColumns");
					multiRowTag.setFormName(this.formName);
					multiRowTag.setAddButtonOutsideFieldset("true");
					multiRowTag.setFieldNamePrefix("updateColumns");
					multiRowTag.setHeadingOption("true");

					if(hasComboInUpdatable()){
						multiRowTag.setFieldNames("columnName,columnOperator,columnOption,columnValue,lookupImg,calImg");
					}
					else {
						multiRowTag.setFieldNames("columnName,columnOperator,columnValue,lookupImg,calImg");
					}

					html.append("<div class=\"columnB\" style=\"padding-left:0px;width:98%;\">").append(newline);
					html.append(multiRowTag.getHtmlOut());
					html.append("</div>").append(newline);

					html.append("</div>").append(newline);
					html.append("</div>").append(newline);
				}

				if (!isForLookup()) {
					String minimizeFilterLabel = ResourceUtils.message(pageContext, "editor.filter.minimizeFilter", "Minimize after Filter");
					boolean minimizeFilter = pageContext.getRequest().getParameter("minimizeFilter") != null ? (new Boolean(pageContext.getRequest().getParameter("minimizeFilter"))).booleanValue() : true;
					String minimizeFilterChecked = minimizeFilter ? "checked" : "";
					html.append("<div class=\"buttonClear_bottomRight noTopMargin\" id=\"").append(getFilterBodyIdPrefix()).append("\" >").append(newline);
					html.append(getSaveFilter(true));
					html.append("<div class=\"buttonOrangeOnWhite\" onclick=\"javascript:doReset();\"><a href=\"#\" onclick=\"return false;\">").append(resetLabel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					html.append("<div class=\"buttonOrangeOnWhite\" onclick=\"javascript:doSubmit();\"><a href=\"#\" onclick=\"return false;\" id=\"Filter\">").append(filterLabel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					if(isMassUpdate && isEditPermission()){
						html.append("<div class=\"buttonOrangeOnWhite\" onclick=\"javascript:doUpdate();\"><a href=\"#\" onclick=\"return false;\" id=\"update\">").append(updateLabel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					}
					html.append("<input type=\"hidden\" name=\"minimizeFilter\" value=\"").append(minimizeFilter).append("\" />");
					html.append("<input type=\"checkbox\" name=\"minimizeFilterFlag\"").append(minimizeFilterChecked).append(" onclick=\"document.forms[0].minimizeFilter.value=this.checked;\" style=\"float:right;\"/>");
					html.append("<label class=\"label\" style=\"float:right;\">").append(minimizeFilterLabel).append("</label>");
					html.append("</div>").append(newline);
				}
				html.append("</div>").append(newline);
				if (!isForLookup()) {
					html.append(tagUtils.getGroupBoxBottom());
				}
			}
			html.append(getSaveFilter(false));

			html.append("<SCRIPT language=\"javascript\">").append(newline);
			html.append("function searchFormValid(form) {").append(newline);
			html.append(tagUtils.getSearchFormValidScript(pageContext, editorMetaData, isForLookup())).append(newline);
			html.append("return true;").append(newline);
			html.append("}").append(newline);

			html.append("</SCRIPT>").append(newline);

			pageContext.getOut().write(html.toString());
			return SKIP_BODY;
		}
		catch (JspException jspe) {
			try {
				pageContext.getOut().write(jspe.toString());
				return SKIP_BODY;
			}
			catch (IOException ioe) {
				throw new JspTagException("SearchTag Error:" + ioe.toString());
			}
		}
		catch (Exception e) {
			try {
				pageContext.getOut().write(e.toString());
				return SKIP_BODY;
			}
			catch (IOException ioe) {
				throw new JspTagException("SearchTag Error:" + ioe.toString());
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
		super.release();
	}

	private String getSaveFilter(boolean isApplyFilter) throws JspException {
		if (isForLookup()) {
			return "";
		}
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String userGroupName = tagUtils.getUserGroupName(request);
		String selected = "";
		String searchFilterName = pageContext.getRequest().getParameter("searchFilterName");
		String newSearchFilterName = pageContext.getRequest().getParameter("newSearchFilterName");
		boolean isSaveFilter = pageContext.getRequest().getAttribute("SaveFilter") != null;
		boolean isDeleteFilter = pageContext.getRequest().getAttribute("DeleteFilter") != null;
		boolean isConfigRole = request.isUserInRole("config");

		if (StrUtl.isEmptyTrimmed(newSearchFilterName)) {
			newSearchFilterName = "";
		}

		if (isDeleteFilter) {
			searchFilterName = "";
		}

		if (isSaveFilter && !StrUtl.isEmptyTrimmed(newSearchFilterName)) {
			searchFilterName = newSearchFilterName;
			newSearchFilterName = "";
		}

		StringBuffer html = new StringBuffer();

		if (isApplyFilter) {
			String saveFilterLabel = ResourceUtils.message(pageContext, "editor.filter.save", "Save Filter");
			String deleteFilterLabel = ResourceUtils.message(pageContext, "editor.filter.delete", "Delete Filter");

			List<String> sharedFilterNames = editorMetaData.getSharedSearchFilterNames();
			List<String> filterNames = editorMetaData.getUserSearchFilterNames();

			boolean isShared = (sharedFilterNames != null && sharedFilterNames.contains(searchFilterName));
			
			 if (filterNames != null && filterNames.size() > 0) {
				if (!StrUtl.isEmptyTrimmed(searchFilterName) && !isShared) {
					html.append("<div id=\"deleteFilterBtn\">");
					html.append("<DIV class=\"buttonOrangeOnWhite\" onclick=\"javascript:if(validateDeleteFilter()){doDeleteFilter();}\"><A href=\"#\" onclick=\"return false;\">").append(deleteFilterLabel).append("</A><DIV class=\"rightImg\"></DIV></DIV>");
					html.append("</div>");
				}				
				
				html.append("<div style=\"float:right;\">");
				html.append("<SELECT name='searchFilterName'  id='searchFilterName' onChange=\"doApplyFilter();\">");
				html.append("<OPTION VALUE=''> </OPTION>");
				tagUtils.sortString(filterNames);
				for (String filterName : filterNames) {
					selected = filterName.equals(searchFilterName) ? "selected" : "";
					html.append("<OPTION ");
					html.append(" VALUE='" + filterName + "' " + selected);
					if (sharedFilterNames != null && sharedFilterNames.contains(filterName)) {
						html.append(" class='inactive'");
					}
					html.append(">");
					html.append(filterName).append("</OPTION>");
				}
				html.append("</SELECT>");
				html.append("</div>");
			}
			html.append("<div class=\"buttonOrangeOnWhite\" onclick=\"javascript:launchModelSaveFilter()\"><a href=\"#\" onclick=\"return false;\">").append(saveFilterLabel).append("</a><div class=\"rightImg\"></div>").append("</div>").append(newline);
		}
		else {
			String saveFilterLabel = ResourceUtils.message(pageContext, "editor.filter.save", "Save Filter");
			String newFilterNameLabel = ResourceUtils.message(pageContext, "editor.filter.newname", "New Filter Name");
			String filterNameLabel = ResourceUtils.message(pageContext, "editor.filter.name", "Filter Name");
			String sharetogroupLabel = ResourceUtils.message(pageContext, "editor.filter.sharetogroup", "Share to User Group");
			String sharetoallLabel = ResourceUtils.message(pageContext, "editor.filter.sharetoall", "Share to All Users");
			String cancelLabel=ResourceUtils.message(pageContext, "editor.filter.cancel", "Cancel");

			String shareToAllUsers = pageContext.getRequest().getParameter("shareToAllUsers");
			String shareToUserGroup = pageContext.getRequest().getParameter("shareToUserGroup");
			String shareToUserGroupName = pageContext.getRequest().getParameter("shareToUserGroupName");
			if (StrUtl.isEmptyTrimmed(shareToUserGroupName)) {
				shareToUserGroupName = "";
			}
			String shareToAllUsersChecked = !StrUtl.isEmptyTrimmed(shareToAllUsers) ? "checked" : "";
			String shareToUserGroupChecked = !StrUtl.isEmptyTrimmed(shareToUserGroup) ? "checked" : "";

			if (!StrUtl.isEmptyTrimmed(searchFilterName) && editorMetaData.getUserSearchFilters() != null) {
				UIComponentMetadata uiCmd = MetaDataUtils.findUIComponentMetadata(editorMetaData.getUserSearchFilters(), searchFilterName);
				if (uiCmd != null) {
					JSONObject savedFilter = JSONObject.fromObject(uiCmd.getJsonString());
					if (savedFilter.containsKey("shareToAllUsers") && savedFilter.getBoolean("shareToAllUsers")) {
						shareToAllUsersChecked = "checked";
					}
					if (savedFilter.containsKey("shareToUserGroup") && savedFilter.getBoolean("shareToUserGroup")) {
						shareToUserGroupChecked = "checked";
						shareToUserGroupName = savedFilter.containsKey("shareToUserGroupName") ? savedFilter.getString("shareToUserGroupName") : "";
					}
				}
			}

			html.append("<div style=\"display: none;\" id=\"SaveFilterSection\">").append(newline);
			html.append(tagUtils.getGroupBoxTop("columnB", null, null, "min-width:100px;"));

			html.append("<DIV id=\"button\">").append(newline);
			html.append("<DIV class=\"buttonClear_topRight\">").append(newline);
			html.append("<div class=\"buttonBlueOnBlue\" style=\"float:right;\" onClick=\"javascipt:savefilterwindow.hide();\"><a href=\"#\" onClick=\"return false;\">").append(cancelLabel).append("</a><div class=\"rightImg\"></div></div>");
			html.append("<DIV class=\"buttonBlueOnBlue\" style=\"float:right;\" onclick=\"javascript:if(validateSaveFilter()){doSaveFilter();}\"><A href=\"#\" onclick=\"return false;\">").append(saveFilterLabel).append("</A><DIV class=\"rightImg\"></DIV></DIV>");
			html.append("</DIV>").append(newline);
			html.append("</DIV>").append(newline);

			html.append("<div class=\"formLayout noBottomMargin\" style=\"float:left;\">").append(newline);
			html.append("<table class=\"tablelayout\" id=\"BODY\">").append(newline);
			html.append("<tr>").append(newline);

			html.append("<td><nobr><p>").append(newFilterNameLabel).append("</p>");
			html.append("<input");
			html.append(" type=\"text\"");
			html.append(" name=\"newSearchFilterName\"");
			html.append(" id=\"newSearchFilterName\"");
			html.append(" value=\"").append(newSearchFilterName).append("\"");
			html.append(" size=\"30\"");
			html.append(" maxlength=\"30\"");
			html.append("/>");
			html.append("</nobr></td>").append(newline);

			html.append("<td id=\"filterName\" style=\"vertical-align: top;\"><nobr><p>");
			html.append(filterNameLabel).append("</p>");
			html.append("<span id=\"filterValue\"></span></nobr></td>").append(newline);

			if(isShareToUserGroup()){
				html.append("<td><nobr><p>").append(sharetogroupLabel).append("</p>");
				html.append("<input");
				html.append(" type=\"CHECKBOX\"");
				html.append(" name=\"shareToUserGroup\"");
				html.append(" id=\"shareToUserGroup\"");
				html.append(" onclick=\"setShareToFlag(this)\"");
				html.append(" value=\"SHARETOUSERGROUP\"");
				html.append(" ").append(shareToUserGroupChecked);
				html.append("/>");
				if (isConfigRole) {
					html.append("<input");
					html.append(" type=\"text\"");
					html.append(" name=\"shareToUserGroupName\"");
					html.append(" id=\"shareToUserGroupName\"");
					html.append(" value=\"").append(shareToUserGroupName).append("\"");
					html.append(" size=\"30\"");
					html.append(" maxlength=\"30\"");
					html.append("/>");
				}
				else {
					html.append("<input");
					html.append(" type=\"hidden\"");
					html.append(" name=\"shareToUserGroupName\"");
					html.append(" id=\"shareToUserGroupName\"");
					html.append(" value=\"").append(userGroupName).append("\"");
					html.append("/>");
				}
				html.append("</nobr></td>").append(newline);
				if (isConfigRole) {
					html.append("<td><nobr><p>").append(sharetoallLabel).append("</p>");
					html.append("<input");
					html.append(" type=\"CHECKBOX\"");
					html.append(" name=\"shareToAllUsers\"");
					html.append(" id=\"shareToAllUsers\"");
					html.append(" onclick=\"setShareToFlag(this)\"");
					html.append(" value=\"SHARETOALLUSERS\"");
					html.append(" ").append(shareToAllUsersChecked);
					html.append("/>");
					html.append("</nobr></td>").append(newline);
				}
			}
			html.append("</table>").append(newline);
			html.append("</div>").append(newline);
			html.append(tagUtils.getGroupBoxBottomWithoutFooter());
			html.append("</div>").append(newline);
		}
		return html.toString();
	}

	private boolean isForLookup() {
		return getId().toUpperCase().indexOf("LOOKUP") != -1;
	}

	private boolean isSecured(ColumnMetaData columnMetaData) {
		return columnMetaData.isSecured() && pageContext.getRequest().getAttribute(columnMetaData.getName() + "_unlock") == null;
	}

	private String getFilterBodyIdPrefix() {
		return editorMetaData.getName() + "_FILTER_BODY";
	}

	private String getColumnValue(ColumnMetaData columnMetaData) {
		String columnName = columnMetaData.getName();
		String columnValue = null;
		if (isForLookup()) {
			columnValue = pageContext.getRequest().getParameter(columnName + "_edit") != null ? (String) pageContext.getRequest().getParameter(columnName + "_edit") : "";
			if (StrUtl.isEmptyTrimmed(columnValue)) {
				columnValue = pageContext.getRequest().getParameter(columnName + "_lookUp") != null ? (String) pageContext.getRequest().getParameter(columnName + "_lookUp") : "";
			}
			if (StrUtl.isEmptyTrimmed(columnValue)) {
				columnValue = pageContext.getRequest().getParameter(columnName + "_look") != null ? (String) pageContext.getRequest().getParameter(columnName + "_look") : "";
			}
			if (StrUtl.isEmptyTrimmed(columnValue)) {
				columnValue = pageContext.getRequest().getParameter(columnName + "_search") != null ? (String) pageContext.getRequest().getParameter(columnName + "_search") : "";
			}
		}
		if (StrUtl.isEmptyTrimmed(columnValue)) {
			columnValue = pageContext.getRequest().getAttribute(columnName + "_lookUp") != null ? (String) pageContext.getRequest().getAttribute(columnName + "_lookUp") : "";
		}
		if (StrUtl.isEmptyTrimmed(columnValue)) {
			columnValue = pageContext.getRequest().getParameter(columnName + "_lookUp") != null ? pageContext.getRequest().getParameter(columnName + "_lookUp") : "";
		}
		if (StrUtl.isEmptyTrimmed(columnValue) && columnMetaData.isCombo() && columnMetaData.getComboSelect() != null && !columnMetaData.getComboSelect().equalsIgnoreCase(columnName)) {
			columnValue = pageContext.getRequest().getAttribute(columnMetaData.getComboSelect() + "_lookUp") != null ? (String) pageContext.getRequest().getAttribute(columnMetaData.getComboSelect() + "_lookUp") : "";
			if (StrUtl.isEmptyTrimmed(columnValue)) {
				columnValue = pageContext.getRequest().getParameter(columnMetaData.getComboSelect() + "_lookUp") != null ? pageContext.getRequest().getParameter(columnMetaData.getComboSelect() + "_lookUp") : "";
			}
		}
		if (columnValue.endsWith("%")) {
			columnValue = columnValue.substring(0, columnValue.length() - 1);
		}
		return columnValue;
	}

	private String getOperatorValue(ColumnMetaData columnMetaData) {
		String columnName = columnMetaData.getName();
		String columnValue = pageContext.getRequest().getAttribute(columnName + "_lookUp") != null ? (String) pageContext.getRequest().getAttribute(columnName + "_lookUp") : "";
		String operator = pageContext.getRequest().getAttribute(columnName + "operator_lookUp") != null ? (String) pageContext.getRequest().getAttribute(columnName + "operator_lookUp") : "";
		if (StrUtl.isEmptyTrimmed(operator)) {
			operator = pageContext.getRequest().getParameter(columnName + "operator_lookUp") != null ? pageContext.getRequest().getParameter(columnName + "operator_lookUp") : "";
		}
		return operator;
	}

	private boolean hasComboInAdvancedMultiRowSearch(){
		Vector<ColumnMetaData> columns = editorMetaData.getSearchableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isAdvancedMultiRowSearch() && columnMetaData.isCombo() && columnMetaData.getEjbResultSet() != null) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasComboInUpdatable(){
		Vector<ColumnMetaData> columns = editorMetaData.getUpdatableColumns();
		if (columns != null) {
			for (ColumnMetaData columnMetaData : columns) {
				if (columnMetaData.isUpdatable() && columnMetaData.isCombo() && columnMetaData.getEjbResultSet() != null) {
					return true;
				}
			}
		}
		return false;
	}

	
}
