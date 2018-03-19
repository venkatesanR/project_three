package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ejb.EJBException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.addval.ejbutils.server.EditorMetaDataDAO.UserEditorColumnsTbl;
import com.addval.environment.Environment;
import com.addval.metadata.ColumnDataType;
import com.addval.metadata.ColumnInfo;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.EditorMetaData;
import com.addval.ui.UIComponentMetadata;
import com.addval.utils.AVConstants;
import com.addval.utils.Pair;
import com.addval.utils.StrUtl;

/**
 Implementation of EJBSEditorMetaDataBean without EJB to work with spring. Any changes in EJBSEditorMetaDataBean should be replicated in this class
 **/

/**
 * EJBSEditorMetaDataBean Implementaion Class. The client calls only the EJBSEditorMetaData session bean for looking up an editor. This session bean inturn calls the EJBEEditorMetaData entity bean for looking up EditorMetaData from the database.
 *
 * @author AddVal Technology Inc.
 * @author AddVal Technology Inc.
 */
public class EJBSEditorMetaDataUtility implements EJBSEditorMetaData {
	/**
	 * Attributes declaration
	 */
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(EJBSEditorMetaDataUtility.class);
	private Environment _env = null;
	private final static String m_module = "com.addval.ejbutils.server.EJBSEditorMetaDataUtility";
	private EditorMetaDataDAO _dao = null;

	public Environment getEnvironment() {
		return _env;
	}

	public void setEnvironment(Environment env) {
		_env = env;
	}

	public void setDao(EditorMetaDataDAO dao) {
		_dao = dao;
	}

	public EditorMetaDataDAO getDao() {
		return _dao;
	}

	/**
	 * Given an editorName and editorType, this method looks up a serializable EditorMetaData object. This method calls an entity bean EJBEEditorMetaData for looking up the editor details.
	 *
	 * @roseuid 3AF926CA01F1
	 * @J2EE_METHOD -- lookup
	 * @param name
	 * @param type
	 * @return EditorMetaData
	 * @throws RemoteException
	 */
	public EditorMetaData lookup(String name, String type) throws RemoteException {
		return lookup(name, type, "DEFAULT");
	}

	/**
	 * @roseuid 3C87BEE7008E
	 * @J2EE_METHOD -- lookupColumnInfo
	 * @param colName
	 * @return ColumnInfo
	 * @throws RemoteException
	 */
	public ColumnInfo lookupColumnInfo(String colName) throws RemoteException {
		try {
			EditorMetaData metadata = getDao().loadEditorMetadata(colName, "COLUMN_INFO", "DEFAULT");

			ColumnMetaData columnsMetaData = null;

			if (metadata != null) {
				columnsMetaData = metadata.getColumnMetaData(1);
			}

			if (columnsMetaData != null)
				return columnsMetaData.getColumnInfo();
			else
				return null;
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * This method will update the Entity Bean with the relavant information
	 *
	 * @roseuid 3D50040601E2
	 * @J2EE_METHOD -- update
	 * @param editorMetaData
	 * @return EditorMetaData
	 * @throws RemoteException
	 */
	public EditorMetaData update(EditorMetaData editorMetaData) throws RemoteException {
		return update(editorMetaData, "DEFAULT");
	}

	/**
	 * Gets all columns for an Editor irrespective of customization
	 *
	 * @roseuid 3D51A0510390
	 * @J2EE_METHOD -- lookupAllColumns
	 * @param name
	 * @param type
	 * @return Vector
	 * @throws RemoteException
	 */
	public java.util.Vector lookupAllColumns(String name, String type) throws RemoteException {

		try {
			EditorMetaData metadata = getDao().loadEditorMetadata(name, type, "DEFAULT");
			metadata = getDao().lookupEditorComboCache(metadata);

			Vector columns = metadata != null ? metadata.getColumnsMetaData() : null;

			return columns;
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * Added to resolve JRUN Bug with userPrincipals
	 *
	 * @roseuid 3D5D65E8035C
	 * @J2EE_METHOD -- lookup
	 * @param name
	 * @param type
	 * @param userId
	 * @return EditorMetaData
	 * @throws RemoteException
	 */

	public EditorMetaData lookup(String name, String type, String userId) throws RemoteException {
		return lookup(name, type, userId, false);
	}

	public EditorMetaData lookup(String name, String type, String userId, boolean configRole) throws RemoteException {
		try {
			EditorMetaData metadata = null;
			if (type != null && type.equalsIgnoreCase("COLUMN_INFO")) {
				metadata = getDao().loadEditorMetadata(name, type, "DEFAULT");
				metadata = getDao().lookupEditorComboCache(metadata);
			}
			else {
				metadata = getDao().loadEditorMetadata(name, "DEFAULT", "DEFAULT");
				String types[] = type.split(":"); // <USER_GROUPS> : <TEMPLATE_NAME> : <COMPONENT_PREFIX> 
				String userGroup = null;
				String templateName = null;
				String componentPrefix = null;
				
				if (types.length > 0) {
					userGroup = types[0];
				}
				if (types.length > 1) {
					templateName = types[1];
				}
				if (types.length > 2) {
					componentPrefix = types[2];
				}
				
				String pageName = (!StrUtl.isEmptyTrimmed(componentPrefix))?  componentPrefix + "_" + name : name;
				 	
				metadata = getDao().buildUserSearchFilters(metadata, pageName, userGroup, userId, "SAVEFILTERS",configRole);
				metadata = getDao().buildUserTemplates(metadata, pageName, userGroup, userId, "SAVETEMPLATES",configRole);
				metadata = customizeEditorColumns(metadata, name, userGroup, templateName, userId, configRole);
				metadata = getDao().lookupEditorComboCache(metadata);
			}
			return metadata;
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}
	}

	/**
	 * @roseuid 3DFA3AC4035C
	 * @J2EE_METHOD -- update
	 * @param editorMetaData
	 * @param userId
	 * @return EditorMetaData
	 * @throws RemoteException
	 */
	public EditorMetaData update(EditorMetaData editorMetaData, String userId) throws RemoteException {
		try {
			try {

				getDao().saveEditorMetadata(editorMetaData.getName(), editorMetaData.getType(), userId, editorMetaData);

				return editorMetaData;
			}
			catch (Exception oe) {

				throw new EJBException("The Editor :" + editorMetaData.getName() + " is not found in the database. The insert method has not been implemented as yet.");
				// The EditorMetaData was not found in the database and so try inserting the new EditorMetaData
				// rv = home.create( editorMetaData );
			}
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}
	}

	
    public List<Pair> lookupNameLabelPairs(String type) throws RemoteException {
		try {
			try {

				List<Pair>  p = getDao().lookupNameLabelPairs(type);

				return p;
			}
			catch (Exception oe) {
				throw new EJBException("Invalid type: " + type + "unable to retrieve labels");
			}
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}    	
    }
	
	
	private EditorMetaData customizeEditorColumns(EditorMetaData metadata, String editorName, String userGroup, String templateName, String userId, boolean configRole) throws SQLException {
		Vector<ColumnMetaData> columns = metadata.getColumnsMetaData();
		boolean isEditorCustomized = false;
		if (!StrUtl.isEmptyTrimmed(userGroup) && !"DEFAULT".equalsIgnoreCase(userGroup) && metadata.getUserTemplates() != null) {
			HashMap<String, JSONObject> jsonObjectRef = new HashMap<String, JSONObject>();
			List<String> templateNames = metadata.getUserTemplateNames();
			for (UIComponentMetadata uiCmd : metadata.getUserTemplates()) {
				jsonObjectRef.put(uiCmd.getComponentId(),JSONObject.fromObject(uiCmd.getJsonString()));
			}
			JSONObject savedTpl = null;
			boolean selected = false;
			boolean shareToAllUsers = false;
			boolean isConfigRole = false;
			String owner = null;
			String tplUserGroup = null;

			if (!StrUtl.isEmptyTrimmed(templateName)) {
				// Template name search.
				for(String tplName :templateNames){
					if (tplName.equalsIgnoreCase(templateName)) {
						columns = customizeEditorColumns(columns, jsonObjectRef.get(tplName));
						isEditorCustomized = true;
						break;
					}
				}
			}

			if (!isEditorCustomized) {
				// User specific template
				for(String tplName :templateNames){
					savedTpl = jsonObjectRef.get(tplName);
					owner = savedTpl.containsKey("owner") ? savedTpl.getString("owner") : "";
					selected = savedTpl.containsKey("selected") ? savedTpl.getBoolean("selected") : false;
					if (userId.equalsIgnoreCase(owner) && selected) {
						columns = customizeEditorColumns(columns, savedTpl);
						isEditorCustomized = true;
						_logger.debug("User Template Matches.." + userId +"," + tplName);
						break;
					}
				}
			}

			if(isEditorCustomized && !configRole){
				Vector<ColumnMetaData> defaultColumns = getDefaultEditorColumns(metadata.getColumnsMetaData(),templateNames,jsonObjectRef,userGroup);
				if(defaultColumns != null){
					HashMap<String, ColumnMetaData> defaultColumnsRef = new HashMap<String, ColumnMetaData>();
					for (ColumnMetaData columnMetaData : defaultColumns) {
						defaultColumnsRef.put(columnMetaData.getName(), columnMetaData);
					}
					ColumnMetaData colMetaData = null;
					for (ColumnMetaData columnMetaData : columns) {
						if(columnMetaData.isDisplayable()){
							colMetaData = defaultColumnsRef.get(columnMetaData.getName());
							columnMetaData.setDisplayable(colMetaData.isDisplayable());
						}
					}
				}
			}

			if(!isEditorCustomized){
				for(String tplName :templateNames){
					savedTpl = jsonObjectRef.get(tplName);
					tplUserGroup = savedTpl.containsKey("shareToUserGroupName") ? savedTpl.getString("shareToUserGroupName") : "";
					isConfigRole = savedTpl.containsKey("isConfigRole") ? savedTpl.getBoolean("isConfigRole") : false;
					if (isConfigRole && tplUserGroup.equalsIgnoreCase(userGroup)) {
						columns = customizeEditorColumns(columns, savedTpl);
						isEditorCustomized = true;
						break;
					}
				}
			}
			if(!isEditorCustomized){
				for(String tplName :templateNames){
					savedTpl = jsonObjectRef.get(tplName);
					shareToAllUsers = savedTpl.containsKey("shareToAllUsers") && savedTpl.getBoolean("shareToAllUsers");
					isConfigRole = savedTpl.containsKey("isConfigRole") ? savedTpl.getBoolean("isConfigRole") : false;
					if (isConfigRole && shareToAllUsers) {
						columns = customizeEditorColumns(columns, savedTpl);
						isEditorCustomized = true;
						_logger.debug("Share to All User Template ");
						break;
					}
				}
			}
		}
		if (!isEditorCustomized && isEditorCustomized(editorName, userId)) {
			columns = customizeEditorColumns(columns, editorName, userId);
		}
		metadata.setColumnsMetaData(columns);
		return metadata;
	}

	private Vector<ColumnMetaData> getDefaultEditorColumns(Vector<ColumnMetaData> columns,List<String> templateNames,HashMap<String, JSONObject> jsonObjectRef,String userGroup) {
		// User Group specific template
		boolean isConfigRole = false;
		JSONObject savedTpl = null;
		String tplUserGroup = null;
		boolean shareToAllUsers = false;
		for(String tplName :templateNames){
			savedTpl = jsonObjectRef.get(tplName);
			tplUserGroup = savedTpl.containsKey("shareToUserGroupName") ? savedTpl.getString("shareToUserGroupName") : "";
			isConfigRole = savedTpl.containsKey("isConfigRole") ? savedTpl.getBoolean("isConfigRole") : false;
			if (isConfigRole && tplUserGroup.equalsIgnoreCase(userGroup)) {
				return customizeEditorColumns(columns, savedTpl);
			}
		}
		for(String tplName :templateNames){
			savedTpl = jsonObjectRef.get(tplName);
			shareToAllUsers = savedTpl.containsKey("shareToAllUsers") && savedTpl.getBoolean("shareToAllUsers");
			isConfigRole = savedTpl.containsKey("isConfigRole") ? savedTpl.getBoolean("isConfigRole") : false;
			if (isConfigRole && shareToAllUsers) {
				_logger.debug("Share to All User Template ");
				return customizeEditorColumns(columns, savedTpl);
			}
		}
		return null;
	}

	private Vector<ColumnMetaData> customizeEditorColumns(Vector<ColumnMetaData> columns, JSONObject savedTpl) {
		try {
			Vector<ColumnMetaData> customizedColumns = new Vector<ColumnMetaData>();
			HashMap<String, ColumnMetaData> columnsRef = new HashMap<String, ColumnMetaData>();

			Vector<String> uniqueColumnNames = new Vector<String>();
			Vector<String> customizedColumnNames = new Vector<String>();

			for (ColumnMetaData columnMetaData : columns) {
				columnsRef.put(columnMetaData.getName(), columnMetaData);
			}

			if (savedTpl.containsKey("columns")) {
				JSONArray jCustomizedColumns = (JSONArray) savedTpl.get("columns");
				JSONObject jsonColumn = null;
				String columnName = null;
				boolean displayable = false;
				int sortOrderSeq = 1;
				int sortOrder = 1;

				ColumnMetaData colMetaData = null;

				for (Iterator<JSONObject> iterator = jCustomizedColumns.iterator(); iterator.hasNext();) {
					jsonColumn = iterator.next();
					columnName = jsonColumn.getString("columnName");
					displayable = jsonColumn.containsKey("displayable") ? jsonColumn.getBoolean("displayable") : false;
					sortOrderSeq = jsonColumn.containsKey("sortOrderSeq") ? jsonColumn.getInt("sortOrderSeq") : -1;
					sortOrder = jsonColumn.containsKey("sortOrder") ? jsonColumn.getInt("sortOrder") : -1;
					sortOrder = (sortOrder == AVConstants._DESC) ? AVConstants._DESC : AVConstants._ASC;
					customizedColumnNames.add(columnName);
					colMetaData = columnsRef.containsKey(columnName) ? columnsRef.get(columnName) : null;
					if (colMetaData != null && colMetaData.isDisplayable()) {
						colMetaData = (ColumnMetaData) columnsRef.get(columnName);
						colMetaData.setDisplayable(displayable);
						colMetaData.setOrdering(sortOrder);
						colMetaData.setSortOrderSeq(sortOrderSeq);

						customizedColumns.add(colMetaData);
						uniqueColumnNames.add(colMetaData.getName());
					}
				}
				if (!customizedColumnNames.isEmpty()) {
					for (ColumnMetaData columnMetaData : columns) {
						if (uniqueColumnNames.contains(columnMetaData.getName())) {
							continue;
						}
						if (columnMetaData.isDisplayable() && !customizedColumnNames.contains(columnMetaData.getName()) && columnMetaData.getType() != ColumnDataType._CDT_LINK) {
							columnMetaData.setDisplayable(false);
						}
						customizedColumns.add(columnMetaData);
						uniqueColumnNames.add(columnMetaData.getName());
					}
					return customizedColumns;
				}
			}
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		return columns;
	}

	private Vector<ColumnMetaData> customizeEditorColumns(Vector<ColumnMetaData> columns, String name, String userId) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Vector<String> customDisplayableColumns = new Vector<String>();
			HashMap<String, ColumnMetaData> columnsRef = new HashMap<String, ColumnMetaData>();
			Vector<ColumnMetaData> customColumns = new Vector<ColumnMetaData>();
			Vector<String> uniqueColumns = new Vector<String>();

			int sortOrder = 1;
			int sortOrderSeq = 1;
			String colName = null;

			for (ColumnMetaData columnMetaData : columns) {
				columnsRef.put(columnMetaData.getName(), columnMetaData);
			}

			conn = getConnection();
			// If the user does't have Customize display columns then check for ALL user id
			String sql = getDao().buildUserEditorColumnsSql();
			// Execute the query for Editor_Columns & Columns
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, userId);
			stmt.setString(3, name);
			stmt.setString(4, name);
			stmt.setString(5, userId);
			
			rs = stmt.executeQuery();

			while (rs.next()) {
				colName = rs.getString(UserEditorColumnsTbl._columnName);
				customDisplayableColumns.add(colName);
				sortOrder = rs.getInt(UserEditorColumnsTbl._sortOrder);
				sortOrderSeq = rs.getInt(UserEditorColumnsTbl._sortOrderSeq);
				sortOrder = (sortOrder == AVConstants._DESC) ? AVConstants._DESC : AVConstants._ASC;
				if (columnsRef.containsKey(colName)) {
					ColumnMetaData columnMetaData = (ColumnMetaData) columnsRef.get(colName);
					columnMetaData.setOrdering(sortOrder);
					columnMetaData.setSortOrderSeq(sortOrderSeq);
					customColumns.add(columnMetaData);
					uniqueColumns.add(columnMetaData.getName());
				}
			}

			if (!customDisplayableColumns.isEmpty()) {
				for (ColumnMetaData columnMetaData : columns) {
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
				return customColumns;
			}
		}
		catch (SQLException se) {
			_logger.error(se);
			throw new EJBException(se);
		}
		finally {
			try {
				// MS Access throws error if we try to close a closed result set
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					releaseConnection(conn);
				}
			}
			catch (SQLException se) {

				_logger.error(se);
				throw new EJBException(se);

			}
		}
		return columns;
	}

	private boolean isEditorCustomized(String name, String userId) {
		boolean customized = false;
		if (StrUtl.isEmptyTrimmed(userId) || "DEFAULT".equalsIgnoreCase(userId.trim())) {
			return customized;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			String sql = "SELECT 1 FROM USER_EDITOR_COLUMNS WHERE EDITOR_NAME= ? AND USER_ID in( ? ,'ALL') ";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, userId);
			
			rs = stmt.executeQuery();
			customized = rs.next();
		}
		catch (SQLException se) {
			_logger.error(se);
			throw new EJBException(se);
		}
		finally {
			try {
				// MS Access throws error if we try to close a closed result set
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					releaseConnection(conn);
				}
			}
			catch (SQLException se) {

				_logger.error(se);
				throw new EJBException(se);

			}
		}
		return customized;
	}

	private Connection getConnection() throws SQLException {
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	private void releaseConnection(Connection conn) throws SQLException {
		getEnvironment().getDbPoolMgr().releaseConnection(conn);
	}

}
