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

import com.addval.dbutils.DAOSQLStatement;
import com.addval.dbutils.DAOUtils;

import com.addval.environment.Environment;

import com.addval.metadata.BulkUpdate;
import com.addval.metadata.DirectoryInfo;
import com.addval.metadata.EditorMetaData;

import com.addval.parser.InvalidInputException;

import com.addval.utils.StrUtl;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;


public class BulkUpdateMgrUtility implements BulkUpdateMgr
{
    private static transient final Logger _logger = Logger.getLogger(BulkUpdateMgrUtility.class);

    // Name of the Cache : BulkUpdateDAOSQL
    private static final String BULKUPDATE_DAO_SQL = "BulkUpdateDAOSQL";
    private static final String LOOKUP_SCENARIO_NAME_SQL = "LookupScenarioName";
    
    private static final String LOOKUP_BULKUPDATE_SQL_NAME = "LookupBulkUpdate";
    private static final String INSERT_BULKUPDATE_SQL_NAME = "InsertBulkUpdate";
    private static final String UPDATE_BULKUPDATE_SQL_NAME = "UpdateBulkUpdate";
    private static final String DELETE_BULKUPDATE_SQL_NAME = "DeleteBulkUpdate";
    private static final String LOOKUP_DIRECTORYINFO_SQL_NAME = "LookupDirectoryInfo";
    private static final String _EDITOR_TYPE = "DEFAULT";
    private Environment _environment = null;
    private EJBSEditorMetaData _editorMetaDataServer = null;

    public Environment getEnvironment()
    {
        return _environment;
    }

    public void setEnvironment(Environment environment)
    {
        _environment = environment;
    }

    public void setEditorMetaDataServer(EJBSEditorMetaData server)
    {
        _editorMetaDataServer = server;
    }

    public EJBSEditorMetaData getEditorMetaDataServer()
    {
        return _editorMetaDataServer;
    }

    public ArrayList<DirectoryInfo> lookupDirectoryInfos(DirectoryInfo filter) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.lookupDirectoryInfos";
        _logger.trace("Enter :" + source);
        ArrayList<DirectoryInfo> directoryInfos = new ArrayList<DirectoryInfo>();
        DirectoryInfo directoryInfo = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            DirectoryInfo newFilter = new DirectoryInfo();
            if(StrUtl.isEmptyTrimmed(filter.getEditorName()))
                newFilter.setEditorName("%");
            else
                newFilter.setEditorName(filter.getEditorName());

            if(StrUtl.isEmptyTrimmed(filter.getDirectoryName()))
                newFilter.setDirectoryName("%");
            else
                newFilter.setDirectoryName(filter.getDirectoryName());

            conn = getConnection();
            DAOSQLStatement statement = getDAOSQLStatement(LOOKUP_DIRECTORYINFO_SQL_NAME);
            DAOUtils utils = new DAOUtils(statement, getServerType());
            pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
            utils.setProperties(pstmt, newFilter);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                directoryInfo = new DirectoryInfo();
                utils.getProperties(rs, directoryInfo);
                directoryInfos.add(directoryInfo);
            }
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll(rs, pstmt, conn);
        }

        _logger.trace("Exit :" + source);
        return directoryInfos;
    }

    public DirectoryInfo lookupDirectoryInfo(DirectoryInfo filter) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.lookupDirectoryInfo";
        _logger.trace("Enter :" + source);
        DirectoryInfo directoryInfo = null;
        ArrayList<DirectoryInfo> directoryInfos = lookupDirectoryInfos(filter);
        if(directoryInfos.size() > 0)
            directoryInfo = directoryInfos.get(0);

        _logger.trace("Exit :" + source);
        return directoryInfo;
    }

    public ArrayList<BulkUpdate> lookupBulkUpdates(BulkUpdate filter) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.lookupBulkUpdate";
        _logger.trace("Enter :" + source);
        ArrayList<BulkUpdate> bulkUpdates = new ArrayList<BulkUpdate>();
        BulkUpdate bulkUpdate = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            BulkUpdate newFilter = new BulkUpdate();
            if(StrUtl.isEmptyTrimmed(filter.getEditorName()))
                newFilter.setEditorName("%");
            else
                newFilter.setEditorName(filter.getEditorName());

            if(StrUtl.isEmptyTrimmed(filter.getDirectoryName()))
                newFilter.setDirectoryName("%");
            else
                newFilter.setDirectoryName(filter.getDirectoryName());

            if(StrUtl.isEmptyTrimmed(filter.getUpdateName()))
                newFilter.setUpdateName("%");
            else
                newFilter.setUpdateName(filter.getUpdateName());

            conn = getConnection();
            DAOSQLStatement statement = getDAOSQLStatement(LOOKUP_BULKUPDATE_SQL_NAME);
            DAOUtils utils = new DAOUtils(statement, getServerType());
            pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
            utils.setProperties(pstmt, newFilter);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                bulkUpdate = new BulkUpdate();
                utils.getProperties(rs, bulkUpdate);
                bulkUpdates.add(bulkUpdate);
            }
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll(rs, pstmt, conn);
        }

        _logger.trace("Exit :" + source);
        return bulkUpdates;
    }

    public BulkUpdate lookupBulkUpdate(BulkUpdate filter) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.lookupBulkUpdate";
        _logger.trace("Enter :" + source);
        BulkUpdate bulkUpdate = null;
        ArrayList<BulkUpdate> bulkUpdates = lookupBulkUpdates(filter);
        if(bulkUpdates.size() > 0)
            bulkUpdate = bulkUpdates.get(0);

        _logger.trace("Exit :" + source);
        return bulkUpdate;
    }

    public void addNewBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.addNewBulkUpdate";
        _logger.trace("Enter :" + source);
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = getConnection();
            DAOSQLStatement statement = getDAOSQLStatement(INSERT_BULKUPDATE_SQL_NAME);
            DAOUtils utils = new DAOUtils(statement, getServerType());
            pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
            utils.setProperties(pstmt, bulkUpdate);
            int rowCount = pstmt.executeUpdate();
            _logger.debug(source + ": rows inserted = " + rowCount);
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll((ResultSet) null, pstmt, conn);
        }

        _logger.trace("Exit :" + source);
    }

    public void updateBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.updateBulkUpdate";
        _logger.trace("Enter :" + source);
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            if(StrUtl.isEmptyTrimmed(bulkUpdate.getLastUpdatedBy()))
                bulkUpdate.setLastUpdatedBy("SYSTEM");

            if(bulkUpdate.getLastUpdatedDate() == null)
            {
                Date today = new Date();
                bulkUpdate.setLastUpdatedDate(new Timestamp(today.getTime()));
            }

            conn = getConnection();
            DAOSQLStatement statement = getDAOSQLStatement(UPDATE_BULKUPDATE_SQL_NAME);
            DAOUtils utils = new DAOUtils(statement, getServerType());
            pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
            utils.setProperties(pstmt, bulkUpdate);
            int rowCount = pstmt.executeUpdate();
            _logger.debug(source + ": rows updated = " + rowCount);
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll((ResultSet) null, pstmt, conn);
        }

        _logger.trace("Exit :" + source);
    }

    public void deleteBulkUpdate(BulkUpdate bulkUpdate) throws RemoteException
    {
        String source = "BulkUpdateMgrUtility.deleteBulkUpdate";
        _logger.trace("Enter :" + source);
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = getConnection();
            DAOSQLStatement statement = getDAOSQLStatement(DELETE_BULKUPDATE_SQL_NAME);
            DAOUtils utils = new DAOUtils(statement, getServerType());
            pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
            utils.setProperties(pstmt, bulkUpdate);
            int rowCount = pstmt.executeUpdate();
            _logger.debug(source + ": rows deleted = " + rowCount);
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll((ResultSet) null, pstmt, conn);
        }

        _logger.trace("Exit :" + source);
    }

    public void validate(EditorMetaData metadata, BulkUpdate bulkUpdate)
    {
        String source = "BulkUpdateMgrUtility.validate";
        _logger.trace("Enter :" + source);
        Connection conn = null;
        Statement stmt = null;
        try
        {
            if(bulkUpdate != null)
            {
                conn = getConnection();
                BulkUpdateSqlBuilder sqlBuilder = new BulkUpdateSqlBuilder(metadata, bulkUpdate);
                String sql = sqlBuilder.getUpdateSql();
                String defaultFilter = getDefaultFilter(bulkUpdate);
                if(defaultFilter != null)
                    sql += (" AND " + defaultFilter);

                sql += " AND 1 = 0";
                _logger.debug(sql);
                //System.out.println( sql );
                stmt = conn.createStatement();
                int rowCount = stmt.executeUpdate(sql);
                _logger.debug(source + ": rows updated = " + rowCount);
            }
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll((ResultSet) null, stmt, conn);
        }

        _logger.trace("Exit :" + source);
    }

    public void runOverwriteBatch(String directoryName, String editorName, String criteriaName)
    {
        String source = "BulkUpdateMgrUtility.runOverwriteBatch - String directoryName, String editorName, String criteriaName";
        _logger.trace("Enter :" + source);
        Connection conn = null;
        try
        {
            if(StrUtl.isEmptyTrimmed(directoryName))
                throw new InvalidInputException("Direcotory Name is invalid");

            BulkUpdate filter = new BulkUpdate();
            filter.setDirectoryName(directoryName);
            if(!StrUtl.isEmptyTrimmed(editorName))
                filter.setEditorName(editorName);

            if(!StrUtl.isEmptyTrimmed(criteriaName))
                filter.setUpdateName(criteriaName);

            List<BulkUpdate> bulkUpdates = lookupBulkUpdates(filter);
            conn = getConnection();
            runOverwriteBatch(bulkUpdates, conn);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll(null, null, conn);
        }

        _logger.trace("Exit :" + source);
    }

    public void runOverwriteBatch(String editorName)
    {
    	runOverwriteBatch(editorName, (BulkUpdate)null );
    }
    
    public void runOverwriteBatch(String editorName, BulkUpdate filter)
    {
        String source = "BulkUpdateMgrUtility.runOverwriteBatch - String editorName";
        Connection conn = null;
        try
        {
        	if (filter == null)
        		filter = new BulkUpdate();
            if(StrUtl.isEmptyTrimmed(filter.getEditorName()))
            	filter.setEditorName(editorName);

            List<BulkUpdate> bulkUpdates = lookupBulkUpdates(filter);
            conn = getConnection();
            runOverwriteBatch(bulkUpdates, conn);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll(null, null, conn);
        }
        _logger.trace("Exit :" + source);
    }

    public void runOverwriteBatch(String scenarioKey, String editorName)
    {
        String source = "BulkUpdateMgrUtility.runOverwriteBatch - String scenarioKey, String editorName";
        _logger.trace("Enter :" + source);
        if(StrUtl.isEmptyTrimmed(scenarioKey))
            throw new EJBException("ScenarioKey is invalid");
        
        int sceKey = Integer.parseInt(scenarioKey);
        BulkUpdate filter = null;
        if (sceKey > 0) {
        	filter = new BulkUpdate();
        	String scenarioName = getScenarioName( sceKey );        
        	filter.setDirectoryName( scenarioName );
        }
        runOverwriteBatch(editorName, filter );
    }
    
    private String getScenarioName(int scenairoKey ) {
    	
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {           
            conn = getConnection();
            DAOSQLStatement statement = getDAOSQLStatement(LOOKUP_SCENARIO_NAME_SQL);            
            pstmt = conn.prepareStatement(statement.getSQL(getServerType()));
            pstmt.setInt(1,scenairoKey );
            rs = pstmt.executeQuery();
            if (!rs.next())
            	throw new EJBException( "No Scenario eixsits with the key " + scenairoKey );
            return rs.getString(1);              
        }
        catch(EJBException ex)
        {
            _logger.error(ex);
            throw ex;
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll(rs, pstmt, conn);
        }
    }

    protected void runOverwriteBatch(List<BulkUpdate> bulkUpdates, Connection conn)
    {
        String source = "BulkUpdateMgrUtility.runOverwriteBatch - List<BulkUpdate> bulkUpdates, Connection conn";
        EditorMetaData metadata = null;
        Hashtable<String, EditorMetaData> editorMetaDataCache = new Hashtable<String, EditorMetaData>();
        BulkUpdateSqlBuilder sqlBuilder = null;
        String sql = null;
        Statement stmt = null;
        try
        {
            for(BulkUpdate bulkUpdate : bulkUpdates)
            {
                if(!editorMetaDataCache.contains(bulkUpdate.getEditorName()))
                {
                    metadata = getEditorMetaDataServer().lookup(bulkUpdate.getEditorName(), _EDITOR_TYPE);
                    editorMetaDataCache.put(bulkUpdate.getEditorName(), metadata);
                }

                sqlBuilder = new BulkUpdateSqlBuilder(editorMetaDataCache.get(bulkUpdate.getEditorName()), bulkUpdate);
                try
                {
                    sql = sqlBuilder.getUpdateSql();
                    String defaultFilter = getDefaultFilter(bulkUpdate);
                    if(defaultFilter != null)
                        sql += (" AND " + defaultFilter);

                    _logger.debug(sql);
                    stmt = conn.createStatement();
                    int rowCount = stmt.executeUpdate(sql);
                    _logger.debug(source + ": rows updated = " + rowCount);
                    bulkUpdate.setUpdateStatusCode("SUCCESS");
                    bulkUpdate.setUpdateStatusDesc("");
                    updateBulkUpdate(bulkUpdate);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    bulkUpdate.setUpdateStatusCode("ERROR");
                    bulkUpdate.setUpdateStatusDesc(ex.getMessage());
                    updateBulkUpdate(bulkUpdate);
                }
                finally
                {
                    closeAll(null, stmt, null);
                }
            }
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new EJBException(ex);
        }
        finally
        {
            closeAll(null, stmt, null);
        }
    }

    private String getDefaultFilter(BulkUpdate bulkUpdate) throws Exception
    {
        String source = "BulkUpdateMgrUtility.getDefaultFilter";
        _logger.trace("Enter :" + source);
        String defaultFilter = null;
        DirectoryInfo filter = new DirectoryInfo();
        filter.setEditorName(bulkUpdate.getEditorName());
        filter.setDirectoryName(bulkUpdate.getDirectoryName());
        DirectoryInfo directoryInfo = lookupDirectoryInfo(filter);
        if(directoryInfo != null)
            defaultFilter = directoryInfo.getDirectoryCondition();

        _logger.trace("Exit :" + source);
        return defaultFilter;
    }

    private Connection getConnection()
    {
        return getEnvironment().getDbPoolMgr().getConnection();
    }

    private void releaseConnection(Connection conn)
    {
        getEnvironment().getDbPoolMgr().releaseConnection(conn);
    }

    private DAOSQLStatement getDAOSQLStatement(String type)
    {
        Map<String, DAOSQLStatement> sqls = (Map) getEnvironment().getCacheMgr().get(BULKUPDATE_DAO_SQL);
        return sqls.get(type);
    }

    private String getServerType()
    {
        return getEnvironment().getDbPoolMgr().getServerType();
    }

    private void closeAll(ResultSet rs, Statement stmt, Connection conn)
    {
        try
        {
            if(rs != null)
                rs.close();
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
        }

        try
        {
            if(stmt != null)
                stmt.close();
        }
        catch(SQLException ex)
        {
            _logger.error(ex);
        }

        if(conn != null)
            releaseConnection(conn);
    }
}
