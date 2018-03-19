package com.addval.aswtaglib;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;

import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.springstruts.ResourceUtils;
import com.addval.utils.StrUtl;

public class EditTag extends GenericBodyTag {
	private ASWTagUtils tagUtils = null;
	private EditorMetaData editorMetaData = null;
	private ResultSet resultSet = null;
	private boolean saveButton = true;
	private boolean cancelButton = true;
	private Map<String, List<ColumnMetaData>> groupColumnInfo = null;

	public EditTag() {
		tagUtils = new ASWTagUtils(ASWTagUtils._EDIT);
	}

	public void setMetaRefId(String metaRefId) throws JspTagException {
		Object object = pageContext.getAttribute(metaRefId, getScope());
		if (object != null && object instanceof EditorMetaData) {
			editorMetaData = (EditorMetaData) object;
		}
		else {
			throw new IllegalArgumentException("EditTag: MetaRefId " + metaRefId + " invalid.");
		}
	}

	public void setDataRefId(String dataRefId) throws JspTagException {
		Object object = pageContext.getAttribute(dataRefId, getScope());
		if (object != null && object instanceof ResultSet) {
			resultSet = (ResultSet) object;
		}
		else {
			throw new IllegalArgumentException("EditTag: DataRefId " + dataRefId + " invalid.");
		}
	}

	public boolean isSaveButton() {
		return saveButton;
	}

	public void setSaveButton(boolean saveButton) {
		this.saveButton = saveButton;
	}


	public boolean isCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(boolean cancelButton) {
		this.cancelButton = cancelButton;
	}

	public Map<String, List<ColumnMetaData>> getGroupColumnInfo() {
		return groupColumnInfo;
	}

	public void setGroupColumnInfo(Map<String, List<ColumnMetaData>> groupColumnInfo) {
		this.groupColumnInfo = groupColumnInfo;
	}

	public int doStartTag() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			Vector<ColumnMetaData> updatableColumns = editorMetaData.getUpdatableColumns();
			if (updatableColumns != null) {
				Object errors = pageContext.getAttribute(Globals.ERROR_KEY, PageContext.REQUEST_SCOPE);
				boolean hasErrors =  (errors != null);
				String kindOfAction = pageContext.getRequest().getParameter("kindOfAction");
				if (StrUtl.isEmptyTrimmed(kindOfAction)) {
					kindOfAction = "edit";
				}
				boolean readonly = pageContext.getRequest().getParameter( "readonly" ) != null ? (new Boolean( pageContext.getRequest().getParameter( "readonly" ) )).booleanValue() : false;
				boolean isBackButtonRequired = pageContext.getRequest().getAttribute("isBackButtonRequired") != null ? (new Boolean( (String)pageContext.getRequest().getAttribute( "isBackButtonRequired" ) )).booleanValue() : false;
				boolean isEditPageTitleCustomized = pageContext.getRequest().getAttribute("isEditPageTitleCustomized") != null ? (new Boolean( (String)pageContext.getRequest().getAttribute( "isEditPageTitleCustomized" ) )).booleanValue() : false;
				if ( isBackButtonRequired ){
					cancelButton = false;
			    }
				boolean isListEdit = isListEdit();
				boolean isChild = isChild();

				String pageTitle = ResourceUtils.editorTitle(pageContext, editorMetaData.getName(), editorMetaData.getDesc());
				if (StrUtl.isEmptyTrimmed(pageTitle)) {
					pageTitle = editorMetaData.getName();
				}
				String actionDesc = ResourceUtils.message(pageContext, "editor.edit.edit", "Edit");
				if ( isEditPageTitleCustomized ) {
					actionDesc = ResourceUtils.message(pageContext, "editor.edit.customizeDesc", "Details");
				}
				else if(readonly){
					actionDesc = ResourceUtils.message(pageContext, "editor.edit.view", "View");
				}
				else if("insert".equalsIgnoreCase(kindOfAction)){
					actionDesc = ResourceUtils.message(pageContext, "editor.edit.add", "Add");
				}
				else if("clone".equalsIgnoreCase(kindOfAction)){
					actionDesc = ResourceUtils.message(pageContext, "editor.edit.clone", "Clone");
				}

				int editSectionCount = tagUtils.getEditSectionCount(updatableColumns);
				Hashtable<String, Integer> updateColsPerRow = new Hashtable<String, Integer>();
				for (int i = 1; i <= editSectionCount; i++) {
					updateColsPerRow.put(String.valueOf(i), tagUtils.getEditSectionColsPerRow(editorMetaData, i));
				}
				if (isListEdit) {
					html.append(tagUtils.getGroupBoxTop("columnB", null, "width: 58%;"));
				}
				else if(isChild){
					html.append(tagUtils.getGroupBoxTop("columnB", null, "padding-top:10px;"));
				}
				else {
					html.append(tagUtils.getGroupBoxTop("columnB", null, null));
				}


				String save = ResourceUtils.message(pageContext, "editor.edit.save", "Save");
				String cancel = ResourceUtils.message(pageContext, "editor.edit.cancel", "Cancel");
				String back = ResourceUtils.message(pageContext, "editor.edit.back", "Back");
				String hidesection = ResourceUtils.message(pageContext, "editor.edit.hidesection", "Hide Section");

				if (!isChild) {
					html.append("<div class=\"boxTitle\">").append(newline);
					html.append("<div class=\"cornerTopLeft\"></div><div class=\"cornerTopRight\"></div>").append(newline);
					html.append("<div class=\"buttonClear_topRight\">").append(newline);
					if (isSaveButton()) {
						html.append("<div class=\"buttonBlueOnBlue\" onclick=\"javascript:doSave()\"><a href=\"#\" onclick=\"return false;\">").append(save).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					}
					if (isCancelButton()) {
						html.append("<div class=\"buttonBlueOnBlue\" onclick=\"javascript:doCancel()\" ><a href=\"#\" onclick=\"return false;\">").append(cancel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					}
					if ( isBackButtonRequired ) {
						html.append("<div class=\"buttonBlueOnBlue\" onclick=\"javascript:doBack()\" ><a href=\"#\" onclick=\"return false;\">").append(back).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					}
					html.append("</div>").append(newline);
					html.append("<p>").append(pageTitle).append(" - " + actionDesc).append("</p>").append(newline);
					html.append("</div> <!--boxTitle-->").append(newline);
				}
				else if ( isChild ) {
					String secId = "SectionId" + 1;
					html.append("<div class=\"sectionTitle\">").append(newline);
					html.append("<div onclick=\"toggleSection('").append(secId).append("');\" id=\"").append(secId).append("Img\" title=\"").append(hidesection).append("\" class=\"toggleImageOpen\"></div>").append(newline);
					html.append("<span style=\"float:left;\">").append(tagUtils.getEditSectionTitle(pageContext,editorMetaData, 1,actionDesc)).append("</span>").append(newline);
					html.append("<div class=\"buttonClear_topRight\">");
					if (isSaveButton()) {
						html.append("<div class=\"buttonSecondary\"><a href=\"javascript:doSave()\">").append(save).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					}
					if (isCancelButton()) {
						html.append("<div class=\"buttonSecondary\"><a href=\"javascript:doCancel()\">").append(cancel).append("</a><div class=\"rightImg\"></div></div>").append(newline);
					}
					html.append("</div>").append(newline);
					html.append("</div>").append(newline);
					html.append("<div style=\"display: block;\" id=\"").append(secId).append("\" class=\"colWrapper\">").append(newline);
				}

				if (!isListEdit && !isChild) {
					html.append("<div class=\"outerborder\">");
					//html.append("<div class=\"groupBoxColumnWrapper detailsColumn\" style=\"width: 98%;\">").append(newline);
					html.append("<div>").append(newline);
					html.append("<div class=\"groupBoxColumn\">").append(newline);
				}

				Hashtable<String, StringBuffer> sectionHash = new Hashtable<String, StringBuffer>();
				Hashtable<String, Integer> colsPerRowHash = new Hashtable<String, Integer>();
				StringBuffer script = new StringBuffer();
				for (int i = 1; i <= editSectionCount; i++) {
					sectionHash.put(String.valueOf(i), new StringBuffer());
					colsPerRowHash.put(String.valueOf(i), updateColsPerRow.get(String.valueOf(i)));
				}

				StringBuffer section = null;
				Integer colsPerRow = null;
				Integer updColsPerRow = null;
				String columnName = null;
				String columnValue = null;
				String defColumnValue = null;
				String caption = null;
				boolean isSecured = false;
				while (resultSet.next()) {
					for (ColumnMetaData columnMetaData : updatableColumns) {
						if (columnMetaData.getType() == ColumnDataType._CDT_USER || columnMetaData.getType() == ColumnDataType._CDT_LINK) {
							continue;
						}
						for (int i = 1; i <= editSectionCount; i++) {
							if (columnMetaData.isUpdateGroup(String.valueOf(i))) {
								section = sectionHash.get(String.valueOf(i));
								colsPerRow = colsPerRowHash.get(String.valueOf(i));
								updColsPerRow = updateColsPerRow.get(String.valueOf(i));
								columnName = columnMetaData.getName();

								if ( columnMetaData.getType() != ColumnDataType._CDT_FILE ) {
									columnValue = resultSet.getString(columnName) == null ? "" : resultSet.getString(columnName);
									columnValue = tagUtils.escapeHtml(columnValue);
								}

								defColumnValue = pageContext.getRequest().getParameter((columnName + "_lookUp"));

								if(!hasErrors){
									if ("insert".equalsIgnoreCase(kindOfAction) || "clone".equalsIgnoreCase(kindOfAction)) {
										if (StrUtl.isEmptyTrimmed(columnValue) && !StrUtl.isEmptyTrimmed(defColumnValue)) {
											columnValue = defColumnValue;
										}
										if (StrUtl.isEmptyTrimmed(columnValue) && !StrUtl.isEmptyTrimmed(columnMetaData.getValue())) {
											columnValue = columnMetaData.getValue();
										}
									}
								}

								if(readonly){
									section.append("<td><nobr>");
									section.append("<p>").append(tagUtils.getUpdateMandatory(columnMetaData)).append(tagUtils.getCaption(pageContext, columnMetaData)).append("</p>");
									section.append(tagUtils.getViewHtmlControl(pageContext, editorMetaData, columnMetaData, columnValue, isSecured));
									section.append("</nobr></td>");

									colsPerRow--;
									if (colsPerRow.equals(new Integer(0))) {
										colsPerRow = updColsPerRow;
										section.append("</tr><tr>");
									}
									colsPerRowHash.put(String.valueOf(i), colsPerRow);
								}
								else if (columnMetaData.isEditKey() && !columnMetaData.isEditKeyDisplayable()) {
									section.append(tagUtils.getEditHtmlControl(pageContext, editorMetaData, columnMetaData, columnValue, isSecured(columnMetaData)));
								}
								else {
									isSecured = isSecured(columnMetaData);
									if (columnMetaData.isEditKey() && columnMetaData.isEditKeyDisplayable() && "edit".equalsIgnoreCase(kindOfAction)) {
										isSecured = true;
									}

									section.append("<td valign=\"top\" class=\"content\" ><nobr>");
									section.append("<p>").append(tagUtils.getUpdateMandatory(columnMetaData)).append(tagUtils.getCaption(pageContext, columnMetaData)).append("</p>");
									section.append(tagUtils.getEditHtmlControl(pageContext, editorMetaData, columnMetaData, columnValue, isSecured));
									section.append("</nobr></td>");

									colsPerRow--;
									if (colsPerRow.equals(new Integer(0))) {
										colsPerRow = updColsPerRow;
										section.append("</tr><tr>");
									}
									colsPerRowHash.put(String.valueOf(i), colsPerRow);
								}
							}
						}
						if ((columnMetaData.getType() != ColumnDataType._CDT_USER) && ((!columnMetaData.isEditKey() && !columnMetaData.isNullable()) || (columnMetaData.isEditKey() && columnMetaData.isEditKeyDisplayable() && !columnMetaData.isNullable()))) {
							String requiredLabel = ResourceUtils.message(pageContext, "error.isrequired", "is required");

							caption = tagUtils.getCaption(pageContext, columnMetaData);
							//String columnLabel = caption.replaceAll("(?i)(<br>|<br/>)", "\\\\n"); // Javascript Alert with newline
							String columnLabel = caption.replaceAll("(?i)(<br>|<br/>)", " "); // Javascript Alert without <br/>
							if (columnMetaData.getComboSelect() != null) {
								columnMetaData = editorMetaData.getColumnMetaData(editorMetaData.getColumnIndex(columnMetaData.getComboSelect()));
							}
							script.append("if(!(frmObj.").append(columnName + "_edit").append(")){").append(newline).append(" return true;").append(newline).append("}").append(newline);
							if(!StrUtl.isEmptyTrimmed(columnMetaData.getFormat()) && columnMetaData.getType() == ColumnDataType._CDT_DATE || columnMetaData.getType() == ColumnDataType._CDT_DATETIME){
								script.append(" if((frmObj.").append(columnName+ "_edit").append(".value)".toLowerCase()).append(" == \"").append(columnMetaData.getFormat().toLowerCase()).append("\") ");
								script.append(" frmObj.").append(columnName + "_edit").append(".value = \"\";");
							}
							script.append("if( trim(frmObj.").append(columnName + "_edit").append(".value)").append("==\"\" )").append(" { ").append(newline);
							script.append("alert(\"").append(columnLabel).append(" ").append(requiredLabel).append("\"").append(");").append(newline);
							script.append("frmObj.").append(columnName + "_edit").append(".focus();").append(newline);
							script.append("return false;").append(newline);
							script.append("}").append(newline);
						}
					}
				}

				for (int i = 1; i <= editSectionCount; i++) {
					section = sectionHash.get(String.valueOf(i));
					String secId = null;
					if (section.length() > 0) {
						colsPerRow = colsPerRowHash.get(String.valueOf(i));
						updColsPerRow = updateColsPerRow.get(String.valueOf(i));
						secId = "SectionId" + i;
						if (!isListEdit && i > 1) {
							html.append("<div class=\"sectionTitle\">").append(newline);
							html.append("<div onclick=\"toggleSection('").append(secId).append("');\" id=\"").append(secId).append("Img\" title=\"").append(hidesection).append("\" class=\"toggleImageOpen\"></div>").append(newline);
							html.append("<span>").append(tagUtils.getEditSectionTitle(pageContext,editorMetaData, i,actionDesc)).append("</span>").append(newline);
							html.append("</div>").append(newline);
							html.append("<div style=\"display: block;\" id=\"").append(secId).append("\" class=\"colWrapper\">").append(newline);
						}
						html.append("<div class=\"formLayout\">").append(newline);
						html.append("<table class=\"tablelayout\">");
						html.append("<tr>");
						if (colsPerRow.equals(updColsPerRow) && section.toString().endsWith("</tr><tr>")) {
							section = section.replace(section.lastIndexOf("</tr><tr>"), section.lastIndexOf("</tr><tr>") + "</tr><tr>".length(), "");
						}
						html.append(section.toString());
						html.append("</tr>");
						html.append("</table>");

						if (i == 1) {
							html.append(getGroupEditHTML());
						}

						html.append("</div>").append(newline);
						if (!isListEdit && i > 1) {
							html.append("</div>").append(newline);
						}
					}
				}
				if (!isListEdit && !isChild ) {
					html.append("</div> <!--groupBoxColumn -->").append(newline);
					html.append("</div><!--groupBoxColumnWrapper-->").append(newline);
				}

				if (editorMetaData.hasChild() && "edit".equalsIgnoreCase(kindOfAction)) {
					html.append(getChildSections()).append(newline);
				}

				if(!isListEdit) {
					html.append("</div><!--outerborder-->").append(newline);
				}

				if (isChild) {
					html.append(tagUtils.getGroupBoxBottomWithoutFooter());
				}
				else {
					html.append(tagUtils.getGroupBoxBottom());
				}

				html.append(newline);
				//html.append("<SCRIPT FOR=edit EVENT = onsubmit>").append(newline);
				//html.append("return checkNulls();").append(newline);
				//html.append("</SCRIPT>").append(newline);

				html.append("<SCRIPT language=\"javascript\">").append(newline);
				html.append("function checkNulls() {").append(newline);
				html.append(" frmObj = document.forms[0];").append(newline);
				html.append(script.toString()).append(newline);
				html.append("return true;").append(newline);
				html.append("}").append(newline);

				html.append("function formValid(form) {").append(newline);
				html.append(tagUtils.getFormValidScript(pageContext, editorMetaData)).append(newline);
				html.append("return true;").append(newline);
				html.append("}").append(newline);

				html.append("</SCRIPT>").append(newline);
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
				throw new JspTagException("EditTag Error:" + ioe.toString());
			}
		}
	}

	public int doEndTag() throws JspTagException {
		try {
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
		super.release();
	}

	private boolean isSecured(ColumnMetaData columnMetaData) {
		return columnMetaData.isSecured() && pageContext.getRequest().getAttribute(columnMetaData.getName() + "_unlock") == null;
	}

	private boolean isListEdit() {
		String listEdit = pageContext.getRequest().getParameter("listEdit");
		return !StrUtl.isEmptyTrimmed(listEdit) ? (new Boolean(listEdit)).booleanValue() : false;
	}

	private boolean isChild() {
		String childIndex = pageContext.getRequest().getParameter("childIndex");
		return !StrUtl.isEmptyTrimmed(childIndex);
	}

	/*
	<DIV class="groupBox">
		<DIV class="tabs">
			<UL>
				<LI id="tab-1" class="selected" onclick="showTab(0, new Array('tab-1', 'tab-2'), new Array('tabBody-1', 'tabBody-2')); return false;">
					<DIV>
						<DIV class=cornerTopLeftWhite></DIV>
						<P>Tab 1</P>
						<DIV class=cornerTopRightWhite></DIV>
					</DIV>
				</LI>
				<LI id="tab-2" onclick="showTab(1, new Array('tab-1', 'tab-2'), new Array('tabBody-1', 'tabBody-2')); return false;">
					<DIV>
						<DIV class=cornerTopLeftWhite></DIV>
						<P>Tab 2</P>
						<DIV class=cornerTopRightWhite></DIV>
					</DIV>
				</LI>
			</UL>
		</DIV>
		<DIV class="tabBodies">
			<DIV style="DISPLAY: block; HEIGHT: 75px" id="tabBody-1" class="tab">
				<!-- Tab1 Body goes here -->
			</DIV>

			<DIV style="DISPLAY: none; HEIGHT: 75px" id="tabBody-2" class="tab">
				<!-- Tab2 Body goes here -->
			</DIV>
		</DIV>
	</DIV>
	*/
	public String getChildSections() throws JspTagException {
		StringBuffer html = new StringBuffer();
		try {
			ArrayList<String> childActions = tagUtils.getChildActions(editorMetaData);
			StringBuffer tabIds = new StringBuffer();
			StringBuffer tabBodyIds = new StringBuffer();
			for (int i = 0; i < childActions.size(); i++) {
				tabIds.append(",").append("'tab-").append(i + 1).append("'");
				tabBodyIds.append(",").append("'tabBody-").append(i + 1).append("'");
			}
			tabIds = new StringBuffer(tabIds.substring(1));
			tabBodyIds = new StringBuffer(tabBodyIds.substring(1));

			html.append("<DIV class=\"groupBox\">").append(newline);
			html.append("<DIV class=\"tabs\">").append(newline);
			html.append("<UL>").append(newline);
			for (int i = 0; i < childActions.size(); i++) {
				html.append("<LI id=\"tab-").append(i + 1).append("\" onclick=\"showTab(").append(i).append(",new Array(").append(tabIds).append("),new Array(").append(tabBodyIds).append(")); return false;\"");
				if (i == 0) {
					html.append(" class=\"selected\"");
				}
				html.append(">").append(newline);
				html.append("<DIV>").append(newline);
				html.append("<DIV class=cornerTopLeftWhite></DIV>").append(newline);
				html.append("<P id=\"tabtitle-").append(i + 1).append("\">Tab ").append(i + 1).append("</P>").append(newline);
				html.append("<DIV class=cornerTopRightWhite></DIV>").append(newline);
				html.append("</DIV>").append(newline);
				html.append("</LI>").append(newline);
			}

			html.append("</UL>").append(newline);
			html.append("</DIV><!--tabs-->").append(newline);
			html.append("<DIV class=\"tabBodies\">").append(newline);

			for (int i = 0; i < childActions.size(); i++) {
				html.append("<DIV id=\"tabBody-").append(i + 1).append("\" class=\"tab\"");
				if (i == 0) {
					html.append(" style=\"DISPLAY: block;\"");
				}
				else {
					html.append(" style=\"DISPLAY: none;\"");
				}
				html.append(">").append(newline);
				html.append("<iframe id=\"childframe_").append(i).append("\" name=\"childframe_").append(i).append("\" frameborder=\"0\" width=\"100%\" height=\"100%\" scrolling=\"no\">").append(newline);
				html.append("</iframe>").append(newline);
				html.append("</DIV>").append(newline);
			}
			html.append("</DIV><!--tabBodies-->").append(newline);
			html.append("</DIV><!--groupBox-->").append(newline);
			return html.toString();
		}
		catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}
	}

	private String getGroupEditHTML() throws Exception {
		StringBuffer html = new StringBuffer();
		if (getGroupColumnInfo() != null) {
			ASWTagUtils lookupTagUtils = new ASWTagUtils(ASWTagUtils._EDIT);

			String addtolist = ResourceUtils.message(pageContext, "editor.edit.addtolist", "Add to List");
			String removefromlist = ResourceUtils.message(pageContext, "editor.edit.removefromlist", "Remove From List");

			html.append("<table class=\"tablelayout\">");
			List<ColumnMetaData> detailInfoColumns = null;
			ColumnMetaData firstColumnMetaData = null;
			int index = 0;
			for(String dtlEditorName:groupColumnInfo.keySet()){
				detailInfoColumns = groupColumnInfo.get(dtlEditorName);
				firstColumnMetaData = (detailInfoColumns.size() > 0)? detailInfoColumns.get(0): null; 
				if(firstColumnMetaData != null){
					String groupColumnName = firstColumnMetaData.getName();
					
					if(index % 2 == 0 && index > 0){
						html.append("</tr>");
					}
					if(index % 2 == 0){
						html.append("<tr>");
					}
					html.append("<td valign=\"top\"><nobr>");
					html.append("<p>").append(tagUtils.getSearchMandatory(firstColumnMetaData)).append(lookupTagUtils.getCaption(pageContext, firstColumnMetaData)).append("</p>");
					html.append(lookupTagUtils.getSearchHtmlControl(pageContext, editorMetaData, firstColumnMetaData, "", "", false, false));
					html.append("</nobr></td>");
					html.append("<td valign=\"top\" style=\"padding-top: 12px;\">");

					html.append("<a href=\"javascript:addItemToList(\'"+ dtlEditorName +"\',\'" + groupColumnName +"\');\"><img src=\"../images/addtolist.gif\" border=\"0\" alt=\"").append(addtolist).append("\"></a>");
					html.append("</td>");
					html.append("<td valign=\"top\" style=\"padding-top: 16px;\">");
					html.append("<select name=\"").append(groupColumnName).append("_array\" multiple size=\"10\">");
					html.append("</select>");
					html.append("</td>");
					html.append("<td valign=\"top\" style=\"padding-top: 15px;\">");
					html.append("<a href=\"javascript:removeFromList(\'"+ dtlEditorName +"\',\'" + groupColumnName +"\');\"><img src=\"../images/deletelist.gif\" border=\"0\" alt=\"").append(removefromlist).append("\"></a>");
					html.append("</td>");
				}
				index++;
			}
			if((index-1) % 2 != 0){
				html.append("</tr>");
			}

			html.append("</table>");
		}
		return html.toString();
	}

}
