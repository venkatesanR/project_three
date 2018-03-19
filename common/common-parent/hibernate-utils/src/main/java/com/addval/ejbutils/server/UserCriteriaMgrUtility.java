package com.addval.ejbutils.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.ejb.EJBException;


import org.apache.log4j.Logger;

import com.addval.dbutils.DAOSQLStatement;
import com.addval.dbutils.DAOUtils;
import com.addval.dbutils.TableManager;
import com.addval.ejbutils.utils.EJBXRuntime;
import com.addval.environment.Environment;
import com.addval.metadata.UserCriteria;
import com.addval.metadata.UserCriteriaChart;
import com.addval.utils.XRuntime;

public class UserCriteriaMgrUtility implements UserCriteriaMgr {
	private static transient final Logger _logger = Logger.getLogger(UserCriteriaMgrUtility.class);

	//Name of the Cache : UserCriteriaDAOSQL 
	private static final String USERCRITERIA_DAO_SQL = "UserCriteriaDAOSQL";

	private static final String LOOKUP_USERCRITERIA_SQL_NAME = "LookupUserCriteria";
	private static final String LOOKUP_USERCRITERIA_BY_KEY_SQL_NAME = "LookupUserCriteriaByKey";
	private static final String GET_USERCRITERIA_NAMES_SQL_NAME = "GetUserCriteriaNames";
	
	private static final String INSERT_USERCRITERIA_SQL_NAME = "InsertUserCriteria";
	private static final String UPDATE_USERCRITERIA_SQL_NAME = "UpdateUserCriteria";
	private static final String UPDATE_USERCRITERIA_BY_KEY_SQL_NAME = "UpdateUserCriteriaByKey";
	private static final String DELETE_USERCRITERIA_SQL_NAME = "DeleteUserCriteria";
	private static final String DELETE_USERCRITERIA_BY_KEY_SQL_NAME = "DeleteUserCriteriaByKey";
	private static final String USER_CRITERIA_SEQ = "USER_CRITERIA_SEQ";

	private static final String LOOKUP_USERCRITERIA_CHART_SQL_NAME = "LookupUserCriteriaChart";
	private static final String INSERT_USERCRITERIA_CHART_SQL_NAME = "InsertUserCriteriaChart";
	private static final String UPDATE_USERCRITERIA_CHART_SQL_NAME = "UpdateUserCriteriaChart";
	private static final String DELETE_USERCRITERIA_CHART_SQL_NAME = "DeleteUserCriteriaChart";

	private static final String DELETE_ALL_USERCRITERIA_CHART_SQL_NAME = "DeleteAllUserCriteriaChart";
	
	private static final String USER_CRITERIA_CHART_SEQ = "USER_CRITERIA_CHART_SEQ";

	
	private Environment _environment = null;

	public Environment getEnvironment() {
		return _environment;
	}

	public void setEnvironment(Environment environment) {
		_environment = environment;
	}
	
	private void handleSqlException(String source,SQLException se) {
		_logger.error(se);
		String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(se);
		if (errorMessage == null)
			throw new EJBException(se);
		throw new XRuntime(source, errorMessage);
	}
	
	public UserCriteria lookupUserCriteria(UserCriteria filter) throws RemoteException {
		String source = "UserCriteriaMgrUtility.lookupUserCriteria";
		_logger.trace("Enter :" + source);
		UserCriteria userCriteria = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = getConnection();
			DAOSQLStatement statement = getDAOSQLStatement(LOOKUP_USERCRITERIA_SQL_NAME);
			if(filter.getUserCriteriaKey() > 0){
				statement = getDAOSQLStatement(LOOKUP_USERCRITERIA_BY_KEY_SQL_NAME);
			}
			DAOUtils utils = new DAOUtils(statement, getServerType());

			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, filter);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				userCriteria = new UserCriteria();
				utils.getProperties(rs, userCriteria);
				userCriteria.setUserName(filter.getUserName());
			}

			if(userCriteria != null){
				userCriteria.setUserCriteriaCharts(lookupUserCriteriaCharts(conn,userCriteria.getUserCriteriaKey()));
			}
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll(rs,pstmt,conn);
		}
		_logger.trace("Exit :" + source);
		return userCriteria;
	}
	
	public ArrayList getUserCriteriaNames(UserCriteria filter) throws RemoteException{
		String source = "UserCriteriaMgrUtility.lookupUserCriteria";
		_logger.trace("Enter :" + source);
		ArrayList userCriteriaNames = new ArrayList();
		UserCriteria userCriteria = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = getConnection();
			DAOSQLStatement statement = getDAOSQLStatement(GET_USERCRITERIA_NAMES_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());

			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, filter);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				userCriteria = new UserCriteria();
				utils.getProperties(rs, userCriteria);
				userCriteriaNames.add(userCriteria.getCriteriaName());
			}
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll(rs,pstmt,conn);
		}
		_logger.trace("Exit :" + source);
		return userCriteriaNames;

	}

	public void addNewUserCriteria(UserCriteria criteria) throws RemoteException {
		String source = "UserCriteriaMgrUtility.addNewUserCriteria";
		_logger.trace("Enter :" + source);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			criteria.setUserCriteriaKey(generateKey(conn,USER_CRITERIA_SEQ));
			DAOSQLStatement statement = getDAOSQLStatement(INSERT_USERCRITERIA_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, criteria);
			int rowCount = pstmt.executeUpdate();
			_logger.debug(source + ": rows inserted = " + rowCount);
			ArrayList charts = criteria.getUserCriteriaCharts();
			if( charts != null){
				UserCriteriaChart chart = null;
				for(int i=0;i<charts.size();i++){
					chart = (UserCriteriaChart) charts.get(i);
					chart.setUserCriteriaKey( criteria.getUserCriteriaKey() );
					addNewUserCriteriaChart(conn,chart);
				}
			}
			
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,conn);
		}
		_logger.trace("Exit :" + source);
	}

	public void updateUserCriteria(UserCriteria criteria) throws RemoteException {
		String source = "UserCriteriaMgrUtility.updateUserCriteria";
		_logger.trace("Enter :" + source);
		Connection conn = null;
		PreparedStatement pstmt = null;
		UserCriteriaChart chart = null;
		ArrayList charts = null;
		ArrayList dbCharts = null;
		try {
			conn = getConnection();
			UserCriteria userCriteria = lookupUserCriteria(criteria);
			
			dbCharts = userCriteria.getUserCriteriaCharts();
			//Insert Operation
			charts = criteria.getUserCriteriaCharts();
			if( charts != null){
				for(int i=0;i<charts.size();i++){
					chart = (UserCriteriaChart) charts.get(i);
					if(!containsChart(chart,dbCharts)){
						chart.setUserCriteriaKey(userCriteria.getUserCriteriaKey());
						addNewUserCriteriaChart(conn,chart);
					}
				}
			}
			if(dbCharts != null){
				//Update or Delete Operation
				for (int i = 0; i < dbCharts.size(); i++) {
					chart = (UserCriteriaChart) dbCharts.get(i);
					if(containsChart(chart,charts)){
						updateUserCriteriaChart(conn,chart);
					}
					else {
						deleteUserCriteriaChart(conn,chart);
					}
				}
			}
			
			DAOSQLStatement statement = getDAOSQLStatement(UPDATE_USERCRITERIA_SQL_NAME);
			if(criteria.getUserCriteriaKey() > 0){
				statement = getDAOSQLStatement(UPDATE_USERCRITERIA_BY_KEY_SQL_NAME);
			}
			DAOUtils utils = new DAOUtils(statement, getServerType());
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, criteria);
			int rowCount = pstmt.executeUpdate();
			_logger.debug(source + ": rows updated = " + rowCount);
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,conn);
		}
		_logger.trace("Exit :" + source);
	}
	
	public void deleteUserCriteria(UserCriteria criteria) throws RemoteException {
		String source = "UserCriteriaMgrUtility.deleteUserCriteria";
		_logger.trace("Enter :" + source);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			UserCriteria userCriteria = lookupUserCriteria(criteria);
			if(userCriteria != null){
				deleteAllUserCriteriaChart(conn,userCriteria);
				DAOSQLStatement statement = getDAOSQLStatement(DELETE_USERCRITERIA_SQL_NAME);
				if(userCriteria.getUserCriteriaKey() > 0){
					statement = getDAOSQLStatement(DELETE_USERCRITERIA_BY_KEY_SQL_NAME);
				}
				DAOUtils utils = new DAOUtils(statement, getServerType());
				pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
				utils.setProperties(pstmt, userCriteria);
				int rowCount = pstmt.executeUpdate();
				_logger.debug(source + ": rows deleted = " + rowCount);
			}
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,conn);
		}
		_logger.trace("Exit :" + source);
	}
	
	
	private ArrayList lookupUserCriteriaCharts(Connection conn,int userCriteriaKey){
		String source = "UserCriteriaMgrUtility.lookupUserCriteriaCharts";
		_logger.trace("Enter :" + source);

		ArrayList userCriteriaCharts = new ArrayList();
		UserCriteriaChart userCriteriaChartIn = null;
		UserCriteriaChart userCriteriaChartout = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			userCriteriaChartIn = new UserCriteriaChart();
			userCriteriaChartIn.setUserCriteriaKey(userCriteriaKey);
			DAOSQLStatement statement = getDAOSQLStatement(LOOKUP_USERCRITERIA_CHART_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());

			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, userCriteriaChartIn);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				userCriteriaChartout = new UserCriteriaChart();
				utils.getProperties(rs, userCriteriaChartout);
				userCriteriaCharts.add(userCriteriaChartout);
			}
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll(rs,pstmt,(Connection)null);
		}
		_logger.trace("Exit :" + source);
		return userCriteriaCharts;
	}

	private void addNewUserCriteriaChart(Connection conn,UserCriteriaChart chart) throws RemoteException {
		String source = "UserCriteriaMgrUtility.addNewUserCriteriaChart";
		_logger.trace("Enter :" + source);

		PreparedStatement pstmt = null;
		try {
			chart.setUserCriteriaChartKey(generateKey(conn,USER_CRITERIA_CHART_SEQ));
			DAOSQLStatement statement = getDAOSQLStatement(INSERT_USERCRITERIA_CHART_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, chart);
			int rowCount = pstmt.executeUpdate();
			_logger.debug(source + ": rows inserted = " + rowCount);
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,(Connection)null);
		}
		_logger.trace("Exit :" + source);
	}

	private void updateUserCriteriaChart(Connection conn,UserCriteriaChart chart) throws RemoteException {
		String source = "UserCriteriaMgrUtility.updateUserCriteriaChart";
		_logger.trace("Enter :" + source);

		PreparedStatement pstmt = null;
		try {
			DAOSQLStatement statement = getDAOSQLStatement(UPDATE_USERCRITERIA_CHART_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, chart);
			int rowCount = pstmt.executeUpdate();
			_logger.debug(source + ": rows updated = " + rowCount);
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,(Connection)null);
		}
		_logger.trace("Exit :" + source);

	}

	private void deleteAllUserCriteriaChart(Connection conn,UserCriteria userCriteria) throws RemoteException {
		String source = "UserCriteriaMgrUtility.deleteUserCriteriaChart";
		_logger.trace("Enter :" + source);

		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			DAOSQLStatement statement = getDAOSQLStatement(DELETE_ALL_USERCRITERIA_CHART_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, userCriteria);
			int rowCount = pstmt.executeUpdate();
			_logger.debug(source + ": rows deleted = " + rowCount);
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,(Connection)null);
		}
		_logger.trace("Exit :" + source);
	}

	private void deleteUserCriteriaChart(Connection conn,UserCriteriaChart chart) throws RemoteException {
		String source = "UserCriteriaMgrUtility.deleteUserCriteriaChart";
		_logger.trace("Enter :" + source);

		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			DAOSQLStatement statement = getDAOSQLStatement(DELETE_USERCRITERIA_CHART_SQL_NAME);
			DAOUtils utils = new DAOUtils(statement, getServerType());
			pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
			utils.setProperties(pstmt, chart);
			int rowCount = pstmt.executeUpdate();
			_logger.debug(source + ": rows deleted = " + rowCount);
		}
		catch (SQLException ex) {
			_logger.error(ex);
			handleSqlException(source,ex);
			//throw new EJBException(ex);
		}
		catch (Exception ex) {
			_logger.error(ex);
			throw new EJBException(ex);
		}
		finally {
			closeAll((ResultSet)null,pstmt,(Connection)null);
		}
		_logger.trace("Exit :" + source);
	}

	private Connection getConnection() {
		return getEnvironment().getDbPoolMgr().getConnection();
	}

	private void releaseConnection(Connection conn) {
		getEnvironment().getDbPoolMgr().releaseConnection(conn);
	}

	private DAOSQLStatement getDAOSQLStatement(String type) {
		Hashtable sqls = (Hashtable) getEnvironment().getCacheMgr().get(USERCRITERIA_DAO_SQL);
		return (DAOSQLStatement) sqls.get(type);
	}

	private String getServerType() {
		return getEnvironment().getDbPoolMgr().getServerType();
	}
	
	private void closeAll(ResultSet rs,Statement stmt,Connection conn){
		try {
			if (rs != null){
				rs.close();
			}
		}
		catch (SQLException ex) {
			_logger.error(ex);
		}
		try {
			if (stmt != null){
				stmt.close();
			}
		}
		catch (SQLException ex) {
			_logger.error(ex);
		}
		if (conn != null){
			releaseConnection(conn);
		}
	}
	
	private int generateKey(Connection conn, String seqName){
		String source =  "UserCriteriaMgrUtility.generateKey";
		_logger.trace("Enter :" + source);
		int ret = 0;

		if (getServerType().equals("SQLSERVER") || getServerType().equals("MSAccess")){
			ret = TableManager.getNextSequence(seqName, conn);
		}
		else{
			ret = TableManager.getNextID(seqName,conn);
		}
		_logger.trace("Exit :" + source);
		return ret;
	}
	
	private boolean containsChart(UserCriteriaChart thisChart,ArrayList charts){
		if(charts != null){
			UserCriteriaChart thatChart =  null;
			for(int i=0;i<charts.size();i++){
				thatChart = (UserCriteriaChart) charts.get(i);
				if(thisChart.getUserCriteriaChartKey() == thatChart.getUserCriteriaChartKey()){
					return true;
				}
			}
		}
		return false;
	}
}
