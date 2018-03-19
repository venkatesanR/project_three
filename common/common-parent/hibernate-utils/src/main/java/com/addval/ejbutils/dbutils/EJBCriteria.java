//Source file: E:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBCriteria.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: D:\\users\\prasad\\Projects\\Common\\src\\client\\source\\com\\addval\\ejbutils\\dbutils\\EJBCriteria.java
/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.ejbutils.dbutils;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

/**
 * This class typically represents the search criteria object that can be used by the client to query a database table/view. This object requires the EditorMetaData object to be avaiable for its creation
 */
public final class EJBCriteria implements Serializable {
	private static final transient String _module = "EJBCriteria";
	private Hashtable _columnIndexes = null;
	private EJBCriteriaColumn[] _updates = null;
	private EJBCriteriaColumn[] _where = null;
	private EJBCriteriaColumn[] _groupBy = null;
	private EJBCriteriaColumn[] _orderBy = null;
	private String _editorName = null;
	private String _editorType = null;
	private int _pageAction = AVConstants._FETCH_FORWARD;
	private int _pageSize = -1;
	private int _currPosition = 0;
	private int _rowCount = 0;
	private Vector _columnsMetaData = null;
	private Vector _columns = null;
	private String queueCriteria = null;
	private HashMap sourceParams = null;
	private EditorMetaData editormetadata = null;
	private boolean distinct = false; // For autocomplete we can set the flag to true.

	/**
	 * @param editorName
	 * @param editorType
	 * @param columnsMetaData
	 * @roseuid 3AF9D4E803D1
	 */
	public EJBCriteria(String editorName, String editorType, Vector columnsMetaData) {

		_editorName = editorName;
		_editorType = editorType;

		if (columnsMetaData != null && columnsMetaData.size() != 0) {

			int index = 0;
			ColumnMetaData columnMetaData = null;
			EJBCriteriaColumn column = null;
			Iterator iterator = columnsMetaData.iterator();
			Vector indexes = null;

			// Initialize member variables
			_columns = new Vector();
			_columnIndexes = new Hashtable();
			_columnsMetaData = columnsMetaData;

			while (iterator.hasNext()) {

				columnMetaData = (ColumnMetaData) iterator.next();

				// Create new EJBCriteriaColumns
				// Duplicate EJBCriteriaColumns will be seperate objects and hence will
				// have their unique values but will point to the same ColumnMetaData
				column = new EJBCriteriaColumn(columnMetaData);
				_columns.add(column);
				// Maintain the indexes
				// If the same column is already present, then the intention is to
				// create a criteria like : COLUMN1 >= VALUE1 AND COLUMN1 <= VALUE2
				// Hence in such cases, to support multiple indexes per column, store
				// the indexes in an array

				if (_columnIndexes.containsKey(columnMetaData.getName())) {

					indexes = (Vector) _columnIndexes.get(columnMetaData.getName());
				}
				else {
					// Create a new Vector of initial capacity of 2. Typically it will
					// be a max of 2
					indexes = new Vector(2);
				}

				indexes.add(new Integer(index));
				_columnIndexes.put(columnMetaData.getName(), indexes);

				index++;
			}
		}

		// The editor type represents the group by
		// For example type = ||POL||POD = group by POL and POD columns
		if (editorType != null && !editorType.equals(AVConstants._DEFAULT))
			setGroupBy(editorType);
	}

	/**
	 * Access method for the _pageAction property.
	 * 
	 * @return the current value of the _pageAction property
	 */
	public int getPageAction() {

		return _pageAction;
	}

	/**
	 * Sets the value of the _pageAction property.
	 * 
	 * @param aPageAction
	 *            the new value of the _pageAction property
	 */
	public void setPageAction(int aPageAction) {
		_pageAction = aPageAction;
	}

	/**
	 * Access method for the _currPosition property.
	 * 
	 * @return the current value of the _currPosition property
	 */
	public int getCurrPosition() {

		return _currPosition;
	}

	/**
	 * Sets the value of the _currPosition property.
	 * 
	 * @param aCurrPosition
	 *            the new value of the _currPosition property
	 */
	public void setCurrPosition(int aCurrPosition) {
		_currPosition = aCurrPosition;
	}

	/**
	 * Access method for the _rowCount property.
	 * 
	 * @return the current value of the _rowCount property
	 */
	public int getRowCount() {
		return _rowCount;
	}

	/**
	 * Sets the value of the _rowCount property.
	 * 
	 * @param aRowCount
	 *            the new value of the _rowCount property
	 */
	public void setRowCount(int aRowCount) {
		_rowCount = aRowCount;
	}

	/**
	 * @param editorType
	 * @roseuid 3B194E4703D0
	 */
	private void setGroupBy(String editorType) {

		StringTokenizer tokenizer = new StringTokenizer(editorType, AVConstants._DELIMITER);
		Vector columns = new Vector();

		while (tokenizer.hasMoreTokens()) {

			columns.add(tokenizer.nextToken());
		}

		groupBy(columns);
	}

	/**
	 * @param names
	 * @roseuid 3AFAD14601F0
	 */
	private void groupBy(Vector names) {

		if (names != null && names.size() != 0) {

			Vector indexes = null;
			Integer index = null;
			int size = names.size();
			_groupBy = new EJBCriteriaColumn[size];

			for (int i = 0; i < size; i++) {

				indexes = (Vector) _columnIndexes.get((String) names.elementAt(i));

				if (indexes == null || indexes.isEmpty())
					throw new XRuntime(_module, "Column name does not exist in the criteria :" + (String) names.elementAt(i));

				// Since for groupby you need the column only once, getting the first element is enough
				index = (Integer) indexes.firstElement();

				_groupBy[i] = (EJBCriteriaColumn) _columns.elementAt(index.intValue());
			}
		}
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B1A684003B9
	 */
	public String getEditorName() {

		return _editorName;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3B1A68490005
	 */
	public String getEditorType() {

		return _editorType;
	}

	/**
	 * @param values
	 * @roseuid 3AFADCBC00D6
	 */
	public void where(Hashtable values) {

		where(values, null);
	}

	/**
	 * @param values
	 * @param operators
	 * @roseuid 3B211337016F
	 */
	public void where(Hashtable values, Hashtable operators) {

		// This method assumes that there is one 1 value for a column
		// i.e. COLUMN1 >= VALUE1 AND COLUMN1 <= VALUE2 cannot be done
		// with this method
		if (values != null && values.size() != 0) {

			Vector indexes = null;
			Integer index = null;
			Enumeration enumeration = values.keys();
			int i = 0;
			String name = null;
			String value = null;
			EJBCriteriaColumn column = null;

			if (_where != null && _where.length > 0) {

				int length = _where.length;
				EJBCriteriaColumn[] temp = new EJBCriteriaColumn[length + values.size()];

				System.arraycopy(_where, 0, temp, 0, length);
				_where = temp;
				i = length;

				/*

				EJBCriteriaColumn[] temp = new EJBCriteriaColumn[length];

				for (int whereIndex = 0; whereIndex < length; whereIndex++) {

					temp[i] = _where[whereIndex];
					i++;
				}

				i = 0;
				_where = new EJBCriteriaColumn[length + values.size()];

				for (int whereIndex = 0; whereIndex < length; whereIndex++) {

					_where[i] = temp[whereIndex];
					i++;
				}
				*/
			}
			else {

				// Create criteria columns for the where clause
				_where = new EJBCriteriaColumn[values.size()];
			}

			while (enumeration.hasMoreElements()) {

				name = (String) enumeration.nextElement();
				value = (String) values.get(name);
				indexes = (Vector) _columnIndexes.get(name);

				if (indexes == null || indexes.isEmpty())
					throw new XRuntime(_module, "Column name does not exist in the criteria :" + name);

				// Since for groupby you need the column only once, getting the first element is enough
				index = (Integer) indexes.firstElement();

				column = (EJBCriteriaColumn) _columns.elementAt(index.intValue());

				EJBConverterFactory.getConverter(column, (ColumnMetaData) _columnsMetaData.elementAt(index.intValue())).setValue(value);

				// Set the operator if it was specified
				if (operators != null && operators.get(name) != null)
					column.setOperator(Integer.parseInt((String) operators.get(name)));

				_where[i] = column;
				i++;
			}
		}
	}

	/**
	 * @param valuesAndOperators
	 * @roseuid 3E4176D00343
	 */
	public void where(Vector valuesAndOperators) {

		// This method assumes that there are more than 1 value for a column
		// i.e. COLUMN1 >= VALUE1 AND COLUMN1 <= VALUE2 can be done
		// with this method

		// The values come as a Vector with the elements as follows
		// Index 0 - Name
		// Index 1 - Value
		// Index 2 - Operator

		if (valuesAndOperators != null && !valuesAndOperators.isEmpty()) {

			final int NAME = 0;
			final int VALUE = 1;
			final int OPER = 2;
			int size = valuesAndOperators.size();
			Vector values = null;
			String name = null;
			String value = null;
			String oper = null;
			Vector indexes = null;
			Integer index = null;
			int colSize = 0;
			int i = 0;
			EJBCriteriaColumn column = null;

			// Create criteria columns for the where clause
			_where = new EJBCriteriaColumn[valuesAndOperators.size()];

			for (int valIndex = 0; valIndex < size; valIndex++) {

				values = (Vector) valuesAndOperators.elementAt(valIndex);
				name = (String) values.elementAt(NAME);
				value = (String) values.elementAt(VALUE);
				oper = (String) values.elementAt(OPER);

				indexes = (Vector) _columnIndexes.get(name);

				if (indexes == null || indexes.isEmpty())
					throw new XRuntime(_module, "Column name does not exist in the criteria :" + name);

				colSize = indexes.size();

				for (int colIndex = 0; colIndex < colSize; colIndex++) {

					index = (Integer) indexes.elementAt(colIndex);

					column = (EJBCriteriaColumn) _columns.elementAt(index.intValue());

					// If the column already has a value, then get the next column
					if (!column.isAvailable()) {

						EJBConverterFactory.getConverter(column, (ColumnMetaData) _columnsMetaData.elementAt(index.intValue())).setValue(value);

						// Set the operator if it was specified
						if (oper != null && oper.length() > 0)
							column.setOperator(Integer.parseInt(oper));

						_where[i] = column;
						i++;

						// go to the next column, this value has been set
						break;
					}
				}
			}
		}
		/*
				EJBCriteriaColumn column         = null;
				for (int index = 0; index < _columns.size(); index++) {

					column = (EJBCriteriaColumn)_columns.elementAt( index );
					System.out.println( column.toXML() );
				}
		*/
	}

	/**
	 * The orderby columns will be passed to this method as COLUMN_NAME||ORDER (e.g. AIRPORT_CD||DESC). This method will tokenize the elements of the vector to find out if the order is ASC or DESC
	 * 
	 * @param names
	 * @roseuid 3AFAD63D037E
	 */
	public void orderBy(Vector names) {

		if (names != null && names.size() != 0) {

			Vector indexes = null;
			Integer index = null;
			String columnName = null;
			String ordering = null;
			int size = names.size();
			EJBCriteriaColumn column = null;
			_orderBy = new EJBCriteriaColumn[size];

			for (int i = 0; i < size; i++) {

				columnName = (String) names.elementAt(i);
				ordering = null;

				// If the column is seperated by a delimiter the subsequent token provides the order (ASC, DESC)
				if (columnName.indexOf(AVConstants._DELIMITER) != -1) {
					StringTokenizer tokenizer = new StringTokenizer(columnName, AVConstants._DELIMITER);

					// Ensure that there are more tokens before reading them
					if (tokenizer.hasMoreTokens())
						columnName = tokenizer.nextToken();
					if (tokenizer.hasMoreTokens())
						ordering = tokenizer.nextToken();
				}

				indexes = (Vector) _columnIndexes.get(columnName);

				if (indexes == null || indexes.isEmpty())
					throw new XRuntime(_module, "Column name does not exist in the criteria :" + (String) names.elementAt(i));

				// Since for groupby you need the column only once, getting the first element is enough
				index = (Integer) indexes.firstElement();

				column = (EJBCriteriaColumn) _columns.elementAt(index.intValue());

				if (ordering != null)
					column.setOrdering(Integer.parseInt(ordering));

				_orderBy[i] = column;
			}
		}
	}

	/**
	 * @return EJBCriteriaColumn[]
	 * @roseuid 3AFAFDD3009B
	 */
	public EJBCriteriaColumn[] getWhere() {

		return _where;
	}

	public void setWhere(EJBCriteriaColumn[] where) {
		_where = where;
	}

	/**
	 * @return EJBCriteriaColumn[]
	 * @roseuid 3AFAFE03007C
	 */
	public EJBCriteriaColumn[] getGroupBy() {

		return _groupBy;
	}

	/**
	 * @return EJBCriteriaColumn[]
	 * @roseuid 3AFAFE030339
	 */
	public EJBCriteriaColumn[] getOrderBy() {

		return _orderBy;
	}

	/**
	 * @return EJBCriteriaColumn[]
	 * @roseuid 3AFAFE030339
	 */
	public void setOrderBy(EJBCriteriaColumn[] orderBy) {

		_orderBy = orderBy;
	}

	/**
	 * @return int
	 * @roseuid 3B83183C0295
	 */
	public int getPageSize() {

		return _pageSize;
	}

	/**
	 * @param value
	 * @roseuid 3B83185903D7
	 */
	public void setPageSize(int value) {

		_pageSize = value;
	}

	/**
	 * @roseuid 3E41A56903A0
	 */
	public void resetUnSecured() {

		// This will reset the values for all non-secured columns
		Vector indexes = null;
		Integer index = null;
		int colSize = 0;
		ColumnMetaData columnMetaData = null;
		EJBCriteriaColumn column = null;
		int size = _columnsMetaData == null ? 0 : _columnsMetaData.size();

		for (int valIndex = 0; valIndex < size; valIndex++) {

			columnMetaData = (ColumnMetaData) _columnsMetaData.elementAt(valIndex);

			if (!columnMetaData.isSecured()) {

				indexes = (Vector) _columnIndexes.get(columnMetaData.getName());

				if (indexes == null || indexes.isEmpty())
					throw new XRuntime(_module, "Column name does not exist in the criteria :" + columnMetaData.getName());

				colSize = indexes.size();

				for (int colIndex = 0; colIndex < colSize; colIndex++) {

					index = (Integer) indexes.elementAt(colIndex);
					column = (EJBCriteriaColumn) _columns.elementAt(index.intValue());
					column.reset();
				}
			}
		}
	}

	public String getQueueCriteria() {

		return queueCriteria;
	}

	public void setQueueCriteria(String queueCriteria) {

		this.queueCriteria = queueCriteria;
	}

	public Vector getColumns() {

		return _columns;
	}

	public Hashtable getColumnIndexes() {

		return _columnIndexes;
	}

	public boolean containsWhere(String columnName) {
		if (columnName == null) {
			return false;
		}
		EJBCriteriaColumn[] columns = getWhere();
		EJBCriteriaColumn column = null;
		int size = (columns == null) ? 0 : columns.length;
		for (int index = 0; index < size; index++) {
			column = columns[index];
			if (columnName.equalsIgnoreCase(column.getName())) {
				return true;
			}
		}
		return false;
	}

	public HashMap getSourceParams() {
		return sourceParams;
	}

	public void setSourceParams(HashMap sourceParams) {
		this.sourceParams = sourceParams;
	}

	public EditorMetaData getEditormetadata() {
		return editormetadata;
	}

	public void setEditormetadata(EditorMetaData editormetadata) {
		this.editormetadata = editormetadata;
	}

	public void update(Hashtable updates) {
		if (updates != null && updates.size() > 0) {
			EJBCriteriaColumn column = null;
			Vector indexes = null;
			Integer index = null;
			Iterator<String> columnNames = updates.keySet().iterator();
			String columnName = null;
			String columnValue = null;
			int i = 0;
			if (_updates != null && _updates.length > 0) {
				int length = _updates.length;
				EJBCriteriaColumn[] temp = new EJBCriteriaColumn[length + updates.size()];
				System.arraycopy(_updates, 0, temp, 0, length);
				_updates = temp;
				i = length;
			}
			else {
				_updates = new EJBCriteriaColumn[updates.size()];
			}

			while (columnNames.hasNext()) {
				columnName = columnNames.next();
				columnValue = (String) updates.get(columnName);
				if (!StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = columnValue.trim();
				}
				indexes = (Vector) _columnIndexes.get(columnName);
				if (indexes == null || indexes.isEmpty()) {
					throw new XRuntime(_module, "Column name does not exist in the criteria :" + columnName);
				}
				index = (Integer) indexes.firstElement();
				column = (EJBCriteriaColumn) _columns.elementAt(index.intValue());
				EJBConverterFactory.getConverter(column, (ColumnMetaData) _columnsMetaData.elementAt(index.intValue())).setValue(columnValue);
				column.setOperator(AVConstants._EQUAL);
				_updates[i] = column;
				i++;
			}
		}
	}

	public EJBCriteriaColumn[] getUpdate() {
		return _updates;
	}

	public void setUpdate(EJBCriteriaColumn[] updates) {
		_updates = updates;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

}
