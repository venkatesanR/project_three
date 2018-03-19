package com.addval.environment;

import com.addval.utils.AVConstants;

import java.io.Serializable;
import java.util.Map;
import com.addval.dbutils.DBPoolMgr;
import java.util.Hashtable;

import org.springframework.jms.core.JmsOperations;

import com.addval.utils.CnfgFileMgr;
import com.addval.utils.CacheMgr;
import com.addval.utils.LogFileMgr;
import com.addval.utils.XRuntime;

/**
 * Environment is used to provide a framework for applications to access common
 * resources such as Properties (via CnfgFileMgr), Logs (via LogFileMgr), DB
 * Connections (via DBPoolMgr) and Cache (via CacheMgr).
 * <p>
 * In order to provide an "isolated" environment, each project, will access their
 * environment using
 * Environment.getInstance( "context" );
 * <p>
 * An Environment for a project can be created in one of two ways:
 * <br>
 * 1. Preferred approach:
 * <br>
 * Env is automatically created on the first call to
 * Env.getInstance( context );
 * This will look for "context".properties (via ResourceBundle - i.e. classpath)
 * and read the following 4 properties.
 * LogName=RS
 * CacheName=Default
 * DataSourceName=RS
 * EnvClassName=com.addval.environment.EJBServerEnvironment
 * "EnvClassName" points to an Environment class to be used for that project.
 * <p>
 * 2. It is also possible to pre-initialize an Environment with a call to
 * <br>
 * new DerivedEnvClass.make( context, config_file );
 * from main() or via an initialization servlet.
 * This will still require the config file to contain
 * LogName=
 * CacheName=Default
 * DataSourceName=
 * The Known instantiable subclasses to Environment are:
 * JbdcEnvironment
 * OrbEnvironment
 * EJBClientEnvironment
 * EJBServerEnvironment
 * While these are instantiable classes that may be used directly, a project can
 * also create a class (derived from Environment) if there is a need.
 * <br>
 * In this approach, the derived class must do 3 things:
 * <br>
 * a. the DerivedEnv.make method should call Environment.setInstance() to add
 * itself to the list of Environments in the JVM so that subsequent calls to
 * Env.getInstance( context ) will work.
 * <br>
 * b. all the constructors should call super ( config_file );
 * <br>
 * c. Provide a protected default constructor so that the Environment class can be
 * initialized dynamically when the 1st approach is used.
 * <br>
 * @verion $Revision$
 * @author Sankar Dhanushkodi
 */
public class Environment implements Serializable{

	/**
	 * Hashtable to store the insances of the "Environment"
	 */
	protected static Hashtable<String, Environment> _instances = new Hashtable<String, Environment>();

	/**
	 * The configuration file manager for the environment.
	 */
	protected CnfgFileMgr _cnfgFileMgr = null;

	/**
	 * The cache manager for the environment.
	 */
	protected CacheMgr _cacheMgr = null;

	/**
	 * The database pool manager for the environment.
	 */
	protected DBPoolMgr _dbPoolMgr = null;

	/**
	 * Name of the environment.
	 */
	private String _name;
//	private static final String _LOG_NAME = "env.LogName";
	private static final String _DATA_SOURCE_NAME = "env.DataSourceName";
	private static final String _CACHE_NAME = "env.CacheName";
	private static final String _ENV_CLASS_NAME = "env.EnvClassName";
	private static final String _DEFAULT_CONTEXT = "DEFAULT";
	private Map<String, JmsOperations> _jmsSenders = null;
	
	/**
	 * @roseuid 3BEC494C011E
	 */
	protected Environment() {

	}

	/**
	 * Constructor.
	 *
	 * @param configFile String. The configuration file. Complete
	 * path should be specified
	 * @throws XRuntime If creation of CnfgFileMgr fails.
	 *
	 * @throws com.addval.utils.XRuntime
	 * @roseuid 39284FF90352
	 */
	protected Environment(String configFile) throws XRuntime {
		// create CnfgFileMgr
		_cnfgFileMgr = new CnfgFileMgr( configFile );
	}

	/**
	 * Returns the default instance of Environemnt
	 *
	 * @param context String. Name of the instance
	 * @return Instance with the given name
	 * @roseuid 3928534502B5
	 */
	public static synchronized Environment getInstance()
	{
		return getInstance( _DEFAULT_CONTEXT );
	}


	public static String getClassName()
	{
		return "com.addval.environment.Environment";
	}

	/**
	 * Returns the instance specified by the context. i.e a named
	 * instance
	 *
	 * @param context String. Name of the instance
	 * @return Instance with the given name
	 * @roseuid 3928534502B5
	 */
	public static synchronized Environment getInstance(String context) {

		Environment env = (Environment)_instances.get(context);
		if (env == null) {

			// read the properties file for this context to get the name of the environment.
 			CnfgFileMgr cnfgFileMgr	= new CnfgFileMgr( context );
			String envName 			= cnfgFileMgr.getPropertyValue( _ENV_CLASS_NAME, getClassName() );

			try {
				// create the correct Environment object and initialize its config file mgr.
				env = (Environment)Class.forName( envName ).newInstance();
				env._name 			= context;
				env._cnfgFileMgr 	= cnfgFileMgr;

				setInstance( context, env );
			}
			catch( Exception e ) {
				e.printStackTrace();
				System.err.println( "Environment could not create instance of " + envName + " to initialize " + context );
			}
		}
		return env;
	}

	/**
	 * @return Name of the current environment.
	 * @roseuid 3BEC2B680342
	 */
	public String getName() {
		return _name;
	}

	/**
	 * This method has been deprecated. Instead, Environment should always be created
	 * with a contextName (i.e. project/sub-system name).
	 * For backward compatibility, this implementation will access the Environment
	 * created with the _DEFAULT contextname.
	 *
	 * @param configFile String. Complete path to the configuration file.
	 * @deprecated This method has been deprecated. Instead, Environment should always
	 * be created with a contextName (i.e. project/sub-system name).
	 * For backward compatibility, this implementation will access the Environment
	 * created with the _DEFAULT contextname.
	 * @roseuid 392853BE01C9
	 */
	public static void make(String configFile) {
		make( AVConstants._DEFAULT, configFile );
	}

	/**
	 * Make a new Environment.  This method allows an application to explicitly create
	 * an environment (instead of having the Environment be automatically created on
	 * the 1st call to getInstance().
	 *
	 * @param context String. The name of the Environment required.
	 * @param configFile String. Complete path to the configuration file.
	 * @roseuid 392854490010
	 */
	public static void make(String context, String configFile) {
		try {
			setInstance(context, new Environment( configFile ));
		}
		catch(Exception e) {

			e.printStackTrace();
			throw new XRuntime( e.getClass().getName(), e.getMessage() );
		}
	}

	/**
	 * This method has been deprecated.  All new code should pass the module name
	 * (typically getClass().getName()) to access the correct LogFileMgr.
	 * For backward compatibility, this implementation will access the LogFileMgr
	 * associated with the root category.
	 *
	 * @return The current value of the _logFileMgr property
	 * @deprecated This method has been deprecated.  All new code should pass the
	 * module name (typically getClass().getName()) to access the correct LogFileMgr.
	 * For backward compatibility, this implementation will access the LogFileMgr
	 * associated with the root category.
	 * @roseuid 3B71891F01CA
	 */
	public final synchronized LogFileMgr getLogFileMgr() {

    	return getLogFileMgr( null );
	}

	/**
	 * Returns the current LogFileMgr. If it is the first call then the a LogFileMgr
	 * is created.
	 *
	 * @param module Module name to get the LogFileMgr.
	 * @return LogFileMgr
	 * @roseuid 3B71888D0134
	 */
	public final synchronized LogFileMgr getLogFileMgr(String module) {

		return LogFileMgr.getInstance( module );
	}

	/**
	 * Returns the current CnfgFileMgr
	 *
	 * @return Returns the current CnfgFileMgr
	 * @roseuid 3B71893F016C
	 */
	public final CnfgFileMgr getCnfgFileMgr() {

		return _cnfgFileMgr;
	}

	/**
	 * Called to get the DBPoolMgr for this environment. If it is the first call then
	 * the DBPoolMgr will be created.
	 *
	 * @return DBPoolMgr for this environment.
	 * @roseuid 3BEB5C490256
	 */
	public synchronized DBPoolMgr getDbPoolMgr() {

		if ( _dbPoolMgr == null ) {

			String dataSourceName = _cnfgFileMgr.getPropertyValue( _DATA_SOURCE_NAME, _name );

			_dbPoolMgr = DBPoolMgr.getInstance( dataSourceName );

			// If _dbPoolMgr is still Null, initialize it and set the memeber variable of Environment
			if (_dbPoolMgr == null) {

				_dbPoolMgr = createDbPoolMgr( dataSourceName );
				DBPoolMgr.setInstance( _dbPoolMgr.getName(), _dbPoolMgr );
			}
		}

		return _dbPoolMgr;
	}

	/**
	 * This method will be used to access a data source that is different from the
	 * default data source for the environment.  This is to support code that needs to
	 * access more than one data source.
	 *
	 * @param source Source name to access corresponding DBPoolMgr
	 * @return DBPoolMgr
	 * @roseuid 3BEAE6D50247
	 */
	public synchronized DBPoolMgr getDbPoolMgr(String source) {

		if ( DBPoolMgr.getInstance( source ) == null ) {

			DBPoolMgr.setInstance( source, createDbPoolMgr( source ) );
		}

		return DBPoolMgr.getInstance( source );
	}

	/**
	 * Called to get the DBCacheMgr for this environment.
	 *
	 * @return DBCacheMgr for this environment.
	 * @roseuid 3BEB5CF20227
	 */
	public synchronized CacheMgr getCacheMgr() {

		if ( _cacheMgr == null ) {

			try {

				String cacheName = _cnfgFileMgr.getPropertyValue( _CACHE_NAME, "" );

				if (cacheName.length() == 0) {
					_cacheMgr = CacheMgr.getInstance( _cnfgFileMgr.getProperties(), _name );
				}
				else if( CacheMgr.hasInstance( cacheName ) ){
					//To avoid circular dependency.
					_cacheMgr = CacheMgr.getRawInstance( cacheName );
				}
				else {
					_cacheMgr = CacheMgr.getInstance( cacheName );
				}
			}
			catch (Exception e) {
				getLogFileMgr( getName() ).logError( e );
				throw new XRuntime( getClass().getName(), e.getMessage() );
			}
		}

		return _cacheMgr;
	}

	/**
	 * Puts the new environment and the corresponding context into the environment
	 * instance
	 *
	 * @param context New context
	 * @param env New environment
	 * @roseuid 3B61CA33026B
	 */
	protected static final void setInstance(String context, Environment env) {

		_instances.put( context , env );

		if ( env._name == null )
			env._name = context;

		env.initialize();
	}

	/**
	 * Derived Environment classes that have their own pool should override this
	 * implementation to create the actual pool class.
	 * @param name
	 * @return com.addval.dbutils.DBPoolMgr
	 * @roseuid 3BEC27C10301
	 */
	protected DBPoolMgr createDbPoolMgr(String name) {

		_dbPoolMgr = new DBPoolMgr(name, getCnfgFileMgr() );

		return _dbPoolMgr;
	}

	/**
	 * @roseuid 3C194BF70055
	 */
	protected void initialize() {

		//getLogFileMgr( getName() );
		//getCacheMgr();
	}

	public void setDbPoolMgr(DBPoolMgr mgr) {
		_dbPoolMgr = mgr;
	}

	public void setCacheMgr(CacheMgr mgr) {
		_cacheMgr = mgr;
	}

	public Map<String, JmsOperations> getJmsSenders() {
		return _jmsSenders;
	}

	public void setJmsSenders(Map<String, JmsOperations> jmsSenders) {
		_jmsSenders = jmsSenders;
	}

	// when the message is to be sent to within any server (irrespective of apps or comms or batch) this method is to be used
	public JmsOperations getJmsSender()
	{
		return getJmsSender( "internalJmsSender" );
	}
	
	// when message is to be sent to apps server use this method
	public JmsOperations getAppsJmsSender()
	{
		return getJmsSender( "appsJmsSender" );
	}

	// when message is to be sent to comms server use this method	
	public JmsOperations getCommsJmsSender()
	{
		return getJmsSender( "commsJmsSender" );
	}

	// when message is to be sent to batch server use this method
	public JmsOperations getBatchJmsSender()
	{
		return getJmsSender( "batchJmsSender" );
	}
	
	// when message is to be sent to MQ server use this method	
	public JmsOperations getMqJmsSender()
	{
		return getJmsSender( "mqJmsSender" );
	}
	
	// use this method when some other new server is to be sent the message
	public JmsOperations getJmsSender(String name)
	{
		if (getJmsSenders() == null || getJmsSenders().isEmpty())
			throw new RuntimeException( "No JmsTemplate is configured in ApplicationContext.xml - " + name );
		JmsOperations jmsOperations = getJmsSenders().get( name );
		if (jmsOperations == null)
			throw new RuntimeException( "No JmsTemplate is configured with bean id - " + name );
		return jmsOperations;
	}
}
