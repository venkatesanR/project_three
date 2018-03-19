package com.addval.dbutils;

import javax.sql.DataSource;

public class DBPoolMgrConfig {
	private String _name;
	private String _dataSourceName;
	private String _poolType;
	private String _driver;
	private String _url;
	private String _serverType = "";
	private String _userName;
	private String _password;
	private String _jndiLookupName;
	private String _jndiContextFactory;
	private String _jndiUrl;
	private String _schemaName;

	private int _maxConn = -1;
	private int _minConn = -1;
	private int _connTimeout = 3600;
	private boolean _readSchemaInfo = false;

	private boolean _translateErrorMessages = true;
	private String _translatorTableName;
	private String _schemaInfoTablesToRead;
	private String _schemaInfoTypesToRead;
	private String _sqlExceptionTranslatorClassName;

	private DataSource _dataSource = null;

	private boolean _useSpringManagedTransactions = false;

	private boolean externalizeDBErrorMessages = false;

	public DBPoolMgrConfig() {

	}

	public String getName() {
		return _name;
	}

	public void setName(String aName) {
		_name = aName;
	}

	public String getDataSourceName() {
		return _dataSourceName;
	}

	public void setDataSourceName(String aName) {
		_dataSourceName = aName;
	}

	public String getPoolType() {
		return _poolType;
	}

	public void setPoolType(String aPoolType) {
		_poolType = aPoolType;
	}

	public String getDriver() {
		return _driver;
	}

	public void setDriver(String aDriver) {
		_driver = aDriver;
	}

	public String getUrl() {
		return _url;
	}

	public void setUrl(String aUrl) {
		_url = aUrl;
	}

	public String getServerType() {
		return _serverType;
	}

	public void setServerType(String aServerType) {
		_serverType = aServerType;
	}

	public String getSchemaName() {
		return _schemaName;
	}

	public void setSchemaName(String aSchemaName) {
		_schemaName = aSchemaName;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String aUserName) {
		_userName = aUserName;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String aPassword) {
		_password = aPassword;
	}

	public String getJndiLookupName() {
		return _jndiLookupName;
	}

	public void setJndiLookupName(String aName) {
		_jndiLookupName = aName;
	}

	public String getJndiContextFactory() {
		return _jndiContextFactory;
	}

	public void setJndiContextFactory(String aName) {
		_jndiContextFactory = aName;
	}

	public String getJndiUrl() {
		return _jndiUrl;
	}

	public void setJndiUrl(String aName) {
		_jndiUrl = aName;
	}

	public int getMaxConn() {
		return _maxConn;
	}

	public void setMaxConn(int aConn) {
		_maxConn = aConn;
	}

	public int getMinConn() {
		return _minConn;
	}

	public void setMinConn(int aConn) {
		_minConn = aConn;
	}

	public int getConnTimeout() {
		return _connTimeout;
	}

	public void setConnTimeout(int aConn) {
		_connTimeout = aConn;
	}

	public boolean getReadSchemaInfo() {
		return _readSchemaInfo;
	}

	public void setReadSchemaInfo(boolean aRead) {
		_readSchemaInfo = aRead;
	}

	public boolean getTranslateErrorMessages() {
		return _translateErrorMessages;
	}

	public void setTranslateErrorMessages(boolean aRead) {
		_translateErrorMessages = aRead;
	}

	public String getTranslatorTableName() {
		return _translatorTableName;
	}

	public void setTranslatorTableName(String aName) {
		_translatorTableName = aName;
	}

	public String getSchemaInfoTablesToRead() {
		return _schemaInfoTablesToRead;
	}

	public void setSchemaInfoTablesToRead(String aName) {
		_schemaInfoTablesToRead = aName;
	}

	public String getSchemaInfoTypesToRead() {
		return _schemaInfoTypesToRead;
	}

	public void setSchemaInfoTypesToRead(String aName) {
		_schemaInfoTypesToRead = aName;
	}

	public String getSqlExceptionTranslatorClassName() {
		return _sqlExceptionTranslatorClassName;
	}

	public void setSqlExceptionTranslatorClassName(String aName) {
		_sqlExceptionTranslatorClassName = aName;
	}

	public DataSource getDataSource() {
		return _dataSource;
	}

	public void setDataSource(DataSource s) {
		_dataSource = s;
	}

	public boolean getUseSpringManagedTransactions() {
		return _useSpringManagedTransactions;
	}

	public void setUseSpringManagedTransactions(boolean springManagedTransactions) {
		_useSpringManagedTransactions = springManagedTransactions;
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

	public String toString() {
		StringBuffer sb = new StringBuffer("DBPoolMgrConfig[");
		sb.append("name=").append(getName()).append(", ");
		sb.append("poolType=").append(getPoolType()).append(", ");
		sb.append("minConn=").append(getMinConn()).append(", ");
		sb.append("maxConn=").append(getMaxConn()).append(", ");
		sb.append("userName=").append(getUserName()).append(", ");
		sb.append("schemaName=").append(getSchemaName()).append(", ");
		sb.append("dataSourceName=").append(getDataSourceName()).append(", ");
		sb.append("driver=").append(getDriver()).append(", ");
		sb.append("url=").append(getUrl()).append(", ");
		sb.append("serverType=").append(getServerType()).append(", ");
		sb.append("jndiLookupName=").append(getJndiLookupName()).append(", ");
		sb.append("jndiUrl=").append(getJndiUrl()).append(", ");
		sb.append("jndiContextFactory=").append(getJndiContextFactory()).append(", ");
		sb.append("dataSource=").append(getDataSource());
		sb.append("externalizeDBErrorMessages=").append(getExternalizeDBErrorMessages());
		sb.append("]DBPoolMgrConfig");
		return sb.toString();
	}

}
