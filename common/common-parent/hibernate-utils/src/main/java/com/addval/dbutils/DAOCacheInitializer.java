//Source file: C:\\users\\prasad\\projects\\cargores\\source\\com\\addval\\cargores\\CargoResDAOCacheInitializer.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

//Source file: C:\\users\\prasad\\projects\\cargores\\source\\com\\addval\\cargores\\CargoresDAOCacheInitializer.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.util.Hashtable;
import com.addval.dbutils.DAOSQLLoader;
import com.addval.utils.CacheMgr;
import com.addval.utils.CacheException;
import com.addval.utils.DefaultNamedCacheInitializer;

public class DAOCacheInitializer extends DefaultNamedCacheInitializer
{
	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(DAOCacheInitializer.class);

	private String _sqlFileName = null;
	private String _daoRulesFileName = "dao_rules.xml";

	public String getSqlFileName() {
		return _sqlFileName;
	}

	public void setSqlFileName(String aFileName) {
		_sqlFileName = aFileName;
	}


	public String getDaoRulesFileName() {
		return _daoRulesFileName;
	}

	public void setDaoRulesFileName(String aFileName) {
		_daoRulesFileName = aFileName;
	}


	/**
	 * @param cache
	 * @param objectName
	 * @param refreshFlag
	 * @return java.lang.Object
	 * @roseuid 3EF8B0A60128
	 */
	public Object populateData(CacheMgr cache, String objectName, boolean refreshFlag) throws CacheException
	{
		try {

			_logger.info( "enter populateData" );

			DAOSQLLoader 	loader    = new DAOSQLLoader();
			String 		 	rulesFile = getEnvironment().getCnfgFileMgr().getPropertyValue( DAOSQLLoader._RULES_URL,  getDaoRulesFileName());
            //To support direct use of dao xml file bypassing cargores_dao_cache_initializer.properties file
            //default expects dao file name to follow a standard naming convention "objectname_dao_sql.xml"
            //the object name can be directly used in xxx_cache.properties file's entry: cache.initializers.daosqls.objects
            //example: old way -> cache.initializers.daosqls.objects=CargoresDAOSQL,RatingEngineDAOSQL
            //new way -> CargoresDAOSQL,RatingEngineDAOSQL=cargores,rating_engine
            //Ravi 2/17/06

			// if a file name is configured (spring) - use that. If not lookup file name from the property file
            String			sqlFile   = getSqlFileName();

			if (sqlFile == null)
				sqlFile   = getEnvironment().getCnfgFileMgr().getPropertyValue( objectName + "." + DAOSQLLoader._SQL_URL, objectName + "_dao_sql.xml" );

			_logger.info( "Loading the DAOSQL file : " + sqlFile + " using " + rulesFile );


			// Read the sqls using the DAOSQLLoader
			Hashtable 		sqls 	  = loader.loadSQL( rulesFile, sqlFile );

			_logger.info( "exit populateData" );

			return sqls;
		}
		catch (Exception e) {
			_logger.error( e );
			throw new CacheException( getClass().getName(), e );
		}
	}
}
