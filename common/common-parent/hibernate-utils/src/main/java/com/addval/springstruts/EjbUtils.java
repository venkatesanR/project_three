//Source file: D:\\Projects\\Common\\src\\client\\source\\com\\addval\\struts\\EjbUtils.java

//Source file: d:\\satya\\projects\\common\\src\\client\\source\\com\\addval\\struts\\EjbUtils.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.springstruts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;

/**
 * Utility class to collect information from a HTTP request (parameters or attributes) and populate EJBResultset, EJBCriteria etc. The class relies on some naming conventions followed by the AddVal tag libraries for the request parameters. parameter names that end with _lookUp are search criteria
 * that needs to be used in where clauses parameter names that end with operator_lookUp can be used to specify >, <, =, <> as operators in where clauses parameter names that end with _KEY are used as primary keys to lookup for editing records parameter called SORT_NAME or sort_Name is used to
 * specify the order by column parameter called SORT_ORDER or sort_Order is used to specify the ascending (ASC) or descending (DESC) order Other columns that are present in the metadata
 */
public class EjbUtils {

	public static EJBCriteria getEJBUpdateCriteria(EditorMetaData editorMetaData, Vector<UpdateColumn> updateColumns, HttpServletRequest request) {
		EJBCriteria ejbCriteria = (new EJBResultSetMetaData(editorMetaData)).getNewCriteria();

		// Where Clause
		boolean isMultipleRowsSelected = request.getParameter(editorMetaData.getName() + "_DELETE") != null;
		Hashtable where = new Hashtable();
		Hashtable operations = new Hashtable();
		if (isMultipleRowsSelected) {
			String[] multipleRows = request.getParameterValues(editorMetaData.getName() + "_DELETE");

			HashMap<String, String> keyValuePair = new HashMap<String, String>();
			String fldName = null;
			String fldValue = null;
			String namevaluepair[] = null;
			String namevalue[] = null;

			for (int i = 0; i < multipleRows.length; i++) {
				namevaluepair = StringUtils.split(multipleRows[i], "&");
				for (int j = 0; j < namevaluepair.length; j++) {
					namevalue = StringUtils.split(namevaluepair[j], "=");
					fldName = namevalue[0];
					fldValue = (namevalue.length > 1) ? namevalue[1] : null;
					if (!StrUtl.isEmptyTrimmed(fldValue)) {
						if (keyValuePair.containsKey(fldName)) {
							fldValue = keyValuePair.get(fldName) + "," + fldValue;
							keyValuePair.put(fldName, fldValue);
						}
						else {
							keyValuePair.put(fldName, fldValue);
						}
					}
				}
				int size = editorMetaData.getColumnCount();
				ColumnMetaData columnMetaData = null;
				for (int index = 1; index <= size; index++) {
					columnMetaData = editorMetaData.getColumnMetaData(index);
					if (keyValuePair.containsKey(columnMetaData.getName() + "_KEY")) {
						fldValue = getCommaSeparatedValues(keyValuePair.get(columnMetaData.getName() + "_KEY"));
						if (columnMetaData.getType() == ColumnDataType._CDT_INT || columnMetaData.getType() == ColumnDataType._CDT_DOUBLE || columnMetaData.getType() == ColumnDataType._CDT_LONG || columnMetaData.getType() == ColumnDataType._CDT_KEY) {
							fldValue = fldValue.replaceAll("'", "");
						}
						where.put(columnMetaData.getName(), fldValue);
						operations.put(columnMetaData.getName(), Integer.toString(AVConstants._IN));
					}
				}
			}
		}
		else {
			where = buildLookUpWhere(request, editorMetaData);
			operations = getOperator(request, editorMetaData);
		}
		ejbCriteria.where(where, operations);

		// Update Columns
		Hashtable update = new Hashtable();
		String columnValue = null;
		for (UpdateColumn updateColumn : updateColumns) {
			if (!StrUtl.isEmptyTrimmed(updateColumn.getColumnName())) {
				columnValue = updateColumn.getColumnValue();
				if (StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = updateColumn.getColumnOption(); // dropdown value.
				}
				if (StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = ""; // We can't update NULL value. Set empty value. Server side we handle it as NULL value
				}
				update.put(updateColumn.getColumnName(), columnValue);
			}
		}
		ejbCriteria.update(update);
		return ejbCriteria;
	}

	public static EJBCriteria getEJBCriteria(EditorMetaData editormetadata, EditorMetaData customEditormetadata, HttpServletRequest request, boolean forList) {
		EJBCriteria ejbCriteria = (new EJBResultSetMetaData(editormetadata)).getNewCriteria();
		ejbCriteria.setEditormetadata(editormetadata);
		if (forList) {
			ejbCriteria.where(buildLookUpWhere(request, editormetadata), getOperator(request, editormetadata));
			ejbCriteria.orderBy(setOrderBy(customEditormetadata, request));
		}
		else {
			ejbCriteria.where(buildEditWhere(request, editormetadata), getOperator(request, editormetadata));
		}
		String queueCriteria = (String) request.getAttribute("QUEUE_CRITERIA");
		if (queueCriteria != null && queueCriteria.length() != 0)
			ejbCriteria.setQueueCriteria(queueCriteria);
		return ejbCriteria;

	}

	public static EJBCriteria getEJBCriteria(EditorMetaData editormetadata, HttpServletRequest request, boolean forList) {
		return getEJBCriteria(editormetadata, editormetadata, request, forList);
	}

	public static void setJSONSaveFilter(HttpServletRequest request, EditorMetaData metadata, Vector<SearchColumn> advancedSearchColumns, String jsonString) {
		if (!StrUtl.isEmptyTrimmed(jsonString)) {
			if (advancedSearchColumns != null) {
				advancedSearchColumns.clear();
			}

			JSONObject savedFilter = JSONObject.fromObject(jsonString);
			JSONObject operatorsJson = JSONObject.fromObject(savedFilter.get("operators"));
			ColumnMetaData columnMetaData = null;
			SearchColumn searchColumn = null;

			if (savedFilter.containsKey("where")) {
				JSONObject whereJson = JSONObject.fromObject(savedFilter.get("where"));
				Map<String, String> where = (Map<String, String>) JSONObject.toBean(whereJson, HashMap.class);
				if (where != null) {
					for (String columnName : where.keySet()) {
						columnMetaData = metadata.getColumnMetaData(columnName);
						if (columnMetaData != null && columnMetaData.isAdvancedMultiRowSearch()) {
							searchColumn = new SearchColumn();
							searchColumn.setColumnName(columnName);
							if (operatorsJson.get(columnName) != null) {
								searchColumn.setColumnOperator((String) operatorsJson.get(columnName));
							}
							if (columnMetaData.isCombo() && columnMetaData.getEjbResultSet() != null) {
								searchColumn.setColumnOption(where.get(columnName));
							}
							else {
								searchColumn.setColumnValue(where.get(columnName));
							}
							advancedSearchColumns.add(searchColumn);
						}
						else {
							request.setAttribute(columnName + "_lookUp", where.get(columnName));
						}
					}
				}
			}
			if (savedFilter.containsKey("operators")) {
				Map<String, String> operators = (Map) JSONObject.toBean(operatorsJson, HashMap.class);
				if (operators != null) {
					for (String columnName : operators.keySet()) {
						columnMetaData = metadata.getColumnMetaData(columnName);
						if (columnMetaData == null || !columnMetaData.isAdvancedMultiRowSearch()) {
							request.setAttribute(columnName + "operator_lookUp", operators.get(columnName));
						}
					}
				}
			}
		}
	}

	public static String getJSONSaveFilter(HttpServletRequest request, EditorMetaData metadata, Vector<SearchColumn> advancedSearchColumns) {
		JSONObject where = new JSONObject();
		JSONObject operators = new JSONObject();

		String columnName = null;
		String columnValue = null;
		String actColumnName = null;
		ColumnMetaData columnMetaData = null;
		boolean isExactSearch = (request.getAttribute("exactSearch") != null) ? ((Boolean) request.getAttribute("exactSearch")).booleanValue() : true;

		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.endsWith("_lookUp") && !columnName.endsWith("operator_lookUp")) {
				columnValue = request.getParameter(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_lookUp"));
				columnMetaData = metadata.getColumnMetaData(actColumnName);
				if ("null".equalsIgnoreCase(columnValue) || columnMetaData == null) {
					continue;
				}
				else if (StrUtl.isEmptyTrimmed(columnValue)) {
					columnValue = ""; // columnValue may null
				}

				if (columnMetaData.getType() == ColumnDataType._CDT_STRING) {
					if (isExactSearch) {
						where.put(actColumnName, columnValue.trim());
					}
					else {
						where.put(actColumnName, columnValue.trim() + "%");
					}
				}
				else {
					where.put(actColumnName, columnValue.trim());
				}
			}
			else if (columnName.endsWith("operator_lookUp")) {
				columnValue = request.getParameter(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("operator_lookUp"));
				columnMetaData = metadata.getColumnMetaData(actColumnName);
				if (!StrUtl.isEmptyTrimmed(columnValue) && columnMetaData != null) {
					operators.put(actColumnName, columnValue.trim());
				}
			}
			else if (!isExactSearch && (columnName.endsWith("_lookUp") || columnName.endsWith("_look") || columnName.endsWith("_edit") || columnName.endsWith("_search") && !columnName.endsWith("operator_lookUp"))) {
				columnValue = getValueFromRequest(request, columnName);
				if (columnName.endsWith("_edit")) {
					actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_edit"));
				}
				if (columnName.endsWith("_lookUp")) {
					actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_lookUp"));
				}
				if (columnName.endsWith("_look")) {
					actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_look"));
				}
				if (columnName.endsWith("_search")) {
					actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_search"));
				}

				columnMetaData = metadata.getColumnMetaData(actColumnName);
				if (StrUtl.isEmptyTrimmed(columnValue) || columnMetaData == null) {
					continue;
				}
				if (columnMetaData.getType() == ColumnDataType._CDT_STRING) {
					operators.put(actColumnName, Integer.toString(AVConstants._LIKE));
				}
			}
		}
		if (advancedSearchColumns != null) {
			for (SearchColumn searchColumn : advancedSearchColumns) {
				if (!StrUtl.isEmptyTrimmed(searchColumn.getColumnName())) {
					columnValue = searchColumn.getColumnValue();
					if (StrUtl.isEmptyTrimmed(columnValue)) {
						columnValue = searchColumn.getColumnOption(); // dropdown value.
					}
					if (StrUtl.isEmptyTrimmed(columnValue)) {
						columnValue = "";
					}
					where.put(searchColumn.getColumnName(), columnValue);
					operators.put(searchColumn.getColumnName(), searchColumn.getColumnOperator());
				}
			}
		}
		JSONObject saveFilter = new JSONObject();
		saveFilter.put("where", JSONObject.fromObject(where));
		saveFilter.put("operators", JSONObject.fromObject(operators));
		return saveFilter.toString();
	}

	/**
	 * @param request
	 * @param metadata
	 * @return java.util.Hashtable
	 * @roseuid 3DCC7998035C
	 */
	public static Hashtable buildLookUpWhere(HttpServletRequest request, EditorMetaData metadata) {
		Hashtable where = new Hashtable();
		boolean isExactSearch = true;
		if (request.getAttribute("exactSearch") != null) {
			isExactSearch = ((Boolean) request.getAttribute("exactSearch")).booleanValue();
		}
		ColumnMetaData columnMetaData = null;
		String columnName = null;
		String columnValue = null;
		String actColumnName = null;
		String operator = null;
		boolean isLookupForm = request.getAttribute("isLookupForm") != null ? (Boolean) request.getAttribute("isLookupForm") : false;
		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName != null && columnName.length() != 0) {
				if (!isLookupForm && columnName.endsWith("_edit")) {
					continue;
				}
				if ((columnName.endsWith("_lookUp") || columnName.endsWith("_edit")) && !columnName.endsWith("operator_lookUp")) {
					columnValue = request.getParameter(columnName).trim();
					if (columnName.endsWith("_lookUp")) {
						actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_lookUp"));
					}
					else if (columnName.endsWith("_edit")) {
						actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_edit"));
					}
					if (columnValue == null || columnValue.trim().length() == 0 || columnValue.equalsIgnoreCase("null") || (metadata.getColumnMetaData(actColumnName) == null)) {
						continue;
					}
					columnMetaData = metadata.getColumnMetaData(actColumnName);
					if (columnMetaData != null) {
						if (columnMetaData.getType() == ColumnDataType._CDT_STRING) {
							operator = (String) request.getParameter(actColumnName + "operator_lookUp");
							if (isExactSearch) {
								where.put(actColumnName, columnValue.trim());
							}
							else if (operator != null && AVConstants._NOT_EQUAL == Integer.parseInt(operator)) {
								where.put(actColumnName, columnValue.trim());
							}
							else if (operator != null && AVConstants._EQUAL == Integer.parseInt(operator)) {
								where.put(actColumnName, columnValue.trim());
							}
							else if (operator != null && AVConstants._ENDS == Integer.parseInt(operator)) {
								if (columnValue.trim().startsWith("%")) {
									where.put(actColumnName, columnValue.trim());
								}
								else {
									where.put(actColumnName, "%" + columnValue.trim());
								}
							}
							else if (operator != null && AVConstants._CONTAINS == Integer.parseInt(operator) && !columnValue.trim().startsWith("%")) {
								where.put(actColumnName, "%" + columnValue.trim() + "%");
							}
							else if (operator != null && AVConstants._IN == Integer.parseInt(operator)) {
								where.put(actColumnName, getCommaSeparatedValues(columnValue.trim()));
							}
							else {
								where.put(actColumnName, columnValue.trim() + "%");
							}
						}
						else {
							where.put(actColumnName, columnValue.trim());
						}
					}
				}
			}
		}
		for (Enumeration enumeration = request.getAttributeNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.endsWith("_lookUp") && !columnName.endsWith("operator_lookUp")) {
				columnValue = (String) request.getAttribute(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_lookUp"));
				if (columnValue != null && columnValue.trim().length() != 0 && (metadata.getColumnMetaData(actColumnName) != null)) {
					columnMetaData = metadata.getColumnMetaData(actColumnName);
					if (columnMetaData.getType() == ColumnDataType._CDT_STRING) {
						operator = (String) request.getAttribute(actColumnName + "operator_lookUp");
						if (isExactSearch) {
							where.put(actColumnName, columnValue.trim());
						}
						else if (operator != null && AVConstants._NOT_EQUAL == Integer.parseInt(operator)) {
							where.put(actColumnName, columnValue.trim());
						}
						else if (operator != null && AVConstants._EQUAL == Integer.parseInt(operator)) {
							where.put(actColumnName, columnValue.trim());
						}
						else if (operator != null && AVConstants._ENDS == Integer.parseInt(operator)) {
							if (columnValue.trim().startsWith("%")) {
								where.put(actColumnName, columnValue.trim());
							}
							else {
								where.put(actColumnName, "%" + columnValue.trim());
							}
						}
						else if (operator != null && AVConstants._CONTAINS == Integer.parseInt(operator) && !columnValue.trim().startsWith("%")) {
							where.put(actColumnName, "%" + columnValue.trim() + "%");
						}
						else {
							where.put(actColumnName, columnValue.trim() + "%");
						}
					}
					else {
						where.put(actColumnName, columnValue.trim());
					}
				}
			}
			else if (columnName.endsWith("_preview")) {
				columnValue = (String) request.getAttribute(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_preview"));
				if (columnValue != null && columnValue.trim().length() != 0 && (metadata.getColumnMetaData(actColumnName) != null)) {
					where.put(actColumnName, columnValue.trim());
				}
			}
		}
		// Now add any secure columns (that are not searchable) to the lookup
		// Searchable columns would already be added by the above code
		Vector columnsMetaData = new Vector();
		Iterator iterator = metadata.getColumnsMetaData().iterator();

		// System.out.println ( "Now checking for secure columns to add to lookup" );
		Hashtable secureValues = new Hashtable();
		while (iterator.hasNext()) {
			columnMetaData = (ColumnMetaData) iterator.next();
			if (columnMetaData.isSecured() && !columnMetaData.isSearchable()) {
				// find the value for this secure column
				columnValue = (String) request.getAttribute(columnMetaData.getName() + "_lookUp");
				// System.out.println ( "  Found a secure column to add to lookup" + columnMetaData.getName() + ":value:" + columnValue );
				if (columnValue != null && columnValue.trim().length() != 0) {
					where.put(columnMetaData.getName(), columnValue.trim());
				}
			}
			if (!where.containsKey(columnMetaData.getName()) && columnMetaData.getType() == ColumnDataType._CDT_BOOLEAN) {
				String YN[] = columnMetaData.getValue().split(":");
				if (YN.length > 1) {
					where.put(columnMetaData.getName(), YN[1].trim()); // Set Option2 if not checked.
				}
			}
		}
		return where;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return java.util.Hashtable
	 * @roseuid 3DCC799900E7
	 */
	public static Hashtable buildEditWhere(HttpServletRequest request, EditorMetaData metadata) {
		// SATYA commented it out because of undesirable side effects when looking up
		// a result set for editing. For editing the lookup should strictly be based on
		// keys

		// Hashtable where = buildLookUpWhere( request, metadata );

		Hashtable where = new Hashtable();
		String columnName = null;
		String columnValue = null;
		String actColumnName = null;

		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {

			columnName = (String) enumeration.nextElement();
			if (columnName.endsWith("_KEY")) {
				columnValue = request.getParameter(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.lastIndexOf("_KEY"));
				if (columnValue != null && columnValue.length() > 0 && (metadata.getColumnMetaData(actColumnName) != null) && metadata.getColumnMetaData(actColumnName).getType() != ColumnDataType._CDT_TIMESTAMP)
					where.put(actColumnName, columnValue.trim());
			}
		}

		for (Enumeration enumeration = request.getAttributeNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.endsWith("_KEY")) {
				columnValue = (String) request.getAttribute(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.lastIndexOf("_KEY"));
				if (columnValue != null && columnValue.length() > 0 && (metadata.getColumnMetaData(actColumnName) != null) && metadata.getColumnMetaData(actColumnName).getType() != ColumnDataType._CDT_TIMESTAMP)
					where.put(actColumnName, columnValue.trim());
			}
		}
		return where;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return java.util.Hashtable
	 * @roseuid 3DCC79990200
	 */
	public static Hashtable getOperator(HttpServletRequest request, EditorMetaData metadata) {
		Hashtable operations = new Hashtable();
		boolean isExactSearch = true;
		if (request.getAttribute("exactSearch") != null) {
			isExactSearch = ((Boolean) request.getAttribute("exactSearch")).booleanValue();
		}

		String columnName = null;
		String columnValue = null;
		String actColumnName = null;
		ColumnMetaData columnMetaData = null;

		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName != null && columnName.length() != 0)
				if (columnName.endsWith("operator_lookUp")) {
					columnValue = request.getParameter(columnName);
					actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("operator_lookUp"));
					if (columnValue != null && columnValue.length() != 0 && (metadata.getColumnMetaData(actColumnName) != null)) {
						operations.put(actColumnName, columnValue.trim());
					}
				}
				else if (!isExactSearch && (columnName.endsWith("_lookUp") || columnName.endsWith("_look") || columnName.endsWith("_edit") || columnName.endsWith("_search") && !columnName.endsWith("operator_lookUp"))) {
					columnValue = getValueFromRequest(request, columnName);
					if (columnName.endsWith("_edit")) {
						actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_edit"));
					}
					if (columnName.endsWith("_lookUp")) {
						actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_lookUp"));
					}
					if (columnName.endsWith("_look")) {
						actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_look"));
					}
					if (columnName.endsWith("_search")) {
						actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("_search"));
					}

					if (columnValue == null || columnValue.trim().length() == 0 || (metadata.getColumnMetaData(actColumnName) == null)) {
						continue;
					}
					columnMetaData = metadata.getColumnMetaData(actColumnName);
					if (columnMetaData == null || columnMetaData.getType() != ColumnDataType._CDT_STRING) {
						continue;
					}
					columnValue = request.getParameter(actColumnName + "operator_lookUp");
					if (StrUtl.isEmptyTrimmed(columnValue)) {
						operations.put(actColumnName, Integer.toString(AVConstants._LIKE));
					}
				}
		}
		for (Enumeration enumeration = request.getAttributeNames(); enumeration.hasMoreElements();) {
			columnName = (String) enumeration.nextElement();
			if (columnName.endsWith("operator_lookUp")) {
				columnValue = (String) request.getAttribute(columnName);
				actColumnName = columnName.toUpperCase().substring(0, columnName.indexOf("operator_lookUp"));
				if (!StrUtl.isEmptyTrimmed(columnValue) && (metadata.getColumnMetaData(actColumnName) != null)) {
					operations.put(actColumnName, columnValue.trim());
				}
			}
		}
		return operations;
	}

	/**
	 * @param metadata
	 * @param request
	 * @return java.util.Vector
	 * @roseuid 3DCC799902F9
	 */
	public static Vector setOrderBy(EditorMetaData metadata, HttpServletRequest request) {
		String sortName = request.getParameter("SORT_NAME") != null ? request.getParameter("SORT_NAME") : "";
		if (sortName.equals("")) {
			sortName = request.getParameter("sort_Name") != null ? request.getParameter("sort_Name") : "";
		}
		if (sortName.equals("")) {
			sortName = (String) request.getAttribute("SORT_NAME") != null ? (String) request.getAttribute("SORT_NAME") : "";
		}

		String sortOrder = request.getParameter("SORT_ORDER") != null ? request.getParameter("SORT_ORDER") : "";
		if (sortOrder.equals("")) {
			sortOrder = request.getParameter("sort_Order") != null ? request.getParameter("sort_Order") : "";
		}
		if (sortOrder.equals("")) {
			sortOrder = (String) request.getAttribute("SORT_ORDER") != null ? (String) request.getAttribute("SORT_ORDER") : "";
		}
		if (sortOrder.equals("")) {
			sortOrder = "ASC";
		}
		return setOrderBy(metadata, sortName, sortOrder);
	}

	/**
	 * @param metadata
	 * @param request
	 * @return java.util.Vector
	 * @roseuid 3DCC799902F9
	 */
	public static Vector setOrderBy(EditorMetaData metadata, String sortName, String sortOrder) {
		Vector orderby = new Vector();
		int intSortOrder = sortOrder.equalsIgnoreCase("ASC") ? AVConstants._ASC : AVConstants._DESC;
		if (metadata.isMultiSorting()) {
			ColumnMetaData columnMetaDatas[] = getColumsBySortOrderSeq(metadata.getColumnsMetaData());
			ColumnMetaData columnMetaData = null;
			for (int i = 0; i < columnMetaDatas.length; i++) {
				columnMetaData = columnMetaDatas[i];
				if (columnMetaData.getType() != ColumnDataType._CDT_LINK && columnMetaData.getType() != ColumnDataType._CDT_CLOB && columnMetaData.getType() != ColumnDataType._CDT_FILE && columnMetaData.getType() != ColumnDataType._CDT_BLOB) {
					if (sortName.equals(columnMetaData.getName())) {
						orderby.add(0, columnMetaData.getName() + AVConstants._DELIMITER + intSortOrder);
					}
					else {
						orderby.add(columnMetaData.getName() + AVConstants._DELIMITER + columnMetaData.getOrdering());
					}
				}
			}
		}
		else {
			ColumnMetaData columnMetaData = null;
			// Default sort by first displayable column.
			if (StrUtl.isEmptyTrimmed(sortName)) {
				columnMetaData = ((ColumnMetaData) metadata.getDisplayableColumns().get(0));
			}
			else {
				columnMetaData = metadata.getColumnMetaData(sortName);
			}
			if (columnMetaData != null) {
				orderby.add(columnMetaData.getName() + AVConstants._DELIMITER + intSortOrder);
			}
		}
		return orderby;
	}

	public static ColumnMetaData[] getColumsBySortOrderSeq(Vector<ColumnMetaData> displayableColumns) {
		List<ColumnMetaData> columns = new ArrayList<ColumnMetaData>();
		columns.addAll(displayableColumns);
		Collections.sort(columns, new Comparator<ColumnMetaData>() {
			public int compare(ColumnMetaData arg1, ColumnMetaData arg2) {
				return Integer.valueOf(arg1.getDisplayGroup()).compareTo(Integer.valueOf(arg2.getDisplayGroup()));
			}
		});

		List<ColumnMetaData> sortableColumns = new ArrayList<ColumnMetaData>();
		for (ColumnMetaData column : columns) {
			if (Integer.parseInt(column.getDisplayGroup()) > 0) {
				sortableColumns.add(column);
			}
		}
		for (ColumnMetaData column : columns) {
			if (Integer.parseInt(column.getDisplayGroup()) == 0) {
				sortableColumns.add(column);
			}
		}
		Collections.sort(sortableColumns, new Comparator<ColumnMetaData>() {
			public int compare(ColumnMetaData arg1, ColumnMetaData arg2) {
				if (arg1.getSortOrderSeq() > -1 && arg2.getSortOrderSeq() > -1) {
					return Integer.valueOf(arg1.getSortOrderSeq()).compareTo(Integer.valueOf(arg2.getSortOrderSeq()));
				}
				return -1;
			}
		});
		return (ColumnMetaData[]) sortableColumns.toArray(new ColumnMetaData[0]);
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC799A0071
	 */
	public static EJBResultSet getDeleteEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request) {
		EJBResultSetMetaData ejbResultSetMetaData = new EJBResultSetMetaData(editorMetaData);
		EJBCriteria ejbCriteria = ejbResultSetMetaData.getNewCriteria();
		EJBResultSet ejbResultSet = new EJBResultSet(ejbResultSetMetaData, ejbCriteria);
		String keyValue = null;
		int size = editorMetaData.getColumnCount();
		ColumnMetaData columnMetaData = null;
		boolean keysExist = false;

		boolean isMultipleDelete = request.getParameter(editorMetaData.getName() + "_DELETE") != null;

		if (isMultipleDelete) {

			String[] deleteRows = request.getParameterValues(editorMetaData.getName() + "_DELETE");
			String deleteQueryString = null;
			HashMap keyValuePair = null;
			String fldName = null;
			String fldValue = null;

			for (int i = 0; i < deleteRows.length; i++) {

				deleteQueryString = deleteRows[i];

				keyValuePair = new HashMap();
				String namevaluepair[] = StringUtils.split(deleteQueryString, "&");

				for (int j = 0; j < namevaluepair.length; j++) {

					String namevalue[] = StringUtils.split(namevaluepair[j], "=");
					fldName = namevalue[0];
					fldValue = (namevalue.length > 1) ? namevalue[1] : null;
					if (fldValue != null && fldValue.length() > 0) {
						keyValuePair.put(fldName, fldValue);
					}

				}

				ejbResultSet.deleteRow();
				keysExist = false;

				for (int index = 1; index <= size; index++) {

					columnMetaData = editorMetaData.getColumnMetaData(index);

					if (keyValuePair.containsKey(columnMetaData.getName() + "_KEY")) {

						ejbResultSet.updateString(index, (String) keyValuePair.get(columnMetaData.getName() + "_KEY"));
						keysExist = true;
					}
				}

				if (keyValuePair.isEmpty() || !keysExist) {
					throw new com.addval.utils.XRuntime("Utils.getDeleteEJBResultSet", "No keys exist for deleting a record");
				}

			}
		}
		else {
			// This is a delete row
			ejbResultSet.deleteRow();
			for (int index = 1; index <= size; index++) {
				columnMetaData = editorMetaData.getColumnMetaData(index);
				keyValue = request.getParameter(columnMetaData.getName() + "_KEY");

				if (keyValue != null && keyValue.length() > 0) {
					ejbResultSet.updateString(index, keyValue);
					keysExist = true;
				}
			}
			if (size == 0 || !keysExist)
				throw new com.addval.utils.XRuntime("Utils.getDeleteEJBResultSet", "No keys exist for deleting a record");

		}

		return ejbResultSet;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @param fromList
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC799A017E
	 */
	public static EJBResultSet getInsertEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request, boolean fromList) {
		EJBCriteria ejbCriteria = getEJBCriteria(editorMetaData, request, fromList);
		EJBResultSet ejbResultSet = new EJBResultSet(new EJBResultSetMetaData(editorMetaData), ejbCriteria);
		// System.out.println ( "after new insertEJBResultSet()" );
		ejbResultSet.insertRow();
		ColumnMetaData columnMetaData = null;
		String value = null;
		for (int index = 1; index <= editorMetaData.getColumnCount(); index++) {
			columnMetaData = editorMetaData.getColumnMetaData(index);
			value = request.getParameter(columnMetaData.getName() + "_edit");
			if (value != null) {
				if (value.length() > 0 || editorMetaData.getColumnMetaData(index).isUpdatable()) {
					ejbResultSet.updateString(index, value);
				}
			}
			else if (columnMetaData.getType() == ColumnDataType._CDT_BOOLEAN) { // Checkbox Field: if it is unchecked browser won't pass the field to the server
				String YN[] = columnMetaData.getValue().split(":");
				value = (YN.length > 1) ? YN[1] : value;
				ejbResultSet.updateString(index, value);
			}
			value = request.getParameter(editorMetaData.getColumnMetaData(index).getName() + "_PARENT_KEY");
			if (value != null && value.length() > 0) {
				// System.out.println ( "key value = "+keyValue );
				ejbResultSet.updateString(index, value);
			}
		}
		return ejbResultSet;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @param fromList
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DCC799A0297
	 */
	public static EJBResultSet getNewEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request, boolean fromList) {

		EJBCriteria ejbCriteria = getEJBCriteria(editorMetaData, request, fromList);
		EJBResultSet ejbResultSet = new EJBResultSet(new EJBResultSetMetaData(editorMetaData), ejbCriteria);

		// System.out.println ( "after new EJBResultSet()" );

		ejbResultSet.updateRow();
		ColumnMetaData columnMetaData = null;
		String value = null;
		for (int index = 1; index <= editorMetaData.getColumnCount(); index++) {
			columnMetaData = editorMetaData.getColumnMetaData(index);
			value = request.getParameter(columnMetaData.getName() + "_edit");
			if (value != null) {
				if (value.length() > 0 || editorMetaData.getColumnMetaData(index).isUpdatable()) {
					ejbResultSet.updateString(index, value);
				}
			}
			else if (columnMetaData.getType() == ColumnDataType._CDT_BOOLEAN) { // Checkbox Field: if it is unchecked browser won't pass the field to the server
				String YN[] = columnMetaData.getValue().split(":");
				value = (YN.length > 1) ? YN[1] : value;
				ejbResultSet.updateString(index, value);
			}
			value = request.getParameter(editorMetaData.getColumnMetaData(index).getName() + "_KEY");
			if (!StrUtl.isEmptyTrimmed(value)) {
				ejbResultSet.updateString(index, value);
			}
		}
		return ejbResultSet;
	}

	/**
	 * @param
	 * @param request
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3DD3D53000ED
	 */
	private static String getValueFromRequest(HttpServletRequest request, String fieldName) {
		String fieldValue = null;
		if (request.getParameter(fieldName) != null) {
			fieldValue = request.getParameter(fieldName).trim();
		}
		if ((fieldValue == null || fieldValue.length() == 0) && request.getAttribute(fieldName) != null) {
			fieldValue = ((String) request.getAttribute(fieldName)).trim();
		}
		return fieldValue;
	}

	public static EJBCriteria getEJBCriteriaForLookup(EditorMetaData editormetadata, EditorMetaData customEditormetadata, HttpServletRequest request) {
		Hashtable where = new Hashtable();
		Hashtable operations = new Hashtable();
		boolean isExactMatchLookup = (request.getParameter("exactMatchLookup") != null);

		String fieldName = null;
		String fieldValue = null;
		String dbFieldName = null;
		ColumnMetaData columnMetaData = null;
		for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			fieldName = (String) enumeration.nextElement();
			if (fieldName == null && fieldName.length() == 0)
				continue;

			if (fieldName.endsWith("_lookUp") || fieldName.endsWith("_look") || fieldName.endsWith("_edit") || fieldName.endsWith("_search") && !fieldName.endsWith("operator_lookUp")) {
				fieldValue = getValueFromRequest(request, fieldName);
				if (fieldName.endsWith("_edit"))
					dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_edit"));
				if (fieldName.endsWith("_lookUp"))
					dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_lookUp"));
				if (fieldName.endsWith("_look"))
					dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_look"));
				if (fieldName.endsWith("_search"))
					dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_search"));

				columnMetaData = editormetadata.getColumnMetaData(dbFieldName);

				if (fieldValue == null || fieldValue.length() == 0 || (columnMetaData == null))
					continue;

				if (isExactMatchLookup) {
					where.put(dbFieldName, fieldValue);
				}
				else {
					if (columnMetaData.getType() == ColumnDataType._CDT_STRING) {
						where.put(dbFieldName, fieldValue + "%");
						operations.put(dbFieldName, Integer.toString(AVConstants._LIKE));
					}
					else {
						where.put(dbFieldName, fieldValue);
					}
				}
			}
		}
		EJBCriteria ejbCriteria = (new EJBResultSetMetaData(editormetadata)).getNewCriteria();
		ejbCriteria.setEditormetadata(editormetadata);
		ejbCriteria.where(where, operations);
		ejbCriteria.orderBy(setOrderBy(customEditormetadata, request));
		return ejbCriteria;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DDD983801A0
	 */
	public static EJBResultSet getDetailEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request) {
		EJBCriteria ejbCriteria = getEJBCriteria(editorMetaData, request, false);
		EJBResultSet ejbResultSet = new EJBResultSet(new EJBResultSetMetaData(editorMetaData), ejbCriteria);
		String[] detailArray = null;
		for (int index = 1; index <= editorMetaData.getColumnCount(); index++) {
			// System.out.println ( "index = "+editorMetaData.getColumnMetaData(index).getName() );
			detailArray = request.getParameterValues(editorMetaData.getColumnMetaData(index).getName() + "_array");
			if (detailArray != null) {
				break;
			}
		}
		if (detailArray != null) {
			ColumnMetaData columnMetaData = null;
			String value = null;
			String[] valueArray = null;
			for (int rec = 0; rec < detailArray.length; rec++) {
				ejbResultSet.insertRow();
				for (int index = 1; index <= editorMetaData.getColumnCount(); index++) {
					columnMetaData = editorMetaData.getColumnMetaData(index);
					value = request.getParameter(columnMetaData.getName() + "_KEY");
					// System.out.println ( "value = "+value );
					if (value != null && value.length() > 0) {
						ejbResultSet.updateString(index, value);
					}
					value = request.getParameter(columnMetaData.getName() + "_edit");
					// System.out.println ( "value = "+value );
					if (value != null && value.length() > 0) {
						ejbResultSet.updateString(index, value);
					}
					valueArray = request.getParameterValues(columnMetaData.getName() + "_array");
					if (valueArray != null && rec < valueArray.length) {
						ejbResultSet.updateString(index, valueArray[rec]);
					}
				}
			}
		}
		return ejbResultSet;
	}

	/**
	 * @param editorMetaData
	 * @param request
	 * @param parentMetaData
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3DDDA98603E4
	 */
	public static EJBResultSet getDetailDeleteEJBResultSet(EditorMetaData editorMetaData, HttpServletRequest request, EditorMetaData parentMetaData) {
		EJBResultSetMetaData ejbResultSetMetaData = new EJBResultSetMetaData(editorMetaData);
		EJBCriteria ejbCriteria = ejbResultSetMetaData.getNewCriteria();
		EJBResultSet ejbResultSet = new EJBResultSet(ejbResultSetMetaData, ejbCriteria);
		String keyValue = null;
		int size = editorMetaData.getColumnCount();
		ColumnMetaData columnMetaData = null;
		ColumnMetaData parentColumnMetaData = null;
		boolean keysExist = false;

		// This is a delete row
		ejbResultSet.deleteRow();

		for (int index = 1; index <= parentMetaData.getColumnCount(); index++) {

			parentColumnMetaData = parentMetaData.getColumnMetaData(index);

			if (parentColumnMetaData.isKey() || parentColumnMetaData.isEditKey()) {

				for (int index2 = 1; index2 <= size; index2++) {

					columnMetaData = editorMetaData.getColumnMetaData(index2);

					if (columnMetaData.getName().equals(parentColumnMetaData.getName())) {
						keyValue = request.getParameter(columnMetaData.getName() + "_KEY");

						if (keyValue != null && keyValue.length() > 0) {

							ejbResultSet.updateString(index2, keyValue);
							keysExist = true;
						}

						keyValue = request.getParameter(columnMetaData.getName() + "_edit");

						if (keyValue != null && keyValue.length() > 0) {

							ejbResultSet.updateString(index2, keyValue);
							keysExist = true;
						}

					}
				}

			}
		}

		return ejbResultSet;
	}

	/**
	 * @param ejbmetadata
	 * @param request
	 * @param forList
	 * @return com.addval.ejbutils.dbutils.EJBCriteria
	 * @roseuid 3E5FE714025B
	 */
	public static EJBCriteria getEJBCriteriaForEJBResultSetMetaData(EJBResultSetMetaData ejbmetadata, HttpServletRequest request, boolean forList) {

		EditorMetaData editormetadata = ejbmetadata.getEditorMetaData();

		EJBCriteria ejbCriteria = ejbmetadata.getNewCriteria();

		if (forList) {
			ejbCriteria.where(buildLookUpWhere(request, editormetadata), getOperator(request, editormetadata));
			ejbCriteria.orderBy(setOrderBy(editormetadata, request));
		}
		else {
			ejbCriteria.where(buildEditWhere(request, editormetadata), getOperator(request, editormetadata));
		}

		// System.out.println("After GetEJBCriteriaForEJBResultSet ");
		return ejbCriteria;
	}

	/**
	 * @param request
	 * @param metadata
	 * @return boolean
	 * @roseuid 3EF5BFFC032E
	 */
	public static boolean isLookupFilterPresent(HttpServletRequest request, EditorMetaData metadata) {
		boolean filterPresent = false;

		String fieldName = null;
		String fieldValue = null;
		String dbFieldName = null;
		fieldName = request.getParameter("displayFieldName");

		if (fieldName != null && (fieldName.endsWith("_lookUp") || fieldName.endsWith("_look") || fieldName.endsWith("_edit") || fieldName.endsWith("_search") && !fieldName.endsWith("operator_lookUp"))) {

			fieldValue = request.getParameter("displayFieldValue");
			filterPresent = (fieldValue != null && fieldValue.length() > 0);

		}
		else {

			for (Enumeration enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
				fieldName = (String) enumeration.nextElement();

				if (fieldName == null && fieldName.length() == 0)
					continue;

				if (fieldName.endsWith("_lookUp") || fieldName.endsWith("_look") || fieldName.endsWith("_edit") || fieldName.endsWith("_search") && !fieldName.endsWith("operator_lookUp")) {
					fieldValue = getValueFromRequest(request, fieldName);
					if (fieldName.endsWith("_edit"))
						dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_edit"));
					if (fieldName.endsWith("_lookUp"))
						dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_lookUp"));
					if (fieldName.endsWith("_look"))
						dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_look"));
					if (fieldName.endsWith("_search"))
						dbFieldName = fieldName.toUpperCase().substring(0, fieldName.indexOf("_search"));
					if (fieldValue == null || fieldValue.length() == 0 || (metadata.getColumnMetaData(dbFieldName) == null))
						continue;

					filterPresent = true;
					break;
				}
			}
		}
		return filterPresent;
	}

	public static String getCommaSeparatedValues(String colValue) {
		if (colValue.indexOf("%") > 0) {
			colValue = colValue.replaceAll("%", "");
		}
		StringBuilder retStr = new StringBuilder();
		String[] valueArr = colValue.split(",");
		for (String val : valueArr) {
			retStr.append(",");
			retStr.append("'");
			retStr.append(val.trim());
			retStr.append("'");
		}
		return retStr.substring(1);
	}
}
