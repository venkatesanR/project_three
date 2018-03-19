/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.StrUtl;

public class DetailEditForm extends EditForm {
	private static final long serialVersionUID = 1L;
	public List<EditorMetaData> detailMetadata = null;
	public List<EJBResultSet> detailResultset = null;
	private List<String> detailEditorName = null;
	private String detailEditorType = null;

	private Map<String, List<ColumnMetaData>> parentKeyColumns;
	private Map<String, List<ColumnMetaData>> detailInfoColumns;

	public List<EditorMetaData> getDetailMetadata() {
		return detailMetadata;
	}

	public void setDetailMetadata(List<EditorMetaData> detailMetadata) {
		this.detailMetadata = detailMetadata;
	}

	public List<EJBResultSet> getDetailResultset() {
		return detailResultset;
	}

	public void setDetailResultset(List<EJBResultSet> detailResultset) {
		this.detailResultset = detailResultset;
	}

	public List<String> getDetailEditorName() {
		return detailEditorName;
	}

	public void setDetailEditorName(List<String> detailEditorName) {
		this.detailEditorName = detailEditorName;
	}

	public String getDetailEditorType() {
		return detailEditorType;
	}

	public void setDetailEditorType(String detailEditorType) {
		this.detailEditorType = detailEditorType;
	}

	public Map<String, List<ColumnMetaData>> getParentKeyColumns() {
		return parentKeyColumns;
	}

	public void setParentKeyColumns(Map<String, List<ColumnMetaData>> parentKeyColumns) {
		this.parentKeyColumns = parentKeyColumns;
	}

	public Map<String, List<ColumnMetaData>> getDetailInfoColumns() {
		return detailInfoColumns;
	}

	public void setDetailInfoColumns(Map<String, List<ColumnMetaData>> detailInfoColumns) {
		this.detailInfoColumns = detailInfoColumns;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		super.reset(mapping, request);

		BaseActionMapping baseMapping = (BaseActionMapping) mapping;

		org.apache.struts.config.ModuleConfig appConfig = (org.apache.struts.config.ModuleConfig) request.getAttribute(org.apache.struts.Globals.MODULE_KEY);

		BaseFormBeanConfig formConfig = (BaseFormBeanConfig) appConfig.findFormBeanConfig(mapping.getName());

		// Initialize the data elements from the formConfig
		String detailEditorName = formConfig.getDetailEditorName();
		String detailEditorType = formConfig.getDetailEditorType();

		if (!StrUtl.isEmptyTrimmed(detailEditorName)) {
			setDetailEditorName(Arrays.asList(detailEditorName.split(",")));
		}
		setDetailEditorType(detailEditorType);

		if (StrUtl.isEmptyTrimmed(detailEditorName)) {
			detailEditorName = (String) request.getAttribute("DETAIL_EDITOR_NAME");
		}

		if (StrUtl.isEmptyTrimmed(detailEditorName)) {
			detailEditorName = (String) request.getParameter("DETAIL_EDITOR_NAME");
		}

		if (StrUtl.isEmptyTrimmed(detailEditorName) && getMetadata() != null) {
			detailEditorName = getMetadata().getDetailEditorName();
		}

		if (formConfig.getDetailEditorName().equals("")) {
			detailMetadata = null;
			parentKeyColumns = null;
			detailInfoColumns = null;
		}
		detailMetadata = null;
		if (!StrUtl.isEmptyTrimmed(detailEditorName)) {
			setDetailEditorName(Arrays.asList(detailEditorName.split(",")));
		}
		setDetailEditorType(detailEditorType);
		if (detailMetadata == null && getDetailEditorName() != null) {
			String userName = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "DEFAULT";
			detailMetadata = new ArrayList<EditorMetaData>();
			for (String editorName : getDetailEditorName()) {
				EditorMetaData editorMetaData = lookupMetadata(editorName, getDetailEditorType(), userName, request.getSession());
				detailMetadata.add(editorMetaData);
			}
			configureMetadata();
		}
	}

	public void configureMetadata() {
		// Now populate parentKeyColumns and detailInfoColumns
		ColumnMetaData columnMetaData = null;
		ColumnMetaData dtlcolumnMetadata = null;

		parentKeyColumns = new HashMap<String, List<ColumnMetaData>>();

		HashMap<String, List<String>> uniqueParentKeyColumns = new HashMap<String, List<String>>();

		for (EditorMetaData dtlEditorMetaData : getDetailMetadata()) {
			parentKeyColumns.put(dtlEditorMetaData.getName(), new ArrayList<ColumnMetaData>());
			uniqueParentKeyColumns.put(dtlEditorMetaData.getName(), new ArrayList<String>());
			for (int i = 1; i <= getMetadata().getColumnCount(); ++i) {
				columnMetaData = getMetadata().getColumnMetaData(i);
				if (columnMetaData.isKey() || columnMetaData.isEditKey()) {
					for (int j = 1; j <= dtlEditorMetaData.getColumnCount(); ++j) {
						dtlcolumnMetadata = dtlEditorMetaData.getColumnMetaData(j);
						if (!uniqueParentKeyColumns.get(dtlEditorMetaData.getName()).contains(dtlcolumnMetadata.getName()) && columnMetaData.getName().equals(dtlcolumnMetadata.getName())) {
							parentKeyColumns.get(dtlEditorMetaData.getName()).add(dtlcolumnMetadata);
							uniqueParentKeyColumns.get(dtlEditorMetaData.getName()).add(dtlcolumnMetadata.getName());
						}
					}
				}
			}
		}

		detailInfoColumns = new HashMap<String, List<ColumnMetaData>>();
		HashMap<String, List<String>> uniqueDetailInfoColumns = new HashMap<String, List<String>>();
		for (EditorMetaData dtlEditorMetaData : getDetailMetadata()) {
			detailInfoColumns.put(dtlEditorMetaData.getName(), new ArrayList<ColumnMetaData>());
			uniqueDetailInfoColumns.put(dtlEditorMetaData.getName(), new ArrayList<String>());
			for (int i = 1; i <= dtlEditorMetaData.getColumnCount(); ++i) {
				dtlcolumnMetadata = dtlEditorMetaData.getColumnMetaData(i);
				if (!uniqueDetailInfoColumns.get(dtlEditorMetaData.getName()).contains(dtlcolumnMetadata.getName()) && !uniqueParentKeyColumns.get(dtlEditorMetaData.getName()).contains(dtlcolumnMetadata.getName())) {
					detailInfoColumns.get(dtlEditorMetaData.getName()).add(dtlcolumnMetadata);
					uniqueDetailInfoColumns.get(dtlEditorMetaData.getName()).add(dtlcolumnMetadata.getName());
				}
			}

		}
	}
}
