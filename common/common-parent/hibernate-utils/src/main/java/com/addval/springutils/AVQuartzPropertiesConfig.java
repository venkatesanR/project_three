package com.addval.springutils;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.addval.utils.StrUtl;

/*
 * Class to enable easy customization of quartz properties from spring configuration files
 *
 * properties - contains all the properties that are constants and would not be changed from one environment to another
 * jobStoreConfigName - stores the jobStoreConfigName that is going to be used
 * jobStoreConfigProperties - a map containing the jobStoreConfigName and the Properties applicable to the jobStore class
 *
 * getQuartzProperties - will return the properties which consists of the properties + the jobStoreProperties selected based on the jobStoreConfigName
 */
public class AVQuartzPropertiesConfig {
	static transient Logger _logger = com.addval.utils.LogMgr.getLogger(AVQuartzPropertiesConfig.class);

	Properties _properties = null;
	String _jobStoreConfigNameOverride = null;
	String _serverType = null;
	Map _jobStoreConfigPropertyMap = null;
	Map _jobStoreConfigServerTypeMap = null;
	Map _autoStartupServerTypeMap = null;

	public AVQuartzPropertiesConfig() {

	}

	public Properties getProperties() {
		return _properties;
	}

	public void setProperties(Properties properties) {
		_properties = properties;
	}

	public String getJobStoreConfigNameOverride() {
		return _jobStoreConfigNameOverride;
	}

	public void setJobStoreConfigNameOverride(String aJobStoreConfigName) {
		_jobStoreConfigNameOverride = aJobStoreConfigName;
	}

	public void setJobStoreConfigPropertyMap(Map aMap) {
		_jobStoreConfigPropertyMap = aMap;
	}

	public Map getJobStoreConfigPropertyMap() {
		return _jobStoreConfigPropertyMap;
	}

	public void setJobStoreConfigServerTypeMap(Map aMap) {
		_jobStoreConfigServerTypeMap = aMap;
	}

	public Map getJobStoreConfigServerTypeMap() {
		return _jobStoreConfigServerTypeMap;
	}

	public void setAutoStartupServerTypeMap(Map aMap) {
		_autoStartupServerTypeMap = aMap;
	}

	public Map getAutoStartupServerTypeMap() {
		return _autoStartupServerTypeMap;
	}

	public String getServerType() {
		return _serverType;
	}

	public void setServerType(String aServerType) {
		_serverType = aServerType;
	}

	public void setQuartzProperties(Properties properties) {

	}

	public Properties getQuartzProperties() throws Exception {

		Properties quartzProperties = new Properties();

		if (getProperties() != null) {
			quartzProperties.putAll(getProperties());
		}

		String jobStoreConfigName = (String) getJobStoreConfigServerTypeMap().get(getServerType());
		// String autoStartup = (String) getAutoStartupServerTypeMap().get(getServerType());

		if (!StrUtl.isEmpty(getJobStoreConfigNameOverride())) {
			jobStoreConfigName = getJobStoreConfigNameOverride();
		}

		Properties props = (Properties) getJobStoreConfigPropertyMap().get(jobStoreConfigName);

		if (props != null) {
			quartzProperties.putAll(props);
		}
		else {
			Exception e = new Exception("Quartz JobStore Config Name: " + jobStoreConfigName + " not found in JobStoreConfigPropertyMap");
			_logger.error(e);
			throw e;
		}

		return quartzProperties;
	}

	public void setAutoStartup(boolean startup) {
	}

	public boolean getAutoStartup() throws Exception {
		// String autoStartup = (String) getAutoStartupServerTypeMap().get(getServerType());
		// return autoStartup.equals("true");
		return true;
	}

}