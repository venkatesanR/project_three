//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\metadata\\ColumnMetaData.java

//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\metadata\\ColumnMetaData.java
package com.addval.metadata;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.addval.utils.AVConstants;

public class ColumnMetaData implements Serializable, Cloneable {
	private static final transient String _module = "ColumnMetaData";
	private boolean _aggregatable = false;
	private boolean _searchable = false;
	private String _searchGroup = null;

	private boolean _displayable = false;
	private String _displayGroup = "0";

	private String _updateGroup = null;
	private boolean _updatable = false;
	private boolean _linkable = false;
	private String _expression = null;
	private String _columnDecoratorProperty = null;
	private String _columnBeanProperty = null;
	private boolean _key = false;
	private boolean _nullable = true;
	private boolean _editKey = false;
	private boolean _editKeyDisplayable = false;
	private boolean _secured = false;
	private boolean _comboCached = false;
	private boolean _comboSelectTag = false;
	private Object _ejbResultSet = null;
	private ColumnInfo _columnInfo = null;
	private String _autoSequenceName = null;
	private int ordering = AVConstants._ASC;
	private int sortOrderSeq = -1;
	private int searchOrderSeq = 0;
	private static final int _DEFAULT_TEXT_SIZE = 10;
	private boolean _searchableMandatory = false;
	private String _searchMandatoryGroup = null;
	private String _editRegExp = null;
	private String _editErrorMsg = null;
    private boolean indexedUpperCaseData = false; // While building search SQL for like operator UPPER(column_name) or not. refer EJBSQLBuilderUtils.java


	/**
	 * @param aggregatable
	 * @param searchable
	 * @param displayable
	 * @param updatable
	 * @param linkable
	 * @param expression
	 * @param key
	 * @param nullable
	 * @param editKey
	 * @param editKeyDisplayable
	 * @param secured
	 * @param searchableMandatory
	 * @roseuid 3E52AC9F02E2
	 */
	public ColumnMetaData(boolean aggregatable, boolean searchable, boolean displayable, boolean updatable, boolean linkable, String expression, boolean key, boolean nullable, boolean editKey, boolean editKeyDisplayable, boolean secured, boolean searchableMandatory) {

		this(aggregatable, searchable, displayable, updatable, linkable, expression, key, searchableMandatory);

		_nullable = nullable;
		_editKey = editKey;
		_editKeyDisplayable = editKeyDisplayable;
		_secured = secured;
	}

	/**
	 * @param aggregatable
	 * @param searchable
	 * @param displayable
	 * @param updatable
	 * @param linkable
	 * @param expression
	 * @param key
	 * @param searchableMandatory
	 * @roseuid 3AEDBE9F02EA
	 */
	public ColumnMetaData(boolean aggregatable, boolean searchable, boolean displayable, boolean updatable, boolean linkable, String expression, boolean key, boolean searchableMandatory) {

		_aggregatable = aggregatable;
		_searchable = searchable;
		_displayable = displayable;
		_updatable = updatable;
		_linkable = linkable;
		_expression = expression;
		_key = key;
		_searchableMandatory = searchableMandatory;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE880002
	 */
	public String getName() {

		return getColumnInfo().getName();
	}

	/**
	 * @return boolean
	 * @roseuid 3AEDBE38033C
	 */
	public boolean isAggregatable() {

		return _aggregatable;
	}

	/**
	 * @param aggregatable
	 * @roseuid 3D5078F5002E
	 */
	public void setAggregatable(boolean aggregatable) {

		_aggregatable = aggregatable;
	}

	/**
	 * @return boolean
	 * @roseuid 3AEDBE390077
	 */
	public boolean isSearchable() {

		return _searchable;
	}

	/**
	 * @param searchable
	 * @roseuid 3D50791101E7
	 */
	public void setSearchable(boolean searchable) {

		_searchable = searchable;
	}

	public void setSearchGroup(String searchGroup) {
		this._searchGroup = searchGroup;
	}

	public boolean isBaseSearch() {
		return "1".equalsIgnoreCase(this._searchGroup);
	}

	public boolean isAdvancedSearch() {
		return "2".equalsIgnoreCase(this._searchGroup);
	}

	public boolean isAdvancedMultiRowSearch() {
		return "3".equalsIgnoreCase(this._searchGroup);
	}

	/**
	 * @return boolean
	 * @roseuid 3AEDBE39017B
	 */
	public boolean isDisplayable() {

		return _displayable;
	}

	/**
	 * @param displayable
	 * @roseuid 3D507919026B
	 */
	public void setDisplayable(boolean displayable) {
		_displayable = displayable;
	}

	public void setDisplayGroup(String displayGroup) {
		this._displayGroup = displayGroup;
	}

	public String getDisplayGroup() {
		return this._displayGroup;
	}
	
	public boolean isBaseDisplay() {
		return "1".equalsIgnoreCase(this._displayGroup);
	}

	public boolean isAdvancedDisplay() {
		return "2".equalsIgnoreCase(this._displayGroup);
	}

	public boolean isBaseDisplayLink() {
		return "3".equalsIgnoreCase(this._displayGroup);
	}

	/**
	 * @return boolean
	 * @roseuid 3AEDBE39029D
	 */
	public boolean isUpdatable() {

		return _updatable;
	}

	/**
	 * @param updatable
	 * @roseuid 3C97800B00C8
	 */
	public void setUpdatable(boolean updatable) {

		_updatable = updatable;
	}

	public void setUpdateGroup(String updateGroup) {
		this._updateGroup = updateGroup;
	}

	public boolean isUpdateGroup(String updateGroup) {
		return updateGroup.equalsIgnoreCase(this._updateGroup);
	}

	public String getUpdateGroup() {
		return this._updateGroup;
	}

	/**
	 * @return boolean
	 * @roseuid 3C927E4E0352
	 */
	public boolean isLinkable() {

		return _linkable;
	}

	/**
	 * @param linkable
	 * @roseuid 3D50792F023A
	 */
	public void setLinkable(boolean linkable) {

		_linkable = linkable;
	}

	/**
	 * @param key
	 * @roseuid 3C97801C0311
	 */
	public void setKey(boolean key) {

		_key = key;
	}

	/**
	 * @return boolean
	 * @roseuid 3B20BC7A00AE
	 */
	public boolean isKey() {
		return _key;
	}

	/**
	 * @return boolean
	 * @roseuid 3AF0BCD70008
	 */
	public boolean isNullable() {
		return _nullable;
	}

	/**
	 * @param v
	 * @roseuid 3CA3B53F0391
	 */
	public void setNullable(boolean v) {
		_nullable = v;
	}

	/**
	 * @param editKey
	 * @roseuid 3CA8FA68012D
	 */
	public void setEditKey(boolean editKey) {
		_editKey = editKey;
	}

	/**
	 * @return boolean
	 * @roseuid 3CA8FA680169
	 */
	public boolean isEditKey() {
		return _editKey;
	}

	/**
	 * @param editKeyDisplayable
	 * @roseuid 3CA8FA8A01EA
	 */
	public void setEditKeyDisplayable(boolean editKeyDisplayable) {

		_editKeyDisplayable = editKeyDisplayable;
	}

	/**
	 * @return boolean
	 * @roseuid 3CA8FA8A0226
	 */
	public boolean isEditKeyDisplayable() {

		return _editKeyDisplayable;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE3903C0
	 */
	public String getExpression() {

		return _expression;
	}

	/**
	 * @param v
	 * @roseuid 3D505CC3031E
	 */
	public void setExpression(String v) {

		_expression = v;
	}

	public String getColumnDecoratorProperty() {
		return _columnDecoratorProperty;
	}

	public void setColumnDecoratorProperty(String columnDecoratorProperty) {
		this._columnDecoratorProperty = columnDecoratorProperty;
	}

	public String getColumnBeanProperty() {
		return _columnBeanProperty;
	}

	public void setColumnBeanProperty(String columnBeanProperty) {
		this._columnBeanProperty = columnBeanProperty;
	}

	/**
	 * @param columnInfo
	 * @roseuid 3AEDCF2401B5
	 */
	public void setColumnInfo(ColumnInfo columnInfo) {

		_columnInfo = columnInfo;
	}

	/**
	 * -- Additionally Added
	 *
	 * @return com.addval.metadata.ColumnInfo
	 * @roseuid 3B0F5D910073
	 */
	public ColumnInfo getColumnInfo() {

		return _columnInfo;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B240274
	 */
	public String getCaption() {
		String[] captions = StringUtils.split(getColumnInfo().getCaption(), AVConstants._COLUMN_CAPTION_DELIMITER);
		return captions[0];
	}

	public String getSettingsLabel() {
        String[] captions = StringUtils.split(getColumnInfo().getCaption(), AVConstants._COLUMN_CAPTION_DELIMITER);
		String cap = (captions.length > 1 ? captions[1] : captions[0]);
		cap = cap.replaceAll("<br>", " ");
		return cap;
 }

	public String getCaptions() {
		return getColumnInfo().getCaption();
	}

	/**
	 * @return boolean
	 * @roseuid 3AEF5B24029D
	 */
	public boolean isCombo() {
		return getColumnInfo().isCombo();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B2402D9
	 */
	public String getComboOrderBy() {
		return getColumnInfo().getComboOrderBy();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B240301
	 */
	public String getComboSelect() {
		return getColumnInfo().getComboSelect();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B240329
	 */
	public String getComboTblName() {

		return getColumnInfo().getComboTblName();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B240351
	 */
	public String getErrorMsg() {

		return getColumnInfo().getErrorMsg();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B240379
	 */
	public String getFormat() {
		return getColumnInfo().getFormat();
	}

	/**
	 * @return double
	 * @roseuid 3AEF5B25003B
	 */
	public double getMaxval() {

		return getColumnInfo().getMaxval();
	}

	/**
	 * @return double
	 * @roseuid 3AEF5B25006D
	 */
	public double getMinval() {

		return getColumnInfo().getMinval();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B2500D1
	 */
	public String getRegexp() {

		return getColumnInfo().getRegexp();
	}

	/**
	 * @return int
	 * @roseuid 3AEF5B2500F9
	 */
	public int getType() {

		return getColumnInfo().getType();
	}

	/**
	 * @return String
	 * @roseuid 3D5187090163
	 */
	public String getTypeName() {

		return getColumnInfo().getTypeName();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEF5B25012B
	 */
	public String getUnit() {
		return getColumnInfo().getUnit();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C927E8C00D0
	 */
	public String getValue() {

		return getColumnInfo().getValue();
	}

	public String getTextAlign() {
		return getColumnInfo().getTextAlign();
	}

	public String getDisplayCSS() {
		return getColumnInfo().getDisplayCSS();
	}

	public String getDisplayStyle() {
		return getColumnInfo().getDisplayStyle();
	}

	public Integer getTextBoxSize() {
		return getColumnInfo().getTextBoxSize();
	}

	public Integer getTextBoxMaxlength() {
		return getColumnInfo().getTextBoxMaxlength();
	}

	public Integer getTextAreaRows() {
		return getColumnInfo().getTextAreaRows();
	}

	public Integer getTextAreaCols() {
		return getColumnInfo().getTextAreaCols();
	}
	
	public HashMap<String,String> getJSEvents() {
		return getColumnInfo().getJSEvents();
	}

	/**
	 * @return String
	 * @roseuid 3C89118E023D
	 */
	public String toXML() {

		final String SPACE = " ";
		StringBuffer xml = new StringBuffer();
		xml.append("<");
		xml.append("column" + SPACE);
		xml.append("name=" + "\"" + getName() + "\"" + SPACE);
		xml.append("aggregatable=" + "\"" + isAggregatable() + "\"" + SPACE);
		xml.append("displayable=" + "\"" + isDisplayable() + "\"" + SPACE);
		xml.append("searchable=" + "\"" + isSearchable() + "\"" + SPACE);
		xml.append("updatable=" + "\"" + isUpdatable() + "\"" + SPACE);
		xml.append("linkable=" + "\"" + isLinkable() + "\"" + SPACE);
		xml.append("expression=" + "\"" + getExpression() + "\"" + SPACE);
		xml.append("columnDecoratorProperty=" + "\"" + getColumnDecoratorProperty() + "\"" + SPACE);
		xml.append("columnBeanProperty=" + "\"" + getColumnBeanProperty() + "\"" + SPACE);
		xml.append("key=" + "\"" + isKey() + "\"" + SPACE);
		xml.append("type=" + "\"" + getType() + "\"" + SPACE);
		xml.append("caption=" + "\"" + getCaption() + "\"" + SPACE);
		xml.append("settingsLabel=" + "\"" + getSettingsLabel() + "\"" + SPACE);
		xml.append("format=" + "\"" + getFormat() + "\"" + SPACE);
		xml.append("unit=" + "\"" + getUnit() + "\"" + SPACE);
		xml.append("nullable=" + "\"" + isNullable() + "\"" + SPACE);
		xml.append("secured=" + "\"" + isSecured() + "\"" + SPACE);
		xml.append("mandatory=" + "\"" + isSearchableMandatory() + "\"" + SPACE);
		xml.append("combo=" + "\"" + isCombo() + "\"" + SPACE);
		xml.append("comboTblName=" + "\"" + getComboTblName() + "\"" + SPACE);
		xml.append("comboSelect=" + "\"" + getComboSelect() + "\"" + SPACE);
		xml.append("comboOrderBy=" + "\"" + getComboOrderBy() + "\"" + SPACE);
		xml.append("minval=" + "\"" + getMinval() + "\"" + SPACE);
		xml.append("maxval=" + "\"" + getMaxval() + "\"" + SPACE);
		xml.append("regexp=" + "\"" + getRegexp() + "\"" + SPACE);
		xml.append("errorMsg=" + "\"" + getErrorMsg() + "\"" + SPACE);
		xml.append("value=" + "\"" + getValue() + "\"" + SPACE);
		xml.append("textAlign=" + "\"" + getTextAlign() + "\"" + SPACE);
		xml.append("textBoxSize=" + "\"" + getTextBoxSize() + "\"" + SPACE);
		xml.append("textBoxMaxlength=" + "\"" + getTextBoxMaxlength() + "\"" + SPACE);
		xml.append("textAreaRows=" + "\"" + getTextAreaRows() + "\"" + SPACE);
		xml.append("textAreaCols=" + "\"" + getTextAreaCols() + "\"" + SPACE);
		xml.append("indexedUpperCaseData=" + "\"" + isIndexedUpperCaseData() + "\"" + SPACE);
		xml.append("/>");
		xml.append(System.getProperty("line.separator"));

		return xml.toString();
	}

	/**
	 * @return boolean
	 * @roseuid 3DE10AA20086
	 */
	public boolean isSecured() {
		return _secured;
	}

	/**
	 * @param secured
	 * @roseuid 3DE10AA200A4
	 */
	public void setSecured(boolean secured) {
		_secured = secured;
	}

	/**
	 * @return boolean
	 * @roseuid 3DE10AA20086
	 */
	public boolean isSearchableMandatory() {
		return _searchable && _searchableMandatory;
	}

	/**
	 * @param searchableMandatory
	 * @roseuid 3DE10AA200A4
	 */
	public void setSearchableMandatory(boolean searchableMandatory) {
		_searchableMandatory = searchableMandatory;
	}

	public String getSearchMandatoryGroup() {
		return _searchMandatoryGroup;
	}

	public void setSearchMandatoryGroup(String searchMandatoryGroup) {
		this._searchMandatoryGroup = searchMandatoryGroup;
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 3E52968302A7
	 */
	public Object clone() {

		ColumnMetaData newColumnMetaData = new ColumnMetaData(_aggregatable, _searchable, _displayable, _updatable, _linkable, _expression, _key, _nullable, _editKey, _editKeyDisplayable, _secured, _searchableMandatory);
		newColumnMetaData.setColumnInfo((ColumnInfo) _columnInfo.clone());
		newColumnMetaData.setSecured(_secured);
		newColumnMetaData.setSearchableMandatory(_searchableMandatory);
		newColumnMetaData.setComboCached(_comboCached);
		newColumnMetaData.setComboSelectTag(_comboSelectTag);
		newColumnMetaData.setColumnDecoratorProperty(_columnDecoratorProperty);
		newColumnMetaData.setColumnBeanProperty(_columnBeanProperty);
		newColumnMetaData.setEjbResultSet(_ejbResultSet);
		newColumnMetaData.setAutoSequenceName(getAutoSequenceName());
		newColumnMetaData.setOrdering(getOrdering());
		newColumnMetaData.setSortOrderSeq(getSortOrderSeq());
		newColumnMetaData.setSearchOrderSeq(getSearchOrderSeq());
		newColumnMetaData.setSearchGroup(_searchGroup);
		newColumnMetaData.setDisplayGroup(_displayGroup);
		newColumnMetaData.setUpdateGroup(_updateGroup);
		newColumnMetaData.setSearchMandatoryGroup(_searchMandatoryGroup);
		newColumnMetaData.setEditRegExp(_editRegExp);
		newColumnMetaData.setEditErrorMsg(_editErrorMsg);
		newColumnMetaData.setIndexedUpperCaseData(indexedUpperCaseData);
		
		return newColumnMetaData;
	}

	/**
	 * @return boolean
	 * @roseuid 3EF663A503A2
	 */
	public boolean isComboCached() {
		return _comboCached;
	}

	/**
	 * @param cached
	 * @roseuid 3EF663A6000A
	 */
	public void setComboCached(boolean cached) {
		_comboCached = cached;
	}

	/**
	 * @return boolean
	 * @roseuid 3EF663C300C0
	 */
	public boolean isComboSelectTag() {
		return _comboSelectTag;
	}

	/**
	 * @param select
	 * @roseuid 3EF663C300F2
	 */
	public void setComboSelectTag(boolean select) {
		_comboSelectTag = select;
	}

	/**
	 * @return java.lang.Object
	 * @roseuid 3EF66418039D
	 */
	public Object getEjbResultSet() {

		return _ejbResultSet;
	}

	/**
	 * @param resultset
	 * @roseuid 3EF6642C0388
	 */
	public void setEjbResultSet(Object resultset) {
		_ejbResultSet = resultset;
	}

	public int getFormatLength() {

		int maxLen = _DEFAULT_TEXT_SIZE; // default

		String format = _columnInfo.getFormat();
		if (format != null) {
			maxLen = format.length();
			int beginIndex = format.indexOf("{");
			if (beginIndex != -1) {
				// recompute size if format has {} syntax
				try {
					int endIndex = format.indexOf("}");
					if (beginIndex + 1 < endIndex && beginIndex > -1)
						maxLen = Integer.parseInt(format.substring(beginIndex + 1, endIndex));
				}
				catch (Exception e) {
					// format does not have the size in the right syntax.
					// Just use default size
				}
			}
		}
		return maxLen;
	}

	public boolean hasAutoSequenceName() {
		return getAutoSequenceName() != null;
	}

	public String getAutoSequenceName() {
		return _autoSequenceName;
	}

	public void setAutoSequenceName(String autoSequenceName) {
		this._autoSequenceName = autoSequenceName;
	}

	public int getOrdering() {
		return ordering;
	}

	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}

	public int getSortOrderSeq() {
		return sortOrderSeq;
	}

	public void setSortOrderSeq(int sortOrderSeq) {
		this.sortOrderSeq = sortOrderSeq;
	}

	public String getEditRegExp() {
		return _editRegExp;
	}

	public void setEditRegExp(String editRegExp) {
		this._editRegExp = editRegExp;
	}


	public String getEditErrorMsg() {
		return _editErrorMsg;
	}

	public void setEditErrorMsg(String editErrorMsg) {
		this._editErrorMsg = editErrorMsg;
	}

	public int getSearchOrderSeq() {
		return searchOrderSeq;
	}

	public void setSearchOrderSeq(int searchOrderSeq) {
		this.searchOrderSeq = searchOrderSeq;
	}

	public boolean isIndexedUpperCaseData() {
		return indexedUpperCaseData;
	}

	public void setIndexedUpperCaseData(boolean indexedUpperCaseData) {
		this.indexedUpperCaseData = indexedUpperCaseData;
	}
	
	
}
