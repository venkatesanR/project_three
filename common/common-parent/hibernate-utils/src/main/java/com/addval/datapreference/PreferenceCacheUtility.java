/*
 * PreferenceCacheUtility.java
 *
 * Created on April 30, 2007, 1:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.addval.datapreference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

import org.apache.log4j.Logger;

import com.addval.utils.*;
import com.addval.dbutils.*;

/**
 *
 * @author ravi
 */
public class PreferenceCacheUtility extends DefaultNamedCacheInitializer
{
    private static final String _COMMON_DAO_SQL = "datapreference";
    public static final String _CACHE_NAME_DATA_PREFERENCES = "DataPreferences";
    private static final String _SELECT_DATA_PREFERENCES_SQL = "selectDataPreferences";
    private static final String _SELECT_DATA_ATTRIB_DIMS_SQL = "selectDataAttribDims";
    private static final String _SELECT_DATA_PREFERENCE_DEFS_SQL = "selectDataPreferenceDefs";
    private static final transient Logger _logger = LogMgr.getLogger(PreferenceCacheUtility.class);
    protected String _daoCacheName = null;
    private CacheMgr _cacheMgr = null;

    /** Creates a new instance of PreferenceCacheUtility */
    public PreferenceCacheUtility()
    {
    }

    public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException
    {
        final String _source = getClass().getName() + ".populateData()";
        _cacheMgr = cache;

        if(_logger.isDebugEnabled())_logger.debug("Enter " + _source);


        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //key - pref id, value = APreferenceService object
        HashMap prefServiceMap = new HashMap();

        try{
            conn = getConnection();

            Map prefInfoMap = this.getPreferenceInfoMap(conn);
            Map attribDimInfoMap = this.getAttribDimInfoMap(conn);

            DAOSQLStatement statement = getDAOSQLStatement(_SELECT_DATA_PREFERENCE_DEFS_SQL);
            DAOUtils    utils = getDAOUtils(statement);

            pstmt = conn.prepareStatement(getSQL(statement));
            rs = pstmt.executeQuery();

            int prevPrefId = 0;

            AttribPreferenceDef attribPrefDef = null;

            //key = attrib code, value  - list of AttribPreferenceDef
            HashMap attribPrefDefMap = null;
            PreferenceInfo prefInfo = null;
            String prevAttribCode = null;
            List prefDefList = null;

            while(rs.next()){
                attribPrefDef = new AttribPreferenceDef();
                utils.getProperties(rs, attribPrefDef);
                if(prevPrefId != attribPrefDef.getPrefId().intValue()){
                    prefInfo = (PreferenceInfo)prefInfoMap.get(attribPrefDef.getPrefId());
                    attribPrefDefMap = new HashMap();
                    prefInfo.setAttribPrefDefMap(attribPrefDefMap);
                    prevPrefId = attribPrefDef.getPrefId().intValue();
                    prefDefList = null;
                    prevAttribCode = null;
                }

                if(!StrUtl.equals(prevAttribCode, attribPrefDef.getAttribCode())){
                    prefDefList = new ArrayList(2);
                    prevAttribCode = attribPrefDef.getAttribCode();
                    attribPrefDefMap.put(attribPrefDef.getAttribCode(), prefDefList);
                }

                prefDefList.add(attribPrefDef);
            }

            //create preference service objects
            Map.Entry entry;
            APreferenceService prefService;
            for(Iterator iter = prefInfoMap.entrySet().iterator(); iter.hasNext();){
                entry = (Map.Entry)iter.next();
                prefInfo = (PreferenceInfo)entry.getValue();
                if(prefInfo.isPrefScoreBased()){
                    prefService = new ScoreBasedPreferenceService(prefInfo, attribDimInfoMap);
                }
                else{
                    prefService = new PreferenceService(prefInfo, attribDimInfoMap);
                }
                prefServiceMap.put(entry.getKey(), prefService);
            }

        }
        catch(Exception e){
            _logger.error("Error loading Data Preference cache", e);
            throw new CacheException(getClass().getName(), e);
        }
        finally{
            DBUtl.closeFinally(rs, pstmt, conn, getDbPoolMgr(), _logger);
        }

        if(_logger.isDebugEnabled())_logger.debug("Exit " + _source);

        return Collections.unmodifiableMap(prefServiceMap);
    }

    /**
     * key - pref id; value - PreferenceInfo object
     */
    public Map getPreferenceInfoMap(Connection conn) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap prefInfoMap = new HashMap();

        try{
            DAOSQLStatement statement = getDAOSQLStatement(_SELECT_DATA_PREFERENCES_SQL);
            DAOUtils    utils = getDAOUtils(statement);
            pstmt = conn.prepareStatement(getSQL(statement));
            rs = pstmt.executeQuery();
            PreferenceInfo prefInfo = null;

            while(rs.next()){
                prefInfo = new PreferenceInfo();
                utils.getProperties(rs, prefInfo);
                prefInfoMap.put(prefInfo.getId(), prefInfo);
            }
        }
        finally{
            DBUtl.closeFinally(rs, pstmt, _logger);
        }

        if(_logger.isDebugEnabled())_logger.debug("Preference Info map Cache : " + prefInfoMap);

        return prefInfoMap;
    }

    /**
     * key - dim; value - AttribDimInfo object
     */
    public Map getAttribDimInfoMap(Connection conn) throws SQLException
    {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap dimInfoMap = new HashMap();

        try{
            DAOSQLStatement statement = getDAOSQLStatement(_SELECT_DATA_ATTRIB_DIMS_SQL);
            DAOUtils    utils = getDAOUtils(statement);
            pstmt = conn.prepareStatement(getSQL(statement));
            rs = pstmt.executeQuery();
            AttribDimInfo dimInfo = null;

            while(rs.next()){
                dimInfo = new AttribDimInfo();
                utils.getProperties(rs, dimInfo);
                dimInfoMap.put(dimInfo.getDim(), dimInfo);
            }
        }
        finally{
            DBUtl.closeFinally(rs, pstmt, _logger);
        }

        if(_logger.isDebugEnabled())_logger.debug("Preference attrib dim Info map Cache : " + dimInfoMap);

        return dimInfoMap;
    }


    public DAOSQLStatement getDAOSQLStatement(String sqlStatementName)
    {
        return getDAOSQLStatement(getDaoCacheName(), sqlStatementName);
    }

    public DAOSQLStatement getDAOSQLStatement(String daoSqlKey, String sqlStatementName)
    {
        Map sqls = (Map)getCacheMgr().get(daoSqlKey);
        return (DAOSQLStatement)sqls.get(sqlStatementName);
    }

    public String getSQL(DAOSQLStatement statement)
    {
        return statement.getSQL(getServerType());
    }

    public DAOUtils getDAOUtils(DAOSQLStatement statement)
    {
        return new DAOUtils(statement, getServerType());
    }

    public String getServerType()
    {
        return getDbPoolMgr().getServerType();
    }

    public CacheMgr getCacheMgr()
    {
        return _cacheMgr != null? _cacheMgr : getEnvironment().getCacheMgr();
    }

    public DBPoolMgr getDbPoolMgr()
    {
        return getEnvironment().getDbPoolMgr();
    }

    public Connection getConnection()
    {
        return getDbPoolMgr().getConnection();
    }

    /**
     * @return Map key: preferenceId, value  - APreferenceService object
     */
    public Map getDataPreferenceMap()
    {
        return (Map)getCacheMgr().get(_CACHE_NAME_DATA_PREFERENCES);
    }

    /**
     * @return Map key: preferenceId, value  - APreferenceService object
     */
    public APreferenceService getDataPreferenceMap(Integer prefId)
    {
        Map map = getDataPreferenceMap();
        return map == null ? null : (APreferenceService)map.get(prefId);
    }

    /**
     * @return Map key: dim, value  - AttribDimInfo object
     */
    public Map getAttribDimInfoMap()
    {
        Map prefMap = getDataPreferenceMap();
        //all pref services have the same attribdiminfo map
        if(prefMap == null || prefMap.isEmpty())return null;
        //get any pref serv
        APreferenceService prefSrv = (APreferenceService)prefMap.values().iterator().next();
        return prefSrv.getAttribDimInfoMap();
    }

    /**
     * @param prefId id of the preference scheme to use
     * @param dimValueMap key: dimension(String), value - value for the dimension(Object)
     * @return score or preference
     */
    public long computePreference(Integer prefId, Map dimValueMap, Object otherInfo)
    {
        APreferenceService prefService = getDataPreferenceMap(prefId);
        return computePreference(prefService, dimValueMap, otherInfo);
    }

    /**
     * @param prefService preference scheme to use
     * @param dimValueMap key: dimension(String), value - value for the dimension(Object)
     * @return score or preference
     */
    public static long computePreference(APreferenceService prefService, Map dimValueMap, Object otherInfo)
    {
        return prefService.calculatePreference(dimValueMap, otherInfo);
    }

    /**
     * @return score/preference for an attrib value
     */
    public static long computeAttribScore(APreferenceService prefService, String attribDim, Object attribValue, Object otherInfo)
    {
        return prefService.computeAttribScore(attribDim, attribValue, otherInfo);
    }

    /**
     * @return score/preference for an attrib value
     */
    public long computeAttribScore(Integer prefId, String attribDim, Object attribValue, Object otherInfo)
    {
        APreferenceService prefService = getDataPreferenceMap(prefId);
        return prefService.computeAttribScore(attribDim, attribValue, otherInfo);
    }

	public void setDaoCacheName(String daoCacheName) { _daoCacheName = daoCacheName; }
	public String getDaoCacheName()
	{
		if (_daoCacheName == null)
			_daoCacheName = _COMMON_DAO_SQL;
		return _daoCacheName;
	}
}

