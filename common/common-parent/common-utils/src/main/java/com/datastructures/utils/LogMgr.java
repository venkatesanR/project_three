
package com.datastructures.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This utility is intended to be the sole "provider" of log4j Logger objects to
 * an application. It provides only five public methods, all of which are
 * static:
 * <ul>
 * <li>doConfigure(), to configure the logger with the correct property file/
 * xml configuration.
 * <li>doReConfigure(), to forcefully do the reconfiguration of the Logger.
 * <li>getLogger(Class), returns a Logger whose name is the full-qualified Class
 * name<\li>
 * <li>getLogger(String), returns a Logger whose name is the specified String
 * <\li>
 * <li>shutdown(), shuts down all futher log4j logging in the JVM. See the
 * CAUTION about usage below.
 * </ul>
 * This utility is intended to replace the use of Environment's .getLogFileMgr()
 * methods as the means to obtain a LogFileMgr to be used for logging. Instead,
 * this utilities methods return a native log4j Logger. Direct use of native
 * log4j Loggers offers benefits over the use of LogFileMgr in both features
 * (e.g., ability to report caller's location) and performance.
 * <p>
 * For non-passivatable classes with normal logging needs, the recommended usage
 * is as shown below. The getLogger(Class) method returns a Logger whose name is
 * the fully-qualified Class name of the caller
 * 
 * <pre>
 *         public class MyClass
 *        {
 *                private static final transient org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(MyClass.class);
 *
 *                public void aMethod(Object anArg)
 *                {
 *                        if (_logger.isDebugEnabled() )
 *                        {
 *                                _logger.debug("entering aMethod(anArg), anArg="+anArg);
 *                        }
 *                        <etc.>
 * </pre>
 * 
 * In special cases where a separate/distinct logging stream is needed, it is
 * still recommended that the class declare and use a Logger in the recommended
 * way (as above), for purposes of normal/debug logging. But you can also
 * declare and use a separate special Logger using the getLogger(String) method,
 * as shown below. The getLogger(String) method returns a logger whose name is
 * the specified String value. All classes that participate in the special
 * logging stream will need to obtain the special logger via the
 * getLogger(String) method, specifying the same mutually-agreed value for the
 * String.
 * 
 * <pre>
 *         public class MyClass
 *        {
 *                private static final transient org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(MyClass.class);
 *
 *                private transient org.apache.log4j.Logger _specialPurposeLogger;
 *
 *      public MyClass(String mySubsystem)
 *                {
 *                        _specialPurposeLogger = com.addval.utils.LogMgr.getLogger(mySubsystem);
 *                }
 *
 *                public void aMethod(Object anArg)
 *                {
 *                        if (_logger.isDebugEnabled() )
 *                        {
 *                                _logger.debug("Normal debug logging example:  entering aMethod(anArg), anArg="+anArg);
 *                        }
 *
 *                        _specialPurposeLogger.info("Special logging example: anArg=" + anArg.toString());
 *
 *                        <etc.>
 * </pre>
 * 
 * This utility does NOT use Environment or EJBEnvironment to obtain
 * configuration info. To configure this utility, you should define the System
 * property "LogMgr.configFile" by including the following option on the "java"
 * command that is used to start the JVM and your application:
 * 
 * <pre>
 *                -DLogMgr.configFile=&lt;YourLogMgrConfigFilename&gt;
 * </pre>
 * 
 * If the LogMgr.configFile property is not defined, the default value
 * "LogMgr.properties" will be used. LogMgr will first to attempt to open the
 * file as specified (i.e., with any pathname that is included). If unable to
 * locate/open the file, LogMgr will then attempt to open it as a resource,
 * i.e., a file locatable on the CLASSPATH.
 * <p>
 * The LogMgr's configuration file is a .properties file. It is used to define
 * the location of the log4j configuration file to be used, and the properties
 * that determine LogMgr behavior. The 2 behavioral properties and their default
 * values are:
 * <ol>
 * <li>watchConfigFile (default=true). If =true, a log4j "watcher" will be used
 * to watch for changes to the log4j config file, every "watchDelay"
 * milliseconds.</li>
 * <li>watchDelay, in milliseconds (default=60000, which is 1 minute)</li>
 * </ol>
 * A sample LogMgr.configFile is shown below:
 * 
 * <pre>
 *                watchConfigFile = true
 *                watchDelay = 300000
 *                log4j.configFile = C:/Projects/ngrm/apps1config/cargores_log.xml
 * </pre>
 * 
 * If the value for the log4j.configFile property ends with ".xml" (case
 * insensitive), then it must be the XML form of a log4j configuration file.
 * Otherwise its name should end with ".properties" and its content must be the
 * properties form of a log4j configuration file.
 * <p>
 * Note that since LogMgr provides a "naked" (not "wrapped") log4j Logger, the
 * Logger will be able to report detailed information about where the message
 * comes from, i.e., the calling class and optional the source file name and
 * line number. Therefore it is not necessary to include package or class name
 * of the caller in the message itself. However, you may wish to include the
 * method name. Also, you should include the class name if it is important that
 * the class name is always to be included to log output, even when configured
 * for production. The use of log4j's ability to report the caller class and/or
 * source file/line is somewhat expensive, so we expect that production
 * configuration will not use it.
 * <p>
 * CAUTION regarding use of the shutdown() method:
 * <p>
 * The shutdown() method should be called ONLY in the following circumstances:
 * <ol>
 * <li>The application has detected that its own termination (or failure) is
 * imminent
 * <li>The application knows that it is the only application running in the JVM
 * that is using log4j
 * <li>Log4j is known to be configured so as to be using FileAppenders with
 * bufferedIO=true
 * <li>The application does not want to lose any of the pending log4j output
 * that is buffered
 * </ol>
 * shutdown() causes log4j to flush all of its buffered file output, which is
 * the only reason why anyone would want to call it. But shutdown() ALSO shuts
 * down log4j; no further log4j logging will occur in the JVM (unless someone
 * reconfigures log4j).
 * </pre>
 * The main motivation for shutdown() is that some Addval batch jobs run in
 * parallel, with multiple jobs logging to the same log files. Log entries in
 * the files are "inter-leaved". To reduce the degree of interleaving, you can
 * set the FileAppenders being used to use bufferedIO, with a big bufferSize.
 * But the problem with that approach is that when a batch job terminates, log4j
 * does not automatically flush its buffers, so the last partial buffer of
 * output would be lost. The solution is that each batch job should include
 * 
 * <pre>
 *                finally
 *                {
 *                        LogMgr.shutdown();
 *                }
 * </pre>
 * 
 * at the end of the "main(String[] args)" procedure of the class that is
 * driving the batch job.
 * <p>
 * Note that LogMgr.shutdown() should never be used in code that runs inside a
 * J2EE container, since it would affect ALL log4j logging within that
 * container.
 */
public class LogMgr {
	private static final String _DEFAULT_LOGMGR_CONFIGFILE = "LogMgr.properties";

	// ----------------------- INTERNALS --------------------
	private static String _logMgrConfigFile = null;
	private static String _log4jConfigFile = null;
	private static boolean _watchConfigFile = true;
	private static long _watchDelay = org.apache.log4j.helpers.FileWatchdog.DEFAULT_DELAY; // =
																							// 60000
																							// msec
																							// =
																							// 60
																							// seconds
	private static boolean configured = false;
	private static Properties _logMgrConfigFileProps = null;
	// ----------------------- PUBLIC METHODS --------------------

	/**
	 * This method returns a Logger whose name is the fully-specified Class name
	 * If the Class argument is null, it returns the log4j root logger.
	 */
	public synchronized static Logger getLogger(Class<?> aClass) {
		if (aClass == null)
			return LogMgr.getLogger((String) null);
		return LogMgr.getLogger(aClass.getName());
	}

	/**
	 * This method returns a Logger whose name is the specified arbitrary
	 * string. If the String argument is null, empty, "ROOT" or "DEFAULT", it
	 * returns the log4j root logger.
	 */
	public synchronized static Logger getLogger(String anArbitraryString) {
		doConfigure();
		if ((anArbitraryString == null) || anArbitraryString.trim().equals("") || anArbitraryString.equals("ROOT")
				|| anArbitraryString.equals("DEFAULT"))
			return Logger.getRootLogger();
		return Logger.getLogger(anArbitraryString);
	}

	/**
	 * Calls log4j's own shutdown() method, which flushes all buffered file
	 * output, then terminates log4j activity.
	 */
	public static void shutdown() {
		Logger localLogger = Logger.getRootLogger();
		localLogger.warn("*** LogMgr.shutdown() has been called, log4j logging will cease.");
		org.apache.log4j.LogManager.getLoggerRepository().shutdown();
		System.out.println("*** LogMgr.shutdown() has been called, log4j logging has ceased.");
	}

	public synchronized static void doConfigure() {
		if (!isConfigured()) {
			doConfigureImpl();
		}
	}

	/**
	 * Configuration through properties. In this case, the LogMgr_configFile
	 * properties is passes to this method, and LogMgr will be initialized with
	 * these properties. The properties will be injected via Spring.
	 */
	public static synchronized LogMgr doConfigure(Properties properties) {
		if (!isConfigured() && properties != null && properties.size() > 0) {
			printMsg("Configuring using doConfigure(Properties)");
			_logMgrConfigFileProps = properties;
			doConfigureImpl();
		}
		return new LogMgr();
	}

	/**
	 * It is configured thru springLog4j.xml
	 * 
	 */
	public static synchronized LogMgr doConfigure(String logMgrConfigFile) {
		if (!isConfigured() && !StrUtl.isEmptyTrimmed(logMgrConfigFile)) {
			printMsg("Configuring using doConfigure(String) + " + logMgrConfigFile);
			_logMgrConfigFile = logMgrConfigFile;
			doConfigureImpl();
		}
		return new LogMgr();
	}

	public static synchronized boolean isConfigured() {
		return configured;
	}

	public static void doReConfigure() {
		printMsg("Configuring using doReConfigure + " + isConfigured());
		doConfigureImpl();
	}

	/**
	 * Static class initialization block. This does the configuration of log4j.
	 */

	// commented out. As it a static block gets executed first and i think as it
	// is not able to get the log dir, creates all logs in the root dir.
	// the initalisation thro the doConfigure() (or the doReconfigure()) alone
	// need to be done.

	// static
	// {
	// printMsg("Configuring using static block" );
	// doConfigureImpl();
	// }
	//
	// call this method to reconfigure log4j after initialization
	//
	private static void doConfigureImpl() {
		if (!isConfigured()) {
			printMsg(
					"*************** Log4j configuration for the first time. To be seen in log only once. ***************");
			configured = true;
		}
		if (_logMgrConfigFile == null && _logMgrConfigFileProps == null) {
			// Check to see if a LogMgr configuration file has been specified.
			// If so, read it and set LogMgr configuration attributes
			// accordingly.
			_logMgrConfigFile = System.getProperty("LogMgr.configFile");
			if (_logMgrConfigFile == null) {
				_logMgrConfigFile = System.getenv().get("LogMgr.configFile");
			}

			// Check the System Property & System Environment property for
			// "LogMgr_configFile".
			if (_logMgrConfigFile != null) {
				printMsg("System property LogMgr.configFile is defined: LogMgr.configFile=" + _logMgrConfigFile);
			} else {
				_logMgrConfigFile = System.getProperty("LogMgr_configFile");
				if (_logMgrConfigFile == null) {
					_logMgrConfigFile = System.getenv().get("LogMgr_configFile");
				}
				if (_logMgrConfigFile != null) {
					printMsg("System property LogMgr_configFile is defined: LogMgr_configFile=" + _logMgrConfigFile);
				}
			}

			if (_logMgrConfigFile == null) {
				// If propery LogMgr.configFile is not defined, use the default
				// value for the filename
				printMsg(
						"System property LogMgr.configFile or LogMgr_configFile is not defined; will use default value="
								+ _DEFAULT_LOGMGR_CONFIGFILE);
				_logMgrConfigFile = _DEFAULT_LOGMGR_CONFIGFILE;
			}
		}
		if (_logMgrConfigFileProps != null) {
			printMsg(
					"LogMgr is constructed with LogMgr_ConfigFile Properties. Now using the configured properties to initialize Log4j.");
		}

		// Get the LogMgr properties from the file.
		// Note that the getLogMgrConfigProperties ALWAYS returns a set of
		// Properties; if there was any error (such as file not found),
		// it will print explanation of the error and return an empty
		// Properties.
		// If _logMgrConfigFileProps itself is injected directly, use the
		// properties for configuration.
		Properties props = _logMgrConfigFileProps != null ? _logMgrConfigFileProps
				: getLogMgrConfigProperties(_logMgrConfigFile);
		if (props.containsKey("log4j.configFile")) {
			_log4jConfigFile = props.getProperty("log4j.configFile").trim();
		}

		if (props.containsKey("watchConfigFile")) {
			String booleanString = props.getProperty("watchConfigFile").trim();
			_watchConfigFile = Boolean.valueOf(booleanString).booleanValue();
		}

		if (props.containsKey("watchDelay")) {
			String longString = props.getProperty("watchDelay").trim();
			try {
				_watchDelay = Long.valueOf(longString).longValue();
			} catch (NumberFormatException ex) {
				printMsg("Invalid value specified in " + _logMgrConfigFile + ": watchDelay=" + longString
						+ "; will use default watchDelay=" + _watchDelay + " (msec)");
			}
		}

		// If the log4j config filename has not been specified inside the LogMgr
		// config file, or
		// if the log4j config file name is specified as system property
		// check the JVM System properties to see if the log4j config filename
		// has been defined there.
		if ((_log4jConfigFile == null) || (System.getProperty("log4j.configFile") != null)) {
			_log4jConfigFile = System.getProperty("log4j.configFile");
		}

		if (_log4jConfigFile == null) {
			printMsg("log4j.configFile has not been specified, using default log4j BasicConfigurator");
			BasicConfigurator.configure();
		} else {
			printMsg("Specified log4j.configFile = " + _log4jConfigFile);
			// If the specified file is found in the current directory, or as a
			// simple file resource on the classpath, use it to initialize log4j
			String fullFilename = getFullFilename(_log4jConfigFile);
			if (fullFilename != null) {
				printMsg("Found log4j.configFile = " + fullFilename);
				setBasicLog4jProperties(props, fullFilename);
				// Do log4j configuration using the simple text file that was
				// found.
				if (fullFilename.toLowerCase().endsWith(".xml")) {
					printMsg("log4j.configFile value ends with \".xml\", using log4j DOMConfigurator");
					if (_watchConfigFile)
						DOMConfigurator.configureAndWatch(fullFilename, _watchDelay);
					else
						DOMConfigurator.configure(fullFilename);
				} else {
					printMsg("log4j.configFile value does not end with \".xml\", using log4j PropertyConfigurator");
					if (_watchConfigFile)
						PropertyConfigurator.configureAndWatch(fullFilename, _watchDelay);
					else
						PropertyConfigurator.configure(fullFilename);
				}
			} else {
				// The specified log4j config file was not found. Look for it as
				// a URL on the classpath; for example, it might be found inside
				// a jar file..
				printMsg("Specified log4j.configFile = " + _log4jConfigFile
						+ " was not found; will attempt to locate it as a classpath resource");
				URL url = getFileURL(_log4jConfigFile);
				if (url == null) {
					printMsg("Specified log4j.configFile = " + _log4jConfigFile
							+ " was not found as a classpath resource, using default log4j BasicConfigurator");
					BasicConfigurator.configure();
				} else {
					printMsg("Found specified log4j.configFile as classpath resource = " + url.toString());
					setBasicLog4jProperties(props, url);
					if (_watchConfigFile)
						printMsg(
								"When log4j.configFile is a classpath resource, \"watchDelay\" feature is not supported");

					if (url.toString().toLowerCase().endsWith(".xml")) {
						printMsg("log4j.configFile value ends with \".xml\", using log4j DOMConfigurator");
						DOMConfigurator.configure(url);
					} else {
						printMsg("log4j.configFile value does not end with \".xml\", using log4j PropertyConfigurator");
						PropertyConfigurator.configure(url);
					}
				}
			}
		}
	}

	/**
	 * Utility for displaying messages to the console, used for messages/errors
	 * issued by this class.
	 */
	private static void printMsg(String msg) {
		System.out.println("com.addval.utils.LogMgr: " + msg);
	}

	/**
	 * Utility to get the fully-qualified filename, based on test for file
	 * existence. Will return fully-qualified filename, if: (a) specified file
	 * exists (and can be opened for read) in current directory; or (b)
	 * specified file exists as a simple file resource on the CLASSPATH (e.g.
	 * URL starts with "file:/") Otherwise returns null.
	 *
	 * Note: returns null if specified file is found as resource on CLASSPATH,
	 * but exists inside a jar (e.g. URL starts with "jar:file:/").
	 */
	private static String getFullFilename(String fileName) {
		if (fileName == null)
			return null;
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			System.out.println("Specified log config file exists " + file.getAbsolutePath());
			return file.getAbsolutePath();
		}
		System.out.println(String.format("Trying to locate log config file %s in classpath", fileName));
		// Not able to find the file with specified filename. Check to see if we
		// can find it as a simple file Resource.
		URL url = getFileURL(fileName); // The leading "/" prevents pre-pending
										// of the LogMgr's package name
		if (url == null) {
			System.out.println("Specified log config file could not be located in classpath also. " + fileName);
			return null;
		}
		String urlString = url.toString();
		if (!urlString.startsWith("file:/")) {
			System.out.println("Specified log config file could not be located in classpath also." + fileName);
			return null;
		}
		// Were able to find the file as a simple file Resource. Extract the
		// fully-qualified filename from the URL, i.e. the part after "file:/".
		System.out.println("Specified log config file located in path " + url);
		String fullFileName = urlString.substring("file:".length());
		file = new File(fullFileName);
		// this is for UNIX based systems
		if (file.isFile() && file.exists())
			return fullFileName;
		fullFileName = urlString.substring("file:/".length());
		// this is for windows based machines
		if (file.isFile() && file.exists())
			return fullFileName;
		System.out.println(String
				.format("log config file is located as %s. However there is a problem reading the file.", urlString));
		return null;
	}

	/**
	 * Utility to get the URL for the specified file.
	 */
	private static URL getFileURL(String filename) {
		if (filename == null)
			return null;
		try {
			return LogMgr.class.getResource("/" + filename); // The leading "/"
																// prevents
																// pre-pending
																// of the
																// LogMgr's
																// package name
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Utility to read a Properties object from specified filename.
	 */
	private static Properties getLogMgrConfigProperties(String filename) {
		Properties configProperties = new Properties();
		try {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(filename);
			} catch (IOException e) {
				printMsg("Unable to open LogMgr properties file " + filename
						+ ", will now search for it as a classpath resource");
			}

			if (inputStream == null) {
				inputStream = LogMgr.class.getResourceAsStream("/" + filename); // The
																				// leading
																				// "/"
																				// prevents
																				// pre-pending
																				// of
																				// the
																				// LogMgr's
																				// package
																				// name
				if (inputStream == null)
					throw new FileNotFoundException("LogMgr properties file \"" + filename + "\" cannot be found");
			}

			// We have a non-null inputStream; load the properties from the
			// stream
			configProperties.load(inputStream);
			inputStream.close();
		} catch (FileNotFoundException ex) {
			printMsg("Error locating LogMgr properties file " + filename + "; defaults will be used\nCause:\n" + ex);
		} catch (IOException ex) {
			printMsg("Error reading LogMgr properties file " + filename + "; defaults will be used\nCause:\n" + ex);
		} catch (Exception ex) {
			printMsg("Error locating or reading LogMgr properties file " + filename
					+ "; defaults will be used\nCause:\n" + ex);
		}

		return configProperties;
	}

	private static void setBasicLog4jProperties(Properties configProperties, String filename) {
		setBasicLog4jProperties(configProperties, filename, null);
	}

	private static void setBasicLog4jProperties(Properties configProperties, URL url) {
		setBasicLog4jProperties(configProperties, null, url);
	}

	private static void setBasicLog4jProperties(Properties configProperties, String filename, URL url) {
		try {
			String log4jStr = "";
			if (url != null) {
				log4jStr = FileUtl.readFile(url.openStream());
			} else if (!StrUtl.isEmptyTrimmed(filename)) {
				log4jStr = FileUtils.readFileToString(new File(filename));
			}
			List<String> refProperties = getLog4JRefProperties(log4jStr);
			if (configProperties.containsKey("MaxFileSize")) {
				setSystemProperty(refProperties, "MaxFileSize".toLowerCase(),
						(String) configProperties.get("MaxFileSize"));
			}
			if (configProperties.containsKey("MaxBackupIndex")) {
				setSystemProperty(refProperties, "MaxBackupIndex".toLowerCase(),
						(String) configProperties.get("MaxBackupIndex"));
			}
			if (configProperties.containsKey("logs.dir")) {
				setSystemProperty(refProperties, "dir".toLowerCase(), (String) configProperties.get("logs.dir"));
			}
		} catch (Exception ex) {
			printMsg("Error while setBasicLog4jProperties(Properties,String,URL)" + ex);
		}
	}

	public static List<String> getLog4JRefProperties(String log4jStr) throws Exception {
		List<String> refProperties = new ArrayList<String>();
		String placeholderPrefix = "${";
		String placeholderSuffix = "}";
		int startIndex = log4jStr.indexOf(placeholderPrefix);
		while (startIndex != -1) {
			int endIndex = log4jStr.indexOf(placeholderSuffix, startIndex + placeholderSuffix.length());
			if (endIndex != -1) {
				String placeholder = log4jStr.substring(startIndex + placeholderPrefix.length(), endIndex);
				if (!refProperties.contains(placeholder)) {
					refProperties.add(placeholder);
				}
			}
			startIndex = log4jStr.indexOf(placeholderPrefix, endIndex);
		}
		return refProperties;
	}

	private static void setSystemProperty(List<String> refProperties, String suffix, String value) {
		for (String prop : refProperties) {
			if (prop.toLowerCase().endsWith(suffix)) {
				if (System.getProperty(prop) == null) {
					System.setProperty(prop, value);
					System.out.println(prop + " -> " + value);
				}
			}
		}
	}

	/**
	 * MAIN method, for testing only.
	 */
	public static void main(String[] args) {
		printMsg("System.getProperties()=" + System.getProperties());
		Logger logger = LogMgr.getLogger(LogMgr.class);
		logger.debug("Test message issued from LogMgr.main at debug Level");
		logger.info("Test message issued from LogMgr.main at Info  Level");
		logger.warn("Test message issued from LogMgr.main at warn  Level");
		logger.error("Test message issued from LogMgr.main at error Level");
		logger.fatal("Test message issued from LogMgr.main at fatal Level");
	}
}
