package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.naming.NamingException;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnInfo;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;

public class EJBEEditorMetaDataBean implements EntityBean {
	private static final String _module = "com.addval.ejbutils.server.EJBEEditorMetaDataBean";
	private String _serverType = "";
	private String _subSystem = "";
	private EntityContext _context = null;
	private static final String _COLUMN_INFO = "COLUMN_INFO";
	private static final String _DEFAULT = "DEFAULT";
	private EditorMetaData _editorMetaData = null;
	private boolean isEditorMetadataChanged = false;

	private String _sqlNullFunction;
	private String _concatenateOperator;

	public String getServerType() {
		return _serverType;
	}

	public String getSubSystem() {
		return _subSystem;
	}

	public boolean isEditorMetadataChanged() {
		return isEditorMetadataChanged;
	}

	public void setEntityContext(EntityContext ctx) throws RemoteException {
		_context = ctx;

		try {

			_subSystem = (String) EJBEnvironment.lookupLocalContext("SUBSYSTEM");
			_serverType = Environment.getInstance(getSubSystem()).getDbPoolMgr().getServerType();

			if (_serverType.equals("SQLSERVER") || _serverType.equals("MSAccess")) {
				_sqlNullFunction = "ISNULL";
				_concatenateOperator = "+";
			}
			else {
				_sqlNullFunction = "NVL";
				_concatenateOperator = "||";
			}

		}
		catch (NamingException nex) {

			nex.printStackTrace();

		}
	}

	public void unsetEntityContext() throws RemoteException {

		_context = null;
	}

	public EditorMetaData lookup() throws RemoteException {

		EditorMetaData editorMetaData = null;
		try {

			lookupEditorComboCache();
			editorMetaData = (EditorMetaData) _editorMetaData.clone();
		}
		catch (Exception e) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
			throw new EJBException(e);

		}
		return editorMetaData;
	}

	public ColumnInfo lookupColumnInfo() {
		ColumnMetaData columnsMetaData = _editorMetaData.getColumnMetaData(1);
		return columnsMetaData.getColumnInfo();

	}

	public EditorMetaData update(EditorMetaData editorMetaData) throws RemoteException {
		try {
			isEditorMetadataChanged = true;
			_editorMetaData = (EditorMetaData) editorMetaData.clone();
		}
		catch (Exception e) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
			throw new EJBException(e);

		}

		return editorMetaData;
	}

	private void lookupEditorComboCache() throws RemoteException {

		for (int i = 1; i <= _editorMetaData.getColumnCount(); i++) {

			ColumnMetaData columnMetaData = _editorMetaData.getColumnMetaData(i);

			if (columnMetaData.isCombo() && columnMetaData.isComboSelectTag()) {

				if ((columnMetaData.isComboCached() == false) || (columnMetaData.getEjbResultSet() == null)) {

					lookupComboCache(columnMetaData);
				}

			}
		}
	}

	private void lookupComboCache(ColumnMetaData colmetadata) throws RemoteException {

		if (colmetadata.isCombo()) {

			if (colmetadata.isComboSelectTag()) {
				// now get the list of values as an EJBResultset and save it in the ColumnMetaData
				EditorMetaData comboMetaData = null;
				try {

					String name = colmetadata.getComboTblName();
					String type = "DEFAULT";

					String beanName = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBEEditorMetaDataBeanName", "");
					EJBEEditorMetaDataHome home = (EJBEEditorMetaDataHome) EJBEnvironment.lookupEJBInterface(getSubSystem(), beanName, EJBEEditorMetaDataHome.class);
					EJBEEditorMetaDataRemote remote = home.findByPrimaryKey(new EJBEEditorMetaDataBeanPK(name, type, _DEFAULT));

					// Get the non-customized EditorMetaData
					comboMetaData = remote.lookup();

					String homeName = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
					EJBSTableManagerHome tableManagerHome = (EJBSTableManagerHome) EJBEnvironment.lookupEJBInterface(getSubSystem(), homeName, EJBSTableManagerHome.class);
					EJBSTableManagerRemote tableManagerRemote = tableManagerHome.create();
					com.addval.ejbutils.dbutils.EJBCriteria ejbCriteria = (new com.addval.ejbutils.dbutils.EJBResultSetMetaData(comboMetaData)).getNewCriteria();

					if (colmetadata.getComboOrderBy() != null) {
						java.util.Vector vector = new java.util.Vector();
						vector.add(colmetadata.getComboOrderBy());
						ejbCriteria.orderBy(vector);
					}
					EJBResultSet ejbResultSet = tableManagerRemote.lookup(ejbCriteria);
					colmetadata.setEjbResultSet(ejbResultSet);
				}
				catch (Exception e) {
					Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
					throw new EJBException(e);
				}
			}
		}
	}

	public EJBEEditorMetaDataBeanPK ejbCreate(String name, String type) throws FinderException {

		return ejbFindByPrimaryKey(new EJBEEditorMetaDataBeanPK(name, type, _DEFAULT));

	}

	public void ejbPostCreate(String name, String type) throws RemoteException, CreateException {

	}

	public EJBEEditorMetaDataBeanPK ejbFindByPrimaryKey(EJBEEditorMetaDataBeanPK pk) throws FinderException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {

			if (pk != null && pk.getName() != null && pk.getType() != null && !pk.getType().equalsIgnoreCase(_COLUMN_INFO)) {

				conn = getConnection();

				String sql = "SELECT 1 FROM " + EditorsTbl._tblName + " WHERE " + EditorsTbl._name + " = ?";

				stmt = conn.prepareStatement(sql);
				stmt.setString(1, pk.getName());

				rs = stmt.executeQuery();

				if (!rs.next())
					throw new FinderException("Editor with name : " + pk.getName() + " not found in the database");
			}
			else if (pk != null && pk.getName() != null && pk.getType() != null && pk.getType().equalsIgnoreCase(_COLUMN_INFO)) {

				String sql = "SELECT 1 FROM " + ColumnsTbl._tblName + " WHERE " + ColumnsTbl._name + " = ?";

				conn = getConnection();
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, pk.getName());
				rs = stmt.executeQuery();

				if (!rs.next())
					throw new FinderException("Column with name : " + pk.getName() + " not found in the database");
			}
			else {

				throw new FinderException("Invalid EJBEEditorMetaDataBeanPK.");

			}

			return pk;
		}
		catch (SQLException se) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
			throw new EJBException(se);
		}
		catch (NamingException ne) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(ne);
			throw new EJBException(ne);
		}
		finally {

			try {
				// MS Access throws error if we try to close a closed result set
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (conn != null)
					releaseConnection(conn);
			}
			catch (SQLException se) {

				Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
				throw new EJBException(se);
			}
		}
	}

	public void ejbLoad() throws RemoteException {
		Connection conn = null;
		try {
			if (_editorMetaData != null)
				return;
			EJBEEditorMetaDataBeanPK pk = (EJBEEditorMetaDataBeanPK) _context.getPrimaryKey();
			String name = pk.getName();
			String type = pk.getType();
			String userId = pk.getUserId();
			conn = getConnection();
			if (type != null && type.equalsIgnoreCase(_COLUMN_INFO))
				buildColumnMetaData(name, conn);
			else
				buildEditorMetaData(name, type, userId, conn);
		}
		catch (SQLException se) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
			throw new EJBException(se);
		}
		catch (Exception e) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
			throw new EJBException(e);
		}
		finally {

			try {
				if (conn != null)
					releaseConnection(conn);
			}
			catch (SQLException se) {

				Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
				throw new EJBException(se);
			}
		}
	}

	public void ejbStore() throws RemoteException {

		Connection conn = null;

		try {

			conn = getConnection();
			EJBEEditorMetaDataBeanPK pk = (EJBEEditorMetaDataBeanPK) _context.getPrimaryKey();

			if (pk.getUserId() != null && pk.getUserId().equalsIgnoreCase(_DEFAULT)) {

				// Do not update Base EditorMetaData
				// updateEditorsTbl ( conn );
				// updateEditorTypesTbl ( conn );
				// updateEditorColumnsTbl ( conn );
				// updateEditorTypeColumnsTbl( conn );
				// updateColumnsTbl ( conn );

			}
			else if (pk.getUserId() != null && !pk.getUserId().equalsIgnoreCase(_DEFAULT)) {

				updateUserEditorColumnsTbl(pk.getName(), pk.getUserId(), conn);

			}

			isEditorMetadataChanged = false;
		}
		catch (SQLException se) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
			throw new EJBException(se);

		}
		catch (Exception e) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
			throw new EJBException(e);

		}
		finally {

			try {

				if (conn != null)
					releaseConnection(conn);
			}
			catch (SQLException se) {

				Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
				throw new EJBException(se);
			}
		}

	}

	public void ejbRemove() throws RemoteException {

	}

	public void ejbActivate() throws RemoteException {

	}

	public void ejbPassivate() throws RemoteException {
		_editorMetaData = null;
	}

	private void buildColumnMetaData(String name, Connection conn) {

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
		String columnSizeStr = null;
		Integer textBoxSize = null;
		Integer textBoxMaxlength = null;
		Integer textAreaRows = null;
		Integer textAreaCols = null;

		ColumnMetaData columnMetaData = null;
		ColumnInfo columnInfo = null;
		Hashtable cols = null;
		int size = 0;

		try {

			sql.append("SELECT * FROM " + ColumnsTbl._tblName);
			sql.append(" WHERE " + ColumnsTbl._name + "= ? ");

			// Execute the query for getting Column Info
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, name);

			// Log the SQL that was generated
			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logSql(sql.toString());

			// Execute the query for Editors
			rs = stmt.executeQuery();

			if (rs.next()) {

				rsMetaData = rs.getMetaData();
				size = rsMetaData.getColumnCount();
				cols = new Hashtable();

				for (int index = 1; index <= size; index++)
					cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));

				name = rs.getString(ColumnsTbl._name);
				type = rs.getString(ColumnsTbl._type);
				caption = rs.getString(ColumnsTbl._caption);
				format = cols.containsKey(ColumnsTbl._format) ? rs.getString(ColumnsTbl._format) : null;
				unit = cols.containsKey(ColumnsTbl._unit) ? rs.getString(ColumnsTbl._unit) : null;
				combo = cols.containsKey(ColumnsTbl._combo) ? (rs.getInt(ColumnsTbl._combo) != 0 ? true : false) : false;
				comboTblName = cols.containsKey(ColumnsTbl._comboTblName) ? rs.getString(ColumnsTbl._comboTblName) : null;
				comboSelect = cols.containsKey(ColumnsTbl._comboSelect) ? rs.getString(ColumnsTbl._comboSelect) : null;
				comboOrderBy = cols.containsKey(ColumnsTbl._comboOrderBy) ? rs.getString(ColumnsTbl._comboOrderBy) : null;
				minval = cols.containsKey(ColumnsTbl._minval) ? rs.getDouble(ColumnsTbl._minval) : 0;
				maxval = cols.containsKey(ColumnsTbl._maxval) ? rs.getDouble(ColumnsTbl._maxval) : 0;
				regexp = cols.containsKey(ColumnsTbl._regexp) ? rs.getString(ColumnsTbl._regexp) : null;
				errorMsg = cols.containsKey(ColumnsTbl._errormsg) ? rs.getString(ColumnsTbl._errormsg) : null;
				value = cols.containsKey(ColumnsTbl._value) ? rs.getString(ColumnsTbl._value) : null;
				textAlign = cols.containsKey(ColumnsTbl._displayAlign) ? rs.getString(ColumnsTbl._displayAlign) : null;
				columnSizeStr = cols.containsKey(ColumnsTbl._size) ? rs.getString(ColumnsTbl._size) : null;

				if (!StrUtl.isEmptyTrimmed(columnSizeStr)) {
					String[] minmax = columnSizeStr.split(",");
					textBoxSize = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textBoxMaxlength = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
					textAreaCols= minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textAreaRows = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
				}
				else {
					textBoxSize = null;
					textBoxMaxlength = null;
					textAreaRows = null;
					textAreaCols = null;
				}
				_editorMetaData = new EditorMetaData(name, null, null, null, null, false);
				columnMetaData = new ColumnMetaData(false, false, false, false, false, null, false, false);
				columnInfo = new ColumnInfo(name, type, caption, format, unit, combo, comboTblName, comboSelect, comboOrderBy, minval, maxval, regexp, errorMsg, value);
				columnInfo.setTextAlign(textAlign);
				columnInfo.setTextBoxSize(textBoxSize);
				columnInfo.setTextBoxMaxlength(textBoxMaxlength);
				columnInfo.setTextAreaRows(textAreaRows);
				columnInfo.setTextAreaCols(textAreaCols);

				columnMetaData.setColumnInfo(columnInfo);
				_editorMetaData.addColumnMetaData(columnMetaData);
			}
			else
				throw new EJBException("ColumnInfo :" + name + ":" + type + " could not be found");
		}

		catch (Exception e) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
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

				Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(se);
				throw new EJBException(se);
			}
		}
	}

	private void buildEditorMetaData(String name, String type, String userId, Connection conn) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String sql = null;
		EditorMetaData editorMetaData = null;

		try {

			// Execute the query for getting Editor Info
		    sql = buildEditorsSql();
		    stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			// Execute the query for Editors
			rs = stmt.executeQuery();

			ResultSetMetaData rsMetaData = rs.getMetaData();
			int size = rsMetaData.getColumnCount();
			Hashtable cols = new Hashtable();

			for (int index = 1; index <= size; index++)
				cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));

			if (rs.next()) {

				String desc = null;
				String source = null;
				String proc = null;
				String editorType = null;
				boolean initialLookup = false;
				String interceptor = null;
				String sourceSQL = null;

				source = rs.getString(EditorsTbl._source);
				desc = rs.getString(EditorsTbl._desc);
				initialLookup = cols.containsKey(EditorsTbl._initialLookup) ? (rs.getInt(EditorsTbl._initialLookup) != 0 ? true : false) : false;
				interceptor = cols.containsKey(EditorsTbl._interceptor) ? rs.getString(EditorsTbl._interceptor) : null;
				sourceSQL = cols.containsKey(EditorsTbl._sourceSQL) ? rs.getString(EditorsTbl._sourceSQL) : null;

				source = (sourceSQL != null) ? sourceSQL : source;

				rs.close();

				// Get the data for Editor_Types
				sql = buildEditorTypesSql();
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, name);
				stmt.setString(2, type);
				rs = stmt.executeQuery();

				if (rs.next()) {

					proc = rs.getString(EditorTypesTbl._proc);
					editorType = rs.getString(EditorTypesTbl._type);
				}

				rs.close();
				editorMetaData = new EditorMetaData(name, desc, type, source, proc, editorType != null);
				editorMetaData.setInitialLookup(initialLookup);
				editorMetaData.setInterceptor(interceptor);

			}
			else {

				throw new EJBException("MetaData for Editor :" + name + ":" + type + " could not be found");
			}

			editorMetaData.setColumnsMetaData(buildColumnsMetaData(name, type, userId, conn));

			_editorMetaData = editorMetaData;

		}
		catch (Exception e) {

			Environment.getInstance(getSubSystem()).getLogFileMgr(_module).logError(e);
			throw new EJBException(e);
		}
		finally {

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
			boolean displayable = false;
			boolean aggregatable = false;
			boolean searchable = false;
			boolean searchableMandatory = false;
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
			String columnSize = null;
			String errorMsg = null;
			boolean comboOvd = false;
			String comboTblNameOvd = null;
			String comboSelectOvd = null;
			String comboOrderByOvd = null;
			String textAlignOvd = null;
			String columnSizeOvd = null;
			Integer textBoxSize = null;
			Integer textBoxMaxlength = null;
			Integer textAreaRows = null;
			Integer textAreaCols = null;

			String columnDecoratorProperty = null;
			String columnBeanProperty = null;

			ResultSetMetaData rsMetaData = rs.getMetaData();
			int size = rsMetaData.getColumnCount();
			Hashtable cols = new Hashtable();

			// Environment.getInstance( getSubSystem() ).getLogFileMgr( _module ).logInfo ("After getting columns RS" );
			for (int index = 1; index <= size; index++)
				cols.put(rsMetaData.getColumnName(index).toUpperCase(), String.valueOf(index));

			while (rs.next()) {

				colName = rs.getString(ColumnsTbl._name);
				colType = rs.getString(ColumnsTbl._type);
				caption = rs.getString(ColumnsTbl._caption);
				displayable = cols.containsKey(EditorColumnsTbl._displayable) ? (rs.getInt(EditorColumnsTbl._displayable) != 0 ? true : false) : false;
				aggregatable = cols.containsKey(EditorColumnsTbl._aggregatable) ? (rs.getInt(EditorColumnsTbl._aggregatable) != 0 ? true : false) : false;
				searchable = cols.containsKey(EditorColumnsTbl._searchable) ? (rs.getInt(EditorColumnsTbl._searchable) != 0 ? true : false) : false;
				searchableMandatory = cols.containsKey(EditorColumnsTbl._searchableMandatory) ? (rs.getInt(EditorColumnsTbl._searchableMandatory) != 0 ? true : false) : false;
				linkable = cols.containsKey(EditorColumnsTbl._linkable) ? (rs.getInt(EditorColumnsTbl._linkable) != 0 ? true : false) : false;
				expression = cols.containsKey(EditorColumnsTbl._expr) ? rs.getString(EditorColumnsTbl._expr) : null;
				format = cols.containsKey(ColumnsTbl._format) ? rs.getString(ColumnsTbl._format) : null;
				unit = cols.containsKey(ColumnsTbl._unit) ? rs.getString(ColumnsTbl._unit) : null;
				combo = cols.containsKey(ColumnsTbl._combo) ? (rs.getInt(ColumnsTbl._combo) != 0 ? true : false) : false;
				comboTblName = cols.containsKey(ColumnsTbl._comboTblName) ? rs.getString(ColumnsTbl._comboTblName) : null;
				comboSelect = cols.containsKey(ColumnsTbl._comboSelect) ? rs.getString(ColumnsTbl._comboSelect) : null;
				comboOrderBy = cols.containsKey(ColumnsTbl._comboOrderBy) ? rs.getString(ColumnsTbl._comboOrderBy) : null;
				minval = cols.containsKey(ColumnsTbl._minval) ? rs.getDouble(ColumnsTbl._minval) : 0;
				maxval = cols.containsKey(ColumnsTbl._maxval) ? rs.getDouble(ColumnsTbl._maxval) : -1.0;
				regexp = cols.containsKey(ColumnsTbl._regexp) ? rs.getString(ColumnsTbl._regexp) : null;
				errorMsg = cols.containsKey(ColumnsTbl._errormsg) ? rs.getString(ColumnsTbl._errormsg) : null;
				value = cols.containsKey(ColumnsTbl._value) ? rs.getString(ColumnsTbl._value) : null;
				textAlign = cols.containsKey(ColumnsTbl._displayAlign) ? rs.getString(ColumnsTbl._displayAlign) : null;
				columnSize = cols.containsKey(ColumnsTbl._size) ? rs.getString(ColumnsTbl._size) : null;

				key = cols.containsKey(EditorColumnsTbl._key) ? (rs.getInt(EditorColumnsTbl._key) != 0 ? true : false) : false;
				secured = cols.containsKey(EditorColumnsTbl._secured) ? (rs.getInt(EditorColumnsTbl._secured) != 0 ? true : false) : false;
				combocached = cols.containsKey(EditorColumnsTbl._comboCached) ? (rs.getInt(EditorColumnsTbl._comboCached) != 0 ? true : false) : false;
				comboSelectTag = cols.containsKey(EditorColumnsTbl._comboSelectTag) ? (rs.getInt(EditorColumnsTbl._comboSelectTag) == 1 ? true : false) : false;

				comboOvd = cols.containsKey(EditorColumnsTbl._comboOvd) ? (rs.getInt(EditorColumnsTbl._comboOvd) != 0 ? true : false) : false;
				comboTblNameOvd = cols.containsKey(EditorColumnsTbl._comboTblNameOvd) ? rs.getString(EditorColumnsTbl._comboTblNameOvd) : null;
				comboSelectOvd = cols.containsKey(EditorColumnsTbl._comboSelectOvd) ? rs.getString(EditorColumnsTbl._comboSelectOvd) : null;
				comboOrderByOvd = cols.containsKey(EditorColumnsTbl._comboOrderByOvd) ? rs.getString(EditorColumnsTbl._comboOrderByOvd) : null;
				textAlignOvd = cols.containsKey(EditorColumnsTbl._displayAlignOvd) ? rs.getString(EditorColumnsTbl._displayAlignOvd) : null;
				columnSizeOvd = cols.containsKey(EditorColumnsTbl._sizeOvd) ? rs.getString(EditorColumnsTbl._sizeOvd) : null;

				columnDecoratorProperty = cols.containsKey(EditorColumnsTbl._columnDecoratorProperty) ? rs.getString(EditorColumnsTbl._columnDecoratorProperty) : null;
				columnBeanProperty = cols.containsKey(EditorColumnsTbl._columnBeanProperty) ? rs.getString(EditorColumnsTbl._columnBeanProperty) : null;

				combo = (comboOvd) ? comboOvd : combo;
				comboTblName = (comboTblNameOvd != null) ? comboTblNameOvd : comboTblName;
				comboSelect = (comboSelectOvd != null) ? comboSelectOvd : comboSelect;
				comboOrderBy = (comboOrderByOvd != null) ? comboOrderByOvd : comboOrderBy;

				textAlign = (textAlignOvd != null) ? textAlignOvd : textAlign;
				columnSize = (columnSizeOvd != null) ? columnSizeOvd : columnSize;

				if (!StrUtl.isEmptyTrimmed(columnSize)) {
					String[] minmax = columnSize.split(",");
					textBoxSize = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textBoxMaxlength = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
					textAreaCols= minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
					textAreaRows= minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
				}
				else {
					textBoxSize = null;
					textBoxMaxlength = null;
					textAreaRows = null;
					textAreaCols = null;
				}

				columnMetaData = new ColumnMetaData(aggregatable, searchable, displayable, updatable, linkable, expression, key, searchableMandatory);
				columnMetaData.setSecured(secured);
				columnMetaData.setComboCached(combocached);
				columnMetaData.setComboSelectTag(comboSelectTag);
				columnMetaData.setColumnDecoratorProperty(columnDecoratorProperty);
				columnMetaData.setColumnBeanProperty(columnBeanProperty);

				columnInfo = new ColumnInfo(colName, colType, caption, format, unit, combo, comboTblName, comboSelect, comboOrderBy, minval, maxval, regexp, errorMsg, value);
				columnInfo.setTextAlign(textAlign);
				columnInfo.setTextBoxSize(textBoxSize);
				columnInfo.setTextBoxMaxlength(textBoxMaxlength);
				columnInfo.setTextAreaRows(textAreaRows);
				columnInfo.setTextAreaCols(textAreaCols);

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

				colName = rs.getString(ColumnsTbl._name);

				// If a row exists for this column in the EditorTypeColumns table then the column is updatable in
				// some form in the edit screen. Key columns can never be changed but they will appear in the
				// edit screen either as labels or as just hidden variables
				updatable = true;

				// edit Key
				editKey = cols.containsKey(EditorTypeColumnsTbl._editKey) ? (rs.getInt(EditorTypeColumnsTbl._editKey) != 0 ? true : false) : false;
				keyDisplayable = cols.containsKey(EditorTypeColumnsTbl._editKeyDisplayable) ? (rs.getInt(EditorTypeColumnsTbl._editKeyDisplayable) != 0 ? true : false) : false;

				// Nullable - default is true
				nullable = cols.containsKey(EditorTypeColumnsTbl._nullable) ? (rs.getInt(EditorTypeColumnsTbl._nullable) != 0 ? true : false) : true;

				// Column Sequence Table Name
				autoSeqName = cols.containsKey(EditorTypeColumnsTbl._autoSeqName) ? rs.getString(EditorTypeColumnsTbl._autoSeqName) : null;

				if (columnNames.containsKey(colName)) {

					// Column Exists. Hence update the columnMetaData
					columnMetaData = (ColumnMetaData) columns.elementAt(((Integer) columnNames.get(colName)).intValue());
				}
				else {

					colType = rs.getString(ColumnsTbl._type);
					caption = rs.getString(ColumnsTbl._caption);
					format = cols.containsKey(ColumnsTbl._format) ? rs.getString(ColumnsTbl._format) : null;
					unit = cols.containsKey(ColumnsTbl._unit) ? rs.getString(ColumnsTbl._unit) : null;
					combo = cols.containsKey(ColumnsTbl._combo) ? (rs.getInt(ColumnsTbl._combo) != 0 ? true : false) : false;
					comboTblName = cols.containsKey(ColumnsTbl._comboTblName) ? rs.getString(ColumnsTbl._comboTblName) : null;
					comboSelect = cols.containsKey(ColumnsTbl._comboSelect) ? rs.getString(ColumnsTbl._comboSelect) : null;
					comboOrderBy = cols.containsKey(ColumnsTbl._comboOrderBy) ? rs.getString(ColumnsTbl._comboOrderBy) : null;
					minval = cols.containsKey(ColumnsTbl._minval) ? rs.getDouble(ColumnsTbl._minval) : 0;
					maxval = cols.containsKey(ColumnsTbl._maxval) ? rs.getDouble(ColumnsTbl._maxval) : -1.0;
					regexp = cols.containsKey(ColumnsTbl._regexp) ? rs.getString(ColumnsTbl._regexp) : null;
					errorMsg = cols.containsKey(ColumnsTbl._errormsg) ? rs.getString(ColumnsTbl._errormsg) : null;
					value = cols.containsKey(ColumnsTbl._value) ? rs.getString(ColumnsTbl._value) : null;
					textAlign = cols.containsKey(ColumnsTbl._displayAlign) ? rs.getString(ColumnsTbl._displayAlign) : null;
					columnSize = cols.containsKey(ColumnsTbl._size) ? rs.getString(ColumnsTbl._size) : null;

					if (!StrUtl.isEmptyTrimmed(columnSize)) {
						String[] minmax = columnSize.split(",");
						textBoxSize = minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
						textBoxMaxlength = minmax.length > 1 ? Integer.valueOf(minmax[1].trim()) : null;
						textAreaCols= minmax.length > 0 ? Integer.valueOf(minmax[0].trim()) : null;
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
					columnInfo.setTextBoxSize(textBoxSize);
					columnInfo.setTextBoxMaxlength(textBoxMaxlength);
					columnInfo.setTextAreaRows(textAreaRows);
					columnInfo.setTextAreaCols(textAreaCols);

					columnMetaData.setColumnInfo(columnInfo);
					columns.add(columnMetaData);
				}
				columnMetaData.setUpdatable(updatable);
				columnMetaData.setEditKey(editKey);
				columnMetaData.setEditKeyDisplayable(keyDisplayable);
				columnMetaData.setNullable(nullable);
				columnMetaData.setAutoSequenceName(autoSeqName);
			}
			// Close the resultset for EditorTypeColumns
			rs.close();
			stmt.close();
			
			if (userId != null && !_DEFAULT.equalsIgnoreCase(userId)) {

				HashMap tempColumns = new HashMap();
				for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					columnMetaData = (ColumnMetaData) iterator.next();
					tempColumns.put(columnMetaData.getName(), columnMetaData);
				}

				Vector customDisplayableColumns = new Vector();
				Vector customColumns = new Vector();
				Vector uniqueColumns = new Vector();

				// If the user does't have Customize display columns then check for ALL user id
				sql = buildUserEditorColumnsSql();
				// Execute the query for Editor_Columns & Columns
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, name);
				stmt.setString(2, userId);
				stmt.setString(3, name);
				stmt.setString(4, name);
				stmt.setString(5, userId);
				
				rs = stmt.executeQuery();

				int sortOrder = 1;
				int sortOrderSeq = 1;
				while (rs.next()) {
					colName = rs.getString(UserEditorColumnsTbl._columnName);
					customDisplayableColumns.add(colName);
					sortOrder = rs.getInt(UserEditorColumnsTbl._sortOrder);
					sortOrderSeq = rs.getInt(UserEditorColumnsTbl._sortOrderSeq);
					sortOrder = (sortOrder == AVConstants._DESC) ? AVConstants._DESC : AVConstants._ASC;

					if (tempColumns.containsKey(colName)) {
						columnMetaData = (ColumnMetaData) tempColumns.get(colName);
						columnMetaData.setOrdering(sortOrder);
						columnMetaData.setSortOrderSeq(sortOrderSeq);

						customColumns.add(columnMetaData);
						uniqueColumns.add(columnMetaData.getName());

					}
				}
				rs.close();

				if (!customDisplayableColumns.isEmpty()) {
					for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
						columnMetaData = (ColumnMetaData) iterator.next();
						if (uniqueColumns.contains(columnMetaData.getName())) {
							continue;
						}
						if (columnMetaData.isDisplayable() && !customDisplayableColumns.contains(columnMetaData.getName()) && columnMetaData.getType() != ColumnDataType._CDT_LINK) {
							columnMetaData.setDisplayable(false);
							customColumns.add(columnMetaData);
							uniqueColumns.add(columnMetaData.getName());
						}
						else {
							customColumns.add(columnMetaData);
							uniqueColumns.add(columnMetaData.getName());
						}
					}
					columns = customColumns;
				}
			}
		}
		finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

		return columns;
	}

	private String buildEditorsSql() {
		String sql = "SELECT *" + " FROM " + EditorsTbl._tblName + " WHERE " + EditorsTbl._name + "= ? ";

		return sql;
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
		String sql = "SELECT *" + " FROM " + EditorTypesTbl._tblName + " WHERE " + EditorTypesTbl._name + "= ? " + " AND " + EditorTypesTbl._type + "= ? ";
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

	private String buildUserEditorColumnsSql() {
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

	private void updateEditorsTbl(Connection conn) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorsTbl._tblName + " SET " + EditorsTbl._desc + "=?, " + EditorsTbl._source + "=?  " + "WHERE " + EditorsTbl._name + "=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, _editorMetaData.getDesc());
			pstmt.setString(2, _editorMetaData.getSource());
			pstmt.setString(3, _editorMetaData.getName());

			pstmt.executeUpdate();
		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateEditorTypesTbl(Connection conn) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorTypesTbl._tblName + " SET " + EditorTypesTbl._proc + "=? " + "WHERE " + EditorTypesTbl._name + "=? AND " + EditorTypesTbl._type + "=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, _editorMetaData.getProcedure());
			pstmt.setString(2, _editorMetaData.getName());
			pstmt.setString(3, _editorMetaData.getType());

			pstmt.executeUpdate();
		}
		finally {

			if (pstmt != null)
				pstmt.close();
		}
	}

	private void updateEditorColumnsTbl(Connection conn) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorColumnsTbl._tblName + " SET " + EditorColumnsTbl._aggregatable + "=?, " + EditorColumnsTbl._searchable + "=?, " + EditorColumnsTbl._displayable + "=?, " + EditorColumnsTbl._linkable + "=?, " + EditorColumnsTbl._expr + "=?, " + EditorColumnsTbl._seq
					+ "=?, " + EditorColumnsTbl._key + "=? " + EditorColumnsTbl._searchableMandatory + "=? " + "WHERE " + EditorColumnsTbl._editorName + "=? AND " + EditorColumnsTbl._columnName + "=?";

			pstmt = conn.prepareStatement(sql);

			ColumnMetaData columnMetaData = null;
			Vector columnsMetaData = _editorMetaData.getColumnsMetaData();
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
				pstmt.setString(9, _editorMetaData.getName());
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

	private void updateEditorTypeColumnsTbl(Connection conn) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + EditorTypeColumnsTbl._tblName + " SET " + EditorTypeColumnsTbl._editKey + "=?, " + EditorTypeColumnsTbl._editKeyDisplayable + "=?, " + EditorTypeColumnsTbl._nullable + "=?, " + EditorTypeColumnsTbl._autoSeqName + "=?  " + "WHERE "
					+ EditorTypeColumnsTbl._editorName + "=? AND " + EditorTypeColumnsTbl._type + "=? AND " + EditorTypeColumnsTbl._columnName + "=?";

			pstmt = conn.prepareStatement(sql);

			ColumnMetaData columnMetaData = null;
			Vector columnsMetaData = _editorMetaData.getColumnsMetaData();
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
				pstmt.setString(5, _editorMetaData.getName());
				pstmt.setString(6, _editorMetaData.getType());
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

	private void updateColumnsTbl(Connection conn) throws SQLException {

		PreparedStatement pstmt = null;

		try {

			String sql = "UPDATE " + ColumnsTbl._tblName + " SET " + ColumnsTbl._type + "=?, " + ColumnsTbl._caption + "=?, " + ColumnsTbl._format + "=?, " + ColumnsTbl._unit + "=?, " + ColumnsTbl._combo + "=?, " + ColumnsTbl._comboTblName + "=?, " + ColumnsTbl._comboSelect + "=?, "
					+ ColumnsTbl._comboOrderBy + "=?, " + ColumnsTbl._minval + "=?, " + ColumnsTbl._maxval + "=?, " + ColumnsTbl._regexp + "=?, " + ColumnsTbl._errormsg + "=?, " + ColumnsTbl._value + "=? " +

					"WHERE " + ColumnsTbl._name + "=?";

			pstmt = conn.prepareStatement(sql);

			ColumnMetaData columnMetaData = null;
			Vector columnsMetaData = _editorMetaData.getColumnsMetaData();
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

	private void updateUserEditorColumnsTbl(String name, String userId, Connection conn) throws SQLException {

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
				Vector columnsMetaData = _editorMetaData.getColumnsMetaData();
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

		return Environment.getInstance(getSubSystem()).getDbPoolMgr().getConnection();

	}

	private void releaseConnection(Connection conn) throws SQLException {

		Environment.getInstance(getSubSystem()).getDbPoolMgr().releaseConnection(conn);

	}

	private final static class EditorsTbl {
		private static final String _tblName = "EDITORS";
		private static final String _name = "EDITOR_NAME";
		private static final String _desc = "EDITOR_DESC";
		private static final String _source = "EDITOR_SOURCE_NAME";
		private static final String _initialLookup = "EDITOR_INITIAL_LOOKUP";
		private static final String _interceptor = "EDITOR_INTERCEPTOR";
		private static final String _sourceSQL = "EDITOR_SOURCE_SQL";

	}

	private final static class EditorTypeColumnsTbl {
		private static final String _tblName = "EDITOR_TYPE_COLUMNS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _columnName = "COLUMN_NAME";
		private static final String _type = "EDITOR_TYPE";
		private static final String _editKey = "COLUMN_EDIT_KEY";
		private static final String _editKeyDisplayable = "COLUMN_EDIT_KEY_DISPLAYABLE";
		private static final String _nullable = "COLUMN_NULLABLE";
		private static final String _autoSeqName = "COLUMN_AUTOSEQ_NAME";
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
		private static final String _displayAlignOvd = "COLUMN_DISPLAY_ALIGN_OVD"; // Column display align in the list. Possible values : left / right / center
		private static final String _sizeOvd = "COLUMN_SIZE_OVD"; // TextBox size and optional maxlength eg 10 or 10,30 where size=10 maxlength=30

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
		private static final String _size = "COLUMN_SIZE";
		// TextBox size and optional maxlength eg 10 or 10,30 where size=10 maxlength=30.
		// TextArea rows,cols eg 10,50

	}

	public class UserEditorTypeColumnsTbl {
		private static final String _tblName = "USER_EDITOR_TYPE_COLUMNS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _columnName = "COLUMN_NAME";
		private static final String _type = "EDITOR_TYPE";
		private static final String _userId = "USER_ID";
	}

	public class UserEditorColumnsTbl {
		private static final String _tblName = "USER_EDITOR_COLUMNS";
		private static final String _editorName = "EDITOR_NAME";
		private static final String _columnName = "COLUMN_NAME";
		private static final String _userId = "USER_ID";
		private static final String _seq = "COLUMN_SEQ";
		private static final String _sortOrder = "COLUMN_SORT_ORDER";
		private static final String _sortOrderSeq = "COLUMN_SORT_ORDER_SEQ";
	}

}
