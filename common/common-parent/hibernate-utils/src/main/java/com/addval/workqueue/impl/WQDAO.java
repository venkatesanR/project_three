package com.addval.workqueue.impl;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;

import org.apache.log4j.Logger;

import com.addval.dbutils.DAOSQLStatement;
import com.addval.dbutils.DAOTxtUtils;
import com.addval.dbutils.DAOUtils;
import com.addval.ejbutils.server.EJBSEditorMetaData;
import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;
import com.addval.utils.AVConstants;
import com.addval.utils.StrUtl;
import com.addval.wqutils.metadata.WQMetaData;
import com.addval.wqutils.utils.WQConstants;

public class WQDAO {
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WQDAO.class);
	private static final String _WQ_DAO_SQL = "WQDAOSQL";
    private static final String _USER_WORKQUEUE_EDITORNAMES = "getUserWorkQueueEditorNames";
    private static final String _UPDATE_MESSAGESTATUS = "updateMessageStatus";

	private Environment _environment = null;
	private Map<String, Environment> envInstances = null;
	private EJBSEditorMetaData _editorMetaDataServer = null;

	public void setEnvironment(Environment environment) {
		_environment = environment;
	}

	public Environment getEnvironment() {
		return _environment;
	}

	public EJBSEditorMetaData getEditorMetaDataServer() {
		return _editorMetaDataServer;
	}

	public void setEditorMetaDataServer(EJBSEditorMetaData editorMetaDataServer) {
		this._editorMetaDataServer = editorMetaDataServer;
	}

	public void setEnvironmentInstances(Map<String, Environment> envInstances) {
		this.envInstances = envInstances;
	}
	

	public String validateQueue(String queue) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = getConnection();
			//String sql = "SELECT QUEUE_NAME FROM QUEUES WHERE QUEUE_NAME = ? OR TO_CHAR(QUEUE_NBR) = ?";
			String sql = "SELECT QUEUE_NAME FROM QUEUES WHERE QUEUE_NAME = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, queue);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString( "QUEUE_NAME" );
			}
			else {
				throw new ObjectNotFoundException("Queue with Name / No : " + queue + " not found in the database");
			}
		}
		catch (SQLException ex) {
			_logger.error("WQDAO:validateQueue" + ex);
			throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error("WQDAO:validateQueue" + ex);
			throw new EJBException(ex);
		}
		finally {
			try {
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
			catch (SQLException ex) {
				_logger.error("WQDAO:validateQueue" +ex);
				throw new EJBException(ex);
			}
		}
	}

	public String validateQueueForUser(String queue, String username) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = getConnection();

			/*String sql = "SELECT QUG.QUEUE_NAME " +
					"FROM USER_PROPERTIES UP, QUEUE_USER_GROUP_QUEUE QUG " +
					"WHERE UP.PROPERTY_VALUE = QUG.Q_USER_GRP_NAME " +
					"AND UPPER (PROPERTY_NAME) = UPPER ('Q_USER_GRP_NAME') " +
					"AND UPPER (USER_NAME) = UPPER (?) " +
					"AND UPPER (QUG.QUEUE_NAME) = UPPER (?)";*/

			/*String sql = "SELECT UNIQUE  QUEUE_NAME FROM QUEUE_GROUP_DETAILS_V " +
					"WHERE UPPER (USER_NAME) = UPPER (?) " +
					"AND (UPPER (QUEUE_NAME) = UPPER (?) OR TO_CHAR(QUEUE_NBR) = ?) ";*/

			String sql = "SELECT UNIQUE  QUEUE_NAME FROM QUEUE_GROUP_DETAILS_V " +
					"WHERE UPPER (USER_NAME) = UPPER (?) " +
					"AND (UPPER (QUEUE_NAME) = UPPER (?) ) ";


			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, queue);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString( "QUEUE_NAME" );
			}
			else {
				throw new ObjectNotFoundException("Queue with Name : " + queue + " not allowed for the user : " + username);
			}
		}
		catch (SQLException ex) {
			_logger.error("WQDAO:validateQueue" + ex);
			throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error("WQDAO:validateQueue" + ex);
			throw new EJBException(ex);
		}
		finally {
			try {
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
			catch (SQLException ex) {
				_logger.error("WQDAO:validateQueue" +ex);
				throw new EJBException(ex);
			}
		}
	}

	public WQMetaData getWQMetaData(String queue) {
		WQMetaData wqMetaData = null;

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			//String sql = "select * from " + WQConstants.QueuesTbl.QUEUES_TBL + " where " + WQConstants.QueuesTbl.QUEUE_NAME + "='" + queue + "' OR TO_CHAR(" + WQConstants.QueuesTbl.QUEUE_NBR + ")='" + queue + "' order by " + WQConstants.QueuesTbl.QUEUE_GROUP_NAME;
			String sql = "select * from " + WQConstants.QueuesTbl.QUEUES_TBL + " where " + WQConstants.QueuesTbl.QUEUE_NAME + "=?  order by " + WQConstants.QueuesTbl.QUEUE_GROUP_NAME;
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, queue);
			rs = stmt.executeQuery();
			if (rs.next()) {
				/*wqMetaData = new WQMetaData(rs.getString(WQConstants.QueuesTbl.QUEUE_NAME), rs.getInt(WQConstants.QueuesTbl.QUEUE_NBR), rs.getString(WQConstants.QueuesTbl.QUEUE_DESC), rs.getString(WQConstants.QueuesTbl.DEFAULT_PROMPT_DEFINITION), getEditorMetaData(rs
						.getString(WQConstants.QueuesTbl.EDITOR_NAME)), rs.getString(WQConstants.QueuesTbl.QUEUE_GROUP_NAME), rs.getString(WQConstants.QueuesTbl.QUEUE_CONDITION), rs.getLong(WQConstants.QueuesTbl.QUEUE_STATUS_TIMEOUT), rs.getLong(WQConstants.QueuesTbl.QUEUE_AUTOREFRESH_TIME));*/
				wqMetaData = new WQMetaData(rs.getString(WQConstants.QueuesTbl.QUEUE_NAME), rs.getString(WQConstants.QueuesTbl.QUEUE_DESC), rs.getString(WQConstants.QueuesTbl.DEFAULT_PROMPT_DEFINITION), getEditorMetaData(rs
						.getString(WQConstants.QueuesTbl.EDITOR_NAME)), rs.getString(WQConstants.QueuesTbl.QUEUE_GROUP_NAME), rs.getString(WQConstants.QueuesTbl.QUEUE_CONDITION), rs.getLong(WQConstants.QueuesTbl.QUEUE_STATUS_TIMEOUT), rs.getLong(WQConstants.QueuesTbl.QUEUE_AUTOREFRESH_TIME));

				try {
					wqMetaData.setQueueNewMessageTime(rs.getLong(WQConstants.QueuesTbl.QUEUE_NEWMESSAGE_TIME));
					wqMetaData.setProcessAction(rs.getString(WQConstants.QueuesTbl.PROCESS_ACTION));
					wqMetaData.setDeleteAction(rs.getString(WQConstants.QueuesTbl.DELETE_ACTION));
				}
				finally {
					// do nothing during for migration
				}
			}
		}
		catch (SQLException ex) {
			_logger.error("WQDAO:getWQMetaData" + ex);
			throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error("WQDAO:getWQMetaData" +ex);
			throw new EJBException(ex);
		}
		finally {
			try {
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
			catch (SQLException ex) {
				_logger.error("WQDAO:getWQMetaData" + ex);
				throw new EJBException(ex);
			}
		}
		return wqMetaData;
	}

	public void populateQueueStatus(Hashtable filters, WQMetaData wqMetaData) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			StringBuffer sqlBuf = new StringBuffer();
			sqlBuf.append("SELECT COUNT(*) AS COUNT,");
			sqlBuf.append(WQConstants._MESSAGE_STATUS);
			sqlBuf.append(" FROM " + getSource(wqMetaData));
			String whereClause = wqMetaData.getWhereClause(filters);
			if (whereClause != null && whereClause.length() > 0) {
				sqlBuf.append(" WHERE ").append(whereClause);
			}
			sqlBuf.append(" GROUP BY " + WQConstants._MESSAGE_STATUS);
			//String sql = customSQLOvd(wqMetaData.getEditorMetaData().getName(), sqlBuf.toString());
			String sql = sqlBuf.toString();

			conn = getConnection(wqMetaData.getEditorMetaData());
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			wqMetaData.setMessageInProcessCount(0);
			wqMetaData.setMessageUnProcessedCount(0);

			String messageStatus = null;
			while (rs.next()) {
				messageStatus = rs.getString(WQConstants._MESSAGE_STATUS);

				if (messageStatus == null) {
					messageStatus = WQConstants._STATUS_UNPROCESSED;
				}

				if (messageStatus.equals(WQConstants._STATUS_INPROCESS)) {
					wqMetaData.setMessageInProcessCount(rs.getInt("COUNT"));
				}
				else if (messageStatus.equals(WQConstants._STATUS_UNPROCESSED)) {
					wqMetaData.setMessageUnProcessedCount(rs.getInt("COUNT"));
				}
				else if (messageStatus.equals( WQConstants._STATUS_PROCESSED )) {
					wqMetaData.setMessageProcessedCount(rs.getInt("COUNT"));
				}
			}
		}
		catch (SQLException ex) {
			_logger.error("WQDAO:populateQueueStatus" + ex);
			throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error("WQDAO:populateQueueStatus" + ex);
			throw new EJBException(ex);
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					releaseConnection(wqMetaData.getEditorMetaData(),conn);
				}
			}
			catch (SQLException ex) {
				_logger.error(ex);
				throw new EJBException(ex);
			}
			catch (Exception ex) {
				_logger.error(ex);
				throw new EJBException(ex);
			}
		}

	}

	public void populateQueueStatusForUser(Hashtable filters, WQMetaData wqMetaData, String username) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			StringBuffer sqlBuf = new StringBuffer();

			/*sqlBuf.append("SELECT   MESSAGE_COUNT, MESSAGE_STATUS FROM QUEUE_GROUP_DETAILS_V " +
					"WHERE UPPER (USER_NAME) = UPPER (?) " +
					"AND (UPPER (QUEUE_NAME) = UPPER (?) OR TO_CHAR(QUEUE_NBR) = ?)");*/

			sqlBuf.append("SELECT   MESSAGE_COUNT, MESSAGE_STATUS FROM QUEUE_GROUP_DETAILS_V " +
					"WHERE UPPER (USER_NAME) = UPPER (?) " +
					"AND (UPPER (QUEUE_NAME) = UPPER (?))");


			/*sqlBuf.append("SELECT COUNT (1) AS MESSAGE_COUNT, MESSAGE_STATUS " +
					"FROM USER_PROPERTIES UP, QUEUE_USER_GROUP_QUEUE QUG, WQ_MANUAL_CAR WMC " +
					"WHERE UP.PROPERTY_VALUE = QUG.Q_USER_GRP_NAME " +
					"AND WMC.QUEUE_ADDRESS1 = QUG.QUEUE_NAME " +
					"AND UPPER (PROPERTY_NAME) = UPPER ('Q_USER_GRP_NAME') " +
					"AND UPPER (USER_NAME) = UPPER (?) " +
					"AND UPPER (WMC.QUEUE_ADDRESS1) = UPPER (?) ");
			sqlBuf.append(" GROUP BY " + WQConstants._MESSAGE_STATUS);*/

			String sql = sqlBuf.toString();

			conn = getConnection(wqMetaData.getEditorMetaData());
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, wqMetaData.getQueueName());
			rs = stmt.executeQuery();

			wqMetaData.setMessageInProcessCount(0);
			wqMetaData.setMessageUnProcessedCount(0);

			String messageStatus = null;
			boolean recordsFound = false;
			int inProcessMessageCount=0;
			int unProcessMessageCount=0;
			int processMessageCount=0;
			while (rs.next()) {

				recordsFound=true;
				messageStatus = rs.getString(WQConstants._MESSAGE_STATUS);

				if (messageStatus == null) {
					messageStatus = WQConstants._STATUS_UNPROCESSED;
				}

				if (messageStatus.equals(WQConstants._STATUS_INPROCESS)) {
					inProcessMessageCount = inProcessMessageCount + rs.getInt("MESSAGE_COUNT");
				}
				else if (messageStatus.equals(WQConstants._STATUS_UNPROCESSED)) {
					unProcessMessageCount = unProcessMessageCount + rs.getInt("MESSAGE_COUNT");
				}
				else if (messageStatus.equals( WQConstants._STATUS_PROCESSED )) {
					processMessageCount = processMessageCount + rs.getInt("MESSAGE_COUNT");
				}

			}

			if(recordsFound) {

				wqMetaData.setMessageInProcessCount(inProcessMessageCount);
				wqMetaData.setMessageUnProcessedCount(unProcessMessageCount);
				wqMetaData.setMessageProcessedCount(processMessageCount);
			}
		}
		catch (SQLException ex) {
			_logger.error("WQDAO:populateQueueStatus" + ex);
			throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error("WQDAO:populateQueueStatus" + ex);
			throw new EJBException(ex);
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					releaseConnection(wqMetaData.getEditorMetaData(), conn);
				}
			}
			catch (SQLException ex) {
				_logger.error(ex);
				throw new EJBException(ex);
			} catch (Exception ex) {
				_logger.error(ex);
				throw new EJBException(ex);
			}
		}

	}

	public void populateNewMessageCount(Hashtable filters, WQMetaData wqMetaData) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			StringBuffer sqlBuf = new StringBuffer();
			sqlBuf.append( "SELECT COUNT(*) AS COUNT" );
			sqlBuf.append( " FROM " + wqMetaData.getEditorMetaData().getSource() + " " );
			String whereClause =  wqMetaData.getNewQueueMessageCountWhereClause(filters);
			if(whereClause != null && whereClause.length() > 0) {
				sqlBuf.append( " WHERE " ).append(whereClause);
			}

			String sql = sqlBuf.toString();
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			wqMetaData.setNewMessageUnProcessedCount( 0 );
			while(rs.next()) {
				wqMetaData.setNewMessageUnProcessedCount( rs.getInt( "COUNT" ) );
			}

		}
		catch (SQLException ex) {
			_logger.error("WQDAO:populateNewMessageCount" +ex);
			throw new EJBException(ex);
		}
		catch (Exception e) {
			_logger.error(e);
			throw new EJBException(e);
		}
		finally {
			try {
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
			catch (SQLException ex) {
				_logger.error("WQDAO:populateNewMessageCount" +ex);
				throw new EJBException(ex);
			}
		}
	}

	public void updateUnProcessMessage(String userName) {
        //Update Message Status to UNPROCESSED.
        Hashtable sqls = (Hashtable)getEnvironment().getCacheMgr().get( _WQ_DAO_SQL );
        updateUnProcessMessage(userName,sqls);
	}

	public void updateUnProcessMessage(String userName, Hashtable sqls) {
        Connection 		  conn  		= null;
		PreparedStatement selectpstmt 		= null;
        Statement         updatestmt 		= null;
		ResultSet		  rs    		= null;

		if (sqls == null)
			return;

		try {
			//Update Message Status to UNPROCESSED.
			String  editorSourceName    = null;
			Map<String, String> map = new HashMap<String, String>();
			map.put("userName",userName);

			DAOSQLStatement selectStatement 	= getDAOSQLStatement(sqls,_USER_WORKQUEUE_EDITORNAMES );
			DAOUtils 	 	selectUtils		    = new DAOUtils( selectStatement, getServerType() );
			DAOSQLStatement updateStatement 	= getDAOSQLStatement(sqls, _UPDATE_MESSAGESTATUS );
			DAOTxtUtils 	updateUtils		    = new DAOTxtUtils( updateStatement, getServerType() );

			conn  = getConnection();
			updatestmt = conn.createStatement();
			selectpstmt = conn.prepareStatement( selectStatement.getSQL( getServerType() ) );
			selectUtils.setProperties(selectpstmt,map);
			rs 	  = selectpstmt.executeQuery();

			while (rs.next()) {
				editorSourceName   = (String) selectUtils.getProperty( rs ,  "editorSourceName" );
				map.put("editorSourceName",editorSourceName);
				updateUtils.setProperties(map);
				updatestmt.addBatch( updateUtils.getText(map) );
			}
			updatestmt.executeBatch();
		}
		catch (SQLException ex) {
			_logger.error("WQDAO:updateUnProcessMessage" +ex);
		}
		catch(Exception ex) {
			_logger.error("WQDAO:updateUnProcessMessage" +ex);
		}
		finally {
			try {
				if (rs    != null) rs.close();
				if (selectpstmt != null) selectpstmt.close();
				if (updatestmt != null) updatestmt.close();
				if (conn  != null) releaseConnection( conn );
			}
			catch( SQLException ex) {
				_logger.error("WQDAO:updateUnProcessMessage" +ex);
			}
		}
    }


	private EditorMetaData getEditorMetaData(String editorName) throws RemoteException {
		EditorMetaData editorMetaData = getEditorMetaDataServer().lookup(editorName, AVConstants._DEFAULT);
		return (editorMetaData != null) ? (EditorMetaData) editorMetaData.clone() : null;
	}

	private Connection getConnection()
	{
		return getEnvironment().getDbPoolMgr().getConnection();
	}
	
	private Connection getConnection(EditorMetaData editorMetaData) throws RemoteException{
		if (editorMetaData != null && !StrUtl.isEmptyTrimmed(editorMetaData.getEnvSpringBeanId()) && envInstances != null && envInstances.containsKey(editorMetaData.getEnvSpringBeanId())) {
			return envInstances.get(editorMetaData.getEnvSpringBeanId()).getDbPoolMgr().getConnection();
		}
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	private java.lang.String getServerType(){
 		return getEnvironment().getDbPoolMgr().getServerType();
    }


	private void releaseConnection(Connection conn) throws SQLException {
		getEnvironment().getDbPoolMgr().releaseConnection(conn);
	}
	
	private void releaseConnection(EditorMetaData editorMetaData, Connection conn) throws RemoteException{
		if (conn != null) {
			if (editorMetaData != null && !StrUtl.isEmptyTrimmed(editorMetaData.getEnvSpringBeanId()) && envInstances != null && envInstances.containsKey(editorMetaData.getEnvSpringBeanId())) {
				envInstances.get(editorMetaData.getEnvSpringBeanId()).getDbPoolMgr().releaseConnection(conn);
			}
			else {
				getEnvironment().getDbPoolMgr().releaseConnection(conn);
			}
		}
	}

	private DAOSQLStatement getDAOSQLStatement(Hashtable sqls,String type) {
        return (DAOSQLStatement)sqls.get( getEnvironment().getCnfgFileMgr().getPropertyValue( type, type ) );
    }

	public List <String>getQueueNames()
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			String sql = "SELECT QUEUE_NAME FROM QUEUES";
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (!rs.next())
				return null;
			List<String> queues = new ArrayList<String>();
			do {
				queues.add( rs.getString( "QUEUE_NAME" ) );
			} while(rs.next());
			return queues;
		}
		catch (SQLException ex) {
			_logger.error( "Error in getQueueNames()-", ex );
			throw new EJBException(ex);
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
			catch (SQLException ex) {
				_logger.error("WQDAO:validateQueue" +ex);
				throw new EJBException(ex);
			}
		}
	}

	public List <String>getQueueNamesForUser(String username)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			String sql = "SELECT UNIQUE  QUEUE_NAME FROM QUEUE_GROUP_DETAILS_V WHERE UPPER (USER_NAME) = UPPER (?)";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			if (!rs.next())
				return null;
			List<String> queues = new ArrayList<String>();
			do {
				queues.add( rs.getString( "QUEUE_NAME" ) );
			} while(rs.next());
			return queues;
		}
		catch (SQLException ex) {
			_logger.error( "Error in getQueueNames()-", ex );
			throw new EJBException(ex);
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
			catch (SQLException ex) {
				_logger.error("WQDAO:validateQueue" +ex);
				throw new EJBException(ex);
			}
		}
	}
	protected String getSource(WQMetaData wqMetaData) throws Exception{
		if(!StrUtl.isEmptyTrimmed(wqMetaData.getEditorSourceName())){
			return wqMetaData.getEditorSourceName();
		}
		else if(!StrUtl.isEmptyTrimmed(wqMetaData.getEditorMetaData().getSource())){
			return wqMetaData.getEditorMetaData().getSource();
		}
		else if(!StrUtl.isEmptyTrimmed(wqMetaData.getEditorMetaData().getSourceSql())){
			return wqMetaData.getEditorMetaData().getSourceSql();
		}
		else {
			new Exception("Editor Source not found.");
		}
		return null;
	}
	/*
	protected String getSource(WQMetaData wqMetaData) {
		String source = wqMetaData.getEditorMetaData().getSource();
		String editorName = wqMetaData.getEditorMetaData().getName();
		if (StrUtl.equals(editorName, "WQ_QUOTE_RESPONSE_QUEUE")) {
			source = "WQ_QT_RESPONSE";
		} 
		else if (StrUtl.equals(editorName, "WQ_QUOTE_ERROR_QUEUE")) {
			source = "WQ_QT_REQUEST";
		}
		return source;
	}

	protected String customSQLOvd(String editorName, String sql) {
		if (StrUtl.equals(editorName, "WQ_QUOTE_RESPONSE_QUEUE")) {
			sql = sql.replace("MESSAGE_STATUS", "WQR_MESSAGE_STATUS");
			sql = sql.replace("LAST_UPDATED_BY", "WQR_LAST_UPDATED_BY");
			sql = sql.replace("QUEUE_ADDRESS1", "WQR_QUEUE_ADDRESS1");
			sql = sql.replace("QUEUE_ADDRESS2", "WQR_QUEUE_ADDRESS2");
		}
		else if (StrUtl.equals(editorName, "WQ_QUOTE_ERROR_QUEUE")) {
			sql = sql.replace("MESSAGE_STATUS", "WQR_MESSAGE_STATUS");
			sql = sql.replace("LAST_UPDATED_BY", "WQR_LAST_UPDATED_BY");
			sql = sql.replace("QUEUE_ADDRESS1", "WQR_QUEUE_ADDRESS1");
			sql = sql.replace("QUEUE_ADDRESS2", "WQR_QUEUE_ADDRESS2");
		}
		return sql;
	}
	*/	
}
