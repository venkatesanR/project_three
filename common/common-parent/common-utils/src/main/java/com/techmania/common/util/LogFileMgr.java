
package com.techmania.common.util;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 08/06/2001 - This class has been changed to work with Log4J. Generic class to
 * manage project logs. Normally a single instance of this class is created
 * during the startup (singleton). Program can use one of the many "log<Type>"
 * methods to log information. For example to log an error the user could use
 * "logError()" method.
 *
 * The amount and type of log can be controlled by setting up parameters in a
 * configration file. The parameters can be one or more of the following:
 * 
 * <pre>
 * DEBUG <=>(SQL TRACE)
 * INFO
 * WARNING
 * ERROR
 * FATAL
 * </pre>
 * 
 * @author Sankar Dhanushkodi
 * @version $Revision$
 * @see CnfgFileMgr@author AddVal Technology Inc.
 * @author AddVal Technology Inc.
 * @author AddVal Technology Inc.
 * @author AddVal Technology Inc.
 */
public final class LogFileMgr {
	public static final int _ERROR = 0;
	public static final int _INFO = 1;
	public static final int _WARNING = 2;
	public static final int _TRACE = 3;
	public static final int _SQL = 4;
	public static final String _ERROR_TYPE = "ERROR";
	public static final String _INFO_TYPE = "INFO";
	public static final String _WARNING_TYPE = "WARNING";
	public static final String _TRACE_TYPE = "TRACE";
	public static final String _SQL_TYPE = "SQL";
	public static final String _ALL_TYPE = _ERROR_TYPE + _INFO_TYPE + _WARNING_TYPE + _TRACE_TYPE + _SQL_TYPE;

	/**
	 * Represents the log4j Logger instance.
	 */
	private Logger _logger = null;

	/**
	 * This was causing re-configuration of log4j, resulting in errors such as
	 * "attempted to append to closed Appender". Replace with new standard call
	 * to LogMgr.
	 */
	private LogFileMgr(String module) {
		_logger = LogMgr.getLogger(module);
	}

	/**
	 * @deprecated
	 */
	public static synchronized LogFileMgr getInstance(String configFile, String module) {
		return new LogFileMgr(module);
	}

	/**
	 * @deprecated
	 */
	public static synchronized LogFileMgr getInstance(Properties properties, String module) {
		return new LogFileMgr(module);
	}

	/**
	 * @deprecated
	 */
	public static synchronized LogFileMgr getInstance(String module) {
		return new LogFileMgr(module);
	}

	/**
	 * @throws java.lang.Throwable
	 * @roseuid 3B71A3DD0160
	 */
	protected void finalize() throws Throwable {
		_logger = null;
		super.finalize();
	}

	public boolean debugOn() {
		return getLogger().isDebugEnabled();
	}

	public boolean infoOn() {
		return getLogger().isInfoEnabled();
	}

	/**
	 * Access method for the _logger property.
	 *
	 * @return the current value of the _logger property
	 */
	public Logger getLogger() {
		return _logger;
	}

	/**
	 * This method has been deprecated. It now peforms no action.
	 *
	 * @deprecated
	 */
	public void setLogger(Logger aLogger) {
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the
	 *             "source" parameter
	 *
	 *             Logs error mesasage
	 * @param source
	 *            String. Origin class/Id
	 * @param message
	 *            String
	 * @exception @roseuid
	 *                378A860D01A1
	 */
	public void logError(String source, String message) {
		_logger.error("[" + source + "] " + message);
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the
	 *             "source" parameter. Logs error mesasage
	 * @param source
	 *            String. Origin class/Id
	 * @param excep
	 *            Exception. The message in the exception logged. A strack trace
	 *            of the excpetion is also logged..
	 * @exception @roseuid
	 *                379D166E0280
	 */
	public void logError(String message, Exception excep) {
		_logger.error(message, excep);
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the
	 *             "source" parameter. Logs warning messages
	 * @param source
	 *            String. Origin class/ID
	 * @param message
	 *            String
	 * @exception @roseuid
	 *                378A86400335
	 */
	public void logWarning(String source, String message) {
		_logger.warn("[" + source + "] " + message);
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the
	 *             "source" parameter. Logs information messages
	 * @param source
	 *            String. Origin class/ID
	 * @param message
	 *            String
	 * @exceptio
	 * @roseuid 378A862C0232
	 */
	public void logInfo(String source, String message) {
		_logger.info("[" + source + "] " + message);
	}

	/**
	 * @deprecated This method has been deprecated. Use the method without the
	 *             "source" parameter. Logs SQL message. All SQL statements
	 *             excuted on the database server will be logged here
	 * @param source
	 *            String. Origin class/ID
	 * @param message
	 *            String
	 * @exceptio
	 * @roseuid 378A8657022A
	 */
	public void logSql(String source, String message) {
		_logger.info("[" + source + "] " + message);
	}

	/**
	 * Logs traceEnter/traceExit. These are put at the top and bottom of methods
	 * to track the current location of the program execution. Good for
	 * debugging.
	 * 
	 * @param source
	 *            String. Origin class/ID
	 * @param message
	 *            String
	 * @exception @see
	 *                LogFileMgr#traceEnter traceEnter
	 * @see LogFileMgr#traceExit traceExit
	 * @roseuid 37A6303402D6
	 */
	public void logTrace(String source, String message) {
		_logger.debug("[" + source + "] " + message);
	}

	/**
	 * @param source
	 * @param message
	 * @roseuid 3F1F313C01F4
	 */
	public void logDebug(String source, String message) {
		_logger.debug("[" + source + "] " + message);
	}

	/**
	 * Logs traceEnter. These are put at the top of methods to track the current
	 * location of the program execution. Good for debugging.
	 * 
	 * @param source
	 *            String. Origin class/ID
	 * @param message
	 *            String
	 * @exception @see
	 *                LogFileMgr#traceExit traceExit
	 * @see LogFileMgr#logTrace logTrace
	 * @roseuid 378A85BF0203
	 */
	public void traceEnter(String source) {
		_logger.debug("TRACE ENTER: " + source);
	}

	/**
	 * Logs traceExit. These are put at the bottom of methods to track the
	 * current location of the program execution. Good for debugging.
	 * 
	 * @param source
	 *            String. Origin class/ID
	 * @param message
	 *            String
	 * @exception @see
	 *                LogFileMgr#traceEnter traceEnter
	 * @see LogFileMgr#logTrace logTrace
	 * @roseuid 378A85C5027A
	 */
	public void traceExit(String source) {
		_logger.debug("TRACE EXIT: " + source);
	}

	/**
	 * Logs all types of message. Instead of calling a specific log message type
	 * (such as logError, logInfo, et.,) we could call this an pass a message
	 * type.
	 * 
	 * @param msgType
	 *            int. Can be ERROR, INFO, etc.,
	 * @param source
	 *            - String. Origin class/Id
	 * @param message
	 *            String
	 * @exception @roseuid
	 *                377A9566002E
	 */
	public void logMsg(int msgType, String source, String message) {

		switch (msgType) {

		case LogFileMgr._ERROR:
			logError(source, message);
			break;

		case LogFileMgr._WARNING:
			logWarning(source, message);
			break;

		case LogFileMgr._INFO:
			logInfo(source, message);
			break;

		case LogFileMgr._SQL:
			logSql(source, message);
			break;

		case LogFileMgr._TRACE:
			logTrace(source, message);
			break;

		default: // do nothing
		}
	}

	/**
	 * @param message
	 * @roseuid 3C0404660112
	 */
	public void logError(String message) {
		_logger.error(message);
	}

	/**
	 * @param excep
	 * @roseuid 3BEFFB6901F0
	 */
	public void logError(Exception excep) {
		if (excep == null)
			_logger.error("null");
		else
			_logger.error(excep.getMessage(), excep);
	}

	/**
	 * @param message
	 * @roseuid 3BEFFB7B001F
	 */
	public void logWarning(String message) {
		_logger.warn(message);
	}

	/**
	 * @param message
	 * @roseuid 3BEFFB9201BD
	 */
	public void logInfo(String message) {
		_logger.info(message);
	}

	/**
	 * @param message
	 * @roseuid 3BEFFB9202CB
	 */
	public void logSql(String message) {
		_logger.info(message);
	}

	/**
	 * @param message
	 * @roseuid 3F1F30CA002E
	 */
	public void logTrace(String message) {
		_logger.debug(message);
	}

	/**
	 * @param message
	 * @roseuid 3F1F31360186
	 */
	public void logDebug(String message) {
		_logger.debug(message);
	}
}
