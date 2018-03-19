/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.dbutils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.addval.utils.AVConstants;
import com.addval.utils.CnfgFileMgr;
import com.addval.utils.MultiHashKey;
import com.addval.utils.StrUtl;
import com.addval.utils.XRuntime;

/**
 * Acts as a DataSource from which application code can call getConnection(). getConnection() returns a DBConnection, which extends the normal Connection. DBConnection ensures that the layer of com.addval.dbutils classes (such as DBStatement, DBPreparedStatement, etc) are used. These
 * com.addval.dbutils classes provide a useful layer of log4j logging, and also a means to translate raw, vendor-specific SQL error messages into more informative, application-specific messages.
 *
 * DBPoolMgr can act as a pooled-connection DataSource, depending on the value of the "poolType" attribute. "poolType" can be:
 *
 * NONE Non-pooled. DBPoolMgr creates the DataSource using relevant attributes. Each call to getConnection() creates a new connection.
 *
 * INTERNAL Pooled. DBPoolMgr creates the DataSource using relevant attributes. DBPoolMgr creates and manages an internal connection pool, according to the relevant attributes.
 *
 * When INTERNAL poolType is used, caller code must be careful to pass connections back to DBPoolMgr via "releaseConnection" method, instead of just doing conn.close().
 *
 * JNDI Can be either pooled or non-pooled. The DataSource is not created by DBPoolMgr, but instead is looked up via jndi. In practice the DataSource is usually a JDBCConnectionPool defined in the J2EE container. DBPoolMgr performs no pool managment; the specified DataSource, if pooled, manages its
 * own connection pool.
 *
 * PROVIDED Can be either pooled or non-pooled. The DataSource MUST be set ("injected") directly into DBPoolMgr via "setDataSource" method. In practice the DataSource is usually an instance of org.apache.commons.dbcp.BasicDataSource. DBPoolMgr performs no pool managment; the specified DataSource, if
 * pooled, manages its own connection pool.
 *
 * DBPoolMgr also provides service for retrieving a Schema containing all table information.
 *
 * The primary reason for adding PROVIDED is so that, if running under Spring and using Hibernate, the same pooled DataSource can be used by both (a) the application code and (b) Hibernate. Hibernate requires that a DataSource be injected, but DBPoolMgr does not implement the DataSource interface,
 * so DBPoolMgr cannot be injected into Hibernate. So our intention is to use an available pooled data source (such as org.apache.commons.dbcp.BasicDataSource), inject it into both Hibernate AND into our DBPoolMgr, with poolType=PROVIDED.
 *
 * @author Sankar Dhanushkodi
 * @version $Revision$
 * @see CnfgFileMgr
 * @author AddVal Technology Inc.
 */
public class DBPoolMgr implements Serializable {
	private static final transient org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger("SQL");

	/**
	 * The password used to access the DataBase.
	 */
	private String _driver;

	/**
	 * The url string to connect to the DataBase
	 */
	private String _url;
	private String _serverType = "";

	/**
	 * The username used to access the DataBase.
	 */
	private String _userName;

	/**
	 * The password used to access the DataBase.
	 */
	private String _password;
	private int _maxConn;
	private int _minConn;
	private int _numConn = 0;
	private int _connTimeout = 3600;
	private String _name;
	private static Hashtable _instances = new Hashtable();
	private String _dataSourceName;
	private boolean _schemaRead;
	private String _poolType;
	private String _jndiLookupName;
	private Hashtable _translatedErrorMessages = null;
	private SQLExceptionTranslator _translator = null;
	private String _jndiContextFactory;
	private String _jndiUrl;
	private Logger _log = null;
	private Vector _pool = null;
	private DBSchemaInfo _schema = null;
	private Logger _errorlog = null;
	private Logger _batcherrorlog = null;
	private DataSource _dataSource = null;

	private String _sqlExceptionTranslatorClassName = null;

	private boolean _useSpringManagedTransactions = false;

	private Map<MultiHashKey, String> _externalizedDBErrorMessages = null;
	private ExternalizedDBErrorMessageTranslator _externalizedDBErrorMessageTranslator = null;

	/**
	 * @param source
	 * @roseuid 3C192C660326
	 */
	public DBPoolMgr(String source) {
		CnfgFileMgr cfgMgr = new CnfgFileMgr(source);

		init(cfgMgr);
	}

	/**
	 * This constructor gets a conection to the database. Collects all the information about all the columns in all the tables present in the Schema and then releases the connection. Calls the "init" method to do the initialization.
	 * 
	 * @param cfgMgr
	 *            CnfgFileMgr. Configuration file containing all the database connection parameters.
	 * @param source
	 * @exception
	 * @roseuid 3C16950C03CD
	 */
	public DBPoolMgr(String source, CnfgFileMgr cfgMgr) {
		_name = source;

		init(cfgMgr);
	}

	/**
	 * @param cfgMgr
	 * @roseuid 3B72C91B0133
	 */
	private DBPoolMgr(CnfgFileMgr cfgMgr) {
		init(cfgMgr);
	}

	protected DBPoolMgr() {

	}

	protected DBPoolMgr(DBPoolMgrConfig cfg) {
		init(cfg);
	}

	protected static Hashtable getInstances() {
		return _instances;
	}

	public static synchronized DBPoolMgr getRawInstance(DBPoolMgrConfig cfg) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		DBPoolMgr c = (DBPoolMgr) _instances.get(cfg.getName());
		if (c == null) {
			c = new DBPoolMgr(cfg);
			_instances.put(cfg.getName(), c);
		}

		return c;
	}

	/**
	 * Access method for the _serverType property.
	 *
	 * @return the current value of the _serverType property
	 */
	public String getServerType() {
		return _serverType;
	}

	/**
	 * Access method for the _poolType property.
	 *
	 * @return the current value of the _poolType property
	 */
	public String getPoolType() {
		return _poolType;
	}

	/**
	 * Access method for the _jndiLookupName property.
	 *
	 * @return the current value of the _jndiLookupName property
	 */
	public String getJndiLookupName() {

		return _jndiLookupName;
	}

	/**
	 * Access method for the _translatedErrorMessages property.
	 *
	 * @return the current value of the _translatedErrorMessages property
	 */
	public Hashtable getTranslatedErrorMessages() {
		return _translatedErrorMessages;
	}

	/**
	 * Access method for the _jndiContextFactory property.
	 *
	 * @return the current value of the _jndiContextFactory property
	 */
	public String getJndiContextFactory() {
		return _jndiContextFactory;
	}

	/**
	 * Access method for the _jndiUrl property.
	 *
	 * @return the current value of the _jndiUrl property
	 */
	public String getJndiUrl() {
		return _jndiUrl;
	}

	/**
	 * Access method for the dataSource property.
	 *
	 * @return the current value of the _dataSource property
	 */
	public DataSource getDataSource() {
		return _dataSource;
	}

	/**
	 * Mutator for the dataSource property.
	 *
	 * @return void
	 */
	public void setDataSource(DataSource s) {
		_dataSource = s;
	}

	/**
	 * Registers the Oracle Driver with JDBC Driver Manager and returns a connection to the dataBase.
	 * 
	 * @param module
	 *            String. Module that is requesting the connection. Used only for logging purpose.
	 * @return Database connection - Connection
	 * @throws com.addval.utils.XRuntime
	 * @exception XRuntime
	 * @roseuid 376F97FC02FC
	 */
	public Connection getConnection() throws XRuntime {
		Connection conn = null;

		if (_poolType.equalsIgnoreCase("INTERNAL")) {

			conn = getInternalConnection();
		}
		else if (_poolType.equalsIgnoreCase("NONE")) {

			conn = getNoPoolConnection();
		}
		else if (_poolType.equalsIgnoreCase("JNDI")) {

			conn = getJndiConnection();
		}
		else if (_poolType.equalsIgnoreCase("PROVIDED")) {

			conn = getProvidedConnection();
		}
		else {
			String errMsg = "Unrecognized poolType='" + _poolType + "'";
			_logger.error(errMsg);
			throw new XRuntime(getClass().getName() + ".getConnection()", errMsg);
		}

		// logConnectionAction("getConnection", conn);

		return conn;
	}

	/**
	 * Closes the connection to the DataBase
	 * 
	 * @param conn
	 *            Connection (to close)
	 * @param module
	 *            String. Module that made this call. For logging purpose only.
	 * @throws com.addval.utils.XRuntime
	 * @exception XRuntime
	 * @roseuid 377A544F014A
	 */
	public void releaseConnection(Connection conn) throws XRuntime {
		// logConnectionAction("releaseConnection", conn);

		if (_poolType.equalsIgnoreCase("INTERNAL")) {

			releaseInternalConnection(conn);
		}
		else if (_poolType.equalsIgnoreCase("NONE")) {

			releaseNoPoolConnection(conn);
		}
		else if (_poolType.equalsIgnoreCase("JNDI")) {

			releaseJndiConnection(conn);
		}
		else if (_poolType.equalsIgnoreCase("PROVIDED")) {

			releaseProvidedConnection(conn);
		}
		else {
			String errMsg = "Unrecognized poolType='" + _poolType + "'";
			_logger.error(errMsg);
			throw new XRuntime(getClass().getName() + ".releaseConnection()", errMsg);
		}
	}

	/**
	 * Returns the Schema object which has information about all the tables in the dataBase.
	 * 
	 * @param
	 * @return Schema object - DBSchemaInfo
	 * @throws com.addval.utils.XRuntime
	 * @exception XRuntime
	 * @roseuid 377109BC03DF
	 */
	public DBSchemaInfo getSchema() throws XRuntime {
		return _schema;
	}

	/**
	 * Creates a new connection into the database.
	 * 
	 * @param
	 * @return A database connection - Connection
	 * @exception
	 * @roseuid 378516DF01CC
	 */
	private Connection makeConnection() {
		if (_numConn == _maxConn)
			throw new XRuntime(getClass().getName(), "Cannot make more than " + _maxConn + " connections");

		Connection conn = null;

		try {

			Driver dr = (Driver) Class.forName(_driver).newInstance();
			DriverManager.registerDriver(dr);

			if (_log == null)
				throw new XRuntime(getClass().getName() + ".makeConnection", "LogFileMgr was not created for SQL");
			else {

				System.out.println("Url is: " + _url);
				System.out.println("User Name is: " + _userName);
				// System.out.println("Password is: " + _password);

				conn = new DBConnection(_log, _errorlog, _batcherrorlog, DriverManager.getConnection(_url, _userName, _password));

			}

			if (_numConn > _maxConn) {

				logInfo("Number of connections : " + _numConn + " exceeds Maximum number of connections : " + _maxConn + " in pool");
			}

			_numConn++;
		}
		catch (Exception e) {
			logError(e);
			throw new XRuntime(getClass().getName() + ".makeConnection", e.getMessage());
		}

		return conn;
	}

	/**
	 * Closes a connection.
	 * 
	 * @param conn
	 *            Connection
	 * @exception
	 * @roseuid 378CC0E30288
	 */
	private void destroyConnection(Connection conn) {
		try {
			_numConn--;
			conn.close();
		}
		catch (SQLException e) {
			// log
			XRuntime x = new XRuntime(getClass().getName() + ".destroyConnection", e.getMessage());
		}
	}

	// support for DBPoolMgr configuration from Spring
	private void init(DBPoolMgrConfig cfg) {
		_logger.info("DBPoolMgr.init(DBPoolMgrConfig=" + cfg + ")");

		_name = StrUtl.isEmpty(cfg.getName()) ? "" : cfg.getName();
		_dataSourceName = StrUtl.isEmpty(cfg.getDataSourceName()) ? "" : cfg.getDataSourceName();
		_schemaRead = cfg.getReadSchemaInfo();
		String schemaName = StrUtl.isEmpty(cfg.getSchemaName()) ? "" : cfg.getSchemaName();
		_poolType = StrUtl.isEmpty(cfg.getPoolType()) ? "" : cfg.getPoolType();
		_useSpringManagedTransactions = cfg.getUseSpringManagedTransactions();

		_log = com.addval.utils.LogMgr.getLogger("SQL");
		_errorlog = com.addval.utils.LogMgr.getLogger("ERROR_SQL");
		_batcherrorlog = com.addval.utils.LogMgr.getLogger("BATCH_ERROR_SQL");

		if (_poolType.equalsIgnoreCase("NONE")) {

			initializeNoPool(cfg);
		}
		else if (_dataSourceName == "" || _poolType.equalsIgnoreCase("INTERNAL")) {

			// If dataSource is not specified, then the poolType is assumed as internal
			_poolType = "INTERNAL";

			if (cfg.getDataSource() != null) {
				setDataSource(cfg.getDataSource());

				Connection conn = null;
				try {
					_logger.info("DBPoolMgr.init(DBPoolMgrConfig): TEST ability to getConnection");
					conn = getDataSource().getConnection();
					// logConnectionAction("DBPoolMgr.init(DBPoolMgrConfig): TEST ability to getConnection", conn);
					_driver = conn.getMetaData().getDriverName();
				}
				catch (SQLException se) {
					logError(se);
					throw new XRuntime("DBPoolMgr.init()", se.getMessage());
				}
				finally {
					try {
						if (conn != null && !conn.isClosed())
							conn.close();
					}
					catch (Exception e) {
						logError(e);
						throw new XRuntime("DBPoolMgr.init()", e.getMessage());
					}
				}

			}
			else {
				initializeInternalPool(cfg);
			}

		}
		else if (_poolType.equalsIgnoreCase("JNDI")) {

			_jndiLookupName = StrUtl.isEmpty(cfg.getJndiLookupName()) ? AVConstants._DATASOURCE_ENC_PREFIX + _name : cfg.getJndiLookupName();

			_jndiContextFactory = StrUtl.isEmpty(cfg.getJndiLookupName()) ? "" : cfg.getJndiContextFactory();

			_jndiUrl = StrUtl.isEmpty(cfg.getJndiUrl()) ? "" : cfg.getJndiUrl();

			logInfo("The JNDI Lookup Name is : " + getJndiLookupName());

			logInfo("The JNDI Context Factory is : " + _jndiContextFactory);

			logInfo("The JNDI Url is : " + _jndiUrl);

			Connection conn = null;
			try {
				initializeDataSourceFromJndi();

				// _logger.info("DBPoolMgr.init(DBPoolMgrConfig): testing ability to getJndiConnection");
				conn = getJndiConnection();
				_driver = conn.getMetaData().getDriverName();
			}
			catch (SQLException se) {
				logError(se);
				throw new XRuntime("DBPoolMgr.init()", se.getMessage());
			}
			finally {
				releaseJndiConnection(conn);
			}
		}
		else if (_poolType.equalsIgnoreCase("PROVIDED")) {

			setDataSource(cfg.getDataSource());

			initializeProvided();

			Connection conn = null;
			try {
				_logger.info("DBPoolMgr.init(DBPoolMgrConfig): testing ability to getProvidedConnection");
				conn = getProvidedConnection();
			}
			catch (Exception ex) {
				logError(ex);
				throw new XRuntime("DBPoolMgr.init()", ex.getMessage());
			}
			finally {
				releaseProvidedConnection(conn);
			}
		}
		else {
			String errMsg = "Unrecognized poolType='" + _poolType + "'";
			_logger.error(errMsg);
			throw new XRuntime(getClass().getName() + ".init()", errMsg);
		}

		// The default _sqlExceptionTranslatorClassName is "com.addval.dbutils.OracleSQLExceptionTranslator".
		_sqlExceptionTranslatorClassName = StrUtl.isEmptyTrimmed(cfg.getSqlExceptionTranslatorClassName()) ? "com.addval.dbutils.OracleSQLExceptionTranslator" : cfg.getSqlExceptionTranslatorClassName();

		// The default _serverType is empty string. If serverType is null or all blank, make sure it is set to empty string.
		_serverType = StrUtl.isEmptyTrimmed(cfg.getServerType()) ? "" : cfg.getServerType();

		// If no _driver name has been specified, then attempt to get the _driver (class name) from the _dataSource.
		if (StrUtl.isEmptyTrimmed(_driver)) {

			attemptInferDriverFromDataSource();
		}

		// If no serverType has been specified, then attempt to infer the serverType from the driver name.
		if (StrUtl.isEmptyTrimmed(_serverType)) {

			attemptInferServerTypeFromDriver();
		}

		readSchema(schemaName, _schemaRead, cfg);

		readTranslatedErrorMessages(cfg);

		readExternalizedDBErrorMessages(cfg);
	}

	/**
	 * Intializes the class. Used by all the constructors.
	 * 
	 * @param cfgMgr
	 *            CnfgFileMgr
	 * @exception
	 * @roseuid 3C16A94300AE
	 */
	private void init(CnfgFileMgr cfgMgr) {
		_logger.info("DBPoolMgr.init(CnfgFileMgr=" + cfgMgr.getProperties() + ")");

		_dataSourceName = cfgMgr.getPropertyValue("env.DataSourceName", "");
		_schemaRead = cfgMgr.getBoolValue("db." + _name + ".ReadSchemaInfo", false);
		String schemaName = cfgMgr.getPropertyValue("db." + _name + ".schema", "");
		_poolType = cfgMgr.getPropertyValue("db." + _name + ".poolType", "");

		// if ( logProps.getProperty( "log4j.category.SQL", "" ).length() == 0 ) {
		// // no log config specified for SQL. Create one with highest level of Priority
		// logProps.setProperty( "log4j.category.SQL", "FATAL" );
		// }

		// BobF 1/27/05: removed code that looked for property _LOG_NAME = "env.LogName",
		// and then used deprecated methods to load and configure a LogFileMgr using the specified file/resource bundle.

		_log = com.addval.utils.LogMgr.getLogger("SQL");
		_errorlog = com.addval.utils.LogMgr.getLogger("ERROR_SQL");
		_batcherrorlog = com.addval.utils.LogMgr.getLogger("BATCH_ERROR_SQL");

		if (_poolType.equalsIgnoreCase("NONE")) {

			initializeNoPool(cfgMgr);
		}
		else if (_dataSourceName == "" || _poolType.equalsIgnoreCase("INTERNAL")) {

			// If env.DataSource is not specified, then the poolType is assumed as internal
			_poolType = "INTERNAL";
			initializeInternalPool(cfgMgr);
		}
		else if (_poolType.equalsIgnoreCase("JNDI")) {

			_jndiLookupName = cfgMgr.getPropertyValue("db." + _name + ".jndiLookupName", AVConstants._DATASOURCE_ENC_PREFIX + _name);

			_jndiContextFactory = cfgMgr.getPropertyValue("db." + _name + ".jndiContextFactory", "");

			_jndiUrl = cfgMgr.getPropertyValue("db." + _name + ".jndiUrl", "");

			logInfo("The JNDI Lookup Name is : " + getJndiLookupName());

			logInfo("The JNDI Context Factory is : " + _jndiContextFactory);

			logInfo("The JNDI Url is : " + _jndiUrl);

			Connection conn = null;
			try {
				initializeDataSourceFromJndi();

				conn = getJndiConnection();

				_driver = conn.getMetaData().getDriverName();
			}
			catch (SQLException se) {
				logError(se);
				throw new XRuntime("DBPoolMgr.init()", se.getMessage());
			}
			finally {
				releaseJndiConnection(conn);
			}
		}
		else if (_poolType.equalsIgnoreCase("PROVIDED")) {

			throw new XRuntime("DBPoolMgr.init(CnfgFileMgr)", "poolType=PROVIDED is not allowed under configuration via CfgFileMgr; must configure via DBPoolMgrConfig");
		}
		else {
			String errMsg = "Unrecognized poolType='" + _poolType + "'";
			_logger.error(errMsg);
			throw new XRuntime(getClass().getName() + ".init()", errMsg);
		}

		// The default _sqlExceptionTranslatorClassName is "com.addval.dbutils.OracleSQLExceptionTranslator".
		_sqlExceptionTranslatorClassName = cfgMgr.getPropertyValue("db." + _name + ".sqlExceptionTranslatorClassName", "com.addval.dbutils.OracleSQLExceptionTranslator");

		// The default _serverType is empty string. If serverType is null or all blank, make sure it is set to empty string.
		_serverType = cfgMgr.getPropertyValue("db." + _name + ".serverType", "");

		// If no _driver name has been specified, but a _dataSource has been specified,
		// then attempt to get the _driver (class name) from the _dataSource.
		if (StrUtl.isEmptyTrimmed(_driver)) {

			attemptInferDriverFromDataSource();
		}

		// If no serverType has been specified, then attempt to infer the serverType from the driver name.
		if (StrUtl.isEmptyTrimmed(_serverType)) {

			attemptInferServerTypeFromDriver();
		}

		readSchema(schemaName, _schemaRead, cfgMgr);

		readTranslatedErrorMessages(cfgMgr);

		readExternalizedDBErrorMessages(cfgMgr);
	}

	private void attemptInferDriverFromDataSource() {
		// Should only attempt inference if _driver not specified.
		if (!StrUtl.isEmptyTrimmed(_driver))
			return;

		// Cannot attempt inference if _dataSource is null.
		if (_dataSource == null) {
			_logger.warn("DBPoolMgr: probable configuration error, no driver is specified, no dataSource is specified, unable to infer driver from dataSource.");
			return;
		}

		// If the _dataSource Class has a method named "getDriverClassName",
		// call that method to get the value, and set it into _driver.
		Class dataSourceClass = _dataSource.getClass();
		try {
			java.lang.reflect.Method method = dataSourceClass.getMethod("getDriverClassName", null);
			if (method != null) {
				String driverClassName = (String) method.invoke(_dataSource, null);
				if (!StrUtl.isEmptyTrimmed(driverClassName)) {
					_logger.info("DBPoolMgr: no driver name is specified, but dataSource is specified; have set driver to the dataSource driver '" + driverClassName + "'.");
					_driver = driverClassName;
				}
			}
		}
		catch (Exception ex) { /* no action needed */
		}
	}

	private void attemptInferServerTypeFromDriver() {
		// Should only attempt inference if _serverType not specified.
		if (!StrUtl.isEmptyTrimmed(_serverType))
			return;

		if (StrUtl.isEmptyTrimmed(_driver))
			_logger.warn("DBPoolMgr: probable configuration error, no DB serverType is specified, no driver is specified, unable to infer DB serverType from driver name.");
		else if (_driver.toLowerCase().indexOf(AVConstants._MSACCESS.toLowerCase()) >= 0 || _driver.toLowerCase().indexOf("ms access") >= 0)
			_serverType = AVConstants._MSACCESS;
		else if (_driver.toLowerCase().indexOf(AVConstants._SQLSERVER.toLowerCase()) >= 0)
			_serverType = AVConstants._SQLSERVER;
		else if (_driver.toLowerCase().indexOf(AVConstants._ORACLE.toLowerCase()) >= 0)
			_serverType = AVConstants._ORACLE;
		else if (_driver.toLowerCase().indexOf(AVConstants._POSTGRES.toLowerCase()) >= 0)
			_serverType = AVConstants._POSTGRES;
		else if (_driver.toLowerCase().indexOf(AVConstants._SQLITE.toLowerCase()) >= 0)
			_serverType = AVConstants._SQLITE;
		else
			_logger.warn("DBPoolMgr: probable configuration error, no DB serverType is specified, unrecognized driver name '" + _driver + "', unable to infer serverType from driver name.");

		if (_serverType.length() > 0)
			_logger.info("DBPoolMgr: serverType was not specified, have inferred serverType='" + _serverType + "' from driver '" + _driver + "'.");

		// If after all the checks above, the serverType could not be determined, leave it blank
		// It is left to the code implementation to use the required serverType
	}

	/**
	 * Intializes the class. Used for spring support
	 */
	private void initializeInternalPool(DBPoolMgrConfig cfg) {
		_driver = StrUtl.isEmpty(cfg.getDriver()) ? "" : cfg.getDriver();
		_url = StrUtl.isEmpty(cfg.getUrl()) ? "" : cfg.getUrl();
		_userName = StrUtl.isEmpty(cfg.getUserName()) ? "" : cfg.getUserName();
		_password = StrUtl.isEmpty(cfg.getPassword()) ? "" : cfg.getPassword();
		_minConn = cfg.getMinConn() >= 0 ? cfg.getMinConn() : 1;
		_maxConn = cfg.getMaxConn() >= 0 ? cfg.getMaxConn() : 1;
		_connTimeout = cfg.getConnTimeout() >= 0 ? cfg.getConnTimeout() : 3600;
		_pool = new Vector(_minConn);

		for (int i = 0; i < _minConn; i++)
			_pool.addElement(makeConnection());
	}

	/**
	 * Intializes the class. Used by all the constructors.
	 * 
	 * @param cfgMgr
	 *            CnfgFileMgr
	 * @exception
	 * @roseuid 37C1C82D039D
	 */
	private void initializeInternalPool(CnfgFileMgr cfgMgr) {
		_driver = cfgMgr.getPropertyValue("db." + _name + ".driver", "");
		_url = cfgMgr.getPropertyValue("db." + _name + ".url", "");
		_userName = cfgMgr.getPropertyValue("db." + _name + ".user", "");
		_password = cfgMgr.getPropertyValue("db." + _name + ".password", "");
		_minConn = (int) cfgMgr.getLongValue("db." + _name + ".connection.min", 1);
		_maxConn = (int) cfgMgr.getLongValue("db." + _name + ".connection.max", 1);
		_connTimeout = (int) cfgMgr.getLongValue("db." + _name + ".connection.timeout", 3600);
		_pool = new Vector(_minConn);

		for (int i = 0; i < _minConn; i++)
			_pool.addElement(makeConnection());
	}

	/**
	 * Intializes the class. Used for spring support
	 */
	private void initializeNoPool(DBPoolMgrConfig cfg) {
		_driver = StrUtl.isEmpty(cfg.getDriver()) ? "" : cfg.getDriver();
		_url = StrUtl.isEmpty(cfg.getUrl()) ? "" : cfg.getUrl();
		_userName = StrUtl.isEmpty(cfg.getUserName()) ? "" : cfg.getUserName();
		_password = StrUtl.isEmpty(cfg.getPassword()) ? "" : cfg.getPassword();
		_minConn = 0;
		_maxConn = -1;
		_connTimeout = cfg.getConnTimeout() >= 0 ? cfg.getConnTimeout() : 3600;
		_pool = new Vector();
	}

	/**
	 * @param cfgMgr
	 * @roseuid 3D078E01026A
	 */
	private void initializeNoPool(CnfgFileMgr cfgMgr) {
		_driver = cfgMgr.getPropertyValue("db." + _name + ".driver", "");
		_url = cfgMgr.getPropertyValue("db." + _name + ".url", "");
		_userName = cfgMgr.getPropertyValue("db." + _name + ".user", "");
		_password = cfgMgr.getPropertyValue("db." + _name + ".password", "");
		_minConn = 0;
		_maxConn = -1;
		_connTimeout = (int) cfgMgr.getLongValue("db." + _name + ".connection.timeout", 3600);
		_pool = new Vector();
	}

	private void readSchema(String schemaName, boolean schemaRead, DBPoolMgrConfig cfg) {
		if (schemaRead) {

			Connection conn = null;
			try {
				conn = getConnection();

				// Get the table names to read
				String tablesToRead = StrUtl.isEmpty(cfg.getSchemaInfoTablesToRead()) ? "" : cfg.getSchemaInfoTablesToRead();

				String tableTypesToRead = StrUtl.isEmpty(cfg.getSchemaInfoTypesToRead()) ? "TABLE" : cfg.getSchemaInfoTypesToRead();

				StringTokenizer tokenizer = new StringTokenizer(tableTypesToRead, ",");
				Vector types = new Vector(5);
				while (tokenizer.hasMoreTokens())
					types.add((String) tokenizer.nextElement());

				_schema = new DBSchemaInfo(schemaName, !types.isEmpty() ? (String[]) types.toArray(new String[types.size()]) : null, tablesToRead, conn);
			}
			finally {
				if (conn != null)
					releaseConnection(conn);
			}
		}
	}

	/**
	 * Intializes the class. Used by all the constructors.
	 * 
	 * @param schemaName
	 *            String - This is the Schema Name in which all the DB Tables are present.
	 * @param schemaRead
	 *            boolean - This is the Schema Read flag which is set to read the schema info
	 * @param cfgMgr
	 * @exception
	 * @roseuid 3C1699F8020D
	 */
	private void readSchema(String schemaName, boolean schemaRead, CnfgFileMgr cfgMgr) {
		if (schemaRead) {

			Connection conn = null;
			try {
				conn = getConnection();

				// Get the table names to read
				String tablesToRead = cfgMgr.getPropertyValue("db." + _name + ".ReadSchemaInfo.TablesToRead", "");

				// Get the table types to read
				String tableTypesToRead = cfgMgr.getPropertyValue("db." + _name + ".ReadSchemaInfo.TypesToRead", "TABLE");
				StringTokenizer tokenizer = new StringTokenizer(tableTypesToRead, ",");
				Vector types = new Vector(5);
				while (tokenizer.hasMoreTokens())
					types.add((String) tokenizer.nextElement());

				_schema = new DBSchemaInfo(schemaName, !types.isEmpty() ? (String[]) types.toArray(new String[types.size()]) : null, tablesToRead, conn);
			}
			finally {
				if (conn != null)
					releaseConnection(conn);
			}
		}
	}

	/**
	 * Accessor to the max number of database connections possible.
	 * 
	 * @param
	 * @return max database connection - int
	 * @exception
	 * @roseuid 37C72F870122
	 */
	public int getMaxConn() {
		return _maxConn;
	}

	/**
	 * Returns the number of free database connections avilable.
	 * 
	 * @param
	 * @return available database connections - int
	 * @exception
	 * @roseuid 37C72FBA01F7
	 */
	public int getFreeConn() {
		return _pool.size();
	}

	/**
	 * Accessor to the min number of database connections that were created on initialization.
	 * 
	 * @param
	 * @return min no. of database connection - int
	 * @exception
	 * @roseuid 37C7313003AE
	 */
	public int getMinConn() {
		return _minConn;
	}

	/**
	 * @return java.lang.String
	 * @roseuid 3C16950D002C
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param name
	 * @return com.addval.dbutils.DBPoolMgr
	 * @roseuid 3C16950D0054
	 */
	public static synchronized DBPoolMgr getInstance(String name) {

		return (DBPoolMgr) _instances.get(name);
	}

	/**
	 * @param name
	 * @param pool
	 * @roseuid 3C16950D00AE
	 */
	public static synchronized void setInstance(String name, DBPoolMgr pool) {

		_instances.put(name, pool);
	}

	/**
	 * @return java.sql.Connection
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3C1699420084
	 */
	private Connection getJndiConnection() throws XRuntime {
		try {

			/*
			InitialContext ctx = null;

			if ((_jndiContextFactory.length() > 0) && (_jndiUrl.length() > 0))
			{
			        Hashtable env = new Hashtable();
			        env.put( javax.naming.Context.INITIAL_CONTEXT_FACTORY , _jndiContextFactory );
					    env.put( javax.naming.Context.PROVIDER_URL , _jndiUrl );
					    ctx = new InitialContext( env );
			} else
			{
					ctx = new InitialContext();
			}


			Object o = ctx.lookup( getJndiLookupName() );
			DataSource s = (DataSource)PortableRemoteObject.narrow( o, DataSource.class );
			*/

			Connection conn = null;
			if (getUseSpringManagedTransactions()) {
				Connection rawConn = DataSourceUtils.getConnection(getDataSource());
				conn = new DBConnection(_log, _errorlog, _batcherrorlog, rawConn);
				logConnectionAction("getJndiConnection - Using SpringManagedTransactions ", conn);
			}
			else {
				conn = new DBConnection(_log, _errorlog, _batcherrorlog, getDataSource().getConnection());
				logConnectionAction("getJndiConnection", conn);
			}

			/*	Oracle 9i connection seems to be closed when we first get it. So don't do the following check anymore

						// For SQL Server Datasource, this check indicates that the connection isClosed, so
						// the check is not being done for SQLServer
						if (_serverType.equals( AVConstants._ORACLE )) {

							if (conn.isClosed())
								throw new XRuntime( getClass().getName() + ".getJndiConnection()", "Connection obtained from JNDI Pool is closed" );
						}
			*/

			return conn;
		}
		/*
		catch( NamingException e ) {
			e.printStackTrace();
			throw new XRuntime( getClass().getName(), "Could not lookup data source:" + getJndiLookupName() );
		} */
		catch (SQLException e) {
			e.printStackTrace();
			throw new XRuntime(getClass().getName(), "Could not get connection for data source :" + getJndiLookupName());
		}
	}

	/**
	 * Initialze the DataSource from JNDI
	 *
	 * @return void
	 * @throws com.addval.utils.XRuntime
	 */
	private void initializeDataSourceFromJndi() throws XRuntime {
		try {
			InitialContext ctx = null;

			if ((_jndiContextFactory.length() > 0) && (_jndiUrl.length() > 0)) {
				Hashtable env = new Hashtable();
				env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, _jndiContextFactory);
				env.put(javax.naming.Context.PROVIDER_URL, _jndiUrl);
				ctx = new InitialContext(env);
			}
			else {
				ctx = new InitialContext();
			}

			Object o = ctx.lookup(getJndiLookupName());
			DataSource s = (DataSource) PortableRemoteObject.narrow(o, DataSource.class);

			setDataSource(s);
		}
		catch (NamingException e) {
			e.printStackTrace();
			throw new XRuntime(getClass().getName(), "Could not lookup data source:" + getJndiLookupName());
		}

	}

	/**
	 * @param conn
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3C16994200B6
	 */
	private void releaseJndiConnection(Connection conn) throws XRuntime {
		// logConnectionAction("releaseJndiConnection", conn);

		try {
			if (getUseSpringManagedTransactions()) {
				if (conn != null && !conn.isClosed()) {
					if (conn instanceof DBConnection) {

						// We should not pass conn itself back to DataSourceUtils.releaseConnection(),
						// since conn was not created by DataSourceUtils.getConnection(DataSource).
						// conn is the DBConnection "wrapper" that we put around the raw connection.
						// If we pass back conn, DataSourceUtils will not recognize it, and will close it.
						// This causes errors such as "Could not commit JDBC transaction; nested exception is java.sql.SQLException: Connection is closed."

						// Instead, we must pass back the original raw UNDERLYING connection to DataSourceUtils.releaseConnection().
						// DataSourceUtils will release the connection back to it's pool, but the connection will not be closed.

						DataSourceUtils.releaseConnection(((DBConnection) conn).getUnderlyingConnection(), getDataSource());

						// Since the underlying connection has been release, disable our DBConnection instance, to
						// make sure that application code does not attempt to use it any more.

						((DBConnection) conn).clearUnderlyingConnection();
					}
					else {
						DataSourceUtils.releaseConnection(conn, getDataSource());
					}
				}
			}
			else {
				if (conn != null && !conn.isClosed())
					conn.close();

				if (_log != null && conn != null)
					_log.info("Release connection: [" + ((DBConnection) conn).getConnId() + "]");
			}

		}
		catch (SQLException se) {
			se.printStackTrace();
			throw new XRuntime(getClass().getName(), "Could not release connection for data source:" + getJndiLookupName());
		}
	}

	/**
	 * @return java.sql.Connection
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3C16996D0195
	 */
	private Connection getInternalConnection() throws XRuntime {
		Connection conn = null;

		// To avoid NullPointerException on "synchronize(_pool)" statement below, which can occur under some configuration/initialization scenarios , we need to instantiate _pool.
		if (_pool == null)
			_pool = new Vector();

		try {
			synchronized (_pool) {
				if (_pool.isEmpty()) {

					conn = makeConnection();
				}
				else {

					conn = (Connection) _pool.firstElement();
					_pool.removeElementAt(0);

					try { // if socket is closed
						if (conn != null && conn.isClosed()) {
							conn = null;
						}
						else {
							// prep connection
							// check here as long inactivity seems to close the socket
							conn.clearWarnings();
							conn.setAutoCommit(true);
							this.testConnection(conn);
						}
					}
					catch (Exception e1) {
						conn = null;
					}

					// check connection
					if ((conn == null) || conn.isClosed()) {

						_numConn--;
						conn = makeConnection();
						if (conn == null || conn.isClosed())
							throw new XRuntime(getClass().getName() + ".getInternalConnection()", "Cannot make a Database Connection");
						// prep connection
						conn.clearWarnings();
						conn.setAutoCommit(true);
					}
				}
			} // end sync
		}
		catch (SQLException e) {
			e.printStackTrace();
			XRuntime x = new XRuntime(getClass().getName() + ".getInternalConnection()", e.getMessage());
			conn = null;
			throw x;
		}
		logConnectionAction("getInternalConnection", conn);
		if (_log != null)
			_log.info("Get connection: [" + ((DBConnection) conn).getConnId() + "]");

		return conn;
	}

	/**
	 * make sure the connection is valid
	 */
	private void testConnection(Connection conn) throws SQLException {
		_logger.debug("DBPoolMgr.testConnection(Connection)");

		java.sql.Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			if (getServerType().equals("ORACLE"))
				rs = stmt.executeQuery("SELECT 1 FROM DUAL");
			else
				rs = stmt.executeQuery("SELECT count(*) FROM systypes");
			if (rs.next()) {
				int i = rs.getInt(1);
			}
		}
		catch (SQLException sqlE) {
			throw sqlE;
		}
		finally {
			DBUtl.closeFinally(rs, stmt, _log);
		}
	}

	/**
	 * @param conn
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3C16996D01BD
	 */
	private synchronized void releaseInternalConnection(Connection conn) throws XRuntime {
		// logConnectionAction("releaseInternalConnection", conn);
		try {
			// This fix is to close the connection once in a while to get rid of any implicitly opened cursors
			// Prasad : But if connection is null, then you do not need to do anything

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, -_connTimeout);
			if (conn != null && !conn.isClosed() && ((DBConnection) conn).getTimestamp().compareTo(calendar.getTime()) < 0)
				conn.close();

			_pool.addElement(conn);

			if (_log != null) {
				_log.info("Release connection: [" + ((DBConnection) conn).getConnId() + "]");
			}
		}
		catch (SQLException se) {
			se.printStackTrace();
			throw new XRuntime(getClass().getName() + ".releaseInternalConnection()", se.getMessage());
		}
	}

	/**
	 * @return java.sql.Connection
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3D078E250171
	 */
	private Connection getNoPoolConnection() throws XRuntime {
		Connection conn = makeConnection();

		if (conn == null)
			throw new XRuntime(getClass().getName() + ".getNoPoolConnection()", "Cannot make a Database Connection");

		logConnectionAction("getNoPoolConnection", conn);

		if (_log != null)
			_log.info("Get connection: [" + ((DBConnection) conn).getConnId() + "]");

		return conn;
	}

	/**
	 * @param conn
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3D078E2501AD
	 */
	private void releaseNoPoolConnection(Connection conn) throws XRuntime {
		// logConnectionAction("releaseNoPoolConnection", conn);
		try {
			if (conn != null && !conn.isClosed())
				conn.close();

			if (_log != null)
				_log.info("Release connection: [" + ((DBConnection) conn).getConnId() + "]");
		}
		catch (SQLException se) {

			se.printStackTrace();
			throw new XRuntime(getClass().getName() + ".releaseNoPoolConnection()", se.getMessage());
		}
	}

	private void initializeProvided() throws XRuntime {
		if (getDataSource() == null) {
			throw new XRuntime(getClass().getName() + ".getProvidedConnection()", "poolType is 'PROVIDED' but dataSource has not been specified.");
		}

		// no other action needed
	}

	private Connection getProvidedConnection() throws XRuntime {
		DBConnection conn = null;

		if (getDataSource() == null) {
			throw new XRuntime(getClass().getName() + ".getProvidedConnection()", "poolType is 'PROVIDED' but dataSource has not been specified.");
		}

		try {
			Connection rawConn = DataSourceUtils.getConnection(getDataSource());
			conn = new DBConnection(_log, rawConn);
			logConnectionAction("getProvidedConnection", conn);
		}
		catch (Exception ex) {
			_logger.error("Exception in getProvidedConnection()", ex);
			throw new XRuntime(getClass().getName() + ".getProvidedConnection()", "Cannot make a Database Connection");
		}

		return conn;
	}

	private void releaseProvidedConnection(Connection conn) throws XRuntime {
		// logConnectionAction("releaseProvidedConnection", conn);
		try {
			if (conn != null && !conn.isClosed()) {

				if (conn instanceof DBConnection) {

					// We should not pass conn itself back to DataSourceUtils.releaseConnection(),
					// since conn was not created by DataSourceUtils.getConnection(DataSource).
					// conn is the DBConnection "wrapper" that we put around the raw connection.
					// If we pass back conn, DataSourceUtils will not recognize it, and will close it.
					// This causes errors such as "Could not commit JDBC transaction; nested exception is java.sql.SQLException: Connection is closed."

					// Instead, we must pass back the original raw UNDERLYING connection to DataSourceUtils.releaseConnection().
					// DataSourceUtils will release the connection back to it's pool, but the connection will not be closed.

					DataSourceUtils.releaseConnection(((DBConnection) conn).getUnderlyingConnection(), getDataSource());

					// Since the underlying connection has been release, disable our DBConnection instance, to
					// make sure that application code does not attempt to use it any more.

					((DBConnection) conn).clearUnderlyingConnection();
				}
				else {
					DataSourceUtils.releaseConnection(conn, getDataSource());
				}
			}
		}
		catch (SQLException se) {
			se.printStackTrace();
			throw new XRuntime(getClass().getName() + ".releaseProvidedConnection()", se.getMessage());
		}
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the "module" parameter
	 * @param module
	 *            String
	 * @return java.sql.Connection
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3C18E34002E7
	 */
	public synchronized Connection getConnection(String module) throws XRuntime {
		return getConnection();
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the "module" parameter
	 * @param module
	 *            String@param conn
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 3C18E34003C3
	 */
	public synchronized void releaseConnection(Connection conn, String module) throws XRuntime {
		releaseConnection(conn);
	}

	/**
	 * @return com.addval.dbutils.SQLExceptionTranslator
	 * @roseuid 3EBC4BA100FA
	 */
	public SQLExceptionTranslator getSQLExceptionTranslator() {
		if (_translator == null) {
			_translator = SQLExceptionTranslator.getInstance(_sqlExceptionTranslatorClassName, getServerType(), getTranslatedErrorMessages());
		}
		return _translator;
	}

	/**
	 * Method is used to get the ExternalizedDBErrorMessageTranslator singleton object
	 * 
	 * @return com.addval.dbutils.ExternalizedDBErrorMessageTranslator
	 */
	public ExternalizedDBErrorMessageTranslator getExternalizedDBErrorMessageTranslator() {
		if (_externalizedDBErrorMessageTranslator == null) {
			_externalizedDBErrorMessageTranslator = ExternalizedDBErrorMessageTranslator.getInstance("com.addval.dbutils.ExternalizedDBErrorMessageTranslator", getServerType(), getExternalizedDBErrorMessages());
		}
		return _externalizedDBErrorMessageTranslator;
	}

	/**
	 * Reads the error message mappings from cache. Cache has to be loaded before this method can be called.
	 * 
	 * @param cfgMgr
	 * @roseuid 3EBC4F320213
	 */
	private void readTranslatedErrorMessages(CnfgFileMgr cfgMgr) {
		boolean translate = cfgMgr.getBoolValue("db." + _name + ".translateErrorMessages", true);

		if (_translatedErrorMessages == null && translate) {

			// Read translated error messages from the database
			String tableName = cfgMgr.getPropertyValue("db." + _name + ".translatorTableName", "ERROR_MESSAGES");
			String sql = "SELECT ERROR_NUMBER, ERROR_KEY, ERROR_MESSAGE FROM " + tableName;

			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();

				while (rs.next()) {

					if (_translatedErrorMessages == null) {
						_translatedErrorMessages = new Hashtable(100);
					}

					_translatedErrorMessages.put(rs.getString("ERROR_NUMBER") + rs.getString("ERROR_KEY"), rs.getString("ERROR_MESSAGE"));
				}
			}
			catch (SQLException se) {
				logError(se);
				throw new XRuntime(getClass().getName(), se.getMessage() + " Please ensure that the " + tableName + " exists in the database or the db." + _name + ".translateErrorMessages value is set to false");
			}
			finally {

				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						releaseConnection(conn);
				}
				catch (SQLException se) {
					logError(se);
					throw new XRuntime(getClass().getName(), se.getMessage());
				}
			}
		}
	}

	/**
	 * for spring support
	 */
	private void readTranslatedErrorMessages(DBPoolMgrConfig cfg) {
		boolean translate = cfg.getTranslateErrorMessages();

		if (_translatedErrorMessages == null && translate) {

			// Read translated error messages from the database
			String tableName = StrUtl.isEmpty(cfg.getTranslatorTableName()) ? "ERROR_MESSAGES" : cfg.getTranslatorTableName();
			String sql = "SELECT ERROR_NUMBER, ERROR_KEY, ERROR_MESSAGE FROM " + tableName;

			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();

				while (rs.next()) {

					if (_translatedErrorMessages == null)
						_translatedErrorMessages = new Hashtable(100);

					_translatedErrorMessages.put(rs.getString("ERROR_NUMBER") + rs.getString("ERROR_KEY"), rs.getString("ERROR_MESSAGE"));
				}
			}
			catch (SQLException se) {
				logError(se);
				throw new XRuntime(getClass().getName(), se.getMessage() + " Please ensure that the " + tableName + " exists in the database or the db." + _name + ".translateErrorMessages value is set to false");
			}
			finally {

				try {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					if (conn != null)
						releaseConnection(conn);
				}
				catch (SQLException se) {
					logError(se);
					throw new XRuntime(getClass().getName(), se.getMessage());
				}
			}
		}
	}

	/*protected void logMsg( String source , String message )
	{
	    if (_log != null)
	        _log.info( source , message );
	} */

	protected void logInfo(String message) {
		if (_log != null)
			_log.info(message);

	}

	/*protected void logSql( String message )
	{
	    logInfo( message );
	}*/

	protected void logError(Exception exception) {
		if (_log != null)
			_log.error(exception);
		else
			exception.printStackTrace();
	}

	protected void logConnectionAction(String methodName, Connection conn) {
		if (conn instanceof DBConnection)
			_logger.info("logConnectionAction - " + methodName + "()[" + ((DBConnection) conn).getConnId() + ", instance=" + conn + ", wrappedInstance=" + ((DBConnection) conn).getUnderlyingConnection() + ", name=" + getName() + "]");
		else
			_logger.info("logConnectionAction - " + methodName + "()[" + "instance=" + conn + ", name=" + getName() + "]");
	}

	public boolean getUseSpringManagedTransactions() {
		return _useSpringManagedTransactions;
	}

	public void setUseSpringManagedTransactions(boolean springManagedTransactions) {
		_useSpringManagedTransactions = springManagedTransactions;
	}

	/**
	 * Access method for the _dbErrorMessages property.
	 * 
	 * @return the current value of the _dbErrorMessages property
	 */
	public Map<MultiHashKey, String> getExternalizedDBErrorMessages() {
		return _externalizedDBErrorMessages;
	}

	/**
	 * 
	 * Responsible to fetch data from ERROR_MASTER table and creates instance Map with MultiHashKey(ErrorCode, ErrorType, Domain) as key and Error Description as value.
	 * 
	 * Currently supporting only EN and US for error messages. In future planned to enhance full internalization support.
	 * 
	 * @Note This method is useful to avoid binary release of EAR, because all the error messages maintained in the DB end. So server startup alone is required if any changes in the error messages.
	 * 
	 * @param cfgMgr
	 */
	private void readExternalizedDBErrorMessages(CnfgFileMgr cfgMgr) {
		boolean isExternalizeDBErrorMessages = cfgMgr.getBoolValue("db." + _name + ".externalizeDBErrorMessages", false);
		if (_externalizedDBErrorMessages == null && isExternalizeDBErrorMessages) {
			readExternalizedDBErrorMessages();
		}
	}

	/**
	 * 
	 * Responsible to fetch data from ERROR_MASTER table and creates instance Map with MultiHashKey(ErrorCode, ErrorType, Domain) as key and Error Description as value.
	 * 
	 * Currently supporting only EN and US for error messages. In future planned to enhance full internalization support.
	 * 
	 * @Note This method is useful to avoid binary release of EAR, because all the error messages maintained in the DB end. So server startup alone is required if any changes in the error messages.
	 * 
	 * @param cfg
	 */
	private void readExternalizedDBErrorMessages(DBPoolMgrConfig cfg) {
		if (_externalizedDBErrorMessages == null && cfg.getExternalizeDBErrorMessages()) {
			readExternalizedDBErrorMessages();
		}
	}

	private void readExternalizedDBErrorMessages() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("SELECT EM_ERROR_CODE, EM_ERROR_MESSAGE_TYPE, EM_ERROR_DESCRIPTION, EM_DOMAIN FROM ERROR_MASTER");
			rs = pstmt.executeQuery();
			if (_externalizedDBErrorMessages == null) {
				_externalizedDBErrorMessages = new HashMap<MultiHashKey, String>(100);
			}
			MultiHashKey multiHashKey = null;
			List<String> errorMasterKeys = null;

			while (rs.next()) {
				errorMasterKeys = new ArrayList<String>(5);
				errorMasterKeys.add(rs.getString("EM_ERROR_CODE"));
				errorMasterKeys.add(rs.getString("EM_ERROR_MESSAGE_TYPE"));
				errorMasterKeys.add(rs.getString("EM_DOMAIN"));
				multiHashKey = new MultiHashKey(errorMasterKeys);
				_externalizedDBErrorMessages.put(multiHashKey, (String) rs.getString("EM_ERROR_DESCRIPTION"));
			}
		}
		catch (SQLException se) {
			logError(se);
			throw new XRuntime(getClass().getName(), se.getMessage() + " Please ensure that the ERROR_MASTER table exists in the database or the db." + _name + ".externalizeDBErrorMessages value is set to false");
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					releaseConnection(conn);
				}
			}
			catch (SQLException se) {
				logError(se);
				throw new XRuntime(getClass().getName(), se.getMessage());
			}
		}

	}

}
