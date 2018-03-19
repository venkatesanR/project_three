package com.addval.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class PageMetadata implements Serializable {
	private static final transient org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(PageMetadata.class);
	private String _name = null;
	private List<UIPageProperty> pageProperties = new ArrayList<UIPageProperty>();
	private List<Table> _tableLayout = new ArrayList<Table>();
	private List<Grid> _gridLayout = new ArrayList<Grid>();
	private List<Grid> _multiRowEditor = new ArrayList<Grid>();

	public PageMetadata() {

	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}
	
	public List<UIPageProperty> getPageProperties() {
		return pageProperties;
	}

	public void setPageProperties(List<UIPageProperty> pageProperties) {
		this.pageProperties = pageProperties;
	}

	public void addUIPageProperty(UIPageProperty property) {
		this.pageProperties.add(property);
	}
	
	public List<Table> getTableLayout() {
		return _tableLayout;
	}

	public void setTableLayout(List<Table> tableLayout) {
		_tableLayout = tableLayout;
	}

	public List<Grid> getGridLayout() {
		return _gridLayout;
	}

	public void setGridLayout(List<Grid> gridLayout) {
		_gridLayout = gridLayout;
	}

	public void addTableLayout(Table tbl) {
		_tableLayout.add(tbl);
	}

	public void addGridLayout(Grid grid) {
		_gridLayout.add(grid);
	}

	public void addMultiRowEditor(Grid grid) {
		_multiRowEditor.add(grid);
	}

	public List<Grid> getMultiRowEditor() {
		return _multiRowEditor;
	}

	public void setMultiRowEditor(List<Grid> multiRowEditor) {
		_multiRowEditor = multiRowEditor;
	}

	public List<UIComponentMetadata> getComponentMetadata() {
		List<UIComponentMetadata> compMetadata = new ArrayList<UIComponentMetadata>();
		UIComponentMetadata uiCmd = null;
		Table t = null;
		Grid g = null;
		if(getPageProperties().size() > 0){
			uiCmd = new UIComponentMetadata();
			uiCmd.setComponentId(UIComponentTypes.PAGE_PROPERTIES);
			uiCmd.setComponentType(UIComponentTypes.PAGE_PROPERTIES); 
			uiCmd.setPageName(getName()); // page for the component
			uiCmd.setUserName("SYSTEM"); // user name associated with the metadata. default is SYSTEM
			uiCmd.setJsonString(getJSONString(uiCmd, getPageProperties()));
			compMetadata.add(uiCmd);
		}
		
		for (int i = 0; i < getTableLayout().size(); ++i) {
			t = getTableLayout().get(i);
			uiCmd = new UIComponentMetadata();
			uiCmd.setComponentId(t.getName());
			uiCmd.setComponentType(UIComponentTypes.TABLE_LAYOUT); // it is a table component
			uiCmd.setPageName(getName()); // page for the component
			uiCmd.setUserName("SYSTEM"); // user name associated with the metadata. default is SYSTEM

			uiCmd.setJsonString(getJSONString(uiCmd, t));
			compMetadata.add(uiCmd);
		}

		for (int i = 0; i < getGridLayout().size(); ++i) {
			g = getGridLayout().get(i);
			uiCmd = new UIComponentMetadata();
			uiCmd.setComponentId(g.getName());
			uiCmd.setComponentType(UIComponentTypes.EXTENDED_GRID); // it is a extended grid component
			uiCmd.setPageName(getName()); // page for the component
			uiCmd.setUserName("SYSTEM"); // user name associated with the metadata. default is SYSTEM

			uiCmd.setJsonString(getJSONString(uiCmd, g));
			compMetadata.add(uiCmd);
		}

		for (int i = 0; i < getMultiRowEditor().size(); ++i) {
			g = getMultiRowEditor().get(i);
			uiCmd = new UIComponentMetadata();
			uiCmd.setComponentId(g.getName());
			uiCmd.setComponentType(UIComponentTypes.EXTENDED_GRID); // it is a multi row editor component
			uiCmd.setPageName(getName()); // page for the component
			uiCmd.setUserName("SYSTEM"); // user name associated with the metadata. default is SYSTEM

			uiCmd.setJsonString(getJSONString(uiCmd, g));
			compMetadata.add(uiCmd);
		}

		return compMetadata;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		UIComponentMetadata uiCmd = new UIComponentMetadata();
		Table t = null;
		Grid g = null;
		for (int i = 0; i < getTableLayout().size(); ++i) {
			t = getTableLayout().get(i);
			buffer.append(getJSONString(uiCmd, t));
		}

		for (int i = 0; i < getGridLayout().size(); ++i) {
			g = getGridLayout().get(i);
			buffer.append(getJSONString(uiCmd, g));
		}

		for (int i = 0; i < getMultiRowEditor().size(); ++i) {
			g = getMultiRowEditor().get(i);
			buffer.append(getJSONString(uiCmd, g));
		}
		return buffer.toString();
	}

	private String getJSONString(UIComponentMetadata uiCmd, Table tbl) {
		JSONObject jsonTable = new JSONObject();
		JSONObject json = null;
		jsonTable.put("table", JSONObject.fromObject(tbl, uiCmd.getJsonConfig()));
		json = JSONObject.fromObject(jsonTable);
		return json.toString();
	}

	private String getJSONString(UIComponentMetadata uiCmd, Grid grid) {
		JSONObject jsonGrid = new JSONObject();
		JSONObject json = null;
		jsonGrid.put("grid", JSONObject.fromObject(grid, uiCmd.getJsonConfig()));
		json = JSONObject.fromObject(jsonGrid);
		return json.toString();
	}

	private String getJSONString(UIComponentMetadata uiCmd, List<UIPageProperty> pageProperties) {
		JSONObject jsonObject = new JSONObject(); 
		UIPageProperty pp = null;
		for (int i = 0; i < pageProperties.size(); ++i) {
			pp = pageProperties.get(i);
			jsonObject.put(pp.getPropertyName(), pp.getPropertyValue());
		}
		return jsonObject.toString();
	}
}
