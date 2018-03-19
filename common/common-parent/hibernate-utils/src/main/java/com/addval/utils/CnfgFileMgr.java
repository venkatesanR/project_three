//Source file: d:\\projects\\common\\src\\client\\source\\com\\addval\\utils\\CnfgFileMgr.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Used to manage project specific configurations. Such as log file names, type of message to log, JDBC connetion information, etc.,
 * <p>
 * The configuration file is a "Properties" file -- as specified in the java.util.Properties package. All the information is specified in the form of a key/value pair. The following is sample section from the config file
 * 
 * <pre>
 * ###########################
 * # Loging configuration information
 * ###########################
 * log.filename=d:\\Projects\\swing\\lib\\swing.log
 * log.msg.types=INFO WARNING ERROR SQL TRACE
 * swing.login=http://localhost/swing/login.jsp
 * swing.homepage=http://localhost/swingt/home.jsp
 * </pre>
 * 
 * Each developer could have his/her own configuration file to suit their developmet needs.
 * <p>
 * The configuration information can also be loaded using the java.util.ResourceBundle
 *
 * Enhanced 1/7/05 as follows: Changed code to eliminate retention of _bundle; the "final" instance properties are now ALWAYS in Properties _prop. Added static HashMap _instances, keyed by configFilename, with value = its Properties Changed constructor to use Properties from _instances, if it
 * already exists. Added support for "chained defaulting" of Properties, via special property name "parentProperties" Added support for new/optional ConfigFileMgr.properties, which can contain only "defaultParentProperties". Non-existence of a specified configFile is no longer an exception; instead,
 * defaultParentProperties (if any) will be used. If defaultParentProperties is defined, and specifed configFile exists, but you want specified configFile to inherit defaults from some OTHER configFile, then put "parentProperties=theOtherConfigFile" in configFile. If defaultParentProperties is
 * defined, and specifed configFile exists, but you do NOT want specified configFile to inherit ANY defaults from ANYWHERE, then put "parentProperties=" or "parentProperties=none" in configFile.
 *
 * @revision $Revision$
 *
 * @author AddVal Technology Inc.
 * @see LogFileMgr
 */
public final class CnfgFileMgr implements Serializable {
	private static final long serialVersionUID = -5281351133599007292L;

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(CnfgFileMgr.class);

	private static final String _CNFGFILEMGR_PROPERTY_FILENAME = "CnfgFileMgr.properties";

	private static final String _DEPRECATED_ENV_LOGNAME = "env.LogName";

	private static HashMap _instances; // key=name, value=Properties
	private static String _defaultParentPropertiesFilename = null;

	private Properties _prop = null;

	private boolean externalizeDBErrorMessages = false;

	/**
	 * Static block to initialize class static members.
	 */
	static {

		synchronized (CnfgFileMgr.class) {

			_instances = new HashMap();

			// Determine the filename for the CnfigFileMgr's property file.
			// The default "CnfgFileMgr.properties" filename can be overrriden via a system property of the same name.
			String cnfgFileMgrPropertyFilename = _CNFGFILEMGR_PROPERTY_FILENAME;
			String overrideCnfgFileMgrPropertyFilename = System.getProperty(_CNFGFILEMGR_PROPERTY_FILENAME);
			if (overrideCnfgFileMgrPropertyFilename != null) {
				cnfgFileMgrPropertyFilename = overrideCnfgFileMgrPropertyFilename;
			}

			// Get the configured value for "defaultParentProperties".
			Properties props = getCnfgFileMgrProperties(cnfgFileMgrPropertyFilename);
			_defaultParentPropertiesFilename = props.getProperty("defaultParentProperties");
			if (_defaultParentPropertiesFilename != null && _defaultParentPropertiesFilename.trim().length() == 0) {
				_defaultParentPropertiesFilename = null;
			}

			_logger.info("Will use defaultParentProperties=" + _defaultParentPropertiesFilename);
		}
	}

	/**
	 * Static initialization helper, loads Properties from the specified CnfgFileMgr.properties file.
	 */
	private static Properties getCnfgFileMgrProperties(String filename) {
		Properties configProperties = new Properties();
		try {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(filename);
			}
			catch (IOException e) {
				_logger.debug("Unable to open CnfgFileMgr properties file " + filename + ", will now search for it as a resource (i.e. on CLASSPATH)");
			}

			if (inputStream == null) {
				inputStream = LogMgr.class.getResourceAsStream("/" + filename); // The leading "/" prevents pre-pending of the LogMgr's package name
				if (inputStream == null) {
					throw new FileNotFoundException("CnfgFileMgr properties file \"" + filename + "\" does not exist on CLASSPATH");
				}
			}

			// We have a non-null inputStream; load the properties from the stream
			configProperties.load(inputStream);
			inputStream.close();
		}
		catch (Exception ex) {
			_logger.info("Unable to locate and/or read CnfgFileMgr properties file " + filename + ", default property inheritance is therefore not enabled");
		}
		return configProperties;
	}

	/**
	 * Constructor. If specified configFile has previously been read, it uses the previously-created Properties; otherwise it calls the load function to load the properties file, and adds the new Properties to the class-static cached instances.
	 *
	 * @param configFile
	 *            Name of the configuration file to load (i.e configuratio file name) or the name of a properties file.
	 * @throws XRuntime
	 * @roseuid 37BDC1B80066
	 */
	public CnfgFileMgr(String configFile) {

		synchronized (_instances) {

			_prop = (Properties) _instances.get(configFile);

			if (_prop == null) {

				_prop = load(configFile);
			}
		}
	}

	/**
	 * Constructor. Create a config file manager with the specified properties
	 *
	 * Used to maintain backward compatibility with Spring
	 */
	public CnfgFileMgr(Properties props) {
		_prop = props;
	}

	/**
	 * Constructor. Create a config file manager with the specified properties and register with the CnfgFileMgr
	 *
	 * Used to maintain compatibility with Spring and
	 */
	public CnfgFileMgr(String configFile, Properties props) {
		_prop = props;

		synchronized (_instances) {
			_instances.put(configFile, props);
		}
	}

	/**
	 * Loads the specified configuration file, and returns it as a Properties. Also implements "chaining" to specified (or default) parentProperties Properties.
	 *
	 * If specified configuration file or the property file could not be found and loaded, it logs an INFO and uses only the defaultParentProperties.
	 *
	 * This function decides if the string that was passed to it was a fully qualified string name (configFile) or a propertyFile Name(ResourceBundle) and calls appropriate code where necessary
	 *
	 * @param propFile
	 *            Name of the configuration file or the property file.
	 * @return the Properties from the file, or the defaultParentProperties Properties if file/resource was not found.
	 * @roseuid 377A90350235
	 */
	private synchronized Properties load(String propFile) {

		Properties finalPropertiesToReturn = null;
		String propFileName = null;
		// Get the "raw" Properties. If file/resource not found, the rawProperties will exist but will be empty.
		Properties rawProperties = new Properties();
		try {

			if (isConfigFile(propFile)) {
				File file = new File(propFile);
				FileInputStream stream = new FileInputStream(file);
				rawProperties.load(stream);
				propFileName = file.getName();
			}
			else {
				propFileName = propFile;
				ResourceBundle tempResourceBundle = ResourceBundle.getBundle(propFile);
				// Copy tempResourceBundle key/value pairs into rawProperties.
				// Note that the Enumeration includes ALL keys for the ResourceBundle entire inheritance chain.
				Enumeration enumeration = tempResourceBundle.getKeys();
				while (enumeration.hasMoreElements()) {
					String name = (String) enumeration.nextElement();
					rawProperties.setProperty(name, tempResourceBundle.getString(name));
				}
			}
		}
		catch (Exception ex) {

			_logger.info("Unable to load properties from " + propFile + "; will use defaultParentProperties=" + _defaultParentPropertiesFilename);
		}

		// Resolve right-hand-side references to other properties (e.g., parentProperties=${cargores_install_name}_site_${cargores_env_name})
		resolveReferences(rawProperties);

		// Now figure out what Properties, if any, should be used for this Properties' default Properties.
		// The configured _defaultParentPropertiesFilename will be used, unless parentProperties was specified.
		String parentPropertiesFilename = rawProperties.getProperty("parentProperties");
		if (parentPropertiesFilename == null)
			parentPropertiesFilename = _defaultParentPropertiesFilename;

		// If _defaultParentPropertiesFilename is null or "parentProperties=" or "parentProperties=none" was specified,
		// or if THIS is the _defaultParentPropertiesFilename, then no "default chaining" is required here,
		// so just return the raw properties.
		// Changed Mar 18 2005 to allow reading the propeties for _defaultParentPropertiesFilename
		if (parentPropertiesFilename == null || parentPropertiesFilename.trim().length() == 0 || parentPropertiesFilename.toLowerCase().equals("none")) {

			finalPropertiesToReturn = rawProperties;
		}
		else {

			// We need to get the Properties to used as the parent for THIS new Properties.
			// Check if we have already cached the parent properties in _instances. If not, trigger its loading/caching.
			Properties parentProperties = (Properties) _instances.get(parentPropertiesFilename);
			if (parentProperties == null) {
				parentProperties = (new CnfgFileMgr(parentPropertiesFilename)).getProperties();
			}
			finalPropertiesToReturn = rawProperties;
			finalPropertiesToReturn.putAll(parentProperties);
		}

		// Next two checks are to facilitate migration away from deprecated code/usage:
		// (1) Report and remove entries for "env.LogName"
		// (2) Report any attempt to use CnfgFileMgr to load a LOG4J properties file (and include stack trace to help find who's doing it)

		String deprecatedLogNameValue = finalPropertiesToReturn.getProperty(_DEPRECATED_ENV_LOGNAME); // "env.LogName"
		if (deprecatedLogNameValue != null) {
			_logger.warn("Removing deprecated entry \"" + _DEPRECATED_ENV_LOGNAME + "=" + deprecatedLogNameValue + "\" that appears in " + propFile + " properties file");
			finalPropertiesToReturn.remove(_DEPRECATED_ENV_LOGNAME);
		}

		if (propFile.toLowerCase().endsWith("_log") || propFile.toLowerCase().endsWith("_log.properties") || finalPropertiesToReturn.containsKey("log4j.rootLogger")) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			RuntimeException ex = new RuntimeException();
			// ? ex = ex.fillInStackTrace();
			ex.printStackTrace(pw);
			pw.close();
			String stackTrace = sw.toString();
			_logger.warn("Apparent attempt to load a LOG4J properties file (" + propFile + ") using CnfgFileMgr, at:\n" + stackTrace);
		}

		// if required, append the site property pertaining to the project
		appendSiteProperties(rawProperties, finalPropertiesToReturn);

		// Resolve right-hand-side references to other properties (e.g., "MyLib=${MyHome}/ext/lib")
		resolveReferences(finalPropertiesToReturn);

		// Cache the finalized properties.
		_instances.put(propFileName, finalPropertiesToReturn);
		if (finalPropertiesToReturn.size() > 0) {
			_logger.debug("------------------------------Loaded properties : " + propFileName + ", with parentProperties=" + parentPropertiesFilename + ",size : " + finalPropertiesToReturn.size());
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("Loaded properties for " + propFileName + ", with parentProperties=" + parentPropertiesFilename + "\n" + formatProperties(finalPropertiesToReturn) + "\n\t");
		}
		return finalPropertiesToReturn;
	}

	/**
	 * If the property file has a property named "isReadSiteProperties" with value as true then, {@link CnfgFileMgr} will look for site properties by one of the below 2 styles. <BR>
	 * <BR>
	 * <B>Style1:</B> CnfgFileMgr will look for a {@link CnfgFileMgr} instance with name "site". This instance will be created in main-server-applicationContext.xml or web-applicationContext.xml. The logic translates to <BR>
	 * <code>
	 * CnfgFileMgr("site", new PropertiesFactoryBean().setLocations(propertyConfigurerLocations));
	 * </code> In this style we can read the entire properly hierarchy. <BR>
	 * <BR>
	 * <B>Style2:</B> CnfgFileMgr will look for a System Environment variable "project_name" (e.g project_name="cargores"). Based on the project_name, CnfgFileMgr will look into "${cargores_site_dir}/${cargores_install_name}_site_${cargores_env_name}.properties" In this style, only
	 * demo_site_properties will be read, other properties like, cargores_product_default.properties, project_override.properties will not be read. CnfgFileMgr will look for <B>Style2</B> only if <B>Style1</B> ( an instance of "site" is not available).
	 * 
	 * <BR>
	 * <BR>
	 * In this way, client properties like, booking_client can leverage the property hierarchy, without any changes.
	 *
	 *
	 * @param rawProperties
	 * @param finalPropertiesToReturn
	 */
	private void appendSiteProperties(Properties rawProperties, Properties finalPropertiesToReturn) {
		String isReadSiteProperties = rawProperties.getProperty("isReadSiteProperties");

		if (!"true".equalsIgnoreCase(isReadSiteProperties))
			return;
		finalPropertiesToReturn.putAll(readSiteProperties());
	}

	/*
	 * First looks for an instance with name "site". If not available then will look for demo_site.properties from System Environment
	 */
	private Properties readSiteProperties() {
		if (_instances.containsKey("site")) {
			return (Properties) _instances.get("site");
		}

		Properties siteProperties = null;
		String projectName = System.getenv("project_name");
		if (!StrUtl.isEmptyTrimmed(projectName)) {
			projectName += "_";
		}
		else {
			projectName = "";
		}
		String sitePropFileName = System.getenv(projectName + "install_name") + "_site_" + System.getenv(projectName + "env_name") + ".properties";
		String sitePropertiesFileName = System.getenv(projectName + "site_dir") + File.separator + sitePropFileName;
		if (_instances.containsKey(sitePropFileName)) {
			siteProperties = (Properties) _instances.get(sitePropFileName);
		}
		else {
			siteProperties = (new CnfgFileMgr(sitePropertiesFileName)).getProperties();
		}
		return siteProperties;
	}

	/**
	 * Examines property values for delimited property references, and attempts to resolve such references.
	 * <p>
	 * By default, uses (the non-escaped equivalents of) "${" as the left delimiter and "}" as the right delimter. Delimiters can be overridden in the Properties itself via (appropriately-escaped) entries for "PropertyReferenceResolver.leftDelim" and "PropertyReferenceResolver.rightDelim".
	 * <p>
	 * E.g., if specified properties contains: AppHome=C:/MyAppHome AppLib=${AppHome}/lib OtherLib=${UnresolvableReference}/lib the modified properties will be: AppHome=C:/MyAppHome AppLib=C:/MyAppHome/lib <== Note that reference has been resolved OtherLib=${UnresolvableReference}/lib
	 * <p>
	 * Same example but with specified non-default delimiters: PropertyReferenceResolver.leftDelim=@ PropertyReferenceResolver.rightDelim=@ AppHome=C:/MyAppHome AppLib=@AppHome@/lib OtherLib=@UnresolvableReference@/lib the modified properties will be: PropertyReferenceResolver.leftDelim=@
	 * PropertyReferenceResolver.rightDelim=@ AppHome=C:/MyAppHome AppLib=C:/MyAppHome/lib <== Note that reference has been resolved OtherLib=@UnresolvableReference@/lib
	 */
	private void resolveReferences(Properties props) {

		props.putAll(System.getenv());
		PropertyReferenceResolver resolver = new PropertyReferenceResolver(props);
		props = resolver.resolveReferences(props);
	}

	/**
	 * Formats properties for display. Uses Enumeration, so all inherited properties will be included.
	 */
	private String formatProperties(Properties props) {
		StringBuffer sb = new StringBuffer();
		Enumeration enumeration = props.propertyNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			String value = props.getProperty(name);
			sb.append("\n\t").append((name + "                                                  ").substring(0, 50)).append("= ").append(value);
		}
		return sb.toString();
	}

	/**
	 * Checks to see if a given file name is a configuration file name. Based on the occurences of / or \\
	 *
	 * @param propFile
	 *            Name of the configuration file.
	 * @return true if the input param is a configuration file. False otherwise.
	 * @roseuid 3B61C67B01D6
	 */
	private boolean isConfigFile(String propFile) {

		// If the file name contains a / or \\ then its a fully qualified configFile
		return propFile.indexOf("/") > -1 || propFile.indexOf("\\") > -1;
	}

	/**
	 * Returns the property value corresponding to the property name that is passed to it.If the property name is not found in the file, then the default value that is passed, is returned.
	 *
	 * @param name
	 *            name of the property to get the value for
	 * @param defValue
	 *            default value of the property. This will be returned if the property is not found in the configuration file
	 * @return Value corresponding to the property
	 * @roseuid 377A906603C6
	 */
	public synchronized String getPropertyValue(String name, String defValue) {

		String rv = defValue;

		if (_prop != null) {

			rv = _prop.getProperty(name, defValue);
		}
		else {

			throw new XRuntime(getClass().getName(), "Properties not set for CnfgFileMgr");
		}

		return rv;
	}

	/**
	 * Returns true, if the property name passed is present in the properties file; otherwise false.
	 *
	 * @param name
	 *            property name value
	 * @param def
	 *            default value for the property
	 * @return true, if the property name passed is present in the properties file; otherwise false
	 * @roseuid 377A90C3023A
	 */
	public synchronized boolean getBoolValue(String name, boolean def) {

		String value = getPropertyValue(name, "" + def);
		return Boolean.valueOf(value).booleanValue();
	}

	/**
	 * Used when we know that the a given property has a value that is "long". Returns the value as a "long"
	 *
	 * @param name
	 *            property name value
	 * @param def
	 *            default value for the property
	 * @return property value as "long"
	 * @roseuid 378A80FB0119
	 */
	public synchronized long getLongValue(String name, long def) {

		String value = getPropertyValue(name, "" + def);
		return Long.valueOf(value).longValue();
	}

	/**
	 * Accessor for the _prop variable
	 *
	 * @return prop Properties
	 * @roseuid 38AC65120364
	 */
	public Enumeration getPropertyNames() {

		return _prop.propertyNames();
	}

	/**
	 * Returns a Properties object. If _prop is not set then create a new Properties object from the ResourceBundle
	 *
	 * @return Properties
	 * @roseuid 3B7196AB0176
	 */
	public final synchronized Properties getProperties() {

		Properties finalPropertiesToReturn = null;
		Enumeration enumeration = _prop != null ? _prop.propertyNames() : null;
		if (enumeration != null) {

			finalPropertiesToReturn = new Properties();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				finalPropertiesToReturn.setProperty(name, _prop.getProperty(name));
			}
		}

		return finalPropertiesToReturn;
	}

	/**
	 * @throws java.lang.Throwable
	 * @roseuid 3B719964022D
	 */
	protected void finalize() throws Throwable {

		_prop = null;
		super.finalize();
	}

	/* Method will retrieve the value of the property and dynamically replace the runtime values into the retrieved value
	 * E.g : is_required_error= {0} is required
	 * We will pass the dynamic value[s] in the third argument 
	 */
	public synchronized String getPropertyValue(String propertyName, String defaultValue, Object[] values) {
		String propertyValue = getPropertyValue(propertyName, defaultValue);
		if (!StrUtl.isEmptyTrimmed(propertyValue) && values != null) {
			propertyValue = (new MessageFormat(propertyValue)).format(values);
		}
		return propertyValue;
	}

	/**
	 * @return the externalizeDBErrorMessages
	 */
	public boolean getExternalizeDBErrorMessages() {
		return externalizeDBErrorMessages;
	}

	/**
	 * @param externalizeDBErrorMessages
	 *            the externalizeDBErrorMessages to set
	 */
	public void setExternalizeDBErrorMessages(boolean externalizeDBErrorMessages) {
		this.externalizeDBErrorMessages = externalizeDBErrorMessages;
	}
}
