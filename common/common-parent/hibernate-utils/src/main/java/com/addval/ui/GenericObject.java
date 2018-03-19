package com.addval.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.addval.utils.LogMgr;
import com.addval.utils.StrUtl;

public class GenericObject implements Serializable{
	private static transient final Logger logger = LogMgr.getLogger(GenericObject.class);

	private String id = null;
	private String componentIdRef = null;
	private HashMap<String, GenericBean> propertyBeans = null;
	private HashMap<String, GenericObject> child = null;
	private HashMap<String, ArrayList<GenericObject>> childList = null;

	private String keyContextNames = null; //pipeline separated keys .
	private String keyContextValues = null; //pipeline separated key values.
	private boolean rowselected;

	public boolean isRowselected() {
		return rowselected;
	}

	public void setRowselected(boolean rowselected) {
		this.rowselected = rowselected;
	}

	public GenericObject() {
		propertyBeans = new HashMap<String, GenericBean>();
		child = new HashMap<String, GenericObject>();
		childList = new HashMap<String, ArrayList<GenericObject>>();
	}

	public GenericObject(String id) {
		this();
		this.setId(id);
	}
	public GenericObject(Grid grid) {
		this();
		addBeans(grid);
	}

	public GenericObject(Table table) {
		this();
		addBeans(table);
	}

	public GenericObject(UIComponentMetadata uiCmd) {
		this();
		if (uiCmd != null) {
			this.componentIdRef = uiCmd.getComponentId();
			addBeans(uiCmd.getTable());
			addBeans(uiCmd.getGrid());
		}
	}
	
	public String getComponentIdRef() {
		return componentIdRef;
	}

	public void setComponentIdRef(String componentIdRef) {
		this.componentIdRef = componentIdRef;
	}

	private void addBeans(Grid grid){
		if (grid != null) {
			EditorFieldHolderBean editorBean = null;
			StringHolderBean stringBean = null;
			DateHolderBean dateBean = null;
			DoubleHolderBean doubleBean = null;
			IntegerHolderBean integerBean = null;
			BooleanHolderBean booleanBean = null;

			for (GridCol col : grid.getCols()) {
				if (ControlTypes._TEXT_TYPE.equalsIgnoreCase(col.getColDataType()) || 
						ControlTypes._HIDDEN_TYPE.equalsIgnoreCase(col.getColDataType()) || 
						ControlTypes._PASSWORD_TYPE.equalsIgnoreCase(col.getColDataType()) ||
						ControlTypes._ENUM_TYPE.equalsIgnoreCase(col.getColDataType()) ||
						ControlTypes._OPTION_TYPE.equalsIgnoreCase(col.getColDataType()) ||
						ControlTypes._OPERATOR_TYPE.equalsIgnoreCase(col.getColDataType()) ||
						ControlTypes._LONGTEXT_TYPE.equalsIgnoreCase(col.getColDataType())) {
					stringBean = new StringHolderBean();
					stringBean.setId(col.getColName());
					stringBean.setMappingName(col.getMappingName());
					stringBean.setUdf(col.isUdf());
					stringBean.setDataDelimiter(col.getColDataDelimiter());
					stringBean.setFormat(col.getColFormat());
					if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
						stringBean.setValue( col.getDefaultValue() );
					}
					add(stringBean);
				}
				else if (ControlTypes._EDITORFIELD_TYPE.equalsIgnoreCase(col.getColDataType())) {
					editorBean = new EditorFieldHolderBean();
					editorBean.setId(col.getColName());
					editorBean.setMappingName(col.getMappingName());
					editorBean.setUdf(col.isUdf());
					editorBean.setDataDelimiter(col.getColDataDelimiter());
					editorBean.setFormat(col.getColFormat());
					add(editorBean);
				}
				else if (ControlTypes._DATE_TYPE.equalsIgnoreCase(col.getColDataType())) {
					dateBean = new DateHolderBean();
					dateBean.setId(col.getColName());
					dateBean.setMappingName(col.getMappingName());
					dateBean.setUdf(col.isUdf());
					dateBean.setDataDelimiter(col.getColDataDelimiter());
					dateBean.setFormat(col.getColFormat());

					add(dateBean);
				}
				else if (ControlTypes._NUMBER_TYPE.equalsIgnoreCase(col.getColDataType()) || ControlTypes._DOUBLE_TYPE.equalsIgnoreCase(col.getColDataType()) ) {
					doubleBean = new DoubleHolderBean();
					doubleBean.setId(col.getColName());
					doubleBean.setMappingName(col.getMappingName());
					doubleBean.setUdf(col.isUdf());
					doubleBean.setDataDelimiter(col.getColDataDelimiter());
					doubleBean.setFormat(col.getColFormat());

					if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
						doubleBean.setValue( Double.valueOf( col.getDefaultValue() ));
					}
					add(doubleBean);
				}
				else if (ControlTypes._INTEGER_TYPE.equalsIgnoreCase(col.getColDataType())) {
					integerBean = new IntegerHolderBean();
					integerBean.setId(col.getColName());
					integerBean.setMappingName(col.getMappingName());
					integerBean.setUdf(col.isUdf());
					integerBean.setDataDelimiter(col.getColDataDelimiter());
					integerBean.setFormat(col.getColFormat());

					if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
						integerBean.setValue( Integer.valueOf( col.getDefaultValue() ));
					}
					add(integerBean);
				}
				else if (ControlTypes._BOOLEAN_TYPE.equalsIgnoreCase(col.getColDataType()) ) {
					
					booleanBean = new BooleanHolderBean();
					booleanBean.setId(col.getColName());
					booleanBean.setMappingName(col.getMappingName());
					booleanBean.setUdf(col.isUdf());
					booleanBean.setDataDelimiter(col.getColDataDelimiter());
					booleanBean.setFormat(col.getColFormat());

					if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
						if(col.getDefaultValue().equalsIgnoreCase("TRUE") || col.getDefaultValue().equalsIgnoreCase("FALSE")){
							booleanBean.setValue( Boolean.valueOf( col.getDefaultValue() ));	
						}
						if(col.getDefaultValue().equalsIgnoreCase("Y") || col.getDefaultValue().equalsIgnoreCase("N")){
							booleanBean.setValue( col.getDefaultValue().equalsIgnoreCase("Y")  );	
						}
					}
					add(booleanBean);
				}
			}
		}
	}

	private void addBeans(Table table){
		if (table != null) {
			EditorFieldHolderBean editorBean = null;
			StringHolderBean stringBean = null;
			DateHolderBean dateBean = null;
			DoubleHolderBean doubleBean = null;
			IntegerHolderBean integerBean = null;
			BooleanHolderBean booleanBean = null;
			
			for (Row row : table.getRows()) {
				for (Col col : row.getCols()) {
					if (ControlTypes._TEXT_TYPE.equalsIgnoreCase(col.getColDataType()) || 
							ControlTypes._HIDDEN_TYPE.equalsIgnoreCase(col.getColDataType()) || 
							ControlTypes._PASSWORD_TYPE.equalsIgnoreCase(col.getColDataType()) ||
							ControlTypes._ENUM_TYPE.equalsIgnoreCase(col.getColDataType()) ||
							ControlTypes._OPTION_TYPE.equalsIgnoreCase(col.getColDataType()) ||
							ControlTypes._OPERATOR_TYPE.equalsIgnoreCase(col.getColDataType()) ||
							ControlTypes._LONGTEXT_TYPE.equalsIgnoreCase(col.getColDataType())) {
						stringBean = new StringHolderBean();
						stringBean.setId(col.getColName());
						stringBean.setMappingName(col.getMappingName());
						stringBean.setUdf(col.isUdf());
						stringBean.setDataDelimiter(col.getColDataDelimiter());
						stringBean.setFormat(col.getColFormat());

						if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
							stringBean.setValue( col.getDefaultValue() );
						}
						add(stringBean);
					}
					else if (ControlTypes._EDITORFIELD_TYPE.equalsIgnoreCase(col.getColDataType())) {
						editorBean = new EditorFieldHolderBean();
						editorBean.setId(col.getColName());
						editorBean.setMappingName(col.getMappingName());
						editorBean.setUdf(col.isUdf());
						editorBean.setDataDelimiter(col.getColDataDelimiter());
						editorBean.setFormat(col.getColFormat());

						add(editorBean);
					}					
					else if (ControlTypes._DATE_TYPE.equalsIgnoreCase(col.getColDataType())) {
						dateBean = new DateHolderBean();
						dateBean.setId(col.getColName());
						dateBean.setMappingName(col.getMappingName());
						dateBean.setUdf(col.isUdf());
						dateBean.setDataDelimiter(col.getColDataDelimiter());
						dateBean.setFormat(col.getColFormat());

						add(dateBean);
					}
					else if (ControlTypes._NUMBER_TYPE.equalsIgnoreCase(col.getColDataType()) || ControlTypes._DOUBLE_TYPE.equalsIgnoreCase(col.getColDataType()) ) {
						doubleBean = new DoubleHolderBean();
						doubleBean.setId(col.getColName());
						doubleBean.setMappingName(col.getMappingName());
						doubleBean.setUdf(col.isUdf());
						doubleBean.setDataDelimiter(col.getColDataDelimiter());
						doubleBean.setFormat(col.getColFormat());

						if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
							doubleBean.setValue( Double.valueOf( col.getDefaultValue() ));
						}
						add(doubleBean);
					}
					else if (ControlTypes._INTEGER_TYPE.equalsIgnoreCase(col.getColDataType())) {
						integerBean = new IntegerHolderBean();
						integerBean.setId(col.getColName());
						integerBean.setMappingName(col.getMappingName());
						integerBean.setUdf(col.isUdf());
						integerBean.setDataDelimiter(col.getColDataDelimiter());
						integerBean.setFormat(col.getColFormat());

						if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
							integerBean.setValue( Integer.valueOf( col.getDefaultValue() ));
						}
						add(integerBean);
					}
					else if (ControlTypes._BOOLEAN_TYPE.equalsIgnoreCase(col.getColDataType()) ) {
						
						booleanBean = new BooleanHolderBean();
						booleanBean.setId(col.getColName());
						booleanBean.setMappingName(col.getMappingName());
						booleanBean.setUdf(col.isUdf());
						booleanBean.setDataDelimiter(col.getColDataDelimiter());
						booleanBean.setFormat(col.getColFormat());

						if(!StrUtl.isEmptyTrimmed( col.getDefaultValue() )){
							if(col.getDefaultValue().equalsIgnoreCase("TRUE") || col.getDefaultValue().equalsIgnoreCase("FALSE")){
								booleanBean.setValue( Boolean.valueOf( col.getDefaultValue() ));	
							}
							if(col.getDefaultValue().equalsIgnoreCase("Y") || col.getDefaultValue().equalsIgnoreCase("N")){
								booleanBean.setValue( col.getDefaultValue().equalsIgnoreCase("Y")  );	
							}
						}
						add(booleanBean);
					}
				}
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void add(GenericBean bean) {
		if (bean == null || bean.getId() == null) {
			throw new RuntimeException("GenericObject:add - GenericBean && Id should not be null.");
		}
		propertyBeans.put(bean.getId().toUpperCase(), bean);
	}

	public GenericBean get(String id) {
		return (id != null && propertyBeans != null && propertyBeans.containsKey( id.toUpperCase() )) ? propertyBeans.get( id.toUpperCase() ) : null;

	}

	public void addChild(GenericObject gObj) {
		if (gObj == null || gObj.getId() == null) {
			throw new RuntimeException("GenericObject:addChild - GenericObject && Id should not be null.");
		}
		child.put(gObj.getId().toUpperCase(), gObj);
	}

	public void addChildList(String gObjListId, ArrayList<GenericObject> gObjList) {
		if (gObjList == null || gObjList == null) {
			throw new RuntimeException("GenericObject:addChildList - List && Id should not be null.");
		}
		childList.put(gObjListId.toUpperCase(), gObjList);
	}

	public Set<String> getPropertyNames() {
		return propertyBeans.keySet();
	}

	public GenericObject child(String childId) {
		if (child.containsKey(childId.toUpperCase())) {
			return child.get(childId.toUpperCase());
		}
		return null;
	}

	public ArrayList<GenericObject> childList(String childListId) {
		if (childList.containsKey(childListId.toUpperCase())) {
			return childList.get(childListId.toUpperCase());
		}
		return null;
	}

	public StringHolderBean stringBean(String propertyName) {
		Object bean = findBean(propertyName);
		if (bean != null) {
			return (StringHolderBean) bean;
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public IntegerHolderBean integerBean(String propertyName) {
		Object bean = findBean(propertyName);
		if (bean != null) {
			return (IntegerHolderBean) bean;
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public DoubleHolderBean doubleBean(String propertyName) {
		Object bean = findBean(propertyName);
		if (bean != null) {
			return (DoubleHolderBean) bean;
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public EditorFieldHolderBean editorBean(String propertyName) {
		Object bean = findBean(propertyName);
		if (bean != null) {
			return (EditorFieldHolderBean) bean;
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public DateHolderBean dateBean(String propertyName) {
		Object bean = findBean(propertyName);
		if (bean != null) {
			return (DateHolderBean) bean;
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public BooleanHolderBean booleanBean(String propertyName) {
		Object bean = findBean(propertyName);
		if (bean != null) {
			return (BooleanHolderBean) bean;
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public boolean containsProperty(String propertyName) {
		return propertyBeans.containsKey(propertyName.toUpperCase()); 
	}

	public GenericBean getBean(String propertyName) {
		if (propertyBeans.containsKey(propertyName.toUpperCase())) {
			return propertyBeans.get(propertyName.toUpperCase());
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	private GenericBean findBean(String propertyName) {
		if (propertyBeans.containsKey(propertyName.toUpperCase())) {
			return propertyBeans.get(propertyName.toUpperCase());
		}
		throw new RuntimeException("Property Name : " + propertyName + " is invalid OR not configured for "+ getComponentIdRef() );
	}

	public void clear() {
		GenericBean bean = null;
		for (String propertyName : propertyBeans.keySet()) {
			bean = propertyBeans.get(propertyName);
			bean.setValue(null);
		}
	}

	public static String getPropertyName(Col col) {
		String propName = null;
		if (ControlTypes._TEXT_TYPE.equalsIgnoreCase(col.getColDataType()) || 
				ControlTypes._HIDDEN_TYPE.equalsIgnoreCase(col.getColDataType()) || 
				ControlTypes._PASSWORD_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._ENUM_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._OPTION_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._OPERATOR_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._LONGTEXT_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "stringBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._EDITORFIELD_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "editorBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._DATE_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "dateBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._DOUBLE_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._NUMBER_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "doubleBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._INTEGER_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "integerBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._BOOLEAN_TYPE.equalsIgnoreCase(col.getColDataType()) ) {
			propName = "booleanBean('" + col.getColName() + "').value";
		}
		return propName !=null ? propName : col.getColName();
	}

	public static String getPropertyName(GridCol col) {
		String propName = null;
		if (ControlTypes._TEXT_TYPE.equalsIgnoreCase(col.getColDataType()) || 
				ControlTypes._HIDDEN_TYPE.equalsIgnoreCase(col.getColDataType()) || 
				ControlTypes._PASSWORD_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._ENUM_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._OPTION_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._OPERATOR_TYPE.equalsIgnoreCase(col.getColDataType()) ||
				ControlTypes._LONGTEXT_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "stringBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._EDITORFIELD_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "editorBean('" + col.getColName() + "').value";
		}		
		else if (ControlTypes._DATE_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "dateBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._NUMBER_TYPE.equalsIgnoreCase(col.getColDataType()) || ControlTypes._DOUBLE_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "doubleBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._INTEGER_TYPE.equalsIgnoreCase(col.getColDataType())) {
			propName = "integerBean('" + col.getColName() + "').value";
		}
		else if (ControlTypes._BOOLEAN_TYPE.equalsIgnoreCase(col.getColDataType()) ) {
			propName = "booleanBean('" + col.getColName() + "').value";
		}
		return propName !=null ? propName : col.getColName();
	}

	public void setJsonString(String jsonString) {
		if (!StrUtl.isEmptyTrimmed(jsonString)) {
			SimpleDateFormat sdf = new SimpleDateFormat();
			try {
				JSONObject jsonObject = JSONObject.fromObject(jsonString);
				if(propertyBeans != null){
					GenericBean bean;
					Object value = null;
					for(String propertyName: propertyBeans.keySet()){
						value = jsonObject.get( propertyName ); 
						bean = propertyBeans.get( propertyName );
						if(bean instanceof DateHolderBean){
							value = (value != null)? sdf.parse(value.toString()) : null;
						}
						bean.setValue( value );
					}
				}
			}
			catch (Exception ex) {
			}
		}
	}
	public String getJsonString() {
		JSONObject jsonObject = new JSONObject();
		if(propertyBeans != null){
			GenericBean bean;
			Object value = null;
			SimpleDateFormat sdf = new SimpleDateFormat();
			for(String propertyName: propertyBeans.keySet()){
				bean = propertyBeans.get( propertyName );
				value = bean.getValue();
				if(bean instanceof DateHolderBean){
					value = (value != null)? sdf.format(value) : null;
				}
				jsonObject.put(propertyName,value);
			}
		}
		return jsonObject.toString();
	}

	public Map getKeyValueMap() {
		Map keyValueMap = new HashMap();
		GenericBean bean = null;
		String key = null;
		for (String propName : getPropertyNames()) {
			bean = get(propName);
			key = !StrUtl.isEmptyTrimmed( bean.getMappingName() )? bean.getMappingName() : bean.getId();
			keyValueMap.put(key,bean.getValue());	
		}
		return keyValueMap;
	}

	public String getKeyContextNames() {
		return keyContextNames;
	}

	public void setKeyContextNames(String keyContextNames) {
		this.keyContextNames = keyContextNames;
	}

	public String getKeyContextValues() {
		return keyContextValues;
	}

	public void setKeyContextValues(String keyContextValues) {
		this.keyContextValues = keyContextValues;
	}

	public boolean isEmpty(){
		boolean isEmpty = true;
		GenericBean genericBean = null;
		for (String propName : getPropertyNames()) {
			genericBean = get(propName);
			if(genericBean.getValue() != null){
				isEmpty = false;
				break;
			}
		}
		return isEmpty;
	}
}
