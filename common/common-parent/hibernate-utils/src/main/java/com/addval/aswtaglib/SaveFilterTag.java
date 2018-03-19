package com.addval.aswtaglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

import net.sf.json.JSONObject;

import com.addval.springstruts.ResourceUtils;
import com.addval.springstruts.SaveFilterForm;
import com.addval.ui.UIComponentMetadata;
import com.addval.utils.MetaDataUtils;
import com.addval.utils.StrUtl;

public class SaveFilterTag extends GenericBodyTag {
	private static final long serialVersionUID = -6355773462483845080L;
	private ASWTagUtils tagUtils = null;
	private boolean applyFilter = false;
	private SaveFilterForm saveFilterForm = null;
	private String formId = null;

	public SaveFilterTag() {
		tagUtils = new ASWTagUtils(ASWTagUtils._SAVEFILTER);
	}

	public void setFormId(String formId) throws JspTagException {
		this.formId = formId;
		Object object = pageContext.getAttribute(formId, getScope());
		if (object != null && object instanceof SaveFilterForm) {
			saveFilterForm = (SaveFilterForm) object;
		}
		else {
			throw new IllegalArgumentException("SaveFilterTag: Form " + formId + " does not extend SaveFilterForm. ");
		}
	}
	
	public String getFormId(){
		return this.formId;
	}

	public boolean isApplyFilter() {
		return applyFilter;
	}

	public void setApplyFilter(boolean applyFilter) {
		this.applyFilter = applyFilter;
	}
	
	public int doStartTag() throws JspTagException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String userGroupName = tagUtils.getUserGroupName(request);
		String selected = "";
		String searchFilterName = request.getParameter("searchFilterName");
		String newSearchFilterName = request.getParameter("newSearchFilterName");
		boolean isSaveFilter = request.getAttribute("SaveFilter") != null;
		boolean isDeleteFilter = request.getAttribute("DeleteFilter") != null;
		boolean isConfigRole = request.isUserInRole("config");
		List<String> sharedFilterNames = null;
		List<String> filterNames = new ArrayList<String>();
		if (saveFilterForm.getUserSearchFilters() != null) {
			for (UIComponentMetadata uiCmd : saveFilterForm.getUserSearchFilters()) {
				filterNames.add(uiCmd.getComponentId());
			}
		}

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
		try {
			StringBuffer html = new StringBuffer();
			if (isApplyFilter()) {
				String saveFilterLabel = ResourceUtils.message(request, "editor.filter.save", "Save Filter");
				String deleteFilterLabel = ResourceUtils.message(request, "editor.filter.delete", "Delete Filter");
				String resetFilterLabel = ResourceUtils.message(pageContext, "editor.filter.reset", "Reset");
	
				boolean isShared = (sharedFilterNames != null && sharedFilterNames.contains(searchFilterName));
				
				html.append("<SCRIPT language=\"javascript\">").append(newline);
				html.append("var loadSearchFilterHash  = new Hashtable();").append(newline);
				html.append("function loadSearchFilters() {").append(newline);
				
				if ( saveFilterForm != null ) {
					List<UIComponentMetadata> tempFilterNames = saveFilterForm.getUserSearchFilters() ;
					if ( tempFilterNames != null ) {
						for ( int i = 0 ; i < tempFilterNames.size(); i++ ) {
							html.append("loadSearchFilterHash.put('").append(tempFilterNames.get(i).getComponentId()).append("','").append(tempFilterNames.get(i).getComponentId()).append("' );").append(newline);
						}
					}
				}
				html.append(" }").append(newline);
								
				html.append("function validateMandatoryFields() {").append(newline);
				html.append("return true;").append(newline).append("}").append(newline);
				html.append("</SCRIPT>").append(newline);
				
				 if (filterNames != null && filterNames.size() > 0) {
					if (!StrUtl.isEmptyTrimmed(searchFilterName) && !isShared) {
						html.append("<div id=\"deleteFilterBtn\">");
						html.append("<DIV class=\"buttonOrangeOnWhite\" onclick=\"javascript:if(validateDeleteFilter()){doDeleteFilter();}\"><A href=\"#\" onclick=\"return false;\">").append(deleteFilterLabel).append("</A><DIV class=\"rightImg\"></DIV></DIV>");
						html.append("</div>");
					}
					html.append("<div style=\"float:right;\">");
					html.append("<SELECT name='searchFilterName'  id='searchFilterName' onChange=\"doApplyFilterForCustomScreen();\">");
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
				html.append("<DIV class=\"buttonOrangeOnWhite\" onclick=\"javascript:doResetForCustomScreen();\"><a href=\"#\" onclick=\"return false;\">").append(resetFilterLabel).append("</a><div class=\"rightImg\"></div></div>");
			}
			else {
				String saveFilterLabel = ResourceUtils.message(request, "editor.filter.save", "Save Filter");
				String newFilterNameLabel = ResourceUtils.message(request, "editor.filter.newname", "New Filter Name");
				String filterNameLabel = ResourceUtils.message(request, "editor.filter.name", "Filter Name");
				String sharetogroupLabel = ResourceUtils.message(request, "editor.filter.sharetogroup", "Share to User Group");
				String sharetoallLabel = ResourceUtils.message(request, "editor.filter.sharetoall", "Share to All Users");
				String cancelLabel = ResourceUtils.message(request, "editor.filter.cancel", "Cancel");
	
				String shareToAllUsers = request.getParameter("shareToAllUsers");
				String shareToUserGroup = request.getParameter("shareToUserGroup");
				String shareToUserGroupName = request.getParameter("shareToUserGroupName");
				if (StrUtl.isEmptyTrimmed(shareToUserGroupName)) {
					shareToUserGroupName = "";
				}
				String shareToAllUsersChecked = !StrUtl.isEmptyTrimmed(shareToAllUsers) ? "checked" : "";
				String shareToUserGroupChecked = !StrUtl.isEmptyTrimmed(shareToUserGroup) ? "checked" : "";
	
				if (!StrUtl.isEmptyTrimmed(searchFilterName) && saveFilterForm.getUserSearchFilters() != null) {
					UIComponentMetadata uiCmd = MetaDataUtils.findUIComponentMetadata(saveFilterForm.getUserSearchFilters(), searchFilterName);
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
				html.append("</table>").append(newline);
				html.append("</div>").append(newline);
				html.append(tagUtils.getGroupBoxBottomWithoutFooter());
				html.append("</div>").append(newline);
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
				throw new JspTagException("SaveFilterTag Error:" + ioe.toString());
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

}
