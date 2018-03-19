package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.naming.NamingException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.environment.Environment;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnInfo;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorFilter;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.Pair;
import com.addval.utils.StrUtl;

/**
 * DAO Implementation of the EntityBean for EditorMetaData. This class is used in the Spring implementations instead of the EJBEEditorMetaDataBean
 **/

public class EditorMetaDataDAO {
	private static final String _module = "com.addval.ejbutils.server.EJBEEditorMetaDataBean";
	private String _serverType = "";
	private static final String _COLUMN_INFO = "COLUMN_INFO";
	protected static final String _DEFAULT = "DEFAULT";
	private String _sqlNullFunction;
	private String _concatenateOperator;
	private Environment _env = null;
	private EJBSTableManager _tableManager;

	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(EditorMetaDataDAO.class);

	public Environment getEnvironment() {
		return _env;
	}

	public void setEnvironment(Environment env) {
		_env = env;
		_serverType = _env.getDbPoolMgr().getServerType();

		if (_serverType.equals("SQLSERVER") || _serverType.equals("MSAccess")) {
			_sqlNullFunction = "ISNULL";
			_concatenateOperator = "+";
		}
		else {
			_sqlNullFunction = "NVL";
			_concatenateOperator = "||";
		}
	}

	public String getServerType() {
		return _serverType;
	}

	public void setTableManager(EJBSTableManager tableManager) {
		_tableManager = tableManager;
	}

	public EJBSTableManager getTableManager() {
		return _tableManager;
	}

	public EditorMetaData lookupEditorComboCache(EditorMetaData metadata) throws Exception {

		for (int i = 1; i <= metadata.getColumnCount(); i++) {

			ColumnMetaData columnMetaData = metadata.getColumnMetaData(i);

			if (columnMetaData.isCombo() && columnMetaData.isComboSelectTag()) {

				if ((columnMetaData.isComboCached() == false) || (columnMetaData.getEjbResultSet() == null)) {

					lookupComboCache(columnMetaData);
				}

			}
		}

		return metadata;
	}

	private void lookupComboCache(ColumnMetaData colmetadata) throws RemoteException {
		if (colmetadata.isCombo()) {

			if (colmetadata.isComboSelectTag()) {
				// now get the list of values as an EJBResultset and save it in the ColumnMetaData
				EditorMetaData comboMetaData = null;
				try {

					String name = colmetadata.getComboTblName();
					String type = "DEFAULT";

					// Get the non-customized EditorMetaData
					comboMetaData = loadEditorMetadata(name, type, _DEFAULT);

					// get any combo information for this editor
					comboMetaData = lookupEditorComboCache(comboMetaData);

					com.addval.ejbutils.dbutils.EJBCriteria ejbCriteria = (new com.addval.ejbutils.dbutils.EJBResultSetMetaData(comboMetaData)).getNewCriteria();

					if (colmetadata.getComboOrderBy() != null) {
						java.util.Vector vector = new java.util.Vector();
						vector.add(colmetadata.getComboOrderBy());
						ejbCriteria.orderBy(vector);
					}

					EJBResultSet ejbResultSet = getTableManager().lookup(ejbCriteria);

					colmetadata.setEjbResultSet(ejbResultSet);
				}
				catch (Exception e) {
					_logger.error(e);
					throw new EJBException(e);
				}
			}
		}
	}

	public void saveEditorMetadata(String name, String type, String userId, EditorMetaData metadata) {
		Connection conn = null;

		try {

			conn = getConnection();

			if (userId != null && userId.equalsIgnoreCase(_DEFAULT)) {
				// Do not update Base EditorMetaData
				// updateEditorsTbl ( conn, metadata );
				// updateEditorTypesTbl ( conn, metadata );
				// updateEditorColumnsTbl ( conn, metadata );
				// updateEditorTypeColumnsTbl( conn, metadata );
				// updateColumnsTbl ( conn, metadata );
			}
			else if (userId != null && !userId.equalsIgnoreCase(_DEFAULT)) {
				updateUserEditorColumnsTbl(name, userId, conn, metadata);
			}

		}
		catch (SQLException se) {

			_logger.error(se);
			throw new EJBException(se);

		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);

		}
		finally {

			try {

				if (conn != null)
					releaseConnection(conn);
			}
			catch (SQLException se) {

				_logger.error(se);
				throw new EJBException(se);
			}
		}

	}

	public EditorMetaData loadEditorMetadata(String name, String type, String userId) {
		Connection conn = null;
		try {
			EditorMetaData metadata = null;
			conn = getConnection();
			if (type != null && type.equalsIgnoreCase(_COLUMN_INFO)) {
				metadata = buildColumnMetaData(name, conn);
			}
			else {
				metadata = buildEditorMetaData(name, type, userId, conn);
			}
			return metadata;
		}
		catch (SQLException se) {
			_logger.error(se);
			throw new EJBException(se);
		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);
		}
		finally {

			try {
				if (conn != null)
					releaseConnection(conn);
			}
			catch (SQLException se) {

				_logger.error(se);
				throw new EJBException(se);
			}
		}
	}

	public List<Pair> lookupNameLabelPairs(String type) throws RemoteException {
		String sql = null;

		if (type.equalsIgnoreCase("COLUMN")) {
			sql = buildColumnLabelsSql();
		}
		else if (type.equalsIgnoreCase("COLUMN-ERRMSG")) {
			sql = buildColumnErrorSql();
		}
		else if (type.equalsIgnoreCase("EDITOR")) {
			sql = buildEditorLabelsSql();
		}
		else if (type.equalsIgnoreCase("MENU")) {
			sql = buildMenuLabelsSql();
		}
		else {
			sql = buildErrorLabelsSql();
		}

		return buildNameLabelCollection(sql);
	}

	private List<Pair> buildNameLabelCollection(String sql) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Pair p = null;
		ArrayList<Pair> list = null;
		int size = 0;

		list = new ArrayList<Pair>();

		/*
		*/

		try {
			conn = getConnection();

			// Execute the query for getting Column Info
			stmt = conn.prepareStatement(sql);

			// Log the SQL that was generated
			_logger.debug(sql);

			// Execute the query for Editors
			rs = stmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString(1);
				String label = rs.getString(2);

				if (label == null) {
					label = name;
				}

				p = new Pair(name, label);

				list.add(p);
			}

			return list;
		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);
		}
		finally {

			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					releaseConnection(conn);
			}

			catch (SQLException se) {

				_logger.error(se);
				throw new EJBException(se);
			}
		}

	}

	private EditorMetaData buildColumnMetaData(String name, Connection conn) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsMetaData = null;
		ColumnInfo colInfo = null;
		StringBuffer sql = new StringBuffer();
		String type = null;
		String caption = null;
		String format = null;
		String unit = null;
		boolean combo = false;
		String comboTblName = null;
		String comboSelect = null;
		String comboOrderBy = null;
		double minval = 0;
		double maxval = -1.0;
		String regexp = null;
		String errorMsg = null;
		String value = null;
		String textAlign = null;
		String displayCSS = null;
		String displayStyle = null;
		String columnSizeStr = null;
		Integer textBoxSize = null;
		Integer textBoxMaxlength = null;
		Integer textAreaRows = null;
		Integer textAreaCols = null;

		ColumnMetaData columnMetaData = null;
		ColumnInfo columnInfo = null;
		Hashtable cols = null;
		int size = 0;

		EditorMetaData editorMetaData = null;

		try {

			sql.append("SELECT * FROM " + ColumnsTbl._tblName);
			sql.append(" WHERE " + ColumnsTbl._name + "= ?");

			// Execute the query for getting Column Info
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, name);

			// Log the SQL that was generated
			_logger.debug(sql.toString());

			// Execute the query for Editors
			rs = stmt.executeQuery();

			if (rs.next()) {

				rsMetaData = rs.getMetaData();
				size = rsMetaData.getColumnCount();
				cols = new Hashtable();

				for (int index = 1; index <= size; index++)
					cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));

				name = getString(rs,ColumnsTbl._name);
				type = getString(rs,ColumnsTbl._type);
				caption = getString(rs,ColumnsTbl._caption);
				format = cols.containsKey(ColumnsTbl._format) ? getString(rs,ColumnsTbl._format) : null;
				unit = cols.containsKey(ColumnsTbl._unit) ? getString(rs,ColumnsTbl._unit) : null;
				combo = cols.containsKey(ColumnsTbl._combo) ? (rs.getInt(ColumnsTbl._combo) != 0 ? true : false) : false;
				comboTblName = cols.containsKey(ColumnsTbl._comboTblName) ? getString(rs,ColumnsTbl._comboTblName) : null;
				comboSelect = cols.containsKey(ColumnsTbl._comboSelect) ? getString(rs,ColumnsTbl._comboSelect) : null;
				comboOrderBy = cols.containsKey(ColumnsTbl._comboOrderBy) ? getString(rs,ColumnsTbl._comboOrderBy) : null;
				minval = cols.containsKey(ColumnsTbl._minval) ? rs.getDouble(ColumnsTbl._minval) : 0;
				maxval = cols.containsKey(ColumnsTbl._maxval) ? rs.getDouble(ColumnsTbl._maxval) : 0;
				regexp = cols.containsKey(ColumnsTbl._regexp) ? getString(rs,ColumnsTbl._regexp) : null;
				errorMsg = cols.containsKey(ColumnsTbl._errormsg) ? getString(rs,ColumnsTbl._errormsg) : null;
				value = cols.containsKey(ColumnsTbl._value) ? getString(rs,ColumnsTbl._value) : null;
				textAlign = cols.containsKey(ColumnsTbl._displayAlign) ? getString(rs,ColumnsTbl._displayAlign) : null;
				displayCSS = cols.containsKey(ColumnsTbl._displayCSS) ? getString(rs,ColumnsTbl._displayCSS) : null;
				displayStyle = cols.containsKey(ColumnsTbl._displayStyle) ? getString(rs,ColumnsTbl._displayStyle) : null;
				columnSizeStr = cols.containsKey(ColumnsTbl._size) ? getString(rs,ColumnsTbl._size) : null;

				if (!StrUtl.isEmptyTrimmed(columnSizeStr)) {
					String[] minmax = columnSizeStr.split(",");
					textBoxSize = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textBoxMaxlength = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
					textAreaCols = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textAreaRows = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
				}
				else {
					textBoxSize = null;
					textBoxMaxlength = null;
					textAreaRows = null;
					textAreaCols = null;
				}

				editorMetaData = new EditorMetaData(name, null, null, null, null, false);
				columnMetaData = new ColumnMetaData(false, false, false, false, false, null, false, false);
				columnInfo = new ColumnInfo(name, type, caption, format, unit, combo, comboTblName, comboSelect, comboOrderBy, minval, maxval, regexp, errorMsg, value);
				columnInfo.setTextAlign(textAlign);
				columnInfo.setDisplayCSS(displayCSS);
				columnInfo.setDisplayStyle(displayStyle);
				columnInfo.setTextBoxSize(textBoxSize);
				columnInfo.setTextBoxMaxlength(textBoxMaxlength);
				columnInfo.setTextAreaRows(textAreaRows);
				columnInfo.setTextAreaCols(textAreaCols);

				columnMetaData.setColumnInfo(columnInfo);
				editorMetaData.addColumnMetaData(columnMetaData);
			}
			else
				throw new EJBException("ColumnInfo :" + name + ":" + type + " could not be found");

			return editorMetaData;
		}

		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);
		}
		finally {

			try {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				// if (conn != null) releaseConnection( conn );
			}

			catch (SQLException se) {

				_logger.error(se);
				throw new EJBException(se);
			}
		}
	}

	private EditorMetaData buildEditorMetaData(String name, String type, String userId, Connection conn) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String sql = null;
		EditorMetaData editorMetaData = null;

		try {

		    sql = buildEditorsSql();
			// Execute the query for getting Editor Info
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);

			// Execute the query for Editors
			rs = stmt.executeQuery();

			ResultSetMetaData rsMetaData = rs.getMetaData();
			int size = rsMetaData.getColumnCount();
			Hashtable cols = new Hashtable();

			for (int index = 1; index <= size; index++) {
				cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));
			}

			if (rs.next()) {
				String desc = null;
				String source = null;
				String proc = null;
				String editorType = null;
				boolean initialLookup = false;
				String interceptor = null;
				String sourceName = null;
				String sourceSQL = null;
				String securityManager = null;
				String securityManagerForEdit = null;
				String formType = null;
				boolean listEdit = false;
				int pageSize = 30;
				String viewRole = null;
				String editRole = null;
				boolean addNewPriv = true;
				boolean multiDeletePriv = false;
				boolean customDisplayPriv = true;
				boolean exportPriv = false;
				boolean exactMatchLookup = false;
				boolean hasHelp = false;
				boolean hasChild = false;

				String childActions = null;
				String detailEditorName = null;
				String cacheToRefresh = null;
				int searchColsPerRow = 0;

				boolean schExportPriv = false;
				boolean footerPriv = true;
				boolean cancelPriv = false;
				boolean showHeader = false;
				boolean showPositions = false;

				String addlink = null;
				String addlinkImage = null;
				String deletelinkImage = null;
				String cancellinkImage = null;

				String clientInterceptorForEdit = null;
				String clientInterceptorForList = null;

				String updateSectionTitles = null; // Comma separated values..
				String updateColsPerRow = null; // Comma separated values..
				String envSpringBeanId = null;

				boolean queryPriv = false;
				boolean bulkUpdatePriv = false;
				boolean queryToolAdditionalFilter = false;
				boolean bulkUploadPriv = false;
				String bulkUploadImage = null;
				boolean multiSorting = true;

				sourceName = getString(rs,EditorsTbl._source);
				desc = getString(rs,EditorsTbl._desc);
				initialLookup = cols.containsKey(EditorsTbl._initialLookup) ? (rs.getInt(EditorsTbl._initialLookup) != 0 ? true : false) : false;
				interceptor = cols.containsKey(EditorsTbl._interceptor) ? getString(rs,EditorsTbl._interceptor) : null;
				sourceSQL = cols.containsKey(EditorsTbl._sourceSQL) ? getString(rs,EditorsTbl._sourceSQL) : null;
				securityManager = cols.containsKey(EditorsTbl._securityManager) ? getString(rs,EditorsTbl._securityManager) : null;
				securityManagerForEdit = cols.containsKey(EditorsTbl._securityManagerForEdit) ? getString(rs,EditorsTbl._securityManagerForEdit) : null;
				formType = cols.containsKey(EditorsTbl._formType) ? getString(rs,EditorsTbl._formType) : null;
				listEdit = cols.containsKey(EditorsTbl._isListEdit) ? (rs.getInt(EditorsTbl._isListEdit) != 0 ? true : false) : false;
				pageSize = cols.containsKey(EditorsTbl._pageSize) ? rs.getInt(EditorsTbl._pageSize) : pageSize;
				viewRole = cols.containsKey(EditorsTbl._viewRole) ? getString(rs,EditorsTbl._viewRole) : null;
				editRole = cols.containsKey(EditorsTbl._editRole) ? getString(rs,EditorsTbl._editRole) : null;
				listEdit = cols.containsKey(EditorsTbl._isListEdit) ? (rs.getInt(EditorsTbl._isListEdit) != 0 ? true : false) : false;
				addNewPriv = cols.containsKey(EditorsTbl._addNewPriv) ? (rs.getInt(EditorsTbl._addNewPriv) != 0 ? true : false) : addNewPriv;
				bulkUploadPriv = cols.containsKey(EditorsTbl._bulkUploadPriv) ? (rs.getInt(EditorsTbl._bulkUploadPriv) != 0 ? true : false) : bulkUploadPriv;
				bulkUploadImage = cols.containsKey(EditorsTbl._bulkUploadImage) ? getString(rs,EditorsTbl._bulkUploadImage) : null;
				multiDeletePriv = cols.containsKey(EditorsTbl._multiDeletePriv) ? (rs.getInt(EditorsTbl._multiDeletePriv) != 0 ? true : false) : multiDeletePriv;
				customDisplayPriv = cols.containsKey(EditorsTbl._customDisplayPriv) ? (rs.getInt(EditorsTbl._customDisplayPriv) != 0 ? true : false) : customDisplayPriv;
				exportPriv = cols.containsKey(EditorsTbl._exportPriv) ? (rs.getInt(EditorsTbl._exportPriv) != 0 ? true : false) : exportPriv;
				exactMatchLookup = cols.containsKey(EditorsTbl._isExactMatchLookup) ? (rs.getInt(EditorsTbl._isExactMatchLookup) != 0 ? true : false) : exactMatchLookup;
				hasHelp = cols.containsKey(EditorsTbl._hasHelp) ? (rs.getInt(EditorsTbl._hasHelp) != 0 ? true : false) : hasHelp;
				hasChild = cols.containsKey(EditorsTbl._hasChild) ? (rs.getInt(EditorsTbl._hasChild) != 0 ? true : false) : hasChild;
				childActions = cols.containsKey(EditorsTbl._childActions) ? getString(rs,EditorsTbl._childActions) : null;
				detailEditorName = cols.containsKey(EditorsTbl._detailEditorName) ? getString(rs,EditorsTbl._detailEditorName) : null;
				cacheToRefresh = cols.containsKey(EditorsTbl._cacheToRefresh) ? getString(rs,EditorsTbl._cacheToRefresh) : null;
				searchColsPerRow = cols.containsKey(EditorsTbl._searchColsPerRow) ? rs.getInt(EditorsTbl._searchColsPerRow) : 0;
				schExportPriv = cols.containsKey(EditorsTbl._schExportPriv) ? (rs.getInt(EditorsTbl._schExportPriv) != 0 ? true : false) : schExportPriv;
				footerPriv = cols.containsKey(EditorsTbl._footerPriv) ? (rs.getInt(EditorsTbl._footerPriv) != 0 ? true : false) : footerPriv;
				cancelPriv = cols.containsKey(EditorsTbl._cancelPriv) ? (rs.getInt(EditorsTbl._cancelPriv) != 0 ? true : false) : cancelPriv;
				showHeader = cols.containsKey(EditorsTbl._showHeader) ? (rs.getInt(EditorsTbl._showHeader) != 0 ? true : false) : showHeader;
				showPositions = cols.containsKey(EditorsTbl._showPositions) ? (rs.getInt(EditorsTbl._showPositions) != 0 ? true : false) : showPositions;
				addlink = cols.containsKey(EditorsTbl._addlink) ? getString(rs,EditorsTbl._addlink) : null;
				addlinkImage = cols.containsKey(EditorsTbl._addlinkImage) ? getString(rs,EditorsTbl._addlinkImage) : null;
				deletelinkImage = cols.containsKey(EditorsTbl._deletelinkImage) ? getString(rs,EditorsTbl._deletelinkImage) : null;
				cancellinkImage = cols.containsKey(EditorsTbl._cancellinkImage) ? getString(rs,EditorsTbl._cancellinkImage) : null;

				queryPriv = cols.containsKey(EditorsTbl._queryPriv) ? (rs.getInt(EditorsTbl._queryPriv) != 0 ? true : false) : queryPriv;
				bulkUpdatePriv = cols.containsKey(EditorsTbl._bulkUpdatePriv) ? (rs.getInt(EditorsTbl._bulkUpdatePriv) != 0 ? true : false) : bulkUpdatePriv;
				queryToolAdditionalFilter = cols.containsKey(EditorsTbl._queryToolAdditionalFilter) ? (rs.getInt(EditorsTbl._queryToolAdditionalFilter) != 0 ? true : false) : queryToolAdditionalFilter;

				clientInterceptorForEdit = cols.containsKey(EditorsTbl._clientInterceptorForEdit) ? getString(rs,EditorsTbl._clientInterceptorForEdit) : null;
				clientInterceptorForList = cols.containsKey(EditorsTbl._clientInterceptorForList) ? getString(rs,EditorsTbl._clientInterceptorForList) : null;

				updateSectionTitles = cols.containsKey(EditorsTbl._updateSectionTitles) ? getString(rs,EditorsTbl._updateSectionTitles) : null;
				updateColsPerRow = cols.containsKey(EditorsTbl._updateColsPerRow) ? getString(rs,EditorsTbl._updateColsPerRow) : null;
				envSpringBeanId = cols.containsKey(EditorsTbl._envSpringBeanId) ? getString(rs,EditorsTbl._envSpringBeanId) : null;
				multiSorting = cols.containsKey(EditorsTbl._multiSorting) ? (rs.getInt(EditorsTbl._multiSorting) != 0 ? true : false) : multiSorting;
				
				source = (sourceSQL != null) ? sourceSQL : sourceName;

				rs.close();
				stmt.close();


				// Get the data for Editor_Types
				sql = buildEditorTypesSql();
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, name);
				stmt.setString(2, type);

				rs = stmt.executeQuery();

				if (rs.next()) {
					proc = getString(rs,EditorTypesTbl._proc);
					editorType = getString(rs,EditorTypesTbl._type);
				}

				rs.close();

				editorMetaData = new EditorMetaData(name, desc, type, source, proc, editorType != null);
				editorMetaData.setSourceName(sourceName);
				editorMetaData.setSourceSql(sourceSQL);
				editorMetaData.setColumnAlias(getColumnAlias(conn,sourceSQL));
				editorMetaData.setInitialLookup(initialLookup);
				editorMetaData.setInterceptor(interceptor);
				editorMetaData.setClientInterceptorForEdit(clientInterceptorForEdit);
				editorMetaData.setClientInterceptorForList(clientInterceptorForList);
				editorMetaData.setSecurityManager(securityManager);
				editorMetaData.setSecurityManagerForEdit(securityManagerForEdit);
				editorMetaData.setFormType(formType);
				editorMetaData.setListEdit(listEdit);
				editorMetaData.setPageSize(pageSize);
				editorMetaData.setViewRole(viewRole);
				editorMetaData.setEditRole(editRole);
				editorMetaData.setAddNewPriv(addNewPriv);
				editorMetaData.setBulkUploadPriv(bulkUploadPriv);
				editorMetaData.setBulkUploadImage(bulkUploadImage);
				editorMetaData.setMultiDeletePriv(multiDeletePriv);
				editorMetaData.setCustomDisplayPriv(customDisplayPriv);
				editorMetaData.setExportPriv(exportPriv);
				editorMetaData.setExactMatchLookup(exactMatchLookup);
				editorMetaData.setHasHelp(hasHelp);
				editorMetaData.setHasChild(hasChild);
				editorMetaData.setChildActions(childActions);
				editorMetaData.setDetailEditorName(detailEditorName);
				editorMetaData.setCacheToRefresh(cacheToRefresh);
				editorMetaData.setSearchColsPerRow(searchColsPerRow);
				editorMetaData.setScheduledExportPriv(schExportPriv);
				editorMetaData.setFooterPriv(footerPriv);
				editorMetaData.setCancelPriv(cancelPriv);
				editorMetaData.setShowHeader(showHeader);
				editorMetaData.setShowPositions(showPositions);
				editorMetaData.setAddlink(addlink);
				editorMetaData.setAddlinkImage(addlinkImage);
				editorMetaData.setDeletelinkImage(deletelinkImage);
				editorMetaData.setCancellinkImage(cancellinkImage);
				editorMetaData.setQueryPriv(queryPriv);
				editorMetaData.setBulkUpdatePriv(bulkUpdatePriv);
				editorMetaData.setQueryToolAdditionalFilter(queryToolAdditionalFilter);

				editorMetaData.setUpdateSectionTitles(updateSectionTitles);
				editorMetaData.setUpdateColsPerRow(updateColsPerRow);
				editorMetaData.setEnvSpringBeanId(envSpringBeanId);
				editorMetaData.setMultiSorting(multiSorting);
			
			}
			else {

				throw new EJBException("MetaData for Editor :" + name + ":" + type + " could not be found");
			}
			editorMetaData.setColumnsMetaData(buildColumnsMetaData(name, type, userId, conn));
			editorMetaData.setEditorFilters(buildEditorFilters(name, conn));
			return editorMetaData;

		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);
		}
		finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

	}

	private Vector buildColumnsMetaData(String name, String type, String userId, Connection conn) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		Vector columns = new Vector();
		try {
			String sql = null;
			sql = buildColumnsSql();

			// Execute the query for Editor_Columns & Columns
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			rs = stmt.executeQuery();

			Hashtable columnNames = new Hashtable();
			int colIndex = 0;
			ColumnMetaData columnMetaData = null;
			ColumnInfo columnInfo = null;
			String colName = null;
			String colType = null;
			String displayGroup = null;
			boolean displayable = false;
			boolean aggregatable = false;
			boolean searchable = false;
			String searchGroup = null;
			int searchOrderSeq = 0;
			boolean searchableMandatory = false;
			String searchMandatoryGroup = null;

			String updateGroup = null;
			boolean updatable = false;
			boolean linkable = false;
			boolean key = false;
			boolean editKey = false;
			boolean keyDisplayable = false;
			boolean nullable = true;
			String autoSeqName = null;
			boolean secured = false;
			boolean combocached = false;
			String expression = null;
			String caption = null;
			String format = null;
			String unit = null;
			boolean combo = false;
			boolean comboSelectTag = false;
			String comboTblName = null;
			String comboSelect = null;
			String comboOrderBy = null;
			double minval = 0.0;
			double maxval = -1.0;
			String regexp = null;
			String value = null;
			String textAlign = null;
			String displayCSS = null;
			String displayStyle = null;
			String columnSize = null;
			String jsEvents = null;
			String errorMsg = null;
			boolean comboOvd = false;
			String comboTblNameOvd = null;
			String comboSelectOvd = null;
			String comboOrderByOvd = null;
			String textAlignOvd = null;
			String displayCSSOvd = null;
			String displayStyleOvd = null;
			String columnSizeOvd = null;
			Integer textBoxSize = null;
			Integer textBoxMaxlength = null;
			Integer textAreaRows = null;
			Integer textAreaCols = null;
			String columnDecoratorProperty = null;
			String columnBeanProperty = null;
			String editRegExp = null;
			String editErrorMsg = null;
			String captionOvd = null;
			String jsEventsOvd = null;
			boolean indexedUpperCaseData = false;

			ResultSetMetaData rsMetaData = rs.getMetaData();
			int size = rsMetaData.getColumnCount();
			Hashtable cols = new Hashtable();

			// Environment.getInstance( getSubSystem() ).getLogFileMgr( _module ).logInfo ("After getting columns RS" );
			for (int index = 1; index <= size; index++) {
				cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));
			}

			while (rs.next()) {
				colName = getString(rs,ColumnsTbl._name);
				colType = getString(rs,ColumnsTbl._type);
				caption = getString(rs,ColumnsTbl._caption);

				displayGroup = cols.containsKey(EditorColumnsTbl._displayable) ? getString(rs,EditorColumnsTbl._displayable) : null;
				displayable = "1".equalsIgnoreCase(displayGroup) || "2".equalsIgnoreCase(displayGroup) || "3".equalsIgnoreCase(displayGroup);
				aggregatable = cols.containsKey(EditorColumnsTbl._aggregatable) ? (rs.getInt(EditorColumnsTbl._aggregatable) != 0 ? true : false) : false;
				searchGroup = cols.containsKey(EditorColumnsTbl._searchable) ? getString(rs,EditorColumnsTbl._searchable) : null;
				searchable = "1".equalsIgnoreCase(searchGroup) || "2".equalsIgnoreCase(searchGroup) || "3".equalsIgnoreCase(searchGroup);
				searchMandatoryGroup = cols.containsKey(EditorColumnsTbl._searchableMandatory) ? getString(rs,EditorColumnsTbl._searchableMandatory) : null;
				searchableMandatory = "1".equalsIgnoreCase(searchMandatoryGroup);

				searchOrderSeq = cols.containsKey(EditorColumnsTbl._columnSearchSeq) ? rs.getInt(EditorColumnsTbl._columnSearchSeq) : 0;
				linkable = cols.containsKey(EditorColumnsTbl._linkable) ? (rs.getInt(EditorColumnsTbl._linkable) != 0 ? true : false) : false;
				expression = cols.containsKey(EditorColumnsTbl._expr) ? getString(rs,EditorColumnsTbl._expr) : null;
				format = cols.containsKey(ColumnsTbl._format) ? getString(rs,ColumnsTbl._format) : null;
				unit = cols.containsKey(ColumnsTbl._unit) ? getString(rs,ColumnsTbl._unit) : null;
				combo = cols.containsKey(ColumnsTbl._combo) ? (rs.getInt(ColumnsTbl._combo) != 0 ? true : false) : false;
				comboTblName = cols.containsKey(ColumnsTbl._comboTblName) ? getString(rs,ColumnsTbl._comboTblName) : null;
				comboSelect = cols.containsKey(ColumnsTbl._comboSelect) ? getString(rs,ColumnsTbl._comboSelect) : null;
				comboOrderBy = cols.containsKey(ColumnsTbl._comboOrderBy) ? getString(rs,ColumnsTbl._comboOrderBy) : null;
				minval = cols.containsKey(ColumnsTbl._minval) ? rs.getDouble(ColumnsTbl._minval) : 0;
				maxval = cols.containsKey(ColumnsTbl._maxval) ? rs.getDouble(ColumnsTbl._maxval) : -1.0;
				regexp = cols.containsKey(ColumnsTbl._regexp) ? getString(rs,ColumnsTbl._regexp) : null;
				errorMsg = cols.containsKey(ColumnsTbl._errormsg) ? getString(rs,ColumnsTbl._errormsg) : null;
				value = cols.containsKey(ColumnsTbl._value) ? getString(rs,ColumnsTbl._value) : null;
				textAlign = cols.containsKey(ColumnsTbl._displayAlign) ? getString(rs,ColumnsTbl._displayAlign) : null;
				displayCSS = cols.containsKey(ColumnsTbl._displayCSS) ? getString(rs,ColumnsTbl._displayCSS) : null;
				displayStyle = cols.containsKey(ColumnsTbl._displayStyle) ? getString(rs,ColumnsTbl._displayStyle) : null;
				columnSize = cols.containsKey(ColumnsTbl._size) ? getString(rs,ColumnsTbl._size) : null;
				jsEvents = cols.containsKey(ColumnsTbl._jsEvents) ? getString(rs,ColumnsTbl._jsEvents) : null;

				key = cols.containsKey(EditorColumnsTbl._key) ? (rs.getInt(EditorColumnsTbl._key) != 0 ? true : false) : false;
				secured = cols.containsKey(EditorColumnsTbl._secured) ? (rs.getInt(EditorColumnsTbl._secured) != 0 ? true : false) : false;
				combocached = cols.containsKey(EditorColumnsTbl._comboCached) ? (rs.getInt(EditorColumnsTbl._comboCached) != 0 ? true : false) : false;
				comboSelectTag = cols.containsKey(EditorColumnsTbl._comboSelectTag) ? (rs.getInt(EditorColumnsTbl._comboSelectTag) == 1 ? true : false) : false;
				comboOvd = cols.containsKey(EditorColumnsTbl._comboOvd) ? (rs.getInt(EditorColumnsTbl._comboOvd) != 0 ? true : false) : false;
				comboTblNameOvd = cols.containsKey(EditorColumnsTbl._comboTblNameOvd) ? getString(rs,EditorColumnsTbl._comboTblNameOvd) : null;
				comboSelectOvd = cols.containsKey(EditorColumnsTbl._comboSelectOvd) ? getString(rs,EditorColumnsTbl._comboSelectOvd) : null;
				comboOrderByOvd = cols.containsKey(EditorColumnsTbl._comboOrderByOvd) ? getString(rs,EditorColumnsTbl._comboOrderByOvd) : null;
				textAlignOvd = cols.containsKey(EditorColumnsTbl._displayAlignOvd) ? getString(rs,EditorColumnsTbl._displayAlignOvd) : null;
				displayCSSOvd = cols.containsKey(EditorColumnsTbl._displayCSSOvd) ? getString(rs,EditorColumnsTbl._displayCSSOvd) : null;
				displayStyleOvd = cols.containsKey(EditorColumnsTbl._displayStyleOvd) ? getString(rs,EditorColumnsTbl._displayStyleOvd) : null;
				columnSizeOvd = cols.containsKey(EditorColumnsTbl._sizeOvd) ? getString(rs,EditorColumnsTbl._sizeOvd) : null;
				captionOvd = cols.containsKey(EditorColumnsTbl._captionOvd) ? getString(rs,EditorColumnsTbl._captionOvd) : null;
				jsEventsOvd = cols.containsKey(EditorColumnsTbl._jsEventsOvd) ? getString(rs,EditorColumnsTbl._jsEventsOvd) : null;
				columnDecoratorProperty = cols.containsKey(EditorColumnsTbl._columnDecoratorProperty) ? getString(rs,EditorColumnsTbl._columnDecoratorProperty) : null;
				columnBeanProperty = cols.containsKey(EditorColumnsTbl._columnBeanProperty) ? getString(rs,EditorColumnsTbl._columnBeanProperty) : null;
				indexedUpperCaseData = cols.containsKey(EditorColumnsTbl._indexedUpperCaseData) ? (rs.getInt(EditorColumnsTbl._indexedUpperCaseData) != 0 ? true : false) : false;

				combo = (comboOvd) ? comboOvd : combo;
				comboTblName = (comboTblNameOvd != null) ? comboTblNameOvd : comboTblName;
				comboSelect = (comboSelectOvd != null) ? comboSelectOvd : comboSelect;
				comboOrderBy = (comboOrderByOvd != null) ? comboOrderByOvd : comboOrderBy;
				textAlign = (textAlignOvd != null) ? textAlignOvd : textAlign;
				displayCSS = (displayCSSOvd != null) ? displayCSSOvd : displayCSS;
				displayStyle = (displayStyleOvd != null) ? displayStyleOvd : displayStyle;
				columnSize = (columnSizeOvd != null) ? columnSizeOvd : columnSize;
				caption = (!StrUtl.isEmptyTrimmed(captionOvd)) ? captionOvd : caption;
				jsEvents = (!StrUtl.isEmptyTrimmed(jsEventsOvd)) ? jsEventsOvd : jsEvents;

				if (!StrUtl.isEmptyTrimmed(columnSize)) {
					String[] minmax = columnSize.split(",");
					textBoxSize = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textBoxMaxlength = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
					textAreaCols = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textAreaRows = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
				}
				else {
					textBoxSize = null;
					textBoxMaxlength = null;
					textAreaRows = null;
					textAreaCols = null;
				}

				columnMetaData = new ColumnMetaData(aggregatable, searchable, displayable, updatable, linkable, expression, key, searchableMandatory);
				columnMetaData.setSearchGroup(searchGroup);
				columnMetaData.setSearchOrderSeq(searchOrderSeq);
				columnMetaData.setDisplayGroup(displayGroup);
				columnMetaData.setSearchMandatoryGroup(searchMandatoryGroup);
				columnMetaData.setSecured(secured);
				columnMetaData.setComboCached(combocached);
				columnMetaData.setComboSelectTag(comboSelectTag);
				columnMetaData.setColumnDecoratorProperty(columnDecoratorProperty);
				columnMetaData.setColumnBeanProperty(columnBeanProperty);
				columnMetaData.setIndexedUpperCaseData(indexedUpperCaseData);

				columnInfo = new ColumnInfo(colName, colType, caption, format, unit, combo, comboTblName, comboSelect, comboOrderBy, minval, maxval, regexp, errorMsg, value);
				columnInfo.setTextAlign(textAlign);
				columnInfo.setDisplayCSS(displayCSS);
				columnInfo.setDisplayStyle(displayStyle);
				columnInfo.setTextBoxSize(textBoxSize);
				columnInfo.setTextBoxMaxlength(textBoxMaxlength);
				columnInfo.setTextAreaRows(textAreaRows);
				columnInfo.setTextAreaCols(textAreaCols);
				if (!StrUtl.isEmptyTrimmed(jsEvents)) {
					JSONObject jsonObject = StrUtl.isEmptyTrimmed(jsEvents) ? new JSONObject() : JSONObject.fromObject(jsEvents);
					if(jsonObject != null){
						HashMap<String,String> _jsEvents = new HashMap<String,String>();
						String event = null;
						for (Iterator iterator = jsonObject.keys(); iterator.hasNext();) {
							event = (String) iterator.next();
							_jsEvents.put(event, jsonObject.getString(event));
						}
						columnInfo.setJSEvents(_jsEvents);
					}
				}

				columnMetaData.setColumnInfo(columnInfo);
				columns.add(columnMetaData);
				columnNames.put(columnMetaData.getName(), new Integer(colIndex));
				colIndex++;
			}

			// Close the resultset for Columns
			rs.close();
			stmt.close();

			// Set the columns that are updatable and if it is a key column
			// Irrespective of whether the editor is editable or not, we do the sql.
			// If there are no rows then the editor is editable

			// Build the sql for Editor_Type_Columns
			sql = buildEditorTypeColumnsSql();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, type);

			rs = stmt.executeQuery();
			rsMetaData = rs.getMetaData();
			size = rsMetaData.getColumnCount();
			cols = new Hashtable();

			for (int index = 1; index <= size; index++)
				cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));

			while (rs.next()) {

				colName = getString(rs,ColumnsTbl._name);

				// If a row exists for this column in the EditorTypeColumns table then the column is updatable in
				// some form in the edit screen. Key columns can never be changed but they will appear in the
				// edit screen either as labels or as just hidden variables

				// edit Key
				editKey = cols.containsKey(EditorTypeColumnsTbl._editKey) ? (rs.getInt(EditorTypeColumnsTbl._editKey) != 0 ? true : false) : false;
				keyDisplayable = cols.containsKey(EditorTypeColumnsTbl._editKeyDisplayable) ? (rs.getInt(EditorTypeColumnsTbl._editKeyDisplayable) != 0 ? true : false) : false;

				updateGroup = cols.containsKey(EditorTypeColumnsTbl._updatable) ? getString(rs,EditorTypeColumnsTbl._updatable) : null;
				updatable = "1".equalsIgnoreCase(updateGroup) || "2".equalsIgnoreCase(updateGroup) || "3".equalsIgnoreCase(updateGroup) || "4".equalsIgnoreCase(updateGroup) || "5".equalsIgnoreCase(updateGroup) || "6".equalsIgnoreCase(updateGroup) || "7".equalsIgnoreCase(updateGroup);

				// Nullable - default is true
				nullable = cols.containsKey(EditorTypeColumnsTbl._nullable) ? (rs.getInt(EditorTypeColumnsTbl._nullable) != 0 ? true : false) : true;

				// Column Sequence Table Name
				autoSeqName = cols.containsKey(EditorTypeColumnsTbl._autoSeqName) ? getString(rs,EditorTypeColumnsTbl._autoSeqName) : null;
				editRegExp = cols.containsKey(EditorTypeColumnsTbl._regExp) ? getString(rs,EditorTypeColumnsTbl._regExp) : null;
				editErrorMsg = cols.containsKey(EditorTypeColumnsTbl._errorMsg) ? getString(rs,EditorTypeColumnsTbl._errorMsg) : null;

				if (columnNames.containsKey(colName)) {
					// Column Exists. Hence update the columnMetaData
					columnMetaData = (ColumnMetaData) columns.elementAt(((Integer) columnNames.get(colName)).intValue());
				}
				else {
					colType = getString(rs,ColumnsTbl._type);
					caption = getString(rs,ColumnsTbl._caption);
					format = cols.containsKey(ColumnsTbl._format) ? getString(rs,ColumnsTbl._format) : null;
					unit = cols.containsKey(ColumnsTbl._unit) ? getString(rs,ColumnsTbl._unit) : null;
					combo = cols.containsKey(ColumnsTbl._combo) ? (rs.getInt(ColumnsTbl._combo) != 0 ? true : false) : false;
					comboTblName = cols.containsKey(ColumnsTbl._comboTblName) ? getString(rs,ColumnsTbl._comboTblName) : null;
					comboSelect = cols.containsKey(ColumnsTbl._comboSelect) ? getString(rs,ColumnsTbl._comboSelect) : null;
					comboOrderBy = cols.containsKey(ColumnsTbl._comboOrderBy) ? getString(rs,ColumnsTbl._comboOrderBy) : null;
					minval = cols.containsKey(ColumnsTbl._minval) ? rs.getDouble(ColumnsTbl._minval) : 0;
					maxval = cols.containsKey(ColumnsTbl._maxval) ? rs.getDouble(ColumnsTbl._maxval) : -1.0;
					regexp = cols.containsKey(ColumnsTbl._regexp) ? getString(rs,ColumnsTbl._regexp) : null;
					errorMsg = cols.containsKey(ColumnsTbl._errormsg) ? getString(rs,ColumnsTbl._errormsg) : null;
					value = cols.containsKey(ColumnsTbl._value) ? getString(rs,ColumnsTbl._value) : null;
					textAlign = cols.containsKey(ColumnsTbl._displayAlign) ? getString(rs,ColumnsTbl._displayAlign) : null;
					displayCSS = cols.containsKey(ColumnsTbl._displayCSS) ? getString(rs,ColumnsTbl._displayCSS) : null;
					displayStyle = cols.containsKey(ColumnsTbl._displayStyle) ? getString(rs,ColumnsTbl._displayStyle) : null;
					columnSize = cols.containsKey(ColumnsTbl._size) ? getString(rs,ColumnsTbl._size) : null;
					jsEvents = cols.containsKey(ColumnsTbl._jsEvents) ? getString(rs,ColumnsTbl._jsEvents) : null;
					if (!StrUtl.isEmptyTrimmed(columnSize)) {
						String[] minmax = columnSize.split(",");
						textBoxSize = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
						textBoxMaxlength = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
						textAreaCols = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
						textAreaRows = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
					}
					else {
						textBoxSize = null;
						textBoxMaxlength = null;
						textAreaRows = null;
						textAreaCols = null;
					}
					// Create a new column metadata in the end
					// ColumnMetaData( aggregatable, searchable, displayable, updatable, linkable, expression, key, searchableMandatory )
					columnMetaData = new ColumnMetaData(false, false, false, false, false, null, false, false);
					columnInfo = new ColumnInfo(colName, colType, caption, format, unit, combo, comboTblName, comboSelect, comboOrderBy, minval, maxval, regexp, errorMsg, value);
					columnInfo.setTextAlign(textAlign);
					columnInfo.setDisplayCSS(displayCSS);
					columnInfo.setDisplayStyle(displayStyle);
					columnInfo.setTextBoxSize(textBoxSize);
					columnInfo.setTextBoxMaxlength(textBoxMaxlength);
					columnInfo.setTextAreaRows(textAreaRows);
					columnInfo.setTextAreaCols(textAreaCols);
					if (!StrUtl.isEmptyTrimmed(jsEvents)) {
						JSONObject jsonObject = StrUtl.isEmptyTrimmed(jsEvents) ? new JSONObject() : JSONObject.fromObject(jsEvents);
						if(jsonObject != null){
							HashMap<String,String> _jsEvents = new HashMap<String,String>();
							String event = null;
							for (Iterator iterator = jsonObject.keys(); iterator.hasNext();) {
								event = (String) iterator.next();
								_jsEvents.put(event, jsonObject.getString(event));
							}
							columnInfo.setJSEvents(_jsEvents);
						}
					}

					columnMetaData.setColumnInfo(columnInfo);
					columns.add(columnMetaData);
				}
				columnMetaData.setUpdatable(updatable);
				columnMetaData.setUpdateGroup(updateGroup);
				columnMetaData.setEditKey(editKey);
				columnMetaData.setEditKeyDisplayable(keyDisplayable);
				columnMetaData.setNullable(nullable);
				columnMetaData.setAutoSequenceName(autoSeqName);
				columnMetaData.setEditRegExp(editRegExp != null ? editRegExp : columnMetaData.getRegexp());
				columnMetaData.setEditErrorMsg(editErrorMsg != null ? editErrorMsg : columnMetaData.getErrorMsg());

			}
			// Close the resultset for EditorTypeColumns
			rs.close();
		}
		finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

		return columns;
	}

	private String getString(ResultSet rs,String columnName) throws SQLException{
		//SQLite null vs empty string.
		return (rs.getObject(columnName) != null)?rs.getString(columnName):null; 
		
	}
	private Vector buildEditorFilters(String name, Connection conn) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		Vector filters = new Vector();
		try {
            String sql = buildFiltersSql();
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, name);

			rs = stmt.executeQuery();

			String filterName = null;
			String filterDesc = null;
			String filterSql = null;

			EditorFilter editorFilter = null;
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int size = rsMetaData.getColumnCount();
			Hashtable cols = new Hashtable();

			for (int index = 1; index <= size; index++) {
				cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));
			}

			while (rs.next()) {
				filterName = cols.containsKey(EditorFiltersTbl._editorFilterName) ? getString(rs,EditorFiltersTbl._editorFilterName) : null;
				filterDesc = cols.containsKey(EditorFiltersTbl._editorFilterDesc) ? getString(rs,EditorFiltersTbl._editorFilterDesc) : null;
				filterSql = cols.containsKey(EditorFiltersTbl._editorFilterSql) ? getString(rs,EditorFiltersTbl._editorFilterSql) : null;
				editorFilter = new EditorFilter(filterName, filterDesc, filterSql);
				filters.add(editorFilter);
			}
		}
		finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

		return filters;
	}

	protected EditorMetaData buildUserSearchFilters(EditorMetaData metadata, String name, String userGroup, String userId, String componentType, boolean configRole) throws SQLException {
		return metadata;
	}

	protected EditorMetaData buildUserTemplates(EditorMetaData metadata, String name, String userGroup, String userId, String componentType, boolean configRole) throws SQLException {
		return metadata;
	}

	private String buildEditorLabelsSql() {
		String sql = "SELECT " + EditorsTbl._name + "," + EditorsTbl._desc + " FROM " + EditorsTbl._tblName + " ORDER BY " + EditorsTbl._name;

		return sql;
	}

	private String buildColumnLabelsSql() {
		String sql = "SELECT " + ColumnsTbl._name + "," + ColumnsTbl._caption + " FROM " + ColumnsTbl._tblName + " ORDER BY " + ColumnsTbl._name;

		return sql;
	}

	private String buildColumnErrorSql() {
		String sql = "SELECT " + ColumnsTbl._name + "," + ColumnsTbl._errormsg + " FROM " + ColumnsTbl._tblName + " WHERE " + ColumnsTbl._errormsg + " IS NOT NULL " + " ORDER BY " + ColumnsTbl._name;

		return sql;
	}

	private String buildMenuLabelsSql() {
		String sql = "SELECT " + MenuTbl._id + "," + MenuTbl._label + " FROM " + MenuTbl._tblName + " ORDER BY " + MenuTbl._id;

		return sql;
	}

	private String buildErrorLabelsSql() {
		String sql = "SELECT " + ErrorMessageTbl._number + " || " + ErrorMessageTbl._key + "," + ErrorMessageTbl._message + " FROM " + ErrorMessageTbl._tblName + " ORDER BY " + ErrorMessageTbl._number + "," + ErrorMessageTbl._key;

		return sql;
	}

	private String buildEditorsSql() {
		String sql = "SELECT *" + " FROM " + EditorsTbl._tblName + " WHERE " + EditorsTbl._name + "= ?";

		return sql;
	}

	private String buildFiltersSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT *");
		sql.append(" FROM " + EditorFiltersTbl._tblName);
		sql.append(" WHERE " + EditorFiltersTbl._editorName + "= ? ");
		sql.append(" ORDER BY " + EditorFiltersTbl._editorFilterName);

		return sql.toString();
	}

	private String buildColumnsSql() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT " + EditorColumnsTbl._tblName + ".*, " + ColumnsTbl._tblName + ".*");
		sql.append(" FROM " + EditorColumnsTbl._tblName + ", " + ColumnsTbl._tblName);
		sql.append(" WHERE " + EditorColumnsTbl._tblName + "." + EditorColumnsTbl._columnName + "=" + ColumnsTbl._tblName + "." + ColumnsTbl._name + " AND ");
		sql.append(EditorColumnsTbl._tblName + "." + EditorColumnsTbl._editorName + "= ? ");
		sql.append(" ORDER BY " + EditorColumnsTbl._tblName + "." + EditorColumnsTbl._seq);

		return sql.toString();
	}

	private String buildEditorTypesSql() {
		String sql = "SELECT *" + " FROM " + EditorTypesTbl._tblName + " WHERE " + EditorTypesTbl._name + "= ? AND " + EditorTypesTbl._type + "= ?";
		return sql;
	}

	private String buildEditorTypeColumnsSql() {

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT " + EditorTypeColumnsTbl._tblName + ".*," + ColumnsTbl._tblName + ".*");
		sql.append(" FROM " + EditorTypeColumnsTbl._tblName + "," + ColumnsTbl._tblName);
		sql.append(" WHERE " + EditorTypeColumnsTbl._tblName + "." + EditorTypeColumnsTbl._columnName + "=" + ColumnsTbl._tblName + "." + ColumnsTbl._name);
		sql.append(" AND " + EditorTypeColumnsTbl._editorName + "= ? ");
		sql.append(" AND " + EditorTypeColumnsTbl._type + "= ? ");

		return sql.toString();
	}

	public String buildUserEditorColumnsSql() {
		return "SELECT " + UserEditorColumnsTbl._columnName + " , " + UserEditorColumnsTbl._sortOrder + " , " + _sqlNullFunction + "(" + UserEditorColumnsTbl._sortOrderSeq + ",-1) as " + UserEditorColumnsTbl._sortOrderSeq + " , " + UserEditorColumnsTbl._seq + " FROM "
				+ UserEditorColumnsTbl._tblName + " WHERE " + UserEditorColumnsTbl._editorName + "= ? AND " + UserEditorColumnsTbl._userId + "= ? union " + " SELECT " + UserEditorColumnsTbl._columnName + " , " + UserEditorColumnsTbl._sortOrder + " , "
				+ _sqlNullFunction + "(" + UserEditorColumnsTbl._sortOrderSeq + ",-1) as " + UserEditorColumnsTbl._sortOrderSeq + " , " + UserEditorColumnsTbl._seq + " FROM " + UserEditorColumnsTbl._tblName + " WHERE " + UserEditorColumnsTbl._editorName + "= ? AND "
				+ UserEditorColumnsTbl._userId + "='ALL'" + " AND not exists ( SELECT 1" + " FROM " + UserEditorColumnsTbl._tblName + " WHERE " + UserEditorColumnsTbl._editorName + "= ? AND " + UserEditorColumnsTbl._userId + "= ? )" + " ORDER BY "
				+ UserEditorColumnsTbl._seq;
		/*
		return
		        "SELECT "+
		        UserEditorColumnsTbl._columnName + " , " +
		        UserEditorColumnsTbl._sortOrder + " , " +
		        "NVL( "+ UserEditorColumnsTbl._sortOrderSeq + ",-1) as "+UserEditorColumnsTbl._sortOrderSeq +
		        " FROM " +
		        UserEditorColumnsTbl._tblName +
		        " WHERE " +
		        UserEditorColumnsTbl._editorName +"='" + name + "' AND " +
		        UserEditorColumnsTbl._userId + "='" + userId +
		        "' order by " + UserEditorColumnsTbl._seq;
		*/
	}

	private void updateEditorsTbl(Connection conn, EditorMetaData metadata) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorsTbl._tblName + " SET " + EditorsTbl._desc + "=?, " + EditorsTbl._source + "=?  " + "WHERE " + EditorsTbl._name + "=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, metadata.getDesc());
			pstmt.setString(2, metadata.getSource());
			pstmt.setString(3, metadata.getName());

			pstmt.executeUpdate();
		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateEditorTypesTbl(Connection conn, EditorMetaData metadata) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorTypesTbl._tblName + " SET " + EditorTypesTbl._proc + "=? " + "WHERE " + EditorTypesTbl._name + "=? AND " + EditorTypesTbl._type + "=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, metadata.getProcedure());
			pstmt.setString(2, metadata.getName());
			pstmt.setString(3, metadata.getType());

			pstmt.executeUpdate();
		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateEditorColumnsTbl(Connection conn, EditorMetaData metadata) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorColumnsTbl._tblName + " SET " + EditorColumnsTbl._aggregatable + "=?, " + EditorColumnsTbl._searchable + "=?, " + EditorColumnsTbl._displayable + "=?, " + EditorColumnsTbl._linkable + "=?, " + EditorColumnsTbl._expr + "=?, " + EditorColumnsTbl._seq
					+ "=?, " + EditorColumnsTbl._key + "=? " + EditorColumnsTbl._searchableMandatory + "=? " + "WHERE " + EditorColumnsTbl._editorName + "=? AND " + EditorColumnsTbl._columnName + "=?";

			pstmt = conn.prepareStatement(sql);

			ColumnMetaData columnMetaData = null;
			Vector columnsMetaData = metadata.getColumnsMetaData();
			int size = columnsMetaData == null ? 0 : columnsMetaData.size();

			for (int index = 0; index < size; index++) {

				columnMetaData = (ColumnMetaData) columnsMetaData.elementAt(index);

				pstmt.setInt(1, columnMetaData.isAggregatable() ? 1 : 0);
				pstmt.setInt(2, columnMetaData.isSearchable() ? 1 : 0);
				pstmt.setInt(3, columnMetaData.isDisplayable() ? 1 : 0);
				pstmt.setInt(4, columnMetaData.isLinkable() ? 1 : 0);
				pstmt.setString(5, columnMetaData.getExpression());
				pstmt.setInt(6, index + 1);
				pstmt.setInt(7, columnMetaData.isKey() ? 1 : 0);
				pstmt.setInt(8, columnMetaData.isSearchableMandatory() ? 1 : 0);
				pstmt.setString(9, metadata.getName());
				pstmt.setString(10, columnMetaData.getName());

				pstmt.addBatch();
			}

			pstmt.executeBatch();

		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateEditorTypeColumnsTbl(Connection conn, EditorMetaData metadata) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorTypeColumnsTbl._tblName + " SET " + EditorTypeColumnsTbl._editKey + "=?, " + EditorTypeColumnsTbl._editKeyDisplayable + "=?, " + EditorTypeColumnsTbl._nullable + "=?, " + EditorTypeColumnsTbl._autoSeqName + "=?  " + "WHERE "
					+ EditorTypeColumnsTbl._editorName + "=? AND " + EditorTypeColumnsTbl._type + "=? AND " + EditorTypeColumnsTbl._columnName + "=?";

			pstmt = conn.prepareStatement(sql);

			ColumnMetaData columnMetaData = null;
			Vector columnsMetaData = metadata.getColumnsMetaData();
			int size = columnsMetaData == null ? 0 : columnsMetaData.size();

			for (int index = 0; index < size; index++) {

				columnMetaData = (ColumnMetaData) columnsMetaData.elementAt(index);

				pstmt.setInt(1, columnMetaData.isEditKey() ? 1 : 0);
				pstmt.setInt(2, columnMetaData.isEditKeyDisplayable() ? 1 : 0);
				pstmt.setInt(3, columnMetaData.isNullable() ? 1 : 0);
				if (columnMetaData.getAutoSequenceName() == null)
					pstmt.setNull(4, java.sql.Types.VARCHAR);
				else
					pstmt.setString(4, columnMetaData.getAutoSequenceName());
				pstmt.setString(5, metadata.getName());
				pstmt.setString(6, metadata.getType());
				pstmt.setString(7, columnMetaData.getName());

				pstmt.addBatch();
			}

			pstmt.executeBatch();

		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateColumnsTbl(Connection conn, EditorMetaData metadata) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + ColumnsTbl._tblName + " SET " + ColumnsTbl._type + "=?, " + ColumnsTbl._caption + "=?, " + ColumnsTbl._format + "=?, " + ColumnsTbl._unit + "=?, " + ColumnsTbl._combo + "=?, " + ColumnsTbl._comboTblName + "=?, " + ColumnsTbl._comboSelect + "=?, "
					+ ColumnsTbl._comboOrderBy + "=?, " + ColumnsTbl._minval + "=?, " + ColumnsTbl._maxval + "=?, " + ColumnsTbl._regexp + "=?, " + ColumnsTbl._errormsg + "=?, " + ColumnsTbl._value + "=? " + "WHERE " + ColumnsTbl._name + "=?";

			pstmt = conn.prepareStatement(sql);

			ColumnMetaData columnMetaData = null;
			Vector columnsMetaData = metadata.getColumnsMetaData();
			int size = columnsMetaData == null ? 0 : columnsMetaData.size();

			for (int index = 0; index < size; index++) {

				columnMetaData = (ColumnMetaData) columnsMetaData.elementAt(index);

				pstmt.setString(1, columnMetaData.getTypeName());
				pstmt.setString(2, columnMetaData.getCaption());
				pstmt.setString(3, columnMetaData.getFormat());
				pstmt.setString(4, columnMetaData.getUnit());
				pstmt.setInt(5, columnMetaData.isCombo() ? 1 : 0);
				pstmt.setString(6, columnMetaData.getComboTblName());
				pstmt.setString(7, columnMetaData.getComboSelect());
				pstmt.setString(8, columnMetaData.getComboOrderBy());
				pstmt.setDouble(9, columnMetaData.getMinval());
				pstmt.setDouble(10, columnMetaData.getMaxval());
				pstmt.setString(11, columnMetaData.getRegexp());
				pstmt.setString(12, columnMetaData.getErrorMsg());
				pstmt.setString(13, columnMetaData.getValue());
				pstmt.setString(14, columnMetaData.getName());
				pstmt.addBatch();
			}

			pstmt.executeBatch();

		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateUserEditorColumnsTbl(String name, String userId, Connection conn, EditorMetaData metadata) throws SQLException {

		// Check if the table exists

		DatabaseMetaData dbMetaData = conn.getMetaData();
		String[] table = { "TABLE" };
		ResultSet rs = dbMetaData.getTables(null, null, UserEditorColumnsTbl._tblName, table);
		boolean tableExists = rs.next();
		rs.close();

		// Delete and insert all the columns for this user
		if (tableExists) {

			PreparedStatement pstmt = null;
			String sql = null;

			try {

				// Delete all customized columns
				sql = "DELETE FROM " + UserEditorColumnsTbl._tblName + " WHERE " + UserEditorColumnsTbl._editorName + "=? AND " + UserEditorColumnsTbl._userId + "=?";

				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, name);
				pstmt.setString(2, userId);
				pstmt.executeUpdate();
			}
			finally {
				if (pstmt != null)
					pstmt.close();
			}

			try {
				// Insert all user specific columns from _columnMetaData
				sql = "INSERT INTO " + UserEditorColumnsTbl._tblName + "(" + UserEditorColumnsTbl._editorName + "," + UserEditorColumnsTbl._columnName + "," + UserEditorColumnsTbl._userId + "," + UserEditorColumnsTbl._sortOrder + "," + UserEditorColumnsTbl._seq + ","
						+ UserEditorColumnsTbl._sortOrderSeq + ") VALUES ( ?, ?, ?, ?,?,?)";

				ColumnMetaData columnMetaData = null;
				Vector columnsMetaData = metadata.getColumnsMetaData();
				int size = columnsMetaData == null ? 0 : columnsMetaData.size();

				pstmt = conn.prepareStatement(sql);

				for (int index = 0; index < size; index++) {

					columnMetaData = (ColumnMetaData) columnsMetaData.elementAt(index);

					if (columnMetaData.isDisplayable() && columnMetaData.getType() != ColumnDataType._CDT_LINK) {

						pstmt.setString(1, name);
						pstmt.setString(2, columnMetaData.getName());
						pstmt.setString(3, userId);
						pstmt.setInt(4, columnMetaData.getOrdering());
						pstmt.setInt(5, index + 1);
						pstmt.setInt(6, columnMetaData.getSortOrderSeq());
						pstmt.addBatch();

					}
				}
				pstmt.executeBatch();

			}
			finally {

				if (pstmt != null)
					pstmt.close();
			}
		}
	}

	private Connection getConnection() throws SQLException, NamingException {

		return getEnvironment().getDbPoolMgr().getConnection();

	}

	private void releaseConnection(Connection conn) throws SQLException {

		getEnvironment().getDbPoolMgr().releaseConnection(conn);

	}

	private HashMap<String,String> getColumnAlias(Connection conn,String sql) throws SQLException{
		HashMap<String,String> columnAlias = null;
		PreparedStatement pstmt = null;
		try{
			/*
			 *  SELECT MYCOL AS MYLABEL FROM MYTABLE
			 * 
			 * ResultSetMetaData.getColumnName and ResultSetMetaData.getColumnName values that are returned for the query
			 * 
			 * -getColumnLabel(int) would return "MYLABEL"
			 * -getColumnName(int)  would return "MYCOL"
			 * 
			 * Note : In SQL AS is not specified then getColumnLabel(int) will return same as  getColumnName(int)
			 * 
			 * http://docs.oracle.com/javase/7/docs/api/java/sql/ResultSetMetaData.html#getColumnLabel(int)
			 */

			if( !StrUtl.isEmptyTrimmed( sql ) ){
				columnAlias = new HashMap<String,String>();
				/*
				pstmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
				ResultSetMetaData rsMetaData = pstmt.getMetaData();
				for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
					if(!rsMetaData.getColumnLabel(i).equalsIgnoreCase(rsMetaData.getColumnName(i))){
						columnAlias.put(rsMetaData.getColumnLabel(i), rsMetaData.getColumnName(i));
					}
				}
				*/
				//Current oracle driver is not JDBC 4.0 compliant so workaround.
				
				sql = sql.toUpperCase();
				int startIndex = sql.indexOf("SELECT ");
				int endIndex = sql.indexOf(" FROM ");

				if(startIndex != -1 && endIndex != -1){
					String selectCols= sql.substring(startIndex + "SELECT ".length() ,endIndex); 
					if(selectCols.indexOf("DECODE") != -1){
						selectCols = handleDECODE(selectCols);
					}
					String cols[] = StringUtils.split(selectCols,",");
					String namelabel[];
					String name;
					String label;
					for(String col:cols){
						namelabel = StringUtils.splitByWholeSeparator(col," AS");
						if(namelabel.length == 2){
							name = StringUtils.trim(namelabel[0]);
							label = StringUtils.trim(namelabel[1]);
							
							name = name.replace(" COMMA ", ",");
							name = name.replace("DE__CODE", "DECODE");
			
							if(!name.equalsIgnoreCase(label)){
								columnAlias.put(label,name);
							}
						}
					}
				}
			}
 		}
		finally {
			if (pstmt != null){
				pstmt.close();
			}
		}
		return columnAlias;
	}
	
	private String handleDECODE(String sql){
		int index = sql.indexOf("DECODE");
		String decodeStr = sql.substring(index, sql.indexOf(")",index)+1);
		String decodeReplaceStr = decodeStr.replaceAll(",", " COMMA ").replace("DECODE", "DE__CODE");
		sql = sql.replace(decodeStr, decodeReplaceStr);
		if(sql.indexOf("DECODE") != -1){
			return handleDECODE(sql);
		}
		return sql;
	}
	
	private final static class EditorsTbl {
		private static final String _tblName = "EDITORS";
		private static final String _name = "EDITOR_NAME";
		private static final String _desc = "EDITOR_DESC";
		private static final String _source = "EDITOR_SOURCE_NAME";
		private static final String _initialLookup = "EDITOR_INITIAL_LOOKUP";
		private static final String _interceptor = "EDITOR_INTERCEPTOR";
		private static final String _sourceSQL = "EDITOR_SOURCE_SQL";
		private static final String _defaultWhereClause = "EDITORS_DEFAULT_WHERECLAUSE";
		private static final String _securityManager = "EDITOR_SECURITY_MGR";
		private static final String _securityManagerForEdit = "EDITOR_SECURITY_MGR_FOR_EDIT";
		private static final String _formType = "EDITOR_FORM_TYPE";
		private static final String _isListEdit = "EDITOR_LIST_EDIT";
		private static final String _pageSize = "EDITOR_PAGE_SIZE";
		private static final String _viewRole = "EDITOR_VIEW_ROLE";
		private static final String _editRole = "EDITOR_EDIT_ROLE";
		private static final String _addNewPriv = "EDITOR_ADD_NEW_PRIV";
		private static final String _bulkUploadPriv = "EDITOR_BULK_UPLOAD_PRIV";
		private static final String _bulkUploadImage = "EDITOR_BULK_UPLOAD_IMAGE";
		private static final String _multiDeletePriv = "EDITOR_MULTI_DELETE_PRIV";
		private static final String _customDisplayPriv = "EDITOR_CUSTOM_DISPLAY_PRIV";
		private static final String _exportPriv = "EDITOR_EXPORT_PRIV";
		private static final String _queryPriv = "EDITOR_QUERY_PRIV";
		private static final String _queryToolAdditionalFilter = "EDITOR_USER_CRITERIA_FILTER";
		private static final String _bulkUpdatePriv = "EDITOR_BULKUPDATE_PRIV";
		private static final String _isExactMatchLookup = "EDITOR_IS_EXACT_MATCH_LKP";
		private static final String _hasHelp = "EDITOR_HAS_HELP";
		private static final String _hasChild = "EDITOR_HAS_CHILD";
		private static final String _childActions = "EDITOR_CHILD_ACTIONS";
		private static final String _searchColsPerRow = "EDITOR_SEARCH_COLS_PER_ROW";
		private static final String _detailEditorName = "EDITOR_DETAIL_EDITOR_NAME";
		private static final String _cacheToRefresh = "EDITOR_CACHE_TO_REFRESH";
		private static final String _schExportPriv = "EDITOR_SCH_EXPORT_PRIV";
		private static final String _footerPriv = "EDITOR_FOOTER_PRIV";
		private static final String _cancelPriv = "EDITOR_CANCEL_PRIV";
		private static final String _showHeader = "EDITOR_SHOW_HEADER";
		private static final String _showPositions = "EDITOR_SHOW_POSITIONS";
		private static final String _addlink = "EDITOR_ADD_LINK";
		private static final String _addlinkImage = "EDITOR_ADD_LINK_IMAGE";
		private static final String _deletelinkImage = "EDITOR_DELETE_LINK_IMAGE";
		private static final String _cancellinkImage = "EDITOR_CANCEL_LINK_IMAGE";
		private static final String _clientInterceptorForEdit = "EDITOR_CLTINTERCEPTOR_EDIT";
		private static final String _clientInterceptorForList = "EDITOR_CLTINTERCEPTOR_LIST";
		private static final String _updateSectionTitles = "EDITOR_UPDATE_SECTION_TITLES";
		private static final String _updateColsPerRow = "EDITOR_UPDATE_COLS_PER_ROW";
		private static final String _envSpringBeanId = "EDITOR_ENV_SPRING_BEAN_ID";
		private static final String _multiSorting = "EDITOR_COLS_MULTISORTING";
	}

	private final static class EditorTypeColumnsTbl {
		private static final String _tblName = "EDITOR_TYPE_COLUMNS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _columnName = "COLUMN_NAME";
		private static final String _type = "EDITOR_TYPE";
		private static final String _editKey = "COLUMN_EDIT_KEY";
		private static final String _editKeyDisplayable = "COLUMN_EDIT_KEY_DISPLAYABLE";
		private static final String _updatable = "COLUMN_UPDATABLE";
		private static final String _nullable = "COLUMN_NULLABLE";
		private static final String _autoSeqName = "COLUMN_AUTOSEQ_NAME";
		private static final String _regExp = "COLUMN_REGEXP";
		private static final String _errorMsg = "COLUMN_ERRORMSG";

	}

	private final static class EditorTypesTbl {
		private static final String _tblName = "EDITOR_TYPES";
		private static final String _name = "EDITOR_NAME";
		private static final String _type = "EDITOR_TYPE";
		private static final String _proc = "EDITOR_PROC_NAME";
	}

	private final static class EditorColumnsTbl {
		private static final String _tblName = "EDITOR_COLUMNS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _columnName = "COLUMN_NAME";
		private static final String _aggregatable = "COLUMN_AGGREGATABLE";
		private static final String _displayable = "COLUMN_DISPLAYABLE";
		private static final String _searchable = "COLUMN_SEARCHABLE";
		private static final String _linkable = "COLUMN_LINKABLE";
		private static final String _expr = "COLUMN_EXPR";
		private static final String _seq = "COLUMN_SEQ";
		private static final String _key = "COLUMN_KEY";
		private static final String _secured = "COLUMN_SECURED";
		private static final String _comboCached = "COLUMN_COMBO_CACHED";
		private static final String _comboSelectTag = "COLUMN_COMBO_SELECT_TAG";
		private static final String _comboOvd = "COLUMN_COMBO_OVD";
		private static final String _comboTblNameOvd = "COLUMN_COMBO_TBLNAME_OVD";
		private static final String _comboSelectOvd = "COLUMN_COMBO_SELECT_OVD";
		private static final String _comboOrderByOvd = "COLUMN_COMBO_ORDERBY_OVD";
		private static final String _columnDecoratorProperty = "COLUMN_DECORATOR_PROPERTY";
		private static final String _columnBeanProperty = "COLUMN_BEAN_PROPERTY";
		private static final String _searchableMandatory = "COLUMN_SEARCHABLE_MANDATORY";
		private static final String _columnSearchSeq = "COLUMN_SEARCH_SEQ";
		private static final String _displayAlignOvd = "COLUMN_DISPLAY_ALIGN_OVD"; // Column display align in the list. Possible values : left / right / center
		private static final String _displayCSSOvd = "COLUMN_DISPLAY_CSS_OVD"; // to specify grid cell CSS class name eg WithBreaks
		private static final String _displayStyleOvd = "COLUMN_DISPLAY_STYLE_OVD"; // to specify grid cell CSS inline style. eg width:300px;
		private static final String _sizeOvd = "COLUMN_SIZE_OVD"; // TextBox size and optional maxlength eg 10 or 10,30 where size=10 maxlength=30
		private static final String _captionOvd = "COLUMN_CAPTION_OVD";
		private static final String _jsEventsOvd = "COLUMN_JS_EVENTS_OVD"; // javascript events as json string .. eg {"onchange":"onEditorColumnChange(this)"}
		private static final String _indexedUpperCaseData = "COLUMN_INDEXED_UPPERCASE_DATA"; // 1/0 While building search SQL where clause with like operator with UPPER(column_name) or not.
	}

	private final static class EditorFiltersTbl {
		private static final String _tblName = "EDITOR_FILTERS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _editorFilterName = "EDITOR_FILTER_NAME";
		private static final String _editorFilterDesc = "EDITOR_FILTER_DESC";
		private static final String _editorFilterSql = "EDITOR_FILTER_SQL";
	}

	private final static class ColumnsTbl {
		private static final String _tblName = "COLUMNS";
		private static final String _name = "COLUMN_NAME";
		private static final String _type = "COLUMN_TYPE";
		private static final String _caption = "COLUMN_CAPTION";
		private static final String _format = "COLUMN_FORMAT";
		private static final String _unit = "COLUMN_UNIT";
		private static final String _combo = "COLUMN_COMBO";
		private static final String _comboTblName = "COLUMN_COMBO_TBLNAME";
		private static final String _comboSelect = "COLUMN_COMBO_SELECT";
		private static final String _comboOrderBy = "COLUMN_COMBO_ORDERBY";
		private static final String _minval = "COLUMN_MINVAL";
		private static final String _maxval = "COLUMN_MAXVAL";
		private static final String _regexp = "COLUMN_REGEXP";
		private static final String _errormsg = "COLUMN_ERRORMSG";
		private static final String _value = "COLUMN_VALUE";
		private static final String _displayAlign = "COLUMN_DISPLAY_ALIGN"; // Column display align in the list. Possible values : left / right / center
		private static final String _displayCSS = "COLUMN_DISPLAY_CSS"; // to specify grid cell CSS class name eg WithBreaks
		private static final String _displayStyle = "COLUMN_DISPLAY_STYLE"; // to specify grid cell CSS inline style. eg width:300px;
		private static final String _size = "COLUMN_SIZE";
		// TextBox size and optional maxlength eg 10 or 10,30 where size=10 maxlength=30.
		// TextArea cols,rows eg 100,5
		private static final String _jsEvents = "COLUMN_JS_EVENTS"; // javascript events as json string .. eg {"onchange":"onEditorColumnChange(this)"} to apply for all editors
	}

	public class UserEditorTypeColumnsTbl {
		private static final String _tblName = "USER_EDITOR_TYPE_COLUMNS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _columnName = "COLUMN_NAME";
		private static final String _type = "EDITOR_TYPE";
		private static final String _userId = "USER_ID";
	}

	public class UserEditorColumnsTbl {
		public static final String _tblName = "USER_EDITOR_COLUMNS";
		public static final String _editorName = "EDITOR_NAME";
		public static final String _columnName = "COLUMN_NAME";
		public static final String _userId = "USER_ID";
		public static final String _seq = "COLUMN_SEQ";
		public static final String _sortOrder = "COLUMN_SORT_ORDER";
		public static final String _sortOrderSeq = "COLUMN_SORT_ORDER_SEQ";
	}

	private final static class MenuTbl {
		private static final String _tblName = "MENU_ITEMS";
		private static final String _id = "MENU_ID";
		private static final String _label = "MENU_LABEL";
	}

	private final static class ErrorMessageTbl {
		private static final String _tblName = "ERROR_MESSAGES";
		private static final String _number = "ERROR_NUMBER";
		private static final String _key = "ERROR_KEY";
		private static final String _message = "ERROR_MESSAGE";
	}

}
