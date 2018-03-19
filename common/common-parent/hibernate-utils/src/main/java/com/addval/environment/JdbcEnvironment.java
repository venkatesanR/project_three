//Source file: D:\\Projects\\Common\\source\\com\\addval\\environment\\JdbcEnvironment.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.environment;

import com.addval.utils.XRuntime;
import com.addval.dbutils.DBPoolMgr;
import com.addval.utils.AVConstants;

/**
 * Extends the Environment class. Used to created JDBC Environments.
 *
 * @author AddVal Technology Inc.
 */
public class JdbcEnvironment extends Environment {

	/**
	 * @roseuid 3BEC476F0335
	 */
	protected JdbcEnvironment() {

	}

	/**
	 * The constructor takes in either the full path to propertyFile or the
	 * popertyFileName
	 * that is available in the classpath.
	 *
	 * @param config Complete path to the configuration (property) file.
	 * @roseuid 399195510017
	 */
	public JdbcEnvironment(String config) {
		super( config );
	}

	/**
	 * This method has been deprecated. Instead, Environment should always be created
	 * with a contextName (i.e. project/sub-system name).
	 * For backward compatibility, this implementation will access the Environment
	 * created with the _DEFAULT contextname.
	 * The function takes in either the full path to propertyFile or the
	 * popertyFileName that is available in the classpath.
	 *
	 * @param config Complete path to the configuration file.
	 * @deprecated This method has been deprecated. Instead, Environment should always
	 * be created with a contextName (i.e. project/sub-system name).
	 * For backward compatibility, this implementation will access the Environment
	 * created with the _DEFAULT contextname.
	 * The function takes in either the full path to propertyFile or the
	 * popertyFileName that is available in the classpath.
	 * @roseuid 399195F7023D
	 */
	public static void make(String config) {

		try {

			JdbcEnvironment env = new JdbcEnvironment( config );
			setInstance( AVConstants._DEFAULT, env );
		}
		catch(Exception e) {

			e.printStackTrace();
			throw new XRuntime( "JdbcEnvironment.make()", e.getMessage() );
		}
	}

	/**
	 * Returns the DBPoolMgr for the current environment.
	 *
	 * @return Returns the DBPoolMgr for the current environment.
	 * @roseuid 3991961A026F
	 */
	public DBPoolMgr getDbPool() {

	   _dbPoolMgr = getDbPoolMgr();
	   return _dbPoolMgr;
	}

	/**
	 * Sets the DBPoolMgr for the current environment.
	 *
	 * @param aDbPoolMgr DBPoolMgr to be set
	 * @roseuid 3C1927450039
	 */
	public void setDbPool(DBPoolMgr aDbPoolMgr) {

    	_dbPoolMgr = aDbPoolMgr;
	}

	/**
	 * @roseuid 3C3518EA02C3
	 */
	protected void initialize() {

		try {

			_dbPoolMgr = getDbPoolMgr(); // this is to initialize the DbPool via a call to createPool
			super.initialize();
			getLogFileMgr( getName() ).logInfo( getName() + " has been initialized for JDBC" );

		}
		catch(Exception e) {

			e.printStackTrace();
			throw new XRuntime( "JdbcEnvironment.make()", e.getMessage() );
		}
	}
}
/**
 *
 *
 *
 * JdbcEnvironment.getPROJECT_NAME(){
 *       return _PROJECT_NAME;
 *    }
 *
 *
 *
 */
