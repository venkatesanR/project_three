package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.addval.utils.StrUtl;

public class Grid implements Serializable {
	private static final long serialVersionUID = 1035565632217208646L;
	public static String newline = System.getProperty("line.separator");

	private String name = null;
	private String header = null;
	private boolean editable = true;
	private boolean sortable = false;
	private boolean customization = true;
	private boolean addnew = false;
	private String addnewButtonTxt = null;
	private String addnewKey = null;
	private List<GridCol> cols = null;
	private int defaultRows = 0; //
	private String zoneId = null;
	private String blockId = null;
	private String headerKey = null;
	private String contentCss = null;


	// UI_PAGE_SEC_PROPERTY_NAMES
	private static final String _GRID_NAME = "GRID_NAME";
	private static final String _GRID_HEADING = "GRID_HEADING";
	private static final String _EDITABLE = "EDITABLE"; // Y/N
	private static final String _SORTABLE = "SORTABLE"; // Y/N
	private static final String _CUSTOMIZATION = "CUSTOMIZATION"; // Y/N
	private static final String _ADDNEW = "ADDNEW"; // Y/N
	private static final String _DEFAULT_ROWS = "DEFAULT_ROWS";
	private static final String _ZONE_ID = "ZONE_ID";
	private static final String _BLOCK_ID = "BLOCK_ID";

	public Grid() {

	}

	public Grid(List<UIPageSectionProperty> uiPageSectionProperties) {
		if (uiPageSectionProperties != null) {
			for (UIPageSectionProperty uiPageSectionProperty : uiPageSectionProperties) {
				if (_GRID_NAME.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					name = uiPageSectionProperty.getPropertyValue();
				}
				else if (_GRID_HEADING.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					header = uiPageSectionProperty.getPropertyValue();
				}
				else if (_EDITABLE.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					editable = "Y".equalsIgnoreCase(uiPageSectionProperty.getPropertyValue());
				}
				else if (_SORTABLE.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					sortable = "Y".equalsIgnoreCase(uiPageSectionProperty.getPropertyValue());
				}
				else if (_CUSTOMIZATION.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					customization = "Y".equalsIgnoreCase(uiPageSectionProperty.getPropertyValue());
				}
				else if (_ADDNEW.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					addnew = "Y".equalsIgnoreCase(uiPageSectionProperty.getPropertyValue());
				}
				else if (_DEFAULT_ROWS.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					defaultRows = StrUtl.isEmptyTrimmed(uiPageSectionProperty.getPropertyValue()) ? 0 : Integer.parseInt(uiPageSectionProperty.getPropertyValue());
				}
				else if (_ZONE_ID.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					zoneId = uiPageSectionProperty.getPropertyValue();
				}
				else if (_BLOCK_ID.equalsIgnoreCase(uiPageSectionProperty.getPropertyName())) {
					blockId = uiPageSectionProperty.getPropertyValue();
				}
			}
		}
	}

	public Grid(String name, String header, boolean editable, boolean sortable, boolean customization, boolean addnew, int defaultRows) {
		setName(name);
		setHeader(header);
		setEditable(editable);
		setSortable(sortable);
		setCustomization(customization);
		setAddnew(addnew);
		setDefaultRows(defaultRows);
		setZoneId(zoneId);
		setBlockId(blockId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isCustomization() {
		return customization;
	}

	public void setCustomization(boolean customization) {
		this.customization = customization;
	}

	public String getContentCss() {
		return contentCss;
	}

	public void setContentCss(String contentCss) {
		this.contentCss = contentCss;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public List<GridCol> getKeyCols() {
		List<GridCol> keyCols = new ArrayList<GridCol>();
		if (cols != null) {
			for (GridCol col : cols) {
				if (col.isKey()) {
					keyCols.add(col);
				}
			}
		}
		return keyCols;
	}

	public List<GridCol> getDisplayableCols() {
		List<GridCol> displayableCols = new ArrayList<GridCol>();
		if (cols != null) {
			for (GridCol col : cols) {
				if (col.isDisplayable()) {
					displayableCols.add(col);
				}
			}
		}
		return displayableCols;
	}

	public List<GridCol> getCols() {
		if (cols == null) {
			cols = new ArrayList<GridCol>();
		}
		return cols;
	}

	public void setCols(List<GridCol> cols) {
		this.cols = cols;
	}

	public void addGridCol(GridCol col) {
		getCols().add(col);
	}

	public void removeGridCol(GridCol col) {
		getCols().remove(col);
	}

	public boolean isAddnew() {
		return addnew;
	}

	public void setAddnew(boolean addnew) {
		this.addnew = addnew;
	}

	public String getAddnewButtonTxt() {
		return addnewButtonTxt;
	}

	public void setAddnewButtonTxt(String addnewButtonTxt) {
		this.addnewButtonTxt = addnewButtonTxt;
	}
	
	public String getAddNewKey() {
		return addnewKey;
	}

	public void setAddNewKey(String addnewKey) {
		this.addnewKey = addnewKey;
	}

	public int getDefaultRows() {
		return defaultRows;
	}

	public void setDefaultRows(int defaultRows) {
		this.defaultRows = defaultRows;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getBlockId() {
		return blockId;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public String getHeaderKey() {
		return headerKey;
	}

	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}

	public GridCol findGridCol(String colName) {
		if (StrUtl.isEmptyTrimmed(colName)) {
			return null;
		}
		for (GridCol gridCol : getCols()) {
			if (colName.equalsIgnoreCase(gridCol.getColName())) {
				return gridCol;
			}
		}
		return null;
	}

	public String toXML(String componentId) {
		StringBuffer buf = new StringBuffer();
		buf.append("<GridLayout ");
		buf.append(" name").append("=\"").append(componentId).append("\"");
		if (!StrUtl.isEmptyTrimmed(header)) {
			buf.append(" header").append("=\"").append(header).append("\"");
		}
		buf.append(" sortable").append("=\"").append(sortable).append("\"");
		buf.append(" editable").append("=\"").append(editable).append("\"");
		buf.append(" customization").append("=\"").append(customization).append("\"");
		buf.append(" contentCss").append("=\"").append(contentCss).append("\"");
		buf.append(" addnew").append("=\"").append(addnew).append("\"");
		if (!StrUtl.isEmptyTrimmed(addnewButtonTxt)) {
			buf.append(" addnewButtonTxt").append("=\"").append(addnewButtonTxt).append("\"");
		}
		if (defaultRows > 0) {
			buf.append(" defaultRows").append("=\"").append(defaultRows).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(zoneId)) {
			buf.append(" zoneId").append("=\"").append(zoneId).append("\"");
		}
		if (!StrUtl.isEmptyTrimmed(blockId)) {
			buf.append(" blockId").append("=\"").append(blockId).append("\"");
		}
		buf.append(">").append(newline);
		for (GridCol col : getCols()) {
			buf.append(col.toXML(editable));
		}
		buf.append("</GridLayout>").append(newline);
		return buf.toString();
	}
}
