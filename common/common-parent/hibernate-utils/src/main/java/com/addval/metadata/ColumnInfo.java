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

package com.addval.metadata;

import java.io.Serializable;
import java.util.HashMap;

import com.addval.utils.XRuntime;

public class ColumnInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = -5572240987307723328L;
	private static final transient String _module = "ColumnInfo";
	private String _name = null;
	private int _type = ColumnDataType._CDT_NOTYPE;
	private String _caption = null;
	private String _format = null;
	private String _unit = null;
	private boolean _combo = false;
	private String _comboTblName = null;
	private String _comboSelect = null;
	private String _comboOrderBy = null;
	private double _minval = 0.0;
	private double _maxval = 0.0;
	private String _regexp = null;
	private String _errorMsg = null;
	private String _value = null;
	private String _textAlign = null; // left/right/center
	private String _displayCSS = null;
	private String _displayStyle = null;
	private Integer _textBoxSize = null;
	private Integer _textBoxMaxlength = null;
	private Integer _textAreaRows = null;
	private Integer _textAreaCols = null;
	private HashMap<String,String> _jsEvents = null;

	/**
	 * ColumnInfo(String,String,String,String,String,boolean,String,String,String,doubl e,double,String,String,String)
	 *
	 * @param name
	 * @param type
	 * @param caption
	 * @param format
	 * @param unit
	 * @param combo
	 * @param comboTblName
	 * @param comboSelect
	 * @param comboOrderBy
	 * @param minval
	 * @param maxval
	 * @param regexp
	 * @param errorMsg
	 * @param value
	 * @roseuid 3B0F57B80035
	 */
	public ColumnInfo(String name, String type, String caption, String format, String unit, boolean combo, String comboTblName, String comboSelect, String comboOrderBy, double minval, double maxval, String regexp, String errorMsg, String value) {

		this(name, type, caption);

		_format = format;
		_unit = unit;
		_combo = combo;
		_comboTblName = comboTblName;
		_comboSelect = comboSelect;
		_comboOrderBy = comboOrderBy;
		_minval = minval;
		_maxval = maxval;
		_regexp = regexp;
		_errorMsg = errorMsg;
		_value = value;
	}

	/**
	 * @param name
	 * @param type
	 * @param caption
	 * @roseuid 3AF091E50280
	 */
	public ColumnInfo(String name, String type, String caption) {
		_name = name;
		_caption = caption;

		setType(type);
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE410321
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE41037B
	 */
	public void setName(String v) {

		_name = v;
	}

	/**
	 * @return int
	 * @roseuid 3AEDBE4200A2
	 */
	public int getType() {

		return _type;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3D51871F02F5
	 */
	public String getTypeName() {

		String type = null;

		switch (_type) {

		case ColumnDataType._CDT_NOTYPE:
			type = ColumnDataType._CDT_NOTYPE_;
			break;

		case ColumnDataType._CDT_STRING:
			type = ColumnDataType._CDT_STRING_;
			break;

		case ColumnDataType._CDT_INT:
			type = ColumnDataType._CDT_INT_;
			break;

		case ColumnDataType._CDT_LONG:
			type = ColumnDataType._CDT_LONG_;
			break;

		case ColumnDataType._CDT_DOUBLE:
			type = ColumnDataType._CDT_DOUBLE_;
			break;

		case ColumnDataType._CDT_FLOAT:
			type = ColumnDataType._CDT_FLOAT_;
			break;

		case ColumnDataType._CDT_DATE:
			type = ColumnDataType._CDT_DATE_;
			break;

		case ColumnDataType._CDT_DATETIME:
			type = ColumnDataType._CDT_DATETIME_;
			break;

		case ColumnDataType._CDT_TIMESTAMP:
			type = ColumnDataType._CDT_TIMESTAMP_;
			break;

		case ColumnDataType._CDT_TIME:
			type = ColumnDataType._CDT_TIME_;
			break;

		case ColumnDataType._CDT_CHAR:
			type = ColumnDataType._CDT_CHAR_;
			break;

		case ColumnDataType._CDT_BOOLEAN:
			type = ColumnDataType._CDT_BOOLEAN_;
			break;

		case ColumnDataType._CDT_KEY:
			type = ColumnDataType._CDT_KEY_;
			break;

		case ColumnDataType._CDT_LINK:
			type = ColumnDataType._CDT_LINK_;
			break;

		case ColumnDataType._CDT_VERSION:
			type = ColumnDataType._CDT_VERSION_;
			break;

		case ColumnDataType._CDT_USER:
			type = ColumnDataType._CDT_USER_;
			break;

		case ColumnDataType._CDT_CREATED_USER:
			type = ColumnDataType._CDT_CREATED_USER_;
			break;

		case ColumnDataType._CDT_FILE:
			type = ColumnDataType._CDT_FILE_;
			break;

		case ColumnDataType._CDT_CLOB:
			type = ColumnDataType._CDT_CLOB_;
			break;

		case ColumnDataType._CDT_BLOB:
			type = ColumnDataType._CDT_BLOB_;
			break;

		case ColumnDataType._CDT_CREATED_TIMESTAMP:
			type = ColumnDataType._CDT_CREATED_TIMESTAMP_;
			break;

		default:
			throw new XRuntime(_module, "Column Type is not recognized :" + _type);
		}

		return type;
	}

	/**
	 * @param v
	 * @roseuid 3D507A0A027B
	 */
	public void setType(int v) {

		_type = v;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE4200FC
	 */
	public void setType(String v) {

		if (v.equals(ColumnDataType._CDT_NOTYPE_)) {

			_type = ColumnDataType._CDT_NOTYPE;
			return;
		}

		if (v.equals(ColumnDataType._CDT_STRING_)) {

			_type = ColumnDataType._CDT_STRING;
			return;
		}

		if (v.equals(ColumnDataType._CDT_INT_)) {

			_type = ColumnDataType._CDT_INT;
			return;
		}

		if (v.equals(ColumnDataType._CDT_LONG_)) {

			_type = ColumnDataType._CDT_LONG;
			return;
		}

		if (v.equals(ColumnDataType._CDT_DOUBLE_)) {

			_type = ColumnDataType._CDT_DOUBLE;
			return;
		}

		if (v.equals(ColumnDataType._CDT_FLOAT_)) {

			_type = ColumnDataType._CDT_FLOAT;
			return;
		}

		if (v.equals(ColumnDataType._CDT_DATE_)) {

			_type = ColumnDataType._CDT_DATE;
			return;
		}

		if (v.equals(ColumnDataType._CDT_DATETIME_)) {

			_type = ColumnDataType._CDT_DATETIME;
			return;
		}

		if (v.equals(ColumnDataType._CDT_TIMESTAMP_)) {

			_type = ColumnDataType._CDT_TIMESTAMP;
			return;
		}

		if (v.equals(ColumnDataType._CDT_TIME_)) {

			_type = ColumnDataType._CDT_TIME;
			return;
		}

		if (v.equals(ColumnDataType._CDT_CHAR_)) {

			_type = ColumnDataType._CDT_CHAR;
			return;
		}

		if (v.equals(ColumnDataType._CDT_BOOLEAN_)) {

			_type = ColumnDataType._CDT_BOOLEAN;
			return;
		}

		if (v.equals(ColumnDataType._CDT_KEY_)) {

			_type = ColumnDataType._CDT_KEY;
			return;
		}

		if (v.equals(ColumnDataType._CDT_LINK_)) {

			_type = ColumnDataType._CDT_LINK;
			return;
		}

		if (v.equals(ColumnDataType._CDT_VERSION_)) {

			_type = ColumnDataType._CDT_VERSION;
			return;
		}

		if (v.equals(ColumnDataType._CDT_USER_)) {

			_type = ColumnDataType._CDT_USER;
			return;
		}
		if (v.equals(ColumnDataType._CDT_CREATED_USER_)) {

			_type = ColumnDataType._CDT_CREATED_USER;
			return;
		}
		if (v.equals(ColumnDataType._CDT_ITERATOR_)) {

			_type = ColumnDataType._CDT_ITERATOR;
			return;
		}
		if (v.equals(ColumnDataType._CDT_OBJECT_)) {

			_type = ColumnDataType._CDT_OBJECT;
			return;
		}
		if (v.equals(ColumnDataType._CDT_FILE_)) {
			_type = ColumnDataType._CDT_FILE;
			return;
		}
		if (v.equals(ColumnDataType._CDT_CLOB_)) {
			_type = ColumnDataType._CDT_CLOB;
			return;
		}
		if (v.equals(ColumnDataType._CDT_BLOB_)) {
			_type = ColumnDataType._CDT_BLOB;
			return;
		}
		if (v.equals(ColumnDataType._CDT_CREATED_TIMESTAMP_)) {
			_type = ColumnDataType._CDT_CREATED_TIMESTAMP;
			return;
		}

		// If type is not set at this point - throw an exception indicating wrong type
		throw new XRuntime(_module, "Column Type is not recognized :" + v);
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE4201F6
	 */
	public String getCaption() {

		return _caption;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE420264
	 */
	public void setCaption(String v) {

		_caption = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE420373
	 */
	public String getFormat() {

		return _format;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE4203CD
	 */
	public void setFormat(String v) {

		_format = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE4300FD
	 */
	public String getUnit() {

		return _unit;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE430157
	 */
	public void setUnit(String v) {

		_unit = v;
	}

	/**
	 * @return boolean
	 * @roseuid 3AEDBE430266
	 */
	public boolean isCombo() {

		return _combo;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE4302DE
	 */
	public void setCombo(boolean v) {

		_combo = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE440018
	 */
	public String getComboTblName() {

		return _comboTblName;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE44007C
	 */
	public void setComboTblName(String v) {

		_comboTblName = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE44019F
	 */
	public String getComboSelect() {

		return _comboSelect;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE44020D
	 */
	public void setComboSelect(String v) {

		_comboSelect = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE44032F
	 */
	public String getComboOrderBy() {

		return _comboOrderBy;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE44039E
	 */
	public void setComboOrderBy(String v) {

		_comboOrderBy = v;
	}

	/**
	 * @return double
	 * @roseuid 3AEDBE4500E2
	 */
	public double getMinval() {

		return _minval;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE450146
	 */
	public void setMinval(double v) {

		_minval = v;
	}

	/**
	 * @return double
	 * @roseuid 3AEDBE45027D
	 */
	public double getMaxval() {

		return _maxval;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE4502F5
	 */
	public void setMaxval(double v) {

		_maxval = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE460075
	 */
	public String getRegexp() {

		return _regexp;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE4600ED
	 */
	public void setRegexp(String v) {

		_regexp = v;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3AEDBE46022E
	 */
	public String getErrorMsg() {
		return _errorMsg;
	}

	/**
	 * @param v
	 * @roseuid 3AEDBE4602BA
	 */
	public void setErrorMsg(String v) {
		_errorMsg = v;
	}

	/**
	 * @param value
	 * @roseuid 3C927EE50060
	 */
	public void setValue(String value) {
		_value = value;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C927EDB012E
	 */
	public String getValue() {
		return _value;
	}

	public String getTextAlign() {
		return _textAlign;
	}

	public void setTextAlign(String textAlign) {
		this._textAlign = textAlign;
	}

	public String getDisplayCSS() {
		return _displayCSS;
	}

	public void setDisplayCSS(String displayCSS) {
		this._displayCSS = displayCSS;
	}

	public String getDisplayStyle() {
		return _displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		this._displayStyle = displayStyle;
	}

	public Integer getTextBoxSize() {
		return _textBoxSize;
	}

	public void setTextBoxSize(Integer textBoxSize) {
		this._textBoxSize = textBoxSize;
	}

	public Integer getTextBoxMaxlength() {
		return _textBoxMaxlength;
	}

	public void setTextBoxMaxlength(Integer textBoxMaxlength) {
		this._textBoxMaxlength = textBoxMaxlength;
	}

	public Integer getTextAreaRows() {
		return _textAreaRows;
	}

	public void setTextAreaRows(Integer textAreaRows) {
		this._textAreaRows = textAreaRows;
	}

	public Integer getTextAreaCols() {
		return _textAreaCols;
	}

	public void setTextAreaCols(Integer textAreaCols) {
		this._textAreaCols = textAreaCols;
	}

	public HashMap<String,String> getJSEvents() {
		return _jsEvents;
	}

	public void setJSEvents(HashMap<String,String> jsEvents) {
		this._jsEvents = jsEvents;
	}

	public String toXML() {
		final String SPACE = " ";
		StringBuffer xml = new StringBuffer();

		xml.append("<");
		xml.append("column" + SPACE);
		xml.append("name=" + "\"" + getName() + "\"" + SPACE);
		xml.append("type=" + "\"" + getType() + "\"" + SPACE);
		xml.append("caption=" + "\"" + getCaption() + "\"" + SPACE);
		xml.append("format=" + "\"" + getFormat() + "\"" + SPACE);
		xml.append("unit=" + "\"" + getUnit() + "\"" + SPACE);
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
		xml.append("displayCSS=" + "\"" + getDisplayCSS() + "\"" + SPACE);
		xml.append("displayStyle=" + "\"" + getDisplayStyle() + "\"" + SPACE);
		xml.append("textBoxSize=" + "\"" + getTextBoxSize() + "\"" + SPACE);
		xml.append("textBoxMaxlength=" + "\"" + getTextBoxMaxlength() + "\"" + SPACE);
		xml.append("textAreaRows=" + "\"" + getTextAreaRows() + "\"" + SPACE);
		xml.append("textAreaCols=" + "\"" + getTextAreaCols() + "\"" + SPACE);
		xml.append("/>");
		xml.append(System.getProperty("line.separator"));

		return xml.toString();
	}

	/**
	 * @return Object
	 * @roseuid 3E5296970011
	 */
	public Object clone() {

		ColumnInfo newColumnInfo = new ColumnInfo(_name, getTypeName(), _caption, _format, _unit, _combo, _comboTblName, _comboSelect, _comboOrderBy, _minval, _maxval, _regexp, _errorMsg, _value);
		newColumnInfo.setTextAlign(_textAlign);
		newColumnInfo.setDisplayCSS(_displayCSS);
		newColumnInfo.setDisplayStyle(_displayStyle);
		newColumnInfo.setTextBoxSize(_textBoxSize);
		newColumnInfo.setTextBoxMaxlength(_textBoxMaxlength);
		newColumnInfo.setTextAreaRows(_textAreaRows);
		newColumnInfo.setTextAreaCols(_textAreaCols);
		newColumnInfo.setJSEvents(_jsEvents);

		return newColumnInfo;
	}
}
