package com.addval.ui;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.addval.utils.GenUtl;
import com.addval.utils.StrUtl;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.util.PropertyFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class UIComponentMetadata implements Serializable {
	private static final long serialVersionUID = 5053275147942419078L;
	private static transient final Logger _logger = Logger.getLogger(UIComponentMetadata.class);
    private static final int MAX_CHAR = 4000;

	private Long key = null;
	private String pageName = null;
	private String userName = null;
	private String componentType = null;
	private String componentId = null;
	private String jsonString = null;
	private Long defaultTplkey = null;

	private List<UIPageProperty> pageProperties=null;
	private Table table = null;
	private Grid grid = null;
	private UIComponentMetadata readOnlyUIComponentMetadata;
	private boolean parsed = false;
	private HashMap<String,List<String>> propertyExclusions = null;

	public static final String _LABEL_SUFFIX = "-label";
	public static final String _HEADING_SUFFIX = "-heading";
	public static final String _MESSAGE_SUFFIX = "-message";
	public static final String _ADDNEW_SUFFIX = "-addnew";
	
	public UIComponentMetadata() {

	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public Long getDefaultTplkey() {
		return defaultTplkey;
	}

	public void setDefaultTplkey(Long defaultTplkey) {
		this.defaultTplkey = defaultTplkey;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public void setJsonPartAString(String jsonPartA){
		jsonString=jsonPartA;
	}

	public void setJsonPartBString(String jsonPartB){
		if(jsonPartB !=null){
			jsonString+=jsonPartB;
		}
	}
	public void setJsonPartCString(String jsonPartC){
		if(jsonPartC !=null){
			jsonString+=jsonPartC;
		}
	}
	public void setJsonPartDString(String jsonPartD){
		if(jsonPartD !=null){
			jsonString+=jsonPartD;
		}
	}

	public void setJsonPartEString(String jsonPartE){
		if(jsonPartE !=null){
			jsonString+=jsonPartE;
		}
	}

	public void setJsonPartFString(String jsonPartF){
		if(jsonPartF !=null){
			jsonString+=jsonPartF;
		}
	}

	public void setJsonPartGString(String jsonPartG){
		if(jsonPartG !=null){
			jsonString+=jsonPartG;
		}
	}

	public void setJsonPartHString(String jsonPartH){
		if(jsonPartH !=null){
			jsonString+=jsonPartH;
		}
	}

	public void setJsonPartIString(String jsonPartI){
		if(jsonPartI !=null){
			jsonString+=jsonPartI;
		}
	}

	public void setJsonPartJString(String jsonPartJ){
		if(jsonPartJ !=null){
			jsonString+=jsonPartJ;
		}
	}

	
	public String getJsonString() {
		return jsonString;
	}

    public String getJsonPartAString(){
        int length = this.jsonString.length();
        int startPos = 0;
        int endPos  = ( ( MAX_CHAR * 1 ) > length )? (int)( length % MAX_CHAR ) : MAX_CHAR;
        return jsonString.substring(startPos , endPos);
    }
    public String getJsonPartBString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 1 ) > length )? (int)( length % ( MAX_CHAR * 1 ) ) : MAX_CHAR * 1;
        int endPos  = ( ( MAX_CHAR * 2 ) > length )? (int)( length % ( MAX_CHAR * 2 ) ) : MAX_CHAR * 2;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }
    public String getJsonPartCString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 2 ) > length )? (int)( length % ( MAX_CHAR * 2 ) ) : MAX_CHAR * 2;
        int endPos  = ( ( MAX_CHAR * 3 ) > length )? (int)( length % ( MAX_CHAR * 3 ) ) : MAX_CHAR * 3;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }
    public String getJsonPartDString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 3 ) > length )? (int)( length % ( MAX_CHAR * 3 ) ) : MAX_CHAR * 3;
        int endPos  = ( ( MAX_CHAR * 4 ) > length )? (int)( length % ( MAX_CHAR * 4 ) ) : MAX_CHAR * 4;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public String getJsonPartEString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 4 ) > length )? (int)( length % ( MAX_CHAR * 4 ) ) : MAX_CHAR * 4;
        int endPos  = ( ( MAX_CHAR * 5 ) > length )? (int)( length % ( MAX_CHAR * 5 ) ) : MAX_CHAR * 5;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public String getJsonPartFString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 5 ) > length )? (int)( length % ( MAX_CHAR * 5 ) ) : MAX_CHAR * 5;
        int endPos  = ( ( MAX_CHAR * 6 ) > length )? (int)( length % ( MAX_CHAR * 6 ) ) : MAX_CHAR * 6;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public String getJsonPartGString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 6 ) > length )? (int)( length % ( MAX_CHAR * 6 ) ) : MAX_CHAR * 6;
        int endPos  = ( ( MAX_CHAR * 7 ) > length )? (int)( length % ( MAX_CHAR * 7 ) ) : MAX_CHAR * 7;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public String getJsonPartHString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 7 ) > length )? (int)( length % ( MAX_CHAR * 7 ) ) : MAX_CHAR * 7;
        int endPos  = ( ( MAX_CHAR * 8 ) > length )? (int)( length % ( MAX_CHAR * 8 ) ) : MAX_CHAR * 8;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public String getJsonPartIString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 8 ) > length )? (int)( length % ( MAX_CHAR * 8 ) ) : MAX_CHAR * 8;
        int endPos  = ( ( MAX_CHAR * 9 ) > length )? (int)( length % ( MAX_CHAR * 9 ) ) : MAX_CHAR * 9;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public String getJsonPartJString(){
        if( jsonString == null ) return jsonString;
        int length = jsonString.length();
        int startPos = ( ( MAX_CHAR * 9 ) > length )? (int)( length % ( MAX_CHAR * 9 ) ) : MAX_CHAR * 9;
        int endPos  = ( ( MAX_CHAR * 10 ) > length )? (int)( length % ( MAX_CHAR * 10 ) ) : MAX_CHAR * 10;
        if(startPos < length ) {
            return jsonString.substring(startPos , endPos);
        }
        return null;
    }

    public List<UIPageProperty> getPageProperties() {
    	if(!parsed){
    		parseJsonString();
    	}
    	return pageProperties;
	}
    
    public Table getTable() {
    	if(!parsed){
    		parseJsonString();
    	}
    	return table;
	}

	public Grid getGrid() {
    	if(!parsed){
    		parseJsonString();
    	}
		return grid;
	}
	
	public UIComponentMetadata getReadOnlyUIComponentMetadata() {
		if (null == readOnlyUIComponentMetadata) {
			/* This call will perform parse operations of pageProperties, grid and table JSONString */
			prepareReadOnlyUIComponentMetadata();
		}
		return readOnlyUIComponentMetadata;
	}
	

	private void parseJsonString() {
		if (!StrUtl.isEmptyTrimmed(this.jsonString)) {
			if (UIComponentTypes.PAGE_PROPERTIES.equalsIgnoreCase(componentType)) {
				pageProperties = new ArrayList<UIPageProperty>();
				pageProperties = parsePageProperties(this.jsonString, pageProperties);
			}
			if (UIComponentTypes.TABLE_LAYOUT.equalsIgnoreCase(componentType)) {
				table = new Table();
				table = parseTableLayout(this.jsonString, table);
			}
			if (UIComponentTypes.EXTENDED_GRID.equalsIgnoreCase(componentType)) {
				grid = new Grid();
				grid = parseGridLayout(this.jsonString, grid);
			}
			parsed = true;
			assignLocalizationKeys();
			
			_logger.info("Completed UIComponentMetadata parsing for page name: " + pageName + ", username: " + userName + ", component type: " + componentType + ", component id: " + componentId);
		}
		else {
			_logger.warn("UIComponentMetadata parsing not done. Field jsonString is empty");
		}
	}

	
	private List<UIPageProperty> parsePageProperties(String jsonPageProperties, List<UIPageProperty> currPageProperties) {
		if (!StrUtl.isEmptyTrimmed(jsonPageProperties)) {
			JSONObject jsonObject = StrUtl.isEmptyTrimmed(jsonPageProperties) ? new JSONObject() : JSONObject.fromObject(jsonPageProperties);
			UIPageProperty uiPageProperty = null;
			String propertyName  = null;
			String propertyValue = null;
			for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
				propertyName = (String) iter.next();
				propertyValue = (String) jsonObject.get(propertyName);

				uiPageProperty = new UIPageProperty();
				uiPageProperty.setPropertyName(propertyName);
				uiPageProperty.setPropertyValue(propertyValue);
				currPageProperties.add(uiPageProperty);
			}
			_logger.info("Completed parsing JSONPageProperties");
		}
		return currPageProperties;
	}
	private Table parseTableLayout(String jsonTableLayout, Table currTable) {
		if (!StrUtl.isEmptyTrimmed(jsonTableLayout)) {
			try {
				JSONObject jsonObject = JSONObject.fromObject(jsonTableLayout);

				if (jsonObject.containsKey("table")) {
					JSONObject jsonTable = JSONObject.fromObject(jsonObject.get("table"));

					Table tableSource = (Table) JSONObject.toBean(jsonTable, Table.class);
					currTable.setName(tableSource.getName());
					currTable.setHeading(tableSource.getHeading());
					currTable.setHeadingCss(tableSource.getHeadingCss());
					currTable.setContentCss(tableSource.getContentCss());
					currTable.setDiscrepancyCheck(tableSource.isDiscrepancyCheck());
					
					Object[] rowValues = JSONArray.toCollection(jsonTable.getJSONArray("rows")).toArray();
					JSONObject jsonObj = null;
					JSONObject jsonTableObj = null;
					Row row = null;
					Row rowSource = null;
					Col colSource;
					Col colTarget = null;
					List<Row> rows = new ArrayList<Row>();
					
					String validate = null;
					String dropDownCacheName = null;
					String colLabel = null;
					String mappingName = null;
					for (int i = 0; i < rowValues.length; i++) {
						jsonObj = JSONObject.fromObject(rowValues[i]);
						rowSource = (Row) JSONObject.toBean(jsonObj, Row.class);
						row = new Row();
						if(!StrUtl.isEmptyTrimmed(rowSource.getContainerStyleClass())){
							row.setContainerStyleClass(rowSource.getContainerStyleClass());
						}
						
						rows.add(row);

						Object[] colValues = JSONArray.toCollection(jsonObj.getJSONArray("cols")).toArray();

						for (int j = 0; j < colValues.length; j++) {
							jsonObj = JSONObject.fromObject(colValues[j]);

							Table childTable = null;
							jsonTableObj = jsonObj.getJSONObject("table");

							if (!jsonTableObj.isNullObject()) {
								childTable = new Table();
								childTable = parseTableLayout(jsonObj.toString(), childTable);
							}

							colSource = (Col) JSONObject.toBean(jsonObj, Col.class);

							validate = colSource.getValidate();
							if(!StrUtl.isEmptyTrimmed(validate)){
								validate = validate.replace("\\&amp;", "\\&");
								colSource.setValidate(validate);
							}
							dropDownCacheName = colSource.getDropDownCacheName();
							if(!StrUtl.isEmptyTrimmed(dropDownCacheName)){
								dropDownCacheName= dropDownCacheName.replace("&lt;", "<");
								dropDownCacheName = dropDownCacheName.replace("&gt;", ">");
								colSource.setDropDownCacheName(dropDownCacheName);
							}
							
							colLabel = colSource.getColLabel();
							if(!StrUtl.isEmptyTrimmed(colLabel)){
								colLabel= colLabel.replace("&lt;", "<");
								colLabel = colLabel.replace("&gt;", ">");
								colSource.setColLabel(colLabel);
							}
							mappingName = colSource.getMappingName();
							if(!StrUtl.isEmptyTrimmed(mappingName)){
								mappingName = mappingName.replace("'", "\"");
								colSource.setMappingName(mappingName);
							}

							colTarget = new Col(colSource.getColName(),colSource.getColDataType(),colSource.getColLabel(), colSource.getWidth(), colSource.getLabelPos(), colSource.getValidate(), colSource.isReadonly(), colSource.getRowspan(), colSource.getColspan(), colSource.getContainerStyleClass(),colSource.getControlStyleClass(),colSource.getLookupLink(),colSource.getDropDownCacheName(),colSource.isUdf(), childTable);
							colTarget.setValidationMessages( colSource.getValidationMessages() );
							colTarget.setDisabled(colSource.isDisabled());
							colTarget.setMappingName(colSource.getMappingName());
							colTarget.setDefaultValue(colSource.getDefaultValue());
							colTarget.setColDataDelimiter(colSource.getColDataDelimiter());
							colTarget.setColFormat(colSource.getColFormat());
							colTarget.setPrivilegeExpr(colSource.getPrivilegeExpr());
							colTarget.setAdditionalParams(colSource.getAdditionalParams());
							row.addCol(colTarget);
						}
					}

					currTable.setRows(rows);
				}
				_logger.info("Completed parsing JSONTableLayout");
			}
			catch (Exception ex) {
				_logger.error(ex);
			}
		}
		return currTable;
	}

    private Grid parseGridLayout(String jsonGridLayout, Grid currGrid) {
        if (!StrUtl.isEmptyTrimmed(jsonGridLayout)) {
            try {
                JSONObject jsonObject = JSONObject.fromObject(jsonGridLayout);

                if (jsonObject.get("grid") != null) {
                    Grid gridSource = null;
                    GridCol gridColSource;
                    GridCol gridColTarget = null;
                    JSONObject jsonObj = null;
                    
					String validate = null;
					String dropDownCacheName = null;
					String colLabel = null;
					String mappingName = null;
                    

                    JSONObject jsonGrid = JSONObject.fromObject(jsonObject.get("grid"));
                    gridSource = (Grid) JSONObject.toBean(jsonGrid, Grid.class);
                    currGrid.setName(gridSource.getName());
                    currGrid.setHeader(gridSource.getHeader());
                    currGrid.setEditable(gridSource.isEditable());
                    currGrid.setSortable(gridSource.isSortable());
                    currGrid.setCustomization(gridSource.isCustomization());
                    currGrid.setContentCss(gridSource.getContentCss());
                    currGrid.setAddnew(gridSource.isAddnew());
                    currGrid.setAddnewButtonTxt(gridSource.getAddnewButtonTxt());
                    currGrid.setDefaultRows(gridSource.getDefaultRows());
                    currGrid.setZoneId(gridSource.getZoneId());
                    currGrid.setBlockId(gridSource.getBlockId());

                    Object[] colValues = JSONArray.toCollection(jsonGrid.getJSONArray("cols")).toArray();

                    for (int j = 0; j < colValues.length; j++) {
                        jsonObj = JSONObject.fromObject(colValues[j]);
                        gridColSource = (GridCol) JSONObject.toBean(jsonObj, GridCol.class);
                        
						validate = gridColSource.getValidate();
						if(!StrUtl.isEmptyTrimmed(validate)){
							validate = validate.replace("\\&amp;", "\\&");
							gridColSource.setValidate(validate);
						}
						dropDownCacheName = gridColSource.getDropDownCacheName();
						if(!StrUtl.isEmptyTrimmed(dropDownCacheName)){
							dropDownCacheName= dropDownCacheName.replace("&lt;", "<");
							dropDownCacheName = dropDownCacheName.replace("&gt;", ">");
							gridColSource.setDropDownCacheName(dropDownCacheName);
						}
						
						colLabel = gridColSource.getColLabel();
						if(!StrUtl.isEmptyTrimmed(colLabel)){
							colLabel= colLabel.replace("&lt;", "<");
							colLabel = colLabel.replace("&gt;", ">");
							gridColSource.setColLabel(colLabel);
						}
						mappingName = gridColSource.getMappingName();
						if(!StrUtl.isEmptyTrimmed(mappingName)){
							mappingName = mappingName.replace("'", "\"");
							gridColSource.setMappingName(mappingName);
						}
						
                        gridColTarget = new GridCol(gridColSource.getColName(),gridColSource.getColDataType(),gridColSource.getColLabel(), gridColSource.isReadOnly(), gridColSource.getWidth(), gridColSource.getTextAlign(), gridColSource.getValidate());
                        gridColTarget.setValidationMessages( gridColSource.getValidationMessages() );
                        gridColTarget.setDisabled( gridColSource.isDisabled() );
                        gridColTarget.setKey(gridColSource.isKey());
                        gridColTarget.setDisplayable(gridColSource.isDisplayable());
                        gridColTarget.setLookupLink(gridColSource.getLookupLink());
                        gridColTarget.setDropDownCacheName(gridColSource.getDropDownCacheName());
                        gridColTarget.setUdf(gridColSource.isUdf());
                        gridColTarget.setDefaultValue(gridColSource.getDefaultValue());
                        gridColTarget.setMappingName(gridColSource.getMappingName() );
                        gridColTarget.setColDataDelimiter(gridColSource.getColDataDelimiter());
                        gridColTarget.setControlStyleClass(gridColSource.getControlStyleClass());
                        gridColTarget.setContainerStyleClass(gridColSource.getContainerStyleClass());
                        gridColTarget.setColFormat(gridColSource.getColFormat());
                        gridColTarget.setPrivilegeExpr(gridColSource.getPrivilegeExpr());
						gridColTarget.setAdditionalParams(gridColSource.getAdditionalParams());
                        currGrid.addGridCol(gridColTarget);
                    }
                    _logger.info("Completed parsing JSONGridLayout");
                }
            } catch (Exception ex) {
            	_logger.error(ex);
            }
        }
        return currGrid;
    }
    
	public void reset() {
		table = null;
		grid = null;
		parsed = false;
		_logger.info("Completed UIComponentMetadata reset");
	}
    
	public JsonConfig getJsonConfig() {
		if (propertyExclusions == null) {
			setPropertyExclusions();
		}
		JsonConfig jsonConfig = new JsonConfig();
		// Default implementation return "" for null values
		jsonConfig.registerDefaultValueProcessor(String.class, new DefaultValueProcessor() {
			public Object getDefaultValue(Class type) {
				return JSONNull.getInstance(); 
			}
		});
		
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (propertyExclusions.containsKey(source.getClass().getName())) {
					List<String> exclude = propertyExclusions.get(source.getClass().getName());
					if ( value == null || exclude.contains(name)) {
						return true; //Only not null values are serialized. 
					}
				}
				return false;
			}
		});
		return jsonConfig;
	}

	private void setPropertyExclusions() {
		propertyExclusions = new HashMap<String, List<String>>();

		List<String> exclude = new ArrayList<String>();
		// Add exclude methods in Table.class here
		propertyExclusions.put(Table.class.getName(), exclude);

		exclude = new ArrayList<String>();
		// Add exclude methods in Row.class here
		propertyExclusions.put(Row.class.getName(), exclude);

		exclude = new ArrayList<String>();
		// Add exclude methods in Col.class here
		exclude.add("operator");
		exclude.add("labelLeft");
		exclude.add("labelRight");
		exclude.add("labelTop");
		exclude.add("labelNone");
		exclude.add("columnSet");
		exclude.add("tableSet");
		exclude.add("emptySet");
		exclude.add("hidden");
		exclude.add("editable");
		exclude.add("linkLookup");
		propertyExclusions.put(Col.class.getName(), exclude);

		exclude = new ArrayList<String>();
		// Add exclude methods in Grid.class here
		exclude.add("keyCols");
		exclude.add("displayableCols");
		propertyExclusions.put(Grid.class.getName(), exclude);

		exclude = new ArrayList<String>();
		// Add exclude methods in GridCol.class here
		exclude.add("linkLookup");
		exclude.add("hidden");
		propertyExclusions.put(GridCol.class.getName(), exclude);
	}

	public void prepareReadOnlyUIComponentMetadata() {
		UIComponentMetadata uiCmd = null;
		try {
			uiCmd = (UIComponentMetadata) GenUtl.cloneObject(this);
			Grid grid = uiCmd.getGrid();
			Table table = uiCmd.getTable();
			
			/* Using the grid object of this class, create a readonly grid JSON string */
			if (grid != null) {
				for (GridCol col : grid.getDisplayableCols()) {
					col.setReadOnly(true);
					if ("delete".equalsIgnoreCase(col.getColName())) {
						col.setDisplayable(false);
					}
				}
				grid.setAddnew(false);
				grid.setEditable(false);
				JSONObject jsonGrid = new JSONObject();
				jsonGrid.put("grid", JSONObject.fromObject(grid, uiCmd.getJsonConfig()));
				JSONObject json = JSONObject.fromObject(jsonGrid);
				uiCmd.setJsonString(json.toString());
			}
			
			/* Using the table object of this class, create a readonly table JSON string */
			if (table != null) {
				for (Row row : table.getRows()) {
					for (Col col : row.getCols()) {
						col.setReadonly(true);
					}
				}
				if (!StrUtl.isEmptyTrimmed(table.getContentCss()) && !table.getContentCss().endsWith("_readonly")) {
					table.setContentCss(table.getContentCss() + "_readonly");
				}
				JSONObject jsonTable = new JSONObject();
				jsonTable.put("table", JSONObject.fromObject(table, uiCmd.getJsonConfig()));
				JSONObject json = JSONObject.fromObject(jsonTable);
				uiCmd.setJsonString(json.toString());
			}
			/* To parse and recreate grid and table objects for readonly UIComponentMetadata, old reference is set to null */
			uiCmd.reset();
			
			/* Using the readonly grid and table JSON String set, create the respective grid and table objects */
			uiCmd.parseJsonString();
            _logger.info("Completed preparing ReadOnlyUIComponentMetadata");
		}
		catch (Exception ex) {
			_logger.error(ex);
			uiCmd = null;
		}
		readOnlyUIComponentMetadata = uiCmd;
	}
	
	public Col findCol(String colName){
		if (getTable() != null) {
			for (Row row : table.getRows()) {
				for (Col col : row.getCols()) {
					if(col.getColName().equalsIgnoreCase(colName)){
						return col;
					}
				}
			}
		}
		return null;
	}
	
	public GridCol findGridCol(String colName){
		if (getGrid() != null) {
			for (GridCol gridCol : grid.getCols()) {
				if(gridCol.getColName().equalsIgnoreCase(colName)){
					return gridCol;
				}
			}
		}
		return null;
	}
	
	public void removeCol(String colName){
		if (getTable() != null) {
			for (Row row : table.getRows()) {
				for(Iterator<Col> iter = row.getCols().iterator(); iter.hasNext();) {
					if(iter.next().getColName().equalsIgnoreCase(colName)){
						iter.remove();
						break;
					}
				 }
			}
		}
		if (getGrid() != null) {
			for(Iterator<GridCol> iter = grid.getCols().iterator(); iter.hasNext();) {
				if(iter.next().getColName().equalsIgnoreCase(colName)){
					iter.remove();
					break;
				}
			 }
		}		
	}


	/*
	 * Setup localization keys. For use later in components.
	 */
	private void assignLocalizationKeys() {									
		String pageName = this.getPageName();
		
		if (table != null) {
			String tableName = table.getName();
			String tableKey = pageName + "." + tableName + _HEADING_SUFFIX;
			table.setHeadingKey(tableKey);
							
			for (Row row : table.getRows()) {
				for (Col col : row.getCols()) {						
					String colName = col.getColName();
					String columnKey = pageName + "." + tableName + "." + colName + _LABEL_SUFFIX;						
					col.setColLabelKey(columnKey);
					
					if (!StrUtl.isEmptyTrimmed(col.getValidationMessages())) {
						String validationKey = pageName + "." + tableName + "." + colName + _MESSAGE_SUFFIX;							
						col.setValidationMessagesKey(validationKey);							
					}
				}					
			}
		}
		
		if (grid != null) {
			String gridName = grid.getName();				
			String gridKey = pageName + "." + gridName +  _HEADING_SUFFIX;				
			grid.setHeaderKey(gridKey);

			String addnewKey = pageName + "." + gridName +  _ADDNEW_SUFFIX;				
			grid.setAddNewKey(addnewKey);

			for (GridCol col : grid.getDisplayableCols()) {
				String colName = col.getColName();
				String columnKey = pageName + "." + gridName + "." + colName + _LABEL_SUFFIX;
				col.setColLabelKey(columnKey);
				
				if (!StrUtl.isEmptyTrimmed(col.getValidationMessages())) {
					String validationKey = pageName + "." + gridName + "." + colName + _MESSAGE_SUFFIX;
					col.setValidationMessagesKey(validationKey);							
				}					
			}
		}		
		
		return;		
	}


}
