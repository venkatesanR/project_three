package com.addval.aswtaglib;

import com.addval.metadata.EditorMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: Oct 13, 2004 Time: 4:49:54 PM To change this template use File | Settings | File Templates.
 */
public class CustomizeColumnTag extends ColumnTag {
	private static final long serialVersionUID = 9201920091153048328L;
	private ArrayList _usedColumns = null;
	private ArrayList _usedColumnLabels = null;
	private ArrayList _columnExpressions = null;
	private String _columnOperations;
	private EditorMetaData _editorMetadata = null;

	/**
	 * @param md
	 * @throws javax.servlet.jsp.JspTagException
	 * @throws javax.servlet.jsp.JspTagException
	 * @roseuid 3B4D41100271
	 */
	public void setMetadata_id(String md) throws JspTagException {
		Object md_obj = pageContext.getAttribute(md);
		HttpSession session = (HttpSession) pageContext.getSession();
		if (md_obj == null) {
			md_obj = session.getAttribute(md);
		}
		if (md_obj == null || !(md_obj instanceof EditorMetaData)) {
			throw new IllegalArgumentException("CustomizeColumnTag: Invalid Object id. " + md + ". It should be an EditorMetaData id");
		}
		_editorMetadata = (EditorMetaData) md_obj;
	}

	public String getAlign(String name) {
		if (_editorMetadata == null)
			return getAlign();
		int type = getColumnType(name);
		switch (type) {
		case ColumnDataType._CDT_STRING:
		case ColumnDataType._CDT_CLOB:
		case ColumnDataType._CDT_BLOB:
		case ColumnDataType._CDT_CHAR:
		case ColumnDataType._CDT_USER:
		case ColumnDataType._CDT_CREATED_USER:
		case ColumnDataType._CDT_DATE:
		case ColumnDataType._CDT_DATETIME:
		case ColumnDataType._CDT_CREATED_TIMESTAMP:
		case ColumnDataType._CDT_TIMESTAMP:
		case ColumnDataType._CDT_TIME:
		case ColumnDataType._CDT_BOOLEAN:
			return "left";
		case ColumnDataType._CDT_LONG:
		case ColumnDataType._CDT_VERSION:
		case ColumnDataType._CDT_INT:
		case ColumnDataType._CDT_DOUBLE:
		case ColumnDataType._CDT_FLOAT:
		case ColumnDataType._CDT_KEY:
			return "right";
		default:
			return getAlign();
		}
	}

	private int getColumnType(String decoratorName) {
		int size = _editorMetadata.getColumnCount();
		for (int i = 0; i < size; i++) {
			ColumnMetaData columnMetaData = _editorMetadata.getColumnMetaData(i + 1);
			if (!columnMetaData.getColumnDecoratorProperty().equals(decoratorName))
				continue;
			if (columnMetaData == null)
				return -1;
			return columnMetaData.getType();
		}
		return -1;
	}

	public ArrayList getUsedColumns() {
		return _usedColumns;
	}

	public void setUsedColumns(ArrayList usedColumns) {
		this._usedColumns = usedColumns;
	}

	public ArrayList getUsedColumnLabels() {
		return _usedColumnLabels;
	}

	public void setUsedColumnLabels(ArrayList usedColumnLabels) {
		this._usedColumnLabels = usedColumnLabels;
	}

	public ArrayList getColumnExpressions() {
		return _columnExpressions;
	}

	public void setColumnExpressions(ArrayList columnExpressions) {
		this._columnExpressions = columnExpressions;
	}

	public String getColumnOperations() {
		return _columnOperations;
	}

	public void setColumnOperations(String aColumnOperations) {
		this._columnOperations = aColumnOperations;
	}

	public int doEndTag() throws JspException {
		Object parent = this.getParent();
		if (parent == null) {
			throw new JspException("Can not use column tag outside of a " + "TableTag. Invalid parent = null");
		}
		if (!(parent instanceof TableTag)) {
			throw new JspException("Can not use column tag outside of a " + "TableTag. Invalid parent = " + parent.getClass().getName());
		}
		CustomizeColumnTag copy = this;
		try {
			copy = (CustomizeColumnTag) this.clone();
		}
		catch (CloneNotSupportedException e) {
		}
		((TableTag) parent).addCustomizeColumn(copy);
		((TableTag) parent).setEditorMetadata(_editorMetadata);
		return super.doEndTag();
	}
}
