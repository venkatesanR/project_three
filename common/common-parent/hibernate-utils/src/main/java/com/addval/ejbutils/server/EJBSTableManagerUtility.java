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
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJBException;

import org.apache.log4j.Logger;

import com.addval.dbutils.DBUtl;
import com.addval.ejbutils.dbutils.EJBColumn;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBDateTime;
import com.addval.ejbutils.dbutils.EJBRecord;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.ejbutils.utils.TableManagerInterceptor;
import com.addval.ejbutils.utils.TableManagerInterceptorFactory;
import com.addval.environment.Environment;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.RecordStatus;
import com.addval.utils.AVConstants;
import com.addval.utils.ListUtl;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

/**
 * Implementation of EJBSTableManagerBean without EJB to work with spring. Any changes in EJBSTableManagerBean should be replicated in this class A session bean implementation that enables CRUD functions on a table/view that is represented by EditorMetaData. The bean returns EJBResultSet objects for
 * lookup and update functions
 */
public class EJBSTableManagerUtility implements EJBSTableManager {
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(EJBSTableManagerUtility.class);

	private Environment _env = null;
	private Map<String, Environment> envInstances = null;

	private static final String _module = "com.addval.ejbutils.server.EJBSTableManagerBean";
	private String _serverType = "";
	private int _MAX_RECORDS = 500;
	private String _callerPrincipalName = null;
	private EJBSEditorMetaData _editorMetaDataServer = null;

	private Hashtable _interceptorMap = null;

	public EJBSTableManagerUtility() {

	}

	public Environment getEnvironment() {
		return _env;
	}

	public void setEnvironment(Environment env) {
		_env = env;
		_serverType = _env.getDbPoolMgr().getServerType();
	}

	public void setEnvironmentInstances(Map<String, Environment> envInstances) {
		this.envInstances = envInstances;
	}

	public void setMaxRecords(int aNbrRecords) {
		_MAX_RECORDS = aNbrRecords;
	}

	public int getMaxRecords() {
		return _MAX_RECORDS;
	}

	public void setCallerPrincipalName(String aName) {
		_callerPrincipalName = aName;
	}

	public String getCallerPrincipalName() {
		return _callerPrincipalName;
	}

	public void setEditorMetaDataServer(EJBSEditorMetaData server) {
		_editorMetaDataServer = server;
	}

	public EJBSEditorMetaData getEditorMetaDataServer() {
		return _editorMetaDataServer;
	}

	public void setInterceptorMap(Hashtable aMap) {
		_interceptorMap = aMap;
	}

	public Hashtable getInterceptorMap() {
		return _interceptorMap;
	}

	/**
	 * Given a criteria, this method will retrieve the data into an EJBResultset (performs a 'select') Can also be used to lock a table using the 'for update of' clause
	 *
	 * @throws java.rmi.RemoteException
	 * @roseuid 3B0944AE025C
	 */
	public EJBResultSet lookup(EJBCriteria criteria, boolean lock) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		EditorMetaData editorMetaData = null;
		try {

			// If a row does not exist for the editor type then the editor is
			// not editable
			// But the metadata cache should have an element for the editor
			// name, editor type.\
			// The edit, delete, add, custom icons will be placed in the UI
			// based on whether
			// the link exists as a column in ColumnInfo and Editor_Columns
			// table
			final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);
			final EJBResultSetMetaData metaData = getMetaData(criteria);
			List<EJBSQLParam> params = new ArrayList<EJBSQLParam>();
			String sql = sqlBuilder.buildLookupSql(metaData, criteria, params);

			// add additional sql if the it needs to be locked
			if (lock) {

				Vector keyColumns = metaData.getEditorMetaData().getKeyColumns();
				if (keyColumns != null) {
					Iterator iterator = keyColumns.iterator();
					if (iterator.hasNext()) {
						ColumnMetaData columnMetaData = (ColumnMetaData) iterator.next();
						sql += " for update of " + columnMetaData.getName();
					}
				}
			}

			EJBResultSet result = new EJBResultSet(metaData, criteria);
			editorMetaData = metaData.getEditorMetaData();
			// Execute the SQL Query
			conn = getConnection(editorMetaData);
			stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			setValues(stmt, params);
			rs = stmt.executeQuery();

			// Populate serializable EJBResultSet from SQL ResultSet
			populateData(rs, result);

			return result;
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
				releaseConnection(editorMetaData, conn);
			}
			catch (SQLException se) {
				_logger.error(se);
				throw new EJBException(se);
			}
		}
	}

	/**
	 * Lookup the database and return a resultset based on the EJBCriteria that is provided. Forces the record to be locked
	 *
	 * @param criteria
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3B09445503D1
	 */
	public EJBResultSet lookupForUpdate(EJBCriteria criteria) {
		return lookup(criteria, true);
	}

	/**
	 * Lookup the database and return a resultset based on the EJBCriteria that is provided. This will get a read-only copy - no lock is placed on the table/row
	 *
	 * @param criteria
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @roseuid 3B09445503D1
	 */
	public EJBResultSet lookup(EJBCriteria criteria) {
		return lookup(criteria, false);
	}

	/**
	 * Utility method to get a resultset from a pre-built SQL.
	 *
	 * Currently used by the export function to set max records to a high number
	 *
	 */
	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData, EJBCriteria criteria) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = this.getConnection(editorMetaData);
			stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();

			// make local copy of the the editor meta data to modify
			EditorMetaData localMetaData = (EditorMetaData) editorMetaData.clone();

			// from the sql metadata prune localmetadata
			ResultSetMetaData sqlMetaData = rs.getMetaData();

			int sqlColCount = sqlMetaData.getColumnCount();
			String columnName = null;
			Vector columnList = new Vector(sqlColCount);
			ColumnMetaData colMetaData;

			for (int i = 1; i <= sqlColCount; i++) {
				columnName = sqlMetaData.getColumnName(i);
				if ((colMetaData = localMetaData.getColumnMetaData(columnName)) != null) {
					columnList.add(colMetaData);
				}
			}
			localMetaData.setColumnsMetaData(columnList);
			EJBCriteria ejbCrit = null;
			if (criteria != null) {

				ejbCrit = criteria;
			}
			else {

				ejbCrit = new EJBCriteria(localMetaData.getName(), localMetaData.getType(), localMetaData.getColumnsMetaData());
			}

			EJBResultSet result = new EJBResultSet(new EJBResultSetMetaData(localMetaData), ejbCrit);
			populateData(rs, result);

			return result;
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}
		finally {
			DBUtl.closeFinally(rs, stmt, _logger);
			this.releaseConnection(editorMetaData, conn);
		}
	}

	public EJBResultSet lookup(String sql, EditorMetaData editorMetaData) {
		return lookup(sql, editorMetaData, null);
	}

	/**
	 * Updates the record set based on the status of each record in it (insert/update/delete). Records that fail updates have their status set to _RSS_ERROR
	 *
	 * @param rs
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B0944560057
	 */
	public EJBResultSet update(EJBResultSet rs) throws EJBXRuntime {
		Connection conn = null;
		EditorMetaData editorMetaData = null;
		try {

			editorMetaData = rs.getEJBResultSetMetaData().getEditorMetaData();

			if (!editorMetaData.isEditable())
				throw new EJBXRuntime("Editor is not editable :" + editorMetaData.getName());

			if (rs.getMetaData() == null)
				throw new EJBXRuntime(_module, "MetaData is not set for the resultset to be updated");

			rs.beforeFirst();

			conn = getConnection(editorMetaData);
			int rv = 0;

			// check if there is any interceptor defined for this editor either spring injected or through a factory
			TableManagerInterceptor tblMgrInterceptor = null;
			if (editorMetaData.getInterceptor() != null) {

				if (getInterceptorMap() != null) {
					tblMgrInterceptor = (TableManagerInterceptor) getInterceptorMap().get(editorMetaData.getInterceptor());
				}

				if (tblMgrInterceptor == null) {
					tblMgrInterceptor = TableManagerInterceptorFactory.getInstance(editorMetaData.getInterceptor());
				}

			}

			// perform any before-update operations
			if (tblMgrInterceptor != null) {
				tblMgrInterceptor.setCallerPrincipalName(getCallerPrincipalName());
				tblMgrInterceptor.beforeUpdate(rs);
			}

			while (rs.next()) {
				try {

					switch (rs.getRecord().getStatus()) {

					case RecordStatus._RMS_INSERTED:
						rv = insert(rs, conn);
						break;

					case RecordStatus._RMS_UPDATED:
						rv = update(rs, conn);
						break;

					case RecordStatus._RMS_DELETED:
						rv = delete(rs, conn);
						break;
					default:
						throw new EJBXRuntime(_module, "Record Status is not recognized:" + rs.getRecord().getStatus());
					}

					if (rv == 1)
						rs.getRecord().setSyncStatus(RecordStatus._RSS_SYNC);
					else
						rs.getRecord().setSyncStatus(RecordStatus._RSS_ERROR);
				}
				catch (SQLException se) {

					_logger.error(se);

					String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(se);

					// Catch only SQL Exceptions and set the sync status to
					// error
					rs.getRecord().setSyncStatus(RecordStatus._RSS_ERROR);

					if (errorMessage != null)
						rs.getRecord().setErrorMsg(errorMessage);
					else
						rs.getRecord().setErrorMsg(se.getErrorCode() + ":" + se.getMessage());
				}
			}

			// perform any before-update operations
			if (tblMgrInterceptor != null) {
				tblMgrInterceptor.setCallerPrincipalName(getCallerPrincipalName());
				tblMgrInterceptor.afterUpdate(rs);
			}
			return rs;
		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);
		}
		finally {

			releaseConnection(editorMetaData, conn);
		}
	}

	/**
	 * Similar to the update method, but if one of the records fail to update, the method will throw an exception thus rolling back the full transaction.
	 *
	 * @param rs
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B82E2CB03DD
	 */
	public EJBResultSet updateTransaction(EJBResultSet rs) throws EJBXRuntime {
		Connection conn = null;
		EditorMetaData editorMetaData = null;
		try {

			editorMetaData = rs.getEJBResultSetMetaData().getEditorMetaData();
			if (!editorMetaData.isEditable())
				throw new EJBXRuntime("Editor is not editable :" + editorMetaData.getName());

			if (rs.getMetaData() == null)
				throw new EJBXRuntime(_module, "MetaData is not set for the resultset to be updated");

			// rs.beforeFirst();

			conn = getConnection(editorMetaData);
			int rv = 0;

			// check if there is any interceptor defined for this editor
			TableManagerInterceptor tblMgrInterceptor = null;
			if (editorMetaData.getInterceptor() != null) {
				if (getInterceptorMap() != null) {
					tblMgrInterceptor = (TableManagerInterceptor) getInterceptorMap().get(editorMetaData.getInterceptor());
				}

				if (tblMgrInterceptor == null) {
					tblMgrInterceptor = TableManagerInterceptorFactory.getInstance(editorMetaData.getInterceptor());
				}

				if (tblMgrInterceptor != null) {
					tblMgrInterceptor.setSubSystem(getEnvironment().getName());
				}
			}

			// perform any before-update operations
			if (tblMgrInterceptor != null) {
				tblMgrInterceptor.setCallerPrincipalName(getCallerPrincipalName());
				tblMgrInterceptor.beforeUpdate(rs);
			}

			rs.beforeFirst();

			while (rs.next()) {
				switch (rs.getRecord().getStatus()) {
				case RecordStatus._RMS_INSERTED:
					rv = insert(rs, conn);
					break;

				case RecordStatus._RMS_UPDATED:
					rv = update(rs, conn);
					break;

				case RecordStatus._RMS_DELETED:
					rv = delete(rs, conn);
					break;

				default:
					throw new EJBXRuntime("Record Status is not recognized:" + rs.getRecord().getStatus());
				}

				if (rv == 1)
					rs.getRecord().setSyncStatus(RecordStatus._RSS_SYNC);
				else
					rs.getRecord().setSyncStatus(RecordStatus._RSS_ERROR);
			}

			// perform any after-update operations
			if (tblMgrInterceptor != null) {
				tblMgrInterceptor.setCallerPrincipalName(getCallerPrincipalName());
				tblMgrInterceptor.afterUpdate(rs);
			}

			return rs;
		}
		catch (SQLException se) {

			// log the raw error
			_logger.error(se);

			// translate the SQL error into an user-displayable error
			String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(se);

			// rollback happens
			if (errorMessage != null) {
				// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				// _context.setRollbackOnly();
				throw new EJBXRuntime(errorMessage);
			}
			else {

				throw new EJBException(se);
			}
		}
		catch (Exception e) {

			_logger.error(e);
			throw new EJBException(e);
		}
		finally {

			releaseConnection(editorMetaData, conn);
		}
	}

	/**
	 * Method for updating the Master/Detail Screens. Will do a) MasterTable Insert, b) detail table delete and then c) detail table inserts. All the above operations are done as a single transaction.
	 *
	 * @param masterSet
	 * @param deleteSet
	 * @param insertSet
	 * @return boolean
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @throws java.rmi.RemoteException
	 * @roseuid 3F2A14F800CB
	 */
	public boolean updateTransaction(EJBResultSet masterSet, EJBResultSet deleteSet, EJBResultSet insertSet) throws EJBXRuntime, RemoteException {
		Connection conn = null;
		boolean updateStatus = true;
		String module = getClass().getName();
		EditorMetaData editorMetaData = null;
		try {
			editorMetaData = masterSet.getEJBResultSetMetaData().getEditorMetaData();
			conn = getConnection(editorMetaData);
			// First deal with the parent table info - delete or insert or update

			EJBResultSet masterRS = updateTransaction(masterSet, conn);

			// Parent-Child hasAutoSequenceName() check.
			EJBResultSetMetaData metaData = masterRS.getEJBResultSetMetaData();
			EJBColumn parentColumn = null;
			ColumnMetaData columnMetaData = null;
			int size = metaData.getColumnCount();
			for (int index = 1; index <= size; index++) {
				columnMetaData = metaData.getColumnMetaData(index);
				if (columnMetaData.hasAutoSequenceName()) {
					parentColumn = masterRS.getRecord().getColumn(columnMetaData.getName());
					deleteSet.beforeFirst();
					while (deleteSet.next()) {
						deleteSet.getRecord().getColumn(parentColumn.getName()).setIntValue(parentColumn.getIntValue());
					}
					insertSet.beforeFirst();
					while (insertSet.next()) {
						insertSet.getRecord().getColumn(parentColumn.getName()).setIntValue(parentColumn.getIntValue());
					}
				}
			}
			// Delete the details
			updateTransaction(deleteSet, conn);
			// Now insert based on the details available
			updateTransaction(insertSet, conn);
		}
		catch (SQLException se) {
			// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// _context.setRollbackOnly();
			_logger.error(se);
			String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(se);
			if (errorMessage == null)
				throw new EJBException(se);
			throw new EJBXRuntime(errorMessage);
		}
		finally {
			releaseConnection(editorMetaData, conn);
		}
		return updateStatus;
	}

	/**
	 * Method for updating the Master/Detail Screens. Will do a) MasterTable Insert, b) detail table delete and then c) detail table inserts. All the above operations are done as a single transaction.
	 *
	 * @param masterSet
	 * @param deleteSet
	 * @param insertSet
	 * @return boolean
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @throws java.rmi.RemoteException
	 * @roseuid 3F2A14F800CB
	 */
	public boolean updateTransaction(EJBResultSet masterSet, List<EJBResultSet> deleteSet, List<EJBResultSet> insertSet) throws EJBXRuntime, RemoteException {
		Connection conn = null;
		boolean updateStatus = true;
		String module = getClass().getName();
		EditorMetaData editorMetaData = null;
		try {
			editorMetaData = masterSet.getEJBResultSetMetaData().getEditorMetaData();
			conn = getConnection(editorMetaData);
			// First deal with the parent table info - delete or insert or update

			EJBResultSet masterRS = updateTransaction(masterSet, conn);

			// Parent-Child hasAutoSequenceName() check.
			EJBResultSetMetaData metaData = masterRS.getEJBResultSetMetaData();
			EJBColumn parentColumn = null;
			ColumnMetaData columnMetaData = null;
			int size = metaData.getColumnCount();
			for (int index = 1; index <= size; index++) {
				columnMetaData = metaData.getColumnMetaData(index);
				if (columnMetaData.hasAutoSequenceName()) {
					parentColumn = masterRS.getRecord().getColumn(columnMetaData.getName());

					if (!ListUtl.isEmpty(deleteSet)) {
						for (EJBResultSet deleteRS : deleteSet) {
							deleteRS.beforeFirst();
							while (deleteRS.next()) {
								deleteRS.getRecord().getColumn(parentColumn.getName()).setIntValue(parentColumn.getIntValue());
							}
						}
					}

					if (!ListUtl.isEmpty(insertSet)) {
						for (EJBResultSet insertRS : insertSet) {
							insertRS.beforeFirst();
							while (insertRS.next()) {
								insertRS.getRecord().getColumn(parentColumn.getName()).setIntValue(parentColumn.getIntValue());
							}
						}
					}
				}
			}

			// Delete the details
			if (!ListUtl.isEmpty(deleteSet)) {
				for (EJBResultSet deleteRS : deleteSet) {
					updateTransaction(deleteRS, conn);
				}
			}
			// Now insert based on the details available
			if (!ListUtl.isEmpty(insertSet)) {
				for (EJBResultSet insertRS : insertSet) {
					updateTransaction(insertRS, conn);
				}
			}
		}
		catch (SQLException se) {
			// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			// _context.setRollbackOnly();
			_logger.error(se);
			String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(se);
			if (errorMessage == null)
				throw new EJBException(se);
			throw new EJBXRuntime(errorMessage);
		}
		finally {
			releaseConnection(editorMetaData, conn);
		}
		return updateStatus;
	}

	/*
	 * Mass Update
	 */
	public int updateTransaction(EJBCriteria criteria) throws RemoteException, EJBXRuntime {
		Connection conn = null;
		PreparedStatement pStmt = null;
		EditorMetaData editorMetaData = null;
		int rv = 0;
		try {
			final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);
			final EJBResultSetMetaData metaData = getMetaData(criteria);
			String sql = sqlBuilder.buildUpdateSql(criteria, metaData, getCallerPrincipalName());
			editorMetaData = metaData.getEditorMetaData();
			conn = getConnection(editorMetaData);
			pStmt = conn.prepareStatement(sql);
			rv = pStmt.executeUpdate();
			if (isPostgres()) {
				if (rv == 0) {
					rv = 1;
				}
			}
		}
		catch (SQLException se) {
			_logger.error(se);
			String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(se);
			if (errorMessage == null) {
				throw new EJBException(se);
			}
			throw new EJBXRuntime(errorMessage);
		}
		finally {
			try {
				if (pStmt != null) {
					pStmt.close();
				}
			}
			catch (SQLException ex) {

			}
			releaseConnection(editorMetaData, conn);
		}
		return rv;
	}

	/**
	 * Internal method to perform operations on a given connection. Does not perform any transaction control
	 *
	 * @param rs
	 * @param conn
	 * @return com.addval.ejbutils.dbutils.EJBResultSet
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @throws java.sql.SQLException
	 * @throws IOException
	 * @roseuid 3F2A18EE01F4
	 */
	private EJBResultSet updateTransaction(EJBResultSet rs, Connection conn) throws EJBXRuntime, SQLException {
		if (rs == null)
			return null;
		EditorMetaData editorMetaData = rs.getEJBResultSetMetaData().getEditorMetaData();
		if (!editorMetaData.isEditable())
			throw new EJBXRuntime("Editor is not editable :" + editorMetaData.getName());

		if (rs.getMetaData() == null)
			throw new EJBXRuntime(_module, "MetaData is not set for the resultset to be updated");

		rs.beforeFirst();
		int rv = 0;
		while (rs.next()) {
			switch (rs.getRecord().getStatus()) {
			case RecordStatus._RMS_INSERTED:
				rv = insert(rs, conn);
				break;

			case RecordStatus._RMS_UPDATED:
				rv = update(rs, conn);
				break;

			case RecordStatus._RMS_DELETED:
				rv = delete(rs, conn);
				break;

			default:
				throw new EJBXRuntime("Record Status is not recognized:" + rs.getRecord().getStatus());
			}

			// System.out.println( "RV = " + rv );

			if (rv == 1)
				rs.getRecord().setSyncStatus(RecordStatus._RSS_SYNC);
			else
				rs.getRecord().setSyncStatus(RecordStatus._RSS_ERROR);
		}
		return rs;
	}

	/**
	 * Underlying method to update a record
	 *
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @throws IOException
	 * @roseuid 3B09270801D6
	 */
	private int insert(EJBResultSet rs, Connection conn) throws SQLException {
		int rv = 0;

		final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);

		// if metadata has a procedure defined, use that. Otherwise build the
		// INSERT statement
		if (rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure() != null && rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure().startsWith("CALL")) {
			rv = execFunction(rs, conn);
		}
		else {
			List<EJBSQLParam> params = new ArrayList<EJBSQLParam>();
			String sql = sqlBuilder.buildInsertSql(rs, getCallerPrincipalName(), conn, params);
			rv = execUpdate(sqlBuilder, sql, rs, conn, params);
		}

		return rv;

	}

	/**
	 * Underlying method to update a record
	 *
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @throws IOException
	 * @roseuid 3B09270801F4
	 */
	private int update(EJBResultSet rs, Connection conn) throws SQLException {
		int rv = 0;

		final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);

		// if metadata has a procedure defined, use that. Otherwise build the
		// UPDATE SQL
		if (rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure() != null && rs.getEJBResultSetMetaData().getEditorMetaData().getProcedure().startsWith("CALL")) {
			rv = execFunction(rs, conn);
		}
		else {
			List<EJBSQLParam> params = new ArrayList<EJBSQLParam>();
			String sql = sqlBuilder.buildUpdateSql(rs, getCallerPrincipalName(), params);
			rv = execUpdate(sqlBuilder, sql, rs, conn, params);
		}

		return rv;
	}

	/**
	 * Underlying method to delete a record
	 *
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B0927080213
	 */
	private int delete(EJBResultSet rs, Connection conn) throws SQLException {
		final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);
		List<EJBSQLParam> params = new ArrayList<EJBSQLParam>();
		String sql = sqlBuilder.buildDeleteSql(rs, getCallerPrincipalName(), params);
		return execUpdate(sql, conn, params);
	}

	private int execUpdate(String sql, Connection conn, List<EJBSQLParam> params) throws SQLException {
		PreparedStatement pStmt = null;
		int rv = 0;

		try {
			pStmt = conn.prepareStatement(sql);
			if (params != null) {
				setValues(pStmt, params);
			}
			rv = pStmt.executeUpdate();
		}
		finally {
			if (pStmt != null)
				pStmt.close();
		}

		/*
		 * postgres jdbc driver workaround: until problem is identified
		 *
		 * postgres jdbc driver when working on "instead of views" returns 0 even when successful
		 * If there is a problem an exception gets thrown
		 */
		if (isPostgres()) {
			if (rv == 0)
				rv = 1;
		}

		// If update was successful rv = 1 else rv = 0
		return rv;
	}

	/**
	 * Responsible for executing the sql update using the connection
	 *
	 * @param sql
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B192CB200A3
	 */
	private int execUpdate(String sql, Connection conn) throws SQLException {
		return execUpdate(sql, conn, null);
	}

	/**
	 * Responsible for executing the sql update using the connection
	 *
	 * @param sql
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B192CB200A3
	 */

	private int execUpdate(EJBSQLBuilderUtils sqlBuilder, String sql, EJBResultSet rs, Connection conn, List<EJBSQLParam> params) throws SQLException {
		PreparedStatement pStmt = null;
		int rv = 0;

		try {
			pStmt = conn.prepareStatement(sql);
			if (params != null) {
				setValues(pStmt, params);
			}
			rv = pStmt.executeUpdate();
		}
		finally {
			if (pStmt != null)
				pStmt.close();
		}

		/*
		 * postgres jdbc driver workaround: until problem is identified
		 *
		 * postgres jdbc driver when working on "instead of views" returns 0 even when successful
		 * If there is a problem an exception gets thrown
		 */
		if (isPostgres()) {
			if (rv == 0)
				rv = 1;
		}

		// If update was successful rv = 1 else rv = 0
		return rv;
	}

	private int execUpdate(EJBSQLBuilderUtils sqlBuilder, String sql, EJBResultSet rs, Connection conn) throws SQLException {
		return execUpdate(sqlBuilder, sql, rs, conn, null);

	}

	/**
	 * Responsible for invoking a SQL procedure/function using the connection Parameters are built using the metadata
	 *
	 * @param rs
	 * @param conn
	 * @return int
	 * @throws java.sql.SQLException
	 * @roseuid 3B1A6CBC02B5
	 */
	private int execFunction(EJBResultSet rs, Connection conn) throws SQLException {

		// Stored procedure arguments are always input parameters
		// The stored procedure will always return 1 output parameter
		// The stored procedure will NOT have any INOUT parameters

		CallableStatement cstmt = null;

		try {
			final EJBSQLBuilderUtils sqlBuilder = new EJBSQLBuilderUtils(_serverType);

			cstmt = conn.prepareCall(sqlBuilder.buildFunction(rs));

			sqlBuilder.buildFuncParams(rs, cstmt);

			// Execute the Function
			cstmt.executeQuery();

			int rv = cstmt.getInt(1);
			cstmt.close();

			return rv > 0 ? 1 : 0;
		}
		finally {

			if (cstmt != null)
				cstmt.close();
		}
	}

	/**
	 * Converts from a JDBC resultset to an EJBResultSet
	 *
	 * @param rs
	 * @param result
	 * @throws java.sql.SQLException
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B0D9A9902B1
	 */
	private void populateData(ResultSet rs, EJBResultSet result) throws SQLException, EJBXRuntime {

		// Environment.getInstance( _subSystem ).getLogFileMgr( _module
		// ).traceEnter( "populateData()" );
		int count = rs.getMetaData().getColumnCount();
		Vector customColumns = result.getEJBResultSetMetaData().getEditorMetaData().getCustomColumns();
		int customCount = customColumns == null ? 0 : customColumns.size();

		if (result.getMetaData().getColumnCount() != count + customCount)
			throw new EJBXRuntime(_module, "Column count in MetaData: " + result.getMetaData().getColumnCount() + " does not match SQL column count: " + count);

		Vector records = new Vector();
		Vector columns = null;
		int index = 0;
		ColumnMetaData columnMetaData = null;
		int rowIndex = 0;
		int action = result.getEJBCriteria().getPageAction();
		int pageSize = result.getEJBCriteria().getPageSize();
		int currPosition = result.getEJBCriteria().getCurrPosition() == 0 ? 1 : result.getEJBCriteria().getCurrPosition();

		// Move the resultset to the starting position (before first record)
		rs.beforeFirst();

		if (pageSize != -1) {

			int rsCount = 0;
			// To see if there are any rows
			if (rs.next())
				rsCount++;

			// Set rs to ordinal position
			rs.beforeFirst();

			// first, depending on the paging operation and current page,
			// setup the resultset position
			if (rsCount != 0) {

				switch (action) {

				case AVConstants._FETCH_FORWARD:
					rs.absolute(currPosition + pageSize - 1);
					result.getEJBCriteria().setCurrPosition(currPosition + pageSize);
					break;

				case AVConstants._FETCH_BACKWARD:
					rs.absolute(currPosition - pageSize - 1);
					result.getEJBCriteria().setCurrPosition(currPosition - pageSize);
					break;

				case AVConstants._FETCH_FIRST:
					result.getEJBCriteria().setCurrPosition(1);
					break;

				case AVConstants._FETCH_LAST:
					rsCount = 0;
					while (rs.next())
						rsCount++;
					result.getEJBCriteria().setRowCount(rsCount);
					rs.beforeFirst();
					// Get only the integral part
					rsCount = rsCount / pageSize;
					rs.absolute(pageSize * rsCount);
					result.getEJBCriteria().setCurrPosition((pageSize * rsCount) + 1);
					break;

				case AVConstants._UNDEF:
					rsCount = 0;
					while (rs.next())
						rsCount++;
					result.getEJBCriteria().setRowCount(rsCount);
					rs.beforeFirst();
					if (currPosition > 1 && currPosition < rsCount) {
						rs.absolute(currPosition - 1);
					}
					else if (currPosition > rsCount) {
						rsCount = rsCount / pageSize;
						rs.absolute(pageSize * rsCount);
						result.getEJBCriteria().setCurrPosition((pageSize * rsCount) + 1);
					}
					break;

				default:
					throw new EJBXRuntime(_module, "Paging Action in EJBCriteria is not recognized : " + String.valueOf(action));
				}
			}
		}
		else {

			pageSize = _MAX_RECORDS;
		}

		// at this point, the resultset has been positioned and rowIndex,
		// pageSize are set.
		// just look through and read
		while (rs.next() && ++rowIndex <= pageSize) {

			columns = new Vector(count);

			// Populate the columns in the record
			for (index = 1; index <= count; index++) {
				// get the columnMetadata for the resultset column
				String columnName = rs.getMetaData().getColumnName(index);
				columnMetaData = result.getEJBResultSetMetaData().getColumnMetaData(columnName);
				if (columnMetaData == null)
					throw new EJBXRuntime(_module, String.format("ResultSet column %s does not have any MetaData configured ", columnName));
				columns.add(createColumn(rs, columnMetaData, index));
			}
			records.add(new EJBRecord(columns));
		}

		result.setRecords(records);
	}

	/**
	 * Build an EJBColumn from the JDBC resultset (using the column-type etc. in the column metadata)
	 *
	 * @param rs
	 * @param columnMetaData
	 * @param index
	 * @return com.addval.ejbutils.dbutils.EJBColumn
	 * @throws java.sql.SQLException
	 * @throws com.addval.ejbutils.utils.EJBXRuntime
	 * @roseuid 3B0E88A10367
	 */
	private EJBColumn createColumn(ResultSet rs, ColumnMetaData columnMetaData, int index) throws SQLException, EJBXRuntime {

		EJBColumn column = new EJBColumn(columnMetaData);

		String value = null;
		Clob clob = null;
		Blob blob = null;
		boolean columnNull = false;
		byte fileBytes[] = null;

		if (columnMetaData.getType() == ColumnDataType._CDT_CLOB) {
			clob = rs.getClob(index);
			columnNull = (clob == null);
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_BLOB) {
			blob = rs.getBlob(index);
			columnNull = (blob == null);
		}
		else if (columnMetaData.getType() == ColumnDataType._CDT_FILE) {
			// fileBytes = rs.getBytes(index); // We shouldn't read ColumnDataType._CDT_FILE column value for select due huge size.
			columnNull = true;
		}
		else {
			value = rs.getString(index);
			columnNull = value == null;
		}

		if (columnNull) {
			column.setNull();
			return column;
		}
		switch (columnMetaData.getType()) {

		case ColumnDataType._CDT_STRING:
		case ColumnDataType._CDT_USER:
			column.setStrValue(value);
			break;

		case ColumnDataType._CDT_INT:
			column.setIntValue(rs.getInt(index));
			break;

		case ColumnDataType._CDT_LONG:
		case ColumnDataType._CDT_VERSION:
			column.setLongValue(rs.getLong(index));
			break;

		case ColumnDataType._CDT_DOUBLE:
			column.setDblValue(rs.getDouble(index));
			break;

		case ColumnDataType._CDT_FLOAT:
			column.setFloatValue(rs.getFloat(index));
			break;

		case ColumnDataType._CDT_TIME:
			column.setTimeValue(rs.getInt(index));
			break;

		case ColumnDataType._CDT_CHAR:
			column.setByteValue(rs.getByte(index));
			break;

		case ColumnDataType._CDT_BOOLEAN:
			column.setStrValue(value);
			column.setBoolValue(("Y".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)));
			break;

		case ColumnDataType._CDT_DOW:
			column.setDowValue(rs.getInt(index));
			break;

		case ColumnDataType._CDT_SHORT:
			column.setShortValue(rs.getShort(index));
			break;

		case ColumnDataType._CDT_KEY:
			column.setKeyValue(value);
			break;

		case ColumnDataType._CDT_DATE:
			column.setDateTimeValue(new EJBDateTime(rs.getDate(index, AVConstants._GMT_CALENDAR)));
			break;

		case ColumnDataType._CDT_DATETIME:
		case ColumnDataType._CDT_TIMESTAMP:
			column.setDateTimeValue(new EJBDateTime(new java.sql.Date(rs.getTimestamp(index, AVConstants._GMT_CALENDAR).getTime())));
			break;

		case ColumnDataType._CDT_CLOB:
			column.setStrValue(DBUtl.clobToString(clob, column.getName()));
			break;

		case ColumnDataType._CDT_FILE:
			column.setObject(fileBytes);
			break;

		case ColumnDataType._CDT_LINK:
			break;

		default:
			throw new EJBXRuntime(_module, "Type of the column is not recognized: " + columnMetaData.getType());
		}
		return column;
	}

	/**
	 * Lookup the metadata for an editor
	 *
	 * @param editorName
	 * @param editorType
	 * @return com.addval.ejbutils.dbutils.EJBResultSetMetaData
	 * @roseuid 3B1A6EC10363
	 */
	private EJBResultSetMetaData getMetaData(EJBCriteria criteria) {
		try {
			if (criteria.getEditormetadata() != null) {
				return new EJBResultSetMetaData(criteria.getEditormetadata());
			}
			else {
				return new EJBResultSetMetaData(getEditorMetaDataServer().lookup(criteria.getEditorName(), criteria.getEditorType()));
			}
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);

		}
	}

	/**
	 * Get a JDBC connection from the pool
	 *
	 * @return java.sql.Connection
	 * @roseuid 3B09C0CF03B7
	 */
	private Connection getConnection(EditorMetaData editorMetaData) {
		if (editorMetaData != null && !StrUtl.isEmptyTrimmed(editorMetaData.getEnvSpringBeanId()) && envInstances != null && envInstances.containsKey(editorMetaData.getEnvSpringBeanId())) {
			return envInstances.get(editorMetaData.getEnvSpringBeanId()).getDbPoolMgr().getConnection();
		}
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	private boolean isPostgres() {
		if (_serverType != null)
			return _serverType.equalsIgnoreCase(com.addval.utils.AVConstants._POSTGRES);
		else
			return false;
	}

	/**
	 * Release a JDBC connection back to the pool
	 *
	 * @param conn
	 * @roseuid 3B09C0E002A3
	 */
	private void releaseConnection(EditorMetaData editorMetaData, Connection conn) {
		if (conn != null) {
			if (editorMetaData != null && !StrUtl.isEmptyTrimmed(editorMetaData.getEnvSpringBeanId()) && envInstances != null && envInstances.containsKey(editorMetaData.getEnvSpringBeanId())) {
				envInstances.get(editorMetaData.getEnvSpringBeanId()).getDbPoolMgr().releaseConnection(conn);
			}
			else {
				getEnvironment().getDbPoolMgr().releaseConnection(conn);
			}
		}
	}

	private void setValues(PreparedStatement stmt, List<EJBSQLParam> params) throws SQLException, XRuntime {
		for (EJBSQLParam param : params) {
			switch (param.getType()) {
			case Types.VARCHAR:
				stmt.setString(param.getIndex() + 1, (String) param.getValue());
				break;
			case Types.INTEGER:
				stmt.setInt(param.getIndex() + 1, (Integer) param.getValue());
				break;
			case Types.BIGINT:
				stmt.setLong(param.getIndex() + 1, (Long) param.getValue());
				break;
			case Types.SMALLINT:
				stmt.setShort(param.getIndex() + 1, (Short) param.getValue());
				break;
			case Types.DOUBLE:
				stmt.setDouble(param.getIndex() + 1, (Double) param.getValue());
				break;
			case Types.FLOAT:
				stmt.setFloat(param.getIndex() + 1, (Float) param.getValue());
				break;
			case Types.BLOB:
				if (param.getValue() instanceof String) {
					String value = (String) param.getValue();
					if (value.length() > Integer.MAX_VALUE) {
						throw new XRuntime(getClass().getName(), " (Clob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + value.length());
					}
					stmt.setCharacterStream(param.getIndex() + 1, getBlobReader(value), value.length());
				}
				else {
					Object fileObject = param.getValue();
					byte[] byteArray = (byte[]) fileObject;
					if (byteArray.length > Integer.MAX_VALUE) {
						throw new XRuntime(getClass().getName(), "(Blob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + byteArray.length);
					}
					ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
					stmt.setBinaryStream(param.getIndex() + 1, inputStream, byteArray.length);
				}
				break;
			case Types.CLOB:
				String value = (String) param.getValue();
				if (value.length() > Integer.MAX_VALUE) {
					throw new XRuntime(getClass().getName(), "(Clob column) could support only upto " + Integer.MAX_VALUE + " characters. The input is of length " + value.length());
				}
				stmt.setCharacterStream(param.getIndex() + 1, getClobReader(value), value.length());
				break;
			}
		}
	}

	protected StringReader getClobReader(String value) {
		return new StringReader(value);
	}

	protected StringReader getBlobReader(String value) {
		return new StringReader(value);
	}
}
