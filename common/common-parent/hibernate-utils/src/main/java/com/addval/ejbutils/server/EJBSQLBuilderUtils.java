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

package com.addval.ejbutils.server;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.EJBException;

import org.apache.commons.lang.StringUtils;

import com.addval.dbutils.TableManager;
import com.addval.ejbutils.dbutils.EJBColumn;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBCriteriaColumn;
import com.addval.ejbutils.dbutils.EJBDateTime;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.utils.AVConstants;
import com.addval.utils.XRuntime;

/**
 * This class is used by EJBSTableManagerBean for build SQL statements based on the EJBCriteria object. It is also used by WQSMetaDataBean to build where clauses and that is why some methods are public.
 *
 * @author AddVal Technology Inc.
 */
public final class EJBSQLBuilderUtils {
	// private static final String _module = "com.addval.ejbutils.server.EJBSQLBuilderUtils";
	private String _serverType = null;
	private static final String _DATEFORMAT = "MM/DD/YYYY";
	private static final String _DATETIMEFORMAT = "MM/DD/YYYY HH24:MI:SS";
	private static final String _TO_DATE = "TO_DATE";
	private static final String _FORMAT = "FORMAT";
	private static final String _SELECT = "SELECT";
	private static final String _WHERE = "WHERE";
	private static final String _HAVING = "HAVING";
	private static final String _DISTINCT = "DISTINCT";
	private static final char _SPACE = ' ';
	private static final char _COMMA = ',';
	private static final char _SLASH = '/';
	private static final char _QUOTE = '\'';
	private static final char _OPEN_BRACKET = '(';
	private static final char _CLOSE_BRACKET = ')';
	private static final String _INSERT = "INSERT";
	private static final String _DELETE = "DELETE";
	private static final String _UPDATE = "UPDATE";
	private static final String _DEFAULT = "DEFAULT";
	private static final String _INTO = "INTO";
	private static final String _FROM = "FROM";
	private static final String _NULL = "NULL";
	private static final String _IS_NULL = "IS NULL";
	private static final char _COLON = ':';
	private static final String _ORDERBY = "ORDER BY";
	private static final String _GROUPBY = "GROUP BY";
	private static final char _EQUAL = '=';
	private static final String _VALUES = "VALUES";
	private static final String _AND = "AND";
	private static final String _AS = "AS";
	private static final String _SET = "SET";
	private static final String _ASC = "ASC";
	private static final String _DESC = "DESC";
	private static final char _SEMICOLON = ';';
	private static final String _BEGIN = "BEGIN";
	private static final String _END = "END";
	private static final char _QUESTION_MARK = '?';
	private static final String _ASSIGN = ":=";
	private static final String _CONVERT = "CONVERT(DATETIME,";

	/**
	 * This class is used by EJBSTableManagerBean for build SQL statements based on the EJBCriteria object. It is also used by WQSMetaDataBean to build where clauses and that is why some methods are public.
	 *
	 * @param serverType
	 * @roseuid 3E4DA8C401D9
	 */
	public EJBSQLBuilderUtils(String serverType) {

		_serverType = serverType;
	}

	public String buildLookupSql(EJBResultSetMetaData metaData, EJBCriteria criteria, List<EJBSQLParam> params) {

		String editorType = metaData.getEditorMetaData().getType();
		boolean group = (editorType == null || editorType.equals(_DEFAULT)) ? false : true;
		StringBuffer sql = new StringBuffer();
		sql.append(_SELECT + _SPACE);
		if (criteria.isDistinct()) { // FOR AUTOCOMPLETE add DISTINCT
			sql.append(_DISTINCT + _SPACE);
		}
		sql.append(buildSelect(metaData) + _SPACE);
		sql.append(buildFrom(criteria, metaData) + _SPACE);
		sql.append(buildWhere(criteria, metaData, params) + _SPACE);

		// Build GroupBy only for NON DEFAULT Editor types
		if (group) {

			sql.append(buildGroupBy(criteria) + _SPACE);
			sql.append(buildHaving(criteria, metaData) + _SPACE);
		}

		sql.append(buildOrderBy(criteria));

		return sql.toString();
	}

	public String buildLookupSql(EJBResultSetMetaData metaData, EJBCriteria criteria) {
		return buildLookupSql(metaData, criteria, null);
	}

	protected String buildInsertSql(EJBResultSet rs, String user, Connection conn, List<EJBSQLParam> params) throws SQLException {
		// Insert will always be by SQL and never by stored proc. If some functionality
		// is required, a ROW LEVEL INSERT trigger should be written
		EJBResultSetMetaData metaData = rs.getEJBResultSetMetaData();
		StringBuffer sql = null;
		StringBuffer insert = new StringBuffer();
		StringBuffer values = new StringBuffer();
		EJBColumn column = null;
		ColumnMetaData columnMetaData = null;
		int size = metaData.getColumnCount();
		int columnDataType = 0;
		String columnName = null;
		HashMap<String, String> columnAlias = rs.getEJBResultSetMetaData().getEditorMetaData().getColumnAlias();
		for (int index = 1; index <= size; index++) {

			columnMetaData = metaData.getColumnMetaData(index);
			column = rs.getRecord().getColumn(index);
			columnDataType = columnMetaData.getType();
			columnName = (columnAlias != null && columnAlias.containsKey(columnMetaData.getName())) ? columnAlias.get(columnMetaData.getName()) : columnMetaData.getName();

			if ((columnDataType == ColumnDataType._CDT_CLOB || columnDataType == ColumnDataType._CDT_BLOB || columnDataType == ColumnDataType._CDT_FILE) && columnMetaData.isUpdatable()) {
				if (columnDataType == ColumnDataType._CDT_FILE && column.getObject() == null) {
					continue;
				}
				if (columnDataType == ColumnDataType._CDT_CLOB && column.getStrValue() == null) {
					continue;
				}
				if (columnDataType == ColumnDataType._CDT_BLOB && column.getStrValue() == null) {
					continue;
				}
				insert.append(_COMMA).append(columnName);
				values.append(_COMMA).append(buildValue(columnMetaData.getType(), column, params));
				continue;
			}
			// If the column is a key that has an AUTO_INCREMENT property then the
			// column should be named COLUMN_KEY or COLUMN_ID and it should have the
			// column_key in the Editor_Type_Columns table set to TRUE.
			// For AUTO_INCREMENT columns, the column will not be generated in the sql
			// Alternately if the data for the column was not provided, then do not use
			// the column in the insert sql
			// TODO: it is a temporary fix and need to investigated in details - Jeyaraj - start
			if (columnDataType == ColumnDataType._CDT_USER)
				columnMetaData.setUpdatable(false);
			// TODO: it is a temporary fix and need to investigated in details - Jeyaraj - end
			if (columnDataType != ColumnDataType._CDT_LINK && !metaData.isAutoIncrement(index) && column.isAvailable() && columnMetaData.isUpdatable()) {

				insert.append(_COMMA + columnName);
				values.append(_COMMA + buildValue(columnMetaData.getType(), column));
			}

			if (conn != null && (columnDataType == ColumnDataType._CDT_KEY || columnDataType == ColumnDataType._CDT_LONG) && columnMetaData.hasAutoSequenceName()) {
				int seqKey = TableManager.getNextID(columnMetaData.getAutoSequenceName(), conn, this._serverType);
				if (columnDataType == ColumnDataType._CDT_KEY)
					column.setKeyValue(String.valueOf(seqKey));
				else
					column.setLongValue(seqKey);
				insert.append(_COMMA + columnName);
				values.append(_COMMA + buildValue(columnMetaData.getType(), column));
			}

			// If the column is a Timestamp column CDT_TIMESTAMP, always update the value to current date
			if (columnDataType == ColumnDataType._CDT_TIMESTAMP) {

				// Get the local time and then
				// offset it for the GMT and DST Rule
				java.util.Date date = new java.util.Date();
				long time = date.getTime() + AVConstants._LOCAL_TIMEZONE_OFFSET + (AVConstants._LOCAL_TIMEZONE.inDaylightTime(date) ? AVConstants._LOCAL_DSTSAVINGS : 0);

				EJBColumn newColumn = new EJBColumn(column.getName(), 0, 0, false, new EJBDateTime(new java.sql.Date(time)));
				insert.append(_COMMA + columnName);
				values.append(_COMMA + buildValue(columnMetaData.getType(), newColumn));
			}

			if (columnDataType == ColumnDataType._CDT_VERSION) {

				EJBColumn newColumn = new EJBColumn(column.getName(), column.getLongValue() + 1, 0, false, null);
				insert.append(_COMMA + columnName);
				values.append(_COMMA + buildValue(columnMetaData.getType(), newColumn));
			}

			if (columnDataType == ColumnDataType._CDT_USER) {

				EJBColumn newColumn = new EJBColumn(columnMetaData);
				newColumn.setStrValue(user);
				insert.append(_COMMA + columnName);
				values.append(_COMMA + buildValue(columnMetaData.getType(), newColumn));
			}

		}

		if (insert.length() == 0 || values.length() == 0) {

			throw new EJBException("No columns to insert were set for metaData :" + metaData.getEditorMetaData().getName() + ":" + metaData.getEditorMetaData().getType());
		}
		else {
			// Get the tablename for one of the columns in the metadata
			String source = metaData.getTableName(1);
			sql = new StringBuffer();

			sql.append(_INSERT + _SPACE + _INTO + _SPACE);
			sql.append(source + _SPACE);
			sql.append(String.valueOf(_OPEN_BRACKET));
			sql.append(_SPACE);

			// Delete the first COMMA
			sql.append(insert.substring(String.valueOf(_COMMA).length()) + _SPACE);
			sql.append(String.valueOf(_CLOSE_BRACKET));
			sql.append(_SPACE);
			sql.append(_VALUES + _SPACE);
			sql.append(String.valueOf(_OPEN_BRACKET));
			sql.append(_SPACE);

			// Delete the first COMMA
			sql.append(values.substring(String.valueOf(_COMMA).length()) + _SPACE);
			sql.append(String.valueOf(_CLOSE_BRACKET));
			sql.append(_SPACE);
		}

		return sql.toString();
	}

	protected String buildInsertSql(EJBResultSet rs, String user, Connection conn) throws SQLException {
		return buildInsertSql(rs, user, conn, null);
	}

	/**
	 * @param rs
	 * @param user
	 * @return java.lang.String
	 * @roseuid 3E4DA7C30374
	 */
	protected String buildInsertSql(EJBResultSet rs, String user) throws SQLException {
		return buildInsertSql(rs, user, null);
	}

	protected String buildUpdateSql(EJBCriteria criteria, EJBResultSetMetaData metaData, String user) {
		StringBuffer sql = new StringBuffer();
		sql.append(_UPDATE + _SPACE);
		sql.append(metaData.getTableName(1) + _SPACE);
		sql.append(_SET + _SPACE);
		sql.append(buildUpdate(criteria, metaData) + _SPACE);
		String where = buildWhere(criteria, metaData);
		/*
		if(where.length() == 0){
			where = _WHERE + _SPACE;
		}
		else {
			where += _SPACE + _AND + _SPACE;
		}
		where += " ROWNUM < 500";
		*/
		sql.append(where);

		return sql.toString();
	}

	protected String buildUpdateSql(EJBResultSet rs, String user, List<EJBSQLParam> params) {

		List<EJBSQLParam> updateParams = null;
		List<EJBSQLParam> whereParams = null;
		if (params != null) {
			updateParams = new ArrayList<EJBSQLParam>();
			whereParams = new ArrayList<EJBSQLParam>();
		}

		EJBResultSetMetaData metaData = rs.getEJBResultSetMetaData();
		StringBuffer sql = null;
		StringBuffer set = new StringBuffer();
		StringBuffer where = new StringBuffer();
		EJBColumn column = null;
		ColumnMetaData columnMetaData = null;
		int size = metaData.getColumnCount();
		int columnDataType = 0;
		String columnName = null;
		HashMap<String, String> columnAlias = rs.getEJBResultSetMetaData().getEditorMetaData().getColumnAlias();

		for (int index = 1; index <= size; index++) {

			columnMetaData = metaData.getColumnMetaData(index);
			if (columnMetaData.getType() == ColumnDataType._CDT_LINK) {
				continue;
			}

			column = rs.getRecord().getColumn(index);
			columnDataType = columnMetaData.getType();
			columnName = (columnAlias != null && columnAlias.containsKey(columnMetaData.getName())) ? columnAlias.get(columnMetaData.getName()) : columnMetaData.getName();

			// If the column is not updatable or if the value was not set by the
			// client then we need not update it
			// The check for whether the column is avaiable is made because some
			// editors may not have all the columns to be updated. In which case
			// the update sql will not contain the column.

			// If the column is a Timestamp column CDT_TIMESTAMP, always update the value to current date
			if (columnDataType == ColumnDataType._CDT_TIMESTAMP) {
				// Get the local time and then
				// offset it for the GMT and DST Rule
				java.util.Date date = new java.util.Date();
				long time = date.getTime() + AVConstants._LOCAL_TIMEZONE_OFFSET + (AVConstants._LOCAL_TIMEZONE.inDaylightTime(date) ? AVConstants._LOCAL_DSTSAVINGS : 0);

				EJBColumn newColumn = new EJBColumn(column.getName(), 0, 0, false, new EJBDateTime(new java.sql.Date(time)));
				set.append(_COMMA + columnName + _EQUAL + buildValue(columnMetaData.getType(), newColumn));
			}
			else if (columnDataType == ColumnDataType._CDT_VERSION) {
				EJBColumn newColumn = new EJBColumn(column.getName(), column.getLongValue() + 1, 0, false, null);
				set.append(_COMMA + columnName + _EQUAL + buildValue(columnMetaData.getType(), newColumn));
			}
			else if (columnDataType == ColumnDataType._CDT_USER) {
				EJBColumn newColumn = new EJBColumn(columnMetaData);
				newColumn.setStrValue(user);
				set.append(_COMMA + columnName + _EQUAL + buildValue(columnMetaData.getType(), newColumn));
			}
			else if (columnDataType == ColumnDataType._CDT_CLOB && columnMetaData.isUpdatable() && column.getStrValue() != null) {
				set.append(_COMMA).append(columnName).append(_EQUAL).append(buildValue(columnDataType, column, updateParams));
			}
			else if (columnDataType == ColumnDataType._CDT_BLOB && columnMetaData.isUpdatable() && column.getStrValue() != null) {
				set.append(_COMMA).append(columnName).append(_EQUAL).append(buildValue(columnDataType, column, updateParams));
			}
			else if (columnDataType == ColumnDataType._CDT_FILE && columnMetaData.isUpdatable() && column.getObject() != null) {
				set.append(_COMMA).append(columnName).append(_EQUAL).append(buildValue(columnDataType, column, updateParams));
			}
			else if (columnMetaData.isUpdatable() && !columnMetaData.isEditKey() && column.isAvailable()) {
				set.append(_COMMA + columnName + _EQUAL + buildValue(columnMetaData.getType(), column));
			}
			// Include all EDIT_KEY columns in the where clause
			// In addition include all where clauses (based on search criteria)
			// irrespective of whether they are key columns or not
			if (columnMetaData.isEditKey() && columnMetaData.getType() != ColumnDataType._CDT_USER) {
				// For the first time the CDT_TIMESTAMP column may be empty for existing rows
				// In such a case concurrency will not be checked !!!!!!!!!!!!!!!
				if (columnMetaData.getType() != ColumnDataType._CDT_TIMESTAMP || column.getDateTimeValue() != null)
					where.append(_AND + _SPACE + columnName + _EQUAL + buildValue(columnMetaData.getType(), column, whereParams) + _SPACE);
			}

		}

		if (set.length() == 0 || where.length() == 0) {

			throw new EJBException("No columns to update were set for metaData :" + metaData.getEditorMetaData().getName() + ":" + metaData.getEditorMetaData().getType());
		}
		else {
			String source = metaData.getTableName(1);
			sql = new StringBuffer();

			sql.append(_UPDATE + _SPACE);
			sql.append(source + _SPACE);
			sql.append(_SET + _SPACE);
			sql.append(set.substring(String.valueOf(_COMMA).length()) + _SPACE);
			sql.append(_WHERE + _SPACE);
			sql.append(where.substring(String.valueOf(_AND + _SPACE).length()) + _SPACE);
		}

		if (params != null) {
			params.addAll(updateParams);
			int offSet = updateParams.size();
			for (EJBSQLParam param : whereParams) {
				param.setIndex(param.getIndex() + offSet);
				params.add(param);
			}
		}
		return sql.toString();
	}

	protected String buildUpdateSql(EJBResultSet rs, String user) {
		return buildUpdateSql(rs, user, null);
	}

	protected String buildDeleteSql(EJBResultSet rs, String user, List<EJBSQLParam> params) {
		// Delete will always be by Key columns. If some functionality
		// is required, a ROW LEVEL DELETE trigger should be written
		EJBResultSetMetaData metaData = rs.getEJBResultSetMetaData();
		StringBuffer sql = null;
		StringBuffer delete = new StringBuffer();
		ColumnMetaData columnMetaData = null;
		int size = metaData.getColumnCount();
		String columnName = null;
		HashMap<String, String> columnAlias = rs.getEJBResultSetMetaData().getEditorMetaData().getColumnAlias();
		for (int index = 1; index <= size; index++) {

			columnMetaData = metaData.getColumnMetaData(index);
			columnName = (columnAlias != null && columnAlias.containsKey(columnMetaData.getName())) ? columnAlias.get(columnMetaData.getName()) : columnMetaData.getName();

			if (columnMetaData.getType() != ColumnDataType._CDT_LINK && columnMetaData.isEditKey() && columnMetaData.getType() != ColumnDataType._CDT_USER && columnMetaData.getType() != ColumnDataType._CDT_TIMESTAMP)
				delete.append(_AND + _SPACE + columnName + _EQUAL + buildValue(columnMetaData.getType(), rs.getRecord().getColumn(index), params) + _SPACE);
		}

		if (delete.length() == 0) {

			throw new EJBException("No columns to delete were set for metaData :" + metaData.getEditorMetaData().getName() + ":" + metaData.getEditorMetaData().getType());
		}
		else {

			String source = metaData.getTableName(1);

			sql = new StringBuffer();
			sql.append(_DELETE + _SPACE);
			sql.append(_FROM + _SPACE);
			sql.append(source + _SPACE);
			sql.append(_WHERE + _SPACE);

			// Delete the last AND + SPACE in the StringBuffer
			sql.append(delete.substring(String.valueOf(_AND + _SPACE).length()));
		}

		return sql.toString();
	}

	/**
	 * @param rs
	 * @param user
	 * @return java.lang.String
	 * @roseuid 3E4DA7C30392
	 */
	protected String buildDeleteSql(EJBResultSet rs, String user) {
		return buildDeleteSql(rs, user, null);
	}

	/**
	 * @param metaData
	 * @return java.lang.String
	 * @roseuid 3E4DA7C303BA
	 */
	protected String buildSelect(EJBResultSetMetaData metaData) {

		// If the editor type is NULL or DEFAULT, use the column name since is not a group by
		// If the editor type is not null, use the aggregate column if the column is not a part of groupby

		String editorType = metaData.getEditorMetaData().getType();
		boolean useExpr = (editorType == null || editorType.equals(_DEFAULT)) ? false : true;
		int size = metaData.getColumnCount();
		ColumnMetaData columnMetaData = null;
		StringBuffer select = new StringBuffer();

		for (int index = 1; index <= size; index++) {

			columnMetaData = metaData.getColumnMetaData(index);

			// Build the SQL only if the column is not a custom column
			if (columnMetaData.getType() != ColumnDataType._CDT_LINK && columnMetaData.getType() != ColumnDataType._CDT_FILE) {

				// Check to see if the column is a part of the editor type, then its a groupBy column
				// If so use the columnName

				if (useExpr) {

					if (editorType.indexOf(columnMetaData.getName()) == -1)
						if (columnMetaData.getExpression() == null)
							select.append(String.valueOf(_COMMA) + _NULL + String.valueOf(_SPACE) + _AS + String.valueOf(_SPACE) + columnMetaData.getName());
						else
							select.append(String.valueOf(_COMMA) + columnMetaData.getExpression() + String.valueOf(_SPACE) + _AS + String.valueOf(_SPACE) + columnMetaData.getName());
					else
						select.append(String.valueOf(_COMMA) + columnMetaData.getName());
				}
				else {
					if (columnMetaData.getExpression() != null && columnMetaData.isAggregatable())
						select.append(String.valueOf(_COMMA) + columnMetaData.getExpression() + String.valueOf(_SPACE) + _AS + String.valueOf(_SPACE) + columnMetaData.getName());
					else
						select.append(String.valueOf(_COMMA) + columnMetaData.getName());
				}
			}
		}

		if (select.length() == 0)
			throw new EJBException("No columns in the metaData :" + metaData.getEditorMetaData().getName() + ":" + metaData.getEditorMetaData().getType());

		// Remove the first COMMA and return the select clause
		return select.toString().substring(String.valueOf(_COMMA).length());
	}

	public String buildWhere(EJBCriteria criteria, EJBResultSetMetaData metaData, List<EJBSQLParam> params) {

		StringBuffer where = new StringBuffer();
		EJBCriteriaColumn[] columns = criteria.getWhere();
		EJBCriteriaColumn column = null;
		ColumnMetaData columnMetaData = null;
		int size = (columns == null) ? 0 : columns.length;
		String editorType = metaData.getEditorMetaData().getType();

		for (int index = 0; index < size; index++) {

			column = columns[index];
			columnMetaData = metaData.getColumnMetaData(column.getName());

			// Create a WHERE clause only if an aggregate expression is NOT specified
			// OR if the editorType is DEFAULT i.e. NOT A GROUP BY
			if (editorType.equals(_DEFAULT) || (!editorType.equals(_DEFAULT) && columnMetaData.getExpression() == null))
				where.append(_SPACE + _AND + _SPACE + buildClause(columnMetaData, column, false, params));
		}
		if (criteria.getQueueCriteria() != null)
			where.append(_SPACE + _AND + _SPACE + criteria.getQueueCriteria());

		// Remove the first _SPACE + _AND + _SPACE
		int length = where.length();

		return length != 0 ? _WHERE + _SPACE + where.toString().substring(String.valueOf(_SPACE + _AND + _SPACE).length()) : "";
	}

	public String buildWhere(EJBCriteria criteria, EJBResultSetMetaData metaData) {
		return buildWhere(criteria, metaData, null);
	}

	public String buildUpdate(EJBCriteria criteria, EJBResultSetMetaData metaData) {
		StringBuffer update = new StringBuffer();
		EJBCriteriaColumn[] columns = criteria.getUpdate();
		EJBCriteriaColumn column = null;
		ColumnMetaData columnMetaData = null;
		int size = (columns == null) ? 0 : columns.length;
		String editorType = metaData.getEditorMetaData().getType();
		String space_comma_space = _SPACE + "" + _COMMA + "" + _SPACE;
		String columnName = null;
		HashMap<String, String> columnAlias = metaData.getEditorMetaData().getColumnAlias();

		for (int index = 0; index < size; index++) {
			column = columns[index];
			columnMetaData = metaData.getColumnMetaData(column.getName());
			columnName = (columnAlias != null && columnAlias.containsKey(columnMetaData.getName())) ? columnAlias.get(columnMetaData.getName()) : columnMetaData.getName();

			if (editorType.equals(_DEFAULT) || (!editorType.equals(_DEFAULT) && columnMetaData.getExpression() == null)) {
				if (column.isNull() && columnMetaData.getType() == ColumnDataType._CDT_DATE) {
					update.append(space_comma_space + columnName + _EQUAL + _NULL);
				}
				else {
					update.append(space_comma_space + buildClause(columnMetaData, column, false));
				}
			}
		}
		// Remove the first _SPACE + _COMMA + _SPACE
		int length = update.length();
		return length != 0 ? _SPACE + update.toString().substring(space_comma_space.length()) : "";
	}

	protected String buildClause(ColumnMetaData columnMetaData, EJBCriteriaColumn column, boolean useExpr, List<EJBSQLParam> params) {
		String operator = null;
		String columnName = useExpr ? columnMetaData.getExpression() : columnMetaData.getName();
		if (column.isNull()) {
			return columnName + _SPACE + _IS_NULL;
		}

		if (columnMetaData.getType() == ColumnDataType._CDT_CLOB && _serverType.equals("ORACLE")) {
			return "DBMS_LOB.INSTR (" + columnName + ", '" + column.getStrValue() + "', 1, 1) > 0";
		}
		if (columnMetaData.getType() == ColumnDataType._CDT_BLOB && _serverType.equals("ORACLE")) {
			return "DBMS_BOB.INSTR (" + columnName + ", '" + column.getStrValue() + "', 1, 1) > 0";
		}

		switch (column.getOperator()) {

		case AVConstants._EQUAL:
			operator = String.valueOf(AVConstants._EQUAL_SIGN);
			break;
		case AVConstants._NOT_EQUAL:
			operator = String.valueOf(AVConstants._NOT_EQUAL_SIGN);
			break;
		case AVConstants._GREATER:
			operator = String.valueOf(AVConstants._GREATER_SIGN);
			break;
		case AVConstants._LESSER:
			operator = String.valueOf(AVConstants._LESSER_SIGN);
			break;
		case AVConstants._GREATER_EQUAL:
			operator = AVConstants._GREATER_EQUAL_SIGN;
			break;
		case AVConstants._LESSER_EQUAL:
			operator = AVConstants._LESSER_EQUAL_SIGN;
			break;
		case AVConstants._LIKE:
			operator = AVConstants._LIKE_SIGN;
			break;
		case AVConstants._CONTAINS:
			operator = AVConstants._LIKE_SIGN;
			break;
		case AVConstants._ENDS:
			operator = AVConstants._LIKE_SIGN;
			break;
		case AVConstants._IN:
			operator = AVConstants._IN_SIGN;
			break;
		case AVConstants._LIKE_IN:
			operator = AVConstants._LIKE_IN_SIGN;
			break;
		default:
			throw new EJBException("Operator is invalid : " + String.valueOf(column.getOperator()));
		}

		if (operator == AVConstants._LIKE_IN_SIGN) {
			StringBuffer buf = new StringBuffer();
			buf.append(_SPACE + String.valueOf(_OPEN_BRACKET));
			String[] strValues = StringUtils.split(column.getStrValue(), ",");
			String value = null;
			for (int i = 0; i < strValues.length; i++) {
				value = strValues[i];
				value = StringUtils.replace(value, "%", "");
				if (i > 0) {
					buf.append(_SPACE + "OR" + _SPACE);
				}
				buf.append("UPPER(" + columnName + ")" + _SPACE + AVConstants._LIKE_SIGN + _SPACE + "'" + value + "%'");
			}
			buf.append(_SPACE + String.valueOf(_CLOSE_BRACKET));
			return buf.toString();
		}
		else if (operator == AVConstants._IN_SIGN) {
			// Not using buildValue function since when the value passed in is ('11','12') this function escapes the single quotes
			// resulting in (''11'',''12'') causing SQL error. Also it encloses a single quote for the entire String.
			// Server will substitute the value as is that is passed by the client.
			// So it is upto the client to escape any single quote or other speciasl characters and
			// delimit the in clause properly

			if (columnMetaData.getType() == ColumnDataType._CDT_KEY || columnMetaData.getType() == ColumnDataType._CDT_DOUBLE || columnMetaData.getType() == ColumnDataType._CDT_INT || columnMetaData.getType() == ColumnDataType._CDT_SHORT || columnMetaData.getType() == ColumnDataType._CDT_FLOAT
					|| columnMetaData.getType() == ColumnDataType._CDT_LONG) {
				return columnName + _SPACE + operator + _SPACE + "(" + column.getStrValue() + ")";
			}
			return "UPPER(" + columnName + ")" + _SPACE + operator + _SPACE + "(" + column.getStrValue().toUpperCase() + ")";
		}
		else if (AVConstants._LIKE_SIGN.equals(operator)) {
			if (columnMetaData.getEjbResultSet() != null && !column.isNull()) {
				operator = String.valueOf(AVConstants._EQUAL_SIGN);
				column.setStrValue(StringUtils.replace(column.getStrValue(), "%", ""));
			}
			if (columnMetaData.isIndexedUpperCaseData()) {
				if (columnMetaData.getEjbResultSet() != null) {
					return columnName + _SPACE + operator + _SPACE + buildValue(columnMetaData.getType(), column, params);
				}
				return columnName + _SPACE + operator + _SPACE + "UPPER(" + buildValue(columnMetaData.getType(), column, params) + ")";
			}
			return "UPPER(" + columnName + ")" + _SPACE + operator + _SPACE + "UPPER(" + buildValue(columnMetaData.getType(), column, params) + ")";
		}
		else if (AVConstants._NOT_EQUAL_SIGN.equals(operator)) {
			return _OPEN_BRACKET + columnName + _SPACE + operator + _SPACE + buildValue(columnMetaData.getType(), column, params) + " OR " + columnName + _SPACE + _IS_NULL + _CLOSE_BRACKET;
		}
		return columnName + _SPACE + operator + _SPACE + buildValue(columnMetaData.getType(), column, params);
	}

	protected String buildClause(ColumnMetaData columnMetaData, EJBCriteriaColumn column, boolean useExpr) {
		return buildClause(columnMetaData, column, useExpr, null);
	}

	protected String buildValue(int columnType, EJBColumn column, List<EJBSQLParam> params) {
		StringBuffer value = new StringBuffer();
		if (column.isNull()) {
			value.append(_NULL);
		}
		else {

			switch (columnType) {

			case ColumnDataType._CDT_STRING:
			case ColumnDataType._CDT_USER:
				if (params != null) {
					// params.add(new EJBSQLParam(params.size(), Types.VARCHAR, escapeQuotes(column.getStrValue())));
					params.add(new EJBSQLParam(params.size(), Types.VARCHAR, column.getStrValue())); // prepareStatement we don't need to escape quotes..
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(_QUOTE).append(escapeQuotes(column.getStrValue())).append(_QUOTE);
				}
				break;

			case ColumnDataType._CDT_INT:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.INTEGER, column.getIntValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getIntValue());
				}
				break;

			case ColumnDataType._CDT_LONG:
			case ColumnDataType._CDT_VERSION:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.BIGINT, column.getLongValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getLongValue());
				}
				break;

			case ColumnDataType._CDT_DOUBLE:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.DOUBLE, column.getDblValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getDblValue());
				}
				break;

			case ColumnDataType._CDT_FLOAT:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.FLOAT, column.getFloatValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getFloatValue());
				}
				break;

			case ColumnDataType._CDT_DATE:
				if (_serverType.equals("MSAccess") || _serverType.equals(AVConstants._MSACCESS))
					value.append(_FORMAT).append(_OPEN_BRACKET);
				else if (_serverType.equals(AVConstants._SQLSERVER))
					value.append(_CONVERT);
				else
					value.append(_TO_DATE).append(_OPEN_BRACKET);

				value.append(_QUOTE).append(column.getDateTimeValue().getMonth()).append(_SLASH).append(column.getDateTimeValue().getDay()).append(_SLASH).append(column.getDateTimeValue().getYear()).append(_QUOTE);

				if (!_serverType.equals(AVConstants._SQLSERVER))
					value.append(_COMMA).append(_QUOTE).append(_DATEFORMAT).append(_QUOTE);

				value.append(_CLOSE_BRACKET);
				break;

			case ColumnDataType._CDT_DATETIME:
			case ColumnDataType._CDT_TIMESTAMP:
				if (_serverType.equals("MSAccess") || _serverType.equals(AVConstants._MSACCESS))
					value.append(_FORMAT).append(_OPEN_BRACKET);
				else if (_serverType.equals(AVConstants._SQLSERVER))
					value.append(_CONVERT);
				else
					value.append(_TO_DATE).append(_OPEN_BRACKET);
				value.append(_QUOTE).append(column.getDateTimeValue().getMonth()).append(_SLASH).append(column.getDateTimeValue().getDay()).append(_SLASH).append(column.getDateTimeValue().getYear()).append(_SPACE).append(column.getDateTimeValue().getHour()).append(_COLON)
						.append(column.getDateTimeValue().getMinute()).append(_COLON).append(column.getDateTimeValue().getSecond()).append(_QUOTE);

				if (!_serverType.equals(AVConstants._SQLSERVER))
					value.append(_SPACE).append(_COMMA).append(_QUOTE).append(_DATETIMEFORMAT).append(_QUOTE);

				value.append(_CLOSE_BRACKET);
				break;

			case ColumnDataType._CDT_TIME:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.INTEGER, column.getIntValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getIntValue());
				}
				break;

			case ColumnDataType._CDT_CHAR:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.VARCHAR, String.valueOf(column.getCharValue())));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(_QUOTE).append(column.getCharValue()).append(_QUOTE);
				}
				break;

			case ColumnDataType._CDT_BOOLEAN:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.VARCHAR, String.valueOf(column.getCharValue())));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(_QUOTE).append(column.getCharValue()).append(_QUOTE);
				}
				break;

			case ColumnDataType._CDT_DOW:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.BIGINT, column.getLongValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getLongValue());
				}
				break;

			case ColumnDataType._CDT_SHORT:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.SMALLINT, column.getShortValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getShortValue());
				}
				break;

			case ColumnDataType._CDT_KEY:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.VARCHAR, column.getKeyValue()));
					value.append(_QUESTION_MARK);
				}
				else {
					value.append(column.getKeyValue());
				}
				break;

			case ColumnDataType._CDT_CLOB:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.CLOB, column.getStrValue()));
				}
				value.append(_QUESTION_MARK);
				break;
			case ColumnDataType._CDT_BLOB:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.BLOB, column.getStrValue()));
				}
				value.append(_QUESTION_MARK);
				break;
			case ColumnDataType._CDT_FILE:
				if (params != null) {
					params.add(new EJBSQLParam(params.size(), Types.BLOB, column.getObject()));
				}
				value.append(_QUESTION_MARK);
				break;
			case ColumnDataType._CDT_LINK:
				break; // Custom columns should not have their value built

			default:
				throw new EJBException("Column Data Type is not recognized :" + String.valueOf(columnType));
			}
		}

		return value.toString();
	}

	protected String buildValue(int columnType, EJBColumn column) {
		return buildValue(columnType, column, null);
	}

	/**
	 * @param inString
	 * @return java.lang.String
	 * @roseuid 3E4DA7C40037
	 */
	protected String escapeQuotes(String inString) {

		StringBuffer outString = new StringBuffer();
		String token = null;

		if (inString != null) {

			StringTokenizer st = new StringTokenizer(inString, String.valueOf(_QUOTE), true);

			while (st.hasMoreTokens()) {

				token = st.nextToken();

				if (token.equals(String.valueOf(_QUOTE)))
					outString.append(_QUOTE).append(_QUOTE);
				else
					outString.append(token);
			}
		}

		return outString.toString();
	}

	/**
	 * @param metaData
	 * @return java.lang.String
	 * @roseuid 3E4DA7C40073
	 */
	protected String buildFrom(EJBCriteria criteria, EJBResultSetMetaData metaData) {

		// The from clause for the lookup is built from the editor_source but the inserts/updates/deletes should
		// execute on the ResultSetMetaData.getTableName()

		StringBuffer source = new StringBuffer(metaData.getEditorMetaData().getSource());
		if (criteria.getSourceParams() != null) {
			String paramName = null;
			String paramValue = null;
			String paramKey = null;
			int startPos = -1;
			Map<String, String> sourceParams = criteria.getSourceParams();
			for (Iterator<String> iterator = sourceParams.keySet().iterator(); iterator.hasNext();) {
				paramName = iterator.next();
				paramValue = sourceParams.get(paramName);
				paramKey = "<%" + paramName + "%>".toUpperCase();
				startPos = source.toString().toUpperCase().indexOf(paramKey);
				while (startPos != -1) {
					source = source.replace(startPos, startPos + paramKey.length(), paramValue);
					startPos = source.toString().toUpperCase().indexOf(paramKey);
				}
			}
		}
		return _FROM + _SPACE + source.toString();
	}

	/**
	 * @param criteria
	 * @return java.lang.String
	 * @roseuid 3E4DA7C4009B
	 */
	public String buildGroupBy(EJBCriteria criteria) {

		StringBuffer groupBy = new StringBuffer();
		EJBCriteriaColumn[] columns = criteria.getGroupBy();
		EJBCriteriaColumn column = null;
		int size = (columns == null) ? 0 : columns.length;

		for (int index = 0; index < size; index++) {

			column = columns[index];

			if (index == size - 1)
				groupBy.append(column.getName());
			else
				groupBy.append(column.getName() + _COMMA);
		}

		return groupBy.length() != 0 ? _GROUPBY + _SPACE + groupBy.toString() : groupBy.toString();
	}

	/**
	 * @param criteria
	 * @param metaData
	 * @return java.lang.String
	 * @roseuid 3E4DA7C400A5
	 */
	public String buildHaving(EJBCriteria criteria, EJBResultSetMetaData metaData) {

		StringBuffer having = new StringBuffer();
		EJBCriteriaColumn[] columns = criteria.getWhere();
		EJBCriteriaColumn column = null;
		ColumnMetaData columnMetaData = null;
		int size = (columns == null) ? 0 : columns.length;

		for (int index = 0; index < size; index++) {

			column = columns[index];
			columnMetaData = metaData.getColumnMetaData(column.getName());

			// Create a HAVING clause only if an aggregate expression is specified
			if (columnMetaData.getExpression() != null)
				having.append(_SPACE + _AND + _SPACE + buildClause(columnMetaData, column, true));
		}

		// Remove the first _SPACE + _AND + _SPACE
		int length = having.length();

		return length != 0 ? _HAVING + _SPACE + having.toString().substring(String.valueOf(_SPACE + _AND + _SPACE).length()) : "";
	}

	/**
	 * @param criteria
	 * @return java.lang.String
	 * @roseuid 3E4DA7C400C3
	 */
	public String buildOrderBy(EJBCriteria criteria) {
		StringBuffer orderBy = new StringBuffer();
		EJBCriteriaColumn[] columns = criteria.getOrderBy();
		EJBCriteriaColumn column = null;
		int size = (columns == null) ? 0 : columns.length;
		String sort = null;

		for (int index = 0; index < size; index++) {

			column = columns[index];
			sort = (column.getOrdering() == AVConstants._ASC) ? _ASC : _DESC;

			orderBy.append(column.getName()).append(_SPACE).append(sort);
			if (index != size - 1)
				orderBy.append(_COMMA);
		}

		return orderBy.length() != 0 ? _ORDERBY + _SPACE + orderBy.toString() : "";
	}

	/**
	 * @param rs
	 * @return java.lang.String
	 * @roseuid 3E4DA7C400D7
	 */
	protected String buildFunction(EJBResultSet rs) {

		// The procedure name should be entered as CALL PROCEDURE_NAME.
		// The keyword CALL should be present.
		StringBuffer funcName = new StringBuffer();
		EJBResultSetMetaData metaData = rs.getEJBResultSetMetaData();
		int size = metaData.getColumnCount();
		ColumnMetaData columnMetaData = null;
		int index;
		String procName = metaData.getEditorMetaData().getProcedure();

		procName = procName.substring(procName.indexOf("CALL ") + 5);

		// The function name will be built in the following format
		// "begin ? := PROC_NAME( ?, ?, ? ... ); end;" );

		// If the proc name already contains the ? then dont build it
		if (procName.indexOf("?") < 0) {

			funcName.append(_BEGIN).append(_SPACE);
			funcName.append(_QUESTION_MARK).append(_SPACE);
			funcName.append(_ASSIGN).append(_SPACE);
			funcName.append(procName);
			funcName.append(_OPEN_BRACKET).append(_SPACE);

			// Build Parameters
			for (index = 1; index <= size; index++) {

				columnMetaData = metaData.getColumnMetaData(index);

				// Include the column if :
				// 1. key column
				// 2. column is updatable
				// 3. column is searchable
				// Include them in the order in which the column is sequenced in db.
				// ColumnMetaData will provide all columns only in that sequence
				if (((columnMetaData.isEditKey()) || columnMetaData.isUpdatable()) && columnMetaData.getType() != ColumnDataType._CDT_LINK)
					funcName.append(_QUESTION_MARK).append(_COMMA);
			}
			// Remove the last COMMA
			funcName.deleteCharAt(funcName.length() - 1);

			funcName.append(_CLOSE_BRACKET);
			funcName.append(_SEMICOLON).append(_SPACE);
			funcName.append(_END);
			funcName.append(_SEMICOLON);
		}
		else {

			funcName.append(procName);
		}
		return funcName.toString();
	}

	/**
	 * @param rs
	 * @param cstmt
	 * @throws java.sql.SQLException
	 * @roseuid 3E4DA7C400EB
	 */
	protected void buildFuncParams(EJBResultSet rs, CallableStatement cstmt) throws SQLException {

		int paramIndex = 1;
		EJBResultSetMetaData metaData = rs.getEJBResultSetMetaData();
		int size = metaData.getColumnCount();
		ColumnMetaData columnMetaData = null;
		int index;

		// The stored procedure will have to have all the parameters in a certain order. The
		// order is based on the columns order in column_seq. The procedure will hence contain
		// 1. All KEY columns
		// 2. All Searchable columns (the columns should have the exact value of these where clauses)
		// 3. All updatable columns

		// The 1st index of the CSTMT should always be a return value
		cstmt.registerOutParameter(paramIndex, java.sql.Types.INTEGER);

		// Indexes for CSTMT start with 2 since the first parameter is always OUT
		// Build the statement's KEY columns
		// Build the statement's searchable columns
		// Build the statement's updatable columns
		for (index = 1; index <= size; index++) {

			columnMetaData = metaData.getColumnMetaData(index);

			if (((columnMetaData.isEditKey()) || (columnMetaData.isUpdatable())) && columnMetaData.getType() != ColumnDataType._CDT_LINK) {

				// System.out.println( "COlumns in SP = " + columnMetaData.getName() );
				paramIndex++;
				buildFuncParamValues(rs, cstmt, columnMetaData.getType(), index, paramIndex);
			}
		}
	}

	/**
	 * @param rs
	 * @param cstmt
	 * @param columnType
	 * @param colIndex
	 * @param paramIndex
	 * @throws java.sql.SQLException
	 * @roseuid 3E4DA7C4014F
	 */
	protected void buildFuncParamValues(EJBResultSet rs, CallableStatement cstmt, int columnType, int colIndex, int paramIndex) throws SQLException {

		String value = rs.getString(colIndex);

		switch (columnType) {

		case ColumnDataType._CDT_STRING:
		case ColumnDataType._CDT_USER:
			if (value == null)
				cstmt.setNull(paramIndex, Types.VARCHAR);
			else
				cstmt.setString(paramIndex, rs.getString(colIndex));
			break;

		case ColumnDataType._CDT_INT:
			if (value == null)
				cstmt.setNull(paramIndex, Types.INTEGER);
			else
				cstmt.setInt(paramIndex, rs.getInt(colIndex));
			break;

		case ColumnDataType._CDT_LONG:
		case ColumnDataType._CDT_VERSION:
			if (value == null)
				cstmt.setNull(paramIndex, Types.BIGINT);
			else
				cstmt.setLong(paramIndex, rs.getLong(colIndex));
			break;

		case ColumnDataType._CDT_DOUBLE:
			if (value == null)
				cstmt.setNull(paramIndex, Types.DOUBLE);
			else
				cstmt.setDouble(paramIndex, rs.getDouble(colIndex));
			break;

		case ColumnDataType._CDT_FLOAT:
			if (value == null)
				cstmt.setNull(paramIndex, Types.FLOAT);
			else
				cstmt.setFloat(paramIndex, rs.getFloat(colIndex));
			break;

		case ColumnDataType._CDT_DATE:
			if (value == null)
				cstmt.setNull(paramIndex, Types.DATE);
			else
				cstmt.setDate(paramIndex, rs.getDate(colIndex, AVConstants._GMT_CALENDAR), AVConstants._GMT_CALENDAR);
			break;

		case ColumnDataType._CDT_DATETIME:
		case ColumnDataType._CDT_TIMESTAMP:
			if (value == null)
				cstmt.setNull(paramIndex, Types.TIMESTAMP);
			else
				cstmt.setTimestamp(paramIndex, rs.getTimestamp(colIndex, AVConstants._GMT_CALENDAR), AVConstants._GMT_CALENDAR);
			break;

		case ColumnDataType._CDT_TIME:
			if (value == null)
				cstmt.setNull(paramIndex, Types.TIME);
			else
				cstmt.setTime(paramIndex, rs.getTime(colIndex));
			break;

		case ColumnDataType._CDT_CHAR:
			if (value == null)
				cstmt.setNull(paramIndex, Types.TINYINT);
			else
				cstmt.setByte(paramIndex, rs.getByte(colIndex));
			break;

		case ColumnDataType._CDT_BOOLEAN:
			if (value == null)
				cstmt.setNull(paramIndex, Types.CHAR);
			else
				cstmt.setString(paramIndex, rs.getString(colIndex));
			break;

		case ColumnDataType._CDT_DOW:
			if (value == null)
				cstmt.setNull(paramIndex, Types.INTEGER);
			else
				cstmt.setInt(paramIndex, rs.getInt(colIndex));
			break;

		case ColumnDataType._CDT_SHORT:
			if (value == null)
				cstmt.setNull(paramIndex, Types.SMALLINT);
			else
				cstmt.setInt(paramIndex, rs.getInt(colIndex));
			break;

		case ColumnDataType._CDT_KEY:
			if (value == null)
				cstmt.setNull(paramIndex, Types.INTEGER);
			else
				cstmt.setInt(paramIndex, Integer.parseInt(rs.getString(colIndex).trim()));
			break;

		case ColumnDataType._CDT_CLOB:
			if (value == null)
				cstmt.setNull(paramIndex, Types.CLOB);
			else
				cstmt.setCharacterStream(paramIndex, getClobReader(value), value.length());
			break;

		case ColumnDataType._CDT_BLOB:
			if (value == null)
				cstmt.setNull(paramIndex, Types.BLOB);
			else
				cstmt.setCharacterStream(paramIndex, getBlobReader(value), value.length());
			break;

		case ColumnDataType._CDT_LINK:
			break; // Do not build custom values

		default:
			throw new EJBException("Column Type is invalid :" + String.valueOf(columnType));
		}
	}

	protected StringReader getClobReader(String value) {
		return new StringReader(value);
	}

	protected StringReader getBlobReader(String value) {
		return new StringReader(value);
	}

	protected void setClob(PreparedStatement pStmt, EJBResultSet rs) throws SQLException {
		if (rs == null)
			return;
		EJBResultSetMetaData metaData = rs.getEJBResultSetMetaData();
		int size = metaData.getColumnCount();
		int count = 1;
		for (int index = 1; index <= size; index++) {
			ColumnMetaData columnMetaData = metaData.getColumnMetaData(index);
			int columnDataType = columnMetaData.getType();
			if (!columnMetaData.isUpdatable() || !(columnDataType == ColumnDataType._CDT_CLOB || columnDataType == ColumnDataType._CDT_BLOB || columnDataType == ColumnDataType._CDT_FILE)) {
				continue;
			}
			EJBColumn column = rs.getRecord().getColumn(columnMetaData.getName());
			if (columnDataType == ColumnDataType._CDT_FILE) {
				Object fileObject = column.getObject();
				if (fileObject != null) {
					byte[] byteArray = (byte[]) fileObject;
					if (byteArray.length > Integer.MAX_VALUE) {
						throw new XRuntime(getClass().getName(), columnMetaData.getName() + " (Blob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + byteArray.length);
					}
					ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
					pStmt.setBinaryStream(count++, inputStream, byteArray.length);
				}
			}
			else {
				String value = column.getStrValue();
				if (value == null) {
					// pStmt.setNull(count++, Types.CLOB );
					continue;
				}
				if (value.length() > Integer.MAX_VALUE) {
					throw new XRuntime(getClass().getName(), columnMetaData.getName() + " (Clob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + value.length());
				}
				pStmt.setCharacterStream(count++, getClobReader(value), value.length());
			}
		}
	}

}
