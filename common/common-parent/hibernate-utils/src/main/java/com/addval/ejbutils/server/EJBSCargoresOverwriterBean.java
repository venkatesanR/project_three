
package com.addval.ejbutils.server;

import com.addval.environment.EJBEnvironment;
import com.addval.environment.Environment;
import com.addval.metadata.EditorMetaData;
import com.addval.metadata.UpdateUserCriteria;
import com.addval.metadata.ColumnMetaData;
import com.addval.metadata.ColumnDataType;
import com.addval.parser.InvalidInputException;
import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.ejbutils.dbutils.EJBCriteria;
import com.addval.ejbutils.dbutils.EJBResultSetMetaData;
import com.addval.dbutils.DBUtl;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class EJBSCargoresOverwriterBean implements SessionBean
{
	private static final String _EDITOR_NAME="UPDATE_USER_CRITERIA";
	private static final String _EDITOR_TYPE="DEFAULT";
	private SessionContext _context;
	private static final String _module = "com.addval.ejbutils.server.EJBSCargoresOverwriterBean";
	private String _subSystem = null;
	private String _serverType = "";
	private Logger _logger  = com.addval.utils.LogMgr.getLogger(_module);

	public EJBSCargoresOverwriterBean() {

	}

	public void ejbCreate() throws RemoteException, CreateException {

	}

	public void setSessionContext(SessionContext context) {
        _context = context;

		try {

            _subSystem  = (String)EJBEnvironment.lookupLocalContext( "SUBSYSTEM" );
            _serverType = Environment.getInstance( _subSystem ).getDbPoolMgr().getServerType();
        }
        catch(NamingException nex) {
            nex.printStackTrace();
            throw new EJBException( nex );
        }
	}

	public void unsetSessionContext() throws RemoteException {
        _context = null;
	}

	public void ejbActivate() throws RemoteException {

	}

	public void ejbPassivate() throws RemoteException {

	}

	public void ejbRemove() throws RemoteException {

	}

	private String getSubSystem() {
		return _subSystem;
	}

	public void runOverwriteBatch(String directoryName,String editorName,String criteriaName) {
        Connection conn = null;
		try {
			if (directoryName == null )
				throw new InvalidInputException("Direcotory Name is invalid" );

			/***************************************/
			EJBSEditorMetaDataRemote metaDataRemote = getEditorMetadataRemote();
			EditorMetaData metadataUUC = metaDataRemote.lookup( _EDITOR_NAME, _EDITOR_TYPE );
			/***************************************/
			EJBSTableManagerRemote tableManagerRemote = getTableManagerRemote();
			Hashtable param = new Hashtable();
			param.put(UpdateUserCriteriaTbl.directoryName,directoryName);
			if(editorName != null && editorName.trim().length() >0 )
				param.put(UpdateUserCriteriaTbl.editorName, editorName.trim());

			if(criteriaName != null && criteriaName.trim().length() >0 )
				param.put(UpdateUserCriteriaTbl.criteriaName, criteriaName.trim());

			EJBCriteria ejbCriteria = getEJBCriteriaForLookup( metadataUUC , param);
			EJBResultSet ejbRS = tableManagerRemote.lookup( ejbCriteria );

			/***************************************/

			if( ejbRS == null )
				return;

			EJBResultSet ejbRSU = null;
			EditorMetaData metadata = null;
			UpdateUserCriteria criteria = null;
			UpdateUserCriteriaSqlBuilder sqlBuilder = null;

			Hashtable editors = new Hashtable();
			ResultSet rs = ( ResultSet ) ejbRS;
			String updateSQL = null;

			conn = getConnection();

			while( rs.next() ) {

				criteria = getUpdateUserCriteria( rs );

				if( !editors.contains( criteria.getEditorName() ) ) {

					metadata = metaDataRemote.lookup( criteria.getEditorName(), _EDITOR_TYPE );
					editors.put(criteria.getEditorName() , metadata );

				}

				sqlBuilder = new UpdateUserCriteriaSqlBuilder( (EditorMetaData)editors.get( criteria.getEditorName() ) , criteria );

				try{
					updateSQL = sqlBuilder.getUpdateSql();
					String defaultFilter = getDefaultFilter(criteria);
					if ( defaultFilter!= null) {
						     updateSQL += " AND " + defaultFilter;
					}

					Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logSql( updateSQL );

					int retVal = execUpdate( updateSQL ,  conn );
					ejbRSU = getUpdateEJBResultSet(criteria,metadataUUC,"SUCCESS","");

				}
				catch(Exception ex) {
					ex.printStackTrace();
					ejbRSU = getUpdateEJBResultSet(criteria,metadataUUC,"ERROR", ex.getMessage());
				}

				ejbRSU = tableManagerRemote.updateTransaction ( ejbRSU );
			}
		}
		catch (Exception e) {

			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
			_context.setRollbackOnly();
			throw new EJBException( e );

		}
		finally {

			releaseConnection( conn );

		}
	}
	public void runOverwriteBatch (String scenarioKey, String editorName)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 ResultSet rs = null;
			try {
			 if (scenarioKey==null || scenarioKey.length()<1) {
				 throw new InvalidInputException("ScenarioKey is invalid");
			 }
				EJBSEditorMetaDataRemote metaDataRemote = getEditorMetadataRemote();
				EditorMetaData metadataUUC = metaDataRemote.lookup( _EDITOR_NAME, _EDITOR_TYPE );
				/***************************************/
				EJBSTableManagerRemote tableManagerRemote = getTableManagerRemote();
				Hashtable param = new Hashtable();
				if(editorName != null && editorName.trim().length() >0 )
					param.put(UpdateUserCriteriaTbl.editorName, editorName.trim());

				EJBCriteria ejbCriteria = getEJBCriteriaForLookup( metadataUUC , param);
				EJBResultSet ejbRS = tableManagerRemote.lookup( ejbCriteria );

				/***************************************/

				if( ejbRS == null )
					return;

			 String sql = "SELECT uuc.editor_name, uuc.directory_name, uuc.criteria_name, uuc.criteria_desc, uuc.filter, uuc.default_filter, uuc.update_value, " +
				 "uuc.update_status, uuc.update_status_desc, di.directory_desc, di.directory_condition " +
					"FROM DIRECTORY_INFO di, UPDATE_USER_CRITERIA uuc WHERE uuc.editor_name = di.editor_name AND uuc.directory_name = di.directory_name " +
					"AND uuc.EDITOR_NAME = '"+editorName+"' AND di.DIRECTORY_CONDITION = 'SCENARIO_KEY="+scenarioKey +"' ORDER BY uuc.criteria_name, uuc.criteria_desc";

			 //conn = Environment.getInstance( getSubSystem() ).getDbPoolMgr().getConnection();
			 conn = getConnection();
			 stmt = conn.createStatement();
			 rs = stmt.executeQuery( sql );

				 if (rs == null )
					 return;
				 Hashtable editors = new Hashtable();
				 EditorMetaData metadata = null;
				 UpdateUserCriteria criteria = null;
				 UpdateUserCriteriaSqlBuilder sqlBuilder = null;
				 EJBResultSet ejbRSU = null;

				 String updateSQL = null;
				 //commented out because ifanother conn is assigned to this var then
				 //the old one is never released - Ravi 10/19
				 //conn = getConnection();
				 while (rs.next()) {
					 criteria = getUpdateUserCriteria( rs );
					 if( !editors.contains( criteria.getEditorName() ) ) {

						 metadata = metaDataRemote.lookup( criteria.getEditorName(), _EDITOR_TYPE );
						 editors.put(criteria.getEditorName() , metadata );
					 }

					 sqlBuilder = new UpdateUserCriteriaSqlBuilder( (EditorMetaData)editors.get( criteria.getEditorName() ) , criteria );

					try{
						updateSQL = sqlBuilder.getUpdateSql();
						String defaultFilter = getDefaultFilter(criteria);
						if ( defaultFilter!= null) {
								 updateSQL += " AND " + defaultFilter;
						}

						Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logSql( updateSQL );

						int retVal = execUpdate( updateSQL ,  conn );
						ejbRSU = getUpdateEJBResultSet(criteria,metadataUUC,"SUCCESS","");

					}
					catch(Exception ex) {
						ex.printStackTrace();
						ejbRSU = getUpdateEJBResultSet(criteria,metadataUUC,"ERROR", ex.getMessage());
					}

					ejbRSU = tableManagerRemote.updateTransaction ( ejbRSU );
				}
		}
		catch (Exception e) {
			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
			_context.setRollbackOnly();
			throw new EJBException( e );
		}
		finally {
			DBUtl.closeFinally(rs, stmt, conn,
				Environment.getInstance( getSubSystem() ).getDbPoolMgr(),
				_logger );
		}
	}
	public void validate(EditorMetaData metadata,UpdateUserCriteria criteria) {
        Connection conn = null;
        try {

			if( criteria != null ) {

				conn = getConnection();

				UpdateUserCriteriaSqlBuilder sqlBuilder = new UpdateUserCriteriaSqlBuilder( metadata , criteria );
				String updateSql = sqlBuilder.getUpdateSql();

				String defaultFilter = getDefaultFilter(criteria);
				if ( defaultFilter!= null) {
						 updateSql += " AND " + defaultFilter;
				}

				updateSql +=" AND 1 = 0";
				int retVal = execUpdate(updateSql , conn );
			}

        }
        catch (Exception e) {

            Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
            throw new EJBException( e );

        }
        finally {

			releaseConnection( conn );

        }
	}

	private int execUpdate(String sql, Connection conn) throws SQLException {
        Statement stmt = null;
        int       rv   = 0;

		try {

            Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logSql( sql );

            stmt = conn.createStatement();
            rv   = stmt.executeUpdate( sql );
        }
        finally {
			DBUtl.closeFinally(null, stmt,
				_logger );
        }
        return rv;
	}


	private EJBCriteria getEJBCriteriaForLookup(EditorMetaData metadata, Hashtable param) {
    	EJBCriteria ejbCriteria = ( new EJBResultSetMetaData( metadata ) ).getNewCriteria();
		ejbCriteria.where ( param, null );
	    Vector orderby = new Vector( );
	    for ( Iterator iter = metadata.getDisplayableColumns().iterator( ); iter.hasNext( ); ) {
	        ColumnMetaData colInfo = ( ColumnMetaData ) iter.next( );
	        if ( colInfo.getType() != ColumnDataType._CDT_LINK )
	    		orderby.add ( colInfo.getName() );
	    }
        ejbCriteria.orderBy ( orderby );
        return ejbCriteria;
	}

	private  UpdateUserCriteria getUpdateUserCriteria(ResultSet rs) throws SQLException {
		return new UpdateUserCriteria(	rs.getString( UpdateUserCriteriaTbl.editorName ),
										rs.getString( UpdateUserCriteriaTbl.directoryName ),
										rs.getString( UpdateUserCriteriaTbl.criteriaName ),
										rs.getString( UpdateUserCriteriaTbl.criteriaDesc ),
										rs.getString( UpdateUserCriteriaTbl.filter ),
										rs.getString( UpdateUserCriteriaTbl.defaultFilter ),
										rs.getString( UpdateUserCriteriaTbl.updateValue )
									);
	}

	private EJBResultSet getUpdateEJBResultSet(UpdateUserCriteria criteria,EditorMetaData metadata,String updateStatus,String updateStatusDesc) {
		EJBResultSet ejbRS = new EJBResultSet ( new EJBResultSetMetaData( metadata ) );
		ejbRS.updateRow();
		ejbRS.updateString( UpdateUserCriteriaTbl.directoryName, 	criteria.getDirectoryName());
		ejbRS.updateString( UpdateUserCriteriaTbl.editorName, 		criteria.getEditorName());
		ejbRS.updateString( UpdateUserCriteriaTbl.criteriaName, 	criteria.getCriteriaName());
		ejbRS.updateString( UpdateUserCriteriaTbl.criteriaDesc, 	criteria.getCriteriaDesc());
		ejbRS.updateString( UpdateUserCriteriaTbl.filter, 			criteria.getFilter() );
		ejbRS.updateString( UpdateUserCriteriaTbl.defaultFilter, 	criteria.getDefaultFilter());
		ejbRS.updateString( UpdateUserCriteriaTbl.updateValue , 	criteria.getUpdateValue());
		ejbRS.updateString( UpdateUserCriteriaTbl.updateStatus , 	updateStatus);
		ejbRS.updateString( UpdateUserCriteriaTbl.updateStatusDesc , updateStatusDesc);
		return ejbRS;
	}

	private Connection getConnection() {
        return Environment.getInstance( _subSystem ).getDbPoolMgr().getConnection();
	}

	private void releaseConnection(Connection conn) {
		if (conn != null)
        	Environment.getInstance( _subSystem ).getDbPoolMgr().releaseConnection( conn );
	}

	private EJBSTableManagerRemote getTableManagerRemote() throws NamingException, RemoteException, Exception {
		String homeName = Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSTableManagerBeanName", "EJBSTableManager");
        EJBSTableManagerHome   home    = ( EJBSTableManagerHome ) EJBEnvironment.lookupEJBInterface( getSubSystem() , homeName , EJBSTableManagerHome.class );
        EJBSTableManagerRemote remote = home.create();
        return remote;
	}
	private EJBSEditorMetaDataRemote getEditorMetadataRemote() throws NamingException, RemoteException, Exception {
		String homeName	= Environment.getInstance(getSubSystem()).getCnfgFileMgr().getPropertyValue("editorMetaData.EJBSEditorMetaDataBeanName", "EJBSEditorMetaData");
        EJBSEditorMetaDataHome   home   = ( EJBSEditorMetaDataHome ) EJBEnvironment.lookupEJBInterface( getSubSystem() , homeName , EJBSEditorMetaDataHome.class );
        EJBSEditorMetaDataRemote remote = home.create();
        return remote;
	}

	public final static class UpdateUserCriteriaTbl {
		private static final String tblName = "UPDATE_USER_CRITERIA";
		private static final String directoryName = "DIRECTORY_NAME";
		private static final String editorName = "EDITOR_NAME";
		private static final String criteriaName = "CRITERIA_NAME";
		private static final String criteriaDesc = "CRITERIA_DESC";
		private static final String filter = "FILTER";
		private static final String defaultFilter = "DEFAULT_FILTER";
		private static final String updateValue = "UPDATE_VALUE";
		private static final String updateStatus = "UPDATE_STATUS";
		private static final String updateStatusDesc = "UPDATE_STATUS_DESC";
	}

	public String getDefaultFilter( UpdateUserCriteria criteria) throws Exception
	{
		String defaultFilter = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT DISTINCT dir.DIRECTORY_CONDITION FROM DIRECTORY_INFO dir, UPDATE_USER_CRITERIA upd WHERE " +
				"dir.editor_name = upd.editor_name AND dir.directory_name = upd.directory_name " +
				"AND dir.editor_name = '" + criteria.getEditorName() + "'" +
				"AND dir.directory_name = '" + criteria.getDirectoryName() + "'";
			conn = Environment.getInstance( getSubSystem() ).getDbPoolMgr().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );
			if (rs.next()) {
				if (rs.getString(1)!= null && rs.getString(1).trim().length() >0) {
					defaultFilter = rs.getString(1);
				}
			}
			return defaultFilter;
		}
		catch (Exception e) {

			Environment.getInstance( _subSystem ).getLogFileMgr( _module ).logError( e );
			_context.setRollbackOnly();
			throw new EJBException( e );

		}
		finally {
			DBUtl.closeFinally(rs, stmt, conn,
				Environment.getInstance( getSubSystem() ).getDbPoolMgr(),
				_logger );
		}
	}
}


